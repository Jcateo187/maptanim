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
 * for Carrot / Karot (Daucus carota subsp. sativus) in Philippine highland (Benguet, Bukidnon)
 * and deep raised-bed settings based on DA-BPI & MapTanim Carrot technical data.
 */
fun getCarrotCheckUpActivities(): List<CheckUpActivity> {
    return listOf(
        CheckUpActivity(
            id = "watering",
            title = "Pagdidilig at Kahalumigmig ng Karot",
            tagalogSubtitle = "Suriin ang moisture ng mabuhanging kamada at kalagayan ng ugat",
            icon = Icons.Default.WaterDrop,
            options = listOf(
                CheckUpOption(
                    id = "carrot_water_normal",
                    title = "Katamtaman at Pantay na Kahalumigmig",
                    tagalogDesc = "Mamasa-masa ang 15–25 cm lalim ng lupa; dinidilig nang banayad 2–3 beses kada linggo",
                    isIdeal = true
                ),
                CheckUpOption(
                    id = "carrot_water_germination_dry",
                    title = "Tuyo ang Ibabaw ng Lupa sa Pagsibol (10–21 Araw)",
                    tagalogDesc = "Mabagal o hindi sumisibol ang maliliit na binhi dahil tuyot ang pinakaibabaw na lupa"
                ),
                CheckUpOption(
                    id = "carrot_water_stress",
                    title = "Tagtuyot / Tigang na Kamada",
                    tagalogDesc = "Lanta ang malalagong dahon; nagiging makahoy, payat, at mapait ang lasa ng karot"
                ),
                CheckUpOption(
                    id = "carrot_root_splitting",
                    title = "Biyak o Pumutok na Ugat (Root Splitting)",
                    tagalogDesc = "Bumuka ang katawan ng karot matapos ang biglaang malakas na ulan kasunod ng tagtuyot"
                ),
                CheckUpOption(
                    id = "carrot_waterlogged",
                    title = "Baha / Masyadong Basang Kamada (Banta ng Root Rot)",
                    tagalogDesc = "Naiipon ang tubig sa kanal; naninilaw ang dahon at nabubulok ang dulo ng taproot"
                )
            )
        ),
        CheckUpActivity(
            id = "fertilizing",
            title = "Abono at Nutrisyon ng Karot",
            tagalogSubtitle = "Suriin ang balanse ng Nitrogen, Phosphorus, Potassium, at Boron",
            icon = Icons.Default.Spa,
            options = listOf(
                CheckUpOption(
                    id = "carrot_fert_balanced",
                    title = "Balanseng Nutrisyon (Mababang N, Mataas na P & K)",
                    tagalogDesc = "Nalagyan ng 14-14-14 basal at Muriate of Potash; tuwid, makinis, at matingkad na kahel ang ugat",
                    isIdeal = true
                ),
                CheckUpOption(
                    id = "carrot_fert_excess_nitrogen",
                    title = "Sobra sa Nitrogen (Mabulaklak na Dahon, Mabuhok na Ugat)",
                    tagalogDesc = "Napakalago ng dahon ngunit maliit, mabuhok, at nagsasangang-ugat (forked) ang karot"
                ),
                CheckUpOption(
                    id = "carrot_fert_fresh_manure",
                    title = "Sariwang Dumi ng Hayop (Fresh Manure Hazard)",
                    tagalogDesc = "Nasunog at nagsanga-sanga ang taproot dahil sa hindi pa nabulok na dumi ng manok/baka"
                ),
                CheckUpOption(
                    id = "carrot_fert_potash_def",
                    title = "Kulang sa Potash (Maputlang Kahel at Matamlay na Lasa)",
                    tagalogDesc = "Maputla ang kulay ng laman; sunog ang gilid ng lumang dahon; kulang sa tamis"
                ),
                CheckUpOption(
                    id = "carrot_fert_phosphorus_def",
                    title = "Kulang sa Phosphorus (Purplish Leaves & Stunted Roots)",
                    tagalogDesc = "Kulay ube ang ilalim ng dahon; maikli at payat ang ugat dulot ng lamig o kakulangan sa P"
                ),
                CheckUpOption(
                    id = "carrot_fert_boron_def",
                    title = "Kulang sa Boron (Nangingitim at May Guwang sa Gitna)",
                    tagalogDesc = "May bitak at maitim na guwang sa gitna ng laman (black heart/hollow core)"
                )
            )
        ),
        CheckUpActivity(
            id = "pests",
            title = "Peste sa Dahon at Ugat ng Karot",
            tagalogSubtitle = "Suriin ang mga dahon, leeg ng ugat, at ilalim ng lupa",
            icon = Icons.Default.BugReport,
            options = listOf(
                CheckUpOption(
                    id = "carrot_pest_clean",
                    title = "Malinis at Walang Peste",
                    tagalogDesc = "Makinis ang mga dahon at walang bakas ng uod o bukol sa ugat",
                    isIdeal = true
                ),
                CheckUpOption(
                    id = "carrot_pest_leafminer_fly",
                    title = "Leafminer at Carrot Rust Fly (Maitim na Lagusan sa Ugat)",
                    tagalogDesc = "May mapuputing guhit sa dahon at may kalawanging mga tunnel o uka sa katawan ng karot"
                ),
                CheckUpOption(
                    id = "carrot_pest_nematodes",
                    title = "Root-Knot Nematodes (Bukol-bukol at Sanga-sangang Ugat)",
                    tagalogDesc = "May maliliit na bukol sa ugat; pilipit, maikli, at parang octopus ang dami ng galamay"
                ),
                CheckUpOption(
                    id = "carrot_pest_aphids",
                    title = "Aphids sa Leeg at Talbos ng Dahon",
                    tagalogDesc = "Kulupon ng maliliit na berdeng kuto sa pinakagitna ng rosette na nagpapakulubot sa talbos"
                ),
                CheckUpOption(
                    id = "carrot_pest_cutworm",
                    title = "Cutworm sa Bagong Sibol na Punla",
                    tagalogDesc = "Pinuputol ang pinong tangkay ng mga bagong sibol na karot sa gabi"
                )
            )
        ),
        CheckUpActivity(
            id = "diseases",
            title = "Mga Sakit sa Dahon at Nabubulok na Ugat",
            tagalogSubtitle = "Suriin ang dahon laban sa leaf blight at ang ugat laban sa root rot",
            icon = Icons.Default.Coronavirus,
            options = listOf(
                CheckUpOption(
                    id = "carrot_dis_none",
                    title = "Malusog at Walang Sakit",
                    tagalogDesc = "Luntiang malabay ang mga dahon; buo at matigas ang katawan ng ugat",
                    isIdeal = true
                ),
                CheckUpOption(
                    id = "carrot_dis_alternaria_blight",
                    title = "Alternaria Leaf Blight (Maiitim na Batik sa Dulo ng Dahon)",
                    tagalogDesc = "Nangingitim at natutuyo ang dulo ng mga dahon na may dilaw na paligid; nauubos ang dahon"
                ),
                CheckUpOption(
                    id = "carrot_dis_cercospora",
                    title = "Cercospora Leaf Spot (Abo at Pabilog na Batik)",
                    tagalogDesc = "Maliliit na pabilog na batik sa mas batang dahon na lumilikha ng pagkaluoy"
                ),
                CheckUpOption(
                    id = "carrot_dis_root_rot",
                    title = "Fungal Root Rot (Rhizoctonia / Sclerotinia / Pythium)",
                    tagalogDesc = "Basang-basa, maitim, at nanginginain ang bulok mula sa dulo o balikat ng ugat"
                ),
                CheckUpOption(
                    id = "carrot_dis_bacterial_softrot",
                    title = "Bacterial Soft Rot sa Balikat ng Karot",
                    tagalogDesc = "Malambot, malugaw, at mabahong pagkabulok sa pinakatuktok ng ugat matapos mababad sa baha"
                )
            )
        ),
        CheckUpActivity(
            id = "field_care",
            title = "Pangangalaga sa Kamada at Paghihimay (Thinning)",
            tagalogSubtitle = "Suriin ang luwang ng tanim, bato sa lupa, at berdeng balikat (green shoulder)",
            icon = Icons.Default.Agriculture,
            options = listOf(
                CheckUpOption(
                    id = "carrot_care_ideal",
                    title = "Buhaghag na Lupa at Na-thin nang Wasto",
                    tagalogDesc = "5–8 cm ang pagitan ng bawat puno; 25–30 cm ang lalim ng buhaghag na kamada; walang bato",
                    isIdeal = true
                ),
                CheckUpOption(
                    id = "carrot_care_unthinned",
                    title = "Masyadong Masinsin / Hindi Na-thin (Crowded)",
                    tagalogDesc = "Nagkakadikit-dikit ang mga halaman; nagiging kasing-payat lamang ng lapis at pilipit ang karot"
                ),
                CheckUpOption(
                    id = "carrot_care_green_shoulders",
                    title = "Berdeng Balikat (Green Shoulder dulot ng Araw)",
                    tagalogDesc = "Nakalitaw ang itaas na bahagi ng ugat at naging berde at mapait dahil sa sikat ng araw"
                ),
                CheckUpOption(
                    id = "carrot_care_rocky_compacted",
                    title = "Mabatong Lupa / Makapal at Matigas na Lupa",
                    tagalogDesc = "Mabato at siksik ang lupa; nababali, nababaon nang maikli, o nagsasanga ang dulo ng ugat"
                )
            )
        ),
        CheckUpActivity(
            id = "harvest",
            title = "Yugto at Pamamaraan ng Pag-aani ng Karot",
            tagalogSubtitle = "Suriin ang kapal ng balikat, tamang kagamitan, at paghuhugas",
            icon = Icons.Default.ContentCut,
            options = listOf(
                CheckUpOption(
                    id = "carrot_harv_ready",
                    title = "Matambok, 2–4 cm ang Balikat, at Handa Nang Bunutin",
                    tagalogDesc = "90–110 araw mula paghasik; matingkad na kahel; lumuluwag ang lupa sa paligid",
                    isIdeal = true
                ),
                CheckUpOption(
                    id = "carrot_harv_immature",
                    title = "Masyado Pang Payat / Hilaw ang Ugat",
                    tagalogDesc = "Mababa sa 1.5 cm ang diameter; maputla pa ang laman; kailangan pa ng 2–3 linggo"
                ),
                CheckUpOption(
                    id = "carrot_harv_overmature_woody",
                    title = "Labis na Magulang / Makahoy at Nagbibitak",
                    tagalogDesc = "Higit 120 araw na; makahoy ang gitna; nagsisimulang mamulaklak o magkalamat ang balat"
                )
            )
        )
    )
}

