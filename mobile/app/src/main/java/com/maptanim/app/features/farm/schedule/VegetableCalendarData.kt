package com.maptanim.app.features.farm.schedule

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Yard
import androidx.compose.ui.graphics.Color
import com.maptanim.app.data.local.entity.CropVarietyEntity
import com.maptanim.app.features.farm.components.CropMaturityProfile
import java.time.LocalDate

/**
 * 13 Core Vegetables with complete agronomic knowledge profiles in MapTanim:
 * 1. Tomato (Kamatis)
 * 2. Carrot (Karot)
 * 3. Eggplant (Talong)
 * 4. Squash (Kalabasa)
 * 5. String Beans / Yardlong Bean (Sitaw)
 * 6. Pechay (Pechay / Bok Choy)
 * 7. Lettuce (Litsugas)
 * 8. Green Onion / Scallion (Sibuyas Dahon)
 * 9. Potato (Patatas)
 * 10. Bitter Gourd (Ampalaya)
 * 11. Cucumber (Pipino)
 * 12. Bell Pepper / Sweet Pepper (Siling Atsal)
 * 13. Cabbage (Repolyo)
 */
enum class SpecificVegetable {
    TOMATO,
    CARROT,
    EGGPLANT,
    SQUASH,
    STRING_BEANS,
    PECHAY,
    LETTUCE,
    GREEN_ONION,
    POTATO,
    BITTER_GOURD,
    CUCUMBER,
    BELL_PEPPER,
    CABBAGE,
    GENERIC
}

fun detectSpecificVegetable(cropName: String): SpecificVegetable {
    val lower = cropName.lowercase().trim()
    return when {
        lower.contains("kamatis") || lower.contains("tomato") -> SpecificVegetable.TOMATO
        lower.contains("karot") || lower.contains("carrot") -> SpecificVegetable.CARROT
        lower.contains("talong") || lower.contains("eggplant") -> SpecificVegetable.EGGPLANT
        lower.contains("kalabasa") || lower.contains("squash") -> SpecificVegetable.SQUASH
        lower.contains("sitaw") || lower.contains("yardlong") || lower.contains("string bean") || lower.contains("beans") -> SpecificVegetable.STRING_BEANS
        lower.contains("pechay") || lower.contains("petsay") || lower.contains("bok choy") || lower.contains("pak choi") -> SpecificVegetable.PECHAY
        lower.contains("lettuce") || lower.contains("litsugas") -> SpecificVegetable.LETTUCE
        lower.contains("green onion") || lower.contains("scallion") || lower.contains("spring onion") || lower.contains("sibuyas dahon") || lower.contains("leek") || lower.contains("onion") -> SpecificVegetable.GREEN_ONION
        lower.contains("patatas") || lower.contains("potato") -> SpecificVegetable.POTATO
        lower.contains("ampalaya") || lower.contains("bitter gourd") || lower.contains("bitter melon") -> SpecificVegetable.BITTER_GOURD
        lower.contains("pipino") || lower.contains("cucumber") -> SpecificVegetable.CUCUMBER
        lower.contains("bell pepper") || lower.contains("sweet pepper") || lower.contains("atsal") || lower.contains("pepper") || lower.contains("sili") -> SpecificVegetable.BELL_PEPPER
        lower.contains("repolyo") || lower.contains("cabbage") -> SpecificVegetable.CABBAGE
        else -> SpecificVegetable.GENERIC
    }
}

fun detectCropCategory(cropName: String): CropAgronomicCategory {
    val lower = cropName.lowercase()
    return when {
        lower.contains("pechay") || lower.contains("mustasa") || lower.contains("lettuce") ||
        lower.contains("cabbage") || lower.contains("repolyo") || lower.contains("spinach") ||
        lower.contains("kangkong") || lower.contains("bok choy") ->
            CropAgronomicCategory.LEAFY_BRASSICA

        lower.contains("potato") || lower.contains("patatas") || lower.contains("kamote") ||
        lower.contains("radish") || lower.contains("labanos") || lower.contains("carrot") ||
        lower.contains("karot") || lower.contains("singkamas") || lower.contains("cassava") ->
            CropAgronomicCategory.TUBER_ROOT

        lower.contains("onion") || lower.contains("sibuyas") || lower.contains("garlic") ||
        lower.contains("bawang") || lower.contains("scallion") || lower.contains("leek") ->
            CropAgronomicCategory.ALLIUM

        lower.contains("corn") || lower.contains("mais") ->
            CropAgronomicCategory.GRAIN_CORN

        lower.contains("sitaw") || lower.contains("beans") || lower.contains("ampalaya") ||
        lower.contains("cucumber") || lower.contains("pipino") || lower.contains("squash") ||
        lower.contains("kalabasa") || lower.contains("patola") || lower.contains("upo") ||
        lower.contains("sayote") || lower.contains("bataw") || lower.contains("sigarilyas") ->
            CropAgronomicCategory.CLIMBING_VINE

        else -> CropAgronomicCategory.SOLANACEOUS_FRUIT
    }
}

fun findStageName(day: Int, stages: List<StageBannerInfo>): String {
    val matched = stages.firstOrNull { day in it.startDay..it.endDay }
    if (matched != null) return matched.stageKey
    return if (day <= 0) stages.firstOrNull()?.stageKey ?: "Pagtatanim" else stages.lastOrNull()?.stageKey ?: "Pag-aani"
}

/**
 * Builds the accurate, biological growth stage roadmap for all 13 crops + fallback.
 */
