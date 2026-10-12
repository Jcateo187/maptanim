package com.maptanim.app.features.farm.dialogs

import androidx.compose.ui.graphics.Color
import com.maptanim.app.R

/**
 * Data models for the comprehensive Vegetable Encyclopedia Dossier.
 * Specially curated and localized for Philippine agricultural and gardening conditions:
 * Tropical lowland & highland agro-climatic zones, Tag-araw (Dry season) & Tag-ulan (Wet season)
 * calendar rhythms, East-West Seed / DA-BPI hybrid and heirloom varieties, local pest/disease profiles
 * (Bacterial Wilt, TYLCV 'Kulot', Harabas, Nematodes), and indigenous farming techniques (CRH, FPJ, CalPhos).
 */
data class CarouselSlide(
    val title: String,
    val subtitle: String,
    val stage: String,
    val badgeColor: Color = Color(0xFF2E7D32),
    val drawableResId: Int? = null
)

data class QuickInfoItem(
    val key: String,
    val label: String,
    val value: String,
    val infoNote: String? = null,
    val isAlert: Boolean = false,
    val explanation: ExplanationData
)

data class PlantRelationItem(
    val name: String,
    val scientificName: String? = null,
    val isBeneficial: Boolean,
    val role: String,
    val explanation: ExplanationData
)

data class NutritionBadgeItem(
    val code: String,
    val name: String,
    val percentageDaily: String,
    val role: String,
    val explanation: ExplanationData
)

data class PestDiseaseItem(
    val name: String,
    val scientificName: String? = null,
    val severity: String,
    val symptoms: String,
    val prevention: String,
    val treatment: String,
    val explanation: ExplanationData
)

data class CritterItem(
    val name: String,
    val role: String,
    val attractsWith: String,
    val explanation: ExplanationData
)

data class GuideSectionItem(
    val id: String,
    val title: String,
    val summary: String,
    val fullContent: String,
    val tips: List<String> = emptyList()
)

data class VarietyInfo(
    val name: String,
    val producer: String,
    val type: String,
    val badge: String,
    val description: String
)

data class PlantingCalendarData(
    val indoorRange: String,
    val outdoorRange: String,
    val harvestRange: String,
    val indoorLabel: String = "Punlaan (Seedbed)",
    val outdoorLabel: String = "Lipat-Tanim (Field)",
    val harvestLabel: String = "Ani (Harvest)",
    val plantTypeNote: String = "Pangunahing Panahon: Tag-araw (Okt–Abr); Off-season na may Rain Shelter (Mayo–Ago)",
    val monthsSchedule: List<Pair<String, String>>
)

data class TimelineStageItem(
    val dayRange: String,
    val stageName: String,
    val description: String,
    val keyAction: String
)

data class SoilPrepData(
    val phRange: String,
    val soilTypes: String,
    val recommendation: String
)

data class LocationDifficultyData(
    val difficulty: String,
    val hardinessZone: String,
    val tempRange: String,
    val sunlight: String,
    val suitableAlso: String
)

data class HowToSectionItem(
    val title: String,
    val steps: List<String>,
    val tips: List<String>,
    val safety: String? = null
)

data class FaqItem(
    val question: String,
    val answer: String
)

data class NutritionTableData(
    val portionSize: String,
    val calories: String,
    val carbs: String,
    val carbsPct: String,
    val sugar: String,
    val fiber: String,
    val fiberPct: String,
    val sucrose: String,
    val summary: String
)

data class ArticleItem(
    val title: String,
    val category: String,
    val excerpt: String
)

data class ExplanationData(
    val title: String,
    val category: String,
    val description: String,
    val details: List<Pair<String, String>> = emptyList(),
    val growerTip: String? = null
)

data class CropEncyclopedia(
    val cropName: String,
    val localName: String,
    val scientificName: String,
    val category: String,
    val family: String,
    val overview: String,
    val selectedVariety: VarietyInfo,
    val otherVarieties: List<String>,
    val carouselSlides: List<CarouselSlide>,
    val quickInfo: List<QuickInfoItem>,
    val companionPlants: List<PlantRelationItem>,
    val combativePlants: List<PlantRelationItem>,
    val nutritionBadges: List<NutritionBadgeItem>,
    val pests: List<PestDiseaseItem>,
    val diseases: List<PestDiseaseItem>,
    val beneficialCritters: List<CritterItem>,
    val growingGuides: List<GuideSectionItem>,
    val plantingCalendar: PlantingCalendarData,
    val locationData: LocationDifficultyData,
    val soilPrep: SoilPrepData,
    val growthTimeline: List<TimelineStageItem>,
    val howTos: List<HowToSectionItem>,
    val faqs: List<FaqItem>,
    val nutritionTable: NutritionTableData,
    val exploreArticles: List<ArticleItem>
)

/**
 * Registry providing rich, research-based encyclopedia data for Philippine vegetable growers.
 */
object CropEncyclopediaRegistry {

