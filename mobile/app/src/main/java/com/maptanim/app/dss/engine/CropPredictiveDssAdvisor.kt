package com.maptanim.app.dss.engine

/**
 * Deprecated: Replaced by evidence-based [BedAgronomicAdvisor].
 * Agronomic guidance is grounded in verified cultural protocols and companion science,
 * avoiding speculative predictive guessing.
 */
@Deprecated("Use BedAgronomicAdvisor for evidence-based stage guidance")
object CropPredictiveDssAdvisor {
    fun getPredictiveInsight(
        cropName: String,
        daysPlanted: Int,
        isWetSeason: Boolean = true
    ): AgronomicGuidanceInsight = BedAgronomicAdvisor.getStageGuidance(cropName, daysPlanted)
}
