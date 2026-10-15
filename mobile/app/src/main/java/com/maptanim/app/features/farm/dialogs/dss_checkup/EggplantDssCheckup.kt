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

fun getEggplantCheckUpActivities(): List<CheckUpActivity> {
    return listOf(
        CheckUpActivity(
            id = "staking",
            title = "🪵 Pagtutulos at Suporta (Bamboo Staking)",
            tagalogTitle = "Single-Stake Support Laban sa Pagkakatumba",
            icon = Icons.Default.Eco,
            description = "Pagsusuri sa tindig ng puno ng talong gamit ang 1-metrong tulos na kawayan upang hindi lumapat ang mabibigat na bunga sa mamasa-masang lupa.",
            options = listOf(
                CheckUpOption(
                    id = "fruit_rot_soil",
                    label = "Sumasayad ang mahahabang lilang bunga sa basang lupa (Phomopsis Risk)",
                    description = "Mabilis kapitan ng Phomopsis fruit rot at soil-borne pathogens kapag nakalapat ang dulo ng bunga sa lupa."
                ),
                CheckUpOption(
                    id = "falling_ground",
                    label = "Bumabagsak o natutumba ang punong talong dahil walang tulos",
                    description = "Kailangan ng single bamboo stake (1m taas mula sa lupa, ibinaon nang 20–30cm) na nakatali sa main stem."
                ),
                CheckUpOption(
                    id = "heavy_fruit",
                    label = "Masyadong mabigat ang kumpol ng mahahabang bunga (Fortuner/Casino F1)",
                    description = "Kailangan ng Figure-8 tie o horizontal cross-brace upang maipamahagi ang bigat at hindi mahati ang sanga."
                ),
                CheckUpOption(
                    id = "wind_storm",
                    label = "Nababali ang mga sanga ng talong dahil sa malalakas na hanging habagat",
                    description = "Kailangan ng mas matibay na tulos na kawayan at pag-aalis ng labis na lumang dahon para mabawasan ang wind resistance."
                )
            )
        ),
        CheckUpActivity(
            id = "watering",
            title = "💧 Pagdidilig at Drainage (Irrigation)",
            tagalogTitle = "Halumigmig at Pamamahala sa Tubig ng Talong",
            icon = Icons.Default.WaterDrop,
            description = "Pagsusuri sa moisture ng root zone. Ang talong ay nangangailangan ng 2–3 beses na dilig bawat linggo ngunit labis na sensitibo sa baha.",
            options = listOf(
                CheckUpOption(
                    id = "cracked_dry",
                    label = "Bitak-bitak ang lupa at mabilis sumingaw ang tubig sa paligid ng puno",
                    description = "Tuyot ang lupa; kailangan ng 1.5–2L dilig sa madaling-araw at 5cm dayami mulch."
                ),
                CheckUpOption(
                    id = "midday_wilt",
                    label = "Nalanta ang malalapad na dahon sa katanghalian ngunit bumabangon sa hapon",
                    description = "Mabilis na transpiration sa malalapad na dahon dahil sa init; senyales ng water stress sa root zone."
                ),
                CheckUpOption(
                    id = "waterlogged",
                    label = "Nalulunod o may nakatambak na tubig sa kanal matapos ang ulan (Bacterial Wilt Risk)",
                    description = "Kulang sa drainage; ang pagkababad sa loob ng 24–48 oras ay mabilis magbunsod ng Ralstonia solanacearum."
                ),
                CheckUpOption(
                    id = "yellow_overwater",
                    label = "Naninilaw ang dahon kahit mamasa-masa pa ang lupa (Root Suffocation)",
                    description = "Nalulunod ang mga ugat ng talong dahil walang hangin (oxygen) sa binahang lupa."
                )
            )
        ),
        CheckUpActivity(
            id = "fertilizer",
            title = "🌱 Pataba at Nutrisyon (14-14-14, Urea & Potash)",
            tagalogTitle = "Sustansya para sa Matamis at Makintab na Bunga",
            icon = Icons.Default.Eco,
            description = "Pagsusuri sa balanseng pataba: 14-14-14 basal, urea side-dress sa 2–3 splits, at muriate of potash / FFJ para sa de-kalidad na bunga.",
            options = listOf(
                CheckUpOption(
                    id = "blossom_drop",
                    label = "Nalalagas ang mga lilang bulaklak bago pa maging bunga",
                    description = "Kakulangan sa phosphorus/potassium o matinding init; itigil ang urea at mag-apply ng Fermented Fruit Juice (FFJ)."
                ),
                CheckUpOption(
                    id = "pale_nitrogen",
                    label = "Maputla at naninilaw ang buong halaman / mabagal ang paglaki",
                    description = "Kakulangan sa Nitrogen; kailangan ng side-dress ng urea o organic Fermented Plant Juice (FPJ)."
                ),
                CheckUpOption(
                    id = "potassium_slow",
                    label = "Mabagal lumaki, maputla ang lilang kulay, o matamlay ang bunga",
                    description = "Kakulangan sa Potassium (K); kailangan ng wood ash o muriate of potash para sa makintab na kulay at tibay ng laman."
                ),
                CheckUpOption(
                    id = "excess_nitrogen",
                    label = "Masyadong madilim ang berdeng dahon at malalabay ngunit kakaunti ang bulaklak",
                    description = "Sobra sa Nitrogen; nagpaparami lamang ng dahon sa halip na magpataba ng bunga."
                )
            )
        ),
        CheckUpActivity(
            id = "pests",
            title = "🐛 Peste at Insekto (Fruit & Shoot Borer / Leafhoppers)",
            tagalogTitle = "Pamamahala sa Harabas at Luksong Kuto sa Talong",
            icon = Icons.Default.BugReport,
            description = "Pagkontrol sa Harabas sa Talbos at Bunga (Leucinodes orbonalis), Luksong Kuto (Amrasca biguttula), Spider Mites, at Flea Beetles.",
            options = listOf(
                CheckUpOption(
                    id = "fruit_borer",
                    label = "May uod sa loob ng bunga o lantang talbos (Fruit & Shoot Borer - Leucinodes)",
                    description = "Leucinodes orbonalis na bumubutas sa talbos at bunga; may butas na may dumi ng uod at nabubulok ang loob."
                ),
                CheckUpOption(
                    id = "leafhopper",
                    label = "Naninilaw at kulot ang gilid ng dahon / Hopperburn (Cotton Leafhopper)",
                    description = "Amrasca biguttula na sumisipsip sa ilalim ng dahon at nagdadala ng Little Leaf Phytoplasma."
                ),
                CheckUpOption(
                    id = "chewed_holes",
                    label = "Tadtad ng butas ang dahon (Talon-talon / Flea Beetle)",
                    description = "Maliliit na lumuluksong uwang na bumubutas sa mga murang dahon ng bagong lipat na punla."
                ),
                CheckUpOption(
                    id = "spider_mites",
                    label = "May pinong sapot at naninilaw na dahon (Pulang Hanip / Spider Mites)",
                    description = "Dumarami sa ilalim ng dahon kapag mainit at tuyot ang panahon."
                ),
                CheckUpOption(
                    id = "aphids_whitefly",
                    label = "May maliliit na insekto sa ilalim ng dahon (Aphids / Whiteflies)",
                    description = "Sumisipsip ng katas ng dahon at nagdudulot ng maitim na sooty mold."
                )
            )
        ),
        CheckUpActivity(
            id = "disease",
            title = "🍂 Sakit at Dahon (Bacterial Wilt & Phomopsis)",
            tagalogTitle = "Foliar at Soil-borne Pathogens sa Talong",
            icon = Icons.Default.Coronavirus,
            description = "Paggamot sa Lanta-Bakterya (Ralstonia), Phomopsis Fruit Rot & Blight, Little Leaf, at Alternaria Leaf Spot.",
            options = listOf(
                CheckUpOption(
                    id = "bacterial_wilt",
                    label = "Biglaang pagkalanta ng buong puno habang berde pa (Bacterial Wilt - Ralstonia)",
                    description = "Ralstonia solanacearum sa vascular bundle na naglalabas ng puting bacterial stream sa tubig."
                ),
                CheckUpOption(
                    id = "phomopsis_rot",
                    label = "Maitim at malambot na agnas sa bunga o sunog na batik sa dahon (Phomopsis Blight)",
                    description = "Phomopsis vexans fungal fruit rot na mabilis kumalat tuwing maulan o kapag basa ang dahon."
                ),
                CheckUpOption(
                    id = "little_leaf",
                    label = "Bansot at kumpol-kumpol na munting dahon na walang bunga (Little Leaf - Phytoplasma)",
                    description = "Phytoplasma infection na ikinakalat ng mga luksong kuto (leafhoppers)."
                ),
                CheckUpOption(
                    id = "early_blight",
                    label = "May pabilog at maitim na mantsa sa dahon (Alternaria Leaf Spot)",
                    description = "Concentric rings na may dilaw na paligid sa ibabang dahon."
                ),
                CheckUpOption(
                    id = "powdery_mildew",
                    label = "Mapuputing parang pulbos sa ibabaw ng dahon (Powdery Mildew)",
                    description = "Puting amag na bumabalot sa dahon at pumipigil sa photosynthesis."
                )
            )
        ),
        CheckUpActivity(
            id = "pruning",
            title = "✂️ Pruning at Ratooning (Training)",
            tagalogTitle = "Pagtatabas ng Talbos at Ratooning Method",
            icon = Icons.Default.ContentCut,
            description = "Pag-aalis ng lantang talbos, pag-aahit ng dahon sa unang 30cm, at Ratooning para sa pangalawang buong ani.",
            options = listOf(
                CheckUpOption(
                    id = "wilting_shoot",
                    label = "Gupitin ang lantang talbos na pinasukan ng uod (Shoot Borer Pruning)",
                    description = "Pagtatabas nang 5cm sa ilalim ng butas upang maputol ang uod bago lumipat sa bunga."
                ),
                CheckUpOption(
                    id = "ratooning",
                    label = "Paraan ng Ratooning (pagputol ng lumang puno 20–30cm para mamunga muli)",
                    description = "Pag-uupod sa matandang puno upang magpasibol ng bagong masisiglang suwi nang walang bagong binhi."
                ),
                CheckUpOption(
                    id = "bottom_leaves",
                    label = "Dahon sa ibaba (unang 30cm) ay sumasayad sa basang lupa",
                    description = "Pag-aalis ng lumang dahon upang maiwasan ang Phomopsis rain-splash blight."
                ),
                CheckUpOption(
                    id = "excess_suckers",
                    label = "Maraming suwi na tumutubo sa kili-kili ng sanga",
                    description = "Mga bagong sanga (suckers) sa ibaba na umagaw sa sustansya ng pangunahing puno."
                )
            )
        ),
        CheckUpActivity(
            id = "harvest",
            title = "🧺 Pag-aani (Harvesting)",
            tagalogTitle = "Gabay sa Pitas ng Talong",
            icon = Icons.Default.Agriculture,
            description = "Tamang yugto ng pagpitas sa makintab na lilang balat (glossy stage) gamit ang pruner na may 2–3 cm calyx stem.",
            options = listOf(
                CheckUpOption(
                    id = "harvest_timing",
                    label = "Paano malalaman kung handa na pitasin (Makintab na lilang balat / Thumb Test)",
                    description = "Pitasin habang makintab at madilim pa ang kulay bago magbutil at pumait."
                ),
                CheckUpOption(
                    id = "proper_picking",
                    label = "Tamang paraan ng paggupit gamit ang pruner na may 2–3 cm calyx",
                    description = "Huwag pilipitin o hilahin upang hindi mapunit ang balat o mabali ang sanga."
                )
            )
        )
    )
}

