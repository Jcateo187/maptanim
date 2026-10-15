package com.maptanim.app.features.farm.dialogs.vegetables_information

import androidx.compose.ui.graphics.Color
import com.maptanim.app.R

/**
 * Rich, research-backed encyclopedia data for Bell Pepper / Siling Atsal (Capsicum annuum var. grossum).
 * Tailored for Philippine highland (Benguet, Bukidnon) and cool upland/lowland growers
 * based on DA-BPI, PhilMech, East-West Seed, and MapTanim agronomic guidelines.
 */
val bellPepperData = CropEncyclopedia(
    cropName = "Bell Pepper",
    localName = "Bell Pepper / Siling Atsal (Capsicum annuum var. grossum)",
    scientificName = "Capsicum annuum var. grossum",
    category = "Solanaceous Fruit Vegetables (Solanaceae)",
    family = "Solanaceae",
    overview = "Ang Bell Pepper (Capsicum annuum var. grossum), na mas kilala sa Pilipinas bilang Siling Atsal, Siling Pari, o Siling Pang-ensalada, ay isang mataas ang halagang pananim (high-value commercial vegetable) mula sa pamilyang Solanaceae. Paborito itong sangkap sa pambansang lutuin tulad ng menudo, afritada, caldereta, chopsuey, at sariwang ensalada dahil sa makapal, malutong, at matamis nitong laman na walang anghang.\n\nKilala ang Benguet (La Trinidad, Buguias, Atok) at Bukidnon sa Mindanao bilang sentro ng produksyon ng de-kalidad na bell pepper dahil sa malamig na hanging bundok (18–25°C) na nagdudulot ng makakapal na pader ng bunga (thick pericarp walls) at matitingkad na kulay mula berde hanggang pula at dilaw. Maaari rin itong itanim sa kapatagan sa panahon ng amihan (Nobyembre hanggang Pebrero) gamit ang mga heat-tolerant hybrids.\n\nNangangailangan ang bell pepper ng matibay na tulos na kawayan (80–100 cm bamboo stakes) dahil marupok ang mga sanga nito at madaling mabali kapag bumigat ang mga bunga. Kritikal din ang regular na suplay ng Calcium at maingat na patubig upang maiwasan ang 'Blossom-End Rot' o tuyong puwit ng bunga.",
    selectedVariety = VarietyInfo(
        name = "Sultan F1",
        producer = "East-West Seed Philippines",
        type = "High-Yielding Commercial Hybrid",
        badge = "F1 Premium Hybrid",
        description = "Ang nangungunang commercial hybrid ng bell pepper sa Pilipinas. Kilala sa malalaki, hugis-blokeng bunga (blocky 3-4 lobes) na may makapal at malutong na laman, mataas na resistensya laban sa Bacterial Wilt (Ralstonia) at Potato Virus Y (PVY), at masaganang pamumunga sa loob ng mahigit 2 buwan."
    ),
    otherVarieties = listOf(
        "Sultan F1 (East-West Seed Commercial Standard)",
        "California Wonder (Classic Open-Pollinated Green-to-Red)",
        "Yolo Wonder (Bacterial Leaf Spot Tolerant)",
        "Red Baron F1 (Sweet Salad Thick-Walled Bell)",
        "Sweet Bell F1 (Highland Cool-Season Specialist)"
    ),
    carouselSlides = listOf(
        CarouselSlide(
            title = "Taniman ng Siling Atsal sa Benguet",
            subtitle = "Mabubulas na puno sa kamada na may laylay na malalaki at makikintab na bell peppers",
            stage = "Yugto ng Pamumunga (Fruiting Phase)",
            badgeColor = Color(0xFF2E7D32),
            drawableResId = R.drawable.ph_bell_pepper_hero
        ),
        CarouselSlide(
            title = "Pagpapatubo sa 104-Hole Seedling Tray",
            subtitle = "30–40 araw na malulusog na punla na may 5–6 na tunay na dahon bago ilipat sa kamada",
            stage = "Yugto ng Punla (Seedling Phase)",
            badgeColor = Color(0xFF1565C0),
            drawableResId = R.drawable.ph_bell_pepper_seedlings
        ),
        CarouselSlide(
            title = "Pamumulaklak at Pagtutulos ng Kawayan",
            subtitle = "Puting bulaklak sa sanga at 80–100 cm tulos upang suportahan ang mabibigat na bunga",
            stage = "Pamumulaklak at Pagtulos (Flowering & Staking)",
            badgeColor = Color(0xFFE65100),
            drawableResId = R.drawable.ph_bell_pepper_flowering
        ),
        CarouselSlide(
            title = "Pag-aani ng Sariwang Bell Pepper",
            subtitle = "Makakapal at makikintab na bungang pinitas gamit ang gunting na may 2 cm tangkay",
            stage = "Pang-araw-araw na Pag-ani (Harvest Phase)",
            badgeColor = Color(0xFFC62828),
            drawableResId = R.drawable.ph_bell_pepper_harvest
        )
    ),
    quickInfo = listOf(
        QuickInfoItem(
            key = "lifespan",
            label = "Buhay ng Halaman",
            value = "120–150 Araw",
            explanation = ExplanationData(
                title = "Siklo ng Buhay ng Bell Pepper",
                summary = "120 hanggang 150 araw mula pagpupunla sa tray hanggang sa huling pitas ng mga bunga.",
                details = listOf(
                    "Punlaan sa Tray: 30–40 araw bago ilipat-tanim sa kamada.",
                    "Pagsasanga at Pagtulos: 2–6 linggo pagkatapos maglipat.",
                    "Unang Pitas: 70–85 araw mula paglipat-tanim.",
                    "Tagal ng Pamimitas: Tuwing 5–7 araw sa loob ng 6–8 linggo."
                )
            )
        ),
        QuickInfoItem(
            key = "waterNeeds",
            label = "Pangangailangan sa Tubig",
            value = "Katamtaman (Drip/Furrow)",
            explanation = ExplanationData(
                title = "Pamamahala sa Patubig ng Bell Pepper",
                summary = "Nangangailangan ng regular at pantay na kahalumigmig nang hindi nalulunod ang ugat.",
                details = listOf(
                    "Dalas ng Patubig: 2–3 beses kada linggo sa umaga.",
                    "Sensitibo sa Baha: Mabilis malanta kapag naipon ang tubig nang mahigit 24 oras.",
                    "Kritikal na Yugto: Pamumulaklak at paglaki ng bunga para maiwasan ang pagkalagas.",
                    "Pag-iwas sa Amag: Drip o furrow irrigation ang gamitin; iwasan ang buhos sa dahon."
                )
            )
        ),
        QuickInfoItem(
            key = "sunlight",
            label = "Sikat ng Araw",
            value = "6–8 Oras (Full Sun)",
            explanation = ExplanationData(
                title = "Sikat ng Araw at Temperatura",
                summary = "Nangangailangan ng buong sikat ng araw ngunit sensitibo sa matinding init na higit 32°C.",
                details = listOf(
                    "Direktang Araw: Hindi bababa sa 6 hanggang 8 oras kada araw.",
                    "Tamang Temperatura: 18°C hanggang 28°C (mainam sa Benguet at Bukidnon).",
                    "Limitasyon sa Init: Nalalagas ang bulaklak kapag lumampas sa 32°C ang init sa tanghali.",
                    "Shade Net: Gumamit ng 30% black net sa mainit na kapatagan kung tag-araw."
                )
            )
        ),
        QuickInfoItem(
            key = "soilType",
            label = "Uri ng Lupa",
            value = "Sandy Loam (pH 5.5–6.8)",
            explanation = ExplanationData(
                title = "Pangangailangan sa Lupa at pH",
                summary = "Mayabong, buhaghag, at well-drained na loam o sandy loam na mayaman sa organikong pataba.",
                details = listOf(
                    "Tamang pH: 5.5 hanggang 6.8 (maglagay ng apog kung mas mababa sa 5.5).",
                    "Drainage: Kailangan ng 20–30 cm mataas na kamada lalo na sa tag-ulan.",
                    "Organikong Pataba: Ihalo ang 2–3 kg vermicast o nabulok na dumi bawat metro kwadrado.",
                    "Texture: Buhaghag upang malayang makahinga at kumalat ang mga ugat."
                )
            )
        ),
        QuickInfoItem(
            key = "spacing",
            label = "Agwat ng Tanim",
            value = "45–50 cm × 50–60 cm",
            explanation = ExplanationData(
                title = "Tamang Agwat at Densidad ng Pagtatanim",
                summary = "Sapat na distansya upang magkaroon ng magandang sirkulasyon ng hangin at hindi magkadikit ang mga sanga.",
                details = listOf(
                    "Pagitan ng Puno: 40 hanggang 50 sentimetro sa parehong hanay.",
                    "Pagitan ng Hanay: 50 hanggang 60 sentimetro sa pagitan ng mga tudling.",
                    "Lapad ng Kamada: 1 metro na may dalawang hanay (double row system).",
                    "Densidad: Tinatayang 25,000 hanggang 30,000 puno bawat ektarya."
                )
            )
        ),
        QuickInfoItem(
            key = "staking",
            label = "Tulos at Suporta",
            value = "80–100 cm Tulos Kawayan",
            explanation = ExplanationData(
                title = "Kahalagahan ng Tulos sa Bell Pepper",
                summary = "Dahil marupok ang mga sanga at mabigat ang bunga ng siling atsal, mandatory ang pagtutulos.",
                details = listOf(
                    "Uri ng Tulos: 80–100 cm na patpat na kawayan bawat puno.",
                    "Kailan Ilalagay: Sa mismong araw ng paglilipat-tanim o 1 linggo pagkaraan.",
                    "Paraan ng Pagtali: Talian ng maluwag na figure-8 gamit ang malambot na pisi.",
                    "Pakinabang: Pinipigilan ang pagtumba sa hangin at pagkasira ng mga sanga."
                )
            )
        )
    ),
    companionPlants = listOf(
        PlantRelationItem(
            name = "Albahaca (Sweet Basil)",
            scientificName = "Ocimum basilicum",
            isBeneficial = true,
            role = "Nagtataboy ng thrips, langaw, at hornworms; nagpapabango sa taniman",
            explanation = ExplanationData(
                title = "Albahaca bilang Kasama ng Bell Pepper",
                summary = "Ang malakas na aroma ng basil ay nagtataboy sa thrips at fruit borer habang humihikayat ng mga bubuyog.",
                details = listOf(
                    "Natural na panlaban sa thrips at aphids.",
                    "Umaakit ng honeybees para sa masaganang pamumunga.",
                    "Itanim sa pagitan ng bawat 3–4 puno ng siling atsal."
                )
            )
        ),
        PlantRelationItem(
            name = "Sibuyas at Bawang (Onion & Garlic)",
            scientificName = "Allium cepa / sativum",
            isBeneficial = true,
            role = "Natural na allium sulfur aroma laban sa aphids at fungal spores",
            explanation = ExplanationData(
                title = "Bakit Magandang Katabi ang Sibuyas?",
                summary = "Ang natural na asupre (sulfur) ng allium ay nagtataboy sa mga sumisipsip na insekto.",
                details = listOf(
                    "Nagtataboy sa aphids, whiteflies, at spider mites.",
                    "Tumutulong mabawasan ang pagkalat ng ilang fungal pathogens.",
                    "Itanim sa paligid o gilid ng kamada ng bell pepper."
                )
            )
        ),
        PlantRelationItem(
            name = "Karot (Carrot)",
            scientificName = "Daucus carota",
            isBeneficial = true,
            role = "Nagpapalambot ng ilalim ng lupa nang hindi nakikipag-agawan sa espasyo",
            explanation = ExplanationData(
                title = "Karot at Bell Pepper Root Harmony",
                summary = "Mababaw ang ugat ng bell pepper samantalang pababa ang taproot ng karot.",
                details = listOf(
                    "Walang kompetisyon sa sustansya sa parehong lebel ng lupa.",
                    "Pinapaganda ang aeration ng kamada.",
                    "Pangkaraniwang magkasama sa highland farming sa Benguet."
                )
            )
        ),
        PlantRelationItem(
            name = "Amarillo (French Marigold)",
            scientificName = "Tagetes patula",
            isBeneficial = true,
            role = "Naglilinis ng lupa laban sa root-knot nematodes gamit ang alpha-terthienyl",
            explanation = ExplanationData(
                title = "Proteksyon Laban sa Nematodes",
                summary = "Ang katas ng ugat ng marigold ay pumapatay sa mga bukol sa ugat (nematodes).",
                details = listOf(
                    "Naglilinis sa root zone ng mga mapanganib na bulate.",
                    "Umaakit ng hoverflies at ladybugs na kumakain ng aphids."
                )
            )
        )
    ),
    combativePlants = listOf(
        PlantRelationItem(
            name = "Talong (Eggplant)",
            scientificName = "Solanum melongena",
            isBeneficial = false,
            role = "Kaparehong pamilyang Solanaceae; nagpapalipat-lipat ang bacterial wilt at flea beetles",
            explanation = ExplanationData(
                title = "Bakit Bawal Pagsamahin ang Talong at Bell Pepper?",
                summary = "Pareho silang madaling mahawa ng Ralstonia solanacearum (bacterial wilt) at fruit borer.",
                details = listOf(
                    "Mabilis na pagkalat ng lanta-bakterya sa buong kamada.",
                    "Parehong matatakaw sa Potassium at Nitroheno.",
                    "Ihiwalay ng kama at huwag isunod sa pinagtaniman ng talong."
                )
            )
        ),
        PlantRelationItem(
            name = "Kamatis (Tomato)",
            scientificName = "Solanum lycopersicum",
            isBeneficial = false,
            role = "Nanganganib sa kaparehong fruit borer, anthracnose, at early blight",
            explanation = ExplanationData(
                title = "Pag-iwas sa Kamatis sa Katabing Kamada",
                summary = "Ang Helicoverpa fruit borer at tobacco mosaic virus (TMV) ay madaling lumipat mula kamatis patungong bell pepper.",
                details = listOf(
                    "Madaling magkahawaan ng mosaic virus sa pamamagitan ng kamay at hangin.",
                    "Umaakit ng parehong uod sa bunga."
                )
            )
        ),
        PlantRelationItem(
            name = "Patatas (Potato)",
            scientificName = "Solanum tuberosum",
            isBeneficial = false,
            role = "Naglilipat ng late blight at soil-borne nematodes sa Benguet plots",
            explanation = ExplanationData(
                title = "Iwasan ang Patatas bilang Katabi o Kasunod",
                summary = "Ang patatas ay nag-iiwan ng fungal spores sa lupa na maaaring sumira sa ugat ng bell pepper.",
                details = listOf(
                    "Phytophthora spores na nananatili sa basa at malamig na lupa.",
                    "Ipalit ang mais, sitaw, o repolyo bago mag-bell pepper."
                )
            )
        ),
        PlantRelationItem(
            name = "Fennel / Hinoho",
            scientificName = "Foeniculum vulgare",
            isBeneficial = false,
            role = "Naglalabas ng allelopathic chemicals sa ugat na nagpapatigil sa paglaki ng bell pepper",
            explanation = ExplanationData(
                title = "Allelopathic Effect ng Fennel",
                summary = "Ang fennel ay naglalabas ng kemikal sa lupa na nakakabansot sa halos lahat ng pananim na solanaceae.",
                details = listOf(
                    "Mabagal na pagtubo at pagkalagas ng dahon ng bell pepper."
                )
            )
        )
    ),
    nutritionBadges = listOf(
        NutritionBadgeItem(
            code = "VIT_C",
            name = "Bitamina C (Ascorbic Acid)",
            percentageDaily = "142% DV (127.7 mg)",
            role = "Pinakamataas na Antioxidant para sa Resistensya at Balat",
            explanation = ExplanationData(
                title = "Pambihirang Bitamina C sa Bell Pepper",
                summary = "Naglalaman ng mahigit 140% ng arawang pangangailangan sa Vitamin C sa 100 gramo lamang.",
                details = listOf(
                    "Mas mataas kaysa sa kahel (orange) at kalamansi bawat timbang.",
                    "Tumutulong sa collagen formation at mabilisang pagpapagaling ng sugat."
                )
            )
        ),
        NutritionBadgeItem(
            code = "VIT_A",
            name = "Bitamina A & Beta-Carotene",
            percentageDaily = "17% DV (157 mcg RAE)",
            role = "Pangangalaga sa Kalusugan ng Mata at Malusog na Balat",
            explanation = ExplanationData(
                title = "Bitamina A para sa Paningin",
                summary = "Lalong tumataas ang beta-carotene habang humihinog mula berde patungong pula at dilaw.",
                details = listOf(
                    "Nagpoprotekta sa retina at paningin sa gabi.",
                    "Mabisang panlaban sa cellular aging at free radicals."
                )
            )
        ),
        NutritionBadgeItem(
            code = "VIT_B6",
            name = "Bitamina B6 (Pyridoxine)",
            percentageDaily = "22% DV (0.29 mg)",
            role = "Mahalaga sa Paggawa ng Pulang Dugo at Utak",
            explanation = ExplanationData(
                title = "Bitamina B6 para sa Enerhiya",
                summary = "Tumutulong sa pagtunaw ng protina at maayos na takbo ng nervous system.",
                details = listOf(
                    "Kritikal sa paggawa ng hemoglobin para sa daloy ng oxygen.",
                    "Tumutulong sa pagpapalakas ng mood at pag-iisip."
                )
            )
        ),
        NutritionBadgeItem(
            code = "LOW_CAL",
            name = "Mababang Calorie & Hibla",
            percentageDaily = "26–31 kcal bawat 100g",
            role = "Mataas sa Tubig at Masustansyang Hibla para sa Puso",
            explanation = ExplanationData(
                title = "Healthy Salad Vegetable",
                summary = "Napakababa sa carbohydrates at taba, perpekto sa healthy diet at diyabetes.",
                details = listOf(
                    "Naglalaman ng 92% natural water content.",
                    "2.1 gramo ng dietary fiber para sa maayos na panunaw."
                )
            )
        )
    ),
    pestsAndDiseases = listOf(
        PestDiseaseItem(
            name = "Fruit Borer (Uod sa Bunga)",
            scientificName = "Helicoverpa armigera",
            severity = "Mataas (High)",
            symptoms = "Pabilog na butas sa tabi ng calyx o katawan ng bell pepper na may maitim na dumi (frass); bulok ang loob ng bunga.",
            prevention = "Magtanim ng marigold at basil sa paligid; maglagay ng pheromone traps upang mahuli ang mga gamu-gamo bago makapangitlog.",
            treatment = "Mag-spray ng Bacillus thuringiensis (Bt) o Spinetoram sa dapit-hapon; agad na pitasin at ibaon ang mga apektadong bunga.",
            explanation = ExplanationData(
                title = "Pamamahala sa Fruit Borer",
                summary = "Ang pinakamatinding sumisira sa kalidad ng bunga ng siling atsal sa Pilipinas.",
                details = listOf(
                    "Bumabarena ang uod sa loob habang maliit pa ang bunga.",
                    "Nagdudulot ng secondary bacterial soft rot sa loob ng silid ng prutas."
                )
            )
        ),
        PestDiseaseItem(
            name = "Thrips (Kuto sa Bulaklak)",
            scientificName = "Thrips palmi",
            severity = "Mataas (High)",
            symptoms = "Tila pilak na guhit (silvery streaks) sa mga dahon at balat ng bunga; kulot na talbos at lagas na bulaklak.",
            prevention = "Maglagay ng blue at yellow sticky traps (1 trap kada 10 metro kwadrado); panatilihing mamasa-masa ang paligid.",
            treatment = "Mag-spray ng horticultural insecticidal soap o neem oil solution; sa matinding impeksyon gumamit ng Spinetoram.",
            explanation = ExplanationData(
                title = "Thrips Management sa Bell Pepper",
                summary = "Sumisipsip sa mga talulot at murang usbong na nagdudulot ng pagkalagas ng bulaklak.",
                details = listOf(
                    "Napakadaling kumalat sa tuyo at mainit na panahon.",
                    "Maaaring magpalaganap ng mga viral disease sa taniman."
                )
            )
        ),
        PestDiseaseItem(
            name = "Blossom-End Rot (Tuyong Puwit)",
            scientificName = "Calcium Deficiency Physiological Disorder",
            severity = "Katamtaman (Moderate)",
            symptoms = "Maitim, patag, at tila balat na tuyong sugat sa ilalim o puwit ng bell pepper habang lumalaki.",
            prevention = "Maglagay ng apog (agricultural lime) sa paghahanda ng lupa; panatilihing pantay ang patubig at huwag hayaang matuyo ang kamada.",
            treatment = "Foliar spray ng Calcium Nitrate o Cheated Calcium na may Boron tuwing 7–10 araw sa pamumulaklak.",
            explanation = ExplanationData(
                title = "Sanhi ng Tuyong Puwit",
                summary = "Hindi ito peste kundi kakulangan sa calcium sa dulo ng bunga dulot ng pabago-bagong patubig.",
                details = listOf(
                    "Mabilis mangyari kapag tuyo ang lupa sa panahong mabilis lumaki ang bunga.",
                    "Nalulutas sa pamamagitan ng consistent drip irrigation at mulching."
                )
            )
        ),
        PestDiseaseItem(
            name = "Lanta-Bakterya (Bacterial Wilt)",
            scientificName = "Ralstonia solanacearum",
            severity = "Napakataas (Critical / Fatal)",
            symptoms = "Biglaang panlalanta ng buong puno sa tanghali kahit basa ang lupa; may puting gatas na lumalabas sa putol na tangkay sa tubig.",
            prevention = "Gumamit ng resistant hybrid tulad ng Sultan F1; gumawa ng 25 cm mataas na kamada; magsagawa ng crop rotation sa mais o sitaw.",
            treatment = "Bunutin at sunugin agad ang apektadong puno kasama ang lupang nakapalibot sa ugat. Maglagay ng apog sa pinagbunutan.",
            explanation = ExplanationData(
                title = "Lethal Bacterial Wilt sa Solanaceae",
                summary = "Pumapasok sa vascular tissues at humaharang sa daanan ng tubig ng buong puno.",
                details = listOf(
                    "Walang kemikal na lunas kapag nasa loob na ng halaman.",
                    "Kalinisan ng kagamitan at resistant seed ang tanging proteksyon."
                )
            )
        ),
        PestDiseaseItem(
            name = "Anthracnose / Pasob sa Bunga",
            scientificName = "Colletotrichum spp.",
            severity = "Mataas (High)",
            symptoms = "Pabilog at lumulubog na maitim na sugat na may mga concentric rings sa balat ng bunga sa mahalumigmig na panahon.",
            prevention = "Pitasin at sirain ang mga apektadong bunga. Iwasan ang overhead watering; mag-tulos upang nakabitin ang bunga.",
            treatment = "Mag-spray ng copper hydroxide o bio-fungicide bago magsimula ang tuloy-tuloy na ulan.",
            explanation = ExplanationData(
                title = "Anthracnose Defense",
                summary = "Fungal pathogen na sumisira sa bunga kapag natalsikan ng putik mula sa lupa sa tag-ulan.",
                details = listOf(
                    "Ang plastic mulch at pagtutulos ay nagbabawas ng 80% ng impeksyon."
                )
            )
        )
    ),
    goodAndBadCritters = listOf(
        CritterItem(
            name = "Kiyot at Pukyutan (Stingless & Honey Bees)",
            role = "Kapaki-pakinabang na Tagapag-polina",
            attractsWith = "Mabangong bulaklak ng basil, marigold, at oregano sa tabi ng kamada",
            explanation = ExplanationData(
                title = "Bees para sa Mas Malalaking Bunga",
                summary = "Mataas ang naitutulong sa cross-pollination para sa mas pantay, malalaki, at makakapal na bunga ng bell pepper."
            )
        ),
        CritterItem(
            name = "Salagubang na Pula (Ladybird Beetles)",
            role = "Likas na Maninila (Predator)",
            attractsWith = "Mga bulaklak ng dill at marigold; hindi ginagamitan ng malalakas na kemikal",
            explanation = ExplanationData(
                title = "Ladybug laban sa Aphids",
                summary = "Kumakain ng daan-daang aphids at maliliit na thrips bawat araw sa ilalim ng mga dahon."
            )
        ),
        CritterItem(
            name = "Hoverfly Larvae (Syrphid Fly)",
            role = "Likas na Tagapuksa ng Kuto",
            attractsWith = "Sweet alyssum at coriander sa paligid ng plot",
            explanation = ExplanationData(
                title = "Hoverfly laban sa Peste",
                summary = "Ang maliliit na uod ng hoverfly ay masisipag na kumakain ng mga kuto (aphids) sa mga talbos ng bell pepper."
            )
        ),
        CritterItem(
            name = "Hornworm (Malaking Uod sa Dahon)",
            role = "Mapaminsalang Peste",
            attractsWith = "Malalagong dahon ng solanaceous crops",
            explanation = ExplanationData(
                title = "Mapanirang Hornworm",
                summary = "Mabilis na kumakain ng buong dahon at balat ng murang bunga sa magdamag; manu-manong pulutin sa gabi."
            )
        )
    ),
    careGuide = listOf(
        HowToSectionItem(
            title = "Wastong Pagtutulos ng Kawayan (80–100 cm Bamboo Staking)",
            summary = "Paraan ng pagsuporta sa mabibigat na sanga upang hindi mabali sa hangin.",
            steps = listOf(
                "Maghanda ng mga patpat na kawayan na may haba na 80 hanggang 100 sentimetro.",
                "Itusok ang tulos 5 hanggang 7 cm ang layo sa puno sa mismong linggo ng paglipat-tanim upang hindi maputol ang mga ugat.",
                "Talian ang pangunahing tangkay gamit ang figure-8 knot upang hindi sumakal sa lumalaking puno.",
                "Magdagdag ng suportang pisi sa pagitan ng mga tulos kapag bumigat na ang mga kumpol ng bunga."
            ),
            tips = listOf(
                "Huwag itusok ang tulos kapag matanda na ang halaman dahil mapipinsala ang malalaking ugat."
            )
        ),
        HowToSectionItem(
            title = "Pruning at Pag-aalis ng Suckers sa Ibaba ng Y-Fork",
            summary = "Teknik upang lumakas ang hangin sa ilalim at lumaki ang mga prutas sa itaas.",
            steps = listOf(
                "Tukuyin ang unang pagsasanga ng puno na hugis 'Y' (tinatawag na Y-fork o crotch).",
                "Putulin ang lahat ng maliliit na 'suckers' o side shoots na tumutubo sa ibaba ng Y-fork.",
                "Alisin ang unang bulaklak (crown flower) sa gitna ng Y kung nais palakasin ang tangkay bago mamunga nang maramihan.",
                "Linisin ang mga lumang dahon na sumasayad sa lupa upang makaiwas sa anthracnose."
            )
        ),
        HowToSectionItem(
            title = "Pamamahala sa Calcium at Pag-iwas sa Blossom-End Rot",
            summary = "Solusyon sa tuyong puwit gamit ang apog at maayos na patubig.",
            steps = listOf(
                "Maghalo ng 100 gramo ng agricultural lime sa bawat metro kwadrado bago magtanim kung acidic ang lupa.",
                "Panatilihing mamasa-masa ang kamada gamit ang drip irrigation; iwasan ang biglaang pagkatuyo na sinundan ng labis na dilig.",
                "Mag-spray ng foliar calcium nitrate (5g bawat litro) tuwing umaga sa panahon ng masinsing pamumulaklak."
            )
        )
    ),
    seasonalCalendar = listOf(
        SeasonalCalendarMonth("Ene", "Mataas na Ani sa Benguet & Lowland", "Malamig at magandang panahon para sa makakapal na bunga; tuloy-tuloy na pamimitas.", Color(0xFF2E7D32)),
        SeasonalCalendarMonth("Peb", "Tugatog ng Produksyon sa Amihan", "Pinakamataas na kalidad ng Sultan F1 para sa merkado ng Maynila.", Color(0xFF2E7D32)),
        SeasonalCalendarMonth("Mar", "Pagtatapos ng Dry Harvest", "Pag-aani ng huling buwig bago pumasok ang matinding init.", Color(0xFF388E3C)),
        SeasonalCalendarMonth("Abr", "Paghahanda ng Kamada / Highland", "Paghahanda ng mataas na kamada sa Benguet at Bukidnon para sa bagong cropping.", Color(0xFFF57F17)),
        SeasonalCalendarMonth("Mayo", "Taniman na may Rain Shelter", "Pagtatanim sa ilalim ng UV plastic tunnel upang maprotektahan sa ulan.", Color(0xFFF57F17)),
        SeasonalCalendarMonth("Hun", "Tag-ulan sa Lowland (Off-Season)", "Kritikal ang 25 cm raised beds at drainage canals laban sa waterlogging.", Color(0xFFE65100)),
        SeasonalCalendarMonth("Hul", "Panahon ng Habagat", "Higpitan ang mga tulos laban sa malalakas na hangin; bantayan ang anthracnose.", Color(0xFFE65100)),
        SeasonalCalendarMonth("Ago", "Mataas na Presyo sa Palengke", "Mataas ang kita ng off-season growers na may proteksyon laban sa bagyo.", Color(0xFFE65100)),
        SeasonalCalendarMonth("Set", "Paghahanda para sa Main Crop", "Malalim na pag-aararo, paglalagay ng vermicast, apog, at CRH para sa amihan crop.", Color(0xFF1565C0)),
        SeasonalCalendarMonth("Okt", "Paghahasik sa Seedling Trays", "Pagsisimula ng punlaan para sa pinakamalaking cropping season ng taon.", Color(0xFF2E7D32)),
        SeasonalCalendarMonth("Nob", "Paglilipat-Tanim at Pagtutulos", "Paglilipat ng 35-araw na punla sa kamada at agad na pagtutulos ng kawayan.", Color(0xFF2E7D32)),
        SeasonalCalendarMonth("Dis", "Pamumulaklak at Pasko Harvest", "Mataas na demand para sa mga lutuing Kapaskuhan; unang pitas ng sariwang bell pepper.", Color(0xFF2E7D32))
    ),
    locationData = LocationDifficultyData(
        difficulty = "Katamtaman (Moderate)",
        hardinessZone = "Highland & Cool Lowland Philippines (Benguet, Bukidnon, Southern Tagalog)",
        tempRange = "18°C–28°C (Sensitibo sa matinding init >32°C)",
        sunlight = "Buong Araw (Full Sun, 6–8 Oras)",
        suitableAlso = "Raised beds / Plasticulture / Greenhouses / 20L Grow Bags"
    ),
    soilPrep = SoilPrepData(
        phRange = "5.5–6.8",
        soilTypes = "Buhaghag na Sandy Loam na mayaman sa Organiko at Magandang Drainage",
        recommendation = "Bungkalin ang kamada sa lalim na 25–30 cm. Ihalo ang 2–3 kg decomposed manure o vermicast bawat metro kwadrado, 20% Carbonized Rice Hull (CRH) para sa airflow ng ugat, at 100g agricultural lime kung acidic ang lupa. Maglagay ng 14-14-14 basal fertilizer bago maglipat-tanim."
    ),
    growthTimeline = listOf(
        TimelineStageItem(
            stageName = "1. Pagsibol ng Buto sa Tray (Germination)",
            durationDays = "Araw 10–15",
            description = "Pagsibol ng binhi sa 104-hole tray sa 24–28°C na temperatura; paglabas ng cotyledons."
        ),
        TimelineStageItem(
            stageName = "2. Pagpapatibay ng Punla (Nursery Care & Hardening)",
            durationDays = "Araw 15–35",
            description = "Paglaki ng 5–6 tunay na dahon; pagpapatigas ng tangkay sa araw 5–7 araw bago ilipat."
        ),
        TimelineStageItem(
            stageName = "3. Paglipat-Tanim at Pagtutulos (Transplanting & Staking)",
            durationDays = "Araw 35–60",
            description = "Paglipat sa kamada bandang hapon; paglalagay ng 80–100 cm kawayang tulos at pruning ng suckers sa ilalim ng Y-fork."
        ),
        TimelineStageItem(
            stageName = "4. Pamumulaklak at Pagbuo ng Bunga (Flowering & Fruit Set)",
            durationDays = "Araw 60–85",
            description = "Paglabas ng mga puting bulaklak; paglaki ng munting hugis-blokeng bunga; regular na patubig at calcium side-dress."
        ),
        TimelineStageItem(
            stageName = "5. Tuloy-tuloy na Anihan (Continuous Harvest)",
            durationDays = "Araw 85–140+",
            description = "Pamimitas tuwing 5–7 araw ng makakapal, makikintab, at mabibigat na bell pepper gamit ang matalas na gunting."
        )
    ),
    howTos = listOf(
        HowToSectionItem(
            title = "Tamang Paraan ng Pagpitas Gamit ang Gunting",
            summary = "Hakbang upang hindi mapunit ang marupok na sanga ng bell pepper.",
            steps = listOf(
                "Huwag kailanman hilahin o baliin ang bunga gamit ang kamay dahil madaling mapunit ang buong sanga.",
                "Gumamit ng matalas at malinis na pruning shears o gunting.",
                "Gupitin ang tangkay na nag-iiwan ng 2 hanggang 3 sentimetro ng berdeng tangkay (peduncle) sa bunga.",
                "Pitasin sa maagang umaga habang malutong at makatas ang laman.",
                "Ayusin nang maingat sa plastic crates na may saping malambot upang maiwasan ang gasgas."
            )
        ),
        HowToSectionItem(
            title = "Kailan Aanihing Berde vs. Hihintaying Magpula o Magdilaw",
            summary = "Pagkakaiba sa presyo, lasa, at tagal sa merkado.",
            steps = listOf(
                "Green Bell Pepper: Pitasin kapag ganap na ang laki (firm, makapal ang laman, makintab ang balat). Mas mahaba ang shelf-life (10–14 araw).",
                "Red / Yellow Bell Pepper: Iwanan sa puno ng karagdagang 2–3 linggo hanggang ganap na magbago ang kulay. Mas matamis, mas mataas ang Vitamin C at antioxidant, at 30–50% na mas mataas ang presyo sa palengke.",
                "Magdesisyon ayon sa demand ng mamimili at lagay ng panahon (sa tag-ulan, mas ligtas anihing berde laban sa anthracnose)."
            )
        )
    ),
    faqs = listOf(
        FaqItem(
            question = "Bakit nalalagas ang mga bulaklak ng aking bell pepper nang hindi nagiging bunga?",
            answer = "Ang pagkalagas ng bulaklak (blossom drop) ay karaniwang sanhi ng matinding init sa tanghali na lumalampas sa 32°C, malamig na gabi sa ibaba ng 15°C, o biglaang panunuyo ng lupa. Maaari rin itong dulot ng labis na chemical nitrogen (sobrang yabong ng dahon ngunit walang bunga) o paninira ng thrips sa mga talulot. Maglagay ng mulch, panatilihing pantay ang patubig, at gumamit ng balanced fertilizer na may Potassium at Boron."
        ),
        FaqItem(
            question = "Maaari ba akong magtanim ng bell pepper sa mainit na kapatagan tulad ng Gitnang Luzon?",
            answer = "Oo, ngunit pinakamainam itong itanim sa pinakamalamig na buwan ng taon mula Nobyembre hanggang Pebrero (Amihan season). Gumamit ng heat-tolerant hybrids tulad ng Sultan F1 mula sa East-West Seed. Sa tag-init (Marso hanggang Mayo), maglagay ng 30% black shade netting upang maprotektahan ang bulaklak laban sa nakapapasong init."
        ),
        FaqItem(
            question = "Bakit nagkakaroon ng tuyo at maitim na sugat sa puwit ng aking bell pepper (Blossom-End Rot)?",
            answer = "Ito ay 'Blossom-End Rot' na sanhi ng kakulangan ng calcium sa lumalaking bunga. Madalas itong nangyayari kapag hindi pantay ang patubig (tuyo ang lupa kaya hindi nahihigop ng ugat ang calcium). Maiiwasan ito sa pamamagitan ng paglalagay ng agricultural lime sa lupa bago magtanim, consistent na drip irrigation, at foliar spray ng calcium-boron tuwing namumulaklak."
        ),
        FaqItem(
            question = "Bakit kailangang lagyan ng tulos na kawayan ang bawat puno ng bell pepper?",
            answer = "Ang kahoy ng bell pepper ay napakarupok kumpara sa ibang gulay. Kapag nagsimula nang bumigat ang mga bunga (bawat isa ay tumitimbang ng 100–180 gramo), ang mga sanga ay madaling mabali o tumumba kapag nahanginan. Ang 80–100 cm kawayang tulos ay nagpapanatiling nakatayo ang puno at naglalayo sa mga bunga mula sa basang lupa upang hindi mabulok."
        )
    ),
    nutritionTable = NutritionTableData(
        servingSize = "100 g (Sariwang Hilaw na Bell Pepper)",
        calories = "26–31 kcal",
        macroNutrients = listOf(
            NutritionRowData("Protina (Protein)", "1.00 g", "2.0%"),
            NutritionRowData("Kabuuang Taba (Total Fat)", "0.30 g", "0.4%"),
            NutritionRowData("Carbohydrates", "6.03 g", "4.6%"),
            NutritionRowData("Dietary Fiber (Hibla)", "2.10 g", "8.4%"),
            NutritionRowData("Asukal (Natural Sugars)", "4.20 g", "—"),
            NutritionRowData("Tubig (Water Content)", "92.2 g", "—")
        ),
        microNutrients = listOf(
            NutritionRowData("Bitamina C (Ascorbic Acid)", "127.7 mg", "142%"),
            NutritionRowData("Bitamina A (RAE)", "157 mcg", "17%"),
            NutritionRowData("Bitamina B6 (Pyridoxine)", "0.29 mg", "22%"),
            NutritionRowData("Folate (Bitamina B9)", "46 mcg", "12%"),
            NutritionRowData("Bitamina K", "4.9 mcg", "4%"),
            NutritionRowData("Potassium (K)", "211 mg", "4%"),
            NutritionRowData("Manganese (Mn)", "0.11 mg", "5%"),
            NutritionRowData("Calcium (Kalsyo)", "7.0 mg", "1%")
        )
    ),
    exploreArticles = listOf(
        ArticleItem("Komersyal na Produksyon ng Bell Pepper sa Benguet Highlands", "Mga agronomic protocols sa La Trinidad at Buguias para sa 15–20 tonelada bawat ektarya gamit ang Sultan F1.", "7 min basahin"),
        ArticleItem("Pamamahala sa Calcium at Pag-iwas sa Blossom-End Rot", "Bakit nagkakaroon ng tuyong puwit ang siling atsal at paano ito gagamutin gamit ang tamang patubig at apog.", "5 min basahin"),
        ArticleItem("IPM Guide: Pagsugpo sa Thrips at Fruit Borer sa Bell Pepper", "Organikong pamamaraan gamit ang blue sticky traps, Bt, at reflective silver-blue plastic mulch.", "6 min basahin")
    )
)
