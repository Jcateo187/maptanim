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
 * for Potato / Patatas (Solanum tuberosum) in Philippine highland settings
 * (Benguet, Mt. Province, Bukidnon) based on BPI-BNCRDC & MapTanim technical guidelines.
 */
fun getPotatoCheckUpActivities(): List<CheckUpActivity> {
    return listOf(
        CheckUpActivity(
            id = "watering",
            title = "Pagdidilig at Kahalumigmig ng Patatas",
            tagalogSubtitle = "Suriin ang moisture sa 25–30 cm tuber zone at drainage ng kamada",
            icon = Icons.Default.WaterDrop,
            options = listOf(
                CheckUpOption(
                    id = "potato_water_normal",
                    title = "Katamtaman at Regular na Patubig",
                    tagalogDesc = "Mamasa-masa ang 20–25 cm lalim ng volcanic soil; dinidilig 2–3 beses kada linggo sa umaga",
                    isIdeal = true
                ),
                CheckUpOption(
                    id = "potato_water_stress",
                    title = "Tagtuyot sa Yugto ng Tuber Bulking (Hollow Heart Hazard)",
                    tagalogDesc = "Tigang ang kamada kasunod ng biglaang patubig; banta ng pagputok ng laman (cracking) at guwang sa gitna",
                    isIdeal = false
                ),
                CheckUpOption(
                    id = "potato_waterlogged",
                    title = "Sobrang Tubig / Baha sa Kamada (Bacterial Soft Rot)",
                    tagalogDesc = "Naiipon ang tubig sa pagitan ng mga tudling; nagiging putik at nabubulok ang mga patatas sa ilalim",
                    isIdeal = false
                ),
                CheckUpOption(
                    id = "potato_water_late_stage",
                    title = "Basang Lupa Bago Mag-Ani (Skin Slippage)",
                    tagalogDesc = "Labis na dinidilig 1–2 linggo bago anihin; nagiging malambot at madaling mabakbak ang balat",
                    isIdeal = false
                )
            )
        ),
        CheckUpActivity(
            id = "fertilizing",
            title = "Abono at Nutrisyon ng Patatas",
            tagalogSubtitle = "Suriin ang balanse ng Nitroheno, Phosphorus para sa tuber set, at Potash para sa starch",
            icon = Icons.Default.Spa,
            options = listOf(
                CheckUpOption(
                    id = "potato_fert_balanced",
                    title = "Balanseng Nutrisyon (14-14-14 Basal + Muriate of Potash)",
                    tagalogDesc = "Matingkad na berdeng dahon, matatag na tangkay, at masaganang malalaking tubers",
                    isIdeal = true
                ),
                CheckUpOption(
                    id = "potato_fert_excess_nitrogen",
                    title = "Sobra sa Nitroheno (Malagong Dahon, Maliit ang Patatas)",
                    tagalogDesc = "Napakataas at mabulaklak ang mga dahon ngunit walang laman o kasing-liit lamang ng holen ang mga patatas",
                    isIdeal = false
                ),
                CheckUpOption(
                    id = "potato_fert_potassium_def",
                    title = "Kulang sa Potash (Madaling Magpasa / Blackspot Bruising)",
                    tagalogDesc = "Sunog ang gilid ng lumang dahon; kulang sa starch at nangingitim ang laman kapag nabagsak",
                    isIdeal = false
                ),
                CheckUpOption(
                    id = "potato_fert_phosphorus_def",
                    title = "Kulang sa Phosphorus (Maliit na Tuber Set & Purplish Foliage)",
                    tagalogDesc = "Kulay ube ang ilalim ng dahon; kakaunti lamang ang nabubuong patatas bawat puno",
                    isIdeal = false
                )
            )
        ),
        CheckUpActivity(
            id = "hilling_greening",
            title = "Pagtatambak ng Lupa at Proteksyon sa Araw (Earthing-Up)",
            tagalogSubtitle = "Suriin kung natatakpan ang lahat ng stolon laban sa nakalalasong solanine",
            icon = Icons.Default.Agriculture,
            options = listOf(
                CheckUpOption(
                    id = "potato_hilling_optimal",
                    title = "Maayos na Na-earth-up (20–25 cm Mataas na Ridge)",
                    tagalogDesc = "Lubos na nakabaon ang lahat ng tubers; walang nasisikatan ng araw o nilalapitan ng tuber moth",
                    isIdeal = true
                ),
                CheckUpOption(
                    id = "potato_hilling_exposed",
                    title = "Nakalitaw sa Araw ang Patatas (Green Skin / Solanine Poisoning)",
                    tagalogDesc = "Berde ang balat ng patatas dahil sa sikat ng araw; mapanganib at may lason na solanine glycoalkaloids",
                    isIdeal = false
                ),
                CheckUpOption(
                    id = "potato_hilling_cracked_soil",
                    title = "Bitak-bitak na Lupa sa Pilapil (Tuber Moth Entry)",
                    tagalogDesc = "May mga bitak sa natuyong ridge kung saan nakakapasok ang paruparo ng tuber moth para mangitlog",
                    isIdeal = false
                )
            )
        ),
        CheckUpActivity(
            id = "pests",
            title = "Mga Peste sa Dahon at Tubers ng Patatas",
            tagalogSubtitle = "Suriin ang mga dahon at ilalim ng lupa laban sa uod at kuto",
            icon = Icons.Default.BugReport,
            options = listOf(
                CheckUpOption(
                    id = "potato_pest_none",
                    title = "Malinis at Walang Sintomas ng Peste",
                    tagalogDesc = "Buo ang mga dahon at walang butas o kuto sa ilalim",
                    isIdeal = true
                ),
                CheckUpOption(
                    id = "potato_pest_tuber_moth",
                    title = "Potato Tuber Moth (Uod na Gumagawa ng Lagusan sa Laman)",
                    tagalogDesc = "May uod na gumagapang sa loob ng patatas na nag-iiwan ng maitim na dumi sa mga lagusan",
                    isIdeal = false
                ),
                CheckUpOption(
                    id = "potato_pest_aphids",
                    title = "Aphids / Kuto (Tagapagdala ng Potato Leafroll Virus PLRV)",
                    tagalogDesc = "Kumpol ng maliliit na berdeng kuto sa ilalim ng batang dahon; nakabaluktot pataas ang mga dahon",
                    isIdeal = false
                ),
                CheckUpOption(
                    id = "potato_pest_white_grub",
                    title = "White Grubs / Uod ng Salagubang (Ngumunguya sa Patatas)",
                    tagalogDesc = "Malalalim at pabilog na kagat sa laman ng patatas mula sa matatabang uod sa ilalim ng lupa",
                    isIdeal = false
                ),
                CheckUpOption(
                    id = "potato_pest_cutworm",
                    title = "Cutworm (Pumuputol sa Tangkay sa Pantay-Lupa)",
                    tagalogDesc = "Napuputol ang buong puno ng patatas sa base tuwing gabi",
                    isIdeal = false
                )
            )
        ),
        CheckUpActivity(
            id = "diseases",
            title = "Mga Sakit at Amag ng Patatas sa Kabundukan",
            tagalogSubtitle = "Suriin ang dahon at tubers laban sa late blight, lanta-bakterya, at scab",
            icon = Icons.Default.Coronavirus,
            options = listOf(
                CheckUpOption(
                    id = "potato_dis_none",
                    title = "Malusog at Walang Amag o Sakit",
                    tagalogDesc = "Matingkad na berde ang foliage at walang itim o kape na sugat",
                    isIdeal = true
                ),
                CheckUpOption(
                    id = "potato_dis_late_blight",
                    title = "Late Blight / Phytophthora (Kritikal na Banta sa Tag-ulan)",
                    tagalogDesc = "Malalaking basang maitim na sugat na may puting amag sa ilalim sa mahamog na umaga; mabilis mabulok ang buong plot",
                    isIdeal = false
                ),
                CheckUpOption(
                    id = "potato_dis_bacterial_wilt",
                    title = "Bacterial Wilt / Brown Rot (Lanta-Bakterya)",
                    tagalogDesc = "Biglaang lanta kahit basa ang lupa; nangingitim ang vascular ring sa loob ng patatas at may puting malapot na ooze",
                    isIdeal = false
                ),
                CheckUpOption(
                    id = "potato_dis_common_scab",
                    title = "Common Scab (Magagaspang na Sugat sa Balat)",
                    tagalogDesc = "Tila kortsa at may bitak-bitak na gasgas sa balat dulot ng tuyo at mataas na pH ng lupa",
                    isIdeal = false
                ),
                CheckUpOption(
                    id = "potato_dis_rhizoctonia",
                    title = "Black Scurf / Rhizoctonia (Maitim na Dumi sa Balat)",
                    tagalogDesc = "Maitim at matigas na tila dumi ng lupa sa balat na hindi maalis ng simpleng tubig",
                    isIdeal = false
                )
            )
        ),
        CheckUpActivity(
            id = "bed_care",
            title = "Kalagayan ng Taniman at Asim ng Lupa (pH)",
            tagalogSubtitle = "Suriin ang buhaghag ng kamada at antas ng pH laban sa scab",
            icon = Icons.Default.Spa,
            options = listOf(
                CheckUpOption(
                    id = "potato_bed_optimal",
                    title = "Buhaghag na Volcanic Loam (pH 5.0–6.0)",
                    tagalogDesc = "Malalim, walang bato, at may maasim-asim na pH na natural na pumipigil sa Common Scab",
                    isIdeal = true
                ),
                CheckUpOption(
                    id = "potato_bed_rocky_compact",
                    title = "Mabato at Makapal na Lupa (Deformed / Bungi Tubers)",
                    tagalogDesc = "Tumitama ang lumalaking patatas sa bato o matigas na clod kaya nagiging pilipit at may bitak",
                    isIdeal = false
                ),
                CheckUpOption(
                    id = "potato_bed_alkaline",
                    title = "Masyadong Mataas ang pH / Labis na Apog (pH > 6.5)",
                    tagalogDesc = "Naglagay ng labis na apog sa kamada na nagpapasimula ng malalang Common Scab infection",
                    isIdeal = false
                )
            )
        ),
        CheckUpActivity(
            id = "harvest_curing",
            title = "Yugto at Pamamaraan ng Pag-aani at De-Haulming",
            tagalogSubtitle = "Pumili ng tamang timing ng de-haulming at pag-iimbak",
            icon = Icons.Default.ContentCut,
            options = listOf(
                CheckUpOption(
                    id = "potato_harvest_dehaulmed",
                    title = "Na-de-haulm nang 10–14 Araw (Matigas ang Balat / Handa sa Ani)",
                    tagalogDesc = "Naputol na ang mga dahon at napatigas na ang balat sa ilalim ng lupa; hindi magagasgasan",
                    isIdeal = true
                ),
                CheckUpOption(
                    id = "potato_harvest_green_vines",
                    title = "Buhay at Berde pa ang Dahon (Hilaw ang Balat)",
                    tagalogDesc = "Inaani nang hindi pinuputol ang dahon; napakanipis at madaling matuklap ang balat (skin feathering)",
                    isIdeal = false
                ),
                CheckUpOption(
                    id = "potato_harvest_curing",
                    title = "Tamang Pag-cure at Madilim na Bodega",
                    tagalogDesc = "Inilalagay sa malamig, madilim, at maaliwalas na lugar upang maghilom ang maliliit na gasgas",
                    isIdeal = true
                )
            )
        )
    )
}