/**
 * Diagnostic solver providing actionable, research-backed Philippine solutions
 * for Carrot (Karot) farming conditions based on DA-BPI & Benguet highland practices.
 */
fun solveCarrotProblem(
    activityId: String,
    selectedOptions: Set<String>
): DirectCropSolution? {
    if (selectedOptions.isEmpty()) return null

    return when (activityId) {
        "watering" -> {
            when {
                selectedOptions.contains("carrot_root_splitting") -> {
                    DirectCropSolution(
                        title = "Biyak at Pumutok na Ugat ng Karot (Root Splitting)",
                        tagalogTitle = "Solusyon sa Pagbibitak Dulot ng Pabago-bagong Moisture",
                        severity = "Mataas (High Market Downgrade)",
                        badgeColor = AlertAmber,
                        directAnswer = "Ang root splitting ay dulot ng biglaang pagbuhos ng maraming tubig matapos ang ilang araw na pagkatigang. Mabilis sumipsip ng tubig ang gitnang pith ng ugat kaya't pumuputok ang panlabas na cortex at balat bago pa ito makapag-unat.",
                        actionSteps = listOf(
                            "Anihin agad ang mga pumutok na karot para sa dagliang konsumo o paggawa ng juice bago pasukin ng amag o lupa.",
                            "Panatilihing regular at pantay ang pagdidilig (2–3 beses kada linggo) upang hindi dumanas ng matinding tagtuyot ang root zone.",
                            "Maglagay ng manipis na organic mulch (dayami ng palay o kusot) upang hindi mabilis matuyo ang mabuhanging lupa sa ilalim ng init.",
                            "Bawasan ang biglaang pagbaha ng kamada sa panahon ng huling 3 linggo bago ang anihan."
                        ),
                        materialsNeeded = listOf(
                            "Drip irrigation lines o fine-rose sprinkler",
                            "Rice straw mulch",
                            "Trowel para sa pag-ani ng nabiyak na ugat"
                        ),
                        criticalRule = "Huwag kailanman hayaang magsalit-salit ang tigang na lupa at bahaing patubig habang lumalaki ang ugat ng karot."
                    )
                }
                selectedOptions.contains("carrot_water_germination_dry") -> {
                    DirectCropSolution(
                        title = "Mabagal na Pagsibol Dulot ng Tuyong Lupa",
                        tagalogTitle = "Tamang Kahalumigmig sa Unang 10–21 Araw ng Karot",
                        severity = "Kritikal (Stand Failure)",
                        badgeColor = DangerRed,
                        directAnswer = "Napakabagal sumibol ng binhi ng karot (tumatagal ng 10 hanggang 21 araw) at napakaliit nito. Kung matutuyo ang pinakaibabaw na 1 cm ng lupa kahit sa loob lamang ng 24 oras matapos mag-umpisang umusbong ang binhi, mamamatay ang bagong sibol na mikro-ugat.",
                        actionSteps = listOf(
                            "Diligan nang banayad gamit ang pinong sprinkler (mist/fine spray) 1–2 beses bawat araw upang laging mamasa-masa ang ibabaw ng kamada.",
                            "Takpan ang kamada ng pansamantalang burlap sack, net, o manipis na dayami hanggang sa lumitaw ang mga unang luntiang usbong upang mapanatili ang moisture.",
                            "Agad na alisin ang pantakip sa sandaling sumibol ang 10–20% ng mga binhi upang masikatan ng araw.",
                            "Iwasan ang malalakas na patak ng hose na naghuhukay o nagpapalubog sa maliliit na binhi."
                        ),
                        materialsNeeded = listOf(
                            "Fine-rose pandilig o mist nozzle",
                            "Burlap sack o breathable net cover",
                            "Malinis na tubig"
                        ),
                        criticalRule = "Panatilihing laging mamasa-masa ang unang 1 sentimetro ng lupa hanggang sa tuluyang makatindig ang mga punla."
                    )
                }
                selectedOptions.contains("carrot_waterlogged") -> {
                    DirectCropSolution(
                        title = "Labis na Tubig at Banta ng Fungal Root Rot",
                        tagalogTitle = "Pamamahala sa Basang Kamada upang Maiwasan ang Pagkabulok",
                        severity = "Kritikal (Critical Hazard)",
                        badgeColor = DangerRed,
                        directAnswer = "Ang karot ay may maselang taproot na nangangailangan ng sapat na oxygen sa lupa. Ang pagkababad sa matubig na lupa ay pumipigil sa paghinga ng ugat at nag-aanyaya ng Rhizoctonia, Pythium, at Bacterial Soft Rot na tumutunaw sa ugat sa ilalim ng lupa.",
                        actionSteps = listOf(
                            "Palalimin ang mga kanal sa paligid ng kamada sa 30 cm upang mabilis kumalas ang naipong tubig.",
                            "Siguraduhing nakataas ang kamada ng 20–25 cm (raised bed system) na may halong buhangin at CRH para sa mabilis na drainage.",
                            "Itigil ang pagdidilig hanggang sa mabuhaghag muli ang lupa sa 5–10 cm lalim.",
                            "Alisin ang mga apektadong halaman upang hindi mahawa ang katabi."
                        ),
                        materialsNeeded = listOf("Pala at asarol para sa drainage trenches"),
                        criticalRule = "Ang karot ay nabubulok at namamatay kapag nalubog ang ugat sa tubig nang higit sa 24 oras."
                    )
                }
                else -> {
                    DirectCropSolution(
                        title = "Tamang Gabay sa Pagdidilig ng Karot",
                        tagalogTitle = "Eksaktong Dami ng Tubig para sa Tuwid at Matamis na Ugat",
                        severity = "Tamang Pangangalaga (Optimal)",
                        badgeColor = LushGreen,
                        directAnswer = "Panatilihing pantay ang kahalumigmig sa 15–25 cm lalim. Ang regular na pagdidilig 2–3 beses bawat linggo ay nagbibigay ng tuwid, makinis, at matamis na karot na walang mga bitak.",
                        actionSteps = listOf(
                            "Magdilig tuwing maagang umaga (6:00 AM–8:00 AM).",
                            "Diligan nang malalim ngunit banayad upang maabot ang pinakailalim ng taproot.",
                            "Maglagay ng manipis na organic mulch upang maiwasan ang mabilis na pagsingaw ng tubig."
                        ),
                        materialsNeeded = listOf("Fine sprinkler o drip lines", "Mulch"),
                        criticalRule = "Iwasan ang pagdidilig sa katanghalian kung kailan mainit ang araw."
                    )
                }
            }
        }

        "fertilizing" -> {
            when {
                selectedOptions.contains("carrot_fert_fresh_manure") || selectedOptions.contains("carrot_fert_excess_nitrogen") -> {
                    DirectCropSolution(
                        title = "Sanga-sangang Ugat (Forking) Dulot ng Abono",
                        tagalogTitle = "Bakit Nagiging Parang Octopus ang Karot at Paano Ito Iwawasto",
                        severity = "Mataas (Deformed Yield Loss)",
                        badgeColor = AlertAmber,
                        directAnswer = "Ang pagsasanga (forking) at labis na pagbuhok ng karot ay madalas sanhi ng dalawang bagay: paggamit ng SARIWANG dumi ng manok/baka na sumusunog sa dulo ng sensitibong taproot, o labis na NITROGEN fertilizer (Urea) na nagdudulot ng abnormal na pagsasanga sa halip na dumiretso pababa.",
                        actionSteps = listOf(
                            "Huwag kailanman maglagay ng sariwang dumi ng hayop bago magtanim; gumamit lamang ng ganap na nabulok (well-decomposed) na compost o vermicast.",
                            "Bawasan ang paglalagay ng puro Urea (46-0-0); limitahan ang kabuuang Nitrogen sa 80–120 kg N/ha lamang.",
                            "Palakasin ang Potassium (Muriate of Potash 0-0-60) at Phosphorus (14-14-14) na nagpapatuwid at nagpapatamis sa laman.",
                            "Maglagay ng pataba sa gilid ng hanay (band application) sa halip na direktang ihagis sa ibabaw ng puno."
                        ),
                        materialsNeeded = listOf(
                            "Well-decomposed compost o vermicompost",
                            "Muriate of Potash (0-0-60)",
                            "Complete Fertilizer (14-14-14)"
                        ),
                        criticalRule = "Huwag gumamit ng sariwang dumi ng hayop sa kamada ng karot sa loob ng 90 araw bago maghasik ng binhi."
                    )
                }
                selectedOptions.contains("carrot_fert_boron_def") -> {
                    DirectCropSolution(
                        title = "Kakulangan sa Boron (Nangingitim at May Guwang)",
                        tagalogTitle = "Solusyon sa Black Heart at Hollow Root Core Gamit ang Solubor",
                        severity = "Mataas (High Internal Damage)",
                        badgeColor = AlertAmber,
                        directAnswer = "Ang micronutrient na Boron ay kritikal sa pagbubuo ng mga dingding ng selula ng taproot. Ang kakulangan nito ay lumilikha ng 'cavitary rot' o maitim na guwang sa pinakagitna ng karot at mga bitak sa balat.",
                        actionSteps = listOf(
                            "Mag-isprey ng foliar Borax o Solubor (1–2 gramo bawat 1 litro ng tubig) sa ika-4 at ika-7 linggo pagkasibol.",
                            "Maghalo ng compost na mayaman sa organikong bagay upang mapataas ang micronutrient retention ng mabuhanging lupa.",
                            "Panatilihing nasa pH 6.0–6.8 ang lupa dahil hindi masipsip ng ugat ang boron kapag masyadong maasim (<5.5) o maalkalina (>7.5)."
                        ),
                        materialsNeeded = listOf(
                            "Solubor / Borax powder (agricultural grade)",
                            "Weighing scale para sa eksaktong timbang",
                            "Foliar sprayer"
                        ),
                        criticalRule = "Huwag lumampas sa inirekomendang dosis ng Boron dahil nakalalason sa halaman ang labis na micronutrients."
                    )
                }
                selectedOptions.contains("carrot_fert_potash_def") -> {
                    DirectCropSolution(
                        title = "Kakulangan sa Potassium (Maputlang Kulay at Walang Tamis)",
                        tagalogTitle = "Solusyon Gamit ang Muriate of Potash at Wood Ash",
                        severity = "Katamtaman (Moderate)",
                        badgeColor = AlertAmber,
                        directAnswer = "Ang Potassium (K) ang nagpapasigla sa produksyon ng beta-carotene (ang matingkad na kahel na kulay) at nag-iipon ng natural na asukal sa taproot. Ang kakulangan nito ay nagreresulta sa matabang at maputlang karot.",
                        actionSteps = listOf(
                            "Mag-side dress ng Muriate of Potash (0-0-60) sa sukat na 5–8 gramo bawat metrong haba ng kamada sa ika-5 linggo pagkasibol.",
                            "Maaari ring maglagay ng pinong abo ng kahoy (wood ash) o Fermented Fruit Juice (FFJ).",
                            "Diligan agad pagkatapos maglagay ng pataba upang mabilis masipsip ng root system."
                        ),
                        materialsNeeded = listOf("Muriate of Potash (0-0-60)", "FFJ concoction"),
                        criticalRule = "Ilayo nang 5 cm ang butil ng abono mula sa hanay ng mga halaman upang hindi masunog ang malalambot na ugat."
                    )
                }
                else -> {
                    DirectCropSolution(
                        title = "Tamang Programa sa Pag-aabono ng Karot",
                        tagalogTitle = "Gabay ng DA-BPI para sa 15–25 Tonelada Bawat Ektarya",
                        severity = "Tamang Nutrisyon (Optimal)",
                        badgeColor = LushGreen,
                        directAnswer = "Nangangailangan ang karot ng mababang nitrogen at mataas na phosphorus at potassium sa buhaghag na lupa.",
                        actionSteps = listOf(
                            "Basal: 250–350 kg/ha ng 14-14-14 na iinihalo sa lupa bago ang huling paghaharow.",
                            "Side-dress (30–40 DAS): Muriate of Potash (0-0-60) at bahagyang Urea sa gilid ng mga hanay.",
                            "Foliar spray ng seaweed extract o CalPhos tuwing 14 araw."
                        ),
                        materialsNeeded = listOf("14-14-14", "0-0-60", "Compost"),
                        criticalRule = "Laging diligan ang taniman matapos maglagay ng granular fertilizers."
                    )
                }
            }
        }

        "pests" -> {
            when {
                selectedOptions.contains("carrot_pest_nematodes") -> {
                    DirectCropSolution(
                        title = "Root-Knot Nematodes (Meloidogyne spp.)",
                        tagalogTitle = "Pamamahala sa Bukol-bukol at Pilipit na Ugat ng Karot",
                        severity = "Kritikal (Severe Root Deformity)",
                        badgeColor = DangerRed,
                        directAnswer = "Ang mga mikroskopikong bulate sa lupa (Root-knot Nematodes) ay pumapasok sa dulo ng taproot, lumilikha ng mga bukol (galls), at pumipigil sa paghaba ng ugat kaya't nagiging bansot, pilipit, at nagsasanga-sanga ang karot.",
                        actionSteps = listOf(
                            "Magtanim ng Marigold (Tagetes erecta o 'Amarillo') sa kamada bago o katabi ng karot; ang ugat ng marigold ay naglalabas ng natural nematicide (alpha-terthienyl).",
                            "Maglagay ng masaganang Carbonized Rice Hull (CRH) at Trichoderma harzianum bio-fungicide/nematicide sa lupa bago maghasik.",
                            "Magsagawa ng crop rotation kasama ang mais o snap beans; huwag magtanim ng karot pagkatapos ng kamatis o talong na may kasaysayan ng nematodes.",
                            "Ibilad sa matinding sikat ng araw (soil solarization) ang binungkal na lupa sa loob ng 2–4 na linggo bago tamnan."
                        ),
                        materialsNeeded = listOf(
                            "Binhi ng Marigold (Amarillo)",
                            "Trichoderma harzianum microbial inoculant",
                            "Carbonized Rice Hull (CRH)"
                        ),
                        criticalRule = "Huwag ilipat ang kontaminadong lupa sa ibang malinis na kamada sa pamamagitan ng maruming kagamitan o sapatos."
                    )
                }
                selectedOptions.contains("carrot_pest_leafminer_fly") -> {
                    DirectCropSolution(
                        title = "Carrot Rust Fly at Leafminer sa Karot",
                        tagalogTitle = "Proteksyon Laban sa Maitim at Kalawanging mga Lagusan sa Ugat",
                        severity = "Mataas (High Market Damage)",
                        badgeColor = AlertAmber,
                        directAnswer = "Ang larvae ng Carrot Rust Fly at Leafminer ay gumagawa ng mapuputing guhit sa dahon at sumisira sa taproot sa pamamagitan ng pag-uka ng kalawanging kulay-tsokolateng mga lagusan na nagdudulot ng pagkabulok.",
                        actionSteps = listOf(
                            "Takpan ang kamada ng pinong insect netting (nylon mesh) mula pagsibol hanggang anihan upang hindi makalapit ang nangingitlog na langaw.",
                            "Magtanim ng sibuyas o bawang sa pagitan ng mga hanay; ang matapang na amoy nito ay nagtatakip sa natural na simoy ng karot.",
                            "Alisin at sirain ang mga apektadong dahon na may bakas ng leafminer sa maagang yugto.",
                            "Mag-isprey ng Neem oil solution (5ml neem + 2ml liquid soap bawat litro) tuwing takipsilim."
                        ),
                        materialsNeeded = listOf(
                            "Fine nylon insect mesh / row cover",
                            "Sibuyas para sa intercropping",
                            "Neem oil spray"
                        ),
                        criticalRule = "Huwag iwanan ang mga pinagputulan ng dahon ng karot sa gilid ng kamada dahil ang amoy nito ay nag-aanyaya ng mga langaw."
                    )
                }
                selectedOptions.contains("carrot_pest_aphids") -> {
                    DirectCropSolution(
                        title = "Aphids sa Talbos ng Karot",
                        tagalogTitle = "Organikong Pagsugpo sa Kuto ng Halaman Gamit ang Neem at Sabon",
                        severity = "Katamtaman (Moderate)",
                        badgeColor = AlertAmber,
                        directAnswer = "Ang mga aphids ay sumisipsip ng katas sa pinakabatang talbos sa gitna ng rosette, na nagiging sanhi ng pagkakulubot at pagkabansot ng halaman.",
                        actionSteps = listOf(
                            "Bombahin ng malakas na spray ng tubig ang gitna ng mga dahon upang mahugasan ang kulupon ng aphids.",
                            "Mag-isprey ng Neem oil (5ml) na may kasamang banayad na dishwashing soap (2ml) sa bawat litro ng tubig.",
                            "Pangalagaan ang mga ladybug na natural na kumakain sa mga aphids."
                        ),
                        materialsNeeded = listOf("Neem oil", "Liquid soap", "Sprayer"),
                        criticalRule = "Direktang patamaan ang gitnang leeg ng halaman kung saan nagkukumpulan ang mga peste."
                    )
                }
                else -> {
                    DirectCropSolution(
                        title = "Malinis at Walang Pesteng Karot",
                        tagalogTitle = "Pagpapanatili ng Malusog na Dahon at Ugat",
                        severity = "Tamang Pangangalaga (Optimal)",
                        badgeColor = LushGreen,
                        directAnswer = "Walang nakitang peste sa taniman. Ipagpatuloy ang regular na pag-iinspeksyon at pag-iingat sa kalinisan ng kamada.",
                        actionSteps = listOf(
                            "Suriin ang mga dahon linggu-linggo.",
                            "Panatilihing malinis ang mga damo sa paligid."
                        ),
                        materialsNeeded = listOf("Hand lens"),
                        criticalRule = "Ang regular na pagmomonitor ay pumipigil sa biglaang pagdami ng mga insekto."
                    )
                }
            }
        }

        "diseases" -> {
            when {
                selectedOptions.contains("carrot_dis_alternaria_blight") || selectedOptions.contains("carrot_dis_cercospora") -> {
                    DirectCropSolution(
                        title = "Alternaria at Cercospora Leaf Blight sa Karot",
                        tagalogTitle = "Solusyon sa Nangingitim na Dahon at Pagkaluoy ng Rosette",
                        severity = "Mataas (Loss of Foliage Strength)",
                        badgeColor = DangerRed,
                        directAnswer = "Ang Alternaria dauci ay fungal disease na nagdudulot ng maitim na batik na may dilaw na paligid sa mga dahon. Kapag nawalan ng dahon ang karot, titigil ang photosynthesis at hindi na lalaki ang taproot, at mapuputol ang dahon kapag binunot sa anihan.",
                        actionSteps = listOf(
                            "Mag-isprey ng preventive Copper Hydroxide o Mancozeb fungicide tuwing maulang panahon tuwing 7–10 araw.",
                            "Iwasan ang sprinkler irrigation sa dapit-hapon upang mabilis matuyo ang mga dahon sa sikat ng araw.",
                            "Gumamit lamang ng sertipikadong malinis na binhi mula sa mapagkakatiwalaang kumpanya (tulad ng New Kuroda ng East-West Seed).",
                            "Panatilihin ang sapat na agwat sa pagitan ng mga halaman (5–8 cm) para sa maayos na sirkulasyon ng hangin."
                        ),
                        materialsNeeded = listOf(
                            "Copper Hydroxide o Mancozeb fungicide",
                            "Knapsack sprayer",
                            "Drip irrigation lines"
                        ),
                        criticalRule = "Kailangan ng malalakas at buong dahon upang maayos na mabunot ang karot mula sa ilalim ng lupa nang hindi nababali."
                    )
                }
                selectedOptions.contains("carrot_dis_root_rot") || selectedOptions.contains("carrot_dis_bacterial_softrot") -> {
                    DirectCropSolution(
                        title = "Fungal at Bacterial Root Rot sa Karot",
                        tagalogTitle = "Agarang Lunas sa Nabubulok at Mabahong Ugat sa Lupa",
                        severity = "Kritikal (Total Root Loss)",
                        badgeColor = DangerRed,
                        directAnswer = "Ang pagkabulok ng ugat sa ilalim ng lupa ay sanhi ng Rhizoctonia, Sclerotinia, o Pectobacterium kapag ang kamada ay barado ang tubig o labis na mamasa-masa.",
                        actionSteps = listOf(
                            "Agad na bunutin at sirain ang lahat ng nabubulok na halaman; huwag iwan sa lupa ang mga bulok na piraso.",
                            "Palalimin ang kanal ng drainage sa pagitan ng mga kamada (30 cm lalim) upang hindi magtagal ang tubig-baha.",
                            "Maglagay ng Trichoderma microbial drench sa lupa sa paligid ng mga natitirang malulusog na halaman.",
                            "Magsagawa ng 2–3 taong crop rotation palayo sa mga root crops."
                        ),
                        materialsNeeded = listOf(
                            "Trichoderma harzianum bio-drench",
                            "Drainage shovel",
                            "Lalagyan para sa pagtatapon ng bulok na ugat"
                        ),
                        criticalRule = "Huwag kailanman magtanim muli ng karot sa kamadang katatapos lamang tamaan ng root rot."
                    )
                }
                else -> {
                    DirectCropSolution(
                        title = "Malusog at Walang Sakit na Tanim na Karot",
                        tagalogTitle = "Pagpapanatili ng Malinis na Kamada at Maayos na Daloy ng Hangin",
                        severity = "Tamang Pangangalaga (Optimal)",
                        badgeColor = LushGreen,
                        directAnswer = "Walang nakitang sakit sa taniman. Panatilihin ang maayos na daloy ng hangin at iwasan ang labis na pagkababad sa tubig.",
                        actionSteps = listOf(
                            "Panatilihing malinis ang mga kanal sa pagitan ng kamada.",
                            "Diligan sa umaga upang matuyo ang dahon sa araw."
                        ),
                        materialsNeeded = listOf("Mulch", "Disinfected tools"),
                        criticalRule = "Ang sikat ng araw at maayos na drainage ang pinakamabisang pananggalang sa mga fungal disease."
                    )
                }
            }
        }

        "field_care" -> {
            when {
                selectedOptions.contains("carrot_care_green_shoulders") -> {
                    DirectCropSolution(
                        title = "Berdeng Balikat ng Karot (Green Shoulder Exposure)",
                        tagalogTitle = "Solusyon: Pagbubungkal at Pagtatambak ng Lupa (Earthing-Up)",
                        severity = "Katamtaman (Market Downgrade & Bitterness)",
                        badgeColor = AlertAmber,
                        directAnswer = "Nangyayari ang 'green shoulder' kapag ang itaas na bahagi ng lumalaking ugat ng karot ay nabilad sa direktang sikat ng araw. Gumagawa ito ng chlorophyll at mga mapapait na compound, na nagpapababa sa presyo sa palengke.",
                        actionSteps = listOf(
                            "Magsagawa ng banayad na 'hilling-up' o pagtatambak ng 2–3 cm ng pinong buhaghag na lupa o compost sa ibabaw ng mga balikat ng karot.",
                            "Takpan ang anumang lumilitaw na kulay kahel na bahagi upang manatiling nakabaon sa dilim ng lupa.",
                            "Maglagay ng manipis na dayami o mulch sa ibabaw ng hanay upang hindi maanod ng ulan o pandilig ang lupa.",
                            "Gawin ito nang maingat upang hindi masugatan ang katawan ng ugat."
                        ),
                        materialsNeeded = listOf(
                            "Pinong buhaghag na lupa o compost",
                            "Maliit na garden trowel",
                            "Dayami o mulch"
                        ),
                        criticalRule = "Takpan agad ang mga lumilitaw na balikat ng karot sa sandaling makita ang kulay kahel sa ibabaw ng lupa."
                    )
                }
                selectedOptions.contains("carrot_care_unthinned") -> {
                    DirectCropSolution(
                        title = "Masyadong Masinsin / Hindi Na-thin na mga Karot",
                        tagalogTitle = "Kritikal na Hakbang: Paghihimay (Thinning) Patungong 5–8 cm Agwat",
                        severity = "Mataas (Loss of Root Size)",
                        badgeColor = DangerRed,
                        directAnswer = "Ang karot ay direct-seeded crop na may napakaliliit na binhi. Kung hindi hihimayin (unthinned), mag-aagawan ang mga halaman sa espasyo at nutrisyon, kaya't magiging kasing-payat lamang ng tingting o lapis ang mga ugat at magkakapilipit-lipit.",
                        actionSteps = listOf(
                            "Magsagawa ng thinning sa ika-3 hanggang ika-4 na linggo pagkasibol kapag may taas nang 5 cm ang mga punla.",
                            "Dahan-dahang bunutin ang mahihinang punla upang mag-iwan ng 5 hanggang 8 cm na espasyo sa pagitan ng bawat natitirang malusog na karot.",
                            "Diligan nang bahagya ang kamada bago mag-thin upang lumambot ang lupa at hindi mabaluktot ang ugat ng katabing maiiwang halaman.",
                            "Maaaring kainin bilang 'baby greens' sa salad ang mga naalis na maliliit na punla."
                        ),
                        materialsNeeded = listOf(
                            "Pandilig para palambutin ang lupa bago mag-thin",
                            "Matalas na gunting o maingat na mga kamay"
                        ),
                        criticalRule = "Ang paghihimay (thinning) ay hindi opsyonal—kung walang thinning, walang malalaking karot na maaani."
                    )
                }
                selectedOptions.contains("carrot_care_rocky_compacted") -> {
                    DirectCropSolution(
                        title = "Mabato at Makapal na Lupa (Deformed Roots)",
                        tagalogTitle = "Paghahanda ng Malalim at Buhaghag na Raised Beds (25–30 cm)",
                        severity = "Mataas (High Deformation)",
                        badgeColor = AlertAmber,
                        directAnswer = "Ang taproot ng karot ay nangangailangan ng zero resistance habang bumabaon pababa. Kapag tumama ang lumalaking dulo sa bato, matigas na clod ng lupa, o hardpan layer, ito ay mababali, magsasanga, o magiging pilipit at maikli.",
                        actionSteps = listOf(
                            "Araruhin at bungkalin nang malalim ang lupa sa 25 hanggang 30 cm bago magtanim.",
                            "Salain o mano-manong alisin ang lahat ng malalaking bato, ugat, at matitigas na tipak ng lupa sa kamada.",
                            "Maghalo ng buhangin (river sand), carbonized rice hull (CRH), at pinong compost upang gawing napakabuhaghag ang kamada.",
                            "Gumawa ng raised bed na may taas na 20–25 cm."
                        ),
                        materialsNeeded = listOf(
                            "Asarol at pala para sa malalim na tillage",
                            "River sand at CRH",
                            "Salandra para sa pag-aalis ng bato"
                        ),
                        criticalRule = "Kung mabato ang inyong lupain, magtanim sa matataas na wooden raised beds na puno ng pinong sandy loam mix."
                    )
                }
                else -> {
                    DirectCropSolution(
                        title = "Tamang Pangangalaga sa Kamada ng Karot",
                        tagalogTitle = "Pagpapanatili ng Malinis at Buhaghag na Taniman",
                        severity = "Tamang Pangangalaga (Optimal)",
                        badgeColor = LushGreen,
                        directAnswer = "Panatilihing malinis sa damo ang kamada lalo na sa unang buwan dahil mahina makipagkumpetensya ang maliliit na punla ng karot.",
                        actionSteps = listOf(
                            "Mano-manong damuhan ang paligid ng mga hanay tuwing 2 linggo.",
                            "Panatilihing natatakpan ng lupa ang balikat ng mga karot."
                        ),
                        materialsNeeded = listOf("Hand weeder"),
                        criticalRule = "Mag-ingat habang nagdadamot upang hindi masugatan ang mababaw na leeg ng karot."
                    )
                }
            }
        }

        "harvest" -> {
            DirectCropSolution(
                title = "Tamang Yugto at Pamamaraan ng Pag-aani ng Karot",
                tagalogTitle = "Gabay sa Masaganang Pitas, Paghuhugas, at Pag-iimbak ng Karot",
                severity = "Pag-aani (Harvest Ready)",
                badgeColor = LushGreen,
                directAnswer = "Ang karot ay inaani sa 90 hanggang 110 araw mula paghasik. Ang handang karot ay may diameter na 2–4 cm sa balikat, may haba na 15–20 cm, may matingkad na kulay kahel, at may timbang na 80–150 gramo bawat piraso.",
                actionSteps = listOf(
                    "Bago mag-ani, diligan nang bahagya ang kamada upang lumambot ang mabuhanging lupa at hindi mabali ang ugat habang binubunot.",
                    "Gumamit ng spading fork o tinidor ng lupa upang luwagan ang lupa sa gilid ng hanay (10–15 cm mula sa puno).",
                    "Hawakan nang mahigpit ang base ng mga dahon sa pinakatuktok ng ugat at dahan-dahang hilahin nang patayo.",
                    "Hugasan kaagad sa malinis na tubig ang kumapit na lupang bukid upang lumitaw ang matingkad na kulay kahel (roots are sold washed and clean in Philippine markets).",
                    "Ihiwalay ang mga may bitak, pilipit, o may sira ng insekto; i-salansan sa ventilated plastic crates o mesh sacks.",
                    "Kung iimbak, gupitin ang dahon (mag-iwan ng 1 cm stem) at ilagay sa malamig na lugar (0–4°C na may mataas na humidity para sa 2–4 na linggong kasariwaan)."
                ),
                materialsNeeded = listOf(
                    "Spading fork / garden fork",
                    "Paghuhugasan ng tubig at malinis na espongha",
                    "Ventilated crates o sako"
                ),
                criticalRule = "Huwag hilahin nang marahas ang mga dahon kapag matigas ang lupa dahil mapipigtas ang dahon at maiiwan ang ugat sa ilalim."
            )
        }

        else -> null
    }
}
