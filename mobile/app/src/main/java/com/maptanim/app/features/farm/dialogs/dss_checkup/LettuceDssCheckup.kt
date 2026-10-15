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
 * for Lettuce / Litsugas (Lactuca sativa) in Philippine highland (Benguet, Mountain Province, Bukidnon)
 * and lowland cool-season / urban raised-bed farm settings based on MapTanim Lettuce technical data & DA-BPI.
 */
fun getLettuceCheckUpActivities(): List<CheckUpActivity> {
    return listOf(
        CheckUpActivity(
            id = "watering",
            title = "Pagdidilig at Kahalumigmig ng Litsugas",
            tagalogSubtitle = "Suriin ang moisture sa mababaw na ugat at kalagayan ng malulutong na dahon",
            icon = Icons.Default.WaterDrop,
            options = listOf(
                CheckUpOption(
                    id = "lettuce_water_normal",
                    title = "Katamtaman at Regular na Halumigmig",
                    tagalogDesc = "Mamasa-masa ngunit hindi putik ang 5–10 cm na ibabaw ng lupa; banayad na dinidilig araw-araw o bawat 2 araw sa umaga",
                    isIdeal = true
                ),
                CheckUpOption(
                    id = "lettuce_water_stress",
                    title = "Tagtuyot / Pagkalanta sa Katanghalian",
                    tagalogDesc = "Naluluoy ang manipis na dahon sa init; nagiging mapait ang lasa at napipilitang mag-bolt o mamulaklak nang maaga"
                ),
                CheckUpOption(
                    id = "lettuce_water_overwater",
                    title = "Sobrang Tubig / Baha at Putik sa Kama",
                    tagalogDesc = "Naiipon ang tubig sa paligid ng rosette; naninilaw ang lumang dahon at nabubulok ang base ng puno (soft rot / damping-off)"
                ),
                CheckUpOption(
                    id = "lettuce_water_splash",
                    title = "Pagtalsik ng Putik sa Ilalim ng Dahon",
                    tagalogDesc = "Malakas na buhos ng pandilig o ulan na nagtatalsik ng lupa sa mga dahon, nagdudulot ng bottom rot at dumi"
                )
            )
        ),
        CheckUpActivity(
            id = "fertilizing",
            title = "Abono at Nutrisyon ng Litsugas",
            tagalogSubtitle = "Suriin ang balanse ng Nitroheno, Kalsyo laban sa Tip-burn, at Potash",
            icon = Icons.Default.Spa,
            options = listOf(
                CheckUpOption(
                    id = "lettuce_fert_balanced",
                    title = "Balanseng Nutrisyon (Compost + 14-14-14 Basal)",
                    tagalogDesc = "Mayabong, matingkad na berde, at malutong ang buong rosette; walang sunog sa gilid ng dahon",
                    isIdeal = true
                ),
                CheckUpOption(
                    id = "lettuce_fert_nitrogen_def",
                    title = "Kulang sa Nitroheno (Maputla at Mabagal ang Laki)",
                    tagalogDesc = "Naninilaw o maputlang berde ang mga lumang dahon; manipis at bansot ang pagbuo ng rosette",
                    isIdeal = false
                ),
                CheckUpOption(
                    id = "lettuce_fert_excess_nitrogen",
                    title = "Sobra sa Nitroheno (Masyadong Malambot at Matubig)",
                    tagalogDesc = "Matingkad ngunit labis na malambot ang dahon; mabilis lapitan ng aphids at madaling mabulok pagkatapos anihin",
                    isIdeal = false
                ),
                CheckUpOption(
                    id = "lettuce_fert_tipburn",
                    title = "Tip-burn / Kulang sa Kalsyo sa Batang Dahon",
                    tagalogDesc = "Sunog, tuyot, at kulay kayumanggi ang mga gilid ng pinakabatang dahon sa gitna ng rosette dulot ng mabilis na paglaki at init",
                    isIdeal = false
                ),
                CheckUpOption(
                    id = "lettuce_fert_potassium_def",
                    title = "Kulang sa Potash (Sunog ang Gilid ng Lumang Dahon)",
                    tagalogDesc = "Nanunuyot ang paligid ng panlabas na dahon (marginal leaf scorch) at mahina ang crispness",
                    isIdeal = false
                )
            )
        ),
        CheckUpActivity(
            id = "temperature_bolting",
            title = "Temperatura, Init ng Araw, at Bolting Control",
            tagalogSubtitle = "Suriin ang epekto ng init sa pamumulaklak, paghaba ng tangkay, at tamis ng dahon",
            icon = Icons.Default.Thermostat,
            options = listOf(
                CheckUpOption(
                    id = "lettuce_temp_ideal",
                    title = "Malamig at Katamtamang Klima (15–24°C)",
                    tagalogDesc = "Siksik at mababa ang rosette; matamis at walang pait ang dagta sa ilalim ng 4–6 oras na banayad na sikat ng araw",
                    isIdeal = true
                ),
                CheckUpOption(
                    id = "lettuce_temp_bolting",
                    title = "Maagang Paghaba ng Tangkay / Bolting (>30°C)",
                    tagalogDesc = "Biglang tumataas ang gitnang tangkay (stalk elongation), lumiliit ang dahon at nag-uumpisang mamukadkad ang dilaw na bulaklak",
                    isIdeal = false
                ),
                CheckUpOption(
                    id = "lettuce_temp_bitter",
                    title = "Mapait na Dagta (Lactucarium Toxin Buildup)",
                    tagalogDesc = "May gatas na mapait na lumalabas sa pinutol na dahon; hindi na masarap gawing sariwang salad dulot ng matinding init",
                    isIdeal = false
                ),
                CheckUpOption(
                    id = "lettuce_temp_sunscald",
                    title = "Sunog sa Dahon / Sunscald sa Katanghalian",
                    tagalogDesc = "Puting sunog o kupas na mga tagpi sa manipis na dahon dulot ng walang lilim na sikat ng araw (>32°C)",
                    isIdeal = false
                )
            )
        ),
        CheckUpActivity(
            id = "pests",
            title = "Mga Peste sa Dahon at Puno ng Litsugas",
            tagalogSubtitle = "Suriin ang aphids, harabas, leafminer, at suso sa ilalim ng dahon",
            icon = Icons.Default.BugReport,
            options = listOf(
                CheckUpOption(
                    id = "lettuce_pest_none",
                    title = "Walang Nakikitang Peste sa Taniman",
                    tagalogDesc = "Malinis ang ilalim ng mga dahon at walang butas o bakas ng uod at suso",
                    isIdeal = true
                ),
                CheckUpOption(
                    id = "lettuce_pest_aphids",
                    title = "Kuto sa Dahon / Green Peach Aphids (Myzus persicae)",
                    tagalogDesc = "Kumpol-kumpol na maliliit na berdeng insekto sa gitna ng rosette at ilalim ng dahon; may nanlalagkit na honeydew at itim na amag",
                    isIdeal = false
                ),
                CheckUpOption(
                    id = "lettuce_pest_armyworm",
                    title = "Harabas / Armyworm at Cutworm (Spodoptera spp.)",
                    tagalogDesc = "Malalaking butas sa dahon o pinutol na punla sa gabi; may itim na dumi ng uod sa gitna ng halaman",
                    isIdeal = false
                ),
                CheckUpOption(
                    id = "lettuce_pest_leafminer",
                    title = "Serpentine Leafminer (Liriomyza spp.)",
                    tagalogDesc = "Mapuputing paikot-ikot na lagusan o tunnel sa loob ng mga dahon na nagpapababa ng kalidad ng salad greens",
                    isIdeal = false
                ),
                CheckUpOption(
                    id = "lettuce_pest_slugs",
                    title = "Suso at Kuhol / Slugs and Snails",
                    tagalogDesc = "Punit-punit na gilid ng dahon sa madaling araw na may naiiwanang makintab at nanlalagkit na bakas ng laway sa lupa",
                    isIdeal = false
                )
            )
        ),
        CheckUpActivity(
            id = "diseases",
            title = "Mga Sakit at Amag ng Litsugas",
            tagalogSubtitle = "Suriin ang damping-off, bacterial soft rot, at downy mildew",
            icon = Icons.Default.Coronavirus,
            options = listOf(
                CheckUpOption(
                    id = "lettuce_dis_clean",
                    title = "Malusog at Walang Sakit ang mga Dahon",
                    tagalogDesc = "Walang amag, batik, o mabahong pagkabulok sa base ng rosette",
                    isIdeal = true
                ),
                CheckUpOption(
                    id = "lettuce_dis_damping_off",
                    title = "Damping-off sa Punlaan (Pythium / Rhizoctonia)",
                    tagalogDesc = "Nalulusaw, pumapayat, at nabubuwal ang tangkay ng mga batang punla sa ibabaw ng lupa",
                    isIdeal = false
                ),
                CheckUpOption(
                    id = "lettuce_dis_soft_rot",
                    title = "Bacterial Soft Rot (Pectobacterium / Erwinia)",
                    tagalogDesc = "Basang-basa, malambot na parang uhog, at mabahong pagkabulok ng base at puso ng litsugas lalo na pagkatapos ng ulan",
                    isIdeal = false
                ),
                CheckUpOption(
                    id = "lettuce_dis_downy_mildew",
                    title = "Downy Mildew (Bremia lactucae)",
                    tagalogDesc = "Madidilaw na kanto-kantong batik sa ibabaw ng lumang dahon na may pinong puting amag sa ilalim sa mahalumigmig na panahon",
                    isIdeal = false
                ),
                CheckUpOption(
                    id = "lettuce_dis_bottom_rot",
                    title = "Bottom Rot sa Lupang Kama (Rhizoctonia solani)",
                    tagalogDesc = "Nangingitim at nabubulok ang mga dahong direktang nakadikit sa basang lupa habang malusog pa ang itaas",
                    isIdeal = false
                )
            )
        ),
        CheckUpActivity(
            id = "bed_and_spacing",
            title = "Kama, Bunton, at Agwat ng Tanim",
            tagalogSubtitle = "Suriin ang 20–25 cm pagitan, raised beds, at pagtanggal ng damo",
            icon = Icons.Default.Agriculture,
            options = listOf(
                CheckUpOption(
                    id = "lettuce_space_optimal",
                    title = "Tamang Agwat sa Matataas na Kama (Raised Beds)",
                    tagalogDesc = "20–25 cm x 25–30 cm na pagitan sa 1-meter wide bed; may 15–20 cm na taas at malinis na daluyan ng tubig",
                    isIdeal = true
                ),
                CheckUpOption(
                    id = "lettuce_space_crowded",
                    title = "Masyadong Masikip / Hindi Napanipis (Thinning Needed)",
                    tagalogDesc = "Nagsisiksikan ang mga puno; mahaba at payat ang tangkay, mahina ang sirkulasyon ng hangin at mabilis kapitan ng amag",
                    isIdeal = false
                ),
                CheckUpOption(
                    id = "lettuce_space_flat_bed",
                    title = "Patag na Lupa / Walang Kanal sa Tag-ulan",
                    tagalogDesc = "Nabababad ang ugat sa naiipong tubig kapag umuulan; nagiging sanhi ng paninilaw at pagkabulok",
                    isIdeal = false
                ),
                CheckUpOption(
                    id = "lettuce_space_weeds",
                    title = "Napapaligiran ng Masusukal na Damo",
                    tagalogDesc = "Naaagawan ng liwanag, tubig, at abono ang mababaw na ugat ng litsugas; pinamumugaran ng kulisap",
                    isIdeal = false
                )
            )
        ),
        CheckUpActivity(
            id = "harvesting",
            title = "Pag-aani at Kalidad ng Salad Greens",
            tagalogSubtitle = "Suriin ang 55–70 araw na maturity, umagang anihan, at pag-iwas sa bolting",
            icon = Icons.Default.ContentCut,
            options = listOf(
                CheckUpOption(
                    id = "lettuce_harvest_ready",
                    title = "Siksik, Buo ang Rosette, at Malutong (Ready to Harvest)",
                    tagalogDesc = "Naabot na ang 55–70 araw; buo ang hugis, makatas, at malutong ang dahon bago pa magsimulang tumaas ang gitna",
                    isIdeal = true
                ),
                CheckUpOption(
                    id = "lettuce_harvest_morning",
                    title = "Pag-aani sa Maagang Umaga (6:00–8:00 AM)",
                    tagalogDesc = "Pinitas bago uminit ang araw habang puno pa ng turgor pressure at kahalumigmig ang mga dahon",
                    isIdeal = true
                ),
                CheckUpOption(
                    id = "lettuce_harvest_cut_outer",
                    title = "Pagpitas ng Panlabas na Dahon Lamang (Cut-and-Come-Again)",
                    tagalogDesc = "Inaani ang 3–4 na panlabas na dahon sa loose-leaf varieties habang hinahayaang magpatuloy lumaki ang gitnang puso",
                    isIdeal = true
                ),
                CheckUpOption(
                    id = "lettuce_harvest_overmature",
                    title = "Lampas na sa Panahon / Mapait na Lasang-Gatas",
                    tagalogDesc = "Nagsimula nang tumubo pataas ang tangkay; makunat, manipis, at mapait na ang mga dahon dulot ng bolting",
                    isIdeal = false
                )
            )
        )
    )
}

