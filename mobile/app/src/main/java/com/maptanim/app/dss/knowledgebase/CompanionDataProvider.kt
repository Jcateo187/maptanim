package com.maptanim.app.dss.knowledgebase

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
 * Standalone data provider containing the full companion planting matrix
 * for Philippine vegetable crops, sourced from DA-BPI intercropping references.
 *
 * Provides lookup and query functions used by:
 *  - Monitoring Dashboard → Companions panel
 *  - DssEngine → CompanionPlantsMatrix
 *  - Edit Mode → Companion compatibility overlay
 */
object CompanionDataProvider {

    /**
     * Returns the relationship between two crops.
     * Order-independent: getRelationship("Tomato", "Carrot") == getRelationship("Carrot", "Tomato")
     */
    fun getRelationship(cropA: String, cropB: String): CompanionEntry? {
        val a = cropA.lowercase().trim()
        val b = cropB.lowercase().trim()
        return companionMatrix.firstOrNull { entry ->
            (entry.cropA.lowercase() == a && entry.cropB.lowercase() == b) ||
            (entry.cropA.lowercase() == b && entry.cropB.lowercase() == a)
        }
    }

    /**
     * Returns all known companion relationships for a given crop.
     */
    fun getCompanionsFor(cropName: String): List<CompanionEntry> {
        val name = cropName.lowercase().trim()
        return companionMatrix.filter { entry ->
            entry.cropA.lowercase() == name || entry.cropB.lowercase() == name
        }
    }

    /**
     * Returns beneficial companions for a given crop.
     */
    fun getBeneficialCompanions(cropName: String): List<String> {
        val name = cropName.lowercase().trim()
        return companionMatrix
            .filter { it.relationship == CompanionRelation.BENEFICIAL }
            .filter { it.cropA.lowercase() == name || it.cropB.lowercase() == name }
            .map { if (it.cropA.lowercase() == name) it.cropB else it.cropA }
    }

    /**
     * Returns antagonist (avoid) crops for a given crop.
     */
    fun getAntagonistCrops(cropName: String): List<String> {
        val name = cropName.lowercase().trim()
        return companionMatrix
            .filter { it.relationship == CompanionRelation.ANTAGONIST }
            .filter { it.cropA.lowercase() == name || it.cropB.lowercase() == name }
            .map { if (it.cropA.lowercase() == name) it.cropB else it.cropA }
    }

    // ── Full Companion Planting Matrix ────────────────────────────────────
    // Source: DA-BPI Philippine Intercropping Guidelines & Published Research