fun buildCropSpecificStages(
    cropName: String,
    varietyName: String? = null,
    totalDays: Int,
    variety: CropVarietyEntity? = null
): List<StageBannerInfo> {
    val specific = detectSpecificVegetable(cropName)
    val stages = mutableListOf<StageBannerInfo>()

    when (specific) {
        SpecificVegetable.TOMATO -> {
            val s1 = 16
            val s2 = 36
            val s3 = 52
            stages.add(StageBannerInfo("Pagtatanim", "Pagtatanim at Paghahanda ng Kama", "Araw 0 • Starter Loam Bed", "Buhaghag na lupa, compost drench, at paglilipat-punla sa dakong hapon", listOf(Color(0xFF1B3B2B), Color(0xFF2E5D43)), Icons.Default.Spa, 0, 0))
            stages.add(StageBannerInfo("Punla", "Yugto 1: Punla at Pagsibol ng Ugat", "Araw 1 – $s1 • Pagsibol at Pag-ugat", "Pagsibol ng unang tunay na dahon; dilig sa base sa 6:30 AM", listOf(Color(0xFF143D1A), Color(0xFF235A29)), Icons.Default.Yard, 1, s1))
            stages.add(StageBannerInfo("Paglago", "Yugto 2: Bamboo Stakes at Pruning", "Araw ${s1 + 1} – $s2 • Staking at Mulching", "Bamboo stake, dayami mulch, at pag-alis ng bottom suckers para sa hangin", listOf(Color(0xFF0F3818), Color(0xFF1C5225)), Icons.Default.Agriculture, s1 + 1, s2))
            stages.add(StageBannerInfo("Pamumulaklak", "Yugto 3: Pamumulaklak at Fruit Set", "Araw ${s2 + 1} – $s3 • Pagsulpot ng Bulaklak", "Calcium boost (eggshell tea) laban sa blossom-end rot; maingat na dilig sa ugat", listOf(Color(0xFF2C1947), Color(0xFF4A2B6F)), Icons.Default.LocalFlorist, s2 + 1, s3))
            stages.add(StageBannerInfo("Pamumunga", "Yugto 4: Pamumunga at Pagkahinog", "Araw ${s3 + 1} – ${totalDays - 1} • Paglaki ng Bunga", "Potassium feed (FFJ) at suporta sa mabigat na sanga gamit ang figure-8 ties", listOf(Color(0xFF4A2010), Color(0xFF7B3B18)), Icons.Default.Eco, s3 + 1, totalDays - 1))
            stages.add(StageBannerInfo("Pag-aani", "Yugto 5: Panahon ng Pag-aani", "Araw $totalDays+ • Regular na Pagpitas", "Pitasin sa umaga gamit ang gunting; iwanan ang calyx/tangkay para sa shelf-life", listOf(Color(0xFF1C2833), Color(0xFFD35400)), Icons.Default.Stars, totalDays, totalDays + 40))
        }

        SpecificVegetable.CARROT -> {
            val s1 = 21
            val s2 = 45
            val s3 = 75
            stages.add(StageBannerInfo("Pagtatanim", "Paghahanda ng 30cm Malalim na Kama", "Araw 0 • Direct Seeding sa Buhaghag na Bed", "Malalim na loam na walang bato; banayad na pagdidilig sa pinong buto", listOf(Color(0xFF2A1B0E), Color(0xFF4A2B15)), Icons.Default.Spa, 0, 0))
            stages.add(StageBannerInfo("Pagsibol", "Yugto 1: Mabagal na Pagsibol", "Araw 1 – $s1 • Pagsulpot ng Unang Feathery Leaves", "Panatilihing mamasa-masa ang ibabaw ng lupa upang hindi matuyuan ang buto", listOf(Color(0xFF1B3B22), Color(0xFF2D5C37)), Icons.Default.Yard, 1, s1))
            stages.add(StageBannerInfo("Pagpapanipis", "Yugto 2: Thinning at Taproot Elongation", "Araw ${s1 + 1} – $s2 • Pagpapanipis sa 5-8cm", "Alisin ang siksikang punla upang magkaroon ng puwang ang taproot pababa", listOf(Color(0xFF1E4620), Color(0xFF336E37)), Icons.Default.Agriculture, s1 + 1, s2))
            stages.add(StageBannerInfo("Tuber Bulking", "Yugto 3: Paglaki ng Karot (Root Bulking)", "Araw ${s2 + 1} – $s3 • Pagkapal ng Orange Taproot", "Side-dress ng wood ash (potassium) para sa tamis at matingkad na kulay orange", listOf(Color(0xFF4D2600), Color(0xFF804000)), Icons.Default.Eco, s2 + 1, s3))
            stages.add(StageBannerInfo("Panghuling Yugto", "Yugto 4: Maturation at Shoulder Coverage", "Araw ${s3 + 1} – ${totalDays - 1} • Pagtabon sa Balikat", "Takpan ng kaunting lupa ang balikat ng karot upang hindi maging berde", listOf(Color(0xFF663300), Color(0xFFA65300)), Icons.Default.Eco, s3 + 1, totalDays - 1))
            stages.add(StageBannerInfo("Pag-aani", "Yugto 5: Paghukay ng Karot", "Araw $totalDays+ • Pag-ani Gamit ang Garden Fork", "Luwayan ang lupa gamit ang tinidor sa 15cm layo upang hindi maputol ang ugat", listOf(Color(0xFF1C2833), Color(0xFFD35400)), Icons.Default.Stars, totalDays, totalDays + 15))
        }

        SpecificVegetable.EGGPLANT -> {
            val s1 = 25
            val s2 = 50
            val s3 = 70
            stages.add(StageBannerInfo("Pagtatanim", "Paghahanda ng Kama at Starter Drench", "Araw 0 • Paglilipat-Puno sa 50cm Pagitan", "Buhaghag na lupa na may halong pinatuyong dumi ng baka o vermicompost", listOf(Color(0xFF1B3B2B), Color(0xFF2E5D43)), Icons.Default.Spa, 0, 0))
            stages.add(StageBannerInfo("Punla", "Yugto 1: Pag-ugat at Paglago ng Punla", "Araw 1 – $s1 • Pagpapatatag ng Collar", "Proteksyon sa init ng araw; masinsing dilig sa base tuwing umaga sa 6:30 AM", listOf(Color(0xFF143D1A), Color(0xFF235A29)), Icons.Default.Yard, 1, s1))
            stages.add(StageBannerInfo("Paglago", "Yugto 2: Bamboo Stake at Bottom Pruning", "Araw ${s1 + 1} – $s2 • Staking at Pag-alis ng Sangay", "Ibaon ang tukod na kawayan; alisin ang mga sangang mababa sa unang bulaklak", listOf(Color(0xFF0F3818), Color(0xFF1C5225)), Icons.Default.Agriculture, s1 + 1, s2))
            stages.add(StageBannerInfo("Pamumulaklak", "Yugto 3: Lilang Bulaklak at Fruit Set", "Araw ${s2 + 1} – $s3 • Paglabas ng Lilang Bulaklak", "Mag-spray ng FPJ at suportahan ang sanga upang hindi mabali sa bigat ng bunga", listOf(Color(0xFF33144A), Color(0xFF56237D)), Icons.Default.LocalFlorist, s2 + 1, s3))
            stages.add(StageBannerInfo("Pag-aani", "Yugto 4: Regular na Pagpitas ng Talong", "Araw $totalDays+ • Makintab na Lilang Bunga", "Pitasin habang makintab pa ang balat bago tumigas ang buto; gupitin may 2cm calyx", listOf(Color(0xFF1C2833), Color(0xFF6C3483)), Icons.Default.Stars, totalDays, totalDays + 60))
        }

        SpecificVegetable.SQUASH -> {
            val s1 = 15
            val s2 = 40
            val s3 = 60
            val s4 = totalDays - 1
            stages.add(StageBannerInfo("Pagtatanim", "Paghahanda ng Burol (Raised Hills)", "Araw 0 • Burol na may 2m Pagitan", "Mound na mayaman sa compost at espasyo para sa malalapad na baging", listOf(Color(0xFF1B3B2B), Color(0xFF2E5D43)), Icons.Default.Spa, 0, 0))
            stages.add(StageBannerInfo("Pagsibol", "Yugto 1: Pagsibol ng Matatag na Buto", "Araw 1 – $s1 • Paglabas ng Malalaking Cotyledon", "Masinsing dilig sa base; bantayan laban sa cutworms at snail", listOf(Color(0xFF143D1A), Color(0xFF235A29)), Icons.Default.Yard, 1, s1))
            stages.add(StageBannerInfo("Pagbaging", "Yugto 2: Mabilis na Pag-gapang ng Baging", "Araw ${s1 + 1} – $s2 • Canopy Expansion at Mulch", "Igabay ang baging sa dayami; mag-spray ng FPJ tuwing hapon para sa dahon", listOf(Color(0xFF0F3818), Color(0xFF1C5225)), Icons.Default.Agriculture, s1 + 1, s2))
            stages.add(StageBannerInfo("Pamumulaklak", "Yugto 3: Dilaw na Bulaklak at Polinasyon", "Araw ${s2 + 1} – $s3 • Lalaki at Babaeng Bulaklak", "Kritikal ang mga bubuyog sa 6:00-9:00 AM; maglagay ng board sa ilalim ng bunga", listOf(Color(0xFF4A3800), Color(0xFF7D5F00)), Icons.Default.LocalFlorist, s2 + 1, s3))
            stages.add(StageBannerInfo("Pagkalaman", "Yugto 4: Pagkalaman at Pagkapal ng Balat", "Araw ${s3 + 1} – $s4 • Pagbuo ng Matamis na Laman", "Potassium feed (FFJ at abo) para sa matingkad na dilaw-orange na laman", listOf(Color(0xFF593000), Color(0xFF8C4B00)), Icons.Default.Eco, s3 + 1, s4))
            stages.add(StageBannerInfo("Pag-aani", "Yugto 5: Pag-aani ng Hinog na Kalabasa", "Araw $totalDays+ • Tuyong Tangkay at Matigas na Balat", "Pitasin kapag tuyo at mala-cork ang tangkay; iwanan ang 5cm tangkay para tumagal", listOf(Color(0xFF1C2833), Color(0xFFD35400)), Icons.Default.Stars, totalDays, totalDays + 30))
        }

        SpecificVegetable.STRING_BEANS -> {
            val s1 = 14
            val s2 = 32
            val s3 = 45
            stages.add(StageBannerInfo("Pagtatanim", "Pagtatanim ng Buto ng Sitaw", "Araw 0 • Direct Seeding sa Tudling", "Ibaon ang 2-3 buto sa bawat burol; diligan nang banayad", listOf(Color(0xFF1B3B2B), Color(0xFF2E5D43)), Icons.Default.Spa, 0, 0))
            stages.add(StageBannerInfo("Punla", "Yugto 1: Pagsibol at Pag-akyat ng Baging", "Araw 1 – $s1 • Pagsibol ng Unang Dahon", "Dilig sa ugat sa 6:30 AM; suriin ang ilalim ng dahon laban sa itim na aphids", listOf(Color(0xFF143D1A), Color(0xFF235A29)), Icons.Default.Yard, 1, s1))
            stages.add(StageBannerInfo("Balag", "Yugto 2: Pagtatayo ng Balag / Trellis", "Araw ${s1 + 1} – $s2 • 1.8m A-Frame Trellis", "Itayo ang balag at igabay ang mga baging paakyat sa lambat o kawayan", listOf(Color(0xFF0F3818), Color(0xFF1C5225)), Icons.Default.Agriculture, s1 + 1, s2))
            stages.add(StageBannerInfo("Pamumulaklak", "Yugto 3: Pamumulaklak at Pod Set", "Araw ${s2 + 1} – $s3 • Pagsulpot ng Lilang Bulaklak", "Bantayan laban sa pod borer; mag-spray ng calcium tea laban sa paglagas", listOf(Color(0xFF2C1947), Color(0xFF4A2B6F)), Icons.Default.LocalFlorist, s2 + 1, s3))
            stages.add(StageBannerInfo("Pag-aani", "Yugto 4: Alternate-Day Harvest", "Araw $totalDays+ • Mahahabang Berdeng Bunga", "Pitasin tuwing 2 araw sa umaga habang malutong at hindi pa maumbok ang buto", listOf(Color(0xFF1C2833), Color(0xFF27AE60)), Icons.Default.Stars, totalDays, totalDays + 25))
        }

        SpecificVegetable.PECHAY -> {
            val s1 = 8
            val s2 = 18
            val s3 = totalDays - 1
            stages.add(StageBannerInfo("Pagtatanim", "Paghahanda ng 15-20cm Raised Bed", "Araw 0 • Buhaghag na Loam Bed", "Maghanda ng pinong kama na may vermicompost; iwasan ang matubig na pwesto", listOf(Color(0xFF1B3B2B), Color(0xFF2E5D43)), Icons.Default.Spa, 0, 0))
            stages.add(StageBannerInfo("Punla", "Yugto 1: Punla at Pagsibol", "Araw 1 – $s1 • Pagsibol at Thinning", "Pagsibol sa loob ng 3 araw; mag-thinning sa 10-15cm pagitan sa Araw 8", listOf(Color(0xFF143D1A), Color(0xFF235A29)), Icons.Default.Yard, 1, s1))
            stages.add(StageBannerInfo("Paglago", "Yugto 2: Mabilis na Paglaki ng Dahon", "Araw ${s1 + 1} – $s2 • FPJ at Mulching", "Dayami mulch at FPJ foliar spray; bantayan laban sa Diamondback moth", listOf(Color(0xFF0F3818), Color(0xFF1C5225)), Icons.Default.Agriculture, s1 + 1, s2))
            stages.add(StageBannerInfo("Rosette", "Yugto 3: Makatas na Tangkay (Rosette)", "Araw ${s2 + 1} – $s3 • Pagkapal ng Puti ng Tangkay", "Masinsing dilig sa 6:00 AM; iwasang mabasa ang puso upang hindi mabulok", listOf(Color(0xFF2E5D43), Color(0xFF4E9B71)), Icons.Default.Eco, s2 + 1, s3))
            stages.add(StageBannerInfo("Pag-aani", "Yugto 4: Buong Bungkos na Pag-ani", "Araw $totalDays+ • Pag-ani Bago Mag-bolt", "Putulin sa may ugat gamit ang kutsilyo sa maagang umaga bago uminit ang araw", listOf(Color(0xFF1C2833), Color(0xFF2ECC71)), Icons.Default.Stars, totalDays, totalDays + 10))
        }

        SpecificVegetable.LETTUCE -> {
            val s1 = 12
            val s2 = 25
            val s3 = totalDays - 1
            stages.add(StageBannerInfo("Pagtatanim", "Mababaw na Paghahasik ng Buto", "Araw 0 • Nursery Seedbed / Tray", "Mababaw na scatter sa ibabaw; kailangan ng liwanag upang sumibol ang buto", listOf(Color(0xFF1B3B2B), Color(0xFF2E5D43)), Icons.Default.Spa, 0, 0))
            stages.add(StageBannerInfo("Punla", "Yugto 1: Nursery Seedling Establishment", "Araw 1 – $s1 • Pag-ugat at Paglipat", "Diluted compost tea; ilipat sa kama na may 20cm pagitan sa Araw 12", listOf(Color(0xFF143D1A), Color(0xFF235A29)), Icons.Default.Yard, 1, s1))
            stages.add(StageBannerInfo("Rosette", "Yugto 2: Malalabay na Dahon at Lilim", "Araw ${s1 + 1} – $s2 • 30% Shade Netting", "Lagyan ng shade net sa tanghali laban sa init; dilig sa umaga at 4:00 PM", listOf(Color(0xFF0F3818), Color(0xFF1C5225)), Icons.Default.Agriculture, s1 + 1, s2))
            stages.add(StageBannerInfo("Head Formation", "Yugto 3: Head Formation at Paglutong", "Araw ${s2 + 1} – $s3 • Crisp Leaf Packing", "Panatilihing malamig ang ugat; iwasan ang tipburn gamit ang regular na dilig", listOf(Color(0xFF2E5D43), Color(0xFF4E9B71)), Icons.Default.Eco, s2 + 1, s3))
            stages.add(StageBannerInfo("Pag-aani", "Yugto 4: Malutong na Pag-aani sa Umaga", "Araw $totalDays+ • Crisp Head / Cut & Come Again", "Pitasin sa maagang umaga habang malutong; hydro-cool agad sa malamig na tubig", listOf(Color(0xFF1C2833), Color(0xFF27AE60)), Icons.Default.Stars, totalDays, totalDays + 10))
        }

        SpecificVegetable.GREEN_ONION -> {
            val s1 = 15
            val s2 = 35
            val s3 = totalDays - 1
            stages.add(StageBannerInfo("Pagtatanim", "Pagtatanim ng Sets o Punla ng Sibuyas", "Araw 0 • Sandy Loam Bed", "Ibaon ang sets sa 10-12cm pagitan; siguraduhing buhaghag at may drainage", listOf(Color(0xFF1B3B2B), Color(0xFF2E5D43)), Icons.Default.Spa, 0, 0))
            stages.add(StageBannerInfo("Pagsibol", "Yugto 1: Pagsibol ng Berdeng Tangkay", "Araw 1 – $s1 • Pagsibol ng Unang Leaves", "Dilig sa base sa 6:00 AM; suriin ang singit laban sa thrips", listOf(Color(0xFF143D1A), Color(0xFF235A29)), Icons.Default.Yard, 1, s1))
            stages.add(StageBannerInfo("Paglago", "Yugto 2: Tillering at Pagdami ng Tangkay", "Araw ${s1 + 1} – $s2 • Paglago at FPJ", "Mag-spray ng FPJ tuwing hapon; mag-alis ng damo sa paligid nang maingat", listOf(Color(0xFF0F3818), Color(0xFF1C5225)), Icons.Default.Agriculture, s1 + 1, s2))
            stages.add(StageBannerInfo("Blanching", "Yugto 3: Stem Blanching / Earthing-up", "Araw ${s2 + 1} – $s3 • Pagtatabon sa Tangkay", "Tabunan ng 4cm lupa ang base upang maging maputi, makatas, at malutong ang leek", listOf(Color(0xFF2E5D43), Color(0xFF4E9B71)), Icons.Default.Eco, s2 + 1, s3))
            stages.add(StageBannerInfo("Pag-aani", "Yugto 4: Pagbunot ng Buong Sibuyas", "Araw $totalDays+ • Bunching Harvest", "Diligin nang kaunti ang lupa bago bunutin upang hindi mapatid ang tangkay", listOf(Color(0xFF1C2833), Color(0xFF16A085)), Icons.Default.Stars, totalDays, totalDays + 20))
        }

        SpecificVegetable.POTATO -> {
            val s1 = 18
            val s2 = 45
            val s3 = 75
            val s4 = totalDays - 10
            stages.add(StageBannerInfo("Pagtatanim", "Pagtatanim ng Sprouted Seed Tubers", "Araw 0 • 25cm Deep Loose Ridge", "Itanim ang may sibol paitaas sa 7-10cm lalim; katamtamang basa lamang", listOf(Color(0xFF2A1B0E), Color(0xFF4A2B15)), Icons.Default.Spa, 0, 0))
            stages.add(StageBannerInfo("Pagsibol", "Yugto 1: Pagsibol mula sa Tuber Eyes", "Araw 1 – $s1 • Paglabas ng mga Tangkay", "Katamtamang dilig bawat 3 araw; huwag lunurin upang hindi mabulok ang seed", listOf(Color(0xFF1B3B22), Color(0xFF2D5C37)), Icons.Default.Yard, 1, s1))
            stages.add(StageBannerInfo("Hilling Up", "Yugto 2: Unang Pagtatabon ng Lupa (1st Hill)", "Araw ${s1 + 1} – $s2 • Pagtabon ng 7cm Lupa", "Tabunan ang tangkay upang protektahan ang umuusbong na stolon sa liwanag", listOf(Color(0xFF1E4620), Color(0xFF336E37)), Icons.Default.Agriculture, s1 + 1, s2))
            stages.add(StageBannerInfo("Tuber Bulking", "Yugto 3: 2nd Hilling Up at Tuber Bulking", "Araw ${s2 + 1} – $s3 • Paglaki ng mga Patatas", "Pangalawang tabon at dayami; side-dress ng wood ash para sa malalaking laman", listOf(Color(0xFF4D2600), Color(0xFF804000)), Icons.Default.Eco, s2 + 1, s3))
            stages.add(StageBannerInfo("De-haulming", "Yugto 4: De-haulming (Pagputol ng Dahon)", "Araw ${s3 + 1} – $s4 • Pagpapatigas ng Balat", "Gupitin ang mga tangkay sa ibabaw ng lupa upang tumigas ang balat ng patatas", listOf(Color(0xFF663300), Color(0xFFA65300)), Icons.Default.Agriculture, s3 + 1, s4))
            stages.add(StageBannerInfo("Pag-aani", "Yugto 5: Paghukay ng mga Patatas", "Araw $totalDays+ • Pag-ani sa 8:00 AM", "Hukayin gamit ang garden fork sa labas ng ridge; patuyuin sa lilim bago itago", listOf(Color(0xFF1C2833), Color(0xFFB7950B)), Icons.Default.Stars, totalDays, totalDays + 15))
        }

        SpecificVegetable.BITTER_GOURD -> {
            val s1 = 16
            val s2 = 35
            val s3 = 50
            stages.add(StageBannerInfo("Pagtatanim", "Paghahanda ng Plot at Overhead Trellis", "Araw 0 • Burol na may Basal Compost", "Ibabad ang buto magdamag bago itanim; maghanda ng espasyo para sa balag", listOf(Color(0xFF1B3B2B), Color(0xFF2E5D43)), Icons.Default.Spa, 0, 0))
            stages.add(StageBannerInfo("Punla", "Yugto 1: Pagsibol at Pag-akyat ng Tendrils", "Araw 1 – $s1 • Pagsibol at Pag-alalay", "Diligan tuwing 2 araw sa 6:30 AM; igabay ang baging sa patpat na kawayan", listOf(Color(0xFF143D1A), Color(0xFF235A29)), Icons.Default.Yard, 1, s1))
            stages.add(StageBannerInfo("Balag", "Yugto 2: Overhead Bamboo Trellis Setup", "Araw ${s1 + 1} – $s2 • Balag at Pruning sa Ibaba", "Prunangin ang lateral shoots sa unang 1 metro upang umakyat ang sigla sa balag", listOf(Color(0xFF0F3818), Color(0xFF1C5225)), Icons.Default.Agriculture, s1 + 1, s2))
            stages.add(StageBannerInfo("Fruit Bagging", "Yugto 3: Pamumulaklak at Pagbabalot ng Bunga", "Araw ${s2 + 1} – $s3 • Fruit Fly Protection", "Balutan ng papel ang batang bunga upang hindi itlugan ng langaw ng prutas", listOf(Color(0xFF4A3800), Color(0xFF7D5F00)), Icons.Default.LocalFlorist, s2 + 1, s3))
            stages.add(StageBannerInfo("Pag-aani", "Yugto 4: Regular na Pagpitas sa Umaga", "Araw $totalDays+ • Makintab na Warty Gourds", "Pitasin bawat 3 araw habang makintab pa ang gulod bago maging dilaw ang dulo", listOf(Color(0xFF1C2833), Color(0xFF196F3D)), Icons.Default.Stars, totalDays, totalDays + 35))
        }

        SpecificVegetable.CUCUMBER -> {
            val s1 = 12
            val s2 = 25
            val s3 = 35
            stages.add(StageBannerInfo("Pagtatanim", "Paghahanda ng 20cm Raised Bed", "Araw 0 • Direct Seeding sa Kama", "Buhaghag na lupa na mayaman sa compost at espasyo para sa trellis netting", listOf(Color(0xFF1B3B2B), Color(0xFF2E5D43)), Icons.Default.Spa, 0, 0))
            stages.add(StageBannerInfo("Punla", "Yugto 1: Mabilis na Pagsibol", "Araw 1 – $s1 • Emergence at Trellis Guide", "Diligan tuwing 2 araw sa 6:30 AM; igabay ang baging paakyat sa lambat", listOf(Color(0xFF143D1A), Color(0xFF235A29)), Icons.Default.Yard, 1, s1))
            stages.add(StageBannerInfo("Paglago", "Yugto 2: Vertical Trellis at Side Pruning", "Araw ${s1 + 1} – $s2 • Pruning sa Ibabang Sangay", "Alisin ang sanga sa unang 30cm para sa hangin; mag-spray ng FPJ booster", listOf(Color(0xFF0F3818), Color(0xFF1C5225)), Icons.Default.Agriculture, s1 + 1, s2))
            stages.add(StageBannerInfo("Pamumulaklak", "Yugto 3: Dilaw na Bulaklak at Fruit Set", "Araw ${s2 + 1} – $s3 • Mabilis na Paglaki ng Bunga", "Panatilihing pantay ang basa sa ugat upang hindi maging mapait (cucurbitacin)", listOf(Color(0xFF4A3800), Color(0xFF7D5F00)), Icons.Default.LocalFlorist, s2 + 1, s3))
            stages.add(StageBannerInfo("Pag-aani", "Yugto 4: Malutong na Pipino Bawat 1-2 Araw", "Araw $totalDays+ • 15-20cm Cylindrical Fruits", "Pitasin tuwing 1–2 araw habang madilim na berde at makinis bago maging dilaw", listOf(Color(0xFF1C2833), Color(0xFF229954)), Icons.Default.Stars, totalDays, totalDays + 25))
        }

        SpecificVegetable.BELL_PEPPER -> {
            val s1 = 20
            val s2 = 45
            val s3 = 65
            stages.add(StageBannerInfo("Pagtatanim", "Paghahanda ng Kama at Paglilipat-punla", "Araw 0 • Raised Bed at Mulching", "Ilipat ang stocky seedlings sa 45cm pagitan sa dakong hapon; starter compost drench", listOf(Color(0xFF1B3B2B), Color(0xFF2E5D43)), Icons.Default.Spa, 0, 0))
            stages.add(StageBannerInfo("Punla", "Yugto 1: Pag-ugat at Pagpapatatag", "Araw 1 – $s1 • Pagsibol at Collar Strength", "Bantayan laban sa mites at aphids; regular na dilig sa base sa 6:30 AM", listOf(Color(0xFF143D1A), Color(0xFF235A29)), Icons.Default.Yard, 1, s1))
            stages.add(StageBannerInfo("Staking", "Yugto 2: Bamboo Stakes at Rice Straw Mulch", "Araw ${s1 + 1} – $s2 • Tukod at Mulching", "Ibaon ang tukod na kawayan; lagyan ng 2 pulgadang dayami sa ibabaw ng kama", listOf(Color(0xFF0F3818), Color(0xFF1C5225)), Icons.Default.Agriculture, s1 + 1, s2))
            stages.add(StageBannerInfo("Pamumulaklak", "Yugto 3: Puti na Bulaklak at Calcium Spray", "Araw ${s2 + 1} – $s3 • Blossom-End Rot Defense", "Mag-spray ng eggshell calcium tea tuwing hapon upang makapal ang dingding ng sili", listOf(Color(0xFF2C1947), Color(0xFF4A2B6F)), Icons.Default.LocalFlorist, s2 + 1, s3))
            stages.add(StageBannerInfo("Pag-aani", "Yugto 4: Makakapal na Atsal (Green / Red)", "Araw $totalDays+ • Blocky Bell Pepper Harvest", "Pitasin habang makintab at makapal ang balat gamit ang gunting na may 2cm tangkay", listOf(Color(0xFF1C2833), Color(0xFFC0392B)), Icons.Default.Stars, totalDays, totalDays + 35))
        }

        SpecificVegetable.CABBAGE -> {
            val s1 = 22
            val s2 = 45
            val s3 = 68
            stages.add(StageBannerInfo("Pagtatanim", "Paghahanda ng Loam Bed (pH 6.0-6.8)", "Araw 0 • Raised Bed na May Apog", "Maglagay ng agricultural lime kung maasim ang lupa upang mapigilan ang clubroot", listOf(Color(0xFF1B3B2B), Color(0xFF2E5D43)), Icons.Default.Spa, 0, 0))
            stages.add(StageBannerInfo("Punla", "Yugto 1: Punla at Pag-ugat sa Kama", "Araw 1 – $s1 • Pag-alalay sa Punla", "Dilig tuwing 2 araw sa 6:30 AM; suriin ang ilalim laban sa uod ng DBM", listOf(Color(0xFF143D1A), Color(0xFF235A29)), Icons.Default.Yard, 1, s1))
            stages.add(StageBannerInfo("Frame Leaves", "Yugto 2: Malalapad na Outer Frame Leaves", "Araw ${s1 + 1} – $s2 • Solar Energy Collector", "Mag-spray ng FPJ at vermicast extract; dayami mulch sa paligid ng puno", listOf(Color(0xFF0F3818), Color(0xFF1C5225)), Icons.Default.Agriculture, s1 + 1, s2))
            stages.add(StageBannerInfo("Head Cupping", "Yugto 3: Head Initiation at Pagbuo ng Ulo", "Araw ${s2 + 1} – $s3 • Pag-ikot at Pagbalot ng Dahon", "Bumibilog na sentro; maglagay ng potassium upang maging solid at mabigat ang ulo", listOf(Color(0xFF2E5D43), Color(0xFF4E9B71)), Icons.Default.Eco, s2 + 1, s3))
            stages.add(StageBannerInfo("Pag-aani", "Yugto 4: Matigas na Ulo ng Repolyo", "Araw $totalDays+ • Solid Head Harvest", "Pitasin kapag matigas sa pagdiin ng hinlalaki; iwanan ang 2-3 wrapper leaves", listOf(Color(0xFF1C2833), Color(0xFF27AE60)), Icons.Default.Stars, totalDays, totalDays + 15))
        }

        SpecificVegetable.GENERIC -> {
            val cat = detectCropCategory(cropName)
            val s1End = (totalDays * 0.25f).toInt().coerceIn(7, 18)
            val s2End = (totalDays * 0.55f).toInt().coerceIn(s1End + 1, totalDays - 15)
            val s3End = totalDays - 1
            stages.add(StageBannerInfo("Pagtatanim", "Paghahanda at Pagtatanim", "Araw 0 • Raised Bed Prep", "Buhaghag na lupa na may compost at magandang sirkulasyon", listOf(Color(0xFF1B3B2B), Color(0xFF2E5D43)), Icons.Default.Spa, 0, 0))
            stages.add(StageBannerInfo("Punla", "Yugto 1: Punla at Pagsibol", "Araw 1 – $s1End • Pagsibol at Pag-ugat", "Masinsing dilig sa base tuwing umaga bago uminit ang araw", listOf(Color(0xFF143D1A), Color(0xFF235A29)), Icons.Default.Yard, 1, s1End))
            stages.add(StageBannerInfo("Paglago", "Yugto 2: Paglago at Pag-aalaga", "Araw ${s1End + 1} – $s2End • Mulching at Pataba", "Mulch at organikong foliar spray sa hapon para sa malalakas na dahon", listOf(Color(0xFF0F3818), Color(0xFF1C5225)), Icons.Default.Agriculture, s1End + 1, s2End))
            stages.add(StageBannerInfo("Paghinog", "Yugto 3: Pagkalaman at Paghinog", "Araw ${s2End + 1} – $s3End • Maturation", "Potassium boost para sa de-kalidad na ani", listOf(Color(0xFF4A2010), Color(0xFF7B3B18)), Icons.Default.Eco, s2End + 1, s3End))
            stages.add(StageBannerInfo("Pag-aani", "Yugto 4: Panahon ng Pag-aani", "Araw $totalDays+ • Harvest Window", "Pitasin sa umaga habang sariwa gamit ang malinis na gamit", listOf(Color(0xFF1C2833), Color(0xFFD35400)), Icons.Default.Stars, totalDays, totalDays + 20))
        }
    }

    return stages
}

