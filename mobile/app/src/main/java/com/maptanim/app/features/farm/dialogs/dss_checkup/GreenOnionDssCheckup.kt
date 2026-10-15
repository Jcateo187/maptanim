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
 * for Green Onion / Scallion / Dahon ng Sibuyas (Allium fistulosum) in Philippine
 * commercial farm beds and urban container gardens based on DA-BPI & MapTanim standards.
 */
fun getGreenOnionCheckUpActivities(): List<CheckUpActivity> {
    return listOf(
        CheckUpActivity(
            id = "watering",
            title = "Pagdidilig at Kahalumigmig ng Dahon ng Sibuyas",
            tagalogSubtitle = "Suriin ang moisture sa mababaw na fibrous roots at tigas ng tubular na dahon",
            icon = Icons.Default.WaterDrop,
            options = listOf(
                CheckUpOption(
                    id = "green_onion_water_normal",
                    title = "Katamtaman at Regular na Patubig",
                    tagalogDesc = "Mamasa-masa ang 10–15 cm lalim ng buhaghag na kamada; dinidilig 2–3 beses kada linggo sa umaga",
                    isIdeal = true
                ),
                CheckUpOption(
                    id = "green_onion_water_stress",
                    title = "Tagtuyot / Tuyot na Lupa at Sunog na Dulo (Tip Burn)",
                    tagalogDesc = "Tuyot ang lupa; laylay ang mga dahon at naninilaw/tuyo ang mga pinakadulo (water deficit stress)"
                ),
                CheckUpOption(
                    id = "green_onion_waterlogged",
                    title = "Sobrang Tubig / Baha at Putik sa Kamada (Root Rot Hazard)",
                    tagalogDesc = "Naiipon ang tubig sa paligid ng puno; nagiging madulas at naninilaw ang base ng halaman (basal plate decay)"
                ),
                CheckUpOption(
                    id = "green_onion_water_splash",
                    title = "Pagtalsik ng Putik sa Puting Tangkay (Mud Splash)",
                    tagalogDesc = "Nababasa at natatalamsikan ng putik ang pinaputing tangkay dahil sa malakas na buhos ng tubig"
                )
            )
        ),
        CheckUpActivity(
            id = "fertilizing",
            title = "Abono at Nutrisyon ng Dahon ng Sibuyas",
            tagalogSubtitle = "Suriin ang balanse ng Nitroheno para sa dahon at Potash para sa tibay ng tangkay",
            icon = Icons.Default.Spa,
            options = listOf(
                CheckUpOption(
                    id = "green_onion_fert_balanced",
                    title = "Balanseng Nutrisyon (Compost + 14-14-14 / MOP)",
                    tagalogDesc = "Matingkad na berdeng tubular na dahon, makakapal at matitigas na puting tangkay na may masaganang suwi",
                    isIdeal = true
                ),
                CheckUpOption(
                    id = "green_onion_fert_nitrogen_def",
                    title = "Kulang sa Nitroheno (Maputlang Dilaw at Payat na Tangkay)",
                    tagalogDesc = "Maputlang berde o madilaw ang lumang dahon; manipis at mabagal magsuwi ang mga halaman"
                ),
                CheckUpOption(
                    id = "green_onion_fert_excess_nitrogen",
                    title = "Sobra sa Nitroheno (Masyadong Malambot at Laylay na Dahon)",
                    tagalogDesc = "Labis na madilim ang berde ngunit malalambot at mabilis mabali ang mga dahon; madaling dapuan ng uod at amag"
                ),
                CheckUpOption(
                    id = "green_onion_fert_potassium_def",
                    title = "Kulang sa Potassium / Potash (Tip Scorch & Weak Stems)",
                    tagalogDesc = "Sunog at kulay abo ang gilid ng mga dulo ng dahon; malambot ang tindig at walang lutong ang tangkay"
                )
            )
        ),
        CheckUpActivity(
            id = "blanching_care",
            title = "Pagtatambak ng Lupa at Pagsusuwi (Blanching & Earthing-Up)",
            tagalogSubtitle = "Suriin ang haba ng puting tangkay at dami ng suwi bawat kumpol",
            icon = Icons.Default.Agriculture,
            options = listOf(
                CheckUpOption(
                    id = "green_onion_care_hilled",
                    title = "Maayos na Na-earth-up (10–15 cm Maputing Tangkay)",
                    tagalogDesc = "Regular na natambakan ng pinong compost o buhaghag na lupa; mahaba, matamis, at malutong ang puting shaft",
                    isIdeal = true
                ),
                CheckUpOption(
                    id = "green_onion_care_unhilled",
                    title = "Hindi Na-tambakan ng Lupa (Maikling Puting Tangkay)",
                    tagalogDesc = "Nakalitaw ang buong tangkay sa araw kaya berde at makunat ang halos buong katawan; maikli ang puting bahagi"
                ),
                CheckUpOption(
                    id = "green_onion_care_overcrowded",
                    title = "Masyadong Masinsin / Overcrowded na Kumpol (>10 Suwi)",
                    tagalogDesc = "Napakadaming suwi sa isang tumpok; nagiging kasing-payat na lamang ng posporo ang mga tangkay dahil sa sikip"
                )
            )
        ),
        CheckUpActivity(
            id = "pests",
            title = "Mga Peste sa Dahon at Tangkay ng Sibuyas",
            tagalogSubtitle = "Suriin ang mga hungkag na dahon para sa thrips, harabas, at leafminer",
            icon = Icons.Default.BugReport,
            options = listOf(
                CheckUpOption(
                    id = "green_onion_pest_none",
                    title = "Malinis at Walang Sintomas ng Peste",
                    tagalogDesc = "Makinis at buo ang bawat dahon; walang pilak na batik o butas ng uod",
                    isIdeal = true
                ),
                CheckUpOption(
                    id = "green_onion_pest_thrips",
                    title = "Onion Thrips (Pinong Pilak na Batik / Silvery Flecks)",
                    tagalogDesc = "Maliliit na mapuputi at tila pilak na batik sa ibabaw ng dahon; natutuyo at nagkukulay abo ang dulo ng dahon"
                ),
                CheckUpOption(
                    id = "green_onion_pest_armyworm",
                    title = "Harabas / Beet Armyworm (Uod sa Loob ng Hungkag na Dahon)",
                    tagalogDesc = "May uod na nakakulong sa loob ng tubular na dahon at kinakain ang berde mula sa loob; naiiwan ang manipis na balat"
                ),
                CheckUpOption(
                    id = "green_onion_pest_leafminer",
                    title = "Leafminer (Mapuputing Paikot-ikot na Guhit)",
                    tagalogDesc = "May tila ahas na mapuputing linya (serpentine trails) sa balat ng berdeng dahon"
                ),
                CheckUpOption(
                    id = "green_onion_pest_cutworm",
                    title = "Cutworm (Pinutol na Tangkay sa Pantay-Lupa)",
                    tagalogDesc = "Napuputol ang buong tangkay malapit sa lupa tuwing gabi; nakatago ang matabang uod sa ilalim ng lupa sa araw"
                )
            )
        ),
        CheckUpActivity(
            id = "diseases",
            title = "Mga Sakit at Amag ng Dahon ng Sibuyas",
            tagalogSubtitle = "Suriin ang mga sugat, amag, at kalagayan ng ugat sa kamada",
            icon = Icons.Default.Coronavirus,
            options = listOf(
                CheckUpOption(
                    id = "green_onion_dis_none",
                    title = "Malusog at Walang Amag o Batik",
                    tagalogDesc = "Walang ube o kayumangging sugat; matatag at malinis ang base ng puno",
                    isIdeal = true
                ),
                CheckUpOption(
                    id = "green_onion_dis_purple_blotch",
                    title = "Purple Blotch (Ube at Kayumangging Malalaking Sugat)",
                    tagalogDesc = "Pabilog na sugat na may kulay ube sa gitna at dilaw na paligid; nababali ang dahon sa mismong sugat"
                ),
                CheckUpOption(
                    id = "green_onion_dis_downy_mildew",
                    title = "Downy Mildew (Kulay Abong Bulak / Fuzzy Gray Mold)",
                    tagalogDesc = "May abong amag sa dahon sa mahamog at malamig na umaga; mabilis matuyo at mamatay ang mga dahon"
                ),
                CheckUpOption(
                    id = "green_onion_dis_basal_rot",
                    title = "Fusarium Basal Rot (Paninilaw at Pagkabulok ng Ugat)",
                    tagalogDesc = "Naninilaw mula sa dulo pababa; mabaho at malambot ang base ng puno at nangingitim/nabubulok ang mga ugat"
                )
            )
        ),
        CheckUpActivity(
            id = "bed_care",
            title = "Kalagayan ng Lupa at Pamamahala sa Damo",
            tagalogSubtitle = "Suriin ang kalinisan ng kamada at aeration ng mabuhanging lupa",
            icon = Icons.Default.Spa,
            options = listOf(
                CheckUpOption(
                    id = "green_onion_bed_optimal",
                    title = "Buhaghag na Sandy Loam na Walang Damo",
                    tagalogDesc = "Madaling lumusot ang tubig at hangin; may carbonized rice hull at compost na nagpapanatili ng sigla",
                    isIdeal = true
                ),
                CheckUpOption(
                    id = "green_onion_bed_weeds",
                    title = "Masukal sa Damo (Sakal ang Mababaw na Ugat)",
                    tagalogDesc = "Kinakain ng matatayog na damo ang liwanag at nutrisyon; mahina lumaki ang payat na dahon ng sibuyas"
                ),
                CheckUpOption(
                    id = "green_onion_bed_compacted",
                    title = "Matigas at Maputik na Clay (Hindi Makasanga ang Ugat)",
                    tagalogDesc = "Mabilis mamuo ang matitigas na tipak ng lupa kapag tuyo at nagpuputik kapag basa; bansot ang mga suwi"
                )
            )
        ),
        CheckUpActivity(
            id = "harvest",
            title = "Yugto at Pamamaraan ng Pag-aani",
            tagalogSubtitle = "Pumili ng pamamaraan ng pag-aani para sa pampalengke o tuloy-tuloy na gamit",
            icon = Icons.Default.ContentCut,
            options = listOf(
                CheckUpOption(
                    id = "green_onion_harvest_whole",
                    title = "Buong Bunot (Whole Plant Bunching - 45–60 DAP)",
                    tagalogDesc = "Binubunot ang buong puno kasama ang ugat para sa pagbubungkos at pagbenta sa palengke"
                ),
                CheckUpOption(
                    id = "green_onion_harvest_ratoon",
                    title = "Cut-and-Come-Again (Paggupit 2.5 cm sa Ibabaw ng Lupa)",
                    tagalogDesc = "Pinuputol lamang ang mga dahon upang muling sumibol ang bago sa loob ng 10–15 araw"
                ),
                CheckUpOption(
                    id = "green_onion_harvest_division",
                    title = "Paghahati ng Kumpol para sa Bagong Taniman",
                    tagalogDesc = "Binubunot ang malalaking kumpol at pinaghihiwalay ang mga suwi para itanim sa bagong plots"
                )
            )
        )
    )
}

