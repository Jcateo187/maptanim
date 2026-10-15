package com.maptanim.app.features.farm.dialogs.vegetables_information

import androidx.compose.ui.graphics.Color
import com.maptanim.app.R

/**
 * Rich, research-backed encyclopedia data for Potato / Patatas (Solanum tuberosum).
 * Tailored for Philippine highland commercial production (Benguet: Buguias, Mankayan, Atok;
 * Mountain Province: Bauko; Bukidnon; Davao del Sur - Mt. Apo foothills)
 * based on DA-BPI, BPI-BNCRDC (Baguio National Crop Research Development Center),
 * and MapTanim agronomic field standards.
 */
val potatoData = CropEncyclopedia(
    cropName = "Potato",
    localName = "Patatas (Solanum tuberosum)",
    scientificName = "Solanum tuberosum",
    category = "Tuber Vegetables (Solanaceae)",
    family = "Solanaceae",
    overview = "Ang Patatas (Solanum tuberosum) ay isa sa pinakamahalagang high-value cash crop at pampalakas na gulay sa Pilipinas, pangunahing itinatanim sa malamig na talampas at kabundukan ng Cordillera Administrative Region (Benguet: Buguias, Mankayan, Atok, Kibungan, Tublay; Mountain Province: Bauko) gayundin sa matataas na lugar ng Bukidnon at paanan ng Bundok Apo sa Davao del Sur.\n\nBilang isang tuber crop mula sa pamilyang Solanaceae, ang patatas ay nagbubunga ng mga binagong underground stems (stem tubers) na nagsisilbing imbakan ng carbohydrates, potassium, bitamina C, at resistensyang starch. Nangangailangan ito ng malamig na klimang subtropikal (15°C–20°C sa gabi) at malalim (25–30 cm), napakabuhaghag, at walang batong volcanic sandy loam na may bahagyang asim (pH 5.0–6.0) upang maiwasan ang sakit na Common Scab.\n\nKabilang sa mga pinakasusing agronomic protocol sa patatas ang 'chitting' o pre-sprouting ng certified seed tubers bago itanim, ang 'earthing-up' o paulit-ulit na pagtatambak ng lupa (ridging) sa paligid ng puno upang maprotektahan ang mga lumalaking tuber laban sa araw na nagdudulot ng nakalalasong solanine (green skin), at ang 'de-haulming' (pagputol ng mga dahon 10–14 araw bago mag-ani) upang kumapal at tumibay ang balat ng patatas bago imbakin.",
    selectedVariety = VarietyInfo(
        name = "Granola",
        producer = "BPI-BNCRDC / Philippine Highland Standard",
        type = "Yellow-Fleshed Semi-Early Highland Table Potato",
        badge = "National Standard",
        description = "Ang nangungunang pambansang barayti ng patatas sa Pilipinas na sumasakop sa mahigit 80% ng kabuuang ani sa Benguet. Nagbubunga ng makikinis, matitibay, at bilog hanggang bahagyang pahabang tubers na may matingkad na dilaw na laman at balat. Paborito sa kusinang Pilipino para sa nilaga, afritada, menudo, at french fries dahil sa katamtamang dry-matter content at hindi madaling madurog kapag iniluluto. Handa nang anihin sa loob ng 90–105 araw."
    ),
    otherVarieties = listOf(
        "Granola (BPI-BNCRDC Commercial Highland Standard)",
        "Igorota (BPI High-Yield & Late Blight Tolerant Selection)",
        "Solibao (High-Altitude Yellow Table Potato)",
        "Raniag (Early Maturing Round Tuber)",
        "Ganza (High Starch Cordillera Variety)",
        "Atlantic (White-Fleshed Chipping Potato for Processing)"
    ),
    carouselSlides = listOf(
        CarouselSlide(
            title = "Patatas sa Terraces ng Cordillera (Benguet)",
            subtitle = "Masaganang taniman sa Atok at Buguias sa ilalim ng malamig na simoy ng bundok",
            stage = "Pamumulaklak at Bulking Phase",
            badgeColor = Color(0xFF2E7D32),
            drawableResId = R.drawable.ph_potato_hero
        ),
        CarouselSlide(
            title = "Chitting ng mga Binhi sa Nursery Tray",
            subtitle = "Maingat na pagpapasibol ng maiikling malulusog na chits sa malilim na bodega bago itanim",
            stage = "Chitting & Sprouting Stage",
            badgeColor = Color(0xFF1565C0),
            drawableResId = R.drawable.ph_potato_seedlings
        ),
        CarouselSlide(
            title = "Pagtatambak ng Kamada (Earthing-Up / Ridging)",
            subtitle = "Pagtatakip ng lupa sa mga stolon upang maprotektahan ang tubers laban sa greening",
            stage = "Earthing-Up & Tuber Bulking",
            badgeColor = Color(0xFFE65100),
            drawableResId = R.drawable.ph_potato_hilling
        ),
        CarouselSlide(
            title = "Masaganang Ani ng Granola sa Benguet",
            subtitle = "Makikinis at malalaking patatas na bagong bunot mula sa matabang volcanic soil",
            stage = "Pang-umagang Pag-aani (Harvest)",
            badgeColor = Color(0xFF6A1B9A),
            drawableResId = R.drawable.ph_potato_harvest
        )
    ),
    quickInfo = listOf(
        QuickInfoItem(
            key = "lifespan",
            label = "Buhay ng Halaman",
            value = "90–110 Araw",
            explanation = ExplanationData(
                title = "Siklo ng Buhay ng Patatas (Crop Lifecycle)",
                summary = "90 hanggang 110 araw mula sa pagtatanim ng pinasibol na seed tuber hanggang ganap na anihan.",
                details = listOf(
                    "Pagsibol mula sa Tuber: 10–20 araw pagkatanim sa kamada.",
                    "Vegetative Growth & Canopy: 20–40 araw (pagsasagawa ng unang earthing-up).",
                    "Pamumulaklak at Tuber Initiation: 40–60 araw (pagsisimula ng pamumuo ng tubers sa dulo ng stolons).",
                    "Tuber Bulking: 60–85 araw (mabilisang paglaki at pag-iipon ng starch).",
                    "Maturation & De-haulming: 85–100 araw (pagkatuyo ng dahon at pagpapatibay ng balat bago anihin)."
                )
            )
        ),
        QuickInfoItem(
            key = "harvest_time",
            label = "Unang Ani",
            value = "90–105 DAP",
            explanation = ExplanationData(
                title = "Tamang Panahon ng Pag-aani",
                summary = "90 hanggang 105 araw pagkatanim (Days After Planting).",
                details = listOf(
                    "Pinuputol ang mga dahon (de-haulming) 10–14 araw bago hukayin upang tumibay ang balat.",
                    "Inaani kapag tuyo ang lupa sa umaga gamit ang garden spading fork upang hindi masugatan ang laman."
                )
            )
        ),
        QuickInfoItem(
            key = "spacing",
            label = "Agwat ng Tanim",
            value = "25–30 cm (Hanay 60–75 cm)",
            explanation = ExplanationData(
                title = "Tamang Agwat ng Taniman at Kamada",
                summary = "25–30 cm sa pagitan ng mga binhi at 60–75 cm sa pagitan ng mga tudling (furrows).",
                details = listOf(
                    "Kailangan ang malapad na hanay (60–75 cm) upang may sapat na lupang maitatambak sa bawat puno habang lumalaki.",
                    "Nagbibigay ng 45,000 hanggang 55,000 puno bawat ektarya sa mga kamada ng Benguet."
                )
            )
        ),
        QuickInfoItem(
            key = "watering",
            label = "Patubig",
            value = "2–3 Beses kada Linggo",
            explanation = ExplanationData(
                title = "Pangangailangan sa Tubig at Moisture",
                summary = "Nangangailangan ng tuloy-tuloy at pantay na halumigmig lalo na sa yugto ng tuber bulking.",
                details = listOf(
                    "Ang biglaang tagtuyot na sinusundan ng malakas na tubig ay nagdudulot ng biyak sa laman (tuber cracking) at guwang sa gitna (hollow heart).",
                    "Itigil ang pagdidilig 10 araw bago mag-ani upang matuyo ang balat at maiwasan ang soft rot."
                )
            )
        ),
        QuickInfoItem(
            key = "sunlight",
            label = "Sikat ng Araw",
            value = "Buong Araw (Malamig na 15–20°C)",
            explanation = ExplanationData(
                title = "Klima at Sikat ng Araw",
                summary = "Buong araw (6–8 oras) ngunit nangangailangan ng malamig na temperatura ng kabundukan.",
                details = listOf(
                    "Kapag lumagpas sa 25°C ang temperatura sa gabi, humihinto ang pamumuo ng tubers (tuberization stops).",
                    "Kaya naman sa Pilipinas, sa Benguet, Mt. Province, at matataas na talampas lamang ito komersyal na lumalago."
                )
            )
        ),
        QuickInfoItem(
            key = "soil_ph",
            label = "Lupa at pH",
            value = "pH 5.0–6.0 (Sandy Loam)",
            explanation = ExplanationData(
                title = "Tamang Asim ng Lupa (pH Level)",
                summary = "Mas gusto ng patatas ang bahagyang maasim na lupa (pH 5.0–6.0).",
                details = listOf(
                    "Ang bahagyang maasim na lupa ay natural na pumipigil sa bacterial disease na Common Scab (Streptomyces scabies).",
                    "Nangangailangan ng buhaghag at walang batong lupa upang hindi maging pilipit o bansot ang mga patatas."
                )
            )
        )
    ),
    companionPlants = listOf(
        PlantRelationItem(
            name = "Beans / Sitaw (Legumes)",
            scientificName = "Phaseolus vulgaris / Fabaceae",
            isBeneficial = true,
            role = "Nitrogen Enrichment & Ground Shade",
            explanation = ExplanationData(
                title = "Patatas + Sitaw / Bush Beans",
                summary = "Mabisang tambalan: ang mga legumbre ay nagbibigay ng natural na nitroheno sa lupa na kailangan ng malagong dahon ng patatas.",
                details = listOf(
                    "Maaaring itanim bilang kasamang hanay o kasunod na rotation crop.",
                    "Hindi nag-aagawan sa espasyo dahil nasa magkaibang lalim ang mga ugat."
                )
            )
        ),
        PlantRelationItem(
            name = "Corn (Mais)",
            scientificName = "Zea mays",
            isBeneficial = true,
            role = "Windbreak & Shade Barrier",
            explanation = ExplanationData(
                title = "Patatas + Mais",
                summary = "Nagsisilbing proteksyon laban sa malalakas na hanging amihan sa gilid ng mga kamada sa bundok.",
                details = listOf(
                    "Maaaring itanim bilang border strips sa gilid ng taniman."
                )
            )
        ),
        PlantRelationItem(
            name = "Cabbage (Repolyo)",
            scientificName = "Brassica oleracea",
            isBeneficial = true,
            role = "Highland Polyculture Intercrop",
            explanation = ExplanationData(
                title = "Patatas + Repolyo sa Benguet",
                summary = "Klasikong tambalan ng mga magsasaka sa Cordillera: mababaw ang ugat ng repolyo at hindi nakakasagabal sa underground tubers.",
                details = listOf(
                    "Tumutulong sa pag-maximize ng limitadong lupang terraces sa kabundukan."
                )
            )
        ),
        PlantRelationItem(
            name = "Marigold (Tagetes)",
            scientificName = "Tagetes erecta",
            isBeneficial = true,
            role = "Nematode & Pest Deterrent",
            explanation = ExplanationData(
                title = "Marigold Bio-Shield",
                summary = "Ang ugat ng marigold ay naglalabas ng alpha-terthienyl na pumapatay sa root-knot nematodes sa kamada.",
                details = listOf(
                    "Pinoprotektahan ang balat ng patatas laban sa mga mikro-bulate na nagdudulot ng bukol."
                )
            )
        )
    ),
    combativePlants = listOf(
        PlantRelationItem(
            name = "Tomato (Kamatis)",
            scientificName = "Solanum lycopersicum",
            isBeneficial = false,
            role = "Late Blight Cross-Infection (Nakamamatay na Amag)",
            explanation = ExplanationData(
                title = "Bakit Bawal Pagsamahin ang Patatas at Kamatis?",
                summary = "Parehong kabilang sa Solanaceae family na lubhang madaling kapitan ng Phytophthora infestans (Late Blight).",
                details = listOf(
                    "Ang spores ng Late Blight ay mabilisang lumilipat mula kamatis patungong patatas at kabaligtaran, na kayang puksain ang buong taniman sa loob ng 72 oras.",
                    "Mag-iwan ng di-bababa sa 10 metrong distansya sa pagitan ng kanilang mga kamada."
                )
            )
        ),
        PlantRelationItem(
            name = "Eggplant (Talong)",
            scientificName = "Solanum melongena",
            isBeneficial = false,
            role = "Ralstonia Wilt & Flea Beetle Vector",
            explanation = ExplanationData(
                title = "Patatas + Talong Incompatibility",
                summary = "Nagsasalinan ng bacterial wilt (Ralstonia solanacearum) at flea beetles.",
                details = listOf(
                    "Maaaring maipasa ang mga sakit sa lupa na magtatagal ng maraming taon sa plot."
                )
            )
        ),
        PlantRelationItem(
            name = "Squash / Kalabasa",
            scientificName = "Cucurbita moschata",
            isBeneficial = false,
            role = "Aggressive Foliar Smothering",
            explanation = ExplanationData(
                title = "Patatas + Kalabasa",
                summary = "Gumagapang ang malalapad na dahon ng kalabasa at tinatakpan ang sikat ng araw na kailangan ng patatas.",
                details = listOf(
                    "Nagdudulot din ng mataas na halumigmig sa ilalim na nagpapasimula ng foliar mold."
                )
            )
        )
    ),
    nutritionBadges = listOf(
        NutritionBadgeItem(
            code = "POTASSIUM",
            name = "Potassium (K)",
            percentageDaily = "19%",
            role = "Presyon ng Dugo at Muscle Function",
            explanation = ExplanationData(
                title = "Mataas na Potassium sa Sariwang Patatas",
                summary = "Naglalaman ng 897 mg ng Potassium sa bawat 100g serving—mas mataas pa kaysa sa saging!",
                details = listOf(
                    "Tumutulong magpanatili ng normal na presyon ng dugo sa pamamagitan ng pagbalanse sa sodium.",
                    "Mahalaga para sa tamang pagtibok ng puso at contraction ng mga kalamnan."
                )
            )
        ),
        NutritionBadgeItem(
            code = "VIT_C",
            name = "Bitamina C",
            percentageDaily = "32%",
            role = "Immunity & Tissue Repair",
            explanation = ExplanationData(
                title = "Makapangyarihang Bitamina C",
                summary = "Isang tuber na sagana sa Vitamin C (28.7 mg bawat 100g) na nagpapalakas ng resistensya laban sa sakit.",
                details = listOf(
                    "Pinapanatili ang tibay ng collagen sa mga kasu-kasuan at balat.",
                    "Tumutulong sa mas mabilis na paghigop ng plant-based iron mula sa pagkain."
                )
            )
        ),
        NutritionBadgeItem(
            code = "VIT_B6",
            name = "Bitamina B6",
            percentageDaily = "24%",
            role = "Metabolism & Brain Health",
            explanation = ExplanationData(
                title = "Bitamina B6 (Pyridoxine)",
                summary = "Mahalagang ko-ensaym sa pagproseso ng protina, taba, at carbohydrates sa katawan.",
                details = listOf(
                    "Sumusuporta sa produksyon ng neurotransmitters para sa alertong pag-iisip at magandang mood."
                )
            )
        ),
        NutritionBadgeItem(
            code = "STARCH",
            name = "Resistant Starch",
            percentageDaily = "Mataas",
            role = "Gut Microbiome & Sustained Energy",
            explanation = ExplanationData(
                title = "Malusog na Complex Carbohydrate",
                summary = "Ang pinalamig na lutong patatas ay bumubuo ng resistant starch na nagsisilbing prebiotic fiber para sa tiyan.",
                details = listOf(
                    "Nagbibigay ng pangmatagalang enerhiya nang hindi biglang nagpapataas ng blood sugar."
                )
            )
        )
    ),
    pestsAndDiseases = listOf(
        PestDiseaseItem(
            name = "Late Blight (Phytophthora infestans)",
            scientificName = "Phytophthora infestans (Mont.) de Bary",
            severity = "Kritikal (Catastrophic Highland Threat)",
            symptoms = "Malalaking tila basang kulay kayumanggi hanggang itim na sugat sa mga dahon na may pinong puting amag sa ilalim kapag mahamog; nabubulok at nagiging kulay kape ang tubers sa ilalim ng lupa.",
            prevention = "Magtanim ng certified resistant seed tubers (tulad ng Igorota); panatilihin ang 75 cm agwat ng hanay para sa malayang hangin; iwasan ang pagbabasa ng dahon sa gabi.",
            treatment = "Agad na putulin at ligtas na sunugin ang apektadong dahon; mag-spray ng Copper Hydroxide o Mancozeb / Metalaxyl preventive bio-fungicides tuwing 5–7 araw sa tag-ulan.",
            explanation = ExplanationData(
                title = "Pagsugpo sa Late Blight sa Benguet",
                summary = "Ang pinakamapanganib na fungal-like oomycete sa patatas sa buong mundo na sumira sa Great Famine.",
                details = listOf(
                    "Lalaganap ito kapag malamig (12–18°C) at may tuloy-tuloy na ambon o hamog.",
                    "Mabilis kumalat ang milyun-milyong sporangia sa hangin kaya kritikal ang agaran at maagang pag-spray."
                )
            )
        ),
        PestDiseaseItem(
            name = "Bacterial Wilt / Brown Rot",
            scientificName = "Ralstonia solanacearum (Smith)",
            severity = "Kritikal (Lethal Soil-Borne Pathogen)",
            symptoms = "Biglaang panlalanta ng mga dahon sa araw kahit basa ang lupa; pangingitim ng vascular ring sa loob ng patatas na naglalabas ng maputing malapot na bacterial ooze kapag pinisil.",
            prevention = "Gumamit lamang ng certified clean seed tubers; magpatupad ng 3–4 na taong crop rotation gamit ang mais o repolyo (huwag itanim sa pinag-kamatisan).",
            treatment = "Bunutin at sunugin ang buong halaman kasama ang nakapalibot na lupa; lagyan ng apog (agricultural lime) ang butas.",
            explanation = ExplanationData(
                title = "Lanta-Bakterya sa Patatas",
                summary = "Bacterial pathogen na pumapasok sa ugat at humaharang sa daanan ng tubig ng halaman.",
                details = listOf(
                    "Walang kemikal na lunas kapag nakapasok na sa loob ng vascular system; pag-iwas at kalinisan ang tanging paraan."
                )
            )
        ),
        PestDiseaseItem(
            name = "Potato Tuber Moth (PTM)",
            scientificName = "Phthorimaea operculella (Zeller)",
            severity = "Mataas (Post-Harvest & Field Threat)",
            symptoms = "Maliliit na uod na gumagawa ng mga lagusan sa loob ng tubers na puno ng maitim na dumi (frass); tila may galeriya sa loob ng laman ng patatas.",
            prevention = "Kritikal ang regular na 'earthing-up' upang matakpan ng hindi bababa sa 5 cm lupa ang lahat ng tubers; maglagay ng pheromone traps sa bodega.",
            treatment = "Mag-spray ng Bacillus thuringiensis (Bt) sa dapit-hapon; takpan ng tuyong dahon ng eucalyptus o lantana ang mga inimbak na patatas.",
            explanation = ExplanationData(
                title = "Potato Tuber Moth Management",
                summary = "Nangingitlog ang munting paruparo sa mga nakalitaw na patatas o bitak sa tuyot na lupa.",
                details = listOf(
                    "Ang pagpapanatiling nakabaon ng patatas sa ilalim ng lupa ang pinakamabisang panangga."
                )
            )
        ),
        PestDiseaseItem(
            name = "Aphids (Green Peach Aphid)",
            scientificName = "Myzus persicae (Sulzer)",
            severity = "Mataas (Virus Vector Hazard)",
            symptoms = "Kumpol ng maliliit na berdeng kuto sa ilalim ng mga batang dahon; nagdudulot ng panunuktok ng dahon at nagkakalat ng Potato Leafroll Virus (PLRV).",
            prevention = "Maglagay ng dilaw na sticky traps sa plot; mag-spray ng neem extract sa unang senyales.",
            treatment = "Mag-spray ng Neem oil (5 ml/L na may mild soap) tuwing 5 araw o insecticidal soap solution.",
            explanation = ExplanationData(
                title = "Kuto ng Dahon bilang Tagapagdala ng Virus",
                summary = "Bagamat maliit, ang pinakamalaking banta ng aphids ay ang pagpapalaganap ng mga viral disease na nagpapabansot sa buong ani."
            )
        ),
        PestDiseaseItem(
            name = "Common Scab (Streptomyces scabies)",
            scientificName = "Streptomyces scabies",
            severity = "Katamtaman (Skin Quality Defect)",
            symptoms = "Magagaspang at tila kortsa (cork-like craters / pits) na mga sugat sa balat ng patatas; hindi naaapektuhan ang loob ngunit bumababa ang presyo.",
            prevention = "Panatilihin ang asim ng lupa sa pH 5.2 hanggang 5.8; iwasan ang labis na apog (liming); panatilihing mamasa-masa ang kamada sa unang 6 na linggo ng tuber set.",
            treatment = "Maglagay ng sulfur powder upang pababain ang pH ng lupa bago magtanim.",
            explanation = ExplanationData(
                title = "Common Scab Prevention",
                summary = "Lumalaganap ang bakterya kapag tuyo ang lupa at alkalina o mataas ang pH (>6.5)."
            )
        )
    ),
    goodAndBadCritters = listOf(
        CritterItem(
            name = "Ladybird Beetle (Ladybug)",
            role = "Beneficial Predator (Mabuting Mangangaso)",
            attractsWith = "Mga bulaklak ng dill, marigold, at coriander sa gilid ng kamada",
            explanation = ExplanationData(
                title = "Ladybug laban sa Aphids",
                summary = "Kumakain ng hanggang 50 aphids bawat araw, pinipigilan ang pagkalat ng PLRV virus sa patatas."
            )
        ),
        CritterItem(
            name = "Copidosoma koehleri",
            role = "Beneficial Parasitic Wasp",
            attractsWith = "Halamanang walang matatapang na kemikal na insecticide",
            explanation = ExplanationData(
                title = "Munting Wasp laban sa Tuber Moth",
                summary = "Nangingitlog sa loob ng itlog ng tuber moth, pinapatay ang peste bago pa makapasok sa patatas."
            )
        ),
        CritterItem(
            name = "White Grubs (Uod ng Salagubang)",
            role = "Harmful Pest (Mapanirang Peste sa Ugat)",
            attractsWith = "Hindi nabulok na sariwang dumi ng baka o manok sa kamada",
            explanation = ExplanationData(
                title = "Banta ng White Grubs sa Tubers",
                summary = "Nguya ng nguya sa ilalim ng lupa, gumagawa ng malalalim na butas sa laman ng patatas."
            )
        )
    ),
    careGuide = listOf(
        HowToSectionItem(
            title = "Chitting at Pre-Sprouting ng Certified Seed Tubers",
            summary = "Paraan ng pagpapasibol bago itanim upang mabilis at sabay-sabay ang pagtubo.",
            steps = listOf(
                "Pumili ng mga certified disease-free seed tubers na may laking kasing-laki ng itlog ng manok (40–60 gramo).",
                "Ayusin ang mga patatas sa mabababaw na wooden trays na nakatayo ang dulo na may maraming mata (rose end pataas).",
                "Ilagay sa malamig, maaliwalas, at may banayad na liwanag na lugar (diffused natural light) sa loob ng 2 hanggang 4 na linggo.",
                "Maghihintay hanggang sa magkaroon ng matitigas, maiikli (1–2 cm), at kulay berde o ube na mga sibol (chits).",
                "Huwag itanim ang mga puti at mahahabang sibol na tumubo sa madilim dahil marupok at madaling mabali ang mga ito."
            ),
            tips = listOf(
                "Ang chitting ay nagpapabilis ng anihan ng hanggang 10–14 na araw kumpara sa direktang unsprouted tubers."
            )
        ),
        HowToSectionItem(
            title = "Pagtatambak ng Lupa (Earthing-Up / Ridging Protocol)",
            summary = "Dalawang beses na pagtatambak upang maprotektahan ang tubers laban sa araw at tuber moth.",
            steps = listOf(
                "Unang Pagtatambak: Isagawa sa ika-3 hanggang ika-4 na linggo pagkatanim kapag may taas nang 15–20 cm ang mga halaman. Magtambak ng 10 cm ng lupa sa paligid ng puno.",
                "Ikalawang Pagtatambak: Isagawa sa ika-6 hanggang ika-7 linggo bago mamulaklak. Bumuo ng mataas at matambok na pilapil (ridge) na may taas na 20–25 cm.",
                "Siguraduhing walang nakalitaw na stolon o munting patatas sa ibabaw ng kamada.",
                "Ang pagtatakip ay pumipigil sa pamumula/pagberde ng balat (solanine greening) at pumipigil sa pangingitlog ng tuber moth."
            ),
            tips = listOf(
                "Gawin ang earthing-up kapag bahagyang mamasa-masa ang lupa upang hindi mabasag ang mga pinong ugat."
            )
        ),
        HowToSectionItem(
            title = "De-Haulming: Pagputol ng Dahon Bago Mag-Ani",
            summary = "Sikreto ng mga magsasaka sa Benguet para sa matibay na balat at mahabang shelf-life.",
            steps = listOf(
                "Kapag umabot na sa 85–95 araw ang taniman at nagsisimula nang manilaw ang mga lumang dahon, putulin ang lahat ng sanga at dahon 5 cm sa ibabaw ng lupa.",
                "Iwanan ang mga patatas sa ilalim ng lupa nang hindi dinidiligan sa loob ng 10 hanggang 14 na araw.",
                "Sa panahong ito, ang manipis na balat ng patatas ay kakapal, titigas (skin setting), at magiging matibay laban sa gasgas at pagkabulok.",
                "Makatipid din ito laban sa paglipat ng late blight spores mula sa dahon pababa sa tubers."
            )
        )
    ),
    seasonalCalendar = listOf(
        SeasonalCalendarMonth("Ene", "Mataas na Ani (Benguet Harvest)", "Malamig at tuyo ang panahon; pinakamataas na kalidad ng tubers na walang blight", Color(0xFF2E7D32)),
        SeasonalCalendarMonth("Peb", "Mataas na Ani (Peak Season)", "Pangunahing anihan ng Granola para sa merkado ng Maynila", Color(0xFF2E7D32)),
        SeasonalCalendarMonth("Mar", "Pagtatapos ng Dry Harvest", "Pag-iimbak at pagpili ng binhi para sa susunod na taniman", Color(0xFF388E3C)),
        SeasonalCalendarMonth("Abr", "Paghahanda ng Plot / Wet Crop", "Paghahanda ng kamada sa mataas na ridges para sa summer crop", Color(0xFFF57F17)),
        SeasonalCalendarMonth("Mayo", "Pagtatanim ng Summer Crop", "Pagtatanim ng chitted seed tubers bago pumasok ang malalakas na ulan", Color(0xFFF57F17)),
        SeasonalCalendarMonth("Hun", "Tag-ulan (Late Blight Alert)", "Kritikal na pag-spray ng bio-fungicide at maayos na drainage furrows", Color(0xFFE65100)),
        SeasonalCalendarMonth("Hul", "Tag-ulan / Habagat Season", "Bantayan ang waterlogging; maghukay ng 30 cm drainage kanal", Color(0xFFE65100)),
        SeasonalCalendarMonth("Ago", "Tag-ulan (Rain Shelter)", "Pag-aani ng off-season crop; mataas ang presyo sa merkado", Color(0xFFE65100)),
        SeasonalCalendarMonth("Set", "Paghahanda para sa Main Crop", "Malalim na pag-aararo, paglalagay ng compost at CRH para sa dry season", Color(0xFF1565C0)),
        SeasonalCalendarMonth("Okt", "Simula ng Pangunahing Taniman", "Pagtatanim ng Granola at Igorota seed tubers sa Benguet", Color(0xFF2E7D32)),
        SeasonalCalendarMonth("Nob", "Aktibong Paglaki at Hilling", "Unang earthing-up at balanseng pagpapataba sa malamig na hangin", Color(0xFF2E7D32)),
        SeasonalCalendarMonth("Dis", "Tuber Bulking Phase", "Mabilisang paglaki ng tubers sa ilalim ng lupa; regular na patubig", Color(0xFF2E7D32))
    ),
    locationData = LocationDifficultyData(
        difficulty = "Katamtaman (Moderate)",
        hardinessZone = "Highland Philippines (1,000–2,500 MASL: Benguet, Mt. Province, Bukidnon)",
        tempRange = "15°C–22°C (Malamig na gabi)",
        sunlight = "Buong Araw (Full Sun, 6–8 Oras)",
        suitableAlso = "Raised beds / Hill ridges / Deep containers (50L grow bags)"
    ),
    soilPrep = SoilPrepData(
        phRange = "5.0–6.0",
        soilTypes = "Buhaghag na Volcanic Sandy Loam na mayaman sa Organiko",
        recommendation = "Bungkalin ang kamada sa lalim na 30 cm at alisin ang lahat ng bato. Maghalo ng 30% decomposed chicken manure o compost, 20% Carbonized Rice Hull (CRH) para sa drainage, at 50% volcanic topsoil. Maglagay ng 14-14-14 basal fertilizer at huwag maglagay ng labis na apog (keep pH slightly acidic laban sa Common Scab)."
    ),
    growthTimeline = listOf(
        TimelineStageItem(
            stageName = "1. Pagsibol mula sa Seed Tuber (Sprouting & Emergence)",
            durationDays = "Araw 10–20",
            description = "Paglabas ng mga unang berdeng sanga mula sa nakabaong binhi; pag-uugat ng mga stolon sa mamasa-masang kamada."
        ),
        TimelineStageItem(
            stageName = "2. Paglago ng Dahon at Unang Hilling (Vegetative Phase)",
            durationDays = "Araw 20–40",
            description = "Mabilisang paglago ng makakapal na dahon; pagsasagawa ng unang pagtatambak ng lupa (10 cm ridge) upang takpan ang mga tangkay."
        ),
        TimelineStageItem(
            stageName = "3. Pamumulaklak at Tuber Set (Tuber Initiation)",
            durationDays = "Araw 40–60",
            description = "Paglabas ng maliliit na puti/ubeng bulaklak; pamamaga ng dulo ng mga underground stolon upang maging munting patatas. Ikalawang earthing-up."
        ),
        TimelineStageItem(
            stageName = "4. Paglaki ng Tubers (Tuber Bulking Stage)",
            durationDays = "Araw 60–85",
            description = "Mabilis na pag-iipon ng starch at carbohydrates sa loob ng patatas; kailangan ang tuloy-tuloy na patubig at Potash."
        ),
        TimelineStageItem(
            stageName = "5. Maturation, De-Haulming at Anihan (Harvest)",
            durationDays = "Araw 85–105",
            description = "Paninilaw ng dahon; pagputol ng sanga (de-haulming) 10 araw bago hukayin upang tumibay ang balat bago ibenta."
        )
    ),
    howTos = listOf(
        HowToSectionItem(
            title = "Paano Magpasibol ng Binhi (Chitting Process)",
            summary = "Wastong pagpapasibol sa mga binhing patatas bago itanim.",
            steps = listOf(
                "Pumili ng buo at certified seed potatoes (huwag gumamit ng galing sa palengke na tinapalan ng sprout inhibitors).",
                "Ilagay sa trays na may rose-end pataas sa ilalim ng diffused sunlight sa loob ng 2–3 linggo.",
                "Maghintay hanggang magkaroon ng matitigas at kulay ubeng 1–2 cm na sibol.",
                "Itanim nang buo sa lalim na 7–10 cm na nakaturo pataas ang mga sibol."
            )
        ),
        HowToSectionItem(
            title = "Hakbang-Hakbang na Pag-ani Nang Hindi Nasusugatan ang Tubers",
            summary = "Paraan ng paghuhukay gamit ang spading fork.",
            steps = listOf(
                "Tiyaking tuyo ang kamada sa umaga bago maghukay.",
                "Itusok ang spading fork 25 cm sa gilid ng hanay (malayo sa puno) upang hindi matuhog ang patatas.",
                "Marahang iangat ang buong kumpol ng lupa at ilantad ang mga patatas.",
                "Mano-manong pulutin ang mga patatas at ihiwalay sa crates.",
                "I-cure sa malamig at madilim na lugar sa loob ng 5–7 araw upang maghilom ang maliliit na gasgas."
            )
        ),
        HowToSectionItem(
            title = "Paano Maiiwasan ang Green Solanine Poisoning sa Patatas",
            summary = "Proteksyon laban sa nakalalasong berdeng balat ng patatas.",
            steps = listOf(
                "Huwag hayaang masikatan ng araw ang lumalaking patatas sa ilalim ng lupa.",
                "Magsagawa ng regular na pagtatambak ng lupa (earthing-up) tuwing ika-3 at ika-6 na linggo.",
                "Maglagay ng dayami sa ibabaw ng kamada.",
                "Itapon o huwag kainin ang mga patatas na may matingkad na berdeng balat dahil may taglay itong solanine alkaloid."
            )
        )
    ),
    faqs = listOf(
        FaqItem(
            question = "Maaari ba akong magtanim ng patatas sa kapatagan o lowland tulad ng Maynila?",
            answer = "Napakahirap magpalaki ng komersyal na patatas sa lowland dahil nangangailangan ito ng malamig na temperatura sa gabi (15°C–20°C). Sa mainit na klima ng kapatagan (>25°C sa gabi), tutubo ang malalagong dahon ngunit hindi mamumuo ang mga patatas sa ilalim ng lupa (tuberization failure). Kung nais magtanim sa kapatagan, gawin ito sa pinakamalamig na buwan (Disyembre hanggang Pebrero) sa matataas na grow bags sa malilim na bahagi, o magtanim na lamang ng Kamote (Sweet Potato) na perpekto sa tropikal na kapatagan."
        ),
        FaqItem(
            question = "Bakit naging berde ang balat ng aking patatas at ligtas ba itong kainin?",
            answer = "Nagiging berde ang patatas kapag nasisikatan ng araw habang lumalaki sa kamada o matapos anihin. Ang kulay berde ay chlorophyll, ngunit kasabay nitong gumagawa ang halaman ng Solanine—isang natural na glycoalkaloid na nakakalason sa tao at nagdudulot ng pananakit ng tiyan, pagsusuka, at panlalata. Huwag kainin ang berdeng bahagi; balatan nang malalim o itapon ang buong patatas kung malala ang pagka-berde."
        ),
        FaqItem(
            question = "Ano ang 'de-haulming' at bakit ito kailangang gawin bago mag-ani?",
            answer = "Ang de-haulming ay ang pagputol o pag-aalis ng lahat ng dahon at sanga ng patatas 10 hanggang 14 na araw bago tuluyang hukayin ang mga patatas. Sa panahong ito habang nakabaon pa sa tuyong lupa, ang manipis at malambot na balat ng patatas ay kakapal at titigas (skin curing). Dahil dito, hindi magagasgasan ang patatas habang inaani at naibibiyahe, at maiiwasan ang pagkabulok sa bodega."
        ),
        FaqItem(
            question = "Bakit may malalaking guwang o butas sa gitna ng aking patatas (Hollow Heart)?",
            answer = "Ito ay 'Hollow Heart' na dulot ng mabilis at pabago-bagong paglaki ng tuber. Karaniwan itong sanhi ng labis na chemical nitrogen kasabay ng pabago-bagong patubig (tagtuyot na sinundan ng biglaang labis na tubig sa yugto ng tuber bulking). Maiiwasan ito sa pamamagitan ng balanseng pataba na may sapat na Potassium at pantay na patubig gamit ang drip irrigation."
        ),
        FaqItem(
            question = "Maaari ko bang itanim ang patatas na nabili ko sa supermarket?",
            answer = "Hindi inirerekomenda para sa seryosong taniman. Karamihan sa mga patatas sa supermarket ay tinapalan ng chlorpropham o iba pang sprout inhibitors upang hindi sumibol sa estante. Bukod dito, maaari silang magdala ng mga nakatagong virus at bacterial ring rot. Laging gumamit ng certified seed potatoes mula sa mga accredited nurseries ng BPI o DA sa Baguio at Benguet."
        )
    ),
    nutritionTable = NutritionTableData(
        servingSize = "100 g (Sariwang Hilaw na Patatas na may Balat)",
        calories = "77 kcal",
        macroNutrients = listOf(
            NutritionRowData("Protina (Protein)", "2.02 g", "4.0%"),
            NutritionRowData("Kabuuang Taba (Total Fat)", "0.09 g", "0.1%"),
            NutritionRowData("Carbohydrates", "17.47 g", "13.4%"),
            NutritionRowData("Dietary Fiber (Hibla)", "2.2 g", "8.8%"),
            NutritionRowData("Asukal (Natural Sugars)", "0.78 g", "—"),
            NutritionRowData("Tubig (Water Content)", "79.3 g", "—")
        ),
        microNutrients = listOf(
            NutritionRowData("Potassium (K)", "897 mg", "19%"),
            NutritionRowData("Bitamina C (Ascorbic Acid)", "28.7 mg", "32%"),
            NutritionRowData("Bitamina B6 (Pyridoxine)", "0.41 mg", "24%"),
            NutritionRowData("Folate (Bitamina B9)", "16 mcg", "4%"),
            NutritionRowData("Iron (Bakal)", "0.78 mg", "4%"),
            NutritionRowData("Magnesium (Mg)", "23 mg", "6%"),
            NutritionRowData("Phosphorus (P)", "57 mg", "6%"),
            NutritionRowData("Niacin (Bitamina B3)", "1.06 mg", "7%"),
            NutritionRowData("Manganese (Mn)", "0.15 mg", "7%")
        )
    ),
    exploreArticles = listOf(
        ArticleItem("Gabay sa Masaganang Patatas sa Benguet Highlands", "Mga agronomic protocol ng BPI-BNCRDC para sa 25–30 tonelada bawat ektarya gamit ang Granola at Igorota.", "7 min basahin"),
        ArticleItem("Pamamahala sa Late Blight sa Panahon ng Habagat", "Integrated pest management laban sa Phytophthora infestans sa kabundukan ng Cordillera.", "6 min basahin"),
        ArticleItem("Sining ng Chitting at De-Haulming para sa Matibay na Patatas", "Hakbang-hakbang na paraan upang mapataas ang germination rate at patibayin ang balat bago ibenta.", "5 min basahin")
    )
)
