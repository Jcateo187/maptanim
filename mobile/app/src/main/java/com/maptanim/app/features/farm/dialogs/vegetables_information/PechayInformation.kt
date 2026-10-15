package com.maptanim.app.features.farm.dialogs.vegetables_information

import androidx.compose.ui.graphics.Color
import com.maptanim.app.R

/**
 * Vegetable encyclopedia dossier for Pechay / Petsay (Brassica rapa subsp. chinensis).
 * Compiled from MapTanim Lettuce & Pechay technical data, DA-BPI, and Philippine lowland & urban agronomic guidelines.
 */
val pechayData = CropEncyclopedia(
    cropName = "Pechay",
    localName = "Pechay / Petsay (Brassica rapa)",
    scientificName = "Brassica rapa subsp. chinensis",
    category = "Leafy Greens (Brassicaceae)",
    family = "Brassicaceae (Cruciferae)",
    overview = "Ang pechay o petsay (Brassica rapa subsp. chinensis) ang itinuturing na pambansang paboritong madaling-anihing madahong gulay sa Pilipinas. Angkop itong itanim sa buong kapuluan sa buong taon (year-round), sa mga malalawak na sakahan sa kapatagan, mga urban container gardens, at maging sa mga bakuran.\n\nKilala ang pechay sa mabilis nitong turnaround cycle—maaari nang magsimulang anihin sa loob lamang ng 25 hanggang 35 araw mula sa pagkakatanim, at may ganap na maturity sa 30 hanggang 45 araw. Ang halaman ay may patayong rosette (upright rosette habit) na may taas na 20–30 cm, na binubuo ng malalapad, makikintab, at madilim na berdeng dahon na nakakabit sa makakapal, malulutong, at mapuputing tangkay (petioles).\n\nDahil mababaw ang ugat ng pechay (5–10 cm sa ibabaw ng lupa), nangangailangan ito ng tuloy-tuloy at madalas na patubig upang hindi kumunat ang dahon. Mabilis din itong tumugon sa mataas na nitroheno (100–150 kg N/ha) para sa mabilisang foliar expansion. Ang micronutrient na Boron ay labis na mahalaga upang maiwasan ang hungkag at biyak na tangkay (Hollow Stem), habang ang Kalsyo naman ang pumipigil sa tip-burn. Sa tamang 15–20 cm raised beds sa panahon ng tag-ulan, nagbibigay ito ng inaasahang ani na 8 hanggang 15 tonelada bawat ektarya (150–400 gramo bawat kumpol).",
    selectedVariety = VarietyInfo(
        name = "Black Behi",
        producer = "Condor / East-West Seed",
        type = "Open-Pollinated Commercial Standard (OPV)",
        badge = "Standard sa Buong Bansa",
        description = "Ang pinakapopular at subok na standard commercial variety ng pechay sa Pilipinas. May malalapad at mapuputing tangkay na may makakapal, makikintab, at madilim na berdeng dahon. Napakatatag sa init ng tag-araw at banayad na ulan. Handa nang anihin sa loob ng 25–30 araw na may timbang na 200–350 gramo bawat kumpol."
    ),
    otherVarieties = listOf(
        "Black Behi (Nationwide Leading Commercial OPV)",
        "Pavon / Pavito (East-West Seed High-Vigor Hybrid)",
        "Shanghai Green Baby Bok Choy (Spoon-Shaped Petioles)",
        "Baguio Pechay / Ching Chiang (Highland & Cool Season)",
        "Tagalog Pechay (Traditional OPV Native White Stem)"
    ),
    carouselSlides = listOf(
        CarouselSlide(
            title = "Luntiang Kama ng Pechay sa Mababang Sakahan",
            subtitle = "Malulusog na Black Behi rosettes sa 15–20 cm raised beds na may proteksyon laban sa baha",
            stage = "Handa nang Anihin (Market Ready)",
            badgeColor = Color(0xFF2E7D32),
            drawableResId = R.drawable.ph_pechay_hero
        ),
        CarouselSlide(
            title = "Pagsibol at Punlaan sa Nursery Tray",
            subtitle = "Mabilis na pagtubo sa 3–7 araw na may hugis-pusong cotyledons bago ilipat sa bukid",
            stage = "Yugto ng Punla (Nursery Phase)",
            badgeColor = Color(0xFF1565C0),
            drawableResId = R.drawable.ph_pechay_seedlings
        ),
        CarouselSlide(
            title = "Mabilisang Paglaki at Pagkapal ng Tangkay",
            subtitle = "18–25 araw na mayabong na vegetative phase na may rice hull mulch at tuloy-tuloy na patubig",
            stage = "Berdeng Paglaki (Vegetative Phase)",
            badgeColor = Color(0xFF388E3C),
            drawableResId = R.drawable.ph_pechay_growth
        ),
        CarouselSlide(
            title = "Sariwa at Malulutong na Ani sa Katutubong Bilao",
            subtitle = "Maagang umaga binunot kasama ang buong ugat upang mapanatili ang turgidity at crispness",
            stage = "Ganap na Hinog (Harvest Maturity)",
            badgeColor = Color(0xFFE65100),
            drawableResId = R.drawable.ph_pechay_harvest
        )
    ),
    quickInfo = listOf(
        QuickInfoItem(
            key = "lifespan",
            label = "Buhay ng Halaman",
            value = "25–45 Araw (Napakabilisang Siklo)",
            explanation = ExplanationData(
                title = "Napakabilis na Siklo ng Pechay",
                summary = "Isa sa pinakamabilis anihing pananim sa Pilipinas, 25 hanggang 45 araw lamang.",
                details = listOf(
                    "Pagsibol ng Binhi: 3 hanggang 7 araw mula paghahasik.",
                    "Yugto ng Punla: 10 hanggang 14 araw bago ilipat-tanim (o direct-seeded sa kama).",
                    "Unang Anihan: 25 hanggang 35 araw mula paghahasik.",
                    "Ganap na Maturity: 30 hanggang 45 araw bago magsimulang mamulaklak (bolting).",
                    "Nagbibigay-daan sa 8 hanggang 10 cropping cycles sa bawat taon."
                )
            )
        ),
        QuickInfoItem(
            key = "spacing",
            label = "Agwat ng Pagtatanim",
            value = "15–20 cm x 20–25 cm",
            explanation = ExplanationData(
                title = "Tamang Densidad sa Kama",
                summary = "20 hanggang 25 halaman bawat metro kwadrado sa 1-metro ang lapad na raised bed.",
                details = listOf(
                    "15–20 cm sa pagitan ng mga puno sa loob ng hanay.",
                    "20–25 cm sa pagitan ng mga hanay / tudling.",
                    "Ang tamang agwat ay nagbibigay ng espasyo para sa paglapad ng puting petioles at sumusugpo sa bacterial soft rot."
                )
            )
        ),
        QuickInfoItem(
            key = "depth",
            label = "Lalim ng Hasik",
            value = "0.5 – 1.0 cm (Mababaw)",
            explanation = ExplanationData(
                title = "Mababaw na Paghahasik",
                summary = "Napakaliit ng binhi ng brassica; hindi dapat ibaon nang malalim.",
                details = listOf(
                    "Ihasik nang mababaw sa lalim na 0.5 hanggang 1.0 cm.",
                    "Takpan ng manipis na pinong compost o carbonized rice hull (CRH).",
                    "Banayad na diligan gamit ang fine rose sprinkler upang hindi maanod ang binhi."
                )
            )
        ),
        QuickInfoItem(
            key = "sunlight",
            label = "Sikat ng Araw",
            value = "4–6 Oras (Full Sun to Light Shade)",
            explanation = ExplanationData(
                title = "Pangangailangan sa Araw",
                summary = "Nangangailangan ng 4 hanggang 6 na oras ng sikat ng araw sa araw-araw.",
                details = listOf(
                    "Mabuting sumisipsip ng buong araw sa tag-araw kung sapat ang tubig sa ugat.",
                    "Sa napakainit na tanghali (>32°C), ang 30% shade net ay nakatutulong upang maiwasan ang panunuyo ng dahon at maagang bolting."
                )
            )
        ),
        QuickInfoItem(
            key = "watering",
            label = "Patubig",
            value = "Araw-araw / Bawat 2 Araw (Shallow & Frequent)",
            explanation = ExplanationData(
                title = "Madalas at Banayad na Patubig",
                summary = "Mababaw ang ugat (5–10 cm) kaya madaling maapektuhan ng panunuyo ng ibabaw ng lupa.",
                details = listOf(
                    "Diligan araw-araw tuwing maagang umaga (6:00–7:30 AM).",
                    "Iwasang magbuhos ng malakas na tubig upang hindi magtalsikan ang putik sa ilalim ng puting dahon.",
                    "Iwasang basain ang dahon sa hapon upang maiwasan ang bacterial soft rot at damping-off."
                )
            )
        ),
        QuickInfoItem(
            key = "temperature",
            label = "Temperatura",
            value = "20–28°C (Optimum) | Year-Round",
            explanation = ExplanationData(
                title = "Klima at Katatagan sa Init",
                summary = "Mas matatag sa init kaysa repolyo at litsugas, kayang mabuhay buong taon sa Pilipinas.",
                details = listOf(
                    "20°C hanggang 28°C ang pinakamainam para sa malulutong at matatabang puting tangkay.",
                    "Maaaring tumubo hanggang 30–32°C basta may tuloy-tuloy na patubig.",
                    "Kapag labis na uminit at tumagal ang tagtuyot, napipilitang mamulaklak (bolting) nang maaga."
                )
            )
        ),
        QuickInfoItem(
            key = "soil_type",
            label = "Uri ng Lupa at pH",
            value = "Loam to Sandy Loam (pH 6.0–6.8)",
            explanation = ExplanationData(
                title = "Matabang Lupa at Paghahanda ng Kama",
                summary = "Buhaghag, mayaman sa organikong pataba, at may magandang daluyan ng tubig.",
                details = listOf(
                    "Optimal pH: 6.0 hanggang 6.8 (katamtamang asido hanggang neutral).",
                    "Maglagay ng 15–20 cm raised beds na may 30 cm tudling sa pagitan para sa drainage.",
                    "Ihalo ang 2–3 kg bawat metro kwadrado ng vermicompost o decomposed chicken manure bago magtanim."
                )
            )
        ),
        QuickInfoItem(
            key = "yield_potential",
            label = "Inaasahang Ani",
            value = "8–15 t/ha (150–400 g bawat kumpol)",
            explanation = ExplanationData(
                title = "Potensyal na Dami ng Ani",
                summary = "8 hanggang 15 tonelada bawat ektarya sa ilalim ng masinsing pamamahala.",
                details = listOf(
                    "150 hanggang 400 gramo bawat buong kumpol ng pechay.",
                    "8,000 hanggang 15,000 kg bawat ektarya depende sa densidad at sustansya ng lupa.",
                    "Maaaring anihin nang buo kasama ang ugat o putulin sa base."
                )
            )
        )
    ),
    companionPlants = listOf(
        PlantRelationItem(
            name = "Karot (Carrots)",
            scientificName = "Daucus carota",
            isBeneficial = true,
            role = "Complementary Root Depth & Soil Aerator",
            explanation = ExplanationData(
                title = "Magandang Katabi: Karot",
                summary = "Perpektong kombinasyon dahil magkaiba ang lalim ng kanilang ugat.",
                details = listOf(
                    "Mababaw ang ugat ng pechay (5–10 cm), samantalang malalim ang ugat ng karot (20–30 cm).",
                    "Hindi nag-aagawan sa sustansya sa iisang layer ng lupa.",
                    "Nagsisilbing natural soil breaker ang karot sa paligid ng pechay bed."
                )
            )
        ),
        PlantRelationItem(
            name = "Labanos (Radish)",
            scientificName = "Raphanus sativus",
            isBeneficial = true,
            role = "Trap Crop for Flea Beetles & Fast Companion",
            explanation = ExplanationData(
                title = "Magandang Katabi: Labanos",
                summary = "Umaakit sa flea beetles palayo sa pechay at mabilis anihin sa loob ng 25–30 araw.",
                details = listOf(
                    "Nagsisilbing biological trap crop laban sa flea beetles.",
                    "Nagpapaluwag sa mabibigat na lupa sa paligid ng kama."
                )
            )
        ),
        PlantRelationItem(
            name = "Sibuyas at Bawang",
            scientificName = "Allium cepa / sativum",
            isBeneficial = true,
            role = "Natural Pungent Pest Deterrent",
            explanation = ExplanationData(
                title = "Magandang Katabi: Sibuyas at Bawang",
                summary = "Ang masangsang na amoy ng allium ay lumilito sa mga uod at aphids.",
                details = listOf(
                    "Nagtataboy sa diamondback moth at green aphids.",
                    "Maaaring itanim sa gilid ng pechay beds bilang insect barrier."
                )
            )
        ),
        PlantRelationItem(
            name = "Amarilyo (Marigold)",
            scientificName = "Tagetes patula",
            isBeneficial = true,
            role = "Nematode Repellent & Predator Attractant",
            explanation = ExplanationData(
                title = "Magandang Katabi: Marigold",
                summary = "Naglalabas ng alpha-terthienyl laban sa nematodes at umaakit ng ladybugs.",
                details = listOf(
                    "Pinoprotektahan ang ugat laban sa root knot nematodes.",
                    "Umaakit ng kapaki-pakinabang na parasitic wasps laban sa uod ng pechay."
                )
            )
        ),
        PlantRelationItem(
            name = "Mint at Tanglad (Lemongrass)",
            scientificName = "Mentha / Cymbopogon",
            isBeneficial = true,
            role = "Natural Moth & Flea Beetle Repellent",
            explanation = ExplanationData(
                title = "Magandang Katabi: Mababangong Damo",
                summary = "Mabisang repellent laban sa gamugamo ng diamondback moth.",
                details = listOf(
                    "Ang mabangong langis ng mint at lemongrass ay nagtatakip sa amoy ng mustard oils ng pechay.",
                    "Pinipigilan ang pamumugad ng mga sumisipsip na kulisap."
                )
            )
        )
    ),
    combativePlants = listOf(
        PlantRelationItem(
            name = "Masinsing Repolyo at Mustasa",
            scientificName = "Brassica oleracea / juncea",
            isBeneficial = false,
            role = "Heavy Pest & Disease Multiplier (DBM & Clubroot)",
            explanation = ExplanationData(
                title = "Iwasan ang Masinsing Ibang Brassicas",
                summary = "Pareho ang mga peste at sakit na mabilis magpasa-pasa.",
                details = listOf(
                    "Mabilis magparami ng diamondback moth at flea beetles.",
                    "Maaaring magdulot ng pamumuo ng clubroot fungus sa lupa.",
                    "Magpatupad ng crop rotation kasama ang root crops o legumes sa bawat cycle."
                )
            )
        ),
        PlantRelationItem(
            name = "Masinsing Kamatis",
            scientificName = "Solanum lycopersicum",
            isBeneficial = false,
            role = "Flea Beetle Vector & Heavy Shading Conflict",
            explanation = ExplanationData(
                title = "Iwasan ang Katabing Masinsing Kamatis",
                summary = "Maaaring magpalipat-lipat ang flea beetles at matabunan sa araw ang pechay.",
                details = listOf(
                    "Ang flea beetles ng kamatis ay mabilis lumipat sa malambot na dahon ng pechay.",
                    "Tiyaking may sapat na espasyo kung itatanim sa parehong hardin."
                )
            )
        ),
        PlantRelationItem(
            name = "Strawberry",
            scientificName = "Fragaria ananassa",
            isBeneficial = false,
            role = "Root Fungus Vector & Nitrogen Competition",
            explanation = ExplanationData(
                title = "Iwasan ang Katabing Strawberry",
                summary = "Maaaring magkaroon ng kompetisyon sa espasyo at pagkalat ng fungal rot.",
                details = listOf(
                    "Nag-aagawan sa mababaw na pataba at tubig sa ibabaw ng kama."
                )
            )
        )
    ),
    nutritionBadges = listOf(
        NutritionBadgeItem(
            code = "VIT_A",
            name = "Bitamina A (Beta-Carotene)",
            percentageDaily = "89% DV",
            role = "Pangangalaga sa Malinaw na Paningin at Malusog na Balat",
            explanation = ExplanationData(
                title = "Mataas na Bitamina A sa Berdeng Dahon",
                summary = "Ang isang serving ng pechay ay naglalaman ng 4,468 IU ng Bitamina A.",
                details = listOf(
                    "Mahalaga sa kalusugan ng mata laban sa night blindness.",
                    "Pinoprotektahan ang balat at respiratory mucosal barriers laban sa impeksyon."
                )
            )
        ),
        NutritionBadgeItem(
            code = "VIT_C",
            name = "Bitamina C (Ascorbic Acid)",
            percentageDaily = "75% DV",
            role = "Pampalakas ng Natural na Resistensya at Produksyon ng Collagen",
            explanation = ExplanationData(
                title = "Masaganang Bitamina C",
                summary = "45 mg ng Vitamin C bawat 100 gramo ng sariwang pechay.",
                details = listOf(
                    "Malakas na natural antioxidant na lumalaban sa cell damage.",
                    "Tumutulong sa mas mabilis na paghilom ng sugat at pagpapalakas ng resistensya."
                )
            )
        ),
        NutritionBadgeItem(
            code = "VIT_K",
            name = "Bitamina K",
            percentageDaily = "57% DV",
            role = "Para sa Maayos na Pamumuo ng Dugo at Tibay ng Buto",
            explanation = ExplanationData(
                title = "Mataas na Bitamina K",
                summary = "Mahalagang sustansya para sa skeletal integrity at vascular health.",
                details = listOf(
                    "Pangunahing sangkap sa natural na blood clotting mechanism.",
                    "Tumutulong sa pag-imbak ng calcium sa loob ng matrix ng mga buto."
                )
            )
        ),
        NutritionBadgeItem(
            code = "CALCIUM",
            name = "Natural na Kalsyo (Plant Calcium)",
            percentageDaily = "11% DV (105 mg)",
            role = "Bio-Available Calcium para sa Matitibay na Ngipin at Buto",
            explanation = ExplanationData(
                title = "Mataas na Kalsyo sa Puting Tangkay",
                summary = "Naglalaman ng 105 mg ng natural na calcium bawat 100g, higit na madaling i-absorb kaysa sa spinach dahil mababa sa oxalates.",
                details = listOf(
                    "Mababang oxalic acid content kaya hindi humaharang sa absorption ng kalsyo.",
                    "Napakainam para sa mga bata, matatanda, at lactose-intolerant na indibidwal."
                )
            )
        )
    ),
    pestsAndDiseases = listOf(
        PestDiseaseItem(
            name = "Diamondback Moth (DBM)",
            scientificName = "Plutella xylostella",
            severity = "Mataas (Pangunahing Peste ng Brassica)",
            symptoms = "Maliliit na luntiang uod na lumilikha ng 'window-paning' at butas-butas na dahon; kapag ginalaw ay mabilis umimbay sa manipis na sutla.",
            prevention = "Crop rotation laban sa tuloy-tuloy na brassica; dilaw na sticky traps; conservation ng predatory wasps; fine insect netting.",
            treatment = "Handpicking sa umaga; mag-spray ng Bacillus thuringiensis (Bt kurstaki) tuwing dapit-hapon; targeted microbial bio-insecticide.",
            explanation = ExplanationData(
                title = "Diamondback Moth sa Pechay",
                summary = "Ang pinakapinsalang uod sa mga madahong brassica sa Pilipinas.",
                details = listOf(
                    "Mabilis magkaroon ng resistensya sa synthetic chemical insecticides.",
                    "Pinakamabisa ang Bt biopesticide at intercropping kasama ang sibuyas o mint."
                )
            )
        ),
        PestDiseaseItem(
            name = "Lupon / Flea Beetle",
            scientificName = "Phyllotreta striolata",
            severity = "Mataas (Shot-Hole Damage sa Dahon)",
            symptoms = "Maliliit na itim at makintab na salagubang na tumatalon tulad ng pulgas; gumagawa ng daan-daang maliliit na bilog na butas (shot-holes) sa dahon.",
            prevention = "Dilaw at puting sticky traps sa lebel ng kama; paglalagay ng fine insect netting (32-mesh); pagtatanim ng labanos bilang trap crop.",
            treatment = "Magbudbod ng pinong kahoy na abo (wood ash) o diatomaceous earth sa basang dahon; i-spray ng neem oil extract (0.5%) tuwing umaga.",
            explanation = ExplanationData(
                title = "Flea Beetles (Lupon)",
                summary = "Mabilis sumira sa kalidad ng dahon lalo na sa panahon ng tag-init.",
                details = listOf(
                    "Ang wood ash ay nagiging sanhi ng dehydration sa balat ng salagubang.",
                    "Panatilihing basa ang lupa dahil ayaw ng flea beetles sa mamasa-masang paligid."
                )
            )
        ),
        PestDiseaseItem(
            name = "Kuto sa Dahon / Green Peach Aphids",
            scientificName = "Myzus persicae / Aphis spp.",
            severity = "Katamtaman (Leaf Curling & Sooty Mold)",
            symptoms = "Kumpol-kumpol na maliliit na kulisap sa ilalim ng dahon at bagong usbong; naninilaw at kulubot na dahon na may maitim na amag.",
            prevention = "Iwasan ang labis na urea o nitrohenong pataba; reflective silver mulch; regular na inspeksyon sa ilalim ng dahon.",
            treatment = "Wisikan ng banayad na water jet sa umaga upang malaglag; mag-spray ng insecticidal soap o diluted neem oil tuwing hapon.",
            explanation = ExplanationData(
                title = "Aphids sa Pechay",
                summary = "Sumisipsip ng katas at nagpapahina sa mabilis na paglaki ng halaman.",
                details = listOf(
                    "Huwag gumamit ng matatapang na kemikal dahil mabilis anihin ang pechay.",
                    "Ligtas at mabisa ang sabon at tubig laban sa aphids."
                )
            )
        ),
        PestDiseaseItem(
            name = "Bacterial Soft Rot",
            scientificName = "Pectobacterium carotovorum",
            severity = "Kritikal (Mabahong Pagkabulok sa Base)",
            symptoms = "Basang-basa, malambot na parang uhog, at masangsang ang amoy na pagkabulok sa base ng puting tangkay at puso ng pechay lalo na pagkatapos ng ulan.",
            prevention = "15–20 cm matataas na raised beds; 30 cm drainage canal; maingat na paggamas upang hindi masugatan ang tangkay; iwasan ang pagbaha.",
            treatment = "Bunutin at ilibing sa malayo (30 cm lalim) ang apektadong halaman; patuyuin ang kama; magbudbod ng apog (lime) sa pinagbunutan.",
            explanation = ExplanationData(
                title = "Bacterial Soft Rot",
                summary = "Pangunahing sakit ng pechay sa tag-ulan na walang kemikal na lunas kapag nagsimula na.",
                details = listOf(
                    "Pumasok ang bakterya sa pamamagitan ng mga sugat mula sa uod o kagamitan.",
                    "Ang maayos na drainage at sanitasyon ang tanging mabisang panangga."
                )
            )
        ),
        PestDiseaseItem(
            name = "Damping-off sa Punlaan",
            scientificName = "Pythium / Rhizoctonia spp.",
            severity = "Mataas sa Nursery Phase",
            symptoms = "Naluluoy, pumapayat, at nabubulok ang base ng tangkay ng punla sa lebel ng lupa, dahilan upang matumba at mamatay ang seedling stands.",
            prevention = "Sterilized potting mix (carbonized rice hull at vermicast); iwasan ang sobrang dilig; sapat na sikat ng araw sa punlaan.",
            treatment = "Ihiwalay ang apektadong trays; magbawas ng dalas ng patubig; mag-drench ng Trichoderma harzianum bio-fungicide.",
            explanation = ExplanationData(
                title = "Damping-off sa Pechay Seedlings",
                summary = "Sakit ng mga munting punla sa sobrang basang punlaan.",
                details = listOf(
                    "Mahalaga ang malinis na seedbed at maayos na daloy ng hangin.",
                    "Ang Trichoderma ay natural na lumalaban sa masasamang fungi sa lupa."
                )
            )
        ),
        PestDiseaseItem(
            name = "Hollow Stem & Tip-burn (Boron & Calcium Deficiency)",
            scientificName = "Boron & Calcium Physiological Disorder",
            severity = "Katamtaman (Cracked Stem & Internal Browning)",
            symptoms = "Hungkag, may biyak, o maitim ang loob ng puting tangkay (Boron deficiency); sunog at tuyot na gilid sa mga pinakabatang dahon sa gitna (Calcium deficiency).",
            prevention = "Balansehin ang pataba; maglagay ng compost na mayaman sa micronutrients; panatilihing pantay ang kahalumigmig sa lupa.",
            treatment = "I-foliar spray ng Solubor o Borax (1–2 gramo bawat litro) o Calcium Nitrate / WCA (fermented eggshells) tuwing hapon.",
            explanation = ExplanationData(
                title = "Hollow Stem at Kakulangan sa Boron",
                summary = "Physiological disorder na karaniwan sa mabilisang lumalaking pechay sa ilalim ng mataas na nitroheno.",
                details = listOf(
                    "Kapag mabilis lumaki ang halaman dahil sa maraming urea ngunit kulang sa boron, nabibiyak ang loob ng tangkay.",
                    "Ang balanseng nutrisyon ay nagbibigay ng siksik at malutong na tangkay."
                )
            )
        )
    ),
    seasonalCalendar = listOf(
        SeasonalCalendarMonth("Ene", "Mataas na Kalidad (Dry Season)", "Malamig na simoy ng hangin; malulutong at mapuputing tangkay; minimal soft rot risk; peak commercial production.", Color(0xFF2E7D32)),
        SeasonalCalendarMonth("Peb", "Tugatog ng Ani sa Kapatagan", "Perpektong klima sa buong bansa; napakabilis na 25-day cycle; saganang ani sa palengke.", Color(0xFF2E7D32)),
        SeasonalCalendarMonth("Mar", "Tag-init: Bantayan ang Patubig", "Mataas ang sikat ng araw; kailangan ng araw-araw na dilig sa umaga upang hindi kumunat ang dahon.", Color(0xFFE65100)),
        SeasonalCalendarMonth("Abr", "Peak Summer Cropping", "Bantayan ang flea beetles at pamumulaklak (bolting); maglagay ng 30% shade net kung kinakailangan.", Color(0xFFE65100)),
        SeasonalCalendarMonth("Mayo", "Pagsisimula ng Pag-ulan", "Paghahanda ng matataas na 15–20 cm raised beds upang maiwasan ang waterlogging sa mababaw na ugat.", Color(0xFF1565C0)),
        SeasonalCalendarMonth("Hun", "Tag-ulan sa Lowlands", "Maglagay ng rice hull mulch upang maiwasan ang pagtalsik ng putik sa ilalim ng dahon.", Color(0xFF1565C0)),
        SeasonalCalendarMonth("Hul", "Panahon ng Habagat", "Mataas ang presyo sa palengke; gumamit ng plastic rain shelter o rain tunnel laban sa leaf beating at bacterial rot.", Color(0xFF2E7D32)),
        SeasonalCalendarMonth("Ago", "Off-Season High Value", "Mataas ang kita ng mga magsasaka na may maayos na drainage; iwasan ang pagbaha sa kama.", Color(0xFF2E7D32)),
        SeasonalCalendarMonth("Set", "Paghahanda sa Bagong Siklo", "Paghahalo ng sariwang vermicompost at apog sa kama para sa paparating na amihan.", Color(0xFF1565C0)),
        SeasonalCalendarMonth("Okt", "Malawakang Pagtatanim", "Pagsisimula ng peak cropping season sa buong kapuluan; magandang tubo at malalapad na dahon.", Color(0xFF2E7D32)),
        SeasonalCalendarMonth("Nob", "Simula ng Malamig na Hangin", "Optimal 20–26°C temperatura; pinakamagandang timbang (300–400g bawat kumpol); mababa ang peste.", Color(0xFF2E7D32)),
        SeasonalCalendarMonth("Dis", "Tugatog ng Pamilihan", "Mataas na demand para sa mga lutong Kapaskuhan (nilaga, bulalo, pansit); masaganang ani sa bawat kama.", Color(0xFF2E7D32))
    ),
    careGuide = listOf(
        HowToSectionItem(
            title = "Wastong Patubig sa Mababaw na Ugat ng Pechay",
            summary = "Panatilihing mamasa-masa ang ibabaw na 5–10 cm ng lupa upang maiwasan ang makunat na tangkay at maagang pamumulaklak.",
            steps = listOf(
                "Diligan araw-araw o bawat 2 araw tuwing maagang umaga (6:00–7:30 AM).",
                "Gumamit ng pinong sprinkler nozzle upang hindi mabali ang marurupok na tangkay.",
                "Iwasang basain ang mga dahon sa dakong hapon upang hindi kapitan ng bacterial soft rot.",
                "Maglagay ng 2–3 cm na rice hull (ipa) o straw mulch upang mapanatiling malamig ang ugat."
            )
        ),
        HowToSectionItem(
            title = "Pamamahala sa Nutrisyon at Pag-iwas sa Hollow Stem",
            summary = "Tamang balanse ng Nitroheno para sa mabilisang dahon at Boron para sa matitibay na tangkay.",
            steps = listOf(
                "Basal application: Maglagay ng 14-14-14 complete fertilizer (10–15g bawat metro kwadrado) o 2 kg vermicompost bago magtanim.",
                "Side-dress: Maglagay ng kaunting Urea o Ammonium Sulfate sa ika-10 hanggang ika-14 na araw mula paglipat-tanim.",
                "Micronutrients: Mag-spray ng 0.1% Boron foliar solution sa ika-15 araw upang maiwasan ang pagbiyak at pagkabulok sa loob ng tangkay.",
                "Iwasan ang labis na nitrogen malapit sa anihan upang hindi maakit ang mga aphids at uod."
            )
        )
    ),
    growthTimeline = listOf(
        TimelineStageItem(
            stageName = "1. Pagsibol (Germination)",
            durationDays = "3–7 Araw",
            description = "Pagsibol ng maliliit na binhi sa 0.5–1.0 cm lalim; paglabas ng dalawang hugis-pusong cotyledon leaves sa mamasa-masang lupa."
        ),
        TimelineStageItem(
            stageName = "2. Yugto ng Punla (Seedling Phase)",
            durationDays = "10–14 Araw",
            description = "Paglabas ng 3 hanggang 4 na tunay na dahon (true leaves); panahon ng hardening sa banayad na sikat ng araw bago ilipat sa bukid."
        ),
        TimelineStageItem(
            stageName = "3. Mabilisang Paglaki ng Rosette (Vegetative Phase)",
            durationDays = "15–25 Araw",
            description = "Mabilis na paglapad ng madilim na berdeng dahon at pagkapal ng malulutong na mapuputing tangkay; panahon ng masinsing patubig at side-dressing."
        ),
        TimelineStageItem(
            stageName = "4. Ganap na Kumpol at Anihan (Harvest Maturity)",
            durationDays = "25–45 Araw",
            description = "Buo at mabigat na kumpol (150–400 gramo); malutong at makintab ang mga tangkay; maagang umaga binubunot bago lumabas ang dilaw na bulaklak."
        )
    ),
    howTos = listOf(
        HowToSectionItem(
            title = "Paano Magtanim ng Pechay: Direct Seeding vs Transplanting",
            summary = "Dalawang subok na paraan ng pagtatanim ng pechay para sa sakahan at urban gardens.",
            steps = listOf(
                "Direct Seeding: Gumawa ng mabababaw na tudling na 20 cm ang agwat; maghasik ng binhi nang manipis sa 0.5 cm lalim; mag-thinning sa 15 cm agwat pagkalipas ng 10–12 araw.",
                "Transplanting: Maghasik sa 104-hole nursery tray; alagaan ng 12–14 araw; ilipat sa kama tuwing alas-4:00 ng hapon upang maiwasan ang transplant shock.",
                "Diligan agad pagkatapos magtanim at protektahan sa matinding sikat ng araw sa unang dalawang araw."
            )
        ),
        HowToSectionItem(
            title = "Tamang Paraan ng Pag-aani at Pagpapanatili ng Crispness",
            summary = "Hakbang sa pag-aani upang manatiling sariwa at malutong ang pechay papuntang kusina o palengke.",
            steps = listOf(
                "Anihin tuwing maagang umaga (6:00–7:30 AM) bago sumikat ang mainit na araw upang buo ang tubig sa mga tangkay.",
                "Basain nang bahagya ang kama 30 minuto bago anihin upang lumambot ang lupa.",
                "Hawakan sa base at hilahin nang buo kasama ang ugat (whole-plant pulling) para sa mas mahabang shelf life.",
                "Hugasan ang ugat sa malinis na malamig na tubig upang maalis ang putik nang hindi nababasa ang mga dahon.",
                "Itali sa kumpol na may 250 hanggang 500 gramo at ilagay sa malilim na lalagyan."
            )
        )
    ),
    faqs = listOf(
        FaqItem(
            question = "Bakit nagiging hungkag (hollow stem) at may biyak ang puting tangkay ng pechay?",
            answer = "Ang hungkag o biyak na tangkay (Hollow Stem) ay pangunahing sanhi ng kakulangan sa micronutrient na Boron na pinapalala ng labis na nitrohenong pataba (sobrang urea) at mabilisang paglaki. Upang maiwasan, maglagay ng organikong compost o mag-foliar spray ng diluted Boron (Solubor sa 1g/L) sa ika-15 araw ng paglaki, at panatilihing pantay ang patubig."
        ),
        FaqItem(
            question = "Gaano kabilis anihin ang pechay mula sa pagtatanim?",
            answer = "Napakabilis ng siklo ng pechay! Maaari na itong anihin bilang 'baby pechay' sa loob ng 25 araw mula sa paghahasik, at ganap na mature sa loob ng 30 hanggang 40 araw (150–400 gramo bawat kumpol). Huwag patagalin nang higit sa 45 araw dahil magsisimula itong mamulaklak (bolting) at magiging makunat ang tangkay."
        ),
        FaqItem(
            question = "Ano ang mabisang organikong pamuksa sa flea beetles (lupon na gumagawa ng maliliit na butas)?",
            answer = "Mabisang panangga ang pagbubudbod ng pinong kahoy na abo (wood ash) o diatomaceous earth sa basang dahon tuwing umaga upang ma-dehydrate ang salagubang. Maaari ding mag-spray ng 0.5% neem oil extract tuwing hapon, maglagay ng dilaw na sticky traps, at magtanim ng labanos sa paligid bilang trap crop."
        ),
        FaqItem(
            question = "Mas mainam ba ang direct seeding o pagpupunla sa tray para sa pechay?",
            answer = "Kapwa may magandang benepisyo: Ang direct seeding ay mas mabilis at walang transplant shock kaya perpekto sa malalawak na kama at sakahan (kailangan lamang mag-thinning sa ika-12 araw). Ang pagpupunla sa 104-hole nursery tray naman ay nakatitipid sa mamahaling binhi, mas madaling bantayan laban sa langgam at ulan, at perpekto para sa urban container farming."
        ),
        FaqItem(
            question = "Paano panatilihing sariwa ang pechay pagkatapos anihin nang hindi nalalanta?",
            answer = "Ang pag-aani kasama ang buong ugat at paghuhugas lamang sa ugat (nang tuyo ang dahon) ay nagpapanatili ng crispness nang 3 hanggang 5 araw. Kung ilalagay sa refrigerator (0–4°C) na nakabalot sa malinis na papel o perforated plastic bag, tatagal ito ng hanggang 1 hanggang 2 linggo."
        )
    ),
    nutritionTable = NutritionTableData(
        servingSize = "100 g (Sariwang Pechay / Petsay)",
        calories = "13 kcal",
        macroNutrients = listOf(
            NutritionRowData("Protina (Protein)", "1.50 g", "3.0%"),
            NutritionRowData("Kabuuang Taba (Total Fat)", "0.20 g", "0.3%"),
            NutritionRowData("Carbohydrates", "2.20 g", "1.7%"),
            NutritionRowData("Dietary Fiber (Hibla)", "1.0 g", "4.0%"),
            NutritionRowData("Asukal (Natural Sugars)", "1.20 g", "—"),
            NutritionRowData("Tubig (Water Content)", "95.3 g", "—")
        ),
        microNutrients = listOf(
            NutritionRowData("Bitamina A (Beta-Carotene)", "4468 IU", "89%"),
            NutritionRowData("Bitamina C (Ascorbic Acid)", "45.0 mg", "75%"),
            NutritionRowData("Bitamina K1 (Phylloquinone)", "45.5 mcg", "57%"),
            NutritionRowData("Folate (Bitamina B9)", "66 mcg", "16%"),
            NutritionRowData("Kalsyo (Calcium, Ca)", "105 mg", "11%"),
            NutritionRowData("Potassium (K)", "252 mg", "7%"),
            NutritionRowData("Iron (Fe)", "0.80 mg", "5%"),
            NutritionRowData("Magnesium (Mg)", "19 mg", "5%")
        )
    ),
    exploreArticles = listOf(
        ArticleItem("Gabay sa Mabilisang Produksyon ng Pechay sa Lowlands at Urban Gardens", "Kumpletong agronomic checklist mula sa paghahanda ng 15 cm raised beds, wastong patubig, hanggang sa 30-araw na anihan.", "5 min basahin"),
        ArticleItem("Likas-Kayang Pamuksa sa Diamondback Moth at Flea Beetles", "Mga organikong pamamaraan gamit ang Bacillus thuringiensis (Bt), kahoy na abo, at insect netting na walang kemikal.", "5 min basahin"),
        ArticleItem("Solusyon sa Hollow Stem at Wastong Paglalagay ng Micronutrients sa Brassica", "Siyentipikong pag-unawa sa ugnayan ng mabilis na paglaki, nitroheno, at kakulangan sa boron sa puting tangkay ng pechay.", "6 min basahin")
    )
)