/**
 * Diagnostic logic providing instant decision support for Green Onion / Scallion.
 */
fun solveGreenOnionProblem(
    activityId: String,
    selectedOptions: Set<String>
): DirectCropSolution? {
    if (selectedOptions.isEmpty()) return null

    return when (activityId) {
        "watering" -> {
            when {
                selectedOptions.contains("green_onion_water_stress") -> {
                    DirectCropSolution(
                        title = "Kakulangan sa Tubig at Tip Burn sa Dahon ng Sibuyas",
                        tagalogTitle = "Agarang Lunas sa Tuyot na Kamada at Sunog na Dulo ng Dahon",
                        severity = "Mataas (Moisture Stress)",
                        badgeColor = AlertAmber,
                        directAnswer = "Ang dahon ng sibuyas ay may napakababaw na fibrous root system (10–15 cm lalim). Kapag natuyo ang pinakaibabaw na lupa, agad itong dumanas ng water stress na nagdudulot ng panunuyo ng mga dulo ng dahon (tip burn) at panlalata ng hungkag na tangkay.",
                        actionSteps = listOf(
                            "Magdilig agad nang banayad ngunit malalim hanggang sa mabasang muli ang 15 cm lalim ng lupa.",
                            "Itakda ang pagdidilig tuwing 6:00 hanggang 8:00 ng umaga, 2 hanggang 3 beses bawat linggo.",
                            "Maglagay ng 3–5 cm na manipis na dayami ng palay (rice straw mulch) o carbonized rice hull (CRH) sa ibabaw ng kamada upang mapanatili ang kahalumigmig.",
                            "Gupitin ang mga tuyot at kayumangging dulo ng dahon gamit ang malinis na gunting upang hindi pumasok ang amag."
                        ),
                        materialsNeeded = listOf(
                            "Pandilig na may pinong rose head o drip line",
                            "Dayami ng palay (rice straw) o CRH para sa mulch",
                            "Gunting ng hardin"
                        ),
                        criticalRule = "Huwag magdilig sa katanghaliang tapat habang mainit ang sikat ng araw dahil 'maluluto' ang mababaw na ugat."
                    )
                }
                selectedOptions.contains("green_onion_waterlogged") -> {
                    DirectCropSolution(
                        title = "Sobrang Tubig at Banta ng Basal Plate Rot",
                        tagalogTitle = "Kritikal na Pagsagip sa Binabahang Kamada ng Sibuyas",
                        severity = "Kritikal (Rot Hazard)",
                        badgeColor = DangerRed,
                        directAnswer = "Ang green onion ay hindi nabubuhay sa nakatayong tubig o putik. Kapag nababad ang basal plate (pinakapuno sa ilalim ng lupa) nang mahigit 24 oras, mabilis itong mapasok ng bacteria at Fusarium fungi na nagdudulot ng mabaho at malambot na pagkabulok.",
                        actionSteps = listOf(
                            "Itigil muna agad ang anumang pagdidilig hanggang sa matuyo ang ibabaw ng kamada.",
                            "Maghukay ng 20 cm drainage trenches o kanal sa paligid ng kamada upang mabilis na umagos ang naipong tubig.",
                            "Bungkalin nang napakaingat ang paligid ng mga hanay gamit ang hand fork upang makapasok ang hangin (aeration).",
                            "I-drench ang lupa ng Trichoderma harzianum bio-fungicide solution upang masugpo ang fungal root rot.",
                            "Kung sa container nakatanim, tiyaking may malalaking butas sa ilalim ang mga paso at iangat sa stand."
                        ),
                        materialsNeeded = listOf(
                            "Pala para sa paggawa ng drainage trench",
                            "Hand fork para sa soil aeration",
                            "Trichoderma harzianum bio-control agent"
                        ),
                        criticalRule = "Laging magtanim sa raised beds na may taas na di-bababa sa 15–20 cm kung magtatanim sa panahon ng tag-ulan."
                    )
                }
                selectedOptions.contains("green_onion_water_splash") -> {
                    DirectCropSolution(
                        title = "Pagtalsik ng Putik sa Puting Tangkay (Mud Splash)",
                        tagalogTitle = "Pag-iwas sa Kontaminasyon at Fungal Entry",
                        severity = "Mababa (Cosmetic & Spore Risk)",
                        badgeColor = LushGreen,
                        directAnswer = "Ang malakas na buhos ng tubig mula sa hose ay nagtatalsik ng putik sa mga singit ng dahon at pinaputing tangkay, na nagpapadilim sa kulay at nagiging daan ng fungal spores mula sa lupa.",
                        actionSteps = listOf(
                            "Palitan ang nozzle ng pandilig ng pinong sprinkler head o gumamit ng drip irrigation hose sa mismong base ng lupa.",
                            "Maglagay ng mulch na carbonized rice hull (CRH) sa ibabaw ng kamada upang walang talsik ng putik tuwing nagdidilig o umuulan.",
                            "Hugasan ng malinis na spray ang mga tangkay kung may kumapit na putik bago magtanghali."
                        ),
                        materialsNeeded = listOf(
                            "Fine-mist rose head para sa pandilig",
                            "Carbonized Rice Hull (CRH)"
                        ),
                        criticalRule = "Iwasan ang paggamit ng malakas na presyon ng hose sa mga murang dahon ng sibuyas."
                    )
                }
                else -> {
                    DirectCropSolution(
                        title = "Tamang Patubig sa Dahon ng Sibuyas",
                        tagalogTitle = "Optimal na Kahalumigmig para sa Masaganang Suwi",
                        severity = "Tamang Pangangalaga (Optimal)",
                        badgeColor = LushGreen,
                        directAnswer = "Nasa tamang moisture level ang inyong kamada! Panatilihin ang ganitong pamamahala upang manatiling malutong, matamis, at makulay ang mga dahon.",
                        actionSteps = listOf(
                            "Ipagpatuloy ang pagdidilig tuwing umaga 2–3 beses kada linggo.",
                            "Bawasan ang patubig 3 araw bago mag-ani upang humaba ang shelf life ng mga tangkay."
                        ),
                        materialsNeeded = listOf("Pangkaraniwang pandilig"),
                        criticalRule = "Panatilihing basa ang lupa ngunit hindi nagpuputik."
                    )
                }
            }
        }

        "fertilizing" -> {
            when {
                selectedOptions.contains("green_onion_fert_nitrogen_def") -> {
                    DirectCropSolution(
                        title = "Kakulangan sa Nitroheno (Nitrogen Deficiency)",
                        tagalogTitle = "Pagpapabilis ng Pagsusuwi at Pagpapanumbalik ng Luntiang Kulay",
                        severity = "Mataas (Stunted Growth)",
                        badgeColor = AlertAmber,
                        directAnswer = "Ang dahon ng sibuyas ay mabigat na mamimili ng nitroheno para sa mabilis na foliar expansion at pagsusuwi. Ang paninilaw ng mga lumang dahon at pagiging payat ng mga tangkay ay palatandaan ng agarang pangangailangan sa N.",
                        actionSteps = listOf(
                            "Maglagay ng side-dress ng Ammonium Sulfate (21-0-0) o Urea (46-0-0) sa daming 5–10 gramo bawat linear meter, 5 cm ang layo sa puno.",
                            "Bilang organikong alternatibo, mag-drench ng Fermented Plant Juice (FPJ) o Fish Amino Acid (FAA) na tinunaw sa tubig (2 kutsara bawat litro) tuwing 7 araw.",
                            "Diligan agad ang kamada matapos mag-abono upang matunaw ang sustansya patungo sa ugat.",
                            "Magdagdag ng 2 dakot ng vermicompost sa paligid ng bawat kumpol."
                        ),
                        materialsNeeded = listOf(
                            "Fish Amino Acid (FAA) o FPJ",
                            "Vermicompost o decomposed chicken manure",
                            "14-14-14 o Urea (opsyonal)"
                        ),
                        criticalRule = "Huwag idikit ang butil ng abono sa pinakakatawan ng sibuyas upang hindi masunog ang malambot na tangkay."
                    )
                }
                selectedOptions.contains("green_onion_fert_excess_nitrogen") -> {
                    DirectCropSolution(
                        title = "Sobra sa Nitroheno (Excess Foliar Nitrogen)",
                        tagalogTitle = "Pagpapatigas ng Malambot at Laylay na mga Dahon",
                        severity = "Katamtaman (Disease & Lodging Risk)",
                        badgeColor = AlertAmber,
                        directAnswer = "Ang labis na chemical nitrogen ay nagdudulot ng mabilis ngunit mahinang paglaki—nagiging sobrang matubig at malambot ang balat ng dahon, kaya't madaling mabali (lodging) at lapitan ng harabas at purple blotch.",
                        actionSteps = listOf(
                            "Itigil muna agad ang lahat ng aplikasyon ng Urea o ammonium fertilizers.",
                            "Maglagay ng Muriate of Potash (0-0-60) o kahoy na abo (wood ash) upang patigasin ang cellular walls ng halaman.",
                            "Mag-spray ng Calcium solution (fermented eggshell and vinegar - Ca-WCA) upang maging malutong at matatag ang tangkay.",
                            "Panatilihin ang sapat na sikat ng araw upang maiproseso ang naipong sustansya."
                        ),
                        materialsNeeded = listOf(
                            "Muriate of Potash o Wood Ash",
                            "Water-soluble Calcium (Ca-WCA)"
                        ),
                        criticalRule = "Balansehin ang N sa pamamagitan ng sapat na Potassium upang hindi maging malata ang ani."
                    )
                }
                selectedOptions.contains("green_onion_fert_potassium_def") -> {
                    DirectCropSolution(
                        title = "Kakulangan sa Potassium (Potash Deficiency)",
                        tagalogTitle = "Pagpapalutong ng Tangkay at Proteksyon sa Tip Scorch",
                        severity = "Katamtaman (Weak Stem Tissue)",
                        badgeColor = AlertAmber,
                        directAnswer = "Ang potassium ang nagbibigay ng lutong sa puting tangkay at tibay laban sa tagtuyot at sakit. Ang panunuyo ng mga gilid ng dahon ay senyales ng kakulangan sa K.",
                        actionSteps = listOf(
                            "Maglagay ng side-dress ng Muriate of Potash (0-0-60) o Potassium Nitrate.",
                            "Magbudbod ng pinong abo ng kahoy (wood ash, 1 dakot bawat plot) na natural na mayaman sa potassium at calcium.",
                            "Mag-spray ng Fermented Fruit Juice (FFJ mula sa saging) sa dahon tuwing linggo."
                        ),
                        materialsNeeded = listOf(
                            "Muriate of Potash o Wood Ash",
                            "Fermented Fruit Juice (FFJ)"
                        ),
                        criticalRule = "Ihalo nang marahan ang abo sa lupa bago diligan."
                    )
                }
                else -> {
                    DirectCropSolution(
                        title = "Balanseng Nutrisyon ng Dahon ng Sibuyas",
                        tagalogTitle = "Napakalusog na Pagsusuwi at Matigas na Tangkay",
                        severity = "Tamang Pangangalaga (Optimal)",
                        badgeColor = LushGreen,
                        directAnswer = "Natutugunan nang husto ang pangangailangan ng inyong mga halaman sa sustansya. Ipagpatuloy ang regular na paglalagay ng organikong pataba.",
                        actionSteps = listOf(
                            "Maglagay ng manipis na vermicompost top-dress tuwing ika-3 linggo.",
                            "Mag-drench ng seaweed extract o compost tea isang beses bawat buwan."
                        ),
                        materialsNeeded = listOf("Vermicompost"),
                        criticalRule = "Iwasan ang biglaang labis na kemikal na pataba bago ang anihan."
                    )
                }
            }
        }

        "blanching_care" -> {
            when {
                selectedOptions.contains("green_onion_care_unhilled") -> {
                    DirectCropSolution(
                        title = "Hindi Na-tambakan ng Lupa (Unhilled / Short White Shaft)",
                        tagalogTitle = "Kritikal na Hakbang: Pagsasagawa ng Earthing-Up (Blanching)",
                        severity = "Katamtaman (Commercial Quality Loss)",
                        badgeColor = AlertAmber,
                        directAnswer = "Ang mataas na halaga ng green onion sa palengke ay nakasalalay sa haba ng makinis at maputing tangkay nito (blanched pseudostem). Kung nakalantad ito sa araw, magiging berde at makunat ang tangkay. Sa pamamagitan ng pagtatambak ng lupa, nananatili itong purong puti, malutong, at matamis.",
                        actionSteps = listOf(
                            "Maghanda ng buhaghag na pinong lupa na may halong 50% carbonized rice hull o compost.",
                            "Marahang itambak ang lupa sa paligid ng mga tangkay sa taas na 3 hanggang 5 cm mula sa lupa.",
                            "Ulitin ang pagtatambak pagkaraan ng 10 hanggang 14 araw habang patuloy na tumataas ang halaman.",
                            "Mag-ingat na huwag matabunan ang pinakagitnang usbong (growing point) kung saan lumalabas ang mga bagong dahon.",
                            "Kung sa paso nakatanim, magdagdag ng patong ng potting mix o magbalot ng cardboard sleeve sa paligid ng base."
                        ),
                        materialsNeeded = listOf(
                            "Pinong buhaghag na lupa o compost",
                            "Carbonized Rice Hull (CRH)",
                            "Hand trowel"
                        ),
                        criticalRule = "Gawin ang earthing-up kapag tuyo ang lupa upang hindi pumasok ang putik sa gitna ng mga dahon."
                    )
                }
                selectedOptions.contains("green_onion_care_overcrowded") -> {
                    DirectCropSolution(
                        title = "Overcrowded na Kumpol (>10 Suwi Bawat Puno)",
                        tagalogTitle = "Paghahati at Paglilipat-Tanim ng mga Suwi (Division)",
                        severity = "Mataas (Thin Pencil Stems)",
                        badgeColor = AlertAmber,
                        directAnswer = "Kapag lumagpas sa 8–10 suwi ang isang tumpok nang hindi binabawasan, magsisiksikan ang mga ugat at magiging kasing-payat na lamang ng tingting o posporo ang mga tangkay dahil sa labis na kompetisyon.",
                        actionSteps = listOf(
                            "Diligan ang kamada 1 oras bago bunutin upang lumambot ang lupa.",
                            "Marahang bunutin ang buong kumpol gamit ang hand fork nang buo ang mga ugat.",
                            "Paghiwalayin ang kumpol sa maliliit na grupo na may tig-2 hanggang 3 malulusog na tangkay.",
                            "Gupitin ang itaas na 1/3 ng mga dahon upang mabawasan ang transpiration.",
                            "Itanim muli ang mga suwi sa agwat na 12–15 cm sa bagong kamada o bagong mga paso.",
                            "Maaaring anihin at kainin ang mga labis na suwi."
                        ),
                        materialsNeeded = listOf(
                            "Hand fork para sa maingat na pagbunot",
                            "Gunting para sa leaf pruning",
                            "Bagong kamada o mga paso"
                        ),
                        criticalRule = "Ang regular na paghahati tuwing 2–3 buwan ay nagpapanatili ng sigla at walang-katapusang produksyon ng scallions."
                    )
                }
                else -> {
                    DirectCropSolution(
                        title = "Mahusay na Blanching at Tindig ng mga Tangkay",
                        tagalogTitle = "Napakataas na Kalidad ng Puting Tangkay",
                        severity = "Tamang Pangangalaga (Optimal)",
                        badgeColor = LushGreen,
                        directAnswer = "Napakaganda ng inyong earthing-up management. Makakakuha kayo ng mahahaba, mapuputi, at malulutong na scallion stalks na may mataas na commercial grade.",
                        actionSteps = listOf(
                            "Panatilihing natatakpan ang base ng halaman.",
                            "Huwag galawin ang lupa malapit sa ugat 1 linggo bago mag-ani."
                        ),
                        materialsNeeded = listOf("Wala"),
                        criticalRule = "Panatilihing tuyo ang ibabaw ng mulch."
                    )
                }
            }
        }

        "pests" -> {
            when {
                selectedOptions.contains("green_onion_pest_thrips") -> {
                    DirectCropSolution(
                        title = "Onion Thrips (Thrips tabaci) Infestation",
                        tagalogTitle = "Agad na Pagsugpo sa Thrips na Sumisipsip sa Dahon ng Sibuyas",
                        severity = "Mataas (High Infestation Hazard)",
                        badgeColor = DangerRed,
                        directAnswer = "Ang thrips ay ang numero unong peste ng alliums sa Pilipinas lalo na sa tag-araw. Dahil sumisipsip sila sa mga siwang ng dahon, nag-iiwan sila ng pinong pilak na marka (silvery patches) na nagpapatuyo sa buong dahon at nagdudulot ng maagang pagkamatay ng halaman.",
                        actionSteps = listOf(
                            "Maglagay agad ng asul o dilaw na sticky traps (blue / yellow sticky cards) 20–30 cm sa ibabaw ng kamada (labis na naaakit ang thrips sa kulay asul).",
                            "Mag-spray ng Neem oil solution (5 ml purong neem oil + 2 ml mild liquid soap bawat litro ng maligamgam na tubig) tuwing 4–5 araw sa takipsilim.",
                            "Siguraduhing mai-spray ang mismong siwang sa pagitan ng mga dahon kung saan nagtatago ang thrips.",
                            "Magsagawa ng light overhead misting sa mainit na umaga—kinatatakutan ng thrips ang mataas na halumigmig at basang paligid.",
                            "Kung malala ang pinsala sa malawak na taniman, mag-spray ng Spinosad bio-insecticide."
                        ),
                        materialsNeeded = listOf(
                            "Blue at yellow sticky trap cards",
                            "Cold-pressed Neem Oil at banayad na sabon",
                            "Sprayer na may adjustable fine nozzle",
                            "Spinosad (para sa malalang kaso)"
                        ),
                        organicRemedy = "Neem oil spray (5 ml/L) na may mild soap surfactant tuwing 5 araw sa dapit-hapon.",
                        criticalRule = "Mag-spray tuwing hapon paglubog ng araw dahil lumalabas ang thrips mula sa mga siwang sa gabi at masisira ng araw ang neem oil kung sa tanghali i-spray."
                    )
                }
                selectedOptions.contains("green_onion_pest_armyworm") -> {
                    DirectCropSolution(
                        title = "Harabas / Beet Armyworm (Spodoptera exigua)",
                        tagalogTitle = "Pagsugpo sa Uod na Nakatago sa Loob ng Hungkag na Dahon",
                        severity = "Kritikal (Fast Foliage Destruction)",
                        badgeColor = DangerRed,
                        directAnswer = "Mapanira ang harabas dahil pumapasok ang maliliit na uod sa loob ng hungkag na dahon ng sibuyas. Kinakain nila ang berdeng laman mula sa loob kaya protektado sila sa karaniwang pamatay-kulisap. Mapapansin ang tila manipis na transparent na balat ng dahon.",
                        actionSteps = listOf(
                            "Suriin ang mga dahon sa umaga; marahang pisilin (hand crush) ang dahon sa bahaging kinaroroonan ng uod upang mapatay ito.",
                            "Pitasin at sunugin ang mga dahong may mga itlog na natatakpan ng puting balahibo (egg masses) bago pa man mapisa.",
                            "Mag-spray ng Bacillus thuringiensis (Bt - Dipel/XenTari) sa daming 1–2 gramo bawat litro sa dakong hapon. Kapag kinain ng uod ang dahon na may Bt, mamamatay ito sa loob ng 24–48 oras.",
                            "Maglagay ng solar-powered light traps sa gilid ng bukid upang mahuli ang mga paruparo ng harabas bago mangitlog."
                        ),
                        materialsNeeded = listOf(
                            "Bacillus thuringiensis (Bt) bio-insecticide",
                            "Hand sprayer",
                            "Gunting para sa apektadong dahon"
                        ),
                        organicRemedy = "Bacillus thuringiensis (Bt) spray tuwing 5 araw sa takipsilim.",
                        criticalRule = "Huwag antayin na lumaki ang uod; ang Bt ay pinaka-epektibo sa maliliit at bagong pisang uod (1st to 2nd instar)."
                    )
                }
                selectedOptions.contains("green_onion_pest_leafminer") -> {
                    DirectCropSolution(
                        title = "Vegetable Leafminer (Liriomyza)",
                        tagalogTitle = "Pamamahala sa Paikot-ikot na Puti sa Dahon",
                        severity = "Mababa (Minor Foliar Damage)",
                        badgeColor = AlertAmber,
                        directAnswer = "Ang leafminer ay larva ng munting langaw na gumagawa ng paikot-ikot na puting lagusan sa loob ng balat ng dahon. Bihira itong makamatay ng buong sibuyas ngunit nakababawas sa ganda ng ani.",
                        actionSteps = listOf(
                            "Pisilin gamit ang daliri ang pinakadulo ng bawat puting guhit upang mapatay ang larva sa loob.",
                            "Maglagay ng dilaw na sticky traps upang mahuli ang mga matatandang langaw.",
                            "Mag-spray ng neem extract solution tuwing 7 araw upang mapigilan ang pangingitlog ng mga langaw."
                        ),
                        materialsNeeded = listOf(
                            "Yellow sticky traps",
                            "Neem oil solution"
                        ),
                        criticalRule = "Iwasan ang malalakas na broad-spectrum insecticide upang hindi mapatay ang kapaki-pakinabang na Diglyphus parasitic wasps."
                    )
                }
                selectedOptions.contains("green_onion_pest_cutworm") -> {
                    DirectCropSolution(
                        title = "Cutworm Damage sa Base ng Sibuyas",
                        tagalogTitle = "Pagkontrol sa Uod na Pumupungos sa Tangkay sa Gabi",
                        severity = "Mataas (Shoot Loss)",
                        badgeColor = DangerRed,
                        directAnswer = "Pumupugot ang cutworm sa base ng tangkay sa gabi at nagtatago sa ilalim ng lupa sa araw.",
                        actionSteps = listOf(
                            "Bungkalin nang mababaw (2 cm) ang lupa sa paligid ng naputol na puno sa umaga upang mahanap at mano-manong patayin ang kulay abong uod.",
                            "Magbudbod ng wood ash o diatomaceous earth (DE) sa paligid ng base ng mga halaman.",
                            "Mag-spray ng Bt solution sa base ng lupa sa gabi bago sila lumabas."
                        ),
                        materialsNeeded = listOf(
                            "Wood ash o Diatomaceous Earth",
                            "Bt solution"
                        ),
                        criticalRule = "Inspeksyunin ang lupa sa tabi ng bagong naputol na tangkay—karaniwang naroroon lamang ang uod sa loob ng 5 cm radius."
                    )
                }
                else -> {
                    DirectCropSolution(
                        title = "Malinis at Ligtas sa Peste ang Taniman",
                        tagalogTitle = "Panatilihin ang Natural Pest Barrier ng Sibuyas",
                        severity = "Tamang Pangangalaga (Optimal)",
                        badgeColor = LushGreen,
                        directAnswer = "Walang nakikitang pinsala ng peste! Ang natural na sulfur scent ng dahon ng sibuyas ay mabisang panangga sa karamihan ng insekto.",
                        actionSteps = listOf(
                            "Panatilihin ang mga bulaklak tulad ng marigold at dill sa paligid upang hikayatin ang ladybugs at hoverflies.",
                            "Magsagawa ng regular na inspeksyon tuwing umaga."
                        ),
                        materialsNeeded = listOf("Wala"),
                        criticalRule = "Ipagpatuloy ang malinis na pamamahala sa hardin."
                    )
                }
            }
        }

        "diseases" -> {
            when {
                selectedOptions.contains("green_onion_dis_purple_blotch") -> {
                    DirectCropSolution(
                        title = "Purple Blotch (Alternaria porri) Fungus",
                        tagalogTitle = "Agad na Pagsugpo sa Ube at Kayumangging Amag sa Dahon",
                        severity = "Mataas (Severe Foliar Disease)",
                        badgeColor = DangerRed,
                        directAnswer = "Ang Purple Blotch ang pangunahing fungal disease ng alliums sa Pilipinas kapag tag-ulan o mahalumigmig. Nagsisimula ito sa maliliit na tila basang sugat na nagiging pabilog na ube na may dilaw na paligid, na nagiging sanhi ng pagkabali ng mga dahon.",
                        actionSteps = listOf(
                            "Agad na gupitin at ligtas na itapon o sunugin ang lahat ng apektadong dahon na may ube o kayumangging sugat (huwag ilagay sa compost).",
                            "Iwasan ang anumang pagbabasa ng dahon sa gabi; patubigan lamang ang ugat sa umaga.",
                            "Mag-spray ng Copper fungicide (Copper Hydroxide o Bordeaux mixture) o Potassium Bicarbonate (3 g bawat litro na may kaunting sabon) tuwing 7 araw.",
                            "Maglagay ng Trichoderma sa lupa upang mapigilan ang pag-akyat ng fungal spores mula sa kamada."
                        ),
                        materialsNeeded = listOf(
                            "Copper fungicide / Copper Hydroxide",
                            "Potassium Bicarbonate",
                            "Gunting para sa diseased leaf pruning"
                        ),
                        organicRemedy = "Potassium Bicarbonate spray (3g/L) na may sticker surfactant tuwing 7 araw.",
                        criticalRule = "Huwag mag-overcrowd ng tanim; kailangan ng sapat na agwat (15 cm) para mabilis matuyo ang dahon sa hangin."
                    )
                }
                selectedOptions.contains("green_onion_dis_downy_mildew") -> {
                    DirectCropSolution(
                        title = "Downy Mildew (Peronospora destructor)",
                        tagalogTitle = "Pagsugpo sa Abong Amag sa Malamig at Mahamog na Panahon",
                        severity = "Mataas (Rapid Foliar Wilt)",
                        badgeColor = AlertAmber,
                        directAnswer = "Lumalitaw ang downy mildew sa malamig na buwan (Nobyembre hanggang Pebrero) kapag mahamog ang umaga. Ang purplish-gray fuzzy mold sa dahon ay mabilis na nagpapatuyo sa buong halaman.",
                        actionSteps = listOf(
                            "Alisin agad ang mga apektadong dahon bago sumabog ang fungal spores sa hangin.",
                            "Tiyaking nasisikatan ng buong araw ang taniman upang mabilis sumingaw ang hamog.",
                            "Mag-spray ng bio-fungicide (Bacillus subtilis o Trichoderma) sa buong halaman.",
                            "Huwag gumamit ng overhead sprinkler sa panahong ito."
                        ),
                        materialsNeeded = listOf(
                            "Bacillus subtilis bio-fungicide",
                            "Drip irrigation lines"
                        ),
                        criticalRule = "Pabilisin ang sirkulasyon ng hangin sa pamamagitan ng pag-alis ng mga ligaw na damo sa paligid."
                    )
                }
                selectedOptions.contains("green_onion_dis_basal_rot") -> {
                    DirectCropSolution(
                        title = "Fusarium Basal Rot sa Pinakapuno ng Sibuyas",
                        tagalogTitle = "Pangangasiwa sa Pagkabulok ng Ugat at Basal Plate",
                        severity = "Kritikal (Lethal Soil-Borne Fungus)",
                        badgeColor = DangerRed,
                        directAnswer = "Ang Fusarium ay nananatili sa lupa at sumasalakay sa mga ugat at basal plate kapag basa ang kamada. Ang buong puno ay naninilaw at nabubulok mula sa ilalim.",
                        actionSteps = listOf(
                            "Bunutin at itapon ang mga apektadong halaman kasama ang lupang nakadikit sa ugat.",
                            "Huwag magtanim ng anumang allium (sibuyas, bawang, kutsay) sa naturang plot sa loob ng 2 hanggang 3 taon (mag-crop rotation sa mais o pechay).",
                            "Ihalo ang masaganang Trichoderma harzianum sa compost bago muling magtanim.",
                            "Ayusin ang drainage canal ng kamada."
                        ),
                        materialsNeeded = listOf(
                            "Trichoderma harzianum",
                            "Pala para sa pag-aalis ng kontaminadong lupa"
                        ),
                        criticalRule = "Mahigpit na ipatupad ang crop rotation upang maubos ang fungal population sa lupa."
                    )
                }
                else -> {
                    DirectCropSolution(
                        title = "Malusog at Ligtas sa Sakit ang mga Dahon",
                        tagalogTitle = "Optimal na Kalusugan laban sa Fungi at Bakterya",
                        severity = "Tamang Pangangalaga (Optimal)",
                        badgeColor = LushGreen,
                        directAnswer = "Napakaganda ng kalusugan ng inyong taniman. Walang palatandaan ng fungal o bacterial disease.",
                        actionSteps = listOf(
                            "Panatilihin ang maayos na distansya at umagang patubig.",
                            "Maglagay ng preventive bio-fungicide drench isang beses bawat buwan."
                        ),
                        materialsNeeded = listOf("Wala"),
                        criticalRule = "Iwasan ang pagtatalsik ng maruming tubig sa gabi."
                    )
                }
            }
        }

        "bed_care" -> {
            when {
                selectedOptions.contains("green_onion_bed_weeds") -> {
                    DirectCropSolution(
                        title = "Masukal sa Damo (Weed Competition Hazard)",
                        tagalogTitle = "Kritikal na Pagdadamot sa Mababaw na Ugat ng Sibuyas",
                        severity = "Mataas (Loss of Vigor)",
                        badgeColor = AlertAmber,
                        directAnswer = "Dahil tuwid at manipis ang dahon ng green onion at mababaw ang ugat nito, napakahina nitong makipagkumpitensya sa matatayog na damo. Inaagaw ng damo ang sikat ng araw, pataba, at tubig.",
                        actionSteps = listOf(
                            "Mano-manong bunutin ang mga damo habang maliliit pa gamit ang hand weeder.",
                            "Mag-ingat na huwag mabunot o maputol ang mabababaw na ugat ng sibuyas.",
                            "Maglagay agad ng 3–5 cm na carbonized rice hull o rice straw mulch upang pigilan ang pagsibol ng bagong damo.",
                            "Damuhan ang taniman tuwing 2 linggo."
                        ),
                        materialsNeeded = listOf(
                            "Hand weeder",
                            "Carbonized Rice Hull o Rice straw mulch"
                        ),
                        criticalRule = "Bunutin ang damo kapag mamasa-masa ang lupa upang madaling mabunot pati ang pinakaugat ng damo nang hindi nagagalaw ang sibuyas."
                    )
                }
                selectedOptions.contains("green_onion_bed_compacted") -> {
                    DirectCropSolution(
                        title = "Matigas at Compacted na Lupa (Impeded Tillering)",
                        tagalogTitle = "Pagpapabuhaghag sa Kamada gamit ang CRH at Compost",
                        severity = "Mataas (Stunted Tillers)",
                        badgeColor = AlertAmber,
                        directAnswer = "Sa matigas at maputik na lupang clay, hindi makapagparami ng suwi ang sibuyas at nahihirapang bumuo ng malalapad na ugat.",
                        actionSteps = listOf(
                            "Bungkalin nang banayad ang espasyo sa pagitan ng mga hanay gamit ang tinidor ng lupa.",
                            "Maghalo ng 1 sako ng Carbonized Rice Hull (CRH) at 1 sako ng vermicompost sa bawat 5 metrong plot.",
                            "Itayo ang raised bed sa taas na 15–20 cm."
                        ),
                        materialsNeeded = listOf(
                            "Carbonized Rice Hull (CRH)",
                            "Vermicompost",
                            "Garden fork"
                        ),
                        criticalRule = "Huwag kailanman apakan ang ibabaw ng kamada kapag basa upang maiwasan ang soil compaction."
                    )
                }
                else -> {
                    DirectCropSolution(
                        title = "Buhaghag at Malinis na Kamada",
                        tagalogTitle = "Perpektong Kondisyon para sa Pagpaparami ng Suwi",
                        severity = "Tamang Pangangalaga (Optimal)",
                        badgeColor = LushGreen,
                        directAnswer = "Perpekto ang kondisyon ng inyong lupang taniman. Malaya at mabilis na makakapagparami ng mga bagong tangkay ang sibuyas.",
                        actionSteps = listOf(
                            "Panatilihin ang kapal ng mulch.",
                            "Mano-manong bunutin ang paisa-isang damo na sumusulpot."
                        ),
                        materialsNeeded = listOf("Wala"),
                        criticalRule = "Panatilihing buhaghag ang lupa."
                    )
                }
            }
        }

        "harvest" -> {
            when {
                selectedOptions.contains("green_onion_harvest_ratoon") -> {
                    DirectCropSolution(
                        title = "Cut-and-Come-Again (Ratoon Harvesting Technique)",
                        tagalogTitle = "Wastong Paggupit para sa Tuloy-tuloy na Produksyon",
                        severity = "Pag-aani (Harvest Ready)",
                        badgeColor = LushGreen,
                        directAnswer = "Ang 'cut-and-come-again' ay ang pinakapraktikal na paraan para sa kusina at hardin. Pinuputol lamang ang mga dahon at hinahayaang muling tumubo nang walang tigil.",
                        actionSteps = listOf(
                            "Gamit ang matalas at sterilized na gunting, gupitin ang mga tangkay 2.5 hanggang 3 cm sa ibabaw ng antas ng lupa.",
                            "Huwag gupitin nang sagad sa pinakaugat (basal plate) upang maprotektahan ang growing meristem.",
                            "Diligan at maglagay ng kaunting compost tea o malabnaw na fish amino acid sa paligid ng puno kinabukasan.",
                            "Sa loob ng 10 hanggang 15 araw, tutubo muli ang buong kumpol ng sariwang dahon para sa susunod na pitas!"
                        ),
                        materialsNeeded = listOf(
                            "Matalas na gunting ng hardin",
                            "Compost tea o FPJ pang-boost ng regrowth"
                        ),
                        criticalRule = "I-sanitize ang gunting gamit ang 70% alcohol bago gupitan ang bawat kumpol upang maiwasan ang pagkalat ng sakit."
                    )
                }
                selectedOptions.contains("green_onion_harvest_division") -> {
                    DirectCropSolution(
                        title = "Paghahati ng Kumpol para sa Pagpaparami (Clump Division)",
                        tagalogTitle = "Teknolohiya sa Mabilis na Pagpaparami ng Sibuyas Tagalog",
                        severity = "Pagpaparami (Propagation Ready)",
                        badgeColor = LushGreen,
                        directAnswer = "Ang paghahati ng kumpol ang pinakamabilis na paraan upang magparami ng mga bagong plots nang hindi bumibili ng binhi.",
                        actionSteps = listOf(
                            "Diligan ang lupa 1 oras bago bunutin upang hindi mapigtas ang mga ugat.",
                            "Bunutin ang kumpol at paghiwalayin sa grupo na may tig-2 hanggang 3 suwi.",
                            "Gupitin ang itaas na 1/3 ng dahon upang mabawasan ang panlalanta.",
                            "Itanim agad sa bagong kamada sa 10–15 cm agwat at diligan nang sagana."
                        ),
                        materialsNeeded = listOf(
                            "Spading fork",
                            "Gunting",
                            "Bagong kamada na may pataba"
                        ),
                        criticalRule = "Itanim sa parehong araw na binunot ang mga suwi upang mataas ang survival rate."
                    )
                }
                else -> {
                    DirectCropSolution(
                        title = "Buong Pagbunot at Pagbubungkos (Commercial Bunching Harvest)",
                        tagalogTitle = "Paghahanda para sa Palengke at Sariwang Pamilihan",
                        severity = "Pag-aani (Market Ready)",
                        badgeColor = LushGreen,
                        directAnswer = "Sa 45 hanggang 60 araw mula pagtatanim, handa na ang green onions para sa whole bunching harvest kung saan may taas itong 35–50 cm at may 1 cm diyametro.",
                        actionSteps = listOf(
                            "Diligan nang bahagya ang kamada upang lumambot ang lupa bago magbunot.",
                            "Hawakan ang kumpol sa pinakababa ng tangkay malapit sa lupa at marahang bunutin nang patayo upang buo ang puting ugat.",
                            "Banlawan ang mga ugat sa malinis na tubig upang maalis ang lupang bukid.",
                            "Alisin ang anumang naninilaw o sirang panlabas na dahon (peeling the sheath).",
                            "Bungkusin gamit ang straw twine o rubber band sa timbang na 250 g hanggang 500 g bawat tali.",
                            "I-salansan nang patayo sa ventilated crates sa malamig na lugar."
                        ),
                        materialsNeeded = listOf(
                            "Paghuhugasan ng malinis na tubig",
                            "Tali o goma para sa pagbubungkos",
                            "Ventilated plastic crates"
                        ),
                        criticalRule = "Huwag ibilad sa araw ang naaning mga bungkos; takpan ng basang sako upang mapanatili ang lutong at kasariwaan."
                    )
                }
            }
        }

        else -> null
    }
}