fun solveEggplantProblem(
    activityId: String,
    selectedOptions: Set<String>
): DirectCropSolution? {
    if (selectedOptions.isEmpty()) return null

    return when (activityId) {
        "staking" -> {
            when {
                selectedOptions.contains("fruit_rot_soil") || selectedOptions.contains("falling_ground") -> {
                    DirectCropSolution(
                        title = "Single Bamboo Stake Installation Laban sa Phomopsis at Soil Rot ng Talong",
                        tagalogTitle = "Ilayo ang Mahahabang Bunga sa Basang Lupa Gamit ang Tulos",
                        severity = "Agad na Gawin (Immediate Action)",
                        badgeColor = RedAlert,
                        directAnswer = "Ang pagkadikit ng dulo ng talong sa basang lupa ang pangunahing sanhi ng Phomopsis fruit rot. Kailangang itayo agad ang puno gamit ang tulos na kawayan (1 metrong taas mula sa lupa, ibinaon nang 20–30 cm sa tabi ng main stem) at talian nang maluwag na Figure-8 tie.",
                        actionSteps = listOf(
                            "Kumuha ng tulos na kawayan na may habang 1.2 hanggang 1.5 metro at patalasin ang ibaba.",
                            "Ibaon ang tulos nang 20–30 sentimetro ang lalim, may distansyang 3–5 pulgada mula sa puno upang hindi maputol ang pangunahing ugat.",
                            "Talian ang puno sa tulos gamit ang malambot na tali sa hugis '8' (Figure-8 tie). Mag-iwan ng 1–2 pulgadang luwag para sa pagkapal ng tangkay.",
                            "Gupitin gamit ang malinis na gunting ang lahat ng dahon na nasa unang 30cm mula sa ibabaw ng lupa upang hindi matalsikan ng ulan."
                        ),
                        materialsNeeded = listOf(
                            "Kawayan na may habang 1.2–1.5 metro (1 tulos bawat puno)",
                            "Malambot na tali (abaka, strips ng lumang tela, o yarn)",
                            "Matalas na shears para sa pruning ng ibabang dahon",
                            "Dayami (mulch) para sa paanan ng puno"
                        ),
                        criticalRule = "Huwag kailanman gumamit ng manipis na alambre o nylon na tali nang mahigpit dahil hihiwain nito ang semi-woody stem kapag nahanginan."
                    )
                }
                selectedOptions.contains("heavy_fruit") || selectedOptions.contains("wind_storm") -> {
                    DirectCropSolution(
                        title = "Double-Stake Suporta para sa Mabibigat na Bunga ng Talong",
                        tagalogTitle = "Pagpapatatag Laban sa Hanging Habagat at Nababaling Sanga",
                        severity = "Mataas na Prayoridad",
                        badgeColor = AmberAlert,
                        directAnswer = "Kapag marami at mahahaba ang bunga ng hybrid na talong (tulad ng Fortuner F1 o Casino F1), hindi na kaya ng iisang sanga ang bigat lalo na kapag may bugso ng hanging habagat. Maglagay ng pangalawang suportang tulos o horizontal bamboo cross-brace.",
                        actionSteps = listOf(
                            "Magbaon ng pangalawang suportang tulos sa kabilang gilid ng halaman kung saan nakalawit ang pinakamabibigat na sanga.",
                            "Magkabit ng horizontal na suportang patpat sa pagitan ng mga tulos upang suportahan ang mga sanga.",
                            "Dahan-dahang iangat ang mga sangang may bunga at isandal sa tali o tulos nang walang puwersa.",
                            "Maglagay ng makapal na mulch (tuyong dayami) sa base upang manatiling matatag ang ugat."
                        ),
                        materialsNeeded = listOf(
                            "2 pirasong patpat na kawayan bawat puno",
                            "Matibay na taling abaka o garden twine",
                            "Tuyong dayami (rice straw) para sa base"
                        ),
                        criticalRule = "Alisin ang mga dahong may mantsa bago magtali upang hindi mahawahan ang mga katabing sanga kapag nagkadikit-dikit."
                    )
                }
                else -> {
                    DirectCropSolution(
                        title = "Gabay sa Pagtutulos (Single-Stake) para sa Talong",
                        tagalogTitle = "Wastong Pamamaraan ng Pagpapatayo ng Puno",
                        severity = "Pangangalaga (Maintenance)",
                        badgeColor = LushGreen,
                        directAnswer = "Ang tamang pagtutulos ng talong ay nagpapataas ng ani ng hanggang 40% dahil mas nasisikatan ng araw ang lahat ng dahon, hindi nababali ang puno, at malinis ang sirkulasyon ng hangin laban sa amag.",
                        actionSteps = listOf(
                            "Ihanda ang tulos na kawayan bago pa mamulaklak ang tanim (2–3 linggo matapos ilipat-tanim).",
                            "Ibaon sa silangang bahagi ng halaman nang 20–30 cm ang lalim.",
                            "Gumawa ng Figure-8 tie tuwing bawat 20cm na pagtaas ng tangkay.",
                            "Panatilihing tuwid ang pangunahing tangkay."
                        ),
                        materialsNeeded = listOf("Kawayan na may habang 1.2–1.5m", "Taling abaka"),
                        criticalRule = "Magtali tuwing umaga habang malambot at hindi malutong ang mga tangkay."
                    )
                }
            }
        }
        "watering" -> {
            when {
                selectedOptions.contains("cracked_dry") || selectedOptions.contains("midday_wilt") -> {
                    DirectCropSolution(
                        title = "Deep Morning Furrow Irrigation at Mulching para sa Talong",
                        tagalogTitle = "Solusyon sa Tuyong Lupa at Heat Stress sa Malalapad na Dahon",
                        severity = "Agad na Gawin (Immediate Action)",
                        badgeColor = BlueAccent,
                        directAnswer = "Malalaki at malalapad ang dahon ng talong kaya malakas itong magsingaw ng tubig sa ilalim ng araw. Huwag magdilig sa tanghali! Magdilig ng 1.5–2 litro bawat puno sa madaling-araw (6:00 AM) at maglagay ng 5cm makapal na mulch upang mapigilan ang 70% ng water evaporation.",
                        actionSteps = listOf(
                            "Diligan nang dahan-dahan ang base o tudling (furrow) ng talong sa madaling-araw hanggang lumambot ang unang 15 sentimetro ng lupa.",
                            "Maglagay agad ng 5 sentimetrong kapal ng tuyong dayami (rice straw) sa ibabaw ng kama, 2 pulgada ang layo sa mismong puno.",
                            "Kung may daytime wilting sa tanghaling tapat (>35°C), panatilihing basa ang root zone sa pamamagitan ng banayad na pandilig sa hapon (4:30 PM).",
                            "Ulitin ang pagdidilig 2–3 beses sa isang linggo depende sa panahon."
                        ),
                        materialsNeeded = listOf(
                            "Dayami (rice straw) para sa mulch",
                            "Regadera o furrow irrigation hose",
                            "30% temporary shade net kung matindi ang tag-init"
                        ),
                        criticalRule = "Huwag kailanman magbuhos ng tubig sa mga bulaklak sa tanghali dahil mahuhugasan ang polen at malalagas ang bulaklak."
                    )
                }
                selectedOptions.contains("waterlogged") || selectedOptions.contains("yellow_overwater") -> {
                    DirectCropSolution(
                        title = "Drainage Canal Excavation at Pagpigil sa Bacterial Wilt ng Talong",
                        tagalogTitle = "Solusyon sa Binahang Kamada at Nalulunod na Ugat",
                        severity = "Kritikal (Critical Action)",
                        badgeColor = RedAlert,
                        directAnswer = "Ang talong ay labis na sensitibo sa baha. Ang pagkababad sa nakatambak na tubig sa loob ng 24–48 oras ay nagiging sanhi ng root suffocation at mabilis na pagkalat ng Bacterial Wilt (Ralstonia solanacearum). Kailangang gumawa agad ng malalim na kanal.",
                        actionSteps = listOf(
                            "Agad maghukay ng 20–30 cm kanal (drainage furrow) sa pagitan ng mga kamada upang mabilis na maubos ang nakatambak na tubig.",
                            "Itigil ang pagdidilig sa susunod na 3 hanggang 5 araw hanggang sa mahanginan ang lupa.",
                            "Magbudbod ng pinong kahoy na abo (wood ash) o agricultural lime sa base upang patayin ang fungal at bacterial spores.",
                            "Luwagan nang bahagya ang ibabaw ng lupa gamit ang trowel para makapasok ang oxygen sa mga ugat."
                        ),
                        materialsNeeded = listOf(
                            "Pala para sa pagpapalalim ng kanal",
                            "Kahoy na abo o agricultural lime",
                            "Hand trowel"
                        ),
                        criticalRule = "Huwag maglagay ng abono habang waterlogged ang lupa dahil masusunog ang nanghihinang mga ugat."
                    )
                }
                else -> {
                    DirectCropSolution(
                        title = "Tamang Iskedyul ng Pagdidilig para sa Talong",
                        tagalogTitle = "Pagpapanatili ng Balanseng Halumigmig sa Ugat",
                        severity = "Pangangalaga (Irigasyon)",
                        badgeColor = LushGreen,
                        directAnswer = "Ang talong ay nangangailangan ng 1.5 hanggang 2 litro ng tubig bawat puno 2–3 beses bawat linggo. Diligan lamang sa ugat tuwing madaling-araw upang maiwasan ang foliar diseases.",
                        actionSteps = listOf(
                            "Magdilig sa pagitan ng 6:00 AM at 7:30 AM direkta sa base o furrow.",
                            "Maglagay ng 3–5cm na organic mulch (dayami) upang mapanatiling mamasa-masa ang lupa.",
                            "Suriin ang lupa: kung basa pa ang unang 2 pulgada, huwag munang diligan."
                        ),
                        materialsNeeded = listOf("Regadera", "Dayami para sa mulch"),
                        criticalRule = "Iwasang mabasa ang mga dahon sa gabi upang hindi tubuan ng Phomopsis blight."
                    )
                }
            }
        }
        "fertilizer" -> {
            when {
                selectedOptions.contains("blossom_drop") || selectedOptions.contains("excess_nitrogen") -> {
                    DirectCropSolution(
                        title = "Bloom Booster at Pagpigil sa Flower Drop ng Talong",
                        tagalogTitle = "Lunas sa Paglaglag ng Bulaklak at Sobrang Dahon",
                        severity = "Mataas na Prayoridad",
                        badgeColor = AmberAlert,
                        directAnswer = "Ang paglaglag ng bulaklak at sobrang kapal ng dahon ay senyales ng labis na Nitrogen (urea) at kakulangan sa Phosphorus/Potassium. Itigil ang urea at mag-apply ng Fermented Fruit Juice (FFJ mula sa saging) at abo.",
                        actionSteps = listOf(
                            "Itigil ang anumang pataba na mataas sa Nitrogen.",
                            "Mag-apply ng Fermented Fruit Juice (FFJ): 2 kutsara sa 1L tubig drench sa lupa tuwing 7 araw.",
                            "Magbudbod ng 2 kutsarang kahoy na abo sa paligid ng puno para sa natural na Potassium.",
                            "Iwasan ang pagbuhos ng tubig nang direkta sa mga bukas na bulaklak."
                        ),
                        materialsNeeded = listOf("Fermented Fruit Juice (FFJ)", "Kahoy na abo", "Tubig na walang chlorine"),
                        criticalRule = "Huwag gumamit ng malakas na sprinkler habang namumulaklak ang talong."
                    )
                }
                selectedOptions.contains("pale_nitrogen") -> {
                    DirectCropSolution(
                        title = "Organikong Nitrogen Drench para sa Naninilaw na Talong",
                        tagalogTitle = "Pagpapasigla sa Mapuputlang Dahon at Mabagal na Pagtubo",
                        severity = "Kailangan ng Sustansya",
                        badgeColor = LushGreen,
                        directAnswer = "Ang pamumutla at mabagal na paglaki ng talong ay kakulangan sa Nitrogen. Mag-side dress ng decomposed vermicompost o drench ng Fermented Plant Juice (FPJ).",
                        actionSteps = listOf(
                            "Maghalo ng 2 kutsarang Fermented Plant Juice (FPJ) o Vermitea sa 1L tubig.",
                            "Idilig sa paligid ng ugat sa umaga tuwing 5 araw hanggang maging malalim na berde ang dahon.",
                            "Maglagay ng 2 dakot ng matandang vermicompost sa paligid ng puno.",
                            "Tiyaking hindi labis ang dilig upang hindi maanod ang sustansya."
                        ),
                        materialsNeeded = listOf("FPJ o Vermitea", "Vermicompost", "Regadera"),
                        criticalRule = "Huwag gumamit ng sariwang dumi ng manok na hindi pa bulok dahil susunugin nito ang mga ugat."
                    )
                }
                selectedOptions.contains("potassium_slow") -> {
                    DirectCropSolution(
                        title = "Potassium Drench para sa Makintab at Malalaking Talong",
                        tagalogTitle = "Solusyon sa Mabagal na Paglaki at Matamlay na Lilang Bunga",
                        severity = "Kailangan ng Sustansya (Potassium)",
                        badgeColor = AmberAlert,
                        directAnswer = "Ang kakulangan sa Potassium (K) ay nagreresulta sa mapusyaw na kulay, mabagal na paglaki, at matabang na laman. Maglagay ng muriate of potash o drench ng Fermented Fruit Juice (FFJ) at pinong kahoy na abo.",
                        actionSteps = listOf(
                            "Maghalo ng 2 kutsarang FFJ (mula sa saging o papaya) sa 1 litro ng tubig at idilig sa ugat tuwing 5 araw.",
                            "Magbudbod ng 1 hanggang 2 kutsarang pinong kahoy na abo sa paligid ng drip line.",
                            "Maglagay ng 1 kutsaritang muriate of potash (0-0-60) sa paligid kung commercial hybrid ang gamit.",
                            "Itigil ang pataba na mataas sa Nitrogen."
                        ),
                        materialsNeeded = listOf("FFJ", "Pinong kahoy na abo", "Regadera"),
                        criticalRule = "Huwag lumampas sa 2 kutsarang abo bawat metro kuwadrado upang mapanatili ang pH ng lupa."
                    )
                }
                else -> {
                    DirectCropSolution(
                        title = "Balanseng Pataba para sa Masaganang Talong (14-14-14 at Kompos)",
                        tagalogTitle = "Pangkalahatang Sustansya sa bawat Yugto ng Talong",
                        severity = "Pangangalaga",
                        badgeColor = LushGreen,
                        directAnswer = "Maglagay ng Complete Fertilizer (14-14-14) o hinog na vermicompost tuwing 2–3 linggo sa paligid ng puno upang mapanatili ang tuloy-tuloy na pamumunga sa loob ng 6–9 na buwan.",
                        actionSteps = listOf(
                            "Maglagay ng 1 dakot ng vermicompost at 1 kutsaritang complete fertilizer sa band o ring 10cm mula sa puno.",
                            "Tabunan ng manipis na lupa (light hilling-up) at diligan agad.",
                            "Panatilihing may 3–5cm na dayami mulch."
                        ),
                        materialsNeeded = listOf("Complete fertilizer 14-14-14 o vermicompost", "Dayami"),
                        criticalRule = "Huwag ilagay ang abono nang nakadikit mismo sa balat ng tangkay."
                    )
                }
            }
        }
        "pests" -> {
            when {
                selectedOptions.contains("fruit_borer") || selectedOptions.contains("wilting_shoot") -> {
                    DirectCropSolution(
                        title = "Puksa sa Fruit & Shoot Borer (Leucinodes orbonalis) ng Talong",
                        tagalogTitle = "Solusyon sa Uod sa Bunga at Lantang Talbos",
                        severity = "Agad na Gawin (Immediate Pest Action)",
                        badgeColor = RedAlert,
                        directAnswer = "Ang Fruit and Shoot Borer (Leucinodes orbonalis) ang pangunahing peste ng talong sa Pilipinas. Ang uod ay sumusuot sa loob ng talbos na nagdudulot ng pagyuko at pagkalanta, bago lumipat sa loob ng bunga. Ang solusyon ay regular na pagtatabas ng lantang talbos bago lumabas ang uod, pag-aalis ng inuod na bunga, at pag-spray ng Chili-Garlic-Neem tuwing hapon.",
                        actionSteps = listOf(
                            "Matahang inspeksyunin ang bawat tanim: Putulin ang lantang talbos nang 5 sentimetro sa ILALIM ng butas upang masigurong kasamang maputol ang uod sa loob.",
                            "Agad pitasin ang lahat ng bungang may butas o dumi ng uod. Ibaon ang mga ito sa lupa (lalim na 30cm) o ilubog sa tubig na may sabon upang mamatay ang uod.",
                            "Magbomba ng Chili-Garlic-Neem Spray: 10 siling labuyo + 1 buong ulo ng bawang + 1 kutsaritang sabon at 1 kutsaritang neem oil sa 1L tubig tuwing alas-4:30 ng hapon.",
                            "Maglagay ng pheromone traps o yellow sticky traps sa itaas ng taniman upang mahuli ang mga matatandang paruparo bago makapangitlog sa bulaklak."
                        ),
                        materialsNeeded = listOf(
                            "Matalas na pruning shears para sa talbos",
                            "Siling labuyo at bawang para sa spray extract",
                            "Neem oil at mild liquid soap",
                            "Timba na may tubig at sabon para sa itinapong uod"
                        ),
                        criticalRule = "Huwag kailanman itapon ang mga inuod na talbos o bunga sa ibabaw ng compost heap dahil magiging paruparo muli ang mga uod at babalik sa taniman."
                    )
                }
                selectedOptions.contains("leafhopper") -> {
                    DirectCropSolution(
                        title = "Pagkontrol sa Luksong Kuto / Cotton Leafhopper (Amrasca biguttula)",
                        tagalogTitle = "Lunas sa Hopperburn at Naninilaw na Gilid ng Dahon",
                        severity = "Mataas na Prayoridad",
                        badgeColor = AmberAlert,
                        directAnswer = "Ang Cotton Leafhopper (Amrasca biguttula) ay sumisipsip ng katas sa ilalim ng dahon ng talong at naglalabas ng lason na nagdudulot ng 'hopperburn' (naninilaw at natutuyong gilid ng dahon). Sila rin ang nagkakalat ng nakamamatay na Little Leaf Phytoplasma.",
                        actionSteps = listOf(
                            "Maghalo ng 1 kutsarang Pure Neem Oil at 1/2 kutsaritang liquid dish soap sa 1 litro ng maligamgam na tubig.",
                            "I-spray nang masinsin DIREKTA sa ILALIM ng mga dahon kung saan mabilis lumukso at magtago ang mga leafhoppers tuwing alas-4:30 ng hapon.",
                            "Magkabit ng Yellow Sticky Traps sa taas ng tanim (pantay sa talbos) upang mahuli ang mga lumuluksong kulisap.",
                            "Magtanim ng repellent companion crops tulad ng marigold (amarillo), sibuyas, o basil sa paligid ng mga kamada ng talong."
                        ),
                        materialsNeeded = listOf(
                            "Cold-pressed neem oil",
                            "Mild liquid soap bilang emulsifier",
                            "Dilaw na sticky insect traps",
                            "Sprayer na may adjustable fine mist nozzle"
                        ),
                        criticalRule = "I-spray sa ilalim ng dahon sa madaling araw o dapithapon. Huwag mag-spray sa tanghaling tapat upang hindi masunog ang dahon."
                    )
                }
                selectedOptions.contains("spider_mites") -> {
                    DirectCropSolution(
                        title = "Puksa sa Pulang Hanip (Spider Mites) ng Talong",
                        tagalogTitle = "Solusyon sa Pinong Sapot at Naninilaw na Dahon",
                        severity = "Mataas na Prayoridad",
                        badgeColor = AmberAlert,
                        directAnswer = "Ang Pulang Hanip (Spider Mites) ay mabilis dumami kapag tuyot at mainit ang panahon. Sumisipsip sila sa ilalim ng dahon at nag-iiwan ng pinong sapot at bronzing o paninilaw sa dahon ng talong.",
                        actionSteps = listOf(
                            "Bomba ng malakas na wisik ng tubig (water jet) sa ilalim ng mga dahon tuwing umaga upang masira ang mga sapot at mahugasan ang mga mites.",
                            "Mag-spray ng Neem Oil (1 kutsara sa 1L tubig na may sabon) o sulfur spray sa ilalim ng dahon tuwing 4 na araw.",
                            "Panatilihing mamasa-masa ang lupa at maglagay ng mulch upang mabawasan ang init at alikabok na pabor sa pagdami ng mites.",
                            "Alisin at sunugin ang mga pinakamatinding apektadong lumang dahon sa ibaba."
                        ),
                        materialsNeeded = listOf("Neem oil", "Liquid soap", "Sprayer na may malakas na spray setting", "Mulch (dayami)"),
                        criticalRule = "Iwasan ang paggamit ng malalakas na chemical insecticides dahil pinapatay nito ang natural predators na kumakain sa spider mites."
                    )
                }
                selectedOptions.contains("chewed_holes") -> {
                    DirectCropSolution(
                        title = "Organikong Solusyon Laban sa Talon-talon (Flea Beetles) sa Talong",
                        tagalogTitle = "Proteksyon sa Tadtad ng Butas na Dahon ng Punla",
                        severity = "Mataas na Prayoridad",
                        badgeColor = AmberAlert,
                        directAnswer = "Ang Flea Beetles (Talon-talon) ay maliliit na maitim o makintab na uwang na lumulukso at kumakain sa dahon ng talong na parang pinitpit ng bala ng baril (shot holes). Lalo silang mapaminsala sa mga bagong lipat na punla.",
                        actionSteps = listOf(
                            "Magbudbod ng pinong kahoy na abo (wood ash) sa mga dahon habang may hamog sa madaling-araw upang hindi makakain ang mga uwang.",
                            "Mag-spray ng Chili-Garlic Spray na may halong neem oil tuwing 3 hanggang 4 na araw sa hapon.",
                            "Maglagay ng dilaw o puting sticky sheets malapit sa paanan ng tanim upang madikit ang mga lumuluksong insekto.",
                            "Regular na bunutin ang mga damo sa paligid ng plot na pinamumugaran ng flea beetles."
                        ),
                        materialsNeeded = listOf("Pinong kahoy na abo (wood ash)", "Siling labuyo at bawang extract", "Sticky traps"),
                        criticalRule = "Manipis na budbod ng abo lamang ang kailangan upang hindi magbara ang stomata ng dahon."
                    )
                }
                else -> {
                    DirectCropSolution(
                        title = "Pangkalahatang Organikong Proteksyon sa Peste ng Talong",
                        tagalogTitle = "Proteksyon Laban sa Butas-butas na Dahon at Uwang",
                        severity = "Pangangalaga",
                        badgeColor = LushGreen,
                        directAnswer = "Mag-spray ng chili-garlic-neem extract at magbudbod ng wood ash kapag may hamog sa umaga para mapanatiling ligtas ang talong laban sa mga insekto.",
                        actionSteps = listOf(
                            "I-spray ang chili-garlic extract tuwing 4 na araw sa hapon.",
                            "Magbudbod ng manipis na kahoy na abo kapag may hamog sa umaga.",
                            "Manwal na alisin ang mga nakikitang malalaking uod."
                        ),
                        materialsNeeded = listOf("Kahoy na abo", "Chili spray", "Sipit"),
                        criticalRule = "Huwag magbudbod ng labis na abo sa basang-basa na dahon."
                    )
                }
            }
        }
        "disease" -> {
            when {
                selectedOptions.contains("bacterial_wilt") -> {
                    DirectCropSolution(
                        title = "Emergency Quarantine sa Lanta-Bakterya (Ralstonia) ng Talong",
                        tagalogTitle = "Kritikal: Biglaang Pagkalanta ng Buong Puno Habang Berde Pa",
                        severity = "Kritikal (Bacterial Wilt)",
                        badgeColor = RedAlert,
                        directAnswer = "Ang Bacterial Wilt (Ralstonia solanacearum) ay soil-borne na bacteria. Subukan ang 'Bacterial Streaming Test': Putulin ang tangkay at ilubog sa baso ng malinis na tubig; kapag may lumabas na puting mala-usok na bacterial stream, positibo itong Bacterial Wilt. Walang kemikal na gamot kapag apektado na; kailangang bunutin agad.",
                        actionSteps = listOf(
                            "Agad bunutin ang apektadong puno kasama ang buong ugat at lupang nakadikit dito.",
                            "Ibalot sa sako o plastic at sunugin o itapon sa malayo. HUWAG isama sa compost!",
                            "Ibuhos ang kumukulong tubig o lagyan ng agricultural lime (apog) ang hukay kung saan binunot ang tanim upang mamatay ang bacteria.",
                            "Huwag itaniman ng kamatis, talong, o sili ang parehong hukay sa susunod na 1–2 taon (magtanim ng mais o pechay sa halip)."
                        ),
                        materialsNeeded = listOf(
                            "Baso ng tubig para sa bacterial streaming test",
                            "Plastic bag para sa quarantine",
                            "Agricultural lime (apog) o kumukulong tubig",
                            "Pala na disinfected gamit ang alcohol"
                        ),
                        criticalRule = "I-sanitize ang gunting o kamay gamit ang 70% alcohol bago humawak ng ibang malulusog na halaman."
                    )
                }
                selectedOptions.contains("phomopsis_rot") -> {
                    DirectCropSolution(
                        title = "Lunas sa Phomopsis Blight at Fruit Rot (Phomopsis vexans) ng Talong",
                        tagalogTitle = "Kritikal: Maitim na Agnas sa Bunga at Sunog na Batik sa Dahon",
                        severity = "Kritikal (Phomopsis Blight)",
                        badgeColor = RedAlert,
                        directAnswer = "Ang Phomopsis Fruit Rot (Phomopsis vexans) ay ang pinakamalalang fungal disease sa bunga ng talong. Nagdudulot ito ng malambot, lumalubog, at maitim na agnas sa bunga na may pabilog na tuldok ng fungal pycnidia, lalo na kapag maulan.",
                        actionSteps = listOf(
                            "Agad pitasin at sunugin o ibaon ang lahat ng bungang may maitim na agnas. Huwag itapon sa lupa o compost upang hindi kumalat ang spores.",
                            "Gupitin ang lahat ng dahon sa ibabang 30cm ng puno upang hindi matalsikan ng tubig-ulan mula sa lupa (rain-splash barrier).",
                            "Maglagay ng 5cm mulch na tuyong dayami upang takpan ang lupa laban sa talsik ng ulan.",
                            "Mag-spray ng Organic Fungicide: 1 kutsaritang Baking Soda + 1 kutsaritang langis ng niyog + 1/2 kutsaritang sabon sa 1L tubig. I-spray sa buong halaman tuwing 5 araw sa umaga matapos ang ulan.",
                            "Itali ang matataas na sanga sa tulos upang hindi lumaylay ang bunga malapit sa basang lupa."
                        ),
                        materialsNeeded = listOf(
                            "Baking soda (Sodium bicarbonate)",
                            "Langis ng niyog o vegetable oil",
                            "Matalas na gunting para sa pruning",
                            "Tuyong dayami (rice straw mulch)"
                        ),
                        criticalRule = "Huwag diligan ng overhead sprinkler ang talong kapag may banta ng Phomopsis; magdilig lamang nang direkta sa paanan ng ugat."
                    )
                }
                selectedOptions.contains("little_leaf") -> {
                    DirectCropSolution(
                        title = "Emergency Quarantine sa Little Leaf (Phytoplasma) ng Talong",
                        tagalogTitle = "Solusyon sa Bansot at Kumpol-kumpol na Munting Dahon",
                        severity = "Kritikal (Phytoplasma Infection)",
                        badgeColor = RedAlert,
                        directAnswer = "Ang Little Leaf ay sanhi ng Phytoplasma na ikinakalat ng Luksong Kuto (Cotton Leafhopper). Ang apektadong talong ay nagiging parang 'witches broom'—napakaliit at mapuputlang kumpol ng dahon, nagiging berde ang bulaklak (phyllody), at tuluyang hindi namumunga. Walang gamot kapag nahawa na ang puno.",
                        actionSteps = listOf(
                            "Agad bunutin ang apektadong puno kasama ang buong ugat, ibalot sa sako o plastic, at sunugin sa labas ng taniman.",
                            "Mag-spray agad ng Neem Oil at Chili Extract sa lahat ng natitirang malulusog na tanim upang patayin ang mga luksong kuto (leafhoppers) na nagkakalat ng sakit.",
                            "Magkabit ng yellow sticky traps sa bawat kamada upang mahuli ang natitirang mga vector.",
                            "Huwag kumuha ng binhi mula sa tanimang may Little Leaf."
                        ),
                        materialsNeeded = listOf(
                            "Plastic bag o sako para sa quarantine",
                            "Pala para sa pagbunot ng ugat",
                            "Neem oil spray para sa leafhoppers",
                            "Yellow sticky insect traps"
                        ),
                        criticalRule = "Huwag iwanan ang apektadong puno sa pag-asang gagaling ito dahil patuloy itong sisipsipan ng mga leafhopper at ikakalat ang sakit sa buong bukirin."
                    )
                }
                else -> {
                    DirectCropSolution(
                        title = "Organikong Proteksyon Laban sa Amag ng Talong",
                        tagalogTitle = "Pagpapanatili ng Malinis at Ligtas na Taniman",
                        severity = "Pangangalaga sa Dahon",
                        badgeColor = LushGreen,
                        directAnswer = "Panatilihing tuyo ang mga dahon at mag-spray ng mild baking soda o diluted milk solution tuwing umaga para maprotektahan ang talong sa foliar diseases.",
                        actionSteps = listOf(
                            "Diligan lamang ang base sa ugat at iwasang mabasa ang mga dahon.",
                            "Gupitin ang mga lumang dahon sa ibabang 20–30 cm.",
                            "Maglagay ng mulch (tuyong dayami) upang hindi tumalsik ang lupa sa dahon kapag umulan."
                        ),
                        materialsNeeded = listOf("Gunting", "Dayami", "Regadera"),
                        criticalRule = "Huwag mag-spray habang basa pa ang dahon ng ulan o hamog."
                    )
                }
            }
        }
        "pruning" -> {
            when {
                selectedOptions.contains("ratooning") -> {
                    DirectCropSolution(
                        title = "Teknik ng Ratooning (Pag-uupod) para sa Pangalawang Ani ng Talong",
                        tagalogTitle = "Pabataing Muli ang Matandang Puno Nang Walang Bagong Binhi",
                        severity = "Espesyal na Pamamaraan (Ratooning)",
                        badgeColor = LushGreen,
                        directAnswer = "Ang Ratooning ay subok na paraan ng mga magsasakang Pilipino upang makakuha ng panibagong buong cycle ng ani mula sa 7–9 buwang gulang na talong nang hindi na kailangang magpunla muli. Sa pamamagitan ng pagputol ng matandang tangkay sa taas na 20–30 sentimetro sa 45-degree angle, sisibol ang 2 hanggang 3 masisiglang bagong sanga na magbubunga muli sa loob lamang ng 45 hanggang 60 araw.",
                        actionSteps = listOf(
                            "Ihanda ang matalas na pruning saw o gunting. I-disinfect ito gamit ang 70% alcohol bago gamitin.",
                            "Putulin ang pangunahing tangkay ng talong nang pahilig (45-degree angle) sa taas na 20 hanggang 30 sentimetro mula sa lupa, sa ibabaw ng malusog na node o buko.",
                            "Pahiran ng paste ng abo ng kahoy o bordeaux mixture ang pinutol na dulo upang maiwasan ang impeksyon ng amag at mabulok ang tangkay.",
                            "Bungkalin nang marahan ang paligid ng puno, maglagay ng 2 dakot ng matandang vermicompost at 1 kutsarang complete fertilizer (14-14-14), at diligan nang sagana.",
                            "Kapag sumibol ang maraming bagong suwi matapos ang 2 linggo, magtira lamang ng 2 hanggang 3 pinakamalalakas na sanga at alisin ang maliliit upang lumaki ang bunga."
                        ),
                        materialsNeeded = listOf(
                            "Matalas na pruning saw o pruning shears",
                            "70% rubbing alcohol para sa sanitasyon",
                            "Pinong abo ng kahoy para sa sugat ng puno",
                            "Vermicompost o organikong pataba",
                            "Tulos na kawayan para sa mga bagong suwi"
                        ),
                        criticalRule = "Gawin lamang ang ratooning sa mga punong walang bacterial wilt o virus. Huwag mag-ratoon kapag kasagsagan ng malakas na bagyo upang hindi mababad sa tubig ang sariwang sugat."
                    )
                }
                selectedOptions.contains("wilting_shoot") -> {
                    DirectCropSolution(
                        title = "Sanitary Pruning Laban sa Shoot Borer sa Talbos ng Talong",
                        tagalogTitle = "Pagtatabas ng Lantang Talbos Bago Lumipat ang Uod sa Bunga",
                        severity = "Agad na Gawin (Pruning Sanitation)",
                        badgeColor = RedAlert,
                        directAnswer = "Kapag may nakitang yumuyukod at lantang talbos ng talong, 100% na may uod ng Fruit & Shoot Borer sa loob ng stem. Kung hindi puputulin agad, kakainin nito ang loob ng sanga hanggang sa lumipat ito sa mga bulaklak at bunga.",
                        actionSteps = listOf(
                            "Hanapin ang maliit na butas na may maitim na dumi (frass) sa gilid ng lantang tangkay.",
                            "Gupitin ang tangkay gamit ang pruner nang hindi bababa sa 5 sentimetro sa ILALIM ng butas upang masiguro na kasama ang uod sa naputol.",
                            "Buksan ang naputol na talbos upang tiyaking nakuha ang uod, at agad ilubog sa tubig na may sabon o sunugin.",
                            "Huwag iwanan sa lupa ang naputol na talbos.",
                            "Mag-spray ng Chili-Garlic extract o Neem oil sa mga bagong talbos upang hindi na muling itlogan ng paruparo."
                        ),
                        materialsNeeded = listOf(
                            "Matalas na pruning shears",
                            "Timba na may tubig at detergent sabon",
                            "Chili-Garlic-Neem spray"
                        ),
                        criticalRule = "Huwag lamang pitasin ang dahon; dapat putulin ang buong bahagi ng tangkay kung saan naroroon ang uod."
                    )
                }
                selectedOptions.contains("bottom_leaves") -> {
                    DirectCropSolution(
                        title = "Pagbabawas ng Dahon sa Ibaba (Bottom 30cm) ng Talong",
                        tagalogTitle = "Pagpapabuti ng Sirkulasyon at Pag-iwas sa Phomopsis Rain-Splash",
                        severity = "Pangangalaga (Pruning)",
                        badgeColor = LushGreen,
                        directAnswer = "Ang mga dahon na sumasayad sa lupa sa unang 30cm ng puno ay laging basa at madaling dapuan ng Phomopsis kapag natalsikan ng ulan o dilig. Ang pag-aahit sa ibaba ay nagpapanatili ng malinis at tuyong puno.",
                        actionSteps = listOf(
                            "Gupitin ang lahat ng dahon at maliliit na suwi na nasa unang 30 sentimetro mula sa lupa gamit ang malinis na shears.",
                            "Ilayo at sunugin o ibaon ang mga pinitas na dahon kung may mga batik o mantsa na.",
                            "Gawin ang pruning sa umaga kapag may sikat ng araw upang mabilis matuyo ang hiwa.",
                            "Maglagay ng mulch (tuyong dayami) sa paligid ng puno matapos mag-prune."
                        ),
                        materialsNeeded = listOf("Pruning shears", "70% alcohol", "Dayami para sa mulch"),
                        criticalRule = "Huwag kailanman mag-prune kapag basa ang dahon dahil mabilis kumalat ang fungal at bacterial spores."
                    )
                }
                else -> {
                    DirectCropSolution(
                        title = "Wastong Pruning at Pagtatanggal ng Suwi para sa Talong",
                        tagalogTitle = "Pagpapalaki ng Bunga at Pagpapabuti ng Sirkulasyon",
                        severity = "Pangangalaga (Pruning)",
                        badgeColor = LushGreen,
                        directAnswer = "Alisin ang mga lumang dahon at maliliit na suwi sa ilalim ng unang sanga (first flower fork) upang maging malinis ang sirkulasyon ng hangin at lumaki ang mga bunga.",
                        actionSteps = listOf(
                            "Tanggalin ang mga suwi sa ibaba habang maliliit pa (2–4 pulgada) gamit ang mga daliri o gunting.",
                            "Gupitin ang lahat ng dahon na nasa unang 30cm mula sa lupa.",
                            "Gawin ang pruning sa umaga kapag maaraw upang mabilis matuyo ang sugat ng halaman."
                        ),
                        materialsNeeded = listOf("Pruning shears o matalas na gunting", "70% alcohol"),
                        criticalRule = "Huwag mag-prune kapag maulan o basa ang dahon."
                    )
                }
            }
        }
        "harvest" -> {
            DirectCropSolution(
                title = "Gabay sa Wastong Pag-aani ng Talong (Glossy Purple Stage)",
                tagalogTitle = "Tamang Yugto ng Pitas at Pag-iingat sa Bunga at Puno",
                severity = "Pag-aani (Harvest Timing)",
                badgeColor = LushGreen,
                directAnswer = "Ang talong ay dapat anihin habang makintab at madilim pa ang lilang kulay (glossy stage). Huwag hintaying maging mapusyaw, naninilaw, o kupas ang kulay dahil magiging makahoy, mabutil, at mapait na ang laman. Gumamit ng matalas na shears at mag-iwan ng 2 hanggang 3 cm na tangkay kasama ang calyx.",
                actionSteps = listOf(
                    "Gawin ang 'Thumb Test': Pisilin nang marahan ang bunga gamit ang hinlalaki. Kung lumubog nang bahagya at bumalik sa dati, handa na itong anihin. Kung matigas na parang kahoy o nananatiling may lubog, lampas na sa tamang gulang.",
                    "Mag-ani sa maagang umaga (6:00 AM–8:00 AM) bago uminit ang araw upang mapanatili ang crispness at shelf-life.",
                    "Gupitin ang tangkay gamit ang matalas na pruning shears o gunting, nag-iiwan ng 2–3 sentimetro kasama ang berdeng calyx na may tinik.",
                    "Huwag pilipitin o hilahin ang bunga gamit ang kamay dahil mababali ang buong sanga o mapupunit ang balat ng talong.",
                    "Ilagay nang marahan sa bilawo o crate na may saping dahon ng saging upang hindi magasgasan ang makinis na balat.",
                    "Itabi sa malamig na lugar (12°–13°C kung may cold storage; huwag ibaba sa 10°C dahil masisira sa chilling injury)."
                ),
                materialsNeeded = listOf(
                    "Matalas na pruning shears o harvest scissors",
                    "Woven bilao o plastic crate na may saping dahon ng saging",
                    "Guwantes (proteksyon sa tinik ng calyx ng talong)"
                ),
                criticalRule = "Huwag kailanman ilagay ang bagong aning talong sa ilalim ng tirik na araw o ibilad sa semento dahil mabilis itong kukulubot at mawawalan ng kintab."
            )
        }
        else -> null
    }
}
