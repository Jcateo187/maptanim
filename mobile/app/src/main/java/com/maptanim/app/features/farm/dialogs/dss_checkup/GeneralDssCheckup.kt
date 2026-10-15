package com.maptanim.app.features.farm.dialogs.dss_checkup

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Coronavirus
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.ui.graphics.Color

private val RedAlert = Color(0xFFD32F2F)
private val AmberAlert = Color(0xFFF57C00)
private val LushGreen = Color(0xFF2E7D32)
private val BlueAccent = Color(0xFF0288D1)

fun getGeneralCheckUpActivities(cropName: String, isStakingCrop: Boolean): List<CheckUpActivity> {
    val list = mutableListOf<CheckUpActivity>()

    if (isStakingCrop) {
        list.add(
            CheckUpActivity(
                id = "staking",
                title = "🪵 Pagtutulos at Balag (Staking & Trellis)",
                tagalogTitle = "Suporta Laban sa Paglapat sa Lupa",
                icon = Icons.Default.Eco,
                description = "Pagsusuri sa tindig ng halaman at pag-iwas sa pagkadikit ng bunga sa basang lupa.",
                options = listOf(
                    CheckUpOption(
                        id = "fruit_rot_soil",
                        label = "Sumasayad ang mga bunga sa basang lupa",
                        description = "Maaaring mabulok o pamahayan ng amag kung nakalapat sa mamasa-masang lupa."
                    ),
                    CheckUpOption(
                        id = "falling_ground",
                        label = "Bumabagsak o natutumba ang tangkay dahil walang tulos",
                        description = "Nababali ang sanga kapag nahanginan o kapag dumami ang mga bunga."
                    ),
                    CheckUpOption(
                        id = "heavy_fruit",
                        label = "Masyadong mabigat ang mga kumpol ng bunga",
                        description = "Kailangan ng karagdagang tali o suporta upang hindi mahati ang katawan ng halaman."
                    ),
                    CheckUpOption(
                        id = "wind_storm",
                        label = "Nababali ang mga sanga dahil sa malalakas na hangin o ulan",
                        description = "Kailangan ng mas matibay na bamboo frame support."
                    )
                )
            )
        )
    }

    list.add(
        CheckUpActivity(
            id = "watering",
            title = "💧 Pagdidilig at Lupa (Irrigation)",
            tagalogTitle = "Halumigmig at Kondisyon ng Root Zone",
            icon = Icons.Default.WaterDrop,
            description = "Pagsusuri kung kulang o labis ang tubig sa ugat na nagiging sanhi ng pagkalanta o pagkabulok ng ugat.",
            options = listOf(
                CheckUpOption(
                    id = "cracked_dry",
                    label = "Mababa ang moisture at bitak-bitak ang lupa sa paligid",
                    description = "Tuyot ang lupa; mabilis sumingaw ang tubig sa ibabaw ng kamada."
                ),
                CheckUpOption(
                    id = "midday_wilt",
                    label = "Nalanta ang mga dahon tuwing katanghalian ngunit bumabangon sa hapon",
                    description = "Heat stress at mabilis na transpiration dulot ng kakulangan sa malalim na halumigmig."
                ),
                CheckUpOption(
                    id = "waterlogged",
                    label = "Nalulunod o may nakatambak na tubig matapos ang malakas na ulan",
                    description = "Kulang sa drainage canal; panganib ng pagkabulok ng ugat."
                ),
                CheckUpOption(
                    id = "yellow_overwater",
                    label = "Naninilaw ang ibabang dahon kahit mamasa-masa pa ang lupa",
                    description = "Senyales ng labis na pagdidilig (overwatering) at kawalan ng oxygen sa ugat."
                )
            )
        )
    )

    list.add(
        CheckUpActivity(
            id = "fertilizer",
            title = "🌱 Pataba at Sustansya (Nutrition)",
            tagalogTitle = "Kakulangan o Sobra sa Abono",
            icon = Icons.Default.Eco,
            description = "Pagsusuri sa pagkalaglag ng bulaklak, pamumutla ng dahon, at tamang nutrisyon.",
            options = listOf(
                CheckUpOption(
                    id = "blossom_drop",
                    label = "Nalalagas ang mga bulaklak bago pa maging bunga",
                    description = "Init, sobrang nitrogen, o kakulangan sa phosphorus at potassium para sa fruit setting."
                ),
                CheckUpOption(
                    id = "pale_nitrogen",
                    label = "Maputla at naninilaw ang buong halaman / mabagal lumaki",
                    description = "Kakulangan sa Nitrogen (N); kailangan ng organikong pampalago (FPJ o vermitea)."
                ),
                CheckUpOption(
                    id = "excess_nitrogen",
                    label = "Napakakapal at madilim ang berdeng dahon ngunit walang bulaklak",
                    description = "Sobra sa Nitrogen; nagpapasibol lamang ng dahon sa halip na mamunga."
                ),
                CheckUpOption(
                    id = "potassium_slow",
                    label = "Mabagal mamula o hindi pantay ang paghinog ng mga bunga",
                    description = "Kakulangan sa Potassium (K); kailangan ng Fermented Fruit Juice (FFJ) at wood ash."
                )
            )
        )
    )

    list.add(
        CheckUpActivity(
            id = "pests",
            title = "🐛 Peste at Insekto (Pest Control)",
            tagalogTitle = "Pamamahala sa mga Uod at Peste",
            icon = Icons.Default.BugReport,
            description = "Pagkontrol sa uod sa bunga, aphids, leaf miner, at iba pang insekto gamit ang organikong pamamaraan.",
            options = listOf(
                CheckUpOption(
                    id = "fruit_borer",
                    label = "May uod sa loob ng bunga o talbos (Fruit & Shoot Borer)",
                    description = "May butas sa bunga na may dumi ng uod at nabubulok ang loob."
                ),
                CheckUpOption(
                    id = "aphids_whitefly",
                    label = "May maliliit na insekto sa ilalim ng dahon (Aphids / Whiteflies)",
                    description = "Sumisipsip ng katas ng dahon at nagdudulot ng pamumutla o amag."
                ),
                CheckUpOption(
                    id = "leaf_miner",
                    label = "May mapuputing parang kalyeng linya sa dahon (Leaf Miner)",
                    description = "Uod na kumakain sa loob ng epidermis ng dahon."
                ),
                CheckUpOption(
                    id = "leaf_curling",
                    label = "Kumukulot at tumitigas ang bagong sibol na dahon (Thrips/Mites)",
                    description = "Deformed ang mga bagong dahon at humihinto ang paghaba."
                ),
                CheckUpOption(
                    id = "chewed_holes",
                    label = "Tadtad ng butas ang dahon (Flea Beetle / Caterpillars)",
                    description = "Kinakain ng uod o salagubang ang dahon hanggang maging butas-butas."
                )
            )
        )
    )

    list.add(
        CheckUpActivity(
            id = "disease",
            title = "🍂 Sakit at Dahon (Diseases)",
            tagalogTitle = "Foliar at Soil-borne Pathogens",
            icon = Icons.Default.Coronavirus,
            description = "Paggamot sa Early Blight, Bacterial Wilt, Powdery Mildew, at Damping-off.",
            options = listOf(
                CheckUpOption(
                    id = "early_blight",
                    label = "May pabilog at maitim na mantsa sa dahon (Leaf Spot / Blight)",
                    description = "Concentric rings na may dilaw na paligid sa ibabang dahon."
                ),
                CheckUpOption(
                    id = "bacterial_wilt",
                    label = "Biglaang pagkalanta ng buong halaman habang berde pa",
                    description = "Bacterial Wilt sa vascular bundle na nagpapatumba sa halaman sa loob ng 24 oras."
                ),
                CheckUpOption(
                    id = "powdery_mildew",
                    label = "Mapuputing parang pulbos sa ibabaw ng dahon (Powdery Mildew)",
                    description = "Puting amag na bumabalot sa dahon at pumipigil sa photosynthesis."
                ),
                CheckUpOption(
                    id = "mosaic_virus",
                    label = "Kulubot at may batik-batik na dilaw ang dahon (Mosaic Virus)",
                    description = "Viral disease na hatid ng aphids o whiteflies."
                )
            )
        )
    )

    if (isStakingCrop) {
        list.add(
            CheckUpActivity(
                id = "pruning",
                title = "✂️ Pruning at Suwi (Training)",
                tagalogTitle = "Pag-aalis ng Suwi at Pagbabawas ng Dahon",
                icon = Icons.Default.ContentCut,
                description = "Pagpapanatili ng magandang sirkulasyon ng hangin at pagtutuon ng sustansya sa mga bunga.",
                options = listOf(
                    CheckUpOption(
                        id = "excess_suckers",
                        label = "Maraming suwi na tumutubo sa kili-kili ng sanga",
                        description = "Mga bagong sanga (suckers) na umagaw sa sustansya ng pangunahing puno."
                    ),
                    CheckUpOption(
                        id = "bottom_leaves",
                        label = "Dahon sa ibaba ay naninilaw at sumasayad sa lupa",
                        description = "Panganib ng soil-borne splash blight tuwing umuulan."
                    ),
                    CheckUpOption(
                        id = "dense_bush",
                        label = "Masyadong masinsin at hindi nasisikatan ng araw ang gitna",
                        description = "Kakulangan ng hangin na nagiging sanhi ng pamumuo ng fungal moisture."
                    )
                )
            )
        )
    }

    list.add(
        CheckUpActivity(
            id = "harvest",
            title = "🧺 Pag-aani (Harvesting)",
            tagalogTitle = "Gabay sa Pitas at Pag-iingat",
            icon = Icons.Default.Agriculture,
            description = "Tamang yugto ng pagpitas, paraan ng paggupit, at pagpapahaba ng shelf life.",
            options = listOf(
                CheckUpOption(
                    id = "harvest_timing",
                    label = "Paano malalaman kung handa na pitasin ang pananim",
                    description = "Mga senyales ng tamang maturity para sa pinakamasarap na lasa."
                ),
                CheckUpOption(
                    id = "proper_picking",
                    label = "Tamang paraan ng pagpitas nang hindi nasisira ang puno",
                    description = "Paggamit ng pruner at pag-iingat sa mga bagong usbong na bulaklak."
                )
            )
        )
    )

    return list
}

