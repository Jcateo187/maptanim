package com.maptanim.app.dss.evaluator

import com.maptanim.app.dss.model.DssFarmerData
import com.maptanim.app.dss.model.DssReferenceData
import com.maptanim.app.dss.model.DssValidationIssue
import com.maptanim.app.dss.model.IssueSeverity
import java.time.LocalDate

/**
 * Validates available input prior to DSS Engine evaluation.
 *
 * Adheres strictly to the architectural constraint:
 * If information required by a particular decision is missing, the DSS does NOT invent
 * a value. Instead, the data gap is surfaced transparently as an issue, and dependent
 * decisions are marked as having insufficient information.
 */
class DssInputValidator {

    fun validate(
        farmerData: DssFarmerData,
        referenceData: DssReferenceData
    ): List<DssValidationIssue> {
        val issues = mutableListOf<DssValidationIssue>()

        if (farmerData.plots.isEmpty()) {
            issues.add(
                DssValidationIssue(
                    plotId = null,
                    plotLabel = null,
                    fieldName = "plots",
                    message = "No crop beds or plots found for this farm. Add or plant beds in the Farm Editor.",
                    severity = IssueSeverity.WARNING
                )
            )
            return issues
        }

        farmerData.plots.forEach { plot ->
            val isBedOnly = plot.cropName.isNullOrBlank() || plot.cropName.equals("Bed", ignoreCase = true)
            if (isBedOnly) {
                // If it's a bed, check if it has crop zones assigned
                val zones = farmerData.cropZones.filter { it.plotId == plot.id && !it.cropName.isNullOrBlank() }
                if (zones.isEmpty()) {
                    issues.add(
                        DssValidationIssue(
                            plotId = plot.id,
                            plotLabel = plot.plotLabel,
                            fieldName = "cropName",
                            message = "${plot.plotLabel} has no crops planted. Decision engine cannot evaluate crop care without an assigned crop.",
                            severity = IssueSeverity.INFO
                        )
                    )
                }
            } else {
                val crop = referenceData.crops.firstOrNull { it.name.equals(plot.cropName, ignoreCase = true) }
                if (crop == null) {
                    issues.add(
                        DssValidationIssue(
                            plotId = plot.id,
                            plotLabel = plot.plotLabel,
                            fieldName = "cropReference",
                            message = "No documented agricultural reference found for crop '${plot.cropName}'. Care & harvest rules unavailable.",
                            severity = IssueSeverity.WARNING
                        )
                    )
                }

                // Planted date check
                if (plot.plantedDate.isNullOrBlank()) {
                    issues.add(
                        DssValidationIssue(
                            plotId = plot.id,
                            plotLabel = plot.plotLabel,
                            fieldName = "plantedDate",
                            message = "Planted date is missing for ${plot.plotLabel}. Growth stage and harvest readiness cannot be calculated without guessing.",
                            severity = IssueSeverity.WARNING
                        )
                    )
                } else {
                    val parsed = runCatching { LocalDate.parse(plot.plantedDate.take(10)) }.getOrNull()
                    if (parsed == null) {
                        issues.add(
                            DssValidationIssue(
                                plotId = plot.id,
                                plotLabel = plot.plotLabel,
                                fieldName = "plantedDate",
                                message = "Invalid planted date format '${plot.plantedDate}' on ${plot.plotLabel}.",
                                severity = IssueSeverity.WARNING
                            )
                        )
                    } else if (parsed.isAfter(farmerData.currentDate)) {
                        issues.add(
                            DssValidationIssue(
                                plotId = plot.id,
                                plotLabel = plot.plotLabel,
                                fieldName = "plantedDate",
                                message = "Planted date '${plot.plantedDate}' is in the future for ${plot.plotLabel}.",
                                severity = IssueSeverity.WARNING
                            )
                        )
                    }
                }
            }
        }

        return issues
    }
}
