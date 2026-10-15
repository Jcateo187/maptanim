package com.maptanim.app.features.farm.dialogs.dss_checkup

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Coronavirus
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
 * for Bell Pepper / Siling Atsal (Capsicum annuum var. grossum) in Philippine highland
 * and cool upland/lowland plots based on DA-BPI & MapTanim standards.
 */
fun getBellPepperCheckUpActivities(): List<CheckUpActivity> {
    return listOf(
        CheckUpActivity(
            id = "watering",
            title = "Pagdidilig at Kahalumigmig ng Lupa",
            tagalogSubtitle = "Suriin ang kahalumigmig upang maiwasan ang tuyong puwit (Blossom-End Rot) at lanta",
            icon = Icons.Default.WaterDrop,
            options = listOf(
                CheckUpOption(
                    id = "bell_pepper_water_normal",
                    title = "Katamtaman at Pantay na Patubig (Drip/Furrow)",
                    tagalogDesc = "Mamasa-masa ang 15–20 cm lalim ng kamada; dinidilig 2–3 beses kada linggo sa umaga nang hindi basa ang dahon",
                    isIdeal = true
                ),
                CheckUpOption(
                    id = "bell_pepper_water_fluctuating",
                    title = "Pabago-bagong Basa at Tuyong Lupa (Blossom-End Rot Trigger)",
                    tagalogDesc = "Minsan ay tuyong-tuyo at minsan ay sobrang basa; nagdudulot ng hindi pantay na pagsipsip ng Calcium",
                    isIdeal = false
                ),
                CheckUpOption(
                    id = "bell_pepper_water_waterlogged",
                    title = "Labis na Tubig / Baha sa Kamada (Dilaw na Dahon at Root Rot)",
                    tagalogDesc = "Naiipon ang tubig nang mahigit 24 oras; naninilaw ang mga dahon mula sa ibaba at nalulunod ang ugat",
                    isIdeal = false
                ),
                CheckUpOption(
                    id = "bell_pepper_water_overhead_splash",
                    title = "Pagtalsik ng Putik sa Dahon at Bulaklak (Anthracnose Risk)",
                    tagalogDesc = "Malakas na buhos ng hose sa dahon at bulaklak na nagtatalsik ng putik mula sa lupa",
                    isIdeal = false
                )
            )
        ),
        CheckUpActivity(
            id = "fertilizing",
            title = "Abono at Balanse ng Nutrisyon",
            tagalogSubtitle = "Suriin ang antas ng Calcium, Potassium, at Nitroheno sa puno",
            icon = Icons.Default.Spa,
            options = listOf(
                CheckUpOption(
                    id = "bell_pepper_fert_balanced",
                    title = "Balanse at Kumpletong Nutrisyon",
                    tagalogDesc = "Matitigas na sanga, malalabay na berdeng dahon, masaganang bulaklak, at makakapal ang pader ng bunga",
                    isIdeal = true
                ),
                CheckUpOption(
                    id = "bell_pepper_fert_excess_nitrogen",
                    title = "Sobra sa Nitroheno (Lush Leaves, Walang Bunga / Lagas-Buko)",
                    tagalogDesc = "Napakaitim at napakalalaki ng dahon ngunit kakaunti ang bulaklak at nalalaglag ang mga murang buko",
                    isIdeal = false
                ),
                CheckUpOption(
                    id = "bell_pepper_fert_calcium_def",
                    title = "Kakulangan sa Calcium (Tuyong Puwit / Blossom-End Rot)",
                    tagalogDesc = "May maitim at tuyong patag na sugat sa dulo o puwit ng mga lumalaking bunga ng bell pepper",
                    isIdeal = false
                ),
                CheckUpOption(
                    id = "bell_pepper_fert_potassium_def",
                    title = "Kakulangan sa Potassium (Manipis na Laman at Sunog na Gilid)",
                    tagalogDesc = "Sunog ang gilid ng lumang dahon; manipis ang pader ng bunga, kulang sa lutong, at madaling malanta",
                    isIdeal = false
                ),
                CheckUpOption(
                    id = "bell_pepper_fert_magnesium_def",
                    title = "Kakulangan sa Magnesium (Dilaw sa Pagitan ng Ugat ng Dahon)",
                    tagalogDesc = "Naninilaw ang gitna ng lumang dahon habang nananatiling berde ang mga ugat (interveinal chlorosis)",
                    isIdeal = false
                )
            )
        ),
        CheckUpActivity(
            id = "staking_pruning",
            title = "Tulos, Suporta, at Pag-pungos (Staking & Pruning)",
            tagalogSubtitle = "Pamamahala sa marupok na sanga at mabigat na bunga ng siling atsal",
            icon = Icons.Default.Agriculture,
            options = listOf(
                CheckUpOption(
                    id = "bell_pepper_stake_optimal",
                    title = "Matatag na Tulos at Malinis na Ilalim (Ideal Staking)",
                    tagalogDesc = "Nakatali sa 80–100 cm kawayang tulos; tinanggal ang side shoots sa ilalim ng Y-fork; malaya sa putik ang mga bunga",
                    isIdeal = true
                ),
                CheckUpOption(
                    id = "bell_pepper_stake_none",
                    title = "Walang Tulos / Nakalapat sa Lupa ang mga Sanga (Lupaypay)",
                    tagalogDesc = "Nakatagilid o bali ang mga sanga sa bigat ng bunga; nakasayad sa basang lupa ang mga bell pepper",
                    isIdeal = false
                ),
                CheckUpOption(
                    id = "bell_pepper_stake_dense_suckers",
                    title = "Masikip na Side Shoots sa Ilalim (Walang Sirkulasyon)",
                    tagalogDesc = "Maraming sanga sa ibaba ng Y-fork; sobrang kapal ng dahon sa ilalim na pinamumugaran ng thrips at amag",
                    isIdeal = false
                )
            )
        ),
        CheckUpActivity(
            id = "pests",
            title = "Mga Peste sa Dahon, Bulaklak, at Bunga",
            tagalogSubtitle = "Bantayan ang fruit borer, thrips, broad mites, at aphids",
            icon = Icons.Default.BugReport,
            options = listOf(
                CheckUpOption(
                    id = "bell_pepper_pest_none",
                    title = "Walang Peste / Malinis ang mga Bunga at Talbos",
                    tagalogDesc = "Makikintab ang balat ng bell pepper, buo ang mga bulaklak, at malinis ang ilalim ng mga dahon",
                    isIdeal = true
                ),
                CheckUpOption(
                    id = "bell_pepper_pest_fruit_borer",
                    title = "Fruit Borer / Uod sa Bunga (Helicoverpa armigera)",
                    tagalogDesc = "May pabilog na butas sa tabi ng calyx na may maitim na dumi (frass); bulok at inuuod ang loob ng bell pepper",
                    isIdeal = false
                ),
                CheckUpOption(
                    id = "bell_pepper_pest_thrips",
                    title = "Thrips / Kuto sa Bulaklak (Silvery Streaks at Kulot na Talbos)",
                    tagalogDesc = "Tila pilak na guhit sa mga dahon at balat ng bunga; kulot ang talbos at nalalaglag ang mga bulaklak",
                    isIdeal = false
                ),
                CheckUpOption(
                    id = "bell_pepper_pest_broad_mites",
                    title = "Broad Mites (Matigas at Pabangong Kulot na Murang Dahon)",
                    tagalogDesc = "Makakapal, matitigas, at pababang nakatiklop ang mga murang dahon; may tansong kulay sa ilalim",
                    isIdeal = false
                ),
                CheckUpOption(
                    id = "bell_pepper_pest_aphids",
                    title = "Aphids at Whiteflies (May Agiw at Madilaw na Talbos)",
                    tagalogDesc = "Kumpol ng maliliit na kuto sa ilalim ng dahon; may malagkit na honey dew at itim na agiw (sooty mold)",
                    isIdeal = false
                )
            )
        ),
        CheckUpActivity(
            id = "diseases",
            title = "Mga Sakit at Amag ng Bell Pepper",
            tagalogSubtitle = "Tukuyin ang lanta-bakterya, anthracnose, at virus bago kumalat",
            icon = Icons.Default.Coronavirus,
            options = listOf(
                CheckUpOption(
                    id = "bell_pepper_dis_none",
                    title = "Walang Sakit / Matatag at Berde ang Puno",
                    tagalogDesc = "Walang lanta, walang sugat sa balat ng bunga, at pantay ang berdeng kulay ng mga dahon",
                    isIdeal = true
                ),
                CheckUpOption(
                    id = "bell_pepper_dis_bacterial_wilt",
                    title = "Lanta-Bakterya / Bacterial Wilt (Ralstonia solanacearum)",
                    tagalogDesc = "Biglaang panlalanta sa tanghali kahit basa ang lupa; nananatiling berde ang dahon; may lumalabas na puting gatas sa tubig",
                    isIdeal = false
                ),
                CheckUpOption(
                    id = "bell_pepper_dis_anthracnose",
                    title = "Anthracnose / Pasob sa Bunga (Colletotrichum spp.)",
                    tagalogDesc = "Pabilog at lumulubog na maitim na sugat na may concentric rings sa balat; may kulay rosas na spore masses sa ulan",
                    isIdeal = false
                ),
                CheckUpOption(
                    id = "bell_pepper_dis_blossom_rot",
                    title = "Tuyong Puwit / Blossom-End Rot sa Dulo ng Bunga",
                    tagalogDesc = "Maitim, patag, at tila tuyong balat sa puwit ng bunga dahil sa kakulangan sa calcium at pabago-bagong tubig",
                    isIdeal = false
                ),
                CheckUpOption(
                    id = "bell_pepper_dis_mosaic_virus",
                    title = "Mosaic Virus (Batik-batik at Kulot na Dahon)",
                    tagalogDesc = "Batik-batik na maputla at madilim na berde sa dahon; bansot ang puno at may deformity ang mga bunga",
                    isIdeal = false
                )
            )
        ),
        CheckUpActivity(
            id = "harvesting",
            title = "Yugto at Paraan ng Pag-aani",
            tagalogSubtitle = "Tiyakin ang tamang kapal ng laman at tamang paggupit sa tangkay",
            icon = Icons.Default.ContentCut,
            options = listOf(
                CheckUpOption(
                    id = "bell_pepper_harv_mature_green",
                    title = "Matigas at Makintab na Berdeng Bunga (Prime Market Grade)",
                    tagalogDesc = "Matigas, makapal ang pader, buo ang hugis-bloke (8–12 cm), at makintab ang balat; ginupit na may 2 cm tangkay",
                    isIdeal = true
                ),
                CheckUpOption(
                    id = "bell_pepper_harv_color_break",
                    title = "Breaker Stage / Nagsisimulang Mag-Pula o Dilaw",
                    tagalogDesc = "May bahagyang pulang o dilaw na kulay sa berde; mas matamis at mataas ang halaga para sa salad market",
                    isIdeal = true
                ),
                CheckUpOption(
                    id = "bell_pepper_harv_immature_thin",
                    title = "Masyadong Maagang Pinitas (Manipis at Malambot na Pader)",
                    tagalogDesc = "Maliit pa at manipis ang balat; mabilis kumulubot at mawalan ng tubig pagkatapos anihin",
                    isIdeal = false
                ),
                CheckUpOption(
                    id = "bell_pepper_harv_overripe_sunburned",
                    title = "Sobrang Hinog / May Paso ng Araw (Sunscald)",
                    tagalogDesc = "Puting sunog na balat dahil sa labis na araw o malambot na bunga na nagsisimulang mabulok sa puno",
                    isIdeal = false
                )
            )
        )
    )
}

