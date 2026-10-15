package com.maptanim.app.features.farm.dialogs.vegetables_information

import androidx.compose.ui.graphics.Color
import com.maptanim.app.R

/**
 * Rich, research-backed encyclopedia data for Yardlong String Beans / Sitaw (Vigna unguiculata subsp. sesquipedalis).
 * Tailored for Philippine lowland and intermediate vegetable farms (Batangas, Laguna, Nueva Ecija, Pangasinan, Quezon)
 * and urban trellis gardens based on DA-BPI, East-West Seed, and MapTanim agronomic standards.
 */
val sitawData = CropEncyclopedia(
    cropName = "Yardlong String Beans",
    localName = "Sitaw (Vigna unguiculata subsp. sesquipedalis)",
    scientificName = "Vigna unguiculata subsp. sesquipedalis",
    category = "Legumes & Podded Vegetables (Fabaceae)",
    family = "Fabaceae",
    overview = "Ang Sitaw (Vigna unguiculata subsp. sesquipedalis), na kilala sa Ingles bilang Yardlong Bean, Asparagus Bean, o Pole String Bean, ay isa sa mga pinakatanyag na gulay sa tradisyunal na awiting 'Bahay Kubo' at pangunahing protina sa hapag-kainang Pilipino. Paboritong sangkap ito sa pambansang lutuin tulad ng sinigang, kare-kare, pinakbet, adobong sitaw, at ginisang gulay.\n\nBilang isang gumagapang na legumbre mula sa pamilyang Fabaceae, ang sitaw ay may likas na kakayahang magsagawa ng 'biological nitrogen fixation' sa tulong ng kapaki-pakinabang na Rhizobium bacteria sa mga bukol ng ugat nito (root nodules). Kinukuha nito ang nitroheno mula sa hangin at inilalagak sa lupa, kaya't ito ay napakahalagang 'soil-enriching' crop kapag isinasalit sa mga mabibigat na mamimili ng pataba tulad ng mais at kamatis.\n\nNangangailangan ang sitaw ng matibay na balag o tulos (A-frame bamboo trellis na may taas na 2–2.5 metro) upang malayang makagapang ang baging, magkaroon ng sapat na bentilasyon laban sa fungal rust, at magbunga ng tuwid, mahahaba (45–65 cm), at hindi putikang mga bunga. Mabilis ang siklo nito: nagsisimula ang unang pitas sa loob ng 48–55 araw mula paghasik, at inaani tuwing ikalawang araw (every other day) sa loob ng 6 hanggang 8 linggo.",
    selectedVariety = VarietyInfo(
        name = "Sandigan F1",
        producer = "East-West Seed Philippines",
        type = "High-Yielding Pole Yardlong Bean Hybrid",
        badge = "National Best Seller",
        description = "Ang nangungunang pambansang barayti ng sitaw sa Pilipinas. Kilala sa napakahaba (55–65 cm), tuwid, at matingkad na madilim na berdeng bunga na may makapal at malutong na laman na hindi madaling maging guwang (non-spongy). May mataas na resistensya laban sa Bean Pod Borer at Bean Rust, at tuloy-tuloy na namumunga tuwing 2 araw sa loob ng mahigit 2 buwan."
    ),
    otherVarieties = listOf(
        "Sandigan F1 (East-West Seed Pole Standard)",
        "Galante F1 (Heat-Tolerant & Prolific Podding)",
        "Negro Star (Black-Seeded Heavy Yield Standard)",
        "Mariposa (Purple-Tipped Specialty Pod)",
        "Batangas Pole Native (Heirloom Batangas Sitaw)",
        "Sinagtala F1 (Vigorous Continuous Flusher)"
    ),
    carouselSlides = listOf(
        CarouselSlide(
            title = "Sitaw sa Bamboo A-Frame Trellis sa Batangas",
            subtitle = "Mabubulas na baging na may laylay na mahahabang sitaw (50–65 cm)",
            stage = "Yugto ng Pamumunga (Podding Phase)",
            badgeColor = Color(0xFF2E7D32),
            drawableResId = R.drawable.ph_sitaw_hero
        ),
        CarouselSlide(
            title = "Pagsibol ng mga Punla sa Kamada",
            subtitle = "Matatabang cotyledons at unang tendrils na gumagapang sa kawayang tulos",
            stage = "Pagsibol at Paggapang",
            badgeColor = Color(0xFF1565C0),
            drawableResId = R.drawable.ph_sitaw_seedlings
        ),
        CarouselSlide(
            title = "Aktibong Pagbalag at Pamumulaklak",
            subtitle = "Mabubulas na trifoliate leaves at mapupusyaw na ubeng bulaklak sa balag",
            stage = "Flowering & Trellis Canopy",
            badgeColor = Color(0xFFE65100),
            drawableResId = R.drawable.ph_sitaw_trellis
        ),
        CarouselSlide(
            title = "Masaganang Pitas at Pagbubungkos",
            subtitle = "Sariwa at malulutong na sitaw na nakabigkis para sa lokal na pamilihan",
            stage = "Pang-araw-araw na Pag-ani (Harvest)",
            badgeColor = Color(0xFF6A1B9A),
            drawableResId = R.drawable.ph_sitaw_harvest
        )
    ),
    quickInfo = listOf(
        QuickInfoItem(
            key = "lifespan",
            label = "Buhay ng Halaman",
            value = "75–90 Araw",
            explanation = ExplanationData(
                title = "Siklo ng Buhay ng Sitaw (Crop Lifecycle)",
                summary = "75 hanggang 90 araw mula paghasik ng buto hanggang huling pitas ng mga bunga.",
                details = listOf(
                    "Pagsibol (Germination): 3–5 araw matapos ihasik ang buto nang direkta sa kamada.",
                    "Paggapang sa Tulos (Vining Phase): 7–25 araw kung saan kailangang iikot ang baging sa kawayan.",
                    "Pamumulaklak (Flowering): 35–45 araw (paglabas ng mapupusyaw na lila o ubeng bulaklak).",
                    "Unang Pitas ng Bunga: 48–55 araw kapag may haba nang 45–60 cm ang mga sitaw.",
                    "Tuloy-tuloy na Anihan: Tuwing 2 araw sa loob ng 30 hanggang 45 araw."
                )
            )
        ),
        QuickInfoItem(
            key = "harvest_time",
            label = "Unang Ani",
            value = "48–55 DAS",
            explanation = ExplanationData(
                title = "Panahon ng Unang Ani",
                summary = "48 hanggang 55 araw pagkahahasik ng buto (Days After Sowing).",
                details = listOf(
                    "Pitasin kapag malambot, malutong, at hindi pa bumubukol ang mga buto sa loob.",
                    "Inaani tuwing ikalawang araw (every 2 days); kapag naantala, magiging makunat at maguguwang ang laman.",
                    "Maingat na baliin sa tangkay at huwag sirain ang flower cushion (peduncle) na muling mamumulaklak."
                )
            )
        ),
        QuickInfoItem(
            key = "spacing",
            label = "Agwat ng Tanim",
            value = "20–25 cm (Hanay 75–100 cm)",
            explanation = ExplanationData(
                title = "Tamang Agwat ng Pagtatanim at Balag",
                summary = "20–25 cm sa pagitan ng mga puno at 75–100 cm sa pagitan ng mga hanay ng balag.",
                details = listOf(
                    "Maghasik ng 2 hanggang 3 buto bawat butas (hill) sa tabi ng kawayang tulos.",
                    "Ang malapad na agwat ng hanay (75–100 cm) ay kailangan para sa daanan habang namimitas at magandang sirkulasyon ng hangin."
                )
            )
        ),
        QuickInfoItem(
            key = "watering",
            label = "Patubig",
            value = "2–3 Beses kada Linggo",
            explanation = ExplanationData(
                title = "Pangangailangan sa Tubig",
                summary = "Kritikal ang regular na kahalumigmig sa panahon ng pamumulaklak at pamumunga.",
                details = listOf(
                    "Kapag dumanas ng tagtuyot habang namumulaklak, malalagas ang mga bulaklak (flower drop).",
                    "Iwasan ang pagbaha sa base ng puno dahil sensitibo ang ugat ng sitaw sa waterlogging na nagdudulot ng root rot."
                )
            )
        ),
        QuickInfoItem(
            key = "sunlight",
            label = "Sikat ng Araw",
            value = "Buong Araw (Full Sun)",
            explanation = ExplanationData(
                title = "Pangangailangan sa Liwanag",
                summary = "Nangangailangan ng buong araw (6–8 oras ng sikat ng araw) araw-araw.",
                details = listOf(
                    "Mahilig sa init ng tropiko (25°C–35°C).",
                    "Sa lilim, humahaba nang labis ang baging ngunit kakaunti ang inilalabas na bulaklak."
                )
            )
        ),
        QuickInfoItem(
            key = "soil_ph",
            label = "Lupa at pH",
            value = "pH 5.5–6.8 (Buhaghag na Loam)",
            explanation = ExplanationData(
                title = "Tamang Lupa para sa Sitaw",
                summary = "Buhaghag, mayaman sa organikong pataba, at may mahusay na daluyan ng tubig.",
                details = listOf(
                    "Mabilis mag-ugat sa lupang may halong Carbonized Rice Hull (CRH) at vermicompost.",
                    "Ang neutral hanggang bahagyang maasim na pH (5.8–6.5) ay perpekto para sa aktibidad ng Rhizobium bacteria."
                )
            )
        )
    ),
    companionPlants = listOf(
        PlantRelationItem(
            name = "Corn (Mais)",
            scientificName = "Zea mays",
            isBeneficial = true,
            role = "Natural Trellis & Nitrogen Exchange",
            explanation = ExplanationData(
                title = "Sitaw + Mais (Tradisyunal na Tambalan)",
                summary = "Mabisang tandem: ang matatag na puno ng mais ay nagsisilbing natural na tulos para sa sitaw, habang ang sitaw ay nagpapataba ng lupa sa pamamagitan ng nitrogen fixation.",
                details = listOf(
                    "Ihasik ang sitaw kapag may taas nang 30 cm ang mais.",
                    "Kapwa malakas ang ani nang hindi nag-aagawan sa espasyo."
                )
            )
        ),
        PlantRelationItem(
            name = "Cucumber (Pipino)",
            scientificName = "Cucumis sativus",
            isBeneficial = true,
            role = "Shared Trellising & Soil Health",
            explanation = ExplanationData(
                title = "Sitaw + Pipino",
                summary = "Maaaring magkasama sa magkabilang panig ng A-frame trellis nang maayos.",
                details = listOf(
                    "Nakatutulong ang nitroheno ng sitaw sa malalabay na dahon ng pipino."
                )
            )
        ),
        PlantRelationItem(
            name = "Squash / Kalabasa",
            scientificName = "Cucurbita moschata",
            isBeneficial = true,
            role = "Living Mulch Ground Cover",
            explanation = ExplanationData(
                title = "Sitaw + Kalabasa",
                summary = "Gumagapang ang kalabasa sa ilalim bilang living mulch na pumipigil sa damo habang paitaas ang gapang ng sitaw sa balag.",
                details = listOf(
                    "Bahagi ng klasikong 'Three Sisters' polyculture system."
                )
            )
        ),
        PlantRelationItem(
            name = "Marigold (Tagetes)",
            scientificName = "Tagetes erecta",
            isBeneficial = true,
            role = "Aphid & Nematode Deterrent",
            explanation = ExplanationData(
                title = "Marigold Bio-Protection",
                summary = "Nagtataboy sa itim na kuto (black aphids) at pumipigil sa mga nematode sa ugat ng sitaw."
            )
        )
    ),
    combativePlants = listOf(
        PlantRelationItem(
            name = "Onion / Sibuyas",
            scientificName = "Allium cepa",
            isBeneficial = false,
            role = "Rhizobium Inhibition (Pinipigilan ang Nitrogen Fixation)",
            explanation = ExplanationData(
                title = "Bakit Bawal Katabi ng Sitaw ang Sibuyas at Alliums?",
                summary = "Ang ugat ng sibuyas ay naglalabas ng sulfur compounds at natural allicin na pumapatay sa kapaki-pakinabang na Rhizobium bacteria sa ugat ng sitaw.",
                details = listOf(
                    "Nawawalan ng kakayahan ang sitaw na mag-ipon ng natural na nitroheno, kaya naninilaw ang dahon at nalalagas ang bulaklak.",
                    "Mag-iwan ng hindi bababa sa 2 metrong agwat sa pagitan ng kanilang mga kamada."
                )
            )
        ),
        PlantRelationItem(
            name = "Garlic / Bawang & Green Onion",
            scientificName = "Allium sativum / Allium fistulosum",
            isBeneficial = false,
            role = "Legume Antagonism",
            explanation = ExplanationData(
                title = "Banta ng Lahat ng Uri ng Allium sa Sitaw",
                summary = "Lahat ng halaman sa pamilya ng sibuyas at bawang ay may katulad na negatibong epekto sa pagtubo at pamumunga ng mga legumbre."
            )
        ),
        PlantRelationItem(
            name = "Fennel",
            scientificName = "Foeniculum vulgare",
            isBeneficial = false,
            role = "Allelopathic Stunting",
            explanation = ExplanationData(
                title = "Fennel Allelopathy",
                summary = "Naglalabas ng kemikal sa lupa na pumipigil sa pag-usbong at pamumulaklak ng sitaw."
            )
        )
    ),
    nutritionBadges = listOf(
        NutritionBadgeItem(
            code = "PROTEIN",
            name = "Plant Protein",
            percentageDaily = "6%",
            role = "Muscle Repair & Growth",
            explanation = ExplanationData(
                title = "Likas na Protina mula sa Gulay",
                summary = "Naglalaman ng 2.8g ng de-kalidad na plant-based protein bawat 100g serving.",
                details = listOf(
                    "Mainam para sa mga nagpapalakas ng katawan at vegetarian diet.",
                    "Nagtutaguyod ng mabilis na paghilom ng tisyu ng katawan."
                )
            )
        ),
        NutritionBadgeItem(
            code = "FIBER",
            name = "Dietary Fiber",
            percentageDaily = "14%",
            role = "Digestion & Blood Sugar Balance",
            explanation = ExplanationData(
                title = "Masaganang Hibla sa Sitaw",
                summary = "Naglalaman ng 3.6g ng natutunaw at di-natutunaw na hibla para sa malusog na panunaw.",
                details = listOf(
                    "Tumutulong magpababa ng masamang kolesterol (LDL).",
                    "Nagpapabagal sa pagsipsip ng asukal sa dugo."
                )
            )
        ),
        NutritionBadgeItem(
            code = "FOLATE",
            name = "Folate (B9)",
            percentageDaily = "16%",
            role = "Red Blood Cells & Fetal Health",
            explanation = ExplanationData(
                title = "Mahalagang Folate para sa Dugo",
                summary = "Nagbibigay ng 62 mcg ng Folate (Vitamin B9) na kailangan sa paggawa ng red blood cells.",
                details = listOf(
                    "Napakainam para sa mga nagdadalantao upang maiwasan ang birth defects."
                )
            )
        ),
        NutritionBadgeItem(
            code = "VIT_C",
            name = "Bitamina C",
            percentageDaily = "21%",
            role = "Antioxidant & Collagen Defense",
            explanation = ExplanationData(
                title = "Bitamina C sa Sariwang Bunga",
                summary = "Pinoprotektahan ang mga selula laban sa free radicals at nagpapalakas ng resistensya laban sa impeksyon."
            )
        )
    ),
    pestsAndDiseases = listOf(
        PestDiseaseItem(
            name = "Bean Pod Borer (Maruca vitrata)",
            scientificName = "Maruca vitrata (Fabricius)",
            severity = "Kritikal (Critical Pod Pest)",
            symptoms = "Maliliit na butas sa mga buko ng bulaklak at bunga na may maitim na dumi (frass) sa paligid; nabubulok at napuputol ang dulo ng sitaw.",
            prevention = "Maglagay ng pheromone o light traps sa gabi; magtanim ng marigold bilang border; suriin ang mga bulaklak sa umaga.",
            treatment = "Mag-spray ng Bacillus thuringiensis (Bt) o Spinosad sa dapit-hapon tuwing 5–7 araw sa simula ng pamumulaklak.",
            explanation = ExplanationData(
                title = "Pagsugpo sa Bean Pod Borer (Maruca)",
                summary = "Ang numero unong peste ng sitaw sa Pilipinas na kayang sumira ng hanggang 70% ng ani.",
                details = listOf(
                    "Nangingitlog ang gamu-gamo sa mga bulaklak; pumapasok ang uod sa loob ng munting bunga.",
                    "Kritikal ang pag-spray sa yugto pa lamang ng pamumulaklak bago makapasok ang uod sa loob ng pod."
                )
            )
        ),
        PestDiseaseItem(
            name = "Black Cowpea Aphids (Aphis craccivora)",
            scientificName = "Aphis craccivora Koch",
            severity = "Mataas (High Infestation & Virus Vector)",
            symptoms = "Makakapal na kumpol ng maliliit na itim na kuto sa mga dulo ng baging, usbong, at bulaklak; naglalabas ng malagkit na honeydew na nagiging itim na amag (sooty mold).",
            prevention = "Maglagay ng dilaw na sticky traps; panatilihin ang ladybugs sa hardin; iwasan ang labis na chemical nitrogen.",
            treatment = "Mag-spray ng Neem oil (5 ml/L na may banayad na sabon) o chili-garlic extract tuwing 4–5 araw.",
            explanation = ExplanationData(
                title = "Itim na Kuto ng Sitaw",
                summary = "Bukod sa pagsipsip ng katas, ito ang pangunahing nagkakalat ng nakamamatay na Bean Common Mosaic Virus (BCMV)."
            )
        ),
        PestDiseaseItem(
            name = "Pod-Sucking Bug (Riptortus)",
            scientificName = "Riptortus pedestris / Nezara viridula",
            severity = "Mataas (Pod Deformity Threat)",
            symptoms = "Matutulis na surot na sumusundot sa mga murang sitaw; nagdudulot ng baluktot, kulubot, at tuyot na mga bunga na may mapuputing tuldok.",
            prevention = "Mano-manong hulihin ang mga surot sa maagang umaga habang mabagal pa silang lumipad; mag-spray ng neem repellent.",
            treatment = "Mag-spray ng botanical pyrethrum o Beauveria bassiana entomopathogenic spray.",
            explanation = ExplanationData(
                title = "Surot sa Bunga ng Sitaw",
                summary = "Sumisipsip ng gatas sa loob ng nagde-develop na buto, kaya nababansot ang buong pod."
            )
        ),
        PestDiseaseItem(
            name = "Bean Rust (Uromyces appendiculatus)",
            scientificName = "Uromyces appendiculatus",
            severity = "Katamtaman (Humid Season Foliar Disease)",
            symptoms = "Maliliit at mapupulang parang kalawang na bukol (reddish-brown powdery pustules) sa ilalim ng mga lumang dahon; natutuyo at nalalagas ang dahon.",
            prevention = "Panatilihin ang sapat na agwat ng balag para sa malayang sirkulasyon ng hangin; iwasan ang pagbabasa ng dahon sa gabi.",
            treatment = "Gupitin ang mga apektadong lumang dahon sa ibaba; mag-spray ng sulfur powder o Copper fungicide tuwing 7 araw.",
            explanation = ExplanationData(
                title = "Kalawang sa Dahon ng Sitaw",
                summary = "Lumalaganap kapag mahalumigmig ang panahon at siksikan ang mga dahon sa balag."
            )
        ),
        PestDiseaseItem(
            name = "Bean Common Mosaic Virus (BCMV)",
            scientificName = "Bean common mosaic virus",
            severity = "Kritikal (Systemic Virus)",
            symptoms = "Dilaw at berdeng tila batik-batik (mosaic pattern) na mga dahon; kulubot at nakabaluktot ang hugis ng dahon; bansot ang buong puno.",
            prevention = "Gumamit lamang ng certified virus-free seeds (tulad ng Sandigan F1); kontrolin agad ang aphids na nagdadala ng virus.",
            treatment = "Walang kemikal na gamot; agad na bunutin at sunugin ang mga apektadong puno upang hindi mahawa ang buong balag.",
            explanation = ExplanationData(
                title = "Mosaic Virus sa Sitaw",
                summary = "Mabilisang kumakalat sa pamamagitan ng katas at kuto; kalinisan at agarang rouging ang tanging lunas."
            )
        )
    ),
    goodAndBadCritters = listOf(
        CritterItem(
            name = "Honeybees & Native Bees",
            role = "Vital Pollinator (Mabuting Tagapag-Polina)",
            attractsWith = "Mga bukas na bulaklak ng sitaw sa maagang umaga",
            explanation = ExplanationData(
                title = "Pukyutan sa Taniman ng Sitaw",
                summary = "Kailangang-kailangan ang mga bubuyog sa umaga upang buksan at i-pollinate ang mga bulaklak para sa 100% pod set."
            )
        ),
        CritterItem(
            name = "Ladybird Beetle (Ladybug)",
            role = "Beneficial Predator",
            attractsWith = "Halamanang walang matatapang na kemikal na insecticide",
            explanation = ExplanationData(
                title = "Ladybug laban sa Black Aphids",
                summary = "Nilalamon ng ladybug at ng larvae nito ang daan-daang itim na kuto sa mga dulo ng baging."
            )
        ),
        CritterItem(
            name = "Maruca Moth",
            role = "Harmful Pest (Peste sa Gabi)",
            attractsWith = "Mga buko ng bulaklak ng sitaw",
            explanation = ExplanationData(
                title = "Paruparo ng Bean Pod Borer",
                summary = "Lumilipad sa gabi upang mangitlog sa mga bulaklak; hulihin gamit ang light traps."
            )
        )
    ),
    careGuide = listOf(
        HowToSectionItem(
            title = "Pagtatayo ng Matibay na Bamboo A-Frame Trellis (Balag at Tulos)",
            summary = "Paraan ng paggawa ng matibay na balag na kayang magbuhat ng mabibigat na baging.",
            steps = listOf(
                "Maghanda ng mga kawayang tulos na may haba na 2.0 hanggang 2.5 metro.",
                "Itusok ang mga tulos sa magkabilang gilid ng kamada sa agwat na 1 metro, at pagtagpuin ang mga dulo sa itaas upang bumuo ng hugis 'A'.",
                "Itali nang mahigpit ang mga dulo gamit ang alambre o matibay na lubid, at maglagay ng pahalang na kawayan (ridge pole) sa pinakatuktok.",
                "Magkabit ng plastic twine o lambat (trellis netting) sa pagitan ng mga tulos upang madaling kapitan ng mga tendrils ng sitaw.",
                "Sanayin ang mga bagong baging na umikot nang pakanan sa tulos sa unang 2 linggo."
            ),
            tips = listOf(
                "Siguraduhing malalim ang baon ng tulos (30 cm) upang hindi itumba ng malalakas na hanging habagat."
            )
        ),
        HowToSectionItem(
            title = "Pangungunang Pruning sa Ibabang 30 cm ng Baging",
            summary = "Pag-aalis ng mga suwi sa ibaba para sa mas malinis at masaganang pamumunga.",
            steps = listOf(
                "Kapag umabot na sa 1 metro ang taas ng baging, suriin ang pinakailalim na 30 cm mula sa lupa.",
                "Gupitin ang lahat ng maliliit na lateral side shoots at mga lumang dahon na sumasayad sa lupa.",
                "Ang gawaing ito ay nagpapaganda ng sirkulasyon ng hangin sa ilalim ng balag at pumipigil sa pag-akyat ng fungal spores mula sa lupa.",
                "Pinupwersa rin nito ang halaman na ituon ang lakas sa itaas na bahagi kung saan mas marami ang bulaklak."
            )
        ),
        HowToSectionItem(
            title = "Wastong Paraan ng Pag-ani Tuwing Ikalawang Araw",
            summary = "Paraan ng pagpitas upang hindi masira ang flowering cushion.",
            steps = listOf(
                "Pitasin ang mga sitaw sa maagang umaga habang malutong at malamig pa ang hangin.",
                "Piliin ang mga bungang may haba nang 50–65 cm na malambot at hindi pa bumubukol ang mga buto.",
                "Hawakan ang base ng bunga at marahang baliin paitaas gamit ang hinlalaki at hintuturo.",
                "HUWAG hilahin nang marahas upang hindi mapigtas ang flower cluster (peduncle) na muling magbubunga sa susunod na linggo.",
                "Bungkusin sa timbang na 500 gramo hanggang 1 kilo gamit ang tali o hibla ng saging."
            )
        )
    ),
    seasonalCalendar = listOf(
        SeasonalCalendarMonth("Ene", "Mataas na Ani (Peak Dry Season)", "Magandang sikat ng araw; mababa ang banta ng anthracnose at rust", Color(0xFF2E7D32)),
        SeasonalCalendarMonth("Peb", "Mataas na Ani (Peak Season)", "Tuloy-tuloy na pamumunga tuwing 2 araw sa buong kapuluan", Color(0xFF2E7D32)),
        SeasonalCalendarMonth("Mar", "Mainam na Ani (Dry Season)", "Bantayan ang black aphids at thrips; magdilig tuwing 2 araw", Color(0xFF388E3C)),
        SeasonalCalendarMonth("Abr", "Tag-init (Mataas na Patubig)", "Kailangan ang regular na patubig sa pamumulaklak laban sa flower drop", Color(0xFFF57F17)),
        SeasonalCalendarMonth("Mayo", "Paghahanda sa Tag-ulan", "Pagtatanim sa matataas na kamada; patatagin ang mga kawayang tulos", Color(0xFFF57F17)),
        SeasonalCalendarMonth("Hun", "Tag-ulan (Rainy Crop)", "Bantayan ang bean rust; i-prune ang ilalim na dahon para sa hangin", Color(0xFFE65100)),
        SeasonalCalendarMonth("Hul", "Tag-ulan / Habagat", "Mag-spray ng bio-fungicide laban sa anthracnose sa mga bunga", Color(0xFFE65100)),
        SeasonalCalendarMonth("Ago", "Tag-ulan (Trellis Care)", "Tiyaking walang nakatayong tubig sa mga kanal ng kamada", Color(0xFFE65100)),
        SeasonalCalendarMonth("Set", "Paghahanda sa Bagong Siklo", "Paghahanda ng lupa at paglalagay ng organikong compost", Color(0xFF1565C0)),
        SeasonalCalendarMonth("Okt", "Simula ng Pangunahing Taniman", "Paghahasik matapos ang habagat; napakabilis sumibol sa mamasa-masang lupa", Color(0xFF2E7D32)),
        SeasonalCalendarMonth("Nob", "Aktibong Pagbalag at Pamumulaklak", "Mabilisang paggapang sa balag at unang paglabas ng mga bulaklak", Color(0xFF2E7D32)),
        SeasonalCalendarMonth("Dis", "Mataas na Ani (Holiday Demand)", "Mataas ang presyo sa palengke para sa pansit at handaan", Color(0xFF2E7D32))
    ),
    locationData = LocationDifficultyData(
        difficulty = "Napakadali (Beginner & Commercial Friendly)",
        hardinessZone = "Lowland & Intermediate Philippines (0–800 MASL)",
        tempRange = "25°C–35°C (Mainit at Tropikal)",
        sunlight = "Buong Araw (Full Sun, 6–8 Oras)",
        suitableAlso = "Raised beds / Trellis plots / Backyard Balag / Large Pots (20L+)"
    ),
    soilPrep = SoilPrepData(
        phRange = "5.5–6.8",
        soilTypes = "Buhaghag na Sandy Loam o Clay Loam na may Maayos na Drainage",
        recommendation = "Bungkalin ang kamada sa lalim na 25 cm. Ihalo ang decomposed compost o vermicompost at Carbonized Rice Hull (CRH). Hindi nangangailangan ng labis na chemical nitrogen dahil sa natural na nitrogen fixation ng Rhizobium nodules sa ugat. Maglagay ng Phosphorus at Potassium (0-20-20 o 14-14-14 basal) para sa malalakas na bulaklak at malulutong na bunga."
    ),
    growthTimeline = listOf(
        TimelineStageItem(
            stageName = "1. Pagsibol mula sa Buto (Germination & Emergence)",
            durationDays = "Araw 3–6",
            description = "Mabilis na pagsibol ng matabang cotyledons at paglabas ng unang pares ng berdeng dahon sa mamasa-masang lupa."
        ),
        TimelineStageItem(
            stageName = "2. Paggapang sa Tulos (Vining & Vegetative Climbing)",
            durationDays = "Araw 7–25",
            description = "Paglabas ng mga tendril; pag-ikot ng baging sa kawayang tulos patungong 1–2 metro taas. Unang side-pruning sa ibabang 30 cm."
        ),
        TimelineStageItem(
            stageName = "3. Pamumulaklak at Pod Initiation (Flowering Stage)",
            durationDays = "Araw 30–45",
            description = "Paglabas ng mapupusyaw na lila o ubeng bulaklak sa mga racemes; aktibong polinisasyon ng mga bubuyog at pamumuo ng maliliit na sitaw."
        ),
        TimelineStageItem(
            stageName = "4. Unang Pitas ng Bunga (First Harvest Flush)",
            durationDays = "Araw 48–55",
            description = "Pag-abot ng mga bunga sa 50–65 cm haba; unang pitas ng malulutong at makakapal na sitaw."
        ),
        TimelineStageItem(
            stageName = "5. Tuloy-tuloy na Anihan (Peak Continuous Harvest)",
            durationDays = "Araw 55–85+",
            description = "Pang-araw-araw o bawat 2 araw na pamimitas; patuloy na namumulaklak at nagbubunga sa loob ng 6 hanggang 8 linggo."
        )
    ),
    howTos = listOf(
        HowToSectionItem(
            title = "Paano Maghasik ng Buto ng Sitaw nang Direkta sa Kamada",
            summary = "Mabisang paraan ng direct seeding para sa 95%+ germination rate.",
            steps = listOf(
                "Ibabad ang mga buto ng sitaw sa malinis na tubig sa loob ng 2 hanggang 4 na oras bago itanim upang mapalambot ang seed coat.",
                "Gumawa ng mga butas na may lalim na 2–3 cm sa tabi ng bawat kawayang tulos, may agwat na 20–25 cm.",
                "Maglagay ng 2 hanggang 3 buto bawat butas at marahang tabunan ng pinong compost o buhaghag na lupa.",
                "Diligan nang banayad. Sa loob ng 3 hanggang 5 araw, sabay-sabay na susulpot ang malulusog na punla."
            )
        ),
        HowToSectionItem(
            title = "Hakbang-Hakbang na Pagkontrol sa Bean Pod Borer (Maruca)",
            summary = "Organikong paraan upang walang uod sa loob ng mga sitaw.",
            steps = listOf(
                "Suriin ang mga buko ng bulaklak tuwing ika-35 araw mula paghasik.",
                "Mag-spray ng Bacillus thuringiensis (Bt) sa daming 1–2 gramo bawat litro sa dakong hapon tuwing 5 araw habang namumulaklak.",
                "Maglagay ng solar light traps sa gilid ng taniman upang maakit at mahuli ang mga gamu-gamo bago mangitlog.",
                "Agad na pitasin at sunugin ang anumang bulaklak o bunga na may nakikitang butas at itim na dumi ng uod."
            )
        ),
        HowToSectionItem(
            title = "Pangangalaga sa Flower Cushions para sa Tuloy-tuloy na Ani",
            summary = "Paraan upang mamunga ang sitaw nang mahigit 2 buwan.",
            steps = listOf(
                "Huwag kailanman gumamit ng kutsilyo o marahas na paghila kapag namimitas ng sitaw.",
                "Baliin lamang ang bunga sa mismong base ng pod gamit ang mga daliri, iniiwan ang pinagkabitang bukol (peduncle).",
                "Ang bukol na ito ay naglalaman ng mga natutulog na flower buds na muling maglalabas ng bagong bulaklak at bunga kinabukasan.",
                "Maglagay ng compost tea o seaweed extract tuwing 10 araw upang mapanatili ang sigla ng pamumulaklak."
            )
        )
    ),
    faqs = listOf(
        FaqItem(
            question = "Bakit nalalagas ang mga bulaklak ng aking sitaw at hindi nagiging bunga?",
            answer = "Ang paglalagas ng bulaklak (flower drop o blossom drop) ay karaniwang sanhi ng tatlong bagay: (1) matinding tagtuyot o kakulangan sa tubig sa yugto ng pamumulaklak, (2) labis na chemical nitrogen fertilizer na nagpapadami ng dahon ngunit nagpapatapon ng bulaklak, o (3) pinsala mula sa Bean Pod Borer (Maruca) o thrips na kumakain sa loob ng buko ng bulaklak. Panatilihing mamasa-masa ang kamada, bawasan ang nitrogen, at mag-spray ng Bt laban sa uod."
        ),
        FaqItem(
            question = "Kailangan ba talagang lagyan ng balag o tulos ang sitaw?",
            answer = "Oo, napakahalaga! Ang sitaw (pole variety) ay gumagapang na baging na umaabot sa 2 hanggang 3 metro ang haba. Kung walang balag at hahayaang gumapang sa lupa: (1) masisira at mabubulok ang mga bunga sa putik tuwing umuulan, (2) magiging baluktot at madilaw ang mga sitaw, (3) mabilis dadapuan ng fungal rust at anthracnose ang mga dahon, at (4) napakahirap mamitas. Ang A-frame bamboo trellis ay nagbibigay ng 3 hanggang 4 na beses na mas mataas na ani."
        ),
        FaqItem(
            question = "Bakit nagkakaroon ng makakapal na itim na kuto sa dulo ng aking mga sitaw?",
            answer = "Ito ang Black Cowpea Aphids (Aphis craccivora). Gustung-gusto nilang sipsipin ang matamis na katas ng mga murang usbong at bulaklak ng sitaw. Bukod sa pagpapatuyo sa sanga, nagkakalat sila ng Bean Common Mosaic Virus. Sugpuin agad sa pamamagitan ng pag-spray ng Neem oil solution (5 ml bawat litro ng tubig na may kaunting sabon) tuwing 4–5 araw sa hapon, o mag-spray ng matapang na sabon at siling labuyo extract."
        ),
        FaqItem(
            question = "Bakit nagiging makunat at parang espongha (spongy) ang aking naaning sitaw?",
            answer = "Ito ay palatandaan ng 'over-mature' o naantalang pag-ani. Ang sitaw ay kailangang pitasin habang bata, malutong, makinis, at hindi pa bumubukol ang mga buto sa loob. Kapag lumipas ang tamang araw, tumitigas ang balat, lumalaki ang buto, at nagiging guwang ang laman. Laging mamitas tuwing ikalawang araw (every 2 days) nang walang palya."
        ),
        FaqItem(
            question = "Bakit sinasabing nagpapataba ng lupa ang sitaw?",
            answer = "Dahil ang sitaw ay kabilang sa pamilya ng Legumes (Fabaceae). Sa mga ugat nito ay naninirahan ang mga microscopic bacteria na tinatawag na 'Rhizobium'. Ang mga bakteryang ito ay may pambihirang kakayahan na kumuha ng nitrogen gas mula sa hangin at gawin itong natural na pataba (ammonium) para sa halaman at sa lupa. Matapos anihin ang sitaw, ang nabubulok na mga ugat nito ay nag-iiwan ng masaganang nitroheno para sa susunod na itatanim na gulay."
        )
    ),
    nutritionTable = NutritionTableData(
        servingSize = "100 g (Sariwang Hilaw na Sitaw)",
        calories = "47 kcal",
        macroNutrients = listOf(
            NutritionRowData("Protina (Protein)", "2.80 g", "5.6%"),
            NutritionRowData("Kabuuang Taba (Total Fat)", "0.40 g", "0.5%"),
            NutritionRowData("Carbohydrates", "8.35 g", "6.4%"),
            NutritionRowData("Dietary Fiber (Hibla)", "3.6 g", "14.4%"),
            NutritionRowData("Asukal (Natural Sugars)", "1.80 g", "—"),
            NutritionRowData("Tubig (Water Content)", "88.1 g", "—")
        ),
        microNutrients = listOf(
            NutritionRowData("Folate (Bitamina B9)", "62 mcg", "16%"),
            NutritionRowData("Bitamina C (Ascorbic Acid)", "18.8 mg", "21%"),
            NutritionRowData("Bitamina A (RAE)", "43 mcg", "5%"),
            NutritionRowData("Calcium (Kalsyo)", "50 mg", "5%"),
            NutritionRowData("Iron (Bakal)", "1.00 mg", "6%"),
            NutritionRowData("Magnesium (Mg)", "44 mg", "11%"),
            NutritionRowData("Phosphorus (P)", "59 mg", "6%"),
            NutritionRowData("Potassium (K)", "240 mg", "5%"),
            NutritionRowData("Manganese (Mn)", "0.22 mg", "10%")
        )
    ),
    exploreArticles = listOf(
        ArticleItem("Gabay sa Komersyal na Produksyon ng Sitaw sa Timog Luzon", "Mga agronomic protocol sa Batangas at Laguna para sa 15–20 tonelada bawat ektarya gamit ang Sandigan F1.", "7 min basahin"),
        ArticleItem("Sining ng Pagtatayo ng Matibay na Balag at Tulos", "Hakbang-hakbang na gabay sa A-frame bamboo trellising at pamamahala sa baging.", "5 min basahin"),
        ArticleItem("Organikong Pagsugpo sa Maruca Pod Borer at Itim na Kuto", "Likas na pamamaraan gamit ang Bt, neem oil, at light traps para sa malinis at walang uod na sitaw.", "6 min basahin")
    )
)
