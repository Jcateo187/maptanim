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
 * for Squash / Kalabasa (Cucurbita moschata) in Philippine lowland, backyard, and upland farm settings
 * based on MapTanim Squash technical data & DA-BPI extension guidelines.
 */
fun getSquashCheckUpActivities(): List<CheckUpActivity> {
    return listOf(
        CheckUpActivity(
            id = "watering",
            title = "Pagdidilig at Kahalumigmig ng Kalabasa",
            tagalogSubtitle = "Suriin ang moisture sa bunton (hills) at kalagayan ng malalapad na dahon",
            icon = Icons.Default.WaterDrop,
            options = listOf(
                CheckUpOption(
                    id = "squash_water_normal",
                    title = "Katamtamang Kahalumigmig sa Bunton",
                    tagalogDesc = "Mamasa-masa ngunit hindi putik ang bunton; dinidilig nang 1–2 beses bawat linggo sa ugat",
                    isIdeal = true
                ),
                CheckUpOption(
                    id = "squash_water_stress",
                    title = "Tagtuyot / Lantang Baging sa Katanghalian",
                    tagalogDesc = "Naluluoy at lumalambot ang malalapad na dahon sa init; nalalaglag ang bulaklak at kupos ang bunga"
                ),
                CheckUpOption(
                    id = "squash_water_overwater",
                    title = "Sobrang Tubig / Baha sa Paligid ng Bunton",
                    tagalogDesc = "Naiipon ang tubig sa pagitan ng mga bunton; naninilaw ang lumang dahon at nabubulok ang puno (crown rot)"
                ),
                CheckUpOption(
                    id = "squash_water_rain_flower",
                    title = "Nabasa ang Bulaklak sa Ulan (Nalulusaw ang Polon)",
                    tagalogDesc = "Nababad sa malakas na ulan ang nakabukang babaeng bulaklak; nabubulok at hindi nabubuo ang bunga"
                )
            )
        ),
        CheckUpActivity(
            id = "fertilizing",
            title = "Abono at Nutrisyon ng Kalabasa",
            tagalogSubtitle = "Suriin ang balanse ng Nitroheno, Potash, Kalsyo, at Boron",
            icon = Icons.Default.Spa,
            options = listOf(
                CheckUpOption(
                    id = "squash_fert_balanced",
                    title = "Balanseng Nutrisyon (14-14-14 at Potash sa Pamumunga)",
                    tagalogDesc = "May basal compost at kumpletong abono sa bunton; makapal, berde ang dahon at mabibilog ang bunga",
                    isIdeal = true
                ),
                CheckUpOption(
                    id = "squash_fert_excess_nitrogen",
                    title = "Sobra sa Nitroheno (Lumalagong Dahon, Walang Bunga)",
                    tagalogDesc = "Napakakapal at madilim na berde ang mga baging ngunit puro lalaking bulaklak lamang ang sumisibol"
                ),
                CheckUpOption(
                    id = "squash_fert_potassium_def",
                    title = "Kulang sa Potash (Kupos at Hindi Matamis ang Laman)",
                    tagalogDesc = "Tustado at sunog ang gilid ng lumang dahon; payat at hindi lumalaki nang husto ang katawan ng bunga"
                ),
                CheckUpOption(
                    id = "squash_fert_calcium_def",
                    title = "Kulang sa Kalsyo (Bulok ang Dulo / Blossom-End Rot)",
                    tagalogDesc = "Nangingitim at nabubulok ang dulo ng bunga kung saan nakakabit ang dating bulaklak"
                ),
                CheckUpOption(
                    id = "squash_fert_boron_def",
                    title = "Kulang sa Boron (Nalalaglag ang Buntis na Bulaklak)",
                    tagalogDesc = "Napipilipit ang bagong usbong na bunga, may guwang at maagang nalalaglag bago pa lumaki"
                )
            )
        ),
        CheckUpActivity(
            id = "pollination",
            title = "Pamumulaklak at Polinasyon",
            tagalogSubtitle = "Suriin ang aktibidad ng mga bubuyog at pagsasalin ng polon sa babaeng bulaklak",
            icon = Icons.Default.Spa,
            options = listOf(
                CheckUpOption(
                    id = "squash_poll_active_bees",
                    title = "Aktibong Bubuyog at Matagumpay na Polinasyon",
                    tagalogDesc = "Maraming bubuyog na umiikot mula 6:00 hanggang 9:00 AM; mabilis lumaki ang obaryo ng babaeng bulaklak",
                    isIdeal = true
                ),
                CheckUpOption(
                    id = "squash_poll_all_male",
                    title = "Puro Lalaking Bulaklak Lamang ang Bumubuka (Unang 2 Linggo)",
                    tagalogDesc = "Walang umbok na maliit na kalabasa sa ilalim ng bulaklak; natural na yugto bago lumabas ang babaeng bulaklak"
                ),
                CheckUpOption(
                    id = "squash_poll_aborted_female",
                    title = "Nalalaglag at Naninilaw ang Munting Kalabasa",
                    tagalogDesc = "Hindi nasalinan ng polon ang babaeng bulaklak dahil walang bubuyog o dahil sa ulan; nalalanta sa loob ng 48 oras"
                ),
                CheckUpOption(
                    id = "squash_poll_need_hand",
                    title = "Kailangan ng Mano-Manong Polinasyon (Hand Pollination)",
                    tagalogDesc = "Walang natural na pukyutan sa paligid; kailangang ikuskos ang anther ng lalaking bulaklak sa stigma ng babae"
                )
            )
        ),
        CheckUpActivity(
            id = "pests",
            title = "Mga Peste sa Baging at Bunga",
            tagalogSubtitle = "Suriin ang mga dahon, tangkay, at balat ng lumalaking kalabasa",
            icon = Icons.Default.BugReport,
            options = listOf(
                CheckUpOption(
                    id = "squash_pest_none",
                    title = "Malinis at Walang Pinsala ng Peste",
                    tagalogDesc = "Buo ang mga dahon at walang marka ng tusok o pangingitlog sa mga munting kalabasa",
                    isIdeal = true
                ),
                CheckUpOption(
                    id = "squash_pest_fruit_fly",
                    title = "Melon Fruit Fly (Bactrocera cucurbitae)",
                    tagalogDesc = "May tuldok na tusok na may tumutulong dagta sa balat ng bunga; may uod (maggots) sa loob at nabubulok"
                ),
                CheckUpOption(
                    id = "squash_pest_squash_beetle",
                    title = "Squash / Cucurbit Leaf Beetle (Aulacophora spp.)",
                    tagalogDesc = "Dilaw-kahel na salagubang na kumakain at gumugupit ng pabilog na butas sa malalapad na dahon"
                ),
                CheckUpOption(
                    id = "squash_pest_aphids",
                    title = "Melon Aphids (Aphis gossypii) at Puting Langaw",
                    tagalogDesc = "Kumpol-kumpol na kuto sa ilalim ng talbos na nagdudulot ng paninilaw, dagta, at itim na amag (sooty mold)"
                ),
                CheckUpOption(
                    id = "squash_pest_vine_borer",
                    title = "Squash Vine Borer (Melittia spp. / Larva sa Puno)",
                    tagalogDesc = "Biglaang pagkalanta ng buong sanga; may butas at parang ipot-lagari (frass) sa puno o buko ng baging"
                )
            )
        ),
        CheckUpActivity(
            id = "diseases",
            title = "Mga Sakit sa Dahon at Baging",
            tagalogSubtitle = "Suriin ang mga amag, batik, at kulubot sa halaman",
            icon = Icons.Default.Coronavirus,
            options = listOf(
                CheckUpOption(
                    id = "squash_disease_none",
                    title = "Malusog at Walang Sakit ang Baging",
                    tagalogDesc = "Matingkad na berde ang dahon, makinis ang baging, at walang pulbos o amag",
                    isIdeal = true
                ),
                CheckUpOption(
                    id = "squash_disease_powdery_mildew",
                    title = "Powdery Mildew (Podosphaera / Erysiphe spp.)",
                    tagalogDesc = "Mapuputing pulbos na parang harina sa ibabaw ng dahon; natutuyo, naninilaw, at nagiging malutong ang dahon"
                ),
                CheckUpOption(
                    id = "squash_disease_downy_mildew",
                    title = "Downy Mildew (Pseudoperonospora cubensis)",
                    tagalogDesc = "Manilaw-nilaw na sulok-sulok na batik (angular lesions) na pinipigil ng ugat ng dahon; may abong amag sa ilalim"
                ),
                CheckUpOption(
                    id = "squash_disease_leaf_curl_virus",
                    title = "Squash Leaf Curl Virus (SLCV) / Mosaic",
                    tagalogDesc = "Kulot, makapal, at kupos ang mga bagong talbos; may manilaw-nilaw na mosaic at bansot ang buong halaman"
                ),
                CheckUpOption(
                    id = "squash_disease_wet_rot",
                    title = "Choanephora Wet Rot (Bulok sa Bulaklak at Bunga)",
                    tagalogDesc = "Maitim at mabalahibong basang amag na lumalabas sa lantang bulaklak at lumalaganap sa dulo ng munting bunga"
                )
            )
        ),
        CheckUpActivity(
            id = "vine_management",
            title = "Pamamahala sa Gapang at Bunton",
            tagalogSubtitle = "Suriin ang espasyo, damo, mulching, at proteksyon sa bunga",
            icon = Icons.Default.Agriculture,
            options = listOf(
                CheckUpOption(
                    id = "squash_vine_proper_spacing",
                    title = "Wastong Agwat ng Bunton (2–3 Metro) at Malinis",
                    tagalogDesc = "May sapat na daanan ang hangin; may tuyong dayami o mulch sa ilalim ng lumalaking bunga",
                    isIdeal = true
                ),
                CheckUpOption(
                    id = "squash_vine_overcrowded",
                    title = "Masyadong Masinsin at Magulo ang Gapang",
                    tagalogDesc = "Nagsisiksikan ang mga baging sa daanan; kulang sa sikat ng araw at mabilis pamahayan ng amag"
                ),
                CheckUpOption(
                    id = "squash_vine_direct_ground_rot",
                    title = "Nakadikit ang Bunga sa Basang Putik / Lupa",
                    tagalogDesc = "Puting amag at pagkaagnas sa ilalim ng kalabasa dahil direktang nakalapat sa mamasa-masang lupa"
                ),
                CheckUpOption(
                    id = "squash_vine_excess_runners",
                    title = "Sobra-sobrang Suwi (Kulang sa Laki ang Pangunahing Bunga)",
                    tagalogDesc = "Masyadong maraming sanga na nag-aagawan sa sustansya; kailangang kaponin (prune) ang mga dulo"
                )
            )
        ),
        CheckUpActivity(
            id = "harvesting",
            title = "Pagtantiya sa Pag-aani ng Kalabasa",
            tagalogSubtitle = "Suriin ang tigas ng balat, kulay ng tangkay, at tamang paraan ng pagputol",
            icon = Icons.Default.ContentCut,
            options = listOf(
                CheckUpOption(
                    id = "squash_harvest_ready_mature",
                    title = "Ganap na Hinog para sa Pamilihan (90–120 DAS)",
                    tagalogDesc = "Matigas ang balat (hindi bumaon ang kuko); tuyo, kulubot, at parang cork ang tangkay; matingkad na kahel ang laman",
                    isIdeal = true
                ),
                CheckUpOption(
                    id = "squash_harvest_ready_young",
                    title = "Murang Kalabasa at Talbos (Panggulay sa Gisado)",
                    tagalogDesc = "Malambot pa ang balat at banayad ang buto; inaani kasama ang malalambot na talbos at bulaklak"
                ),
                CheckUpOption(
                    id = "squash_harvest_not_ready",
                    title = "Hilaw Pa ang Bunga (Bumabakat ang Kuko)",
                    tagalogDesc = "Matingkad na makintab na berde pa ang balat at basa ang tangkay; kulang pa sa asukal at almirol"
                ),
                CheckUpOption(
                    id = "squash_harvest_stem_cut",
                    title = "Wastong Pag-ani: Mag-iwan ng 3–5 cm na Tangkay",
                    tagalogDesc = "Huwag pilipitin o baliin gamit ang kamay; gupitin gamit ang pruning shears upang hindi mabutas at mabulok ang puwit"
                )
            )
        )
    )
}