    fun getCropEncyclopedia(cropName: String): CropEncyclopedia {
        val clean = cropName.lowercase().replace(" ", "").replace("_", "").replace("-", "")
        return when {
            clean.contains("tomato") || clean.contains("kamatis") -> tomatoData
            clean.contains("carrot") || clean.contains("karot") -> carrotData
            clean.contains("eggplant") || clean.contains("talong") -> eggplantData
            clean.contains("pechay") || clean.contains("bokchoy") -> pechayData
            clean.contains("sili") || clean.contains("pepper") || clean.contains("chili") -> siliData
            clean.contains("pumpkin") || clean.contains("squash") || clean.contains("kalabasa") -> pumpkinData
            else -> tomatoData.copy(
                cropName = cropName.replaceFirstChar { it.uppercase() },
                localName = cropName.replaceFirstChar { it.uppercase() }
            )
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════════
    // 1. TOMATOES / KAMATIS (Solanum lycopersicum) — Philippine Agricultural Data
    // ═══════════════════════════════════════════════════════════════════════════════
    val tomatoData = CropEncyclopedia(
        cropName = "Tomatoes",
        localName = "Kamatis (Solanum lycopersicum)",
        scientificName = "Solanum lycopersicum",
        category = "Nightshades (Solanaceae)",
        family = "Solanaceae",
        overview = "Ang kamatis (Solanum lycopersicum) ay isa sa pinakamahalagang high-value commercial vegetables sa Pilipinas. Bagamat madaling patubuin sa tropikal na klima, ang tagumpay ng ani ay lubos na nakasalalay sa pagpili ng barayting angkop sa lokal na panahon at may resistensya sa mga karaniwang sakit. Sa Pilipinas, nahahati ang mga barayti sa determinate ('bush-type' tulad ng Diamante Max at Rosanna) na lumalaki hanggang 1–1.5 metro at sabay-sabay kung mamunga, at indeterminate ('vine-type' tulad ng mga cherry tomato at highland greenhouse varieties sa Benguet at Bukidnon) na nangangailangan ng matibay na balag at tulos na kawayan dahil tuloy-tuloy ang paghaba ng baging hanggang matapos ang ani.\n\nAng pangunahing panahon ng pagtatanim sa kapatagan (lowland) ay sa Tag-araw (Oktubre hanggang Marso), kung kailan mas mababa ang halumigmig at naiiwasan ang mapaminsalang Lanta-Bakterya (Ralstonia solanacearum) at mga fungal blight. Ang 'off-season' naman (Mayo hanggang Agosto) ay isinasagawa sa ilalim ng rain shelter o UV plastic tunnel upang maprotektahan ang bulaklak sa malalakas na buhos ng ulan at makakuha ng pinakamataas na presyo sa palengke. Inirerekomenda ng DA-BPI at East-West Seed ang paggamit ng certified hybrid seeds na may natural na tibay sa Tomato Yellow Leaf Curl Virus (TYLCV o 'Kulot').",
        selectedVariety = VarietyInfo(
            name = "Diamante Max F1",
            producer = "East-West Seed Philippines",
            type = "Semi-Determinate Hybrid",
            badge = "F1",
            description = "Ang nangungunang hybrid na kamatis sa buong Pilipinas. Espesyal na binuo para sa mainit at mahalumigmig na kapatagan. May mataas na resistensya laban sa Bacterial Wilt (Ralstonia) at Tomato Yellow Leaf Curl Virus (TYLCV o 'Kulot'). Nagbubunga ng masinsin at matitigas na bungang hugis-itlog (60–70g bawat isa) na may makapal na laman, kaya't matagal mabulok at angkop sa malayuang biyahe patungong palengke."
        ),
        otherVarieties = listOf(
            "Diamante Max F1 (East-West Seed)",
            "Apollo (BPI-UPLB Heat-Tolerant)",
            "Rosanna (UPLB Determinate)",
            "Marimar F1 (High-Yield Commercial)",
            "Kamatis Tagalog / Native Kamatis",
            "Sweet Princess F1 (Cherry Tomato)",
            "Red Jewel (High-Brix Cherry)",
            "Batac Native (Ilocos Drought-Tolerant)",
            "Roma VF (Condor Processing Tomato)"
        ),
        carouselSlides = listOf(
            CarouselSlide("Punlaan sa Seedling Tray", "21–25 araw sa 104-hole tray gamit ang 1:1:1 lupa, vermicast at CRH (Carbonized Rice Hull)", "Yugto ng Punla", Color(0xFF2E7D32), R.drawable.ph_tomato_seedlings),
            CarouselSlide("Tulos, Balag at Bulaklak", "Pagtulos ng kawayan (1.5m), A-frame balag, at pag-usbong ng matingkad na dilaw na bulaklak", "Pamumulaklak", Color(0xFFF57F17), R.drawable.ph_tomato_flowers),
            CarouselSlide("Puno ng Bunga sa Balag", "Masinsing kumpol ng makikintab at matitigas na bungang hugis-itlog (Diamante Max F1)", "Pamumunga", Color(0xFF1B5E20), R.drawable.ph_tomato_hero),
            CarouselSlide("Ani sa Manibalang Stage", "Pitasin sa 'breaker stage' sa katutubong bilao; nagtatagal ng 1–2 linggo sa temperatura ng kusina", "Handa nang Anihin", Color(0xFFD32F2F), R.drawable.ph_tomato_harvest)
        ),
        quickInfo = listOf(
            QuickInfoItem("spacing", "Spacing", "40–50 cm", null, false, ExplanationData("Plant Spacing sa Pilipinas", "DENSIDAD SA TANIMAN", "Sa tradisyunal na kama o tudling sa Pilipinas, 40–50 cm ang pagitan ng bawat puno (hills) at 75–100 cm ang pagitan ng tudling (furrows). Sa urban gardening, magtanim ng 1 puno bawat 5-gallon na timba o 1 square foot.", listOf("Pagitan ng Puno" to "40–50 cm (hills)", "Pagitan ng Tudling" to "75–100 cm (furrows)", "Urban Containers" to "1 puno bawat 20-litro na paso"), "Panatilihin ang sapat na distansya upang makadaloy ang hangin at maiwasan ang fungal blights sa panahon ng tag-ulan.")),
            QuickInfoItem("depth", "Depth", "0.5 – 1 cm", null, false, ExplanationData("Lalim ng Pagpupunla", "PUNLAAN", "Ipunla ang binhi sa lalim na 0.5 hanggang 1 sentimetro sa seedling tray o punlaan gamit ang pinaghalong compost, garden soil, at Carbonized Rice Hull (CRH).", listOf("Lalim ng Binhi" to "0.5–1.0 cm (1/4 in)", "Media" to "1:1:1 Soil, Vermicast, at CRH"), "Dahan-dahang tabunan at diligan gamit ang pinong sprayer upang hindi maanod ang maliliit na binhi.")),
            QuickInfoItem("sun", "Sun", "Full Sun", null, false, ExplanationData("Sikat ng Araw", "SINA-AG NG ARAW", "Kailangan ng kamatis ng 6 hanggang 8 oras ng direktang sikat ng araw araw-araw para sa tuloy-tuloy na pamumulaklak at matamis na bunga.", listOf("Minimum na Araw" to "6 na oras", "Pinakamainam" to "8+ oras buong araw"), "Tuwing matinding init ng Marso–Mayo (>35°C), nakatutulong ang 30% netting sa tanghali upang maiwasan ang pagkalagas ng bulaklak.")),
            QuickInfoItem("water", "Water", "1–1.5 L/araw", "ⓘ", false, ExplanationData("Pagpapatubig sa Tropiko", "IRIGASYON", "Magdilig ng 1 hanggang 1.5 litro bawat puno 3 beses sa isang linggo o araw-araw tuwing matinding tag-init. Diligan lamang ang punong ugat at iwasang mabasa ang dahon.", listOf("Dalas ng Pagdidilig" to "Araw-araw sa tag-init; bawas sa tag-ulan", "Paraan" to "Sa ugat lamang (Drip o pandilig sa base)"), "Ang pabago-bagong pagdidilig ay sanhi ng bitak sa bunga at blossom end rot. Maglagay ng mulch na dayami o silver-black plastic.")),
            QuickInfoItem("season", "Season", "Tag-araw (Dry)", "ⓘ", false, ExplanationData("Panahon ng Pagtatanim", "KLIMA AT PANAHON", "Ang pangunahing panahon ng pagtatanim sa Pilipinas ay Tag-araw (Oktubre hanggang Marso). Para sa tag-ulan (Mayo–Agosto), magtanim sa ilalim ng rain shelter o elevated beds upang hindi malunod.", listOf("Pangunahing Panahon" to "Oktubre hanggang Marso (Tag-araw)", "Off-Season" to "Mayo hanggang Agosto (May rain shelter)"), "Umiwas sa pagtatanim sa panahon ng sunod-sunod na bagyo kung walang maayos na drainage at pananggalang sa hangin.")),
            QuickInfoItem("frost", "Flooding", "Di-bahaing Lupa", "!", true, ExplanationData("Sensitibo sa Baha at Tubig", "KATATAGAN SA TUBIG", "Lubhang sensitibo ang kamatis sa pagkababad sa tubig. Ang pagkakababad ng ugat sa baha sa loob ng 24 oras ay nagdudulot ng pagkabulok ng ugat at mabilis na pagkamatay dahil sa Bacterial Wilt (Ralstonia).", listOf("Baha / Waterlogging" to "Hindi tatagal nang 24 oras", "Lunas" to "Gumawa ng mataas na kamada (20–30 cm raised beds)"), "Siguraduhing may malalim na kanal sa paligid ng taniman upang mabilis umagos ang tubig-ulan.")),
            QuickInfoItem("height", "Height", "1 – 2.5 m*", "I", false, ExplanationData("Taas ng Halaman at Balag", "SUKAT AT TULOS", "Ang mga semi-determinate bush (tulad ng Diamante Max) ay umaabot sa 1 hanggang 1.5 metro. Ang indeterminate vine varieties sa highland ay umaabot sa 2 hanggang 2.5 metro.", listOf("Semi-Determinate" to "1.0–1.5 metro (Kailangan ng tulos)", "Indeterminate" to "2.0–2.5+ metro (Kailangan ng matataas na balag)"), "Lagyan agad ng tulos na kawayan pagkalipas ng 2 linggo mula sa paglipat-tanim upang hindi dumapa ang sanga kapag namunga.")),
            QuickInfoItem("germ", "Germination", "5–10 araw", null, false, ExplanationData("Pagsibol ng Binhi", "PAGSIBOL", "Mabilis sumibol ang binhi sa mainit na klima ng Pilipinas sa loob ng 5 hanggang 10 araw kapag sapat ang basa ng punlaan at mahangin ang paligid.", listOf("Panahon ng Pagsibol" to "5 hanggang 10 araw", "Lugar" to "Nalililimang punlaan"), "Ilabas sa umagang araw ang punla sa ika-3 araw pagkasibol upang hindi tumangkad nang payat (legginess).")),
            QuickInfoItem("germ_temp", "Germination Temp", "24°–32°C", null, false, ExplanationData("Temperatura ng Pagsibol", "TEMPERATURA", "Ang normal na temperatura sa Pilipinas (24°C–32°C) ay perpekto para sa mabilis at pantay na pagsibol ng binhi ng kamatis nang hindi na kailangan ng heat mat.", listOf("Mainam na Temp" to "24°C hanggang 30°C", "Klima" to "Warm Tropical Lowland"), "Huwag ibilad sa nakapapasong tanghaling tapat ang punlaan habang hindi pa sumisibol.")),
            QuickInfoItem("harvest", "Sprout to Harvest", "60–75 DAP", null, false, ExplanationData("Araw Bago Mag-ani", "KABUOANG SIKLO", "Mula sa paglipat-tanim (DAP - Days After Planting), magsisimulang mamitas sa loob ng 60 hanggang 75 araw. Nagpapatuloy ang ani sa loob ng 1 hanggang 2 buwan.", listOf("Mula Lipat-Tanim" to "60–75 araw (DAP)", "Panahon ng Pamumitas" to "Bawat 3–4 araw sa loob ng 6–8 linggo"), "Pitasin habang manibalang (breaker stage) pa lamang upang mas tumagal ang shelf-life sa palengke.")),
            QuickInfoItem("ph", "Soil pH", "6.0 – 6.8 (Neutral)", "PH", false, ExplanationData("Asido ng Lupa", "KONDISYON NG LUPA", "Gusto ng kamatis ang bahagyang maasim hanggang neutral na lupa (pH 6.0–6.8). Kung acidic ang lupa (pH < 5.8), maglagay ng apog (agricultural lime o dolomite) 2 linggo bago maglipat-tanim.", listOf("Tamang pH" to "6.0 hanggang 6.8", "Paggamot" to "Agricultural lime o dolomite (1–2 tonelada/ha)"), "Ang tamang pH ay nagbibigay-daan sa ugat upang mahigop ang calcium at phosphorus laban sa blossom end rot.")),
            QuickInfoItem("seedling", "Seedling ID", "2–4 True Leaves", null, false, ExplanationData("Pagkilala sa Punla", "PAGLILIPAT-TANIM", "Handa nang ilipat-tanim ang punla kapag mayroon na itong 2 hanggang 4 na tunay na dahon (may ngipin ang gilid), humigit-kumulang 21 hanggang 25 araw mula sa pagpupunla sa tray.", listOf("Dahon" to "2 hanggang 4 na tunay na dahon", "Taas ng Punla" to "10–15 cm na may matibay na tangkay"), "Sanayin sa araw (hardening) ang mga punla sa loob ng 5–7 araw bago tuluyang itanim sa bukid o paso."))
        ),
        companionPlants = listOf(
            PlantRelationItem("Marigold (Amarilyo)", "Tagetes patula", true, "Natural na nematicide at panaboy sa whiteflies", ExplanationData("Amarilyo (Marigold)", "HALAMANG KASAMA", "Naglalabas ang ugat ng amarilyo ng natural na kemikal (alpha-terthienyl) na pumapatay sa root-knot nematodes (bulate sa ugat) sa lupa. Ang amoy nito ay nagtataboy din sa whiteflies (puting langaw).", listOf("Pangunahing Pakinabang" to "Pamuksa sa nematodes sa lupa", "Peste na Itinataboy" to "Puting langaw, aphids, thrips"), "Magtanim ng amarilyo sa mga sulok at dulo ng bawat kama ng kamatis.")),
            PlantRelationItem("Basil (Sulasi / Balanoy)", "Ocimum basilicum", true, "Mabangong pantaboy sa thrips at uod", ExplanationData("Sulasi / Basil", "HALAMANG KASAMA", "Ang taglay nitong natural na eugenol at linalool ay lumilito sa pang-amoy ng mga uod at thrips habang pinagaganda ang lasa ng kamatis.", listOf("Pakinabang" to "Natural na insecticide at repellent", "Peste na Itinataboy" to "Thrips, hornworms, fruit fly"), "Magtanim ng sulasi sa pagitan ng bawat dalawang puno ng kamatis.")),
            PlantRelationItem("Sibuyas (Onion / Shallots)", "Allium cepa", true, "Matapang na amoy panlaban sa aphids", ExplanationData("Sibuyas / Shallots", "HALAMANG KASAMA", "Ang sulfur compounds at matapang na amoy ng sibuyas ay nagtataboy sa aphids, spider mites, at mga sumisipsip na insekto.", listOf("Pakinabang" to "Pangharang sa kuto ng halaman", "Peste" to "Aphids, armyworms"), "Itanim sa paligid ng kama ng kamatis nang may 10 cm na layo sa tangkay.")),
            PlantRelationItem("Bawang (Garlic)", "Allium sativum", true, "Panlaban sa fungal pathogens at amag", ExplanationData("Bawang", "HALAMANG KASAMA", "Kilalang natural na fungicide ang allicin ng bawang na pumipigil sa pamumuo ng fungal spores sa basang lupa.", listOf("Pakinabang" to "Natural na anti-fungal at insect deterrent", "Peste" to "Spider mites, beetles"), "Maaari ding gawing natural na spray ang dinurog na bawang at sili.")),
            PlantRelationItem("Tanglad (Lemongrass)", "Cymbopogon citratus", true, "Citronella aroma na panaboy sa borers", ExplanationData("Tanglad", "HALAMANG KASAMA", "Ang malakas na amoy ng citronella ay nagtataboy sa mga gamugamo (moths) na nangingitlog ng harabas at fruit borer.", listOf("Pakinabang" to "Moth repellent at soil stabilizer", "Peste" to "Fruit borer, cutworm adults"), "Itanim bilang bakod sa paligid ng taniman.")),
            PlantRelationItem("Pechay", "Brassica rapa", true, "Mababaw ang ugat at natural na trap crop", ExplanationData("Pechay", "HALAMANG KASAMA", "Mabilis lumaki (30 araw) at mababaw ang ugat kaya hindi nakikipag-agawan ng sustansya; nagsisilbing alay na pananim sa flea beetles.", listOf("Pakinabang" to "Living cover at panandaliang ani", "Siklo" to "Maaaring anihin bago lumaki ang kamatis"), "Ipunla sa pagitan ng mga hanay ng kamatis habang maliit pa ang punla.")),
            PlantRelationItem("Mustasa", "Brassica juncea", true, "Biofumigant sa lupa bago mag-ani", ExplanationData("Mustasa", "HALAMANG KASAMA", "Umaakit sa mga insekto palayo sa kamatis, at kapag ibinaon sa lupa ay gumaganap bilang biofumigant laban sa masasamang mikrobyo.", listOf("Pakinabang" to "Trap crop at soil biofumigation", "Peste" to "Flea beetles, aphids"), "Itanim sa dulo ng kama bilang pain sa mga insekto.")),
            PlantRelationItem("Lettuce", "Lactuca sativa", true, "Living mulch na nagpapanatili ng basa sa lupa", ExplanationData("Lettuce", "HALAMANG KASAMA", "Tinatabunan ang ibabaw ng lupa upang maiwasan ang mabilis na pagsingaw ng tubig sa ilalim ng mainit na araw ng Pilipinas.", listOf("Pakinabang" to "Living mulch at panangga sa damo", "Synergy" to "Nabibigyan ng bahagyang lilim ng kamatis"), "Itanim sa paanan ng tulos.")),
            PlantRelationItem("Oregano", "Coleus aromaticus", true, "Mabangong pananggalang sa gumagapang na peste", ExplanationData("Oregano", "HALAMANG KASAMA", "Makapal na halamang-gamot na naglalabas ng carvacrol na kinatatakutan ng mga uod at uwang.", listOf("Pakinabang" to "Pest repellent groundcover", "Amoy" to "Matapang na essential oil"), "Itanim sa gilid ng mga plot o paso.")),
            PlantRelationItem("Alyssum", "Lobularia maritima", true, "Umaakit sa hoverflies na kumakain ng aphids", ExplanationData("Sweet Alyssum", "HALAMANG KASAMA", "May maliliit na bulaklak na paboritong kainan ng mga hoverfly (syrphid fly) na ang uod ay kumakain ng libu-libong aphids.", listOf("Pakinabang" to "Attractor ng predatory insects", "Predators" to "Hoverflies, parasitic micro-wasps"), "Mabuting groundcover sa paligid ng taniman.")),
            PlantRelationItem("Nasturtium", "Tropaeolum majus", true, "Alay na pananim (sacrificial trap crop)", ExplanationData("Nasturtium", "HALAMANG KASAMA", "Inaakit ang mga aphids palayo sa kamatis upang sa nasturtium sila magtipon kung saan madali silang puksain.", listOf("Pakinabang" to "Trap crop at edible flowers", "Peste" to "Aphids, whiteflies"), "Itanim 1–2 metro ang layo sa kamatis.")),
            PlantRelationItem("Borage", "Borago officinalis", true, "Umaakit sa mga pukyutan para sa polinasyon", ExplanationData("Borage", "HALAMANG KASAMA", "Ang asul na bulaklak nito ay paborito ng mga katutubong bubuyog (Apis cerana) na nagpapataas ng fruit set sa kamatis.", listOf("Pakinabang" to "Pollinator magnet", "Epekto" to "Hanggang 30% dagdag sa dami ng bunga"), "Magtanim sa bawat dulo ng kamada."))
        ),
        combativePlants = listOf(
            PlantRelationItem("Talong (Eggplant)", "Solanum melongena", false, "Magkaparehong pamilya; nagkakalat ng Bacterial Wilt at Fruit Borer", ExplanationData("Talong (Incompatible)", "DI-DAPAT KATABI", "Kabilang sa pamilyang Solanaceae. Kung magkatabi, madaling kumalat ang Lanta-Bakterya (Ralstonia solanacearum), flea beetles, at fruit borer sa pagitan nila.", listOf("Peligro" to "Bacterial wilt at shared insect vectors", "Panuntunan" to "Huwag pagtabihin at huwag isunod sa pinagtaniman ng talong"), "Ihiwalay ng kama o maghintay ng 2 taon bago magpalit ng tanim sa parehong plot.")),
            PlantRelationItem("Patatas (Potato)", "Solanum tuberosum", false, "Nagkakalat ng nakamamatay na late blight at mosaic virus", ExplanationData("Patatas (Incompatible)", "DI-DAPAT KATABI", "Parehong kapamilya na lubhang madaling mahawahan ng late blight at viral mosaic diseases na sumisira sa buong taniman.", listOf("Peligro" to "Fungal blights at viral infections", "Epekto" to "Mabilis na pagkaagnas ng dahon"), "Ihiwalay ng hindi bababa sa 5 metro ang taniman ng patatas at kamatis.")),
            PlantRelationItem("Sili (Chili / Pepper)", "Capsicum annuum", false, "Nagpapasalin-salin ng thrips, whiteflies, at leaf curl virus", ExplanationData("Sili (Incompatible)", "DI-DAPAT KATABI", "Magkatulad na dinadapuan ng thrips at whiteflies na nagdadala ng nakamamatay na Tomato Yellow Leaf Curl Virus (TYLCV o Kulot).", listOf("Peligro" to "TYLCV vector transmission", "Agawan" to "Magkatulad na micronutrients"), "Iwasang magkatabi sa iisang kama; magtanim ng harang tulad ng mais o marigold sa pagitan.")),
            PlantRelationItem("Mais (Corn)", "Zea mays", false, "Ang corn earworm ay ang mismong tomato fruitworm (harabas)", ExplanationData("Mais (Incompatible)", "DI-DAPAT KATABI", "Ang uod sa mais (Helicoverpa zea / armigera) ay ang mismong harabas na sumisira at bumubutas sa bunga ng kamatis.", listOf("Peligro" to "Fruit borer multiplication", "Harang sa Araw" to "Tinatabunan ng matatangkad na mais ang sikat ng araw"), "Maglagay ng hindi bababa sa 5–10 metrong distansya sa pagitan ng mais at kamatis.")),
            PlantRelationItem("Repolyo (Cabbage)", "Brassica oleracea", false, "Matinding kaagaw sa sustansya (nitrogen) at calcium", ExplanationData("Repolyo (Incompatible)", "DI-DAPAT KATABI", "Malakas humigop ng nitrogen at calcium sa lupa ang repolyo, na nagiging dahilan ng mabagal na paglaki at blossom end rot sa kamatis.", listOf("Peligro" to "Nutrient depletion", "Peste" to "Umaakit ng mga uod na pumupunta sa kamatis"), "Itanim sa magkahiwalay na season o plot.")),
            PlantRelationItem("Koliplor (Cauliflower)", "Brassica oleracea var. botrytis", false, "Umaagaw ng boron at nagpapatamlay sa ugat ng kamatis", ExplanationData("Koliplor (Incompatible)", "DI-DAPAT KATABI", "Naglalabas ng glucosinolate residues na nagpapatamlay sa paglaki ng ugat ng kamatis.", listOf("Peligro" to "Allelopathic root inhibition", "Soil" to "Matinding agawan sa pataba"), "Ihiwalay sa ibang bahagi ng hardin.")),
            PlantRelationItem("Fennel", "Foeniculum vulgare", false, "Naglalabas ng kemikal na nakakalasong allelopathic sa kamatis", ExplanationData("Fennel (Incompatible)", "DI-DAPAT KATABI", "Ang ugat ng fennel ay naglalabas ng kemikal na nagpapatigil sa paglaki ng kamatis at maaaring pumatay sa punla.", listOf("Peligro" to "Allelopathic toxicity", "Resulta" to "Naninilaw at bansot na halaman"), "Itanim lamang ang fennel sa nakahiwalay na paso.")),
            PlantRelationItem("Kohlrabi", "Brassica oleracea", false, "Nagpapanatili ng flea beetles at umuubos ng pataba", ExplanationData("Kohlrabi (Incompatible)", "DI-DAPAT KATABI", "Umuubos ng potassium sa lupa at nag-aakit ng mga lumuluksong flea beetle.", listOf("Peligro" to "Shared pest load", "Epekto" to "Bawas sa laki ng bunga"), "Itanim sa ibang kama.")),
            PlantRelationItem("Kale", "Brassica oleracea var. sabellica", false, "Malalawak ang ugat na umuubos ng tubig sa tag-araw", ExplanationData("Kale (Incompatible)", "DI-DAPAT KATABI", "Umuubos ng moisture sa ibabaw ng lupa na nagiging sanhi ng dehydration sa kamatis sa gitna ng tag-araw.", listOf("Peligro" to "Moisture competition sa tag-init", "Epekto" to "Mabilis na pagkatuyo ng dahon"), "Ihiwalay ng plot."))
        ),
        nutritionBadges = listOf(
            NutritionBadgeItem("B9", "Folate (B9)", "7% DV", "Para sa paggawa ng selula at malusog na dugo", ExplanationData("Bitamina B9 (Folate)", "NUTRISYON", "Mahalaga para sa DNA synthesis, pagbuo ng pulang selula sa dugo, at kalusugan ng mga nagdadalang-tao.", listOf("Dami bawat 100g" to "15 mcg", "Pangunahing Gamit" to "Pangangalaga sa cardiovascular at nervous system"))),
            NutritionBadgeItem("C", "Bitamina C", "28% DV", "Pampalakas ng resistensya at pampakinis ng balat", ExplanationData("Bitamina C", "NUTRISYON", "Ang isang katamtamang kamatis ay nagbibigay ng halos sangkapat ng kailangang ascorbic acid araw-araw para labanan ang impeksyon.", listOf("Dami bawat 100g" to "14 mg (28% DV)", "Gamit" to "Antioxidant at collagen production"))),
            NutritionBadgeItem("K1", "Bitamina K1", "7% DV", "Para sa pamumuo ng dugo at tibay ng buto", ExplanationData("Bitamina K1", "NUTRISYON", "Tumutulong sa maayos na blood clotting at pagpapanatili ng calcium sa mga buto.", listOf("Dami bawat 100g" to "7.9 mcg", "Gamit" to "Bone mineralization at vascular integrity"))),
            NutritionBadgeItem("K", "Potassium", "5% DV", "Pang-kontrol sa presyon ng dugo at tibok ng puso", ExplanationData("Potassium", "NUTRISYON", "Tumutulong sa pagpapaluwag ng mga ugat at pagbabalanse ng fluid at electrolytes sa katawan.", listOf("Dami bawat 100g" to "237 mg", "Gamit" to "Presyon ng dugo at kalamnan"))),
            NutritionBadgeItem("LYC", "Lycopene", "Mataas", "Mabisang panlaban sa cancer at pamamaga", ExplanationData("Lycopene (Antioxidant)", "PHYTONUTRIENT", "Ang pulang kulay ng kamatis ay mula sa lycopene, isang napakalakas na antioxidant na nagpoprotekta sa puso at laban sa prostate cancer. Mas madaling ma-absorb kapag niluto na may kaunting mantika.", listOf("Bioavailability" to "Tumataas kapag ginisa o niluto", "Epekto" to "Proteksyon sa selula laban sa free radicals")))
        ),
        pests = listOf(
            PestDiseaseItem("Harabas sa Bunga (Fruit Borer)", "Helicoverpa armigera", "Malubha", "May butas ang bunga na may kasamang dumi (frass); nabubulok ang loob", "Magtanim ng marigold at basil sa paligid; iwasang itabi sa mais", "Mag-spray ng Bacillus thuringiensis (Bt) o Spinosad sa takip-silim", ExplanationData("Fruit Borer Control sa Pilipinas", "PESTE SA BULAKLAK AT BUNGA", "Ang Helicoverpa armigera (harabas sa bunga) ang pinakakaraniwang sumisira sa bunga ng kamatis sa Pilipinas. Bumabarena ang uod sa loob ng luntiang bunga.", listOf("Sintomas" to "May itim na butas malapit sa tangkay ng bunga", "Natural Control" to "Bacillus thuringiensis (Bt), Trichogramma chilonis wasps"), "Pitasin agad at ibaon ang mga apektadong bunga upang hindi lumipat ang uod sa ibang sanga.")),
            PestDiseaseItem("Puting Langaw (Whitefly)", "Bemisia tabaci", "Kritikal", "Maliliit na puting langaw sa ilalim ng dahon; tagapagdala ng TYLCV (Kulot)", "Dilaw na sticky trap; silver-black plastic mulch; lambat (fine mesh)", "Spray ng diluted neem oil solution o insecticidal soap tuwing umaga", ExplanationData("Whitefly Management sa Pilipinas", "TAGAPAGDALA NG SAKIT (VECTOR)", "Ang puting langaw ang sanhi ng pagkalat ng Tomato Yellow Leaf Curl Virus (Kulot) na sumisira sa buong taniman sa Luzon, Visayas, at Mindanao.", listOf("Sintomas" to "Nagkukulot at naninilaw ang mga usbong; may malagkit na honeydew", "Pangunahing Solusyon" to "Paggamit ng barayting resistant tulad ng Diamante Max F1"), "Maglagay ng dilaw na sticky traps (yellow traps) kada 5 metro upang mahuli ang mga lumilipad na langaw.")),
            PestDiseaseItem("Harabas sa Punla (Cutworm)", "Spodoptera litura", "Mataas", "Napuputol ang tangkay ng bagong lipat na punla sa pantay ng lupa", "Lagyan ng 2-pulgadang karton o plastic ring ang tangkay; linisin ang damo", "Maghanap gamit ang flashlight sa gabi; maglagay ng abo sa paligid ng puno", ExplanationData("Cutworm Defense sa Taniman", "PESTE SA LUPA", "Nananahan sa ilalim ng lupa ang uod sa araw at pinuputol ang tangkay ng mga punla sa gabi.", listOf("Sintomas" to "Putol na tangkay na nakahandusay sa lupa pagkagising", "Tradisyunal na Lunas" to "Kuwelyo na karton (collar) o abo ng kahoy sa paanan"), "Ang paglalagay ng paper collar sa unang 2 linggo ay 100% mabisang proteksyon.")),
            PestDiseaseItem("Kuto ng Halaman (Aphids)", "Aphis gossypii", "Katamtaman", "Nangungulubot na dahon; maitim na sooty mold sa dumi ng insekto", "Mag-spray ng malakas na tubig sa ilalim ng dahon; alagaan ang mga hoverfly", "Spray ng sabon (Perla) na may kaunting mantika at tubig", ExplanationData("Aphid Control sa Kamatis", "SUMISIPSIP NA INSEKTO", "Maliliit na insektong sumisipsip ng katas sa ilalim ng usbong at dahon na nagdudulot ng panghihina ng halaman.", listOf("Sintomas" to "Kulubot na dahon at maitim na amag (sooty mold)", "Organikong Lunas" to "1 kutsarang Perla soap + 1 kutsaritang mantika sa 1 litrong tubig"), "Huwag maglagay ng sobrang nitrogen fertilizer dahil lalong dumarami ang aphids sa malalambot na dahon.")),
            PestDiseaseItem("Thrips", "Thrips tabaci", "Mataas", "Pilak o kulay-pilak na guhit sa dahon at pagkalagas ng bulaklak", "Asul at dilaw na sticky trap; regular na pagpapanatili ng basa sa lupa", "Spray ng neem extract o bio-insecticide bago sumikat ang araw", ExplanationData("Thrips Management", "PESTE SA BULAKLAK", "Napakaliliit na insekto na sumisipsip sa mga talulot ng bulaklak kaya nalalagas bago pa maging bunga.", listOf("Sintomas" to "Pagkalagas ng bulaklak at pilak na batik sa dahon", "Trasmisyon" to "Nagkakalat ng Tomato Spotted Wilt Virus"), "Mas marami tuwing tag-init at mahanging panahon sa pagitan ng Enero at Mayo.")),
            PestDiseaseItem("Pulang Hanip (Spider Mites)", "Tetranychus urticae", "Katamtaman", "Maliit na dilaw na tuldok sa dahon na may pinong sapot sa ilalim", "Huwag hayaang matuyuan at maalikabukan ang taniman; mag-mulch", "Spray ng sulfur o sabon sa ilalim ng dahon sa hapon", ExplanationData("Spider Mites sa Tag-Init", "HANIP SA DAHON", "Dumarami sa tuyo, maalikabok, at mainit na panahon. Pinuputol ang daloy ng photosynthesis sa dahon.", listOf("Sintomas" to "Pinong sapot at paninilaw ng ibabaw ng dahon", "Trigger" to "Matinding tagtuyot at alikabok"), "Diligan nang banayad ang ilalim ng dahon upang mapatid ang kanilang mga sapot.")),
            PestDiseaseItem("Flea Beetles (Talon-talon)", "Phyllotreta spp.", "Katamtaman", "Maliliit na butas (shot-holes) sa mga dahon ng bagong lipat na punla", "Gamitin ang pechay bilang trap crop; maglagay ng pinong kulambo", "Budburan ng abo ng kahoy o diatomaceous earth ang dahon", ExplanationData("Flea Beetle Control", "LUMULUKSONG INSEKTO", "Maliliit at makintab na itim na uwang na lumulukso kapag nilapitan at bumubutas sa dahon ng punla.", listOf("Sintomas" to "Parang tinadtad ng karayom ang mga dahon", "Organikong Panangga" to "Abo ng kahoy na may apog"), "Mabilis malampasan ng matatandang halaman ang pinsala ng flea beetle.")),
            PestDiseaseItem("Bulate sa Ugat (Nematodes)", "Meloidogyne incognita", "Malubha", "Bansot na halaman na madaling malanta; may bukol-bukol (galls) sa ugat", "Magtanim ng maraming amarilyo (marigold); ihalo ang CRH sa lupa", "Maglagay ng vermicast na mayaman sa Trichoderma at Paecilomyces", ExplanationData("Root-Knot Nematodes sa Pilipinas", "SALOT SA ILALIM NG LUPA", "Mikroskopikong bulate sa buhanging lupa na bumubutas at nagpapabukol sa ugat kaya hindi makasipsip ng tubig at pataba.", listOf("Sintomas" to "Lantang halaman sa tanghali kahit basa ang lupa; bukol sa ugat", "Mabisang Panlunas" to "Pagtatanim ng French Marigold bago at kasabay ng kamatis"), "Ang ugat ng amarilyo ay naglalabas ng natural na lason sa nematodes na naglilinis ng lupa sa loob ng 60 araw."))
        ),
        diseases = listOf(
            PestDiseaseItem("Lanta-Bakterya (Bacterial Wilt)", "Ralstonia solanacearum", "Nakamamatay", "Biglang pagkalanta ng buong halaman habang lunti pa ang dahon; mabilis kumalat", "Magtanim ng Diamante Max F1 o Apollo; gumawa ng mataas na kamada; iwasan ang baha", "Walang kemikal na lunas; bunutin agad, sunugin o ibaon sa malayo; huwag i-compost", ExplanationData("Bacterial Wilt — Pinakamalubhang Sakit sa Pilipinas", "BAKTERYAL NA SAKIT", "Ang Ralstonia solanacearum ang #1 sumisira sa mga taniman ng kamatis sa Pilipinas, lalo na sa mainit at basing lupa pagkatapos ng ulan.", listOf("Sintomas" to "Biglang lanta sa umaga kahit basa ang lupa; may puting gatas na lumalabas sa tangkay kapag inilubog sa baso ng tubig (bacterial streaming)", "Pag-iwas" to "Crop rotation na may palay o mais; paggamit ng Trichoderma"), "Huwag kailanman magtatanim muli ng kamatis, talong, o sili sa lupang may bacterial wilt nang walang 2–3 taong pahinga.")),
            PestDiseaseItem("Tomato Yellow Leaf Curl (Kulot)", "TYLCV Begomovirus", "Kritikal", "Nangungulubot, tumitigas, at naninilaw paitaas ang mga dahon; walang nabubuong bunga", "Gumamit ng certified hybrid seeds (Diamante Max); kontrolin ang whitefly gamit ang yellow traps", "Walang lunas kapag nahawa na; bunutin upang hindi mahawa ang katabing puno", ExplanationData("TYLCV o 'Kulot' sa Kamatis", "VIRAL NA SAKIT", "Dinadala ng puting langaw (Bemisia tabaci). Kapag natusok ang batang punla, titigil ang paglaki at magiging kulot ang mga talbos.", listOf("Sintomas" to "Naninilaw na gilid ng dahon na nakatupi paitaas tulad ng tasa", "Panangga" to "Fine insect netting sa punlaan at pagpuksa sa whiteflies"), "Ang pagtatanim ng Diamante Max F1 na may likas na TYLCV resistance ang pinakamabisang pananggalang ng magsasaka.")),
            PestDiseaseItem("Pagkatuyo ng Punla (Damping-Off)", "Pythium & Rhizoctonia", "Malubha", "Lumalambot at pumuputok ang tangkay ng punla sa pantay ng lupa; bumubulagta", "Gumamit ng sterile soil media na may CRH; huwag pasobrahan ang dilig sa punlaan", "Ihalo ang Trichoderma harzianum sa punlaan; lagyan ng sapat na sikat ng araw", ExplanationData("Damping-Off sa Punlaan", "SAKIT SA PUNLA", "Fungal disease na umaatake sa malalambot na tangkay ng punla kapag madilim, kulob, at labis ang tubig sa seed tray.", listOf("Sintomas" to "Lulubog at mabubulok ang base ng tangkay sa loob ng isang gabi", "Solusyon" to "Maglagay ng Carbonized Rice Hull (CRH) para sa magandang drainage"), "Magdilig lamang sa umaga upang matuyo ang ibabaw ng seed tray bago sumapit ang dilim.")),
            PestDiseaseItem("Early Blight (Pasob)", "Alternaria solani", "Mataas", "Pabilog na maitim na batik na may bullseye rings sa lumang dahon sa ibaba", "Alisin ang mga dahon sa ibaba (hanggang 20 cm mula sa lupa); maglagay ng mulch", "Mag-spray ng copper fungicide o bio-fungicide kapag madalas ang ambon", ExplanationData("Early Blight o Pasob sa Pilipinas", "FUNGAL DISEASE", "Namumuo sa lupang natatalsikan ng ulan sa mga mabababang dahon. Unti-unting umaakyat pataas hanggang malagas ang dahon.", listOf("Sintomas" to "Batik na may hugis target sa ibabang dahon", "Pag-iwas" to "Mulching gamit ang dayami upang hindi tumalsik ang lupa sa dahon"), "Tagpasan ang lahat ng dahon sa ilalim ng unang buwig ng bulaklak.")),
            PestDiseaseItem("Bacterial Spot", "Xanthomonas campestris", "Mataas", "Maliliit na basang batik na nagiging itim na may dilaw na paligid sa dahon at bunga", "Huwag magtrabaho sa taniman habang basa ang dahon; gumamit ng malinis na binhi", "Spray ng copper bactericide kasama ng mancozeb tuwing tag-ulan", ExplanationData("Bacterial Leaf Spot", "BAKTERYAL NA SAKIT", "Lumalaganap tuwing may bagyo o tuloy-tuloy na ulan sa tulong ng hangin at talsik ng tubig.", listOf("Sintomas" to "Magaspang na parang langib sa bunga at dahon", "Paalala" to "Huwag mag-spray habang umuulan"), "Huwag hawakan o pungusan ang kamatis kapag basa ang mga dahon.")),
            PestDiseaseItem("Sakit sa Bunga (Anthracnose)", "Colletotrichum coccodes", "Katamtaman", "Lulubog na pabilog na batik sa hinog na bunga na may maitim na tuldok sa gitna", "Pitasin sa manibalang stage; huwag hayaang lumapat ang bunga sa basang lupa", "Maglagay ng balag upang nakabitin ang bunga; mag-spray ng bio-fungicide", ExplanationData("Anthracnose sa Hinog na Bunga", "FUNGAL PATHOGEN", "Umaatake sa mga hinog na bunga, lalo na kapag lumapat sa basang lupa o nabasa ng ulan.", listOf("Sintomas" to "Lulubog at mabubulok na bilog sa balat ng bunga", "Lunas" to "Pag-aani sa breaker stage bago pa tuluyang mamula sa puno"), "Ang pagtatali sa tulos ay naglalayo sa bunga mula sa spores na nasa lupa.")),
            PestDiseaseItem("Blossom End Rot (Tuyong Puwit)", "Kakulangan sa Calcium", "Mataas", "Maitim, tuyo, at parang katad na pagkabulok sa pinakailalim (puwit) ng bunga", "Panatilihing pantay ang dilig; maglagay ng apog o CalPhos (Calcium Phosphate)", "Mag-spray ng Fermented Eggshell Calcium (CalPhos) sa dahon tuwing namumulaklak", ExplanationData("Blossom End Rot — Kakulangan sa Kalsyo", "PISYOLOHIKAL NA SAKIT (HINDI INSEKTO)", "Hindi ito sanhi ng peste, kundi kakulangan ng calcium sa lumalaking bunga dahil sa pabago-bagong dilig o tuyong lupa.", listOf("Sintomas" to "Maitim at pipi na puwit ng bunga", "Natural na Gamot" to "CalPhos mula sa sinangag na balat ng itlog na ibinabad sa sukang tuba"), "Ang regular na pagdidilig at paglalagay ng mulch ang lulutas sa 90% ng kaso ng blossom end rot.")),
            PestDiseaseItem("Fusarium Wilt", "Fusarium oxysporum", "Malubha", "Naninilaw ang dahon sa isang bahagi lamang ng tangkay; may kulay-kape sa loob ng tangkay", "Magtanim ng certified F1 hybrid na may resistensya; maglagay ng Trichoderma", "Bunutin ang apektadong puno; huwag ihalo sa compost", ExplanationData("Fusarium Wilt sa Kamatis", "FUNGUS SA LUPA", "Pumapasok sa ugat at hinaharangan ang daluyan ng tubig at pataba sa tangkay.", listOf("Sintomas" to "Kalahati lamang ng dahon o sanga ang naninilaw", "Pagsusuri" to "May kulay tsokolate sa loob ng hiniwang tangkay"), "Pumili ng mga hybrid tulad ng Diamante Max na may natural na resistance."))
        ),
        beneficialCritters = listOf(
            CritterItem("Kiwot / Stingless Bees (Tetragonula biroi)", "Pangunahing katutubong tagapagsabog ng polen (pollinator) sa Pilipinas", "Bulaklak ng kamatis, sulasi, borage, at cosmos", ExplanationData("Kiwot (Stingless Bee ng Pilipinas)", "KATUTUBONG BUBUYOG", "Katutubong bubuyog sa Pilipinas na walang tibo. Napakahusay magsagawa ng 'buzz pollination' sa mga bulaklak ng kamatis na nagpapalaki sa ani.", listOf("Tungkulin" to "Sonication / Buzz Pollination", "Pakinabang" to "30–40% dagdag sa dami at laki ng bunga"))),
            CritterItem("Pukyutan (Honeybees / Apis cerana)", "Mahalagang tagapagsabog ng polen sa bukid at bakuran", "Borage, basil, zinnia, at mga namumulaklak na gulay", ExplanationData("Pukyutan (Honeybee)", "POLLINATOR", "Tumutulong sa polinasyon ng mga bulaklak sa umaga at nagpapabuti sa kabuoang ekolohiya ng taniman.", listOf("Tungkulin" to "Pang-araw-araw na polinasyon", "Katangian" to "Aktibo mula alas-6 hanggang alas-10 ng umaga"))),
            CritterItem("Hoverfly (Syrphid Fly)", "Ang uod nito ay lumalamon sa daan-daang aphids at thrips araw-araw", "Alyssum, dill, coriander, at marigold", ExplanationData("Hoverfly (Kaibigang Langaw)", "PREDATOR AT POLLINATOR", "Ang matandang hoverfly ay kumakain ng nectar; ang munting luntiang uod naman nito ay walang tigil na lumalamon sa mga aphids.", listOf("Pagkain ng Uod" to "Kuto ng halaman, thrips, maliliit na uod", "Pakinabang" to "Likas na pamatay-peste nang walang lason"))),
            CritterItem("Mandurukot (Assassin Bug)", "Mabilis na mangangaso na sumisipsip sa mga uod, tipaklong, at uwang", "Makakapal na damong-gamot, oregano, at mulch", ExplanationData("Mandurukot (Assassin Bug)", "MABANGIS NA MANGANGASO", "May matulis na tuka na itinusok sa katawan ng harabas at fruit borer upang ubusin ang laman-loob nito.", listOf("Biktima" to "Harabas, cutworm, flea beetles", "Tirahan" to "Ilalim ng malalagong sanga"))),
            CritterItem("Aphid Lion (Green Lacewing)", "Uod na kumakain ng hanggang 200 aphids at hanip bawat linggo", "Namumulaklak na cosmos, marigold, at caraway", ExplanationData("Green Lacewing (Aphid Lion)", "PREDATOR SA DAHON", "Tinaguriang 'leon ng aphids' dahil sa bilis lumamon ng mga itlog ng uod, hanip, at sumisipsip na insekto.", listOf("Biktima" to "Aphids, spider mites, thrips eggs", "Anyo" to "Pino at luntiang pakpak parang puntas"))),
            CritterItem("Simbahan (Praying Mantis)", "Nananambang at lumalamon sa mga tipaklong, gamugamo, at salagubang", "Mataas na balag, kawayan, at malalaking dahon ng kamatis", ExplanationData("Simbahan (Praying Mantis)", "GENERAL PREDATOR", "Mahusay magkubli at sumunggab sa mga lumilipad na gamugamo bago pa makapangitlog ng mga mapanirang harabas.", listOf("Biktima" to "Gamugamo (moths), tipaklong, uwang", "Paraan" to "Matiyagang pananambang"))),
            CritterItem("Tutubi (Dragonfly / Damselfly)", "Nanghuhuli ng mga lumilipad na puting langaw at lamok sa hangin", "Malapit sa kanal, drum ng tubig, at bukas na taniman", ExplanationData("Tutubi (Dragonfly)", "AERIAL HUNTER", "Humahagibis sa himpapawid upang daklutin ang mga puting langaw (whitefly) bago sila makalapag sa mga dahon ng kamatis.", listOf("Biktima" to "Puting langaw, lamok, maliliit na lumilipad na insekto", "Oras" to "Buong maghapon"))),
            CritterItem("Ibon (Maya at Pipit)", "Pumupulot sa malalaking harabas at uod na nakakapit sa mga sanga", "Tubigan ng ibon, mga tulos na kawayan, at paligid ng bakod", ExplanationData("Ibon sa Hardin (Maya at Pipit)", "NATURAL PEST CONTROL", "Dumadapo sa mga tulos na kawayan at maingat na pumupulot sa mga uod na sumisira sa dahon at bunga.", listOf("Biktima" to "Malalaking uod, armyworms, tipaklong", "Paalala" to "Maglagay ng malinis na inuman ng ibon sa tabi ng taniman")))
        ),
        growingGuides = listOf(
            GuideSectionItem(
                id = "punlaan",
                title = "1. Pagpupunla at Pangangalaga sa Punlaan (Seedbed & Trays)",
                summary = "Paghahanda ng 104-hole trays, sterile 1:1:1 media na may Carbonized Rice Hull (CRH), at proteksyon sa ulan.",
                fullContent = "Sa Pilipinas, hindi isinasabog nang direkta sa bukid ang binhi ng kamatis; kailangan itong ipunla sa plastic seedling tray (104 holes) upang masiguro ang 95%+ germination at malulusog na ugat.\n\nMedia Mix: Paghaluin ang 1 bahagi ng pinong lupang-hardin, 1 bahagi ng vermicast o pinatuyong dumi ng baka, at 1 bahagi ng Carbonized Rice Hull (CRH). Ang CRH ay napakahalaga sa Pilipinas upang maging buhaghag ang media at hindi mababad sa tubig ang ugat.\n\nIpunla ang 1 binhi bawat butas sa lalim na 0.5 cm. Diligan gamit ang pinong sprayer at takpan ng basang sako o dyaryo sa unang 3 araw. Sa sandaling sumibol, ilagay sa punlaang may 50% net shade. Sa ika-14 hanggang ika-21 araw, unti-unting ilantad sa buong araw (hardening) bago ilipat sa bukid.",
                tips = listOf("Gumamit ng Carbonized Rice Hull (CRH) upang maiwasan ang damping-off.", "Magdilig lamang sa umaga; iwasan ang pagbabasa sa gabi.", "Gawin ang 'hardening' (bawas dilig at buong araw) 5 araw bago maglipat-tanim.")
            ),
            GuideSectionItem(
                id = "pagtatanim",
                title = "2. Paghahanda ng Lupa at Lipat-Tanim (Land Prep & Transplanting)",
                summary = "Paggawa ng mataas na kamada (20–30 cm), plastic mulch, paglalagay ng basal fertilizer, at pagtatanim sa hapon.",
                fullContent = "Araruhin at suyurin ang lupa ng 2–3 beses hanggang maging pinong-pino. Gumawa ng matataas na kamada (plots) na may taas na 20–30 cm at lapad na 1 metro. Napakahalaga ng mataas na kamada upang hindi malunod ang mga ugat kapag bumuhos ang biglaang ulan sa hapon.\n\nPlastic Mulching: Mas mainam maglagay ng silver-black plastic mulch. Ang kulay-pilak sa ibabaw ay nagtataboy sa aphids at thrips sa pamamagitan ng reflection ng sikat ng araw, habang pinipigilan ang damo at pinapanatili ang halumigmig ng lupa.\n\nOras ng Pagtatanim: Ilipat ang mga punla (21–25 araw mula punla) sa bandang alas-4 ng hapon (4:00 PM) kapag lumamig na ang hangin upang maiwasan ang transplanting shock. Ibaon ang punla hanggang sa ibaba ng unang tunay na dahon—maglalabas ito ng mga bagong ugat sa buong ibinaong tangkay.",
                tips = listOf("Laging maglipat-tanim sa bandang alas-4 ng hapon upang hindi malanta sa init.", "Ibaon nang malalim ang tangkay upang dumami ang ugat.", "Diligan agad pagkatanim gamit ang may halong vermitea o root stimulant.")
            ),
            GuideSectionItem(
                id = "pagpapataba",
                title = "3. Tamang Abono at Pataba (Fertilization - UPLB & DA Protocol)",
                summary = "Kombinasyon ng Complete (14-14-14), Urea, Muriate of Potash, at Organikong Concoctions (FPJ at CalPhos).",
                fullContent = "Ang kamatis ay 'heavy feeder' na nangangailangan ng sapat na nutrisyon sa bawat yugto ng kanyang buhay:\n\nBasal Application (Sa Pagtatanim): Maglagay ng 1 kutsarang Complete (14-14-14) at 1 dakot ng vermicast sa bawat butas bago itanim ang punla. Haluin sa lupa upang hindi direktang dumikit sa ugat.\n\nSidedress 1 (14–21 Araw / DAP): Maglagay ng 1 kutsaritang Urea (46-0-0) o 16-20-0 na may layong 5 cm mula sa tangkay upang bumilis ang pagdami ng dahon at sanga. Maaaring mag-spray ng Fermented Plant Juice (FPJ mula sa kangkong o talbos ng kamote) tuwing ika-7 araw.\n\nSidedress 2 (Pamumulaklak at Pamumunga - 35–45 DAP): Maglagay ng Complete (14-14-14) at Muriate of Potash (0-0-60) upang maging matitigas at matatamis ang mga bunga. Mag-spray ng Fermented Eggshell Calcium (CalPhos) sa mga bulaklak at dahon upang maiwasan ang Blossom End Rot.",
                tips = listOf("Itigil ang mataas na nitrogen (Urea) kapag namumulaklak na upang hindi puro dahon lamang.", "Mag-spray ng CalPhos tuwing umaga sa yugto ng pamumulaklak.", "Diligan pagkatapos maglagay ng kemikal na abono upang hindi masunog ang ugat.")
            ),
            GuideSectionItem(
                id = "balag",
                title = "4. Pagtutos, Balag at Pag-aahit ng Suwi (Trellising & Pruning)",
                summary = "Kawayan na tulos (1.5–2m), A-frame na balag, pag-aalis ng suwi (pruning), at bentilasyon laban sa amag.",
                fullContent = "Sa Pilipinas, kailangan ng tulos na kawayan ang lahat ng kamatis upang hindi lumapat ang mabibigat na bunga sa basang lupa kung saan naghihintay ang mga fungal spores at peste.\n\nParaan ng Pagtutos: Magtirik ng tulos na kawayan (1.5 metro ang haba) sa tabi ng bawat puno 2 linggo pagkalipat-tanim. Itali ang pangunahing tangkay gamit ang plastic straw sa maluwag na pormang figure-8 upang hindi masakal ang sanga habang lumalaki.\n\nPag-aahit ng Suwi (Pruning): Alisin ang lahat ng suwi (suckers na tumutubo sa pagitan ng dahon at pangunahing tangkay) mula sa lupa hanggang sa unang buwig ng bulaklak. Panatilihin lamang ang 1 o 2 pangunahing sanga (single o double leader). Ang pagtatanggal ng mga suwi sa ibaba ay nagbibigay ng maaliwalas na bentilasyon ng hangin sa ilalim ng taniman upang hindi mamuo ang amag.",
                tips = listOf("Pitasin ang suwi gamit ang kamay habang maliit pa (2–3 pulgada).", "Huwag mag-prune kapag basa ang dahon upang hindi pumasok ang bakterya.", "Gumamit ng A-frame na kawayan para sa mas matibay na panangga sa hangin.")
            ),
            GuideSectionItem(
                id = "ani",
                title = "5. Tamang Pamimitas at Pagtatabi (Harvesting & Post-Harvest)",
                summary = "Pag-ani sa 'Manibalang' (breaker stage), tamang paghawak, shallow crates, at pagbabawal sa paglagay sa refrigerator.",
                fullContent = "Kailan Aaniin: Pitasin ang kamatis sa tinatawag na 'breaker stage' o manibalang—kung kailan buo na ang laki ng bunga at nag-uumpisa pa lamang mamula ang dulo o puwit nito. Ang pamimitas sa yugtong ito ay nagliligtas sa bunga mula sa mga ibon, uod, at basag na balat, habang pinasisigla ang halaman na magpalaki pa ng mga natitirang bunga.\n\nParaan ng Pagpitas: Marahang pihitin paitaas ang bunga habang hawak ng kabilang kamay ang tangkay upang hindi mapigtas ang sanga. Mag-ani tuwing maaliwalas at tuyo ang panahon (mula alas-8 ng umaga).\n\nPagtatabi: Ilagay sa mabababang plastic crates (kamada) na may saping dahon ng saging o dyaryo. Huwag pagpatung-patungin nang higit sa 3 patong. Itabi sa temperatura ng kusina (20°C–25°C) na hindi nasisikatan ng araw. HUWAG ILALAGAY SA REFRIGERATOR ang sariwang kamatis dahil nasisira ng lamig ang mga enzymes na nagbibigay ng natural na tamis at bango ng kamatis.",
                tips = listOf("Pitasin sa manibalang stage para sa pinakamahabang shelf-life sa palengke.", "Huwag ilagay sa ref; magtatagal ng 1–2 linggo sa mesa sa kusina.", "Baligtarin ang bunga (nakaharap sa ibaba ang tangkay) upang hindi sumingaw ang moisture.")
            )
        ),
        plantingCalendar = PlantingCalendarData(
            indoorRange = "Okt. 15 – Nob. 30",
            outdoorRange = "Nob. 15 – Dis. 30",
            harvestRange = "Ene. 15 – Abr. 30",
            indoorLabel = "Punlaan (Seedbed)",
            outdoorLabel = "Lipat-Tanim (Field)",
            harvestLabel = "Ani (Harvest)",
            plantTypeNote = "Pangunahing Panahon: Tag-araw (Okt–Abr); Off-season na may Rain Shelter (Mayo–Ago)",
            monthsSchedule = listOf(
                "Okt" to "Punlaan (Seedbed)",
                "Nob" to "Lipat-Tanim",
                "Dis" to "Pagtutulos at Balag",
                "Ene" to "Simula ng Ani",
                "Peb" to "Tugatog ng Ani (Peak)",
                "Mar" to "Tugatog ng Ani (Peak)",
                "Abr" to "Huling Ani / Linis Taniman",
                "May" to "Paghahanda sa Off-Season",
                "Hun" to "Off-Season (Rain Shelter)",
                "Hul" to "Off-Season Ani (Mataas ang Presyo)",
                "Ago" to "Tag-ulan / Habagat Break",
                "Set" to "Paghahanda sa Tag-araw"
            )
        ),
        locationData = LocationDifficultyData(
            difficulty = "Madali hanggang Katamtaman (Easy–Moderate)",
            hardinessZone = "Mababang Kapatagan at Kabundukan (0–1,500m ASL)",
            tempRange = "22°C–32°C (Tropikal)",
            sunlight = "Buong Araw (6–8 oras)",
            suitableAlso = "Nalililiman sa tanghali kung sobra ang init (>35°C)"
        ),
        soilPrep = SoilPrepData(
            phRange = "6.0–6.8 (Bahagyang Maasim hanggang Neutral)",
            soilTypes = "Buhaghag na Loam, Buhangin at Luwad na may CRH (Carbonized Rice Hull)",
            recommendation = "Maglagay ng 2–3 kilong vermicompost o dumi ng baka/manok bawat metro kuwadrado. Ihalo ang Carbonized Rice Hull (CRH) upang lumuwag ang lupa at hindi magtubig ang ugat. Maglagay ng apog (agricultural lime o dolomite) 2 linggo bago maglipat-tanim kung acidic ang lupa (pH < 5.8)."
        ),
        growthTimeline = listOf(
            TimelineStageItem("Araw 1", "Punlaan", "Ipunla ang binhi sa 104-hole seedling tray gamit ang 1:1:1 lupa, vermicast at CRH.", "Panatilihing mamasa-masa"),
            TimelineStageItem("Araw 5–10", "Pagsibol", "Pagsulpot ng unang dalawang dahon (cotyledon); ilantad sa sikat ng araw sa umaga.", "Iwasan ang sobrang dilig"),
            TimelineStageItem("Araw 21–25", "Lipat-Tanim", "May 2–4 na tunay na dahon na ang punla; ilipat sa bukid o paso sa bandang alas-4 ng hapon.", "Maglagay ng basal fertilizer"),
            TimelineStageItem("Araw 35–45", "Balag at Suwi", "Magtulos ng kawayan; itali ang halaman; alisin ang mga suwi sa ibaba para sa bentilasyon.", "Alisin ang suwi linggu-linggo"),
            TimelineStageItem("Araw 60–75+", "Manibalang na Ani", "Pitasin ang mga bunga sa 'breaker stage' (nag-uumpisa nang mamula) bawat 3–4 na araw.", "Ingatang mapigtas ang tangkay")
        ),
        howTos = listOf(
            HowToSectionItem(
                title = "Tamang Paraan ng Paglilipat-Tanim (Transplanting Guide)",
                steps = listOf(
                    "Diligan ang seedling tray 2 oras bago maglipat upang madaling hugutin ang buong root ball nang buo.",
                    "Maghukay sa kamada o paso na may lalim na 10–15 cm—sapat upang maibaon ang tangkay hanggang sa unang tunay na dahon.",
                    "Maglagay ng 1 kutsarang Complete (14-14-14) at 1 dakot na vermicast sa ilalim at haluin sa lupa.",
                    "Maingat na ipasok ang punla sa hukay nang nakatayo nang tuwid.",
                    "Tabunan ng buhaghag na lupa at dahan-dahang pisilin ang paligid ng puno upang mawala ang bulsa ng hangin.",
                    "Diligan agad nang sagana upang kumapit ang mga bagong ugat sa basang lupa."
                ),
                tips = listOf(
                    "Gawin palagi sa bandang alas-4 ng hapon (4:00 PM) upang hindi malanta sa init ng araw.",
                    "Maglagay ng plastic mulch o tuyong dayami upang hindi matalsikan ng putik ang mga dahon kapag umulan."
                ),
                safety = "Magsuot ng bota at guwantes sa paghahalaman; mag-ingat sa matatalim na tulos na kawayan."
            ),
            HowToSectionItem(
                title = "Pagkuha at Pagpapatuyo ng Binhi (Seed Saving sa Kamatis)",
                steps = listOf(
                    "Pumili ng buo, malusog, at pinakamatamis na hinog na kamatis mula sa open-pollinated variety (tulad ng Kamatis Tagalog o Batac Native; hindi F1 hybrid).",
                    "Hiwain sa gitna at kayurin ang mga binhi kasama ng kanilang gelatinous sac sa isang malinis na basong garapon.",
                    "Lagyan ng 2 kutsarang tubig at iwanan ng 2 hanggang 3 araw sa malilim na sulok upang mag-ferment (aagnasin nito ang balot na pumipigil sa pagsibol at papatayin ang mga bacterial pathogens).",
                    "Kapag may namuong manipis na puting amag sa ibabaw, hugasan nang maigi sa salaan gamit ang tumutulong tubig.",
                    "Ilatag ang malilinis na binhi sa ibabaw ng coffee filter o platong seramika sa tuyo at malilim na lugar sa loob ng 7 hanggang 10 araw.",
                    "Kapag lumalagitik na sa tuyo, ilagay sa malinis na sobreng may silica gel at itabi sa tuyo at malamig na lagayan."
                ),
                tips = listOf(
                    "Huwag gumamit ng tissue o paper towel dahil didikit ang buto at mahirap nang tanggalin.",
                    "Ang fermentation ay natural na paraan upang puksain ang bacterial canker sa balat ng binhi."
                )
            )
        ),
        faqs = listOf(
            FaqItem("Bakit naninilaw at kulot ang mga talbos ng kamatis ko?", "Ito ay sintomas ng Tomato Yellow Leaf Curl Virus (TYLCV o 'Kulot') na dinadala ng puting langaw (whitefly). Kapag nahawa na ang halaman, wala na itong lunas. Upang maiwasan ito sa susunod, magtanim ng may resistensyang barayti tulad ng Diamante Max F1 at maglagay ng dilaw na sticky traps para mahuli ang mga puting langaw."),
            FaqItem("Bakit biglang nalanta ang buong puno ng kamatis kahit sariwa pa at basa ang lupa?", "Ito ay ang kinatatakutang Lanta-Bakterya (Bacterial Wilt o Ralstonia solanacearum). Mabubusisi ito sa pamamagitan ng pagputol sa tangkay at paglubog nito sa baso ng tubig—kung may lumabas na puting mala-gatas na usok (bacterial streaming), kumpirmadong bacterial wilt ito. Bunutin agad at sunugin; huwag nang taniman muli ng talong o kamatis ang lupang iyon."),
            FaqItem("Bakit nalalaglag ang mga bulaklak ng kamatis sa tanghali?", "Nalalaglag ang bulaklak kapag lumagpas sa 35°C ang temperatura sa tanghali o kapag lubhang natutuyo ang lupa. Sa matinding init ng Marso hanggang Mayo, maglagay ng 30% net shade sa ibabaw ng taniman at panatilihing mamasa-masa ang lupa gamit ang makapal na dayami bilang mulch."),
            FaqItem("Ano ang mabisang pamuksa sa harabas at uod sa bunga nang walang matapang na kemikal?", "Mabisang gamitin ang biological spray na Bacillus thuringiensis (Bt) o Spinosad sa takip-silim kung kailan lumalabas ang mga uod. Ang pagtatanim ng marigold (amarilyo) at sulasi (basil) sa tabi ng kamatis ay natural ding nagtataboy sa mga gamugamong nangingitlog ng uod."),
            FaqItem("Bakit nangingitim at nabubulok ang puwit ng bunga ng kamatis?", "Ito ay Blossom End Rot na dulot ng kakulangan ng calcium sa lumalaking bunga sanhi ng pabago-bagong dilig. Upang masolusyunan, magdilig nang regular at mag-spray ng CalPhos (sinangag na balat ng itlog na ibinabad sa sukang tuba sa loob ng 10–14 araw) sa mga bulaklak tuwing umaga.")
        ),
        nutritionTable = NutritionTableData(
            portionSize = "100 g",
            calories = "32 Kcal",
            carbs = "7 g",
            carbsPct = "5.38%",
            sugar = "4.73 g",
            fiber = "2.2 g",
            fiberPct = "5.79%",
            sucrose = "0 g",
            summary = "Ang kamatis ay sagana sa Bitamina C, Potassium, Folate, at Lycopene. Ang regular na pagkain ng kamatis ay tumutulong sa pagpapababa ng presyon ng dugo, nagpapalakas ng resistensya, at nagbibigay ng proteksyon laban sa sakit sa puso at cancer."
        ),
        exploreArticles = listOf(
            ArticleItem("Gabay sa Pagtatanim ng Kamatis sa Tag-araw at Tag-ulan (DA-BPI Manual)", "Agrikultura Pilipinas", "5 min basahin"),
            ArticleItem("Paano Sugpuin ang Lanta-Bakterya (Ralstonia) at Kulot sa Kamatis", "Pangangalaga sa Pananim", "4 min basahin"),
            ArticleItem("Urban Gardening: Pagpapatubo ng Kamatis sa Paso sa Metro Manila", "Urban Farming", "6 min basahin"),
            ArticleItem("Paggawa ng FPJ, FAA, at CalPhos: Natural Farming Concoctions", "Organikong Pagsasaka", "5 min basahin"),
            ArticleItem("Kahalagahan ng Carbonized Rice Hull (CRH) sa Lupang Taniman", "Paghahanda ng Lupa", "4 min basahin"),
            ArticleItem("Top 5 Hybrid Kamatis na Patok sa Palengke (East-West Seed)", "Barayti at Merkado", "3 min basahin"),
            ArticleItem("Paggamit ng Silver-Black Plastic Mulch para Makatipid sa Tubig", "Modernong Teknolohiya", "4 min basahin"),
            ArticleItem("Pangangalaga sa Gulayan Tuwing Panahon ng Bagyo at Habagat", "Klima at Panahon", "7 min basahin"),
            ArticleItem("A-Frame Bamboo Trellis at Drip Irrigation sa Bakuran", "Disenyo ng Taniman", "6 min basahin")
        )
    )

    // ═══════════════════════════════════════════════════════════════════════════════
    // 2. CARROT / KAROT (Daucus carota) — Philippine Highland & Lowland Data
    // ═══════════════════════════════════════════════════════════════════════════════
    val carrotData = tomatoData.copy(
        cropName = "Carrot",
        localName = "Karot (Daucus carota)",
        scientificName = "Daucus carota subsp. sativus",
        category = "Root Vegetables (Apiaceae)",
        family = "Apiaceae",
        overview = "Ang karot ay isa sa pinakatanyag na root crop sa Pilipinas, pangunahing itinatanim sa malamig na kabundukan ng Cordillera (Benguet, Mountain Province) at Bukidnon. Nangangailangan ito ng malalim at buhaghag na buhanging lupa na walang bato upang dumiretso at maging makinis ang ugat.",
        selectedVariety = VarietyInfo(
            name = "New Kuroda",
            producer = "East-West Seed Philippines",
            type = "Tropical Open-Pollinated Root Crop",
            badge = "OPV",
            description = "Nangungunang barayti ng karot sa Pilipinas na may mataas na resistensya sa init at amag ng dahon. Matamis, makinis, at matingkad na kahel ang kulay ng laman."
        ),
        otherVarieties = listOf("New Kuroda (East-West Seed)", "Terracotta F1", "Chantenay Red Cored", "Danvers 126", "Imperator 58")
    )

    // ═══════════════════════════════════════════════════════════════════════════════
    // 3. EGGPLANT / TALONG (Solanum melongena) — Premier Philippine Vegetable
    // ═══════════════════════════════════════════════════════════════════════════════
    val eggplantData = tomatoData.copy(
        cropName = "Eggplant",
        localName = "Talong (Solanum melongena)",
        scientificName = "Solanum melongena",
        category = "Nightshades (Solanaceae)",
        family = "Solanaceae",
        overview = "Ang talong ang numero unong gulay sa Pilipinas ayon sa dami ng produksyon at lawak ng taniman. Matibay sa init ng kapatagan at patok sa lahat ng lutuing Pilipino tulad ng pinakbet at torta.",
        selectedVariety = VarietyInfo(
            name = "Fortuner F1",
            producer = "East-West Seed Philippines",
            type = "Highland & Lowland Hybrid",
            badge = "F1",
            description = "Mabentang mahabang lilang talong sa Pilipinas na may luntiang calyx. Napakatagal mabulok, makintab ang balat, at tuloy-tuloy mamunga nang masagana."
        ),
        otherVarieties = listOf("Fortuner F1 (East-West Seed)", "Casino F1", "Long Purple (Condor)", "Mistisa F1 (Bicol Favorite)", "Black Beauty", "Ping Tung Long")
    )

    // ═══════════════════════════════════════════════════════════════════════════════
    // 4. PECHAY / BOK CHOY (Brassica rapa subsp. chinensis) — Fast-Growing Leafy Green
    // ═══════════════════════════════════════════════════════════════════════════════
    val pechayData = tomatoData.copy(
        cropName = "Pechay",
        localName = "Pechay (Brassica rapa)",
        scientificName = "Brassica rapa subsp. chinensis",
        category = "Leafy Greens (Brassicaceae)",
        family = "Brassicaceae",
        overview = "Ang pechay ang paboritong pananim ng mga baguhan at urban gardeners sa Pilipinas dahil maaari na itong anihin sa loob lamang ng 25 hanggang 35 araw. Perpekto sa nilaga, sinigang, at ginisang ulam.",
        selectedVariety = VarietyInfo(
            name = "Black Behi",
            producer = "Condor / East-West Seed",
            type = "Open-Pollinated Commercial Standard",
            badge = "OPV",
            description = "Standard commercial pechay sa Pilipinas na may malalapad at mapuputing tangkay at makakapal na madilim na luntiang dahon na matatag sa init at ulan."
        ),
        otherVarieties = listOf("Black Behi (Standard)", "Pavito (East-West Seed)", "Shanghai Green Baby Bok Choy", "Ching Chiang")
    )

    // ═══════════════════════════════════════════════════════════════════════════════
    // 5. CHILI / SILI (Capsicum annuum & frutescens) — Hot Pepper & Labuyo
    // ═══════════════════════════════════════════════════════════════════════════════
    val siliData = tomatoData.copy(
        cropName = "Chili",
        localName = "Sili (Capsicum annuum / frutescens)",
        scientificName = "Capsicum annuum / frutescens",
        category = "Nightshades (Solanaceae)",
        family = "Solanaceae",
        overview = "Mula sa maanghang na katutubong Siling Labuyo hanggang sa mahabang Siling Haba (Pangsigang), ang sili ay lubhang pinahahalagahan sa agrikultura ng Pilipinas. Nangangailangan ng mainit na araw at buhaghag na lupa.",
        selectedVariety = VarietyInfo(
            name = "Django F1",
            producer = "East-West Seed Philippines",
            type = "High-Heat Hot Pepper Hybrid",
            badge = "F1",
            description = "Pangunahing hybrid na siling pansigang at pang-merkado sa Pilipinas na may makakapal at makikintab na bunga, matinding anghang, at matibay laban sa anthracnose at virus."
        ),
        otherVarieties = listOf("Django F1", "Katutubong Siling Labuyo", "Red Hot F1", "Siling Tingala", "Panigang F1", "Habanero")
    )

    // ═══════════════════════════════════════════════════════════════════════════════
    // 6. SQUASH / KALABASA (Cucurbita moschata) — Rich Tropical Cucurbit
    // ═══════════════════════════════════════════════════════════════════════════════
    val pumpkinData = tomatoData.copy(
        cropName = "Squash / Pumpkin",
        localName = "Kalabasa (Cucurbita moschata)",
        scientificName = "Cucurbita moschata",
        category = "Cucurbits (Cucurbitaceae)",
        family = "Cucurbitaceae",
        overview = "Ang kalabasa ay isang gumagapang na baging na mayaman sa bitamina A. Kinakain ang bunga, bulaklak, at talbos nito. Lubos na angkop sa tropikal na klima ng Pilipinas.",
        selectedVariety = VarietyInfo(
            name = "Suprema F1",
            producer = "East-West Seed Philippines",
            type = "Tropical Hybrid Winter Squash",
            badge = "F1",
            description = "Ang pinakasikat na hybrid na kalabasa sa bansa na may pambihirang resistensya sa Squash Leaf Curl Virus (SLCV). Matamis, malagkit, at matingkad na dilaw-kahel ang laman."
        ),
        otherVarieties = listOf("Suprema F1 (East-West Seed)", "Rizal F1", "San Mateo Native Kalabasa", "Butternut Rugosa")
    )
}
