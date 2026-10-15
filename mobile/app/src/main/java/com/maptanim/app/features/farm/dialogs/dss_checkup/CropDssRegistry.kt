package com.maptanim.app.features.farm.dialogs.dss_checkup

object CropDssRegistry {

    fun getCheckUpActivitiesForCrop(cropName: String): List<CheckUpActivity> {
        val clean = cropName.lowercase().trim()
        val isTomato = clean.contains("tomato") || clean.contains("kamatis")
        val isEggplant = clean.contains("eggplant") || clean.contains("talong")
        val isCabbage = clean.contains("cabbage") || clean.contains("repolyo")
        val isCarrot = clean.contains("carrot") || clean.contains("karot")
        val isSquash = clean.contains("squash") || clean.contains("kalabasa") || clean.contains("pumpkin")
        val isLettuce = clean.contains("lettuce") || clean.contains("litsugas") || clean.contains("letsugas")
        val isPechay = clean.contains("pechay") || clean.contains("petsay") || clean.contains("bok choy") || clean.contains("pak choi") || clean.contains("bokchoy")
        val isAmpalaya = clean.contains("ampalaya") || clean.contains("bitter") || clean.contains("gourd")
        val isCucumber = clean.contains("cucumber") || clean.contains("pipino")

        val isGreenOnion = clean.contains("green onion") || clean.contains("green_onion") || clean.contains("greenonion") ||
                clean.contains("scallion") || clean.contains("spring onion") || clean.contains("spring_onion") ||
                clean.contains("springonion") || clean.contains("dahon ng sibuyas") || clean.contains("dahonngsibuyas") ||
                clean.contains("sibuyas na mura") || clean.contains("sibuyasnamura") || clean.contains("bunching") ||
                clean.contains("onion") || clean.contains("sibuyas")

        val isPotato = clean.contains("potato") || clean.contains("patatas")
        val isSitaw = clean.contains("sitaw") || clean.contains("stringbean") || clean.contains("string bean") ||
                clean.contains("string_bean") || clean.contains("yardlong") || clean.contains("pole bean") ||
                clean.contains("pole_bean") || clean.contains("paayap") || clean.contains("beans") || clean.contains("bean")
        val isBellPepper = clean.contains("bell pepper") || clean.contains("bell_pepper") || clean.contains("bellpepper") ||
                clean.contains("sweet pepper") || clean.contains("sweet_pepper") || clean.contains("sweetpepper") ||
                clean.contains("siling atsal") || clean.contains("siling_atsal") || clean.contains("silingatsal") ||
                clean.contains("atsal") || clean.contains("siling pari") || clean.contains("silingpari")

        return when {
            isBellPepper -> getBellPepperCheckUpActivities()
            isSitaw -> getSitawCheckUpActivities()
            isPotato -> getPotatoCheckUpActivities()
            isGreenOnion -> getGreenOnionCheckUpActivities()
            isTomato -> getTomatoCheckUpActivities()
            isEggplant -> getEggplantCheckUpActivities()
            isCabbage -> getCabbageCheckUpActivities()
            isCarrot -> getCarrotCheckUpActivities()
            isSquash -> getSquashCheckUpActivities()
            isLettuce -> getLettuceCheckUpActivities()
            isPechay -> getPechayCheckUpActivities()
            isAmpalaya -> getAmpalayaCheckUpActivities()
            isCucumber -> getCucumberCheckUpActivities()
            else -> {
                val isStakingCrop = isTomato || isEggplant || isBellPepper || isAmpalaya || isSitaw || isCucumber
                getGeneralCheckUpActivities(cropName, isStakingCrop)
            }
        }
    }

    fun solveCropProblem(
        cropName: String,
        activityId: String,
        selectedOptions: Set<String>
    ): DirectCropSolution? {
        if (selectedOptions.isEmpty()) return null

        val clean = cropName.lowercase().trim()
        val isTomato = clean.contains("tomato") || clean.contains("kamatis")
        val isEggplant = clean.contains("eggplant") || clean.contains("talong")
        val isCabbage = clean.contains("cabbage") || clean.contains("repolyo")
        val isCarrot = clean.contains("carrot") || clean.contains("karot")
        val isSquash = clean.contains("squash") || clean.contains("kalabasa") || clean.contains("pumpkin")
        val isLettuce = clean.contains("lettuce") || clean.contains("litsugas") || clean.contains("letsugas")
        val isPechay = clean.contains("pechay") || clean.contains("petsay") || clean.contains("bok choy") || clean.contains("pak choi") || clean.contains("bokchoy")
        val isAmpalaya = clean.contains("ampalaya") || clean.contains("bitter") || clean.contains("gourd")
        val isCucumber = clean.contains("cucumber") || clean.contains("pipino")
        val isGreenOnion = clean.contains("green onion") || clean.contains("green_onion") || clean.contains("greenonion") ||
                clean.contains("scallion") || clean.contains("spring onion") || clean.contains("spring_onion") ||
                clean.contains("springonion") || clean.contains("dahon ng sibuyas") || clean.contains("dahonngsibuyas") ||
                clean.contains("sibuyas na mura") || clean.contains("sibuyasnamura") || clean.contains("bunching") ||
                clean.contains("onion") || clean.contains("sibuyas")

        val isPotato = clean.contains("potato") || clean.contains("patatas")
        val isSitaw = clean.contains("sitaw") || clean.contains("stringbean") || clean.contains("string bean") ||
                clean.contains("string_bean") || clean.contains("yardlong") || clean.contains("pole bean") ||
                clean.contains("pole_bean") || clean.contains("paayap") || clean.contains("beans") || clean.contains("bean")
        val isBellPepper = clean.contains("bell pepper") || clean.contains("bell_pepper") || clean.contains("bellpepper") ||
                clean.contains("sweet pepper") || clean.contains("sweet_pepper") || clean.contains("sweetpepper") ||
                clean.contains("siling atsal") || clean.contains("siling_atsal") || clean.contains("silingatsal") ||
                clean.contains("atsal") || clean.contains("siling pari") || clean.contains("silingpari")

        return when {
            isBellPepper -> solveBellPepperProblem(activityId, selectedOptions)
            isSitaw -> solveSitawProblem(activityId, selectedOptions)
            isPotato -> solvePotatoProblem(activityId, selectedOptions)
            isGreenOnion -> solveGreenOnionProblem(activityId, selectedOptions)
            isTomato -> solveTomatoProblem(activityId, selectedOptions)
            isEggplant -> solveEggplantProblem(activityId, selectedOptions)
            isCabbage -> solveCabbageProblem(activityId, selectedOptions)
            isCarrot -> solveCarrotProblem(activityId, selectedOptions)
            isSquash -> solveSquashProblem(activityId, selectedOptions)
            isLettuce -> solveLettuceProblem(activityId, selectedOptions)
            isPechay -> solvePechayProblem(activityId, selectedOptions)
            isAmpalaya -> solveAmpalayaProblem(activityId, selectedOptions)
            isCucumber -> solveCucumberProblem(activityId, selectedOptions)
            else -> solveGeneralCropProblem(cropName, activityId, selectedOptions)
        }
    }
}
