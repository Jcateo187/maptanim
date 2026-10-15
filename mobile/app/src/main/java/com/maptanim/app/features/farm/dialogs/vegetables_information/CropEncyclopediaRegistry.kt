package com.maptanim.app.features.farm.dialogs.vegetables_information

/**
 * Registry providing rich, research-based encyclopedia data for Philippine vegetable growers.
 * Dispatches to dedicated vegetable dossiers (Tomato, Eggplant, Cabbage, Carrot, Pechay, Sili, Kalabasa).
 */
object CropEncyclopediaRegistry {

    fun getCropEncyclopedia(cropName: String): CropEncyclopedia {
        val clean = cropName.lowercase().replace(" ", "").replace("_", "").replace("-", "")
        return when {
            clean.contains("greenonion") || clean.contains("scallion") || clean.contains("springonion") ||
                    clean.contains("dahonngsibuyas") || clean.contains("sibuyasnamura") || clean.contains("bunchingonion") -> greenOnionData
            clean.contains("tomato") || clean.contains("kamatis") -> tomatoData
            clean.contains("eggplant") || clean.contains("talong") -> eggplantData
            clean.contains("cabbage") || clean.contains("repolyo") -> cabbageData
            clean.contains("carrot") || clean.contains("karot") -> carrotData
            clean.contains("pechay") || clean.contains("petsay") || clean.contains("bokchoy") || clean.contains("pakchoi") || clean.contains("pakchoy") -> pechayData
            clean.contains("bellpepper") || clean.contains("sweetpepper") || clean.contains("silingatsal") ||
                    clean.contains("atsal") || clean.contains("silingpari") || clean.contains("pepper") ||
                    clean.contains("sili") || clean.contains("chili") -> bellPepperData
            clean.contains("squash") || clean.contains("kalabasa") || clean.contains("pumpkin") -> squashData
            clean.contains("lettuce") || clean.contains("litsugas") || clean.contains("letsugas") -> lettuceData
            clean.contains("ampalaya") || clean.contains("bittergourd") || clean.contains("bittermelon") || clean.contains("bitter") || clean.contains("gourd") -> ampalayaData
            clean.contains("cucumber") || clean.contains("pipino") -> cucumberData
            clean.contains("potato") || clean.contains("patatas") -> potatoData
            clean.contains("sitaw") || clean.contains("stringbean") || clean.contains("yardlong") || clean.contains("polebean") || clean.contains("paayap") || clean.contains("bean") -> sitawData
            clean.contains("onion") || clean.contains("sibuyas") -> greenOnionData
            else -> tomatoData.copy(
                cropName = cropName.replaceFirstChar { it.uppercase() },
                localName = cropName.replaceFirstChar { it.uppercase() }
            )
        }
    }
}
