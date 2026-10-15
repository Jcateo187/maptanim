package com.maptanim.app.features.farm.dialogs.vegetables_information

import androidx.compose.ui.graphics.Color
import com.maptanim.app.R

/**
 * Vegetable encyclopedia dossier for Cucumber / Pipino (Cucumis sativus).
 * Compiled from MapTanim Cucumber technical data, DA-BPI, and Philippine lowland agronomic guidelines.
 */
val cucumberData = CropEncyclopedia(
    cropName = "Cucumber",
    localName = "Pipino (Cucumis sativus)",
    scientificName = "Cucumis sativus",
    category = "Cucurbit / Climbing Vine (Cucurbitaceae)",
    family = "Cucurbitaceae (Gourd Family)",
    overview = "Ang pipino (Cucumis sativus) ay isa sa pinakapopular at mabilis anihing vining salad vegetable sa Pilipinas. Kilala ito sa buong kapuluan para sa sariwang ensalada, sawsawan, atsara, pampapreskong inumin, at pampaganda sa balat. Dahil higit 95% ng bunga ay binubuo ng natural na tubig na mayaman sa electrolytes, ito ang paboritong pamatid-uhaw at hydration crop ng mga Pilipino sa mainit na panahon.\n\nBilang isang indeterminate climbing vine, nangangailangan ang pipino ng patayong balag o vertical trellis netting (1.5 hanggang 2.0 metro ang taas). Ang pagpapatayo ng balag ay nagpapanatili sa mga bunga na tuwid, makinis, malinis sa putik, at protektado laban sa pagkabulok dulot ng basang lupa. Napakabilis ng ikot ng buhay nito—maaari nang magsimulang mag-ani sa loob lamang ng 35 hanggang 45 araw mula sa pagkakatanim, at tuloy-tuloy na pinipitas tuwing 1 hanggang 2 araw sa loob ng 4 hanggang 8 linggo.\n\nSa ilalim ng wastong patubig tuwing maagang umaga, regular na paglalagay ng Potassium para sa malutong na laman, at pruning ng mga sanga sa ibaba, nagbibigay ang pipino ng napakataas na ani na 15 hanggang 25 tonelada bawat ektarya (1 hanggang 2 kg bawat puno).",
    selectedVariety = VarietyInfo(
        name = "Diamante F1",
        producer = "East-West Seed",
        type = "High-Yielding Commercial Slicing Hybrid (F1)",
        badge = "Nangungunang Commercial Hybrid",
        description = "Ang nangungunang commercial hybrid cucumber sa Pilipinas. May pantay, silindrikal, at matingkad na berdeng bunga (18–22 cm), napakalutong at makatas na laman, at mataas na resistensya sa Downy Mildew at Cucumber Mosaic Virus (CMV). Handa nang simulan ang pag-aani sa loob lamang ng 35–40 araw mula paghahasik."
    ),
    otherVarieties = listOf(
        "Diamante F1 (Leading Commercial Slicing Hybrid)",
        "Emerald F1 (Dark Green Disease-Tolerant Hybrid)",
        "Pochero F1 (High Vigor All-Season Slicing Type)",
        "Pipino Pilipino (Traditional Open-Pollinated Native OPV)",
        "Governor F1 (Extra-Early Heavy Bearing Hybrid)",
        "Green Long (Extended Length Market Type)"
    ),
    carouselSlides = listOf(
        CarouselSlide(
            title = "Luntiang Taniman ng Pipino sa Patayong Balag",
            subtitle = "Malulusog na Diamante F1 vines na may nakabiting tuwid na berdeng pipino sa vertical trellis net",
            stage = "Handa nang Anihin (Market Ready)",
            badgeColor = Color(0xFF2E7D32),
            drawableResId = R.drawable.ph_cucumber_hero
        ),
        CarouselSlide(
            title = "Yugto ng Punla sa Nursery Tray",
            subtitle = "7–10 araw na matitibay na punla na may luntiang cotyledons at tunay na dahon bago ilipat sa kama",
            stage = "Yugto ng Punla (Nursery Phase)",
            badgeColor = Color(0xFF1565C0),
            drawableResId = R.drawable.ph_cucumber_seedlings
        ),
        CarouselSlide(
            title = "Pag-akyat sa Balag at Dilaw na Bulaklak",
            subtitle = "Matingkad na dilaw na bulaklak at pinong tendrils na mahigpit na pumupulupot sa nylon trellis netting",
            stage = "Pamumulaklak at Paggapang (Trellis Climbing)",
            badgeColor = Color(0xFF388E3C),
            drawableResId = R.drawable.ph_cucumber_trellis
        ),
        CarouselSlide(
            title = "Sariwa at Malulutong na Ani sa Katutubong Bilao",
            subtitle = "Tuwid, makinis, at preskong mga pipino na maagang umaga pinitas na may 1–2 cm maikling tangkay",
            stage = "Ganap na Hinog (Harvest Maturity)",
            badgeColor = Color(0xFFE65100),
            drawableResId = R.drawable.ph_cucumber_harvest
        )
    ),
    quickInfo = listOf(
        QuickInfoItem(
            key = "lifespan",
            label = "Buhay ng Halaman",
            value = "2–2.5 Buwan (Napakabilisang Siklo)",
            explanation = ExplanationData(
                title = "Mabilisang Siklo ng Pipino",
                summary = "Mula sa paghahasik hanggang huling anihan sa loob lamang ng 60 hanggang 75 araw.",
                details = listOf(
                    "Pagsibol ng Binhi: 4 hanggang 7 araw mula paghahasik.",
                    "Yugto ng Baging: 7 hanggang 25 araw ng mabilisang pag-akyat sa patayong balag.",
                    "Unang Anihan: 35 hanggang 45 araw pagkatapos magtanim.",
                    "Tuloy-tuloy na Pitas: Pitas tuwing 1 hanggang 2 araw sa loob ng 4 hanggang 6 na linggo."
                )
            )
        ),
        QuickInfoItem(
            key = "spacing",
            label = "Agwat ng Pagtatanim",
            value = "30–40 cm (Sa Hanay ng Balag)",
            explanation = ExplanationData(
                title = "Tamang Agwat sa Patayong Balag",
                summary = "30 hanggang 40 cm sa pagitan ng mga puno sa hanay, at 1.5 hanggang 2.0 metro sa pagitan ng mga hanay ng balag.",
                details = listOf(
                    "Ang masinsing 30–40 cm agwat ay nagbibigay-daan sa mataas na populasyon ng puno sa vertical trellis.",
                    "Nagbibigay ng maluwang na pasilyo para sa madaling araw-araw na pag-aani at pag-spray.",
                    "Pumipigil sa pamumuo ng downy mildew sa pamamagitan ng magandang sirkulasyon ng hangin."
                )
            )
        ),
        QuickInfoItem(
            key = "depth",
            label = "Lalim ng Hasik",
            value = "2.0 – 3.0 cm (Mababaw)",
            explanation = ExplanationData(
                title = "Lalim ng Pagtatanim ng Buto",
                summary = "Direct-seeded o transplanted nang mababaw sa mamasa-masang lupa.",
                details = listOf(
                    "Ibaon ang buto nang 2 hanggang 3 cm ang lalim sa malambot na lupa.",
                    "Maaaring ibabad sa malinis na tubig nang 8 oras bago ihasik upang mapabilis ang pagsibol.",
                    "Kung nagpupunla sa tray, ilipat-tanim sa ika-7 hanggang ika-10 araw bago maging maselan ang ugat."
                )
            )
        ),
        QuickInfoItem(
            key = "sunlight",
            label = "Sikat ng Araw",
            value = "6–8 Oras (Full Sun)",
            explanation = ExplanationData(
                title = "Pangangailangan sa Liwanag",
                summary = "Nangangailangan ng buong sikat ng araw upang mabilis na mamulaklak at magpalaki ng bunga.",
                details = listOf(
                    "Pinakamababa ang 6 hanggang 8 oras ng maliwanag na sikat ng araw araw-araw.",
                    "Ang lilim ay nagdudulot ng maninipis na baging at pagkalagas ng mga bulaklak.",
                    "Sa matinding init na lampas 35°C, ang banayad na shade net ay tumutulong upang maiwasan ang pollen abortion."
                )
            )
        ),
        QuickInfoItem(
            key = "watering",
            label = "Patubig",
            value = "2–3x Bawat Linggo (Critical: 95% Tubig)",
            explanation = ExplanationData(
                title = "Pantay at Regular na Patubig",
                summary = "95% tubig ang bunga ng pipino kaya kritikal ang tuloy-tuloy na moisture sa ugat.",
                details = listOf(
                    "Diligan 2 hanggang 3 beses bawat linggo gamit ang furrow o drip irrigation tuwing maagang umaga.",
                    "Ang panunuyo ng lupa ay nagdudulot ng mapait na bunga (cucurbitacin) at baluktot na hugis.",
                    "Iwasang magbasa ng dahon sa dakong hapon upang maiwasan ang downy mildew."
                )
            )
        ),
        QuickInfoItem(
            key = "temperature",
            label = "Temperatura",
            value = "24–30°C (Optimum) | Limit >35°C",
            explanation = ExplanationData(
                title = "Klima at Katatagan sa Init",
                summary = "Hiyang sa mainit na kapatagan ng Pilipinas na may katamtamang halumigmig.",
                details = listOf(
                    "24°C hanggang 30°C ang pinakamainam para sa matamis, malutong, at makatas na bunga.",
                    "Mabilis huminto ang pamumulaklak kapag bumaba sa 18°C.",
                    "Kapag lumagpas sa 35°C, kailangan ang regular na dilig upang hindi maluto ang pollen."
                )
            )
        ),
        QuickInfoItem(
            key = "soil_type",
            label = "Uri ng Lupa at pH",
            value = "Sandy Loam to Loam (pH 5.5–6.8)",
            explanation = ExplanationData(
                title = "Buhaghag na Lupa at Drainage",
                summary = "Mayaman sa organikong pataba, buhaghag, at may magandang daluyan ng tubig.",
                details = listOf(
                    "Optimal pH: 5.5 hanggang 6.8.",
                    "Sensitibo sa pagkababad ng tubig; kailangan ang 20 cm matataas na raised beds.",
                    "Maglagay ng 2–3 kg ng vermicompost o decomposed chicken manure sa bawat metro bago magtanim."
                )
            )
        ),
        QuickInfoItem(
            key = "yield_potential",
            label = "Inaasahang Ani",
            value = "15–25 t/ha (1–2 kg bawat puno)",
            explanation = ExplanationData(
                title = "Potensyal na Dami ng Ani",
                summary = "15 hanggang 25 tonelada bawat ektarya sa ilalim ng masinsing pamamahala sa vertical trellis.",
                details = listOf(
                    "150 hanggang 250 gramo bawat piraso ng sariwang bunga.",
                    "15,000 hanggang 25,000 kg bawat ektarya.",
                    "Araw-araw o bawat 2 araw na pitas sa panahon ng peak fruiting."
                )
            )
        )
    ),
    companionPlants = listOf(
        PlantRelationItem(
            name = "Mais (Corn)",
            scientificName = "Zea mays",
            isBeneficial = true,
            role = "Natural Windbreak & Microclimate Shield",
            explanation = ExplanationData(
                title = "Magandang Katabi: Mais",
                summary = "Humaharang sa malalakas na hangin na maaaring sumira sa vertical trellis.",
                details = listOf(
                    "Pinoprotektahan ang maseselang dahon at bulaklak ng pipino laban sa hangin.",
                    "Umaakit ng mga kapaki-pakinabang na predatory insects sa sakahan."
                )
            )
        ),
        PlantRelationItem(
            name = "Sitaw at Munggo (Beans/Legumes)",
            scientificName = "Vigna unguiculata / radiata",
            isBeneficial = true,
            role = "Natural Nitrogen Fixer & Soil Partner",
            explanation = ExplanationData(
                title = "Magandang Katabi: Legumes",
                summary = "Nagdadagdag ng natural na nitroheno sa lupa gamit ang root nodules.",
                details = listOf(
                    "Tumutulong sa masiglang paggapang ng baging ng pipino.",
                    "Maaaring itanim bilang kasunod na pananim sa crop rotation."
                )
            )
        ),
        PlantRelationItem(
            name = "Labanos (Radish)",
            scientificName = "Raphanus sativus",
            isBeneficial = true,
            role = "Soil Aerator & Cucumber Beetle Deterrent",
            explanation = ExplanationData(
                title = "Magandang Katabi: Labanos",
                summary = "Pinapalambot ang lupa sa paanan ng balag at nagtataboy sa salagubang.",
                details = listOf(
                    "Ang masangsang na amoy ng labanos ay nagtataboy sa cucumber beetles.",
                    "Mabilis anihin sa loob ng 25–30 araw habang lumalaki pa lamang ang pipino."
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
                summary = "Mabisang panangga sa root-knot nematodes at umaakit ng bubuyog para sa polinasyon.",
                details = listOf(
                    "Naglalabas ng alpha-terthienyl na sumusugpo sa nematodes sa ugat.",
                    "Ang matingkad na bulaklak ay umaakit ng mga pukyutan sa umaga."
                )
            )
        ),
        PlantRelationItem(
            name = "Sunflower",
            scientificName = "Helianthus annuus",
            isBeneficial = true,
            role = "Bee Attractor & Natural Trellis Anchor",
            explanation = ExplanationData(
                title = "Magandang Katabi: Sunflower",
                summary = "Pambihirang atraksyon para sa mga pukyutan na kailangan sa polinasyon ng pipino.",
                details = listOf(
                    "Pinatataas ang rate ng polinasyon ng mga babaeng bulaklak ng pipino.",
                    "Nagsisilbing matibay na haligi sa dulo ng hanay ng balag."
                )
            )
        )
    ),
    combativePlants = listOf(
        PlantRelationItem(
            name = "Kalabasa, Ampalaya, at Pakwan (Cucurbits)",
            scientificName = "Cucurbita / Momordica / Citrullus",
            isBeneficial = false,
            role = "Severe Shared Pest & Downy Mildew Multiplier",
            explanation = ExplanationData(
                title = "Iwasang Itabi sa Ibang Cucurbits",
                summary = "Pareho ang mga peste at sakit na mabilis magpasa-pasa sa bukid.",
                details = listOf(
                    "Mabilis magpalipat-lipat ang Melon Fruit Fly at Downy Mildew.",
                    "Magpatupad ng crop rotation kasama ang mais o palay sa loob ng 1 hanggang 2 taon."
                )
            )
        ),
        PlantRelationItem(
            name = "Patatas (Potato)",
            scientificName = "Solanum tuberosum",
            isBeneficial = false,
            role = "Fungal Blight & Nutrient Conflict",
            explanation = ExplanationData(
                title = "Iwasan ang Katabing Patatas",
                summary = "Maaaring magdulot ng pamumuo ng fungal pathogens sa root zone.",
                details = listOf(
                    "Nag-aagawan sa potassium at moisture sa lupa."
                )
            )
        ),
        PlantRelationItem(
            name = "Masinsing Repolyo",
            scientificName = "Brassica oleracea",
            isBeneficial = false,
            role = "Heavy Nutrient Competition",
            explanation = ExplanationData(
                title = "Iwasan ang Katabing Malalaking Brassicas",
                summary = "Mabilis ubusin ng repolyo ang abono sa lupa na kailangan ng pipino.",
                details = listOf(
                    "Maaaring maantala ang paglaki ng mga bunga ng pipino."
                )
            )
        )
    ),
    nutritionBadges = listOf(
        NutritionBadgeItem(
            code = "HYDRATION",
            name = "Deep Cellular Hydration",
            percentageDaily = "95.2% Likas na Tubig",
            role = "Mayaman sa Natural Electrolytes para sa Pamatid-Uhaw",
            explanation = ExplanationData(
                title = "Pambihirang Natural Hydration",
                summary = "Ang pipino ay isa sa pinakamataas ang water content sa lahat ng gulay, naglalaman ng 95.2% malinis na mineralized water.",
                details = listOf(
                    "Tumutulong sa mabilisang hydration sa ilalim ng mainit na panahon ng Pilipinas.",
                    "Nagtataglay ng natural na potassium at magnesium para sa balanse ng electrolytes."
                )
            )
        ),
        NutritionBadgeItem(
            code = "VIT_K",
            name = "Bitamina K (Phylloquinone)",
            percentageDaily = "21% DV (16.4 mcg)",
            role = "Pangangalaga sa Maayos na Pamumuo ng Dugo at Buto",
            explanation = ExplanationData(
                title = "Bitamina K sa Madilim na Berdeng Balat",
                summary = "Ang balat ng sariwang pipino ay nagtataglay ng 21% ng pang-araw-araw na pangangailangan sa Vitamin K.",
                details = listOf(
                    "Mahalaga sa blood clotting at pag-iwas sa labis na pagdurugo.",
                    "Tumutulong sa pag-absorb ng calcium para sa matitibay na buto."
                )
            )
        ),
        NutritionBadgeItem(
            code = "SILICA",
            name = "Silica & Flavonoids",
            percentageDaily = "Collagen & Skin Health",
            role = "Pampaganda ng Balat at Pampalakas ng Kasukasuan",
            explanation = ExplanationData(
                title = "Natural Silica para sa Balat",
                summary = "Naglalaman ng bio-available silica na mahalaga sa connective tissues, litid, at balat.",
                details = listOf(
                    "Kilala sa pampabawas ng pamamaga (eye puffiness) at sunburn relief.",
                    "Pangunahing sangkap sa natural na skincare at hydration masks."
                )
            )
        ),
        NutritionBadgeItem(
            code = "LOW_CAL",
            name = "Mababang Calorie & Panunaw",
            percentageDaily = "15 kcal bawat 100g",
            role = "Perpekto sa Pagpapanatili ng Malusog na Timbang",
            explanation = ExplanationData(
                title = "Napakababa sa Calories",
                summary = "15 kcal lamang bawat 100 gramo, perpektong meryenda at salad para sa mga nagbabawas ng timbang.",
                details = listOf(
                    "Walang taba at kolesterol.",
                    "Tumutulong sa maayos na pagdumi at paglilinis ng digestive tract."
                )
            )
        )
    ),
    pestsAndDiseases = listOf(
        PestDiseaseItem(
            name = "Melon Fruit Fly (Langaw-Bunga)",
            scientificName = "Bactrocera cucurbitae",
            severity = "Kritikal (Pangunahing Peste ng Pipino)",
            symptoms = "Maliliit na brown na tuldok o tagas ng dagta sa balat; nagiging dilaw, malambot, at nabubulok ang loob dahil sa uod (maggots).",
            prevention = "Fruit Bagging gamit ang paper sleeves 3–5 araw pagkatapos mamulaklak; methyl eugenol traps; field sanitation.",
            treatment = "Ibaon sa 50 cm lalim ang LAHAT ng nabulok na bunga; mag-spray ng protein bait spray sa labas ng taniman tuwing umaga.",
            explanation = ExplanationData(
                title = "Melon Fruit Fly sa Pipino",
                summary = "Napakabilis sumira sa komersyal na halaga ng mga bunga.",
                details = listOf(
                    "Hindi sapat ang kemikal kapag natusok na ang balat.",
                    "Ang sanitasyon sa bukid at pagbabalot ang tanging lunas."
                )
            )
        ),
        PestDiseaseItem(
            name = "Kuto sa Dahon / Melon Aphids",
            scientificName = "Aphis gossypii",
            severity = "Mataas (Virus Vector & Leaf Curling)",
            symptoms = "Kumpol ng maliliit na berdeng kulisap sa talbos at ilalim ng dahon; kulubot na dahon na may maitim na amag (sooty mold).",
            prevention = "Iwasan ang labis na nitroheno; dilaw na sticky traps sa lebel ng balag; reflective silver mulch.",
            treatment = "Wisikan ng banayad na pressure ng tubig sa umaga; mag-spray ng 0.5% neem oil extract na may kaunting sabon tuwing hapon.",
            explanation = ExplanationData(
                title = "Aphids at PRSV-W / CMV Virus",
                summary = "Pangunahing tagapagdala ng viral mosaic disease sa cucurbits.",
                details = listOf(
                    "Mabilis magparami sa tuyo at mainit na panahon.",
                    "Panatilihing malinis ang mga talbos gamit ang regular na organikong spray."
                )
            )
        ),
        PestDiseaseItem(
            name = "Aulacophora Leaf Beetle",
            scientificName = "Aulacophora similis / foveicollis",
            severity = "Katamtaman (Pabilog na Butas sa Dahon)",
            symptoms = "Madilaw-kahel na salagubang na gumagawa ng pabilog na butas sa dahon at kumakain ng mga bulaklak.",
            prevention = "Handpicking tuwing maagang umaga; pagbubudbod ng pinong kahoy na abo sa dahon.",
            treatment = "Mag-spray ng chili-garlic botanical extract (sili + bawang + sabon) tuwing hapon.",
            explanation = ExplanationData(
                title = "Aulacophora Beetle sa Pipino",
                summary = "Salagubang na sumisira sa photosynthesis at bulaklak.",
                details = listOf(
                    "Mabagal lumipad sa maagang umaga kaya madaling pulutin.",
                    "Mabisang pantaboy ang natural na abo ng kahoy."
                )
            )
        ),
        PestDiseaseItem(
            name = "Downy Mildew sa Balag",
            scientificName = "Pseudoperonospora cubensis",
            severity = "Kritikal sa Maulang Panahon",
            symptoms = "Kanto-kantong dilaw na tagpi sa ibabaw ng dahon na hinaharang ng mga ugat; may abuhing amag sa ilalim sa mahamog na umaga.",
            prevention = "Maluwang na agwat ng tanim; pruning ng masusukal na sanga sa ibaba; iwasang magbasa ng dahon sa hapon.",
            treatment = "Gupitin at sunugin ang mga apektadong dahon; mag-spray ng Baking Soda solution (5g/L na may sabon) o Copper fungicide.",
            explanation = ExplanationData(
                title = "Downy Mildew sa Pipino",
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
            severity = "Katamtaman (Puting Pulbos sa Dahon)",
            symptoms = "Puting pulbos na parang harina na bumabalot sa ibabaw ng mga dahon at tangkay sa tuyong araw at mahamog na gabi.",
            prevention = "Panatilihing buhaghag ang canopy para masikatan ng araw; iwasan ang labis na nitrogen fertilizer.",
            treatment = "Mag-spray ng 10% fresh milk solution sa maaraw na umaga o wettable sulfur powder (2g/L).",
            explanation = ExplanationData(
                title = "Powdery Mildew sa Pipino",
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
            prevention = "Paggamit ng malinis na binhi; pag-iwas sa pagtatanim sa dating tinaniman ng solanaceous crops; maayos na drainage.",
            treatment = "Bunutin agad ang halaman kasama ang ugat at lupa; sunugin sa labas ng sakahan; magbudbod ng apog (lime) sa hukay.",
            explanation = ExplanationData(
                title = "Bacterial Wilt sa Pipino",
                summary = "Sakit mula sa lupa na bumabara sa daluyan ng tubig ng baging.",
                details = listOf(
                    "Walang gamot na kemikal kapag nahawa na ang halaman.",
                    "Mahalaga ang crop rotation kasama ang mais o palay sa loob ng 2–3 taon."
                )
            )
        )
    ),
    seasonalCalendar = listOf(
        SeasonalCalendarMonth("Ene", "Tugatog ng Dry Season Cropping", "Malamig na simoy ng amihan; minimal downy mildew risk; perpektong kalidad ng malulutong na pipino; mataas ang demand sa palengke.", Color(0xFF2E7D32)),
        SeasonalCalendarMonth("Peb", "Peak Harvest & High Quality", "Tuloy-tuloy na anihan tuwing 1–2 araw; siksik at makatas ang laman; bantayan ang patubig sa ugat.", Color(0xFF2E7D32)),
        SeasonalCalendarMonth("Mar", "Tag-init: Bantayan ang Patubig", "Mataas ang sikat ng araw; kailangan ng regular na furrow o drip irrigation upang maiwasan ang mapait na cucurbitacin.", Color(0xFFE65100)),
        SeasonalCalendarMonth("Abr", "Peak Summer Demand", "Mataas ang presyo para sa refreshing drinks at salads; maglagay ng 30% shade net kung lumagpas sa 35°C ang init.", Color(0xFFE65100)),
        SeasonalCalendarMonth("Mayo", "Paghahanda sa Tag-ulan", "Paghahanda ng matataas na 20 cm raised beds at pagpapatibay sa vertical trellis net.", Color(0xFF1565C0)),
        SeasonalCalendarMonth("Hun", "Simula ng Ulan sa Lowlands", "Mataas ang presyo sa palengke; maglagay ng drainage furrows upang maiwasan ang waterlogging sa ugat.", Color(0xFF1565C0)),
        SeasonalCalendarMonth("Hul", "Panahon ng Habagat", "Higpitan ang guy-wires ng balag laban sa malalakas na hangin; mag-prune ng lumang dahon laban sa downy mildew.", Color(0xFF2E7D32)),
        SeasonalCalendarMonth("Ago", "Off-Season High Price", "Mataas ang kita ng mga magsasaka na may matitibay na balag at drainage; regular na pag-spray ng organic protectant.", Color(0xFF2E7D32)),
        SeasonalCalendarMonth("Set", "Paghahanda sa Bagong Siklo", "Paghahalo ng sariwang vermicompost at apog sa mga kama para sa paparating na amihan season.", Color(0xFF1565C0)),
        SeasonalCalendarMonth("Okt", "Malawakang Pagtatanim", "Pagsisimula ng peak cropping season sa buong bansa; napakabilis na paggapang sa vertical net.", Color(0xFF2E7D32)),
        SeasonalCalendarMonth("Nob", "Simula ng Pamumulaklak", "Optimal 24–28°C temperatura; masiglang polinasyon ng mga bubuyog sa umaga; simula ng masaganang anihan.", Color(0xFF2E7D32)),
        SeasonalCalendarMonth("Dis", "Tugatog ng Pamilihan (Pasko)", "Mataas na demand para sa mga handaang Kapaskuhan at ensalada; masaganang ani sa bawat balag.", Color(0xFF2E7D32))
    ),
    careGuide = listOf(
        HowToSectionItem(
            title = "Wastong Pagpapatayo ng Vertical Trellis sa Pipino",
            summary = "Paggawa ng matibay na patayong lambat (1.8m taas) upang mapanatiling tuwid at malinis ang mga bunga.",
            steps = listOf(
                "Magtayo ng mga poste ng kawayan na may taas na 1.8 hanggang 2.0 metro tuwing 2–3 metro sa kahabaan ng hanay.",
                "Mag-inat ng matibay na alambre sa itaas at ibaba, at ikabit ang nylon trellis netting (mesh size 15–20 cm).",
                "I-tali nang malumanay ang mga dulo ng baging gamit ang malambot na straw paakyat sa lambat.",
                "I-prune ang lahat ng sanga sa ibabang 30–50 cm upang manatiling malinis ang paanan ng puno.",
                "Kapag umabot sa itaas ng lambat, kurutin ang dulo (pinch tip) upang pasiglahin ang mga sangang mamumunga."
            )
        ),
        HowToSectionItem(
            title = "Pang-araw-araw na Pag-aani at Pag-iwas sa Kapaitan (Cucurbitacin)",
            summary = "Wastong pamamahala sa patubig at dalas ng pitas upang manatiling matamis at malutong ang pipino.",
            steps = listOf(
                "Diligan ang lupa 2–3 beses bawat linggo tuwing maagang umaga; huwag hayaang matuyo ang lupa nang higit 2 araw.",
                "Maglagay ng makapal na dayami (rice straw) mulch sa paanan ng puno upang mapanatiling malamig ang ugat.",
                "Pitasin ang mga bunga tuwing 1 hanggang 2 araw kapag umabot na sa 15–22 cm ang haba.",
                "Huwag hintaying lumobo o manilaw ang dulo upang hindi maging makahoy ang buto at mapait ang balat."
            )
        )
    ),
    growthTimeline = listOf(
        TimelineStageItem(
            stageName = "1. Pagsibol ng Binhi (Germination)",
            durationDays = "4–7 Araw",
            description = "Pagsibol ng binhi sa 2–3 cm lalim; paglabas ng dalawang makakapal na cotyledons sa mamasa-masang lupa."
        ),
        TimelineStageItem(
            stageName = "2. Yugto ng Baging at Pag-akyat sa Balag",
            durationDays = "7–25 Araw",
            description = "Mabilisang paghaba ng baging (2–3 metro) at paglabas ng mga tendril na kumakapit sa lambat; panahon ng lower canopy pruning."
        ),
        TimelineStageItem(
            stageName = "3. Pamumulaklak at Pamumunga",
            durationDays = "25–35 Araw",
            description = "Paglabas ng matingkad na dilaw na bulaklak; masiglang polinasyon ng mga bubuyog sa umaga; mabilisang paglaki ng munting pipino."
        ),
        TimelineStageItem(
            stageName = "4. Tuloy-tuloy na Anihan (Continuous Harvest)",
            durationDays = "35–70+ Araw",
            description = "Pitas tuwing 1 hanggang 2 araw ng makikinis, matitigas, at berdeng pipino (150–250g); tuloy-tuloy sa loob ng 4 hanggang 6 na linggo."
        )
    ),
    howTos = listOf(
        HowToSectionItem(
            title = "Tamang Paraan ng Pag-aani Tuwing 1–2 Araw",
            summary = "Hakbang sa pagpitas upang mapanatili ang crispness at shelf life ng pipino.",
            steps = listOf(
                "Anihin tuwing maagang umaga (6:00–7:30 AM) bago sumikat ang mainit na araw upang buo ang turgidity at lutong ng laman.",
                "Gamitin ang matalas na gunting o kutsilyo; mag-iwan ng 1 hanggang 2 sentimetro ng tangkay (peduncle).",
                "Iwasang hilahin gamit ang kamay upang hindi mapunit ang balat sa base.",
                "Ipunin nang maayos sa malinis na crates na may saping dahon ng saging upang maiwasan ang gasgas.",
                "I-imbak sa 10–13°C; huwag ilagay sa ibaba ng 7°C upang maiwasan ang chilling injury."
            )
        ),
        HowToSectionItem(
            title = "Pruning ng Ibabang Sanga (Lower Lateral Shoot Pruning)",
            summary = "Paraan ng pag-aalis ng masusukal na sanga upang mapataas ang ani at maiwasan ang amag.",
            steps = listOf(
                "Gamit ang malinis na gunting, putulin ang lahat ng sangang lumalabas mula sa lupa hanggang 30–50 cm ang taas ng puno.",
                "Iwanan ang nag-iisang matipunong main vine upang mabilis itong umakyat sa lambat nang hindi nag-aaksaya ng sustansya.",
                "Linisin ang mga tuyo at naninilaw na dahon sa paanan ng puno.",
                "Magsagawa ng pruning tuwing tuyo ang panahon sa umaga upang mabilis maghilom ang mga hiwa."
            )
        )
    ),
    faqs = listOf(
        FaqItem(
            question = "Bakit nagiging mapait ang bunga ng pipino?",
            answer = "Ang kapaitan sa pipino ay sanhi ng pamumuo ng kemikal na 'cucurbitacin' kapag na-stress ang halaman sa matinding tagtuyot sa mababaw na ugat, hindi pantay na patubig, o labis na init (>35°C). Panatilihing pantay ang patubig tuwing 2–3 araw, maglagay ng makapal na organic mulch upang mapanatiling malamig ang ugat, at pitasin ang bunga bago lumobo."
        ),
        FaqItem(
            question = "Bakit nagiging baluktot o kurbado (hugis titik C) ang pipino?",
            answer = "Ang baluktot na pipino ay dulot ng sagabal sa lambat habang lumalaki, hindi pantay na patubig (ang isang bahagi ay kulang sa tubig kaya mas mabagal lumaki), o bahagyang tusok ng melon fruit fly. Tiyaking nakabitin nang malaya pababa ang bawat bunga, magdilig nang regular, at maglagay ng sapat na Potassium at Boron."
        ),
        FaqItem(
            question = "Bakit kailangang anihin araw-araw o bawat 2 araw ang pipino?",
            answer = "Napakabilis ng cell expansion ng pipino; maaari itong lumaki ng 3–5 cm sa loob lamang ng isang araw! Kung hindi aanihin agad, mabilis itong magiging overmature (naninilaw, lumolobo, tumitigas ang buto, at nagiging maasim ang laman). Bukod dito, ang madalas na pag-aani ay nag-uudyok sa baging na patuloy na magbunga ng mga bagong pipino."
        ),
        FaqItem(
            question = "Paano maiiwasan ang downy mildew sa balag ng pipino?",
            answer = "Ang downy mildew ay maiiwasan sa pamamagitan ng pagtatanim sa patayong balag (1.8m vertical trellis), pag-prune sa mga sanga sa ibabang 30–50 cm para sa magandang daloy ng hangin, pagdidilig sa lupa tuwing umaga (hindi binabasa ang dahon sa hapon), at pag-spray ng baking soda solution (5g/L na may sabon) o copper fungicide kapag mahalumigmig ang panahon."
        ),
        FaqItem(
            question = "Ano ang tamang temperatura sa pag-imbak ng pipino laban sa chilling injury?",
            answer = "Ang pinakamainam na temperatura sa pag-imbak ng pipino ay 10°C hanggang 13°C na may 90–95% relative humidity. Huwag ilalagay sa ilalim ng 7°C (tulad ng pinakamalamig na bahagi ng refrigerator) dahil magkakaroon ito ng 'chilling injury'—nagkakaroon ng lubog na tubig sa balat (water-soaked pits), mabilis na lumalambot, at nabubulok."
        )
    ),
    nutritionTable = NutritionTableData(
        servingSize = "100 g (Sariwang Hilaw na Pipino na may Balat)",
        calories = "15 kcal",
        macroNutrients = listOf(
            NutritionRowData("Protina (Protein)", "0.65 g", "1.3%"),
            NutritionRowData("Kabuuang Taba (Total Fat)", "0.11 g", "0.1%"),
            NutritionRowData("Carbohydrates", "3.63 g", "2.8%"),
            NutritionRowData("Dietary Fiber (Hibla)", "0.50 g", "2.0%"),
            NutritionRowData("Asukal (Natural Sugars)", "1.67 g", "—"),
            NutritionRowData("Tubig (Water Content)", "95.2 g", "—")
        ),
        microNutrients = listOf(
            NutritionRowData("Bitamina K1 (Phylloquinone)", "16.4 mcg", "21%"),
            NutritionRowData("Bitamina C (Ascorbic Acid)", "2.80 mg", "5%"),
            NutritionRowData("Potassium (K)", "147 mg", "4%"),
            NutritionRowData("Kalsyo (Calcium, Ca)", "16.0 mg", "2%"),
            NutritionRowData("Magnesium (Mg)", "13.0 mg", "3%"),
            NutritionRowData("Phosphorus (P)", "24.0 mg", "2%"),
            NutritionRowData("Iron (Fe)", "0.28 mg", "2%"),
            NutritionRowData("Zinc (Zn)", "0.20 mg", "2%")
        )
    ),
    exploreArticles = listOf(
        ArticleItem("Gabay sa Modernong Vertical Trellis at Produksyon ng Pipino sa Pilipinas", "Kumpletong agronomic checklist mula sa pag-iinat ng vertical net, patubig, hanggang sa 15-25 toneladang ani gamit ang Diamante F1.", "6 min basahin"),
        ArticleItem("Siyensya ng Pag-iwas sa Kapaitan (Cucurbitacin) at Pagpapanatili ng Tamis", "Paliwanag kung paano kontrolin ang soil moisture at temperatura upang mapanatiling matamis at malutong ang pipino.", "5 min basahin"),
        ArticleItem("Organikong Pamamahala sa Downy Mildew at Melon Fruit Fly sa Cucurbits", "Ligtas na pamamaraan gamit ang baking soda, milk spray, fruit bagging, at pheromone traps na walang kemikal.", "5 min basahin")
    )
)
