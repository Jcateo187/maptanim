package com.maptanim.app.features.farm.companion

import androidx.compose.ui.geometry.Offset
import com.maptanim.app.domain.model.CompanionRelation
import com.maptanim.app.dss.knowledgebase.CompanionDataProvider
import com.maptanim.app.features.farm.components.getCropFamily
import com.maptanim.app.features.farm.renderer.model.CropZoneRenderData
import com.maptanim.app.features.farm.renderer.model.realLifeCropDiameterM
import kotlin.math.hypot
import kotlin.math.max

enum class NeighborStatus {
    BENEFICIAL,  // Good companion (Shield icon, Green glowing zone)
    ANTAGONIST,  // Bad neighbor (Warning icon, Red glowing zone)
    NEUTRAL      // Normal (No badge)
}

data class CropNeighborAnalysis(
    val status: NeighborStatus,
    val primaryCrop: String,
    val neighborCrop: String? = null,
    val distanceM: Float = 0f,
    val reason: String = "",
    val headline: String = "",
    val relationship: CompanionRelation = CompanionRelation.NEUTRAL
)

data class CompanionProximityAlert(
    val id: String,
    val status: NeighborStatus,
    val headline: String,
    val cropsSubtitle: String,
    val explanation: String,
    val distanceM: Float,
    val timestamp: Long = System.currentTimeMillis()
)

object FarmCompanionManager {

    /**
     * Interaction range in meters: crops within this distance interact through
     * root exudates (rhizosphere), shared pest vectors, and microclimate.
     */
    const val MAX_INTERACTION_DISTANCE_M = 1.0f

    /**
     * Minimum distance to be considered safe from bad neighbor effects.
     * Beyond this distance ("too long / far away"), negative interactions disappear.
     */
    const val SAFE_SEPARATION_DISTANCE_M = 1.2f

    /**
     * Evaluates relationship between cropA and cropB.
     * Uses DA-BPI CompanionDataProvider and supplements with botanical family conflict detection.
     */
    fun evaluatePair(cropA: String, cropB: String): Pair<CompanionRelation, String> {
        val cleanA = cropA.trim()
        val cleanB = cropB.trim()
        if (cleanA.isBlank() || cleanB.isBlank() || cleanA.equals(cleanB, ignoreCase = true) ||
            cleanA.equals("Bed", ignoreCase = true) || cleanB.equals("Bed", ignoreCase = true)
        ) {
            return Pair(CompanionRelation.NEUTRAL, "")
        }

        // 1. Direct research-backed lookup
        val entry = CompanionDataProvider.getRelationship(cleanA, cleanB)
        if (entry != null && entry.relationship != CompanionRelation.NEUTRAL) {
            return Pair(entry.relationship, entry.reason)
        }

        // 2. Botanical Family conflict check (e.g., both Solanaceae or both Cucurbitaceae)
        val famA = getCropFamily(cleanA)
        val famB = getCropFamily(cleanB)
        if (famA.scientificName == famB.scientificName && famA.scientificName != "Horticultural") {
            val familyReason = when (famA.scientificName) {
                "Solanaceae" -> "Both belong to Solanaceae (Nightshade family). They compete for identical nutrients and share aggressive insect vectors (fruit borer) and bacterial wilt (Ralstonia solanacearum)."
                "Cucurbitaceae" -> "Both belong to Cucurbitaceae (Gourd family). They share powdery mildew vectors, fruit flies, and compete for surface vine space."
                "Brassicaceae" -> "Both belong to Brassicaceae (Cabbage/Mustard family) sharing diamondback moth larvae and flea beetle pressure."
                else -> "Both belong to the same botanical family (${famA.scientificName}) and compete for identical nutrient bands while sharing pests."
            }
            return Pair(CompanionRelation.ANTAGONIST, familyReason)
        }

        return Pair(CompanionRelation.NEUTRAL, "No documented antagonism or active synergy. Standard planting spacing applies.")
    }

    /**
     * Calculates center-to-center and edge-to-edge distance between two positions in meters.
     */
    fun calculateEdgeDistance(
        posA: Offset, diamA: Float,
        posB: Offset, diamB: Float
    ): Float {
        val centerA = Offset(posA.x + diamA / 2f, posA.y + diamA / 2f)
        val centerB = Offset(posB.x + diamB / 2f, posB.y + diamB / 2f)
        val centerDist = hypot(centerA.x - centerB.x, centerA.y - centerB.y)
        val edgeGap = max(0f, centerDist - (diamA / 2f) - (diamB / 2f))
        return edgeGap
    }

