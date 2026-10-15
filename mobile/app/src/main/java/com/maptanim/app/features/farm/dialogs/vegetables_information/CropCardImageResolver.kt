package com.maptanim.app.features.farm.dialogs.vegetables_information

import com.maptanim.app.R

/**
 * Image resolver for Vegetable Information cards (Pest/Disease, Companion Plants, Critters).
 * Maps domain items to high-detail photographic or illustrated assets from either
 * res/drawable or android_asset/metadata.
 */
object CropCardImageResolver {

    /**
     * Resolves a photo/image model (Drawable Int or asset URL string) for a Pest or Disease item.
     */
    fun resolvePestDiseaseImage(item: PestDiseaseItem): Any? {
        if (item.drawableResId != null) return item.drawableResId
        if (!item.imageUrl.isNullOrBlank()) return item.imageUrl

        val text = "${item.name} ${item.scientificName ?: ""} ${item.symptoms}".lowercase()

        return when {
            text.contains("clubroot") || text.contains("bukol sa ugat") || text.contains("plasmodiophora") ->
                R.drawable.ph_pest_clubroot

            text.contains("blossom") || text.contains("blossom-end") || text.contains("ber") || text.contains("tuyong puwit") ->
                R.drawable.ph_pest_blossom_rot

            text.contains("nematode") || text.contains("bulate sa ugat") || text.contains("meloidogyne") ->
                R.drawable.ph_pest_clubroot

            text.contains("diamondback") || text.contains("dbm") || text.contains("xylostella") ->
                "file:///android_asset/metadata/pest/Diamondback_moth.png"

            text.contains("bacterial wilt") || text.contains("ralstonia") || text.contains("lanta-bakterya") || text.contains("lanta bakterya") ||
            text.contains("soft rot") || text.contains("black rot") || text.contains("pectobacterium") || text.contains("erwinia") ||
            text.contains("bacterial spot") || text.contains("xanthomonas") ->
                "file:///android_asset/metadata/pest/Bacterial_wilt.png"

            text.contains("fruit borer") || text.contains("shoot borer") || text.contains("borer") ||
            text.contains("fruit fly") || text.contains("bactrocera") || text.contains("langaw") ||
            text.contains("leucinodes") || text.contains("helicoverpa") || text.contains("harabas") ||
            text.contains("cutworm") ->
                "file:///android_asset/metadata/pest/Fruit_borer.png"

            text.contains("leaf curl") || text.contains("tylcv") || text.contains("kulot") || text.contains("yellow leaf curl") ||
            text.contains("whitefly") || text.contains("puting langaw") || text.contains("bemisia") ->
                "file:///android_asset/metadata/pest/Tomato_leaf_curlvirus.png"

            text.contains("leafminer") || text.contains("leaf miner") || text.contains("liriomyza") ||
            text.contains("rust fly") || text.contains("leafhopper") || text.contains("amrasca") ->
                "file:///android_asset/metadata/pest/Vegetable_leafminer.png"

            text.contains("aphid") || text.contains("kuto") || text.contains("mite") || text.contains("hanip") ||
            text.contains("tetranychus") ->
                "file:///android_asset/metadata/pest/Melon_and_cotton_aphids.png"

            text.contains("anthracnose") || text.contains("blight") || text.contains("pasob") ||
            text.contains("alternaria") || text.contains("phomopsis") || text.contains("damping-off") ||
            text.contains("pythium") || text.contains("rhizoctonia") || text.contains("fusarium") ||
            text.contains("root rot") ->
                "file:///android_asset/metadata/pest/Chilli_anthracnose_fruit_rot.png"

            text.contains("powdery mildew") ->
                "file:///android_asset/metadata/pest/Powdery_mildew.png"

            text.contains("downy mildew") ->
                "file:///android_asset/metadata/pest/Cucurbit_downy_mildew.png"

            text.contains("flea beetle") || text.contains("fleabeetle") || text.contains("beetle") ||
            text.contains("aulacophora") || text.contains("epilachna") || text.contains("salagubang") ||
            text.contains("talon-talon") || text.contains("phyllotreta") ->
                "file:///android_asset/metadata/pest/Eggplant_and_brassica_fleabeetle.png"

            text.contains("thrip") || text.contains("thrips") ->
                "file:///android_asset/metadata/pest/Onion_thrips.png"

            text.contains("armyworm") || text.contains("looper") || text.contains("cabbage worm") ||
            text.contains("pieris") || text.contains("trichoplusia") || text.contains("worm") ->
                "file:///android_asset/metadata/pest/Fall_armyworm.png"

            else -> null
        }
    }

