package com.maptanim.app.features.farm.dialogs.vegetables_information

import androidx.compose.ui.graphics.Color
import com.maptanim.app.R

/**
 * Vegetable encyclopedia dossier for Lettuce / Litsugas (Lactuca sativa).
 * Compiled from MapTanim Lettuce technical data, DA-BPI, and Philippine highland extension guidelines.
 */
val lettuceData = CropEncyclopedia(
    cropName = "Lettuce",
    localName = "Litsugas (Lactuca sativa)",
    scientificName = "Lactuca sativa",
    category = "Salad Greens (Asteraceae)",
    family = "Asteraceae (Compositae)",
    overview = "Ang litsugas (Lactuca sativa) ay isa sa pinakamataas ang halagang pananim na leafy salad vegetable sa Pilipinas. Pangunahing itinatanim ito sa malalamig na kabundukan ng Benguet (La Trinidad, Atok, Tublay, Buguias), Mountain Province, Bukidnon, at sa malalamig na buwan sa kapatagan (Nobyembre hanggang Pebrero), gayundin sa mga modernong greenhouse at urban container farms.\n\nKilala ang litsugas sa mabilis nitong siklo ng buhay (55–70 araw mula punla hanggang anihan) at mababang compact rosette growth habit (15–25 cm ang taas). Dahil 95% ng halaman ay tubig at napakababaw ng ugat nito (5–15 cm), nangangailangan ito ng tuloy-tuloy at banayad na kahalumigmig sa lupa, buhaghag na loam na mayaman sa organikong pataba, at proteksyon sa matinding init ng tanghali.\n\nIsa sa pinakamalaking hamon sa produksyon ng litsugas sa tropikal na Pilipinas ay ang 'bolting'—ang maagang pamumulaklak at paghaba ng tangkay kapag ang temperatura ay lumagpas sa 28–30°C. Ang bolting ay nagdudulot ng pamumuo ng mapait na gatas (lactucarium) na sumisira sa sariwang tamis at lutong ng dahon. Sa tamang 30–50% shade netting, 15–20 cm raised beds, at balanseng patubig, nagbibigay ito ng masaganang ani na 10 hanggang 20 tonelada bawat ektarya.",
    selectedVariety = VarietyInfo(
        name = "Grand Rapids",
        producer = "East-West Seed / Condor Quality Seeds",
        type = "Tropical Loose-Leaf Green Lettuce",
        badge = "Popular Loose-leaf",
        description = "Ang pinakapopular na loose-leaf green lettuce sa Pilipinas. May malalapad, kulot at magagaspang na gilid na may matingkad na berdeng kulay. Napakalutong, masarap, at may mataas na tolerance sa init kumpara sa crisphead types. Handa nang anihin sa loob ng 40–50 araw pagkalipat-tanim."
    ),
    otherVarieties = listOf(
        "Grand Rapids (Leading Loose-Leaf Variety)",
        "Great Lakes 54 (Crisphead Iceberg for Highlands)",
        "General / Green Span (Heat-Tolerant Romaine / Cos Type)",
        "Red Sails / Lollo Rossa (Frilly Burgundy-Red Salad Greens)",
        "Butterhead / Boston Bibb (Soft Tender Sweet Heart)",
        "Green Towers (Upright Sweet Romaine)"
    ),
    carouselSlides = listOf(
        CarouselSlide(
            title = "Luntiang Taniman ng Litsugas sa Benguet",
            subtitle = "Lush Grand Rapids at Crisphead rosettes sa raised beds sa malamig na hangin ng Atok",
            stage = "Matipid sa Ani (Market Ready)",
            badgeColor = Color(0xFF2E7D32),
            drawableResId = R.drawable.ph_lettuce_hero
        ),
        CarouselSlide(
            title = "Punlaan sa 104-Hole Nursery Tray",
            subtitle = "20–25 araw na pinong punla na may 3–4 true leaves bago ilipat sa bukid",
            stage = "Yugto ng Punla (Nursery Phase)",
            badgeColor = Color(0xFF1565C0),
            drawableResId = R.drawable.ph_lettuce_seedlings
        ),
        CarouselSlide(
            title = "Pagbuo ng Mayabong na Rosette",
            subtitle = "Pantay na 20–25 cm agwat sa matabang lupa na may drip line at straw mulch",
            stage = "Berdeng Paglaki (Vegetative Phase)",
            badgeColor = Color(0xFF388E3C),
            drawableResId = R.drawable.ph_lettuce_rosette
        ),
        CarouselSlide(
            title = "Sariwa at Malutong na Ani sa Bilao",
            subtitle = "Maagang umaga pinitas bago sumikat ang araw upang mapanatili ang crispness",
            stage = "Ganap na Hinog (Harvest Maturity)",
            badgeColor = Color(0xFFE65100),
            drawableResId = R.drawable.ph_lettuce_harvest
        )
    ),
    quickInfo = listOf(
        QuickInfoItem(
            key = "lifespan",
            label = "Buhay ng Halaman",
            value = "60–80 Araw (Mabilisang Siklo)",
            explanation = ExplanationData(
                title = "Maikling Siklo ng Litsugas",
                summary = "Mula sa paghahasik hanggang sa ganap na anihan sa loob ng 2 hanggang 2.5 buwan.",
                details = listOf(
                    "Pagsibol ng Binhi: 3 hanggang 7 araw sa seedling tray.",
                    "Panahon sa Punlaan: 20 hanggang 25 araw bago ilipat-tanim.",
                    "Paglaki sa Kama: 30 hanggang 45 araw mula transplanting.",
                    "Mabilis na turnaround na nagbibigay-daan sa 4 hanggang 6 na crop cycles bawat taon."
                )
            )
        ),
        QuickInfoItem(
            key = "spacing",
            label = "Agwat ng Pagtatanim",
            value = "20–25 cm x 25–30 cm",
            explanation = ExplanationData(
                title = "Tamang Densidad sa Kama",
                summary = "12 hanggang 16 na halaman bawat metro kwadrado sa 1-meter wide raised beds.",
                details = listOf(
                    "20–25 cm sa pagitan ng mga puno sa loob ng hanay.",
                    "25–30 cm sa pagitan ng mga tudling.",
                    "Ang sapat na agwat ay nagbibigay-daan sa pagbilog ng buong rosette at sirkulasyon ng hangin laban sa soft rot."
                )
            )
        ),
        QuickInfoItem(
            key = "depth",
            label = "Lalim ng Hasik",
            value = "0.5 – 1.0 cm (Mababaw)",
            explanation = ExplanationData(
                title = "Mababaw na Pagpupunla",
                summary = "Napakaliit ng binhi ng litsugas; nangangailangan ito ng bahagyang liwanag upang sumibol.",
                details = listOf(
                    "Huwag ibaon nang malalim; takpan lamang ng pinong CRH o vermicast na 0.5 cm.",
                    "Banayad na diligan gamit ang sprayer upang hindi lumubog ang binhi sa ilalim ng tray."
                )
            )
        ),
        QuickInfoItem(
            key = "sunlight",
            label = "Sikat ng Araw",
            value = "4–6 Oras (Partial to Full Sun)",
            explanation = ExplanationData(
                title = "Pangangailangan sa Liwanag",
                summary = "Nangangailangan ng 4–6 na oras ng maliwanag na sikat ng araw; sensitibo sa labis na init sa katanghalian.",
                details = listOf(
                    "Sa malalamig na highland (Benguet): Full sun sa buong maghapon.",
                    "Sa mainit na lowland: 30–50% shade netting mula 10:30 AM hanggang 2:30 PM upang maiwasan ang bolting at sunscald."
                )
            )
        ),
        QuickInfoItem(
            key = "watering",
            label = "Patubig",
            value = "Araw-araw / Bawat 2 Araw (Moderate & Consistent)",
            explanation = ExplanationData(
                title = "Wastong Kahalumigmig",
                summary = "Mababaw ang ugat (5–15 cm) kaya kailangan ng tuloy-tuloy na bahagyang moisture.",
                details = listOf(
                    "Diligan tuwing maagang umaga (6:00–7:30 AM) sa base ng puno.",
                    "Iwasang basain ang mga dahon bago magtakipsilim upang hindi kapitan ng downy mildew at bacterial rot.",
                    "Huwag patuyuin ang lupa upang hindi pumait ang dahon."
                )
            )
        ),
        QuickInfoItem(
            key = "temperature",
            label = "Temperatura",
            value = "15–24°C (Optimum) | Limit >30°C",
            explanation = ExplanationData(
                title = "Klima at Bolting Trigger",
                summary = "Likas na cool-season crop; madaling maapektuhan ng labis na init ng tropiko.",
                details = listOf(
                    "15°C hanggang 24°C ang pinakamainam para sa matamis, malutong, at siksik na dahon.",
                    "Kapag lumagpas sa 28–30°C, maagang namumulaklak (bolting) at nagiging mapait ang dagta."
                )
            )
        ),
        QuickInfoItem(
            key = "soil_type",
            label = "Uri ng Lupa at pH",
            value = "Loam to Sandy Loam (pH 6.0–6.8)",
            explanation = ExplanationData(
                title = "Lupa at Asido",
                summary = "Buhaghag, mayaman sa organikong pataba, at may magandang daluyan ng tubig.",
                details = listOf(
                    "Optimal pH: 6.0 hanggang 6.8.",
                    "Kapag mababa sa 5.5 ang pH, maglagay ng agricultural lime (apog) upang maiwasan ang nutrient lockup.",
                    "15–20 cm raised beds upang maiwasan ang waterlogging sa ugat."
                )
            )
        ),
        QuickInfoItem(
            key = "yield_potential",
            label = "Inaasahang Ani",
            value = "10–20 t/ha (300–600 g bawat ulo)",
            explanation = ExplanationData(
                title = "Potensyal na Dami ng Ani",
                summary = "10 hanggang 20 tonelada bawat ektarya sa ilalim ng masinsing pamamahala.",
                details = listOf(
                    "Loose-leaf type: 200–400 gramo bawat halaman.",
                    "Iceberg / Crisphead type: 400–600 gramo bawat buong ulo.",
                    "Maaaring anihin nang buo o pitasin ang panlabas na dahon (cut-and-come-again)."
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
                summary = "Napakagandang kombinasyon dahil magkaiba ang lalim ng kanilang ugat.",
                details = listOf(
                    "Mababaw ang ugat ng litsugas (ibabaw na 10 cm), samantalang malalim ang ugat ng karot (20–30 cm).",
                    "Hindi sila nag-aagawan sa sustansya at tubig sa iisang layer ng lupa."
                )
            )
        ),
        PlantRelationItem(
            name = "Labanos (Radish)",
            scientificName = "Raphanus sativus",
            isBeneficial = true,
            role = "Natural Soil Breaker & Fast Companion",
            explanation = ExplanationData(
                title = "Magandang Katabi: Labanos",
                summary = "Mabilis lumaki at nagpapaluwag sa lupa sa paligid ng litsugas.",
                details = listOf(
                    "Maaani ang labanos sa loob ng 25–30 araw habang lumalaki pa lamang ang litsugas.",
                    "Ang amoy ng labanos ay nagtataboy sa ilang sumisipsip na kulisap."
                )
            )
        ),
        PlantRelationItem(
            name = "Sibuyas at Chives",
            scientificName = "Allium cepa / schoenoprasum",
            isBeneficial = true,
            role = "Natural Pungent Aphid Repellent",
            explanation = ExplanationData(
                title = "Magandang Katabi: Sibuyas",
                summary = "Ang matapang na amoy ng allium ay lumilito sa mga kuto at aphids.",
                details = listOf(
                    "Nagtataboy sa green peach aphids na pangunahing peste ng litsugas.",
                    "Nagsisilbing natural na insect barrier sa gilid ng mga kama."
                )
            )
        ),
        PlantRelationItem(
            name = "Amarilyo (Marigold)",
            scientificName = "Tagetes patula",
            isBeneficial = true,
            role = "Root Nematode Repellent & Predator Attractant",
            explanation = ExplanationData(
                title = "Magandang Katabi: Marigold",
                summary = "Naglalabas ng natural na kemikal laban sa nematodes at umaakit ng ladybugs.",
                details = listOf(
                    "Pinipigilan ang root-knot nematodes sa root zone.",
                    "Inaakit ang mga hoverflies at ladybugs na kumakain ng aphids."
                )
            )
        ),
        PlantRelationItem(
            name = "Talong at Kamatis",
            scientificName = "Solanum lycopersicum",
            isBeneficial = true,
            role = "Canopy Shade Provider in Hot Weather",
            explanation = ExplanationData(
                title = "Magandang Katabi: Matatangkad na Tanim",
                summary = "Nagbibigay ng banayad na lilim sa mainit na tanghali.",
                details = listOf(
                    "Ang lilim ng kamatis sa tanghali ay pumipigil sa maagang bolting ng litsugas sa lowlands.",
                    "Itanim ang litsugas sa silangang bahagi upang masikatan ng araw sa umaga."
                )
            )
        )
    ),
    combativePlants = listOf(
        PlantRelationItem(
            name = "Masinsing Repolyo at Pechay",
            scientificName = "Brassica oleracea / rapa",
            isBeneficial = false,
            role = "Heavy Nutrient Competitor & Flea Beetle Vector",
            explanation = ExplanationData(
                title = "Iwasang Itabi sa Masinsing Brassicas",
                summary = "Matatakaw sa nitrogen at nagpapasalin ng mga uod at flea beetles.",
                details = listOf(
                    "Mabilis ubusin ng malalaking repolyo ang pataba sa lupa.",
                    "Maaaring lumipat ang diamondback moth at cabbage worms sa malambot na dahon ng litsugas."
                )
            )
        ),
        PlantRelationItem(
            name = "Kintsay (Celery)",
            scientificName = "Apium graveolens",
            isBeneficial = false,
            role = "Root Depth Conflict & Calcium Depleter",
            explanation = ExplanationData(
                title = "Iwasan ang Katabing Kintsay",
                summary = "Magkapareho ng mababaw na root zone at parehong matakaw sa calcium.",
                details = listOf(
                    "Nagpapalala sa insidente ng tip-burn sa gitna ng rosette ng litsugas."
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
                summary = "Naglalabas ng allelopathic toxin sa ugat na nagpapatigil sa paglaki ng litsugas.",
                details = listOf(
                    "Nagiging sanhi ng stunted growth at pagkabansot ng salad greens."
                )
            )
        )
    ),
    nutritionBadges = listOf(
        NutritionBadgeItem(
            code = "VIT_K",
            name = "Bitamina K",
            percentageDaily = "102% DV",
            role = "Pambihirang Sustansya sa Pamumuo ng Dugo at Tibay ng Buto",
            explanation = ExplanationData(
                title = "Napakataas na Bitamina K",
                summary = "Ang isang tasa ng sariwang litsugas ay sumasaklaw sa higit 100% ng pang-araw-araw na pangangailangan sa Vitamin K.",
                details = listOf(
                    "Mahalaga sa blood clotting at pag-iwas sa sobrang pagdurugo.",
                    "Tumutulong sa pag-absorb ng calcium para sa matitibay na buto."
                )
            )
        ),
        NutritionBadgeItem(
            code = "VIT_A",
            name = "Bitamina A (Beta-Carotene)",
            percentageDaily = "148% DV",
            role = "Para sa Malinaw na Mata at Malusog na Balat",
            explanation = ExplanationData(
                title = "Mataas na Beta-Carotene sa Berdeng Dahon",
                summary = "Mayaman sa provitamin A carotenoids lalo na sa matingkad na madilim na berdeng loose-leaf at romaine types.",
                details = listOf(
                    "Pangangalaga sa paningin laban sa macular degeneration.",
                    "Nagpapalakas sa immune system laban sa impeksyon."
                )
            )
        ),
        NutritionBadgeItem(
            code = "FOLATE",
            name = "Folate (Bitamina B9)",
            percentageDaily = "10% DV",
            role = "Para sa Produksyon ng Pulang Selula sa Dugo",
            explanation = ExplanationData(
                title = "Natural na Folate para sa Katawan",
                summary = "Mahalaga sa cellular growth at paggawa ng DNA.",
                details = listOf(
                    "Napakainam para sa mga nagdadalang-tao at lumalaking kabataan.",
                    "Tumutulong sa pag-iwas sa anemia."
                )
            )
        ),
        NutritionBadgeItem(
            code = "FIBER",
            name = "Dietary Fiber & Hydration",
            percentageDaily = "95% Tubig | 5% DV Fiber",
            role = "Mababang Calorie at Pampasigla ng Panunaw",
            explanation = ExplanationData(
                title = "Masustansyang Hydration at Hibla",
                summary = "Napakababa sa calories (15 kcal bawat 100g) ngunit mayaman sa malinis na mineralized water.",
                details = listOf(
                    "Perpektong sangkap sa pampapayat at malusog na digestive tract.",
                    "Tumutulong sa pagpapanatili ng tamang hydration sa katawan."
                )
            )
        )
    ),
    pestsAndDiseases = listOf(
        PestDiseaseItem(
            name = "Kuto sa Dahon / Green Peach Aphids",
            scientificName = "Myzus persicae",
            severity = "Mataas (Salad Contamination & Virus Vector)",
            symptoms = "Kumpol-kumpol na maliliit na berdeng kulisap sa ilalim ng dahon at gitna ng rosette; may malagkit na honeydew at itim na sooty mold.",
            prevention = "Yellow sticky traps; regular na inspeksyon; pag-iwas sa labis na nitrohenong pataba; conservation ng ladybugs.",
            treatment = "Wisikan ng banayad na water jet sa umaga; mag-spray ng insecticidal soap o neem oil extract (0.5%) sa ilalim ng dahon tuwing hapon.",
            explanation = ExplanationData(
                title = "Aphids sa Litsugas",
                summary = "Ang pangunahing insect pest sa salad greens sa Pilipinas.",
                details = listOf(
                    "Bumabawas sa kalidad ng salad dahil naiiwan ang mga patay na insekto sa mga kulubot ng dahon.",
                    "Huwag gumamit ng synthetic chemical sprays malapit sa anihan dahil sariwang kinakain ang dahon."
                )
            )
        ),
        PestDiseaseItem(
            name = "Harabas / Armyworm at Cutworm",
            scientificName = "Spodoptera litura / frugiperda",
            severity = "Mataas (Defoliation & Cut Seedlings)",
            symptoms = "Pumupugot sa tangkay ng mga bagong lipat na punla sa gabi; malalaking butas sa gitna ng rosette na may itim na dumi.",
            prevention = "Cardboard collars sa paligid ng tangkay ng punla; kalinisan sa paligid ng kama; pheromone traps para sa gamugamo.",
            treatment = "Handpicking sa gabi gamit ang flashlight (8:00–10:00 PM); mag-spray ng Bacillus thuringiensis (Bt) biopesticide tuwing hapon.",
            explanation = ExplanationData(
                title = "Harabas at Cutworm",
                summary = "Mabilis kumain ng malalambot na dahon sa gabi.",
                details = listOf(
                    "Ligtas sa tao ang Bt biopesticide at mabisa laban sa uod.",
                    "Suriin ang ilalim ng mga dahon para sa kumpol ng itlog ng gamugamo."
                )
            )
        ),
        PestDiseaseItem(
            name = "Bacterial Soft Rot",
            scientificName = "Pectobacterium carotovorum",
            severity = "Kritikal (Rotting of Rosette Heart)",
            symptoms = "Basang-basa, malambot na parang uhog, at mabahong pagkabulok ng base at puso ng litsugas lalo na pagkatapos ng malakas na ulan.",
            prevention = "15–20 cm matataas na raised beds; 20–25 cm maluwang na agwat para sa sirkulasyon ng hangin; iwasang masugatan ang halaman sa paggamas.",
            treatment = "Bunutin at ilibing sa malayo (30 cm lalim) ang apektadong halaman; patuyuin ang kama; magbudbod ng apog o abo sa pinagbunutan.",
            explanation = ExplanationData(
                title = "Bacterial Soft Rot sa Litsugas",
                summary = "Nakamamatay na sakit dulot ng bakterya sa basang panahon.",
                details = listOf(
                    "Walang gamot na kemikal kapag nabulok na; sanitation at drainage ang susi.",
                    "Huwag ihalo sa compost ang nabubulok na litsugas."
                )
            )
        ),
        PestDiseaseItem(
            name = "Tip-burn (Calcium Deficiency Disorder)",
            scientificName = "Physiological Calcium Deficiency",
            severity = "Katamtaman (Cosmetic & Quality Damage)",
            symptoms = "Sunog, tuyot, at kulay kayumangging gilid sa mga pinakabatang dahon sa gitna ng rosette; mapait na lasa.",
            prevention = "Pantay at regular na patubig; 30–50% shade netting sa katanghalian; iwasan ang sobrang nitrogen at potassium fertilizer.",
            treatment = "I-foliar spray ng diluted Water-Soluble Calcium (WCA gawa sa balat ng itlog) o Calcium Nitrate (0.5%) tuwing hapon.",
            explanation = ExplanationData(
                title = "Tip-burn sa Salad Greens",
                summary = "Physiological disorder dulot ng mabilis na paglaki sa ilalim ng init at kakulangan ng calcium.",
                details = listOf(
                    "Hindi sakit mula sa mikrobyo kundi kakulangan sa sirkulasyon ng kalsyo sa gitna ng rosette.",
                    "Ang pantay na patubig ang pangunahing lunas."
                )
            )
        ),
        PestDiseaseItem(
            name = "Downy Mildew",
            scientificName = "Bremia lactucae",
            severity = "Katamtaman (Foliar Spotting)",
            symptoms = "Madidilaw na kanto-kantong tagpi sa ibabaw ng dahon na may pinong puting amag sa ilalim sa malalamig at mahalumigmig na umaga.",
            prevention = "Drip irrigation upang manatiling tuyo ang dahon; maayos na agwat ng tanim; pag-iwas sa pagdidilig sa hapon.",
            treatment = "Gupitin ang mga apektadong lumang dahon; mag-spray ng baking soda solution (1 tsp sa 2L tubig) na may kaunting sabon.",
            explanation = ExplanationData(
                title = "Downy Mildew sa Litsugas",
                summary = "Fungal infection na karaniwan sa malamig at mahamog na kabundukan ng Benguet.",
                details = listOf(
                    "Pinipigilan ng mga ugat ng dahon ang hugis ng tagpi.",
                    "Ang tuyong dahon bago dumilim ang pinakamahusay na proteksyon."
                )
            )
        )
    ),
    seasonalCalendar = listOf(
        SeasonalCalendarMonth("Ene", "Mataas na Ani (Benguet)", "Malamig na simoy ng hangin; pinakamagandang kalidad ng malulutong na rosette; peak season.", Color(0xFF2E7D32)),
        SeasonalCalendarMonth("Peb", "Pangunahing Anihan", "Tugatog ng ani ng litsugas sa Benguet; huling batch ng tanim sa kapatagan.", Color(0xFF2E7D32)),
        SeasonalCalendarMonth("Mar", "Pagtatanim sa Kabundukan", "Highland production; kailangan ang 30% shade net sa katanghalian laban sa bolting.", Color(0xFF1565C0)),
        SeasonalCalendarMonth("Abr", "Patubig sa Tag-init", "Mataas na pangangailangan sa tubig; highland only (Atok, Buguias, Bukidnon).", Color(0xFFE65100)),
        SeasonalCalendarMonth("Mayo", "Pagsisimula ng Ulan", "Paghahanda ng matataas na 20 cm raised beds upang maiwasan ang bottom rot sa tag-ulan.", Color(0xFF1565C0)),
        SeasonalCalendarMonth("Hun", "Tag-ulan sa Kabundukan", "Mataas ang presyo sa palengke; maglagay ng plastic rain shelter laban sa bacterial soft rot.", Color(0xFF2E7D32)),
        SeasonalCalendarMonth("Hul", "Panahon ng Habagat", "Protektadong greenhouse o rain tunnel production upang hindi mabugbog ang pinong dahon.", Color(0xFFE65100)),
        SeasonalCalendarMonth("Ago", "Off-Season High Price", "Mataas ang halaga ng sariwang salad greens sa Metro Manila; kailangan ang drainage canals.", Color(0xFF2E7D32)),
        SeasonalCalendarMonth("Set", "Paghahanda sa Punlaan", "Paghahasik sa 104-hole trays para sa paparating na malamig na simoy ng amihan.", Color(0xFF1565C0)),
        SeasonalCalendarMonth("Okt", "Malawakang Lipat-Tanim", "Simula ng pagtatanim sa lowlands (Nobyembre hanggang Pebrero cropping).", Color(0xFF2E7D32)),
        SeasonalCalendarMonth("Nob", "Simula ng Amihan", "Peak planting month sa buong Pilipinas; mainam na 15–24°C para sa matamis na dahon.", Color(0xFF2E7D32)),
        SeasonalCalendarMonth("Dis", "Tugatog ng Produksyon", "Saganang ani ng sariwang salad greens para sa Pasko; matamis at walang kapaitan.", Color(0xFF2E7D32))
    ),
    careGuide = listOf(
        HowToSectionItem(
            title = "Wastong Patubig sa Mababaw na Ugat",
            summary = "Panatilihing mamasa-masa ang 10 cm na ibabaw ng lupa upang maiwasan ang tip-burn at mapait na dagta.",
            steps = listOf(
                "Diligan araw-araw o bawat 2 araw tuwing maagang umaga (6:00–7:30 AM).",
                "Iwasang basain ang mga dahon bago magtakipsilim upang hindi kapitan ng downy mildew at soft rot.",
                "Maglagay ng 3 cm na malinis na straw mulch upang mapanatiling malamig ang lupa sa tanghali."
            )
        ),
        HowToSectionItem(
            title = "Pagkontrol sa Init at Bolting Management",
            summary = "Paggamit ng shade nets upang mapigilan ang pamumulaklak at kapaitan sa init.",
            steps = listOf(
                "Magtayo ng 30–50% black shade net mula 10:30 AM hanggang 2:30 PM kapag lumagpas sa 28°C ang init.",
                "Pumili ng heat-tolerant varieties tulad ng General (Romaine) sa kapatagan ng Pilipinas.",
                "Anihin agad ang mga dahon kapag napansing tumataas na ang gitnang tangkay bago tuluyang pumait."
            )
        )
    ),
    growthTimeline = listOf(
        TimelineStageItem(
            stageName = "1. Pagsibol (Sprout / Germination)",
            durationDays = "3–7 Araw",
            description = "Pagsibol ng munting binhi sa 0.5 cm lalim sa seedling tray; kailangan ang banayad na kahalumigmig at liwanag."
        ),
        TimelineStageItem(
            stageName = "2. Punla sa Nursery Tray",
            durationDays = "20–25 Araw",
            description = "Pagbuo ng 3 hanggang 4 na tunay na dahon; pagpapatigas ng punla (hardening) sa banayad na araw 4 na araw bago i-transplant."
        ),
        TimelineStageItem(
            stageName = "3. Paglaki ng Rosette (Vegetative Stage)",
            durationDays = "25–40 Araw (DAT)",
            description = "Mabilisang paglapad at pagbilog ng malulutong na dahon; panahon ng regular na patubig at light side-dress ng compost/tea."
        ),
        TimelineStageItem(
            stageName = "4. Ganap na Ulo at Anihan (Harvest Maturity)",
            durationDays = "50–70 Araw",
            description = "Siksik, matambok, at malutong ang buong rosette (300–600 g); maagang umaga pinuputol sa base gamit ang matalas na kutsilyo."
        )
    ),
    howTos = listOf(
        HowToSectionItem(
            title = "Paano Maglipat-Tanim ng Litsugas",
            summary = "Hakbang-hakbang na paraan upang maiwasan ang transplant shock sa litsugas.",
            steps = listOf(
                "Diligan ang seedling tray 1 oras bago kumuha ng punla upang buo ang root plug.",
                "Maglipat-tanim tuwing alas-4:00 ng hapon o sa maulap na araw upang hindi malanta sa init.",
                "Ibaon sa dating lalim ng seedling plug; huwag lulubog ang gitnang growing point.",
                "Panatilihin ang 20–25 cm pagitan sa hanay at diligan agad sa base ng puno."
            )
        ),
        HowToSectionItem(
            title = "Cut-and-Come-Again Harvesting Protocol",
            summary = "Tuloy-tuloy na pagpitas ng panlabas na dahon para sa home at backyard gardeners.",
            steps = listOf(
                "Pitasin ang 3–4 na pinakamalaking panlabas na dahon gamit ang malinis na gunting.",
                "Iwanan ang gitnang 4–6 na dahon at ang lumalaking puso upang magpatuloy ang pagtubo.",
                "Diligan at lagyan ng kaunting vermitea pagkatapos mamitas upang lumaki ang susunod na batch."
            )
        )
    ),
    faqs = listOf(
        FaqItem(
            question = "Bakit nagiging mapait ang dahon ng litsugas?",
            answer = "Ang kapaitan ay sanhi ng pamumuo ng mapait na gatas (lactucarium) kapag na-stress ang halaman sa matinding init (>28–30°C), tagtuyot sa mababaw na ugat, o kapag nagsimula na itong mamulaklak (bolting). Panatilihing malamig ang lupa gamit ang mulch, magdilig nang regular, at anihin sa maagang umaga bago lumaki ang tangkay."
        ),
        FaqItem(
            question = "Maaari bang itanim ang litsugas sa kapatagan (lowlands) ng Pilipinas?",
            answer = "Oo, ngunit pinakamainam ito sa malalamig na buwan mula Nobyembre hanggang Pebrero. Kung itatanim sa tag-init, gumamit ng mga heat-tolerant varieties tulad ng 'General' (Romaine), 'Green Span', o loose-leaf types, at maglagay ng 40% shade net sa katanghalian."
        ),
        FaqItem(
            question = "Ano ang pamamaraang 'Cut-and-Come-Again'?",
            answer = "Ito ay ang pagpitas lamang sa 3–4 na pinakamalaking panlabas na dahon habang iniiwan ang gitnang puso at usbong upang magpatuloy lumaki. Nagbibigay-daan ito sa tuloy-tuloy na anihan sa parehong puno tuwing 4–6 na araw sa loob ng halos isang buwan sa mga backyard gardens."
        ),
        FaqItem(
            question = "Paano maiiwasan ang tip-burn (sunog na gilid ng batang dahon)?",
            answer = "Ang tip-burn ay dulot ng kawalan ng calcium sa mabilis na lumalaking dahon sa init. Panatilihing pantay ang patubig upang tuloy-tuloy ang paghigop ng ugat sa natural na calcium, i-spray ng fermented eggshell and vinegar (WCA) tuwing hapon, at maglagay ng shade net sa katanghalian."
        ),
        FaqItem(
            question = "Gaano katagal tumatagal ang sariwang litsugas pagkatapos anihin?",
            answer = "Sa normal na temperatura sa Pilipinas, tumatagal lamang ito ng 1 hanggang 2 araw bago malanta. Kung ilalagay sa refrigerator o cold storage sa temperaturang 0–4°C na may katamtamang kahalumigmig sa loob ng paper towel o perforated plastic, mananatili itong malutong nang 1 hanggang 2 linggo."
        )
    ),
    nutritionTable = NutritionTableData(
        servingSize = "100 g (Sariwang Litsugas)",
        calories = "15 kcal",
        macroNutrients = listOf(
            NutritionRowData("Protina (Protein)", "1.36 g", "2.7%"),
            NutritionRowData("Kabuuang Taba (Total Fat)", "0.15 g", "0.2%"),
            NutritionRowData("Carbohydrates", "2.87 g", "2.2%"),
            NutritionRowData("Dietary Fiber (Hibla)", "1.3 g", "5.2%"),
            NutritionRowData("Asukal (Natural Sugars)", "0.78 g", "—"),
            NutritionRowData("Tubig (Water Content)", "95.0 g", "—")
        ),
        microNutrients = listOf(
            NutritionRowData("Bitamina A (Beta-Carotene)", "7405 IU", "148%"),
            NutritionRowData("Bitamina K1 (Phylloquinone)", "126.3 mcg", "105%"),
            NutritionRowData("Bitamina C (Ascorbic Acid)", "9.2 mg", "15%"),
            NutritionRowData("Folate (Bitamina B9)", "38 mcg", "10%"),
            NutritionRowData("Iron (Fe)", "0.86 mg", "5%"),
            NutritionRowData("Calcium (Ca)", "36 mg", "4%"),
            NutritionRowData("Potassium (K)", "194 mg", "4%"),
            NutritionRowData("Magnesium (Mg)", "13 mg", "3%")
        )
    ),
    exploreArticles = listOf(
        ArticleItem("Gabay sa Modernong Taniman ng Litsugas sa Benguet at Lowlands", "Mga pamamaraan ng mga magsasaka sa La Trinidad para sa 15–20 toneladang malutong na ani gamit ang raised beds at shade netting.", "6 min basahin"),
        ArticleItem("Pagpigil sa Bolting at Pagpapanatili ng Tamis ng Salad Greens", "Siyentipikong paliwanag kung paano kontrolin ang temperatura at patubig upang hindi pumait ang dagta ng litsugas.", "5 min basahin"),
        ArticleItem("Sining ng Organikong Peste Control sa Sariwang Gulay", "Ligtas na pamuksa sa aphids at harabas gamit ang insecticidal soap, neem, at companion planting na walang kemikal.", "5 min basahin")
    )
)
