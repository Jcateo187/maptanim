package com.maptanim.app.features.farm.dialogs.vegetables_information

import androidx.compose.ui.graphics.Color
import com.maptanim.app.R

val cabbageData = CropEncyclopedia(
    cropName = "Cabbage",
    localName = "Repolyo (Brassica oleracea var. capitata)",
    scientificName = "Brassica oleracea var. capitata",
    category = "Leafy Greens (Brassicaceae)",
    family = "Brassicaceae",
    overview = "Ang repolyo (Brassica oleracea var. capitata) ay isa sa pinakamahahalagang commercial high-value temperate crops sa Pilipinas, pangunahing itinatanim sa malamig na kabundukan ng Cordillera Administrative Region (Benguet: La Trinidad, Atok, Buguias, Mankayan; Mountain Province) at sa talampas ng Bukidnon sa Mindanao. Kilala bilang biennial plant na pinalalaki bilang annual para sa siksik at sapin-saping bilog na ulo ng mga malulutong na dahon.\n\nNangangailangan ang repolyo ng malamig na klima (15–20°C optimum) upang maayos na bumilog at sumiksik ang ulo; sa mainit na kapatagan, madalas itong mag-loose head o mag-bolt (mamulaklak nang maaga) maliban na lamang kung gagamit ng mga heat-tolerant tropical hybrid tulad ng KK Cross at Rare Ball sa panahon ng amihan (Nobyembre hanggang Pebrero).\n\nAng produksyon ng repolyo ay may dalawang pangunahing hamon sa bansa: ang salot na Diamondback Moth (Plutella xylostella o DBM) na mabilis magkaroon ng resistensya sa kemikal, at ang mapaminsalang sakit sa asidikong lupa na Clubroot (Plasmodiophora brassicae) na lumilikha ng mga bukol sa ugat. Sa pamamagitan ng paglalagay ng agricultural lime, maayos na raised bed drainage, at biological pest management gamit ang Bt at parasitic wasps, nakakamit ng mga magsasaka ang ani na 25 hanggang 40 tonelada bawat ektarya.",
    selectedVariety = VarietyInfo(
        name = "KK Cross F1",
        producer = "Takii Seed / East-West Seed Philippines",
        type = "Tropical Heat-Tolerant Hybrid",
        badge = "F1",
        description = "Ang pinakatanyag at pinagkakatiwalaang hybrid na repolyo sa Pilipinas. May pambihirang tibay sa init kumpara sa tradisyunal na highland varieties, kaya't matagumpay na naitatanim sa mid-elevation at malamig na buwan sa kapatagan. Nagbubunga ng siksik, bahagyang sapad na ulo (1.5–2.5 kg) na may makikinis na panlabas na wrapper leaves, matamis ang lasa, at matibay sa biyahe."
    ),
    otherVarieties = listOf(
        "KK Cross F1 (Takii / Commercial Standard)",
        "Scorpio F1 (East-West Seed - Tip-Burn Tolerant)",
        "Rare Ball F1 (Highland Early Maturing)",
        "Green Coronet (Heavy Highland Head)",
        "Rubi Ball F1 (Red/Purple Cabbage)",
        "Gloria F1 (High-Yield Highland Hybrid)"
    ),
    carouselSlides = listOf(
        CarouselSlide(
            title = "Siksik na Ulo ng Repolyo sa Benguet",
            subtitle = "Malamig na klima at matabang volcanic loam sa Atok at La Trinidad",
            stage = "Matipid sa Ani (Market Ready)",
            badgeColor = Color(0xFF2E7D32),
            drawableResId = R.drawable.ph_cabbage_hero
        ),
        CarouselSlide(
            title = "Malulusog na Punla sa Nursery Tray",
            subtitle = "25–30 araw na punla na may 4–5 true leaves sa nursery greenhouse",
            stage = "Yugto ng Punlaan (Nursery Phase)",
            badgeColor = Color(0xFF1565C0),
            drawableResId = R.drawable.ph_cabbage_seedlings
        ),
        CarouselSlide(
            title = "Pagbuo ng Ulo sa Raised Beds",
            subtitle = "Pag-ikot at pagsisiksik ng mga gitnang dahon sa ilalim ng silver-black mulch",
            stage = "Head Formation (Pag-uulo)",
            badgeColor = Color(0xFFE65100),
            drawableResId = R.drawable.ph_cabbage_heads
        ),
        CarouselSlide(
            title = "Masaganang Pag-aani sa Kabundukan",
            subtitle = "Maingat na pagputol sa base gamit ang kutsilyo na may 2–3 protective wrapper leaves",
            stage = "Pang-umagang Pag-aani (Harvest)",
            badgeColor = Color(0xFF6A1B9A),
            drawableResId = R.drawable.ph_cabbage_harvest
        )
    ),
    quickInfo = listOf(
        QuickInfoItem(
            key = "lifespan",
            label = "Buhay ng Halaman",
            value = "70–100 Araw",
            explanation = ExplanationData(
                title = "Buhay ng Halaman (Crop Lifecycle)",
                summary = "Ang repolyo ay biennial botanically ngunit inaani bilang annual head sa loob ng 70–100 araw mula transplanting.",
                details = listOf(
                    "Punlaan (Nursery): 25–35 araw sa seedling tray hanggang magkaroon ng 4–5 true leaves.",
                    "Vegetative Rosette: 2–4 na linggo pagkatanim para sa pagpapalapad ng mga panlabas na dahon.",
                    "Head Formation: 4–6 na linggo para sa pagsisiksik ng panloob na mga dahon patungong 1–3 kg bawat ulo."
                )
            )
        ),
        QuickInfoItem(
            key = "harvest_time",
            label = "Unang Ani",
            value = "70–90 DAT",
            explanation = ExplanationData(
                title = "Panahon ng Unang Ani",
                summary = "70 hanggang 90 araw pagkatapos ilipat ang punla (Days After Transplanting).",
                details = listOf(
                    "Handa na kapag matigas at siksik kapag pinisil ng kamay.",
                    "Single harvest per plant: isang beses lamang pinuputol ang bawat puno.",
                    "Inaani sa umaga upang mapanatili ang kasariwaan at lutong ng mga dahon."
                )
            )
        ),
        QuickInfoItem(
            key = "spacing",
            label = "Agwat ng Tanim",
            value = "40–50 × 40–50 cm",
            explanation = ExplanationData(
                title = "Tamang Agwat ng Pagtatanim",
                summary = "40–50 cm pagitan ng mga halaman sa 1-metrong lapad na raised bed.",
                details = listOf(
                    "Nagbibigay ng 40,000 hanggang 50,000 puno bawat ektarya.",
                    "Ang sapat na agwat ay nagpapaganda ng sirkulasyon ng hangin at nagpapababa ng banta ng Bacterial Soft Rot at Black Rot.",
                    "Sa mas masinsing agwat (35 cm), mas maliliit ngunit mas maraming ulo ang naaani."
                )
            )
        ),
        QuickInfoItem(
            key = "sowing_depth",
            label = "Lalim ng Binhi",
            value = "0.5–1.0 cm",
            explanation = ExplanationData(
                title = "Lalim ng Pagpupunla",
                summary = "Mababaw lamang na 0.5 hanggang 1.0 sentimetro sa seedling tray.",
                details = listOf(
                    "Gumamit ng sterilized seedling medium (1 part CRH, 1 part vermicast, 1 part garden soil).",
                    "Sumisibol sa loob ng 4–7 araw sa katamtamang kahalumigmig.",
                    "I-transplant sa parehong antas ng seedling plug depth sa kamada."
                )
            )
        ),
        QuickInfoItem(
            key = "water_needs",
            label = "Pangangailangan sa Tubig",
            value = "Mataas at Regular (2–3x/wk)",
            explanation = ExplanationData(
                title = "Patubig at Kahalumigmig",
                summary = "Mataas at tuloy-tuloy na supply ng tubig lalo na sa yugto ng pagbuo ng ulo.",
                details = listOf(
                    "25–40 mm bawat linggo sa pamamagitan ng furrow o drip irrigation.",
                    "Kritikal: Iwasan ang drought stress na nagdudulot ng tip-burn at mapapait na dahon.",
                    "Huwag mag-overwater o magbaha upang hindi kapitan ng Bacterial Soft Rot."
                )
            )
        ),
        QuickInfoItem(
            key = "sun_needs",
            label = "Sikat ng Araw",
            value = "Buong Araw (Full Sun 6h+)",
            explanation = ExplanationData(
                title = "Pangangailangan sa Araw",
                summary = "Nangangailangan ng hindi bababa sa 6 na oras ng direktang sikat ng araw bawat araw.",
                details = listOf(
                    "Kailangan ang mataas na light intensity kasama ang malamig na temperatura para sa siksik na photosynthesis.",
                    "Sa malilim na lugar, nagiging maluwag at matamlay ang pamumuo ng ulo ng repolyo."
                )
            )
        ),
        QuickInfoItem(
            key = "soil_ph",
            label = "Asim ng Lupa (pH)",
            value = "pH 6.0–6.8 (Iwasan <5.5)",
            infoNote = "pH < 5.5 nagdudulot ng Clubroot",
            isAlert = true,
            explanation = ExplanationData(
                title = "pH ng Lupa at Pamamahala sa Apog (Lime)",
                summary = "Napakaselan ng repolyo sa asidikong lupa dahil sa sakit na Clubroot.",
                details = listOf(
                    "Optimum pH: 6.0 hanggang 6.8.",
                    "Kapag ang pH ay mas mababa sa 5.5, mabilis kumalat ang fungal spores ng Clubroot (Plasmodiophora brassicae) na lumilikha ng malalaking bukol sa ugat.",
                    "Maglagay ng Agricultural Lime (Apog / Calcitic Lime, 2–4 tonelada bawat ektarya) 3–4 na linggo bago maglipat-tanim upang itaas ang pH sa ligtas na antas."
                )
            )
        ),
        QuickInfoItem(
            key = "ideal_temp",
            label = "Tamang Temperatura",
            value = "15–20°C (Optimum Highland)",
            explanation = ExplanationData(
                title = "Temperatura at Agro-Climatic Zone",
                summary = "Malamig na klima ang pinakamainam para sa compact heading.",
                details = listOf(
                    "Optimum: 15–20°C; minimum: 10°C; maximum: 24°C.",
                    "Kapag higit 25°C sa kapatagan, nagkakaroon ng maluluwag na dahon, tip-burn, o maagang pamumulaklak (bolting).",
                    "Sa kapatagan, itanim lamang sa cool dry months (Nobyembre hanggang Pebrero) gamit ang KK Cross F1."
                )
            )
        ),
        QuickInfoItem(
            key = "difficulty",
            label = "Antas ng Alaga",
            value = "Katamtaman (Moderate)",
            explanation = ExplanationData(
                title = "Antas ng Kahirapan sa Pagtatanim",
                summary = "Nangangailangan ng maingat na pagbabantay sa peste (DBM) at pamamahala sa pH ng lupa.",
                details = listOf(
                    "Madaling patubuin ang punla ngunit nangangailangan ng disiplina sa pagdidilig at regular na scouting laban sa mga uod.",
                    "Napakalaki ng kita sa high-yield commercial production sa Cordillera at Bukidnon."
                )
            )
        ),
        QuickInfoItem(
            key = "feeding",
            label = "Pangangailangan sa Abono",
            value = "Mataas (Heavy Feeder)",
            explanation = ExplanationData(
                title = "Programa sa Pagpapataba",
                summary = "Malakas kumain ng Nitrogen sa rosette stage at Potassium sa head-filling stage.",
                details = listOf(
                    "Basal: 400–600 kg/ha ng 14-14-14 + saganang organikong compost/manure.",
                    "Side-dress 1 (20 DAT): Urea (46-0-0) para sa malalapad at luntiang dahon.",
                    "Side-dress 2 (40 DAT): Muriate of Potash (0-0-60) para sa matitigas at siksik na ulo.",
                    "Foliar Boron at Calcium laban sa hollow stem at tip-burn."
                )
            )
        ),
        QuickInfoItem(
            key = "trellising",
            label = "Tulos / Balag",
            value = "Hindi Kailangan (Self-Standing)",
            explanation = ExplanationData(
                title = "Suporta sa Halaman",
                summary = "Ang repolyo ay self-supporting compact rosette crop.",
                details = listOf(
                    "Hindi nangangailangan ng tulos o balag.",
                    "Isinasagawa ang magaan na 'hilling-up' (pagtatambak ng lupa sa base) sa ika-3–4 na linggo upang patatagin ang tangkay laban sa malalakas na hangin sa kabundukan."
                )
            )
        ),
        QuickInfoItem(
            key = "pest_pressure",
            label = "Banta ng Peste",
            value = "Mataas (#1 Diamondback Moth)",
            isAlert = true,
            explanation = ExplanationData(
                title = "Presyur ng Peste sa Brassicas",
                summary = "Mataas ang presyur ng Diamondback Moth (DBM) at Cabbage Looper.",
                details = listOf(
                    "Ang DBM (Plutella xylostella) ay may kakayahang sumira ng 60–100% ng ani kung hindi mapapamahalaan.",
                    "Nangangailangan ng Integrated Pest Management (IPM): paggamit ng Bt biopesticide, parasitic wasps, at trap crops (mustasa) sa halip na iisang kemikal lamang."
                )
            )
        )
    ),
    companionPlants = listOf(
        PlantRelationItem(
            name = "Sibuyas (Allium cepa)",
            scientificName = "Allium cepa",
            isBeneficial = true,
            role = "Natural Insect Repellent",
            explanation = ExplanationData(
                title = "Sibuyas bilang Companion ng Repolyo",
                summary = "Ang matapang na amoy ng allicin sa sibuyas ay lumilito at nagtataboy sa Diamondback Moth at Cabbage Worms.",
                details = listOf(
                    "Itanim bilang border crop sa gilid ng mga kamada ng repolyo.",
                    "Hindi nakikipag-agawan sa espasyo dahil patayo ang tubo ng dahon ng sibuyas."
                )
            )
        ),
        PlantRelationItem(
            name = "Mustasa (Brassica juncea)",
            scientificName = "Brassica juncea",
            isBeneficial = true,
            role = "Trap Crop para sa Diamondback Moth",
            explanation = ExplanationData(
                title = "Mustasa bilang Trap Crop",
                summary = "Mas pinipili ng Diamondback Moth na mangitlog sa mustasa kaysa sa repolyo.",
                details = listOf(
                    "Magtanim ng 1–2 hanay ng mustasa sa paligid ng taniman ng repolyo 10–14 araw bago maglipat-tanim.",
                    "I-spray o lipulin ang mga uod sa mustasa upang hindi na makalipat sa repolyo."
                )
            )
        ),
        PlantRelationItem(
            name = "Aromatic Herbs (Dill, Thyme, Mint)",
            scientificName = "Anethum graveolens / Thymus vulgaris",
            isBeneficial = true,
            role = "Pang-akit sa Parasitic Wasps",
            explanation = ExplanationData(
                title = "Aromatic Herbs para sa Natural Predators",
                summary = "Umaakit sa Diadegma semiclausum at hoverflies na natural na kumakain sa mga uod ng repolyo.",
                details = listOf(
                    "Ang maliliit na bulaklak ng dill at thyme ay nagbibigay ng nectar sa mga kapaki-pakinabang na wasps.",
                    "Nagtataboy din sa cabbage aphids."
                )
            )
        ),
        PlantRelationItem(
            name = "Iba pang Brassicas (Pechay, Broccoli, Cauliflower)",
            scientificName = "Brassica spp.",
            isBeneficial = false,
            role = "Pinagsasaluhang Peste at Clubroot Spores",
            explanation = ExplanationData(
                title = "Bakit Dapat Iwasan ang Katabing Brassicas",
                summary = "Ang magkakatabing brassicas ay nagdudulot ng mabilisang pagdami ng DBM at pagkalat ng Clubroot.",
                details = listOf(
                    "Huwag itanim nang magkasunod sa iisang kamada (maghintay ng 2–3 taon).",
                    "Mabilis magkahawaan ng Black Rot at Bacterial Soft Rot."
                )
            )
        ),
        PlantRelationItem(
            name = "Strawberry (Fragaria × ananassa)",
            scientificName = "Fragaria × ananassa",
            isBeneficial = false,
            role = "Magkatunggali sa Nutrisyon",
            explanation = ExplanationData(
                title = "Strawberry at Repolyo",
                summary = "Karaniwang itinatanim pareho sa Benguet ngunit hindi dapat pagtabihin sa iisang kamada.",
                details = listOf(
                    "Ang repolyo ay heavy feeder na umuubos sa nutrients na kailangan ng mababaw na ugat ng strawberry.",
                    "Maaari ring makaakit ng parehong slugs at snails."
                )
            )
        )
    ),
    nutritionBadges = listOf(
        NutritionBadgeItem(
            code = "Vit C",
            name = "Bitamina C (Ascorbic Acid)",
            percentageDaily = "44% DV",
            role = "Immune Defense & Collagen",
            explanation = ExplanationData(
                title = "Mataas na Bitamina C sa Sariwang Repolyo",
                summary = "Naglalaman ng 36.6 mg ng Bitamina C bawat 100 gramo, mas mataas kaysa sa maraming prutas.",
                details = listOf(
                    "Nagpapalakas ng resistensya laban sa sipon at impeksyon.",
                    "Makapangyarihang antioxidant na nagpoprotekta sa mga selula laban sa cellular damage.",
                    "Pinapanatili ang collagen para sa makinis na balat."
                )
            )
        ),
        NutritionBadgeItem(
            code = "Vit K1",
            name = "Bitamina K1 (Phylloquinone)",
            percentageDaily = "63% DV",
            role = "Bone Health & Blood Clotting",
            explanation = ExplanationData(
                title = "Sagana sa Bitamina K1",
                summary = "Isa sa pinakamayamang gulay sa Vitamin K1 (76 mcg bawat 100g).",
                details = listOf(
                    "Mahalaga para sa normal na pamumuo ng dugo (blood coagulation).",
                    "Tumutulong sa osteocalcin para sa matitibay na buto at pag-iwas sa osteoporosis."
                )
            )
        ),
        NutritionBadgeItem(
            code = "Fiber",
            name = "Dietary Fiber (Hibla)",
            percentageDaily = "10% DV",
            role = "Gut Health & Digestion",
            explanation = ExplanationData(
                title = "Mayaman sa Hibla para sa Tiyan",
                summary = "Naglalaman ng parehong soluble at insoluble fiber para sa malusog na digestive tract.",
                details = listOf(
                    "Nagsisilbing pagkain (prebiotic) para sa kapaki-pakinabang na bacteria sa tiyan.",
                    "Tumutulong sa pagpapababa ng blood cholesterol at regular na pagdumi."
                )
            )
        ),
        NutritionBadgeItem(
            code = "Sulforaphane",
            name = "Glucosinolates & Sulforaphane",
            percentageDaily = "Antioxidant",
            role = "Anti-Cancer & Detoxification",
            explanation = ExplanationData(
                title = "Sulforaphane at Glucosinolates",
                summary = "Natatanging phytonutrients ng pamilyang Brassica na may napatunayang anti-cancer properties.",
                details = listOf(
                    "Pinasisigla ang mga natural detoxifying enzymes ng atay.",
                    "Tumutulong sa paglaban sa pamamaga at pagkasira ng mga daluyan ng dugo."
                )
            )
        )
    ),
    pestsAndDiseases = listOf(
        PestDiseaseItem(
            name = "Diamondback Moth (DBM)",
            scientificName = "Plutella xylostella",
            severity = "Kritikal (Critical Hazard)",
            symptoms = "Maliliit na berdeng uod na pumapalag; window-paning (maninipis na parang salaming butas) sa dahon; butas-butas na rosette.",
            prevention = "Crop rotation palayo sa brassicas; pagtatanim ng mustard trap crop sa paligid; fine netting sa punlaan.",
            treatment = "I-isprey ang Bacillus thuringiensis (Bt) sa takipsilim; magsalit-salit ng magkakaibang insecticide class (hal. Spinetoram / Emamectin) upang hindi magkaroon ng resistensya.",
            explanation = ExplanationData(
                title = "Diamondback Moth (Plutella xylostella)",
                summary = "Ang #1 peste ng repolyo sa buong Pilipinas.",
                details = listOf(
                    "Ang uod ay 8–10 mm lamang ang haba, kulay berde, at mabilis pumalag o magbitin sa sutla kapag nagalaw.",
                    "Mabilis maging immune sa mga karaniwang kemikal kapag ginagamit linggu-linggo.",
                    "Laging gamitin ang Bt biopesticide at pangalagaan ang parasitic wasp na Diadegma semiclausum."
                )
            )
        ),
        PestDiseaseItem(
            name = "Clubroot (Bukol sa Ugat)",
            scientificName = "Plasmodiophora brassicae",
            severity = "Kritikal (Devastating Soil Disease)",
            symptoms = "Pananamlay at pagkalanta ng repolyo sa katanghalian; pagkabansot; malalaking distorted club-shaped galls sa mga ugat.",
            prevention = "Paglalagay ng Agricultural Lime upang itaas ang soil pH sa 7.0–7.2; 3–4 na taong crop rotation; paggamit ng malinis na punla.",
            treatment = "Walang kemikal na pamatay-amag sa may sakit nang tanim; bunutin at sunugin agad ang buong halaman kasama ang ugat.",
            explanation = ExplanationData(
                title = "Clubroot sa Asidikong Lupa ng Cordillera",
                summary = "Soil-borne fungal pathogen na nabubuhay sa asidikong lupa nang hanggang 10 taon.",
                details = listOf(
                    "Hinaharang ng mga bukol sa ugat ang transportasyon ng tubig at sustansya.",
                    "Laging suriin ang pH ng lupa bago magtanim; kapag mababa sa 5.5, maglagay agad ng apog (lime)."
                )
            )
        ),
        PestDiseaseItem(
            name = "Bacterial Soft Rot",
            scientificName = "Pectobacterium carotovorum (Erwinia)",
            severity = "Kritikal (Total Head Loss)",
            symptoms = "Basang-basa, malambot, malugaw, at napakabahong ulo ng repolyo; mabilis kumalat sa maulang panahon.",
            prevention = "20–25 cm mataas na raised bed; maiwasan ang mga sugat sa pag-aani at pagbubungkal; huwag mag-overhead watering.",
            treatment = "Alisin agad ang bulok na halaman; mag-isprey ng Copper Hydroxide bilang proteksyon sa mga katabing puno.",
            explanation = ExplanationData(
                title = "Bacterial Soft Rot sa Repolyo",
                summary = "Mabilis kumalat na bacterial infection na sumisira sa buong ani sa bukid at biyahe.",
                details = listOf(
                    "Pumapasok sa mga sugat na dulot ng uod o matalim na kagamitan.",
                    "I-disimpekta ang mga kutsilyo sa pag-aani at panatilihing malamig at tuyo ang transportasyon."
                )
            )
        ),
        PestDiseaseItem(
            name = "Black Rot",
            scientificName = "Xanthomonas campestris pv. campestris",
            severity = "Mataas (High)",
            symptoms = "Hugis-V na paninilaw mula sa gilid ng dahon papasok; maiitim na ugat (blackened veins) sa loob ng tangkay.",
            prevention = "Certified disease-free seeds; hot-water seed treatment (50°C sa loob ng 25 min); 2–3 taong crop rotation.",
            treatment = "Preventive copper-based bactericide spray tuwing tag-ulan; alisin ang mga unang apektadong dahon.",
            explanation = ExplanationData(
                title = "Black Rot (Xanthomonas)",
                summary = "Bacterial vascular disease na kumakalat sa tilamsik ng ulan at kontaminadong binhi.",
                details = listOf(
                    "Pumapasok sa hydathodes (mga butas sa gilid ng dahon) tuwing umaga kapag may hamog.",
                    "Nagdudulot ng pagkabulok sa imbakan kung hindi maaagapan."
                )
            )
        ),
        PestDiseaseItem(
            name = "Cabbage Looper at Cabbage Worm",
            scientificName = "Trichoplusia ni / Pieris rapae",
            severity = "Mataas (High)",
            symptoms = "Malalaking gutay-gutay na ngatngat sa mga dahon; maiitim na butil ng dumi ng uod sa gitna ng rosette.",
            prevention = "Field sanitation; paggamit ng nylon insect netting sa nursery; hand-picking ng larvae.",
            treatment = "Bacillus thuringiensis (Bt) spray; chili-garlic-neem organic solution.",
            explanation = ExplanationData(
                title = "Cabbage Loopers at Green Worms",
                summary = "Malalaking uod na mabilis ubusin ang buong dahon ng repolyo bago sumara ang ulo.",
                details = listOf(
                    "Ang looper ay kumikilos nang paarko (looping movement) habang gumagapang.",
                    "Mas epektibo ang Bt spray habang bata pa ang larvae."
                )
            )
        )
    ),
    goodAndBadCritters = listOf(
        CritterItem(
            name = "Parasitic Wasp (Diadegma semiclausum)",
            role = "Numero Unong Natural Predator ng DBM",
            attractsWith = "Mga bulaklak ng dill, coriander, at mustard",
            explanation = ExplanationData(
                title = "Diadegma semiclausum sa Kabundukan",
                summary = "Munting wasp na nangingitlog sa loob ng DBM caterpillars, na pumapatay sa 70–90% ng uod.",
                details = listOf(
                    "Natural na naninirahan sa malamig na klima ng Benguet at Mountain Province.",
                    "Nasisira kapag nag-iisprey ng malalakas na broad-spectrum synthetic chemical pesticides."
                )
            )
        ),
        CritterItem(
            name = "Ladybugs at Hoverflies",
            role = "Kumakain ng Cabbage Aphids",
            attractsWith = "Yellow flowering companion plants",
            explanation = ExplanationData(
                title = "Ladybugs at Hoverfly Larvae",
                summary = "Kumakain ng daan-daang aphids bawat araw, pinipigilan ang paninilaw ng mga dahon.",
                details = listOf(
                    "Pangalagaan ang kanilang larvae na mukhang maliliit na buwaya.",
                    "Nagbibigay ng tuloy-tuloy na natural pest suppression."
                )
            )
        ),
        CritterItem(
            name = "Diamondback Moth Adults",
            role = "Mapaminsalang Moths (Pest)",
            attractsWith = "Brassica volatile scents",
            explanation = ExplanationData(
                title = "Paru-paro ng DBM",
                summary = "Kulay abuhin na maliit na moth na may markang diyamante sa likod kapag nakatiklop ang pakpak.",
                details = listOf(
                    "Nangingitlog nang paisa-isa sa ilalim ng dahon sa gabi.",
                    "I-monitor gamit ang pheromone traps at yellow sticky cards."
                )
            )
        )
    ),
    seasonalCalendar = listOf(
        SeasonalCalendarMonth("Ene", "Mataas na Ani (Benguet)", "Malamig na simoy ng hangin; pinakamagandang kalidad ng siksik na ulo; anihan sa kapatagan.", Color(0xFF2E7D32)),
        SeasonalCalendarMonth("Peb", "Pangunahing Anihan", "Tugatog ng ani ng repolyo sa Cordillera; mataas ang supply sa La Trinidad Trading Post.", Color(0xFF2E7D32)),
        SeasonalCalendarMonth("Mar", "Pagtatanim sa Kabundukan", "Paghahanda ng punlaan para sa summer highland cropping na may patubig.", Color(0xFF1565C0)),
        SeasonalCalendarMonth("Abr", "Patubig sa Tag-init", "Kailangan ang tuloy-tuloy na patubig sa mga bukid sa Atok at Buguias upang maiwasan ang tip-burn.", Color(0xFFE65100)),
        SeasonalCalendarMonth("Mayo", "Pagsisimula ng Ulan", "Pag-aayos ng matataas na raised bed (25 cm) upang maihanda ang lupa sa tag-ulan.", Color(0xFF1565C0)),
        SeasonalCalendarMonth("Hun", "Tag-ulan sa Kabundukan", "Mataas ang presyo sa merkado; kailangan ang copper spray laban sa bacterial soft rot.", Color(0xFF2E7D32)),
        SeasonalCalendarMonth("Hul", "Panahon ng Habagat", "Pag-aalaga sa drainage canals; paggamit ng plastic rain shelters sa punlaan.", Color(0xFFE65100)),
        SeasonalCalendarMonth("Ago", "Off-Season High Price", "Pinakamataas na presyo ng repolyo sa Metro Manila dahil sa banta ng bagyo sa Cordillera.", Color(0xFF2E7D32)),
        SeasonalCalendarMonth("Set", "Paghahanda sa Punlaan", "Paghahasik ng binhi para sa pangunahing regular season.", Color(0xFF1565C0)),
        SeasonalCalendarMonth("Okt", "Malawakang Lipat-Tanim", "Malawakang paglilipat-tanim sa Benguet at simula ng pagtatanim sa malamig na kapatagan.", Color(0xFF2E7D32)),
        SeasonalCalendarMonth("Nob", "Simula ng Amihan", "Pagsisimula ng malamig na panahon; mabilisang pagbuo ng matitigas at siksik na ulo.", Color(0xFF2E7D32)),
        SeasonalCalendarMonth("Dis", "Tugatog ng Produksyon", "Saganang ani para sa Kapaskuhan; matamis at malutong ang mga dahon dulot ng lamig.", Color(0xFF2E7D32))
    ),
    careGuide = listOf(
        HowToSectionItem(
            title = "Wastong Patubig sa Yugto ng Pag-uulo",
            summary = "Panatilihing regular at pantay ang moisture upang hindi pumutok ang ulo ng repolyo.",
            steps = listOf(
                "Diligan ang taniman 2–3 beses bawat linggo tuwing maagang umaga (6:00 AM–8:00 AM).",
                "Sa pagsisimula ng head filling (40–60 DAT), taasan ang dami ng tubig hanggang 1.5 litro bawat puno.",
                "Huwag magdilig sa ibabaw ng ulo ng repolyo upang maiwasan ang Bacterial Soft Rot.",
                "Maglagay ng makapal na mulch upang maiwasan ang mabilis na pagkatuyo ng lupa."
            )
        ),
        HowToSectionItem(
            title = "Pamamahala sa Asim ng Lupa at Pag-aapog (Liming)",
            summary = "Paggamit ng agricultural lime upang maiwasan ang mapaminsalang Clubroot disease.",
            steps = listOf(
                "Kumuha ng soil sample at suriin ang pH bago mag-araro.",
                "Kung ang pH ay mas mababa sa 5.5, magsabog ng 2–3 tonelada bawat ektarya ng Agricultural Lime (CaCO3).",
                "Ihalo nang mabuti ang apog sa 15–20 cm lalim ng lupa 3–4 na linggo bago maglipat-tanim.",
                "Siguraduhing sapat ang moisture ng lupa upang mabilis na umepekto ang apog."
            )
        ),
        HowToSectionItem(
            title = "Balanseng Programa sa Pag-aabono",
            summary = "Paghahalo ng Complete, Urea, Muriate of Potash, at Micronutrients.",
            steps = listOf(
                "Basal: 10–15g ng 14-14-14 bawat butas + 1 dakot ng processed chicken manure o compost.",
                "Unang Side-dress (20 DAT): 5g Urea bawat puno sa paligid ng root zone bago mag-hilling up.",
                "Ikalawang Side-dress (40 DAT): 5g Muriate of Potash (0-0-60) para sa pagsiksik ng ulo.",
                "Foliar Spray: Mag-isprey ng CalPhos at Solubor (Boron) tuwing 10 araw hanggang sa sumara ang ulo."
            )
        ),
        HowToSectionItem(
            title = "Pag-aalis ng Damo at Hilling-Up",
            summary = "Pagtatambak ng lupa sa paligid ng puno bago tuluyang sumara ang canopy.",
            steps = listOf(
                "Alisin ang mga damo nang manu-mano sa unang 3–4 na linggo.",
                "Magsagawa ng magaan na pagtatambak ng buhaghag na lupa (hilling-up) sa base ng repolyo.",
                "Itigil ang pagbubungkal kapag nagsara na ang mga dahon upang hindi masugatan ang mabababaw na ugat."
            )
        )
    ),
    growthTimeline = listOf(
        TimelineStageItem(
            stageName = "1. Pagsibol (Sprout / Germination)",
            durationDays = "4–7 Araw",
            description = "Pagsibol ng munting binhi sa seedling tray; kailangan ang banayad na kahalumigmig at lilim sa unang 3 araw."
        ),
        TimelineStageItem(
            stageName = "2. Punla (Nursery Phase)",
            durationDays = "25–35 Araw",
            description = "Paglaki ng 4 hanggang 5 true leaves; pagpapatigas ng punla (hardening) sa pamamagitan ng pagbawas ng tubig at pagbibilad sa araw 5 araw bago i-transplant."
        ),
        TimelineStageItem(
            stageName = "3. Rosette / Vegetative Stage",
            durationDays = "2–4 Linggo (DAT)",
            description = "Mabilisang paglapad ng mga panlabas na dahon; yugto ng unang side-dress ng Urea at pagtatanggal ng damo bago sumara ang canopy."
        ),
        TimelineStageItem(
            stageName = "4. Head Formation (Pag-uulo)",
            durationDays = "4–6 Linggo",
            description = "Pag-ikot at pagsisiksik ng mga gitnang dahon patungong matigas na bola; kritikal ang mataas na tubig, Potash, at proteksyon sa DBM at tip-burn."
        ),
        TimelineStageItem(
            stageName = "5. Pag-aani (Harvest Maturity)",
            durationDays = "70–90 Araw (DAT)",
            description = "Siksik, matigas, at buo na ang ulo (1–3 kg, 15–25 cm); pagputol sa umaga gamit ang kutsilyo na may 2–3 protective wrapper leaves."
        )
    ),
    howTos = listOf(
        HowToSectionItem(
            title = "Paano Maglipat-Tanim ng Punla ng Repolyo",
            summary = "Hakbang-hakbang na paraan upang maiwasan ang transplant shock sa repolyo.",
            steps = listOf(
                "Diligan ang seedling tray 1 oras bago kunin ang mga punla upang buo ang soil plug.",
                "Maglipat-tanim sa dapit-hapon (3:00 PM–5:00 PM) o sa maulap na araw upang hindi malanta sa init.",
                "Ibaon sa dating lalim ng seedling plug; huwag lulubog ang gitnang usbong (growing point).",
                "Diligan agad ng starter solution (hinalong vermitea o mahinang 14-14-14) sa paligid ng puno."
            )
        ),
        HowToSectionItem(
            title = "Biyolohikal na Pamamahala sa Diamondback Moth (DBM)",
            summary = "Pagkontrol sa #1 peste nang walang nakalalasong synthetic pesticides.",
            steps = listOf(
                "Magtanim ng mustasa bilang trap crop sa paligid ng kamada 2 linggo bago ang repolyo.",
                "Mag-isprey ng Bacillus thuringiensis (Bt) biopesticide sa takipsilim (5:00 PM) tuwing 5–7 araw.",
                "Magkabit ng yellow sticky traps sa bawat 10 metrong agwat upang mahuli ang mga adult moths.",
                "Pangalagaan ang Diadegma semiclausum parasitic wasps sa pamamagitan ng hindi paggamit ng malalakas na kemikal."
            )
        ),
        HowToSectionItem(
            title = "Tamang Pag-aani at Pag-iingat sa Biyahe",
            summary = "Pagpapanatili ng sariwang kalidad patungong Trading Post at palengke.",
            steps = listOf(
                "Pitasin sa maagang umaga habang malutong at puno ng tubig ang mga selula.",
                "Putulin ang tangkay gamit ang matalas na kutsilyo at mag-iwan ng 2–3 berde at sariwang wrapper leaves.",
                "Salansanin sa plastic crates na may padding; huwag ihagis upang hindi magkapasa.",
                "Iimbak sa malamig na bodega (0–4°C) o sa may bentilasyong lugar na malayo sa araw."
            )
        )
    ),
    faqs = listOf(
        FaqItem(
            question = "Bakit hindi bumibilog o maluwag ang ulo ng aking repolyo?",
            answer = "Karaniwang sanhi ito ng mataas na temperatura (higit 24°C sa kapatagan), kakulangan sa sikat ng araw, kulang sa pataba lalo na sa Potassium (Potash), o hindi pantay na pagdidilig. Sa maiinit na lugar, gumamit lamang ng tropical varieties tulad ng KK Cross F1 sa malamig na buwan ng Nobyembre hanggang Pebrero."
        ),
        FaqItem(
            question = "Ano ang lunas kapag may bukol-bukol na ang ugat ng repolyo (Clubroot)?",
            answer = "Walang pamatay-amag na makagagaling sa repolyong may bukol na. Ang tanging paraan ay bunutin at sunugin ang mga apektadong halaman. Bago ang susunod na tanim, maglagay ng Agricultural Lime (apog) upang itaas ang pH ng lupa sa 7.0–7.2, at magtanim ng ibang halaman tulad ng mais o beans sa loob ng 2–3 taon."
        ),
        FaqItem(
            question = "Bakit pumutok o bumuka ang ulo ng repolyo bago anihin?",
            answer = "Ito ay 'head splitting' na nangyayari kapag dumanas ng tagtuyot ang repolyo at biglang nakatanggap ng napakaraming tubig (hal. malakas na buhos ng ulan). Mabilis lumaki ang mga panloob na dahon kaysa sa panlabas na balat. Anihin agad ang mga pumutok bago mapasukan ng bacterial soft rot."
        ),
        FaqItem(
            question = "Bakit mangingitim ang dulo ng dahon sa loob ng repolyo (Tip-burn)?",
            answer = "Ang Tip-burn ay kakulangan ng Calcium sa dulo ng dahon dulot ng mabagal na daloy ng tubig o labis na nitrogen fertilizer. Mag-isprey ng fermented CalPhos o Calcium Nitrate foliar spray at panatilihing regular ang pagdidilig."
        ),
        FaqItem(
            question = "Maaari bang magtanim ng repolyo sa paso sa Metro Manila?",
            answer = "Oo! Gumamit ng malaking paso o container (hindi bababa sa 5–10 galon) na may buhaghag na potting mix (lupa, compost, CRH). Magtanim ng KK Cross F1 sa buwan ng Nobyembre hanggang Enero kapag mas malamig ang simoy ng hangin at ilagay sa lugar na may buong sikat ng araw."
        )
    ),
    nutritionTable = NutritionTableData(
        servingSize = "100 g (Sariwang Hilaw na Repolyo)",
        calories = "25 kcal",
        macroNutrients = listOf(
            NutritionRowData("Protina (Protein)", "1.28 g", "2.5%"),
            NutritionRowData("Kabuuang Taba (Total Fat)", "0.1 g", "0.1%"),
            NutritionRowData("Carbohydrates", "5.8 g", "4.4%"),
            NutritionRowData("Dietary Fiber (Hibla)", "2.5 g", "10%"),
            NutritionRowData("Asukal (Natural Sugars)", "3.2 g", "—"),
            NutritionRowData("Tubig (Water Content)", "92.2 g", "—")
        ),
        microNutrients = listOf(
            NutritionRowData("Bitamina C (Ascorbic Acid)", "36.6 mg", "44%"),
            NutritionRowData("Bitamina K1 (Phylloquinone)", "76.0 mcg", "63%"),
            NutritionRowData("Folate (Bitamina B9)", "43 mcg", "11%"),
            NutritionRowData("Bitamina B6 (Pyridoxine)", "0.12 mg", "9%"),
            NutritionRowData("Potassium (K)", "170 mg", "4%"),
            NutritionRowData("Calcium (Ca)", "40 mg", "4%"),
            NutritionRowData("Magnesium (Mg)", "12 mg", "3%"),
            NutritionRowData("Iron (Fe)", "0.47 mg", "3%"),
            NutritionRowData("Sulforaphane / Glucosinolates", "Mataas", "Phytonutrient")
        )
    ),
    exploreArticles = listOf(
        ArticleItem("Gabay sa Masaganang Repolyo sa Benguet Highlands", "Mga teknik ng mga nangungunang magsasaka sa Atok at Buguias para sa 30–40 tonelada bawat ektarya gamit ang KK Cross at Scorpio F1.", "7 min basahin"),
        ArticleItem("Organikong Pagpuksa sa Diamondback Moth (DBM)", "Paggamit ng Bt biopesticide, mustard trap cropping, at parasitic wasps nang walang synthetic chemical spray.", "6 min basahin"),
        ArticleItem("Pamamahala sa Clubroot Gamit ang Agricultural Lime", "Hakbang-hakbang na pagtatama sa pH ng asidikong lupa upang tuluyang mapuksa ang bukol sa ugat.", "5 min basahin")
    )
)