/**
 * Agronomic Agenda Schedule Engine for all 13 vegetables + fallback.
 */
fun generateCropAgendaSchedule(
    cropName: String,
    varietyName: String? = null,
    plantedDate: LocalDate,
    profile: CropMaturityProfile,
    variety: CropVarietyEntity? = null,
    stages: List<StageBannerInfo> = emptyList()
): List<AgendaScheduleItem> {
    val items = mutableListOf<AgendaScheduleItem>()
    val specific = detectSpecificVegetable(cropName)
    val totalDays = variety?.growthDurationDays ?: profile.baseDaysToHarvest

    fun resolveStage(day: Int): String = findStageName(day, stages)

    // ── 1. DAY 0: SOIL PREP & PLANTING ──
    val prepTime = when (specific) {
        SpecificVegetable.PECHAY, SpecificVegetable.LETTUCE, SpecificVegetable.GREEN_ONION -> "6:00 AM"
        else -> "6:30 AM"
    }
    val prepTitle = when (specific) {
        SpecificVegetable.CARROT -> "Paghahanda ng 30cm Malalim na Kamang Taniman (Walang Bato)"
        SpecificVegetable.POTATO -> "Paghahanda ng 25cm Deep Ridge at Buhaghag na Buhangin"
        SpecificVegetable.SQUASH -> "Paghahanda ng Raised Hills / Burol (2m ang Pagitan)"
        SpecificVegetable.BITTER_GOURD, SpecificVegetable.STRING_BEANS, SpecificVegetable.CUCUMBER -> "Paghahanda ng Plot at Espasyo para sa Balag / Trellis"
        SpecificVegetable.CABBAGE -> "Paghahanda ng Raised Bed na may Apog (pH 6.0-6.8)"
        SpecificVegetable.PECHAY, SpecificVegetable.LETTUCE -> "Paghahanda ng 15-20cm Pinong Raised Bed na may Compost"
        SpecificVegetable.GREEN_ONION -> "Paghahanda ng Sandy Loam Bed na may Magandang Drainage"
        else -> "Paghahanda ng Kamang Taniman at Basal Compost Mix"
    }
    val prepInstruction = when (specific) {
        SpecificVegetable.CARROT -> "Bungkaling maigi ang lupa hanggang 30cm lalim. Alisin ang lahat ng bato at tigkal ng lupa upang hindi maging sanga-sanga (forked) ang karot."
        SpecificVegetable.POTATO -> "Bungkaling malalim ang tudling. Haluan ng buhangin at organikong compost. Panatilihing buhaghag upang makahinga ang seed tubers."
        SpecificVegetable.SQUASH -> "Gumawa ng mga burol na may 60cm lapad at 2m pagitan. Lagyan ng 2 dakot ng vermicompost sa bawat burol."
        SpecificVegetable.CABBAGE -> "Maglagay ng agricultural lime (apog) kung asidiko ang lupa upang mapigilan ang Clubroot fungus."
        SpecificVegetable.PECHAY, SpecificVegetable.LETTUCE -> "Patagin ang pinong loam bed. Basain nang banayad gamit ang pinong sprayer upang maging mamasa-masa."
        else -> "Basain ang kama o paso nang banayad. Siguraduhing buhaghag at may sapat na drainage ang lupa."
    }
    items.add(AgendaScheduleItem("prep-0", plantedDate, prepTime, 0, AgendaTaskType.WATERING, "$cropName: $prepTitle", prepInstruction, resolveStage(0), isKeyMilestone = true))

    val plantTitle = when (specific) {
        SpecificVegetable.POTATO -> "Pagtatanim ng Seed Tubers sa Hapon"
        SpecificVegetable.CARROT -> "Mababaw na Paghahasik ng Pinong Buto ng Karot"
        SpecificVegetable.LETTUCE -> "Mababaw na Paghahasik / Paglipat ng Punla ng Litsugas"
        SpecificVegetable.PECHAY -> "Paghahasik ng Buto sa Mababaw na Tudling (0.5cm)"
        SpecificVegetable.STRING_BEANS, SpecificVegetable.SQUASH -> "Pagtatanim ng Buto sa Bawat Burol sa Hapon"
        SpecificVegetable.GREEN_ONION -> "Pagtatanim ng Sets o Punla ng Sibuyas Dahon"
        else -> "Paglilipat-tanim ng Punla sa Hapon (Starter Drench)"
    }
    val plantInstruction = when (specific) {
        SpecificVegetable.POTATO -> "Itanim ang seed tuber na may sibol paitaas sa lalim na 7-10cm na may 30cm pagitan. Diligan nang katamtaman—huwag lunurin."
        SpecificVegetable.CARROT -> "Ihasik sa mababaw na tudling (0.5cm). Takpan ng napakanipis na compost at diligan gamit ang pinong mist spray."
        SpecificVegetable.LETTUCE -> "Ihasik nang napakababaw dahil kailangan ng buto ng kaunting liwanag para sumibol; o ilipat ang punla sa 20cm pagitan sa dakong hapon."
        SpecificVegetable.PECHAY -> "Ihasik ang buto sa 0.5cm lalim. Diligan nang banayad sa ugat upang hindi maanod ang buto."
        SpecificVegetable.STRING_BEANS -> "Magtanim ng 2-3 buto bawat burol sa 2cm lalim. Diligan ng starter solution sa paligid."
        SpecificVegetable.SQUASH -> "Magtanim ng 2 buto bawat burol sa 2.5cm lalim. Diligan nang banayad sa paligid ng burol."
        SpecificVegetable.GREEN_ONION -> "Ibaon ang sets o punla na may 10-12cm pagitan. Diligan nang banayad sa base ng lupa."
        else -> "Itanim ang punla sa dakong hapon upang maiwasan ang init ng araw. Diligan ng banayad sa ugat (Starter Drench)."
    }
    items.add(AgendaScheduleItem("plant-0", plantedDate, "4:30 PM", 0, AgendaTaskType.MILESTONE, "$cropName: $plantTitle", plantInstruction, resolveStage(0), isKeyMilestone = true))

    // ── 2. ROUTINE WATERING (CROP-SPECIFIC INTERVAL, TIME & INSTRUCTION) ──
    val waterInterval = when (specific) {
        SpecificVegetable.PECHAY -> 1
        SpecificVegetable.LETTUCE -> 1
        SpecificVegetable.CARROT -> 3
        SpecificVegetable.POTATO -> 3
        SpecificVegetable.SQUASH -> 4
        SpecificVegetable.CABBAGE -> 2
        else -> variety?.wateringIntervalDays?.coerceAtLeast(1) ?: 2
    }
    val waterTime = when (specific) {
        SpecificVegetable.PECHAY, SpecificVegetable.LETTUCE, SpecificVegetable.GREEN_ONION -> "6:00 AM"
        else -> "6:30 AM"
    }
    val waterInstruction = when (specific) {
        SpecificVegetable.PECHAY -> "Masinsing pagdidilig sa base ng lupa bago mag-init ang araw. Iwasang basain ang sentro ng rosette upang maiwasan ang soft rot."
        SpecificVegetable.LETTUCE -> "Banayad na pagdidilig sa lupa sa paligid ng puno. Panatilihing malamig ang ugat upang maiwasan ang tipburn at maagang pag-bolt."
        SpecificVegetable.CARROT -> "Pantay na pagdidilig sa pagitan ng mga linya. Huwag hayaang matuyuan nang labis upang hindi magbiyak-biyak (cracking) ang taproot."
        SpecificVegetable.POTATO -> "Katamtamang dilig sa paligid ng ridge. Huwag lunurin upang hindi mabulok ang mga lumalaking patatas sa ilalim ng lupa."
        SpecificVegetable.SQUASH -> "Malalim na dilig sa paligid ng burol. Iwasang basain ang malalapad na dahon upang maiwasan ang powdery mildew."
        SpecificVegetable.CABBAGE -> "Regular na pagpapanatili ng kahalumigmigan sa lupa. Ang pabago-bagong pagdidilig ay nagiging sanhi ng pagputok (splitting) ng ulo."
        SpecificVegetable.CUCUMBER -> "Pantay na dilig sa base ng lupa sa 6:30 AM. Ang kakulangan sa tubig ay nagdudulot ng mapait na lasa (cucurbitacin) at baluktot na pipino."
        SpecificVegetable.BITTER_GOURD -> "Malalim na dilig sa ugat sa ilalim ng balag tuwing 2 araw upang maging makatas at malalaki ang mga ampalaya."
        SpecificVegetable.STRING_BEANS -> "Dilig sa ugat sa ilalim ng balag. Huwag basain ang mga dahon upang hindi kumalat ang bean rust at anthracnose."
        SpecificVegetable.GREEN_ONION -> "Diligan sa ilalim ng tangkay. Iwasang mababad nang labis kapag lumalaki na upang maiwasan ang basal plate rot."
        else -> "Idilig sa base ng lupa sa ilalim ng mulch bago mag-init ang araw. Huwag basain ang dahon upang maiwasan ang fungal blight."
    }

    val waterTitle = when (specific) {
        SpecificVegetable.PECHAY -> "$cropName: Pagdidilig sa Base ng Lupa ($waterTime)"
        SpecificVegetable.LETTUCE -> "$cropName: Banayad na Pagdidilig sa Puno ($waterTime)"
        SpecificVegetable.CARROT -> "$cropName: Pantay na Pagdidilig sa Linya ($waterTime)"
        SpecificVegetable.POTATO -> "$cropName: Katamtamang Dilig sa Ridge ($waterTime)"
        SpecificVegetable.SQUASH -> "$cropName: Malalim na Pagdilig sa Burol ($waterTime)"
        SpecificVegetable.CABBAGE -> "$cropName: Pagpapanatili ng Tubig sa Lupa ($waterTime)"
        SpecificVegetable.CUCUMBER -> "$cropName: Regular na Pagdilig sa Base ($waterTime)"
        SpecificVegetable.BITTER_GOURD -> "$cropName: Malalim na Dilig sa Ilalim ng Balag ($waterTime)"
        SpecificVegetable.STRING_BEANS -> "$cropName: Pagdidilig sa Ugat sa Balag ($waterTime)"
        SpecificVegetable.GREEN_ONION -> "$cropName: Dilig sa Ilalim ng Tangkay ($waterTime)"
        SpecificVegetable.TOMATO -> "$cropName: Pagdidilig sa Ugat sa Ilalim ng Mulch ($waterTime)"
        SpecificVegetable.EGGPLANT -> "$cropName: Malalim na Pagdilig sa Ugat ($waterTime)"
        SpecificVegetable.BELL_PEPPER -> "$cropName: Pagdidilig sa Base ng Puno ($waterTime)"
        else -> "$cropName: Masinsing Pagdidilig sa Umaga ($waterTime)"
    }

    val maxWaterDay = totalDays + (if (profile.isMultiPick) profile.harvestLongevityDays.coerceAtMost(35) else 10)
    for (day in 1..maxWaterDay step waterInterval) {
        val taskDate = plantedDate.plusDays(day.toLong())
        items.add(AgendaScheduleItem("water-$day", taskDate, waterTime, day, AgendaTaskType.WATERING, waterTitle, waterInstruction, resolveStage(day)))
    }

    // ── 3. ROUTINE PEST & DISEASE SCOUTING (7:30 AM - CROP-SPECIFIC PESTS) ──
    val pestInterval = if (specific == SpecificVegetable.PECHAY || specific == SpecificVegetable.LETTUCE) 3 else 4
    val pestTitle = when (specific) {
        SpecificVegetable.TOMATO -> "Pagsusuri sa Sanga: Fruit Borer at Bacterial Wilt"
        SpecificVegetable.CARROT -> "Pagsusuri sa Dahon: Alternaria Blight at Cutworms"
        SpecificVegetable.EGGPLANT -> "Pagsusuri sa Dulo ng Sanga: Shoot at Fruit Borer (EFSB)"
        SpecificVegetable.SQUASH -> "Pagsusuri sa Dahon: Squash Beetle at Powdery Mildew"
        SpecificVegetable.STRING_BEANS -> "Pagsusuri sa Bunga: Pod Borer at Black Bean Aphids"
        SpecificVegetable.PECHAY -> "Pagsusuri sa Ilalim ng Dahon: Diamondback Moth (DBM)"
        SpecificVegetable.LETTUCE -> "Pagsusuri sa Puno: Aphids, Slugs, at Tipburn"
        SpecificVegetable.GREEN_ONION -> "Pagsusuri sa Singit ng Dahon: Thrips at Purple Blotch"
        SpecificVegetable.POTATO -> "Pagsusuri sa Lupa at Dahon: Tuber Moth at Late Blight"
        SpecificVegetable.BITTER_GOURD -> "Pagsusuri sa Baging: Melon Fruit Fly at Downy Mildew"
        SpecificVegetable.CUCUMBER -> "Pagsusuri sa Bunga: Cucumber Beetles at Downy Mildew"
        SpecificVegetable.BELL_PEPPER -> "Pagsusuri sa Bunga: Broad Mites, Thrips, at Anthracnose"
        SpecificVegetable.CABBAGE -> "Pagsusuri sa Dahon: Diamondback Moth at Cabbage Webworm"
        else -> "Pagsusuri sa Dahon at Peste sa Umaga"
    }
    val pestInstruction = when (specific) {
        SpecificVegetable.TOMATO -> "Suriin ang mga dulo ng sanga at ilalim ng dahon para sa whitefly; tingnan kung may butas ang batang bunga mula sa fruit borer."
        SpecificVegetable.CARROT -> "Suriin ang feathery leaves para sa dark brown spots (Alternaria) at tingnan kung may cutworms na pumupugot sa tangkay."
        SpecificVegetable.EGGPLANT -> "Suriin ang mga nalalantang dulo ng sanga mula sa uod ng shoot borer. Putulin at sunugin ang lantang sanga."
        SpecificVegetable.SQUASH -> "Suriin ang ilalim ng malalapad na dahon para sa dilaw-pulang squash beetle at pulbos na puti (powdery mildew)."
        SpecificVegetable.STRING_BEANS -> "Suriin ang mga bulaklak at pods para sa butas ng pod borer at kumpol ng itim na aphids sa mga dulo ng baging."
        SpecificVegetable.PECHAY -> "Suriin ang ilalim ng malalapad na dahon. Maghanap ng maliliit na berdeng uod (DBM) at butas. Mag-spray ng neem oil kung kailangan."
        SpecificVegetable.LETTUCE -> "Suriin ang base ng ulo para sa mga snails at slugs. Suriin ang gilid ng dahon kung nangingitim (tipburn dulot ng calcium deficit)."
        SpecificVegetable.GREEN_ONION -> "Suriin ang singit ng mga dahon para sa maliliit na thrips at lilang batik. Mag-spray ng sabon at neem solution."
        SpecificVegetable.POTATO -> "Suriin ang ibabang dahon para sa dark concentric rings (blight) at tiyaking walang nakalitaw na tuber na maaring dapuan ng gamu-gamo."
        SpecificVegetable.BITTER_GOURD -> "Suriin ang mga batang bunga kung may nangingitlog na fruit fly. Siguraduhing nakabalot ang mga bunga sa papel."
        SpecificVegetable.CUCUMBER -> "Suriin ang mga dahon para sa dilaw na angular spots (downy mildew) at tingnan kung may sumusutsot na cucumber beetles."
        SpecificVegetable.BELL_PEPPER -> "Suriin kung kulubot ang bagong usbong (broad mites) at tingnan ang dulo ng bunga laban sa blossom-end rot."
        SpecificVegetable.CABBAGE -> "Suriin ang loob ng mga nakabalot na dahon para sa uod ng repolyo at DBM. Maglagay ng organic BT spray kung may uod."
        else -> "Suriin ang ilalim ng dahon habang malamig pa ang panahon. Maghanap ng aphids, whitefly, o uod ng dahon."
    }
    for (day in 3..maxWaterDay step pestInterval) {
        val taskDate = plantedDate.plusDays(day.toLong())
        items.add(AgendaScheduleItem("pest-$day", taskDate, "7:30 AM", day, AgendaTaskType.PEST_SCOUT, "$cropName: $pestTitle", pestInstruction, resolveStage(day)))
    }

    // ── 4. CROP-SPECIFIC NUTRITION & FERTILIZATION (4:30 PM - 5:00 PM) ──
    when (specific) {
        SpecificVegetable.PECHAY -> {
            if (6 < totalDays) items.add(AgendaScheduleItem("fert-6", plantedDate.plusDays(6), "4:30 PM", 6, AgendaTaskType.FERTILIZER, "Unang Pataba: Vermicast Tea Extract", "Mag-drench ng vermicompost extract sa paligid ng ugat upang pasiglahin ang pagdami ng puting ugat.", resolveStage(6)))
            if (12 < totalDays) items.add(AgendaScheduleItem("fert-12", plantedDate.plusDays(12), "4:30 PM", 12, AgendaTaskType.FERTILIZER, "Foliar Nitrogen Boost: Fermented Plant Juice (FPJ)", "Mag-spray ng 2 kutsarang FPJ kada 1 litrong tubig sa hapon para sa malalapad at luntiang dahon.", resolveStage(12)))
            if (18 < totalDays) items.add(AgendaScheduleItem("fert-18", plantedDate.plusDays(18), "4:30 PM", 18, AgendaTaskType.FERTILIZER, "Fish Amino Acid (FAA) Side-dress", "Maglagay ng kaunting abonong organiko sa gilid ng tudling (5cm mula sa puno) para sa sigla ng tangkay.", resolveStage(18)))
            if (24 < totalDays) items.add(AgendaScheduleItem("fert-24", plantedDate.plusDays(24), "5:00 PM", 24, AgendaTaskType.FERTILIZER, "Panghuling Organic Booster bago Mag-ani", "Banayad na FPJ spray upang manatiling makatas at malutong ang tangkay bago anihin.", resolveStage(24)))
        }

        SpecificVegetable.LETTUCE -> {
            if (10 < totalDays) items.add(AgendaScheduleItem("fert-10", plantedDate.plusDays(10), "4:30 PM", 10, AgendaTaskType.FERTILIZER, "Starter Foliar: Diluted Compost Tea", "Mag-drench ng compost tea sa paligid ng punla upang mapalakas ang mga ugat sa bagong kama.", resolveStage(10)))
            if (18 < totalDays) items.add(AgendaScheduleItem("fert-18", plantedDate.plusDays(18), "4:30 PM", 18, AgendaTaskType.FERTILIZER, "Vegetative Foliar: FPJ Spray sa Hapon", "Mag-spray ng FPJ para sa malalapad na crisp leaves; iwasan ang sobrang nitrogen upang hindi pumait ang dahon.", resolveStage(18)))
            if (28 < totalDays) items.add(AgendaScheduleItem("fert-28", plantedDate.plusDays(28), "4:30 PM", 28, AgendaTaskType.FERTILIZER, "Calcium Drench laban sa Tipburn", "Mag-spray ng fermented eggshell calcium vinegar solution upang mapanatiling buo at malutong ang gilid ng dahon.", resolveStage(28)))
        }

        SpecificVegetable.CARROT -> {
            if (14 < totalDays) items.add(AgendaScheduleItem("fert-14", plantedDate.plusDays(14), "4:30 PM", 14, AgendaTaskType.FERTILIZER, "Starter Organic Feed: Compost Drench", "Diligan ng vermitea sa pagitan ng mga linya upang patatagin ang seedling emergence.", resolveStage(14)))
            if (35 < totalDays) items.add(AgendaScheduleItem("fert-35", plantedDate.plusDays(35), "4:30 PM", 35, AgendaTaskType.FERTILIZER, "Phosphorus Boost: Bone Meal Extract", "Maglagay ng phosphorus sa gilid ng tudling para sa malalim na pagbaon ng taproot.", resolveStage(35)))
            if (65 < totalDays) items.add(AgendaScheduleItem("fert-65", plantedDate.plusDays(65), "4:30 PM", 65, AgendaTaskType.FERTILIZER, "Potassium Boost: Abo ng Kahoy (Wood Ash Tea)", "Ihalo ang abo ng kahoy sa tudling para sa mabilis na paglaki ng laman at matingkad na kulay orange.", resolveStage(65)))
            if (85 < totalDays) items.add(AgendaScheduleItem("fert-85", plantedDate.plusDays(85), "5:00 PM", 85, AgendaTaskType.FERTILIZER, "Panghuling Maturation Feed: Fermented Fruit Juice", "Foliar mist ng FFJ upang mapataas ang tamis at beta-carotene bago ang takdang anihan.", resolveStage(85)))
        }

        SpecificVegetable.POTATO -> {
            if (10 < totalDays) items.add(AgendaScheduleItem("fert-10", plantedDate.plusDays(10), "4:30 PM", 10, AgendaTaskType.FERTILIZER, "Unang Pataba sa Ugat: Vermicast at Compost", "Ihalo ang 1 dakot ng vermicast sa paligid ng puno upang mapalakas ang mga umuusbong na tangkay.", resolveStage(10)))
            if (28 < totalDays) items.add(AgendaScheduleItem("fert-28", plantedDate.plusDays(28), "4:30 PM", 28, AgendaTaskType.FERTILIZER, "Potassium & Phosphorus Boost: Abo ng Kahoy", "Maglagay ng abo ng kahoy bago mag-hilling up para sa pagsisimula ng tuber initiation sa ilalim.", resolveStage(28)))
            if (50 < totalDays) items.add(AgendaScheduleItem("fert-50", plantedDate.plusDays(50), "4:30 PM", 50, AgendaTaskType.FERTILIZER, "Tuber Bulking Feed: Fermented Fruit Juice (FFJ)", "Mag-spray ng FFJ at bone meal extract sa dakong hapon para sa mabilis na paglaki ng mga laman ng patatas.", resolveStage(50)))
            if (75 < totalDays) items.add(AgendaScheduleItem("fert-75", plantedDate.plusDays(75), "5:00 PM", 75, AgendaTaskType.FERTILIZER, "Panghuling Potassium Side-dress: Abo at Bokashi", "Pampatigas ng balat at pampabigat ng laman ng mga tuber sa ilalim ng lupa.", resolveStage(75)))
        }

        SpecificVegetable.EGGPLANT, SpecificVegetable.TOMATO, SpecificVegetable.BELL_PEPPER -> {
            if (7 < totalDays) items.add(AgendaScheduleItem("fert-7", plantedDate.plusDays(7), "4:30 PM", 7, AgendaTaskType.FERTILIZER, "Unang Pagpapataba: Basal Vermicast", "Maglagay ng 1 dakot ng vermicast sa paligid ng drip line (5cm layo sa tangkay).", resolveStage(7)))
            if (21 < totalDays) items.add(AgendaScheduleItem("fert-21", plantedDate.plusDays(21), "4:30 PM", 21, AgendaTaskType.FERTILIZER, "Foliar Vegetative Booster: FPJ at Fish Amino", "Magtimpla ng 2 kutsarang FPJ sa 1 litrong tubig at i-spray sa dahon sa dakong hapon.", resolveStage(21)))
            if (38 < totalDays) items.add(AgendaScheduleItem("fert-38", plantedDate.plusDays(38), "4:30 PM", 38, AgendaTaskType.FERTILIZER, "Calcium Phosphate Spray (Eggshell Tea)", "Mag-spray ng fermented eggshell vinegar solution laban sa blossom-end rot at paglagas ng bulaklak.", resolveStage(38)))
            if (55 < totalDays) items.add(AgendaScheduleItem("fert-55", plantedDate.plusDays(55), "5:00 PM", 55, AgendaTaskType.FERTILIZER, "Potassium Boost: Fermented Fruit Juice (FFJ)", "Mag-spray ng FFJ para sa tamis, laki, at magandang kulay ng hinog na bunga.", resolveStage(55)))
        }

        SpecificVegetable.SQUASH -> {
            if (10 < totalDays) items.add(AgendaScheduleItem("fert-10", plantedDate.plusDays(10), "4:30 PM", 10, AgendaTaskType.FERTILIZER, "Unang Pataba sa Burol: Basal Compost", "Maglagay ng 2 dakot ng compost sa paligid ng bawat burol upang pasiglahin ang baging.", resolveStage(10)))
            if (25 < totalDays) items.add(AgendaScheduleItem("fert-25", plantedDate.plusDays(25), "4:30 PM", 25, AgendaTaskType.FERTILIZER, "Vine Vigor Booster: FPJ Spray sa Hapon", "Mag-spray ng Fermented Plant Juice upang lumakas ang pag-gapang at pagdami ng dahon.", resolveStage(25)))
            if (45 < totalDays) items.add(AgendaScheduleItem("fert-45", plantedDate.plusDays(45), "4:30 PM", 45, AgendaTaskType.FERTILIZER, "Flower Induction: Calcium at Bone Meal", "Mag-drench ng bone meal at eggshell tea upang lumakas ang kapit ng mga babaeng bulaklak.", resolveStage(45)))
            if (65 < totalDays) items.add(AgendaScheduleItem("fert-65", plantedDate.plusDays(65), "5:00 PM", 65, AgendaTaskType.FERTILIZER, "Fruit Bulking: Potassium Wood Ash Side-dress", "Maglagay ng abo ng kahoy sa paligid ng burol para sa matingkad na dilaw-orange na laman.", resolveStage(65)))
        }

        SpecificVegetable.BITTER_GOURD, SpecificVegetable.CUCUMBER, SpecificVegetable.STRING_BEANS -> {
            if (7 < totalDays) items.add(AgendaScheduleItem("fert-7", plantedDate.plusDays(7), "4:30 PM", 7, AgendaTaskType.FERTILIZER, "Unang Pagpapataba: Basal Vermicast", "Maglagay ng 1 dakot ng vermicast sa bawat burol ng tanim.", resolveStage(7)))
            if (20 < totalDays) items.add(AgendaScheduleItem("fert-20", plantedDate.plusDays(20), "4:30 PM", 20, AgendaTaskType.FERTILIZER, "Trellis Climbing Booster: FPJ Spray", "Mag-spray ng FPJ tuwing hapon upang mabilis na umakyat ang baging sa balag.", resolveStage(20)))
            if (35 < totalDays) items.add(AgendaScheduleItem("fert-35", plantedDate.plusDays(35), "4:30 PM", 35, AgendaTaskType.FERTILIZER, "Fruit Setting Boost: FFJ at Eggshell Calcium", "Mag-spray ng FFJ at eggshell tea sa dahon at bulaklak upang hindi malaglag ang mga batang bunga.", resolveStage(35)))
            if (48 < totalDays) items.add(AgendaScheduleItem("fert-48", plantedDate.plusDays(48), "5:00 PM", 48, AgendaTaskType.FERTILIZER, "Continuous Harvest Feed: Potassium Side-dress", "Maglagay ng organikong pataba at abo sa drip line para sa tuloy-tuloy na pamumunga.", resolveStage(48)))
        }

        SpecificVegetable.CABBAGE -> {
            if (10 < totalDays) items.add(AgendaScheduleItem("fert-10", plantedDate.plusDays(10), "4:30 PM", 10, AgendaTaskType.FERTILIZER, "Unang Pataba sa Kama: Vermicompost Drench", "Mag-drench ng vermicast extract sa paligid ng punla para sa mabilis na pag-ugat.", resolveStage(10)))
            if (25 < totalDays) items.add(AgendaScheduleItem("fert-25", plantedDate.plusDays(25), "4:30 PM", 25, AgendaTaskType.FERTILIZER, "Frame Leaf Booster: FPJ at Fish Amino", "Mag-spray ng FPJ para sa malalapad na panlabas na dahon na mag-iipon ng enerhiya para sa ulo.", resolveStage(25)))
            if (48 < totalDays) items.add(AgendaScheduleItem("fert-48", plantedDate.plusDays(48), "4:30 PM", 48, AgendaTaskType.FERTILIZER, "Head Packing Feed: Potassium at Calcium", "Maglagay ng abo ng kahoy at eggshell tea upang maging solid ang pagbalot ng ulo at walang tipburn.", resolveStage(48)))
        }

        SpecificVegetable.GREEN_ONION -> {
            if (8 < totalDays) items.add(AgendaScheduleItem("fert-8", plantedDate.plusDays(8), "4:30 PM", 8, AgendaTaskType.FERTILIZER, "Starter Organic Feed: Compost Tea", "Diligan ng compost tea sa gilid ng kama upang mapabilis ang pagdami ng puting ugat.", resolveStage(8)))
            if (20 < totalDays) items.add(AgendaScheduleItem("fert-20", plantedDate.plusDays(20), "4:30 PM", 20, AgendaTaskType.FERTILIZER, "Vegetative Foliar: FPJ Spray sa Tangkay", "Mag-spray ng FPJ para sa malalabay at matatag na berdeng dahon.", resolveStage(20)))
            if (38 < totalDays) items.add(AgendaScheduleItem("fert-38", plantedDate.plusDays(38), "5:00 PM", 38, AgendaTaskType.FERTILIZER, "Potassium Side-dress: Abo ng Kahoy", "Maglagay ng kaunting tuyong abo ng kahoy sa pagitan ng mga linya para sa makatas na tangkay.", resolveStage(38)))
        }

        else -> {
            if (10 < totalDays) items.add(AgendaScheduleItem("fert-10", plantedDate.plusDays(10), "4:30 PM", 10, AgendaTaskType.FERTILIZER, "Unang Pagpapataba: Basal Compost", "Maglagay ng 1 dakot ng compost sa paligid ng drip line.", resolveStage(10)))
            if (30 < totalDays) items.add(AgendaScheduleItem("fert-30", plantedDate.plusDays(30), "4:30 PM", 30, AgendaTaskType.FERTILIZER, "Mid-Season Organic Booster: FPJ / FFJ", "Foliar spray ng FPJ o FFJ sa dakong hapon para sa kalusugan ng tanim.", resolveStage(30)))
        }
    }

    // ── 5. CROP-SPECIFIC AGRONOMIC MILESTONES ──
    when (specific) {
        SpecificVegetable.PECHAY -> {
            if (8 < totalDays) items.add(AgendaScheduleItem("thin-8", plantedDate.plusDays(8), "7:30 AM", 8, AgendaTaskType.MILESTONE, "Pagpapanipis ng Punla (Thinning)", "Bunutin ang labis na punla upang magkaroon ng 10-15cm pagitan. Maaari nang lutuin ang mga nabunot bilang microgreens.", resolveStage(8), isKeyMilestone = true))
            if (12 < totalDays) items.add(AgendaScheduleItem("mulch-12", plantedDate.plusDays(12), "5:00 PM", 12, AgendaTaskType.MILESTONE, "Paglalagay ng Dayami (Rice Straw Mulch)", "Takpan ng tuyong dayami ang lupa upang hindi tumalsik ang putik sa dahon tuwing umuulan.", resolveStage(12)))
        }

        SpecificVegetable.LETTUCE -> {
            if (14 < totalDays) items.add(AgendaScheduleItem("shade-14", plantedDate.plusDays(14), "10:00 AM", 14, AgendaTaskType.MILESTONE, "Pagsasaayos ng 30% Shade Netting", "Maglagay ng lambat na pananggalang sa matinding init ng tanghali (11:00 AM - 2:00 PM) upang hindi pumait ang dahon.", resolveStage(14), isKeyMilestone = true))
        }

        SpecificVegetable.CARROT -> {
            if (25 < totalDays) items.add(AgendaScheduleItem("thin-25", plantedDate.plusDays(25), "7:30 AM", 25, AgendaTaskType.MILESTONE, "Mahalagang Pagpapanipis sa 5–8cm Pagitan", "Bunutin ang mga magkakadikit na punla. Kung hindi pagninipisin, magiging payat at pilipit ang mga karot.", resolveStage(25), isKeyMilestone = true))
            if (55 < totalDays) items.add(AgendaScheduleItem("cover-55", plantedDate.plusDays(55), "4:30 PM", 55, AgendaTaskType.MILESTONE, "Pagtabon sa Balikat ng Karot (Shoulder Earthing)", "Magtabon ng 2cm lupa sa nakalitaw na balikat ng karot upang hindi maging berde at mapait sa araw.", resolveStage(55), isKeyMilestone = true))
        }

        SpecificVegetable.POTATO -> {
            if (20 < totalDays) items.add(AgendaScheduleItem("hill-20", plantedDate.plusDays(20), "4:30 PM", 20, AgendaTaskType.MILESTONE, "Unang Pagtatabon ng Lupa sa Ugat (1st Hilling Up)", "Magtabon ng 7cm buhaghag na lupa sa paligid ng puno upang protektahan ang umuusbong na stolons.", resolveStage(20), isKeyMilestone = true))
            if (40 < totalDays) items.add(AgendaScheduleItem("hill-40", plantedDate.plusDays(40), "4:30 PM", 40, AgendaTaskType.MILESTONE, "Pangalawang Pagtatabon (2nd Hilling Up) at Mulch", "Dagdagan ng 5cm lupa at dayami. Siguraduhing walang nakalitaw na patatas upang hindi maging berde at nakalalason (solanine).", resolveStage(40), isKeyMilestone = true))
            if (totalDays > 75) {
                val dehaulmDay = totalDays - 10
                items.add(AgendaScheduleItem("dehaulm-$dehaulmDay", plantedDate.plusDays(dehaulmDay.toLong()), "7:30 AM", dehaulmDay, AgendaTaskType.PRUNING_TRELLIS, "De-haulming / Pagputol ng Tangkay Bago Mag-ani", "Gupitin ang mga tangkay sa ibabaw ng lupa 10 araw bago anihin upang tumigas ang balat ng patatas bago hukayin.", resolveStage(dehaulmDay), isKeyMilestone = true))
            }
        }

        SpecificVegetable.SQUASH -> {
            if (20 < totalDays) items.add(AgendaScheduleItem("guide-20", plantedDate.plusDays(20), "7:30 AM", 20, AgendaTaskType.PRUNING_TRELLIS, "Paggabay sa Baging sa Ibabaw ng Dayami", "Ayusin ang direksyon ng baging paikot sa burol sa ibabaw ng makapal na dayami upang hindi madikit sa putik.", resolveStage(20)))
            if (42 < totalDays) items.add(AgendaScheduleItem("poll-42", plantedDate.plusDays(42), "6:30 AM", 42, AgendaTaskType.MILESTONE, "Pagsusuri sa Polinasyon (Umaga 6:00-9:00 AM)", "Siguraduhing may bumibisitang bubuyog sa babaeng bulaklak (may munting kalabasa sa ilalim). Maaari ring mag-hand pollinate gamit ang lalaking bulaklak.", resolveStage(42), isKeyMilestone = true))
            if (60 < totalDays) items.add(AgendaScheduleItem("board-60", plantedDate.plusDays(60), "5:00 PM", 60, AgendaTaskType.MILESTONE, "Paglalagay ng Sapin sa Ilalim ng Bunga", "Maglagay ng piraso ng kahoy, tabla, o tuyong dayami sa ilalim ng bawat lumalaking kalabasa upang hindi mabulok sa basang lupa.", resolveStage(60), isKeyMilestone = true))
        }

        SpecificVegetable.BITTER_GOURD -> {
            if (16 < totalDays) items.add(AgendaScheduleItem("trellis-16", plantedDate.plusDays(16), "4:30 PM", 16, AgendaTaskType.PRUNING_TRELLIS, "Pagtatayo ng Balag / Overhead Trellis", "Magtayo ng 1.8m balag na kawayan at lambat. Igabay ang baging paakyat gamit ang malambot na panali.", resolveStage(16), isKeyMilestone = true))
            if (28 < totalDays) items.add(AgendaScheduleItem("prune-28", plantedDate.plusDays(28), "7:30 AM", 28, AgendaTaskType.PRUNING_TRELLIS, "Pagpupungos ng Sangay sa Unang 1 Metro", "Alisin ang mga lateral branches sa ibabang 1 metro ng puno upang umakyat ang sustansya sa balag.", resolveStage(28)))
            if (40 < totalDays) items.add(AgendaScheduleItem("bag-40", plantedDate.plusDays(40), "7:30 AM", 40, AgendaTaskType.MILESTONE, "Pagbabalot ng Bunga (Fruit Bagging)", "Balutan ng lumang dyaryo o plastic bag ang bawat batang bunga upang hindi itlugan ng fruit fly.", resolveStage(40), isKeyMilestone = true))
        }

        SpecificVegetable.CUCUMBER -> {
            if (12 < totalDays) items.add(AgendaScheduleItem("net-12", plantedDate.plusDays(12), "4:30 PM", 12, AgendaTaskType.PRUNING_TRELLIS, "Pagtatayo ng Vertical Trellis Netting", "Maglagay ng lambat o tukod na kawayan upang maiangat ang mga bunga sa lupa.", resolveStage(12), isKeyMilestone = true))
            if (22 < totalDays) items.add(AgendaScheduleItem("prune-22", plantedDate.plusDays(22), "7:30 AM", 22, AgendaTaskType.PRUNING_TRELLIS, "Pag-alis ng Sangay sa Ibabang 30cm", "Alisin ang dahon at side shoots sa pinakaibabang 30cm para sa magandang sirkulasyon ng hangin.", resolveStage(22)))
        }

        SpecificVegetable.STRING_BEANS -> {
            if (14 < totalDays) items.add(AgendaScheduleItem("trellis-14", plantedDate.plusDays(14), "4:30 PM", 14, AgendaTaskType.PRUNING_TRELLIS, "Pagtatayo ng A-Frame Trellis ng Sitaw", "Magtayo ng balag na kawayan (1.8m taas) at igabay ang mga baging paakyat sa direksyong clockwise.", resolveStage(14), isKeyMilestone = true))
        }

        SpecificVegetable.EGGPLANT, SpecificVegetable.TOMATO, SpecificVegetable.BELL_PEPPER -> {
            if (14 < totalDays) items.add(AgendaScheduleItem("mulch-14", plantedDate.plusDays(14), "5:00 PM", 14, AgendaTaskType.MILESTONE, "Paglalagay ng Rice Straw Mulch", "Lagyan ng 2 pulgadang tuyong dayami sa ibabaw ng kama upang mapanatili ang moisture.", resolveStage(14)))
            if (24 < totalDays) items.add(AgendaScheduleItem("stake-24", plantedDate.plusDays(24), "4:30 PM", 24, AgendaTaskType.PRUNING_TRELLIS, "Pagtatayo ng Bamboo Stakes / Tukod", "Ibaon ang tukod na kawayan at itali nang maluwag ang tangkay sa hugis-figure-8.", resolveStage(24), isKeyMilestone = true))
            if (34 < totalDays) items.add(AgendaScheduleItem("prune-34", plantedDate.plusDays(34), "7:30 AM", 34, AgendaTaskType.PRUNING_TRELLIS, "Pagpupungos ng Bottom Suckers", "Alisin ang mga suckers sa kili-kili ng sangay bago ang unang kumpol ng bulaklak para lumaki ang mga bunga.", resolveStage(34)))
        }

        SpecificVegetable.GREEN_ONION -> {
            if (25 < totalDays) items.add(AgendaScheduleItem("blanch-25", plantedDate.plusDays(25), "4:30 PM", 25, AgendaTaskType.MILESTONE, "Pagpapatambak ng Lupa sa Tangkay (Stem Blanching)", "Tabunan ng 4cm lupa ang ibabang bahagi ng tangkay upang maging maputi, makatas, at malutong ang leek.", resolveStage(25), isKeyMilestone = true))
        }

        SpecificVegetable.CABBAGE -> {
            if (14 < totalDays) items.add(AgendaScheduleItem("mulch-14", plantedDate.plusDays(14), "5:00 PM", 14, AgendaTaskType.MILESTONE, "Rice Straw Mulch sa Paligid ng Repolyo", "Takpan ng dayami ang lupa upang maiwasan ang pagtalsik ng spores ng clubroot at mapanatiling malamig ang ugat.", resolveStage(14)))
            if (50 < totalDays) items.add(AgendaScheduleItem("check-50", plantedDate.plusDays(50), "7:30 AM", 50, AgendaTaskType.MILESTONE, "Pagsusuri sa Head Cupping (Pagbalot ng Ulo)", "Suriin ang sentro habang nagsisimulang magbalot ang dahon. Panatilihing pantay ang dilig upang hindi magbiyak ang ulo.", resolveStage(50), isKeyMilestone = true))
        }

        else -> {}
    }

    // ── 6. FIRST HARVEST DAY (VEGETABLE-ACCURATE METHOD & CRITERIA) ──
    val harvestDate = plantedDate.plusDays(totalDays.toLong())
    val harvestTime = when (specific) {
        SpecificVegetable.POTATO, SpecificVegetable.CARROT -> "8:00 AM"
        else -> "6:00 AM"
    }
    val harvestTitle = when (specific) {
        SpecificVegetable.TOMATO -> "$cropName: UNANG ARAW NG PAG-AANI (First Tomato Flush)"
        SpecificVegetable.CARROT -> "$cropName: ARAW NG PAG-AANI (Paghukay ng Karot)"
        SpecificVegetable.EGGPLANT -> "$cropName: UNANG PAG-AANI NG TALONG (First Eggplant Harvest)"
        SpecificVegetable.SQUASH -> "$cropName: ARAW NG PAG-AANI NG KALABASA (Mature Harvest)"
        SpecificVegetable.STRING_BEANS -> "$cropName: UNANG ARAW NG PAGPITAS NG SITAW"
        SpecificVegetable.PECHAY -> "$cropName: ARAW NG PAG-AANI (Whole Head Pechay Harvest)"
        SpecificVegetable.LETTUCE -> "$cropName: ARAW NG PAG-AANI (Crisp Lettuce Harvest)"
        SpecificVegetable.GREEN_ONION -> "$cropName: ARAW NG PAG-AANI (Pagbunot ng Sibuyas Dahon)"
        SpecificVegetable.POTATO -> "$cropName: ARAW NG PAG-AANI (Paghukay ng mga Patatas)"
        SpecificVegetable.BITTER_GOURD -> "$cropName: UNANG PAG-AANI NG AMPALAYA"
        SpecificVegetable.CUCUMBER -> "$cropName: UNANG ARAW NG PAGPITAS NG PIPINO"
        SpecificVegetable.BELL_PEPPER -> "$cropName: UNANG ARAW NG PAGPITAS NG ATSAL"
        SpecificVegetable.CABBAGE -> "$cropName: ARAW NG PAG-AANI NG REPOLYO (Solid Head)"
        else -> "$cropName: UNANG ARAW NG PAG-AANI (Harvest Window)"
    }
    val harvestInstruction = when (specific) {
        SpecificVegetable.TOMATO -> "Pitasin ang mga hinog na kulay-pula o rosas sa umaga gamit ang malinis na gunting. Iwanan ang peduncle/calyx para sa shelf-life."
        SpecificVegetable.CARROT -> "Luwayan ang lupa gamit ang garden fork sa 15cm layo mula sa puno. Hugutin nang marahan upang hindi maputol ang taproot."
        SpecificVegetable.EGGPLANT -> "Pitasin habang makintab pa ang lilang balat bago maging dull o matigas ang buto. Gupitin nang may 2cm green calyx."
        SpecificVegetable.SQUASH -> "Pitasin kapag tuyo at mala-cork na ang tangkay at matigas na ang balat. Mag-iwan ng 5cm tangkay upang tumagal ng 2-4 buwan sa storage."
        SpecificVegetable.STRING_BEANS -> "Pitasin ang mga pod habang malalambot pa ang buto sa loob. Gamitin ang gunting upang hindi mapinsala ang baging."
        SpecificVegetable.PECHAY -> "Putulin sa may base ng ugat gamit ang matalas na kutsilyo sa maagang umaga bago uminit ang araw."
        SpecificVegetable.LETTUCE -> "Pitasin sa maagang umaga bago sumikat ang araw upang maiwasan ang mapait na katas. Putulin ang buong ulo o pitasin ang panlabas na dahon."
        SpecificVegetable.GREEN_ONION -> "Diligin nang kaunti ang lupa bago bunutin upang madaling mahugot nang buo ang ugat at hindi mapatid ang tangkay."
        SpecificVegetable.POTATO -> "Hukayin nang maingat gamit ang garden fork sa labas ng ridge. Patuyuin sa lilim (curing) nang ilang araw bago itago sa tuyong bodega."
        SpecificVegetable.BITTER_GOURD -> "Pitasin ang makintab na berdeng ampalaya tuwing umaga bago maging dilaw ang dulo ng prutas."
        SpecificVegetable.CUCUMBER -> "Pitasin ang mga 15-20cm cylindrical na pipino habang madilim na berde at malutong pa."
        SpecificVegetable.BELL_PEPPER -> "Gupitin ang makapal na bunga gamit ang gunting na may 2cm tangkay. Maaaring pitasin habang berde o hintaying maging pula/dilaw."
        SpecificVegetable.CABBAGE -> "Pitasin kapag matigas at solid sa pagdiin ng hinlalaki. Gupitin sa may puno at mag-iwan ng 2-3 protective wrapper leaves."
        else -> "Pitasin sa maagang umaga gamit ang malinis na gunting o kutsilyo habang sariwa pa ang tanim."
    }
    items.add(AgendaScheduleItem("harvest-first", harvestDate, harvestTime, totalDays, AgendaTaskType.HARVEST, harvestTitle, harvestInstruction, resolveStage(totalDays), isKeyMilestone = true))

    // ── 7. MULTI-PICK EXTENDED HARVEST FLUSHES ──
    val isMultiPick = profile.isMultiPick || when (specific) {
        SpecificVegetable.TOMATO, SpecificVegetable.EGGPLANT, SpecificVegetable.STRING_BEANS,
        SpecificVegetable.BITTER_GOURD, SpecificVegetable.CUCUMBER, SpecificVegetable.BELL_PEPPER -> true
        else -> false
    }

    if (isMultiPick) {
        val interval = when (specific) {
            SpecificVegetable.CUCUMBER -> 2
            SpecificVegetable.STRING_BEANS -> 2
            SpecificVegetable.BITTER_GOURD -> 3
            SpecificVegetable.TOMATO, SpecificVegetable.EGGPLANT -> 3
            SpecificVegetable.BELL_PEPPER -> 4
            else -> profile.pickingIntervalDays.coerceAtLeast(2)
        }
        val longevity = when (specific) {
            SpecificVegetable.EGGPLANT -> 45
            SpecificVegetable.TOMATO, SpecificVegetable.BELL_PEPPER -> 35
            SpecificVegetable.BITTER_GOURD, SpecificVegetable.STRING_BEANS -> 25
            SpecificVegetable.CUCUMBER -> 20
            else -> profile.harvestLongevityDays.coerceAtMost(30)
        }

        for (extraDay in (totalDays + interval)..(totalDays + longevity) step interval) {
            val pickDate = plantedDate.plusDays(extraDay.toLong())
            items.add(
                AgendaScheduleItem(
                    id = "pick-$extraDay",
                    date = pickDate,
                    timeStr = harvestTime,
                    dayNumber = extraDay,
                    type = AgendaTaskType.HARVEST,
                    title = "$cropName: Regular na Pagpitas / Harvest Flush ($extraDay Araw)",
                    tagalogInstruction = "Pitasin ang mga sumunod na hinog na bunga. Alisin ang anumang may sira o inuod na prutas upang hindi mahawa ang iba.",
                    stageName = resolveStage(extraDay)
                )
            )
        }
    }

    return items.sortedBy { it.date }
}
