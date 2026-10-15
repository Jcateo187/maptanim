package com.maptanim.app.features.farm.dialogs.dss_checkup

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Coronavirus
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.ui.graphics.Color

private val ForestGreen = Color(0xFF1B5E20)
private val LushGreen = Color(0xFF2E7D32)
private val AlertAmber = Color(0xFFE65100)
private val DangerRed = Color(0xFFC62828)

/**
 * Diagnostic and Decision-Support System (DSS) activities tailored specifically
 * for Cabbage / Repolyo (Brassica oleracea var. capitata) in Philippine highland (Benguet, Bukidnon)
 * and lowland cool-season settings based on DA-BPI & MapTanim Cabbage technical data.
 */
fun getCabbageCheckUpActivities(): List<CheckUpActivity> {
    return listOf(
        CheckUpActivity(
            id = "watering",
            title = "Pagdidilig at Kahalumigmig",
            tagalogSubtitle = "Suriin ang moisture ng kamada at kalagayan ng namumuong ulo",
            icon = Icons.Default.WaterDrop,
            options = listOf(
                CheckUpOption(
                    id = "cabbage_water_normal",
                    title = "Katamtamang Basa at Buhaghag",
                    tagalogDesc = "Mamasa-masa ang lupa sa 5–10 cm lalim; dinidilig nang 2–3 beses kada linggo",
                    isIdeal = true
                ),
                CheckUpOption(
                    id = "cabbage_water_stress",
                    title = "Nanunuyo / Lanta sa Katanghalian",
                    tagalogDesc = "Tigang ang lupa; lumalambot at hindi sumisiksik ang namumuong ulo ng repolyo"
                ),
                CheckUpOption(
                    id = "cabbage_waterlogged",
                    title = "Baha / Masyadong Basang Kamada",
                    tagalogDesc = "Naiipon ang tubig sa kanal; maputla ang dahon at nanganganib sa Bacterial Soft Rot"
                ),
                CheckUpOption(
                    id = "cabbage_head_splitting",
                    title = "Pumutok o Bumukang Ulo (Head Splitting)",
                    tagalogDesc = "Bumuka o pumutok ang siksik na ulo matapos ang biglaang ulan kasunod ng tagtuyot"
                ),
                CheckUpOption(
                    id = "cabbage_tipburn_moisture",
                    title = "Nangingitim ang Dulo ng Panloob na Dahon",
                    tagalogDesc = "Tip-burn sa loob ng ulo dulot ng hindi pantay na moisture at mabagal na daloy ng calcium"
                )
            )
        ),
        CheckUpActivity(
            id = "fertilizing",
            title = "Abono at Nutrisyon ng Repolyo",
            tagalogSubtitle = "Suriin ang kulay ng dahon at katigasan ng namumuong ulo",
            icon = Icons.Default.Spa,
            options = listOf(
                CheckUpOption(
                    id = "cabbage_fert_complete",
                    title = "Kumpleto at Malusog ang Nutrisyon",
                    tagalogDesc = "Nalagyan ng basal 14-14-14 (400–600 kg/ha) at may regular na side-dress ng Urea at Potash",
                    isIdeal = true
                ),
                CheckUpOption(
                    id = "cabbage_fert_nitrogen_def",
                    title = "Maputlang Dilaw ang Lumang Dahon (Kulang sa N)",
                    tagalogDesc = "Bansot ang buong rosette; maliliit at mabagal sumiksik ang ulo ng repolyo"
                ),
                CheckUpOption(
                    id = "cabbage_fert_phosphorus_def",
                    title = "Kulay Ube / Purplish ang Ilalim ng Dahon (Kulang sa P)",
                    tagalogDesc = "Maitim o mamula-mulang ube ang dahon sa malamig na lupa; naantala ang pag-uulo"
                ),
                CheckUpOption(
                    id = "cabbage_fert_potash_def",
                    title = "Tustado ang Gilid ng Dahon (Kulang sa Potash - K)",
                    tagalogDesc = "Sunog ang gilid ng mga dahon; malambot at maluwag ang nabuong ulo ng repolyo"
                ),
                CheckUpOption(
                    id = "cabbage_fert_calcium_tipburn",
                    title = "Tip-Burn sa Loob ng Ulo (Kulang sa Calcium - Ca)",
                    tagalogDesc = "Sunog at kulay kape ang dulo ng mga panloob na dahon; kulang sa Calcium / CalPhos"
                ),
                CheckUpOption(
                    id = "cabbage_fert_boron_hollow",
                    title = "Hollow Stem / May Butas ang Tangkay (Kulang sa Boron)",
                    tagalogDesc = "May guwang o nangingitim na espasyo sa loob ng pinakapuno dulot ng boron deficiency"
                )
            )
        ),
        CheckUpActivity(
            id = "pests",
            title = "Peste at mga Uod sa Repolyo",
            tagalogSubtitle = "Suriin ang ilalim ng dahon at gitnang usbong para sa mga peste",
            icon = Icons.Default.BugReport,
            options = listOf(
                CheckUpOption(
                    id = "cabbage_pest_clean",
                    title = "Malinis at Walang Uod",
                    tagalogDesc = "Buo at makinis ang mga dahon; walang butas o dumi ng uod sa gitnang rosette",
                    isIdeal = true
                ),
                CheckUpOption(
                    id = "cabbage_pest_dbm",
                    title = "Diamondback Moth (Plutella xylostella) #1 Peste",
                    tagalogDesc = "Maliliit na berdeng uod na pumapalag; lumilikha ng window-paning at butas-butas na dahon"
                ),
                CheckUpOption(
                    id = "cabbage_pest_looper",
                    title = "Cabbage Worm / Looper (Pieris / Trichoplusia)",
                    tagalogDesc = "Malalaking berdeng uod na parang sumusukat; malalaki at gutay-gutay ang ngatngat sa dahon"
                ),
                CheckUpOption(
                    id = "cabbage_pest_aphids",
                    title = "Kulupon ng Cabbage Aphids (Kuto ng Halaman)",
                    tagalogDesc = "Kulay abo o berdeng aphids sa ilalim ng dahon; nagiging sanhi ng kulubot at dumi"
                ),
                CheckUpOption(
                    id = "cabbage_pest_cutworm",
                    title = "Cutworm / Harabas sa Bagong Lipat na Punla",
                    tagalogDesc = "Pinuputol ang tangkay ng mga batang punla sa antas ng lupa tuwing gabi"
                )
            )
        ),
        CheckUpActivity(
            id = "diseases",
            title = "Mga Sakit ng Repolyo (Brassica Diseases)",
            tagalogSubtitle = "Suriin ang ugat, tangkay, at dahon laban sa clubroot at bacterial soft rot",
            icon = Icons.Default.Coronavirus,
            options = listOf(
                CheckUpOption(
                    id = "cabbage_dis_none",
                    title = "Walang Sakit / Malusog na Rosette",
                    tagalogDesc = "Makinis at sariwa ang mga dahon; buo at matigas ang namumuong ulo",
                    isIdeal = true
                ),
                CheckUpOption(
                    id = "cabbage_dis_clubroot",
                    title = "Clubroot / Bukol sa Ugat (Plasmodiophora brassicae)",
                    tagalogDesc = "Lanta ang repolyo sa katanghalian; may malalaking kumpol ng bukol sa ugat sa asidikong lupa"
                ),
                CheckUpOption(
                    id = "cabbage_dis_softrot",
                    title = "Bacterial Soft Rot (Pectobacterium / Erwinia)",
                    tagalogDesc = "Basang-basa, mabaho, at naging parang lugaw ang ulo ng repolyo pagkatapos ng ulan"
                ),
                CheckUpOption(
                    id = "cabbage_dis_blackrot",
                    title = "Black Rot / V-Shaped Yellowing (Xanthomonas)",
                    tagalogDesc = "Hugis V na paninilaw mula sa gilid ng dahon papasok; maiitim ang ugat sa loob ng tangkay"
                ),
                CheckUpOption(
                    id = "cabbage_dis_alternaria",
                    title = "Alternaria Leaf Spot (Pabilog na Maiitim na Batik)",
                    tagalogDesc = "May concentric rings na batik sa matatandang dahon; lumalala sa mahalumigmig na panahon"
                )
            )
        ),
        CheckUpActivity(
            id = "field_care",
            title = "Pangangalaga sa Kamada at Paligid",
            tagalogSubtitle = "Suriin ang damo, hilling-up, drainage, at crop rotation",
            icon = Icons.Default.Agriculture,
            options = listOf(
                CheckUpOption(
                    id = "cabbage_care_ideal",
                    title = "Maayos na Kamada at Daluyan ng Tubig",
                    tagalogDesc = "20–25 cm mataas na raised bed; may malinis na kanal; walang damo bago sumara ang canopy",
                    isIdeal = true
                ),
                CheckUpOption(
                    id = "cabbage_care_weeds",
                    title = "Makapal ang Damo sa Kamada",
                    tagalogDesc = "Nakikipag-agawan ang damo sa pataba at tubig; kailangang linisin bago sumara ang repolyo"
                ),
                CheckUpOption(
                    id = "cabbage_care_poor_drainage",
                    title = "Mababaw ang Kanal / Baradong Drainage",
                    tagalogDesc = "Mabagal humupa ang tubig-ulan; mataas ang banta ng soil-borne diseases at soft rot"
                ),
                CheckUpOption(
                    id = "cabbage_care_continuous_brassica",
                    title = "Sunod-sunod na Pagtatanim ng Brassica (Walang Pahinga)",
                    tagalogDesc = "Pechay o repolyo rin ang dating tanim; mataas ang peligro ng pamumugad ng Clubroot spores"
                )
            )
        ),
        CheckUpActivity(
            id = "harvest",
            title = "Yugto at Pamamaraan ng Pag-aani",
            tagalogSubtitle = "Suriin ang laki, katigasan, at tamang oras ng pagpitas sa umaga",
            icon = Icons.Default.ContentCut,
            options = listOf(
                CheckUpOption(
                    id = "cabbage_harv_ready",
                    title = "Siksik, Matigas, at Handa Nang Anihin",
                    tagalogDesc = "1–3 kg ang timbang; matigas pindutin ang ulo; may sariwang wrapper leaves (70–90 DAT)",
                    isIdeal = true
                ),
                CheckUpOption(
                    id = "cabbage_harv_immature",
                    title = "Malambot Pa at Hindi Pa Buo ang Gitna",
                    tagalogDesc = "Maluwag pa ang mga panloob na dahon; kailangan pa ng 1–2 linggo ng patubig at potash"
                ),
                CheckUpOption(
                    id = "cabbage_harv_split_risk",
                    title = "Labis na Siksik / Nanganganib Pumutok",
                    tagalogDesc = "Napakasiksik na at nag-uumpisang magkalamat ang balat; kailangang pitasin agad sa umaga"
                )
            )
        )
    )
}