    /**
     * Analyzes an existing placed crop zone against all other crops in the garden.
     * Prioritizes ANTAGONIST warnings, then BENEFICIAL synergies.
     */
    fun analyzePlacedCrop(
        target: CropZoneRenderData,
        allZones: List<CropZoneRenderData>
    ): CropNeighborAnalysis {
        val cropName = target.cropName ?: return CropNeighborAnalysis(NeighborStatus.NEUTRAL, "")
        if (cropName.equals("Bed", ignoreCase = true)) {
            return CropNeighborAnalysis(NeighborStatus.NEUTRAL, cropName)
        }

        val targetDiam = realLifeCropDiameterM(cropName)
        val targetPos = Offset(target.offsetX, target.offsetY)

        var worstAntagonist: Pair<CropZoneRenderData, Pair<CompanionRelation, String>>? = null
        var minAntagonistDist = Float.MAX_VALUE

        var bestBeneficial: Pair<CropZoneRenderData, Pair<CompanionRelation, String>>? = null
        var minBeneficialDist = Float.MAX_VALUE

        for (other in allZones) {
            if (other.id == target.id) continue
            val otherName = other.cropName ?: continue
            if (otherName.isBlank() || otherName.equals("Bed", ignoreCase = true)) continue

            val otherDiam = realLifeCropDiameterM(otherName)
            val otherPos = Offset(other.offsetX, other.offsetY)
            val edgeDist = calculateEdgeDistance(targetPos, targetDiam, otherPos, otherDiam)

            if (edgeDist <= MAX_INTERACTION_DISTANCE_M) {
                val (rel, reason) = evaluatePair(cropName, otherName)
                if (rel == CompanionRelation.ANTAGONIST) {
                    if (edgeDist < minAntagonistDist) {
                        minAntagonistDist = edgeDist
                        worstAntagonist = Pair(other, Pair(rel, reason))
                    }
                } else if (rel == CompanionRelation.BENEFICIAL) {
                    if (edgeDist < minBeneficialDist) {
                        minBeneficialDist = edgeDist
                        bestBeneficial = Pair(other, Pair(rel, reason))
                    }
                }
            }
        }

        if (worstAntagonist != null) {
            val neighborName = worstAntagonist.first.cropName ?: ""
            return CropNeighborAnalysis(
                status = NeighborStatus.ANTAGONIST,
                primaryCrop = cropName,
                neighborCrop = neighborName,
                distanceM = minAntagonistDist,
                reason = worstAntagonist.second.second,
                headline = "Bad Neighbor Alert: $cropName & $neighborName are too close (${String.format("%.2fm", minAntagonistDist)})",
                relationship = CompanionRelation.ANTAGONIST
            )
        }

        if (bestBeneficial != null) {
            val neighborName = bestBeneficial.first.cropName ?: ""
            return CropNeighborAnalysis(
                status = NeighborStatus.BENEFICIAL,
                primaryCrop = cropName,
                neighborCrop = neighborName,
                distanceM = minBeneficialDist,
                reason = bestBeneficial.second.second,
                headline = "Beneficial Synergy: $cropName 🤝 $neighborName (${String.format("%.2fm", minBeneficialDist)})",
                relationship = CompanionRelation.BENEFICIAL
            )
        }

        return CropNeighborAnalysis(
            status = NeighborStatus.NEUTRAL,
            primaryCrop = cropName
        )
    }

    /**
     * Real-time analysis for a crop being dragged over the canvas.
     */
    fun analyzeDragHover(
        dragCropName: String,
        hoverWorldPos: Offset,
        allZones: List<CropZoneRenderData>
    ): CropNeighborAnalysis? {
        if (dragCropName.isBlank() || dragCropName.equals("Bed", ignoreCase = true)) return null

        val dragDiam = realLifeCropDiameterM(dragCropName)

        var worstAntagonist: Pair<CropZoneRenderData, Pair<CompanionRelation, String>>? = null
        var minAntagonistDist = Float.MAX_VALUE

        var bestBeneficial: Pair<CropZoneRenderData, Pair<CompanionRelation, String>>? = null
        var minBeneficialDist = Float.MAX_VALUE

        for (other in allZones) {
            val otherName = other.cropName ?: continue
            if (otherName.isBlank() || otherName.equals("Bed", ignoreCase = true)) continue

            val otherDiam = realLifeCropDiameterM(otherName)
            val otherPos = Offset(other.offsetX, other.offsetY)
            val edgeDist = calculateEdgeDistance(hoverWorldPos, dragDiam, otherPos, otherDiam)

            if (edgeDist <= MAX_INTERACTION_DISTANCE_M) {
                val (rel, reason) = evaluatePair(dragCropName, otherName)
                if (rel == CompanionRelation.ANTAGONIST) {
                    if (edgeDist < minAntagonistDist) {
                        minAntagonistDist = edgeDist
                        worstAntagonist = Pair(other, Pair(rel, reason))
                    }
                } else if (rel == CompanionRelation.BENEFICIAL) {
                    if (edgeDist < minBeneficialDist) {
                        minBeneficialDist = edgeDist
                        bestBeneficial = Pair(other, Pair(rel, reason))
                    }
                }
            }
        }

        if (worstAntagonist != null) {
            val neighborName = worstAntagonist.first.cropName ?: ""
            return CropNeighborAnalysis(
                status = NeighborStatus.ANTAGONIST,
                primaryCrop = dragCropName,
                neighborCrop = neighborName,
                distanceM = minAntagonistDist,
                reason = worstAntagonist.second.second,
                headline = "⚠️ Bad Neighbor: $dragCropName & $neighborName are too close (${String.format("%.2fm", minAntagonistDist)})",
                relationship = CompanionRelation.ANTAGONIST
            )
        }

        if (bestBeneficial != null) {
            val neighborName = bestBeneficial.first.cropName ?: ""
            return CropNeighborAnalysis(
                status = NeighborStatus.BENEFICIAL,
                primaryCrop = dragCropName,
                neighborCrop = neighborName,
                distanceM = minBeneficialDist,
                reason = bestBeneficial.second.second,
                headline = "🛡️ Companion Synergy: $dragCropName & $neighborName (${String.format("%.2fm", minBeneficialDist)})",
                relationship = CompanionRelation.BENEFICIAL
            )
        }

        return null
    }
}
