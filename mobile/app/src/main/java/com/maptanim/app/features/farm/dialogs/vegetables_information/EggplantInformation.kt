package com.maptanim.app.features.farm.dialogs.vegetables_information

import androidx.compose.ui.graphics.Color
import com.maptanim.app.R

    val eggplantData = CropEncyclopedia(
        cropName = "Eggplant",
        localName = "Talong (Solanum melongena)",
        scientificName = "Solanum melongena",
        category = "Nightshades (Solanaceae)",
        family = "Solanaceae",
        overview = "Ang talong (Solanum melongena) ang numero unong gulay sa Pilipinas ayon sa dami ng produksyon, lawak ng taniman, at kinikita ng mga maliliit na magsasaka sa Luzon, Visayas, at Mindanao. Kilala bilang staple ingredient sa mga paboritong lutuing Pilipino tulad ng pinakbet, tortang talong, sinigang, at kare-kare.\n\nBagamat matatag sa init ng tropiko at kayang mamunga nang tuloy-tuloy sa loob ng 6 hanggang 9 na buwan, ang pinakamalaking hamon sa produksyon ay ang mapaminsalang Harabas sa Talbos at Bunga (Fruit and Shoot Borer o Leucinodes orbonalis) at ang Lanta-Bakterya (Ralstonia solanacearum). Inirerekomenda ng DA-BPI, UPLB-IPB, at East-West Seed ang paggamit ng certified hybrid varieties tulad ng Fortuner F1 at Morena F1 na may mataas na resistensya sa sakit at masagana kung mamunga.\n\nBukod dito, tanging sa talong isinasagawa ang 'Ratooning' (pag-uupod o pagpuputol ng lumang tangkay 20–30 cm mula sa lupa pagkatapos ng unang siklo ng ani) upang magpatubo ng mga bagong suwi at makakuha ng pangalawang buong ani nang hindi na kailangang bumili ng bagong binhi o maghintay sa punlaan.",
        selectedVariety = VarietyInfo(
            name = "Fortuner F1",
            producer = "East-West Seed Philippines",
            type = "Lowland & Highland Commercial Hybrid",
            badge = "F1",
            description = "Nangungunang lilang hybrid na talong sa buong kapuluan. May makintab at makapal na balat na may luntiang calyx, mabigat at siksik ang laman, at matagal mabulok kaya't perpekto sa malayuang biyahe patungong pamilihan. May likas na tibay laban sa Bacterial Wilt at tag-tuyot."
        ),
        otherVarieties = listOf(
            "Fortuner F1 (East-West Seed)",
            "Morena F1 (Bacterial Wilt Resistant)",
            "Dumaguete Long Purple (Heritage OPV)",
            "Casino F1 (High-Yield Commercial)",
            "Mistisa F1 (Bicol Green-Purple Striped)",
            "Batangas Native Long (Sweet Heirloom)",
            "Black Beauty (Round Western Eggplant)",
            "Ping Tung Long (Slender Oriental)"
        ),
        carouselSlides = listOf(
            CarouselSlide("Punlaan sa Seedling Tray", "30–35 araw sa 104-hole tray gamit ang 1:1:1 pinong lupa, vermicast at CRH (Carbonized Rice Hull)", "Yugto ng Punla", Color(0xFF2E7D32), R.drawable.ph_eggplant_seedlings),
            CarouselSlide("Lilang Bulaklak at Tulos", "Paglabas ng matingkad na lilang bulaklak na may dilaw na anther; paglalagay ng 1.5m tulos na kawayan", "Pamumulaklak", Color(0xFF7B1FA2), R.drawable.ph_eggplant_flowers),
            CarouselSlide("Masaganang Bunga sa Halaman", "Kumpol ng mahahaba, matitigas, at makikintab na lilang talong (Fortuner F1) sa mayabong na puno", "Pamumunga", Color(0xFF4A148C), R.drawable.ph_eggplant_hero),
            CarouselSlide("Ani sa Makintab na Yugto", "Pinitas gamit ang gunting na may 2–3 cm tangkay sa katutubong bilao; sariwa at makintab bago magbutil", "Handa nang Anihin", Color(0xFF1B5E20), R.drawable.ph_eggplant_harvest)
        ),
        quickInfo = listOf(
            QuickInfoItem("spacing", "Spacing", "50–75 cm", null, false, ExplanationData("Plant Spacing sa Talong", "DENSIDAD SA TANIMAN", "Sa tradisyunal na kama o tudling sa Pilipinas, 50–75 cm ang pagitan ng bawat puno (hills) at 90–120 cm ang pagitan ng tudling (furrows). Malaki at malabay ang puno ng talong kapag nasa tugatog ng pamumunga.", listOf("Pagitan ng Puno" to "50–75 cm (hills)", "Pagitan ng Tudling" to "90–120 cm (furrows)", "Urban Containers" to "1 puno bawat 5-gallon na timba"), "Ang sapat na distansya ay nagpapadali sa pamimitas at nagbibigay ng maayos na daloy ng hangin laban sa amag at pests.")),
            QuickInfoItem("depth", "Depth", "0.5 – 1 cm", null, false, ExplanationData("Lalim ng Pagpupunla", "PUNLAAN", "Ipunla ang binhi sa lalim na 0.5 hanggang 1 sentimetro sa seedling tray gamit ang pinaghalong compost, garden soil, at Carbonized Rice Hull (CRH).", listOf("Lalim ng Binhi" to "0.5–1.0 cm", "Media" to "1:1:1 Soil, Vermicast, at CRH"), "Dahan-dahang diligan gamit ang sprayer tuwing umaga upang hindi maanod ang binhi.")),
            QuickInfoItem("sun", "Sun", "Full Sun", null, false, ExplanationData("Sikat ng Araw", "SINA-AG NG ARAW", "Kailangan ng talong ng 6 hanggang 8 oras ng direktang sikat ng araw buong maghapon upang lumalim ang lilang kulay ng balat at dumami ang bulaklak.", listOf("Minimum na Araw" to "6 na oras", "Pinakamainam" to "8+ oras buong araw"), "Maaaring maglagay ng 30% netting sa matinding tag-init (>35°C) kapag madalas malagas ang bulaklak sa tanghali.")),
            QuickInfoItem("water", "Water", "1.5–2 L/araw", "ⓘ", false, ExplanationData("Pagdidilig sa Talong", "IRIGASYON", "Malalaki at malalapad ang dahon ng talong kaya malakas itong magsingaw ng tubig. Magdilig ng 1.5 hanggang 2 litro bawat puno 2–3 beses sa isang linggo sa umaga.", listOf("Dalas ng Pagdidilig" to "Bawat 2–3 araw sa umaga", "Paraan" to "Direkta sa ugat (Drip o flood furrow)"), "Iwasang mabasa ang mga bulaklak upang hindi mahugasan ang polen at maiwasan ang fungal blight.")),
            QuickInfoItem("season", "Season", "Buong Taon (All Year)", "ⓘ", false, ExplanationData("Panahon ng Pagtatanim", "KLIMA AT PANAHON", "Maaaring itanim ang talong sa buong taon sa Pilipinas. Ang pinakamainam na panahon sa kapatagan ay Oktubre hanggang Marso upang makaiwas sa malalakas na habagat at bagyo.", listOf("Pangunahing Panahon" to "Oktubre hanggang Marso", "Off-Season / Tag-ulan" to "Abril hanggang Hulyo (May mataas na kamada)"), "Tuwing tag-ulan, gumawa ng matataas na drainage canal upang maiwasan ang pagkabulok ng ugat.")),
            QuickInfoItem("frost", "Flooding", "Di-bahaing Lupa", "!", true, ExplanationData("Sensitibo sa Baha", "KATATAGAN SA TUBIG", "Sensitibo ang talong sa matagal na pagkababad sa baha. Ang pagkababad sa tubig sa loob ng 24–48 oras ay nagiging sanhi ng mabilis na pag-atake ng Bacterial Wilt (Ralstonia).", listOf("Baha / Waterlogging" to "Hindi tatagal nang 24 oras", "Lunas" to "20–30 cm raised beds (mataas na kamada)"), "Maghukay ng malalalim na kanal sa paligid upang mabilis umagos ang tubig-ulan.")),
            QuickInfoItem("height", "Height", "1.0 – 1.5 m", "I", false, ExplanationData("Taas ng Puno at Tulos", "SUKAT AT TULOS", "Umaabot sa 1 hanggang 1.5 metro ang taas ng talong. Dahil mabigat ang sunod-sunod na bungang lumalabas, kailangan itong lagyan ng tulos na kawayan.", listOf("Taas ng Puno" to "1.0 hanggang 1.5 metro", "Tulos" to "1.5m patpat na kawayan"), "Magtirik ng tulos 2–3 linggo pagkalipat-tanim at talian gamit ang maluwag na Figure-8 tie.")),
            QuickInfoItem("germ", "Germination", "7–14 araw", null, false, ExplanationData("Pagsibol ng Binhi", "PAGSIBOL", "Medyo makunat ang balat ng binhi ng talong kumpara sa kamatis; sumisibol ito sa loob ng 7 hanggang 14 na araw sa mainit at mamasa-masang punlaan.", listOf("Panahon ng Pagsibol" to "7 hanggang 14 araw", "Tips" to "Ibabad sa maligamgam na tubig ng 6 na oras bago itanim"), "Ilantad agad sa banayad na sikat ng araw sa umaga pagkalabas ng unang dahon.")),
            QuickInfoItem("germ_temp", "Germination Temp", "24°–32°C", null, false, ExplanationData("Temperatura ng Pagsibol", "TEMPERATURA", "Ang normal na temperatura sa Pilipinas (24°C–32°C) ay napakainam para sa pantay na pagsibol ng talong.", listOf("Mainam na Temp" to "24°C hanggang 32°C", "Klima" to "Warm Tropical Lowland"), "Huwag hayaang matuyo ang ibabaw ng seed tray habang hindi pa sumisibol.")),
            QuickInfoItem("harvest", "Sprout to Harvest", "75–85 DAP", null, false, ExplanationData("Araw Bago Mag-ani", "KABUOANG SIKLO", "Mula sa paglipat-tanim (DAP), magsisimulang mamitas ng bunga sa loob ng 75 hanggang 85 araw. Nagpapatuloy ang ani tuwing ika-4 hanggang ika-6 na araw sa loob ng 6–9 na buwan.", listOf("Mula Lipat-Tanim" to "75–85 araw (DAP)", "Dalas ng Ani" to "Tuwing 4–6 na araw"), "Pitasin habang makintab pa ang balat at hindi pa naninilaw o tumitigas ang mga buto sa loob.")),
            QuickInfoItem("ph", "Soil pH", "5.5 – 6.8", "PH", false, ExplanationData("Asido ng Lupa", "KONDISYON NG LUPA", "Nabubuhay ang talong sa bahagyang maasim hanggang neutral na lupa (pH 5.5–6.8). Mas matatag ito sa bahagyang asidong lupa kumpara sa kamatis.", listOf("Tamang pH" to "5.5 hanggang 6.8", "Paggamot" to "Agricultural lime kung pH < 5.2"), "Ang balanseng pH ay nagbibigay-daan sa ugat upang mahigop ang potassium para sa pagpapataba ng bunga.")),
            QuickInfoItem("seedling", "Seedling ID", "3–4 True Leaves", null, false, ExplanationData("Pagkilala sa Punla", "PAGLILIPAT-TANIM", "Handa nang ilipat-tanim ang punla ng talong kapag mayroon na itong 3 hanggang 4 na tunay na dahon, humigit-kumulang 30 hanggang 35 araw mula sa punlaan.", listOf("Dahon" to "3–4 tunay na dahon", "Taas" to "12–15 cm na may matigas na tangkay"), "Sanayin sa buong araw (hardening) sa loob ng 5–7 araw bago tuluyang itanim sa bukid."))
        ),
        companionPlants = listOf(
            PlantRelationItem("Sulasi (Sweet Basil)", "Ocimum basilicum", true, "Natural na panaboy sa thrips, flea beetles, at fruit borer", ExplanationData("Sulasi / Sweet Basil", "HALAMANG KASAMA", "Ang taglay nitong linalool at eugenol ay lumilito sa pang-amoy ng mga gamugamo ng fruit and shoot borer habang itinataboy ang flea beetles.", listOf("Pakinabang" to "Natural repellent laban sa borers", "Peste" to "Fruit borer, thrips, aphids"), "Magtanim ng sulasi sa pagitan ng bawat dalawang puno ng talong.")),
            PlantRelationItem("Amarilyo (Marigold)", "Tagetes patula", true, "Nematicide sa ugat at panangga sa puting langaw", ExplanationData("Amarilyo / French Marigold", "HALAMANG KASAMA", "Naglalabas ang ugat ng natural na kemikal na pumapatay sa root-knot nematodes at nagtataboy sa puting langaw (whiteflies).", listOf("Pakinabang" to "Pamuksa sa bulate sa lupa", "Peste" to "Root-knot nematodes, whiteflies"), "Itanim sa dulo at gilid ng bawat plot.")),
            PlantRelationItem("Sibuyas / Green Onion", "Allium fistulosum", true, "Matapang na amoy panlaban sa spider mites at amag", ExplanationData("Sibuyas / Green Onion", "HALAMANG KASAMA", "Ang sulfur compounds ng sibuyas ay nagtataboy sa mga sumisipsip na hanip at pumipigil sa pamumuo ng fungal pathogens sa lupa.", listOf("Pakinabang" to "Fungicidal barrier at mite deterrent", "Peste" to "Red spider mites, aphids"), "Itanim sa gilid ng kama ng talong.")),
            PlantRelationItem("Sitaw / Bush Beans", "Phaseolus vulgaris", true, "Nagpapataba ng lupa sa pamamagitan ng nitrogen fixation", ExplanationData("Sitaw / Bush Beans", "HALAMANG KASAMA", "Nagsasalin ng natural na nitrogen sa lupa sa pamamagitan ng rhizobia bacteria sa ugat na labis na kailangan ng matatakaw na puno ng talong.", listOf("Pakinabang" to "Natural nitrogen fixer", "Sustansya" to "Dagdag nitrogen sa root zone"), "Itanim sa pagitan ng mga hanay ng talong.")),
            PlantRelationItem("Tanglad (Lemongrass)", "Cymbopogon citratus", true, "Citronella aroma na panaboy sa lumilipad na gamugamo", ExplanationData("Tanglad", "HALAMANG KASAMA", "Ang malakas na amoy ng citronella ay nagtataboy sa mga paruparo at gamugamo na nangingitlog ng harabas sa mga dahon ng talong.", listOf("Pakinabang" to "Moth repellent barrier", "Peste" to "Adult shoot borer moths"), "Itanim bilang panangga sa hangin sa paligid ng taniman.")),
            PlantRelationItem("Oregano", "Coleus aromaticus", true, "Mabangong groundcover laban sa gumagapang na peste", ExplanationData("Oregano", "HALAMANG KASAMA", "Makapal na herbal groundcover na nagtataboy sa mga armyworm at uwang gamit ang carvacrol essential oils nito.", listOf("Pakinabang" to "Aromatic ground defense", "Peste" to "Cutworm, armyworm"), "Itanim sa paanan o dulo ng kamada.")),
            PlantRelationItem("Pechay", "Brassica rapa", true, "Living trap crop na umaakit sa flea beetles palayo sa talong", ExplanationData("Pechay", "HALAMANG KASAMA", "Nagsisilbing alay na pananim (trap crop) sa mga lumuluksong flea beetle habang maliit pa ang punla ng talong.", listOf("Pakinabang" to "Trap crop at mabilisang ani", "Siklo" to "Maaani sa loob ng 30 araw"), "Ipunla sa paligid ng plot bilang pain sa mga insekto.")),
            PlantRelationItem("Nasturtium", "Tropaeolum majus", true, "Umaakit sa aphids palayo sa talong", ExplanationData("Nasturtium", "HALAMANG KASAMA", "Inaakit ang mga aphids at puting langaw sa sarili nitong dahon upang hindi lapitan ang talong.", listOf("Pakinabang" to "Sacrificial trap crop", "Peste" to "Aphids, whiteflies"), "Itanim 1 metro ang layo sa puno ng talong."))
        ),
        combativePlants = listOf(
            PlantRelationItem("Mais (Corn)", "Zea mays", false, "Pinagmumulan ng Helicoverpa borer at humaharang sa sikat ng araw", ExplanationData("Mais (Incompatible)", "DI-DAPAT KATABI", "Ang corn borer at corn earworm ay umaatake rin sa bunga ng talong. Tinatakpan din ng matatangkad na mais ang sikat ng araw.", listOf("Peligro" to "Borer crossover at matinding lilim", "Panuntunan" to "Ihiwalay ng 5–10 metro"), "Magtanim ng mais sa malayong bahagi ng bukid.")),
            PlantRelationItem("Patatas (Potato)", "Solanum tuberosum", false, "Magkapamilyang nagpapasalin ng late blight at viral mosaic", ExplanationData("Patatas (Incompatible)", "DI-DAPAT KATABI", "Parehong miyembro ng Solanaceae na madaling kapitan ng magkaparehong fungal pathogens at nakamamatay na late blight.", listOf("Peligro" to "Foliar blight cross-infection", "Resulta" to "Mabilis na pagkasunog ng dahon"), "Iwasang pagtabihin sa iisang kama o bakuran.")),
            PlantRelationItem("Fennel", "Foeniculum vulgare", false, "Naglalabas ng kemikal na nakakapigil sa paglaki ng ugat ng talong", ExplanationData("Fennel (Incompatible)", "DI-DAPAT KATABI", "Ang ugat ng fennel ay naglalabas ng allelopathic toxin na nagpapatigil sa paglaki ng ugat ng talong at nagdudulot ng pagkabansot.", listOf("Peligro" to "Allelopathic root inhibition", "Resulta" to "Bansot at walang bulaklak"), "Itanim lamang ang fennel sa nakahiwalay na paso.")),
            PlantRelationItem("Kalabasa / Squash", "Cucurbita moschata", false, "Malalapad na gumagapang na baging na sumasakal sa puno ng talong", ExplanationData("Kalabasa (Incompatible)", "DI-DAPAT KATABI", "Mabilis gumapang ang baging ng kalabasa at tinatakpan ang base ng talong, na nagiging sanhi ng kulob na hangin at pamumuo ng amag.", listOf("Peligro" to "Light & space competition", "Amag" to "Mahalumigmig na base na umaakit ng snails"), "Maglagay ng hiwalay na trellis o balag para sa kalabasa.")),
            PlantRelationItem("Repolyo (Cabbage)", "Brassica oleracea", false, "Matinding kaagaw sa sustansya (nitrogen) at tubig", ExplanationData("Repolyo (Incompatible)", "DI-DAPAT KATABI", "Umuubos ng calcium at nitrogen sa lupa na nagiging sanhi ng mabagal na pamumulaklak ng talong.", listOf("Peligro" to "Nutrient depletion", "Lupa" to "Matinding agawan sa pataba"), "Itanim sa magkahiwalay na plots."))
        ),
        nutritionBadges = listOf(
            NutritionBadgeItem("NAS", "Nasunin", "Napakataas", "Malakas na antioxidant sa lilang balat para sa utak at puso", ExplanationData("Nasunin (Anthocyanin)", "PHYTONUTRIENT", "Ang matingkad na lilang kulay ng talong ay nagmumula sa nasunin, isang uri ng anthocyanin na pumipigil sa pagkasira ng brain cell membranes at nagpapababa ng pamamaga sa ugat ng dugo.", listOf("Saan Matatagpuan" to "Sa mismong lilang balat ng talong", "Gamit" to "Proteksyon sa utak at cardiovascular system"))),
            NutritionBadgeItem("FIB", "Dietary Fiber", "12% DV", "Pampababa ng kolesterol at pampaganda ng panunaw", ExplanationData("Dietary Fiber", "NUTRISYON", "Mayaman sa natutunaw at hindi natutunaw na hibla na tumutulong sa maayos na pagdumi at nagpapanatili ng malusog na asukal sa dugo.", listOf("Dami bawat 100g" to "3.0 gramo (12% DV)", "Gamit" to "Kalusugan ng bituka at puso"))),
            NutritionBadgeItem("K", "Potassium", "5% DV", "Pang-kontrol sa presyon ng dugo at tibok ng puso", ExplanationData("Potassium", "NUTRISYON", "Tumutulong sa pagpapaluwag ng mga ugat at pagbalanse ng sodium sa katawan upang mapanatili ang normal na presyon ng dugo.", listOf("Dami bawat 100g" to "229 mg", "Gamit" to "Electrolyte balance"))),
            NutritionBadgeItem("B9", "Folate (B9)", "6% DV", "Para sa paggawa ng pulang selula sa dugo", ExplanationData("Bitamina B9 (Folate)", "NUTRISYON", "Mahalaga sa cellular growth, pagbuo ng hemoglobin, at kalusugan ng mga nagdadalang-tao.", listOf("Dami bawat 100g" to "22 mcg", "Gamit" to "Red blood cell production"))),
            NutritionBadgeItem("MN", "Manganese", "11% DV", "Para sa tibay ng buto at metabolism", ExplanationData("Manganese", "MINERAL", "Mahalagang co-factor ng mga enzymes para sa pagproseso ng carbohydrates at kalusugan ng buto.", listOf("Dami bawat 100g" to "0.23 mg (11% DV)", "Gamit" to "Enzyme activation at bone strength")))
        ),
        pests = listOf(
            PestDiseaseItem("Harabas sa Talbos at Bunga (Fruit and Shoot Borer)", "Leucinodes orbonalis", "Nakamamatay", "Nalalantang dulo ng talbos na parang naputol; may itim na butas ang bunga na may dumi sa loob", "Gupitin agad ang lantang talbos 5cm sa ilalim ng butas; mag-spray ng Bt o neem", "Spray ng Chili-Garlic-Neem extract tuwing alas-4:30 ng hapon; pheromone traps", ExplanationData("Eggplant Fruit & Shoot Borer — #1 Peste sa Pilipinas", "PESTE SA TALBOS AT BUNGA", "Ang Leucinodes orbonalis ang pinakamapaminsalang peste ng talong sa Pilipinas. Bumabarena ang uod sa malalambot na talbos kaya yumuyukod at nalalanta, bago lumipat sa bunga.", listOf("Sintomas" to "Yumuyukod na talbos sa umaga; butas sa bunga na may frass", "Natural Control" to "Pagtatabas ng lantang talbos linggu-linggo; Sex pheromone traps"), "Pitasin at ibaon sa lupa (30cm lalim) ang lahat ng apektadong talbos at bunga upang maputol ang siklo ng uod.")),
            PestDiseaseItem("Luksong Kuto / Leafhopper", "Amrasca biguttula", "Mataas", "Naninilaw, namumula, at natutuyo ang gilid ng dahon (hopper burn); kulot na dahon", "Dilaw na sticky traps; magtanim ng kasamang sulasi at marigold; panatilihing basa ang lupa", "Mag-spray ng diluted neem oil solution na may mild soap tuwing umaga", ExplanationData("Cotton Leafhopper sa Talong", "SUMISIPSIP NA INSEKTO", "Napakaliliit na luntiang insektong lumulukso sa ilalim ng dahon. Ang kanilang lason kapag sumipsip ng katas ay nagdudulot ng 'hopper burn' kung saan natutuyo ang gilid ng dahon na parang nasunog.", listOf("Sintomas" to "V-shaped na paninilaw at pagkatuyo sa gilid ng dahon", "Lunas" to "Dilaw na sticky trap kada 5 metro at neem spray"), "Huwag maglagay ng labis na nitrogen dahil nagiging paborito ng leafhoppers ang malalambot na dahon.")),
            PestDiseaseItem("Talon-talon (Flea Beetle)", "Phyllotreta spp.", "Katamtaman", "Tadtad ng maliliit na pabilog na butas (shot-holes) ang dahon ng bagong lipat na punla", "Gamitin ang pechay bilang trap crop; maglagay ng pinong insect netting sa punlaan", "Budburan ng manipis na kahoy na abo (wood ash) ang mga dahon sa umaga", ExplanationData("Flea Beetle Management", "LUMULUKSONG UWANG", "Maliliit at makintab na itim na uwang na lumulukso kapag nilapitan. Madalas sumalakay sa unang 3 linggo pagkalipat-tanim.", listOf("Sintomas" to "Parang tinadtad ng bala ng baril ang mga dahon", "Organikong Gamot" to "Kahoy na abo o diatomaceous earth"), "Kapag lumaki na ang puno ng talong, madali na nitong malalampasan ang pinsala ng flea beetle.")),
            PestDiseaseItem("Pulang Hanip (Spider Mites)", "Tetranychus urticae", "Mataas", "Maliit na mapuputlang tuldok sa ibabaw ng dahon; may pinong sapot sa ilalim", "Huwag hayaang matuyuan at maalikabukan ang taniman; maglagay ng rice straw mulch", "Spray ng sulfur o banayad na sabong Perla sa ilalim ng dahon sa hapon", ExplanationData("Spider Mites sa Tag-Init", "HANIP SA ILALIM NG DAHON", "Mabilis dumami kapag mainit, tuyo, at maalikabok ang panahon sa pagitan ng Pebrero at Mayo. Pinuputol ang daloy ng photosynthesis kaya nalalagas ang dahon.", listOf("Sintomas" to "Pinong sapot sa ilalim at naninilaw na parang tanso ang dahon", "Trigger" to "Matinding tagtuyot at tuyong lupa"), "Diligan nang banayad ang ilalim ng mga dahon upang mapatid ang kanilang mga sapot.")),
            PestDiseaseItem("Puting Langaw (Whitefly)", "Bemisia tabaci", "Katamtaman", "Maliliit na puting langaw sa ilalim ng dahon na nagdudulot ng maitim na sooty mold", "Maglagay ng dilaw na sticky traps; gumamit ng silver-black plastic mulch", "Spray ng neem oil extract o insecticidal soap", ExplanationData("Whitefly Control", "TAGAPAGDALA NG DUMI AT SAKIT", "Sumisipsip ng katas sa ilalim ng dahon at naglalabas ng malagkit na dumi (honeydew) na tinutubuan ng maitim na amag.", listOf("Sintomas" to "Puting insekto kapag inalog ang sanga; maitim na dumi sa dahon", "Pang-akit" to "Kulay dilaw na sticky paper"), "Mabisa ang silver-black plastic mulch dahil nasisilaw ang mga lumilipad na langaw.")),
            PestDiseaseItem("Bulate sa Ugat (Nematodes)", "Meloidogyne incognita", "Malubha", "Bansot na halaman na madaling malanta sa tanghali; may bukol-bukol na galls sa ugat", "Magtanim ng maraming amarilyo (marigold); ihalo ang CRH at vermicompost sa lupa", "Maglagay ng Trichoderma at Paecilomyces sa root zone", ExplanationData("Root-Knot Nematodes sa Talong", "SALOT SA UGAT", "Mikroskopikong bulate sa buhanging lupa na bumubutas at nagpapabukol sa ugat kaya hindi makasipsip ng tubig at pataba.", listOf("Sintomas" to "Lanta sa tanghali kahit basa ang lupa; bukol sa ugat", "Panlaban" to "French Marigold intercropping"), "Ang ugat ng amarilyo ay natural na lumilinis sa lupa laban sa nematodes."))
        ),
        diseases = listOf(
            PestDiseaseItem("Lanta-Bakterya (Bacterial Wilt)", "Ralstonia solanacearum", "Nakamamatay", "Biglang pagkalanta ng buong puno sa gitna ng init kahit basa ang lupa; lunti pa ang dahon", "Magtanim ng Morena F1 o Fortuner F1; gumawa ng mataas na kamada; iwasan ang baha", "Walang kemikal na lunas; agad bunutin, ibalot sa plastic at sunugin; huwag i-compost", ExplanationData("Bacterial Wilt sa Talong", "BAKTERYAL NA SAKIT SA LUPA", "Ang Ralstonia solanacearum ang pinakamapanganib na sakit sa talong sa Pilipinas. Pumapasok sa sugat ng ugat at bumabara sa daluyan ng tubig.", listOf("Sintomas" to "Biglang lanta sa umaga kahit basa ang lupa; may lumalabas na puting gatas sa tangkay sa baso ng tubig (bacterial streaming)", "Pag-iwas" to "Crop rotation na may palay o mais; paglalagay ng apog sa hukay"), "Huwag kailanman magtatanim muli ng talong o kamatis sa lupang tinamaan nang walang 2 taong pahinga.")),
            PestDiseaseItem("Phomopsis Blight & Fruit Rot", "Phomopsis vexans", "Mataas", "Pabilog na kulay-tsokolateng mantsa sa dahon; lulubog at mabubulok na malambot na batik sa bunga", "Alisin ang mga dahon sa ibaba (bottom 30cm); maglagay ng mulch; magtulos", "Mag-spray ng copper fungicide o baking soda solution tuwing maulan", ExplanationData("Phomopsis Fruit Rot sa Talong", "FUNGAL PATHOGEN", "Umaatake sa dahon at bunga tuwing tag-ulan. Ang mga natalsikang bunga mula sa basang lupa ay mabilis mabulok at maging kulubot na mummy fruit.", listOf("Sintomas" to "Lulubog na bilog sa balat ng talong na may maliliit na itim na tuldok", "Lunas" to "Pagtutulos upang nakabitin ang bunga at hindi lumapat sa lupa"), "Pitasin at sunugin agad ang mga apektadong bunga upang hindi kumalat ang spores.")),
            PestDiseaseItem("Pagkatuyo ng Punla (Damping-Off)", "Pythium & Rhizoctonia", "Malubha", "Lumalambot at nabubulok ang base ng punla sa seed tray; bumubulagta sa lupa", "Gumamit ng sterile soil media na may CRH; huwag pasobrahan ang dilig sa hapon", "Ihalo ang Trichoderma harzianum sa punlaan; ilantad sa sikat ng araw sa umaga", ExplanationData("Damping-Off sa Punlaan", "SAKIT SA PUNLA", "Fungal disease na pumapatay sa malalambot na tangkay ng punla kapag madilim at labis ang tubig sa seed tray.", listOf("Sintomas" to "Lulubog at mabubulok ang tangkay sa pantay ng lupa sa magdamag", "Solusyon" to "Maglagay ng Carbonized Rice Hull para sa magandang drainage"), "Magdilig lamang sa umaga upang matuyo ang ibabaw ng seed tray bago sumapit ang gabi.")),
            PestDiseaseItem("Anthracnose sa Bunga", "Colletotrichum gloeosporioides", "Katamtaman", "Lulubog na pabilog na mantsa sa matandang bunga na may mamula-mulang spores sa gitna", "Pitasin habang makintab at bata pa; huwag hayaang lumapat sa lupa; mag-mulch", "Mag-spray ng bio-fungicide o fermented plant juice na may sabon", ExplanationData("Anthracnose sa Talong", "FUNGAL SPORES", "Umaatake sa mga matatandang bunga, lalo na kapag lumapat sa basang lupa o nasugatan ng insekto.", listOf("Sintomas" to "Lulubog na parang bulutong sa balat ng bunga", "Lunas" to "Maagang pamimitas bago labis na tumanda ang bunga"), "Panatilihing nakatali sa tulos ang mga sanga.")),
            PestDiseaseItem("Munting Dahon (Little Leaf Disease)", "Phytoplasma", "Malubha", "Nagiging napakaliliit at kumpol-kumpol na parang walis ang mga dahon; walang nabubuong bulaklak", "Puksain ang leafhoppers (luksong kuto) na nagkakalat ng sakit; bunutin ang apektadong puno", "Walang lunas kapag nahawa na; alisin agad upang hindi kumalat sa katabing puno", ExplanationData("Little Leaf Disease sa Talong", "PHYTOPLASMA INFECTION", "Dinadala ng luksong kuto (leafhopper). Ang apektadong puno ay naglalabas ng napakaraming bansot at maliliit na dahon na walang kakayahang mamunga.", listOf("Sintomas" to "Bansot na sangang may kumpol ng munting dahon (witches' broom)", "Panlaban" to "Pagsugpo sa leafhoppers gamit ang neem oil at yellow traps"), "Bunutin agad ang puno sa unang senyales ng little leaf upang hindi mahawa ang buong taniman."))
        ),
        beneficialCritters = listOf(
            CritterItem("Kiwot / Stingless Bees (Tetragonula biroi)", "Pangunahing tagapagsabog ng polen (pollinator) na nagpapalaki sa bunga ng talong", "Bulaklak ng talong, sulasi, cosmos, at marigold", ExplanationData("Kiwot (Stingless Bee ng Pilipinas)", "POLLINATOR", "Katutubong bubuyog na napakahusay magsagawa ng 'buzz pollination' sa mga bulaklak ng talong, na nagpapataas ng ani ng hanggang 35%.", listOf("Tungkulin" to "Buzz Pollination sa bulaklak ng talong", "Pakinabang" to "Mas malalaki at pantay ang hugis ng bunga"))),
            CritterItem("Trichogramma Wasps (Trichogramma chilonis)", "Likas na parasitoid na lumalamon at pumapatay sa itlog ng fruit and shoot borer", "Dahon ng talong at taniman ng mais", ExplanationData("Trichogramma Parasitic Wasps", "BIOLOGICAL CONTROL", "Napakaliliit na kapaki-pakinabang na putakti na nangingitlog sa loob ng itlog ng shoot borer, kaya hindi na nakakapisa ang mapaminsalang uod.", listOf("Target" to "Itlog ng Fruit and Shoot Borer", "Pakinabang" to "Likas na proteksyon nang walang kemikal"))),
            CritterItem("Mandurukot (Assassin Bug)", "Mabangis na mangangaso na sumisipsip sa mga uod ng shoot borer at tipaklong", "Makakapal na sanga ng talong, damong-gamot, at mulch", ExplanationData("Mandurukot (Assassin Bug)", "PREDATOR SA SANGGA", "May matulis na tuka na ibinabaon sa katawan ng mga uod upang ubusin ang laman-loob nito.", listOf("Biktima" to "Uod sa talbos, cutworm, flea beetles", "Tirahan" to "Ilalim ng malalagong dahon"))),
            CritterItem("Hoverfly (Syrphid Fly)", "Ang munting uod nito ay lumalamon sa daan-daang leafhoppers at aphids araw-araw", "Bulaklak ng marigold, sulasi, at dill", ExplanationData("Hoverfly sa Talong", "PREDATOR AT POLLINATOR", "Ang matandang hoverfly ay pollinator; ang uod naman nito ay lumalamon sa mga leafhopper at kuto ng halaman.", listOf("Pagkain ng Uod" to "Luksong kuto (leafhoppers), aphids, thrips", "Pakinabang" to "Pangontra sa hopper burn"))),
            CritterItem("Simbahan (Praying Mantis)", "Nananambang at lumalamon sa mga gamugamo bago makapangitlog ng harabas", "Mataas na sanga ng talong at tulos na kawayan", ExplanationData("Simbahan (Praying Mantis)", "GENERAL PREDATOR", "Mahusay magkubli sa mga dahon upang daklutin ang mga lumilipad na gamugamo ng shoot borer.", listOf("Biktima" to "Gamugamo (moths), tipaklong, uwang", "Paraan" to "Matiyagang pananambang")))
        ),
        growingGuides = listOf(
            GuideSectionItem(
                id = "punlaan",
                title = "1. Pagpupunla at Pangangalaga (Seedbed & Trays)",
                summary = "Paghahanda ng 104-hole trays, 1:1:1 media na may Carbonized Rice Hull (CRH), at 30–35 araw na pag-aalaga.",
                fullContent = "Hindi isinasabog nang direkta sa bukid ang binhi ng talong; kailangan itong ipunla sa plastic seedling tray (104 holes) upang masiguro ang 90%+ germination at malalakas na ugat.\n\nMedia Mix: Paghaluin ang 1 bahagi ng pinong lupang-hardin, 1 bahagi ng vermicast o pinatuyong dumi ng baka, at 1 bahagi ng Carbonized Rice Hull (CRH). Ang CRH ay nagpapanatili ng buhaghag na drainage upang hindi magkaroon ng damping-off.\n\nIpunla ang 1 binhi bawat butas sa lalim na 0.5–1 cm. Takpan ng basang dyaryo o sako sa unang 4 na araw. Pagkasibol sa ika-7 hanggang ika-14 na araw, ilagay sa punlaang may 50% net shade. Sa ika-25 hanggang ika-35 araw, gawin ang 'hardening' (bawas dilig at buong araw) bago ilipat sa bukid.",
                tips = listOf("Ibabad ang binhi sa maligamgam na tubig ng 4–6 na oras bago itanim para sa mabilis na pagsibol.", "Magdilig lamang sa umaga upang maiwasan ang damping-off.", "Gawin ang 'hardening' 7 araw bago maglipat-tanim.")
            ),
            GuideSectionItem(
                id = "pagtatanim",
                title = "2. Paghahanda ng Lupa at Lipat-Tanim (Land Prep & Transplanting)",
                summary = "Mataas na kamada (20–30 cm), silver-black plastic mulch, 50–75 cm spacing, at pagtatanim sa hapon.",
                fullContent = "Araruhin at suyurin ang lupa ng 2–3 beses hanggang maging pinong-pino. Gumawa ng matataas na kamada na may taas na 20–30 cm at lapad na 1 metro upang mabilis umagos ang tubig-ulan at maiwasan ang Bacterial Wilt.\n\nSilver-Black Plastic Mulch: Mas mainam maglagay ng plastic mulch. Ang kulay-pilak sa ibabaw ay sumisilaw at nagtataboy sa leafhoppers, aphids, at thrips, habang pinipigilan ang damo at pinapanatili ang halumigmig ng lupa.\n\nLipat-Tanim: Ilipat ang mga punla (30–35 araw mula punla) sa bandang alas-4 ng hapon (4:00 PM) kapag lumamig na ang hangin. Panatilihin ang 50–75 cm na distansya bawat puno. Ibaon ang punla hanggang sa base ng unang tunay na dahon at diligan agad nang sagana.",
                tips = listOf("Laging maglipat-tanim sa dakong alas-4 ng hapon upang maiwasan ang transplanting shock.", "Maglagay ng basal organic fertilizer at 1 kutsarang Complete (14-14-14) sa ilalim ng bawat hukay.", "Diligan agad pagkatanim gamit ang may halong vermitea.")
            ),
            GuideSectionItem(
                id = "pagpapataba",
                title = "3. Tamang Abono at Pataba (Fertilization Protocol - UPLB & DA)",
                summary = "Kombinasyon ng Complete (14-14-14), Urea, Muriate of Potash, at Organikong Concoctions (FPJ, FFJ, CalPhos).",
                fullContent = "Ang talong ay matagal mabuhay (6–9 na buwan) kaya't nangangailangan ito ng tuloy-tuloy at balanseng nutrisyon:\n\nBasal Application (Sa Pagtatanim): 1 kutsarang Complete (14-14-14) at 1 dakot ng vermicompost sa bawat butas bago itanim ang punla.\n\nSidedress 1 (14–21 Araw pagkatanim): Maglagay ng 1 kutsaritang Urea (46-0-0) o 16-20-0 na may layong 5 cm mula sa puno upang mapabilis ang paglaki ng mga sanga at dahon. Maaaring mag-spray ng Fermented Plant Juice (FPJ) tuwing ika-7 araw.\n\nSidedress 2 (Pamumulaklak at Pamumunga - 45–60 Araw): Maglagay ng Complete (14-14-14) kasama ng Muriate of Potash (0-0-60) tuwing ika-14 na araw upang maging makikintab, matitigas, at mabibigat ang mga bunga. Mag-drench ng Fermented Fruit Juice (FFJ) tuwing ika-7 araw para sa natural na tamis at tibay sa transportasyon.",
                tips = listOf("Itigil ang mataas na nitrogen kapag namumulaklak na upang hindi maging puro dahon.", "Maglagay ng abo ng kahoy o Potash sa yugto ng pamumunga para sa makintab na lilang balat.", "Diligan agad pagkatapos maglagay ng pataba upang hindi masunog ang ugat.")
            ),
            GuideSectionItem(
                id = "balag",
                title = "4. Pagtutulos at Ratooning (Trellising & Ratoon Cropping)",
                summary = "Kawayan na tulos (1.5m), Figure-8 tie, pagtatabas ng lantang talbos, at Ratooning para sa pangalawang ani.",
                fullContent = "Pagtutulos (Staking): Magbaon ng tulos na kawayan (1.5 metro) sa tabi ng bawat puno 2–3 linggo pagkalipat-tanim. Itali ang pangunahing tangkay gamit ang malambot na tali sa pormang figure-8 upang hindi mabuwal kapag bumigat ang mga bunga o umihip ang malakas na hangin.\n\nPagtatabas ng Talbos: Regular na inspeksyunin ang mga talbos. Kapag may nakitang lantang talbos dahil sa uod (shoot borer), agad itong gupitin 5 cm sa ilalim ng pinasukan ng uod at ibaon sa lupa.\n\nRatooning (Pangalawang Siklo ng Ani): Matapos ang 5–7 buwan ng pamumunga kapag tumanda na ang puno, putulin ang tangkay 20–30 cm mula sa ibabaw ng lupa. Maglagay ng bagong pataba at mulch. Magpapasibol ito ng mga bagong masisiglang suwi na mamumunga muli sa loob ng 40 araw nang hindi na nagpupunla!",
                tips = listOf("Magtali sa tulos bago pa maging mabigat ang mga bunga.", "Gawin ang ratooning sa simula ng tag-ulan upang mabilis sumibol ang mga bagong suwi.", "I-disinfect ang lagari o gunting gamit ang 70% alcohol bago mag-ratoon.")
            ),
            GuideSectionItem(
                id = "ani",
                title = "5. Tamang Pamimitas at Post-Harvest (Harvesting & Handling)",
                summary = "Pamimitas sa makintab na yugto bago magbutil, paggamit ng gunting na may 2–3cm tangkay, at tamang pagtatabi.",
                fullContent = "Kailan Aaniin: Pitasin ang talong habang makintab at madilim pa ang lilang balat, at buo na ang laki nito ngunit malambot pa ang mga buto sa loob. Kapag naging mapusyaw o mapusyaw na kayumanggi ang balat, makunat at mapait na ang laman dahil matigas na ang mga buto.\n\nParaan ng Pagpitas: Gumamit ng matalas na gunting o pruner upang gupitin ang tangkay nang may 2 hanggang 3 sentimetrong calyx. Huwag hilahin o pilipitin gamit ang kamay upang hindi mapunit ang balat o mabali ang sanga ng puno.\n\nPagtatabi: Ilagay sa mabababang plastic crates o katutubong bilao na may saping sariwang dahon ng saging. Itabi sa malamig at malilim na lugar (12°C–15°C). Huwag ilagay sa freezer o napakalamig na refrigerator dahil magkakaroon ito ng chilling injury (pangingitim at paglambot ng balat).",
                tips = listOf("Pitasin tuwing ika-4 hanggang ika-6 na araw upang pasiglahin ang halaman na maglabas pa ng mga bagong bulaklak.", "Mag-ani sa maaliwalas na umaga mula alas-7 hanggang alas-9 ng umaga.", "Ingatang masugatan ang balat upang hindi pasukin ng Phomopsis rot sa biyahe.")
            )
        ),
        plantingCalendar = PlantingCalendarData(
            indoorRange = "Okt. 1 – Nob. 15",
            outdoorRange = "Nob. 15 – Ene. 15",
            harvestRange = "Peb. 1 – Ago. 30",
            indoorLabel = "Punlaan (Seedbed)",
            outdoorLabel = "Lipat-Tanim (Field)",
            harvestLabel = "Ani (Harvest)",
            plantTypeNote = "Pangunahing Panahon: Tag-araw (Okt–Mar); Off-season na may mataas na kama (Abr–Hul); Ratoon Cycle (Ago–Okt)",
            monthsSchedule = listOf(
                "Okt" to "Punlaan sa Tray",
                "Nob" to "Lipat-Tanim sa Bukid",
                "Dis" to "Pagtutulos at Unang Bulaklak",
                "Ene" to "Simula ng Ani",
                "Peb" to "Tugatog ng Ani (Peak)",
                "Mar" to "Tugatog ng Ani (Peak)",
                "Abr" to "Tuloy-tuloy na Pamimitas",
                "May" to "Pamimitas / Paghahanda sa Ratoon",
                "Hun" to "Ratoon Pruning (Pag-uupod)",
                "Hul" to "Pagsibol ng Bagong Suwi",
                "Ago" to "Pangalawang Siklo ng Ani (Ratoon)",
                "Set" to "Linis Taniman / Soil Rest"
            )
        ),
        locationData = LocationDifficultyData(
            difficulty = "Madali hanggang Katamtaman (Easy–Moderate)",
            hardinessZone = "Mababang Kapatagan at Katamtamang Burol (0–1,200m ASL)",
            tempRange = "22°C–34°C (Tropikal)",
            sunlight = "Buong Araw (6–8 oras)",
            suitableAlso = "Napakainam sa mainit na kapatagan ng Luzon, Visayas, at Mindanao"
        ),
        soilPrep = SoilPrepData(
            phRange = "5.5–6.8 (Bahagyang Maasim hanggang Neutral)",
            soilTypes = "Buhaghag na Sandy Loam, Clay Loam na may CRH at Vermicompost",
            recommendation = "Maglagay ng 2–3 kilong vermicompost o decomposed chicken manure bawat metro kuwadrado. Ihalo ang Carbonized Rice Hull (CRH) upang lumuwag ang lupa at hindi magtubig ang ugat. Maglagay ng apog (agricultural lime) kung acidic ang lupa (pH < 5.2) 2 linggo bago maglipat-tanim."
        ),
        growthTimeline = listOf(
            TimelineStageItem("Araw 1", "Punlaan", "Ipunla ang binhi sa 104-hole tray gamit ang 1:1:1 lupa, vermicast at CRH.", "Panatilihing mamasa-masa"),
            TimelineStageItem("Araw 7–14", "Pagsibol", "Pagsulpot ng unang dalawang dahon (cotyledon); ilabas sa umagang araw.", "Iwasan ang sobrang dilig sa gabi"),
            TimelineStageItem("Araw 30–35", "Lipat-Tanim", "May 3–4 na tunay na dahon na ang punla; ilipat sa bukid sa dakong alas-4 ng hapon.", "Maglagay ng basal fertilizer"),
            TimelineStageItem("Araw 45–60", "Tulos at Bulaklak", "Magbaon ng 1.5m tulos na kawayan; itali ang puno; alisin ang lantang talbos.", "Inspeksyunin ang shoot borer linggu-linggo"),
            TimelineStageItem("Araw 75–85+", "Masaganang Ani", "Pitasin ang mga bunga habang makintab pa ang lilang balat tuwing ika-4 hanggang ika-6 na araw.", "Pitasin gamit ang gunting na may calyx")
        ),
        howTos = listOf(
            HowToSectionItem(
                title = "Wastong Pagpuksa sa Uod sa Talbos (Shoot Borer Pruning Protocol)",
                steps = listOf(
                    "Suriin ang taniman tuwing umaga sa pagitan ng alas-7 at alas-9 ng umaga kung kailan pinakamadaling makita ang mga yumuyukod at lantang talbos.",
                    "Kapag may nakitang lantang talbos, hanapin ang maliit na butas na pinasukan ng uod sa tangkay.",
                    "Gamit ang matalas at disinfected na gunting, gupitin ang tangkay nang 5 sentimetro (2 pulgada) sa ILALIM ng butas upang masigurong kasamang maputol ang uod sa loob.",
                    "Agad ilagay ang naputol na talbos sa plastic bag at ibaon sa lupa nang 30 cm lalim o sunugin. HUWAG itapon sa paligid ng taniman.",
                    "Mag-spray ng Chili-Garlic Extract o Neem Oil solution sa buong halaman sa dakong alas-4:30 ng hapon upang mapigilan ang mga gamugamo sa pangingitlog."
                ),
                tips = listOf(
                    "Ang lingguhang pagtatabas ng lantang talbos sa unang 2 buwan ay nagpapababa ng pinsala sa bunga ng hanggang 80%.",
                    "I-sanitize ang gunting gamit ang 70% alcohol bago lumipat sa susunod na puno."
                ),
                safety = "Magsuot ng guwantes habang nagtatabas upang hindi mapaso sa natural na dagta o chili spray."
            ),
            HowToSectionItem(
                title = "Paraan ng Ratooning (Pag-uupod para sa Pangalawang Buong Ani)",
                steps = listOf(
                    "Isagawa ang ratooning kapag natapos na ang unang 5–7 buwan ng pamumunga at matanda na ang mga sanga ng puno.",
                    "Gamit ang matalas na pruning saw o matalas na itak, putulin ang pangunahing tangkay nang 20 hanggang 30 sentimetro mula sa ibabaw ng lupa.",
                    "Pahiran ng wood ash (abo ng kahoy) o copper fungicide paste ang pinutol na dulo upang hindi pasukin ng fungal infection.",
                    "Linisin ang damo sa paligid at maghukay ng mababaw na tudling sa paligid ng puno (drip line).",
                    "Maglagay ng 2 dakot ng vermicompost at 1 kutsarang Complete (14-14-14), tabunan ng lupa at diligan nang sagana.",
                    "Sa loob ng 10–14 na araw, maglalabas ito ng 3–5 bagong masisiglang suwi. Pumili ng 2–3 pinakamatitibay na suwi at alisin ang iba upang muling mamunga sa loob ng 40 araw."
                ),
                tips = listOf(
                    "Nakatitipid ang ratooning ng 100% sa gastos sa binhi at nursery care.",
                    "Dahil malalim at matatag na ang ugat ng matandang puno, mas mabilis itong lumaki at mamunga kaysa bagong punla."
                )
            )
        ),
        faqs = listOf(
            FaqItem("Bakit nalalanta ang dulo ng talbos ng aking talong?", "Ito ay malinaw na senyales ng Eggplant Fruit and Shoot Borer (Leucinodes orbonalis). Bumabarena ang uod sa loob ng tangkay kaya napuputol ang daloy ng tubig at sustansya. Agad gupitin ang lantang talbos 5 cm sa ilalim ng butas at ibaon sa lupa."),
            FaqItem("Bakit nagiging mapait at makunat ang aking talong?", "Nagiging mapait at makunat ang talong kapag naantala ang pamimitas at tumigas na ang mga buto sa loob, o kaya'y nakaranas ng matinding tagtuyot ang halaman habang lumalaki ang bunga. Pitasin habang makintab pa ang balat at panatilihing regular ang pagdidilig."),
            FaqItem("Ano ang 'Ratooning' at bakit ito ginagawa sa talong?", "Ang Ratooning ay ang pagputol sa lumang puno ng talong 20–30 cm mula sa lupa matapos ang buwanang ani. Magpapasibol ito ng mga bagong masisiglang sanga na mamumunga muli sa loob ng 40 araw, kaya nakakakuha ang magsasaka ng pangalawang buong ani nang walang panibagong punla."),
            FaqItem("Bakit naninilaw at natutuyo ang gilid ng dahon (parang sunog)?", "Ito ay tinatawag na 'hopper burn' na dulot ng luksong kuto (Cotton Leafhopper o Amrasca biguttula). Sumisipsip sila sa ilalim ng dahon at naglalabas ng lason. Maglagay ng dilaw na sticky traps at mag-spray ng neem oil extract sa ilalim ng dahon.")
        ),
        nutritionTable = NutritionTableData(
            servingSize = "100g (Hilaw / Sariwang Talong)",
            calories = "25 kcal",
            macroNutrients = listOf(
                NutritionRowData("Protina (Protein)", "0.98 g", "2%"),
                NutritionRowData("Taba (Total Fat)", "0.18 g", "0%"),
                NutritionRowData("Carbohydrates", "5.88 g", "2%"),
                NutritionRowData("Dietary Fiber (Hibla)", "3.0 g", "12%"),
                NutritionRowData("Asukal (Sugars)", "3.53 g", "—")
            ),
            microNutrients = listOf(
                NutritionRowData("Potassium (K)", "229 mg", "5%"),
                NutritionRowData("Manganese (Mn)", "0.23 mg", "11%"),
                NutritionRowData("Bitamina C (Ascorbic Acid)", "2.2 mg", "4%"),
                NutritionRowData("Folate (Bitamina B9)", "22 mcg", "6%"),
                NutritionRowData("Bitamina K1", "3.5 mcg", "3%"),
                NutritionRowData("Magnesium (Mg)", "14 mg", "3%"),
                NutritionRowData("Nasunin / Anthocyanin", "20 mg", "Mataas (Antioxidant)")
            )
        ),
        exploreArticles = listOf(
            ArticleItem("Gabay sa Masaganang Talong sa Kapatagan", "Mga teknik ng mga premyadong magsasaka sa Batangas at Nueva Ecija para sa 20–30 tonelada bawat ektarya gamit ang Fortuner F1.", "7 min basahin"),
            ArticleItem("Organikong Pamamahala sa Shoot Borer", "Paano makokontrol ang #1 salot sa talong gamit ang pest monitoring, pruning, at chili-garlic-neem spray nang walang nakalalasong pestisidyo.", "5 min basahin"),
            ArticleItem("Sikreto ng Ratooning sa Talong", "Hakbang-hakbang na paraan upang doblehin ang kita sa pamamagitan ng pag-uupod ng matatandang puno para sa pangalawang ani.", "6 min basahin")
        )
    )

    // ═══════════════════════════════════════════════════════════════════════════════