/**
 * Diagnostic logic providing research-backed Philippine recommendations
 * for Bell Pepper problems based on farmer selections.
 */
fun solveBellPepperProblem(
    activityId: String,
    selectedOptions: Set<String>
): DirectCropSolution? {
    return when (activityId) {
        "watering" -> {
            when {
                selectedOptions.contains("bell_pepper_water_fluctuating") -> {
                    DirectCropSolution(
                        tagalogTitle = "Lunas sa Pabago-bagong Patubig Laban sa Tuyong Puwit",
                        severity = "Mataas (Blossom-End Rot Risk)",
                        badgeColor = AlertAmber,
                        directAnswer = "Ang pabago-bagong basa at tuyong lupa ang pangunahing dahilan kung bakit hindi makasipsip ng sapat na Calcium ang ugat ng bell pepper, na nagreresulta sa maitim at tuyong puwit ng bunga.",
                        actionSteps = listOf(
                            "Maglagay ng makapal na organikong mulch (5–8 cm na dayami o rice hull) sa paligid ng puno upang mapanatiling pantay ang lamig at kahalumigmig ng lupa.",
                            "Magtakda ng regular na iskedyul ng patubig: diligan nang malalim tuwing ikalawa o ikatlong araw sa umaga.",
                            "Huwag hayaang matuyo nang husto ang kamada bago muling magdilig.",
                            "Magsagawa ng foliar spray ng Calcium Nitrate (10 gramo bawat 4L tubig) upang direktang matulungan ang lumalaking bunga."
                        ),
                        materialsNeeded = listOf(
                            "Malinis na dayami (rice straw) o CRH para sa mulch",
                            "Calcium Nitrate (fertilizer grade)",
                            "Knapsack sprayer"
                        ),
                        criticalRule = "Iwasan ang biglaang pagbaha sa kamada pagkatapos ng matagal na tagtuyot dahil nagdudulot ito ng pagputok (fruit cracking) sa balat ng bell pepper."
                    )
                }

                selectedOptions.contains("bell_pepper_water_waterlogged") -> {
                    DirectCropSolution(
                        tagalogTitle = "Lunas sa Sobrang Tubig at Baha sa Kamada",
                        severity = "Kritikal (Root Rot & Bacterial Wilt Trigger)",
                        badgeColor = DangerRed,
                        directAnswer = "Nalulunod ang mga ugat ng bell pepper kapag naipon ang tubig nang mahigit 24 oras. Ito ang pangunahing nagpapasimula ng lanta-bakterya at pagkabulok ng ugat.",
                        actionSteps = listOf(
                            "Maghukay agad ng malalalim na drainage canal (30–40 cm ang lalim) sa paligid ng kamada upang mabilis na maubos ang nakatambak na tubig.",
                            "Itigil muna ang anumang pagdidilig hanggang sa matuyo ang ibabaw ng lupa.",
                            "I-hilling up ang lupa sa paligid ng tangkay upang maiangat ang root crown sa ibabaw ng tubig.",
                            "Mag-drench ng Trichoderma harzianum sa lupa upang labanan ang mga fungal root rot pathogens."
                        ),
                        materialsNeeded = listOf(
                            "Asarol o pala para sa drainage canal",
                            "Trichoderma biological fungicide"
                        ),
                        criticalRule = "Huwag mag-aplay ng anumang kemikal na abono habang nakababad sa tubig ang lupa."
                    )
                }

                selectedOptions.contains("bell_pepper_water_overhead_splash") -> {
                    DirectCropSolution(
                        tagalogTitle = "Lunas sa Pagtalsik ng Putik sa Dahon at Bulaklak",
                        severity = "Katamtaman (Fungal Spore Risk)",
                        badgeColor = AlertAmber,
                        directAnswer = "Ang malakas na buhos ng tubig sa lupa ay nagtatalsik ng bacterial spot at anthracnose spores mula sa lupa patungo sa mga dahon at bulaklak.",
                        actionSteps = listOf(
                            "Lumipat sa furrow irrigation o gumamit ng drip system sa base ng puno.",
                            "Kung mano-manong nagdidilig gamit ang regadera o hose, itutok lamang sa lupa sa ilalim ng mulch nang hindi binabasa ang mga dahon.",
                            "Maglagay ng plastic mulch o tuyong dayami upang hindi tumalsik ang putik kapag umuulan."
                        ),
                        materialsNeeded = listOf(
                            "Drip tape o sprinkler wand na nakatutok sa lupa",
                            "Silver-black plastic mulch o dayami"
                        ),
                        criticalRule = "Diligan lamang sa umaga upang mabilis matuyo ang anumang basang dahon sa sikat ng araw."
                    )
                }

                else -> {
                    DirectCropSolution(
                        tagalogTitle = "Wastong Pamamahala sa Patubig ng Bell Pepper",
                        severity = "Mainam (Optimal Care)",
                        badgeColor = LushGreen,
                        directAnswer = "Nasa pinakamainam na kalagayan ang inyong patubig! Malalim at pantay ang kahalumigmig sa ugat.",
                        actionSteps = listOf(
                            "Panatilihin ang regular na patubig tuwing 2–3 araw sa umaga.",
                            "Inspeksyunin ang lalim ng kahalumigmig gamit ang daliri (dapat basa sa 10–15 cm lalim).",
                            "Dagdagan bahagya ang patubig kapag pumasok na sa peak fruit bulking stage."
                        ),
                        materialsNeeded = listOf("Soil moisture probe o panukat"),
                        criticalRule = "Panatilihing tuyo ang mga dahon bago sumapit ang gabi."
                    )
                }
            }
        }

        "fertilizing" -> {
            when {
                selectedOptions.contains("bell_pepper_fert_calcium_def") -> {
                    DirectCropSolution(
                        tagalogTitle = "Lunas sa Kakulangan sa Calcium (Tuyong Puwit / Blossom-End Rot)",
                        severity = "Mataas (Direct Fruit Loss)",
                        badgeColor = DangerRed,
                        directAnswer = "Ang Blossom-End Rot ay hindi dulot ng peste o amag kundi kakulangan ng Calcium sa pinakadulo ng lumalaking bell pepper sa panahon ng mabilis na paglaki.",
                        actionSteps = listOf(
                            "Magsagawa agad ng foliar spray ng Calcium Nitrate (15 gramo sa bawat 4L tubig) na may halong Boron tuwing 7 araw hanggang sa mawala ang sintomas.",
                            "Alisin at itapon ang mga bunga na may maitim nang puwit upang hindi magsayang ng sustansya ang puno.",
                            "Panatilihing mamasa-masa ang lupa dahil hindi kayang dalhin ng halaman ang Calcium kapag tuyo ang root zone.",
                            "Sa susunod na pagtatanim, maghalo ng 150–200 gramo ng agricultural lime (apog) bawat metro kwadrado bago maglipat-tanim."
                        ),
                        materialsNeeded = listOf(
                            "Calcium Nitrate fertilizer",
                            "Foliar Boron supplement",
                            "Agricultural lime (apog) para sa lupa"
                        ),
                        criticalRule = "Huwag maglagay ng labis na Potassium o Ammonium nitrogen habang nagpapagaling dahil nakikipagkumpitensya ito sa pagsipsip ng Calcium."
                    )
                }

                selectedOptions.contains("bell_pepper_fert_excess_nitrogen") -> {
                    DirectCropSolution(
                        tagalogTitle = "Lunas sa Sobrang Nitroheno (Lush Leaves, Lagas-Bulaklak)",
                        severity = "Katamtaman (Vegetative Overdrive)",
                        badgeColor = AlertAmber,
                        directAnswer = "Ang labis na Urea o dumi ng manok ay nagtutulak sa puno na magpalaki lamang ng dahon sa halip na mamulaklak at mamunga.",
                        actionSteps = listOf(
                            "Itigil muna ang paglalagay ng Urea (46-0-0) at dumi ng manok.",
                            "Mag-side dress agad ng Muriate of Potash (0-0-60, 10 gramo bawat puno) at Solophos (0-20-0, 10 gramo) upang itulak ang pamumulaklak.",
                            "Alisin ang mga labis na side suckers sa ilalim ng Y-fork upang mapunta ang enerhiya sa mga bulaklak.",
                            "Mag-spray ng foliar bloom booster (mataas sa Phosphorus at Potassium tulad ng 0-52-34)."
                        ),
                        materialsNeeded = listOf(
                            "Muriate of Potash (0-0-60)",
                            "Solophos o Single Superphosphate (0-20-0)",
                            "Pruning shears"
                        ),
                        criticalRule = "Kailangan ng Potassium para maging makapal at malutong ang laman ng bell pepper."
                    )
                }

                selectedOptions.contains("bell_pepper_fert_potassium_def") -> {
                    DirectCropSolution(
                        tagalogTitle = "Lunas sa Kakulangan sa Potassium (Manipis at Sunog na Gilid)",
                        severity = "Mataas (Fruit Quality Loss)",
                        badgeColor = AlertAmber,
                        directAnswer = "Kailangan ng malaking suplay ng Potassium upang lumikha ng makapal na pericarp wall (laman ng bell pepper) at maiwasan ang sunog na gilid ng dahon.",
                        actionSteps = listOf(
                            "Mag-side dress ng Muriate of Potash (0-0-60) sa daming 10–15 gramo bawat puno sa paligid ng drip line.",
                            "Diligan agad pagkatapos mag-abono upang matunaw at maabot ng ugat ang sustansya.",
                            "Mag-spray ng potassium nitrate foliar (2 tbsp bawat 16L sprayer) sa hapon tuwing 10 araw habang aktibong lumalaki ang mga bunga."
                        ),
                        materialsNeeded = listOf(
                            "Muriate of Potash (0-0-60)",
                            "Potassium nitrate foliar fertilizer"
                        ),
                        criticalRule = "Ilayo ang kemikal na abono ng 10 cm mula sa puno upang hindi masunog ang malalambot na ugat."
                    )
                }

                selectedOptions.contains("bell_pepper_fert_magnesium_def") -> {
                    DirectCropSolution(
                        tagalogTitle = "Lunas sa Kakulangan sa Magnesium (Interveinal Chlorosis)",
                        severity = "Katamtaman (Photosynthetic Drop)",
                        badgeColor = AlertAmber,
                        directAnswer = "Ang dilaw na kulay sa pagitan ng ugat ng lumang dahon ay senyales na kulang sa Magnesium, na mahalaga sa chlorophyll at paggawa ng asukal sa bunga.",
                        actionSteps = listOf(
                            "Mag-spray ng Epsom salt (Magnesium Sulfate, 20 gramo bawat 4L tubig) sa mga dahon tuwing 10–14 araw.",
                            "Maghalo ng dolomitic limestone sa lupa kung mababa rin ang pH ng taniman."
                        ),
                        materialsNeeded = listOf("Epsom Salt (Magnesium Sulfate)"),
                        criticalRule = "Mabilis makabawi ang halaman sa loob ng 7 araw kapag ginamitan ng foliar Epsom spray."
                    )
                }

                else -> {
                    DirectCropSolution(
                        tagalogTitle = "Gabay sa Regular na Nutrisyon ng Siling Atsal",
                        severity = "Mainam (Balanced Nutrition)",
                        badgeColor = LushGreen,
                        directAnswer = "Malusog at balanse ang sustansya ng inyong bell pepper! Magpatuloy sa nakatakdang iskedyul.",
                        actionSteps = listOf(
                            "Panatilihin ang side-dressing ng 14-14-14 at 0-0-60 tuwing 14 araw habang patuloy na namumunga.",
                            "Maglagay ng foliar micronutrients (Zinc, Boron, Calcium) tuwing buwanan."
                        ),
                        materialsNeeded = listOf("Complete 14-14-14", "Muriate of Potash"),
                        criticalRule = "Ibagay ang dami ng pataba ayon sa bilang ng nakasabit na bunga sa bawat puno."
                    )
                }
            }
        }

        "staking_pruning" -> {
            when {
                selectedOptions.contains("bell_pepper_stake_none") -> {
                    DirectCropSolution(
                        tagalogTitle = "Lunas sa Walang Tulos at Nakalapat na Sanga",
                        severity = "Mataas (Physical Breakage & Soil Rot)",
                        badgeColor = DangerRed,
                        directAnswer = "Napakabigat ng bunga ng bell pepper para sa marupok nitong mga sanga. Ang paglapat sa lupa ay nagdudulot ng bali at pagkabulok sa putik.",
                        actionSteps = listOf(
                            "Magtulos agad ng 80–100 cm na patpat na kawayan 5–7 cm mula sa puno, itarak ng 20 cm sa lupa.",
                            "Itaas nang marahan ang mga nakahandusay na sanga at talian ng maluwag gamit ang malambot na pisi (figure-8 knot).",
                            "Alisin ang mga nabaling sanga at sugatang bunga.",
                            "Maglagay ng dayami sa ilalim ng halaman upang hindi direktang dumikit ang bunga sa lupa."
                        ),
                        materialsNeeded = listOf(
                            "Patpat na kawayan (80–100 cm ang haba)",
                            "Malambot na plastik na tali o pisi",
                            "Malinis na dayami"
                        ),
                        criticalRule = "Huwag higpitan nang labis ang tali upang hindi masakal ang sanga habang lumalaki."
                    )
                }

                selectedOptions.contains("bell_pepper_stake_dense_suckers") -> {
                    DirectCropSolution(
                        tagalogTitle = "Lunas sa Masikip na Side Shoots (Pruning Protocol)",
                        severity = "Katamtaman (Airflow & Disease Risk)",
                        badgeColor = AlertAmber,
                        directAnswer = "Ang masisikip na sanga sa ilalim ay humaharang sa sirkulasyon ng hangin at nagiging pugad ng thrips, mites, at fungal leaf spots.",
                        actionSteps = listOf(
                            "Putulin gamit ang malinis na gunting ang lahat ng sanga at dahon sa ilalim ng unang Y-fork ng puno.",
                            "Panatilihin lamang ang 2 hanggang 4 na pangunahing sanga paitaas.",
                            "Gawin ang pruning sa umaga kapag tuyo ang araw upang mabilis maghilom ang mga sugat.",
                            "I-disinfect ang gunting gamit ang 70% alcohol pagkatapos ng bawat hanay."
                        ),
                        materialsNeeded = listOf(
                            "Matalas na pruning shears",
                            "70% rubbing alcohol para sa disinfection"
                        ),
                        criticalRule = "Huwag mag-prune kapag basa ang mga dahon o umuulan dahil kakalat ang bacterial canker."
                    )
                }

                else -> {
                    DirectCropSolution(
                        tagalogTitle = "Wastong Suporta at Balangkas ng Puno",
                        severity = "Mainam (Optimal Staking)",
                        badgeColor = LushGreen,
                        directAnswer = "Mahusay ang pagkakatulos at sirkulasyon ng hangin sa inyong taniman ng bell pepper!",
                        actionSteps = listOf(
                            "Regular na i-tsek ang mga tali kung lumalaki na ang sanga.",
                            "Dagdagan ng suportang tali sa itaas kapag marami nang sabay-sabay na lumalaking bunga."
                        ),
                        materialsNeeded = listOf("Pisi para sa dagdag na tali"),
                        criticalRule = "Panatilihing malinis ang ilalim ng puno sa mga damo at tuyong dahon."
                    )
                }
            }
        }

        "pests" -> {
            when {
                selectedOptions.contains("bell_pepper_pest_fruit_borer") -> {
                    DirectCropSolution(
                        tagalogTitle = "Lunas sa Fruit Borer / Uod sa Bunga (Helicoverpa armigera)",
                        severity = "Napakataas (Direct Market Rejection)",
                        badgeColor = DangerRed,
                        directAnswer = "Ang larvae ng fruit borer ay pumuputol sa loob ng bunga at nag-iiwan ng frass sa tabi ng tangkay, na nagiging sanhi ng mabilis na pagkabulok.",
                        actionSteps = listOf(
                            "Pitasin agad at ibaon sa lupa ang lahat ng may butas na bunga upang mamatay ang uod sa loob.",
                            "Mag-spray ng Bacillus thuringiensis (Bt) o Neem oil tuwing takipsilim (5:00 PM) kung kailan aktibo ang mga uod.",
                            "Maglagay ng sex pheromone traps (4 na bitag bawat 1,000 sqm) upang mahuli ang mga lalakeng gamu-gamo.",
                            "Kung matindi ang pinsala, gumamit ng Chlorantraniliprole o Spinetoram ayon sa tamang dosing at sundin ang 3–5 araw na Pre-Harvest Interval (PHI)."
                        ),
                        materialsNeeded = listOf(
                            "Bacillus thuringiensis (Bt) bio-insecticide",
                            "Cold-pressed Neem Oil",
                            "Helicoverpa pheromone traps",
                            "Chlorantraniliprole (emergency chemical option)"
                        ),
                        criticalRule = "Huwag mag-spray ng matapang na kemikal sa umaga upang hindi mapatay ang mga kapaki-pakinabang na bubuyog (pollinators)."
                    )
                }

                selectedOptions.contains("bell_pepper_pest_thrips") -> {
                    DirectCropSolution(
                        tagalogTitle = "Lunas sa Thrips / Kuto sa Bulaklak (Silvery Streaks)",
                        severity = "Mataas (Virus Vector & Scarring)",
                        badgeColor = AlertAmber,
                        directAnswer = "Ang thrips ay sumisipsip sa mga bulaklak at murang dahon, nagdudulot ng kulot na talbos, pilak na guhit sa balat, at nagkakalat ng Tomato Spotted Wilt Virus (TSWV).",
                        actionSteps = listOf(
                            "Maglagay ng blue at yellow sticky traps (1 trap bawat 10 metro kwadrado) sa taas ng canopy ng halaman.",
                            "Maglagay ng reflective silver plastic mulch upang masilaw at maitaboy ang mga thrips mula sa lupa.",
                            "Mag-spray ng horticultural insecticidal soap o kakawate extract na may halong sili tuwing 5 araw.",
                            "Kung malala, mag-spray ng Spinetoram o Dinotefuran nang maingat."
                        ),
                        materialsNeeded = listOf(
                            "Blue sticky traps (partikular na mabisa sa thrips)",
                            "Reflective silver-black mulch",
                            "Insecticidal soap o Spinetoram"
                        ),
                        criticalRule = "Tratuhin agad sa maagang yugto ng pamumulaklak bago pa makapangitlog sa loob ng mga buko."
                    )
                }

                selectedOptions.contains("bell_pepper_pest_broad_mites") -> {
                    DirectCropSolution(
                        tagalogTitle = "Lunas sa Broad Mites (Matigas at Pababang Kulot na Dahon)",
                        severity = "Mataas (Severe Stunting)",
                        badgeColor = AlertAmber,
                        directAnswer = "Napakaliit ng broad mites at hindi nakikita ng mata; naglalabas sila ng lason na nagpapatigas at nagpapakulot pababa sa mga murang dahon na nagiging tanso ang ilalim.",
                        actionSteps = listOf(
                            "Mag-spray ng Wettable Sulfur (2–3 tbsp bawat 16L sprayer) na nakatutok sa ilalim ng mga dahon sa dakong hapon.",
                            "Iwasan ang pag-spray ng sulfur kapag mainit ang araw (>30°C) upang hindi masunog ang dahon.",
                            "Maaari ring gumamit ng Abamectin spray kung hindi mapigil ng sulfur."
                        ),
                        materialsNeeded = listOf(
                            "Wettable Sulfur powder",
                            "Abamectin miticide (alternate option)"
                        ),
                        criticalRule = "Siguraduhing nababasa ang ilalim ng bawat dahon dahil doon nagtatago ang mga hanip."
                    )
                }

                selectedOptions.contains("bell_pepper_pest_aphids") -> {
                    DirectCropSolution(
                        tagalogTitle = "Lunas sa Aphids at Whiteflies (Agiw at Madilaw na Talbos)",
                        severity = "Katamtaman (Sap-Sucking & Sooty Mold)",
                        badgeColor = AlertAmber,
                        directAnswer = "Sumisipsip ng katas ang aphids at naglalabas ng matamis na dumi (honeydew) na tinutubuan ng itim na agiw (sooty mold).",
                        actionSteps = listOf(
                            "Gamitan ng malakas na wisik ng tubig sa ilalim ng dahon upang malaglag ang mga kuto.",
                            "Mag-spray ng neem oil o soap-and-oil spray (1 tbsp dish soap + 1 tbsp mantika sa 4L tubig).",
                            "Pangalagaan ang mga ladybird beetles (salagubang na pula) na natural na kumakain sa kanila."
                        ),
                        materialsNeeded = listOf("Neem oil", "Mild insecticidal soap"),
                        criticalRule = "Ulitin ang pag-spray makalipas ang 4 na araw upang matamaan ang mga bagong pisang itlog."
                    )
                }

                else -> {
                    DirectCropSolution(
                        tagalogTitle = "Wastong Pagmomonitor sa Peste ng Bell Pepper",
                        severity = "Mainam (Clean Crop)",
                        badgeColor = LushGreen,
                        directAnswer = "Ligtas at malinis sa anumang mapaminsalang peste ang inyong mga puno ng bell pepper!",
                        actionSteps = listOf(
                            "Ipagpatuloy ang lingguhang pag-iinspeksyon sa ilalim ng mga dahon at paligid ng calyx.",
                            "Panatilihin ang sticky traps para sa maagang babala."
                        ),
                        materialsNeeded = listOf("Sticky traps"),
                        criticalRule = "Maging mapagmatyag sa simula ng pamumulaklak."
                    )
                }
            }
        }

        "diseases" -> {
            when {
                selectedOptions.contains("bell_pepper_dis_bacterial_wilt") -> {
                    DirectCropSolution(
                        tagalogTitle = "Kagyat na Aksyon sa Lanta-Bakterya (Bacterial Wilt)",
                        severity = "Napakataas (Fatal Soil Pathogen)",
                        badgeColor = DangerRed,
                        directAnswer = "Ang Ralstonia solanacearum ay naninirahan sa ugat at sumisira sa daluyan ng tubig ng halaman. Walang kemikal na lunas kapag nahawa na ang puno.",
                        actionSteps = listOf(
                            "Bunutin agad ang buong apektadong puno kasama ang lupang nakakapit sa ugat at ilagay sa sako; sunugin o ibaon nang malayo sa taniman.",
                            "Huwag itapon sa compost pit dahil mabubuhay ang bakterya.",
                            "Magbuhos ng agricultural lime (apog) o tawas sa butas na pinagbunutan upang ma-disinfect ang lupa.",
                            "Huwag itanim muli ang solanaceous crops (kamatis, talong, patatas) sa kamadang ito nang 1–2 taon; ipalit ang mais o sitaw.",
                            "I-disinfect ang lahat ng kagamitan (asarol, gunting) gamit ang 10% bleach solution."
                        ),
                        materialsNeeded = listOf(
                            "Agricultural lime (apog)",
                            "Bleach o alcohol para sa disinfection",
                            "Resistant variety seeds (Sultan F1) para sa susunod na tanim"
                        ),
                        criticalRule = "Huwag magpatubig sa pamamagitan ng pagpapaagos mula sa may sakit na plot patungo sa malulusog na halaman."
                    )
                }

                selectedOptions.contains("bell_pepper_dis_anthracnose") -> {
                    DirectCropSolution(
                        tagalogTitle = "Lunas sa Anthracnose / Pasob sa Bunga (Colletotrichum)",
                        severity = "Mataas (Fruit Rot Epidemic)",
                        badgeColor = DangerRed,
                        directAnswer = "Ang fungal anthracnose ay nagdudulot ng lumulubog at pabilog na sugat na may mga itim at kulay-rosas na amag sa balat ng bell pepper lalo na sa tag-ulan.",
                        actionSteps = listOf(
                            "Pitasin agad at sunugin ang lahat ng may pasob na bunga upang mapigil ang pagdami ng spores.",
                            "Alisin ang mga lumang dahon sa ilalim upang hindi maipon ang halumigmig sa paligid ng mga bunga.",
                            "Mag-spray ng Copper Hydroxide o Azoxystrobin bilang protectant fungicide bago dumating ang tuloy-tuloy na ulan.",
                            "Lumipat sa drip o furrow irrigation upang manatiling tuyo ang balat ng mga bunga."
                        ),
                        materialsNeeded = listOf(
                            "Copper Fungicide (Copper Hydroxide o Copper Oxychloride)",
                            "Azoxystrobin fungicide (protectant)"
                        ),
                        criticalRule = "Sundin ang 5–7 araw na Pre-Harvest Interval (PHI) bago muling mamitas pagkatapos mag-spray ng fungicide."
                    )
                }

                selectedOptions.contains("bell_pepper_dis_blossom_rot") -> {
                    DirectCropSolution(
                        tagalogTitle = "Lunas sa Tuyong Puwit / Blossom-End Rot",
                        severity = "Mataas (Physiological Fruit Loss)",
                        badgeColor = AlertAmber,
                        directAnswer = "Ito ay physiological disorder sanhi ng hindi sapat na Calcium na umaabot sa lumalaking puwit ng bunga kapag tuyo o hindi pantay ang patubig.",
                        actionSteps = listOf(
                            "Mag-spray ng Calcium-Boron foliar (15 gramo Calcium Nitrate bawat 4L tubig) diretso sa mga bunga tuwing 7 araw.",
                            "Panatilihing pantay at regular ang patubig; maglagay ng mulch upang mapanatili ang kahalumigmig.",
                            "Alisin ang mga apektadong bunga upang hindi magsayang ng enerhiya ang puno."
                        ),
                        materialsNeeded = listOf(
                            "Calcium Nitrate fertilizer",
                            "Foliar Boron supplement",
                            "Mulch (dayami o rice hull)"
                        ),
                        criticalRule = "Hindi ito nakakahawang amag kaya hindi kailangan ng fungicide."
                    )
                }

                selectedOptions.contains("bell_pepper_dis_mosaic_virus") -> {
                    DirectCropSolution(
                        tagalogTitle = "Lunas sa Mosaic Virus (TMV / PVY)",
                        severity = "Mataas (Stunted & Deformed Growth)",
                        badgeColor = DangerRed,
                        directAnswer = "Ang virus ay kumakalat sa pamamagitan ng aphids, thrips, o maruming kamay at kagamitan. Walang pamatay-virus kapag nasa loob na ng halaman.",
                        actionSteps = listOf(
                            "Bunutin at sunugin ang malubhang apektadong puno upang hindi maging sanhi ng paghawa sa iba.",
                            "Sugpuin ang mga aphids at thrips na nagkakalat ng virus gamit ang sticky traps at insecticidal soap.",
                            "Maghugas ng kamay gamit ang sabon at tubig bago humawak sa ibang halaman.",
                            "Iwasan ang paninigarilyo o paghawak ng tabako malapit sa bell pepper (TMV risk)."
                        ),
                        materialsNeeded = listOf("Blue & yellow sticky traps", "Sabon panlaba para sa kamay"),
                        criticalRule = "Huwag kumuha ng buto mula sa mga punong nagkaroon ng mosaic virus."
                    )
                }

                else -> {
                    DirectCropSolution(
                        tagalogTitle = "Wastong Pag-iingat Laban sa Sakit",
                        severity = "Mainam (Disease Free)",
                        badgeColor = LushGreen,
                        directAnswer = "Ligtas at malinis sa anumang amag o bakterya ang inyong mga puno ng bell pepper!",
                        actionSteps = listOf(
                            "Panatilihin ang maaliwalas na canopy at iwasan ang pagbasa sa mga dahon.",
                            "Maglagay ng preventive bio-fungicide (Trichoderma) sa lupa."
                        ),
                        materialsNeeded = listOf("Trichoderma bio-fungicide"),
                        criticalRule = "Manatiling mapagmatyag lalo na sa panahon ng tag-ulan."
                    )
                }
            }
        }

        "harvesting" -> {
            when {
                selectedOptions.contains("bell_pepper_harv_immature_thin") -> {
                    DirectCropSolution(
                        tagalogTitle = "Gabay sa Masyadong Maagang Pagpitas",
                        severity = "Katamtaman (Premature Harvest)",
                        badgeColor = AlertAmber,
                        directAnswer = "Ang maagang pagpitas habang manipis pa ang balat ay nagdudulot ng mabilis na pagkaluoy, pangungulubot, at kawalan ng tamis sa palengke.",
                        actionSteps = listOf(
                            "Hintaying maging makapal at matigas ang pader ng bunga (pindutin nang bahagya: dapat hindi lumulubog ang balat).",
                            "Tiyaking umabot sa 7–10 cm ang diyametro at may makintab na wax coating bago anihin.",
                            "Iwan muna sa puno ang mga malalambot pa sa loob ng 5–7 araw."
                        ),
                        materialsNeeded = listOf("Calipers o panukat ng laki"),
                        criticalRule = "Ang makapal na pader (thick pericarp) ang sukatan ng Grade A bell pepper sa Benguet at Divisoria."
                    )
                }

                selectedOptions.contains("bell_pepper_harv_overripe_sunburned") -> {
                    DirectCropSolution(
                        tagalogTitle = "Lunas sa Paso ng Araw (Sunscald) at Sobrang Hinog",
                        severity = "Katamtaman (Quality Degradation)",
                        badgeColor = AlertAmber,
                        directAnswer = "Ang direktang matinding init sa tanghali na walang lilim ng dahon ay sumusunog sa balat ng bell pepper na nagiging puti at tuyo (sunscald).",
                        actionSteps = listOf(
                            "Huwag mag-prune ng labis sa itaas ng canopy upang may sapat na lilim ang mga dahon para sa mga nakasabit na bunga.",
                            "Maglagay ng 30% black shade net kung nagtatanim sa mainit na kapatagan sa tag-araw.",
                            "Pitasin agad ang mga hinog bago lumambot at maakit ang mga fruit flies."
                        ),
                        materialsNeeded = listOf("30% black shade netting"),
                        criticalRule = "Pitasin ang mga may sunscald agad at iproseso o gamitin sa pagluluto upang hindi masayang."
                    )
                }

                selectedOptions.contains("bell_pepper_harv_color_break") -> {
                    DirectCropSolution(
                        tagalogTitle = "Gabay sa Pag-aani sa Breaker Stage (Pula / Dilaw)",
                        severity = "Mainam (Premium Salad Market)",
                        badgeColor = LushGreen,
                        directAnswer = "Nasa breaker stage ang inyong bell pepper! Ito ang pinakamainam na yugto para sa mga gourmet markets at supermarkets dahil mas matamis at mataas ang halaga.",
                        actionSteps = listOf(
                            "Gupitin ang tangkay gamit ang matalim na gunting na may natitirang 2–3 cm tangkay.",
                            "Mamitas sa maagang umaga (6:00–8:30 AM) bago uminit ang araw.",
                            "I-imbak sa malamig na lugar (7–10°C) upang magpatuloy ang pagpula nang hindi lumalambot.",
                            "I-grade ang mga bunga ayon sa kulay (50% red, 100% red, o yellow)."
                        ),
                        materialsNeeded = listOf(
                            "Matalas na harvest clippers",
                            "Plastic crates na may saping papel"
                        ),
                        criticalRule = "Huwag hawakan nang marahas upang maiwasan ang mga pasa na nagiging simula ng bulok sa imbakan."
                    )
                }

                else -> {
                    DirectCropSolution(
                        tagalogTitle = "Gabay sa Pag-aani ng Grade A Mature Green Bell Pepper",
                        severity = "Mainam (Commercial Standard Harvest)",
                        badgeColor = LushGreen,
                        directAnswer = "Nasa perpektong yugto ng pag-aani ang inyong mature green bell pepper! Matigas, makapal ang pader, at makintab ang balat.",
                        actionSteps = listOf(
                            "Gupitin ang tangkay gamit ang gunting na may 2 cm tangkay (huwag hilahin gamit ang kamay upang hindi mabali ang sanga).",
                            "Punasan ng malinis na basahan ang anumang dumi ng lupa bago ilagay sa crates.",
                            "Ayusin sa shallow layers sa loob ng ventilated crates upang hindi magkapatong-patong ang bigat.",
                            "I-imbak sa maaliwalas at malilim na lugar (7–10°C kung may cold storage) upang tumagal ng 1–2 linggo."
                        ),
                        materialsNeeded = listOf(
                            "Pruning clippers o harvest scissors",
                            "Ventilated harvest crates",
                            "Malinis na basahan"
                        ),
                        criticalRule = "Huwag ibilad sa araw ang mga bagong pitas na crates."
                    )
                }
            }
        }

        else -> null
    }
}