/**
 * Diagnostic logic providing instant decision support for Potato / Patatas.
 */
fun solvePotatoProblem(
    activityId: String,
    selectedOptions: Set<String>
): DirectCropSolution? {
    if (selectedOptions.isEmpty()) return null

    return when (activityId) {
        "watering" -> {
            when {
                selectedOptions.contains("potato_water_stress") -> {
                    DirectCropSolution(
                        title = "Water Stress sa Yugto ng Tuber Bulking (Banta ng Hollow Heart)",
                        tagalogTitle = "Agarang Lunas sa Tuyot na Kamada at Pag-iwas sa Bitak ng Tubers",
                        severity = "Mataas (Tuber Deformation Risk)",
                        badgeColor = AlertAmber,
                        directAnswer = "Sa ika-6 hanggang ika-10 linggo, aktibong nag-iipon ng tubig at starch ang lumalaking patatas. Kapag natuyo ang kamada at biglang nakatanggap ng maraming tubig, mabilis na lumalaki ang panlabas na bahagi kaysa sa gitna, na nagdudulot ng 'Hollow Heart' (guwang na may maitim na tisyu sa gitna) at malalalim na bitak sa balat.",
                        actionSteps = listOf(
                            "Panatilihing pantay ang pagdidilig tuwing 2–3 araw gamit ang banayad na patubig o drip system.",
                            "Huwag biglaang bahaing maigi ang tuyong kamada; magdilig nang paunti-unti sa loob ng 2 magkasunod na araw.",
                            "Maglagay ng 5 cm rice straw mulch o dry pine needles sa ibabaw ng mga pilapil upang mapanatiling malamig ang lupa.",
                            "Tiyaking basa ang 20–25 cm lalim kung saan naroroon ang mga patatas."
                        ),
                        materialsNeeded = listOf(
                            "Drip irrigation lines o pandilig na may banayad na spray",
                            "Rice straw o pine needle mulch"
                        ),
                        criticalRule = "Iwasan ang 'drought-and-deluge' cycle: ang regular na kaunting tubig ay mas mainam kaysa minsanang baha."
                    )
                }
                selectedOptions.contains("potato_waterlogged") -> {
                    DirectCropSolution(
                        title = "Baha sa Kamada at Banta ng Bacterial Soft Rot (Pectobacterium)",
                        tagalogTitle = "Kritikal na Pagsagip sa Binabahang Patatas sa Kabundukan",
                        severity = "Kritikal (Lethal Tuber Rot)",
                        badgeColor = DangerRed,
                        directAnswer = "Ang patatas na nababad sa tubig nang mahigit 24 oras ay nauubusan ng oxygen (lenticel suffocation). Agad itong pinapasok ng Pectobacterium carotovora na nagdudulot ng mabaho, malapot, at mabilis na pagkabulok ng buong tuber crop.",
                        actionSteps = listOf(
                            "Itigil muna agad ang anumang patubig.",
                            "Maghukay ng 25–30 cm malalim na drainage canal sa dulo ng bawat tudling upang mabilis na maubos ang tubig.",
                            "Iangat ang pilapil (ridges) sa taas na 25 cm bago pumasok ang panahon ng habagat.",
                            "Agad na hukayin at ihiwalay ang anumang patatas na may amoy mabaho o nagluluha ng puting katas."
                        ),
                        materialsNeeded = listOf(
                            "Pala para sa drainage trenches",
                            "Asarol para sa pagpapatambok ng pilapil"
                        ),
                        criticalRule = "Laging itanim ang patatas sa matataas na ridges na may 2% slope para sa natural na pag-agos ng tubig-ulan."
                    )
                }
                selectedOptions.contains("potato_water_late_stage") -> {
                    DirectCropSolution(
                        title = "Basang Lupa Bago Mag-Ani (Skin Slippage & Rot)",
                        tagalogTitle = "Pagpapatuyo ng Kamada para sa Matigas na Balat",
                        severity = "Katamtaman (Skin Damage Hazard)",
                        badgeColor = AlertAmber,
                        directAnswer = "Ang pagdidilig sa huling 10–14 na araw bago mag-ani ay nagiging sanhi ng malambot at madaling mabakbak na balat (skin feathering) at nagiging daan ng impeksyon sa bodega.",
                        actionSteps = listOf(
                            "Ganap na itigil ang lahat ng pagdidilig 10 hanggang 14 na araw bago ang takdang araw ng paghuhukay.",
                            "Hayaang matuyo ang lupa sa paligid ng mga patatas upang tumigas ang balat."
                        ),
                        materialsNeeded = listOf("Wala"),
                        criticalRule = "Maghukay lamang kapag tuyo ang lupa upang lumabas na malinis at hindi maputik ang patatas."
                    )
                }
                else -> {
                    DirectCropSolution(
                        title = "Tamang Patubig sa Taniman ng Patatas",
                        tagalogTitle = "Optimal na Moisture para sa Malalaking Tubers",
                        severity = "Tamang Pangangalaga (Optimal)",
                        badgeColor = LushGreen,
                        directAnswer = "Nasa pinakamainam na kondisyon ang inyong patubig! Makakakuha kayo ng pantay at makikinis na patatas na walang bitak.",
                        actionSteps = listOf(
                            "Ipagpatuloy ang regular na patubig 2–3 beses bawat linggo sa maagang umaga.",
                            "Ihanda ang sarili sa paghinto ng patubig kapag nag-de-haulm na."
                        ),
                        materialsNeeded = listOf("Pangkaraniwang pandilig"),
                        criticalRule = "Panatilihing mamasa-masa ngunit hindi putik ang kamada."
                    )
                }
            }
        }

        "fertilizing" -> {
            when {
                selectedOptions.contains("potato_fert_excess_nitrogen") -> {
                    DirectCropSolution(
                        title = "Sobra sa Nitroheno (Excess Foliar Nitrogen)",
                        tagalogTitle = "Pagwawasto sa Masyadong Malagong Dahon at Walang Laman na Patatas",
                        severity = "Mataas (Delayed Tuber Initiation)",
                        badgeColor = AlertAmber,
                        directAnswer = "Kapag labis ang Urea o dumi ng manok, inilalaan ng patatas ang lahat ng carbohydrates sa pagpapalago ng dahon at sanga. Nadedelay o tuluyang nahihinto ang pamumuo ng tubers sa ilalim ng lupa (tuberization suppression).",
                        actionSteps = listOf(
                            "Itigil agad ang anumang paglalagay ng Urea (46-0-0) o ammonium fertilizers.",
                            "Mag-side dress agad ng Muriate of Potash (0-0-60) sa daming 10–15 gramo bawat linear meter upang piliting ilipat ng halaman ang asukal patungo sa ugat.",
                            "Mag-spray ng Fermented Fruit Juice (FFJ mula sa hinog na saging) o Potassium Nitrate foliar spray.",
                            "Panatilihin ang sapat na liwanag at iwasan ang lilim."
                        ),
                        materialsNeeded = listOf(
                            "Muriate of Potash (0-0-60)",
                            "Fermented Fruit Juice (FFJ)"
                        ),
                        criticalRule = "Sa patatas, ang Potassium at Phosphorus ang nagpapalaki ng laman—hindi ang sobrang Nitrogen."
                    )
                }
                selectedOptions.contains("potato_fert_potassium_def") -> {
                    DirectCropSolution(
                        title = "Kakulangan sa Potash (Potassium Deficiency & Blackspot Bruising)",
                        tagalogTitle = "Pagpapalutong at Pagpapatibay ng Starch sa Patatas",
                        severity = "Mataas (Loss of Market Grade)",
                        badgeColor = AlertAmber,
                        directAnswer = "Ang patatas ay isa sa pinakamabigat na kumukunsumo ng Potassium sa lahat ng gulay. Kapag kulang sa K, nagiging madilim at sunog ang gilid ng lumang dahon at madaling magkaroon ng internal blackspot bruising ang laman kapag naipit o naibagsak.",
                        actionSteps = listOf(
                            "Maglagay ng Muriate of Potash (0-0-60) sa pagitan ng mga hanay bago ang ikalawang earthing-up.",
                            "Magbudbod ng wood ash (abo ng kahoy) sa paligid ng mga pilapil.",
                            "Mag-spray ng potassium-rich seaweed extract foliar fertilizer tuwing 10 araw."
                        ),
                        materialsNeeded = listOf(
                            "Muriate of Potash o Wood Ash",
                            "Seaweed extract spray"
                        ),
                        criticalRule = "Ihalo ang Potash sa lupa bago diligan upang maabot ng mga sumisipsip na ugat."
                    )
                }
                selectedOptions.contains("potato_fert_phosphorus_def") -> {
                    DirectCropSolution(
                        title = "Kakulangan sa Phosphorus (Maliit na Tuber Set)",
                        tagalogTitle = "Pagpaparami ng Bilang ng Patatas Bawat Puno",
                        severity = "Katamtaman (Low Tuber Count)",
                        badgeColor = AlertAmber,
                        directAnswer = "Ang Phosphorus ang nagpapasimula ng pamumuo ng mga stolon at tuber set. Kapag kulang sa P (karaniwan sa acidic volcanic soils na may P-fixation), kakaunti lamang ang nabubuong patatas.",
                        actionSteps = listOf(
                            "Maglagay ng Solophos (0-20-0) o Complete 14-14-14 bilang basal fertilizer sa mismong tudling bago itanim ang binhi.",
                            "Maghalo ng mycorrhizal fungi inoculant sa lupa upang tulungang higupin ang nakakandadong phosphorus sa volcanic loam."
                        ),
                        materialsNeeded = listOf(
                            "Solophos o 14-14-14",
                            "Mycorrhizal fungi bio-fertilizer"
                        ),
                        criticalRule = "Laging ihalo ang Phosphorus bilang basal fertilizer bago itanim ang patatas."
                    )
                }
                else -> {
                    DirectCropSolution(
                        title = "Balanseng Nutrisyon ng Patatas",
                        tagalogTitle = "Masaganang Paglaki at Malalaking Granola Tubers",
                        severity = "Tamang Pangangalaga (Optimal)",
                        badgeColor = LushGreen,
                        directAnswer = "Napakaganda ng balanse ng inyong pataba. Makakaasa kayo ng mataas na ani ng Grade A patatas na may makakapal at makikinis na balat.",
                        actionSteps = listOf(
                            "Ipagpatuloy ang regular na pag-aalaga.",
                            "Huwag nang magdagdag ng pataba kapag nagsimula nang manilaw ang mga dahon."
                        ),
                        materialsNeeded = listOf("Wala"),
                        criticalRule = "Panatilihing malinis ang paligid ng kamada."
                    )
                }
            }
        }

        "hilling_greening" -> {
            when {
                selectedOptions.contains("potato_hilling_exposed") -> {
                    DirectCropSolution(
                        title = "Nakalitaw sa Araw ang Patatas (Green Skin & Solanine Hazard)",
                        tagalogTitle = "Kritikal na Pagsagip: Earthing-Up Laban sa Lason ng Solanine",
                        severity = "Kritikal (Toxic Alkaloid Development)",
                        badgeColor = DangerRed,
                        directAnswer = "Kapag nasikatan ng araw ang lumalaking patatas, gumagawa ito ng chlorophyll (green skin) kasabay ng Solanine—isang nakalalasong glycoalkaloid. Ang mga berdeng patatas ay mapait, hindi maibebenta sa palengke, at maaaring magdulot ng pagkalason sa tiyan.",
                        actionSteps = listOf(
                            "Agad na maghakot ng buhaghag na lupa o compost at itambak sa paligid ng puno upang matakpan ng 5–8 cm kapal ng lupa ang lahat ng nakalitaw na patatas.",
                            "Bumuo ng mataas at matambok na pilapil (ridge) na may taas na 20–25 cm.",
                            "Maglagay ng makapal na dayami (rice straw mulch) sa ibabaw ng pilapil upang walang liwanag na makatagos.",
                            "Ihiwalay at itapon ang anumang patatas na ganap nang naging berde ang balat."
                        ),
                        materialsNeeded = listOf(
                            "Pala at asarol para sa earthing-up",
                            "Rice straw mulch"
                        ),
                        criticalRule = "Huwag kailanman kainin o ibenta ang patatas na may berdeng balat dahil hindi nawawala ang lason ng solanine kahit pakuluan."
                    )
                }
                selectedOptions.contains("potato_hilling_cracked_soil") -> {
                    DirectCropSolution(
                        title = "Bitak-bitak na Lupa sa Pilapil (Tuber Moth Entry Risk)",
                        tagalogTitle = "Pagtatakip ng mga Bitak Laban sa Pangingitlog ng Peste",
                        severity = "Mataas (Pest Entry Hole)",
                        badgeColor = AlertAmber,
                        directAnswer = "Ang mga bitak sa tuyong lupa ay nagsisilbing highway para sa Potato Tuber Moth upang direktang gapangan at pangitlugan ang mga patatas sa ilalim ng lupa.",
                        actionSteps = listOf(
                            "Diligan nang bahagya ang kamada upang lumambot ang lupa.",
                            "Marahang bungkalin ang ibabaw at tabunan ng pinong buhaghag na compost o lupa ang lahat ng bitak.",
                            "Maglagay ng mulch upang mapanatiling sarado ang lupa."
                        ),
                        materialsNeeded = listOf("Pinong compost", "Rice straw"),
                        criticalRule = "Panatilihing sarado ang lupa sa ibabaw ng mga pilapil hanggang sa anihan."
                    )
                }
                else -> {
                    DirectCropSolution(
                        title = "Mahusay na Earthing-Up at Matambok na Ridge",
                        tagalogTitle = "Protektadong mga Tubers Laban sa Araw at Peste",
                        severity = "Tamang Pangangalaga (Optimal)",
                        badgeColor = LushGreen,
                        directAnswer = "Napakataas ng kalidad ng inyong pilapil. Ang lahat ng patatas ay ligtas sa sikat ng araw at tuber moth.",
                        actionSteps = listOf(
                            "Panatilihin ang kapal ng lupa hanggang sa de-haulming phase."
                        ),
                        materialsNeeded = listOf("Wala"),
                        criticalRule = "Iwasan ang pagkaagnas ng pilapil kapag umuulan."
                    )
                }
            }
        }

        "pests" -> {
            when {
                selectedOptions.contains("potato_pest_tuber_moth") -> {
                    DirectCropSolution(
                        title = "Potato Tuber Moth (Phthorimaea operculella)",
                        tagalogTitle = "Pagsugpo sa Mapanirang Uod sa Loob ng Patatas",
                        severity = "Mataas (Tuber Destruction)",
                        badgeColor = DangerRed,
                        directAnswer = "Ang uod ng tuber moth ay gumagawa ng mga lagusan sa loob ng patatas na puno ng maitim na frass, na nagdudulot ng mabilisang pagkabulok ng ani sa bukid at sa bodega.",
                        actionSteps = listOf(
                            "Siguraduhing may 5–8 cm makapal na lupa sa ibabaw ng lahat ng patatas (earthing-up).",
                            "Mag-spray ng Bacillus thuringiensis (Bt) sa dapit-hapon kung kailan aktibo ang mga paruparo.",
                            "Maglagay ng pheromone sticky traps sa bukid at bodega upang mahuli ang mga lalaking gamu-gamo.",
                            "Sa bodega, takpan ang mga crates ng tuyong dahon ng eucalyptus o lantana na natural na nagtataboy sa tuber moth."
                        ),
                        materialsNeeded = listOf(
                            "Bacillus thuringiensis (Bt)",
                            "Pheromone traps",
                            "Tuyo na dahon ng eucalyptus o lantana"
                        ),
                        organicRemedy = "Bacillus thuringiensis (Bt) foliar spray sa takipsilim.",
                        criticalRule = "Huwag kailanman iwanan ang naaning patatas na nakatiwangwang sa bukid sa magdamag."
                    )
                }
                selectedOptions.contains("potato_pest_aphids") -> {
                    DirectCropSolution(
                        title = "Aphids (Green Peach Aphid) Infestation",
                        tagalogTitle = "Pagkontrol sa Kuto Bago Magkalat ng Virus (PLRV)",
                        severity = "Mataas (Virus Vector Risk)",
                        badgeColor = AlertAmber,
                        directAnswer = "Ang aphids ay sumisipsip ng katas sa ilalim ng mga batang dahon at nagdadala ng Potato Leafroll Virus (PLRV) na nagpapaliit at nagpapabansot sa tubers.",
                        actionSteps = listOf(
                            "Maglagay ng dilaw na sticky traps sa taas na 30 cm sa kamada.",
                            "Mag-spray ng Neem oil (5 ml/L na may mild soap) tuwing 5 araw sa ilalim ng dahon.",
                            "Hikayatin ang ladybugs at hoverflies sa pamamagitan ng pagtatanim ng marigold."
                        ),
                        materialsNeeded = listOf("Yellow sticky traps", "Neem oil spray"),
                        criticalRule = "Agad na bunutin at sunugin ang mga halamang nagpapakita ng matinding pagbaluktot ng dahon (virus symptoms)."
                    )
                }
                selectedOptions.contains("potato_pest_white_grub") -> {
                    DirectCropSolution(
                        title = "White Grubs (Uod ng Salagubang) sa Ilalim ng Lupa",
                        tagalogTitle = "Pagkontrol sa Matatabang Uod na Ngumunguya sa Tubers",
                        severity = "Mataas (Root & Tuber Damage)",
                        badgeColor = DangerRed,
                        directAnswer = "Ang puting uod ng salagubang ay naninirahan sa ilalim ng lupa at gumagawa ng malalalim na bungi sa laman ng patatas.",
                        actionSteps = listOf(
                            "Huwag kailanman maglagay ng sariwang dumi ng hayop; fully decomposed compost lamang ang gamitin.",
                            "I-drench ang kamada ng Metarhizium anisopliae o Beauveria bassiana entomopathogenic fungi.",
                            "Bungkalin nang malalim ang lupa bago magtanim upang mailantad ang mga uod sa mga ibon at araw."
                        ),
                        materialsNeeded = listOf("Metarhizium anisopliae bio-control"),
                        criticalRule = "Iwasan ang paggamit ng hindi pa nabubulok na dumi ng baka o manok."
                    )
                }
                else -> {
                    DirectCropSolution(
                        title = "Malinis at Ligtas sa Peste ang Taniman ng Patatas",
                        tagalogTitle = "Protektadong Kalagayan Laban sa mga Peste",
                        severity = "Tamang Pangangalaga (Optimal)",
                        badgeColor = LushGreen,
                        directAnswer = "Napakalinis ng inyong taniman. Walang nakikitang pinsala mula sa tuber moth o aphids.",
                        actionSteps = listOf(
                            "Panatilihin ang regular na pag-iinspeksyon sa ilalim ng mga dahon."
                        ),
                        materialsNeeded = listOf("Wala"),
                        criticalRule = "Ipagpatuloy ang malinis na pamamahala."
                    )
                }
            }
        }

        "diseases" -> {
            when {
                selectedOptions.contains("potato_dis_late_blight") -> {
                    DirectCropSolution(
                        title = "Late Blight (Phytophthora infestans) Epidemic Threat",
                        tagalogTitle = "Kritikal na Pagsugpo sa Nakamamatay na Amag sa Benguet",
                        severity = "Kritikal (Complete Crop Loss Hazard)",
                        badgeColor = DangerRed,
                        directAnswer = "Ang Late Blight ang numero unong mamamatay-pananim sa patatas sa buong mundo. Sa malamig at mahamog na panahon ng Benguet (12–18°C na may hamog), ang mga basang maitim na sugat na may puting amag ay kayang patayin ang buong ektarya sa loob ng 48 hanggang 72 oras at magpabulok sa lahat ng patatas sa ilalim ng lupa.",
                        actionSteps = listOf(
                            "Agad na putulin at ligtas na sunugin ang lahat ng apektadong dahon at sanga (huwag kailanman i-compost!).",
                            "Mag-spray agad ng Copper Hydroxide o Bordeaux mixture (organikong panangga) o Mancozeb / Metalaxyl-M bio-chemical fungicide tuwing 5–7 araw sa tag-ulan.",
                            "I-adjust ang spray nozzle upang mabasang maigi ang ilalim ng bawat dahon kung saan lumalabas ang puting amag.",
                            "Kung malapit na ang anihan (lampas 80 araw), magsagawa agad ng de-haulming (putulin ang lahat ng dahon) upang hindi bumaba ang spores sa tubers."
                        ),
                        materialsNeeded = listOf(
                            "Copper Hydroxide o Mancozeb",
                            "Knapsack sprayer na may pinong cone nozzle",
                            "Gunting para sa leaf sanitation"
                        ),
                        organicRemedy = "Copper Hydroxide spray (3g/L) tuwing 5 araw sa mahamog na panahon.",
                        criticalRule = "Huwag magpatulo ng tubig sa mga dahon sa hapon; kailangang tuyo ang foliage bago lumubog ang araw."
                    )
                }
                selectedOptions.contains("potato_dis_bacterial_wilt") -> {
                    DirectCropSolution(
                        title = "Bacterial Wilt / Brown Rot (Ralstonia solanacearum)",
                        tagalogTitle = "Lanta-Bakterya: Agarang Pag-quarantine sa Puno at Lupa",
                        severity = "Kritikal (Soil Contamination Hazard)",
                        badgeColor = DangerRed,
                        directAnswer = "Walang gamot kapag pumasok na ang Ralstonia bacteria sa xylem vessels ng patatas. Ang buong puno ay nanlalanta sa tanghali kahit basa ang lupa, at ang loob ng patatas ay may kulay kapeng singsing na naglalabas ng malapot na puting bacterial ooze.",
                        actionSteps = listOf(
                            "Agad na bunutin at sunugin ang buong halaman kasama ang lahat ng ugat at nakadikit na lupa.",
                            "Lagyan ng 2 dakot ng agricultural lime (apog) ang butas upang mapigilan ang pagkalat ng bakterya sa katabing puno.",
                            "Magpatupad ng mahigpit na 3 hanggang 4 na taong crop rotation gamit ang mais o damo (huwag magtanim ng kamatis, talong, o sili).",
                            "Gumamit lamang ng certified clean seed tubers sa susunod na taniman."
                        ),
                        materialsNeeded = listOf(
                            "Agricultural Lime (Apog)",
                            "Sako para sa kontaminadong halaman"
                        ),
                        criticalRule = "Huwag kailanman patubigan ang katabing malulusog na halaman gamit ang tubig na dumaan sa lantang puno."
                    )
                }
                selectedOptions.contains("potato_dis_common_scab") -> {
                    DirectCropSolution(
                        title = "Common Scab (Streptomyces scabies)",
                        tagalogTitle = "Pagpapababa ng pH ng Lupa Laban sa Magaspang na Balat",
                        severity = "Katamtaman (Skin Quality Blemish)",
                        badgeColor = AlertAmber,
                        directAnswer = "Ang Common Scab ay nagdudulot ng magagaspang at tila kortsang sugat sa balat ng patatas kapag tuyo ang lupa sa tuber set o kapag mataas ang pH (>6.5 dulot ng labis na apog).",
                        actionSteps = listOf(
                            "Panatilihing mamasa-masa ang kamada sa unang 4–6 na linggo matapos sumibol.",
                            "Maglagay ng agricultural sulfur sa lupa bago magtanim upang mapanatili ang pH sa 5.2 hanggang 5.8.",
                            "Iwasan ang paglalagay ng sariwang apog o sariwang dumi ng hayop bago magtanim ng patatas."
                        ),
                        materialsNeeded = listOf("Agricultural Sulfur powder"),
                        criticalRule = "Panatilihing bahagyang maasim ang lupa (pH 5.2–5.8) dahil hindi nabubuhay ang scab bacteria sa asim."
                    )
                }
                else -> {
                    DirectCropSolution(
                        title = "Malusog at Ligtas sa Sakit ang Taniman",
                        tagalogTitle = "Protektadong Kalusugan Laban sa mga Amag at Bakterya",
                        severity = "Tamang Pangangalaga (Optimal)",
                        badgeColor = LushGreen,
                        directAnswer = "Napakaganda ng kalusugan ng inyong patatas! Walang palatandaan ng mapanganib na late blight o bacterial wilt.",
                        actionSteps = listOf(
                            "Panatilihin ang maayos na distansya para sa malayang sirkulasyon ng hangin.",
                            "Mag-spray ng preventive copper bio-fungicide bago pumasok ang mahahabang ulan."
                        ),
                        materialsNeeded = listOf("Wala"),
                        criticalRule = "Maging mapagmatyag tuwing mahamog ang umaga."
                    )
                }
            }
        }

        "bed_care" -> {
            when {
                selectedOptions.contains("potato_bed_rocky_compact") -> {
                    DirectCropSolution(
                        title = "Mabato at Makapal na Lupa (Deformed / Bungi Tubers)",
                        tagalogTitle = "Paghahanda ng Malalim at Buhaghag na Volcanic Ridge (30 cm)",
                        severity = "Mataas (Loss of Premium Grade)",
                        badgeColor = AlertAmber,
                        directAnswer = "Ang patatas ay nangangailangan ng napakaluwag at walang resistensyang lupa upang lumaki nang bilog, pantay, at makinis. Kapag tumama ang lumalaking tuber sa bato o matigas na clod, ito ay nagiging bungi, pilipit, o may malalalim na guwang.",
                        actionSteps = listOf(
                            "Bungkalin nang malalim ang lupa sa 30 cm bago magtanim.",
                            "Salain o mano-manong alisin ang lahat ng bato at matitigas na tipak ng lupa.",
                            "Maghalo ng 30% decomposed compost at 20% Carbonized Rice Hull (CRH) upang maging napakabuhaghag ang pilapil.",
                            "Bumuo ng matatambok na ridges na may taas na 20–25 cm."
                        ),
                        materialsNeeded = listOf(
                            "Asarol at pala",
                            "Carbonized Rice Hull (CRH)",
                            "Compost"
                        ),
                        criticalRule = "Ang malalim at buhaghag na lupa ang tanging susi para sa malalaki at hugis-itlog na Granola patatas."
                    )
                }
                else -> {
                    DirectCropSolution(
                        title = "Tamang Katangian ng Kamada ng Patatas",
                        tagalogTitle = "Optimal na Volcanic Sandy Loam para sa Tubers",
                        severity = "Tamang Pangangalaga (Optimal)",
                        badgeColor = LushGreen,
                        directAnswer = "Perpekto ang kondisyon ng inyong lupa para sa malayang paglaki ng patatas.",
                        actionSteps = listOf("Panatilihing malinis sa damo ang mga tudling."),
                        materialsNeeded = listOf("Wala"),
                        criticalRule = "Huwag apakan ang ibabaw ng pilapil."
                    )
                }
            }
        }

        "harvest_curing" -> {
            when {
                selectedOptions.contains("potato_harvest_green_vines") -> {
                    DirectCropSolution(
                        title = "Buhay at Berde Pa ang Dahon (Hilaw ang Balat / Skin Feathering)",
                        tagalogTitle = "Kritikal na Hakbang: Pagsasagawa ng De-Haulming Bago Mag-Ani",
                        severity = "Mataas (High Bruising & Rot Risk)",
                        badgeColor = AlertAmber,
                        directAnswer = "Kapag hinukay ang patatas habang sariwa pa ang mga dahon, napakalambot at manipis ng balat nito. Sa kaunting dampi lamang ay matutuklap ang balat (skin feathering) na magiging sanhi ng mabilisang pagkabulok sa biyahe at bodega.",
                        actionSteps = listOf(
                            "Putulin ang lahat ng mga dahon at sanga 5 cm sa ibabaw ng lupa (de-haulming).",
                            "Iwanan ang mga patatas sa ilalim ng lupa nang hindi dinidiligan sa loob ng 10 hanggang 14 na araw.",
                            "Sa panahong ito, titigas at kakapal ang balat ng patatas bago tuluyang hukayin.",
                            "Makatutulong din ito upang hindi bumaba ang spores ng late blight sa tubers."
                        ),
                        materialsNeeded = listOf("Matalas na karit o scythe"),
                        criticalRule = "Huwag kailanman laktawan ang de-haulming sa komersyal na produksyon ng patatas."
                    )
                }
                else -> {
                    DirectCropSolution(
                        title = "Wastong Paghuhukay, Pag-Cure, at Pag-iimbak ng Patatas",
                        tagalogTitle = "Gabay sa Masaganang Ani at Mahabang Buhay sa Bodega",
                        severity = "Pag-aani (Market Ready)",
                        badgeColor = LushGreen,
                        directAnswer = "Handa na ang inyong mga patatas! Ang matigas na balat ay magbibigay ng 2–4 na buwang kasariwaan sa tamang bodega.",
                        actionSteps = listOf(
                            "Maghukay gamit ang spading fork sa umaga kapag tuyo ang lupa; itusok ang tinidor 25 cm ang layo sa puno upang hindi matuhog ang patatas.",
                            "Mano-manong pulutin at ilagay sa malilinis na wooden crates o plastic baskets.",
                            "I-cure ang mga patatas sa malamig, madilim, at maaliwalas na lugar (15–18°C) sa loob ng 5–7 araw upang maghilom ang maliliit na gasgas.",
                            "Iimbak sa ganap na madilim na bodega upang hindi maging berde ang balat."
                        ),
                        materialsNeeded = listOf(
                            "Spading fork",
                            "Wooden harvest crates",
                            "Madilim at malamig na storage area"
                        ),
                        criticalRule = "Huwag hugasan ng tubig ang patatas kung itatabi nang matagal; alisin lamang ang tuyong lupa gamit ang malambot na brush o kamay."
                    )
                }
            }
        }

        else -> null
    }
}
