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
 * for Yardlong String Beans / Sitaw (Vigna unguiculata subsp. sesquipedalis) in
 * Philippine lowland and intermediate farm plots based on DA-BPI & MapTanim standards.
 */
fun getSitawCheckUpActivities(): List<CheckUpActivity> {
    return listOf(
        CheckUpActivity(
            id = "watering",
            title = "Pagdidilig at Kahalumigmig ng Sitaw",
            tagalogSubtitle = "Suriin ang moisture sa panahon ng pamumulaklak at pamumunga",
            icon = Icons.Default.WaterDrop,
            options = listOf(
                CheckUpOption(
                    id = "sitaw_water_normal",
                    title = "Katamtaman at Regular na Patubig",
                    tagalogDesc = "Mamasa-masa ang 15–20 cm lalim ng kamada; dinidilig 2–3 beses kada linggo sa umaga",
                    isIdeal = true
                ),
                CheckUpOption(
                    id = "sitaw_water_stress",
                    title = "Tagtuyot sa Yugto ng Pamumulaklak (Flower Drop / Lagas-Buko)",
                    tagalogDesc = "Tigang ang kamada; nanlalanta ang mga baging at nalalagas ang mga bulaklak bago pa maging bunga",
                    isIdeal = false
                ),
                CheckUpOption(
                    id = "sitaw_waterlogged",
                    title = "Sobrang Tubig / Baha sa Kamada (Dilaw na Dahon at Root Rot)",
                    tagalogDesc = "Naiipon ang tubig sa paligid ng puno; naninilaw ang dahon mula ibaba at nabubulok ang mga ugat",
                    isIdeal = false
                ),
                CheckUpOption(
                    id = "sitaw_water_splash",
                    title = "Pagtalsik ng Putik sa Ibabang Dahon (Anthracnose Spore Risk)",
                    tagalogDesc = "Nababasa at natatalamsikan ng putik ang mga dahon sa ibaba dahil sa malakas na buhos ng tubig",
                    isIdeal = false
                )
            )
        ),
        CheckUpActivity(
            id = "fertilizing",
            title = "Abono at Nutrisyon ng Sitaw",
            tagalogSubtitle = "Suriin ang balanse ng Phosphorus para sa bulaklak at iwasan ang labis na Nitroheno",
            icon = Icons.Default.Spa,
            options = listOf(
                CheckUpOption(
                    id = "sitaw_fert_balanced",
                    title = "Balanseng Nutrisyon (Compost + 14-14-14 / Solophos Basal)",
                    tagalogDesc = "Matingkad na berdeng trifoliate leaves, masaganang bulaklak, at tuloy-tuloy na pamumunga ng mahahabang sitaw",
                    isIdeal = true
                ),
                CheckUpOption(
                    id = "sitaw_fert_excess_nitrogen",
                    title = "Sobra sa Chemical Nitroheno (Puro Baging at Dahon, Walang Bunga)",
                    tagalogDesc = "Napakakapal at mabulaklak ang mga baging ngunit walang inilalabas na bulaklak o bunga (vegetative explosion)",
                    isIdeal = false
                ),
                CheckUpOption(
                    id = "sitaw_fert_phosphorus_def",
                    title = "Kulang sa Phosphorus (Maliit na Pod Set & Mahinang Root Nodules)",
                    tagalogDesc = "Kakaunti ang lumalabas na bulaklak; payat at maikli ang mga nabubuong sitaw",
                    isIdeal = false
                ),
                CheckUpOption(
                    id = "sitaw_fert_potassium_def",
                    title = "Kulang sa Potash (Manipis, Matamlay, at Madaling Mabali ang Bunga)",
                    tagalogDesc = "Sunog ang gilid ng lumang dahon; kulang sa lutong at malambot ang katawan ng sitaw",
                    isIdeal = false
                )
            )
        ),
        CheckUpActivity(
            id = "trellising_care",
            title = "Balag, Tulos, at Pamamahala sa Baging (Trellising & Pruning)",
            tagalogSubtitle = "Suriin ang katatagan ng A-frame bamboo trellis at sirkulasyon ng hangin",
            icon = Icons.Default.Agriculture,
            options = listOf(
                CheckUpOption(
                    id = "sitaw_trellis_optimal",
                    title = "Maayos na A-Frame Bamboo Trellis (2–2.5m Taas, Maaliwalas)",
                    tagalogDesc = "Matibay na balag, nakagapang nang maayos ang baging, at nakabitin nang tuwid ang mga sitaw",
                    isIdeal = true
                ),
                CheckUpOption(
                    id = "sitaw_trellis_none",
                    title = "Walang Balag / Gumagapang sa Lupa (Bulok at Baluktot sa Putik)",
                    tagalogDesc = "Nakahandusay ang mga baging sa lupa; nabubulok ang mga sitaw kapag umuulan at madilaw ang gilid",
                    isIdeal = false
                ),
                CheckUpOption(
                    id = "sitaw_trellis_overgrown_bottom",
                    title = "Masukal ang Ilalim (Hindi Na-prune ang Ibabang 30 cm)",
                    tagalogDesc = "Masyadong masinsin ang mga sanga sa ibaba; sumasayad ang mga dahon sa lupa at nagiging daan ng amag",
                    isIdeal = false
                )
            )
        ),
        CheckUpActivity(
            id = "pests",
            title = "Mga Peste sa Dahon, Bulaklak, at Bunga ng Sitaw",
            tagalogSubtitle = "Suriin ang mga buko at bunga laban sa pod borer, aphids, at surot",
            icon = Icons.Default.BugReport,
            options = listOf(
                CheckUpOption(
                    id = "sitaw_pest_none",
                    title = "Malinis at Walang Sintomas ng Peste",
                    tagalogDesc = "Buo at makinis ang mga bunga; walang itim na kuto o butas ng uod",
                    isIdeal = true
                ),
                CheckUpOption(
                    id = "sitaw_pest_pod_borer",
                    title = "Bean Pod Borer / Maruca (Uod sa Bulaklak at May Butas na Bunga)",
                    tagalogDesc = "May butas sa mga buko at bunga na may maitim na dumi (frass); bulok at napuputol ang dulo ng sitaw",
                    isIdeal = false
                ),
                CheckUpOption(
                    id = "sitaw_pest_black_aphids",
                    title = "Black Cowpea Aphids (Makakapal na Itim na Kuto sa Dulo ng Baging)",
                    tagalogDesc = "Kumpol ng itim na kuto sa mga murang usbong at bulaklak; may itim na amag (sooty mold) sa paligid",
                    isIdeal = false
                ),
                CheckUpOption(
                    id = "sitaw_pest_pod_sucking_bug",
                    title = "Pod-Sucking Bug / Riptortus (Baluktot at Kulubot na Bunga)",
                    tagalogDesc = "Matutulis na surot na sumisipsip sa murang sitaw; nagiging baluktot, may puting tuldok, at tuyot ang bunga",
                    isIdeal = false
                ),
                CheckUpOption(
                    id = "sitaw_pest_leafminer",
                    title = "Leafminer (Mapuputing Paikot-ikot na Guhit sa Dahon)",
                    tagalogDesc = "May mapuputing tila ahas na lagusan sa balat ng dahon",
                    isIdeal = false
                )
            )
        ),
        CheckUpActivity(
            id = "diseases",
            title = "Mga Sakit at Amag ng Sitaw",
            tagalogSubtitle = "Suriin ang mga dahon at bunga laban sa rust, anthracnose, at mosaic virus",
            icon = Icons.Default.Coronavirus,
            options = listOf(
                CheckUpOption(
                    id = "sitaw_dis_none",
                    title = "Malusog at Walang Amag o Sakit",
                    tagalogDesc = "Matingkad na berde ang foliage at makinis ang balat ng mga bunga",
                    isIdeal = true
                ),
                CheckUpOption(
                    id = "sitaw_dis_rust",
                    title = "Bean Rust (Mapulang Kalawang na Bukol sa Ilalim ng Dahon)",
                    tagalogDesc = "Mapupulang pulbos o parang kalawang na mga bukol sa ilalim ng lumang dahon; nanunuyo ang foliage",
                    isIdeal = false
                ),
                CheckUpOption(
                    id = "sitaw_dis_anthracnose",
                    title = "Anthracnose (Maitim at Malalim na Sugat sa Bunga)",
                    tagalogDesc = "Pabilog at maitim na tila sunog na sugat sa katawan ng sitaw; nabubulok ang bunga sa ulan",
                    isIdeal = false
                ),
                CheckUpOption(
                    id = "sitaw_dis_bcmv",
                    title = "Bean Common Mosaic Virus (Kulubot at Batik-Batik na Dahon)",
                    tagalogDesc = "Dilaw at berdeng tila mosaic na kulay sa dahon; nakabaluktot ang gilid at bansot ang buong puno",
                    isIdeal = false
                )
            )
        ),
        CheckUpActivity(
            id = "bed_care",
            title = "Kalagayan ng Taniman at Pamamahala sa Damo",
            tagalogSubtitle = "Suriin ang drainage ng kamada at aeration sa paligid ng ugat",
            icon = Icons.Default.Spa,
            options = listOf(
                CheckUpOption(
                    id = "sitaw_bed_optimal",
                    title = "Buhaghag na Loam na may Maayos na Drainage",
                    tagalogDesc = "Madaling lumusot ang tubig at hangin; may masaganang bulate at malulusog na root nodules",
                    isIdeal = true
                ),
                CheckUpOption(
                    id = "sitaw_bed_weeds",
                    title = "Masukal sa Damo (Sakal ang Mababaw na Ugat)",
                    tagalogDesc = "Matatayog na damo sa paligid ng tulos na umaagaw sa pataba at nagiging pugad ng peste",
                    isIdeal = false
                ),
                CheckUpOption(
                    id = "sitaw_bed_compacted",
                    title = "Matigas at Binabahang Lupa (Suppressed Nodulation)",
                    tagalogDesc = "Matigas na clay kung saan hindi makahinga ang ugat at hindi makabuo ng Rhizobium nodules",
                    isIdeal = false
                )
            )
        ),
        CheckUpActivity(
            id = "harvest",
            title = "Yugto at Pamamaraan ng Pag-aani",
            tagalogSubtitle = "Pumili ng tamang timing upang mapanatili ang lutong at flowering cushions",
            icon = Icons.Default.ContentCut,
            options = listOf(
                CheckUpOption(
                    id = "sitaw_harvest_optimal",
                    title = "Tamang Yugto ng Pitas Tuwing 2 Araw (Malutong at Pliable)",
                    tagalogDesc = "May haba nang 50–65 cm, makinis, malambot, at hindi pa bumubukol ang mga buto sa loob",
                    isIdeal = true
                ),
                CheckUpOption(
                    id = "sitaw_harvest_overmature",
                    title = "Naantalang Pitas / Bukol ang Buto (Makunat at Guwang)",
                    tagalogDesc = "Lumagpas sa araw; matigas, makunat, at guwang ang laman na tila espongha (spongy pod)",
                    isIdeal = false
                ),
                CheckUpOption(
                    id = "sitaw_harvest_cushion_damage",
                    title = "Napigtas ang Flower Cushion sa Marahas na Paghila",
                    tagalogDesc = "Nahugot ang buong peduncle habang namimitas; nawalan ng kakayahang mamulaklak muli ang naturang buhol",
                    isIdeal = false
                )
            )
        )
    )
}