    val companionMatrix: List<CompanionEntry> = listOf(
        // ── TOMATO RELATIONSHIPS ──────────────────────────────────────────
        CompanionEntry(
            "Tomato", "Lettuce", CompanionRelation.BENEFICIAL,
            "Lettuce provides ground cover that retains soil moisture and suppresses weeds around tomato base. Tomato provides partial shade for heat-sensitive lettuce."
        ),
        CompanionEntry(
            "Tomato", "Carrot", CompanionRelation.BENEFICIAL,
            "Carrot's deep taproot loosens subsoil for tomato roots. Tomato's foliage provides partial shade that benefits carrot root development."
        ),
        CompanionEntry(
            "Tomato", "Onion", CompanionRelation.BENEFICIAL,
            "Onion's sulfur compounds repel aphids and whiteflies that attack tomato. Strong onion scent masks tomato from pest detection."
        ),
        CompanionEntry(
            "Tomato", "Eggplant", CompanionRelation.ANTAGONIST,
            "Both are Solanaceae family members competing for identical nutrients and sharing the same pests (fruit borer, bacterial wilt) and diseases."
        ),
        CompanionEntry(
            "Tomato", "Cabbage", CompanionRelation.ANTAGONIST,
            "Cabbage and tomato compete for similar nutrients. Cabbage can inhibit tomato growth through allelopathic root exudates."
        ),
        CompanionEntry(
            "Tomato", "Corn", CompanionRelation.ANTAGONIST,
            "Both are heavy nitrogen feeders competing for the same soil nutrients. Corn's tall canopy shades tomato excessively."
        ),

        // ── EGGPLANT RELATIONSHIPS ────────────────────────────────────────
        CompanionEntry(
            "Eggplant", "String Beans", CompanionRelation.BENEFICIAL,
            "String beans fix atmospheric nitrogen into the soil, directly benefiting nitrogen-hungry eggplant. Beans' climbing habit doesn't shade eggplant."
        ),
        CompanionEntry(
            "Eggplant", "Cucumber", CompanionRelation.NEUTRAL,
            "No significant positive or negative interaction. Can coexist if spacing is adequate, but no active synergy documented."
        ),
        CompanionEntry(
            "Eggplant", "Onion", CompanionRelation.BENEFICIAL,
            "Onion repels flea beetles and aphids that commonly attack eggplant foliage."
        ),

        // ── CUCUMBER RELATIONSHIPS ────────────────────────────────────────
        CompanionEntry(
            "Cucumber", "Corn", CompanionRelation.BENEFICIAL,
            "Classic Three Sisters principle — corn provides natural trellis for cucumber vines, cucumber provides ground cover reducing weed pressure."
        ),
        CompanionEntry(
            "Cucumber", "String Beans", CompanionRelation.BENEFICIAL,
            "Beans fix nitrogen benefiting cucumber growth. Both can share a trellis system efficiently."
        ),
        CompanionEntry(
            "Cucumber", "Lettuce", CompanionRelation.BENEFICIAL,
            "Lettuce serves as living mulch under cucumber trellis, conserving soil moisture. Cucumber provides shade for heat-sensitive lettuce."
        ),

        // ── CABBAGE RELATIONSHIPS ─────────────────────────────────────────
        CompanionEntry(
            "Cabbage", "Onion", CompanionRelation.BENEFICIAL,
            "Onion's strong scent masks cabbage from diamondback moth and cabbage looper. Onion acts as a natural pest deterrent border."
        ),
        CompanionEntry(
            "Cabbage", "String Beans", CompanionRelation.ANTAGONIST,
            "String beans' climbing habit can smother low-growing cabbage. Both compete for space and light in bed configurations."
        ),
        CompanionEntry(
            "Cabbage", "Lettuce", CompanionRelation.BENEFICIAL,
            "Lettuce and cabbage have complementary root depths. Lettuce matures faster, freeing space as cabbage heads develop."
        ),

        // ── ONION RELATIONSHIPS ───────────────────────────────────────────
        CompanionEntry(
            "Onion", "Carrot", CompanionRelation.BENEFICIAL,
            "Classic beneficial pair — carrot fly is repelled by onion scent, onion fly is repelled by carrot foliage. Mutually protective."
        ),
        CompanionEntry(
            "Onion", "String Beans", CompanionRelation.ANTAGONIST,
            "Onion's sulfur root exudates inhibit nitrogen-fixing bacteria on bean roots, reducing bean productivity."
        ),
        CompanionEntry(
            "Onion", "Pechay", CompanionRelation.BENEFICIAL,
            "Onion repels flea beetles that damage pechay leaves. Pechay matures quickly before onion needs full bed space."
        ),

        // ── LETTUCE RELATIONSHIPS ─────────────────────────────────────────
        CompanionEntry(
            "Lettuce", "Carrot", CompanionRelation.BENEFICIAL,
            "Lettuce's shallow roots and carrot's deep roots share soil space efficiently without competition. Lettuce provides ground shade."
        ),

        // ── CORN RELATIONSHIPS ────────────────────────────────────────────
        CompanionEntry(
            "Corn", "Squash", CompanionRelation.BENEFICIAL,
            "Three Sisters principle — squash's large leaves shade the ground, conserving moisture and suppressing weeds around corn stalks."
        ),
        CompanionEntry(
            "Corn", "Kangkong", CompanionRelation.NEUTRAL,
            "No significant interaction documented. Can coexist in adjacent plots without mutual benefit or harm."
        ),
        CompanionEntry(
            "Corn", "String Beans", CompanionRelation.BENEFICIAL,
            "Three Sisters principle — corn provides natural trellis for climbing beans, beans fix nitrogen for corn's heavy demand."
        ),

        // ── OKRA RELATIONSHIPS ────────────────────────────────────────────
        CompanionEntry(
            "Okra", "Tomato", CompanionRelation.BENEFICIAL,
            "Okra attracts beneficial insects (ladybugs, lacewings) that control aphids on adjacent tomato plants."
        ),
        CompanionEntry(
            "Okra", "Eggplant", CompanionRelation.NEUTRAL,
            "Both are warm-season crops that coexist without significant interaction. Adequate spacing required."
        ),
        CompanionEntry(
            "Okra", "Pechay", CompanionRelation.BENEFICIAL,
            "Okra's tall structure provides partial shade for heat-sensitive pechay during hot months."
        ),

        // ── SQUASH / PUMPKIN RELATIONSHIPS ────────────────────────────────
        CompanionEntry(
            "Squash", "Okra", CompanionRelation.NEUTRAL,
            "No significant interaction. Both are vigorous growers — ensure adequate spacing to prevent vine competition."
        ),
        CompanionEntry(
            "Squash", "String Beans", CompanionRelation.BENEFICIAL,
            "Beans fix nitrogen for squash, squash ground cover suppresses weeds around bean trellis base."
        ),

        // ── CHILI / SILI RELATIONSHIPS ────────────────────────────────────
        CompanionEntry(
            "Chili Pepper", "Carrot", CompanionRelation.BENEFICIAL,
            "Carrot's deep taproot improves soil aeration for chili's shallow root system. Different root zones avoid competition."
        ),
        CompanionEntry(
            "Chili Pepper", "Eggplant", CompanionRelation.ANTAGONIST,
            "Both are Solanaceae sharing identical disease vectors (bacterial wilt, anthracnose). Cross-infection risk is high."
        ),
        CompanionEntry(
            "Chili Pepper", "Onion", CompanionRelation.BENEFICIAL,
            "Onion repels aphids that transmit viral diseases to chili peppers."
        ),

        // ── KANGKONG RELATIONSHIPS ────────────────────────────────────────
        CompanionEntry(
            "Kangkong", "Eggplant", CompanionRelation.BENEFICIAL,
            "Kangkong serves as moisture-retaining ground cover under eggplant. Both thrive in moist conditions."
        ),
        CompanionEntry(
            "Kangkong", "Lettuce", CompanionRelation.NEUTRAL,
            "Both are fast-growing leafy crops. No interaction — can share adjacent beds without issue."
        ),

        // ── AMPALAYA RELATIONSHIPS ────────────────────────────────────────
        CompanionEntry(
            "Ampalaya", "Corn", CompanionRelation.BENEFICIAL,
            "Corn provides natural trellis support for ampalaya vines, reducing trellis material costs."
        ),
        CompanionEntry(
            "Ampalaya", "Onion", CompanionRelation.BENEFICIAL,
            "Onion's scent deters fruit flies and aphids that attack ampalaya vines and fruits."
        ),
        CompanionEntry(
            "Ampalaya", "Squash", CompanionRelation.ANTAGONIST,
            "Both are cucurbits sharing the same pests (fruit fly, downy mildew) and competing for identical vine space."
        ),

        // ── PECHAY ADDITIONAL ─────────────────────────────────────────────
        CompanionEntry(
            "Pechay", "Carrot", CompanionRelation.BENEFICIAL,
            "Pechay matures in 25–30 days, harvested before slow-growing carrot needs full bed space. Efficient succession planting."
        ),
        CompanionEntry(
            "Pechay", "Lettuce", CompanionRelation.NEUTRAL,
            "Similar growth habits and requirements. Can coexist but no synergistic benefit."
        )
    )
}
