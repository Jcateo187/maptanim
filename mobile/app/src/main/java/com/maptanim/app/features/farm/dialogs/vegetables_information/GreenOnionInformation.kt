package com.maptanim.app.features.farm.dialogs.vegetables_information

import androidx.compose.ui.graphics.Color
import com.maptanim.app.R

/**
 * Rich, research-backed encyclopedia data for Green Onion / Scallion / Dahon ng Sibuyas (Allium fistulosum).
 * Tailored for Philippine commercial farms (Nueva Ecija, Batangas, Benguet) and urban container gardens
 * based on DA-BPI, East-West Seed, and MapTanim agronomic field standards.
 */
val greenOnionData = CropEncyclopedia(
    cropName = "Green Onion / Scallion",
    localName = "Dahon ng Sibuyas / Sibuyas Tagalog (Allium fistulosum)",
    scientificName = "Allium fistulosum / Allium cepa var. aggregatum",
    category = "Alliums / Green Alliums (Amaryllidaceae)",
    family = "Amaryllidaceae",
    overview = "Ang Dahon ng Sibuyas o Scallion (Allium fistulosum / Allium cepa var. aggregatum), na kilala rin bilang Sibuyas Tagalog o Spring Onion, ay isa sa pinakamahalagang panimpla at gulay sa agrikultura at lutuing Pilipino. Hindi tulad ng karaniwang bulb onion na bumubuo ng malaking bilog na bombilya sa ilalim ng lupa, ang green onion ay pinalalaki para sa malalabay, hungkag (tubular), at madilim na berdeng dahon nito, gayundin para sa makinis, malutong, at pinaputing tangkay (blanched pseudostem) sa ibaba.\n\nItinatanim ito sa buong kapuluan—mula sa malalawak na kamada ng Nueva Ecija, Batangas, at Ilocos, hanggang sa malamig na kabundukan ng Benguet at urban container gardens sa Kalakhang Maynila. Pambihira ang kakayahan nitong magbunga nang tuloy-tuloy: maaari itong anihin nang buo (whole-bunch uprooting) sa loob ng 45–60 araw, o anihin sa pamamagitan ng 'cut-and-come-again' (ratoon cropping) kung saan pinuputol lamang ang mga dahon 2–3 cm sa ibabaw ng ugat upang muling sumibol nang maraming beses.\n\nBukod sa kahalagahan sa kusina (paboritong sahog sa arroz caldo, lugaw, pansit, mami, at sawsawan), ang green onion ay isang kampeon sa intercropping at companion planting. Ang taglay nitong sulfur compounds at allicin ay nagtataboy ng aphids, whiteflies, at diamondback moth kapag itinabi sa kamatis, talong, repolyo, at pechay.",
    selectedVariety = VarietyInfo(
        name = "Paragon F1",
        producer = "East-West Seed Philippines",
        type = "Tropical Bunching Scallion Hybrid",
        badge = "F1 Hybrid",
        description = "Ang nangungunang commercial bunching scallion hybrid sa Pilipinas. Kilala sa matitikas at makakapal na tubular na dahon na may matingkad na kulay berde, mahabang pinaputing tangkay (12–18 cm white shaft), at mabilis na pagsusuwi (profuse tillering). May mataas na resistensya laban sa Purple Blotch at pagkalanta sa tag-ulan."
    ),
    otherVarieties = listOf(
        "Paragon F1 (East-West Seed Heavy Yield Standard)",
        "Kincho (Kaneko Seeds - Uniform Cylindrical Stems)",
        "Batanes Bunching Native (Sibuyas Tagalog OPV)",
        "White Tower F1 (Extra Long Blanched Shank)",
        "Bongbong Native (Heirloom Ilocos Scallion)",
        "Tokyo Long White (Cold-Hardy & Sweet Stem)",
        "Red Mountain (Red-Shanked Specialty Bunching)"
    ),
    carouselSlides = listOf(
        CarouselSlide(
            title = "Sariwang Dahon ng Sibuyas sa Kamada",
            subtitle = "Malulusog na kumpol ng green onion sa pinatabang buhaghag na raised bed",
            stage = "Handa sa Pitas (Market Ready)",
            badgeColor = Color(0xFF2E7D32),
            drawableResId = R.drawable.ph_green_onion_hero
        ),
        CarouselSlide(
            title = "Pagsibol ng mga Punla sa Seedling Tray",
            subtitle = "7–14 araw na pinong pagsibol sa mamasa-masang carbonized rice hull at compost",
            stage = "Yugto ng Pagsibol (Emergence Phase)",
            badgeColor = Color(0xFF1565C0),
            drawableResId = R.drawable.ph_green_onion_seedlings
        ),
        CarouselSlide(
            title = "Vegetative Clumping at Pagtatambak (Earthing-Up)",
            subtitle = "Aktibong pagpapalaki at pagpapaputi ng tangkay sa pamamagitan ng blanching",
            stage = "Tillering & Shaft Elongation",
            badgeColor = Color(0xFFE65100),
            drawableResId = R.drawable.ph_green_onion_growth
        ),
        CarouselSlide(
            title = "Masaganang Pag-aani sa Bilao at Pagbubungkos",
            subtitle = "Malilinis na bungkos ng dahon ng sibuyas na handa para sa lokal na pamilihan",
            stage = "Pang-umagang Pag-aani (Harvest Phase)",
            badgeColor = Color(0xFF6A1B9A),
            drawableResId = R.drawable.ph_green_onion_harvest
        )
    ),
    quickInfo = listOf(
        QuickInfoItem(
            key = "lifespan",
            label = "Buhay ng Halaman",
            value = "45–60 Araw (o Perennial)",
            explanation = ExplanationData(
                title = "Siklo ng Buhay ng Dahon ng Sibuyas",
                summary = "45 hanggang 60 araw para sa buong bunot; tuloy-tuloy na taon kung ratoon harvesting.",
                details = listOf(
                    "Pagsibol (Germination): 7–12 araw mula sa binhi (o 3–5 araw kung root division).",
                    "Pag-uugat at Pagsuwi (Tillering): 20–35 araw kung saan dumarami ang tangkay bawat puno.",
                    "Blanching Phase: 35–45 araw kung saan tinatambakan ng lupa upang humaba ang puting tangkay.",
                    "Anihan: 45–60 araw kapag may taas nang 30–45 cm at 1 cm kapal ng tangkay."
                )
            )
        ),
        QuickInfoItem(
            key = "harvest_time",
            label = "Unang Ani",
            value = "45–50 DAP / DAS",
            explanation = ExplanationData(
                title = "Panahon ng Pag-aani",
                summary = "Maaaring anihin nang buo o gupitan lamang ang mga panlabas na dahon tuwing 20–25 araw.",
                details = listOf(
                    "Whole Plant Harvest: Binubunot kasama ang mapuputing ugat at binubuklod ng tali.",
                    "Cut-and-Come-Again: Pinuputol 2.5 cm sa ibabaw ng lupa; muling tutubo sa loob ng 2–3 linggo.",
                    "Gawin ang pag-aani sa maagang umaga upang sariwa at malutong ang mga tubular na dahon."
                )
            )
        ),
        QuickInfoItem(
            key = "spacing",
            label = "Agwat ng Tanim",
            value = "10–15 cm (Hanay 20–25 cm)",
            explanation = ExplanationData(
                title = "Tamang Agwat ng Pagtatanim",
                summary = "10–15 cm sa pagitan ng bawat tumpok (hills) at 20–25 cm sa pagitan ng mga hanay.",
                details = listOf(
                    "Magtanim ng 2 hanggang 3 suwi o punla bawat butas para sa masaganang kumpol.",
                    "Nagbibigay-daan ang agwat para sa maginhawang 'earthing-up' o pagtatambak ng lupa sa paligid ng puno."
                )
            )
        ),
        QuickInfoItem(
            key = "watering",
            label = "Patubig",
            value = "2–3 Beses kada Linggo",
            explanation = ExplanationData(
                title = "Pangangailangan sa Tubig",
                summary = "Mababaw ang fibrous root system kaya kailangan ng mamasa-masang lupa nang walang naipong baha.",
                details = listOf(
                    "Diligan nang regular tuwing umaga; iwasan ang pagbabasa ng dahon sa takipsilim.",
                    "Sensitibo sa waterlogging: mabilis mabulok ang basal plate kapag matubig at putik ang kamada."
                )
            )
        ),
        QuickInfoItem(
            key = "sunlight",
            label = "Sikat ng Araw",
            value = "Buong Araw (Full Sun)",
            explanation = ExplanationData(
                title = "Pangangailangan sa Liwanag",
                summary = "Nangangailangan ng di-bababa sa 6 hanggang 8 oras ng direktang sikat ng araw araw-araw.",
                details = listOf(
                    "Ang buong araw ay nagpapanatili ng matigas at tuwid na mga dahon na may mataas na chlorophyll.",
                    "Sa lilim, nagiging payat, malambot, at laylay ang mga tubular na tangkay."
                )
            )
        ),
        QuickInfoItem(
            key = "soil_ph",
            label = "Lupa at pH",
            value = "pH 6.0–7.0 (Sandy Loam)",
            explanation = ExplanationData(
                title = "Tamang Katangian ng Lupa",
                summary = "Buhaghag, mayaman sa organikong bagay, at may mahusay na daluyan ng tubig.",
                details = listOf(
                    "Mainam ang buhaghag na sandy loam na may halong Carbonized Rice Hull (CRH) at vermicompost.",
                    "Ang pH na 6.2 hanggang 6.8 ang pinaka-angkop para sa tuluy-tuloy na uptake ng Nitrogen at Potassium."
                )
            )
        )
    ),
    companionPlants = listOf(
        PlantRelationItem(
            name = "Carrot (Karot)",
            scientificName = "Daucus carota",
            isBeneficial = true,
            role = "Mutual Pest Deterrent",
            explanation = ExplanationData(
                title = "Green Onion + Carrot Companion Protection",
                summary = "Klasikong tambalan: itinataboy ng amoy ng sibuyas ang carrot rust fly, habang ang dahon ng karot ay naglilimot sa thrips.",
                details = listOf(
                    "Ang allicin at volatile oils ng alliums ay nakakalito sa mga pesteng lumilipad patungo sa karot.",
                    "Magkaiba ang lalim ng kanilang mga ugat kaya hindi nag-aagawan sa espasyo ng kamada."
                )
            )
        ),
        PlantRelationItem(
            name = "Tomato (Kamatis)",
            scientificName = "Solanum lycopersicum",
            isBeneficial = true,
            role = "Aphid & Whitefly Barrier",
            explanation = ExplanationData(
                title = "Green Onion + Kamatis",
                summary = "Pinoprotektahan ng berdeng sibuyas ang kamatis laban sa aphids at whiteflies na nagdadala ng TYLCV virus.",
                details = listOf(
                    "Maaaring itanim bilang border crop sa paligid ng kamada ng kamatis.",
                    "Pinatataas din ang natural na resistensya ng kamatis laban sa fungal spores sa lupa."
                )
            )
        ),
        PlantRelationItem(
            name = "Cabbage (Repolyo)",
            scientificName = "Brassica oleracea",
            isBeneficial = true,
            role = "Diamondback Moth Deterrent",
            explanation = ExplanationData(
                title = "Green Onion + Repolyo / Brassica",
                summary = "Ang masangsang na aroma ng allium ay nagtatakip sa amoy ng repolyo laban sa Diamondback Moth (DBM) at cabbage looper.",
                details = listOf(
                    "Nagsisilbing natural bio-repellent boundary sa pagitan ng mga hanay ng brassicas."
                )
            )
        ),
        PlantRelationItem(
            name = "Pechay (Petsay)",
            scientificName = "Brassica rapa subsp. chinensis",
            isBeneficial = true,
            role = "Flea Beetle Protection",
            explanation = ExplanationData(
                title = "Green Onion + Pechay",
                summary = "Itinataboy ang flea beetles (salagubang) na pumupunit sa malambot na dahon ng pechay.",
                details = listOf(
                    "Mabilis maani ang pechay (30 araw) habang patuloy na lumalago ang green onion."
                )
            )
        ),
        PlantRelationItem(
            name = "Chili Pepper (Sili)",
            scientificName = "Capsicum annuum",
            isBeneficial = true,
            role = "Mite & Aphid Repellent",
            explanation = ExplanationData(
                title = "Green Onion + Sili",
                summary = "Binabawasan ang populasyon ng thrips at aphids sa halamanan ng sili.",
                details = listOf(
                    "Mabuting intercrop sa pagitan ng mga punong sili sa raised beds."
                )
            )
        ),
        PlantRelationItem(
            name = "Lettuce (Litsugas)",
            scientificName = "Lactuca sativa",
            isBeneficial = true,
            role = "Shade & Space Maximization",
            explanation = ExplanationData(
                title = "Green Onion + Lettuce",
                summary = "Perpektong kombinasyon sa raised bed dahil mababaw ang ugat ng litsugas at tuwid ang tindig ng sibuyas.",
                details = listOf(
                    "Nagsisilbing natural living mulch ang litsugas na nagpapanatili ng moisture sa ibabaw ng lupa."
                )
            )
        )
    ),
    combativePlants = listOf(
        PlantRelationItem(
            name = "String Beans (Sitaw)",
            scientificName = "Vigna unguiculata",
            isBeneficial = false,
            role = "Rhizobia Inhibition (Pumipigil sa Nitrogen Fixation)",
            explanation = ExplanationData(
                title = "Bakit Bawal Pagsamahin ang Dahon ng Sibuyas at Sitaw?",
                summary = "Ang ugat ng allium ay naglalabas ng sulfur compounds na pumapatay sa kapaki-pakinabang na Rhizobium bacteria sa ugat ng beans.",
                details = listOf(
                    "Nabubansot ang sitaw at bumababa ang kakayahan nitong kumuha ng natural na nitroheno mula sa hangin.",
                    "Mag-iwan ng di-bababa sa 1.5 metrong distansya sa pagitan ng kamada ng allium at legumes."
                )
            )
        ),
        PlantRelationItem(
            name = "Peas & Munggo (Mungbean / Legumes)",
            scientificName = "Fabaceae Family",
            isBeneficial = false,
            role = "Allelopathic Stunting",
            explanation = ExplanationData(
                title = "Banta sa Lahat ng Uri ng Legumbre",
                summary = "Lahat ng uri ng sitsaro, bataw, at munggo ay naaapektuhan ng antibyotikong allicin ng dahon ng sibuyas.",
                details = listOf(
                    "Nagdudulot ng paninilaw ng mga dahon ng legume at mababang produksyon ng bunga."
                )
            )
        )
    ),
    nutritionBadges = listOf(
        NutritionBadgeItem(
            code = "VIT_K",
            name = "Bitamina K",
            percentageDaily = "173%",
            role = "Kalusugan ng Buto at Pamumuo ng Dugo",
            explanation = ExplanationData(
                title = "Napakayamang Bitamina K sa Dahon ng Sibuyas",
                summary = "Ang 100g ng green onion ay nagbibigay ng mahigit 170% ng pang-araw-araw na pangangailangan sa Vitamin K1.",
                details = listOf(
                    "Mahalaga para sa osteocalcin synthesis na nagpapatibay sa mga buto.",
                    "Tumutulong sa wastong pamumuo ng dugo (blood clotting) at kalusugan ng puso."
                )
            )
        ),
        NutritionBadgeItem(
            code = "VIT_C",
            name = "Bitamina C",
            percentageDaily = "21%",
            role = "Immune Support & Antioxidant",
            explanation = ExplanationData(
                title = "Natural Vitamin C mula sa Sariwang Dahon",
                summary = "Pinoprotektahan ang mga selula laban sa free radicals at pinabibilis ang paghilom ng sugat.",
                details = listOf(
                    "Nagpapalakas ng resistensya laban sa ubo at sipon.",
                    "Pinapanatili ang sigla at kalusugan ng balat."
                )
            )
        ),
        NutritionBadgeItem(
            code = "ALLICIN",
            name = "Allicin & Quercetin",
            percentageDaily = "Mataas",
            role = "Natural Antibacterial & Heart Health",
            explanation = ExplanationData(
                title = "Aktibong Phytochemicals ng Alliums",
                summary = "Ang allicin ang nagbibigay ng mabangong aroma at natural na proteksyon laban sa impeksyon.",
                details = listOf(
                    "Tumutulong magpababa ng presyon ng dugo at kolesterol.",
                    "May natural na antifungal at antiviral properties."
                )
            )
        ),
        NutritionBadgeItem(
            code = "FOLATE",
            name = "Folate (B9)",
            percentageDaily = "16%",
            role = "Cell Division & Red Blood Cells",
            explanation = ExplanationData(
                title = "Mahalagang Folate para sa Katawan",
                summary = "Kinakailangan sa paggawa ng red blood cells at maayos na cellular DNA synthesis.",
                details = listOf(
                    "Mainam para sa mga nagdadalantao at pangkalahatang kalusugan ng dugo."
                )
            )
        )
    ),
    pestsAndDiseases = listOf(
        PestDiseaseItem(
            name = "Onion Thrips (Thrips tabaci)",
            scientificName = "Thrips tabaci Lindeman",
            severity = "Mataas (High)",
            symptoms = "Pinong mapuputi o tila pilak (silvery flecks) na mga batik sa hungkag na berdeng dahon; natutuyo at nagiging kulay abo ang dulo ng dahon.",
            prevention = "Maglagay ng blue o yellow sticky traps sa kamada; panatilihin ang mataas na kahalumigmig sa pamamagitan ng banayad na misting sa mainit na umaga.",
            treatment = "Mag-spray ng Neem oil (5 ml/L na may mild soap surfactant) tuwing 5 araw o Beauveria bassiana entomopathogenic spray sa ilalim ng dahon.",
            explanation = ExplanationData(
                title = "Pagkontrol sa Onion Thrips",
                summary = "Ang thrips ay napakaliit na insektong sumisipsip ng katas sa loob ng mga siwang ng dahon.",
                details = listOf(
                    "Lalaganap ito tuwing tag-init o dry season kapag tuyot ang hangin.",
                    "Mag-spray sa takipsilim kung kailan lumalabas ang thrips mula sa base ng dahon."
                )
            )
        ),
        PestDiseaseItem(
            name = "Beet Armyworm / Harabas",
            scientificName = "Spodoptera exigua (Hübner)",
            severity = "Kritikal (Critical)",
            symptoms = "Mga uod na pumapasok sa loob ng hungkag na dahon ng sibuyas at kinakain ang berdeng tisyu mula sa loob; naiiwan ang manipis na tila plastik na balat.",
            prevention = "Regular na suriin ang kamada para sa mga kumpol ng itlog na natatakpan ng puting balahibo sa dulo ng dahon; maglagay ng pheromone traps.",
            treatment = "Pitasin at sirain ang mga apektadong dahon na may uod sa loob; mag-spray ng Bacillus thuringiensis (Bt) o Spinosad sa hapon.",
            explanation = ExplanationData(
                title = "Harabas (Beet Armyworm) sa Dahon ng Sibuyas",
                summary = "Mapanirang uod na protektado sa loob ng tubular cavity ng dahon kaya hindi tinatamaan ng karaniwang kontak na pamatay-peste.",
                details = listOf(
                    "Ang biological control gamit ang Bt ay napakaepektibo kapag nakain ng uod.",
                    "I-crush nang marahan ang dahon sa bahaging kinaroroonan ng uod kung sa maliit na taniman lamang."
                )
            )
        ),
        PestDiseaseItem(
            name = "Purple Blotch (Alternaria porri)",
            scientificName = "Alternaria porri",
            severity = "Mataas (High)",
            symptoms = "Maliliit na tila basang batik na nagiging pabilog at kulay ube/kayumanggi (purple-brown) na may dilaw na paligid; nababali ang dahon sa mismong sugat.",
            prevention = "Iwasan ang pagbabasa ng dahon sa gabi; magtanim sa mga nakaangat na kamada na may 25 cm agwat para sa malayang sirkulasyon ng hangin.",
            treatment = "Gupitin at sunugin ang mga apektadong dahon; mag-spray ng Copper fungicide o Potassium Bicarbonate solution tuwing 7 araw sa tag-ulan.",
            explanation = ExplanationData(
                title = "Sakit na Purple Blotch sa Dahon ng Sibuyas",
                summary = "Pangunahing fungal disease ng alliums sa Pilipinas lalo na sa panahon ng habagat at mataas na halumigmig.",
                details = listOf(
                    "Ang spores ay kumakalat sa pamamagitan ng talsik ng ulan at hangin.",
                    "Huwag mag-overuse ng Nitrogen dahil nagiging malambot ang balat ng dahon."
                )
            )
        ),
        PestDiseaseItem(
            name = "Downy Mildew (Peronospora destructor)",
            scientificName = "Peronospora destructor",
            severity = "Katamtaman (Moderate)",
            symptoms = "Mapusyaw na kulay ube o abong tila bulak (fuzzy purplish-gray mold) sa lumang dahon sa malamig at mahamog na umaga; natutuyo at nababali ang dahon.",
            prevention = "Magtanim sa lugar na may buong sikat ng araw; gumamit ng drip irrigation sa halip na overhead sprinkler.",
            treatment = "Agad na alisin ang mga apektadong tangkay; mag-spray ng bio-fungicide tulad ng Trichoderma harzianum o copper hydroxide.",
            explanation = ExplanationData(
                title = "Downy Mildew Management",
                summary = "Karaniwang lumalabas sa malamig na buwan (Nobyembre hanggang Pebrero) sa highland at lowland misting areas.",
                details = listOf(
                    "Kailangan ang mabilisang pagpapatuyo ng dahon bago sumapit ang tanghali."
                )
            )
        ),
        PestDiseaseItem(
            name = "Fusarium Basal Rot",
            scientificName = "Fusarium oxysporum f. sp. cepae",
            severity = "Mataas (High)",
            symptoms = "Paninilaw at pagkalanta ng mga dahon mula dulo papuntang base; nabubulok ang ugat at nagiging kulay rosas/kayumanggi ang pinakapuno sa ilalim ng lupa.",
            prevention = "Mag-crop rotation (huwag itanim sa pinag-anihan ng sibuyas o bawang sa loob ng 3 taon); maghalo ng Trichoderma sa compost bago magtanim.",
            treatment = "Bunutin at ligtas na itapon ang mga apektadong halaman kasama ang lupang nakapalibot; patuyuin ang kamada.",
            explanation = ExplanationData(
                title = "Fusarium Basal Rot sa Alliums",
                summary = "Soil-borne fungus na pumapasok sa mga sugat sa ugat kapag nababad sa matagal na tubig.",
                details = listOf(
                    "Mahigpit na tiyaking may drainage canal ang mga kamada sa panahon ng tag-ulan."
                )
            )
        ),
        PestDiseaseItem(
            name = "Vegetable Leafminer",
            scientificName = "Liriomyza sativae Blanchard",
            severity = "Mababa (Low)",
            symptoms = "Mapuputing paikot-ikot o tila ahas na mga linya (serpentine trails) sa balat ng berdeng dahon.",
            prevention = "Maglagay ng dilaw na sticky card traps (yellow sticky traps) sa taas na 30 cm mula sa lupa.",
            treatment = "Pisilin ang dulo ng linya kung saan naroroon ang larva; mag-spray ng neem extract solution.",
            explanation = ExplanationData(
                title = "Leafminer sa Sibuyas",
                summary = "Kadalasan ay mababa lamang ang pinsala kung malusog at mabilis lumaki ang mga bagong dahon.",
                details = listOf(
                    "Huwag labis na mag-spray ng malalakas na kemikal upang maprotektahan ang mga natural parasitic wasps na kumakain sa leafminer."
                )
            )
        )
    ),
    goodAndBadCritters = listOf(
        CritterItem(
            name = "Syrphid Fly / Hoverfly Larvae",
            role = "Beneficial Predator (Mabuting Mangangaso)",
            attractsWith = "Mga bulaklak ng dill, marigold, at coriander sa paligid ng kamada",
            explanation = ExplanationData(
                title = "Hoverfly bilang Natural Ally ng Sibuyas",
                summary = "Ang larvae nito ay mabilisang lumalamon ng hanggang 40 thrips at aphids bawat araw.",
                details = listOf(
                    "Huwag patayin ang maliliit na berdeng tila higad na dahan-dahang gumagapang sa dahon—ito ang hoverfly larvae!"
                )
            )
        ),
        CritterItem(
            name = "Ladybug / Ladybird Beetle",
            role = "Beneficial Predator",
            attractsWith = "Mababang pesticide usage at kasamang tanim na cosmos at marigold",
            explanation = ExplanationData(
                title = "Ladybug",
                summary = "Kinakain ng ladybug at ng larvae nito ang maliliit na thrips at mites na nagtatago sa dahon ng sibuyas.",
                details = listOf(
                    "Pinapanatili ang natural ecological balance sa halamanan nang walang kemikal."
                )
            )
        ),
        CritterItem(
            name = "Green Lacewing",
            role = "Beneficial Predator",
            attractsWith = "Pollen at nectar ng mga kasamang bulaklak sa gilid ng plot",
            explanation = ExplanationData(
                title = "Green Lacewing",
                summary = "Tinaguriang 'aphid lion', nilalamon nito ang mga surot, thrips nymphs, at maliliit na uod.",
                details = listOf(
                    "Nakatutulong nang malaki sa gabi kung kailan aktibo ang mga peste."
                )
            )
        )
    ),
    careGuide = listOf(
        HowToSectionItem(
            title = "Pagtatambak ng Lupa para sa Maputing Tangkay (Blanching / Earthing-Up)",
            summary = "Paraan upang maging mahaba, makinis, malutong, at matamis ang puting tangkay ng dahon ng sibuyas.",
            steps = listOf(
                "Simulan sa ika-3 hanggang ika-4 na linggo pagkatanim kapag may taas nang 15–20 cm ang mga suwi.",
                "Maghakot ng pinong compost o buhaghag na lupang may halong CRH at itambak sa paligid ng base ng halaman (3–5 cm ang taas).",
                "Ulitin ang pagtatambak pagkaraan ng 2 linggo habang tumataas ang halaman, ngunit huwag takpan ang pinakagitnang usbong.",
                "Ang kawalan ng liwanag sa natatakpang tangkay ang nag-aalis ng pait at lumilikha ng matamis at malutong na puting shaft."
            ),
            tips = listOf(
                "Gawin ang earthing-up kapag tuyo ang lupa upang hindi mapuno ng putik ang pagitan ng mga dahon.",
                "Maaari ring gumamit ng karton o paper sleeves sa container gardening."
            )
        ),
        HowToSectionItem(
            title = "Wastong Patubig at Pagpapatuyo sa Raised Beds",
            summary = "Pagpapanatili ng tamang moisture nang hindi nabubulok ang mga ugat at basal plate.",
            steps = listOf(
                "Diligan ang kamada 2 hanggang 3 beses bawat linggo tuwing 6:00 hanggang 8:00 ng umaga.",
                "Gumamit ng drip irrigation o banayad na pandilig malapit sa ugat.",
                "Siguraduhing may 15–20 cm taas ang raised bed upang mabilis umagos ang labis na tubig kapag umuulan nang malakas.",
                "Maglagay ng manipis na rice straw mulch upang mapanatiling malamig ang lupa at maiwasan ang evaporation."
            ),
            tips = listOf(
                "Bawasan ang pagdidilig 3–5 araw bago ang malawakang pag-ani upang hindi malambot o madaling mabulok ang mga tangkay sa imbakan."
            )
        ),
        HowToSectionItem(
            title = "Pagpaparami sa Pamamagitan ng Paghahati ng Kumpol (Vegetative Division)",
            summary = "Mabilis at libreng paraan ng pagpaparami gamit ang mga suwi ng Sibuyas Tagalog.",
            steps = listOf(
                "Pumili ng malusog at mayabong na kumpol ng green onion na may 8 hanggang 12 sanga/suwi.",
                "Marahang bunutin ang buong kumpol gamit ang trowel o spading fork.",
                "Gamit ang mga kamay o malinis na kutsilyo, paghiwa-hiwalayin ang kumpol sa maliliit na grupo na may tig-2 hanggang 3 tangkay at sariling mga ugat.",
                "Gupitin ang itaas na 1/3 bahagi ng mga dahon upang mabawasan ang water loss habang nag-uugat.",
                "Itanim agad sa bagong kamada sa lalim na 3–4 cm at diligan nang sagana."
            ),
            tips = listOf(
                "Maaari ring itanim muli ang mga pinutol na ugat (white root base) ng green onions na binili mula sa palengke!"
            )
        )
    ),
    seasonalCalendar = listOf(
        SeasonalCalendarMonth("Ene", "Mataas na Ani (Peak Season)", "Malamig na simoy at buong araw; pinakamababang banta ng purple blotch", Color(0xFF2E7D32)),
        SeasonalCalendarMonth("Peb", "Mataas na Ani (Peak Season)", "Mabilis na paglaki; mainam sa regular na patubig at earthing-up", Color(0xFF2E7D32)),
        SeasonalCalendarMonth("Mar", "Mainam na Ani (Dry Season)", "Bantayan ang onion thrips; maglagay ng mulch laban sa init", Color(0xFF388E3C)),
        SeasonalCalendarMonth("Abr", "Katamtaman (Dry Season)", "Kailangan ang araw-araw na umagang patubig; mag-spray ng neem oil", Color(0xFFF57F17)),
        SeasonalCalendarMonth("Mayo", "Paghahanda sa Tag-ulan", "Itanim sa matataas na raised beds na may maayos na drainage kanal", Color(0xFFF57F17)),
        SeasonalCalendarMonth("Hun", "Tag-ulan (May Silong / Raised)", "Mag-spray ng copper bio-fungicide laban sa fungal spores", Color(0xFFE65100)),
        SeasonalCalendarMonth("Hul", "Tag-ulan (Rain Shelter)", "Iwasan ang baha; mas mainam sa containers at sheltered garden beds", Color(0xFFE65100)),
        SeasonalCalendarMonth("Ago", "Tag-ulan (Rain Shelter)", "Pitasin ang apektadong dahon; tiyakin ang sirkulasyon ng hangin", Color(0xFFE65100)),
        SeasonalCalendarMonth("Set", "Paghahanda sa Pagtatanim", "Paghahanda ng kamada, paglalagay ng compost at CRH para sa bagong siklo", Color(0xFF1565C0)),
        SeasonalCalendarMonth("Okt", "Simula ng Pagtatanim", "Paghahasik ng binhi at pagtatanim ng mga suwi matapos ang habagat", Color(0xFF2E7D32)),
        SeasonalCalendarMonth("Nob", "Aktibong Paglaki", "Napakagandang panahon para sa clumping at rapid tillering", Color(0xFF2E7D32)),
        SeasonalCalendarMonth("Dis", "Mataas na Ani (Peak Season)", "Pambansang pangangailangan sa kapaskuhan; mataas ang presyo sa merkado", Color(0xFF2E7D32))
    ),
    locationData = LocationDifficultyData(
        difficulty = "Napakadali (Beginner Friendly)",
        hardinessZone = "Highland & Lowland Philippines",
        tempRange = "18°C–32°C",
        sunlight = "Buong Araw (Full Sun, 6+ Oras)",
        suitableAlso = "Raised beds / Pots / Container Gardening / Hydroponics"
    ),
    soilPrep = SoilPrepData(
        phRange = "6.0–7.0",
        soilTypes = "Buhaghag na Sandy Loam o Loam na mayaman sa Organiko",
        recommendation = "Bungkalin ang kamada sa lalim na 20–25 cm. Ihalo ang 30% decomposed compost o vermicompost, 20% Carbonized Rice Hull (CRH) para sa buhaghag na drainage, at 50% topsoil. Maglagay ng 14-14-14 complete fertilizer bilang basal bago magtanim."
    ),
    growthTimeline = listOf(
        TimelineStageItem(
            stageName = "1. Pagsibol o Pagtatanim ng Suwi (Emergence / Sprouting)",
            durationDays = "Araw 1–10",
            description = "Pagsibol ng pinong berdeng sibol mula sa binhi, o paglalabas ng bagong ugat at berdeng dulo ng mga itinanim na root cuttings."
        ),
        TimelineStageItem(
            stageName = "2. Pagtatatag ng Ugat (Seedling Establishment)",
            durationDays = "Araw 10–25",
            description = "Paglalim ng fibrous root system sa 10–15 cm lalim; paglitaw ng 3 hanggang 5 matitigas na tubular na dahon."
        ),
        TimelineStageItem(
            stageName = "3. Pagsuwi at Pagdami (Tillering & Clumping)",
            durationDays = "Araw 25–40",
            description = "Aktibong paglabas ng mga bagong suwi mula sa base; lumalawak ang kumpol patungong 4–8 tangkay bawat puno. Panahon ng unang side-dress ng abono."
        ),
        TimelineStageItem(
            stageName = "4. Pagpapahaba at Blanching (Shaft Elongation)",
            durationDays = "Araw 40–50",
            description = "Pagtatambak ng lupa (earthing-up) upang mapanatiling puti, malutong, at makatas ang 10–15 cm ibabang bahagi ng tangkay."
        ),
        TimelineStageItem(
            stageName = "5. Ganap na Anihan o Ratoon (Harvest Maturity)",
            durationDays = "Araw 45–60+",
            description = "May taas nang 35–50 cm at may 1 cm diyametro ang tangkay; handa nang bunutin nang buo para sa palengke o gupitin para sa kusina."
        )
    ),
    howTos = listOf(
        HowToSectionItem(
            title = "Paano Itanim ang Pinutol na Ugat ng Green Onion mula sa Palengke (Regrowing Scallions)",
            summary = "Mabisang paraan ng zero-waste gardening gamit ang tira-tirang kusina.",
            steps = listOf(
                "Kapag gumagamit ng dahon ng sibuyas sa pagluluto, gupitin ang ibaba na nag-iiwan ng 2.5 hanggang 3 cm ng puting tangkay kasama ang mga ugat sa ilalim.",
                "Ilagay muna sa maliit na baso na may 1 cm tubig sa loob ng 2–3 araw sa tabi ng bintana hanggang sa lumabas ang mga bagong berdeng sibol.",
                "Itanim sa paso o kamada na may buhaghag na potting mix, ilubog ang ugat hanggang sa kalahati ng puting tangkay.",
                "Diligan tuwing umaga at ilagay sa nasisikatan ng araw. Makalipas ang 2–3 linggo, mayroon ka nang sariwang dahon ng sibuyas!"
            )
        ),
        HowToSectionItem(
            title = "Hakbang-Hakbang na Pag-ani Gamit ang 'Cut-and-Come-Again' Paraan",
            summary = "Paraan upang hindi na muling bumili o magtanim bawat buwan.",
            steps = listOf(
                "Gamit ang matalas at malinis na gunting ng hardin, gupitin ang mga panlabas o buong kumpol ng dahon 2.5 hanggang 3 cm sa ibabaw ng lupa.",
                "Huwag gupitin nang sagad sa pinakaugat (basal plate) upang hindi mamatay ang halaman.",
                "Maglagay ng kaunting compost tea o tunaw na 14-14-14 sa paligid ng puno matapos gupitan.",
                "Sa loob ng 10 hanggang 15 araw, tutubo muli ang buong kumpol ng malulusog na dahon!"
            )
        ),
        HowToSectionItem(
            title = "Paano Maiiwasan ang Paninilaw at Pagkatuyo ng Dulo ng Dahon (Tip Burn)",
            summary = "Pagtugon sa karaniwang problema ng mga nagtatanim ng alliums.",
            steps = listOf(
                "Panatilihing pantay ang pagdidilig; iwasan ang pabago-bagong pagkatuyo at pagbaha ng lupa.",
                "Maglagay ng Potassium at Calcium (tulad ng fermented eggshell solution o complete fertilizer) upang patibayin ang dulo ng dahon.",
                "Mag-spray ng neem solution kung may nakikitang maliliit na thrips na sumisipsip sa tisyu.",
                "Iwasan ang labis na kemikal na nitrogen na nagdudulot ng mabilis ngunit mahinang paghaba ng dahon."
            )
        )
    ),
    faqs = listOf(
        FaqItem(
            question = "Paano ko pahahabain ang puting bahagi ng tangkay ng aking green onion?",
            answer = "Ang sikreto ay tinatawag na 'blanching' o 'earthing-up'. Habang tumataas ang dahon ng sibuyas mula ika-3 linggo, regular na magtambak ng 3 hanggang 5 cm ng buhaghag na lupa o compost sa paligid ng puno. Dahil hindi nasisikatan ng araw ang natatakpang bahagi, hindi ito nakakagawa ng berdeng chlorophyll at nananatiling purong puti, malutong, at matamis ang tangkay."
        ),
        FaqItem(
            question = "Bakit may maliliit na pilak na batik at natutuyo ang dulo ng aking mga dahon ng sibuyas?",
            answer = "Ito ay klasikong palatandaan ng pinsala mula sa Onion Thrips (Thrips tabaci). Ang maliliit na insektong ito ay kumakaskas sa ibabaw ng dahon at sumisipsip ng katas ng halaman. Maaari itong kontrolin sa pamamagitan ng pag-spray ng Neem oil (5 ml bawat litro ng tubig na may kaunting sabon) tuwing hapon, paglalagay ng asul o dilaw na sticky traps, at pagdidilig sa umaga upang madagdagan ang halumigmig na kinatatakutan ng thrips."
        ),
        FaqItem(
            question = "May butas sa loob ng dahon at may uod na kumakain sa loob—ano ito at paano sugpuin?",
            answer = "Ito ang 'Harabas' o Beet Armyworm (Spodoptera exigua). Ang mga batahang uod ay pumapasok sa loob ng hungkag (tubular) na dahon ng sibuyas kung saan ligtas sila sa kontak na spray. Pisilin nang marahan ang dahon sa bahaging kinaroroonan ng uod upang mapatay ito, o mag-spray ng biological control agent tulad ng Bacillus thuringiensis (Bt) na pumapatay sa uod kapag nakain nito ang tisyu."
        ),
        FaqItem(
            question = "Maaari ko bang itanim ang green onion sa mga paso o plastic bottles sa urban garden?",
            answer = "Oo, siguradong-sigurado! Ang dahon ng sibuyas ay isa sa pinakaangkop na gulay para sa container gardening at condo balconies. Nangangailangan lamang ito ng lalim ng paso na 15 hanggang 20 cm, may mga butas sa ilalim para sa drainage, at nakatatanggap ng di-bababa sa 4 hanggang 6 na oras ng sikat ng araw."
        ),
        FaqItem(
            question = "Ano ang pagkakaiba ng Green Onion, Scallion, at Sibuyas Tagalog?",
            answer = "Sa karaniwang gamit, ang mga katagang ito ay tumutukoy sa magkakahawig na bunching alliums. Ang 'Scallion' at 'Green Onion' ay pangkalahatang tawag sa mga uri ng Allium fistulosum o batang Allium cepa na hindi bumubuo ng malaking bilog na bombilya. Ang 'Sibuyas Tagalog' (Allium cepa var. aggregatum) ay isang katutubong uri na bumubuo ng kumpol ng maliliit na pulang bulb at mayayabong na luntiang dahon na paboritong panimpla sa Pilipinas."
        )
    ),
    nutritionTable = NutritionTableData(
        servingSize = "100 g (Sariwang Hilaw na Dahon ng Sibuyas)",
        calories = "32 kcal",
        macroNutrients = listOf(
            NutritionRowData("Protina (Protein)", "1.83 g", "3.7%"),
            NutritionRowData("Kabuuang Taba (Total Fat)", "0.19 g", "0.2%"),
            NutritionRowData("Carbohydrates", "7.34 g", "5.6%"),
            NutritionRowData("Dietary Fiber (Hibla)", "2.6 g", "10.4%"),
            NutritionRowData("Asukal (Natural Sugars)", "2.33 g", "—"),
            NutritionRowData("Tubig (Water Content)", "89.8 g", "—")
        ),
        microNutrients = listOf(
            NutritionRowData("Bitamina K1 (Phylloquinone)", "207 mcg", "173%"),
            NutritionRowData("Bitamina C (Ascorbic Acid)", "18.8 mg", "21%"),
            NutritionRowData("Bitamina A (RAE)", "50 mcg", "6%"),
            NutritionRowData("Folate (Bitamina B9)", "64 mcg", "16%"),
            NutritionRowData("Calcium (Kalsyo)", "72 mg", "7%"),
            NutritionRowData("Iron (Bakal)", "1.48 mg", "8%"),
            NutritionRowData("Potassium (K)", "276 mg", "6%"),
            NutritionRowData("Magnesium (Mg)", "20 mg", "5%"),
            NutritionRowData("Phosphorus (P)", "37 mg", "4%"),
            NutritionRowData("Zinc (Zn)", "0.39 mg", "4%")
        )
    ),
    exploreArticles = listOf(
        ArticleItem("Gabay sa Komersyal na Dahon ng Sibuyas sa Luzon", "Teknolohiya sa produksyon ng Paragon F1 at pamamahala sa patubig sa Gitnang Luzon para sa mataas na kita.", "6 min basahin"),
        ArticleItem("Sining ng Blanching: Paano Makamit ang Puting Tangkay", "Hakbang-hakbang na gabay sa earthing-up para sa malalambot at matatamis na scallions.", "4 min basahin"),
        ArticleItem("Regrowing Greens: Zero-Waste Kitchen Gardening", "Paraan ng pagpapatubo muli ng dahon ng sibuyas mula sa pinutol na ugat sa kusina.", "3 min basahin")
    )
)