/**
 * Diagnostic logic providing instant decision support for Yardlong String Beans / Sitaw.
 */
fun solveSitawProblem(
    activityId: String,
    selectedOptions: Set<String>
): DirectCropSolution? {
    if (selectedOptions.isEmpty()) return null

    return when (activityId) {
        "watering" -> {
            when {
                selectedOptions.contains("sitaw_water_stress") -> {
                    DirectCropSolution(
                        title = "Water Stress sa Panahon ng Pamumulaklak (Flower Drop / Lagas-Buko)",
                        tagalogTitle = "Agarang Lunas sa Tuyot na Kamada at Pagkalagas ng Bulaklak ng Sitaw",
                        severity = "Mataas (Blossom Drop Hazard)",
                        badgeColor = AlertAmber,
                        directAnswer = "Ang sitaw ay labis na sensitibo sa kakulangan sa tubig sa yugto ng pamumulaklak at maagang pamumunga. Kapag dumanas ng tagtuyot ang kamada, agad na inilalaglag ng baging ang mga bulaklak (blossom drop) upang protektahan ang sarili, na nagdudulot ng biglaang pagbagsak ng ani.",
                        actionSteps = listOf(
                            "Magdilig agad nang sagana sa umaga upang mabasang muli ang 20 cm lalim ng root zone.",
                            "Itakda ang pagdidilig tuwing 2 hanggang 3 araw sa umaga (6:00–8:00 AM) sa buong panahon ng pamumunga.",
                            "Maglagay ng 5 cm kapal ng dayami (rice straw mulch) sa ibabaw ng kamada upang mapigilan ang mabilis na pagsingaw ng tubig.",
                            "Mag-spray ng fermented plant juice (FPJ) o seaweed extract foliar spray upang mabawasan ang moisture shock."
                        ),
                        materialsNeeded = listOf(
                            "Pandilig o drip irrigation system",
                            "Rice straw mulch para sa kamada",
                            "FPJ o Seaweed extract foliar spray"
                        ),
                        criticalRule = "Iwasan ang pagpapatuyo ng lupa habang namumulaklak ang sitaw; panatilihing mamasa-masa ang kamada."
                    )
                }
                selectedOptions.contains("sitaw_waterlogged") -> {
                    DirectCropSolution(
                        title = "Sobrang Tubig at Banta ng Root Rot (Rhizoctonia & Fusarium)",
                        tagalogTitle = "Kritikal na Pagsagip sa Binabahang Kamada ng Sitaw",
                        severity = "Kritikal (Lethal Root Rot)",
                        badgeColor = DangerRed,
                        directAnswer = "Ang mga ugat at nitrogen-fixing nodules ng sitaw ay nangangailangan ng hangin (oxygen) sa lupa. Kapag nababad sa baha o putik nang mahigit 24 oras, namamatay ang Rhizobium nodules at nabubulok ang ugat, na nagiging sanhi ng paninilaw ng mga dahon mula sa ibaba pataas.",
                        actionSteps = listOf(
                            "Itigil muna agad ang anumang pagdidilig.",
                            "Maghukay ng 20–25 cm malalim na drainage canal sa pagitan ng mga hanay ng balag upang mabilis na umagos ang naipong tubig.",
                            "Bungkalin nang marahan ang ibabaw ng lupa gamit ang hand fork upang makapasok ang hangin (aeration).",
                            "I-drench ang lupa ng Trichoderma harzianum bio-fungicide solution upang masugpo ang fungal root rot."
                        ),
                        materialsNeeded = listOf(
                            "Pala para sa paggawa ng drainage trenches",
                            "Hand fork para sa soil aeration",
                            "Trichoderma harzianum bio-control agent"
                        ),
                        criticalRule = "Laging itanim ang sitaw sa nakataas na kamada (15–20 cm raised bed) lalo na kung magtatanim sa tag-ulan."
                    )
                }
                selectedOptions.contains("sitaw_water_splash") -> {
                    DirectCropSolution(
                        title = "Pagtalsik ng Putik sa Ibabang Dahon (Anthracnose Spore Risk)",
                        tagalogTitle = "Pag-iwas sa Kontaminasyon at Fungal Entry",
                        severity = "Mababa (Spore Splash Risk)",
                        badgeColor = LushGreen,
                        directAnswer = "Ang malakas na buhos ng tubig sa lupa ay nagtatalsik ng fungal spores ng Anthracnose at Rust mula sa lupa patungo sa mga dahon.",
                        actionSteps = listOf(
                            "Gupitin ang mga dahon at suwi sa pinakaibabang 30 cm ng baging (bottom pruning).",
                            "Maglagay ng dayami o carbonized rice hull (CRH) mulch sa ilalim ng balag upang hindi tumalsik ang putik.",
                            "Diligan ang mismong base ng lupa at iwasang basain ang mga dahon sa hapon."
                        ),
                        materialsNeeded = listOf("Gunting para sa pruning", "Mulch (rice straw / CRH)"),
                        criticalRule = "Panatilihing malinis at walang sumasayad na dahon sa lupa."
                    )
                }
                else -> {
                    DirectCropSolution(
                        title = "Tamang Patubig sa Taniman ng Sitaw",
                        tagalogTitle = "Optimal na Moisture para sa Tuloy-tuloy na Pamumunga",
                        severity = "Tamang Pangangalaga (Optimal)",
                        badgeColor = LushGreen,
                        directAnswer = "Nasa tamang kahalumigmig ang inyong kamada! Makakaasa kayo ng tuloy-tuloy na pamumulaklak at mahahabang malulutong na sitaw.",
                        actionSteps = listOf(
                            "Ipagpatuloy ang regular na pagdidilig 2–3 beses kada linggo tuwing umaga.",
                            "Diligan pagkatapos mamitas tuwing ikalawang araw."
                        ),
                        materialsNeeded = listOf("Pangkaraniwang pandilig"),
                        criticalRule = "Panatilihing mamasa-masa ang lupa."
                    )
                }
            }
        }

        "fertilizing" -> {
            when {
                selectedOptions.contains("sitaw_fert_excess_nitrogen") -> {
                    DirectCropSolution(
                        title = "Sobra sa Chemical Nitroheno (Vegetative Explosion / No Flowers)",
                        tagalogTitle = "Pagwawasto sa Masyadong Malagong Baging na Walang Bunga",
                        severity = "Mataas (Delayed Podding)",
                        badgeColor = AlertAmber,
                        directAnswer = "Ang sitaw ay natural na nag-iipon ng sariling nitroheno gamit ang Rhizobium bacteria. Kapag labis ang inilagay na Urea (46-0-0) o kemikal na pataba, nagiging 'tamad' ang nodules at inilalaan ng halaman ang lahat ng lakas sa pagpapalago ng makakapal na baging at dahon nang walang inilalabas na bulaklak.",
                        actionSteps = listOf(
                            "Itigil agad ang anumang paglalagay ng Urea o high-nitrogen fertilizers.",
                            "Mag-side dress agad ng Phosphorus at Potassium (0-20-20 o Solophos + Muriate of Potash) sa daming 10 gramo bawat puno.",
                            "Mag-spray ng Fermented Fruit Juice (FFJ) o Potassium Nitrate foliar spray upang pasiglahin ang pamumulaklak.",
                            "I-prune ang mga sobrang vegetative side shoots sa itaas upang pumasok ang sikat ng araw."
                        ),
                        materialsNeeded = listOf(
                            "Muriate of Potash (0-0-60) at Solophos",
                            "Fermented Fruit Juice (FFJ)",
                            "Gunting para sa pruning"
                        ),
                        criticalRule = "Huwag kailanman mag-overdose ng Urea sa mga legumbre tulad ng sitaw."
                    )
                }
                selectedOptions.contains("sitaw_fert_phosphorus_def") -> {
                    DirectCropSolution(
                        title = "Kakulangan sa Phosphorus (Maliit na Pod Set at Mahinang Nodules)",
                        tagalogTitle = "Pagpaparami ng Bulaklak at Pagpapatibay ng mga Ugat",
                        severity = "Mataas (Low Flower Count)",
                        badgeColor = AlertAmber,
                        directAnswer = "Kailangan ng sitaw ang Phosphorus para sa pagpaparami ng bulaklak at pagpapaunlad ng aktibong Rhizobium nodules sa ugat. Ang kakulangan sa P ay nagreresulta sa kakaunting bulaklak at maikling sitaw.",
                        actionSteps = listOf(
                            "Maglagay ng Solophos (0-20-0) o Complete 14-14-14 bilang side-dress 10 cm ang layo sa puno.",
                            "Maglagay ng bone meal o processed rock phosphate bilang organikong pinagmumulan ng phosphorus.",
                            "Diligan agad matapos mag-abono."
                        ),
                        materialsNeeded = listOf("Solophos (0-20-0) o Bone meal"),
                        criticalRule = "Ihalo ang abono sa lupa bago diligan upang maabot ng mga sumisipsip na ugat."
                    )
                }
                selectedOptions.contains("sitaw_fert_potassium_def") -> {
                    DirectCropSolution(
                        title = "Kakulangan sa Potassium (Manipis at Walang Lutong na Sitaw)",
                        tagalogTitle = "Pagpapalutong at Pagpapahaba ng mga Bunga",
                        severity = "Katamtaman (Poor Pod Quality)",
                        badgeColor = AlertAmber,
                        directAnswer = "Ang Potassium ang nagbibigay ng kapal, tigas, at lutong sa mga bunga ng sitaw. Kapag kulang sa K, nagiging madaling mabali, manipis, at mapusyaw ang kulay ng sitaw.",
                        actionSteps = listOf(
                            "Maglagay ng Muriate of Potash (0-0-60) sa pagitan ng mga puno.",
                            "Magbudbod ng pinong abo ng kahoy (wood ash) sa kamada.",
                            "Mag-spray ng potassium foliar spray tuwing 10 araw habang namumunga."
                        ),
                        materialsNeeded = listOf("Muriate of Potash o Wood Ash"),
                        criticalRule = "Iwasan ang pagdikit ng Potash sa pinakapunong tangkay."
                    )
                }
                else -> {
                    DirectCropSolution(
                        title = "Balanseng Nutrisyon ng Sitaw",
                        tagalogTitle = "Napakalusog na Baging at Masaganang Pamumulaklak",
                        severity = "Tamang Pangangalaga (Optimal)",
                        badgeColor = LushGreen,
                        directAnswer = "Napakaganda ng kondisyon ng inyong taniman. Patuloy na mamumunga ang inyong sitaw tuwing 2 araw.",
                        actionSteps = listOf(
                            "Maglagay ng kaunting vermicompost sa base bawat 3 linggo.",
                            "Diligan nang maayos pagkatapos mag-ani."
                        ),
                        materialsNeeded = listOf("Vermicompost"),
                        criticalRule = "Panatilihin ang natural na sustansya ng lupa."
                    )
                }
            }
        }

        "trellising_care" -> {
            when {
                selectedOptions.contains("sitaw_trellis_none") -> {
                    DirectCropSolution(
                        title = "Walang Balag / Gumagapang sa Lupa (Severe Yield Loss)",
                        tagalogTitle = "Kritikal na Hakbang: Agarang Pagtatayo ng Bamboo A-Frame Trellis",
                        severity = "Kritikal (Severe Quality Loss)",
                        badgeColor = DangerRed,
                        directAnswer = "Ang sitaw ay isang pole climbing bean na kayang umabot sa 2–3 metro ang haba. Kapag pinabayaang gumapang sa lupa: (1) nabubulok ang mga bunga sa putik tuwing umuulan, (2) nagiging baluktot at madilaw ang mga sitaw, (3) mabilis dadapuan ng bean rust at anthracnose, at (4) babagsak ng hanggang 70% ang inyong kabuuang ani.",
                        actionSteps = listOf(
                            "Magtayo agad ng kawayang A-frame trellis o tulos na may taas na 2.0 hanggang 2.5 metro.",
                            "Itusok ang mga kawayan sa 25 cm agwat sa gilid ng mga puno at pagtagpuin ang dulo sa itaas.",
                            "Maglagay ng plastic twine o lambat (netting) upang madaling kapitan ng mga tendrils.",
                            "Marahang iikot ang mga dulo ng baging pakanan sa kawayan upang magsimulang umakyat.",
                            "Iangat ang mga nababad na baging mula sa putik."
                        ),
                        materialsNeeded = listOf(
                            "Kawayang tulos (2–2.5 metro)",
                            "Plastic twine / balag netting",
                            "Alambre o matibay na panali"
                        ),
                        criticalRule = "Huwag kailanman magtanim ng pole sitaw nang walang nakahandang balag o tulos."
                    )
                }
                selectedOptions.contains("sitaw_trellis_overgrown_bottom") -> {
                    DirectCropSolution(
                        title = "Masukal ang Ilalim (Hindi Na-prune ang Ibabang 30 cm)",
                        tagalogTitle = "Pagsasagawa ng Bottom Pruning para sa Bentilasyon",
                        severity = "Katamtaman (Fungal Entry Risk)",
                        badgeColor = AlertAmber,
                        directAnswer = "Kapag masyadong makapal ang mga dahon at suwi sa pinakaibabang 30 cm ng balag, nakukulong ang halumigmig at nagiging tulay ito ng fungal spores mula sa lupa patungo sa mga itaas na dahon.",
                        actionSteps = listOf(
                            "Gamit ang malinis na gunting, gupitin ang lahat ng maliliit na sanga at lumang dahon sa ibabang 30 cm mula sa lupa.",
                            "Alisin ang mga dahong naninilaw o sumasayad sa putik.",
                            "Linisin ang mga natanggal na dahon sa paligid ng kamada."
                        ),
                        materialsNeeded = listOf("Gunting ng hardin"),
                        criticalRule = "Gawin ang pruning sa umaga kapag tuyo ang mga dahon upang mabilis maghilom ang sugat."
                    )
                }
                else -> {
                    DirectCropSolution(
                        title = "Mahusay na Balag at Maaliwalas na Taniman",
                        tagalogTitle = "Napakataas na Kalidad ng Pamumunga sa Trellis",
                        severity = "Tamang Pangangalaga (Optimal)",
                        badgeColor = LushGreen,
                        directAnswer = "Perpekto ang inyong balag! Nakabitin nang tuwid, malinis, at malaya sa putik ang lahat ng inyong sitaw.",
                        actionSteps = listOf(
                            "Tiyaking matatag ang mga panali ng tulos laban sa malalakas na hangin."
                        ),
                        materialsNeeded = listOf("Wala"),
                        criticalRule = "Panatilihing tuwid ang pagbitin ng mga bunga."
                    )
                }
            }
        }

        "pests" -> {
            when {
                selectedOptions.contains("sitaw_pest_pod_borer") -> {
                    DirectCropSolution(
                        title = "Bean Pod Borer (Maruca vitrata) Infestation",
                        tagalogTitle = "Kritikal na Pagsugpo sa Uod na Bumubutas sa Bulaklak at Sitaw",
                        severity = "Kritikal (Severe Pod Damage)",
                        badgeColor = DangerRed,
                        directAnswer = "Ang Maruca pod borer ang numero unong mamumuksang peste ng sitaw. Nangingitlog ang gamu-gamo sa mga buko ng bulaklak sa gabi. Pagkapisa, pumapasok ang uod sa loob ng munting bunga at kinakain ang laman, na nag-iiwan ng maitim na dumi (frass) at nagpapadapa sa buong pod.",
                        actionSteps = listOf(
                            "Suriin ang mga buko at bulaklak tuwing umaga; agad na pitasin at sunugin ang anumang may butas o dumi ng uod.",
                            "Mag-spray agad ng Bacillus thuringiensis (Bt - Dipel/XenTari, 1–2g bawat litro) sa dapit-hapon tuwing 5 araw habang namumulaklak.",
                            "Bilang organikong bio-spray, mag-spray ng Spinosad kung mataas na ang populasyon ng uod.",
                            "Maglagay ng solar-powered light traps sa tabi ng taniman upang hulihin ang mga paruparo sa gabi bago mangitlog."
                        ),
                        materialsNeeded = listOf(
                            "Bacillus thuringiensis (Bt) bio-insecticide",
                            "Spinosad (para sa malalang kaso)",
                            "Light traps / solar insect traps"
                        ),
                        organicRemedy = "Bacillus thuringiensis (Bt) spray tuwing 5 araw sa dapit-hapon sa mismong mga bulaklak.",
                        criticalRule = "Kritikal na mag-spray habang nasa bulaklak pa lamang ang uod; kapag nakabaon na sa loob ng bunga, hindi na ito tatablan ng karaniwang spray."
                    )
                }
                selectedOptions.contains("sitaw_pest_black_aphids") -> {
                    DirectCropSolution(
                        title = "Black Cowpea Aphids (Aphis craccivora)",
                        tagalogTitle = "Pagsugpo sa Makakapal na Itim na Kuto sa Dulo ng Baging",
                        severity = "Mataas (Virus Vector Hazard)",
                        badgeColor = DangerRed,
                        directAnswer = "Ang itim na kuto ay sumisipsip ng katas ng mga murang usbong, bulaklak, at sitaw. Bukod sa panunuyo ng sanga, nagkakalat sila ng Bean Common Mosaic Virus (BCMV) at nagdudulot ng itim na sooty mold.",
                        actionSteps = listOf(
                            "Mag-spray agad ng Neem oil solution (5 ml purong neem oil + 2 ml mild liquid soap bawat litro ng tubig) sa mismong kumpol ng kuto tuwing 4 na araw.",
                            "Maaari ring gumamit ng concentrated chili-garlic extract spray (tinadtad na sili at bawang na ibinabad sa tubig na may sabon).",
                            "Hulihin o patayin gamit ang kamay ang maliliit na kumpol sa pamamagitan ng basang basahan o espongha.",
                            "Hikayatin ang mga ladybugs na kumakain sa mga aphids."
                        ),
                        materialsNeeded = listOf(
                            "Cold-pressed Neem Oil at banayad na sabon",
                            "Siling labuyo at bawang para sa natural spray",
                            "Sprayer"
                        ),
                        organicRemedy = "Neem oil (5 ml/L) o chili-garlic-soap extract tuwing 4 na araw.",
                        criticalRule = "I-spray nang direkta sa ilalim ng mga dahon at dulo ng baging kung saan nakakumpol ang mga kuto."
                    )
                }
                selectedOptions.contains("sitaw_pest_pod_sucking_bug") -> {
                    DirectCropSolution(
                        title = "Pod-Sucking Bug (Riptortus pedestris)",
                        tagalogTitle = "Pagkontrol sa Surot na Nagpapadapo at Nagpapabaluktot sa Sitaw",
                        severity = "Mataas (Pod Deformity)",
                        badgeColor = AlertAmber,
                        directAnswer = "Sumusundot ang surot sa mga murang sitaw at sumisipsip ng katas ng nagde-develop na buto, na nagreresulta sa baluktot, kulubot, at di-maibebentang bunga.",
                        actionSteps = listOf(
                            "Mano-manong manghuli ng surot sa maagang umaga (6:00–7:00 AM) habang mabagal pa silang lumipad dahil sa lamig; ilubog sa may sabong tubig.",
                            "Mag-spray ng Beauveria bassiana entomopathogenic fungus o neem repellent sa hapon.",
                            "Magtanim ng marigold at basil sa paligid upang lituhin ang kanilang pang-amoy."
                        ),
                        materialsNeeded = listOf("Beauveria bassiana", "Neem oil repellent"),
                        criticalRule = "Manghuli ng surot nang maaga bago sumikat ang araw."
                    )
                }
                else -> {
                    DirectCropSolution(
                        title = "Malinis at Ligtas sa Peste ang Balag ng Sitaw",
                        tagalogTitle = "Napakagandang Proteksyon Laban sa mga Insekto",
                        severity = "Tamang Pangangalaga (Optimal)",
                        badgeColor = LushGreen,
                        directAnswer = "Napakalinis ng inyong balag! Walang uod sa bulaklak o itim na kuto sa mga dulo.",
                        actionSteps = listOf(
                            "Panatilihin ang regular na pag-iinspeksyon tuwing umaga bago mamitas."
                        ),
                        materialsNeeded = listOf("Wala"),
                        criticalRule = "Ipagpatuloy ang malinis na pamamahala."
                    )
                }
            }
        }

        "diseases" -> {
            when {
                selectedOptions.contains("sitaw_dis_rust") -> {
                    DirectCropSolution(
                        title = "Bean Rust (Uromyces appendiculatus) Fungus",
                        tagalogTitle = "Pagsugpo sa Kalawang sa Ilalim ng Dahon ng Sitaw",
                        severity = "Mataas (Foliar Necrosis)",
                        badgeColor = AlertAmber,
                        directAnswer = "Ang Bean Rust ay lumalaganap kapag mahalumigmig ang panahon. Ang mapupulang bukol na may tila kalawang na pulbos sa ilalim ng dahon ay sumisira sa chlorophyll at nagpapalagas sa foliage.",
                        actionSteps = listOf(
                            "Agad na gupitin at sunugin ang lahat ng apektadong dahon sa ibaba ng balag.",
                            "Mag-spray ng wettable sulfur powder o Copper fungicide (Copper Hydroxide, 2–3 g/L) sa ilalim ng dahon tuwing 7 araw.",
                            "Iwasan ang pagbabasa ng mga dahon sa hapon.",
                            "Panatilihing bukas ang ilalim ng balag para sa malayang sirkulasyon ng hangin."
                        ),
                        materialsNeeded = listOf(
                            "Copper Hydroxide o Sulfur powder",
                            "Gunting para sa leaf pruning"
                        ),
                        organicRemedy = "Wettable sulfur spray o Copper fungicide tuwing 7 araw.",
                        criticalRule = "I-spray ang ilalim ng mga dahon dahil doon naninirahan ang mga fungal pustules."
                    )
                }
                selectedOptions.contains("sitaw_dis_anthracnose") -> {
                    DirectCropSolution(
                        title = "Anthracnose (Colletotrichum lindemuthianum)",
                        tagalogTitle = "Pagsugpo sa Maitim na Sugat sa Katawan ng Bunga",
                        severity = "Mataas (Pod Rot Hazard)",
                        badgeColor = DangerRed,
                        directAnswer = "Nagdudulot ang Anthracnose ng pabilog at maitim na tila sunog na sugat sa mga sitaw at tangkay lalo na sa panahon ng habagat.",
                        actionSteps = listOf(
                            "Agad na pitasin at itapon ang lahat ng apektadong bunga na may itim na sugat.",
                            "Mag-spray ng bio-fungicide tulad ng Bacillus subtilis o Copper Hydroxide tuwing 5–7 araw sa tag-ulan.",
                            "Huwag maglakad sa taniman habang basa ang mga dahon upang hindi kumalat ang spores sa damit o kamay."
                        ),
                        materialsNeeded = listOf("Copper fungicide", "Bacillus subtilis"),
                        criticalRule = "Huwag mamitas kapag basa ang balag sa ulan upang hindi kumalat ang sakit."
                    )
                }
                selectedOptions.contains("sitaw_dis_bcmv") -> {
                    DirectCropSolution(
                        title = "Bean Common Mosaic Virus (BCMV)",
                        tagalogTitle = "Kritikal na Pagsugpo sa Nakakahawang Virus ng Sitaw",
                        severity = "Kritikal (Systemic Viral Disease)",
                        badgeColor = DangerRed,
                        directAnswer = "Ang BCMV ay systemic virus na dinadala ng black aphids. Ang mga dahon ay nagkakaroon ng dilaw-berdeng batik, kumukulubot, at nababansot ang buong puno.",
                        actionSteps = listOf(
                            "Walang kemikal na gamot kapag may virus na ang halaman; agad na bunutin at sunugin ang buong apektadong puno upang hindi mahawa ang katabing balag.",
                            "I-spray ng neem oil ang katabing mga puno upang patayin ang lahat ng aphids na maaaring magkalat ng virus.",
                            "Sa susunod na taniman, gumamit lamang ng certified virus-resistant hybrid seeds tulad ng Sandigan F1."
                        ),
                        materialsNeeded = listOf("Neem oil spray para sa aphids"),
                        criticalRule = "Agad na i-rouge (bunutin at sunugin) ang apektadong puno sa unang senyales ng mosaic pattern."
                    )
                }
                else -> {
                    DirectCropSolution(
                        title = "Malusog at Ligtas sa Sakit ang mga Baging",
                        tagalogTitle = "Optimal na Kalusugan Laban sa mga Fungi at Virus",
                        severity = "Tamang Pangangalaga (Optimal)",
                        badgeColor = LushGreen,
                        directAnswer = "Napakaganda ng kalusugan ng inyong taniman. Walang palatandaan ng rust o mosaic virus.",
                        actionSteps = listOf(
                            "Panatilihin ang sapat na bentilasyon sa pamamagitan ng bottom pruning."
                        ),
                        materialsNeeded = listOf("Wala"),
                        criticalRule = "Iwasan ang pagtatalsik ng putik sa mga dahon."
                    )
                }
            }
        }

        "bed_care" -> {
            when {
                selectedOptions.contains("sitaw_bed_weeds") -> {
                    DirectCropSolution(
                        title = "Masukal sa Damo ang Kamada (Weed Competition)",
                        tagalogTitle = "Pagdadamot sa Paligid ng Tulos ng Sitaw",
                        severity = "Mataas (Loss of Vigor)",
                        badgeColor = AlertAmber,
                        directAnswer = "Inaagaw ng matatayog na damo ang pataba at tubig sa mababaw na ugat ng sitaw at nagiging taguan ng mga peste tulad ng aphids at surot.",
                        actionSteps = listOf(
                            "Mano-manong bunutin ang mga damo sa paligid ng tulos gamit ang hand weeder.",
                            "Maglagay agad ng 5 cm dayami (rice straw mulch) sa pagitan ng mga hanay upang masupil ang pagtubo ng bagong damo.",
                            "Damuhan ang taniman tuwing 2 linggo."
                        ),
                        materialsNeeded = listOf("Hand weeder", "Rice straw mulch"),
                        criticalRule = "Mag-ingat habang nagdadamot upang hindi masugatan ang mabababaw na ugat ng sitaw."
                    )
                }
                else -> {
                    DirectCropSolution(
                        title = "Buhaghag at Malinis na Kamada ng Sitaw",
                        tagalogTitle = "Perpektong Kondisyon para sa Rhizobium at Ugat",
                        severity = "Tamang Pangangalaga (Optimal)",
                        badgeColor = LushGreen,
                        directAnswer = "Perpekto ang kondisyon ng inyong lupa para sa masaganang nitrogen nodulation at malalalim na ugat.",
                        actionSteps = listOf("Panatilihin ang kapal ng mulch sa kamada."),
                        materialsNeeded = listOf("Wala"),
                        criticalRule = "Panatilihing buhaghag ang lupa."
                    )
                }
            }
        }

        "harvest" -> {
            when {
                selectedOptions.contains("sitaw_harvest_overmature") -> {
                    DirectCropSolution(
                        title = "Naantalang Pitas / Bukol ang Buto (Makunat at Guwang / Spongy Pods)",
                        tagalogTitle = "Pagwawasto sa Iskedyul ng Pag-aani Tuwing Ikalawang Araw",
                        severity = "Mataas (Loss of Market Grade)",
                        badgeColor = AlertAmber,
                        directAnswer = "Kapag naantala ang pagpitas ng kahit 1 hanggang 2 araw lamang, mabilis na lumalaki at bumubukol ang mga buto sa loob ng sitaw. Nagiging makunat, matigas, at guwang ang laman na tila espongha (spongy), na hindi na tinatanggap sa palengke bilang fresh grade.",
                        actionSteps = listOf(
                            "Agad na pitasin ang lahat ng over-mature na sitaw upang hindi maubos ang lakas ng halaman sa pagpapataba ng buto.",
                            "Ang mga over-mature na sitaw ay maaari pa ring gamitin sa bahay (hiwain nang pino sa ginisang sitaw) o patuyuin para kunin ang mga buto.",
                            "Mahigpit na ipatupad ang pamimitas tuwing ikalawang araw (every 2 days) nang walang palya.",
                            "Pumitas kapag malambot, makinis, at madilim na berde pa ang balat."
                        ),
                        materialsNeeded = listOf("Harvest baskets / crates"),
                        criticalRule = "Ang regular na pamimitas tuwing 2 araw ay nag-uudyok sa halaman na patuloy na maglabas ng mga bagong bulaklak."
                    )
                }
                selectedOptions.contains("sitaw_harvest_cushion_damage") -> {
                    DirectCropSolution(
                        title = "Napigtas ang Flower Cushion sa Marahas na Paghila",
                        tagalogTitle = "Tamang Paraan ng Pagbali ng Bunga Nang Hindi Nasusugatan ang Peduncle",
                        severity = "Mataas (Loss of Future Flushes)",
                        badgeColor = AlertAmber,
                        directAnswer = "Ang bawat buhol ng bulaklak (flower cushion / peduncle) ng sitaw ay may kakayahang maglabas ng 4 hanggang 8 sunud-sunod na pares ng mga bunga sa loob ng 2 buwan. Kapag marahas na hinila o ginupit nang sagad ang buhol habang namimitas, nawawalan ng kakayahan ang naturang bahagi na mamulaklak muli.",
                        actionSteps = listOf(
                            "Sanayin ang mga namimitas na gamitin ang dalawang kamay: hawakan ang tangkay ng isang kamay, at marahang baliin paitaas ang mismong base ng pod gamit ang hinlalaki at hintuturo.",
                            "Iwanan ang buong buhol (peduncle) na nakakabit sa baging.",
                            "Huwag kailanman gumamit ng marahas na paghila o paghatak pababa."
                        ),
                        materialsNeeded = listOf("Wala"),
                        criticalRule = "Ingatan ang bawat flower cushion—iyan ang balon ng inyong susunod na mga pitas."
                    )
                }
                else -> {
                    DirectCropSolution(
                        title = "Wastong Yugto ng Pitas at Pagbubungkos ng Sitaw",
                        tagalogTitle = "Gabay sa Masaganang Ani at Pang-Merkadong Pagbubungkos",
                        severity = "Pag-aani (Market Ready)",
                        badgeColor = LushGreen,
                        directAnswer = "Nasa pinakamainam na yugto ng pitas ang inyong sitaw! Malulutong, makakapal, at matatamis ang mga bunga.",
                        actionSteps = listOf(
                            "Mamitas tuwing 6:00 hanggang 8:30 ng umaga bago uminit ang araw upang manatiling sariwa at makunat ang balat.",
                            "I-grade ang mga sitaw: paghiwalayin ang tuwid at mahahabang Grade A (50–65 cm).",
                            "Bungkusin sa timbang na 500 gramo o 1 kilo gamit ang tali o hibla ng saging.",
                            "Ilagay sa malilim at maaliwalas na lugar; takpan ng basang sako upang mapanatili ang kasariwaan bago ibenta."
                        ),
                        materialsNeeded = listOf(
                            "Harvest crates o woven bilao",
                            "Tali o hibla ng saging para sa pagbubungkos",
                            "Basang sako para sa panatiling sariwa"
                        ),
                        criticalRule = "Huwag ibilad sa araw ang mga bagong pitas na sitaw dahil mabilis itong malanta."
                    )
                }
            }
        }

        else -> null
    }
}
