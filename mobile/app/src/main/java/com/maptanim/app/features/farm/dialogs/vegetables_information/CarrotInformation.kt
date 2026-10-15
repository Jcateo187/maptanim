package com.maptanim.app.features.farm.dialogs.vegetables_information

import androidx.compose.ui.graphics.Color
import com.maptanim.app.R

val carrotData = CropEncyclopedia(
    cropName = "Carrot",
    localName = "Karot (Daucus carota subsp. sativus)",
    scientificName = "Daucus carota subsp. sativus",
    category = "Root Vegetables (Apiaceae)",
    family = "Apiaceae",
    overview = "Ang karot (Daucus carota subsp. sativus) ay isa sa pinakatanyag at pinakamataas ang halagang root crop sa Pilipinas, pangunahing itinatanim sa malamig na kabundukan ng Cordillera Administrative Region (Benguet: La Trinidad, Atok, Buguias, Mankayan; Mountain Province) at sa talampas ng Bukidnon. Kilala bilang biennial plant na itinatanim bilang annual para sa matamis, malutong, at makinis na matingkad na kahel na taproot nito na mayaman sa beta-carotene.\n\nNangangailangan ang karot ng malalim (25–30 cm), buhaghag, at walang batong mabuhanging lupa (sandy loam) upang malayang makabaon nang tuwid ang taproot. Kapag tumama ang lumalaking ugat sa bato, matigas na tipak ng lupa, o hindi pa nabubulok na sariwang dumi ng hayop, ito ay nababali o nagsasanga (forking), na lubhang nagpapababa sa presyo nito sa merkado.\n\nBukod dito, ang karot ay strictly direct-seeded—hindi ito inililipat-tanim (transplanting) dahil ang pagkasira ng sensitibong dulo ng mikro-ugat ng punla ay agad na nagdudulot ng pilipit at deformed na ugat. Nangangailangan din ito ng maingat na 'thinning' (paghihimay) sa 5–8 cm agwat at 'earthing-up' upang matakpan ang balikat ng ugat laban sa pamumula o pagberde (green shoulder) dulot ng sikat ng araw.",
    selectedVariety = VarietyInfo(
        name = "New Kuroda",
        producer = "East-West Seed Philippines",
        type = "Tropical Open-Pollinated Root Crop (OPV)",
        badge = "OPV",
        description = "Ang pambansang standard na barayti ng karot sa Pilipinas. May pambihirang tibay sa init at resistensya laban sa Alternaria leaf blight kumpara sa mga tradisyunal na temperate varieties. Nagbubunga ng makikinis, matatamis, at makakapal na silindrikong ugat (15–20 cm ang haba, 120–180 gramo) na may matingkad na kulay kahel mula sa balat hanggang sa pinakagitnang core, kaya't paborito ng mga mamimili at magsasaka."
    ),
    otherVarieties = listOf(
        "New Kuroda (East-West Seed Standard)",
        "Terracotta F1 (High-Yield Hybrid)",
        "Chantenay Red Cored (Conical for heavier soils)",
        "Kuroda Improved (Heavy Shoulder Standard)",
        "Danvers 126 (Deep Tapering Root)",
        "Nantes Fancy (Cylindrical Sweet Carrot)"
    ),
    carouselSlides = listOf(
        CarouselSlide(
            title = "Matingkad na Kahel na Karot sa Benguet",
            subtitle = "Sariwang ani sa matabang volcanic sandy loam ng Atok at La Trinidad",
            stage = "Matipid sa Ani (Market Ready)",
            badgeColor = Color(0xFFE65100),
            drawableResId = R.drawable.ph_carrot_hero
        ),
        CarouselSlide(
            title = "Pagsibol ng mga Pinong Punla",
            subtitle = "10–21 araw na mabagal na pagsibol sa malinis at mamasa-masang kamada",
            stage = "Yugto ng Pagsibol (Emergence Phase)",
            badgeColor = Color(0xFF1565C0),
            drawableResId = R.drawable.ph_carrot_seedlings
        ),
        CarouselSlide(
            title = "Paglaki ng Ugat sa Raised Bed",
            subtitle = "Aktibong root bulking sa ilalim ng buhaghag at walang batong lupa",
            stage = "Root Bulking (Paglaki ng Taproot)",
            badgeColor = Color(0xFF2E7D32),
            drawableResId = R.drawable.ph_carrot_bulking
        ),
        CarouselSlide(
            title = "Masaganang Pagbunot at Paghuhugas",
            subtitle = "Maingat na pag-ani gamit ang garden fork at paghuhugas bago i-crate",
            stage = "Pang-umagang Pag-aani (Harvest)",
            badgeColor = Color(0xFF6A1B9A),
            drawableResId = R.drawable.ph_carrot_harvest
        )
    ),
    quickInfo = listOf(
        QuickInfoItem(
            key = "lifespan",
            label = "Buhay ng Halaman",
            value = "90–120 Araw",
            explanation = ExplanationData(
                title = "Buhay ng Halaman (Crop Lifecycle)",
                summary = "Ang karot ay may 90 hanggang 120 araw na kabuuang siklo mula paghasik hanggang anihan.",
                details = listOf(
                    "Pagsibol (Germination): 10–21 araw (mabagal kumpara sa ibang gulay).",
                    "Paghihimay (Thinning): 25–35 araw (kapag 5 cm na ang taas).",
                    "Root Bulking: 40–90 araw para sa paghaba at pagtaba ng taproot.",
                    "Anihan: 90–110 araw kapag umabot na sa 2–4 cm ang balikat ng ugat."
                )
            )
        ),
        QuickInfoItem(
            key = "harvest_time",
            label = "Unang Ani",
            value = "90–100 DAS",
            explanation = ExplanationData(
                title = "Panahon ng Unang Ani",
                summary = "90 hanggang 100 araw pagkatapos ihasik ang binhi (Days After Sowing).",
                details = listOf(
                    "Handa na kapag ang balikat ng ugat na nakalitaw sa lupa ay may 2–4 cm diameter.",
                    "Single harvest per crop: binubunot ang buong ugat kasama ang dahon.",
                    "Inaani sa malamig na oras ng umaga upang maiwasan ang panlalata."
                )
            )
        ),
        QuickInfoItem(
            key = "spacing",
            label = "Agwat ng Tanim",
            value = "5–8 cm (Hanay 20–30 cm)",
            explanation = ExplanationData(
                title = "Tamang Agwat at Paghihimay",
                summary = "20–30 cm ang pagitan ng bawat hanay; hinihimay (thinned) sa 5–8 cm pagitan ng bawat puno.",
                details = listOf(
                    "Kritikal ang thinning: kapag masyadong masinsin, magiging kasing-payat lamang ng lapis ang mga karot.",
                    "Nagbibigay ng 400,000 hanggang 600,000 halaman bawat ektarya sa raised beds."
                )
            )
        ),
        QuickInfoItem(
            key = "sowing_depth",
            label = "Lalim ng Binhi",
            value = "0.5–1.0 cm (Mababaw)",
            explanation = ExplanationData(
                title = "Lalim ng Paghahasik",
                summary = "Mababaw lamang na kalahati hanggang isang sentimetro sa mabuhanging kamada.",
                details = listOf(
                    "Napakaliit ng binhi ng karot; kapag ibinaon nang malalim sa 1.5 cm, hindi ito makakaahon sa lupa.",
                    "Takpan ng pinong buhangin o compost at diligan gamit ang pinong sprinkler (mist)."
                )
            )
        ),
        QuickInfoItem(
            key = "water_needs",
            label = "Pangangailangan sa Tubig",
            value = "Katamtaman at Pantay (2–3x/wk)",
            explanation = ExplanationData(
                title = "Patubig at Kahalumigmig",
                summary = "Kailangan ang tuloy-tuloy at pantay na kahalumigmig sa buong yugto ng paglaki.",
                details = listOf(
                    "Kritikal sa unang 2 linggo upang hindi matuyo ang sumisibol na binhi.",
                    "Iwasan ang pagkatigang na sinusundan ng biglaang baha dahil nagdudulot ito ng root splitting (pagbibitak ng ugat).",
                    "Huwag ibabad sa tubig upang hindi tamaan ng root rot."
                )
            )
        ),
        QuickInfoItem(
            key = "sun_needs",
            label = "Sikat ng Araw",
            value = "Buong Araw (Full Sun 6h+)",
            explanation = ExplanationData(
                title = "Pangangailangan sa Araw",
                summary = "Nangangailangan ng buong sikat ng araw upang makagawa ng sapat na carbohydrates para sa ugat.",
                details = listOf(
                    "Ang 6 o higit pang oras ng direktang araw ay nagpapalago sa feathery foliage.",
                    "Kailangan ang light penetration kasama ang malamig na hangin sa kabundukan."
                )
            )
        ),
        QuickInfoItem(
            key = "soil_ph",
            label = "Asim ng Lupa (pH)",
            value = "pH 6.0–6.8 (Buhaghag)",
            infoNote = "Iwasan ang mabato at siksik na lupa",
            isAlert = true,
            explanation = ExplanationData(
                title = "Tekstura ng Lupa at pH",
                summary = "Malalim, buhaghag, at walang batong sandy loam na may pH 6.0 hanggang 6.8.",
                details = listOf(
                    "Nangangailangan ng 25–30 cm deep tillage.",
                    "Bawal ang matitigas na clods o bato dahil nagiging sanhi ito ng crooked at forked roots.",
                    "Bawal ang sariwang dumi ng hayop dahil sinusunog nito ang taproot at nagpapasanga sa ugat."
                )
            )
        ),
        QuickInfoItem(
            key = "ideal_temp",
            label = "Tamang Temperatura",
            value = "16–21°C (Optimum Highland)",
            explanation = ExplanationData(
                title = "Temperatura at Agro-Climatic Zone",
                summary = "Malamig na klima para sa pinakamatamis na lasa at matingkad na kulay kahel.",
                details = listOf(
                    "Optimum: 16–21°C; minimum: 10°C; maximum: 27°C.",
                    "Kapag higit 25°C sa kapatagan, nagiging maputla ang laman, makahoy ang texture, at matabang ang lasa.",
                    "Sa Benguet at Bukidnon, naipupundar ang pinakamataas na kalidad ng beta-carotene."
                )
            )
        ),
        QuickInfoItem(
            key = "difficulty",
            label = "Antas ng Alaga",
            value = "Katamtaman (Needs Patience)",
            explanation = ExplanationData(
                title = "Antas ng Kahirapan sa Pagtatanim",
                summary = "Nangangailangan ng pasensya sa mabagal na pagsibol at disiplina sa paghihimay (thinning).",
                details = listOf(
                    "Mabagal mag-umpisa ang binhi ngunit madaling alagaan kapag naitatag na ang mga punla.",
                    "Kritikal ang pagtatanggal ng damo sa unang buwan dahil madaling matabunan ang maliliit na dahon."
                )
            )
        ),
        QuickInfoItem(
            key = "feeding",
            label = "Pangangailangan sa Abono",
            value = "Mababa sa N, Mataas sa P & K",
            explanation = ExplanationData(
                title = "Programa sa Pagpapataba ng Karot",
                summary = "Limitahan ang Nitrogen; palakasin ang Phosphorus, Potassium, at Boron.",
                details = listOf(
                    "Basal: 250–350 kg/ha ng 14-14-14 + well-decomposed compost.",
                    "Iwasan ang labis na Urea: nagdudulot ng mabuhok at sanga-sangang ugat sa halip na tumaba.",
                    "Side-dress: Muriate of Potash (0-0-60) sa ika-5 linggo para sa kulay at tamis.",
                    "Foliar Boron laban sa black heart at hollow core."
                )
            )
        ),
        QuickInfoItem(
            key = "trellising",
            label = "Tulos / Balag",
            value = "Hindi Kailangan (Root Crop)",
            explanation = ExplanationData(
                title = "Suporta sa Halaman",
                summary = "Mababang root crop na may rosette foliage.",
                details = listOf(
                    "Hindi nangangailangan ng tulos.",
                    "Isinasagawa ang 'earthing-up' (pagtatambak ng 2–3 cm ng lupa) sa balikat ng karot upang maiwasan ang sun-greening (green shoulders)."
                )
            )
        ),
        QuickInfoItem(
            key = "pest_pressure",
            label = "Banta ng Peste",
            value = "Katamtaman (Leafminer & Nematodes)",
            explanation = ExplanationData(
                title = "Presyur ng Peste at Nematodes",
                summary = "Bantayan ang Root-knot nematodes sa lupa at leafminers sa dahon.",
                details = listOf(
                    "Nematodes: nagdudulot ng bukol-bukol at bansot na ugat; sugpuin gamit ang marigold intercropping at CRH.",
                    "Leafminer & Carrot fly: nag-iiwan ng rusty tunnels sa ugat; sugpuin gamit ang fine netting at neem spray."
                )
            )
        )
    ),
    companionPlants = listOf(
        PlantRelationItem(
            name = "Sibuyas at Bawang (Allium spp.)",
            scientificName = "Allium cepa / sativum",
            isBeneficial = true,
            role = "Natural Repellent Laban sa Carrot Fly",
            explanation = ExplanationData(
                title = "Sibuyas at Bawang bilang Companion",
                summary = "Ang matapang na amoy ng allicin ay nagtatakip sa simoy ng karot upang hindi maamoy ng carrot rust fly.",
                details = listOf(
                    "Itanim nang salitan sa pagitan ng mga hanay ng karot.",
                    "Nagbibigay din ng magkatuwang na proteksyon laban sa aphids."
                )
            )
        ),
        PlantRelationItem(
            name = "Amarillo / Marigold (Tagetes erecta)",
            scientificName = "Tagetes erecta",
            isBeneficial = true,
            role = "Bio-Nematicide sa Ilalim ng Lupa",
            explanation = ExplanationData(
                title = "Marigold laban sa Root-Knot Nematodes",
                summary = "Ang ugat ng marigold ay naglalabas ng alpha-terthienyl na pumapatay sa mga nematodes sa lupa.",
                details = listOf(
                    "Itanim sa paligid o bilang cover crop bago maghasik ng karot.",
                    "Pinoprotektahan ang taproot laban sa pamumukol at pagsasanga."
                )
            )
        ),
        PlantRelationItem(
            name = "Lettuce / Letsugas (Lactuca sativa)",
            scientificName = "Lactuca sativa",
            isBeneficial = true,
            role = "Mababaw na Kasama sa Kamada",
            explanation = ExplanationData(
                title = "Letsugas at Karot",
                summary = "Perpektong intercrop dahil mababaw ang ugat ng letsugas habang malalim ang karot.",
                details = listOf(
                    "Nagsisilbing buhay na mulch ang malalapad na dahon ng letsugas habang sumisibol ang karot.",
                    "Naaani ang letsugas sa loob ng 35–45 araw bago pa lumaki nang husto ang ugat ng karot."
                )
            )
        ),
        PlantRelationItem(
            name = "Dill (Anethum graveolens)",
            scientificName = "Anethum graveolens",
            isBeneficial = false,
            role = "Kaparehong Pamilya / Peste Attractor",
            explanation = ExplanationData(
                title = "Bakit Dapat Iwasan ang Dill Katabi ng Karot",
                summary = "Kapareho sa pamilyang Apiaceae na umaakit ng parehong mga peste at maaaring magpabagal sa paglaki ng ugat.",
                details = listOf(
                    "Huwag itanim sa mismong kamada ng karot.",
                    "Maaaring makaapekto sa matamis na lasa ng taproot."
                )
            )
        ),
        PlantRelationItem(
            name = "Labanos / Radish (Raphanus sativus)",
            scientificName = "Raphanus sativus",
            isBeneficial = false,
            role = "Magkatunggali sa Root Zone",
            explanation = ExplanationData(
                title = "Iwasan ang Sunod-sunod na Root Crops",
                summary = "Nag-aagawan sa espasyo at nagpaparami ng mga soil-borne root pests.",
                details = listOf(
                    "Mabilis lumaki ang labanos at sinasapawan ang mabagal sumibol na karot.",
                    "Huwag itanim nang magkasunod sa iisang kamada."
                )
            )
        )
    ),
    nutritionBadges = listOf(
        NutritionBadgeItem(
            code = "Vit A",
            name = "Beta-Carotene (Bitamina A)",
            percentageDaily = "104% DV",
            role = "Eye Vision & Immunity",
            explanation = ExplanationData(
                title = "Nangungunang Pinagkukunan ng Beta-Carotene",
                summary = "Naglalaman ng 8,285 mcg ng Beta-Carotene bawat 100 gramo.",
                details = listOf(
                    "Pinapanatili ang matalas na paningin sa dilim (rhodopsin production).",
                    "Nagpoprotekta sa cornea at nagpapalakas ng mucous membrane immunity laban sa impeksyon.",
                    "Makapangyarihang antioxidant para sa kalusugan ng balat."
                )
            )
        ),
        NutritionBadgeItem(
            code = "Fiber",
            name = "Dietary Fiber (Pectin)",
            percentageDaily = "11% DV",
            role = "Cholesterol & Healthy Digestion",
            explanation = ExplanationData(
                title = "Mayaman sa Soluble Fiber (Pectin)",
                summary = "Naglalaman ng 2.8 gramo ng de-kalidad na hibla bawat 100 gramo.",
                details = listOf(
                    "Tumutulong sa pagpapababa ng blood cholesterol sa pamamagitan ng paggapos sa bile acids.",
                    "Nagpapabagal sa pagsipsip ng asukal para sa balanced blood glucose."
                )
            )
        ),
        NutritionBadgeItem(
            code = "Vit K1",
            name = "Bitamina K1 (Phylloquinone)",
            percentageDaily = "13% DV",
            role = "Bone Strength & Blood Clotting",
            explanation = ExplanationData(
                title = "Bitamina K1 para sa Buto",
                summary = "Mahalaga para sa protein synthesis na may kinalaman sa pamumuo ng dugo at bone mineralization.",
                details = listOf(
                    "Tumutulong sa pag-iwas sa osteoporosis.",
                    "Nagpapanatili ng malulusog na daluyan ng dugo."
                )
            )
        ),
        NutritionBadgeItem(
            code = "Potassium",
            name = "Potassium (K)",
            percentageDaily = "7% DV",
            role = "Blood Pressure Regulation",
            explanation = ExplanationData(
                title = "Potassium para sa Puso",
                summary = "Naglalaman ng 320 mg ng Potassium bawat 100 gramo.",
                details = listOf(
                    "Tumutulong sa pagbabalanse ng sodium sa katawan at pagpapanatili ng normal na presyon ng dugo.",
                    "Mahalaga para sa normal na function ng mga kalamnan at nerbiyos."
                )
            )
        )
    ),
    pestsAndDiseases = listOf(
        PestDiseaseItem(
            name = "Root-Knot Nematodes",
            scientificName = "Meloidogyne spp.",
            severity = "Kritikal (Severe Root Deformity)",
            symptoms = "Maliliit na bukol sa ugat; bansot, maikli, at pilipit na taproot na may maraming sangang galamay; panlalata sa tanghali.",
            prevention = "Pagtatanim ng Marigold (Amarillo) bilang bio-fumigant; paglalagay ng Carbonized Rice Hull (CRH) at Trichoderma; crop rotation kasama ang mais.",
            treatment = "Walang pamatay-bulate kapag may bukol na ang ugat; bunutin at sirain ang apektadong tanim; solarize ang lupa bago ang susunod na tanim.",
            explanation = ExplanationData(
                title = "Root-Knot Nematodes sa Mabuhanging Lupa",
                summary = "Mikroskopikong bulate na sumisira sa dulo ng taproot habang bata pa.",
                details = listOf(
                    "Mabilis dumami sa magkakasunod na pagtatanim ng solanaceous crops at carrots.",
                    "Laging maghalo ng CRH at compost na mayaman sa beneficial microbes."
                )
            )
        ),
        PestDiseaseItem(
            name = "Alternaria Leaf Blight",
            scientificName = "Alternaria dauci",
            severity = "Mataas (High Foliage Loss)",
            symptoms = "Maiitim at kayumangging batik na may dilaw na gilid sa mga panlabas na dahon; natutuyo at napuputol ang foliage sa anihan.",
            prevention = "Certified clean seeds (tulad ng New Kuroda); iwasan ang sprinkler irrigation sa dapit-hapon; 5–8 cm wastong agwat.",
            treatment = "Preventive Copper Hydroxide o Mancozeb spray tuwing maulang panahon tuwing 7–10 araw.",
            explanation = ExplanationData(
                title = "Alternaria Leaf Blight sa Karot",
                summary = "Fungal disease na umuubos sa dahon na kailangan sa photosynthesis at pagbunot ng ugat.",
                details = listOf(
                    "Kapag mahina ang dahon, hindi mahihila ang karot at maiiwan ang ugat sa ilalim ng lupa.",
                    "Mag-isprey sa maagang yugto bago pa kumalat sa buong kamada."
                )
            )
        ),
        PestDiseaseItem(
            name = "Carrot Rust Fly at Leafminer",
            scientificName = "Psila rosae / Liriomyza spp.",
            severity = "Mataas (Tunnels in Root)",
            symptoms = "Mapuputing guhit sa dahon; kalawanging maiitim na uka at tunnels sa katawan ng ugat na nagdudulot ng pagkabulok.",
            prevention = "Takpan ng nylon insect netting mula pagsibol; intercropping kasama ang sibuyas at bawang; alisin ang pinagputulang dahon.",
            treatment = "Neem oil spray (5ml/L) sa dapit-hapon; alisin ang apektadong dahon.",
            explanation = ExplanationData(
                title = "Carrot Rust Fly Larvae",
                summary = "Maliliit na uod na naghuhukay sa loob ng matamis na ugat ng karot.",
                details = listOf(
                    "Naaakit sa amoy ng nabugbog o pinutol na dahon ng karot.",
                    "Ang intercropping ng sibuyas ang pinakamabisang natural na panlaban."
                )
            )
        ),
        PestDiseaseItem(
            name = "Fungal Root Rot",
            scientificName = "Rhizoctonia solani / Pythium spp. / Sclerotinia",
            severity = "Kritikal (Total Root Loss)",
            symptoms = "Basang-basa, malambot, maitim, at nanginginain na bulok mula sa dulo o balikat ng ugat.",
            prevention = "20–25 cm mataas na raised beds; malalim na drainage; huwag magbabad sa tubig; buhaghag na sandy loam.",
            treatment = "Bunutin at itapon ang bulok na halaman; mag-drench ng Trichoderma sa natitirang malulusog na halaman.",
            explanation = ExplanationData(
                title = "Pagkabulok ng Ugat sa Matubig na Lupa",
                summary = "Soil-borne fungi na sumasalakay sa baradong kamada.",
                details = listOf(
                    "Hindi mabubuhay ang fungi sa well-drained at mahanging lupa.",
                    "Laging siguraduhing mabilis kumalas ang tubig-ulan sa mga kanal."
                )
            )
        )
    ),
    goodAndBadCritters = listOf(
        CritterItem(
            name = "Ladybugs at Lacewings",
            role = "Natural Predators ng Aphids",
            attractsWith = "Feathery carrot foliage and flowering herbs",
            explanation = ExplanationData(
                title = "Ladybirds sa Taniman ng Karot",
                summary = "Kumakain ng daan-daang aphids bawat araw, pinipigilan ang pagkulubot ng mga dahon.",
                details = listOf(
                    "Natural na naninirahan sa malamig na bukid ng Benguet.",
                    "Huwag mag-spray ng malalakas na broad-spectrum chemicals."
                )
            )
        ),
        CritterItem(
            name = "Beneficial Soil Fungi (Trichoderma)",
            role = "Likas na Pananggalang sa Root Rot at Nematodes",
            attractsWith = "Organikong compost at Carbonized Rice Hull",
            explanation = ExplanationData(
                title = "Trichoderma sa Kamada",
                summary = "Bumabalot sa ugat ng karot upang harangan ang masasamang amag sa lupa.",
                details = listOf(
                    "Inihahalo sa lupa bago maghasik ng binhi.",
                    "Nagpapasigla sa paghaba at pagkinis ng taproot."
                )
            )
        ),
        CritterItem(
            name = "Root-Knot Nematodes",
            role = "Mapaminsalang Mikroskopikong Peste sa Ugat",
            attractsWith = "Mababaw at mahinang ugat sa acidic soil",
            explanation = ExplanationData(
                title = "Banta ng Nematodes",
                summary = "Nagdudulot ng bukol at sanga-sangang ugat na hindi maibebenta sa palengke.",
                details = listOf(
                    "Sugpuin gamit ang marigold intercropping at solarization.",
                    "Iwasan ang magkakasunod na pagtatanim ng root crops."
                )
            )
        )
    ),
    seasonalCalendar = listOf(
        SeasonalCalendarMonth("Ene", "Tugatog ng Produksyon (Benguet)", "Malamig na klima; pinakamagandang kulay kahel at pinakamatamis na lasa.", Color(0xFF2E7D32)),
        SeasonalCalendarMonth("Peb", "Pangunahing Anihan", "Saganang ani sa La Trinidad at Atok; mataas ang kalidad sa merkado.", Color(0xFF2E7D32)),
        SeasonalCalendarMonth("Mar", "Pagtatanim sa Tag-araw", "Paghahanda ng kamada na may maayos na patubig (sprinkler o drip).", Color(0xFF1565C0)),
        SeasonalCalendarMonth("Abr", "Patubig at Mulching", "Kailangan ang tuloy-tuloy na patubig upang maiwasan ang root splitting at pagkatigang.", Color(0xFFE65100)),
        SeasonalCalendarMonth("Mayo", "Pagsisimula ng Ulan", "Paggawa ng matataas na raised bed (25 cm) upang maihanda ang lupa sa tag-ulan.", Color(0xFF1565C0)),
        SeasonalCalendarMonth("Hun", "Tag-ulan sa Kabundukan", "Kailangan ang malalim na kanal upang maiwasan ang fungal root rot.", Color(0xFF2E7D32)),
        SeasonalCalendarMonth("Hul", "Panahon ng Habagat", "Pag-aalaga sa drainage canals; pag-iingat laban sa Alternaria leaf blight.", Color(0xFFE65100)),
        SeasonalCalendarMonth("Ago", "Off-Season High Price", "Mataas ang presyo ng karot sa Metro Manila dahil sa banta ng bagyo sa Cordillera.", Color(0xFF2E7D32)),
        SeasonalCalendarMonth("Set", "Paghahanda sa Pagtatanim", "Paghahasik ng binhi para sa pangunahing regular season.", Color(0xFF1565C0)),
        SeasonalCalendarMonth("Okt", "Malawakang Paghahasik", "Malawakang paghahasik sa Benguet at simula ng pagtatanim sa malamig na buwan sa kapatagan.", Color(0xFF2E7D32)),
        SeasonalCalendarMonth("Nob", "Paghihimay (Thinning)", "Yugto ng paghihimay sa 5–8 cm agwat at magaan na hilling-up laban sa green shoulders.", Color(0xFF2E7D32)),
        SeasonalCalendarMonth("Dis", "Saganang Pamilihan", "Mataas na demand para sa Kapaskuhan; matamis at malutong ang mga ugat dulot ng amihan.", Color(0xFF2E7D32))
    ),
    careGuide = listOf(
        HowToSectionItem(
            title = "Paghahanda ng Lupa: Lalim at Pag-aalis ng Bato",
            summary = "Paggawa ng 25–30 cm na malalim at buhaghag na kamada para sa tuwid na ugat.",
            steps = listOf(
                "Araruhin at bungkalin ang lupa nang malalim (25–30 cm) nang 2–3 beses.",
                "Salain o alisin ang lahat ng bato, matitigas na clods, at lumang ugat.",
                "Maghalo ng buhangin (river sand), carbonized rice hull (CRH), at fully decomposed compost.",
                "Gumawa ng raised bed na may taas na 20–25 cm at lapad na 1 metro."
            )
        ),
        HowToSectionItem(
            title = "Kritikal na Paghihimay (Thinning) sa 5–8 cm",
            summary = "Pagbibigay ng sapat na espasyo upang lumapad at tumaba ang taproot.",
            steps = listOf(
                "Magsagawa ng thinning sa ika-3 hanggang ika-4 na linggo pagkasibol kapag may taas nang 5 cm ang mga punla.",
                "Diligan nang bahagya ang kamada bago mag-thin upang lumambot ang lupa.",
                "Bunutin ang mahihinang punla upang mag-iwan ng 5–8 cm na agwat sa pagitan ng bawat halaman.",
                "Iwanan ang pinakamatitikas at tuwid na mga halaman sa hanay."
            )
        ),
        HowToSectionItem(
            title = "Pagtatambak ng Lupa Laban sa Green Shoulders",
            summary = "Pagtatakip sa balikat ng karot upang maiwasan ang pamumula o pagberde dulot ng araw.",
            steps = listOf(
                "Suriin ang ibabaw ng kamada tuwing ika-6 hanggang ika-8 linggo.",
                "Kapag may nakikitang kulay kahel na balikat na lumilitaw, magtambak ng 2–3 cm ng pinong lupa o compost sa paligid.",
                "Takpan ang buong tuktok ng ugat upang manatiling nakabaon sa dilim.",
                "Maglagay ng manipis na dayami upang hindi maanod ng pandilig ang lupa."
            )
        ),
        HowToSectionItem(
            title = "Wastong Patubig sa Yugto ng Root Bulking",
            summary = "Pagpapanatili ng pantay na moisture upang maiwasan ang pagbibitak ng ugat.",
            steps = listOf(
                "Diligan ang taniman 2–3 beses bawat linggo gamit ang banayad na pandilig o drip irrigation.",
                "Huwag patuyuin nang husto ang lupa bago muling magdilig.",
                "Bawasan ang pagdidilig 1–2 linggo bago ang anihan upang tumamis ang laman at hindi pumutok ang balat."
            )
        )
    ),
    growthTimeline = listOf(
        TimelineStageItem(
            stageName = "1. Pagsibol (Germination / Emergence)",
            durationDays = "10–21 Araw",
            description = "Mabagal na pagsibol ng munting binhi; kailangan ang palagiang mamasa-masang ibabaw ng lupa at pinong patubig."
        ),
        TimelineStageItem(
            stageName = "2. Punla at Paghihimay (Thinning Phase)",
            durationDays = "25–35 Araw (DAS)",
            description = "Paglaki ng feathery foliage hanggang 5 cm; pagsasagawa ng kritikal na thinning sa 5–8 cm agwat at unang pagtatanggal ng damo."
        ),
        TimelineStageItem(
            stageName = "3. Paghaba ng Taproot (Root Elongation)",
            durationDays = "35–60 Araw",
            description = "Mabilisang pagbaon ng manipis na taproot pababa sa malalim na buhaghag na lupa; kailangan ang Phosphorus at walang sagabal na bato."
        ),
        TimelineStageItem(
            stageName = "4. Root Bulking (Pagtaba ng Ugat)",
            durationDays = "60–90 Araw",
            description = "Pagtaba ng katawan ng ugat at pag-iipon ng beta-carotene at asukal; paglalagay ng Potash at pagtatambak ng lupa sa balikat laban sa araw."
        ),
        TimelineStageItem(
            stageName = "5. Pag-aani (Harvest Maturity)",
            durationDays = "90–110 Araw (DAS)",
            description = "Matambok na balikat (2–4 cm), matingkad na kahel (15–20 cm haba, 80–150g); maingat na pagbunot gamit ang garden fork at paghuhugas."
        )
    ),
    howTos = listOf(
        HowToSectionItem(
            title = "Paano Maghasik ng Binhi ng Karot (Direct Seeding)",
            summary = "Wastong paghahasik sa mabuhanging kamada upang mataas ang germination rate.",
            steps = listOf(
                "Gumawa ng mababaw na tudling (furrow) na may lalim na 0.5 hanggang 1 cm, may agwat na 20–30 cm sa pagitan ng mga hanay.",
                "Ihasik nang manipis ang binhi (maaaring ihalo sa pinong buhangin upang maging pantay ang pagkalat).",
                "Tabunan ng napakanipis na pinong compost o buhangin at marahang pikpikin ng palad.",
                "Diligan gamit ang pinong mist spray at takpan ng pansamantalang burlap sack hanggang sa sumibol."
            )
        ),
        HowToSectionItem(
            title = "Hakbang-Hakbang na Pag-ani at Paghuhugas",
            summary = "Paraan ng pagbunot nang hindi nababali ang malutong na ugat ng karot.",
            steps = listOf(
                "Diligan nang bahagya ang kamada 1–2 oras bago mag-ani upang lumambot ang lupa.",
                "Itusok ang spading fork 15 cm sa gilid ng hanay at marahang iangat upang lumuwag ang lupa sa ilalim.",
                "Hawakan ang base ng mga dahon at bunutin nang patayo.",
                "Ilubog sa malinis na tubig upang maalis ang lupang bukid at i-grade ayon sa laki at kakinisan."
            )
        ),
        HowToSectionItem(
            title = "Pagkontrol sa Pagsasanga ng Ugat (Forking Prevention)",
            summary = "Mga paraan upang makakuha ng 100% tuwid at makinis na mga karot.",
            steps = listOf(
                "Huwag kailanman maglagay ng sariwang dumi ng hayop sa kamada.",
                "Bungkalin ang lupa sa 25–30 cm lalim at salain ang lahat ng bato at matitigas na clods.",
                "Iwasan ang labis na Urea (Nitrogen) at huwag maglipat-tanim (direct seed lamang).",
                "Panatilihing buhaghag ang lupa gamit ang river sand at carbonized rice hull."
            )
        )
    ),
    faqs = listOf(
        FaqItem(
            question = "Bakit nagsasanga-sanga o nagiging parang octopus ang aking karot?",
            answer = "Ito ay tinatawag na 'forking'. Karaniwan itong sanhi ng tatlong bagay: (1) pagtama ng dulo ng ugat sa bato o matigas na clod ng lupa, (2) pagkasunog ng dulo ng ugat dulot ng sariwang dumi ng hayop, o (3) pinsala mula sa root-knot nematodes. Siguraduhing buhaghag ang lupa sa 25–30 cm lalim at fully decomposed compost lamang ang gamitin."
        ),
        FaqItem(
            question = "Bakit berde at mapait ang itaas na bahagi ng aking karot?",
            answer = "Ito ay 'green shoulder' na dulot ng sikat ng araw kapag nakalitaw ang balikat ng karot sa ibabaw ng lupa. Gumagawa ito ng chlorophyll at mapapait na solanine-like compounds. Maiiwasan ito sa pamamagitan ng 'earthing-up' o pagtatambak ng 2–3 cm ng lupa o mulch sa ibabaw ng mga balikat."
        ),
        FaqItem(
            question = "Bakit naging kasing-payat lamang ng lapis ang aking mga naaning karot?",
            answer = "Ito ay dahil sa kakulangan ng paghihimay (thinning). Kapag masyadong masinsin ang pagkakatubo ng mga halaman, nag-aagawan sila sa espasyo at walang puwang para tumaba ang ugat. Kailangang himayin ang mga punla sa 5–8 cm agwat sa ika-3–4 na linggo pagkasibol."
        ),
        FaqItem(
            question = "Maaari bang i-transplant ang punla ng karot mula sa seedling tray?",
            answer = "Hindi inirerekomenda! Ang karot ay taproot crop. Kapag nabaluktot o nasugatan ang pinakadulo ng ugat habang inililipat, magiging pilipit, maikli, at sanga-sanga ang kalalabasan nito. Laging direct-seeded sa kamada ang karot."
        ),
        FaqItem(
            question = "Bakit pumutok o nabiyak ang katawan ng aking karot bago anihin?",
            answer = "Ito ay 'root splitting' dulot ng pabago-bagong moisture—kapag dumanas ng tagtuyot ang kamada at biglang nakatanggap ng napakaraming tubig (hal. malakas na buhos ng ulan). Panatilihing pantay ang pagdidilig at maglagay ng mulch."
        )
    ),
    nutritionTable = NutritionTableData(
        servingSize = "100 g (Sariwang Hilaw na Karot)",
        calories = "41 kcal",
        macroNutrients = listOf(
            NutritionRowData("Protina (Protein)", "0.93 g", "1.9%"),
            NutritionRowData("Kabuuang Taba (Total Fat)", "0.24 g", "0.3%"),
            NutritionRowData("Carbohydrates", "9.58 g", "7.4%"),
            NutritionRowData("Dietary Fiber (Hibla)", "2.8 g", "11.2%"),
            NutritionRowData("Asukal (Natural Sugars)", "4.74 g", "—"),
            NutritionRowData("Tubig (Water Content)", "88.3 g", "—")
        ),
        microNutrients = listOf(
            NutritionRowData("Beta-Carotene (Pro-Vit A)", "8,285 mcg", "104%"),
            NutritionRowData("Alpha-Carotene", "3,477 mcg", "Antioxidant"),
            NutritionRowData("Lutein & Zeaxanthin", "256 mcg", "Eye Health"),
            NutritionRowData("Bitamina K1 (Phylloquinone)", "13.2 mcg", "13%"),
            NutritionRowData("Bitamina C (Ascorbic Acid)", "5.9 mg", "7%"),
            NutritionRowData("Potassium (K)", "320 mg", "7%"),
            NutritionRowData("Calcium (Ca)", "33 mg", "3%"),
            NutritionRowData("Folate (Bitamina B9)", "19 mcg", "5%"),
            NutritionRowData("Manganese (Mn)", "0.14 mg", "6%")
        )
    ),
    exploreArticles = listOf(
        ArticleItem("Gabay sa Masaganang Karot sa Benguet Highlands", "Mga lihim ng mga magsasaka sa Atok at Buguias para sa 20–25 tonelada bawat ektarya gamit ang New Kuroda.", "7 min basahin"),
        ArticleItem("Paano Maiiwasan ang Sanga-sangang Ugat (Forking)", "Paghahanda ng 30 cm malalim na kamada, tamang pataba, at pagkontrol sa nematodes.", "5 min basahin"),
        ArticleItem("Sining ng Paghihimay (Thinning) at Earthing-Up", "Hakbang-hakbang na paraan upang mapalaki ang ugat at maiwasan ang berdeng balikat.", "4 min basahin")
    )
)
