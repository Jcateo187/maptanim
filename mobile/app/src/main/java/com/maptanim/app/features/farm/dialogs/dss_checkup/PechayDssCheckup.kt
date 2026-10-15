package com.maptanim.app.features.farm.dialogs.dss_checkup

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Coronavirus
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.ui.graphics.Color

private val ForestGreen = Color(0xFF1B5E20)
private val LushGreen = Color(0xFF2E7D32)
private val AlertAmber = Color(0xFFE65100)
private val DangerRed = Color(0xFFC62828)

/**
 * Diagnostic and Decision-Support System (DSS) activities tailored specifically
 * for Pechay / Petsay (Brassica rapa subsp. chinensis) in Philippine lowland, urban, and highland
 * farm settings based on MapTanim technical data & DA-BPI extension guidelines.
 */
fun getPechayCheckUpActivities(): List<CheckUpActivity> {
    return listOf(
        CheckUpActivity(
            id = "watering",
            title = "Pagdidilig at Kahalumigmig ng Pechay",
            tagalogSubtitle = "Suriin ang moisture sa mababaw na ugat at tigas ng mapuputing tangkay",
            icon = Icons.Default.WaterDrop,
            options = listOf(
                CheckUpOption(
                    id = "pechay_water_normal",
                    title = "Katamtaman at Regular na Patubig",
                    tagalogDesc = "Mamasa-masa ngunit hindi putik ang 5–10 cm na ibabaw ng lupa; dinidilig araw-araw o bawat 2 araw sa umaga",
                    isIdeal = true
                ),
                CheckUpOption(
                    id = "pechay_water_stress",
                    title = "Tagtuyot / Makunat at Matigas na Dahon",
                    tagalogDesc = "Naluluoy sa tanghali, tumitigas at makunat ang puting tangkay, at napipilitang mamulaklak nang maaga"
                ),
                CheckUpOption(
                    id = "pechay_water_overwater",
                    title = "Sobrang Tubig / Baha at Putik sa Kama",
                    tagalogDesc = "Naiipon ang tubig sa paligid ng puno; naninilaw ang lumang dahon at nabubulok ang base (soft rot / damping-off)"
                ),
                CheckUpOption(
                    id = "pechay_water_splash",
                    title = "Pagtalsik ng Putik sa Ilalim ng Dahon",
                    tagalogDesc = "Nababasa at natatalamsikan ng putik ang pinakailalim na dahon at puting tangkay sa malakas na buhos ng tubig"
                )
            )
        ),
        CheckUpActivity(
            id = "fertilizing",
            title = "Abono at Nutrisyon ng Pechay",
            tagalogSubtitle = "Suriin ang balanse ng Nitroheno, Boron laban sa Hollow Stem, at Kalsyo",
            icon = Icons.Default.Spa,
            options = listOf(
                CheckUpOption(
                    id = "pechay_fert_balanced",
                    title = "Balanseng Nutrisyon (Compost + 14-14-14 Basal)",
                    tagalogDesc = "Makakapal, makikintab na madilim na berdeng dahon at matitigas, malulusog na mapuputing tangkay",
                    isIdeal = true
                ),
                CheckUpOption(
                    id = "pechay_fert_nitrogen_def",
                    title = "Kulang sa Nitroheno (Maputlang Dilaw at Mabagal Lumaki)",
                    tagalogDesc = "Naninilaw ang mga lumang dahon mula dulo papasok; manipis, payat, at bansot ang buong halaman",
                    isIdeal = false
                ),
                CheckUpOption(
                    id = "pechay_fert_boron_def",
                    title = "Kulang sa Boron (Guwang at Biyak na Tangkay / Hollow Stem)",
                    tagalogDesc = "May butas o guwang sa gitna ng puting tangkay, may bitak at kulay kayumangging gasgas sa balat ng petiole",
                    isIdeal = false
                ),
                CheckUpOption(
                    id = "pechay_fert_calcium_def",
                    title = "Kulang sa Kalsyo (Tip-burn sa Batang Dahon)",
                    tagalogDesc = "Sunog at tuyot ang gilid ng mga pinakabatang dahon sa gitna ng rosette dahil sa mabilis na paglaki",
                    isIdeal = false
                ),
                CheckUpOption(
                    id = "pechay_fert_excess_nitrogen",
                    title = "Sobra sa Nitroheno (Labis na Malambot at Matubig)",
                    tagalogDesc = "Madilim na berde ngunit labis na malambot ang dahon; mabilis lapitan ng uod at madaling mabulok",
                    isIdeal = false
                )
            )
        ),
        CheckUpActivity(
            id = "temperature_bolting",
            title = "Temperatura at Bolting Control",
            tagalogSubtitle = "Suriin ang epekto ng init ng araw sa pamumulaklak at lutong ng tangkay",
            icon = Icons.Default.Thermostat,
            options = listOf(
                CheckUpOption(
                    id = "pechay_temp_ideal",
                    title = "Katamtamang Init ng Tropiko (20–28°C)",
                    tagalogDesc = "Malusog at siksik ang tindig ng rosette; malutong at makatas ang tangkay sa ilalim ng buong araw",
                    isIdeal = true
                ),
                CheckUpOption(
                    id = "pechay_temp_bolting",
                    title = "Maagang Pamumulaklak / Bolting dulot ng Init (>30°C)",
                    tagalogDesc = "Bumubuka pataas ang tangkay at naglalabas ng dilaw na bulaklak bago pa lumapad nang husto ang mga dahon",
                    isIdeal = false
                ),
                CheckUpOption(
                    id = "pechay_temp_sunscald",
                    title = "Sunog sa Dahon / Sunscald sa Katanghalian",
                    tagalogDesc = "Puting sunog o laylay na mga dahon dulot ng matinding init sa tag-araw na walang patubig",
                    isIdeal = false
                )
            )
        ),
        CheckUpActivity(
            id = "pests",
            title = "Mga Peste sa Dahon at Tangkay ng Pechay",
            tagalogSubtitle = "Suriin ang Diamondback moth, Flea beetle, Aphids, at Harabas",
            icon = Icons.Default.BugReport,
            options = listOf(
                CheckUpOption(
                    id = "pechay_pest_none",
                    title = "Walang Nakikitang Peste sa Taniman",
                    tagalogDesc = "Buo at walang butas ang mga dahon; malinis ang ilalim ng bawat tangkay",
                    isIdeal = true
                ),
                CheckUpOption(
                    id = "pechay_pest_dbm",
                    title = "Diamondback Moth (DBM / Plutella xylostella)",
                    tagalogDesc = "Maliliit na berdeng uod na kumakain sa ilalim ng dahon, nag-iiwan ng maninipis na parang bintana (window-paning) at butas",
                    isIdeal = false
                ),
                CheckUpOption(
                    id = "pechay_pest_flea_beetle",
                    title = "Flea Beetle / Talon-talon (Phyllotreta spp.)",
                    tagalogDesc = "Mabilis lumuksong maliliit na itim na salagubang na gumagawa ng maliliit na bilog na butas (shot holes) sa batang dahon",
                    isIdeal = false
                ),
                CheckUpOption(
                    id = "pechay_pest_aphids",
                    title = "Kuto sa Dahon / Cabbage Aphids (Brevicoryne / Myzus)",
                    tagalogDesc = "Kumpol-kumpol na abuhin o berdeng kuto sa ilalim ng dahon at gitna ng tangkay na nagiging sanhi ng pagkulot",
                    isIdeal = false
                ),
                CheckUpOption(
                    id = "pechay_pest_armyworm",
                    title = "Harabas / Armyworm at Cutworm (Spodoptera spp.)",
                    tagalogDesc = "Malalaking butas sa dahon o pinutol na punla sa gabi; may itim na dumi ng uod sa puno",
                    isIdeal = false
                )
            )
        ),
        CheckUpActivity(
            id = "diseases",
            title = "Mga Sakit at Amag ng Pechay",
            tagalogSubtitle = "Suriin ang Damping-off, Bacterial Soft Rot, at Black Rot",
            icon = Icons.Default.Coronavirus,
            options = listOf(
                CheckUpOption(
                    id = "pechay_dis_clean",
                    title = "Malusog at Walang Sakit ang mga Dahon",
                    tagalogDesc = "Walang amag, basang pagkabulok, o V-shaped na sunog sa mga gilid ng dahon",
                    isIdeal = true
                ),
                CheckUpOption(
                    id = "pechay_dis_damping_off",
                    title = "Damping-off sa Punlaan (Pythium / Rhizoctonia)",
                    tagalogDesc = "Nalulusaw, pumapayat, at nabubuwal ang tangkay ng mga batang punla sa ibabaw ng lupa",
                    isIdeal = false
                ),
                CheckUpOption(
                    id = "pechay_dis_soft_rot",
                    title = "Bacterial Soft Rot (Pectobacterium carotovorum)",
                    tagalogDesc = "Basang-basa, malambot na parang uhog, at mabahong pagkabulok ng puting tangkay at base ng pechay pagkatapos ng ulan",
                    isIdeal = false
                ),
                CheckUpOption(
                    id = "pechay_dis_black_rot",
                    title = "Black Rot (Xanthomonas campestris)",
                    tagalogDesc = "V-shaped na paninilaw at pagkasunog sa gilid ng dahon na may nangingitim na mga ugat (black veins)",
                    isIdeal = false
                ),
                CheckUpOption(
                    id = "pechay_dis_downy_mildew",
                    title = "Downy Mildew (Peronospora parasitica)",
                    tagalogDesc = "Madidilaw na tagpi sa ibabaw ng dahon na may pinong puting amag sa ilalim sa mahalumigmig na panahon",
                    isIdeal = false
                )
            )
        ),
        CheckUpActivity(
            id = "bed_and_spacing",
            title = "Kama, Bunton, at Agwat ng Tanim",
            tagalogSubtitle = "Suriin ang 15–20 cm pagitan, raised beds, at hilling-up sa tangkay",
            icon = Icons.Default.Agriculture,
            options = listOf(
                CheckUpOption(
                    id = "pechay_space_optimal",
                    title = "Tamang Agwat sa Raised Beds (15–20 cm x 20–25 cm)",
                    tagalogDesc = "15–20 cm sa pagitan ng puno at 20–25 cm sa pagitan ng tudling sa 15–20 cm raised bed",
                    isIdeal = true
                ),
                CheckUpOption(
                    id = "pechay_space_crowded",
                    title = "Masyadong Masinsin / Hindi Napanipis (Thinning Needed)",
                    tagalogDesc = "Nagsisiksikan ang mga punla mula sa direct seeding; payat at mahaba ang puting tangkay",
                    isIdeal = false
                ),
                CheckUpOption(
                    id = "pechay_space_flat_bed",
                    title = "Patag na Lupa / Walang Kanal sa Tag-ulan",
                    tagalogDesc = "Naiipon ang tubig sa paligid ng puno; nagiging sanhi ng pagkabulok ng ugat at puting tangkay",
                    isIdeal = false
                ),
                CheckUpOption(
                    id = "pechay_space_hilling_needed",
                    title = "Nangangailangan ng Bahagyang Hilling-Up",
                    tagalogDesc = "Lantad ang ibabaw ng ugat sa malalakas na dilig; kailangang tambakan ng buhaghag na lupa para sa tibay",
                    isIdeal = false
                )
            )
        ),
        CheckUpActivity(
            id = "harvesting",
            title = "Pag-aani at Post-Harvest Quality",
            tagalogSubtitle = "Suriin ang 25–35 araw na maturity, umagang anihan, at pag-iwas sa bulaklak",
            icon = Icons.Default.ContentCut,
            options = listOf(
                CheckUpOption(
                    id = "pechay_harvest_ready",
                    title = "Matigas, Malapad ang Tangkay, at Malutong (25–35 Araw)",
                    tagalogDesc = "Naabot na ang 25–35 araw; buo ang kumpol, malalapad at mapuputi ang tangkay bago mamukadkad ang bulaklak",
                    isIdeal = true
                ),
                CheckUpOption(
                    id = "pechay_harvest_morning",
                    title = "Pag-aani sa Maagang Umaga (6:00–7:30 AM)",
                    tagalogDesc = "Pinitas bago uminit ang araw habang puno pa ng tubig at crispness ang mga tangkay",
                    isIdeal = true
                ),
                CheckUpOption(
                    id = "pechay_harvest_whole_pull",
                    title = "Pagbunot Kasama ang Ugat at Paghuhugas",
                    tagalogDesc = "Binubunot nang buo at hinuhugasan sa malinis na tubig ang ugat para sa mas mahabang shelf life sa palengke",
                    isIdeal = true
                ),
                CheckUpOption(
                    id = "pechay_harvest_overmature",
                    title = "Lampas na sa Panahon / Namulaklak na (Bolted)",
                    tagalogDesc = "Naglabas na ng dilaw na bulaklak; makunat, manipis, at makahoy na ang puting tangkay",
                    isIdeal = false
                )
            )
        )
    )
}