fun solveGeneralCropProblem(
    cropName: String,
    activityId: String,
    selectedOptions: Set<String>
): DirectCropSolution? {
    if (selectedOptions.isEmpty()) return null

    return when (activityId) {
        "staking" -> {
            DirectCropSolution(
                title = "Gabay sa Pagtutulos para sa $cropName",
                tagalogTitle = "Wastong Pamamaraan ng Pagpapatayo ng Tanim",
                severity = "Pangangalaga",
                badgeColor = LushGreen,
                directAnswer = "Ang tamang pagtutulos ng $cropName ay nag-aangat sa mga dahon at bunga palayo sa mamasa-masang lupa upang maiwasan ang mabilis na pagkabulok.",
                actionSteps = listOf(
                    "Magbaon ng patpat na may habang 1.2–1.5m sa tabi ng halaman.",
                    "Talian nang maluwag gamit ang malambot na tali sa hugis '8'.",
                    "Gupitin ang mga lumang dahon na sumasayad sa lupa."
                ),
                materialsNeeded = listOf("Patpat o kawayan", "Malambot na tali", "Gunting"),
                criticalRule = "Huwag higpitan ang tali sa tangkay upang hindi masakal ang halaman."
            )
        }
        "watering" -> {
            DirectCropSolution(
                title = "Tamang Iskedyul ng Pagdidilig para sa $cropName",
                tagalogTitle = "Pagpapanatili ng Tamang Halumigmig sa Ugat",
                severity = "Pangangalaga (Irigasyon)",
                badgeColor = LushGreen,
                directAnswer = "Diligan ang $cropName sa umaga (6:00–7:30 AM) direkta sa ugat at maglagay ng organic mulch (dayami) upang hindi mabilis matuyo ang lupa.",
                actionSteps = listOf(
                    "Magdilig sa umaga bago uminit ang araw.",
                    "Maglagay ng 3–5cm na mulch ng dayami.",
                    "Suriin ang lalim ng lupa; kung basa pa ang unang 2 pulgada, huwag munang diligan."
                ),
                materialsNeeded = listOf("Regadera", "Dayami"),
                criticalRule = "Iwasang mabasa ang mga dahon sa gabi."
            )
        }
        "fertilizer" -> {
            DirectCropSolution(
                title = "Balanseng Organikong Pataba para sa $cropName",
                tagalogTitle = "Pangkalahatang Sustansya at Pagpapataba",
                severity = "Pangangalaga",
                badgeColor = LushGreen,
                directAnswer = "Maglagay ng hinog na vermicompost at mag-drench ng diluted compost tea tuwing 7–10 araw para sa malusog na pagtubo ng $cropName.",
                actionSteps = listOf(
                    "Maglagay ng 1 dakot ng vermicompost sa paligid ng puno.",
                    "Diligan pagkatapos maglagay ng abono.",
                    "Panatilihing may mulch ng dayami."
                ),
                materialsNeeded = listOf("Vermicompost", "Compost tea", "Dayami"),
                criticalRule = "Huwag maglagay ng sariwang dumi ng hayop na hindi pa nabulok."
            )
        }
        "pests" -> {
            DirectCropSolution(
                title = "Pangkalahatang Organikong Pest Barrier para sa $cropName",
                tagalogTitle = "Proteksyon Laban sa mga Peste at Uod",
                severity = "Pangangalaga",
                badgeColor = LushGreen,
                directAnswer = "Mag-spray ng chili-garlic extract tuwing hapon at magbudbod ng kahoy na abo sa mga dahon kapag may hamog sa umaga.",
                actionSteps = listOf(
                    "I-spray ang chili-garlic extract tuwing 4 na araw sa hapon.",
                    "Magbudbod ng manipis na kahoy na abo kapag may hamog sa umaga.",
                    "Manwal na alisin ang mga nakikitang malalaking uod."
                ),
                materialsNeeded = listOf("Kahoy na abo", "Chili spray", "Sipit"),
                criticalRule = "Huwag mag-spray sa tirik ng araw."
            )
        }
        "disease" -> {
            DirectCropSolution(
                title = "Organikong Proteksyon Laban sa Sakit ng $cropName",
                tagalogTitle = "Pagpapanatili ng Malinis at Ligtas na Taniman",
                severity = "Pangangalaga sa Dahon",
                badgeColor = LushGreen,
                directAnswer = "Panatilihing tuyo ang mga dahon, gupitin ang lumang dahon sa ibaba, at mag-spray ng mild baking soda solution tuwing umaga.",
                actionSteps = listOf(
                    "Diligan lamang ang base sa ugat.",
                    "Gupitin ang mga dahon sa ibabang 20cm.",
                    "Maglagay ng mulch laban sa rain-splash."
                ),
                materialsNeeded = listOf("Gunting", "Dayami", "Regadera"),
                criticalRule = "Huwag mag-spray habang basa pa ang dahon."
            )
        }
        "pruning" -> {
            DirectCropSolution(
                title = "Wastong Pruning para sa $cropName",
                tagalogTitle = "Pagpapabuti ng Sirkulasyon ng Hangin",
                severity = "Pangangalaga (Pruning)",
                badgeColor = LushGreen,
                directAnswer = "Alisin ang mga lumang dahon at suwi sa ibaba upang magkaroon ng sikat ng araw sa loob ng puno.",
                actionSteps = listOf(
                    "Gupitin ang mga dahon sa ibabang 20–30cm mula sa lupa.",
                    "Gawin ang pruning sa umaga kapag maaraw.",
                    "I-sanitize ang gunting gamit ang 70% alcohol."
                ),
                materialsNeeded = listOf("Pruning shears", "70% alcohol"),
                criticalRule = "Huwag mag-prune kapag maulan o basa ang dahon."
            )
        }
        "harvest" -> {
            DirectCropSolution(
                title = "Tamang Yugto ng Pag-aani ng $cropName",
                tagalogTitle = "Gabay sa Pitas at Pag-iingat",
                severity = "Pag-aani (Harvest Time)",
                badgeColor = LushGreen,
                directAnswer = "Pitasin ang $cropName sa umaga (6:00–8:00 AM) bago uminit ang araw gamit ang matalas na shears at itabi sa malilim at malamig na lugar.",
                actionSteps = listOf(
                    "Pitasin sa umaga bago uminit ang araw.",
                    "Gumamit ng matalas na gunting o pruner.",
                    "Ilagay sa malinis at may bentilasyong lalagyan."
                ),
                materialsNeeded = listOf("Pruning shears", "Woven basket"),
                criticalRule = "Huwag ibilad sa direktang sikat ng araw matapos pitasin."
            )
        }
        else -> null
    }
}