/**
 * Diagnostic solver providing actionable, organic, and agronomic direct solutions
 * for Lettuce / Litsugas growers based on MapTanim Lettuce technical data & DA-BPI.
 */
fun solveLettuceProblem(
    activityId: String,
    selectedOptions: Set<String>
): DirectCropSolution? {
    if (selectedOptions.isEmpty()) return null

    when (activityId) {
        "watering" -> {
            if (selectedOptions.contains("lettuce_water_stress")) {
                return DirectCropSolution(
                    title = "Drought Stress & Heat Wilting Mitigation",
                    tagalogTitle = "Lunas sa Tagtuyot at Pagkalanta ng Litsugas",
                    severity = "WARNING",
                    badgeColor = AlertAmber,
                    directAnswer = "Napakababaw ng ugat ng litsugas (5–15 cm) at 95% ng timbang nito ay tubig. Kapag natuyo ang lupa sa init, mabilis itong maglalabas ng mapait na dagta (lactucarium) at maagang mamumulaklak (bolting).",
                    actionSteps = listOf(
                        "Magdilig agad nang banayad sa base ng kama tuwing 6:00–7:30 ng umaga gamit ang fine rose sprinkler o drip line.",
                        "Maglagay ng 3–5 cm na tuyong dayami (rice straw) o sawdust sa ibabaw ng kama upang mapanatiling malamig at basa ang lupa.",
                        "Magtayo ng 30–50% black shade net sa ibabaw ng taniman mula 10:30 AM hanggang 2:30 PM upang mapigilan ang matinding sikat ng araw.",
                        "Huwag magdilig sa tirik na araw ng katanghalian upang hindi maluto ang pinong dahon."
                    ),
                    materialsNeeded = listOf("Fine Rose Sprinkler o Drip Tape", "Tuyong Dayami (Straw Mulch)", "30–50% Shade Netting"),
                    organicRemedy = "Paglalagay ng makapal na organic mulch at pag-spray ng fermented plant juice (FPJ) sa gabi upang mapabilis ang pagbawi ng selula.",
                    criticalRule = "Panatilihing mamasa-masa ang ibabaw ng lupa araw-araw; ang isang araw na matinding tagtuyot ay sapat na upang mapait ang buong ani ng litsugas."
                )
            }
            if (selectedOptions.contains("lettuce_water_overwater")) {
                return DirectCropSolution(
                    title = "Overwatering & Waterlogged Bed Drainage",
                    tagalogTitle = "Solusyon sa Sobrang Tubig at Pagkababad sa Putik",
                    severity = "DANGER",
                    badgeColor = DangerRed,
                    directAnswer = "Mabilis mabulok ang ugat at base ng litsugas kapag nakalubog sa putik. Ang kawalan ng hangin sa ugat ay nagdudulot ng paninilaw at nakamamatay na bacterial soft rot.",
                    actionSteps = listOf(
                        "Itigil muna ang pagdidilig ng 1 hanggang 2 araw hanggang sa bahagyang matuyo ang ibabaw ng kama.",
                        "Palalimin ang mga kanal sa pagitan ng mga kama sa 20–30 cm upang mabilis umagos ang tubig-baha.",
                        "Alisin at itapon sa malayo ang mga panlabas na dahong nangingitim, nanlalata, o nangangamoy soft rot.",
                        "Maglagay ng buhaghag na carbonized rice hull (CRH) o buhangin sa paligid ng puno upang maalis ang pagkakadikit ng dahon sa basang putik."
                    ),
                    materialsNeeded = listOf("Pala para sa Pagpapalalim ng Kanal", "Carbonized Rice Hull (CRH)"),
                    organicRemedy = "Pagbubudbod ng pinong abo ng kahoy (wood ash) at CRH sa paligid ng rosette upang higupin ang labis na moisture at pigilan ang bacteria.",
                    criticalRule = "Huwag kailanman magtanim ng litsugas sa patag na lupang walang drainage canals tuwing tag-ulan sa Pilipinas."
                )
            }
            if (selectedOptions.contains("lettuce_water_splash")) {
                return DirectCropSolution(
                    title = "Soil Splash & Bottom Rot Prevention",
                    tagalogTitle = "Pagpigil sa Pagtalsik ng Putik sa Ilalim ng Dahon",
                    severity = "WARNING",
                    badgeColor = AlertAmber,
                    directAnswer = "Ang putik na tumatalsik sa ilalim ng dahon ay nagdadala ng Rhizoctonia spores at bacteria na nagiging sanhi ng bottom rot at maruming ani.",
                    actionSteps = listOf(
                        "Maglagay ng malinis na dayami o silver-black plastic mulch sa ibabaw ng kama.",
                        "Gamitin ang pinong pandilig o mag-drip irrigation sa ilalim ng dahon.",
                        "Gupitin at alisin ang mga pinakailalim na dahon na nakalapat na sa lupa."
                    ),
                    materialsNeeded = listOf("Rice Straw Mulch", "Pruning Shears", "Drip System"),
                    organicRemedy = "Paglalatag ng malinis na tuyong ipa o dayami upang magsilbing hadlang sa pagtalsik ng lupa.",
                    criticalRule = "Diligan ang lupa sa tabi ng puno, huwag ibuhos mula sa itaas kung saan tatalsik ang lupa sa buong rosette."
                )
            }
            if (selectedOptions.contains("lettuce_water_normal")) {
                return DirectCropSolution(
                    title = "Optimal Lettuce Moisture Maintenance",
                    tagalogTitle = "Wastong Kahalumigmig at Patubig",
                    severity = "IDEAL",
                    badgeColor = LushGreen,
                    directAnswer = "Napakaganda ng kondisyon ng patubig! Ang pare-parehong moisture sa mababaw na ugat ay nagbibigay ng malutong, matamis, at makatas na litsugas.",
                    actionSteps = listOf(
                        "Ipagpatuloy ang regular na pagdidilig tuwing maagang umaga.",
                        "Suriin ang moisture gamit ang daliri sa 2 cm lalim bago magdilig sa mga araw na maulap."
                    ),
                    materialsNeeded = listOf("Pangdilig o Drip Tape"),
                    organicRemedy = "Panatilihin ang organikong mulch upang mabawasan ang pagsingaw ng tubig sa araw.",
                    criticalRule = "Iwasan ang biglaang pagpalit mula sa tuyo patungo sa basang-basa upang maiwasan ang tipburn."
                )
            }
        }

        "fertilizing" -> {
            if (selectedOptions.contains("lettuce_fert_tipburn")) {
                return DirectCropSolution(
                    title = "Calcium Deficiency & Tip-burn Correction",
                    tagalogTitle = "Lunas sa Tip-burn (Kulang sa Kalsyo sa Batang Dahon)",
                    severity = "WARNING",
                    badgeColor = AlertAmber,
                    directAnswer = "Ang Tip-burn ay sanhi ng kakulangan ng Calcium sa mabilis na lumalaking mga gilid ng batang dahon sa gitna ng rosette. Karaniwan itong lumalala sa mainit na panahon kapag mabilis ang transpiration ng lumang dahon at hindi nakakarating ang calcium sa gitna.",
                    actionSteps = listOf(
                        "I-spray ang dahon ng Calcium Nitrate (0.5% solution) o Fermented Eggshell and Vinegar (WCA) tuwing hapon linggu-linggo.",
                        "Panatilihing pantay at huwag patuyuin ang patubig upang tuloy-tuloy ang paghigop ng ugat sa natural na kalsyo sa lupa.",
                        "Maglagay ng shade net sa katanghalian upang pabagalin ang labis na transpiration sa panlabas na dahon.",
                        "Bawasan ang sobrang abono na may Potassium at Nitrogen na nakikipagkumpitensya sa pagsipsip ng Calcium."
                    ),
                    materialsNeeded = listOf("Water-Soluble Calcium (WCA)", "Calcium Nitrate Spray", "Shade Cloth"),
                    organicRemedy = "Maghanda ng Water-Soluble Calcium (WCA) mula sa inihaw na balat ng itlog na ibinabad sa sukang tuba ng 10–14 araw; maghalo ng 2 kutsara sa 16 litrong tubig.",
                    criticalRule = "Hindi maiwawasto ang tipburn kung pabago-bago ang patubig; ang pantay na moisture ang susi sa pag-akyat ng kalsyo."
                )
            }
            if (selectedOptions.contains("lettuce_fert_nitrogen_def")) {
                return DirectCropSolution(
                    title = "Nitrogen Deficiency Treatment",
                    tagalogTitle = "Lunas sa Kakulangan ng Nitroheno (Maputlang Dahon)",
                    severity = "WARNING",
                    badgeColor = AlertAmber,
                    directAnswer = "Ang litsugas ay nangangailangan ng 100–150 kg N/ha sa maikling panahon. Ang paninilaw ng lumang dahon ay senyales ng kakulangan ng nitrogen para sa mabilis na vegetative growth.",
                    actionSteps = listOf(
                        "Mag-drench ng Fish Amino Acid (FAA) o Vermitea sa base ng mga puno (10–15 ml bawat litro ng tubig) tuwing ika-5 araw.",
                        "Bilang synthetic alternative, mag-side-dress ng kaunting Urea (46-0-0) o Complete (14-14-14) sa 10–15 araw pagkalipat-tanim, 5 cm ang layo sa puno, at diligan agad.",
                        "Magdagdag ng matabang compost o vermicast sa ibabaw ng kama."
                    ),
                    materialsNeeded = listOf("Fish Amino Acid (FAA)", "Vermicast / Compost", "Complete 14-14-14 Fertilizer"),
                    organicRemedy = "Drenching ng purong Vermitea o diluted fermented plant juice (FPJ gawa sa kangkong o talbos ng kamote).",
                    criticalRule = "Huwag ilagay ang abono nang dikit sa pinong puno ng litsugas upang hindi masunog ang malambot na tangkay."
                )
            }
            if (selectedOptions.contains("lettuce_fert_excess_nitrogen")) {
                return DirectCropSolution(
                    title = "Excess Nitrogen Management",
                    tagalogTitle = "Pamamahala sa Sobrang Nitroheno (Masyadong Malambot)",
                    severity = "WARNING",
                    badgeColor = AlertAmber,
                    directAnswer = "Ang sobrang nitrogen ay nagdudulot ng napakalambot, matubig na dahon na may mataas na nitrate content. Madali itong lapitan ng aphids at mabilis masira pagkaani.",
                    actionSteps = listOf(
                        "Itigil agad ang paglalagay ng urea at mga nitrogen-rich fertilizers.",
                        "Mag-spray ng diluted wood ash water o Muriate of Potash (0-0-60) sa mababang konsentrasyon upang patibayin ang cell walls ng dahon.",
                        "I-flush ang kama ng sapat na tubig upang mahugasan ang labis na soluble salts sa root zone kung maayos ang drainage."
                    ),
                    materialsNeeded = listOf("Muriate of Potash o Wood Ash Extract", "Malinis na Tubig"),
                    organicRemedy = "Pagbubudbod ng pinong uling at abo upang ma-absorb ang labis na nitrate sa root zone.",
                    criticalRule = "Itigil ang lahat ng abono 10 araw bago mag-ani upang maging ligtas at masarap ang salad greens."
                )
            }
            if (selectedOptions.contains("lettuce_fert_potassium_def")) {
                return DirectCropSolution(
                    title = "Potassium Deficiency Correction",
                    tagalogTitle = "Lunas sa Kulang sa Potash (Marginal Leaf Scorch)",
                    severity = "WARNING",
                    badgeColor = AlertAmber,
                    directAnswer = "Ang potassium ang nagbibigay ng tigas, lutong, at resistensya sa sakit ng litsugas. Ang sunog na gilid ng dahon ay senyales ng kakulangan nito.",
                    actionSteps = listOf(
                        "Maglagay ng Fermented Fruit Juice (FFJ gawa sa saging o papaya) sa base ng halaman.",
                        "Maglagay ng 0-0-60 Muriate of Potash (1 kutsarita bawat 4 na puno) sa pagitan ng mga hanay at diligan.",
                        "Magbudbod ng pinulbos na abo ng kahoy sa paligid ng mga kama."
                    ),
                    materialsNeeded = listOf("Fermented Fruit Juice (FFJ)", "Wood Ash", "0-0-60 MOP"),
                    organicRemedy = "Diluted banana trunk extract o fermented fruit juice na mayaman sa bio-available potassium.",
                    criticalRule = "Siguraduhing may sapat na moisture kapag naglalagay ng potassium upang matunaw at masipsip agad."
                )
            }
            if (selectedOptions.contains("lettuce_fert_balanced")) {
                return DirectCropSolution(
                    title = "Optimal Nutrition Plan Maintained",
                    tagalogTitle = "Balanseng Nutrisyon ng Litsugas",
                    severity = "IDEAL",
                    badgeColor = LushGreen,
                    directAnswer = "Napakalusog ng iyong litsugas! Ang kombinasyon ng organikong compost at wastong pataba ay nagbibigay ng mabilis na paglaki at malulutong na dahon.",
                    actionSteps = listOf(
                        "Ipagpatuloy ang regular na pagdidilig at pagsubaybay sa kalagayan ng rosette.",
                        "Maghanda ng kaunting compost para sa susunod na crop cycle."
                    ),
                    materialsNeeded = listOf("Kompost"),
                    organicRemedy = "Pagpapanatili ng buhay na microorganism sa lupa gamit ang compost tea.",
                    criticalRule = "Panatilihin ang balanseng fertility sa bawat maikling 60-araw na siklo."
                )
            }
        }

        "temperature_bolting" -> {
            if (selectedOptions.contains("lettuce_temp_bolting") || selectedOptions.contains("lettuce_temp_bitter")) {
                return DirectCropSolution(
                    title = "Bolting Control & Heat Bitterness Prevention",
                    tagalogTitle = "Lunas sa Bolting (Pamumulaklak) at Mapait na Dagta",
                    severity = "WARNING",
                    badgeColor = AlertAmber,
                    directAnswer = "Kapag ang temperatura ay lumagpas sa 28–30°C o humaba ang matinding sikat ng araw, natural na lumilipat ang litsugas mula sa vegetative tungo sa reproductive phase (bolting). Ang paghaba ng tangkay ay nagdudulot ng produksyon ng lactucarium na nagpapapait sa lasa.",
                    actionSteps = listOf(
                        "Kung nagsisimula pa lamang humaba ang tangkay, anihin na agad ang lahat ng natitirang dahon ngayon bago tuluyang pumait.",
                        "Para sa mga batang tanim, magtayo agad ng 40–50% shade net tunnel sa taas na 1.5 metro sa ibabaw ng kama upang pababain ang temperatura ng 3–5°C.",
                        "Pumili ng mga heat-tolerant varieties para sa lowlands tulad ng 'General' (Romaine type), 'Green Span', o heat-resistant loose-leaf varieties sa halip na traditional iceberg crisphead.",
                        "Sa kapatagan ng Pilipinas, itanim lamang ang litsugas sa malalamig na buwan (Nobyembre hanggang Pebrero) o sa ilalim ng semi-shaded nursery beds."
                    ),
                    materialsNeeded = listOf("40–50% Shade Netting", "Kawayan para sa Frame ng Net", "Heat-Tolerant Seeds (General/Romaine)"),
                    organicRemedy = "Pagdidilig gamit ang malamig na tubig sa madaling araw at hapon upang palamigin ang temperatura ng root zone.",
                    criticalRule = "Kapag buo na ang bulaklak sa tuktok, hindi na mapapawi ang pait ng dahon; anihin ang mga dahon bago lumagpas sa 10 cm ang tangkay."
                )
            }
            if (selectedOptions.contains("lettuce_temp_sunscald")) {
                return DirectCropSolution(
                    title = "Sunscald & Leaf Scorch Recovery",
                    tagalogTitle = "Lunas sa Sunog sa Dahon dulot ng Matinding Araw",
                    severity = "WARNING",
                    badgeColor = AlertAmber,
                    directAnswer = "Masyadong manipis ang cuticle layer ng dahon ng litsugas kaya madali itong masunog sa direktang sikat ng araw sa tanghali kung walang lilim.",
                    actionSteps = listOf(
                        "Maglagay ng pansamantalang shade net o dahon ng niyog sa ibabaw ng taniman sa pagitan ng 10:00 AM at 3:00 PM.",
                        "Gupitin ang mga nasunog at tuyot na panlabas na dahon upang hindi pamugaran ng amag.",
                        "Siguraduhing hindi natutuyo ang lupa sa paligid ng ugat sa oras ng matinding init."
                    ),
                    materialsNeeded = listOf("Shade Netting o Dahon ng Niyog", "Pruning Shears"),
                    organicRemedy = "Pag-spray ng seaweed extract o aloe vera juice extract (10 ml bawat litro) upang tulungan ang dahon na mag-regenerate.",
                    criticalRule = "Huwag i-spray ang dahon ng tubig sa gitna ng matinding init ng tanghali; ang mga patak ng tubig ay parang magnifying glass na lalong magsusunog sa dahon."
                )
            }
            if (selectedOptions.contains("lettuce_temp_ideal")) {
                return DirectCropSolution(
                    title = "Ideal Growing Climate Confirmed",
                    tagalogTitle = "Mainam na Temperatura para sa Litsugas",
                    severity = "IDEAL",
                    badgeColor = LushGreen,
                    directAnswer = "Nasa pinakamainam na temperatura (15–24°C) ang iyong taniman! Ang ganitong klima sa Benguet, Bukidnon, o taglamig sa kapatagan ay nagbibigay ng pinakamatamis at malutong na kalidad ng litsugas.",
                    actionSteps = listOf(
                        "Panatilihin ang sapat na daloy ng sariwang hangin sa paligid ng kama.",
                        "Maghanda ng susunod na batch ng binhi para sa tuloy-tuloy na staggered harvesting."
                    ),
                    materialsNeeded = listOf("Binhi para sa Susunod na Batch"),
                    organicRemedy = "Pagpapanatili ng organic mulch upang mapanatili ang lamig ng lupa.",
                    criticalRule = "Sulitin ang malamig na panahon sa pamamagitan ng sunod-sunod na pagtatanim tuwing 1–2 linggo."
                )
            }
        }

        "pests" -> {
            if (selectedOptions.contains("lettuce_pest_aphids")) {
                return DirectCropSolution(
                    title = "Aphid Colonization Control on Salad Greens",
                    tagalogTitle = "Lunas sa Kuto sa Dahon / Aphids (Myzus persicae)",
                    severity = "WARNING",
                    badgeColor = AlertAmber,
                    directAnswer = "Ang mga aphids ay sumisipsip ng katas sa ilalim ng dahon at nag-iiwan ng malagkit na honeydew na nagiging itim na amag (sooty mold). Dahil sariwang kinakain ang litsugas bilang salad, mahigpit na ipinagbabawal ang paggamit ng synthetic chemical insecticides malapit sa anihan.",
                    actionSteps = listOf(
                        "Dislodge: Wisikan ng malakas ngunit pino na water jet spray ang ilalim ng mga dahon sa umaga upang malaglag ang mga kolonya ng kuto.",
                        "Organic Spray: Mag-spray ng Mild Insecticidal Soap solution (1 kutsarang liquid Castile soap o banayad na sabon sa 4 litrong tubig) nang direkta sa mga kuto tuwing hapon bawat 3 araw.",
                        "Neem Oil: Mag-spray ng 0.5% neem oil solution na may kaunting emulsifier tuwing alas-4:30 ng hapon.",
                        "Trap Cropping: Maglagay ng yellow sticky traps sa taas na 20 cm sa ibabaw ng kama upang mahuli ang mga lumilipad na winged aphids."
                    ),
                    materialsNeeded = listOf("Castile Soap o Insecticidal Soap", "Neem Oil Solution", "Yellow Sticky Traps", "Hand Sprayer"),
                    organicRemedy = "Chili-Garlic-Soap Spray: Pakuluan ang 5 pirasong dinurog na siling labuyo at 3 butil ng bawang sa 1 litrong tubig, salain, haluan ng 1 kutsaritang sabon, at i-spray sa mga insekto.",
                    criticalRule = "Huwag mag-spray ng neem o sabon sa loob ng 3 araw bago mag-ani upang hindi magbago ang lasa ng sariwang dahon."
                )
            }
            if (selectedOptions.contains("lettuce_pest_armyworm")) {
                return DirectCropSolution(
                    title = "Armyworm & Cutworm Eradication",
                    tagalogTitle = "Lunas sa Harabas at Cutworm sa Litsugas",
                    severity = "DANGER",
                    badgeColor = DangerRed,
                    directAnswer = "Ang mga uod ng harabas at cutworm ay nanginginain sa gabi at pumuputol sa leeg ng mga batang punla o gumagawa ng malalaking butas sa gitna ng rosette.",
                    actionSteps = listOf(
                        "Handpicking: Mag-inspeksyon sa gabi gamit ang flashlight (8:00–10:00 PM) at manu-manong pulutin ang mga uod na nanginginain sa dahon.",
                        "Biological Control: Mag-spray ng Bacillus thuringiensis (Bt) biopesticide sa hapon; lubos itong ligtas sa tao ngunit pumapatay sa mga uod sa loob ng 48 oras.",
                        "Protective Collars: Sa mga bagong lipat na punla, maglagay ng 5 cm cardboard collar o cut plastic cup sa paligid ng tangkay upang hindi maputol ng cutworm.",
                        "Field Sanitation: Alisin ang mga damo sa paligid ng kama kung saan nangingitlog ang mga gamugamo."
                    ),
                    materialsNeeded = listOf("Bacillus thuringiensis (Bt)", "Flashlight para sa Night Patrol", "Cardboard Cutworm Collars"),
                    organicRemedy = "Bacillus thuringiensis (Bt) biopesticide na may kasamang neem leaf extract.",
                    criticalRule = "Pitasin ang uod habang maliit pa; ang isang malaking harabas ay kayang umubos ng isang buong puno ng litsugas sa isang gabi."
                )
            }
            if (selectedOptions.contains("lettuce_pest_slugs")) {
                return DirectCropSolution(
                    title = "Slug & Snail Control for Leafy Beds",
                    tagalogTitle = "Lunas sa Suso at Kuhol sa Kama ng Litsugas",
                    severity = "WARNING",
                    badgeColor = AlertAmber,
                    directAnswer = "Paboritong kainin ng mga suso at kuhol ang malalambot na dahon ng litsugas sa madaling araw at maulang gabi, nag-iiwan ng malalaking butas at maruming laway.",
                    actionSteps = listOf(
                        "Beer Trap: Magbaon ng mababaw na lalagyan na may kaunting serbesa o fermented yeast water sa pantay ng lupa; aakitin at malulunod ang mga suso.",
                        "Abrasive Barrier: Magbudbod ng dinurog na balat ng itlog (crushed eggshells), magaspang na buhangin, o abo ng kahoy sa paligid ng mga kama; hindi makakatawid ang malalambot na katawan ng suso.",
                        "Night Handpicking: Pulutin ang mga suso sa madaling araw (5:30 AM) bago sila magtago sa ilalim ng dayami."
                    ),
                    materialsNeeded = listOf("Dinurog na Balat ng Itlog", "Mababaw na Lalagyan para sa Beer Trap", "Abo ng Kahoy"),
                    organicRemedy = "Abo ng kahoy at dinurog na eggshells bilang natural physical barrier sa gilid ng plots.",
                    criticalRule = "Huwag hayaang magpatong-patong ang basang dayami nang dikit sa puno kung saan pwedeng magtago ang mga suso."
                )
            }
            if (selectedOptions.contains("lettuce_pest_leafminer")) {
                return DirectCropSolution(
                    title = "Serpentine Leafminer Control",
                    tagalogTitle = "Lunas sa Leafminer sa Dahon ng Litsugas",
                    severity = "WARNING",
                    badgeColor = AlertAmber,
                    directAnswer = "Ang larvae ng leafminer ay gumagawa ng mapuputing paikot-ikot na lagusan sa loob ng dahon, sumisira sa hitsura at bentahe ng salad greens.",
                    actionSteps = listOf(
                        "Pitasin at sirain agad ang mga dahong may mga unang puting linya upang mapatay ang uod sa loob.",
                        "Mag-spray ng neem extract solution na sumisipsip sa cuticle ng dahon.",
                        "Maglagay ng yellow sticky traps upang mahuli ang mga matatandang langaw ng leafminer bago mangitlog."
                    ),
                    materialsNeeded = listOf("Yellow Sticky Traps", "Neem Extract Solution"),
                    organicRemedy = "Pagpitas ng apektadong dahon at regular na paglalagay ng yellow card traps.",
                    criticalRule = "Itapon at sunugin o ibaon ang napitas na dahon; huwag iwan sa ibabaw ng kama kung saan pwedeng lumabas ang langaw."
                )
            }
            if (selectedOptions.contains("lettuce_pest_none")) {
                return DirectCropSolution(
                    title = "Pest-Free Healthy Lettuce Stand",
                    tagalogTitle = "Walang Peste sa Taniman",
                    severity = "IDEAL",
                    badgeColor = LushGreen,
                    directAnswer = "Malinis at ligtas sa peste ang iyong litsugas! Ipagpatuloy ang regular na pag-iinspeksyon sa ilalim ng dahon.",
                    actionSteps = listOf(
                        "Mag-inspeksyon linggu-linggo sa ilalim ng mga dahon.",
                        "Pangalagaan ang mga kapaki-pakinabang na ladybugs at spiders sa taniman."
                    ),
                    materialsNeeded = listOf("Magnifying Glass para sa Scouting"),
                    organicRemedy = "Pagpapanatili ng sari-saring bulaklak (marigold, basil) sa paligid para sa beneficial insects.",
                    criticalRule = "Ang maagang pagtuklas ang pinakamabisang pananggalang sa salad greens."
                )
            }
        }

        "diseases" -> {
            if (selectedOptions.contains("lettuce_dis_soft_rot")) {
                return DirectCropSolution(
                    title = "Bacterial Soft Rot Eradication",
                    tagalogTitle = "Lunas sa Bacterial Soft Rot (Mabahong Pagkabulok)",
                    severity = "DANGER",
                    badgeColor = DangerRed,
                    directAnswer = "Ang Bacterial Soft Rot (Pectobacterium / Erwinia carotovora) ay pumapasok sa mga sugat ng dahon o base ng puno sa basang panahon. Naglalabas ito ng enzymes na tumutunaw sa mga selula ng halaman, nagiging sanhi ng malambot, parang uhog, at mabahong pagkabulok ng buong rosette.",
                    actionSteps = listOf(
                        "Bunutin at ilibing sa malayo (30 cm lalim) ang lahat ng apektadong halaman; HUWAG ilagay sa compost pile.",
                        "Bawasan agad ang pagdidilig at patuyuin ang ibabaw ng kama.",
                        "Palakihin ang agwat ng pagtatanim sa susunod na batch upang mapabuti ang sirkulasyon ng hangin.",
                        "Magbudbod ng apog (agricultural lime) o abo sa butas na pinagbunutan upang ma-disinfect ang lupa.",
                        "Iwasang masugatan ang mga dahon habang nagtatanggal ng damo."
                    ),
                    materialsNeeded = listOf("Pala para sa Paglilibing", "Agricultural Lime (Apog)", "Wood Ash"),
                    organicRemedy = "Pagbubudbod ng pinong apog at abo sa apektadong lugar upang mapigilan ang pagkalat ng bacterial slime.",
                    criticalRule = "Walang kemikal na gamot na makakapagpagaling sa halamang bulok na; ang agarang pagbubunot at field sanitation ang tanging paraan upang mailigtas ang natitirang tanim."
                )
            }
            if (selectedOptions.contains("lettuce_dis_damping_off")) {
                return DirectCropSolution(
                    title = "Seedling Damping-off Intervention",
                    tagalogTitle = "Lunas sa Damping-off sa Punlaan ng Litsugas",
                    severity = "DANGER",
                    badgeColor = DangerRed,
                    directAnswer = "Ang Damping-off (Pythium / Rhizoctonia) ay pumapatay sa mga batang punla sa seedling tray kapag sobrang basa ang seedling mix at kulang sa hangin at araw.",
                    actionSteps = listOf(
                        "Ilipat agad ang seedling trays sa tuyo at mahanging lugar na may banayad na sikat ng araw sa umaga.",
                        "Bawasan ang pagdidilig; diligan lamang kapag tuyo na ang ibabaw ng seed mix.",
                        "Alisin at itapon ang lahat ng nakatumbang punla kasama ang seedling cell mix nito.",
                        "Sa susunod na pagpupunla, gumamit lamang ng sterile potting mix (pinong CRH + vermicast + pinakuluang garden soil)."
                    ),
                    materialsNeeded = listOf("Sterile Seedling Mix", "Carbonized Rice Hull (CRH)", "Malinis na Seedling Trays"),
                    organicRemedy = "Drenching ng diluted Trichoderma harzianum bio-fungicide solution sa potting mix bago maghasik ng binhi.",
                    criticalRule = "Huwag kailanman maghasik nang masyadong masinsin; ang siksikang punla sa basang tray ay 100% dadapuan ng damping-off."
                )
            }
            if (selectedOptions.contains("lettuce_dis_downy_mildew")) {
                return DirectCropSolution(
                    title = "Downy Mildew (Bremia lactucae) Treatment",
                    tagalogTitle = "Lunas sa Downy Mildew sa Litsugas",
                    severity = "WARNING",
                    badgeColor = AlertAmber,
                    directAnswer = "Ang Downy Mildew ay nagdudulot ng madidilaw na kanto-kantong tagpi sa ibabaw ng dahon na pinipigilan ng mga ugat ng dahon (angular lesions), na may puting amag sa ilalim sa malalamig at mahalumigmig na umaga.",
                    actionSteps = listOf(
                        "Gupitin at sunugin ang mga apektadong lumang dahon sa ilalim ng halaman.",
                        "Mag-spray ng Baking Soda solution (1 kutsarita ng baking soda + 1/2 kutsaritang sabon sa 2 litrong tubig) sa ilalim ng dahon.",
                        "Mag-drip irrigation sa lupa upang manatiling tuyo ang mga dahon sa buong maghapon.",
                        "Magtanim ng mga resistant varieties sa mga lugar na madalas magka-downy mildew."
                    ),
                    materialsNeeded = listOf("Baking Soda (Sodium Bicarbonate)", "Mild Soap", "Pruning Shears"),
                    organicRemedy = "Baking soda spray solution tuwing umaga upang baguhin ang pH sa ibabaw ng dahon at mapigilan ang germination ng spores.",
                    criticalRule = "Iwasan ang pagdidilig sa hapon o gabi; ang dahong nananatiling basa sa buong magdamag ang paboritong pamugaran ng amag."
                )
            }
            if (selectedOptions.contains("lettuce_dis_bottom_rot")) {
                return DirectCropSolution(
                    title = "Bottom Rot Soil Management",
                    tagalogTitle = "Lunas sa Bottom Rot sa Ilalim ng Rosette",
                    severity = "WARNING",
                    badgeColor = AlertAmber,
                    directAnswer = "Ang Bottom Rot (Rhizoctonia solani) ay nabubuo kapag ang mga panlabas na dahon ay matagal na nakalapat sa mamasa-masa at kontaminadong lupa.",
                    actionSteps = listOf(
                        "Maglatag ng malinis na dayami o tuyong ipa sa ilalim ng mga dahon upang maihiwalay sa lupa.",
                        "Gupitin ang mga nabubulok na panlabas na dahon.",
                        "Tiyaking mataas ang kamada (15–20 cm) upang mabilis matuyo ang lupa sa paligid."
                    ),
                    materialsNeeded = listOf("Rice Straw Mulch", "Pruning Shears"),
                    organicRemedy = "Mulching gamit ang tuyong dayami upang maiwasan ang direktang kontak ng dahon sa lupa.",
                    criticalRule = "Gupitin ang mga dahong lapat sa lupa bago pa mag-umpisa ang pagkabulok."
                )
            }
            if (selectedOptions.contains("lettuce_dis_clean")) {
                return DirectCropSolution(
                    title = "Disease-Free Crisp Greens",
                    tagalogTitle = "Malusog at Walang Sakit ang Tanim",
                    severity = "IDEAL",
                    badgeColor = LushGreen,
                    directAnswer = "Napakalinis at walang anumang amag o impeksyon ang iyong litsugas! Ipagpatuloy ang magandang drainage at sirkulasyon ng hangin.",
                    actionSteps = listOf(
                        "Panatilihing tuyo ang mga dahon bago sumapit ang takipsilim.",
                        "Magpatuloy sa regular na sanitasyon ng kama."
                    ),
                    materialsNeeded = listOf("Malinis na Pandilig"),
                    organicRemedy = "Compost tea application para sa malusog na dahon.",
                    criticalRule = "Panatilihing malinis ang paligid ng taniman laban sa nabulok na mga tirang dahon."
                )
            }
        }

        "bed_and_spacing" -> {
            if (selectedOptions.contains("lettuce_space_crowded")) {
                return DirectCropSolution(
                    title = "Thinning & Plant Spacing Correction",
                    tagalogTitle = "Lunas sa Masyadong Masikip na Tanim (Thinning)",
                    severity = "WARNING",
                    badgeColor = AlertAmber,
                    directAnswer = "Kapag masikip ang tanim, nag-aagawan ang mga punla sa liwanag at hangin. Nagiging payat at mahaba ang tangkay, maliit ang rosette, at madaling kapitan ng soft rot.",
                    actionSteps = listOf(
                        "Magsagawa agad ng thinning (paghimay) sa direct-seeded beds: panatilihin ang 20–25 cm na distansya bawat puno.",
                        "Ang mga nahimay na malulusog na punla (microgreens / baby greens) ay maaaring kainin agad sa salad o ilipat-tanim sa ibang bakanteng kama sa hapon.",
                        "Sa transplanting, laging sundin ang 20–25 cm sa pagitan ng puno at 25–30 cm sa pagitan ng tudling."
                    ),
                    materialsNeeded = listOf("Trowel para sa Paglilipat-Tanim", "Bagong Kama para sa Sobrang Punla"),
                    organicRemedy = "Kainin ang mga nahimay na punla bilang masustansyang baby salad greens.",
                    criticalRule = "Gawin ang thinning 10–14 araw pagkasibol; kapag pinatagal, magkakasalisihan ang mga ugat at mababansot ang parehong halaman."
                )
            }
            if (selectedOptions.contains("lettuce_space_flat_bed")) {
                return DirectCropSolution(
                    title = "Raised Bed Construction & Furrow Drainage",
                    tagalogTitle = "Paggawa ng Matataas na Kama (Raised Beds)",
                    severity = "WARNING",
                    badgeColor = AlertAmber,
                    directAnswer = "Sensitibo ang litsugas sa pagkababad sa tubig. Ang pagtatanim sa patag na lupa ay nagiging sanhi ng root asphyxiation at bottom rot sa tag-ulan.",
                    actionSteps = listOf(
                        "Gumawa ng mga kamada (raised beds) na may taas na 15–20 cm at lapad na 1 metro.",
                        "Maghukay ng 30 cm na tudling (drainage furrow) sa pagitan ng bawat kama.",
                        "Ihalo ang 2–3 sako ng compost at CRH sa bawat kama upang maging buhaghag at buhos ang tubig."
                    ),
                    materialsNeeded = listOf("Pala at Asarol", "Kompost at CRH"),
                    organicRemedy = "Pagbubuhaghag ng lupa gamit ang organikong compost at carbonized rice hull.",
                    criticalRule = "Ang 15–20 cm raised bed ay nagbibigay ng 100% proteksyon sa ugat laban sa panandaliang pagbaha sa malalakas na ulan."
                )
            }
            if (selectedOptions.contains("lettuce_space_weeds")) {
                return DirectCropSolution(
                    title = "Shallow Root Weeding & Mulching",
                    tagalogTitle = "Wastong Paggagamas ng Damo sa Mababaw na Ugat",
                    severity = "WARNING",
                    badgeColor = AlertAmber,
                    directAnswer = "Mababaw ang ugat ng litsugas kaya madali itong masira kapag gumamit ng mabibigat na asarol sa paggagamas ng damo malapit sa puno.",
                    actionSteps = listOf(
                        "Gamitin ang kamay sa banayad na pagbunot ng damo malapit sa base ng litsugas.",
                        "Maglagay agad ng 3–5 cm na organic mulch (dayami o tuyong damo) upang pigilan ang pagsibol ng bagong damo.",
                        "Gamasin ang damo habang maliliit pa bago lumalim ang ugat."
                    ),
                    materialsNeeded = listOf("Tuyong Dayami (Mulch)", "Hand Weeder"),
                    organicRemedy = "Paglalagay ng makapal na dayami bilang natural weed suppressor.",
                    criticalRule = "Huwag magsaka o magbungkal nang malalim malapit sa puno ng litsugas upang hindi maputol ang mga sumisipsip na ugat."
                )
            }
            if (selectedOptions.contains("lettuce_space_optimal")) {
                return DirectCropSolution(
                    title = "Optimal Bed Dimensions & Spacing",
                    tagalogTitle = "Tamang Agwat at Sukat ng Kama",
                    severity = "IDEAL",
                    badgeColor = LushGreen,
                    directAnswer = "Napakagaling! Ang 20–25 cm x 25–30 cm na agwat sa 15–20 cm raised bed ay ang pamantayang ginagamit sa mga propesyonal na taniman sa Benguet at DA-BPI para sa pinakamataas na ani.",
                    actionSteps = listOf(
                        "Panatilihing malinis ang mga kanal sa pagitan ng kama.",
                        "Ipagpatuloy ang regular na pagsubaybay sa moisture."
                    ),
                    materialsNeeded = listOf("Panukat o Spacing Guide"),
                    organicRemedy = "Mulch maintenance para sa tuloy-tuloy na proteksyon sa lupa.",
                    criticalRule = "Panatilihin ang standard spacing para sa pantay-pantay na laki ng bawat ulo ng litsugas."
                )
            }
        }

        "harvesting" -> {
            if (selectedOptions.contains("lettuce_harvest_ready") || selectedOptions.contains("lettuce_harvest_morning")) {
                return DirectCropSolution(
                    title = "Peak Quality Morning Harvest Protocol",
                    tagalogTitle = "Wastong Pamamaraan ng Pag-aani sa Umaga",
                    severity = "IDEAL",
                    badgeColor = LushGreen,
                    directAnswer = "Handa na para sa pinakamalutong at pinakamasarap na ani! Ang pag-aani sa maagang umaga (6:00–8:00 AM) bago uminit ang araw ay nagpapanatili ng buong turgor pressure sa mga dahon.",
                    actionSteps = listOf(
                        "Gumamit ng matalas at malinis na kutsilyo o shears; gupitin ang puno sa mismong base ng lupa sa ilalim ng pinakailalim na dahon.",
                        "Alisin ang anumang marumi o dilaw na panlabas na dahon sa mismong bukid.",
                        "Iwasang ibilad sa sikat ng araw ang mga inani; ilagay agad sa malilim at mahanging lugar o palamigin sa 0–4°C kung may chiller.",
                        "I-pack nang maluwag sa mga ventilated plastic crate upang hindi mapisa ang malulutong na dahon."
                    ),
                    materialsNeeded = listOf("Matalas na Harvest Knife", "Ventilated Harvest Crates", "Malinis na Tubig para sa Trimming"),
                    organicRemedy = "Banayad na paglubog ng tangkay sa malamig na malinis na tubig upang manatiling sariwa nang 3–5 araw.",
                    criticalRule = "Huwag mag-ani sa katanghalian; ang mainit na dahon ay mabilis malanta at magiging kupos sa loob lamang ng 2 oras."
                )
            }
            if (selectedOptions.contains("lettuce_harvest_cut_outer")) {
                return DirectCropSolution(
                    title = "Cut-and-Come-Again Continuous Harvesting",
                    tagalogTitle = "Tuloy-tuloy na Pagpitas ng Panlabas na Dahon",
                    severity = "IDEAL",
                    badgeColor = LushGreen,
                    directAnswer = "Napakagandang pamamaraan para sa mga backyard at urban gardeners! Ang 'Cut-and-Come-Again' ay nagbibigay-daan sa 3 hanggang 5 beses na pamimitas sa parehong puno sa loob ng 3–4 na linggo.",
                    actionSteps = listOf(
                        "Pitasin lamang ang 3–4 na pinakamalaking panlabas na dahon sa pamamagitan ng paggupit sa base ng tangkay.",
                        "Iwanan ang gitnang 4–6 na dahon at ang lumalaking puso upang magpatuloy ang fotosintesis.",
                        "Diligan at maglagay ng kaunting vermitea pagkatapos mamitas upang mapabilis ang paglaki ng susunod na dahon."
                    ),
                    materialsNeeded = listOf("Pruning Shears", "Vermitea"),
                    organicRemedy = "Pagpitas tuwing 4–5 araw habang patuloy na naglalabas ng bagong talbos ang gitna.",
                    criticalRule = "Huwag putulin ang gitnang usbong (growing point) upang hindi maputol ang siklo ng bagong dahon."
                )
            }
            if (selectedOptions.contains("lettuce_harvest_overmature")) {
                return DirectCropSolution(
                    title = "Overmature Crop Salvage & Replanting Plan",
                    tagalogTitle = "Pamamahala sa Lampas na Litsugas (Bolted / Bitter)",
                    severity = "WARNING",
                    badgeColor = AlertAmber,
                    directAnswer = "Ang litsugas na nagsimula nang mag-bolt at may mataas na tangkay ay hindi na angkop para sa sariwang salad dahil sa makunat na balat at mapait na dagta.",
                    actionSteps = listOf(
                        "Anihin na agad ang lahat ng natitira; maaaring gamitin sa mga lutong sabaw o igisa kung hindi na masarap sa sariwang salad.",
                        "Linisin ang kama at bungkalin nang marahan.",
                        "Maglagay ng bagong compost bago itanim ang susunod na batch ng punla.",
                        "I-stagger ang pagtatanim tuwing 10–14 araw upang hindi sabay-sabay na lumampas sa anihan."
                    ),
                    materialsNeeded = listOf("Pala", "Bagong Binhi", "Kompost"),
                    organicRemedy = "Pagkain sa lutong ginisang gulay o sabaw kung saan nawawala ang kapaitan ng dahon.",
                    criticalRule = "Huwag nang patagalin sa bukid kapag tumaas na ang tangkay; kukunin lamang nito ang sustansya ng lupa."
                )
            }
        }
    }

    return null
}
