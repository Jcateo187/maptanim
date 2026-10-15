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
 * for Cucumber / Pipino (Cucumis sativus) in Philippine farming, commercial trellising,
 * and backyard gardens based on MapTanim technical data & DA-BPI extension guidelines.
 */
fun getCucumberCheckUpActivities(): List<CheckUpActivity> {
    return listOf(
        CheckUpActivity(
            id = "watering",
            title = "Patubig at Pag-iwas sa Kapaitan ng Pipino",
            tagalogSubtitle = "Suriin ang kahalumigmig sa ugat (95% tubig ang bunga) at pag-iwas sa mapait na cucurbitacin",
            icon = Icons.Default.WaterDrop,
            options = listOf(
                CheckUpOption(
                    id = "cucumber_water_normal",
                    title = "Katamtaman at Pantay na Patubig",
                    tagalogDesc = "Mamasa-masa ang 15–20 cm lalim ng lupa; dinidilig 2 hanggang 3 beses bawat linggo tuwing maagang umaga; malutong at matamis ang laman",
                    isIdeal = true
                ),
                CheckUpOption(
                    id = "cucumber_water_stress_bitter",
                    title = "Tagtuyot / Mapait at Kurbadong Bunga",
                    tagalogDesc = "Naluluoy ang mga dulo ng baging sa katanghalian; nagiging mapait ang dulo ng bunga (cucurbitacin); nalalaglag ang mga bulaklak"
                ),
                CheckUpOption(
                    id = "cucumber_water_overwater",
                    title = "Sobrang Tubig / Pagkababad ng Ugat sa Baha",
                    tagalogDesc = "Naiipon ang tubig sa paanan ng puno; naninilaw ang lumang dahon at nabubulok ang mga ugat (damping-off / root rot)"
                ),
                CheckUpOption(
                    id = "cucumber_water_wet_leaves",
                    title = "Panghapong Pagdidilig sa mga Dahon",
                    tagalogDesc = "Nababasa ang mga dahon bago lumubog ang araw; nagiging sanhi ng mabilisang pagsiklab ng Downy Mildew sa balag"
                )
            )
        ),
        CheckUpActivity(
            id = "trellising_and_pruning",
            title = "Balag (Trellis) at Paggagapas ng mga Sanga",
            tagalogSubtitle = "Suriin ang suporta ng patayong lambat (1.5–2m) at sirkulasyon ng hangin sa mga gumagapang na baging",
            icon = Icons.Default.Agriculture,
            options = listOf(
                CheckUpOption(
                    id = "cucumber_trellis_ideal",
                    title = "Matibay na Patayong Balag at Maayos na Pruning",
                    tagalogDesc = "Nakatayo sa vertical net (1.8m); inalis ang mga sanga sa ibabang 30–50 cm; nakabitin nang tuwid at malinis ang mga bunga",
                    isIdeal = true
                ),
                CheckUpOption(
                    id = "cucumber_trellis_ground_trailing",
                    title = "Gumagapang sa Lupa Nang Walang Balag",
                    tagalogDesc = "Nasa ibabaw lamang ng lupa ang baging; nabubulok ang bunga sa basang lupa; pumuputla ang ilalim at madaling kapitan ng amag"
                ),
                CheckUpOption(
                    id = "cucumber_trellis_dense_unpruned",
                    title = "Masukal na Ibabang Sanga (Unpruned Base)",
                    tagalogDesc = "Makakapal ang mga sanga sa lupa; kulang sa sirkulasyon ng hangin at dinadapuan ng downy mildew at peste"
                ),
                CheckUpOption(
                    id = "cucumber_trellis_sagging",
                    title = "Lumalawlaw o Babagsak na Lambat",
                    tagalogDesc = "Maluwag ang nylon net o nababaling kawayan dahil sa bigat ng mabilis na lumalaking mga pipino"
                )
            )
        ),
        CheckUpActivity(
            id = "fertilizing",
            title = "Abono at Nutrisyon sa Mabilisang Pamumunga",
            tagalogSubtitle = "Suriin ang sustansya para sa tuwid, makinis, at matatabang berdeng bunga",
            icon = Icons.Default.Spa,
            options = listOf(
                CheckUpOption(
                    id = "cucumber_fert_balanced",
                    title = "Balanse at Napapanahong Pag-aabono",
                    tagalogDesc = "May organikong compost sa hukay; may basal 14-14-14 at regular na side-dress ng Potassium at Nitrogen tuwing ika-2 linggo",
                    isIdeal = true
                ),
                CheckUpOption(
                    id = "cucumber_fert_nitrogen_def",
                    title = "Kakulangan sa Nitroheno (Maputlang Baging)",
                    tagalogDesc = "Naninilaw ang matatandang dahon sa ilalim; manipis ang baging at mabagal ang paglabas ng mga bagong talbos"
                ),
                CheckUpOption(
                    id = "cucumber_fert_potassium_def",
                    title = "Kakulangan sa Potassium (Payat ang Gitna, Mataba ang Dulo)",
                    tagalogDesc = "Tuyo o sunog ang gilid ng dahon; impis at payat ang base ng bunga ngunit matambok ang dulo; hindi pantay ang hugis"
                ),
                CheckUpOption(
                    id = "cucumber_fert_calcium_def",
                    title = "Kakulangan sa Kalsyo (Blossom-End Distortion)",
                    tagalogDesc = "Pumapayat, nangingitim, o nabubulok ang pinakadulo ng munting bunga; kulubot ang mga bagong usbong na dahon"
                ),
                CheckUpOption(
                    id = "cucumber_fert_excess_nitrogen",
                    title = "Sobrang Nitroheno (Puro Dahon, Malalagas na Bulaklak)",
                    tagalogDesc = "Napakalalapad at napakaitim ng dahon ngunit kakaunti ang bunga at madaling malagas ang mga buko sa balag"
                )
            )
        ),
        CheckUpActivity(
            id = "pest_control",
            title = "Peste sa Bunga at Dahon ng Pipino",
            tagalogSubtitle = "Suriin ang tusok ng langaw-bunga, kuto sa talbos, at salagubang sa dahon",
            icon = Icons.Default.BugReport,
            options = listOf(
                CheckUpOption(
                    id = "cucumber_pest_clean",
                    title = "Malinis at Walang Pinsala ng Insekto",
                    tagalogDesc = "Makikinis at walang marka ng tusok ang mga bunga; malulusog ang mga dulo ng baging at talbos",
                    isIdeal = true
                ),
                CheckUpOption(
                    id = "cucumber_pest_fruit_fly",
                    title = "Melon Fruit Fly (Tusok at Uod sa Loob ng Bunga)",
                    tagalogDesc = "May tagas ng dagta o brown na tuldok sa balat; nagiging dilaw, malambot, at nabubulok ang loob dahil sa uod (maggots)"
                ),
                CheckUpOption(
                    id = "cucumber_pest_aphids",
                    title = "Kuto sa Dahon / Aphids sa mga Talbos",
                    tagalogDesc = "Kumpol ng maliliit na berdeng kulisap sa ilalim ng dahon at talbos; kulubot na dahon na may maitim na amag (sooty mold)"
                ),
                CheckUpOption(
                    id = "cucumber_pest_leaf_beetle",
                    title = "Aulacophora Beetle / Salagubang sa Dahon",
                    tagalogDesc = "Madilaw-kahel na salagubang na gumagawa ng pabilog na butas sa mga dahon at kumakain ng mga bulaklak"
                )
            )
        ),
        CheckUpActivity(
            id = "diseases",
            title = "Sakit sa Dahon at Amag sa Balag",
            tagalogSubtitle = "Suriin ang downy mildew, powdery mildew, at pamumutla ng mga baging",
            icon = Icons.Default.Coronavirus,
            options = listOf(
                CheckUpOption(
                    id = "cucumber_disease_healthy",
                    title = "Malusog at Luntiang mga Baging",
                    tagalogDesc = "Walang kanto-kantong dilaw na tagpi o puting pulbos sa dahon; tuloy-tuloy ang pagtalbos at pamumulaklak",
                    isIdeal = true
                ),
                CheckUpOption(
                    id = "cucumber_disease_downy_mildew",
                    title = "Downy Mildew (Kanto-kantong Dilaw na Tagpi)",
                    tagalogDesc = "Angular o kanto-kantong dilaw na mantsa na hinaharang ng ugat ng dahon; may abuhing amag sa ilalim sa mahamog na umaga"
                ),
                CheckUpOption(
                    id = "cucumber_disease_powdery_mildew",
                    title = "Powdery Mildew (Puting Pulbos sa Ibabaw ng Dahon)",
                    tagalogDesc = "Maputing parang harina na bumabalot sa ibabaw ng mga dahon at tangkay; natutuyo at nalalanta ang apektadong dahon"
                ),
                CheckUpOption(
                    id = "cucumber_disease_bacterial_wilt",
                    title = "Bacterial Wilt / Biglaang Pagkalanta ng Baging",
                    tagalogDesc = "Biglang naluluoy ang buong baging sa katanghalian kahit basa ang lupa; naglalabas ng puting malagkit na dagta sa tangkay"
                )
            )
        ),
        CheckUpActivity(
            id = "pollination_and_flowers",
            title = "Pamumulaklak at Pagbuo ng Bunga",
            tagalogSubtitle = "Suriin ang aktibidad ng mga bubuyog at pag-unlad ng babaeng bulaklak sa balag",
            icon = Icons.Default.Eco,
            options = listOf(
                CheckUpOption(
                    id = "cucumber_poll_active_bees",
                    title = "Masiglang Polinasyon ng mga Bubuyog",
                    tagalogDesc = "Maraming pukyutan at ligaw na bubuyog na lumilipad sa mga dilaw na bulaklak sa umaga (6:00–9:00 AM); mabilis lumaki ang bunga",
                    isIdeal = true
                ),
                CheckUpOption(
                    id = "cucumber_poll_flower_drop",
                    title = "Pagkalagas ng Buko ng Babaeng Bulaklak",
                    tagalogDesc = "Naninilaw at nalalaglag ang munting buko bago o pagkatapos bumuka ang bulaklak dahil sa kakulangan ng polinasyon"
                ),
                CheckUpOption(
                    id = "cucumber_poll_heat_abortion",
                    title = "Matinding Init (>35°C) sa Panahon ng Pamumulaklak",
                    tagalogDesc = "Naluluto ang pollen at nalalanta ang maseselang tangkay ng bulaklak sa sobrang init at tuyong hangin"
                )
            )
        ),
        CheckUpActivity(
            id = "harvesting",
            title = "Dalas at Paraan ng Pag-aani ng Pipino",
            tagalogSubtitle = "Suriin ang laki, lutong, at dalas ng pitas (bawat 1–2 araw) upang maiwasan ang paninilaw",
            icon = Icons.Default.ContentCut,
            options = listOf(
                CheckUpOption(
                    id = "cucumber_harvest_ready_crisp",
                    title = "Tamang Gulang, Makinis, at Matingkad na Berde",
                    tagalogDesc = "Silindrikal, 15–22 cm ang haba; matigas at malutong ang laman; matingkad na berde (hindi naninilaw); pinitas sa umaga",
                    isIdeal = true
                ),
                CheckUpOption(
                    id = "cucumber_harvest_overmature_yellow",
                    title = "Overmature / Lumobo at Nanilaw ang Bunga",
                    tagalogDesc = "Lumampas sa 25 cm, maputla o naninilaw ang balat; matigas at makahoy ang mga buto sa loob; maasim at buhaghag ang laman"
                ),
                CheckUpOption(
                    id = "cucumber_harvest_crooked_culls",
                    title = "Baluktot o Kurbadong Hugis Titik C",
                    tagalogDesc = "Kurbado ang bunga dahil sumabit sa lambat, nakaranas ng tagtuyot sa lumalaking yugto, o bahagyang natusok ng insekto"
                ),
                CheckUpOption(
                    id = "cucumber_harvest_torn_stem",
                    title = "Punit o Walang Tangkay sa Pag-aani",
                    tagalogDesc = "Hinila ang bunga sa halip na gupitin; napunit ang balat sa base kaya mabilis mabulok at malanta sa imbakan"
                )
            )
        )
    )
}