    /**
     * Resolves a photo/image model for companion and enemy plants.
     */
    fun resolvePlantRelationImage(item: PlantRelationItem): Any? {
        if (item.drawableResId != null) return item.drawableResId
        if (!item.imageUrl.isNullOrBlank()) return item.imageUrl

        val text = "${item.name} ${item.scientificName ?: ""}".lowercase()

        return when {
            text.contains("tomato") || text.contains("kamatis") ->
                "file:///android_asset/metadata/crops_images/tomato.png"
            text.contains("eggplant") || text.contains("talong") ->
                "file:///android_asset/metadata/crops_images/eggplant.png"
            text.contains("cabbage") || text.contains("repolyo") ->
                "file:///android_asset/metadata/crops_images/cabbage.png"
            text.contains("carrot") || text.contains("karot") ->
                "file:///android_asset/metadata/crops_images/carrot.png"
            text.contains("potato") || text.contains("patatas") ->
                "file:///android_asset/metadata/crops_images/potato.png"
            text.contains("green onion") || text.contains("scallion") || text.contains("spring onion") || text.contains("dahon ng sibuyas") || text.contains("onion") || text.contains("sibuyas") ->
                "file:///android_asset/metadata/crops_images/green_onion.png"
            text.contains("bell pepper") || text.contains("sweet pepper") || text.contains("atsal") || text.contains("pepper") || text.contains("sili") ->
                "file:///android_asset/metadata/crops_images/bell_pepper.png"
            text.contains("pechay") || text.contains("petsay") || text.contains("bokchoy") ->
                "file:///android_asset/metadata/crops_images/pechay.png"
            text.contains("lettuce") || text.contains("letsugas") || text.contains("litsugas") ->
                "file:///android_asset/metadata/crops_images/lettuce.png"
            text.contains("pipino") || text.contains("cucumber") ->
                "file:///android_asset/metadata/crops_images/pipino.png"
            text.contains("sitaw") || text.contains("beans") || text.contains("bean") || text.contains("yardlong") || text.contains("stringbean") || text.contains("legume") ->
                "file:///android_asset/metadata/crops_images/sitaw.png"
            text.contains("pumpkin") || text.contains("kalabasa") || text.contains("squash") ->
                "file:///android_asset/metadata/crops_images/pumpkin.png"
            text.contains("ampalaya") || text.contains("bitter") ->
                "file:///android_asset/metadata/crops_images/ampalaya.png"
            else -> null
        }
    }

    /**
     * Resolves a photo/image model for beneficial or harmful critters.
     */
    fun resolveCritterImage(item: CritterItem): Any? {
        if (item.drawableResId != null) return item.drawableResId
        if (!item.imageUrl.isNullOrBlank()) return item.imageUrl

        val text = "${item.name} ${item.role}".lowercase()

        return when {
            text.contains("ladybug") || text.contains("ladybird") || text.contains("lacewing") ||
            text.contains("hoverfly") || text.contains("syrphid") || text.contains("aphid") ->
                "file:///android_asset/metadata/pest/Melon_and_cotton_aphids.png"
            text.contains("moth") || text.contains("dbm") || text.contains("wasp") ||
            text.contains("diadegma") || text.contains("trichogramma") ->
                "file:///android_asset/metadata/pest/Diamondback_moth.png"
            text.contains("flea beetle") ->
                "file:///android_asset/metadata/pest/Eggplant_and_brassica_fleabeetle.png"
            text.contains("armyworm") || text.contains("looper") || text.contains("worm") ->
                "file:///android_asset/metadata/pest/Fall_armyworm.png"
            text.contains("borer") ->
                "file:///android_asset/metadata/pest/Fruit_borer.png"
            text.contains("thrip") ->
                "file:///android_asset/metadata/pest/Onion_thrips.png"
            text.contains("spider") || text.contains("gagamba") || text.contains("leafminer") ->
                "file:///android_asset/metadata/pest/Vegetable_leafminer.png"
            else -> null
        }
    }
}