/**
 * Diagnostic solver providing actionable, research-backed Philippine solutions
 * for Cabbage (Repolyo) farming conditions based on DA-BPI & highland agricultural manuals.
 */
fun solveCabbageProblem(
    activityId: String,
    selectedOptions: Set<String>
): DirectCropSolution? {
    if (selectedOptions.isEmpty()) return null

    return when (activityId) {
        "watering" -> {
            when {
                selectedOptions.contains("cabbage_head_splitting") -> {
                    DirectCropSolution(
                        title = "Pagputok ng Ulo ng Repolyo (Head Splitting)",
                        tagalogTitle = "Solusyon sa Pagbitak Dulot ng Biglaang Sobrang Tubig",
                        severity = "Katamtaman hanggang Mataas (Moderate-High)",
                        badgeColor = AlertAmber,
                        directAnswer = "Ang pagputok ng ulo ng repolyo ay nangyayari kapag ang halaman ay dumanas ng panunuyo at biglang binuhusan ng labis na tubig. Mabilis sumipsip ng tubig ang mga panloob na dahon kaya't pumuputok ang panlabas na balat bago pa ito makapag-unat.",
                        actionSteps = listOf(
                            "Anihin agad ang mga pumutok na ulo upang magamit o maibenta bago mapasukan ng bacterial soft rot o insekto.",
                            "Panatilihing regular at pantay ang pagdidilig (2–3 beses kada linggo) upang hindi dumanas ng matinding 'drought stress' ang repolyo.",
                            "Sa mga siksik na ulong malapit nang anihin, magsagawa ng bahagyang pagpilipit sa puno (root pruning) upang mabawasan ang biglaang paghigop ng tubig mula sa ulan.",
                            "Maglagay ng makapal na organikong mulch (dayami o silver-black plastic mulch) upang mapanatiling pantay ang moisture ng lupa."
                        ),
                        materialsNeeded = listOf(
                            "Matalas na kutsilyo para sa agarang pag-aani",
                            "Organikong dayami o rice straw mulch",
                            "Drip irrigation o pantay na pandilig"
                        ),
                        criticalRule = "Huwag hayaang magsalit-salit ang sobrang tuyo at sobrang basang lupa sa panahon ng head-formation stage."
                    )
                }
                selectedOptions.contains("cabbage_tipburn_moisture") -> {
                    DirectCropSolution(
                        title = "Tip-Burn sa Panloob na Dahon (Calcium & Moisture Stress)",
                        tagalogTitle = "Solusyon sa Panunuyo ng Dulo ng Dahon sa Loob ng Ulo",
                        severity = "Mataas (High Market Damage)",
                        badgeColor = AlertAmber,
                        directAnswer = "Ang Tip-burn ay sanhi ng kakulangan ng calcium sa pinakabatang dulo ng dahon sa loob ng ulo. Dahil ang calcium ay dumadaloy lamang kasabay ng tubig sa xylem, ang patigil-tigil na pagdidilig o mabilisang paglaki dulot ng sobrang init ay nagdudulot ng panunuyo sa gilid ng dahon.",
                        actionSteps = listOf(
                            "Panatilihing tuloy-tuloy at matatag ang moisture ng kamada; huwag patuyuin ang ugat sa pagitan ng pagdidilig.",
                            "Mag-isprey ng foliar Calcium Nitrate o fermented CalPhos (Calcium Phosphate mula sa pinag-ihaw na balat ng itlog at suka) tuwing umaga.",
                            "Bawasan ang labis na nitrogen fertilizer (Urea) na nagdudulot ng masyadong mabilisang paglaki ng dahon.",
                            "Gumamit ng varieties na may likas na resistensya sa tip-burn tulad ng Scorpio F1 at KK Cross."
                        ),
                        materialsNeeded = listOf(
                            "CalPhos organic spray (1:200 dilution) o Calcium Nitrate",
                            "Knapsack sprayer",
                            "Drip irrigation lines para sa pantay na daloy"
                        ),
                        criticalRule = "I-isprey ang calcium sa maagang yugto bago tuluyang sumara ang ulo ng repolyo upang maabot ang mga panloob na dahon."
                    )
                }
                selectedOptions.contains("cabbage_waterlogged") -> {
                    DirectCropSolution(
                        title = "Labis na Tubig at Banta ng Soft Rot",
                        tagalogTitle = "Tamang Pamamahala sa Basang Kamada ng Repolyo",
                        severity = "Kritikal (Critical Hazard)",
                        badgeColor = DangerRed,
                        directAnswer = "Ang repolyo ay may mababaw ngunit maselang fibrous root system. Ang matagal na pagkababad sa tubig ay pumipigil sa paghinga ng ugat at mabilis na nag-aanyaya ng Pectobacterium (Bacterial Soft Rot) na tumutunaw sa buong ulo.",
                        actionSteps = listOf(
                            "Palalimin ang mga kanal sa pagitan ng kamada (30–40 cm ang lalim) upang mabilis humupa ang naipong tubig.",
                            "Siguraduhing nakataas ang taniman ng hindi bababa sa 20–25 cm (raised bed system).",
                            "Itigil muna ang pagdidilig hanggang sa matuyo ang ibabaw ng kamada sa 5 cm lalim.",
                            "Alisin ang mga nabubulok na panlabas na dahon na nakalapat sa basang lupa."
                        ),
                        materialsNeeded = listOf(
                            "Asarol o pala para sa pagpapalalim ng kanal",
                            "Drainage trenches",
                            "Matalas na shears para sa pag-aalis ng bulok na dahon"
                        ),
                        criticalRule = "Huwag kailanman hayaang lumubog sa tubig ang ugat ng repolyo nang higit sa 24 oras."
                    )
                }
                selectedOptions.contains("cabbage_water_stress") -> {
                    DirectCropSolution(
                        title = "Kakulangan sa Tubig (Water Stress)",
                        tagalogTitle = "Solusyon sa Pagkatigang at Maluwag na Ulo ng Repolyo",
                        severity = "Katamtaman (Moderate)",
                        badgeColor = AlertAmber,
                        directAnswer = "Nangangailangan ang repolyo ng mataas at tuloy-tuloy na supply ng tubig (25–35 mm bawat linggo), lalo na sa panahon ng pag-uulo (head filling). Ang tagtuyot ay nagreresulta sa maliliit, matigas ang hibla, at mapait na repolyo.",
                        actionSteps = listOf(
                            "Diligan agad ang kamada nang banayad ngunit malalim (soaking watering) hanggang maabot ang 15 cm sa ilalim ng lupa.",
                            "Magdilig tuwing maagang umaga (6:00 AM–8:00 AM) upang masipsip ng ugat bago uminit ang araw.",
                            "Maglagay ng makapal na mulch sa paligid ng puno upang maiwasan ang mabilis na pagsingaw ng moisture.",
                            "Sa mga rehiyong may init, magdilig tuwing ikalawang araw."
                        ),
                        materialsNeeded = listOf(
                            "Sprinkler hose o drip irrigation system",
                            "Rice straw o organic mulch"
                        ),
                        criticalRule = "Iwasan ang pagdidilig sa katanghalian kung kailan mainit ang araw upang hindi mapaso ang mga dahon."
                    )
                }
                else -> {
                    DirectCropSolution(
                        title = "Tamang Gabay sa Pagdidilig ng Repolyo",
                        tagalogTitle = "Eksaktong Dami at Oras ng Tubig para sa Siksik na Ulo",
                        severity = "Tamang Pangangalaga (Optimal)",
                        badgeColor = LushGreen,
                        directAnswer = "Panatilihing mamasa-masa ngunit hindi baha ang lupa. Ang regular na pagdidilig 2–3 beses kada linggo ay nagbibigay ng pinakamagandang timbang (1.5–3 kg bawat ulo) at makinis na wrapper leaves.",
                        actionSteps = listOf(
                            "Magdilig tuwing umaga bago mag-alas otso.",
                            "Magbuhos ng 1–1.5 litro bawat halaman kada pagdidilig depende sa panahon.",
                            "Suriin ang ilalim ng lupa gamit ang daliri bago magdilig."
                        ),
                        materialsNeeded = listOf("Drip tape o sprinkler system", "Mulch"),
                        criticalRule = "Panatilihing buhaghag at may maayos na daloy ng hangin ang lupang tinataniman."
                    )
                }
            }
        }

        "fertilizing" -> {
            when {
                selectedOptions.contains("cabbage_fert_calcium_tipburn") -> {
                    DirectCropSolution(
                        title = "Kakulangan sa Calcium at Tip-Burn",
                        tagalogTitle = "Agarang Paggamot Gamit ang CalPhos at Calcium Foliar",
                        severity = "Mataas (High)",
                        badgeColor = AlertAmber,
                        directAnswer = "Ang kakulangan sa Calcium ay nagdudulot ng pagkamatay ng mga selula sa gilid ng pinakabatang dahon sa loob ng repolyo (Tip-burn). Ito ay madalas lumitaw kapag labis ang potassium o nitrogen na humaharang sa calcium uptake, o kapag kapos sa tubig.",
                        actionSteps = listOf(
                            "Mag-isprey ng fermented CalPhos (2 kutsara bawat 1 litro ng tubig) tuwing umaga tuwing 5–7 araw.",
                            "Maglagay ng Calcium Nitrate (15.5-0-0) bilang drench o foliar spray.",
                            "Bawasan ang paglalagay ng puro Urea (46-0-0) upang maiwasan ang mabilis ngunit marupok na paglaki ng tisyu.",
                            "Panatilihing mamasa-masa ang kamada upang tuloy-tuloy ang paghigop ng ugat sa calcium."
                        ),
                        materialsNeeded = listOf(
                            "Organic CalPhos concoction o Calcium Nitrate fertilizer",
                            "Sprayer",
                            "Malinis na tubig-ulan o tubig-poso"
                        ),
                        criticalRule = "Huwag ihalo ang calcium sa mga abono na may mataas na phosphorus o sulfur sa iisang tangke upang hindi mag-precipitate."
                    )
                }
                selectedOptions.contains("cabbage_fert_boron_hollow") -> {
                    DirectCropSolution(
                        title = "Kakulangan sa Boron (Hollow Stem)",
                        tagalogTitle = "Solusyon sa Pagkakaron ng Butas o Guwang sa Tangkay",
                        severity = "Katamtaman (Moderate)",
                        badgeColor = AlertAmber,
                        directAnswer = "Ang hollow stem ay karaniwang sakit sa repolyo sa mga mabuhanging lupa sa Benguet at Bukidnon kapag kapos sa micronutrient na Boron at labis ang nitrogen. Nagiging marupok at madaling mabulok ang gitna ng repolyo.",
                        actionSteps = listOf(
                            "Mag-isprey ng foliar Borax o Solubor (0.1% solution o 1–2 gramo bawat litro ng tubig) sa panahon ng maagang rosette stage.",
                            "Bawasan ang labis na nitrogen fertilizing; huwag magbuhos ng maraming Urea nang sabay-sabay.",
                            "Maghalo ng compost na mayaman sa organikong bagay upang mapataas ang micronutrient retention ng lupa."
                        ),
                        materialsNeeded = listOf(
                            "Solubor / Borax powder (agricultural grade)",
                            "Weighing scale para sa eksaktong timbang",
                            "Foliar sprayer"
                        ),
                        criticalRule = "Maging maingat sa sukat ng Boron—ang labis na dosis ay nakalalason sa mga halaman."
                    )
                }
                selectedOptions.contains("cabbage_fert_potash_def") -> {
                    DirectCropSolution(
                        title = "Kakulangan sa Potash (Maluwag at Malambot na Ulo)",
                        tagalogTitle = "Solusyon Gamit ang Muriate of Potash para sa Siksik na Ani",
                        severity = "Katamtaman (Moderate)",
                        badgeColor = AlertAmber,
                        directAnswer = "Ang Potassium (K) ang pangunahing nutrisyon na nagpapatigas, nagpapasiksik, at nagpapatibay sa mga dingding ng selula ng repolyo. Ang kakulangan nito ay lumilikha ng 'spongy' at maluwag na repolyo na madaling malanta.",
                        actionSteps = listOf(
                            "Mag-side dress ng Muriate of Potash (0-0-60) sa sukat na 5–10 gramo bawat puno sa 4–6 na linggo pagkatanim.",
                            "Magdilig agad matapos ilagay ang abono upang matunaw patungo sa root zone.",
                            "Maaari ring mag-isprey ng Fermented Fruit Juice (FFJ mula sa saging o hinog na papaya) na sagana sa natural potassium.",
                            "Siguraduhing hindi nakadikit ang butil ng abono sa mismong tangkay ng repolyo."
                        ),
                        materialsNeeded = listOf(
                            "Muriate of Potash (0-0-60)",
                            "FFJ (Fermented Fruit Juice)",
                            "Pandilig"
                        ),
                        criticalRule = "Ilagay ang potash sa layong 10 cm mula sa puno (ring method) upang hindi mapaso ang ugat."
                    )
                }
                selectedOptions.contains("cabbage_fert_nitrogen_def") -> {
                    DirectCropSolution(
                        title = "Kakulangan sa Nitrogen (Paninilaw at Pagkabansot)",
                        tagalogTitle = "Solusyon sa Maputlang Dahon Gamit ang Urea o Fish Amino Acid",
                        severity = "Mataas (High)",
                        badgeColor = AlertAmber,
                        directAnswer = "Malakas kumain ng Nitrogen ang repolyo para sa pagpapalapad ng mga dahon sa rosette stage (150–200 kg N/ha). Ang paninilaw ng lumang dahon ay palatandaan na kailangan na nitong kagyat na side-dress.",
                        actionSteps = listOf(
                            "Mag-side dress ng Urea (46-0-0) sa sukat na 5–8 gramo bawat puno, o 14-14-14 kung hindi pa nakapag-basal.",
                            "Magsagawa ng foliar application ng Fish Amino Acid (FAA) sa sukat na 2 kutsara bawat 1 litro ng tubig tuwing 7 araw.",
                            "Tabunan ng manipis na lupa at diligan upang maiwasan ang pagsingaw ng nitrogen gas."
                        ),
                        materialsNeeded = listOf(
                            "Urea (46-0-0) o Complete Fertilizer",
                            "Fish Amino Acid (FAA)",
                            "Panghukay para sa side-dressing"
                        ),
                        criticalRule = "Huwag maglagay ng labis na nitrogen sa huling 3 linggo bago ang ani upang hindi lumambot at pumutok ang ulo."
                    )
                }
                else -> {
                    DirectCropSolution(
                        title = "Tamang Programa sa Pag-aabono ng Repolyo",
                        tagalogTitle = "Gabay ng DA-BPI para sa 30–40 Tonelada Bawat Ektarya",
                        severity = "Tamang Nutrisyon (Optimal)",
                        badgeColor = LushGreen,
                        directAnswer = "Ang balanseng abono para sa repolyo ay binubuo ng basal Complete (14-14-14) sa planting, na sinusundan ng 2–3 beses na side-dressing ng Nitrogen at Potash sa ika-3 at ika-6 na linggo.",
                        actionSteps = listOf(
                            "Basal: 10–15g ng 14-14-14 bawat butas + 1 dakot ng processed chicken manure o compost.",
                            "Unang side-dress (20 DAT): 5g Urea + 5g 14-14-14 bawat puno.",
                            "Ikalawang side-dress (40 DAT): 5g Muriate of Potash + 5g Urea bawat puno sa pagsisimula ng head filling."
                        ),
                        materialsNeeded = listOf("14-14-14", "Urea (46-0-0)", "0-0-60", "Compost"),
                        criticalRule = "Laging diligan ang kamada pagkatapos maglagay ng granular chemical fertilizers."
                    )
                }
            }
        }

        "pests" -> {
            when {
                selectedOptions.contains("cabbage_pest_dbm") -> {
                    DirectCropSolution(
                        title = "Diamondback Moth (Plutella xylostella) #1 Salot",
                        tagalogTitle = "Biyolohikal at Pinagsamang Pamamahala (IPM) sa DBM",
                        severity = "Kritikal (Critical - #1 Cabbage Pest)",
                        badgeColor = DangerRed,
                        directAnswer = "Ang Diamondback Moth (DBM) ang numero unong peste ng repolyo sa Pilipinas. Ang maliliit na berdeng uod ay kumakain sa ilalim ng dahon at lumilikha ng 'window-pane' na butas. Mabilis magkaroon ng resistensya sa synthetic chemical insecticides ang DBM.",
                        actionSteps = listOf(
                            "Mag-isprey ng Bacillus thuringiensis (Bt kurstaki o Bt aizawai) sa takipsilim (5:00 PM) kung kailan aktibo ang uod at hindi nasisira ng araw ang Bt bacteria.",
                            "Magtanim ng trap crops tulad ng mustasa (Brassica juncea) sa paligid ng taniman upang maakit doon ang DBM bago mapunta sa repolyo.",
                            "Pangalagaan ang natural parasitic wasp (Diadegma semiclausum) na natural na nangingitlog sa loob ng DBM larvae sa mga kabundukan ng Benguet.",
                            "Kung gagamit ng insecticide para sa malalang atake, magsalit-salit ng magkakaibang chemical mode-of-action (hal. Spinetoram, Chlorantraniliprole, Emamectin benzoate) at huwag gagamit ng parehong kemikal nang sunod-sunod.",
                            "Mag-install ng yellow sticky traps at sex pheromone traps para sa maagang pag-monitor ng adult moths."
                        ),
                        materialsNeeded = listOf(
                            "Bacillus thuringiensis (Bt) biopesticide",
                            "Pheromone traps para sa DBM",
                            "Mustard trap crop seeds",
                            "Targeted rotated insecticide (kung kinakailangan)"
                        ),
                        criticalRule = "Huwag mag-isprey ng broad-spectrum pyrethroids nang tuloy-tuloy dahil pinapatay nito ang mga parasitoid wasp at nagdudulot ng DBM resurgence."
                    )
                }
                selectedOptions.contains("cabbage_pest_looper") -> {
                    DirectCropSolution(
                        title = "Cabbage Worm at Looper (Pieris / Trichoplusia)",
                        tagalogTitle = "Pamamahala sa Gutay-gutay na Dahon at Malalaking Uod",
                        severity = "Mataas (High)",
                        badgeColor = AlertAmber,
                        directAnswer = "Ang Cabbage Looper at Cabbage Worm ay malalaking berdeng uod na mabilis kumain ng malalaking bahagi ng dahon at nag-iiwan ng maiitim na dumi sa gitna ng rosette na nagdudulot ng amag.",
                        actionSteps = listOf(
                            "Magsagawa ng regular na pag-iinspeksyon at mano-manong pulutin (hand-picking) ang malalaking uod kung maliit ang taniman.",
                            "Mag-isprey ng Bacillus thuringiensis (Bt) habang maliliit pa ang uod para sa 100% pagkalipol.",
                            "Maaari ring gumamit ng katas ng siling labuyo, bawang, at neem oil bilang natural repellent.",
                            "Takpan ng fine insect netting (nylon mesh) ang punlaan upang hindi makapangitlog ang mga paru-paro."
                        ),
                        materialsNeeded = listOf(
                            "Bacillus thuringiensis (Bt)",
                            "Neem oil + liquid dish soap surfactant",
                            "Insect netting para sa punlaan"
                        ),
                        criticalRule = "Pitasin agad ang mga uod bago makapasok sa loob ng namumuong ulo kung saan hindi na maabot ng spray."
                    )
                }
                selectedOptions.contains("cabbage_pest_cutworm") -> {
                    DirectCropSolution(
                        title = "Cutworm / Harabas sa Bagong Lipat na Punla",
                        tagalogTitle = "Proteksyon sa Pinupugot na Tangkay ng Repolyo",
                        severity = "Mataas (High seedling loss)",
                        badgeColor = AlertAmber,
                        directAnswer = "Ang cutworm ay nagtatago sa ilalim ng lupa tuwing araw at lumalabas sa gabi upang putulin ang buong tangkay ng mga batang repolyo sa antas ng lupa.",
                        actionSteps = listOf(
                            "Maglagay ng proteksyong 'cardboard collar' o plastic cup ring sa paligid ng bawat tangkay ng bagong lipat na punla.",
                            "Mag-araro nang malalim bago magtanim upang mabilad sa araw at kainin ng ibon ang mga pupa sa lupa.",
                            "Maglagay ng neem cake o trichoderma sa lupa sa paligid ng tanim.",
                            "Mag-spray ng Bt o entomopathogenic nematodes sa base ng halaman sa dapit-hapon."
                        ),
                        materialsNeeded = listOf(
                            "Cardboard / paper collars",
                            "Neem cake powder",
                            "Bt spray para sa gabi"
                        ),
                        criticalRule = "Hukayin nang bahagya ang paligid ng naputol na punla—kadalasan ay nakatago lamang ang cutworm sa 2–3 cm sa ilalim ng lupa."
                    )
                }
                selectedOptions.contains("cabbage_pest_aphids") -> {
                    DirectCropSolution(
                        title = "Cabbage Aphids (Kuto ng Dahon)",
                        tagalogTitle = "Organikong Pamamahala Gamit ang Neem at Sabon",
                        severity = "Katamtaman (Moderate)",
                        badgeColor = AlertAmber,
                        directAnswer = "Ang mga abuhin o berdeng aphids ay sumisipsip ng katas sa ilalim ng dahon at nagdudulot ng paninilaw at kulubot na dahon, pati na pagkalat ng mga virus.",
                        actionSteps = listOf(
                            "Bombahin ng malakas na spray ng tubig ang ilalim ng mga dahon upang mahulog ang aphids.",
                            "Mag-isprey ng solusyon ng Neem oil (5ml) + banayad na sabon (2ml) sa bawat litro ng tubig.",
                            "Pangalagaan ang mga ladybug at hoverfly larvae na natural na kumakain ng libo-libong aphids bawat araw."
                        ),
                        materialsNeeded = listOf("Neem oil", "Liquid soap", "Sprayer"),
                        criticalRule = "Direktang patamaan ang ilalim ng dahon kung saan nagkukumpulan ang mga peste."
                    )
                }
                else -> {
                    DirectCropSolution(
                        title = "Malinis at Protektadong Pananim na Repolyo",
                        tagalogTitle = "Pagpapanatili ng Malusog na Dahon at Ulo",
                        severity = "Tamang Pangangalaga (Optimal)",
                        badgeColor = LushGreen,
                        directAnswer = "Walang nakitang mapaminsalang peste. Ipagpatuloy ang lingguhang pag-iinspeksyon sa ilalim ng dahon at paggamit ng yellow sticky traps sa paligid ng kamada.",
                        actionSteps = listOf(
                            "Suriin ang 10–20 halaman linggu-linggo para sa maagang senyales ng uod.",
                            "Panatilihing malinis ang mga damo sa paligid ng kamada."
                        ),
                        materialsNeeded = listOf("Yellow sticky traps", "Hand magnifying lens"),
                        criticalRule = "Ang maagang pagtuklas sa DBM larvae ang susi upang maiwasan ang malawakang pagkasira ng repolyo."
                    )
                }
            }
        }

        "diseases" -> {
            when {
                selectedOptions.contains("cabbage_dis_clubroot") -> {
                    DirectCropSolution(
                        title = "Clubroot / Bukol sa Ugat (Plasmodiophora brassicae)",
                        tagalogTitle = "Solusyon sa Asidikong Lupa: Agricultural Lime at Rotation",
                        severity = "Kritikal (Devastating Soil-Borne Disease)",
                        badgeColor = DangerRed,
                        directAnswer = "Ang Clubroot ang pinakamapaminsalang fungal disease sa repolyo at brassicas sa Cordillera (Benguet) at asidikong kabundukan. Nagdudulot ito ng abnormal na pamamaga at pagiging bukol-bukol ng ugat na pumipigil sa pagsipsip ng tubig, kaya't lanta ang halaman sa araw. Ang spores nito ay nananatiling buhay sa lupa nang higit 10 taon.",
                        actionSteps = listOf(
                            "Maglagay ng Agricultural Lime (Apog / Calcitic Lime o Hydrated Lime) sa lupa 3–4 na linggo bago magtanim upang itaas ang soil pH sa 7.0–7.2 (hindi nabubuhay ang spores sa alkaline pH).",
                            "Bunutin at sunugin agad ang lahat ng apektadong halaman kasama ang buong bukol na ugat; huwag ilalagay sa compost pile.",
                            "Magsagawa ng mahigpit na 3 hanggang 4 na taong Crop Rotation palayo sa pamilya ng brassicas (magtanim ng mais, beans, patatas, o karot sa halip na repolyo/pechay).",
                            "Hugasan at disimpektahin ang lahat ng gamit sa pagsasaka at bota bago lumipat sa ibang kamada upang hindi maikalat ang kontaminadong lupa.",
                            "Pumili ng mga hybrid varieties na may 'Clubroot resistance' kung kilalang may kasaysayan ng sakit ang bukid."
                        ),
                        materialsNeeded = listOf(
                            "Agricultural Lime (CaCO3) o Hydrated Lime",
                            "pH soil test kit",
                            "Sako para sa pagtatapon ng may sakit na halaman",
                            "Disinfectant (bleach solution para sa mga kagamitan)"
                        ),
                        criticalRule = "Walang kemikal na pamatay-amag na makagagaling sa repolyong may bukol na sa ugat—ang tanging lunas ay pagkontrol sa pH ng lupa bago magtanim at crop rotation."
                    )
                }
                selectedOptions.contains("cabbage_dis_softrot") -> {
                    DirectCropSolution(
                        title = "Bacterial Soft Rot (Pectobacterium / Erwinia)",
                        tagalogTitle = "Solusyon sa Mabaho at Malugaw na Ulo ng Repolyo",
                        severity = "Kritikal (Total Post-Harvest / Field Loss)",
                        badgeColor = DangerRed,
                        directAnswer = "Ang Bacterial Soft Rot ay pumapasok sa mga sugat dulot ng insekto, abono, o pag-aani. Mabilis nitong tinutunaw ang mga dingding ng selula at ginagawang malugaw at mabaho ang buong ulo ng repolyo sa loob lamang ng 24–48 oras.",
                        actionSteps = listOf(
                            "Agad na pitasin at ilibing nang malayo ang lahat ng may sakit na repolyo upang hindi kumalat sa katabing mga halaman.",
                            "Siguraduhing mabilis humupa ang tubig sa kamada; huwag magdidilig sa ibabaw ng ulo ng repolyo (overhead watering).",
                            "Kontrolin ang mga uod (DBM, looper) na lumilikha ng mga sugat na pinapasukan ng bakterya.",
                            "Mag-isprey ng copper-based fungicide/bactericide (Copper Hydroxide o Copper Oxychloride) bilang preventive spray pagkatapos ng malalakas na ulan.",
                            "Sa pag-aani, gumamit ng malinis at nadidisimpektang kutsilyo at iwasang mabugbog ang mga ulo."
                        ),
                        materialsNeeded = listOf(
                            "Copper Hydroxide / Copper Oxychloride bactericide",
                            "Malinis na kutsilyo na may 70% alcohol",
                            "Drainage shovels"
                        ),
                        criticalRule = "Huwag anihin o hawakan ang repolyo habang basa ang mga dahon upang hindi kumalat ang bakterya sa pamamagitan ng tubig."
                    )
                }
                selectedOptions.contains("cabbage_dis_blackrot") -> {
                    DirectCropSolution(
                        title = "Black Rot (Xanthomonas campestris)",
                        tagalogTitle = "Pamamahala sa Hugis-V na Paninilaw at Maitim na Ugat",
                        severity = "Mataas (High)",
                        badgeColor = DangerRed,
                        directAnswer = "Ang Black Rot ay nakahahawang sakit na sanhi ng bakterya. Makikita ang mga naninilaw na hugis-V na sugat sa gilid ng dahon na may maitim na ugat (veins). Kumakalat ito sa pamamagitan ng tilamsik ng tubig-ulan.",
                        actionSteps = listOf(
                            "Gumamit lamang ng sertipikadong malinis na binhi na sumailalim sa 'hot water seed treatment' (50°C sa loob ng 25–30 minuto).",
                            "Mag-isprey ng Copper-based bactericide bilang proteksyon tuwing tag-ulan.",
                            "Alisin at sirain ang mga apektadong dahon sa maagang yugto.",
                            "Iwasan ang pagtatrabaho sa taniman kapag basa pa ang hamog sa dahon."
                        ),
                        materialsNeeded = listOf(
                            "Certified seeds",
                            "Copper bactericide",
                            "Personal protective equipment"
                        ),
                        criticalRule = "Magsagawa ng 2–3 taong crop rotation palayo sa lahat ng brassicas upang maubos ang bakterya sa lupa."
                    )
                }
                selectedOptions.contains("cabbage_dis_alternaria") -> {
                    DirectCropSolution(
                        title = "Alternaria Leaf Spot (Batik sa Dahon ng Repolyo)",
                        tagalogTitle = "Solusyon sa Pabilog na Concentric Dark Rings",
                        severity = "Katamtaman (Moderate)",
                        badgeColor = AlertAmber,
                        directAnswer = "Fungal disease na lumilikha ng maiitim at pabilog na batik sa panlabas na mga dahon. Lumalaganap ito sa mahalumigmig at maulang panahon.",
                        actionSteps = listOf(
                            "Alisin ang matatandang dahon sa ibaba na nakadikit sa lupa upang magkaroon ng magandang sirkulasyon ng hangin.",
                            "Mag-isprey ng preventive fungicide tulad ng Mancozeb o Chlorothalonil tuwing panahon ng tag-ulan.",
                            "Iwasan ang sprinkler irrigation sa dapit-hapon upang hindi magdamag na basa ang mga dahon."
                        ),
                        materialsNeeded = listOf("Mancozeb / Copper fungicide", "Pruning shears"),
                        criticalRule = "Panatilihing maluwag ang pagitan ng mga tanim (40–50 cm) para sa mabilis na pagkatuyo ng hangin."
                    )
                }
                else -> {
                    DirectCropSolution(
                        title = "Malusog at Walang Sakit na Repolyo",
                        tagalogTitle = "Pagpapanatili ng Kalinisan sa Bukid (Sanitation)",
                        severity = "Tamang Pangangalaga (Optimal)",
                        badgeColor = LushGreen,
                        directAnswer = "Walang nakitang sakit sa taniman. Panatilihin ang kalinisan ng kamada, maayos na drainage, at regular na pagsubaybay sa pH ng lupa.",
                        actionSteps = listOf(
                            "Panatilihing malinis ang mga kanal sa pagitan ng mga kamada.",
                            "Alisin ang mga luma at naninilaw na wrapper leaves sa ilalim."
                        ),
                        materialsNeeded = listOf("Mulch", "Disinfected tools"),
                        criticalRule = "Laging sumunod sa crop rotation schedule pagkatapos ng anihan."
                    )
                }
            }
        }

        "field_care" -> {
            when {
                selectedOptions.contains("cabbage_care_continuous_brassica") -> {
                    DirectCropSolution(
                        title = "Peligro ng Sunod-sunod na Brassica Cropping",
                        tagalogTitle = "Planong Crop Rotation para Maiwasan ang Clubroot",
                        severity = "Mataas (High Soil Risk)",
                        badgeColor = AlertAmber,
                        directAnswer = "Ang sunod-sunod na pagtatanim ng repolyo, pechay, mustasa, o broccoli sa iisang plot ay nagdudulot ng mabilisang pagdami ng soil-borne pathogens tulad ng Plasmodiophora (Clubroot) at Xanthomonas (Black Rot), pati na pagkaubos ng calcium at boron sa lupa.",
                        actionSteps = listOf(
                            "Pagkatapos ng aning ito, magtanim ng Legumes (sitaw, snap beans, o munggo) na nagbabalik ng natural nitrogen sa lupa.",
                            "Maaari ring magtanim ng Root crops (karot o patatas) o Solanaceous crops (kamatis o sili) bilang kapalit.",
                            "Huwag ibalik ang repolyo sa parehong kamada sa loob ng 2 hanggang 3 taon.",
                            "Maglagay ng saganang compost at carbonized rice hull (CRH) bago ang susunod na pananim."
                        ),
                        materialsNeeded = listOf(
                            "Binhi ng snap beans o mais para sa rotation",
                            "Compost at CRH",
                            "Lime para sa pagpapahinga ng lupa"
                        ),
                        criticalRule = "Ang crop rotation ang pinakamurang at pinakaepektibong paraan upang maiwasan ang permanenteng pagkasira ng lupang taniman."
                    )
                }
                selectedOptions.contains("cabbage_care_poor_drainage") -> {
                    DirectCropSolution(
                        title = "Baradong Drainage at Mababaw na Kanal",
                        tagalogTitle = "Paggawa ng Matataas na Raised Bed at Drainage Canal",
                        severity = "Mataas (High)",
                        badgeColor = AlertAmber,
                        directAnswer = "Ang mababaw na kamada ay madaling maging sanhi ng pagkalunod ng ugat at mabilisang pagkalat ng Bacterial Soft Rot sa panahon ng ulan.",
                        actionSteps = listOf(
                            "Gumawa ng kamada na may taas na 20–25 cm at lapad na 1 metro.",
                            "Maghukay ng 30–40 cm na kanal sa pagitan ng mga kamada na may patungo sa pangunahing daluyan ng tubig.",
                            "Lagyan ng banayad na hilling-up ang paligid ng puno upang dumausdos ang tubig palayo sa tangkay."
                        ),
                        materialsNeeded = listOf("Pala at asarol"),
                        criticalRule = "Siguraduhing hindi nagkakaroon ng 'stagnant pools' ng tubig sa gitna ng taniman."
                    )
                }
                else -> {
                    DirectCropSolution(
                        title = "Pangangalaga sa Kamada at Hilling-Up",
                        tagalogTitle = "Tamang Pag-aalaga Bago Sumara ang Canopy ng Repolyo",
                        severity = "Tamang Pangangalaga (Optimal)",
                        badgeColor = LushGreen,
                        directAnswer = "Bago tuluyang sumara ang mga dahon ng repolyo (canopy closure), mahalagang maisagawa ang pagtatanggal ng damo at hilling-up (pagtatambak ng lupa) upang maging matatag ang halaman.",
                        actionSteps = listOf(
                            "Alisin ang mga damo nang manu-mano habang maliliit pa.",
                            "Maglagay ng manipis na tambak ng buhaghag na lupa sa paligid ng puno kasabay ng huling side-dress ng abono.",
                            "Iwasang masugatan ang mga ugat habang nagbubungkal."
                        ),
                        materialsNeeded = listOf("Maliit na trowel o kalaykay"),
                        criticalRule = "Itigil ang pagbubungkal sa lupa kapag nagsara na ang mga dahon ng repolyo upang hindi masugatan ang mga kumakalat na ugat."
                    )
                }
            }
        }

        "harvest" -> {
            DirectCropSolution(
                title = "Tamang Yugto at Pamamaraan ng Pag-aani ng Repolyo",
                tagalogTitle = "Gabay sa Masaganang Pitas at Tamang Post-Harvest ng Repolyo",
                severity = "Pag-aani (Harvest Ready)",
                badgeColor = LushGreen,
                directAnswer = "Ang repolyo ay handa nang anihin sa loob ng 70–90 araw pagkatanim (DAT). Ang handang ulo ay siksik at matigas kapag pinindot ng hinlalaki, may timbang na 1–3 kg, at may diameter na 15–25 cm.",
                actionSteps = listOf(
                    "Pitasin ang repolyo sa maagang umaga (6:00 AM–8:00 AM) bago uminit ang araw upang mapanatili ang crispness at turgidity ng mga dahon.",
                    "Gumamit ng matalas at malinis na kutsilyo. Putulin ang tangkay sa mismong base ng halaman at mag-iwan ng 2 hanggang 3 'wrapper leaves' upang magsilbing proteksyon sa biyahe patungong pamilihan.",
                    "Alisin ang anumang naninilaw, may sira ng uod, o nadumihang panlabas na dahon.",
                    "I-salansan nang maingat sa mga may bentilasyong plastic crate o basket na may sapin upang hindi magkapasa o magkasugat ang mga ulo.",
                    "Kung iimbak, ilagay sa malamig na lugar (0–4°C na may 95% humidity para sa 2–3 linggong kasariwaan)."
                ),
                materialsNeeded = listOf(
                    "Matalas na harvest knife",
                    "Disinfectant (alcohol para sa kutsilyo)",
                    "Plastic harvest crates na may padding"
                ),
                criticalRule = "Huwag kailanman ihagis o ibagsak ang mga naaning repolyo—ang mga pasa (bruises) ay madaling kapitan ng Bacterial Soft Rot habang ibinibiyahe."
            )
        }

        else -> null
    }
}