/**
 * Diagnostic logic providing instant, research-backed Philippine solutions
 * for Cucumber / Pipino (Cucumis sativus) based on MapTanim technical data.
 */
fun solveCucumberProblem(
    activityId: String,
    selectedOptions: Set<String>
): DirectCropSolution? {
    if (selectedOptions.isEmpty()) return null

    when (activityId) {
        "watering" -> {
            if (selectedOptions.contains("cucumber_water_stress_bitter")) {
                return DirectCropSolution(
                    title = "Cucumber Water Stress & Cucurbitacin Bitterness Relief",
                    tagalogTitle = "Solusyon sa Tagtuyot at Mapait na Bunga ng Pipino (Cucurbitacin)",
                    severity = "CRITICAL",
                    badgeColor = DangerRed,
                    directAnswer = "Ang pipino ay 95% tubig. Kapag nakaranas ng matinding tagtuyot ang mababaw na ugat, naglalabas ang halaman ng mapait na kemikal na 'cucurbitacin' sa ilalim ng balat at dulo ng tangkay, at bumabaluktot ang hugis ng bunga.",
                    actionSteps = listOf(
                        "Magdilig nang malalim gamit ang furrow o drip irrigation tuwing maagang umaga (6:00–7:30 AM).",
                        "Panatilihin ang 2 hanggang 3 beses bawat linggong patubig; huwag hayaang matuyo ang lupa nang higit 2 araw sa panahon ng pamumunga.",
                        "Maglagay ng 5–8 cm makapal na dayami (rice straw) o ipa sa paligid ng puno upang mapanatiling malamig ang ugat.",
                        "I-prune ang mga tuyong dulo ng dahon upang mabawasan ang transpiration."
                    ),
                    materialsNeeded = listOf("Drip Line o Hose", "Dayami (Rice Straw) o Ipa", "Mulch Fork"),
                    organicRemedy = "Makapal na organic mulch upang mapanatili ang steady 60–70% soil moisture capacity.",
                    criticalRule = "Huwag hayaang magpalit-palit ang tuyo at basang lupa upang hindi maging mapait ang pipino."
                )
            }
            if (selectedOptions.contains("cucumber_water_overwater")) {
                return DirectCropSolution(
                    title = "Drainage Channeling & Damping-off Control",
                    tagalogTitle = "Lunas sa Pagkababad ng Ugat sa Baha at Root Rot",
                    severity = "WARNING",
                    badgeColor = AlertAmber,
                    directAnswer = "Ang basang-basa at baradong lupa ay nagdudulot ng pagkabulok ng maseselang ugat ng pipino at nag-aanyaya ng Pythium at Fusarium root rot.",
                    actionSteps = listOf(
                        "Maghukay agad ng 20–30 cm na drainage furrows sa pagitan ng mga kama ng balag upang mabilis umagos ang tubig-ulan.",
                        "Itaas ang mga kama (raised beds) nang 20 cm bago magtanim.",
                        "Itigil pansamantala ang pagdidilig hanggang sa matuyo ang ibabaw na lupa.",
                        "Mag-drench ng Trichoderma harzianum bio-fungicide sa paanan ng puno upang protektahan ang mga ugat."
                    ),
                    materialsNeeded = listOf("Pala para sa Kanal", "Trichoderma Harzianum Bio-fungicide"),
                    organicRemedy = "Trichoderma root drench upang puksain ang masasamang fungi sa basang lupa.",
                    criticalRule = "Huwag hayaang mag-ipon ang tubig sa paanan ng puno nang higit 12 oras."
                )
            }
            if (selectedOptions.contains("cucumber_water_wet_leaves")) {
                return DirectCropSolution(
                    title = "Morning Ground Irrigation Protocol for Cucumbers",
                    tagalogTitle = "Paglipat sa Patubig sa Lupa Tuwing Umaga",
                    severity = "INFO",
                    badgeColor = LushGreen,
                    directAnswer = "Ang pagkabasa ng dahon sa dapit-hapon ang numero unong sanhi ng mabilisang pagsiklab ng Downy Mildew sa balag ng pipino.",
                    actionSteps = listOf(
                        "Ilipat ang pagdidilig sa maagang umaga mula 6:00 hanggang 7:30 AM lamang.",
                        "Itutok ang tubig sa lupa sa paanan ng puno; iwasang wisikan ang mga dahon sa lambat.",
                        "Gupitin ang mga apektadong dahon sa ibaba upang makasingaw ang hangin.",
                        "Tiyaking tuyo ang buong dahon bago sumapit ang gabi."
                    ),
                    materialsNeeded = listOf("Hose na may Ground Wand o Drip System"),
                    organicRemedy = "Direct-to-root irrigation sa halip na overhead sprinkler.",
                    criticalRule = "Bawal basain ang mga dahon ng pipino pagkalipas ng alas-3:00 ng hapon."
                )
            }
        }
        "trellising_and_pruning" -> {
            if (selectedOptions.contains("cucumber_trellis_ground_trailing")) {
                return DirectCropSolution(
                    title = "Vertical Trellis Setup for Clean & Straight Cucumbers",
                    tagalogTitle = "Kagyat na Pagtatayo ng Balag para sa Gumagapang na Pipino",
                    severity = "CRITICAL",
                    badgeColor = DangerRed,
                    directAnswer = "Ang pipino ay nangangailangan ng patayong balag. Kapag gumapang sa lupa, higit 50% ng mga bunga ang nabubulok sa basa, nagiging dilaw sa isang tabi, at madaling kapitan ng soil-borne fungi at fruit fly.",
                    actionSteps = listOf(
                        "Magtayo agad ng vertical trellis (1.8m taas) gamit ang kawayan at nylon trellis netting (mesh size 15–20 cm).",
                        "I-gabay at itali nang malumanay ang mga dulo ng baging paakyat sa lambat gamit ang malambot na straw.",
                        "Linisin ang mga dahong nadikit sa putik.",
                        "Maglagay ng mulch sa ilalim ng bagong tayong balag."
                    ),
                    materialsNeeded = listOf("Poste ng Kawayan", "Nylon Trellis Netting", "Straw Cord"),
                    organicRemedy = "Pag-aangat ng baging sa lambat para sa 100% proteksyon laban sa ground contact rot.",
                    criticalRule = "Ang patayong balag ay nagpapanatili ng tuwid, berde, at primera-klaseng hugis ng bunga."
                )
            }
            if (selectedOptions.contains("cucumber_trellis_dense_unpruned")) {
                return DirectCropSolution(
                    title = "Lower Lateral Pruning Protocol for Cucumbers",
                    tagalogTitle = "Tamang Paggagapas ng mga Sanga sa Ibaba (Pruning)",
                    severity = "WARNING",
                    badgeColor = AlertAmber,
                    directAnswer = "Ang mga sanga sa ibabang 30–50 cm ng puno ay kumukuha ng sustansya, nagpapapasok ng amag mula sa lupa, at humaharang sa sirkulasyon ng hangin sa balag.",
                    actionSteps = listOf(
                        "Gamit ang malinis na gunting, putulin ang lahat ng lateral branches at bulaklak mula sa lupa hanggang 30–50 sentimetro ang taas.",
                        "Panatilihin lamang ang nag-iisang matipunong main stem paakyat sa lambat.",
                        "Hayaang magsangay ang baging sa itaas kung saan may sapat na sikat ng araw at hangin.",
                        "Alisin ang lahat ng naninilaw na dahon sa paanan ng puno at sunugin sa malayo."
                    ),
                    materialsNeeded = listOf("Pruning Shears", "Alcohol para sa Disinfection"),
                    organicRemedy = "Pruning para sa natural na sirkulasyon ng hangin at sikat ng araw sa balag.",
                    criticalRule = "Mag-prune lamang tuwing tuyo ang panahon sa umaga upang mabilis maghilom ang mga hiwa."
                )
            }
            if (selectedOptions.contains("cucumber_trellis_sagging")) {
                return DirectCropSolution(
                    title = "Trellis Reinforcement & Net Tightening",
                    tagalogTitle = "Pagpapatibay at Pag-alalay sa Bumibigat na Balag ng Pipino",
                    severity = "WARNING",
                    badgeColor = AlertAmber,
                    directAnswer = "Ang mabilis na pagdami at paglaki ng mga pipino ay maaaring magpalaylay sa lambat at magpabagsak sa mga poste ng kawayan.",
                    actionSteps = listOf(
                        "Maglagay ng karagdagang poste ng kawayan tuwing 2 hanggang 3 metro sa kahabaan ng hanay.",
                        "Higpitan ang itaas na alambre o nylon cord gamit ang matitibay na tulos sa dulo ng hanay.",
                        "I-tali ang mga bumibigat na sanga gamit ang malambot na tali.",
                        "Anihin na agad ang mga handa nang bunga upang mabawasan ang bigat sa lambat."
                    ),
                    materialsNeeded = listOf("Kawayang Poste", "GI Wire #16 o Nylon Cord", "Tulos"),
                    organicRemedy = "Pagpapatibay sa istruktura gamit ang natural na kawayan at abaka.",
                    criticalRule = "Patatagin agad ang balag bago tuluyang bumagsak sa panahon ng malakas na ulan o hangin."
                )
            }
        }
        "fertilizing" -> {
            if (selectedOptions.contains("cucumber_fert_nitrogen_def")) {
                return DirectCropSolution(
                    title = "Nitrogen Boost for Fast Vegetative Growth",
                    tagalogTitle = "Lunas sa Naninilaw na Dahon at Mahinang Baging (Kakulangan sa Nitroheno)",
                    severity = "WARNING",
                    badgeColor = AlertAmber,
                    directAnswer = "Ang paninilaw ng matatandang dahon sa ilalim at manipis na baging ay nagpapakita ng kakulangan sa Nitroheno sa maagang yugto ng paggapang.",
                    actionSteps = listOf(
                        "Mag-side-dress ng Urea (46-0-0) o Ammonium Sulfate (21-0-0) sa daming 10 gramo bawat puno, 15 cm ang layo sa paanan.",
                        "O magdilig ng Fermented Plant Juice (FPJ gawa sa kangkong o kakawate) sa timplang 2 kutsara bawat litro ng tubig.",
                        "Diligan agad pagkatapos mag-abono upang mabilis masipsip ng ugat.",
                        "Ulitin pagkalipas ng 10 araw kung kinakailangan."
                    ),
                    materialsNeeded = listOf("Urea o Ammonium Sulfate", "Fermented Plant Juice (FPJ)", "Regadera"),
                    organicRemedy = "FPJ drench mula sa talbos ng kangkong o vermitea.",
                    criticalRule = "Huwag ilagay ang abono nang dikit sa puno upang hindi masunog ang malambot na tangkay."
                )
            }
            if (selectedOptions.contains("cucumber_fert_potassium_def")) {
                return DirectCropSolution(
                    title = "Potassium Supplementation for Straight & Crisp Cucumbers",
                    tagalogTitle = "Pagpapataas ng Potassium para sa Tuwid, Makapal, at Malutong na Bunga",
                    severity = "WARNING",
                    badgeColor = AlertAmber,
                    directAnswer = "Ang kakulangan sa Potassium ay nagdudulot ng impis na base ng bunga, sunog na gilid ng dahon, at malambot na balat na madaling masira sa biyahe.",
                    actionSteps = listOf(
                        "Maglagay ng Muriate of Potash (0-0-60) sa daming 10 gramo bawat puno tuwing ika-2 linggo sa panahon ng pamumunga.",
                        "Bilang organikong alternatibo, mag-spray ng Fermented Fruit Juice (FFJ gawa sa hinog na saging) o magbudbod ng pinong abo ng kahoy sa paligid ng puno.",
                        "Panatilihing pantay ang patubig upang mabilis mahigop ang potassium ng mga ugat.",
                        "Gupitin ang mga sirang lumang dahon sa ibaba."
                    ),
                    materialsNeeded = listOf("0-0-60 Muriate of Potash", "Fermented Fruit Juice (FFJ)", "Wood Ash (Abo ng Kahoy)"),
                    organicRemedy = "FFJ mula sa hinog na saging at malinis na abo ng kahoy na mayaman sa potassium.",
                    criticalRule = "Kailangan ng mataas na potassium sa buong panahon ng mabilisang pamumunga ng pipino."
                )
            }
            if (selectedOptions.contains("cucumber_fert_calcium_def")) {
                return DirectCropSolution(
                    title = "Calcium Supplementation for Blossom-End Rot & Tip Firmness",
                    tagalogTitle = "Lunas sa Nangangayayat o Nabubulok na Dulo ng Bunga (Kakulangan sa Kalsyo)",
                    severity = "WARNING",
                    badgeColor = AlertAmber,
                    directAnswer = "Ang pagkaitim, pagkasunog, o pagkalanta ng dulo ng lumalaking pipino ay dulot ng kawalan ng Calcium sa mabilis na naghahating selula sa dulo ng bunga.",
                    actionSteps = listOf(
                        "Mag-spray ng Water-Soluble Calcium (WCA gawa sa piniritong balat ng itlog at suka, 10ml bawat litro) minsan sa isang linggo tuwing hapon.",
                        "O mag-foliar spray ng Calcium Nitrate (0.5%) sa mga dahon at lumalaking bunga.",
                        "Panatilihing pantay ang patubig dahil ang tagtuyot ang humahadlang sa paghigop ng kalsyo.",
                        "Maglagay ng mulch sa paanan ng puno upang maiwasan ang biglaang pagkatuyo ng lupa."
                    ),
                    materialsNeeded = listOf("Water-Soluble Calcium (WCA)", "Calcium Nitrate", "Sprayer"),
                    organicRemedy = "Foliar spray ng WCA mula sa balat ng itlog.",
                    criticalRule = "Mahalaga ang pantay na dilig upang tuloy-tuloy na umakyat ang kalsyo sa dulo ng bunga."
                )
            }
            if (selectedOptions.contains("cucumber_fert_excess_nitrogen")) {
                return DirectCropSolution(
                    title = "Excess Nitrogen Correction & Flowering Restoration",
                    tagalogTitle = "Solusyon sa Sobrang Dahon at Kakarampot na Bunga (Vegetative Dominance)",
                    severity = "WARNING",
                    badgeColor = AlertAmber,
                    directAnswer = "Kapag labis ang Urea o dumi ng manok, nagiging sobrang madilim ang lunti ng dahon ngunit nalalaglag ang mga bulaklak dahil puro vegetative growth ang inaatupag ng halaman.",
                    actionSteps = listOf(
                        "Itigil agad ang paglalagay ng Urea at sariwang dumi ng hayop.",
                        "Maglagay ng abono na mayaman sa Phosphorus at Potassium (0-0-60 at Solophos) upang pasiglahin ang pamumunga.",
                        "Mag-prune ng 20–30% ng pinakamasusukal na dahon sa itaas ng balag upang pumasok ang araw sa mga bulaklak.",
                        "Mag-spray ng Fermented Fruit Juice (FFJ) upang hikayatin ang paglabas ng mga babaeng bulaklak."
                    ),
                    materialsNeeded = listOf("0-0-60 Muriate of Potash", "Solophos (0-20-0)", "Pruning Shears"),
                    organicRemedy = "Pagbabawas ng nitrogen at pag-spray ng calcium-phosphate tea.",
                    criticalRule = "Huwag maglagay ng urea kapag namumulaklak na ang balag ng pipino."
                )
            }
        }
        "pest_control" -> {
            if (selectedOptions.contains("cucumber_pest_fruit_fly")) {
                return DirectCropSolution(
                    title = "Melon Fruit Fly (Bactrocera cucurbitae) Management in Cucumbers",
                    tagalogTitle = "Pambansang Pamamaraan Laban sa Melon Fruit Fly sa Pipino",
                    severity = "CRITICAL",
                    badgeColor = DangerRed,
                    directAnswer = "Ang Melon Fruit Fly ay nangingitlog sa malambot na balat ng batang pipino. Ang mga uod (maggots) sa loob ang nagpapabulok, nagpapadilaw, at sumisira sa bunga.",
                    actionSteps = listOf(
                        "Fruit Bagging: Balutin ang maliliit na bunga gamit ang paper bag o perforated plastic sleeves 3 hanggang 5 araw pagkatapos mamulaklak.",
                        "Field Sanitation: Pulutin at ilibing sa 50 cm lalim ang LAHAT ng nabulok o nalaglag na bunga; huwag iwan sa lupa dahil lalabas ang mga bagong langaw.",
                        "Magtayo ng Methyl Eugenol / Protein Bait Traps sa paligid ng taniman upang maakit at mapuksa ang mga lalaking fruit fly.",
                        "Mag-spray ng protein bait spray sa labas ng taniman tuwing umaga."
                    ),
                    materialsNeeded = listOf("Paper Bagging Sleeves", "Methyl Eugenol Pheromone Traps", "Pala para sa Paglilibing"),
                    organicRemedy = "100% mechanical fruit bagging gamit ang paper sleeves at pheromone lure traps.",
                    criticalRule = "Ang pagbabalot ng bunga at sanitasyon sa bukid ang tanging mabisang panangga laban sa fruit fly."
                )
            }
            if (selectedOptions.contains("cucumber_pest_aphids")) {
                return DirectCropSolution(
                    title = "Aphid Colony Control & Virus Prevention",
                    tagalogTitle = "Pagsugpo sa Kuto sa Dahon (Aphids) at Sooty Mold sa Pipino",
                    severity = "WARNING",
                    badgeColor = AlertAmber,
                    directAnswer = "Ang aphids ay sumisipsip ng katas sa mga talbos at nagpapasalin ng Papaya Ringspot Virus (PRSV-W) at Cucumber Mosaic Virus (CMV) na nagdudulot ng matinding kulot at pagkabansot.",
                    actionSteps = listOf(
                        "Wisikan ng banayad ngunit madiing pressure ng tubig ang ilalim ng mga apektadong dahon tuwing umaga.",
                        "Mag-spray ng 0.5% neem oil extract na may kaunting liquid soap tuwing hapon.",
                        "Magkabit ng dilaw na sticky traps sa lebel ng balag upang mahuli ang mga may pakpak na aphids.",
                        "Gupitin at sunugin ang mga talbos na labis nang pinamumugaran."
                    ),
                    materialsNeeded = listOf("Neem Oil Extract", "Biodegradable Soap", "Yellow Sticky Traps"),
                    organicRemedy = "Neem oil spray (0.5%) na may sabon at konserbasyon ng ladybird beetles.",
                    criticalRule = "Iwasan ang sobrang nitroheno na nagpapasarap sa talbos para sa aphids."
                )
            }
            if (selectedOptions.contains("cucumber_pest_leaf_beetle")) {
                return DirectCropSolution(
                    title = "Aulacophora Leaf Beetle Control",
                    tagalogTitle = "Pamuksa sa Salagubang sa Dahon ng Cucurbits (Aulacophora)",
                    severity = "WARNING",
                    badgeColor = AlertAmber,
                    directAnswer = "Ang dilaw-kahel na Aulacophora beetle ay gumagawa ng pabilog na butas sa dahon at kumakain ng mga bulaklak, na nagpapabagal sa pamumunga.",
                    actionSteps = listOf(
                        "Handpick: Pulutin ang mga salagubang tuwing maagang umaga kapag mabagal pa silang lumipad; ilagay sa timba na may tubig at sabon.",
                        "Magbudbod ng pinong kahoy na abo (wood ash) sa basang dahon upang maitaboy ang mga salagubang.",
                        "Mag-spray ng chili-garlic botanical extract (sili + bawang + sabon) tuwing hapon.",
                        "Alisin ang mga damong ligaw sa paligid ng taniman."
                    ),
                    materialsNeeded = listOf("Timba na may Tubig at Sabon", "Wood Ash (Abo ng Kahoy)", "Chili-Garlic Spray"),
                    organicRemedy = "Wood ash dusting at chili-garlic extract spray.",
                    criticalRule = "Magsagawa ng handpicking bago sumikat ang mainit na araw."
                )
            }
        }
        "diseases" -> {
            if (selectedOptions.contains("cucumber_disease_downy_mildew")) {
                return DirectCropSolution(
                    title = "Downy Mildew (Pseudoperonospora cubensis) Emergency Defense",
                    tagalogTitle = "Lunas sa Downy Mildew sa Balag ng Pipino",
                    severity = "CRITICAL",
                    badgeColor = DangerRed,
                    directAnswer = "Ang Downy Mildew ang pinakamalubhang sakit ng pipino sa maulang panahon o mahamog na gabi. Lumilikha ito ng kanto-kantong dilaw na tagpi na nagpapatuyo sa buong balag sa loob lamang ng ilang araw.",
                    actionSteps = listOf(
                        "Gupitin at alisin agad ang lahat ng apektadong dahon sa ibaba; ilagay sa sako at sunugin sa labas ng sakahan.",
                        "Mag-spray ng Copper Hydroxide o Copper Oxychloride protectant fungicide tuwing maagang umaga.",
                        "Bilang organikong lunas, mag-spray ng Baking Soda solution (5g baking soda + 2ml liquid soap bawat litro ng tubig) tuwing ika-5 araw.",
                        "Pataasin ang sirkulasyon ng hangin sa pamamagitan ng pag-prune ng masusukal na sanga sa ibaba.",
                        "Iwasang magbasa ng dahon kapag nagdidilig."
                    ),
                    materialsNeeded = listOf("Copper Fungicide", "Baking Soda", "Liquid Soap", "Pruning Shears"),
                    organicRemedy = "Baking soda (sodium bicarbonate) spray na may sabon bilang bio-fungicide.",
                    criticalRule = "Huwag kailanman ihalo sa compost ang mga dahong may downy mildew; mabilis kumalat ang mga spores nito."
                )
            }
            if (selectedOptions.contains("cucumber_disease_powdery_mildew")) {
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
                    criticalRule = "Huwag mag-spray ng sulfur kapag lumagpas sa 32°C ang init upang maiwasan ang sunog sa dahon."
                )
            }
            if (selectedOptions.contains("cucumber_disease_bacterial_wilt")) {
                return DirectCropSolution(
                    title = "Bacterial Wilt (Ralstonia solanacearum) Rogueing Protocol",
                    tagalogTitle = "Lunas at Pagpigil sa Bacterial Wilt sa Pipino",
                    severity = "CRITICAL",
                    badgeColor = DangerRed,
                    directAnswer = "Kapag ang buong baging ay biglang nalanta sa katanghalian nang berde pa ang dahon, ito ay Bacterial Wilt. Bara ang daluyan ng tubig sa loob ng tangkay dulot ng bakterya sa lupa; walang gamot na kemikal kapag pumasok na sa halaman.",
                    actionSteps = listOf(
                        "Bunutin agad ang buong halaman kasama ang ugat at ang lupa sa paligid nito; ilagay sa sako at sunugin sa labas ng sakahan.",
                        "Maglagay ng 2 dakot ng apog (agricultural lime) sa hukay na pinagbunutan upang mapigilan ang paggapang ng bakterya sa karatig na puno.",
                        "Huwag itaniman ng cucurbits o solanaceous crops ang parehong plot sa loob ng 2 hanggang 3 taon (magtanim ng mais o palay bilang rotation).",
                        "Disimpektahin ang lahat ng gamit (itak, gunting) sa 10% bleach bago gamitin sa ibang halaman."
                    ),
                    materialsNeeded = listOf("Apog (Agricultural Lime)", "Bleach Solution", "Sako para sa Pagtatapon"),
                    organicRemedy = "Agad na pagbunot (rogueing), pag-apog sa lupa, at 2–3 year crop rotation kasama ang mais o palay.",
                    criticalRule = "Huwag hayaang mabulok sa bukid ang halamang may bacterial wilt upang hindi mahawa ang buong balag."
                )
            }
        }
        "pollination_and_flowers" -> {
            if (selectedOptions.contains("cucumber_poll_flower_drop")) {
                return DirectCropSolution(
                    title = "Hand Pollination & Honeybee Conservation in Cucumbers",
                    tagalogTitle = "Solusyon sa Pagkalagas ng Bulaklak at Pagsasagawa ng Hand Pollination",
                    severity = "WARNING",
                    badgeColor = AlertAmber,
                    directAnswer = "Ang pipino ay may magkahiwalay na lalaki at babaeng dilaw na bulaklak. Kung walang bubuyog na maglilipat ng pollen mula 6:00 AM hanggang 9:00 AM, ang munting buko ay naninilaw at nalalaglag nang hindi lumalaki.",
                    actionSteps = listOf(
                        "Magtanim ng Marigold, Cosmos, at Zinnia sa paligid ng balag upang umakit ng mga pukyutan at ligaw na bubuyog.",
                        "Hand Pollination Protocol: Sa pagitan ng 6:30 AM at 8:30 AM, mamitas ng sariwang lalaking bulaklak (walang munting pipino sa ilalim), tanggalin ang mga talulot, at marahang ikuskos ang dilaw na pollen sa gitna ng babaeng bulaklak.",
                        "Itigil ang pag-spray ng anumang kemikal na insecticide sa umaga habang lumilipad ang mga pollinator.",
                        "Panatilihing mamasa-masa ang lupa upang hindi matuyo ang pollen."
                    ),
                    materialsNeeded = listOf("Marigold Seeds", "Pinong Pintura Brush o Kamay para sa Hand Pollination"),
                    organicRemedy = "Hand pollination sa umaga at pagtatanim ng marigold bilang bee attractant.",
                    criticalRule = "Huwag mag-spray ng insecticide mula 6:00 AM hanggang 10:00 AM upang hindi mapatay ang mga bubuyog."
                )
            }
            if (selectedOptions.contains("cucumber_poll_heat_abortion")) {
                return DirectCropSolution(
                    title = "Heat Stress & Overhead Mist Relief for Cucumbers",
                    tagalogTitle = "Lunas sa Pagkakaluto ng Pollen sa Matinding Init (>35°C)",
                    severity = "WARNING",
                    badgeColor = AlertAmber,
                    directAnswer = "Sa temperaturang lampas 35°C, nagiging baog ang pollen at natutuyo ang maseselang stigma ng babaeng bulaklak, dahilan upang malaglag ang mga buko.",
                    actionSteps = listOf(
                        "Maglagay ng 30% black shade net sa itaas ng balag sa panahon ng matinding tag-init (Marso–Mayo).",
                        "Panatilihing basa ang lupa sa pamamagitan ng pagdidilig tuwing maagang umaga.",
                        "Maglagay ng makapal na organic mulch upang mapanatiling malamig ang ugat.",
                        "Mag-spray ng banayad na mist sa pasilyo upang mapababa ang init sa paligid ng balag."
                    ),
                    materialsNeeded = listOf("30% Black Shade Netting", "Makapal na Dayami Mulch"),
                    organicRemedy = "Mulching at banayad na lilim upang mapababa ang temperatura sa balag.",
                    criticalRule = "Panatilihing malamig ang root zone upang makayanan ng baging ang init."
                )
            }
        }
        "harvesting" -> {
            if (selectedOptions.contains("cucumber_harvest_overmature_yellow")) {
                return DirectCropSolution(
                    title = "Daily Harvest Frequency & Overmature Cucumber Removal",
                    tagalogTitle = "Tamang Dalas ng Pag-aani Bago Manilaw at Lumobo ang Pipino",
                    severity = "WARNING",
                    badgeColor = AlertAmber,
                    directAnswer = "Napakabilis lumaki ng pipino (cell enlargement sa loob ng 1–2 araw). Kapag lumagpas sa 22–25 cm at nanilaw ang balat, lumalambot ang laman, tumitigas ang buto, at nawawala ang commercial market value.",
                    actionSteps = listOf(
                        "Pitasin na agad ang lahat ng naninilaw na bunga ngayon upang hindi ubusin ang sustansya ng baging.",
                        "Magpatupad ng mahigpit na anihan tuwing 1 hanggang 2 araw sa panahon ng peak fruiting.",
                        "Anihin kapag matigas at makinis pa ang balat at may sukat na 15 hanggang 22 sentimetro.",
                        "Anihin tuwing maagang umaga (6:00–7:30 AM) bago sumikat ang mainit na araw para sa pinakamalutong na kalidad."
                    ),
                    materialsNeeded = listOf("Pruning Shears", "Ventilated Plastic Crates"),
                    organicRemedy = "Pag-aani tuwing 1–2 araw upang manatiling masipag mamunga ang baging.",
                    criticalRule = "Huwag mag-iwan ng malalaking naninilaw na pipino sa baging; pinipigilan nito ang paglabas ng mga bagong bulaklak."
                )
            }
            if (selectedOptions.contains("cucumber_harvest_crooked_culls")) {
                return DirectCropSolution(
                    title = "Prevention of Curved & Crooked Cucumbers (Hugis Titik C)",
                    tagalogTitle = "Solusyon sa Baluktot at Kurbadong Bunga ng Pipino",
                    severity = "WARNING",
                    badgeColor = AlertAmber,
                    directAnswer = "Ang baluktot na pipino ay dulot ng sagabal sa lambat habang lumalaki, tagtuyot sa lupa, o bahagyang tusok ng fruit fly sa isang bahagi ng bunga.",
                    actionSteps = listOf(
                        "Ayusin ang nakabiting bunga upang malayang nakabitin pababa nang walang sumasabit sa lambat o alambre.",
                        "Panatilihin ang pantay na patubig tuwing 2 hanggang 3 araw upang maging pantay ang paglaki ng mga selula sa magkabilang bahagi ng bunga.",
                        "Balutin ang bunga habang maliit pa upang maprotektahan sa tusok ng insekto.",
                        "Maglagay ng sapat na Potassium (0-0-60) at Boron foliar."
                    ),
                    materialsNeeded = listOf("Paper Bags para sa Bunga", "Potassium Fertilizer", "Solubor"),
                    organicRemedy = "Pag-aayos ng posisyon sa lambat upang malayang bumitin nang tuwid pababa.",
                    criticalRule = "Tiyaking nakabitin nang malaya pababa ang bawat bunga upang maging diretso at primera klase."
                )
            }
            if (selectedOptions.contains("cucumber_harvest_torn_stem")) {
                return DirectCropSolution(
                    title = "Proper Stem-Cutting Harvest Protocol for Cucumbers",
                    tagalogTitle = "Tamang Paraan ng Pagputol ng Tangkay sa Pag-aani",
                    severity = "INFO",
                    badgeColor = LushGreen,
                    directAnswer = "Ang paghila o pagpunit sa bunga gamit ang kamay ay nagdudulot ng bukas na sugat sa base, na nagiging daan ng mabilisang pagkabulok at pagkaubos ng tubig sa imbakan.",
                    actionSteps = listOf(
                        "Gumamit ng matalas na gunting o kutsilyo sa pag-aani.",
                        "Mag-iwan ng 1 hanggang 2 sentimetro ng tangkay (peduncle) na nakakabit sa bawat bunga.",
                        "Iwasang mauntog o maipit ang mga bunga sa crate.",
                        "I-imbak sa malamig na lugar (10–13°C); huwag ilagay sa ibaba ng 7°C upang maiwasan ang chilling injury."
                    ),
                    materialsNeeded = listOf("Matalas na Harvest Shears", "Ventilated Crates na may Sapin"),
                    organicRemedy = "Paggamit ng matalas na gunting at saping dahon sa crate.",
                    criticalRule = "Huwag hilahin ang pipino gamit ang kamay; palaging gupitin ang tangkay."
                )
            }
        }
    }

    return null
}
