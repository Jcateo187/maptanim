package com.maptanim.app.features.farm.dialogs.dss_checkup

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Coronavirus
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.ui.graphics.Color

private val ForestGreen = Color(0xFF1B5E20)
private val LushGreen = Color(0xFF2E7D32)
private val AlertAmber = Color(0xFFE65100)
private val DangerRed = Color(0xFFC62828)

/**
 * Diagnostic and Decision-Support System (DSS) activities tailored specifically
 * for Bitter Gourd / Ampalaya (Momordica charantia) in Philippine farming, commercial trellising,
 * and backyard gardens based on MapTanim technical data & DA-BPI extension guidelines.
 */
fun getAmpalayaCheckUpActivities(): List<CheckUpActivity> {
    return listOf(
        CheckUpActivity(
            id = "watering",
            title = "Patubig at Kahalumigmig ng Ampalaya",
            tagalogSubtitle = "Suriin ang moisture sa ugat at kalagayan ng malalapad na dahon at lumalaking bunga",
            icon = Icons.Default.WaterDrop,
            options = listOf(
                CheckUpOption(
                    id = "ampalaya_water_normal",
                    title = "Katamtaman at Regular na Kahalumigmig",
                    tagalogDesc = "Mamasa-masa ngunit buhaghag ang lupa sa paligid ng puno; dinidilig 2 hanggang 3 beses bawat linggo tuwing maagang umaga",
                    isIdeal = true
                ),
                CheckUpOption(
                    id = "ampalaya_water_stress",
                    title = "Tagtuyot / Pagkalanta ng Baging sa Tanghali",
                    tagalogDesc = "Naluluoy ang mga dulo ng baging at talbos; nalalaglag ang mga bulaklak; nagiging baluktot, maliit, at labis na mapait ang bunga"
                ),
                CheckUpOption(
                    id = "ampalaya_water_overwater",
                    title = "Sobrang Tubig / Pagkababad ng Ugat sa Baha",
                    tagalogDesc = "Naiipon ang tubig sa paligid ng puno; naninilaw ang lumang dahon at nabubulok ang ugat at base ng tangkay"
                ),
                CheckUpOption(
                    id = "ampalaya_water_foliar_wet",
                    title = "Panghapong Pagdidilig sa Dahon at Balag",
                    tagalogDesc = "Nababasa ang mga dahon bago lumubog ang araw, nagiging sanhi ng pamumuo ng amag at fungal leaf spots sa balag"
                )
            )
        ),
        CheckUpActivity(
            id = "trellising_and_pruning",
            title = "Balag (Trellis) at Paggagapas ng Sanga",
            tagalogSubtitle = "Suriin ang suporta ng balag at sirkulasyon ng hangin sa mga gumagapang na baging",
            icon = Icons.Default.Agriculture,
            options = listOf(
                CheckUpOption(
                    id = "ampalaya_trellis_ideal",
                    title = "Matibay na Balag at Maayos na Pruning",
                    tagalogDesc = "May matatag na balag (overhead o vertical A-frame, 1.5–2m); natanggal ang mga sanga sa ibabang 50–100 cm ng puno",
                    isIdeal = true
                ),
                CheckUpOption(
                    id = "ampalaya_trellis_dense_unpruned",
                    title = "Masukal na Ibabang Sanga (Unpruned Base)",
                    tagalogDesc = "Makakapal ang mga lateral shoots malapit sa lupa; kulang sa sirkulasyon ng hangin at dinadapuan ng amag at peste"
                ),
                CheckUpOption(
                    id = "ampalaya_trellis_sagging",
                    title = "Lalaylay o Babagsak na Balag sa Bigat",
                    tagalogDesc = "Maluwag o nababaling kawayan at lambat dahil sa bigat ng mayabong na baging at dagsa ng mga bunga"
                ),
                CheckUpOption(
                    id = "ampalaya_trellis_ground_trailing",
                    title = "Gumagapang sa Lupa Nang Walang Balag",
                    tagalogDesc = "Nasa ibabaw lamang ng lupa ang mga baging; nabubulok ang bunga sa basa at madaling dapuan ng insekto at putik"
                )
            )
        ),
        CheckUpActivity(
            id = "fertilizing",
            title = "Abono at Nutrisyon sa Pamumunga",
            tagalogSubtitle = "Suriin ang sustansya para sa malalabay na baging at matatabang berdeng ampalaya",
            icon = Icons.Default.Spa,
            options = listOf(
                CheckUpOption(
                    id = "ampalaya_fert_balanced",
                    title = "Balanse at Napapanahong Pag-aabono",
                    tagalogDesc = "May organikong compost sa taniman; may basal 14-14-14 at regular na side-dress ng Potassium at Nitrogen bawat 2–3 linggo",
                    isIdeal = true
                ),
                CheckUpOption(
                    id = "ampalaya_fert_nitrogen_def",
                    title = "Kakulangan sa Nitroheno (Naninilaw na Dahon)",
                    tagalogDesc = "Maputla at manilaw-nilaw ang matatandang dahon sa ilalim; manipis ang baging at mabagal ang paglabas ng talbos"
                ),
                CheckUpOption(
                    id = "ampalaya_fert_potassium_def",
                    title = "Kakulangan sa Potassium (Pangit ang Hubog ng Bunga)",
                    tagalogDesc = "Tuyo o sunog ang gilid ng mga dahon; payat at impis ang gitna ng bunga at hindi makapal ang mga tagaytay (ridges)"
                ),
                CheckUpOption(
                    id = "ampalaya_fert_boron_calcium_def",
                    title = "Kakulangan sa Boron at Kalsyo (Baluktot / Blossom Rot)",
                    tagalogDesc = "Baluktot na bunga na may bitak sa balat; itim o bulok ang dulo ng munting bunga; nalalaglag ang mga buko ng bulaklak"
                ),
                CheckUpOption(
                    id = "ampalaya_fert_excess_nitrogen",
                    title = "Sobrang Nitroheno (Puro Dahon, Walang Bunga)",
                    tagalogDesc = "Napakaitim at napakalalapad ng dahon sa balag ngunit kakaunti ang babaeng bulaklak at madaling malagas ang buko"
                )
            )
        ),
        CheckUpActivity(
            id = "pest_fruit_fly",
            title = "Melon Fruit Fly at Pamumuksa sa Bunga",
            tagalogSubtitle = "Suriin ang tusok ng langaw-bunga (Bactrocera cucurbitae) at kaligtasan ng ampalaya",
            icon = Icons.Default.BugReport,
            options = listOf(
                CheckUpOption(
                    id = "ampalaya_pest_no_damage",
                    title = "Malinis at Protektadong mga Bunga",
                    tagalogDesc = "Nakatakip o nakabalot ang mga bunga; walang marka ng tusok ng insekto; makikintab at diretso ang mga tagaytay",
                    isIdeal = true
                ),
                CheckUpOption(
                    id = "ampalaya_pest_fruit_fly_stings",
                    title = "May Tusok ng Fruit Fly (Uod sa Loob ng Bunga)",
                    tagalogDesc = "May maliliit na brown na tuldok o tagas ng dagta sa balat ng bunga; nagiging dilaw at nabubulok ang loob na may uod"
                ),
                CheckUpOption(
                    id = "ampalaya_pest_aphids_curling",
                    title = "Kuto sa Dahon / Aphids sa mga Talbos",
                    tagalogDesc = "Kumpol ng maliliit na berdeng kulisap sa ilalim ng dahon at talbos; kulubot ang dahon at may malagkit na amag (sooty mold)"
                ),
                CheckUpOption(
                    id = "ampalaya_pest_yellow_ladybeetle",
                    title = "Epilachna Beetle / Salagubang sa Dahon",
                    tagalogDesc = "Madilaw-kayumangging salagubang na may mga itim na tuldok; kumakain ng ibabaw ng dahon kaya nagiging parang lambat (skeletonized)"
                )
            )
        ),
        CheckUpActivity(
            id = "diseases",
            title = "Sakit sa Dahon at Pagkabulok ng Baging",
            tagalogSubtitle = "Suriin ang amag, downy mildew, powdery mildew, at pamumutla sa balag",
            icon = Icons.Default.Coronavirus,
            options = listOf(
                CheckUpOption(
                    id = "ampalaya_disease_healthy",
                    title = "Malusog at Luntiang Baging sa Balag",
                    tagalogDesc = "Walang tagpi-tagping dilaw o puting amag sa dahon; matitibay ang tangkay at tuloy-tuloy ang pagtalbos",
                    isIdeal = true
                ),
                CheckUpOption(
                    id = "ampalaya_disease_downy_mildew",
                    title = "Downy Mildew (Kanto-kantong Dilaw na Tagpi)",
                    tagalogDesc = "Angular o kanto-kantong dilaw na mantsa na hinaharang ng ugat ng dahon; may abuhing amag sa ilalim sa mahalumigmig na panahon"
                ),
                CheckUpOption(
                    id = "ampalaya_disease_powdery_mildew",
                    title = "Powdery Mildew (Puting Pulbos sa Ibabaw ng Dahon)",
                    tagalogDesc = "Maputing parang polbo o harina na bumabalot sa ibabaw ng mga dahon at tangkay; natutuyo at nalalanta ang apektadong dahon"
                ),
                CheckUpOption(
                    id = "ampalaya_disease_bacterial_wilt",
                    title = "Bacterial Wilt / Biglaang Pagkamatay ng Baging",
                    tagalogDesc = "Biglang naluluoy ang buong baging sa katanghalian kahit basa ang lupa; pumuputok o naglalabas ng puting malagkit na likido sa tangkay"
                )
            )
        ),
        CheckUpActivity(
            id = "pollination_and_flowers",
            title = "Pamumulaklak at Pagtatagumpay ng Polinasyon",
            tagalogSubtitle = "Suriin ang aktibidad ng mga bubuyog at pamumuo ng babaeng bulaklak sa balag",
            icon = Icons.Default.Eco,
            options = listOf(
                CheckUpOption(
                    id = "ampalaya_poll_active_bees",
                    title = "Masiglang Polinasyon ng mga Bubuyog",
                    tagalogDesc = "Maraming pukyutan at ligaw na bubuyog na lumilipad sa mga dilaw na bulaklak sa umaga (6:00–9:00 AM); lumalaki ang munting bunga",
                    isIdeal = true
                ),
                CheckUpOption(
                    id = "ampalaya_poll_flower_drop",
                    title = "Pagkalagas ng Buko ng Babaeng Bulaklak",
                    tagalogDesc = "Naninilaw at nalalaglag ang munting buko ng bunga bago o pagkatapos bumuka ang bulaklak dahil sa kakulangan ng polinasyon"
                ),
                CheckUpOption(
                    id = "ampalaya_poll_rain_washout",
                    title = "Malakas na Ulan sa Umaga (Nahugasang Pollen)",
                    tagalogDesc = "Nababasa at nahuhugasan ang pollen sa lalaking bulaklak dulot ng malalakas na buhos ng ulan mula 6:00 hanggang 9:00 AM"
                ),
                CheckUpOption(
                    id = "ampalaya_poll_heat_abortion",
                    title = "Matinding Init (>35°C) sa Panahon ng Pamumulaklak",
                    tagalogDesc = "Naluluto ang pollen at nalalanta ang maseselang tangkay ng bulaklak sa sobrang init at tuyong hangin"
                )
            )
        ),
        CheckUpActivity(
            id = "harvesting",
            title = "Yugto ng Pag-aani at Kalidad ng Bunga",
            tagalogSubtitle = "Suriin ang tamang gulang, lutong, at paraan ng pagpitas sa balag",
            icon = Icons.Default.ContentCut,
            options = listOf(
                CheckUpOption(
                    id = "ampalaya_harvest_ready_green",
                    title = "Tugma sa Gulang at Matingkad na Berde",
                    tagalogDesc = "Matambok at makakapal ang mga tagaytay (ridges); matigas at malutong ang laman; matingkad na berde (hindi naninilaw)",
                    isIdeal = true
                ),
                CheckUpOption(
                    id = "ampalaya_harvest_overmature_yellow",
                    title = "Overmature / Nagsisimulang Manilaw ang Dulo",
                    tagalogDesc = "Pumuputla at nagiging dilaw o kahel ang dulo ng bunga; malambot na ang laman at namumula na ang buto sa loob"
                ),
                CheckUpOption(
                    id = "ampalaya_harvest_short_peduncle",
                    title = "Punit o Walang Tangkay sa Pag-aani",
                    tagalogDesc = "Hinila ang bunga sa halip na gupitin; napunit ang balat sa base kaya mabilis mabulok at malanta sa biyahe"
                ),
                CheckUpOption(
                    id = "ampalaya_harvest_crooked_culls",
                    title = "Baluktot o Kurbadong Bunga (Culls)",
                    tagalogDesc = "Kurbado na hugis titik C o baluktot ang bunga dulot ng sagabal sa lambat, kakulangan sa tubig, o bahagyang tusok ng insekto"
                )
            )
        )
    )
}

