package com.maptanim.app.dss.knowledgebase

import com.maptanim.app.data.local.dao.DssRuleDao
import com.maptanim.app.data.local.entity.DssRuleEntity
import com.maptanim.app.data.local.seeds.CropCompanionSeeds
import com.maptanim.app.domain.model.CompanionRelation

/**
 * A companion planting relationship entry.
 */
data class CompanionEntry(
    val cropA: String,
    val cropB: String,
    val relationship: CompanionRelation,
    val reason: String
)

/**
 * Converts a Room [DssRuleEntity] from the `dss_rules` table into a [CompanionEntry].
 */
fun DssRuleEntity.toCompanionEntry(): CompanionEntry = CompanionEntry(
    cropA = cropA,
    cropB = cropB,
    relationship = try {
        CompanionRelation.valueOf(relationship.uppercase().trim())
    } catch (_: Exception) {
        CompanionRelation.NEUTRAL
    },
    reason = reason ?: ""
)

/**
 * Standalone data provider providing companion planting relationship lookups,
 * backed dynamically by the Room `dss_rules` table and synchronized with remote
 * agricultural rules.
 *
 * Provides lookup and query functions used by:
 *  - Monitoring Dashboard -> Companions panel
 *  - DssEngine / CompanionEvaluator -> Companion compatibility & conflict detection
 *  - Edit Mode / Plan Tab -> Companion compatibility badges
 */
object CompanionDataProvider {

    @Volatile
    private var dssRuleDao: DssRuleDao? = null

    @Volatile
    private var cachedMatrix: List<CompanionEntry>? = null

    /**
     * Initializes the provider with Room's [DssRuleDao] to enable database-backed lookups.
     */
    fun initialize(dao: DssRuleDao) {
        dssRuleDao = dao
        refreshCache()
    }

    /**
     * Refreshes the in-memory cache directly from the Room `dss_rules` table.
     */
    fun refreshCache() {
        val dao = dssRuleDao ?: return
        try {
            val rules = dao.getAllRules()
            if (rules.isNotEmpty()) {
                cachedMatrix = rules.map { it.toCompanionEntry() }
            }
        } catch (_: Exception) {
            // Keep existing cache if query fails during startup
        }
    }

    /**
     * Returns the active companion matrix, prioritizing the Room DB cache.
     */
    val activeCompanionMatrix: List<CompanionEntry>
        get() = cachedMatrix ?: run {
            dssRuleDao?.let { dao ->
                try {
                    val rules = dao.getAllRules()
                    if (rules.isNotEmpty()) {
                        val mapped = rules.map { it.toCompanionEntry() }
                        cachedMatrix = mapped
                        return@run mapped
                    }
                } catch (_: Exception) {
                    // Fall back to seed defaults
                }
            }
            CropCompanionSeeds.allCompanionEntries
        }

    val defaultCompanionMatrix: List<CompanionEntry>
        get() = CropCompanionSeeds.allCompanionEntries

    val companionMatrix: List<CompanionEntry>
        get() = activeCompanionMatrix

    fun updateMatrix(entries: List<CompanionEntry>) {
        cachedMatrix = entries
    }

    /**
     * Returns the relationship between two crops.
     * Order-independent: getRelationship("Tomato", "Carrot") == getRelationship("Carrot", "Tomato")
     * Queries Room `dss_rules` directly, falling back to cached or seed matrix.
     */
    fun getRelationship(cropA: String, cropB: String): CompanionEntry? {
        val a = cropA.lowercase().trim()
        val b = cropB.lowercase().trim()

        // 1. Direct Room query if DAO is available
        dssRuleDao?.let { dao ->
            try {
                val entity = dao.findRelationship(cropA, cropB)
                if (entity != null) return entity.toCompanionEntry()
            } catch (_: Exception) {
                // Fallback to active matrix
            }
        }

        // 2. Fallback to active matrix
        return activeCompanionMatrix.firstOrNull { entry ->
            (entry.cropA.lowercase() == a && entry.cropB.lowercase() == b) ||
            (entry.cropA.lowercase() == b && entry.cropB.lowercase() == a)
        }
    }

    /**
     * Returns all known companion relationships for a given crop.
     */
    fun getCompanionsFor(cropName: String): List<CompanionEntry> {
        val name = cropName.lowercase().trim()

        // 1. Query Room DAO if available
        dssRuleDao?.let { dao ->
            try {
                val entities = dao.findRulesForCrop(cropName)
                if (entities.isNotEmpty()) return entities.map { it.toCompanionEntry() }
            } catch (_: Exception) {
                // Fallback to active matrix
            }
        }

        // 2. Fallback to active matrix
        return activeCompanionMatrix.filter { entry ->
            entry.cropA.lowercase() == name || entry.cropB.lowercase() == name
        }
    }

    /**
     * Returns beneficial companions for a given crop.
     */
    fun getBeneficialCompanions(cropName: String): List<String> {
        val name = cropName.lowercase().trim()
        return getCompanionsFor(cropName)
            .filter { it.relationship == CompanionRelation.BENEFICIAL }
            .map { if (it.cropA.lowercase() == name) it.cropB else it.cropA }
    }

    /**
     * Returns antagonist (avoid) crops for a given crop.
     */
    fun getAntagonistCrops(cropName: String): List<String> {
        val name = cropName.lowercase().trim()
        return getCompanionsFor(cropName)
            .filter { it.relationship == CompanionRelation.ANTAGONIST }
            .map { if (it.cropA.lowercase() == name) it.cropB else it.cropA }
    }
}