/**
 * Diagnostic solver providing actionable, organic, and agronomic direct solutions
 * for Pechay / Petsay growers based on MapTanim technical data & DA-BPI.
 */
fun solvePechayProblem(
    activityId: String,
    selectedOptions: Set<String>
): DirectCropSolution? {
    if (selectedOptions.isEmpty()) return null

    when (activityId) {
        "watering" -> {
            if (selectedOptions.contains("pechay_water_stress")) {
                return DirectCropSolution(
                    title = "Drought Stress & Tough Petiole Recovery",
                    tagalogTitle = "Lunas sa Tagtuyot at Pagkunat ng Tangkay ng Pechay",
                    severity = "WARNING",
                    badgeColor = AlertAmber,
                    directAnswer = "Mababaw ang ugat ng pechay (10–15 cm). Kapag natuyo ang lupa sa init, tumitigas at nagiging makunat ang puting tangkay (petiole) at maagang namumulaklak (bolting).",
                    actionSteps = listOf(
                        "Magdilig agad nang banayad tuwing 6:00–7:30 AM gamit ang fine sprinkler o drip irrigation.",
                        "Maglagay ng 2–3 cm na rice straw o carbonized rice hull (CRH) sa ibabaw ng kama upang mapanatiling malamig ang lupa.",
                        "Kung matindi ang sikat ng araw (>32°C), maglagay ng 30% shade net sa katanghalian.",
                        "Huwag magdilig sa mainit na tanghali upang hindi maluto ang malalambot na dahon."
                    ),
                    materialsNeeded = listOf("Fine Rose Sprinkler", "Rice Straw Mulch", "Shade Netting"),
                    organicRemedy = "Paglalagay ng organic mulch at drenching ng diluted Vermitea upang mabilis na mabawi ang moisture sa cell walls.",
                    criticalRule = "Panatilihing mamasa-masa ang lupa sa buong maikling 30-araw na siklo; ang tagtuyot sa ika-20 araw ay magpapatigas sa tangkay."
                )
            }
            if (selectedOptions.contains("pechay_water_overwater")) {
                return DirectCropSolution(
                    title = "Waterlogged Bed Drainage & Soft Rot Prevention",
                    tagalogTitle = "Lunas sa Sobrang Tubig at Baha sa Pechay",
                    severity = "DANGER",
                    badgeColor = DangerRed,
                    directAnswer = "Mabilis mabulok ang puting tangkay ng pechay kapag nakababad sa putik. Ang kawalan ng oxygen sa ugat ay nagiging sanhi ng paninilaw at nakamamatay na Bacterial Soft Rot.",
                    actionSteps = listOf(
                        "Itigil ang pagdidilig ng 1–2 araw hanggang sa bahagyang matuyo ang ibabaw ng kama.",
                        "Palalimin ang drainage furrows sa pagitan ng mga kama sa 20–30 cm.",
                        "Gupitin at itapon ang mga panlabas na dahong naninilaw at nabubulok.",
                        "Magbudbod ng abo ng kahoy o CRH sa paligid ng puno upang maalis ang pagkakadikit ng tangkay sa putik."
                    ),
                    materialsNeeded = listOf("Pala para sa Kanal", "Wood Ash", "Carbonized Rice Hull"),
                    organicRemedy = "Abo ng kahoy at tuyong ipa upang higupin ang labis na tubig sa paligid ng petiole.",
                    criticalRule = "Laging magtanim sa 15–20 cm raised beds tuwing tag-ulan sa Pilipinas."
                )
            }
            if (selectedOptions.contains("pechay_water_splash")) {
                return DirectCropSolution(
                    title = "Mud Splash & Petiole Staining Prevention",
                    tagalogTitle = "Pagpigil sa Pagtalsik ng Putik sa Puting Tangkay",
                    severity = "WARNING",
                    badgeColor = AlertAmber,
                    directAnswer = "Ang putik na tumatalsik sa puting tangkay ay nagdudulot ng mantsa at nagdadala ng bacteria at amag mula sa lupa.",
                    actionSteps = listOf(
                        "Maglatag ng malinis na dayami o tuyong ipa sa ibabaw ng kama.",
                        "Gumamit ng pino at banayad na pandilig sa halip na malalaking buhos ng hose.",
                        "Diligan ang lupa sa tabi ng puno, huwag ibuhos mula sa itaas."
                    ),
                    materialsNeeded = listOf("Fine Sprinkler", "Clean Straw Mulch"),
                    organicRemedy = "Mulching gamit ang dayami upang pigilan ang pagtalbog ng patak ng ulan.",
                    criticalRule = "Ang malinis na puting tangkay ang batayan ng magandang presyo ng pechay sa palengke."
                )
            }
            if (selectedOptions.contains("pechay_water_normal")) {
                return DirectCropSolution(
                    title = "Optimal Pechay Moisture Plan Maintained",
                    tagalogTitle = "Wastong Kahalumigmig ng Pechay",
                    severity = "IDEAL",
                    badgeColor = LushGreen,
                    directAnswer = "Napakaganda ng kalagayan ng patubig! Ang pare-parehong moisture ay nagbibigay ng malulutong at makakapal na puting tangkay.",
                    actionSteps = listOf(
                        "Ipagpatuloy ang regular na pagdidilig tuwing maagang umaga.",
                        "Suriin ang lupa gamit ang daliri sa 2 cm lalim bago magdilig kung maulap."
                    ),
                    materialsNeeded = listOf("Pangdilig"),
                    organicRemedy = "Pagpapanatili ng organic mulch sa ibabaw ng kama.",
                    criticalRule = "Panatilihing pantay ang dilig hanggang sa anihan."
                )
            }
        }

        "fertilizing" -> {
            if (selectedOptions.contains("pechay_fert_boron_def")) {
                return DirectCropSolution(
                    title = "Boron Deficiency & Hollow Stem Correction",
                    tagalogTitle = "Lunas sa Hollow Stem / Kulang sa Boron sa Pechay",
                    severity = "WARNING",
                    badgeColor = AlertAmber,
                    directAnswer = "Ang Boron deficiency ay nagiging sanhi ng 'Hollow Stem'—guwang at biyak na puting tangkay na may kulay kayumangging gasgas sa balat. Madalas itong mangyari sa lupang mababa ang organic matter o may mataas na pH.",
                    actionSteps = listOf(
                        "I-foliar spray ang dahon ng Solubor o Borax solution (1 gramo bawat 1 litrong tubig) tuwing hapon nang 1–2 beses bago ang ika-20 araw.",
                        "Ihalo ang organikong compost at vermicast sa lupa bago magtanim; mayaman ito sa natural na micronutrients.",
                        "Panatilihing regular ang patubig upang tuloy-tuloy ang paghigop ng ugat sa boron sa lupa."
                    ),
                    materialsNeeded = listOf("Solubor o Borax Foliar Fertilizer", "Organikong Kompost", "Hand Sprayer"),
                    organicRemedy = "Vermitea drench na mayaman sa bio-available micronutrients at trace minerals.",
                    criticalRule = "Huwag sosobrahan ang boron spray; 1 gramo bawat litro lamang dahil toxic sa halaman ang labis na boron."
                )
            }
            if (selectedOptions.contains("pechay_fert_nitrogen_def")) {
                return DirectCropSolution(
                    title = "Nitrogen Deficiency Treatment for Leafy Greens",
                    tagalogTitle = "Lunas sa Kulang sa Nitroheno (Maputlang Dahon)",
                    severity = "WARNING",
                    badgeColor = AlertAmber,
                    directAnswer = "Dahil 30 araw lamang ang buhay ng pechay, mabilis itong nangangailangan ng 100–150 kg N/ha para sa vegetative expansion. Ang paninilaw ng lumang dahon ay patunay ng kakulangan nito.",
                    actionSteps = listOf(
                        "Mag-drench ng Fish Amino Acid (FAA) o Vermitea sa base ng mga puno (10–15 ml bawat litro ng tubig) tuwing ika-4 na araw.",
                        "Bilang synthetic side-dress, maglagay ng 1 kutsaritang Urea o Complete (14-14-14) sa pagitan ng mga hanay sa ika-10 at ika-18 araw pagkasibol, at diligan agad.",
                        "Magdagdag ng 1 dakot ng vermicast sa base ng bawat puno."
                    ),
                    materialsNeeded = listOf("Fish Amino Acid (FAA)", "Complete 14-14-14", "Vermicast"),
                    organicRemedy = "Fermented Plant Juice (FPJ gawa sa kangkong o talbos) at FAA drench tuwing hapon.",
                    criticalRule = "Huwag ilagay ang synthetic fertilizer nang dikit sa pinong tangkay upang hindi masunog."
                )
            }
            if (selectedOptions.contains("pechay_fert_calcium_def")) {
                return DirectCropSolution(
                    title = "Calcium Deficiency & Tip-burn Intervention",
                    tagalogTitle = "Lunas sa Tip-burn sa Gilid ng Batang Dahon",
                    severity = "WARNING",
                    badgeColor = AlertAmber,
                    directAnswer = "Ang Tip-burn ay sanhi ng kakulangan ng calcium sa mabilis na lumalaking gilid ng dahon sa ilalim ng matinding sikat ng araw.",
                    actionSteps = listOf(
                        "Mag-spray ng Water-Soluble Calcium (WCA gawa sa balat ng itlog at suka, 2 kutsara sa 16L tubig) tuwing hapon.",
                        "Panatilihing pantay ang patubig araw-araw upang tuloy-tuloy ang pag-akyat ng calcium.",
                        "Bawasan ang sobrang potassium at nitrogen fertilizer na humaharang sa calcium absorption."
                    ),
                    materialsNeeded = listOf("Water-Soluble Calcium (WCA)", "Calcium Nitrate Foliar Spray"),
                    organicRemedy = "Inihaw na balat ng itlog na ibinabad sa sukang tuba (WCA) spray linggu-linggo.",
                    criticalRule = "I-spray ang calcium sa ilalim at ibabaw ng mga batang dahon sa hapon."
                )
            }
            if (selectedOptions.contains("pechay_fert_excess_nitrogen")) {
                return DirectCropSolution(
                    title = "Excess Nitrogen Management",
                    tagalogTitle = "Pamamahala sa Sobrang Nitroheno sa Pechay",
                    severity = "WARNING",
                    badgeColor = AlertAmber,
                    directAnswer = "Ang labis na nitrogen ay nagpapadali sa paglambot ng tangkay, umaakit ng aphids at DBM, at nagpapabilis ng pagkabulok pagkaani.",
                    actionSteps = listOf(
                        "Itigil agad ang paglalagay ng urea at nitrogen fertilizers.",
                        "Mag-spray ng diluted wood ash water o MOP (0-0-60) sa mababang konsentrasyon upang patibayin ang cell walls.",
                        "Itigil ang lahat ng abono 7 araw bago mag-ani."
                    ),
                    materialsNeeded = listOf("Wood Ash Extract", "Malinis na Tubig"),
                    organicRemedy = "Pagbubudbod ng pinong uling at abo upang balansehin ang lupa.",
                    criticalRule = "Ang malutong at matibay na tangkay ay nangangailangan ng balanseng calcium at potash, hindi puro nitrogen."
                )
            }
            if (selectedOptions.contains("pechay_fert_balanced")) {
                return DirectCropSolution(
                    title = "Balanced Pechay Nutrition Confirmed",
                    tagalogTitle = "Balanseng Nutrisyon ng Pechay",
                    severity = "IDEAL",
                    badgeColor = LushGreen,
                    directAnswer = "Napakaganda ng kalusugan ng iyong pechay! Ang sapat na abono ay magbibigay ng mabigat at malutong na ani sa loob ng 25–30 araw.",
                    actionSteps = listOf(
                        "Ipagpatuloy ang regular na patubig.",
                        "Ihanda ang kama para sa susunod na batch ng binhi."
                    ),
                    materialsNeeded = listOf("Kompost para sa Susunod na Siklo"),
                    organicRemedy = "Compost tea maintenance.",
                    criticalRule = "Panatilihin ang sustansya sa maikling 30-araw na turnaround."
                )
            }
        }

        "temperature_bolting" -> {
            if (selectedOptions.contains("pechay_temp_bolting")) {
                return DirectCropSolution(
                    title = "Heat Bolting & Premature Flowering Control",
                    tagalogTitle = "Lunas sa Maagang Pamumulaklak / Bolting ng Pechay",
                    severity = "WARNING",
                    badgeColor = AlertAmber,
                    directAnswer = "Kapag lumagpas sa 30°C ang init o humaba ang matinding sikat ng araw sa tag-araw, natural na nagmamadali ang pechay na mamulaklak bago pa lumapad ang mga dahon. Tumitigas at nagiging makahoy ang tangkay.",
                    actionSteps = listOf(
                        "Anihin na agad ang buong halaman ngayon bago bumuka ang mga dilaw na bulaklak.",
                        "Para sa mga bagong tanim, maglagay ng 30–40% shade net sa ibabaw ng kama sa katanghalian (10:30 AM–2:30 PM).",
                        "Diligan nang maaga sa umaga at hapon upang palamigin ang temperatura ng lupa.",
                        "Pumili ng mga bolting-resistant varieties tulad ng Black Behi o Pavito sa tag-araw."
                    ),
                    materialsNeeded = listOf("30–40% Shade Netting", "Black Behi Seeds"),
                    organicRemedy = "Pagpapanatili ng makapal na mulch upang palamigin ang root zone.",
                    criticalRule = "Anihin agad kapag napansing sumusulpot ang gitnang bulaklak; masarap pa ang tangkay basta't hindi pa bukas ang dilaw na bulaklak."
                )
            }
            if (selectedOptions.contains("pechay_temp_sunscald")) {
                return DirectCropSolution(
                    title = "Sunscald & Leaf Scorch Prevention",
                    tagalogTitle = "Lunas sa Sunog sa Dahon ng Pechay sa Init",
                    severity = "WARNING",
                    badgeColor = AlertAmber,
                    directAnswer = "Mabilis malanta at masunog ang malalapad na dahon ng pechay sa matinding araw sa tag-init kung walang patubig.",
                    actionSteps = listOf(
                        "Maglagay ng pansamantalang shade net o dahon ng niyog sa katanghalian.",
                        "Panatilihing mamasa-masa ang lupa sa paligid ng ugat.",
                        "Gupitin ang mga tuyot na panlabas na dahon."
                    ),
                    materialsNeeded = listOf("Shade Netting"),
                    organicRemedy = "Aloe vera extract spray upang pabilisin ang recovery ng dahon.",
                    criticalRule = "Huwag magdilig sa tirik na araw ng tanghali."
                )
            }
            if (selectedOptions.contains("pechay_temp_ideal")) {
                return DirectCropSolution(
                    title = "Optimal Pechay Climate Maintained",
                    tagalogTitle = "Mainam na Temperatura para sa Pechay",
                    severity = "IDEAL",
                    badgeColor = LushGreen,
                    directAnswer = "Napakainam ng klima (20–28°C)! Mabilis lalapad ang mga dahon at puputi ang mga tangkay sa loob ng 25–30 araw.",
                    actionSteps = listOf(
                        "Panatilihin ang sapat na araw at hangin.",
                        "I-stagger ang pagtatanim tuwing 7–10 araw para sa tuloy-tuloy na supply."
                    ),
                    materialsNeeded = listOf("Binhi para sa Staggered Planting"),
                    organicRemedy = "Compost mulch maintenance.",
                    criticalRule = "Sulitin ang magandang klima sa pamamagitan ng lingguhang pagtatanim."
                )
            }
        }

        "pests" -> {
            if (selectedOptions.contains("pechay_pest_dbm")) {
                return DirectCropSolution(
                    title = "Diamondback Moth (DBM) Biological Eradication",
                    tagalogTitle = "Lunas sa Diamondback Moth (DBM) sa Pechay",
                    severity = "DANGER",
                    badgeColor = DangerRed,
                    directAnswer = "Ang Diamondback Moth (Plutella xylostella) ang #1 peste ng mga brassica sa Pilipinas. Ang maliliit na berdeng uod ay kumakain sa ilalim ng dahon, nag-iiwan ng window-paning at malalaking butas na sumisira sa benta ng pechay.",
                    actionSteps = listOf(
                        "Bacillus thuringiensis (Bt): Mag-spray ng Bt biopesticide tuwing alas-5:00 ng hapon bawat 5–7 araw; lubos itong ligtas sa tao ngunit pumapatay sa DBM larvae sa loob ng 48 oras.",
                        "Mustard Trap Crop: Magtanim ng mustasa sa paligid ng kamada 1–2 linggo bago ang pechay upang doon pumunta ang mga uod.",
                        "Handpicking: Manu-manong alisin ang mga uod at pupa sa ilalim ng mga dahon sa maagang umaga.",
                        "Yellow Sticky Traps: Magkabit ng yellow cards sa taas na 25 cm upang mahuli ang mga lumilipad na adult moths."
                    ),
                    materialsNeeded = listOf("Bacillus thuringiensis (Bt)", "Yellow Sticky Traps", "Mustard Seeds para sa Trap Crop"),
                    organicRemedy = "Chili-Garlic-Neem Spray: 5 pirasong siling labuyo + 3 butil ng bawang + 1 kutsarang neem extract sa 1L tubig na may kaunting sabon.",
                    criticalRule = "Huwag gumamit ng synthetic chemical insecticide malapit sa anihan dahil 30 araw lamang ang siklo ng pechay; Bt at neem ang pinakaligtas."
                )
            }
            if (selectedOptions.contains("pechay_pest_flea_beetle")) {
                return DirectCropSolution(
                    title = "Flea Beetle (Shot Holes) Defense",
                    tagalogTitle = "Lunas sa Flea Beetle / Talon-talon sa Pechay",
                    severity = "WARNING",
                    badgeColor = AlertAmber,
                    directAnswer = "Ang mga lumuluksong flea beetle (Phyllotreta spp.) ay nanginginain sa mga batang punla ng pechay, gumagawa ng daan-daang maliliit na butas (shot holes) na nagpapabansot sa halaman.",
                    actionSteps = listOf(
                        "Wood Ash Dusting: Magbudbod ng pinong abo ng kahoy sa mamasa-masang dahon sa madaling araw; ayaw ng beetle ang gaspang at amoy ng abo.",
                        "Row Covers / Netting: Takpan ng pinong nylon netting ang mga kamada sa unang 2 linggo pagkasibol.",
                        "Neem Oil Spray: Mag-spray ng 0.5% neem solution sa ilalim at ibabaw ng mga dahon.",
                        "Trap Cropping: Magpunla ng labanos o mustasa sa gilid bilang sacrificial crop."
                    ),
                    materialsNeeded = listOf("Pinong Abo ng Kahoy (Wood Ash)", "Fine Nylon Insect Net", "Neem Oil Solution"),
                    organicRemedy = "Abo ng kahoy at dinurog na sili powder na ibinubudbod sa basang dahon.",
                    criticalRule = "Protektahan ang pechay sa unang 14 araw; kapag malaki na ang dahon, hindi na ito mapapatay ng flea beetle."
                )
            }
            if (selectedOptions.contains("pechay_pest_aphids")) {
                return DirectCropSolution(
                    title = "Cabbage Aphid Colonization Control",
                    tagalogTitle = "Lunas sa Kuto sa Dahon / Aphids sa Pechay",
                    severity = "WARNING",
                    badgeColor = AlertAmber,
                    directAnswer = "Ang mga kuto ay sumisipsip ng katas sa ilalim ng dahon at nagdudulot ng kulot at banat na paglaki ng puting tangkay.",
                    actionSteps = listOf(
                        "Water Jet: Wisikan ng malakas na pino na tubig ang ilalim ng mga dahon sa umaga.",
                        "Insecticidal Soap: Mag-spray ng 1 kutsarang liquid Castile soap sa 4L tubig tuwing hapon bawat 3 araw.",
                        "Yellow Sticky Cards: Maglagay ng yellow traps upang mahuli ang mga winged aphids.",
                        "Pangalagaan ang mga ladybugs na natural na kumakain ng aphids."
                    ),
                    materialsNeeded = listOf("Castile Soap", "Yellow Sticky Traps", "Hand Sprayer"),
                    organicRemedy = "Chili-Garlic soap spray solution.",
                    criticalRule = "Huwag mag-spray ng sabon sa loob ng 3 araw bago mag-ani upang hindi magbago ang lasa."
                )
            }
            if (selectedOptions.contains("pechay_pest_armyworm")) {
                return DirectCropSolution(
                    title = "Armyworm & Cutworm Eradication",
                    tagalogTitle = "Lunas sa Harabas at Cutworm sa Pechay",
                    severity = "DANGER",
                    badgeColor = DangerRed,
                    directAnswer = "Pumuputol sa leeg ng mga punla sa gabi o gumagawa ng malalaking butas sa mga panlabas na dahon.",
                    actionSteps = listOf(
                        "Mag-inspeksyon sa gabi gamit ang flashlight at manu-manong pulutin ang mga uod.",
                        "Mag-spray ng Bacillus thuringiensis (Bt) biopesticide sa dapit-hapon.",
                        "Linisin ang mga damo sa paligid kung saan nagtatago ang mga uod."
                    ),
                    materialsNeeded = listOf("Bacillus thuringiensis (Bt)", "Flashlight"),
                    organicRemedy = "Bt biopesticide na may kasamang neem extract.",
                    criticalRule = "Pulutin ang uod habang maliit pa upang hindi maubos ang kumpol ng pechay."
                )
            }
            if (selectedOptions.contains("pechay_pest_none")) {
                return DirectCropSolution(
                    title = "Pest-Free Pechay Beds Maintained",
                    tagalogTitle = "Walang Peste sa Taniman ng Pechay",
                    severity = "IDEAL",
                    badgeColor = LushGreen,
                    directAnswer = "Malinis at buo ang mga dahon ng iyong pechay! Ipagpatuloy ang regular na pag-iinspeksyon.",
                    actionSteps = listOf(
                        "Mag-inspeksyon linggu-linggo sa ilalim ng mga dahon.",
                        "Pangalagaan ang beneficial predators (ladybugs, lacewings)."
                    ),
                    materialsNeeded = listOf("Magnifying Glass"),
                    organicRemedy = "Compost tea foliar maintenance.",
                    criticalRule = "Ang maagang pagtuklas ang pinakamahusay na paraan upang mapanatiling Grade-A ang ani."
                )
            }
        }

        "diseases" -> {
            if (selectedOptions.contains("pechay_dis_soft_rot")) {
                return DirectCropSolution(
                    title = "Bacterial Soft Rot Eradication in Pechay",
                    tagalogTitle = "Lunas sa Bacterial Soft Rot (Mabahong Pagkabulok)",
                    severity = "DANGER",
                    badgeColor = DangerRed,
                    directAnswer = "Ang Bacterial Soft Rot (Pectobacterium carotovorum) ay pumapasok sa mga sugat ng puting tangkay sa basang panahon. Nagdudulot ito ng malambot, parang uhog, at mabahong pagkabulok ng buong kumpol.",
                    actionSteps = listOf(
                        "Bunutin at ilibing sa malayo (30 cm lalim) ang lahat ng apektadong halaman; HUWAG ilagay sa compost pile.",
                        "Bawasan agad ang pagdidilig at patuyuin ang ibabaw ng kama.",
                        "Magbudbod ng apog (agricultural lime) o abo sa butas na pinagbunutan upang ma-disinfect ang lupa.",
                        "Siguraduhing 15–20 cm ang taas ng mga kama at malalim ang drainage canal."
                    ),
                    materialsNeeded = listOf("Pala para sa Paglilibing", "Agricultural Lime", "Wood Ash"),
                    organicRemedy = "Pagbubudbod ng pinong apog at abo sa lupa upang pigilan ang bakterya.",
                    criticalRule = "Walang kemikal na gamot na makapagpapagaling sa bulok na; agarang pagbubunot at kalinisan ang tanging paraan."
                )
            }
            if (selectedOptions.contains("pechay_dis_black_rot")) {
                return DirectCropSolution(
                    title = "Black Rot (Xanthomonas campestris) Management",
                    tagalogTitle = "Lunas sa Black Rot sa Pechay",
                    severity = "DANGER",
                    badgeColor = DangerRed,
                    directAnswer = "Ang Black Rot ay nagdudulot ng V-shaped na paninilaw at pagkasunog sa gilid ng dahon na may nangingitim na mga ugat (black veins). Kumakalat ito sa pamamagitan ng talsik ng ulan at maruming binhi.",
                    actionSteps = listOf(
                        "Gupitin at sirain ang mga apektadong dahon bago kumalat sa buong halaman.",
                        "Iwasang magdilig mula sa itaas kung saan tatalsik ang tubig sa mga dahon.",
                        "Sa susunod na tanim, ibabad ang binhi sa mainit na tubig (50°C) sa loob ng 20 minuto upang patayin ang bakterya bago ihasik.",
                        "Mag-crop rotation: huwag itanim ang pechay sa lupang katataniman lamang ng repolyo o mustasa."
                    ),
                    materialsNeeded = listOf("Termometro para sa Hot Water Seed Treatment", "Pruning Shears"),
                    organicRemedy = "Hot water seed treatment (50°C sa 20 minuto) bago maghasik.",
                    criticalRule = "Huwag humawak sa malulusog na tanim pagkatapos humawak sa dahong may black rot upang hindi mailipat ang bakterya."
                )
            }
            if (selectedOptions.contains("pechay_dis_damping_off")) {
                return DirectCropSolution(
                    title = "Seedling Damping-off Intervention",
                    tagalogTitle = "Lunas sa Damping-off sa Punlaan ng Pechay",
                    severity = "DANGER",
                    badgeColor = DangerRed,
                    directAnswer = "Nalulusaw at nabubuwal ang mga batang punla sa seedling tray kapag sobrang basa ang potting mix at kulang sa araw.",
                    actionSteps = listOf(
                        "Ilipat ang trays sa mahangin at may sikat ng araw na lugar.",
                        "Bawasan ang pagdidilig; diligan lamang kapag tuyo na ang ibabaw.",
                        "Alisin ang mga nakatumbang punla kasama ang lupa nito.",
                        "Gumamit ng sterile mix (pinong CRH + vermicast) sa susunod na pagpupunla."
                    ),
                    materialsNeeded = listOf("Sterile Seedling Mix", "CRH"),
                    organicRemedy = "Trichoderma harzianum bio-fungicide drench sa potting mix.",
                    criticalRule = "Huwag maghasik nang masyadong masinsin upang magkaroon ng sapat na hangin."
                )
            }
            if (selectedOptions.contains("pechay_dis_downy_mildew")) {
                return DirectCropSolution(
                    title = "Downy Mildew Treatment for Brassicas",
                    tagalogTitle = "Lunas sa Downy Mildew sa Pechay",
                    severity = "WARNING",
                    badgeColor = AlertAmber,
                    directAnswer = "Nagdudulot ng madidilaw na tagpi sa ibabaw ng dahon na may puting amag sa ilalim sa mahalumigmig na panahon.",
                    actionSteps = listOf(
                        "Gupitin ang mga apektadong lumang dahon sa ilalim.",
                        "Mag-spray ng Baking Soda solution (1 tsp sa 2L tubig) na may kaunting sabon sa ilalim ng dahon.",
                        "Panatilihing tuyo ang mga dahon bago magtakipsilim."
                    ),
                    materialsNeeded = listOf("Baking Soda", "Mild Soap", "Pruning Shears"),
                    organicRemedy = "Baking soda spray sa maagang umaga.",
                    criticalRule = "Iwasan ang pagdidilig sa hapon upang hindi magdamag na basa ang dahon."
                )
            }
            if (selectedOptions.contains("pechay_dis_clean")) {
                return DirectCropSolution(
                    title = "Disease-Free Pechay Crop Confirmed",
                    tagalogTitle = "Malusog at Walang Sakit ang Pechay",
                    severity = "IDEAL",
                    badgeColor = LushGreen,
                    directAnswer = "Napakalinis at walang anumang amag o pagkabulok ang iyong pechay! Ipagpatuloy ang magandang drainage at sirkulasyon ng hangin.",
                    actionSteps = listOf(
                        "Panatilihing tuyo ang mga dahon sa gabi.",
                        "Ipagpatuloy ang regular na sanitasyon ng kama."
                    ),
                    materialsNeeded = listOf("Malinis na Pandilig"),
                    organicRemedy = "Compost tea maintenance.",
                    criticalRule = "Ang malinis na taniman ay proteksyon laban sa lahat ng sakit."
                )
            }
        }

        "bed_and_spacing" -> {
            if (selectedOptions.contains("pechay_space_crowded")) {
                return DirectCropSolution(
                    title = "Thinning & Spacing Adjustment for Pechay",
                    tagalogTitle = "Lunas sa Masyadong Masinsin na Punla (Thinning)",
                    severity = "WARNING",
                    badgeColor = AlertAmber,
                    directAnswer = "Kapag masinsin ang punla mula sa direct seeding, nag-aagawan ang mga puno sa araw at pataba. Nagiging payat at mahaba ang puting tangkay sa halip na matambok at malapad.",
                    actionSteps = listOf(
                        "Magsagawa agad ng thinning (paghimay) 10–14 araw pagkasibol: panatilihin ang 15–20 cm na distansya bawat puno.",
                        "Ang mga nahimay na batang punla ay maaaring kainin agad bilang masustansyang baby pechay o ilipat-tanim sa ibang kama sa hapon.",
                        "Mag-iwan ng 20–25 cm sa pagitan ng mga tudling."
                    ),
                    materialsNeeded = listOf("Hand Trowel para sa Paglilipat-Tanim"),
                    organicRemedy = "Kainin ang nahimay na seedlings bilang sariwang baby greens sa sopas o noodles.",
                    criticalRule = "Gawin ang thinning bago mag-15 araw; kapag pinatagal, magbubuhol ang mga ugat at babansot ang parehong halaman."
                )
            }
            if (selectedOptions.contains("pechay_space_flat_bed")) {
                return DirectCropSolution(
                    title = "Raised Bed Elevation & Drainage",
                    tagalogTitle = "Paggawa ng Matataas na Kama para sa Pechay",
                    severity = "WARNING",
                    badgeColor = AlertAmber,
                    directAnswer = "Mababaw at sensitibo ang ugat ng pechay sa pagkababad sa tubig. Ang patag na lupang walang kanal ay sanhi ng pagkabulok ng ugat sa tag-ulan.",
                    actionSteps = listOf(
                        "Gumawa ng 1-meter wide raised beds na may 15–20 cm ang taas.",
                        "Maghukay ng 25–30 cm na drainage furrows sa pagitan ng mga kama.",
                        "Ihalo ang 2 sako ng compost at CRH bawat kama upang maging buhaghag ang lupa."
                    ),
                    materialsNeeded = listOf("Pala at Asarol", "Kompost at CRH"),
                    organicRemedy = "Pagbubuhaghag ng lupa gamit ang compost at ipa.",
                    criticalRule = "Ang 15–20 cm raised bed ay nagbibigay ng agarang proteksyon laban sa baha."
                )
            }
            if (selectedOptions.contains("pechay_space_hilling_needed")) {
                return DirectCropSolution(
                    title = "Hilling-Up & Stem Support Protocol",
                    tagalogTitle = "Wastong Hilling-Up sa Base ng Tangkay ng Pechay",
                    severity = "WARNING",
                    badgeColor = AlertAmber,
                    directAnswer = "Dahil mabilis lumaki ang mga dahon ng pechay, madali itong mabuwal kapag mahina ang kapit ng ugat o nahugasan ang lupa sa malalakas na dilig.",
                    actionSteps = listOf(
                        "Magsagawa ng magaan na pagtatambak ng buhaghag na lupa (hilling-up) sa base ng puno sa ika-15 hanggang ika-20 araw.",
                        "Ihalo ang kaunting compost sa itatambak na lupa.",
                        "Mag-ingat na huwag masugatan ang puting tangkay habang nagtatambak."
                    ),
                    materialsNeeded = listOf("Hand Trowel", "Buhaghag na Kompost"),
                    organicRemedy = "Pagtatambak ng pinaghalong compost at vermicast para sa dagdag na tibay at sustansya.",
                    criticalRule = "Huwag itambak nang lampas sa growing point ng puso upang hindi mabulok ang gitna."
                )
            }
            if (selectedOptions.contains("pechay_space_optimal")) {
                return DirectCropSolution(
                    title = "Optimal Pechay Density Maintained",
                    tagalogTitle = "Tamang Agwat at Sukat ng Kama",
                    severity = "IDEAL",
                    badgeColor = LushGreen,
                    directAnswer = "Napakagaling! Ang 15–20 cm x 20–25 cm na agwat sa raised bed ay nagbibigay ng pinakamalapad at pinakamabigat na puting tangkay sa bawat puno.",
                    actionSteps = listOf(
                        "Panatilihing malinis ang mga kanal sa pagitan ng kama.",
                        "Ipagpatuloy ang regular na pagdidilig."
                    ),
                    materialsNeeded = listOf("Spacing Guide"),
                    organicRemedy = "Mulch maintenance.",
                    criticalRule = "Panatilihin ang standard spacing para sa Grade-A market weight."
                )
            }
        }

        "harvesting" -> {
            if (selectedOptions.contains("pechay_harvest_ready") || selectedOptions.contains("pechay_harvest_morning")) {
                return DirectCropSolution(
                    title = "Peak Freshness Morning Harvest Protocol",
                    tagalogTitle = "Wastong Pamamaraan ng Pag-aani sa Umaga",
                    severity = "IDEAL",
                    badgeColor = LushGreen,
                    directAnswer = "Handa na para sa pinakamalutong at pinakamasarap na ani! Sa ika-25 hanggang ika-35 araw, malalapad at mapuputi na ang mga tangkay na may makapal na berdeng dahon bago lumabas ang bulaklak.",
                    actionSteps = listOf(
                        "Anihin sa maagang umaga (6:00–7:30 AM) bago sumikat ang mainit na araw habang puno pa ng tubig ang mga selula.",
                        "Paraan 1 (Pagbunot): Hilahin nang marahan ang buong halaman kasama ang ugat; hugasan ang ugat sa malinis na tubig upang manatiling sariwa nang 2–3 araw.",
                        "Paraan 2 (Paggupit): Gupitin ang tangkay sa mismong base gamit ang matalas na kutsilyo, mag-iwan ng 1 cm.",
                        "I-trim ang anumang marumi o dilaw na panlabas na dahon.",
                        "Ilagay sa mga ventilated plastic crate sa malilim na lugar; huwag ibilad sa araw."
                    ),
                    materialsNeeded = listOf("Matalas na Kutsilyo", "Planggana ng Malinis na Tubig para sa Hugas", "Ventilated Crates"),
                    organicRemedy = "Pagpapanatiling basa ng ugat gamit ang basang sako upang hindi malanta patungong palengke.",
                    criticalRule = "Huwag mag-ani sa katanghalian; ang mainit na dahon ay mabilis malanta at mangingitim sa loob lamang ng ilang oras."
                )
            }
            if (selectedOptions.contains("pechay_harvest_whole_pull")) {
                return DirectCropSolution(
                    title = "Whole Plant Root Retention for Extended Shelf Life",
                    tagalogTitle = "Pagbunot Kasama ang Ugat para sa Mahabang Imbakan",
                    severity = "IDEAL",
                    badgeColor = LushGreen,
                    directAnswer = "Ang pagbunot kasama ang buong ugat at paghuhugas sa malinis na tubig ay nagpapanatili ng buhay at crispness ng pechay nang 3 hanggang 5 araw mas matagal kaysa pinutol na tangkay.",
                    actionSteps = listOf(
                        "Basain nang bahagya ang kama 30 minuto bago mag-ani upang lumambot ang lupa.",
                        "Hawakan sa base ng puting tangkay at hilahin nang dahan-dahan upang buo ang ugat.",
                        "Isawsaw ang ugat sa malinis na tubig upang maalis ang putik nang hindi nababasa ang mga dahon.",
                        "Itali sa kumpol na may 250–500 gramo gamit ang manipis na tali o dahon ng saging."
                    ),
                    materialsNeeded = listOf("Malinis na Tubig para sa Ugat", "Tali para sa Kumpol"),
                    organicRemedy = "Banayad na paghuhugas ng ugat sa malamig na tubig.",
                    criticalRule = "Huwag basain ang buong dahon kapag itatago sa plastik upang hindi maging sanhi ng amag."
                )
            }
            if (selectedOptions.contains("pechay_harvest_overmature")) {
                return DirectCropSolution(
                    title = "Overmature Pechay Salvage & Replanting Plan",
                    tagalogTitle = "Pamamahala sa Namulaklak na Pechay (Bolted)",
                    severity = "WARNING",
                    badgeColor = AlertAmber,
                    directAnswer = "Kapag lumabas na ang dilaw na bulaklak sa gitna ng pechay, nagiging makunat, makahoy, at mapait ang puting tangkay dahil naubos ang sustansya sa pamumulaklak.",
                    actionSteps = listOf(
                        "Anihin na agad ang lahat ng natitirang halaman ngayon; maaari pang gamitin sa mga lutong sabaw tulad ng nilaga o sinigang.",
                        "Linisin ang kama at bungkalin nang marahan.",
                        "Maglagay ng bagong compost bago itanim ang susunod na batch.",
                        "I-stagger ang pagtatanim tuwing 7–10 araw upang hindi sabay-sabay na lumampas sa anihan."
                    ),
                    materialsNeeded = listOf("Pala", "Bagong Binhi", "Kompost"),
                    organicRemedy = "Gamitin sa lutong ulam o ihalo sa compost pile ang mga matitigas na tangkay.",
                    criticalRule = "Huwag patagalin sa bukid kapag namulaklak na; uubusin lamang nito ang pataba sa lupa."
                )
            }
        }
    }

    return null
}