/**
 * Diagnostic logic providing instant, research-backed Philippine solutions
 * for Bitter Gourd / Ampalaya (Momordica charantia) based on MapTanim technical data.
 */
fun solveAmpalayaProblem(
    activityId: String,
    selectedOptions: Set<String>
): DirectCropSolution? {
    if (selectedOptions.isEmpty()) return null

    when (activityId) {
        "watering" -> {
            if (selectedOptions.contains("ampalaya_water_stress")) {
                return DirectCropSolution(
                    title = "Ampalaya Water Stress & Blossom Abortion Recovery",
                    tagalogTitle = "Solusyon sa Tagtuyot at Pagkalagas ng Bulaklak ng Ampalaya",
                    severity = "CRITICAL",
                    badgeColor = DangerRed,
                    directAnswer = "Ang ampalaya ay naglalaman ng higit 92% tubig sa lumalaking bunga. Kapag natuyo ang mababaw na ugat, agad na inilalaglag ng baging ang mga bulaklak, bumabaluktot ang bunga, at nagiging labis na mapait ang laman.",
                    actionSteps = listOf(
                        "Magdilig nang malalim gamit ang furrow o drip irrigation tuwing maagang umaga (6:00–7:30 AM).",
                        "Panatilihin ang 2 hanggang 3 beses bawat linggong patubig lalo na sa panahon ng pamumunga.",
                        "Maglagay ng 5–8 cm makapal na dayami (rice straw) o ipa (rice hull) sa paligid ng puno upang mapanatiling malamig ang lupa.",
                        "Iwasang magpatuyo ng lupa nang higit 3 araw sa panahon ng pamumulaklak."
                    ),
                    materialsNeeded = listOf("Drip Line o Hose", "Dayami (Rice Straw) o Ipa", "Mulch Fork"),
                    organicRemedy = "Paglalagay ng makapal na organic mulch upang mapigilan ang pagsingaw ng kahalumigmig sa lupa.",
                    criticalRule = "Huwag magdilig sa kasagsagan ng tanghali upang maiwasan ang root shock at pagkakaluto ng ugat."
                )
            }
            if (selectedOptions.contains("ampalaya_water_overwater")) {
                return DirectCropSolution(
                    title = "Drainage Improvement & Root Rot Control",
                    tagalogTitle = "Lunas sa Pagkababad ng Ugat sa Baha at Root Rot",
                    severity = "WARNING",
                    badgeColor = AlertAmber,
                    directAnswer = "Ang basang-basa at nakatenggang tubig sa paligid ng puno ay nagdudulot ng asphyxiation sa ugat at nag-aanyaya ng nakamamatay na Fusarium at Pythium root rot.",
                    actionSteps = listOf(
                        "Maghukay agad ng 20–30 cm na drainage furrows sa pagitan ng mga hanay ng balag upang mabilis umagos ang tubig-ulan.",
                        "Itaas ang mga bunton o kama (raised mounds/hills) nang 20 cm sa susunod na cropping.",
                        "Itigil pansamantala ang pagdidilig hanggang sa matuyo ang ibabaw na 5 cm ng lupa.",
                        "Mag-drench ng Trichoderma harzianum bio-fungicide sa base ng puno upang maprotektahan ang mga ugat laban sa fungi."
                    ),
                    materialsNeeded = listOf("Pala para sa Kanal", "Trichoderma Harzianum Bio-fungicide"),
                    organicRemedy = "Trichoderma drench sa basang lupa upang labanan ang mga pathogen na nagdudulot ng root rot.",
                    criticalRule = "Huwag hayaang magtubig ang paligid ng puno nang higit 12 oras."
                )
            }
            if (selectedOptions.contains("ampalaya_water_foliar_wet")) {
                return DirectCropSolution(
                    title = "Morning Ground Irrigation Protocol",
                    tagalogTitle = "Paglipat sa Patubig sa Lupa Tuwing Umaga",
                    severity = "INFO",
                    badgeColor = LushGreen,
                    directAnswer = "Ang pagkabasa ng dahon sa dapit-hapon ang pangunahing dahilan ng pagsiklab ng Downy Mildew at Powdery Mildew sa balag ng ampalaya.",
                    actionSteps = listOf(
                        "Ilipat ang pagdidilig sa maagang umaga mula 6:00 hanggang 7:30 AM lamang.",
                        "Itutok ang tubig sa lupa sa paanan ng puno; iwasang wisikan ang mga baging sa balag.",
                        "Gupitin ang mga apektadong dahon sa ibaba upang makasingaw ang hangin.",
                        "Tiyaking tuyo ang buong dahon bago lumubog ang araw."
                    ),
                    materialsNeeded = listOf("Hose na may Ground Wand o Drip System"),
                    organicRemedy = "Direct-to-root irrigation sa halip na overhead sprinkler.",
                    criticalRule = "Bawal basain ang mga dahon ng ampalaya pagkalipas ng alas-3:00 ng hapon."
                )
            }
        }
        "trellising_and_pruning" -> {
            if (selectedOptions.contains("ampalaya_trellis_dense_unpruned")) {
                return DirectCropSolution(
                    title = "Lower Canopy Pruning Protocol for Ampalaya",
                    tagalogTitle = "Tamang Paggagapas ng mga Sanga sa Ibaba (Pruning)",
                    severity = "WARNING",
                    badgeColor = AlertAmber,
                    directAnswer = "Ang mga sanga sa ibabang 50–100 cm ng puno ay kumukuha ng sustansya ngunit hindi nagbubunga nang maayos, at nagiging lungga ng mga amag at insekto dahil sa kawalan ng sirkulasyon ng hangin.",
                    actionSteps = listOf(
                        "Gamit ang malinis na gunting o matalas na pruning shears, putulin ang lahat ng lateral branches mula sa lupa hanggang 1 metro ang taas.",
                        "Panatilihin lamang ang nag-iisang pangunahing tangkay (main stem) paakyat sa balag.",
                        "Kapag umabot na ang main stem sa itaas ng balag, kurutin ang dulo (pinch tip) upang magsangay nang masagana sa ibabaw ng lambat.",
                        "Alisin ang lahat ng naninilaw o may sakit na dahon sa paanan ng puno at ilibing sa malayo."
                    ),
                    materialsNeeded = listOf("Pruning Shears", "Alcohol para sa Disinfection", "Plastic Bag para sa Basura"),
                    organicRemedy = "Pruning ng masusukal na sanga para sa 100% natural na sirkulasyon ng araw at hangin.",
                    criticalRule = "Mag-prune lamang tuwing tuyo ang panahon sa umaga upang mabilis maghilom ang sugat ng baging nang walang impeksyon."
                )
            }
            if (selectedOptions.contains("ampalaya_trellis_sagging")) {
                return DirectCropSolution(
                    title = "Trellis Reinforcement & Guy-Wire Tightening",
                    tagalogTitle = "Pagpapatibay at Pag-alalay sa Bumibigat na Balag",
                    severity = "WARNING",
                    badgeColor = AlertAmber,
                    directAnswer = "Ang bigat ng malalagong baging at daan-daang bunga ay maaaring magpabagsak sa balag, na sumisira sa buong sakahan kapag nabuwal.",
                    actionSteps = listOf(
                        "Maglagay ng mga dagdag na poste ng kawayan (bamboo prop posts) tuwing 2 hanggang 3 metro sa kahabaan ng balag.",
                        "Higpitan ang mga alambre o nylon cords sa bawat sulok gamit ang matitibay na tulos (guy-wire stakes).",
                        "I-prune ang mga labis na lumang dahon sa ibabaw upang gumaan ang canopy.",
                        "Itali ang mga sanga gamit ang malalambot na straw twine."
                    ),
                    materialsNeeded = listOf("Poste ng Kawayan (Bamboo Poles)", "Nylon Cord #200 o GI Wire #16", "Tulos (Stakes)"),
                    organicRemedy = "Pagpapatibay ng balag gamit ang matitigas na kawayan at abaka.",
                    criticalRule = "Huwag hintaying bumagsak ang balag; patatagin agad habang lumalaki ang mga bunga."
                )
            }
            if (selectedOptions.contains("ampalaya_trellis_ground_trailing")) {
                return DirectCropSolution(
                    title = "Urgent Trellis Installation for Ampalaya Vines",
                    tagalogTitle = "Kagyat na Pagtatayo ng Balag para sa Gumagapang na Baging",
                    severity = "CRITICAL",
                    badgeColor = DangerRed,
                    directAnswer = "Ang ampalaya ay likas na climbing vine. Kapag gumapang sa lupa, higit 60% ng mga bunga ang mabubulok, mababaluktot, mapuputla sa isang tabi, at madaling kakainin ng fruit flies at daga.",
                    actionSteps = listOf(
                        "Magtayo agad ng vertical trellis (1.8m taas na may lambat) o overhead balag gamit ang kawayan at nylon trellis netting (mesh 15–20 cm).",
                        "I-gabay at itali nang malumanay ang mga dulo ng baging paakyat sa poste gamit ang malambot na straw.",
                        "Linisin ang mga dahong nadikit sa putik.",
                        "Maglagay ng mulch sa ilalim ng bagong tayong balag."
                    ),
                    materialsNeeded = listOf("Kawayan", "Nylon Trellis Netting (Balag Net)", "Straw Cord"),
                    organicRemedy = "Pag-aangat ng baging sa trellis upang maiwasan ang fruit rot at contact fungal pathogens.",
                    criticalRule = "Kailangan ng balag ang ampalaya upang maging tuwid at de-kalidad ang mga bunga."
                )
            }
        }
        "fertilizing" -> {
            if (selectedOptions.contains("ampalaya_fert_nitrogen_def")) {
                return DirectCropSolution(
                    title = "Nitrogen Boost for Vegetative Vine Growth",
                    tagalogTitle = "Lunas sa Naninilaw na Dahon at Mahinang Baging (Kakulangan sa Nitroheno)",
                    severity = "WARNING",
                    badgeColor = AlertAmber,
                    directAnswer = "Ang paninilaw ng matatandang dahon sa ilalim at mabagal na pagtalbos ay tanda ng kakulangan sa Nitroheno, na kailangan para sa malalagong baging.",
                    actionSteps = listOf(
                        "Mag-side-dress ng Urea (46-0-0) o Ammonium Sulfate (21-0-0) sa daming 10–15 gramo bawat puno, 15 cm ang layo sa base.",
                        "O kaya ay magdilig ng Fermented Plant Juice (FPJ gawa sa talbos ng kangkong/kakawate) na tinimpla sa 2 kutsara bawat litro ng tubig.",
                        "Diligan agad pagkatapos maglagay ng pataba upang masipsip ng mga ugat.",
                        "Ulitin tuwing ika-14 na araw hanggang sa manariwa ang kulay ng mga dahon."
                    ),
                    materialsNeeded = listOf("Urea o Ammonium Sulfate", "Fermented Plant Juice (FPJ)", "Regadera"),
                    organicRemedy = "FPJ mula sa kangkong o kakawate o vermitea drench.",
                    criticalRule = "Huwag ilagay ang abono nang dikit sa puno upang hindi masunog ang malambot na tangkay."
                )
            }
            if (selectedOptions.contains("ampalaya_fert_potassium_def")) {
                return DirectCropSolution(
                    title = "Potassium Supplementation for Fruit Bulking & Ridging",
                    tagalogTitle = "Pagpapataas ng Potassium para sa Makakapal at Tuwid na Bunga",
                    severity = "WARNING",
                    badgeColor = AlertAmber,
                    directAnswer = "Ang Potassium ang nagpapakapal sa mga tagaytay (ridges), nagpapatuwid sa hugis, at nagbibigay ng tamang lutong at bigat sa bunga ng ampalaya.",
                    actionSteps = listOf(
                        "Maglagay ng Muriate of Potash (0-0-60) sa daming 10 gramo bawat puno tuwing ika-2 linggo sa panahon ng pamumunga.",
                        "Bilang organikong alternatibo, mag-spray ng Fermented Fruit Juice (FFJ gawa sa hinog na saging/papaya) o magbudbod ng pinong abo ng kahoy sa paligid ng puno.",
                        "Panatilihing pantay ang patubig upang mabilis mahigop ang potassium.",
                        "Gupitin ang mga sirang lumang dahon na may tuyong gilid."
                    ),
                    materialsNeeded = listOf("0-0-60 Muriate of Potash", "Fermented Fruit Juice (FFJ)", "Wood Ash (Abo ng Kahoy)"),
                    organicRemedy = "FFJ mula sa hinog na saging at malinis na abo ng kahoy na mayaman sa natural na potassium.",
                    criticalRule = "Kailangan ng mataas na potassium mula simula ng pamumulaklak hanggang huling anihan."
                )
            }
            if (selectedOptions.contains("ampalaya_fert_boron_calcium_def")) {
                return DirectCropSolution(
                    title = "Boron & Calcium Correction for Blossom-End Rot & Distortion",
                    tagalogTitle = "Lunas sa Baluktot na Bunga, Bitak, at Pagkabulok ng Dulo",
                    severity = "WARNING",
                    badgeColor = AlertAmber,
                    directAnswer = "Ang baluktot na bunga, bitak sa balat, at pagkaitim ng dulo ay dulot ng kakulangan sa micronutrient na Boron at Kalsyo habang mabilis na lumalaki ang bunga.",
                    actionSteps = listOf(
                        "Mag-foliar spray ng Solubor o Borax (1 gramo bawat litro ng tubig) tuwing hapon bago bumuka ang mga bulaklak.",
                        "Mag-spray ng Water-Soluble Calcium (WCA gawa sa piniritong balat ng itlog at suka, 10ml bawat litro) minsan sa isang linggo.",
                        "Panatilihing pantay ang patubig dahil ang tagtuyot ang humahadlang sa pag-akyat ng calcium sa lumalaking dulo ng bunga.",
                        "I-mulch ang paligid ng puno upang maiwasan ang biglaang pagkatuyo ng lupa."
                    ),
                    materialsNeeded = listOf("Solubor / Borax", "Water-Soluble Calcium (WCA)", "Sprayer"),
                    organicRemedy = "Foliar spray ng WCA (calcium mula sa balat ng itlog) at wood ash tea.",
                    criticalRule = "Huwag sumobra sa Boron; 1g bawat litro lamang upang hindi magdulot ng toxicity sa dahon."
                )
            }
            if (selectedOptions.contains("ampalaya_fert_excess_nitrogen")) {
                return DirectCropSolution(
                    title = "Excess Nitrogen Correction & Bloom Stimulation",
                    tagalogTitle = "Solusyon sa Sobrang Dahon at Kakarampot na Bunga (Vegetative Dominance)",
                    severity = "WARNING",
                    badgeColor = AlertAmber,
                    directAnswer = "Kapag labis ang Urea o dumi ng manok, nagiging sobrang madilim ang lunti ng dahon ngunit nalalaglag ang mga bulaklak dahil puro vegetative growth ang inaatupag ng halaman.",
                    actionSteps = listOf(
                        "Itigil agad ang paglalagay ng Urea at sariwang dumi ng hayop.",
                        "Maglagay ng abono na mayaman sa Phosphorus at Potassium tulad ng 0-0-60 at Solophos (0-20-0) upang pasiglahin ang pamumunga.",
                        "Mag-prune ng 20–30% ng pinakamasusukal na dahon sa itaas ng balag upang pumasok ang sikat ng araw sa mga bulaklak.",
                        "Mag-spray ng Fermented Fruit Juice (FFJ) upang hikayatin ang paglabas ng mga babaeng bulaklak."
                    ),
                    materialsNeeded = listOf("0-0-60 Muriate of Potash", "Solophos (0-20-0)", "Pruning Shears"),
                    organicRemedy = "Pagbabawas ng nitrogen at pag-spray ng calcium-phosphate tea mula sa sinunog na buto ng hayop.",
                    criticalRule = "Huwag maglagay ng urea kapag namumulaklak na ang balag."
                )
            }
        }
        "pest_fruit_fly" -> {
            if (selectedOptions.contains("ampalaya_pest_fruit_fly_stings")) {
                return DirectCropSolution(
                    title = "Melon Fruit Fly (Bactrocera cucurbitae) Eradication Protocol",
                    tagalogTitle = "Pambansang Pamamaraan Laban sa Melon Fruit Fly (Langaw-Bunga)",
                    severity = "CRITICAL",
                    badgeColor = DangerRed,
                    directAnswer = "Ang Melon Fruit Fly ang numero unong kalaban ng ampalaya sa Pilipinas. Nangingitlog ang babaeng langaw sa loob ng batang bunga; ang mga uod (maggots) ang sumisira at nagpapabulok sa laman.",
                    actionSteps = listOf(
                        "Magbalot ng bunga (Fruit Bagging): Balutin ang bawat munting bunga gamit ang paper bags, dyaryo, o fine mesh sleeves sa ika-3 hanggang ika-5 araw pagkatapos bumuka ang bulaklak. Bukas ang ilalim para tumulo ang tubig.",
                        "Sanitasyon sa Bukid: Pulutin at ilibing sa lalim na 50 cm o sunugin ang LAHAT ng nalaglag o nabulok na bunga; huwag iwan sa lupa dahil lalabas ang mga bagong langaw.",
                        "Magtayo ng Methyl Eugenol / Protein Bait Traps sa paligid ng taniman upang maakit at mapuksa ang mga lalaking fruit fly.",
                        "Mag-spray ng attractant bait spray (protein bait + spinosad) sa labas ng taniman tuwing umaga."
                    ),
                    materialsNeeded = listOf("Paper Bagging Sleeves o Dyaryo", "Methyl Eugenol Pheromone Traps", "Pala para sa Paglilibing"),
                    organicRemedy = "100% mechanical fruit bagging gamit ang dyaryo at pheromone lure traps na walang kemikal sa mismong bunga.",
                    criticalRule = "Ang pagbabalot ng bunga ang tanging sigurado at pinakamabisang panangga laban sa fruit fly."
                )
            }
            if (selectedOptions.contains("ampalaya_pest_aphids_curling")) {
                return DirectCropSolution(
                    title = "Aphid Colony Control & Virus Prevention",
                    tagalogTitle = "Pagsugpo sa Kuto sa Dahon (Aphids) at Sooty Mold",
                    severity = "WARNING",
                    badgeColor = AlertAmber,
                    directAnswer = "Ang aphids ay sumisipsip ng katas sa mga talbos at nagpapasalin ng Papaya Ringspot Virus - Cucurbit Strain (PRSV-W) na nagpapakulot sa dahon ng ampalaya.",
                    actionSteps = listOf(
                        "Wisikan ng banayad ngunit madiing pressure ng tubig ang ilalim ng mga apektadong dahon tuwing umaga.",
                        "Mag-spray ng insecticidal soap o neem oil extract (5ml neem + 2ml liquid soap bawat litro ng tubig) tuwing hapon.",
                        "Magkabit ng dilaw na sticky traps (yellow sticky traps) sa lebel ng balag upang mahuli ang mga may pakpak na aphids.",
                        "Gupitin at sunugin ang mga talbos na labis nang pinamumugaran."
                    ),
                    materialsNeeded = listOf("Neem Oil Extract", "Biodegradable Soap", "Yellow Sticky Traps", "Sprayer"),
                    organicRemedy = "Neem oil spray (0.5%) na may sabon at konserbasyon ng ladybird beetles.",
                    criticalRule = "Iwasan ang sobrang nitroheno na nagpapatambok at nagpapasarap sa talbos para sa aphids."
                )
            }
            if (selectedOptions.contains("ampalaya_pest_yellow_ladybeetle")) {
                return DirectCropSolution(
                    title = "Epilachna Beetle Leaf-Scraping Management",
                    tagalogTitle = "Pamuksa sa Epilachna Beetle (Salagubang na Kumakain ng Dahon)",
                    severity = "WARNING",
                    badgeColor = AlertAmber,
                    directAnswer = "Ang 28-spotted ladybeetle (Epilachna) ay kumakain ng ibabaw ng dahon, nag-iiwan ng mistulang lambat na nagpapabagal sa photosynthesis ng ampalaya.",
                    actionSteps = listOf(
                        "Handpick: Pulutin ang mga salagubang at ang kanilang madidilaw na matinik na uod tuwing maagang umaga kapag mabagal pa silang gumalaw; ilagay sa timba na may tubig at sabon.",
                        "Tingnan ang ilalim ng mga dahon at pisain ang mga kumpol ng madidilaw na itlog.",
                        "Mag-spray ng hot pepper extract (sili + luya + sabon) o neem oil tuwing hapon upang maitaboy ang mga salagubang.",
                        "Alisin ang mga damong solanaceous at cucurbit sa paligid ng sakahan."
                    ),
                    materialsNeeded = listOf("Timba na may Tubig at Sabon", "Sili at Bawang Spray", "Guwantes"),
                    organicRemedy = "Handpicking sa umaga at pag-spray ng botanical chili-garlic extract.",
                    criticalRule = "Huwag ipagkamali sa kapaki-pakinabang na pulang ladybug; ang Epilachna ay may 28 maliliit na itim na tuldok at mapusyaw na dilaw-kahel."
                )
            }
        }
        "diseases" -> {
            if (selectedOptions.contains("ampalaya_disease_downy_mildew")) {
                return DirectCropSolution(
                    title = "Downy Mildew (Pseudoperonospora cubensis) Canopy Defense",
                    tagalogTitle = "Gamot sa Downy Mildew sa Balag ng Ampalaya",
                    severity = "CRITICAL",
                    badgeColor = DangerRed,
                    directAnswer = "Ang Downy Mildew ay mabilis kumalat sa malamig at mahamog na gabi o maulang panahon. Lumilikha ito ng kanto-kantong dilaw na tagpi na nagpapatuyo sa buong balag sa loob lamang ng ilang araw.",
                    actionSteps = listOf(
                        "Gupitin at tanggalin agad ang lahat ng apektadong dahon sa ibaba; ilagay sa sako at sunugin o ilibing sa malayo.",
                        "Mag-spray ng Copper Hydroxide o Copper Oxychloride protectant fungicide tuwing maagang umaga.",
                        "Bilang organikong lunas, mag-spray ng Baking Soda solution (5g baking soda + 2ml liquid soap bawat litro ng tubig) tuwing ika-5 araw.",
                        "Pataasin ang sirkulasyon ng hangin sa pamamagitan ng pag-prune ng masusukal na sanga sa ibabaw ng balag.",
                        "Iwasang magbasa ng dahon kapag nagdidilig."
                    ),
                    materialsNeeded = listOf("Copper Fungicide", "Baking Soda", "Liquid Soap", "Pruning Shears"),
                    organicRemedy = "Baking soda (sodium bicarbonate) solution na may sabon bilang bio-fungicide.",
                    criticalRule = "Huwag kailanman ihalo sa compost ang mga dahong may downy mildew; mabilis kumalat ang mga spores nito sa hangin."
                )
            }
            if (selectedOptions.contains("ampalaya_disease_powdery_mildew")) {
                return DirectCropSolution(
                    title = "Powdery Mildew Eradication & Airflow Protocol",
                    tagalogTitle = "Solusyon sa Powdery Mildew (Puting Pulbos sa Dahon)",
                    severity = "WARNING",
                    badgeColor = AlertAmber,
                    directAnswer = "Ang puting pulbos sa ibabaw ng dahon ay fungal mycelium na sumisipsip sa sustansya ng dahon sa ilalim ng tuyong araw ngunit malamig at mahalumigmig na gabi.",
                    actionSteps = listOf(
                        "Mag-spray ng diluted fresh milk solution (1 bahagi gatas sa 9 na bahagi tubig) sa ilalim ng sikat ng araw; ang protina sa gatas ay lumilikha ng natural na antiseptic laban sa amag.",
                        "O mag-spray ng wettable sulfur powder (2g bawat litro) tuwing umaga bago uminit nang husto ang panahon.",
                        "Gupitin ang mga lumang dahon na labis nang natakpan ng puting pulbos.",
                        "Panatilihing buhaghag ang canopy para sa sikat ng araw."
                    ),
                    materialsNeeded = listOf("Wettable Sulfur o Sariwang Gatas", "Sprayer", "Pruning Shears"),
                    organicRemedy = "Milk spray (10% milk solution) sa maaraw na umaga o diluted sulfur.",
                    criticalRule = "Huwag mag-spray ng sulfur kapag lumagpas sa 32°C ang init upang maiwasan ang sunog sa dahon (leaf scorch)."
                )
            }
            if (selectedOptions.contains("ampalaya_disease_bacterial_wilt")) {
                return DirectCropSolution(
                    title = "Bacterial Wilt (Ralstonia solanacearum) Rogueing & Soil Containment",
                    tagalogTitle = "Lunas at Pagpigil sa Bacterial Wilt (Biglaang Pagkamatay ng Baging)",
                    severity = "CRITICAL",
                    badgeColor = DangerRed,
                    directAnswer = "Kapag ang buong baging ay biglang nalanta sa katanghalian nang berde pa ang dahon, ito ay Bacterial Wilt. Bara ang daluyan ng tubig sa loob ng tangkay dulot ng bakterya sa lupa; walang gamot na kemikal kapag pumasok na sa halaman.",
                    actionSteps = listOf(
                        "Bunutin agad ang buong halaman kasama ang ugat at ang lupa sa paligid nito; ilagay sa sako at sunugin sa labas ng sakahan.",
                        "Maglagay ng 2 dakot ng apog (agricultural lime) sa hukay na pinagbunutan upang mapigilan ang paggapang ng bakterya sa karatig na puno.",
                        "Huwag itaniman ng ampalaya, kamatis, o talong ang parehong plot sa loob ng 2 hanggang 3 taon (magtanim ng mais o palay bilang rotation).",
                        "Disimpektahin ang lahat ng gamit (itak, gunting) sa 10% bleach bago gamitin sa ibang halaman."
                    ),
                    materialsNeeded = listOf("Apog (Agricultural Lime)", "Bleach Solution", "Sako para sa Pagtatapon"),
                    organicRemedy = "Agad na pagbunot (rogueing), pag-apog sa lupa, at 3-year crop rotation kasama ang mais o palay.",
                    criticalRule = "Huwag hayaang mabulok sa bukid ang halamang may bacterial wilt upang hindi mahawa ang buong balag."
                )
            }
        }
        "pollination_and_flowers" -> {
            if (selectedOptions.contains("ampalaya_poll_flower_drop")) {
                return DirectCropSolution(
                    title = "Hand Pollination & Honeybee Conservation",
                    tagalogTitle = "Solusyon sa Pagkalagas ng Bulaklak at Pagsasagawa ng Hand Pollination",
                    severity = "WARNING",
                    badgeColor = AlertAmber,
                    directAnswer = "Ang ampalaya ay monoecious (magkahiwalay ang lalaki at babaeng bulaklak sa parehong baging). Kapag walang bubuyog na nagdadala ng pollen mula sa lalaki patungo sa babae, ang munting buko ay naninilaw at nalalaglag.",
                    actionSteps = listOf(
                        "Magtanim ng Marigold, Cosmos, at Zinnia sa paligid ng balag upang umakit ng mga katutubong bubuyog at pukyutan.",
                        "Hand Pollination Protocol: Sa pagitan ng 6:30 AM at 8:30 AM, mamitas ng sariwang lalaking bulaklak (walang buko sa ilalim), tanggalin ang mga talulot (petals), at marahang ikuskos ang pollen sa gitna ng bukang babaeng bulaklak (may munting ampalaya sa base).",
                        "Itigil ang pag-spray ng anumang kemikal na insecticide sa umaga habang lumilipad ang mga pollinator.",
                        "Diligan ang lupa nang regular upang hindi matuyo ang pollen."
                    ),
                    materialsNeeded = listOf("Marigold Seeds", "Pinong Pintura Brush o Kamay para sa Hand Pollination"),
                    organicRemedy = "Hand pollination sa umaga at pagtatanim ng marigold bilang bee attractant.",
                    criticalRule = "Huwag mag-spray ng insecticide mula 6:00 AM hanggang 10:00 AM upang hindi mapatay ang mga kapaki-pakinabang na bubuyog."
                )
            }
            if (selectedOptions.contains("ampalaya_poll_rain_washout")) {
                return DirectCropSolution(
                    title = "Rain Shelter & Controlled Morning Pollination",
                    tagalogTitle = "Proteksyon sa Nahugasang Pollen sa Maulang Umaga",
                    severity = "INFO",
                    badgeColor = LushGreen,
                    directAnswer = "Ang malakas na buhos ng ulan bago mag-alas nuwebe ng umaga ay naghuhugas sa pinong pollen at nagpapasara sa mga bulaklak bago pa madapuan ng mga bubuyog.",
                    actionSteps = listOf(
                        "Maglagay ng maliit na paper cone o payong sa mga piniling babaeng bulaklak bago umulan kung nais mag-save ng binhi.",
                        "Magsagawa ng maagang hand pollination sa sandaling tumila ang ulan kung tuyo pa ang anther.",
                        "Tiyakin ang maayos na daloy ng tubig sa ilalim ng balag upang hindi maging sanhi ng labis na humidity."
                    ),
                    materialsNeeded = listOf("Paper Covers para sa Bulaklak"),
                    organicRemedy = "Proteksiyon sa bulaklak laban sa direktang hampas ng malakas na ulan.",
                    criticalRule = "Mahalaga ang tuyong umaga para sa 100% matagumpay na polinasyon ng ampalaya."
                )
            }
            if (selectedOptions.contains("ampalaya_poll_heat_abortion")) {
                return DirectCropSolution(
                    title = "Extreme Heat Stress & Overhead Mist Relief",
                    tagalogTitle = "Lunas sa Pagkakaluto ng Pollen sa Matinding Init (>35°C)",
                    severity = "WARNING",
                    badgeColor = AlertAmber,
                    directAnswer = "Sa temperaturang lampas 35°C, nagiging baog ang pollen at natutuyo ang maseselang stigma ng babaeng bulaklak, dahilan upang malaglag ang mga buko.",
                    actionSteps = listOf(
                        "Maglagay ng 30% shade net sa itaas ng balag sa panahon ng matinding tag-araw (Marso–Mayo).",
                        "Panatilihing basa ang lupa sa pamamagitan ng pagdidilig tuwing maagang umaga.",
                        "Maglagay ng makapal na organic mulch upang mapanatiling malamig ang ugat.",
                        "Mag-spray ng banayad na mist sa pasilyo (hindi sa bulaklak) upang mapataas ang halumigmig sa paligid."
                    ),
                    materialsNeeded = listOf("30% Black Shade Netting", "Makapal na Dayami Mulch"),
                    organicRemedy = "Mulching at banayad na lilim upang mapababa ang temperatura sa balag.",
                    criticalRule = "Panatilihing malamig ang root zone upang makayanan ng baging ang init sa itaas ng balag."
                )
            }
        }
        "harvesting" -> {
            if (selectedOptions.contains("ampalaya_harvest_overmature_yellow")) {
                return DirectCropSolution(
                    title = "Overmature Ampalaya Harvesting & Strict Frequency Schedule",
                    tagalogTitle = "Tamang Pag-ani Bago Manilaw at Magmura ang Bunga",
                    severity = "WARNING",
                    badgeColor = AlertAmber,
                    directAnswer = "Kapag nanilaw na ang dulo ng ampalaya, lumalambot ang laman, namumula ang mga buto, at nawawala ang commercial market value dahil hindi na ito bibilhin sa palengke.",
                    actionSteps = listOf(
                        "Anihin agad ang lahat ng naninilaw na bunga ngayon; alisin sa balag upang hindi ubusin ang sustansya ng baging.",
                        "Maaaring kunin ang mga mapupulang buto kung ito ay open-pollinated variety (OPV) upang patuyuin para sa susunod na binhi.",
                        "Magtakda ng mahigpit na anihan tuwing ika-2 hanggang ika-4 na araw.",
                        "Anihin kapag matambok na ang mga tagaytay ngunit matingkad pa ang kulay berde."
                    ),
                    materialsNeeded = listOf("Pruning Shears", "Lagayan ng Bunga (Crates / Bilao)"),
                    organicRemedy = "Pag-aani tuwing 3 araw upang manatiling masipag mamunga ang baging.",
                    criticalRule = "Huwag mag-iwan ng dilaw na bunga sa balag; pinipigilan nito ang paglabas ng mga bagong bulaklak."
                )
            }
            if (selectedOptions.contains("ampalaya_harvest_short_peduncle")) {
                return DirectCropSolution(
                    title = "Proper Stem-Cutting Harvest Protocol",
                    tagalogTitle = "Tamang Paraan ng Pagputol ng Tangkay sa Pag-aani",
                    severity = "INFO",
                    badgeColor = LushGreen,
                    directAnswer = "Ang paghila o pagpunit sa bunga nang walang tangkay ay nagdudulot ng bukas na sugat sa base, na nagiging daan ng mabilisang pagkabulok at pagkatuyo sa imbakan.",
                    actionSteps = listOf(
                        "Gumamit ng matalas na gunting o kutsilyo sa pag-aani.",
                        "Mag-iwan ng 2 hanggang 3 sentimetro ng tangkay (peduncle) na nakakabit sa bawat bunga.",
                        "Iwasang mahulog o mauntog ang bunga upang hindi mapinsala ang mga tagaytay.",
                        "Ipunin nang maayos sa malinis na basket o crate na may saping dahon ng saging."
                    ),
                    materialsNeeded = listOf("Matalas na Harvest Shears", "Ventilated Plastic Crates", "Dahon ng Saging para sa Sapin"),
                    organicRemedy = "Paggamit ng matalas na gunting at saping dahon ng saging sa crate.",
                    criticalRule = "Huwag hilahin ang bunga gamit ang kamay; palaging gupitin ang tangkay."
                )
            }
            if (selectedOptions.contains("ampalaya_harvest_crooked_culls")) {
                return DirectCropSolution(
                    title = "Prevention of Crooked & Curved Ampalaya Fruits",
                    tagalogTitle = "Solusyon sa Baluktot at Kurbadong Bunga (Hugis Titik C)",
                    severity = "WARNING",
                    badgeColor = AlertAmber,
                    directAnswer = "Ang baluktot na bunga ay kadalasang dulot ng sagabal sa lambat habang lumalaki, bahagyang tusok ng fruit fly sa isang bahagi, o kawalan ng pantay na patubig.",
                    actionSteps = listOf(
                        "Ayusin ang nakabiting bunga upang malayang nakabitin pababa nang walang sumasabit na alambre o lambat.",
                        "Panatilihin ang pantay na patubig tuwing 2 hanggang 3 araw upang maging pantay ang paglaki ng mga selula sa magkabilang bahagi ng bunga.",
                        "Balutin ang bunga habang maliit pa upang maprotektahan sa tusok ng insekto.",
                        "Maglagay ng sapat na Potassium (0-0-60) at Boron foliar."
                    ),
                    materialsNeeded = listOf("Paper Bags para sa Bunga", "Potassium Fertilizer", "Solubor"),
                    organicRemedy = "Pag-aayos ng posisyon sa lambat upang malayang bumitin nang tuwid pababa.",
                    criticalRule = "Tiyaking nakabitin nang malaya pababa ang bawat bunga upang maging diretso at primera klase."
                )
            }
        }
    }

    return null
}
