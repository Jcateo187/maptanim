package com.maptanim.app.features.farm.schedule

/**
 * Data structure representing post-harvest soil rehabilitation and
 * crop rotation recommendations based on Philippine BPI / DA agronomic science.
 */
data class CropRotationRecommendation(
    val currentCropName: String,
    val botanicalFamily: String,
    val soilImpactSummary: String,
    val recommendedNextCrops: List<String>,
    val avoidNextCrops: List<String>,
    val rotationReasonTagalog: String,
    val bedRehabSteps: List<String>
)

object CropRotationEngine {

    fun getRecommendation(cropName: String): CropRotationRecommendation {
        val specific = detectSpecificVegetable(cropName)

        return when (specific) {
            SpecificVegetable.TOMATO -> CropRotationRecommendation(
                currentCropName = "Kamatis (Tomato)",
                botanicalFamily = "Pamilyang Solanaceae (Nightshades)",
                soilImpactSummary = "Mataas ang nakunsumong Nitrogen at Potassium; nag-iiwan ng panganib ng Bacterial Wilt at root-knot nematodes.",
                recommendedNextCrops = listOf("Sitaw (String Beans)", "Pechay", "Litsugas (Lettuce)", "Sibuyas Dahon (Green Onion)"),
                avoidNextCrops = listOf("Talong", "Siling Atsal (Bell Pepper)", "Patatas (Potato)"),
                rotationReasonTagalog = "Ang Sitaw (Legumes) ay nagbabalik ng Nitrogen sa lupa sa pamamagitan ng rhizobia nodules. Ang Sibuyas Dahon naman ay may natural na allicin na pumupuksa sa nematodes.",
                bedRehabSteps = listOf(
                    "Bunutin ang lumang puno at ugat ng kamatis. Kung may bacterial wilt o virus, sunugin o itapon sa labas ng hardin.",
                    "Bungkalin ang lupa sa lalim na 20cm at hayaang maaarawan (soil solarization) nang 3–5 araw.",
                    "Maghalo ng 2–3 pala ng sariwang vermicompost at 30% Carbonized Rice Hull (CRH) bago magtanim ng sitaw."
                )
            )

            SpecificVegetable.EGGPLANT -> CropRotationRecommendation(
                currentCropName = "Talong (Eggplant)",
                botanicalFamily = "Pamilyang Solanaceae (Nightshades)",
                soilImpactSummary = "Malalim ang kinuhang sustansya sa lupa sa loob ng 4-6 na buwang pamumunga; nananatili ang uod ng Fruit & Shoot Borer sa mga tuyong tangkay.",
                recommendedNextCrops = listOf("Sitaw (String Beans)", "Mais (Corn)", "Pechay", "Sibuyas Dahon"),
                avoidNextCrops = listOf("Kamatis", "Siling Atsal", "Patatas"),
                rotationReasonTagalog = "Magtanim ng Sitaw upang magpakain ng nitrogen sa lupa, o Sibuyas Dahon upang linisin ang lupa sa mga fungal spores.",
                bedRehabSteps = listOf(
                    "Hukayin at bunutin ang matitigas na ugat ng talong upang hindi maging pugad ng mga insekto.",
                    "Pahanginan ang lupa at maglagay ng apog (agricultural lime) kung naging maasim ang lupa dulot ng madalas na pagdidilig.",
                    "Maglagay ng sariwang compost tea at dumi ng baka/manok na lubusang nabulok."
                )
            )

            SpecificVegetable.BELL_PEPPER -> CropRotationRecommendation(
                currentCropName = "Siling Atsal (Bell Pepper)",
                botanicalFamily = "Pamilyang Solanaceae (Nightshades)",
                soilImpactSummary = "Katamtaman hanggang mataas ang kinuhang Calcium at Potassium; sensitibo sa Anthracnose spores sa lupa.",
                recommendedNextCrops = listOf("Sitaw (String Beans)", "Pechay", "Sibuyas Dahon (Green Onion)"),
                avoidNextCrops = listOf("Kamatis", "Talong", "Patatas"),
                rotationReasonTagalog = "Ang pagpapalit sa Sitaw o mga dahong gulay ay pumuputol sa siklo ng Anthracnose at Phytophthora root rot.",
                bedRehabSteps = listOf(
                    "Alisin ang lahat ng lagas na dahon at nabulok na bunga sa ibabaw ng mulch.",
                    "Buhaghagin ang lupa at maglagay ng abo ng kahoy (wood ash) para sa natural na potassium at disinfectant.",
                    "Dagdagan ng organikong pataba at ihanda ang tudling para sa sumunod na legumbre."
                )
            )

            SpecificVegetable.POTATO -> CropRotationRecommendation(
                currentCropName = "Patatas (Potato)",
                botanicalFamily = "Pamilyang Solanaceae (Root Tubers)",
                soilImpactSummary = "Napakataas ang nahigop na Potassium; nag-iiwan ng buhaghag ngunit pagod na ridge na posibleng may spores ng Late Blight.",
                recommendedNextCrops = listOf("Sitaw (String Beans)", "Pechay", "Repolyo (Cabbage)"),
                avoidNextCrops = listOf("Kamatis", "Talong", "Karot"),
                rotationReasonTagalog = "Iwasan ang pagtatanim ng kamatis agad sa pinag-anihan ng patatas dahil magkapareho sila ng Late Blight pathogen. Sitaw ang pinakamabisang pananim.",
                bedRehabSteps = listOf(
                    "Pulutin ang lahat ng maliliit na 'marble potatoes' upang hindi tumubo muli bilang damo (volunteer plants).",
                    "Pantayin muli ang mga ridge at haluan ng 30% composted dayami at dumi ng baka.",
                    "Mag-drench ng Trichoderma o Indigenous Microorganisms (IMO) laban sa soil-borne fungi."
                )
            )

            SpecificVegetable.STRING_BEANS -> CropRotationRecommendation(
                currentCropName = "Sitaw (Yardlong String Beans)",
                botanicalFamily = "Pamilyang Fabaceae / Leguminosae (Legumes)",
                soilImpactSummary = "Napakayaman ang iniwang lupa sa bio-available Nitrogen mula sa root nodules! Napakagandang pundasyon para sa susunod na tanim.",
                recommendedNextCrops = listOf("Repolyo (Cabbage)", "Pechay", "Kamatis (Tomato)", "Mais (Corn)"),
                avoidNextCrops = listOf("Sitaw", "Bataw", "Patani (ibang legumes)"),
                rotationReasonTagalog = "Dahil mayaman na sa natural na Nitrogen ang kama, perpekto itong taniman ng mga 'Heavy Feeders' tulad ng Repolyo, Pechay, o Kamatis nang hindi na kailangan ng maraming sintetikong abono.",
                bedRehabSteps = listOf(
                    "Gupitin ang baging sa ibabaw ng lupa ngunit IWANAN ANG MGA UGAT sa ilalim upang mabulok at magsilbing pataba ang mga nitrogen nodules.",
                    "Gamitin ang mga pinutol na baging ng sitaw bilang sariwang mulch o isama sa compost bin.",
                    "Maglagay ng manipis na layer ng vermicast sa ibabaw at handa na agad para sa punla ng repolyo o pechay."
                )
            )

            SpecificVegetable.PECHAY -> CropRotationRecommendation(
                currentCropName = "Pechay (Bok Choy)",
                botanicalFamily = "Pamilyang Brassicaceae (Crucifers)",
                soilImpactSummary = "Mabilis na kumonsumo ng mababaw na Nitrogen; nag-iiwan ng malinis na kama ngunit may panganib ng flea beetle at soft rot.",
                recommendedNextCrops = listOf("Karot (Carrot)", "Sitaw (String Beans)", "Sibuyas Dahon (Green Onion)"),
                avoidNextCrops = listOf("Repolyo", "Mustasa", "Broccoli"),
                rotationReasonTagalog = "Ang Karot (Root Crop) ay kukuha ng sustansya sa mas malalim na bahagi ng lupa na hindi naabot ng mababaw na ugat ng pechay. Ang Sibuyas Dahon naman ay nagtataboy ng peste ng brassica.",
                bedRehabSteps = listOf(
                    "Linisin ang mga natirang ugat at nabulok na panlabas na dahon sa kama.",
                    "Bungkalin nang malalim (hanggang 25cm) lalo na kung magtatanim ng karot upang walang sagabal ang taproot.",
                    "Maglagay ng buhaghag na buhangin at compost tea bago maghasik ng bagong binhi."
                )
            )

            SpecificVegetable.CABBAGE -> CropRotationRecommendation(
                currentCropName = "Repolyo (Cabbage)",
                botanicalFamily = "Pamilyang Brassicaceae (Crucifers)",
                soilImpactSummary = "Heavy Feeder; humigop ng malaking bulto ng Nitrogen at Potassium sa loob ng 70–90 araw.",
                recommendedNextCrops = listOf("Sitaw (String Beans)", "Karot (Carrot)", "Sibuyas Dahon"),
                avoidNextCrops = listOf("Pechay", "Mustasa", "Broccoli", "Cauliflower"),
                rotationReasonTagalog = "Huwag muling magtanim ng anumang kauri ng repolyo upang mapuksa ang Diamondback Moth (DBM) at maiwasan ang Clubroot disease. Sitaw ang magbabalik ng lakas sa lupa.",
                bedRehabSteps = listOf(
                    "Bunutin ang buong tuod at ugat ng repolyo. Huwag i-compost ang mga tuod na may bukol (clubroot).",
                    "Maglagay ng agricultural lime kung mababa ang pH ng lupa.",
                    "Pahanginan ang kama nang 5 araw at lagyan ng masaganang compost bago magtanim ng sitaw o ugat."
                )
            )

            SpecificVegetable.LETTUCE -> CropRotationRecommendation(
                currentCropName = "Litsugas (Lettuce)",
                botanicalFamily = "Pamilyang Asteraceae (Compositae)",
                soilImpactSummary = "Magaan ang naging konsumo sa lupa; mababaw ang ugat kaya sariwa pa ang ilalim na sustansya.",
                recommendedNextCrops = listOf("Karot (Carrot)", "Sitaw (String Beans)", "Kamatis (Tomato)"),
                avoidNextCrops = listOf("Litsugas (Lettuce back-to-back)"),
                rotationReasonTagalog = "Maaaring sundan ng mga gulay na may malalim na ugat (Karot) o mamumungang gulay (Kamatis/Talong) dahil hindi naubos ng litsugas ang sustansya sa ilalim.",
                bedRehabSteps = listOf(
                    "Alisin ang mga tuyong dahon sa ibabaw ng mulch.",
                    "Bahagyang buhaghagin ang unang 10cm ng lupa.",
                    "Mag-drench ng vermicast extract para handa agad sa bagong tanim."
                )
            )

            SpecificVegetable.CARROT -> CropRotationRecommendation(
                currentCropName = "Karot (Carrot)",
                botanicalFamily = "Pamilyang Apiaceae / Umbelliferae (Root Crops)",
                soilImpactSummary = "Humigop ng Potassium at Phosphorus sa kailaliman ng lupa; nag-iwan ng malalim na buhaghag na istraktura.",
                recommendedNextCrops = listOf("Sitaw (String Beans)", "Pechay", "Litsugas (Lettuce)", "Sibuyas Dahon"),
                avoidNextCrops = listOf("Karot", "Labanos (Radish)"),
                rotationReasonTagalog = "Magtanim ng mababaw na leafy greens (Pechay) o Legumes (Sitaw) upang pakinabangan ang maluwag na lupa nang hindi nagpapasok ng root parasites.",
                bedRehabSteps = listOf(
                    "Siguraduhing walang putol na piraso ng karot na naiwan sa ilalim ng lupa.",
                    "I-level muli ang matataas na ridge at lagyan ng organikong pataba sa ibabaw.",
                    "Takpan ng manipis na dayami laban sa pagkatuyo sa init ng araw."
                )
            )

            SpecificVegetable.GREEN_ONION -> CropRotationRecommendation(
                currentCropName = "Sibuyas Dahon / Scallion",
                botanicalFamily = "Pamilyang Amaryllidaceae / Alliaceae (Alliums)",
                soilImpactSummary = "Natural Soil Sanitizer! Ang allicin at sulfur compounds na inilabas ng ugat ay pumatay ng masasamang bacteria at nematodes sa lupa.",
                recommendedNextCrops = listOf("Kamatis (Tomato)", "Talong (Eggplant)", "Pipino (Cucumber)", "Kalabasa (Squash)"),
                avoidNextCrops = listOf("Sibuyas Dahon", "Bawang (Garlic)"),
                rotationReasonTagalog = "Ito ang pinakamagandang kama para sa Kamatis o Talong dahil nilinis ng sibuyas ang lupa laban sa bacterial wilt at root-knot nematodes!",
                bedRehabSteps = listOf(
                    "Alisin ang mga lumang tuyong balat at ugat ng sibuyas.",
                    "Magdagdag ng compost at buhaghagin ang lupa kasama ang Carbonized Rice Hull (CRH).",
                    "Handa na agad ang kama para sa mga maselan ngunit mataas ang halagang bungang-gulay."
                )
            )

            SpecificVegetable.BITTER_GOURD, SpecificVegetable.CUCUMBER, SpecificVegetable.SQUASH -> CropRotationRecommendation(
                currentCropName = when (specific) {
                    SpecificVegetable.BITTER_GOURD -> "Ampalaya (Bitter Gourd)"
                    SpecificVegetable.CUCUMBER -> "Pipino (Cucumber)"
                    else -> "Kalabasa (Squash)"
                },
                botanicalFamily = "Pamilyang Cucurbitaceae (Cucurbits / Vines)",
                soilImpactSummary = "Mataas ang kinonsumong Potassium at Tubig; nag-iiwan ng panganib ng Downy Mildew spores at Melon Fruit Fly pupae sa paligid.",
                recommendedNextCrops = listOf("Sitaw (String Beans)", "Pechay", "Repolyo", "Sibuyas Dahon"),
                avoidNextCrops = listOf("Ampalaya", "Pipino", "Kalabasa", "Pakwan", "Melon"),
                rotationReasonTagalog = "Huwag magtanim muli ng kahit anong baging na cucurbit sa parehong balag upang maputol ang Downy Mildew at fruit fly. Magtanim ng Sitaw o madahong Pechay.",
                bedRehabSteps = listOf(
                    "Baklasin ang mga tuyong baging mula sa balag at sunugin o ilayo sa taniman kung inatake ng mildew.",
                    "Bungkalin ang burol upang mabilad sa araw ang mga pupae ng fruit fly na nagtago sa lupa.",
                    "Maglagay ng masaganang compost at wood ash para sa sariwang pagpapataba."
                )
            )

            else -> CropRotationRecommendation(
                currentCropName = cropName,
                botanicalFamily = "Pangkaraniwang Pananim",
                soilImpactSummary = "Nagbawas ng pangkalahatang sustansya sa kama pagkatapos ng anihan.",
                recommendedNextCrops = listOf("Sitaw (Legumes)", "Pechay", "Sibuyas Dahon"),
                avoidNextCrops = listOf(cropName),
                rotationReasonTagalog = "Palitan ng ibang pamilya ng gulay upang mapanatili ang sigla ng lupa at maputol ang siklo ng mga insekto.",
                bedRehabSteps = listOf(
                    "Linisin ang mga natirang puno at damo.",
                    "Bungkalin at pasingawin ang lupa sa loob ng 3 araw.",
                    "Haluan ng compost at vermicast bago maglipat-tanim."
                )
            )
        }
    }
}
