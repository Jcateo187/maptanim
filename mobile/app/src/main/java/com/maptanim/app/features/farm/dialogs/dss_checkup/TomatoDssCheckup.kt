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

fun getTomatoCheckUpActivities(): List<CheckUpActivity> {
    return listOf(
        CheckUpActivity(
            id = "staking",
            title = "🪵 Pagtutulos at Balag (Staking & Trellis)",
            tagalogTitle = "Suporta Laban sa Paglapat sa Lupa",
            icon = Icons.Default.Eco,
            description = "Pagsusuri sa tindig ng puno ng kamatis, suportang kawayan, at pag-iwas sa pagkadikit ng bunga sa basang lupa.",
            options = listOf(
                CheckUpOption(
                    id = "fruit_rot_soil",
                    label = "Sumasayad ang mga bunga ng kamatis sa basang lupa",
                    description = "Maaaring mabulok (soil rot) o pamahayan ng amag kung nakalapat sa mamasa-masang lupa."
                ),
                CheckUpOption(
                    id = "falling_ground",
                    label = "Bumabagsak o natutumba ang tangkay dahil walang tulos",
                    description = "Nababali ang sanga kapag nahanginan o kapag dumami ang mga kumpol ng kamatis."
                ),
                CheckUpOption(
                    id = "heavy_fruit",
                    label = "Masyadong mabigat ang mga kumpol ng berdeng bunga",
                    description = "Kailangan ng karagdagang 'Figure-8' tie o sangang suporta upang hindi mahati ang katawan ng halaman."
                ),
                CheckUpOption(
                    id = "wind_storm",
                    label = "Nababali ang mga sanga dahil sa malalakas na hangin o ulan",
                    description = "Kailangan ng mas matibay na bamboo A-frame o Florida Weave system."
                )
            )
        ),
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
                    description = "Kulang sa drainage canal; panganib ng bacterial wilt at pagkabulok ng ugat."
                ),
                CheckUpOption(
                    id = "yellow_overwater",
                    label = "Naninilaw ang ibabang dahon kahit mamasa-masa pa ang lupa",
                    description = "Senyales ng labis na pagdidilig (overwatering) at kawalan ng oxygen sa ugat."
                )
            )
        ),
        CheckUpActivity(
            id = "fertilizer",
            title = "🌱 Pataba at Sustansya (Nutrition)",
            tagalogTitle = "Kakulangan o Sobra sa Abono",
            icon = Icons.Default.Eco,
            description = "Pagsusuri sa blossom-end rot, pagkalaglag ng bulaklak, at tamang balanseng nutrisyon (N-P-K at Calcium).",
            options = listOf(
                CheckUpOption(
                    id = "blossom_end_rot",
                    label = "Maitim at lubog ang puwit ng bunga ng kamatis (Blossom End Rot)",
                    description = "Kakulangan sa Calcium (Ca) at hindi pantay na pagdidilig habang lumalaki ang bunga."
                ),
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
        ),
        CheckUpActivity(
            id = "pests",
            title = "🐛 Peste at Insekto (Pest Control)",
            tagalogTitle = "Pamamahala sa mga Uod at Peste sa Kamatis",
            icon = Icons.Default.BugReport,
            description = "Pagkontrol sa uod sa bunga (Fruit Borer), aphids, whiteflies, at leaf miners sa kamatis.",
            options = listOf(
                CheckUpOption(
                    id = "fruit_borer",
                    label = "May butas ang bunga na may dumi ng uod (Tomato Fruit Borer)",
                    description = "Helicoverpa armigera na pumapasok sa loob ng bunga at nagpabubulok nito."
                ),
                CheckUpOption(
                    id = "aphids_whitefly",
                    label = "May maliliit na mapuputi o berdeng kulisap sa ilalim ng dahon (Whiteflies / Aphids)",
                    description = "Sumisipsip ng katas ng dahon at nagdadala ng Tomato Yellow Leaf Curl Virus (TYLCV)."
                ),
                CheckUpOption(
                    id = "leaf_miner",
                    label = "May mapuputing parang kalyeng linya sa dahon (Leaf Miner)",
                    description = "Uod na gumagapang sa loob ng epidermis ng dahon ng kamatis."
                ),
                CheckUpOption(
                    id = "leaf_curling",
                    label = "Kumukulot paitaas at tumitigas ang bagong dahon (Kulot / TYLCV)",
                    description = "Viral infection na ikinalat ng mga puting langaw (whiteflies)."
                ),
                CheckUpOption(
                    id = "chewed_holes",
                    label = "Tadtad ng butas ang dahon (Flea Beetle / Hornworms)",
                    description = "Kinakain ng mga uwang o malalaking berdeng uod ang buong dahon."
                )
            )
        ),
        CheckUpActivity(
            id = "disease",
            title = "🍂 Sakit at Dahon (Diseases)",
            tagalogTitle = "Foliar at Soil-borne Pathogens sa Kamatis",
            icon = Icons.Default.Coronavirus,
            description = "Paggamot sa Early Blight, Bacterial Wilt, Powdery Mildew, at Mosaic Virus sa kamatis.",
            options = listOf(
                CheckUpOption(
                    id = "bacterial_wilt",
                    label = "Biglaang pagkalanta ng buong halaman habang berde pa ang dahon (Bacterial Wilt)",
                    description = "Ralstonia solanacearum na bumabara sa vascular bundle sa loob ng 24–48 oras."
                ),
                CheckUpOption(
                    id = "early_blight",
                    label = "May pabilog at maitim na concentric rings sa dahon (Early Blight - Alternaria)",
                    description = "Target-like spots na may dilaw na paligid sa ibabang dahon ng kamatis."
                ),
                CheckUpOption(
                    id = "powdery_mildew",
                    label = "Mapuputing parang pulbos sa ibabaw ng dahon (Powdery Mildew)",
                    description = "Puting amag na bumabalot sa dahon at humaharang sa sikat ng araw."
                ),
                CheckUpOption(
                    id = "mosaic_virus",
                    label = "Kulubot at may batik-batik na dilaw/berde ang dahon (Mosaic Virus)",
                    description = "Tobacco o Tomato Mosaic Virus na ikinakalat ng aphids at maruming kamay."
                )
            )
        ),
        CheckUpActivity(
            id = "pruning",
            title = "✂️ Pruning at Suwi (Training)",
            tagalogTitle = "Pag-aalis ng Suwi at Pagbabawas ng Dahon",
            icon = Icons.Default.ContentCut,
            description = "Pagtatanggal ng suckers sa kili-kili ng sanga at lumang dahon sa ibaba para lumaki ang mga bunga.",
            options = listOf(
                CheckUpOption(
                    id = "excess_suckers",
                    label = "Maraming suwi na tumutubo sa kili-kili ng sanga (Suckers)",
                    description = "Mga bagong sanga na umagaw sa sustansya ng pangunahing tangkay ng kamatis."
                ),
                CheckUpOption(
                    id = "bottom_leaves",
                    label = "Dahon sa ibaba (unang 30cm) ay naninilaw at sumasayad sa basang lupa",
                    description = "Panganib ng soil-borne splash blight tuwing umuulan o nagdidilig."
                ),
                CheckUpOption(
                    id = "dense_bush",
                    label = "Masyadong masinsin ang gitna ng puno at walang pumapasok na hangin",
                    description = "Kakulangan ng sirkulasyon na nagiging sanhi ng pamumuo ng amag."
                )
            )
        ),
        CheckUpActivity(
            id = "harvest",
            title = "🧺 Pag-aani (Harvesting)",
            tagalogTitle = "Gabay sa Pitas ng Kamatis",
            icon = Icons.Default.Agriculture,
            description = "Tamang yugto ng pitas (Breaker vs Red Ripe), paraan ng paggupit, at pag-iingat sa shelf-life.",
            options = listOf(
                CheckUpOption(
                    id = "harvest_timing",
                    label = "Paano malalaman kung handa na pitasin (Breaker stage hanggang Pink/Red)",
                    description = "Pagpitas kapag may bahagyang kulay rosas na sa puwit ng bunga para tumagal sa transportasyon."
                ),
                CheckUpOption(
                    id = "proper_picking",
                    label = "Tamang paraan ng pagpitas gamit ang shears na may calyx/tangkay",
                    description = "Pag-iwan ng 1 pulgadang tangkay upang hindi pasukin ng mikrobyo ang bunga."
                )
            )
        )
    )
}

