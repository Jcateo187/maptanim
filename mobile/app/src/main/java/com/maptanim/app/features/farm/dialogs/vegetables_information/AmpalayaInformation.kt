package com.maptanim.app.features.farm.dialogs.vegetables_information

import androidx.compose.ui.graphics.Color
import com.maptanim.app.R

/**
 * Vegetable encyclopedia dossier for Bitter Gourd / Ampalaya (Momordica charantia).
 * Compiled from MapTanim Bitter Gourd technical data, DA-BPI, and Philippine lowland agronomic guidelines.
 */
val ampalayaData = CropEncyclopedia(
    cropName = "Ampalaya",
    localName = "Ampalaya / Bitter Gourd",
    scientificName = "Momordica charantia",
    category = "Cucurbit / Climbing Vine (Cucurbitaceae)",
    family = "Cucurbitaceae (Gourd Family)",
    overview = "Ang ampalaya (Momordica charantia) ay isa sa pinakamataas ang halagang commercial at medicinal crop sa Pilipinas. Kilala ito bilang pambansang sangkap sa Pinakbet, Ginisang Ampalaya na may itlog, at sari-saring lutuing Ilocano at Tagalog. Bukod sa sarap nito sa hapag-kainan, tanyag ang ampalaya sa buong mundo sa taglay nitong bioactive compounds tulad ng charantin, momordicin, at polypeptide-p (halamang insulin) na siyentipikong napatunayang tumutulong sa pagpapababa ng blood sugar levels sa mga may diabetes.\n\nBilang isang indeterminate climbing vine, nangangailangan ang ampalaya ng matatag na balag (overhead o vertical bamboo trellis arbor na may taas na 1.5–2 metro) upang ang mahahabang bunga ay malayang bumitin nang tuwid, malinis sa putik, at malayo sa mga sakit sa lupa. Ang mga baging ay gumagapang nang 3 hanggang 5 metro at nagtataglay ng monoecious yellow flowers (magkahiwalay na lalaki at babaeng bulaklak) na dumedepende sa mga bubuyog at pollinators sa umaga.\n\nHanda nang simulan ang pag-aani sa loob lamang ng 50 hanggang 60 araw mula pagkakatanim, at tuloy-tuloy na namumunga tuwing 2 hanggang 4 na araw sa loob ng 2 hanggang 3 buwan. Sa ilalim ng masinsing pamamahala, pagbabalot ng bunga laban sa melon fruit fly, at balanseng pataba, nagbibigay ito ng masaganang ani na 8 hanggang 15 tonelada bawat ektarya (1 hanggang 2 kg bawat puno).",
    selectedVariety = VarietyInfo(
        name = "Jade Star XL F1",
        producer = "East-West Seed",
        type = "High-Yielding Commercial Hybrid (F1)",
        badge = "Pambansang Commercial Standard",
        description = "Ang nangungunang commercial hybrid ampalaya sa Pilipinas. May napakahahabang bunga (30–35 cm), matataba at prominenteng tagaytay (ridges), matingkad na luntiang balat, at katamtamang pait na labis na hinahanap sa palengke. Matibay sa transportasyon at may mataas na resistensya sa Papaya Ringspot Virus - Cucurbit Strain (PRSV-W)."
    ),
    otherVarieties = listOf(
        "Jade Star XL F1 (Premier High-Yielding Commercial Hybrid)",
        "Galaxy F1 (Deep Green Extra-Long Hybrid, Rain-Tolerant)",
        "Makiling F1 / OPV (Traditional Native Bitter Gourd)",
        "Mestisa F1 (High Vigor Hybrid for Wet-Season Cropping)",
        "Sta. Rita (Native Open-Pollinated Variety)",
        "Bonito F1 (Compact High-Density Hybrid)"
    ),
    carouselSlides = listOf(
        CarouselSlide(
            title = "Masaganang Bunga sa Kawayang Balag",
            subtitle = "Malalagong baging ng Jade Star XL na may nakabiting mahahabang berdeng ampalaya sa ilalim ng balag",
            stage = "Handa nang Anihin (Market Ready)",
            badgeColor = Color(0xFF2E7D32),
            drawableResId = R.drawable.ph_ampalaya_hero
        ),
        CarouselSlide(
            title = "Yugto ng Punla sa Nursery Bags",
            subtitle = "10–14 araw na matitibay na punla na may luntiang palmate leaves at umuusbong na tendril",
            stage = "Yugto ng Punla (Nursery Phase)",
            badgeColor = Color(0xFF1565C0),
            drawableResId = R.drawable.ph_ampalaya_seedlings
        ),
        CarouselSlide(
            title = "Pag-akyat sa Balag at Dilaw na Bulaklak",
            subtitle = "Matingkad na dilaw na bulaklak at maseselang tendrils na mahigpit na pumupulupot sa kawayan at trellis net",
            stage = "Pamumulaklak at Paggapang (Trellis Climbing)",
            badgeColor = Color(0xFF388E3C),
            drawableResId = R.drawable.ph_ampalaya_trellis
        ),
        CarouselSlide(
            title = "Sariwa at Malulutong na Ani sa Katutubong Bilao",
            subtitle = "Diretsong berdeng ampalaya na may makakapal na tagaytay at 2-cm peduncle para sa mahabang shelf life",
            stage = "Ganap na Hinog (Harvest Maturity)",
            badgeColor = Color(0xFFE65100),
            drawableResId = R.drawable.ph_ampalaya_harvest
        )
    ),
    quickInfo = listOf(
        QuickInfoItem(
            key = "lifespan",
            label = "Buhay ng Halaman",
            value = "3–4 Buwan (Tuloy-tuloy na Anihan)",
            explanation = ExplanationData(
                title = "Siklo ng Buhay ng Ampalaya",
                summary = "Mula sa pagtatanim hanggang huling anihan sa loob ng 90 hanggang 120 araw.",
                details = listOf(
                    "Pagsibol ng Binhi: 5 hanggang 10 araw mula paghahasik sa seedling tray o bukid.",
                    "Yugto ng Baging: 14 hanggang 35 araw ng paggapang at pagsasanay sa balag.",
                    "Unang Anihan: 50 hanggang 60 araw pagkatapos magtanim.",
                    "Panahon ng Anihan: Tuloy-tuloy na pitas tuwing 2 hanggang 4 na araw sa loob ng 2 hanggang 3 buwan."
                )
            )
        ),
        QuickInfoItem(
            key = "spacing",
            label = "Agwat ng Pagtatanim",
            value = "1.0–1.5 m (Sa Hanay ng Balag)",
            explanation = ExplanationData(
                title = "Tamang Agwat sa Balag",
                summary = "1.0 hanggang 1.5 metro sa pagitan ng mga puno sa hanay, at 2.0 hanggang 2.5 metro sa pagitan ng mga hanay ng balag.",
                details = listOf(
                    "Ang maluwang na agwat ay nagbibigay-daan sa sapat na sikat ng araw sa mga baging sa balag.",
                    "Nagpapadali sa pagbabalot ng bunga (fruit bagging) at pag-aani sa pasilyo.",
                    "Pumipigil sa pamumuo ng downy mildew sa pamamagitan ng magandang sirkulasyon ng hangin."
                )
            )
        ),
        QuickInfoItem(
            key = "depth",
            label = "Lalim ng Hasik",
            value = "2.0 – 3.0 cm (Hasik o Punla)",
            explanation = ExplanationData(
                title = "Lalim ng Pagtatanim ng Binhi",
                summary = "May matigas na balat ang buto ng ampalaya kaya kailangan ang tamang lalim at kahalumigmig.",
                details = listOf(
                    "Ibaon ang buto nang 2 hanggang 3 sentimetro ang lalim sa mamasa-masang lupa.",
                    "Maaaring ibabad sa maligamgam na tubig nang 12 oras bago ihasik upang mapabilis ang germination.",
                    "Kung nagpupunla sa tray, gumamit ng 50-hole o 104-hole tray na may rich potting mix."
                )
            )
        ),
        QuickInfoItem(
            key = "sunlight",
            label = "Sikat ng Araw",
            value = "6–8 Oras (Full Sun)",
            explanation = ExplanationData(
                title = "Pangangailangan sa Liwanag",
                summary = "Nangangailangan ng buong sikat ng araw upang masaganang mamulaklak at mamunga.",
                details = listOf(
                    "Pinakamababa ang 6 hanggang 8 oras ng sikat ng araw araw-araw.",
                    "Ang lilim ay nagdudulot ng puro dahon (vegetative growth) at kakarampot na babaeng bulaklak.",
                    "Sa matinding init na lampas 35°C, ang 30% shade net ay nakatutulong upang maiwasan ang flower abortion."
                )
            )
        ),
        QuickInfoItem(
            key = "watering",
            label = "Patubig",
            value = "2–3x Bawat Linggo (Moderate & Critical)",
            explanation = ExplanationData(
                title = "Wastong Kahalumigmig sa Ugat",
                summary = "Mababaw ang ugat ngunit malalapad ang dahon kaya kailangan ang regular na patubig.",
                details = listOf(
                    "Diligan 2 hanggang 3 beses bawat linggo gamit ang furrow o drip irrigation tuwing maagang umaga.",
                    "Kritikal ang tubig sa panahon ng pamumulaklak at pagpapalaki ng bunga (92% tubig ang laman).",
                    "Iwasang magbasa ng dahon sa dapit-hapon upang maiwasan ang downy mildew at powdery mildew."
                )
            )
        ),
        QuickInfoItem(
            key = "temperature",
            label = "Temperatura",
            value = "24–30°C (Optimum) | Limit >35°C",
            explanation = ExplanationData(
                title = "Klima at Katatagan sa Init",
                summary = "Likas na tropikal na pananim na hiyang sa mainit na kapatagan ng Pilipinas.",
                details = listOf(
                    "24°C hanggang 30°C ang pinakamainam para sa mabilis na paggapang at pamumulaklak.",
                    "Hindi gaanong angkop sa napakalamig na kabundukan (Benguet highlands).",
                    "Kapag lumagpas sa 35°C, maaaring maluto ang pollen at malagas ang mga bulaklak."
                )
            )
        ),
        QuickInfoItem(
            key = "soil_type",
            label = "Uri ng Lupa at pH",
            value = "Sandy Loam to Loam (pH 6.0–6.7)",
            explanation = ExplanationData(
                title = "Lupa at Daluyan ng Tubig",
                summary = "Buhaghag, mayaman sa organikong pataba, at may magandang daluyan ng tubig.",
                details = listOf(
                    "Optimal pH: 6.0 hanggang 6.7.",
                    "Labis na sensitibo sa pagkababad ng tubig (waterlogging); kailangan ang 20 cm raised mounds.",
                    "Maglagay ng 2–3 kg ng decomposed manure o vermicompost sa bawat hukay bago magtanim."
                )
            )
        ),
        QuickInfoItem(
            key = "yield_potential",
            label = "Inaasahang Ani",
            value = "8–15 t/ha (1–2 kg bawat puno)",
            explanation = ExplanationData(
                title = "Potensyal na Dami ng Ani",
                summary = "8 hanggang 15 tonelada bawat ektarya sa ilalim ng maayos na trellising at pamamahala.",
                details = listOf(
                    "150 hanggang 350 gramo bawat piraso ng bunga depende sa barayti.",
                    "1 hanggang 2 kg kabuuang ani bawat puno sa buong cropping cycle.",
                    "Tuloy-tuloy na anihan tuwing 2 hanggang 4 na araw sa loob ng 8 hanggang 12 linggo."
                )
            )
        )
    ),
    companionPlants = listOf(
        PlantRelationItem(
            name = "Mais (Corn)",
            scientificName = "Zea mays",
            isBeneficial = true,
            role = "Natural Windbreak & Microclimate Buffer",
            explanation = ExplanationData(
                title = "Magandang Katabi: Mais",
                summary = "Nagsisilbing proteksyon laban sa malalakas na hangin na maaaring sumira sa balag.",
                details = listOf(
                    "Ang matatangkad na hanay ng mais ay humaharang sa malalakas na hangin sa paligid ng balag.",
                    "Umaakit ng mga kapaki-pakinabang na predatory bugs na kumakain ng maliliit na uod."
                )
            )
        ),
        PlantRelationItem(
            name = "Sitaw at Bataw (Legumes)",
            scientificName = "Vigna unguiculata",
            isBeneficial = true,
            role = "Natural Nitrogen Fixer & Trellis Partner",
            explanation = ExplanationData(
                title = "Magandang Katabi: Sitaw",
                summary = "Nagdadagdag ng natural na nitroheno sa lupa gamit ang root nodules.",
                details = listOf(
                    "Tumutulong sa pagpapayaman ng lupa nang hindi nag-aagawan sa sustansya.",
                    "Maaaring itanim sa dulo ng hanay o bilang kasunod na crop sa rotation."
                )
            )
        ),
        PlantRelationItem(
            name = "Labanos (Radish)",
            scientificName = "Raphanus sativus",
            isBeneficial = true,
            role = "Natural Soil Breaker & Insect Deterrent",
            explanation = ExplanationData(
                title = "Magandang Katabi: Labanos",
                summary = "Mabilis lumaki sa paanan ng balag at nagtataboy sa ilang peste sa lupa.",
                details = listOf(
                    "Pinapalambot ang lupa sa ilalim ng balag.",
                    "Ang amoy ng labanos ay nagtataboy sa ilang uri ng salagubang."
                )
            )
        ),
        PlantRelationItem(
            name = "Amarilyo (Marigold)",
            scientificName = "Tagetes patula",
            isBeneficial = true,
            role = "Nematode Repellent & Pollinator Magnet",
            explanation = ExplanationData(
                title = "Magandang Katabi: Marigold",
                summary = "Napakabisang panangga sa root-knot nematodes at umaakit ng bubuyog para sa polinasyon.",
                details = listOf(
                    "Naglalabas ng alpha-terthienyl sa ugat na pumipigil sa mga nematodes sa lupa.",
                    "Ang matingkad na dilaw at kahel na bulaklak ay umaakit ng mga pukyutan sa umaga."
                )
            )
        ),
        PlantRelationItem(
            name = "Tanglad (Lemongrass) at Basil",
            scientificName = "Cymbopogon / Ocimum basilicum",
            isBeneficial = true,
            role = "Natural Fruit Fly & Aphid Repellent",
            explanation = ExplanationData(
                title = "Magandang Katabi: Mababangong Damo",
                summary = "Ang masangsang na aroma ay lumilito sa mga melon fruit flies at aphids.",
                details = listOf(
                    "Nagsisilbing natural na barrier sa gilid ng taniman ng ampalaya.",
                    "Nababawasan ang insidente ng tusok ng langaw sa mga bukas na bunga."
                )
            )
        )
    ),
    combativePlants = listOf(
        PlantRelationItem(
            name = "Pipino, Kalabasa, at Pakwan (Cucurbits)",
            scientificName = "Cucurbita / Cucumis / Citrullus",
            isBeneficial = false,
            role = "Severe Shared Pest & Disease Multiplier",
            explanation = ExplanationData(
                title = "Iwasang Itabi sa Ibang Cucurbits",
                summary = "Pareho ang mga peste at sakit na mabilis magpasa-pasa sa bukid.",
                details = listOf(
                    "Mabilis magpalipat-lipat ang Melon Fruit Fly (Bactrocera cucurbitae).",
                    "Mabilis kumalat ang Downy Mildew at Powdery Mildew sa pagitan ng mga balag.",
                    "Magpatupad ng crop rotation kasama ang mais o palay sa loob ng 1 hanggang 2 taon."
                )
            )
        ),
        PlantRelationItem(
            name = "Patatas (Potato)",
            scientificName = "Solanum tuberosum",
            isBeneficial = false,
            role = "Fungal Blight & Nematode Conflict",
            explanation = ExplanationData(
                title = "Iwasan ang Katabing Patatas",
                summary = "Maaaring magdulot ng pamumuo ng fungal pathogens at agawan sa potassium.",
                details = listOf(
                    "Parehong matakaw sa potassium sa panahon ng pamumunga at pamumulaklak."
                )
            )
        ),
        PlantRelationItem(
            name = "Fennel",
            scientificName = "Foeniculum vulgare",
            isBeneficial = false,
            role = "Allelopathic Growth Inhibitor",
            explanation = ExplanationData(
                title = "Iwasan ang Katabing Fennel",
                summary = "Naglalabas ng kemikal sa ugat na nagpapabagal sa paggapang ng baging.",
                details = listOf(
                    "Nagiging sanhi ng pagkabansot ng mga dulo ng baging."
                )
            )
        )
    ),
    nutritionBadges = listOf(
        NutritionBadgeItem(
            code = "CHARANTIN",
            name = "Charantin & Polypeptide-p",
            percentageDaily = "Bioactive Insulin Mimetic",
            role = "Siyentipikong Napatunayang Nagpapababa ng Asukal sa Dugo",
            explanation = ExplanationData(
                title = "Natural na Panlaban sa Diabetes",
                summary = "Ang ampalaya ay mayaman sa charantin, vicine, at polypeptide-p na kumikilos tulad ng natural na insulin sa katawan.",
                details = listOf(
                    "Tumutulong sa cellular uptake ng glucose upang mapanatiling normal ang blood sugar.",
                    "Kinikilala ng Department of Health (DOH) bilang isa sa 10 subok na halamang-gamot sa Pilipinas."
                )
            )
        ),
        NutritionBadgeItem(
            code = "VIT_C",
            name = "Bitamina C (Ascorbic Acid)",
            percentageDaily = "140% DV (84 mg)",
            role = "Mataas na Antioxidant para sa Resistensya at Balat",
            explanation = ExplanationData(
                title = "Napakataas na Bitamina C",
                summary = "Ang 100 gramo ng ampalaya ay nagbibigay ng 84 mg ng Vitamin C, higit pa kaysa sa karamihan ng citrus fruits.",
                details = listOf(
                    "Nagpapalakas ng natural immunity laban sa ubo, sipon, at impeksyon.",
                    "Mahalaga sa produksyon ng collagen para sa malusog na mga ugat at balat."
                )
            )
        ),
        NutritionBadgeItem(
            code = "FOLATE",
            name = "Folate (Bitamina B9)",
            percentageDaily = "18% DV (72 mcg)",
            role = "Para sa Produksyon ng Pulang Selula at DNA Synthesis",
            explanation = ExplanationData(
                title = "Masaganang Natural Folate",
                summary = "Mahalaga sa pagbuo ng bagong selula at kalusugan ng mga nagdadalang-tao.",
                details = listOf(
                    "Tumutulong sa pag-iwas sa neural tube defects sa mga sanggol.",
                    "Pangunahing katuwang sa paggawa ng red blood cells laban sa anemia."
                )
            )
        ),
        NutritionBadgeItem(
            code = "POTASSIUM",
            name = "Potassium & Dietary Fiber",
            percentageDaily = "296 mg Potassium | 2.8 g Fiber",
            role = "Pangangalaga sa Presyon ng Dugo at Kalusugan ng Tiyan",
            explanation = ExplanationData(
                title = "Hibla at Mineral para sa Puso",
                summary = "Mababang calorie (17 kcal bawat 100g) ngunit mayaman sa potassium na nagbabalanse ng sodium sa katawan.",
                details = listOf(
                    "Tumutulong sa pagpapanatili ng malusog na presyon ng dugo.",
                    "Ang soluble at insoluble fiber ay nagpapabilis ng metabolismo at panunaw."
                )
            )
        )
    ),
    pestsAndDiseases = listOf(
        PestDiseaseItem(
            name = "Melon Fruit Fly (Langaw-Bunga)",
            scientificName = "Bactrocera cucurbitae",
            severity = "Kritikal (Pangunahing Peste ng Ampalaya)",
            symptoms = "Maliliit na brown na tuldok o tagas ng dagta sa balat ng bunga; maagang naninilaw at nabubulok ang loob dahil sa uod (maggots).",
            prevention = "Fruit Bagging gamit ang dyaryo sa ika-3 araw pagkatapos ng bulaklak; methyl eugenol pheromone traps; field sanitation.",
            treatment = "Ibaon sa 50 cm lalim ang LAHAT ng nabulok na bunga; mag-spray ng protein bait spray sa labas ng taniman tuwing umaga.",
            explanation = ExplanationData(
                title = "Melon Fruit Fly sa Ampalaya",
                summary = "Ang pinakamalaking banta sa produksyon ng ampalaya sa buong Pilipinas.",
                details = listOf(
                    "Hindi sapat ang chemical insecticides dahil nasa loob na ng balat ang mga itlog at uod.",
                    "Ang pagbabalot ng bunga (fruit bagging) ang 100% pinakamabisang proteksyon."
                )
            )
        ),
        PestDiseaseItem(
            name = "Kuto sa Dahon / Melon Aphids",
            scientificName = "Aphis gossypii",
            severity = "Mataas (Virus Vector & Leaf Curling)",
            symptoms = "Kumpol ng maliliit na berdeng kulisap sa talbos at ilalim ng dahon; kulubot ang dahon na may maitim na amag (sooty mold).",
            prevention = "Iwasan ang labis na nitroheno; dilaw na sticky traps sa lebel ng balag; reflective silver mulch.",
            treatment = "Wisikan ng banayad na pressure ng tubig sa umaga; mag-spray ng 0.5% neem oil extract na may kaunting sabon tuwing hapon.",
            explanation = ExplanationData(
                title = "Aphids at PRSV-W Virus",
                summary = "Pangunahing tagapagdala ng nakamamatay na Papaya Ringspot Virus sa cucurbits.",
                details = listOf(
                    "Mabilis magparami sa tuyo at mainit na panahon.",
                    "Panatilihing malinis ang mga talbos gamit ang regular na organikong spray."
                )
            )
        ),
        PestDiseaseItem(
            name = "Epilachna Beetle (28-Spotted Ladybeetle)",
            scientificName = "Henosepilachna vigintioctopunctata",
            severity = "Katamtaman (Leaf-Scraping Damage)",
            symptoms = "Salagubang at madilaw na matinik na uod na kumakain sa ibabaw ng dahon, nag-iiwan ng mistulang lambat (skeletonized leaves).",
            prevention = "Handpicking tuwing maagang umaga; pagpisa sa mga kumpol ng madidilaw na itlog sa ilalim ng dahon.",
            treatment = "Mag-spray ng hot pepper extract (sili + bawang + sabon) o neem oil tuwing hapon.",
            explanation = ExplanationData(
                title = "Epilachna Beetle",
                summary = "Salagubang na sumisira sa photosynthesis ng mga dahon sa balag.",
                details = listOf(
                    "Mabagal gumalaw sa maagang umaga kaya madaling pulutin gamit ang kamay.",
                    "Huwag patayin ang pulang carnivorous ladybugs na kumakain ng aphids."
                )
            )
        ),
        PestDiseaseItem(
            name = "Downy Mildew sa Balag",
            scientificName = "Pseudoperonospora cubensis",
            severity = "Kritikal sa Maulang Panahon",
            symptoms = "Kanto-kantong dilaw na tagpi sa ibabaw ng dahon na hinaharang ng mga ugat; may abuhing amag sa ilalim sa mahalumigmig na umaga.",
            prevention = "Maluwang na agwat ng tanim; pruning ng masusukal na sanga sa ibaba; iwasang magbasa ng dahon sa hapon.",
            treatment = "Gupitin at sunugin ang mga apektadong dahon; mag-spray ng Baking Soda solution (5g/L na may sabon) o Copper fungicide.",
            explanation = ExplanationData(
                title = "Downy Mildew sa Ampalaya",
                summary = "Mabilis makalagas ng dahon at magpatuyo sa buong balag sa loob ng isang linggo.",
                details = listOf(
                    "Nabubuhay sa halumigmig at lamig sa gabi.",
                    "Ang magandang daloy ng hangin sa balag ang susi sa pag-iwas."
                )
            )
        ),
        PestDiseaseItem(
            name = "Powdery Mildew",
            scientificName = "Podosphaera xanthii / Erysiphe",
            severity = "Katamtaman (Foliar Coating)",
            symptoms = "Puting pulbos na parang harina na bumabalot sa ibabaw ng mga dahon at tangkay sa tuyong araw at mahamog na gabi.",
            prevention = "Panatilihing buhaghag ang canopy para masikatan ng araw; iwasan ang labis na nitrogen fertilizer.",
            treatment = "Mag-spray ng 10% fresh milk solution (1 bahagi gatas sa 9 bahagi tubig) sa maaraw na umaga o wettable sulfur (2g/L).",
            explanation = ExplanationData(
                title = "Powdery Mildew",
                summary = "Fungal infection na karaniwan sa panahon ng tag-araw sa Pilipinas.",
                details = listOf(
                    "Ang natural na protina sa gatas ay lumilikha ng antiseptic reaction sa ilalim ng araw.",
                    "Huwag mag-sulfur kapag lampas 32°C ang temperatura."
                )
            )
        ),
        PestDiseaseItem(
            name = "Bacterial Wilt",
            scientificName = "Ralstonia solanacearum",
            severity = "Kritikal (Nakamamatay sa Baging)",
            symptoms = "Biglang pagkalanta ng buong baging sa katanghalian habang berde pa ang dahon; naglalabas ng puting malagkit na bacterial slime sa tangkay.",
            prevention = "Paggamit ng malinis na binhi; pag-iwas sa pagtatanim sa dating tinaniman ng kamatis o talong; maayos na drainage.",
            treatment = "Bunutin agad ang halaman kasama ang ugat at lupa; sunugin sa labas ng sakahan; magbudbod ng apog (lime) sa hukay.",
            explanation = ExplanationData(
                title = "Bacterial Wilt sa Ampalaya",
                summary = "Sakit mula sa lupa na bumabara sa daluyan ng tubig ng baging.",
                details = listOf(
                    "Walang gamot na kemikal kapag nahawa na ang halaman.",
                    "Mahalaga ang crop rotation kasama ang mais o palay sa loob ng 2–3 taon."
                )
            )
        )
    ),
    seasonalCalendar = listOf(
        SeasonalCalendarMonth("Ene", "Tugatog ng Dry Season Cropping", "Malamig na simoy ng amihan; minimal downy mildew risk; perpektong kalidad ng mahahabang bunga; mataas ang demand sa palengke.", Color(0xFF2E7D32)),
        SeasonalCalendarMonth("Peb", "Peak Harvest & High Value", "Tuloy-tuloy na anihan tuwing 2–3 araw; siksik at malutong ang mga tagaytay; bantayan ang patubig sa ugat.", Color(0xFF2E7D32)),
        SeasonalCalendarMonth("Mar", "Tag-init: Bantayan ang Fruit Fly", "Mataas ang aktibidad ng melon fruit fly; higpitan ang fruit bagging at maglagay ng pheromone traps.", Color(0xFFE65100)),
        SeasonalCalendarMonth("Abr", "Peak Summer Cropping", "Kailangan ng regular na furrow o drip irrigation; maglagay ng 30% shade net kung lumagpas sa 35°C ang init.", Color(0xFFE65100)),
        SeasonalCalendarMonth("Mayo", "Paghahanda sa Tag-ulan", "Paghahanda ng matataas na 20 cm raised mounds at pagpapatibay sa mga kawayang poste ng balag.", Color(0xFF1565C0)),
        SeasonalCalendarMonth("Hun", "Simula ng Ulan sa Lowlands", "Mataas ang presyo sa palengke; maglagay ng drainage furrows upang maiwasan ang waterlogging sa ugat.", Color(0xFF1565C0)),
        SeasonalCalendarMonth("Hul", "Panahon ng Habagat", "Higpitan ang guy-wires ng balag laban sa malalakas na hangin; mag-prune ng lumang dahon laban sa downy mildew.", Color(0xFF2E7D32)),
        SeasonalCalendarMonth("Ago", "Off-Season High Price", "Mataas ang kita ng mga magsasaka na may matitibay na balag at drainage; regular na pag-spray ng organic protectant.", Color(0xFF2E7D32)),
        SeasonalCalendarMonth("Set", "Paghahanda sa Bagong Siklo", "Paghahalo ng sariwang vermicompost at apog sa mga hukay para sa paparating na amihan season.", Color(0xFF1565C0)),
        SeasonalCalendarMonth("Okt", "Malawakang Pagtatanim", "Pagsisimula ng peak cropping season sa buong bansa; mabilis na paggapang ng mga baging sa balag.", Color(0xFF2E7D32)),
        SeasonalCalendarMonth("Nob", "Simula ng Pamumulaklak", "Optimal 24–28°C temperatura; masiglang polinasyon ng mga bubuyog sa umaga; simula ng fruit bagging.", Color(0xFF2E7D32)),
        SeasonalCalendarMonth("Dis", "Tugatog ng Pamilihan (Pasko)", "Mataas na demand para sa Pinakbet at sari-saring lutuing Kapaskuhan; masaganang ani sa bawat balag.", Color(0xFF2E7D32))
    ),
    careGuide = listOf(
        HowToSectionItem(
            title = "Wastong Pagpapatayo ng Balag (Trellis) at Pag-aayos ng Baging",
            summary = "Paggawa ng matibay na kawayang balag at gabay sa pag-akyat ng baging para sa tuwid at malinis na ani.",
            steps = listOf(
                "Magtayo ng overhead balag o A-frame trellis na may taas na 1.5 hanggang 2.0 metro gamit ang matitibay na poste ng kawayan tuwing 2–3 metro.",
                "Magkabit ng nylon trellis net (mesh size 15–20 cm) o alambre sa ibabaw ng balag.",
                "I-tali nang malumanay ang batang baging gamit ang malambot na straw paakyat sa poste hanggang sa marating ang lambat.",
                "I-prune ang lahat ng sanga sa ibabang 1 metro upang manatiling malinis ang paanan ng puno.",
                "Kapag umabot sa itaas, kurutin ang dulo (pinch main stem) upang sumabog ang mga sangang mamumunga sa ibabaw ng balag."
            )
        ),
        HowToSectionItem(
            title = "Pangangalaga Laban sa Melon Fruit Fly Gamit ang Pagbabalot (Fruit Bagging)",
            summary = "Ang 100% garantisadong paraan upang maiwasan ang uod at bulok sa bunga nang walang nakalalasong kemikal.",
            steps = listOf(
                "Suriin ang balag tuwing umaga para sa mga bagong buka na babaeng bulaklak na may munting ampalaya sa base.",
                "Sa ika-3 hanggang ika-5 araw pagkatapos mamulaklak (kapag nalanta na ang dilaw na talulot), balutin ang bunga gamit ang paper bag o dyaryo.",
                "I-stapler o itali ang itaas na bahagi ng bag sa tangkay ng bunga; tiyaking bukas ang ilalim nang bahagya upang makatulo ang tubig-ulan.",
                "Iwanan ang balot hanggang sa araw ng pag-ani upang mapanatiling makinis at sariwa ang balat."
            )
        )
    ),
    growthTimeline = listOf(
        TimelineStageItem(
            stageName = "1. Pagsibol ng Binhi (Germination)",
            durationDays = "5–10 Araw",
            description = "Pagsibol ng matigas na buto sa 2–3 cm lalim; paglabas ng dalawang makakapal na cotyledons sa mamasa-masang lupa."
        ),
        TimelineStageItem(
            stageName = "2. Yugto ng Baging at Pag-akyat sa Balag",
            durationDays = "14–35 Araw",
            description = "Mabilisang paghaba ng baging (3–5 metro) at paglabas ng mga tendril na kumakapit sa kawayan; panahon ng lower canopy pruning."
        ),
        TimelineStageItem(
            stageName = "3. Pamumulaklak at Pagbabalot ng Bunga",
            durationDays = "35–50 Araw",
            description = "Paglabas ng matingkad na dilaw na bulaklak; polinasyon ng mga bubuyog sa umaga; simula ng fruit bagging sa maliliit na bunga."
        ),
        TimelineStageItem(
            stageName = "4. Tuloy-tuloy na Anihan (Continuous Harvest)",
            durationDays = "50–100+ Araw",
            description = "Anihan tuwing 2 hanggang 4 na araw ng mahahaba, matitigas, at berdeng ampalaya (150–350g); tuloy-tuloy sa loob ng 2 hanggang 3 buwan."
        )
    ),
    howTos = listOf(
        HowToSectionItem(
            title = "Tamang Paraan ng Pagbabalot ng Bunga (Fruit Bagging Protocol)",
            summary = "Hakbang-hakbang na proseso ng mekanikal na proteksyon laban sa langaw-bunga.",
            steps = listOf(
                "Maghanda ng paper bags (10 x 30 cm) gawa sa brown kraft paper o lumang dyaryo.",
                "Piliin ang mga munting bunga na 5–8 sentimetro ang haba at kalalanta pa lamang ng bulaklak.",
                "Isuklob ang paper bag mula sa ibaba pataas nang hindi nasusugatan ang mga pinong tagaytay.",
                "I-tupi at i-stapler ang itaas sa tangkay; panatilihing bukas ang maliit na butas sa ilalim para sa aeration at drainage.",
                "Huwag gumamit ng saradong plastik na bag dahil naiipon ang init at nagiging sanhi ng pagkaluto ng bunga."
            )
        ),
        HowToSectionItem(
            title = "Pruning ng Ibabang Sanga (Lower Lateral Shoot Pruning)",
            summary = "Paraan ng pag-aalis ng masusukal na sanga upang mapataas ang ani at maiwasan ang sakit.",
            steps = listOf(
                "Gamit ang malinis na pruning shears, putulin ang lahat ng sangang lumalabas mula sa lupa hanggang 1 metro ang taas ng puno.",
                "Iwanan ang nag-iisang matipunong main vine upang mabilis itong umakyat sa balag nang hindi nag-aaksaya ng sustansya.",
                "Linisin ang mga tuyo at naninilaw na dahon sa paanan ng puno.",
                "Magsagawa ng pruning tuwing tuyo ang panahon sa umaga upang mabilis maghilom ang mga hiwa."
            )
        )
    ),
    faqs = listOf(
        FaqItem(
            question = "Bakit nagiging baluktot o kurbado (hugis titik C) ang bunga ng ampalaya?",
            answer = "Ang baluktot na bunga ay kadalasang dulot ng sagabal sa lambat habang lumalaki, hindi pantay na patubig, o bahagyang tusok ng insekto sa isang bahagi. Upang maiwasan, tiyaking malayang nakabitin pababa ang bunga nang walang sumasabit, panatilihin ang pantay na dilig tuwing 2–3 araw, balutin ang bunga nang maaga, at maglagay ng sapat na Potassium at Boron."
        ),
        FaqItem(
            question = "Paano mabisang mapupuksa ang melon fruit fly nang walang mapanganib na kemikal?",
            answer = "Ang pagbabalot ng bunga (Fruit Bagging) gamit ang paper bag o dyaryo sa ika-3 hanggang ika-5 araw pagkatapos ng bulaklak ang 100% garantisadong panangga. Dagdagan ito ng paglalagay ng Methyl Eugenol pheromone lure traps sa paligid ng sakahan at agad na paglilibing sa lalim na 50 cm ng lahat ng nabulok o nalaglag na bunga."
        ),
        FaqItem(
            question = "Bakit nalalagas ang mga dilaw na bulaklak at munting buko bago lumaki?",
            answer = "Ang ampalaya ay may magkahiwalay na lalaki at babaeng bulaklak. Kung walang bubuyog na maglilipat ng pollen mula 6:30 AM hanggang 8:30 AM, hindi mabubuntis ang babaeng bulaklak at malalaglag ang buko. Magsagawa ng hand pollination sa pamamagitan ng pagkuskos ng anther ng lalaking bulaklak sa stigma ng babae, magtanim ng marigold bilang bee attractant, at iwasan ang chemical sprays sa umaga."
        ),
        FaqItem(
            question = "Kailan ang tamang oras ng pag-ani bago manilaw ang dulo ng ampalaya?",
            answer = "Anihin ang ampalaya kapag matambok at makakapal na ang mga tagaytay (ridges) at matigas ang laman, ngunit matingkad pa ang berdeng kulay (karaniwang 12 hanggang 18 araw pagkatapos bumuka ang bulaklak). Huwag hintaying pumusyaw o manilaw ang dulo dahil magiging malambot ang laman, mamumula ang buto, at mawawala ang commercial market value."
        ),
        FaqItem(
            question = "Paano mababawasan ang labis na pait ng ampalaya nang hindi nawawala ang sustansya nito?",
            answer = "Hiwain ang ampalaya nang manipis, budburan ng kaunting asin, at hayaan nang 10 hanggang 15 minuto upang kusa nitong ilabas ang mapait na katas; banlawan nang bahagya sa malinis na tubig at pigain nang marahan. Sa pagluluto, huwag haluin nang haluin kapag isinalang na sa kawali upang hindi kumalat ang pait."
        )
    ),
    nutritionTable = NutritionTableData(
        servingSize = "100 g (Sariwang Hilaw na Ampalaya)",
        calories = "17 kcal",
        macroNutrients = listOf(
            NutritionRowData("Protina (Protein)", "1.00 g", "2.0%"),
            NutritionRowData("Kabuuang Taba (Total Fat)", "0.17 g", "0.2%"),
            NutritionRowData("Carbohydrates", "3.70 g", "2.8%"),
            NutritionRowData("Dietary Fiber (Hibla)", "2.80 g", "11.2%"),
            NutritionRowData("Asukal (Natural Sugars)", "0.60 g", "—"),
            NutritionRowData("Tubig (Water Content)", "94.0 g", "—")
        ),
        microNutrients = listOf(
            NutritionRowData("Bitamina C (Ascorbic Acid)", "84.0 mg", "140%"),
            NutritionRowData("Bitamina A (Beta-Carotene)", "471 IU", "9%"),
            NutritionRowData("Folate (Bitamina B9)", "72.0 mcg", "18%"),
            NutritionRowData("Potassium (K)", "296 mg", "8%"),
            NutritionRowData("Kalsyo (Calcium, Ca)", "19.0 mg", "2%"),
            NutritionRowData("Iron (Fe)", "0.43 mg", "2.5%"),
            NutritionRowData("Magnesium (Mg)", "17.0 mg", "4%"),
            NutritionRowData("Zinc (Zn)", "0.80 mg", "7%")
        )
    ),
    exploreArticles = listOf(
        ArticleItem("Gabay sa Modernong Balag at Komersyal na Taniman ng Ampalaya sa Pilipinas", "Kumpletong agronomic checklist mula sa pagpapatayo ng kawayang balag, trellis net, hanggang sa 8-15 toneladang ani gamit ang Jade Star XL.", "6 min basahin"),
        ArticleItem("Pambansang Pamamaraan Laban sa Melon Fruit Fly Nang Walang Mapanganib na Kemikal", "Mabisang pagbabalot ng bunga (fruit bagging), pheromone traps, at sanitasyon sa sakahan upang mapanatiling primera klase ang ani.", "5 min basahin"),
        ArticleItem("Siyentipikong Paliwanag sa Charantin at Halamang Insulin ng Ampalaya", "Medikal na pag-unawa kung paano tumutulong ang bioactive compounds ng ampalaya sa natural na pagpapababa ng asukal sa dugo.", "5 min basahin")
    )
)