fun solveSquashProblem(activityId: String, selectedOptions: Set<String>): DirectCropSolution? {
    if (selectedOptions.isEmpty()) return null

    // 1. WATERING
    if (activityId == "watering") {
        if (selectedOptions.contains("squash_water_normal")) {
            return DirectCropSolution(
                title = "Optimal Squash Hill Moisture",
                tagalogTitle = "Tamang Pagdidilig sa Bunton ng Kalabasa",
                severity = "IDEAL",
                badgeColor = LushGreen,
                directAnswer = "Nasa pinakamagandang kalagayan ang kahalumigmig ng iyong kalabasa. Ang 1–2 beses na pagdidilig bawat linggo sa base ng bunton ay sumusuporta sa malalim na ugat.",
                actionSteps = listOf(
                    "Panatilihin ang pagdidilig sa base ng bunton (basin watering o furrow).",
                    "Iwasang basain ang mga dahon at bulaklak lalo na sa umaga upang hindi maabala ang polinasyon ng mga bubuyog.",
                    "Maglagay ng 5–10 cm na tuyong dayami sa paligid ng bunton upang mapanatili ang lamig ng lupa."
                ),
                materialsNeeded = listOf("Pandilig o Hose sa Lupa", "Tuyong Dayami o Rice Straw Mulch"),
                organicRemedy = "Rice straw mulching para sa matatag na moisture nang hindi nabubulok ang leeg ng baging.",
                criticalRule = "Huwag magdilig mula sa itaas (overhead spray) kapag may bumubukang bulaklak."
            )
        }
        if (selectedOptions.contains("squash_water_stress")) {
            return DirectCropSolution(
                title = "Water Stress & Vine Wilting in Squash",
                tagalogTitle = "Lunas sa Tagtuyot at Pagkalanta ng Baging",
                severity = "WARNING",
                badgeColor = AlertAmber,
                directAnswer = "Malaki ang surface area ng malalapad na dahon ng kalabasa kaya mabilis itong sumingaw (transpiration). Kapag tuyot ang bunton, nalalaglag ang bulaklak at nagiging kupos ang lumalaking kalabasa.",
                actionSteps = listOf(
                    "Magdilig agad nang malalim sa base ng bawat bunton (5–10 litro bawat bunton) sa maagang umaga o dapit-hapon.",
                    "Hukayin nang bahagya ang paligid ng bunton upang makapasok ang tubig at hindi umagos palayo.",
                    "Maglatag ng makapal na mulching (rice straw o tuyong damo) sa buong bunton upang harangan ang init ng araw.",
                    "Huwag magdilig sa tirik na araw ng tanghali upang maiwasan ang thermal shock sa mainit na ugat."
                ),
                materialsNeeded = listOf("Sapat na Malinis na Tubig", "Tuyong Dayami / Mulch", "Maliit na Asarol"),
                organicRemedy = "Malalim na furrow irrigation na may makapal na rice straw mulch sa paligid ng bunton.",
                criticalRule = "Kritikal ang tubig kapag namumulaklak at lumalaki ang bunga (fruit bulking)."
            )
        }
        if (selectedOptions.contains("squash_water_overwater")) {
            return DirectCropSolution(
                title = "Waterlogging & Crown Rot Hazard",
                tagalogTitle = "Banta ng Pambabaha at Pagkabulok ng Puno",
                severity = "CRITICAL",
                badgeColor = DangerRed,
                directAnswer = "Bagamat mas matatag ang kalabasa kumpara sa ibang cucurbits, ang nakababad na bunton ay nagdudulot ng Phytophthora crown rot at kakulangan sa hangin ng ugat, na nagiging sanhi ng paninilaw at pagkabulok.",
                actionSteps = listOf(
                    "Palalimin agad ang mga kanal (drainage furrows) sa pagitan ng mga bunton upang mabilis na humupa ang tubig.",
                    "Taasan ang bunton sa 25–30 cm mula sa kanal kapag tag-ulan.",
                    "I-spray o alisin ang basang-basang mulch sa mismong leeg ng baging upang mahanginan ang puno.",
                    "Magwisik ng Trichoderma harzianum sa base ng baging upang labanan ang mga pathogen sa basang lupa."
                ),
                materialsNeeded = listOf("Piko o Pala para sa Kanal", "Trichoderma Microbial Inoculant"),
                organicRemedy = "Trichoderma drench sa leeg ng baging at paglalagay ng buhaghag na compost sa paligid.",
                criticalRule = "Hindi mabubuhay ang kalabasa sa nakatayong tubig nang higit sa 24–48 oras."
            )
        }
        if (selectedOptions.contains("squash_water_rain_flower")) {
            return DirectCropSolution(
                title = "Rain-Damaged Flowers & Pollen Washout",
                tagalogTitle = "Lunas sa Nababad na Bulaklak sa Ulan",
                severity = "ADVISORY",
                badgeColor = AlertAmber,
                directAnswer = "Ang mabigat na ulan sa madaling-araw o umaga ay naghuhugas ng polon at nagpapalubog sa stigma, dahilan upang hindi magtagumpay ang polinasyon at mabulok ang bagong bunga.",
                actionSteps = listOf(
                    "Para sa mga home garden, maglagay ng maliliit na paper cups o plastic cones na nakataob sa ibabaw ng malapit nang bumukang babaeng bulaklak sa gabi bago umulan.",
                    "Magsagawa ng mano-manong polinasyon pagsapit ng 6:00–8:00 AM bago dumating ang malalakas na ulan.",
                    "Alisin agad ang mga bulaklak na tuluyang nalusaw at nangingitim upang hindi pamahayan ng Choanephora wet rot."
                ),
                materialsNeeded = listOf("Paper Cups / Cloche Shields", "Gunting / Pruning Shears"),
                organicRemedy = "Sanitasyon: agarang pag-aalis ng basang lantang bulaklak bago pa mahawa ang dulo ng bunga.",
                criticalRule = "Bukas lamang ang bulaklak ng kalabasa mula madaling araw hanggang mga 10:00 AM."
            )
        }
    }

    // 2. FERTILIZING
    if (activityId == "fertilizing") {
        if (selectedOptions.contains("squash_fert_balanced")) {
            return DirectCropSolution(
                title = "Balanced Nutrition Profile",
                tagalogTitle = "Pinakamahusay na Nutrisyon sa Kalabasa",
                severity = "IDEAL",
                badgeColor = LushGreen,
                directAnswer = "Tamang-tama ang antas ng pataba. Ang balanse ng Nitrogen para sa baging at Potash para sa laman ng kalabasa ay nagbibigay ng matamis, malagkit, at mabigat na ani.",
                actionSteps = listOf(
                    "Ipagpatuloy ang side-dressing ng Muriate of Potash (0-0-60) o abo ng kahoy tuwing 2 linggo habang lumalaki ang mga bunga.",
                    "Magwisik ng fermented fruit juice (FFJ) o seaweed extract tuwing 10–14 araw para sa tamis at kulay ng laman.",
                    "Panatilihin ang sapat na calcium upang maiwasan ang bitak sa balat ng bunga."
                ),
                materialsNeeded = listOf("Fermented Fruit Juice (FFJ)", "Muriate of Potash / Wood Ash"),
                organicRemedy = "Fermented Fruit Juice (gawa sa hinog na saging/kalabasa at molasses) 2 tbsp/litro lingguhan.",
                criticalRule = "Huwag nang magdagdag ng maraming urea kapag lumalaki na ang mga bunga."
            )
        }
        if (selectedOptions.contains("squash_fert_excess_nitrogen")) {
            return DirectCropSolution(
                title = "Excess Nitrogen & Male-Only Flowering",
                tagalogTitle = "Sobra sa Nitroheno: Puro Dahon at Walang Bunga",
                severity = "WARNING",
                badgeColor = AlertAmber,
                directAnswer = "Ang labis na Nitrogen (tulad ng sobrang Urea o sariwang dumi ng manok) ay nagdudulot ng 'vegetative runaway'—napakakapal na baging ngunit puro lalaking bulaklak lamang ang lumalabas dahil ayaw magbuntis ng halaman.",
                actionSteps = listOf(
                    "Itigil agad ang paglalagay ng Urea (46-0-0) o anumang high-nitrogen na pataba.",
                    "Maglagay agad ng pataba na may mataas na Phosphorus at Potassium (0-0-60 Muriate of Potash o Solophos 0-20-0) upang pilitin ang halaman na maglabas ng babaeng bulaklak.",
                    "Putulan ng 10–15 cm ang dulo ng pinakamahabang baging (apical pinching) upang maipon ang enerhiya sa pagpapalabas ng lateral fruiting branches."
                ),
                materialsNeeded = listOf("Muriate of Potash (0-0-60)", "Abo ng Kahoy (Wood Ash)", "Pruning Shears"),
                organicRemedy = "Pagbubudbod ng 1–2 tasa ng abo ng kahoy sa paligid ng bawat bunton at foliar spray ng FFJ.",
                criticalRule = "Itigil ang nitrogen fertilizer kapag nagsimula nang gumapang nang mabilis ang baging."
            )
        }
        if (selectedOptions.contains("squash_fert_potassium_def")) {
            return DirectCropSolution(
                title = "Potassium Deficiency in Squash",
                tagalogTitle = "Kakulangan sa Potash: Kupos at Matamlay na Bunga",
                severity = "WARNING",
                badgeColor = AlertAmber,
                directAnswer = "Ang Potassium ang nagtutulak ng asukal at starch papunta sa laman ng kalabasa. Kapag kulang, sunog ang gilid ng lumang dahon at magiging matubig, matabang, at maliit ang bunga.",
                actionSteps = listOf(
                    "Mag-apply ng side-dress na Muriate of Potash (0-0-60) sa agwat na 20–30 cm mula sa puno (10–15 gramo bawat puno) at diligan agad.",
                    "Magbudbod ng purong abo ng kahoy (mayaman sa soluble potassium at calcium) sa ibabaw ng bunton.",
                    "Mag-spray ng potassium-rich foliar fertilizer sa maagang umaga."
                ),
                materialsNeeded = listOf("Muriate of Potash (0-0-60)", "Sinalang Abo ng Kahoy"),
                organicRemedy = "Abo ng kahoy o fermented banana stalk/fruit extract na idinidilig sa lupa.",
                criticalRule = "Kailangan ng kalabasa ang mataas na Potash mula sa ika-6 hanggang ika-10 linggo matapos mamunga."
            )
        }
        if (selectedOptions.contains("squash_fert_calcium_def")) {
            return DirectCropSolution(
                title = "Blossom-End Rot (Calcium Deficiency)",
                tagalogTitle = "Blossom-End Rot: Bulok na Puwit ng Kalabasa",
                severity = "WARNING",
                badgeColor = AlertAmber,
                directAnswer = "Kakulangan sa Calcium sa dulo ng bunga habang mabilis itong lumalaki. Karaniwang sanhi ng pabago-bagong pagdidilig (tuyo-basa) kaya hindi naiakyat ng ugat ang calcium sa dulo ng bunga.",
                actionSteps = listOf(
                    "Gawing pantay at regular ang pagdidilig; huwag hayaang matuyo nang husto ang bunton bago diligan.",
                    "Mag-spray ng Foliar Calcium-Boron o Tubig-Apog (Calphos) tuwing 7 araw sa umaga.",
                    "Tanggalin at itapon ang mga bungang may nangingitim nang puwit upang hindi mag-aksaya ng sustansya ang baging."
                ),
                materialsNeeded = listOf("Water-Soluble Calcium (Calphos)", "Agricultural Lime / Apog", "Pandilig"),
                organicRemedy = "Eggshell vinegar concoction (WCA - Water Soluble Calcium) 2 kutsara bawat litro ng tubig.",
                criticalRule = "Hindi maiaakyat ang calcium sa bunga kapag tuyot ang lupa, gaano man karami ang apog."
            )
        }
        if (selectedOptions.contains("squash_fert_boron_def")) {
            return DirectCropSolution(
                title = "Boron Deficiency & Fruit Abortion",
                tagalogTitle = "Kakulangan sa Boron: Nalalaglag at Pilipit na Bunga",
                severity = "ADVISORY",
                badgeColor = AlertAmber,
                directAnswer = "Mahalaga ang Boron sa cell division ng obaryo at paghaba ng pollen tube. Ang kakulangan ay nagiging sanhi ng pagkalaglag ng munting kalabasa bago o agad matapos bumuka ang bulaklak.",
                actionSteps = listOf(
                    "Mag-apply ng Solubor o Borax sa napakababang timpla (1–2 gramo bawat 16 litrong knapsack) bilang foliar spray sa pamumulaklak.",
                    "Iwasan ang labis na paglalagay ng apog (lime) na nagpapataas ng pH lagpas 7.0, na nagpapahirap sa pagsipsip ng boron.",
                    "Maglagay ng masaganang vermicompost sa bunton na naglalaman ng natural na micronutrients."
                ),
                materialsNeeded = listOf("Solubor / Boron Micronutrient Foliar", "Vermicompost"),
                organicRemedy = "Vermicompost tea na mayaman sa trace minerals at natural growth promoters.",
                criticalRule = "Huwag sosobrahan ang boron spray dahil mabilis magdulot ng toxicity kapag marami."
            )
        }
    }

    // 3. POLLINATION
    if (activityId == "pollination") {
        if (selectedOptions.contains("squash_poll_active_bees")) {
            return DirectCropSolution(
                title = "Excellent Natural Pollination",
                tagalogTitle = "Masiglang Polinasyon mula sa mga Bubuyog",
                severity = "IDEAL",
                badgeColor = LushGreen,
                directAnswer = "Napakaganda ng aktibidad ng mga bubuyog sa iyong taniman. Bawat babaeng bulaklak ay nangangailangan ng 8–12 pagbisita ng bubuyog upang ganap na mabuo ang mga buto at lumaki nang pantay ang kalabasa.",
                actionSteps = listOf(
                    "Huwag kailanman mag-spray ng pamatay-peste (kemikal o organic) sa umaga mula 5:30 AM hanggang 10:00 AM.",
                    "Magtanim ng mga bulaklak tulad ng marigold, cosmos, o sunflowers sa paligid upang lalong maakit ang mga pukyutan at kiwot (stingless bees).",
                    "Maglagay ng mabababaw na lagayan ng tubig na may lumulutang na bato o kahoy para inuman ng mga bubuyog."
                ),
                materialsNeeded = listOf("Halamang Marigold / Bulaklak", "Mababaw na Lalagyan ng Tubig"),
                organicRemedy = "Pagtatanim ng nectar-rich companion plants sa paligid ng taniman ng kalabasa.",
                criticalRule = "Iwasan ang anumang pesticide spray sa umaga upang hindi mamatay ang mga kapaki-pakinabang na bubuyog."
            )
        }
        if (selectedOptions.contains("squash_poll_all_male")) {
            return DirectCropSolution(
                title = "Normal Male-Phase Flowering",
                tagalogTitle = "Puro Lalaking Bulaklak Lamang (Normal na Yugto)",
                severity = "ADVISORY",
                badgeColor = AlertAmber,
                directAnswer = "Likas sa kalabasa (monoecious) na maglabas ng 10–20 lalaking bulaklak sa unang 1–2 linggo ng pamumulaklak upang maakit ang mga bubuyog sa taniman bago lumabas ang unang babaeng bulaklak.",
                actionSteps = listOf(
                    "Huwag mabahala; hintayin ang susunod na 7–10 araw para sa paglabas ng mga babaeng bulaklak (may munting bilog na kalabasa sa ilalim).",
                    "Maaaring anihin ang sobrang lalaking bulaklak para ulamin (gisado o pinakbet), magtira lamang ng 2–3 bulaklak bawat baging para sa polon.",
                    "Siguraduhing sapat ang sikat ng araw (buong araw) dahil ang lilim ay nagpapadami ng lalaking bulaklak."
                ),
                materialsNeeded = listOf("Gunting sa Pag-ani ng Bulaklak"),
                organicRemedy = "Pag-ani ng 50% ng lalaking bulaklak bilang masustansyang gulay bago lumabas ang babae.",
                criticalRule = "Huwag ubusin ang lalaking bulaklak; kailangan ang polon nito kapag bumuka na ang babae."
            )
        }
        if (selectedOptions.contains("squash_poll_aborted_female") || selectedOptions.contains("squash_poll_need_hand")) {
            return DirectCropSolution(
                title = "Hand Pollination for High Fruit Set",
                tagalogTitle = "Mano-Manong Polinasyon (Hand Pollination)",
                severity = "WARNING",
                badgeColor = AlertAmber,
                directAnswer = "Kapag walang bubuyog o maulan, ang babaeng bulaklak ay hindi mabubuntis at naninilaw, lumalambot, at nalalaglag sa loob ng 2 araw. Ang mano-manong polinasyon ay nagtitiyak ng 95% tagumpay sa pamumunga.",
                actionSteps = listOf(
                    "Gumising nang maaga (6:00 AM – 8:30 AM) kapag sariwang bumubuka ang mga bulaklak.",
                    "Pumitas ng sariwang lalaking bulaklak (yung mahaba ang tangkay at walang bukol sa ilalim).",
                    "Maingat na punitin o tanggalin ang dilaw na talulot (petals) upang maiwan ang gitnang anther na puno ng dilaw na polon.",
                    "Dahan-dahang ikuskos o ipahid ang polon sa stigma (gitnang bahagi) ng nakabukang babaeng bulaklak.",
                    "Ang isang lalaking bulaklak ay sapat para lagyan ng polon ang 2 hanggang 3 babaeng bulaklak."
                ),
                materialsNeeded = listOf("Malinis na Kamay o Maliit na Watercolor Brush", "Gunting"),
                organicRemedy = "Mano-manong paglilipat ng polon gamit ang anther ng lalaking bulaklak o malambot na brush.",
                criticalRule = "Gawin bago mag-9:00 AM; matutuyo ang polon at magsasara ang bulaklak pagsapit ng tanghali."
            )
        }
    }

    // 4. PESTS
    if (activityId == "pests") {
        if (selectedOptions.contains("squash_pest_none")) {
            return DirectCropSolution(
                title = "Clean Vines & Zero Pest Infestation",
                tagalogTitle = "Malinis at Ligtas sa Peste ang Kalabasa",
                severity = "IDEAL",
                badgeColor = LushGreen,
                directAnswer = "Walang nakikitang pinsala ng peste. Panatilihin ang lingguhang pag-iinspeksyon lalo na sa ilalim ng mga dahon at sa balat ng maliliit na kalabasa.",
                actionSteps = listOf(
                    "Magsagawa ng regular na scouting tuwing umaga bago mag-init ang araw.",
                    "Panatilihing malinis ang paligid ng taniman laban sa mga ligaw na damo na pamahayan ng leaf beetles.",
                    "Magkabit ng yellow sticky traps sa agwat na 5 metro upang maagang mahuli ang whiteflies at aphids."
                ),
                materialsNeeded = listOf("Yellow Sticky Traps"),
                organicRemedy = "Preventive neem oil spray (1 kutsara neem + 1 kutsara liquid soap sa 4 litrong tubig) tuwing 14 araw.",
                criticalRule = "Huwag maghintay na dumami ang peste bago maglagay ng monitoring traps."
            )
        }
        if (selectedOptions.contains("squash_pest_fruit_fly")) {
            return DirectCropSolution(
                title = "Melon Fruit Fly (Bactrocera cucurbitae)",
                tagalogTitle = "Pagpuksa sa Melon Fruit Fly sa Bunga",
                severity = "CRITICAL",
                badgeColor = DangerRed,
                directAnswer = "Ang langaw ng prutas ang numero unong kaaway ng kalabasa. Tinutusok ng babaeng langaw ang balat ng munting kalabasa at nangingitlog sa loob; ang mga uod ay kumakain ng laman at nagdudulot ng pagkabulok at pagkalaglag.",
                actionSteps = listOf(
                    "Balutin agad ang maliliit na kalabasa (fruit bagging) gamit ang lumang diyaryo, brown paper bag, o net bag pagkatapos ma-pollinate.",
                    "Maglagay ng Cue-lure o Methyl Eugenol traps sa paligid ng taniman upang hulihin ang mga lalaking langaw.",
                    "Pulutin at ilibing nang malalim (hindi bababa sa 50 cm sa ilalim ng lupa) ang lahat ng natusok at bulok na bunga; huwag iwan sa ibabaw.",
                    "Mag-spray ng protein bait na may halong spinosad sa mga damo sa paligid, huwag direkta sa bulaklak."
                ),
                materialsNeeded = listOf("Fruit Bags / Lumang Diyaryo", "Cue-lure Pheromone Traps", "Pala sa Paglilibing"),
                organicRemedy = "Fruit bagging ng bunga at pheromone traps na may Cue-lure para sa lalaking langaw.",
                criticalRule = "Ilibing ang lahat ng bulok na bunga; bawat isang bulok na kalabasa ay naglalabas ng daan-daang bagong langaw."
            )
        }
        if (selectedOptions.contains("squash_pest_squash_beetle")) {
            return DirectCropSolution(
                title = "Cucurbit Leaf Beetle (Aulacophora spp.)",
                tagalogTitle = "Pagpuksa sa Dilaw na Salagubang ng Kalabasa",
                severity = "WARNING",
                badgeColor = AlertAmber,
                directAnswer = "Ang mga dilaw-kahel na salagubang ay mabilis gumupit ng mga pabilog na butas sa dahon, lalo na sa mga bagong sibol na punla, na nagdudulot ng pagkabansot.",
                actionSteps = listOf(
                    "Pumitas at puksain sa kamay (handpicking) ang mga salagubang sa maagang umaga habang mabagal pa silang lumipad dahil sa hamog.",
                    "Magbudbod ng pinong abo ng kahoy (wood ash) sa ibabaw ng mga dahon; ayaw ng salagubang ang lasa at texture nito.",
                    "Mag-spray ng pinakuluang katas ng sili at bawang o neem oil spray sa ilalim at ibabaw ng dahon.",
                    "Takpan ng net o row cover ang mga batang punla sa unang 3 linggo."
                ),
                materialsNeeded = listOf("Abo ng Kahoy (Wood Ash)", "Neem Extract / Sili-Garlic Spray", "Gunting"),
                organicRemedy = "Pagbubudbod ng sariwang tuyong abo ng kahoy sa dahon pagkatapos madiligan.",
                criticalRule = "Pinakasensitibo ang kalabasa sa beetle sa yugto ng punla (unang 2–4 na dahon)."
            )
        }
        if (selectedOptions.contains("squash_pest_aphids")) {
            return DirectCropSolution(
                title = "Melon Aphids & Virus Transmission",
                tagalogTitle = "Pagpuksa sa Kuto at Puting Langaw (SLCV Vector)",
                severity = "WARNING",
                badgeColor = AlertAmber,
                directAnswer = "Ang aphids at whiteflies ay sumisipsip ng katas ng talbos at siyang pangunahing nagpapakalat ng Squash Leaf Curl Virus (SLCV) at Watermelon Mosaic Virus sa buong taniman.",
                actionSteps = listOf(
                    "Mag-spray agad ng sabong panlaba o potassium soap solution (1 kutsarang liquid dish soap sa 4 litrong tubig) sa ilalim ng mga dahon.",
                    "Maglagay ng dilaw na sticky traps (yellow sticky traps) sa taas ng mga dahon upang mahuli ang mga lumilipad na whiteflies.",
                    "Huwag gumamit ng broad-spectrum insecticides na pumapatay sa mga ladybug at gagamba na natural na kumakain ng aphids."
                ),
                materialsNeeded = listOf("Insecticidal Soap / Mild Liquid Soap", "Yellow Sticky Traps", "Neem Oil"),
                organicRemedy = "Soapy water spray na may neem oil tuwing 5 araw hanggang maubos ang kuto.",
                criticalRule = "Wisikan ang ilalim ng dahon kung saan nagkukumpulan ang mga peste."
            )
        }
        if (selectedOptions.contains("squash_pest_vine_borer")) {
            return DirectCropSolution(
                title = "Squash Vine Borer (Melittia spp.)",
                tagalogTitle = "Pagpuksa sa Uod sa Loob ng Baging (Vine Borer)",
                severity = "CRITICAL",
                badgeColor = DangerRed,
                directAnswer = "Nangingitlog ang gamu-gamo sa puno ng baging at ang uod ay ngumangata sa loob ng stem, dahilan upang biglang malanta ang sanga kahit basa ang lupa. Makikita ang parang basing lagari (frass) sa butas.",
                actionSteps = listOf(
                    "Hanapin ang butas na may frass; gumamit ng matalas na labaha upang maingat na hiwain ang baging nang paayon (longitudinal slit) at patayin ang uod sa loob.",
                    "Matapos mapatay ang uod, itakip at ibaon sa matabang basang lupa ang bahaging nahiwa (hilling up node) upang tubuan ito ng mga bagong adventitious roots.",
                    "Balutin ng aluminum foil o lumang tela ang unang 10 cm ng base ng puno upang hindi mapangitlugan ng gamu-gamo.",
                    "I-drench ng Bacillus thuringiensis (Bt) ang base ng baging."
                ),
                materialsNeeded = listOf("Matalas na Labaha / Cutter", "Basang Lupa para sa Hilling", "Bacillus thuringiensis (Bt)"),
                organicRemedy = "Pagbaon ng mga buko (nodes) ng baging sa ilalim ng lupa upang maglabas ng bagong ugat.",
                criticalRule = "Kapag napatay ang uod, agad ibaon sa lupa ang buko ng baging upang makabawi ang halaman."
            )
        }
    }

    // 5. DISEASES
    if (activityId == "diseases") {
        if (selectedOptions.contains("squash_disease_none")) {
            return DirectCropSolution(
                title = "Disease-Free Foliage & Vigor",
                tagalogTitle = "Malinis at Ligtas sa Sakit ang mga Dahon",
                severity = "IDEAL",
                badgeColor = LushGreen,
                directAnswer = "Ligtas sa amag at virus ang iyong taniman. Ang maayos na sirkulasyon ng hangin sa pagitan ng mga baging ay susi sa patuloy na kalusugan nito.",
                actionSteps = listOf(
                    "Panatilihin ang sapat na agwat at iwasang magpatong-patong nang makapal ang mga dahon.",
                    "Iwasan ang pagdidilig sa hapon o gabi upang hindi manatiling basa ang dahon magdamag.",
                    "Putulan ang mga naninilaw na lumang dahon sa pinakapuno upang makapasok ang hangin."
                ),
                materialsNeeded = listOf("Pruning Shears"),
                organicRemedy = "Preventive baking soda spray (1 kutsaritang baking soda + kaunting sabon sa 1 litrong tubig) tuwing 2 linggo.",
                criticalRule = "Ang tuyong dahon sa gabi ay ang pinakamabisang panangga laban sa fungal mildew."
            )
        }
        if (selectedOptions.contains("squash_disease_powdery_mildew")) {
            return DirectCropSolution(
                title = "Powdery Mildew (Podosphaera / Erysiphe)",
                tagalogTitle = "Lunas sa Puting Pulbos sa Dahon (Powdery Mildew)",
                severity = "WARNING",
                badgeColor = AlertAmber,
                directAnswer = "Karaniwang lumalabas kapag mainit sa araw ngunit malamig at mahalumigmig sa gabi. Ang puting amag ay bumabalot sa dahon, humaharang sa sikat ng araw, at nagpapatuyo sa baging bago pa mahinog ang kalabasa.",
                actionSteps = listOf(
                    "Gupitin at sunugin ang mga lumang dahon na balot na balot na ng puting pulbos.",
                    "Mag-spray ng Baking Soda Solution (1 kutsara ng baking soda + 1 kutsara ng cooking oil + kaunting liquid soap sa 4 litrong tubig) sa buong halaman.",
                    "Maaari ring gumamit ng diluted fresh milk spray (1 bahagi gatas sa 9 na bahagi ng tubig) na may natural na protinang sumisira sa fungal spores sa ilalim ng araw.",
                    "Sa malubhang impeksyon, gumamit ng wettable sulfur spray, ngunit huwag gagamitin kapag lampas 32°C ang temperatura."
                ),
                materialsNeeded = listOf("Baking Soda", "Cooking Oil / Liquid Soap", "Fresh Milk", "Wettable Sulfur"),
                organicRemedy = "Milk spray (1:9 ratio sa tubig) na ini-spray sa katanghalian; ang sikat ng araw ay gumagawa ng antiseptic free radicals laban sa mildew.",
                criticalRule = "I-spray sa unang senyales pa lamang ng puting tuldok bago mabalot ang buong dahon."
            )
        }
        if (selectedOptions.contains("squash_disease_downy_mildew")) {
            return DirectCropSolution(
                title = "Downy Mildew (Pseudoperonospora cubensis)",
                tagalogTitle = "Lunas sa Downy Mildew (Sulok-sulok na Dilaw na Batik)",
                severity = "CRITICAL",
                badgeColor = DangerRed,
                directAnswer = "Mabilis kumalat sa maulang panahon o malamig na mahamog na gabi. Nagpapakita ng angular yellow spots sa ibabaw at purplish-gray mold sa ilalim ng dahon; mabilis nitong pinapatay ang buong baging sa loob ng ilang araw.",
                actionSteps = listOf(
                    "Tanggalin agad ang mga apektadong dahon at ilayo sa taniman; huwag i-compost.",
                    "Mag-spray ng Copper Fungicide (Copper Hydroxide o Copper Oxychloride) o Bordeaux mixture sa ilalim at ibabaw ng mga dahon.",
                    "Huwag kailanman magdilig mula sa itaas (no overhead irrigation); panatilihing tuyo ang mga dahon.",
                    "Magtanim ng mga resistant varieties tulad ng Suprema F1 sa susunod na cropping."
                ),
                materialsNeeded = listOf("Copper Fungicide / Bordeaux Mixture", "Knapsack Sprayer", "Pruning Shears"),
                organicRemedy = "Copper-based protectant spray at agarang sanitation ng mga apektadong dahon.",
                criticalRule = "Nangangailangan ng tubig sa ibabaw ng dahon ang downy mildew upang makahawa; panatilihing tuyo ang dahon."
            )
        }
        if (selectedOptions.contains("squash_disease_leaf_curl_virus")) {
            return DirectCropSolution(
                title = "Squash Leaf Curl Virus (SLCV)",
                tagalogTitle = "Squash Leaf Curl Virus (Kulot at Bansot na Talbos)",
                severity = "CRITICAL",
                badgeColor = DangerRed,
                directAnswer = "Sakit na dulot ng virus at ikinakalat ng whiteflies (Bemisia tabaci). Walang kemikal na nakakapagpagaling sa virus kapag pumasok na sa halaman. Nagdudulot ito ng makakapal na kulot na talbos at pagkabansot ng bunga.",
                actionSteps = listOf(
                    "Bunutin at sunugin o ilibing agad ang mga punlang may malubhang sintomas upang hindi mahawa ang ibang halaman.",
                    "Puksain agad ang mga puting langaw (whiteflies) gamit ang yellow sticky traps at insecticidal soap spray.",
                    "Sa susunod na taniman, pumili lamang ng sertipikadong hybrid na may resistensya sa virus tulad ng Suprema F1.",
                    "Maglagay ng reflective silver plastic mulch sa bunton upang itaboy ang mga lumilipad na whiteflies."
                ),
                materialsNeeded = listOf("Yellow Sticky Traps", "Silver Reflective Mulch", "Suprema F1 Seeds"),
                organicRemedy = "Aggressive control sa whiteflies gamit ang neem extract at reflective mulch.",
                criticalRule = "Bunutin agad ang may virus; ang pag-iwan dito ay magiging sanhi ng pagkalat sa buong taniman."
            )
        }
        if (selectedOptions.contains("squash_disease_wet_rot")) {
            return DirectCropSolution(
                title = "Choanephora Wet Rot of Blossoms & Fruit",
                tagalogTitle = "Choanephora Wet Rot: Bulok na Bulaklak at Mura",
                severity = "WARNING",
                badgeColor = AlertAmber,
                directAnswer = "Fungus na sumasalakay sa mga lantang bulaklak sa panahon ng sunod-sunod na ulan. Nagmumukhang basa at may maitim na parang balahibo (whisker-like growth) na lumilipat sa dulo ng munting kalabasa.",
                actionSteps = listOf(
                    "Maingat na tanggalin sa kamay ang mga lantang talulot ng bulaklak sa dulo ng munting kalabasa matapos ang polinasyon.",
                    "Itaas ang mga munting bunga sa ibabaw ng tuyong dayami upang hindi mababad sa basang putik.",
                    "Iwasan ang overhead watering at mag-spray ng copper fungicide kung tuloy-tuloy ang pag-ulan."
                ),
                materialsNeeded = listOf("Tuyong Dayami / Rice Straw", "Copper Fungicide"),
                organicRemedy = "Pag-aalis ng nalantang talulot (de-petaling) 2–3 araw matapos ma-pollinate ang bunga.",
                criticalRule = "Huwag hayaang nakadikit ang nabubulok na bulaklak sa dulo ng lumalaking bunga."
            )
        }
    }

    // 6. VINE MANAGEMENT
    if (activityId == "vine_management") {
        if (selectedOptions.contains("squash_vine_proper_spacing")) {
            return DirectCropSolution(
                title = "Ideal Hill Spacing & Mulching",
                tagalogTitle = "Wastong Agwat ng Bunton at Pag-aalaga sa Baging",
                severity = "IDEAL",
                badgeColor = LushGreen,
                directAnswer = "Napakaganda ng ayos ng iyong taniman. Ang 2–3 metrong agwat ng bawat bunton at malinis na daanan ay nagbibigay ng pinakamataas na sikat ng araw at mababang banta ng amag.",
                actionSteps = listOf(
                    "Igiya ang mga gapang ng baging palayo sa mga daanan upang hindi maapakan ng magsasaka.",
                    "Panatilihin ang makapal na dayami sa ilalim ng bawat bunga upang mapanatiling makinis at walang peklat ang balat.",
                    "I-weed ang paligid ng bunton hanggang sa matakpan ng mga dahon ang buong lupa."
                ),
                materialsNeeded = listOf("Tuyong Dayami / Mulch"),
                organicRemedy = "Rice straw mulching na sumusupil din sa mga damo at nagpapanatili ng moisture.",
                criticalRule = "Huwag tapakan ang mga gumagapang na baging dahil madali itong mabalian ng daluyan ng sustansya."
            )
        }
        if (selectedOptions.contains("squash_vine_overcrowded")) {
            return DirectCropSolution(
                title = "Overcrowded Vines & Poor Aeration",
                tagalogTitle = "Lunas sa Masinsin at Magulong Baging",
                severity = "WARNING",
                badgeColor = AlertAmber,
                directAnswer = "Kapag nagsapaw-sapaw ang mga baging sa maliit na espasyo, naiipon ang hamog at init, na nagiging sanhi ng powdery at downy mildew, at nahihirapan ang mga bubuyog na makita ang mga bulaklak.",
                actionSteps = listOf(
                    "Igiya nang maayos ang mga baging sa magkakaibang direksyon (fan pattern).",
                    "Putulin ang mga sobrang tertiary lateral branches na walang bulaklak o bunga.",
                    "Alisin ang mga matatandang dahon sa ilalim na naninilaw na upang pumasok ang hangin at liwanag.",
                    "Kung maliit ang lote, gumawa ng simpleng balag o trellis (1.5–2 metro ang taas) upang ipaakyat ang mga baging."
                ),
                materialsNeeded = listOf("Pruning Shears", "Tulos na Kawayan / Trellis Net"),
                organicRemedy = "Selective pruning ng mga sangang walang bunga upang mapabuti ang sirkulasyon ng hangin.",
                criticalRule = "Magtira ng sapat na dahon para sa photosynthesis ng bawat lumalaking bunga."
            )
        }
        if (selectedOptions.contains("squash_vine_direct_ground_rot")) {
            return DirectCropSolution(
                title = "Ground-Contact Fruit Rot Prevention",
                tagalogTitle = "Proteksyon sa Bungang Nakalapat sa Lupa",
                severity = "WARNING",
                badgeColor = AlertAmber,
                directAnswer = "Ang ilalim ng kalabasa na direktang nakalapat sa basang putik ay madaling maagnas, magkaroon ng puting amag, o mapasok ng mga uod at insekto sa lupa.",
                actionSteps = listOf(
                    "Maglatag ng makapal na tuyong dayami (rice straw), tuyong dahon ng saging, o piraso ng kahoy/karton sa ilalim ng bawat lumalaking bunga.",
                    "Maingat na iangat at patagin ang posisyon ng kalabasa nang hindi napipilipit o nababali ang tangkay.",
                    "Siguraduhing hindi maiipon ang tubig sa ilalim ng bunga kapag umuulan."
                ),
                materialsNeeded = listOf("Tuyong Dayami", "Dahon ng Saging", "Pira-pirasong Kahoy o Styrofoam"),
                organicRemedy = "Paglalagay ng sapin na dayami o tuyong dahon sa ilalim ng bawat kalabasa.",
                criticalRule = "Huwag pilipitin nang marahas ang tangkay habang naglalagay ng sapin."
            )
        }
        if (selectedOptions.contains("squash_vine_excess_runners")) {
            return DirectCropSolution(
                title = "Runner Pruning & Fruit Thinning",
                tagalogTitle = "Pagpupungos ng Suwi para Lumaki ang Bunga",
                severity = "ADVISORY",
                badgeColor = AlertAmber,
                directAnswer = "Kapag hinayaang lumago ang napakaraming suwi, nag-aagawan ang mga ito sa sustansya kaya nagiging maliliit ang mga kalabasa. Ang pagpupungos ay nagpapataba sa 2–3 piling bunga bawat puno.",
                actionSteps = listOf(
                    "Pumili ng 2 hanggang 3 pinakamalusog at magandang hubog na bunga bawat puno at alisin ang mga deformed o huling sumibol na bunga.",
                    "Putulan ng dulo ang baging mga 2–3 dahon lampas sa huling bunga (tip pinching) upang maipon ang enerhiya sa pagpapalaki ng bunga.",
                    "Alisin ang maliliit na bagong suwi (suckers) na sumisibol sa mga buko."
                ),
                materialsNeeded = listOf("Matalas na Pruning Shears"),
                organicRemedy = "Pinching at pag-ani ng mga pinungos na talbos bilang masustansyang gulay sa hapag-kainan.",
                criticalRule = "Huwag mag-iwan ng higit sa 3–4 na malalaking bunga bawat puno para sa de-kalidad na laki."
            )
        }
    }

    // 7. HARVESTING
    if (activityId == "harvesting") {
        if (selectedOptions.contains("squash_harvest_ready_mature")) {
            return DirectCropSolution(
                title = "Mature Squash Ready for Harvest",
                tagalogTitle = "Ganap na Hinog na Kalabasa: Handang Anihin",
                severity = "IDEAL",
                badgeColor = LushGreen,
                directAnswer = "Nasa tamang yugto na ng maturity ang iyong kalabasa (90–120 araw pagkatanim). Matigas na ang balat, parang cork at tuyo ang tangkay, at mayaman sa beta-carotene ang matingkad na kahel na laman.",
                actionSteps = listOf(
                    "Gamitin ang pruning shears upang gupitin ang tangkay, mag-iwan ng 3 hanggang 5 cm na peduncle (tangkay) na nakakabit sa bunga.",
                    "Huwag buhatin o hawakan ang kalabasa sa mismong tangkay nito dahil kapag nabali ang tangkay ay mabilis pumasok ang mikrobyo at mabulok ang bunga.",
                    "Punasan ng tuyong basahan upang maalis ang dumi sa bukid; iwasang hugasan ng maraming tubig bago itago.",
                    "I-cure o patuyuin sa malilim at mahanging lugar sa loob ng 7–10 araw bago ibiyahe o itago."
                ),
                materialsNeeded = listOf("Pruning Shears", "Malinis na Basahan", "Kahon o Plastic Crates"),
                organicRemedy = "Curing sa malilim at mahanging lugar (24–28°C) para tumigas ang balat at magtagal nang ilang buwan.",
                criticalRule = "Laging mag-iwan ng 3–5 cm na tangkay; ang kalabasang walang tangkay ay madaling mabulok sa imbakan."
            )
        }
        if (selectedOptions.contains("squash_harvest_ready_young")) {
            return DirectCropSolution(
                title = "Harvesting Young Squash & Shoots",
                tagalogTitle = "Pag-aani ng Murang Kalabasa at Talbos",
                severity = "IDEAL",
                badgeColor = LushGreen,
                directAnswer = "Maaari nang anihin ang murang kalabasa (50–60 araw pagkatanim) kasama ang malalambot na talbos at bulaklak para sa sariwang pagkonsumo o mabilisang benta sa palengke.",
                actionSteps = listOf(
                    "Pitasin ang mga murang bunga habang malambot pa ang balat at hindi pa matigas ang mga buto.",
                    "Anihin ang mga talbos sa umaga bago mag-init ang araw; pumutol ng 15–20 cm mula sa dulo ng baging.",
                    "Ibenta o lutuin agad dahil mas mabilis malanta at masira ang murang kalabasa kumpara sa ganap na hinog."
                ),
                materialsNeeded = listOf("Gunting / Pruning Knife", "Basket para sa Talbos"),
                organicRemedy = "Pang-araw-araw na pag-ani ng talbos at bulaklak para sa dagdag na kita habang hinihintay ang hinog na bunga.",
                criticalRule = "Magtira ng sapat na baging kapag kumukuha ng talbos upang hindi maunsyami ang pamumunga."
            )
        }
        if (selectedOptions.contains("squash_harvest_not_ready")) {
            return DirectCropSolution(
                title = "Immature Fruit: Not Ready for Storage",
                tagalogTitle = "Hilaw Pa ang Kalabasa: Huwag Muna Anihin",
                severity = "ADVISORY",
                badgeColor = AlertAmber,
                directAnswer = "Kapag bumaon pa ang kuko sa balat at matingkad na berde pa ang tangkay, hilaw pa ang kalabasa. Kapag inani ngayon, magiging matubig, maputla, matabang, at mabubulok lamang sa imbakan sa loob ng ilang araw.",
                actionSteps = listOf(
                    "Iwanan muna sa baging ang bunga sa loob ng susunod na 10 hanggang 20 araw.",
                    "Hintaying maging dull (hindi makintab) ang kulay ng balat at magkaroon ng parang pulbos o wax.",
                    "Hintaying matuyo at magkulay kayumanggi o abuhin ang tangkay bago gupitin."
                ),
                materialsNeeded = listOf("Kaunting Pasensya at Patuloy na Pag-aalaga"),
                organicRemedy = "Hayaang mahinog sa mismong baging upang maipon ang natural na starch at beta-carotene.",
                criticalRule = "Hindi hihusto ang tamis at linamnam ng kalabasa kapag pinitas habang malambot pa ang balat."
            )
        }
        if (selectedOptions.contains("squash_harvest_stem_cut")) {
            return DirectCropSolution(
                title = "Stem Retention Technique for Long Shelf Life",
                tagalogTitle = "Tamang Pamamaraan ng Pagputol ng Tangkay",
                severity = "IDEAL",
                badgeColor = LushGreen,
                directAnswer = "Napakahalaga ng tamang pagputol ng tangkay para sa pangmatagalang imbakan. Ang kalabasang may matigas na 3–5 cm na tangkay ay tumatagal ng 2 hanggang 4 na buwan nang hindi nabubulok.",
                actionSteps = listOf(
                    "Gumamit lamang ng matalas at malinis na pruning shears o kutsilyo; huwag pilipitin gamit ang kamay.",
                    "I-disinfect ang talim sa 70% alcohol bago gupitin ang bawat bunga upang hindi maglipat ng sakit.",
                    "Itago ang mga inaning kalabasa sa tuyo, mahangin, at madilim na bodega sa temperaturang 12–15°C kung mayroon, o sa silong ng bahay."
                ),
                materialsNeeded = listOf("Pruning Shears", "70% Isopropyl Alcohol", "Mahanging Imbakan"),
                organicRemedy = "Pagpapatuyo ng gupit na tangkay sa hangin upang magkalyo (callus) at sumara laban sa bacteria.",
                criticalRule = "Huwag pagpatung-patungin nang higit sa 2–3 patong ang mabibigat na kalabasa upang hindi magkapasa ang balat."
            )
        }
    }

    return null
}