fun solveTomatoProblem(
    activityId: String,
    selectedOptions: Set<String>
): DirectCropSolution? {
    if (selectedOptions.isEmpty()) return null

    return when (activityId) {
        "staking" -> {
            when {
                selectedOptions.contains("fruit_rot_soil") || selectedOptions.contains("falling_ground") -> {
                    DirectCropSolution(
                        title = "Pagtutulos at Elevation Laban sa Soil Rot ng Kamatis",
                        tagalogTitle = "Ilayo ang Bunga sa Basang Lupa Gamit ang Tulos",
                        severity = "Agad na Gawin (Immediate Action)",
                        badgeColor = RedAlert,
                        directAnswer = "Ang pagkadikit ng bunga ng kamatis sa basang lupa ang pangunahing sanhi ng soil rot at fungal contamination. Kailangang itayo agad ang halaman gamit ang tulos na kawayan (1.5m hanggang 2m) at gupitin ang lahat ng dahong sumasayad sa lupa upang hindi makatalon ang fungal spores.",
                        actionSteps = listOf(
                            "Kumuha ng tulos na kawayan (1.5 hanggang 2 metro ang haba) at patalasin ang ibaba.",
                            "Ibaon ang tulos nang 20–30 sentimetro ang lalim, may distansyang 3–5 pulgada mula sa puno upang hindi masira ang pangunahing ugat.",
                            "Talian ang kamatis sa tulos gamit ang malambot na tali sa hugis '8' (Figure-8 tie). Mag-iwan ng 1–2 pulgadang luwag para sa paglaki ng tangkay.",
                            "Gupitin gamit ang malinis na gunting ang lahat ng dahon na nasa unang 30cm mula sa ibabaw ng lupa."
                        ),
                        materialsNeeded = listOf(
                            "Kawayan o patpat na may habang 1.5–2 metro",
                            "Malambot na tali (abaka, strips ng lumang t-shirt, o yarn)",
                            "Matalas na gunting o pruning shears",
                            "Kahoy na abo (wood ash) para ibudbod sa lupa sa paligid ng puno"
                        ),
                        criticalRule = "Huwag kailanman gumamit ng manipis na alambre o nylon na tali nang mahigpit dahil hihiwain nito ang tangkay kapag humangin."
                    )
                }
                selectedOptions.contains("heavy_fruit") || selectedOptions.contains("wind_storm") -> {
                    DirectCropSolution(
                        title = "Double-Stake Suporta para sa Mabigat na Bunga ng Kamatis",
                        tagalogTitle = "Pagpapatatag Laban sa Hangin at Nababaling Sanga",
                        severity = "Mataas na Prayoridad",
                        badgeColor = AmberAlert,
                        directAnswer = "Kapag marami at mabigat na ang kumpol ng kamatis, kailangan ng matibay na vertical stake na may side-arm support o Florida Weave system upang maipamahagi ang bigat at hindi mabali ang sanga.",
                        actionSteps = listOf(
                            "Magbaon ng pangalawang suportang tulos sa kabilang gilid ng halaman kung napakabigat na ng mga kumpol.",
                            "Magkabit ng horizontal na suporta (bamboo cross-brace) sa pagitan ng mga tulos.",
                            "Dahan-dahang iangat ang mga sangang may bunga at isandal sa tali o tulos nang walang puwersa.",
                            "Maglagay ng mulch (tuyong dayami) sa base upang manatiling matatag ang ugat."
                        ),
                        materialsNeeded = listOf(
                            "2 pirasong kawayan bawat puno (1.8m ang haba)",
                            "Matibay na taling abaka o garden twine",
                            "Dayami (rice straw) para sa base"
                        ),
                        criticalRule = "Alisin ang mga dahong may mantsa bago magtali upang hindi mahawahan ang mga katabing sanga kapag nagkadikit-dikit."
                    )
                }
                else -> {
                    DirectCropSolution(
                        title = "Gabay sa Pagtutulos (Single-Stake / Trellis) para sa Kamatis",
                        tagalogTitle = "Wastong Pamamaraan ng Pagpapatayo ng Kamatis",
                        severity = "Pangangalaga (Maintenance)",
                        badgeColor = LushGreen,
                        directAnswer = "Ang tamang pagtutulos ng kamatis ay nagpapataas ng ani ng hanggang 40% dahil mas nasisikatan ng araw ang lahat ng dahon at malinis ang sirkulasyon ng hangin.",
                        actionSteps = listOf(
                            "Ihanda ang tulos na kawayan bago pa mamulaklak ang tanim.",
                            "Ibaon sa hilagang o silangang bahagi ng halaman upang hindi harangan ang sikat ng araw sa umaga.",
                            "Gumawa ng Figure-8 tie tuwing bawat 20cm na pagtaas ng tangkay.",
                            "Panatilihing tuwid ang pangunahing tangkay."
                        ),
                        materialsNeeded = listOf("Kawayan na may habang 1.5m", "Taling abaka o garden yarn"),
                        criticalRule = "Magtali tuwing umaga habang malambot at hindi malutong ang mga tangkay."
                    )
                }
            }
        }
        "watering" -> {
            when {
                selectedOptions.contains("cracked_dry") || selectedOptions.contains("midday_wilt") -> {
                    DirectCropSolution(
                        title = "Deep Morning Irrigation at Organic Mulching para sa Kamatis",
                        tagalogTitle = "Solusyon sa Tuyong Lupa at Heat Stress",
                        severity = "Agad na Gawin (Immediate Action)",
                        badgeColor = BlueAccent,
                        directAnswer = "Ang bitak-bitak na lupa at pagkalanta sa tanghali ay nagpapakita na mabilis sumingaw ang tubig sa ibabaw habang tuyot ang root zone. Huwag magdilig sa tirik ng araw! Magdilig nang malalim sa madaling-araw (6:00 AM) at agad lagyan ng 5cm makapal na mulch upang mapigilan ang 70% ng water evaporation.",
                        actionSteps = listOf(
                            "Diligan nang dahan-dahan ang base ng kamatis sa madaling-araw (6:00–7:30 AM) hanggang sa lumambot ang unang 15 sentimetro ng lupa.",
                            "Maglagay agad ng 5 sentimetrong kapal ng tuyong dayami (rice straw) sa ibabaw ng bed ngunit mag-iwan ng 2 pulgadang layo sa mismong tangkay.",
                            "Kung matindi ang sikat ng araw sa tanghali (11 AM–2 PM), maglagay ng temporary 30% shade net sa itaas ng bed.",
                            "Ulitin ang pagdidilig sa dakong alas-4:30 ng hapon kapag lumamig na ang lupa kung nananatiling tuyo ang ilalim."
                        ),
                        materialsNeeded = listOf(
                            "Dayami (rice straw) o cocopeat para sa mulch",
                            "Regadera o hose na may shower head",
                            "Temporary shade net kung tag-init"
                        ),
                        criticalRule = "Huwag magbuhos ng malamig na tubig sa kamatis kapag tirik ang araw ng alas-12 ng tanghali dahil magiging sanhi ito ng thermal shock."
                    )
                }
                selectedOptions.contains("waterlogged") || selectedOptions.contains("yellow_overwater") -> {
                    DirectCropSolution(
                        title = "Drainage Aeration at Pagpigil sa Root Rot ng Kamatis",
                        tagalogTitle = "Solusyon sa Binahang Ugat at Labis na Dilig",
                        severity = "Kritikal (Critical Action)",
                        badgeColor = RedAlert,
                        directAnswer = "Ang paninilaw ng dahon kahit basa ang lupa ay senyales na nalulunod ang ugat (root suffocation) dahil walang hangin sa lupa. Kung hindi agad patutuyuin ang drainage, aatakihin ng Pythium root rot at Bacterial Wilt ang kamatis sa loob ng 48 oras.",
                        actionSteps = listOf(
                            "Agad maghukay ng 15cm kanal sa magkabilang gilid ng plot upang mabilis na umagos palabas ang naipong tubig.",
                            "Itigil ang pagdidilig sa susunod na 3 hanggang 5 araw hanggang sa matuyo ang unang 2 pulgada ng lupa.",
                            "Magbudbod ng kalahating dakot ng pinong kahoy na abo (wood ash) sa paligid ng root crown upang matulungan ang pagpatay sa fungal spores.",
                            "Luwagan nang marahan ang ibabaw ng lupa gamit ang hand trowel (huwag laliman upang hindi masira ang ugat) para makapasok ang oxygen."
                        ),
                        materialsNeeded = listOf(
                            "Pala o hand trowel para sa drainage trench",
                            "Kahoy na abo (wood ash)",
                            "Apog (agricultural lime) kung acidic ang lupa matapos ang baha"
                        ),
                        criticalRule = "Huwag maglagay ng kemikal na pataba habang waterlogged ang lupa dahil masusunog ang nanghihinang mga ugat."
                    )
                }
                else -> {
                    DirectCropSolution(
                        title = "Tamang Iskedyul ng Pagdidilig para sa Kamatis",
                        tagalogTitle = "Pagpapanatili ng Tamang Halumigmig sa Ugat",
                        severity = "Pangangalaga (Irigasyon)",
                        badgeColor = LushGreen,
                        directAnswer = "Ang kamatis ay nangangailangan ng 1 hanggang 1.5 litro ng tubig bawat puno tuwing 2 araw. Diligan lamang sa ugat tuwing madaling-araw upang maiwasan ang fungal diseases.",
                        actionSteps = listOf(
                            "Magdilig sa pagitan ng 6:00 AM at 7:30 AM gamit ang drip o shower can sa paanan ng puno.",
                            "Maglagay ng 3–5cm na organic mulch (dayami) upang mapanatiling mamasa-masa ang lupa.",
                            "Suriin ang lalim ng lupa gamit ang daliri; kung basa pa ang unang 2 pulgada, huwag munang diligan."
                        ),
                        materialsNeeded = listOf("Regadera", "Dayami para sa mulch"),
                        criticalRule = "Iwasang mabasa ang mga dahon sa gabi upang hindi tubuan ng amag."
                    )
                }
            }
        }
        "fertilizer" -> {
            when {
                selectedOptions.contains("blossom_end_rot") -> {
                    DirectCropSolution(
                        title = "CalPhos Treatment Laban sa Blossom End Rot ng Kamatis",
                        tagalogTitle = "Direktang Lunas sa Maitim at Sunog na Puwit ng Bunga",
                        severity = "Kritikal (Blossom End Rot)",
                        badgeColor = RedAlert,
                        directAnswer = "Ang pangingitim ng puwit ng bunga ng kamatis ay hindi peste kundi Blossom End Rot dulot ng kakulangan sa Calcium at pabagu-bagong supply ng tubig sa lupa. Ang direktang lunas ay CalPhos foliar spray at regular na pagpapanatili ng pantay na moisture.",
                        actionSteps = listOf(
                            "Pitasin at itapon ang lahat ng bungang may itim sa puwit upang hindi magsayang ng sustansya ang halaman.",
                            "Mag-spray ng CalPhos (Calcium Phosphate mula sa inihaw na balat ng itlog at suka): Paghaluin ang 2 kutsara ng CalPhos sa 1 litro ng tubig. I-spray sa buong halaman tuwing alas-6 ng umaga.",
                            "Ulitin ang pag-spray tuwing 5 araw sa loob ng 3 linggo.",
                            "Panatilihing basa tulad ng 'wrung-out sponge' ang lupa; huwag hayaang maging sobrang tuyo bago diligan nang marami."
                        ),
                        materialsNeeded = listOf(
                            "CalPhos solution (Eggshell & vinegar extract)",
                            "Sprayer",
                            "Organic mulch para maging pantay ang moisture"
                        ),
                        criticalRule = "Huwag mag-spray ng mataas na Nitrogen fertilizer habang may blossom end rot dahil lalong aagawan ng calcium ang bunga."
                    )
                }
                selectedOptions.contains("blossom_drop") || selectedOptions.contains("excess_nitrogen") -> {
                    DirectCropSolution(
                        title = "Bloom Booster at Pagpigil sa Flower Drop ng Kamatis",
                        tagalogTitle = "Lunas sa Paglaglag ng Bulaklak at Sobrang Dahon",
                        severity = "Mataas na Prayoridad",
                        badgeColor = AmberAlert,
                        directAnswer = "Ang paglaglag ng bulaklak at labis na kapal ng dahon ay nangangahulugang sobra sa Nitrogen (pampalago) at kulang sa Phosphorus/Potassium (pampabulaklak). Kailangang itigil ang urea at mag-apply ng Fermented Fruit Juice (FFJ).",
                        actionSteps = listOf(
                            "Itigil ang anumang pataba na mataas sa Nitrogen (FAA o chemical urea).",
                            "Mag-apply ng Fermented Fruit Juice (FFJ mula sa saging/molasses): 2 kutsara sa 1L tubig drench sa lupa tuwing 7 araw.",
                            "Magbudbod ng 2 kutsarang kahoy na abo sa paligid ng drip line para sa natural na Potassium.",
                            "Iwasan ang pagbuhos ng tubig nang direkta sa mga bukas na bulaklak upang hindi mahugasan ang pollen."
                        ),
                        materialsNeeded = listOf(
                            "Fermented Fruit Juice (FFJ)",
                            "Kahoy na abo (wood ash para sa Potassium)",
                            "Tubig na walang chlorine"
                        ),
                        criticalRule = "Huwag diligan ng malakas na sprinkler ang ibabaw ng halaman habang namumulaklak."
                    )
                }
                selectedOptions.contains("pale_nitrogen") -> {
                    DirectCropSolution(
                        title = "Organikong Nitrogen Drench para sa Naninilaw na Kamatis",
                        tagalogTitle = "Pagpapasigla sa Mapuputlang Dahon at Mabagal na Pagtubo",
                        severity = "Kailangan ng Sustansya",
                        badgeColor = LushGreen,
                        directAnswer = "Ang pamumutla at paninilaw ng buong halaman ay malinaw na kakulangan sa Nitrogen (Chlorosis). Ang pinakamabilis na organikong paraan ay drench ng Vermitea o Fermented Plant Juice (FPJ).",
                        actionSteps = listOf(
                            "Maghalo ng 2 kutsarang Fermented Plant Juice (FPJ) o Vermitea sa 1 litro ng tubig.",
                            "Idilig sa paligid ng ugat sa umaga tuwing 5 araw hanggang maging malalim na berde ang dahon.",
                            "Maglagay ng 2 dakot ng matandang vermicompost sa paligid ng puno.",
                            "Tiyaking hindi labis ang dilig dahil hinuhugasan nito ang natitirang nitrogen sa lupa."
                        ),
                        materialsNeeded = listOf(
                            "Fermented Plant Juice (FPJ) o Vermicompost tea",
                            "Vermicompost o decomposed compost",
                            "Regadera"
                        ),
                        criticalRule = "Huwag gumamit ng sariwang dumi ng manok o baka na hindi pa bulok dahil susunugin nito ang mga ugat."
                    )
                }
                selectedOptions.contains("potassium_slow") -> {
                    DirectCropSolution(
                        title = "Organikong Potassium para sa Pagpula ng Kamatis",
                        tagalogTitle = "Solusyon sa Mabagal na Paghinog at Matamlay na Bunga",
                        severity = "Kailangan ng Sustansya (Potassium)",
                        badgeColor = AmberAlert,
                        directAnswer = "Ang mabagal na pamumula at kawalan ng tamis sa bunga ng kamatis ay senyales ng kakulangan sa Potassium (K). Mag-drench ng Fermented Fruit Juice (FFJ mula sa hinog na saging) at magbudbod ng pinong kahoy na abo sa paligid ng puno.",
                        actionSteps = listOf(
                            "Maghalo ng 2 kutsarang Fermented Fruit Juice (FFJ) sa 1 litro ng tubig na walang chlorine.",
                            "Idilig sa paligid ng ugat sa umaga tuwing 5 araw hanggang sa mapansin ang mabilis na pamumula ng mga bunga.",
                            "Magbudbod ng 1 hanggang 2 kutsarang pinong kahoy na abo sa paligid ng drip line.",
                            "Itigil ang anumang pataba na mataas sa Nitrogen upang hindi lumago ang mga bagong dahon sa halip na mahinog ang bunga."
                        ),
                        materialsNeeded = listOf(
                            "Fermented Fruit Juice (FFJ mula sa saging o papaya)",
                            "Pinong kahoy na abo (wood ash)",
                            "Regadera o sprayer"
                        ),
                        criticalRule = "Huwag lumampas sa 2 kutsarang abo bawat metro kuwadrado upang hindi labis na tumaas ang pH ng lupa."
                    )
                }
                else -> {
                    DirectCropSolution(
                        title = "Balanseng Organikong Pataba para sa Kamatis",
                        tagalogTitle = "Pangkalahatang Sustansya at Pagpapataba",
                        severity = "Pangangalaga",
                        badgeColor = LushGreen,
                        directAnswer = "Para sa pangkalahatang kalusugan ng kamatis, maglagay ng hinog na vermicompost at regular na mag-drench ng diluted vermitea o compost tea tuwing 7 araw.",
                        actionSteps = listOf(
                            "Maglagay ng 1 dakot ng vermicompost sa paligid ng puno (drip line).",
                            "Diligan pagkatapos maglagay ng abono upang mahigop ng ugat ang sustansya.",
                            "Panatilihing may 3–5cm na mulch ng tuyong dayami upang hindi matangay ng ulan ang pataba."
                        ),
                        materialsNeeded = listOf("Vermicompost", "Compost tea", "Dayami"),
                        criticalRule = "Huwag kailanman maglagay ng sariwang dumi ng hayop nang hindi pa nabulok."
                    )
                }
            }
        }
        "pests" -> {
            when {
                selectedOptions.contains("fruit_borer") -> {
                    DirectCropSolution(
                        title = "Organikong Solusyon Laban sa Uod ng Bunga ng Kamatis (Fruit Borer)",
                        tagalogTitle = "Pamuksa sa Tomato Fruit Borer (Helicoverpa)",
                        severity = "Agad na Gawin (Immediate Pest Action)",
                        badgeColor = RedAlert,
                        directAnswer = "Ang Tomato Fruit Borer (Helicoverpa armigera) ay nanganganak sa bulaklak at sumusuot ang uod sa loob ng bunga. Walang kemikal na makakapasok sa loob kapag nakabaon na ang uod, kaya kailangan ng mekanikal na pagtatanggal at chili-garlic-neem spray.",
                        actionSteps = listOf(
                            "Agad pitasin at ibaon sa lupa (nang 30cm lalim) ang lahat ng apektadong bunga na may butas upang hindi makapag-pupa ang uod.",
                            "Maghanda ng Chili-Garlic Spray: 10 pirasong siling labuyo + 1 buong ulo ng bawang + 1 kutsaritang sabon at 1 kutsaritang neem oil sa 1L tubig. I-spray sa buong halaman tuwing alas-4:30 ng hapon.",
                            "Magkabit ng yellow sticky traps at pheromone traps sa taas ng taniman upang mahuli ang mga gamugamo bago makapangitlog.",
                            "Ulitin ang pag-spray tuwing 4 na araw habang namumulaklak at nagbubunga."
                        ),
                        materialsNeeded = listOf(
                            "Siling labuyo at bawang",
                            "Neem oil o mild dishwashing liquid",
                            "Sprayer"
                        ),
                        criticalRule = "Huwag itapon ang mga inuod na bunga sa ibabaw ng compost heap dahil magiging paruparo muli ang mga ito at babalik sa tanim."
                    )
                }
                selectedOptions.contains("aphids_whitefly") || selectedOptions.contains("leaf_curling") -> {
                    DirectCropSolution(
                        title = "Neem Oil at Soap Drench Laban sa Whiteflies at Kulot sa Kamatis",
                        tagalogTitle = "Solusyon sa Sap-Sucking Pests at Kulot na Dahon (TYLCV)",
                        severity = "Mataas na Prayoridad",
                        badgeColor = AmberAlert,
                        directAnswer = "Ang aphids at whiteflies ay sumisipsip sa ilalim ng dahon at nagpapakalat ng Tomato Yellow Leaf Curl Virus (TYLCV). Ang sabon at neem oil ay sumisira sa protective wax coat ng mga insekto at pumapatay sa kanila.",
                        actionSteps = listOf(
                            "Maghalo ng 1 kutsarang Neem Oil at 1/2 kutsaritang liquid dish soap sa 1 litro ng maligamgam na tubig.",
                            "I-spray nang masinsin sa ILALIM ng mga dahon kung saan nagtatago ang mga peste, tuwing alas-4:30 ng hapon.",
                            "Magkabit ng Yellow Sticky Traps sa taas ng tanim upang mahuli ang mga lumilipad na whiteflies.",
                            "Ulitin ang spray tuwing 3 araw sa loob ng 2 linggo hanggang maubos ang peste."
                        ),
                        materialsNeeded = listOf(
                            "Neem oil (cold-pressed)",
                            "Mild liquid soap (emulsifier)",
                            "Yellow sticky insect traps",
                            "Sprayer na may fine mist"
                        ),
                        criticalRule = "Huwag mag-spray ng neem oil sa tirik ng araw dahil mapapaso ang mga dahon ng tanim."
                    )
                }
                selectedOptions.contains("leaf_miner") -> {
                    DirectCropSolution(
                        title = "Organikong Solusyon Laban sa Leaf Miner ng Kamatis",
                        tagalogTitle = "Pagpuksa sa Uod sa Loob ng Dahon (Leaf Miner)",
                        severity = "Mataas na Prayoridad",
                        badgeColor = AmberAlert,
                        directAnswer = "Ang mga mapuputing parang kalyeng linya ay dulot ng uod ng Leaf Miner na kumakain sa loob ng epidermis ng dahon. Ang pinakamabisang lunas ay paggupit sa apektadong dahon at neem oil spray.",
                        actionSteps = listOf(
                            "Pitasin at agad ibaon o sunugin ang mga dahong may matitinding linya kung saan nakikita ang uod.",
                            "Mag-spray ng Neem Oil (1 kutsara + 1/2 kutsaritang sabon sa 1L tubig) sa buong halaman tuwing alas-4:30 ng hapon.",
                            "Maglagay ng dilaw na sticky traps sa taas ng taniman upang mahuli ang mga matatandang lumilipad na langaw.",
                            "Ulitin ang neem spray tuwing 4 na araw sa loob ng 2 linggo."
                        ),
                        materialsNeeded = listOf("Cold-pressed neem oil", "Mild liquid soap", "Yellow sticky traps", "Sprayer"),
                        criticalRule = "Huwag itapon ang mga pinitas na dahon sa compost pile kung buhay pa ang uod sa loob."
                    )
                }
                else -> {
                    DirectCropSolution(
                        title = "Pangkalahatang Organikong Pest Barrier para sa Kamatis",
                        tagalogTitle = "Proteksyon Laban sa Butas-butas na Dahon",
                        severity = "Pangangalaga",
                        badgeColor = LushGreen,
                        directAnswer = "Para sa mga kumakain ng dahon tulad ng flea beetle at caterpillars, mag-spray ng fermented plant extracts at magbudbod ng wood ash sa ibabaw ng dahon.",
                        actionSteps = listOf(
                            "I-spray ang chili-garlic extract tuwing 4 na araw sa hapon.",
                            "Magbudbod ng manipis na kahoy na abo sa mga dahon kapag may hamog sa umaga.",
                            "Manwal na pulutin ang nakikitang malalaking uod gamit ang sipit."
                        ),
                        materialsNeeded = listOf("Kahoy na abo", "Chili spray", "Sipit"),
                        criticalRule = "Huwag magbudbod ng labis na abo sa basang-basa na dahon upang hindi magbara ang pores."
                    )
                }
            }
        }
        "disease" -> {
            when {
                selectedOptions.contains("bacterial_wilt") -> {
                    DirectCropSolution(
                        title = "Emergency Quarantine Laban sa Bacterial Wilt ng Kamatis",
                        tagalogTitle = "Kritikal: Biglaang Pagkalanta ng Buong Puno",
                        severity = "Kritikal (Bacterial Wilt)",
                        badgeColor = RedAlert,
                        directAnswer = "Ang Bacterial Wilt (Ralstonia solanacearum) ay soil-borne na bacteria na bumabara sa vascular water system ng halaman. Walang kemikal na lunas kapag apektado na ang puno. Kailangan itong bunutin agad upang hindi kumalat sa buong bed.",
                        actionSteps = listOf(
                            "Agad bunutin ang apektadong kamatis kasama ang buong ugat at lupang nakadikit dito.",
                            "Ibalot sa plastic at sunugin o itapon sa malayo. HUWAG isama sa compost!",
                            "Ibuhos ang kumukulong tubig o lagyan ng agricultural lime (apog) ang hukay kung saan binunot ang tanim upang mamatay ang bacteria.",
                            "Huwag itaniman ng kamatis, talong, o sili ang parehong hukay sa susunod na crop rotation (magtanim ng mais o pechay sa halip)."
                        ),
                        materialsNeeded = listOf(
                            "Plastic bag para sa quarantine",
                            "Agricultural lime (apog) o kumukulong tubig",
                            "Pala na disinfected gamit ang alcohol"
                        ),
                        criticalRule = "I-sanitize ang gunting o kamay gamit ang 70% alcohol bago humawak ng ibang malulusog na halaman."
                    )
                }
                selectedOptions.contains("early_blight") || selectedOptions.contains("powdery_mildew") -> {
                    DirectCropSolution(
                        title = "Baking Soda at Milk Fungicide Spray para sa Kamatis",
                        tagalogTitle = "Lunas sa Leaf Spots, Early Blight, at Powdery Mildew",
                        severity = "Mataas na Prayoridad",
                        badgeColor = AmberAlert,
                        directAnswer = "Ang Early Blight (Alternaria solani) at powdery mildew ay fungal spores na nabubuhay sa basang dahon. Ang baking soda ay nagpapataas ng pH sa ibabaw ng dahon na pumapatay sa fungi.",
                        actionSteps = listOf(
                            "Gupitin at sunugin ang lahat ng apektadong dahon na may concentric rings. Huwag iwanan sa ibabaw ng lupa.",
                            "Maghalo ng 1 kutsaritang Baking Soda + 1/2 kutsaritang sabon sa 1L tubig. I-spray sa buong halaman tuwing 5 araw.",
                            "Bilang alternatibo sa amag: Paghaluin ang 1 basong gatas sa 9 na basong tubig (1:9) at i-spray sa maaraw na umaga.",
                            "Siguraduhing sa ugat lang nagdidilig at hindi nababasa ang mga dahon."
                        ),
                        materialsNeeded = listOf("Baking soda (Sodium bicarbonate)", "Mild liquid soap", "Gatas", "Sprayer"),
                        criticalRule = "Huwag lumampas sa 1 kutsaritang baking soda bawat litro upang hindi masunog ang talbos ng halaman."
                    )
                }
                selectedOptions.contains("mosaic_virus") -> {
                    DirectCropSolution(
                        title = "Quarantine at Vector Control Laban sa Mosaic Virus ng Kamatis",
                        tagalogTitle = "Solusyon sa Kulubot at Batik-batik na Dahon (Viral)",
                        severity = "Kritikal (Viral Infection)",
                        badgeColor = RedAlert,
                        directAnswer = "Ang Mosaic Virus ay walang direktang kemikal na gamot kapag nakapasok na sa halaman. Dinadala ito ng mga aphids at whiteflies. Kailangang ibukod o bunutin ang apektadong puno at puksain ang mga insekto.",
                        actionSteps = listOf(
                            "Kung 1 o 2 puno pa lamang ang apektado, bunutin agad at ibaon sa malayo o sunugin. HUWAG i-compost.",
                            "Agad mag-spray ng Neem Oil at sabon (1 kutsarang neem + 1/2 kutsaritang sabon sa 1L tubig) upang ubusin ang aphids at whiteflies.",
                            "Magkabit ng yellow sticky traps sa bawat kama upang mahuli ang mga lumilipad na insekto.",
                            "I-sanitize ang mga kamay at kagamitan gamit ang 70% alcohol bago humawak sa malulusog na pananim."
                        ),
                        materialsNeeded = listOf("Neem oil", "Dilaw na sticky traps", "70% rubbing alcohol", "Plastic bag"),
                        criticalRule = "Huwag gumamit ng tabako o humawak ng sigarilyo bago magtrabaho sa taniman dahil ang Tobacco Mosaic Virus (TMV) ay madaling maisalin sa kamatis gamit ang kamay."
                    )
                }
                else -> {
                    DirectCropSolution(
                        title = "Organikong Proteksyon Laban sa Amag at Sakit ng Kamatis",
                        tagalogTitle = "Pagpapanatili ng Malinis at Ligtas na Taniman",
                        severity = "Pangangalaga sa Dahon",
                        badgeColor = LushGreen,
                        directAnswer = "Upang maiwasan ang anumang fungal o bacterial foliar disease sa kamatis, panatilihing tuyo ang mga dahon at mag-spray ng mild baking soda solution tuwing umaga.",
                        actionSteps = listOf(
                            "Diligan lamang ang base sa ugat at iwasang mabasa ang mga dahon.",
                            "Gupitin ang mga lumang dahon sa ibabang 20cm upang magkaroon ng magandang sirkulasyon ng hangin.",
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
                selectedOptions.contains("bottom_leaves") -> {
                    DirectCropSolution(
                        title = "Pagbabawas ng Dahon sa Ibaba (Bottom 30cm) ng Kamatis",
                        tagalogTitle = "Pagpapabuti ng Sirkulasyon at Pag-iwas sa Splash Blight",
                        severity = "Pangangalaga (Pruning)",
                        badgeColor = LushGreen,
                        directAnswer = "Ang mga dahon na sumasayad sa lupa sa unang 30cm ng puno ay laging basa at madaling dapuan ng soil-borne fungi kapag natalsikan ng ulan o dilig. Ang pag-aahit sa ibaba ay nagpapanatili ng malinis at tuyong puno.",
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
                        title = "Wastong Pruning at Pagtatanggal ng Suwi para sa Kamatis",
                        tagalogTitle = "Pagpapalaki ng Bunga at Pagpapabuti ng Sirkulasyon",
                        severity = "Pangangalaga (Pruning)",
                        badgeColor = LushGreen,
                        directAnswer = "Ang pruning sa kamatis ay nag-aalis ng mga suwi (suckers) na kumakain ng sustansya nang hindi nagbubunga, at nagtatanggal ng mga lumang dahon sa ibaba na madaling kapitan ng sakit mula sa lupa.",
                        actionSteps = listOf(
                            "Tingnan ang 'kili-kili' sa pagitan ng pangunahing tangkay at sanga. Tanggalin ang mga suwi habang maliliit pa (2–4 pulgada) gamit ang mga daliri.",
                            "Mag-iwan lamang ng 1 o 2 pangunahing tangkay (main stems) upang lumaki nang husto ang mga bunga.",
                            "Gupitin ang lahat ng dahon na nasa unang 30cm mula sa lupa gamit ang disinfected shears.",
                            "Gawin ang pruning sa umaga kapag maaraw upang mabilis matuyo ang sugat ng halaman."
                        ),
                        materialsNeeded = listOf(
                            "Pruning shears o matalas na gunting",
                            "70% alcohol para sa pag-sanitize ng gamit"
                        ),
                        criticalRule = "Huwag mag-prune kapag maulan o basa ang dahon dahil madaling makapasok ang bacterial infection sa basang sugat."
                    )
                }
            }
        }
        "harvest" -> {
            DirectCropSolution(
                title = "Tamang Yugto at Pamamaraan ng Pag-aani ng Kamatis",
                tagalogTitle = "Gabay sa Masaganang Pitas at Pagpapanatili ng Kasariwaan",
                severity = "Pag-aani (Harvest Time)",
                badgeColor = LushGreen,
                directAnswer = "Ang tamang pag-aani ng kamatis sa breaker stage (may bahagyang kulay rosas sa puwit) ay nagbibigay ng pinakamatagal na shelf-life at nagpapanatili ng tamis at tibay ng balat habang ibinibiyahe.",
                actionSteps = listOf(
                    "Pitasin ang kamatis sa umaga (6:00 AM–8:00 AM) bago uminit ang araw habang puno pa ng tubig ang mga selula ng gulay.",
                    "Pitasin sa 'breaker stage' kung ibibiyahe sa malayo, o 'pink to light red' kung gagamitin agad sa kusina.",
                    "Gumamit ng matalas na gunting o pruner at mag-iwan ng 1 pulgadang tangkay kasama ang berdeng calyx.",
                    "Huwag hilahin nang sapilitan ang bunga dahil mababali ang mga katabing bulaklak at sanga.",
                    "Ilagay sa malinis at may bentilasyong lalagyan sa malamig na lugar."
                ),
                materialsNeeded = listOf(
                    "Matalas na gunting o pruning shears",
                    "Woven basket o breathable crate",
                    "Malinis na tela"
                ),
                criticalRule = "Huwag ibilad sa direktang sikat ng araw ang mga bagong aning kamatis pagkatapos pitasin."
            )
        }
        else -> null
    }
}
