package com.maptanim.app.features.farm.dialogs.vegetables_information

import androidx.compose.ui.graphics.Color
import com.maptanim.app.R

/**
 * Vegetable encyclopedia dossier for Squash / Kalabasa (Cucurbita moschata).
 * Compiled from MapTanim Squash technical data, DA-BPI, and East-West Seed agronomic guidelines.
 */
val squashData = CropEncyclopedia(
    cropName = "Squash",
    localName = "Kalabasa (Cucurbita moschata)",
    scientificName = "Cucurbita moschata",
    category = "Cucurbits (Cucurbitaceae)",
    family = "Cucurbitaceae",
    overview = "Ang kalabasa (Cucurbita moschata) ay isa sa pinakamahalagang pananim na gulay sa Pilipinas, paboritong itanim sa mga bakuran, malalawak na sakahan sa kapatagan (Pangasinan, Nueva Ecija, Tarlac, Ilocos Region, Batangas, Quezon), at maging sa mga upland areas ng bansa. Kilala ito bilang matatag at mabungang trailing vine na gumagapang nang 3 hanggang 6 na metro sa lupa o sa simpleng balag. Bukod sa siksik, malagkit, at matamis na bunga nito na mayaman sa beta-carotene (Pro-Vitamin A), kinakain din sa lutuing Pilipino ang mga sariwang talbos, bulaklak, at maging ang mga pinatuyong buto nito.\n\nNangangailangan ang kalabasa ng mainit at buong sikat ng araw (full sun) at lumalago sa sandy loam hanggang clay loam na lupa. Dahil sa mabilis at malawak nitong paggapang, karaniwan itong itinatanim sa matataas na 'bunton' o 'hills' (60–90 cm ang lapad, 20–30 cm ang taas) na may 2 hanggang 3 metrong pagitan, na may malalalim na drainage canals upang hindi mababad ang ugat sa tag-ulan.\n\nIsa sa pinakakritikal na aspeto sa produksyon ng kalabasa ay ang polinasyon. Bilang halaman na may hiwalay na lalaki at babaeng bulaklak (monoecious), umaasa ito sa aktibong paglipad ng mga bubuyog (honeybees at kiwot) mula 6:00 hanggang 9:00 ng umaga. Kapag kulang ang bubuyog o maulan, kinakailangan ang mano-manong polinasyon (hand pollination) upang maiwasan ang pagkalaglag ng mga munting kalabasa.",
    selectedVariety = VarietyInfo(
        name = "Suprema F1",
        producer = "East-West Seed Philippines",
        type = "Tropical Hybrid Winter Squash (F1)",
        badge = "F1 Hybrid",
        description = "Ang nangungunang commercial hybrid na kalabasa sa Pilipinas. May pambihirang resistensya sa mapaminsalang Squash Leaf Curl Virus (SLCV) at mataas na tolerance sa tag-ulan. Nagbubunga ng siksik, makapal, at malagkit na laman na may matingkad na kulay kahel (deep orange flesh), may katamtamang bigat na 2.5 hanggang 4.5 kilo bawat bunga, at matigas na balat na tumatagal sa malayuang biyahe at pangmatagalang imbakan."
    ),
    otherVarieties = listOf(
        "Suprema F1 (East-West Seed Standard)",
        "Rizal F1 (High-Yield Commercial Hybrid)",
        "Golden Nugget (Kabocha-Type OPV)",
        "San Mateo Native Kalabasa (Local Open-Pollinated)",
        "Butternut Rugosa (Neck Pumpkin Type)",
        "Tambuli F1 (Early Maturing Hybrid)"
    ),
    carouselSlides = listOf(
        CarouselSlide(
            title = "Masaganang Kalabasa sa Pilipinas",
            subtitle = "Suprema F1 na siksik at matingkad ang kulay kahel sa matabang bunton",
            stage = "Matipid sa Ani (Market Ready)",
            badgeColor = Color(0xFFE65100),
            drawableResId = R.drawable.ph_squash_hero
        ),
        CarouselSlide(
            title = "Direktang Pagpupunla sa Bunton",
            subtitle = "5–10 araw na mabilis na pagsibol ng malulusog na binhi sa raised hills",
            stage = "Yugto ng Pagsibol (Emergence)",
            badgeColor = Color(0xFF1565C0),
            drawableResId = R.drawable.ph_squash_seedlings
        ),
        CarouselSlide(
            title = "Pamumulaklak at Pagsasalin ng Polon",
            subtitle = "Hiwalay na lalaki at babaeng bulaklak na binibisita ng mga bubuyog sa umaga",
            stage = "Yugto ng Polinasyon (Flowering)",
            badgeColor = Color(0xFFF57F17),
            drawableResId = R.drawable.ph_squash_flowers
        ),
        CarouselSlide(
            title = "Pag-aani ng Matigas na Kalabasa",
            subtitle = "Paggupit na may 3–5 cm na tangkay para sa 2–4 na buwang imbakan",
            stage = "Ganap na Hinog (Harvest Maturity)",
            badgeColor = Color(0xFF2E7D32),
            drawableResId = R.drawable.ph_squash_harvest
        )
    ),
    quickInfo = listOf(
        QuickInfoItem(
            key = "lifespan",
            label = "Buhay ng Halaman",
            value = "90–120 Araw",
            explanation = ExplanationData(
                title = "Buhay ng Halaman (Crop Lifecycle)",
                summary = "Ang kalabasa ay may 90 hanggang 120 araw na kabuuang siklo mula paghahasik ng binhi hanggang sa ganap na hinog na bunga.",
                details = listOf(
                    "Pagsibol (Germination): 5–10 araw matapos ihasik nang direkta sa bunton.",
                    "Vegetative Vining: 3–6 linggo ng mabilisang paggapang ng baging.",
                    "Pamumulaklak: 5–7 linggo kung saan unang lumalabas ang lalaking bulaklak bago ang babae.",
                    "Paglaki ng Bunga: 6–10 linggo matapos ang matagumpay na polinasyon.",
                    "Anihan: 80–100 araw para sa sariwang palengke; 100–120 araw para sa pang-imbak."
                )
            )
        ),
        QuickInfoItem(
            key = "harvest_time",
            label = "Unang Ani",
            value = "80–100 DAS",
            explanation = ExplanationData(
                title = "Panahon ng Pag-aani",
                summary = "80 hanggang 100 araw pagkatanim para sa hinog na bunga; 50–60 araw para sa murang kalabasa at talbos.",
                details = listOf(
                    "Murang kalabasa (tender fruit): maaaring anihin nang maaga kasabay ng talbos.",
                    "Hinog na kalabasa (mature fruit): matigas ang balat at parang cork ang tuyong tangkay.",
                    "Single/Periodic Harvest: inaani paisa-isa habang tumitigas ang balat ng bawat bunga."
                )
            )
        ),
        QuickInfoItem(
            key = "spacing",
            label = "Agwat ng Tanim",
            value = "2.0–3.0 m bawat Bunton",
            explanation = ExplanationData(
                title = "Tamang Agwat ng mga Bunton (Hill Spacing)",
                summary = "2 hanggang 3 metro ang agwat ng bawat bunton (hills); 60–90 cm ang lapad at 20–30 cm ang taas ng bunton.",
                details = listOf(
                    "Naghahasik ng 3–4 na buto bawat bunton, at hinihimay (thinned) sa 2 pinakamalulusog na halaman.",
                    "Nagbibigay ng 1,500 hanggang 2,500 burol bawat ektarya.",
                    "Ang maluwang na agwat ay nagbibigay ng espasyo para sa 3–6 metrong gapang ng baging at sapat na sikat ng araw."
                )
            )
        ),
        QuickInfoItem(
            key = "sunlight",
            label = "Sikat ng Araw",
            value = "Buong Araw (6–8 Oras)",
            explanation = ExplanationData(
                title = "Pangangailangan sa Liwanag",
                summary = "Nangangailangan ng buong sikat ng araw (full sun) mula umaga hanggang hapon.",
                details = listOf(
                    "Ang lilim ay nagpapababa sa bilang ng babaeng bulaklak at nagdudulot ng panlalata ng baging.",
                    "Pinatutuyo ng sikat ng araw ang hamog sa dahon, na pumipigil sa powdery at downy mildew."
                )
            )
        ),
        QuickInfoItem(
            key = "watering",
            label = "Pangangailangan sa Tubig",
            value = "1–2x bawat Linggo (Moderate)",
            explanation = ExplanationData(
                title = "Patubig sa Bunton",
                summary = "Katamtamang patubig; kritikal sa pamumulaklak at mabilisang paglaki ng bunga.",
                details = listOf(
                    "Diligan sa base ng bunton o sa pamamagitan ng furrow irrigation.",
                    "Iwasang basain ang mga dahon at bulaklak upang hindi matunaw ang polon at maantala ang polinasyon.",
                    "Bawasan ang patubig 2 linggo bago anihin upang tumigas ang balat at tumamis ang laman."
                )
            )
        ),
        QuickInfoItem(
            key = "soil_type",
            label = "Uri ng Lupa at pH",
            value = "Sandy Loam to Clay Loam (pH 5.5–6.8)",
            explanation = ExplanationData(
                title = "Pangangailangan sa Lupa at pH",
                summary = "Buhaghag, mayaman sa organikong pataba, at may magandang daluyan ng tubig.",
                details = listOf(
                    "Optimal pH: 5.5 hanggang 6.8 (katamtamang asido hanggang neutral).",
                    "Kapag mababa sa 5.5 ang pH, maglagay ng agricultural lime (apog) bago magtanim.",
                    "Tinatanggap ang clay loam basta't may matataas na bunton at malalalim na kanal sa tag-ulan."
                )
            )
        ),
        QuickInfoItem(
            key = "yield_potential",
            label = "Inaasahang Ani",
            value = "15–25 t/ha (3–6 kg bawat puno)",
            explanation = ExplanationData(
                title = "Potensyal na Dami ng Ani",
                summary = "15 hanggang 25 tonelada bawat ektarya sa ilalim ng maayos na pamamahala at pest control.",
                details = listOf(
                    "Bawat puno ay karaniwang nagbubunga ng 2 hanggang 3 malalaking kalabasa (2.5–4.5 kg bawat isa).",
                    "Depende ang ani sa matagumpay na polinasyon ng bubuyog at proteksyon laban sa melon fruit fly."
                )
            )
        )
    ),
    companionPlants = listOf(
        PlantRelationItem(
            name = "Mais (Corn)",
            scientificName = "Zea mays",
            isBeneficial = true,
            role = "Natural na Windbreak at Trellis Support",
            explanation = ExplanationData(
                title = "Kasama ang Mais sa Sakahan",
                summary = "Bahagi ng tradisyunal na 'Three Sisters' system kung saan ang mais ay nagbibigay ng proteksyon sa hangin at bahagyang lilim sa mainit na lupa.",
                details = listOf(
                    "Nagsisilbing harang laban sa malalakas na hanging sumisira sa malalapad na dahon.",
                    "Umaakit ng mga beneficial predators na kumakain ng aphids."
                )
            )
        ),
        PlantRelationItem(
            name = "Sitaw at Beans (Legumes)",
            scientificName = "Vigna unguiculata subsp. sesquipedalis",
            isBeneficial = true,
            role = "Natural Nitrogen Fixer sa Lupa",
            explanation = ExplanationData(
                title = "Pagtatanim Kasabay ng Sitaw",
                summary = "Nagsasalin ng atmospheric nitrogen sa lupa ang nodules ng sitaw para sa masiglang paggapang ng baging.",
                details = listOf(
                    "Nagbibigay ng tuloy-tuloy na natural na pataba sa lupa nang hindi sumusunog sa ugat.",
                    "Maaaring itanim sa pagitan ng mga bunton ng kalabasa."
                )
            )
        ),
        PlantRelationItem(
            name = "Amarilyo (Marigold)",
            scientificName = "Tagetes erecta",
            isBeneficial = true,
            role = "Pangtaboy ng Nematodes at Insekto",
            explanation = ExplanationData(
                title = "Marigold sa Paligid ng Bunton",
                summary = "Naglalaman ang ugat ng marigold ng alpha-terthienyl na pumapatay sa root-knot nematodes at nagtataboy sa squash beetles.",
                details = listOf(
                    "Itanim sa gilid ng bawat bunton ng kalabasa.",
                    "Umaakit din ng mga pukyutan at hoverflies na tumutulong sa polinasyon at pest control."
                )
            )
        ),
        PlantRelationItem(
            name = "Labanos (Radish)",
            scientificName = "Raphanus sativus",
            isBeneficial = true,
            role = "Pangtaboy ng Cucumber Beetle",
            explanation = ExplanationData(
                title = "Labanos Bilang Trap at Repellent",
                summary = "Ang maanghang na amoy ng labanos ay nagtataboy sa mga salagubang (cucurbit leaf beetles) habang maliit pa ang punla ng kalabasa.",
                details = listOf(
                    "Mabilis maani (30 araw) kaya hindi nakikipag-agawan sa espasyo ng kalabasa.",
                    "Pinabubuhaghag din nito ang siksik na lupa sa paligid."
                )
            )
        )
    ),
    combativePlants = listOf(
        PlantRelationItem(
            name = "Ampalaya (Bitter Gourd)",
            scientificName = "Momordica charantia",
            isBeneficial = false,
            role = "Shared Cucurbit Pests & Mildew Vector",
            explanation = ExplanationData(
                title = "Iwasan ang Katabing Ampalaya",
                summary = "Parehong kabilang sa Cucurbitaceae family na nagpapalipat-lipat ng melon fruit fly, downy mildew, at aphids.",
                details = listOf(
                    "Mabilis kumalat ang fungal spores ng downy mildew sa pagitan ng dalawang pananim.",
                    "Maglaan ng hindi bababa sa 20 metrong distansya o magtanim ng ibang pamilya sa pagitan."
                )
            )
        ),
        PlantRelationItem(
            name = "Pipino (Cucumber)",
            scientificName = "Cucumis sativus",
            isBeneficial = false,
            role = "Kakumpitensya sa Peste at Amag",
            explanation = ExplanationData(
                title = "Ilayo sa Taniman ng Pipino",
                summary = "Parehong umaakit ng Aulacophora leaf beetles at nagtataglay ng mga virus na nakakahawa sa kalabasa.",
                details = listOf(
                    "Nababawasan ang bilang ng mga bubuyog na dumarapo sa kalabasa dahil nag-aagawan sa mga bulaklak.",
                    "Iwasang itanim sa parehong kamada nang magkasunod."
                )
            )
        ),
        PlantRelationItem(
            name = "Pakwan at Melon",
            scientificName = "Citrullus lanatus / Cucumis melo",
            isBeneficial = false,
            role = "Inbreeding Pests & Melon Aphids Magnet",
            explanation = ExplanationData(
                title = "Iwasan ang Katabing Pakwan o Melon",
                summary = "Malaking paboritong tirahan ng melon fruit flies na mabilis na mangitlog sa mga munting bunga ng kalabasa.",
                details = listOf(
                    "Magiging sanhi ng maagang pagkalaglag ng mga bunga.",
                    "Panatilihin ang crop rotation: huwag itanim ang kalabasa sa lupang katataniman lamang ng melon."
                )
            )
        ),
        PlantRelationItem(
            name = "Patatas (Potato)",
            scientificName = "Solanum tuberosum",
            isBeneficial = false,
            role = "Allelopathic Retardation & Agaw-Sustansya",
            explanation = ExplanationData(
                title = "Iwasan ang Katabing Patatas",
                summary = "Mabigat sumipsip ng sustansya (Potassium at Nitrogen) ang patatas at maaaring magpabagal sa paggapang ng kalabasa.",
                details = listOf(
                    "Maaaring magdulot ng stunted growth sa mga baging ng kalabasa."
                )
            )
        )
    ),
    nutritionBadges = listOf(
        NutritionBadgeItem(
            code = "VIT_A",
            name = "Bitamina A (Beta-Carotene)",
            percentageDaily = "245% DV",
            role = "Pambihirang Sustansya para sa Mata at Resistensya",
            explanation = ExplanationData(
                title = "Mataas na Beta-Carotene sa Kalabasa",
                summary = "Ang matingkad na dilaw-kahel na kulay ng laman ay patunay ng napakataas na antas ng provitamin A carotenoids.",
                details = listOf(
                    "Nagpoprotekta sa paningin (laban sa night blindness) at nagpapalakas ng immune system.",
                    "Mahalagang sustansya para sa kalusugan ng mga lumalaking bata sa Pilipinas."
                )
            )
        ),
        NutritionBadgeItem(
            code = "VIT_C",
            name = "Bitamina C",
            percentageDaily = "17% DV",
            role = "Antioxidant at Pampatibay ng Balat",
            explanation = ExplanationData(
                title = "Bitamina C sa Bunga at Talbos",
                summary = "Tumutulong sa pagpapalakas ng resistensya laban sa mga karaniwang sakit at ubo.",
                details = listOf(
                    "Taglay kapwa sa dilaw na laman at sa sariwang talbos ng kalabasa.",
                    "Sumusuporta sa mabilis na paggaling ng mga sugat at absorption ng iron."
                )
            )
        ),
        NutritionBadgeItem(
            code = "POTASSIUM",
            name = "Potassium (K)",
            percentageDaily = "12% DV",
            role = "Pampapantay ng Presyon ng Dugo at Puso",
            explanation = ExplanationData(
                title = "Potassium para sa Kalusugan ng Puso",
                summary = "Naglalaman ng 340 mg ng potassium bawat 100 gramo ng lutong kalabasa.",
                details = listOf(
                    "Tumutulong sa pagkontrol ng altapresyon at fluid balance sa katawan.",
                    "Mabuti para sa wastong pagkilos ng mga kalamnan."
                )
            )
        ),
        NutritionBadgeItem(
            code = "FIBER",
            name = "Dietary Fiber (Hibla)",
            percentageDaily = "11% DV",
            role = "Pampalusog ng Tiyan at Panunaw",
            explanation = ExplanationData(
                title = "Mataas na Hibla para sa Panunaw",
                summary = "Mayaman sa soluble at insoluble fiber na nagpapadali sa pagdumi at nagpapanatili ng busog.",
                details = listOf(
                    "Tumutulong sa pagpapababa ng cholesterol sa dugo.",
                    "Mababa sa calories kaya paborito sa masustansyang diyeta."
                )
            )
        )
    ),
    pestsAndDiseases = listOf(
        PestDiseaseItem(
            name = "Melon Fruit Fly",
            scientificName = "Bactrocera cucurbitae",
            severity = "Kritikal (Major Fruit Loss)",
            symptoms = "Tuldok na tusok na may dagta sa balat ng bunga; may uod (maggots) sa loob; nabubulok at maagang nalalaglag ang bunga.",
            prevention = "Fruit bagging gamit ang paper/net bag agad matapos ma-pollinate; Cue-lure pheromone traps; field sanitation.",
            treatment = "Ilibing nang malalim (50 cm) ang lahat ng bulok na bunga; mag-spray ng protein bait na may spinosad sa paligid.",
            explanation = ExplanationData(
                title = "Melon Fruit Fly sa Kalabasa",
                summary = "Ang numero unong maninira sa ani ng kalabasa sa Pilipinas.",
                details = listOf(
                    "Nangingitlog sa ilalim ng balat ng bata o katamtamang kalabasa.",
                    "Ang fruit bagging ang pinakamabisang ligtas at organic na paraan ng proteksyon."
                )
            )
        ),
        PestDiseaseItem(
            name = "Squash / Cucurbit Leaf Beetle",
            scientificName = "Aulacophora similis / Epilachna spp.",
            severity = "Katamtaman (Seedling Defoliation)",
            symptoms = "Dilaw-kahel na salagubang na kumakain at gumugupit ng mga pabilog na butas sa dahon; pagkalanta ng mga batang punla.",
            prevention = "Takpan ng row covers ang punla sa unang 3 linggo; magbudbod ng pinong abo ng kahoy (wood ash); intercrop ng marigold.",
            treatment = "Pitasin sa kamay (handpicking) sa maagang umaga; mag-spray ng neem extract o sili-bawang spray sa dahon.",
            explanation = ExplanationData(
                title = "Salagubang ng Kalabasa (Cucurbit Beetle)",
                summary = "Mabilis kumain ng dahon lalo na sa panahon ng tag-araw.",
                details = listOf(
                    "Mabagal lumipad sa madaling araw dahil sa hamog kaya madaling pulutin.",
                    "Ayaw ng salagubang ang amoy at gaspang ng abo ng kahoy."
                )
            )
        ),
        PestDiseaseItem(
            name = "Melon Aphids & Whiteflies",
            scientificName = "Aphis gossypii / Bemisia tabaci",
            severity = "Mataas (Virus Vector)",
            symptoms = "Kumpol-kumpol na kuto sa ilalim ng talbos; kulot na dahon; matamis na dagta na nagiging itim na amag (sooty mold).",
            prevention = "Maglagay ng yellow sticky traps; reflective silver mulch sa bunton; pangalagaan ang mga ladybug at gagamba.",
            treatment = "Mag-spray ng mild insecticidal soap (1 tbsp dish soap sa 4L tubig) o neem oil solution sa ilalim ng dahon.",
            explanation = ExplanationData(
                title = "Kuto sa Talbos at Tagapagdala ng Virus",
                summary = "Pangunahing tagapagdala ng Squash Leaf Curl Virus sa bukid.",
                details = listOf(
                    "Sumisipsip ng katas ng talbos kaya bansot ang paggapang.",
                    "Wisikan nang diretso ang ilalim ng mga dahon."
                )
            )
        ),
        PestDiseaseItem(
            name = "Squash Vine Borer",
            scientificName = "Melittia spp. / Diaphania spp.",
            severity = "Kritikal (Vine Death)",
            symptoms = "Biglaang pagkalanta ng buong sanga; may butas na may parang basang lagari (frass) sa puno o buko ng baging.",
            prevention = "Balutin ng aluminum foil ang unang 10 cm ng base ng puno; i-drench ng Bacillus thuringiensis (Bt) ang puno.",
            treatment = "Hiwain nang paayon ang baging gamit ang labaha upang patayin ang uod; ibaon agad sa matabang lupa ang buko upang mag-ugat.",
            explanation = ExplanationData(
                title = "Uod sa Loob ng Baging (Vine Borer)",
                summary = "Pumapasok ang larva sa loob ng baging at kinakain ang daluyan ng tubig.",
                details = listOf(
                    "May kakayahan ang kalabasa na maglabas ng bagong ugat sa mga buko kapag ibinaon sa lupa.",
                    "I-monitor ang base ng puno linggu-linggo."
                )
            )
        ),
        PestDiseaseItem(
            name = "Powdery Mildew",
            scientificName = "Podosphaera xanthii / Erysiphe cichoracearum",
            severity = "Mataas (Leaf Desiccation)",
            symptoms = "Mapuputing pulbos na parang harina sa ibabaw ng dahon; natutuyo, naninilaw, at nagiging malutong ang mga dahon.",
            prevention = "Maluwang na 2–3 metrong agwat ng bunton; huwag magdilig sa hapon; piliin ang matitibay na barayti.",
            treatment = "Mag-spray ng Baking Soda spray (1 tbsp baking soda + 1 tbsp oil + soap sa 4L tubig) o diluted fresh milk spray (1:9 ratio).",
            explanation = ExplanationData(
                title = "Puting Pulbos sa Dahon (Powdery Mildew)",
                summary = "Lumalaganap sa mainit na araw na may mahamog at malamig na gabi.",
                details = listOf(
                    "Humaharang sa sikat ng araw kaya hindi makagawa ng pagkain ang halaman.",
                    "Agad gupitin ang mga lumang dahon na labis nang apektado."
                )
            )
        ),
        PestDiseaseItem(
            name = "Downy Mildew",
            scientificName = "Pseudoperonospora cubensis",
            severity = "Kritikal (Rapid Defoliation)",
            symptoms = "Manilaw-nilaw na sulok-sulok na batik (angular lesions) sa ibabaw ng dahon; may abong amag sa ilalim kapag mahamog.",
            prevention = "Iwasan ang overhead watering; panatilihing tuyo ang dahon; magtanim ng tolerant hybrids tulad ng Suprema F1.",
            treatment = "Tanggalin at sunugin ang mga apektadong dahon; mag-spray ng Copper Hydroxide o Bordeaux mixture.",
            explanation = ExplanationData(
                title = "Downy Mildew sa Tag-Ulan",
                summary = "Mabilis makasira ng taniman sa panahon ng habagat at tuloy-tuloy na ulan.",
                details = listOf(
                    "Nangangailangan ng moisture sa dahon upang dumami ang spores.",
                    "Maglaan ng magandang drainage sa pagitan ng mga bunton."
                )
            )
        ),
        PestDiseaseItem(
            name = "Squash Leaf Curl Virus (SLCV)",
            scientificName = "Squash Leaf Curl Begomovirus",
            severity = "Kritikal (Incurable Stunting)",
            symptoms = "Makakapal, kulot, at kupos na mga bagong talbos; may dilaw na mosaic pattern; bansot ang halaman at walang nabubuong bunga.",
            prevention = "Magtanim ng Suprema F1 (SLCV-resistant); kontrolin ang whiteflies gamit ang yellow sticky traps at silver mulch.",
            treatment = "Walang gamot ang virus; agad bunutin at sunugin ang mga may impeksyong halaman upang hindi kumalat sa iba.",
            explanation = ExplanationData(
                title = "Squash Leaf Curl Virus (SLCV)",
                summary = "Ang pinakamatinding viral disease ng kalabasa sa Pilipinas.",
                details = listOf(
                    "Ikinakalat ng puting langaw (Bemisia tabaci).",
                    "Ang Suprema F1 ang binuong panlaban ng East-West Seed para sa sakit na ito."
                )
            )
        )
    ),
    goodAndBadCritters = listOf(
        CritterItem(
            name = "Pukyutan at Kiwot (Honeybees & Stingless Bees)",
            role = "Pinakamahalagang Pollinators ng Kalabasa",
            attractsWith = "Malalaki at mababangong dilaw na bulaklak sa umaga",
            explanation = ExplanationData(
                title = "Mga Bubuyog sa Pamumulaklak",
                summary = "Kailangan ng 8 hanggang 12 pagbisita ng bubuyog upang ma-pollinate ang isang babaeng bulaklak ng kalabasa.",
                details = listOf(
                    "Aktibo mula 6:00 AM hanggang 9:00 AM sa madaling araw.",
                    "Huwag kailanman mag-spray ng pamatay-insekto sa umaga upang hindi sila mamatay."
                )
            )
        ),
        CritterItem(
            name = "Ladybird Beetles (Mananahi / Ladybugs)",
            role = "Natural Predator ng Aphids at Spider Mites",
            attractsWith = "Marigold flowers and diverse farm borders",
            explanation = ExplanationData(
                title = "Ladybugs sa Baging ng Kalabasa",
                summary = "Kumakain ng hanggang 50 aphids bawat araw, pinipigilan ang pagdami ng vectors ng virus.",
                details = listOf(
                    "Likas na naninirahan sa mga damo at bulaklak sa paligid ng sakahan.",
                    "Iwasan ang malawakang paggamit ng synthetic insecticides."
                )
            )
        ),
        CritterItem(
            name = "Gagamba (Garden Spiders)",
            role = "Likas na Maninila ng Moths at Beetles",
            attractsWith = "Maluwang na baging at matatayog na mais",
            explanation = ExplanationData(
                title = "Gagamba sa Taniman",
                summary = "Nanghuhuli ng mga lumilipad na gamu-gamo ng vine borer at mga salagubang sa gabi.",
                details = listOf(
                    "Tumutulong sa pagbalanse ng ekolohiya sa bukid nang walang lason."
                )
            )
        ),
        CritterItem(
            name = "Melon Fruit Fly (Bactrocera cucurbitae)",
            role = "Mapaminsalang Peste sa Bunga",
            attractsWith = "Amoy ng sariwang munting bunga ng kalabasa",
            explanation = ExplanationData(
                title = "Banta ng Fruit Fly",
                summary = "Pangunahing dahilan ng pagkabulok at pagkaagnas ng kalabasa sa bukid.",
                details = listOf(
                    "Sugpuin gamit ang fruit bagging at pheromone traps na may Cue-lure.",
                    "Ilibing ang lahat ng bulok na kalabasa nang malalim sa lupa."
                )
            )
        )
    ),
    seasonalCalendar = listOf(
        SeasonalCalendarMonth("Ene", "Pangunahing Yugto ng Paglaki", "Malamig na amihan; magandang pamumulaklak at mabilis na paglaki ng bunga sa irigasyon.", Color(0xFF2E7D32)),
        SeasonalCalendarMonth("Peb", "Pangunahing Anihan (Peak Harvest)", "Mataas ang kalidad ng bunga; matigas ang balat at matamis ang laman sa tag-araw.", Color(0xFF2E7D32)),
        SeasonalCalendarMonth("Mar", "Pagtatanim sa Tag-Araw", "Paghahanda ng bunton na may sapat na patubig; maglagay ng makapal na mulch sa bunton.", Color(0xFF1565C0)),
        SeasonalCalendarMonth("Abr", "Patubig sa Matinding Init", "Kailangang diligan 1–2x bawat linggo upang hindi malanta ang mga baging sa init.", Color(0xFFE65100)),
        SeasonalCalendarMonth("Mayo", "Pagsisimula ng Tag-Ulan", "Paggawa ng matataas na bunton (30 cm) at drainage canals bago magsimula ang malalakas na ulan.", Color(0xFF1565C0)),
        SeasonalCalendarMonth("Hun", "Tag-Ulan sa Kapatagan", "Pag-aalaga sa drainage canals upang maiwasan ang crown rot at waterlogging.", Color(0xFF2E7D32)),
        SeasonalCalendarMonth("Hul", "Panahon ng Habagat", "Paglalagay ng tuyong dayami sa ilalim ng mga bunga upang hindi lumapat sa putik.", Color(0xFFE65100)),
        SeasonalCalendarMonth("Ago", "Banta ng Bagyo / Off-Season", "Mataas ang presyo sa palengke; kailangan ang maingat na proteksyon sa baging laban sa hangin.", Color(0xFF2E7D32)),
        SeasonalCalendarMonth("Set", "Paghahanda para sa Regular Season", "Pag-aararo at pagbubunton sa mga palayan pagkatapos maani ang palay.", Color(0xFF1565C0)),
        SeasonalCalendarMonth("Okt", "Malawakang Paghahasik", "Pinakamainam na buwan ng pagtatanim ng kalabasa sa buong Luzon, Visayas, at Mindanao.", Color(0xFF2E7D32)),
        SeasonalCalendarMonth("Nob", "Mabilis na Paggapang ng Baging", "Yugto ng mabilisang pamumulaklak at aktibong polinasyon ng mga bubuyog sa amihan.", Color(0xFF2E7D32)),
        SeasonalCalendarMonth("Dis", "Pamumunga at Pagpapatamis", "Mataas na demand para sa Kapaskuhan; unti-unting pagpapatuyo ng tangkay para sa anihan.", Color(0xFF2E7D32))
    ),
    careGuide = listOf(
        HowToSectionItem(
            title = "Paghahanda ng Bunton (Hills) at Kanal",
            summary = "Paggawa ng 60–90 cm na lapad at 20–30 cm na taas na bunton para sa magandang drainage.",
            steps = listOf(
                "Bungkalin ang lupa sa lokasyon ng bunton sa lalim na 25–30 cm sa agwat na 2 hanggang 3 metro.",
                "Maghalo ng 2–3 pala ng fully decomposed na dumi ng baka o compost at 50 gramo ng 14-14-14 sa bawat bunton.",
                "Hubugin ang bunton na may taas na 20–30 cm upang hindi maipon ang tubig sa paligid ng puno.",
                "Gumawa ng malinaw na drainage furrows sa pagitan ng mga hanay ng bunton."
            )
        ),
        HowToSectionItem(
            title = "Mano-Manong Polinasyon sa Umaga (Hand Pollination)",
            summary = "Pagtitiyak ng 95% tagumpay sa pamumunga kapag maulan o kulang ang bubuyog.",
            steps = listOf(
                "Pumunta sa taniman sa pagitan ng 6:00 AM at 8:30 AM bago mag-init ang araw.",
                "Pumitas ng sariwang lalaking bulaklak at maingat na punitin ang mga dilaw na talulot (petals).",
                "Dahan-dahang ipahid ang dilaw na polon sa dulo ng stigma ng nakabukang babaeng bulaklak.",
                "Ang isang lalaking bulaklak ay sapat para sa 2–3 babaeng bulaklak."
            )
        ),
        HowToSectionItem(
            title = "Pagpupungos ng Baging at Fruit Thinning",
            summary = "Pagpili ng 2–3 pinakamagagandang kalabasa bawat puno para sa de-kalidad na ani.",
            steps = listOf(
                "Kapag nakapagtakda na ng 2 hanggang 3 malulusog na bunga ang baging, putulan ng dulo ang sanga 2–3 dahon lampas sa huling bunga (tip pinching).",
                "Alisin ang mga deformed o huling sumibol na maliliit na kalabasa.",
                "Putulin ang maliliit na tertiary suckers upang maipon ang sustansya sa pangunahing bunga.",
                "Anihin ang mga pinungos na talbos bilang masarap at sariwang gulay."
            )
        ),
        HowToSectionItem(
            title = "Paglalagay ng Sapin sa Bunga Laban sa Putik",
            summary = "Pagpapanatili ng makinis at walang amag na balat ng kalabasa.",
            steps = listOf(
                "Maglatag ng makapal na tuyong dayami (rice straw) o tuyong dahon sa ilalim ng bawat bunga.",
                "Iangat nang maingat ang kalabasa upang maiwasan ang pagkapilipit ng tangkay.",
                "Siguraduhing hindi sumasayad sa basang lupa upang hindi maagnas ang ilalim nito."
            )
        )
    ),
    growthTimeline = listOf(
        TimelineStageItem(
            stageName = "1. Pagsibol ng Binhi (Germination)",
            durationDays = "5–10 Araw",
            description = "Direktang pagsibol ng 3–4 na buto sa mamasa-masang bunton; mabilis lumabas ang makakapal na cotyledon leaves."
        ),
        TimelineStageItem(
            stageName = "2. Yugto ng Punla at Thinning",
            durationDays = "10–21 Araw",
            description = "Paglabas ng unang tunay na dahon; paghihimay (thinning) sa 2 pinakamalulusog na halaman bawat bunton at mulching."
        ),
        TimelineStageItem(
            stageName = "3. Paggapang ng Baging (Vegetative Vining)",
            durationDays = "3–6 Linggo",
            description = "Mabilisang paggapang ng makakapal na baging (3–6 metro); paglalagay ng side-dress urea at paggiya sa mga sanga."
        ),
        TimelineStageItem(
            stageName = "4. Pamumulaklak at Polinasyon",
            durationDays = "5–7 Linggo",
            description = "Unang paglabas ng mga lalaking bulaklak kasunod ang mga babaeng bulaklak; aktibong polinasyon ng mga bubuyog sa umaga."
        ),
        TimelineStageItem(
            stageName = "5. Paglaki ng Bunga (Fruit Bulking)",
            durationDays = "6–10 Linggo matapos mamunga",
            description = "Mabilisang paglaki ng bunga at pag-iipon ng beta-carotene at asukal; paglalagay ng Potash at fruit bagging laban sa fruit fly."
        ),
        TimelineStageItem(
            stageName = "6. Ganap na Hinog at Pag-aani",
            durationDays = "90–120 Araw (DAS)",
            description = "Matigas na balat, tuyong kulubot na tangkay, at matingkad na kahel na laman; maingat na paggupit na may 3–5 cm na peduncle."
        )
    ),
    howTos = listOf(
        HowToSectionItem(
            title = "Paano Magtanim ng Kalabasa sa Bunton (Direct Seeding)",
            summary = "Wastong pamamaraan ng direktang pagpupunla para sa malalakas na ugat.",
            steps = listOf(
                "Gumawa ng bunton na 60–90 cm ang lapad at 20–30 cm ang taas, may 2–3 metrong agwat bawat isa.",
                "Magbaon ng 3 hanggang 4 na buto sa lalim na 2 hanggang 3 cm sa tuktok ng bunton.",
                "Diligan nang marahan gamit ang pandilig upang mamasa-masa ang lupa nang hindi naanod ang buto.",
                "Kapag may 2–3 tunay na dahon na (10–14 araw), bunutin ang mahihinang punla at mag-iwan ng 2 malulusog na halaman."
            )
        ),
        HowToSectionItem(
            title = "Pangangalaga Laban sa Fruit Fly (Fruit Bagging)",
            summary = "Proteksyon sa munting kalabasa laban sa tusok ng langaw ng prutas.",
            steps = listOf(
                "Bantayan ang babaeng bulaklak kapag natapos na ang polinasyon at nagsimula nang magsara ang talulot (1–2 araw).",
                "Kumuha ng brown paper bag, lumang diyaryo, o fine mesh fruit bag.",
                "Isuot sa munting kalabasa at itali nang maluwag sa tangkay upang hindi makapasok ang langaw.",
                "Hayaang nakabalot ang bunga hanggang sa tumigas ang balat nito."
            )
        ),
        HowToSectionItem(
            title = "Pagpapatuyo at Curing para sa Mahabang Imbakan",
            summary = "Paraan ng pag-imbak upang tumagal ang kalabasa nang 2 hanggang 4 na buwan.",
            steps = listOf(
                "Gupitin ang kalabasa sa baging na may nakakabit na 3–5 cm na matigas na tangkay.",
                "Punasan ng tuyong basahan upang maalis ang dumi; huwag basain ng tubig.",
                "Ilatag sa isang patong (single layer) sa malilim, tuyo, at mahanging lugar sa temperaturang 24–28°C sa loob ng 7–10 araw.",
                "Itago sa bodega na may maayos na bentilasyon; huwag pagpatung-patungin nang makapal."
            )
        )
    ),
    faqs = listOf(
        FaqItem(
            question = "Bakit nalalaglag at naninilaw ang maliliit na kalabasa bago pa lumaki?",
            answer = "Ang pangunahing dahilan ay hindi nagtagumpay ang polinasyon (unpollinated female flower). Ang babaeng bulaklak ay may munting bilog na kalabasa sa ilalim, ngunit kung walang bubuyog na nagdala ng polon mula sa lalaking bulaklak sa loob ng umaga, malalanta at malalaglag ito sa loob ng 48 oras. Maaari ring sanhi ito ng tusok ng melon fruit fly o labis na ulan. Magsagawa ng mano-manong polinasyon sa pagitan ng 6:00 AM at 8:30 AM."
        ),
        FaqItem(
            question = "Bakit puro lalaking bulaklak lamang ang sumisibol sa aking kalabasa?",
            answer = "Likas sa kalabasa na maglabas ng 10–20 lalaking bulaklak sa unang 1–2 linggo ng pamumulaklak bago lumabas ang unang babae. Kung lumampas na ang 2 linggo at puro lalaki pa rin, sanhi ito ng: (1) sobrang Nitroheno (hal. sobrang Urea o dumi ng manok), (2) kakulangan sa sikat ng araw, o (3) mataas na temperatura sa gabi. Magbawas ng nitrogen, maglagay ng Muriate of Potash, at putulan ng dulo ang baging."
        ),
        FaqItem(
            question = "Paano malalaman kung handa nang anihin ang kalabasa?",
            answer = "Suriin ang tatlong palatandaan: (1) Balat: Matigas na ang balat at hindi na bumabakat kapag bumaon ang kuko. (2) Tangkay: Tuyo, kulubot, at parang cork o kahoy na ang peduncle. (3) Kulay: Nawawala ang kintab ng balat at nagiging 'dull' na may manipis na waxy coating. Karaniwang 90–120 araw ito pagkatanim."
        ),
        FaqItem(
            question = "Bakit mahalagang mag-iwan ng tangkay kapag nag-aani ng kalabasa?",
            answer = "Ang tangkay (peduncle) ang nagsisilbing natural na selyo ng bunga. Kapag pinilipit o binunot nang walang tangkay, magkakaroon ng sugat at butas sa puwit ng kalabasa kung saan mabilis papasok ang bacteria at fungi, dahilan upang mabulok ito sa loob lamang ng 1–2 linggo. Ang kalabasang may 3–5 cm na tuyong tangkay ay tumatagal ng 2 hanggang 4 na buwan."
        ),
        FaqItem(
            question = "Bakit may puting pulbos na parang harina sa mga dahon ng kalabasa?",
            answer = "Ito ay Powdery Mildew (Podosphaera / Erysiphe spp.), isang karaniwang sakit na dulot ng amag lalo na kapag mainit sa araw ngunit mahamog at malamig sa gabi. Puksain gamit ang Baking Soda spray (1 tbsp baking soda + 1 tbsp oil + soap sa 4L tubig) o gatas na may tubig (1:9 ratio) na ini-spray sa ilalim ng sikat ng araw."
        )
    ),
    nutritionTable = NutritionTableData(
        servingSize = "100 g (Sariwang Lutong Kalabasa)",
        calories = "26 kcal",
        macroNutrients = listOf(
            NutritionRowData("Protina (Protein)", "1.0 g", "2.0%"),
            NutritionRowData("Kabuuang Taba (Total Fat)", "0.1 g", "0.1%"),
            NutritionRowData("Carbohydrates", "6.5 g", "5.0%"),
            NutritionRowData("Dietary Fiber (Hibla)", "2.8 g", "11.2%"),
            NutritionRowData("Asukal (Natural Sugars)", "2.8 g", "—"),
            NutritionRowData("Tubig (Water Content)", "91.6 g", "—")
        ),
        microNutrients = listOf(
            NutritionRowData("Beta-Carotene (Pro-Vit A)", "8,512 mcg", "245%"),
            NutritionRowData("Lutein & Zeaxanthin", "1,500 mcg", "Eye Health"),
            NutritionRowData("Bitamina C (Ascorbic Acid)", "15.0 mg", "17%"),
            NutritionRowData("Potassium (K)", "340 mg", "10%"),
            NutritionRowData("Calcium (Ca)", "21 mg", "2%"),
            NutritionRowData("Magnesium (Mg)", "12 mg", "3%"),
            NutritionRowData("Iron (Bakal)", "0.8 mg", "4%"),
            NutritionRowData("Folate (Bitamina B9)", "16 mcg", "4%"),
            NutritionRowData("Phosphorus (P)", "44 mg", "4%")
        )
    ),
    exploreArticles = listOf(
        ArticleItem("Gabay sa Masaganang Suprema F1 Kalabasa sa Pilipinas", "Mga pamamaraan ng mga magsasaka sa Gitnang Luzon para sa 20–25 toneladang ani gamit ang raised hills at drip irrigation.", "7 min basahin"),
        ArticleItem("Sining ng Mano-Manong Polinasyon sa Umaga", "Hakbang-hakbang na gabay upang mapataas ang fruit set sa 95% kahit kulang ang bubuyog o maulan ang panahon.", "5 min basahin"),
        ArticleItem("Lunas sa Melon Fruit Fly at Pangmatagalang Imbakan", "Paggamit ng pheromone traps, fruit bagging, at wastong curing ng kalabasa para sa 3 buwang sariwang imbakan.", "6 min basahin")
    )
)

val pumpkinData = squashData