import urllib.request

urls = {
    "tomato": "https://upload.wikimedia.org/wikipedia/commons/8/89/Tomato_je.jpg",
    "eggplant": "https://upload.wikimedia.org/wikipedia/commons/7/76/Solanum_melongena_24_08_2012_%281%29.JPG",
    "sitaw": "https://upload.wikimedia.org/wikipedia/commons/f/f6/Yard_Long_Bean_Flowers.jpg",
    "carrot": "https://upload.wikimedia.org/wikipedia/commons/a/a2/Papilio_machaon_-_Daucus_carota_-_Keila.jpg",
    "onion": "https://upload.wikimedia.org/wikipedia/commons/2/25/Allium_cepa_viviparum_001.JPG",
    "pumpkin": "https://upload.wikimedia.org/wikipedia/commons/5/5c/Cucurbita_moschata_Butternut_2012_G2.jpg",
    "corn": "https://upload.wikimedia.org/wikipedia/commons/d/d4/Zea_mays_%27morado%27_MHNT.BOT.2015.34.11.jpg",
    "cabbage": "https://upload.wikimedia.org/wikipedia/commons/6/6f/Chou_cabus_blanc_01.jpg",
    "pechay": "https://upload.wikimedia.org/wikipedia/commons/4/48/Pak_Choi_%28Brassica_rapa_subsp._chinensis%29.jpg",
    "ampalaya": "https://upload.wikimedia.org/wikipedia/commons/b/b3/Momordica_charantia_22042014_%282%29.JPG",
    "okra": "https://upload.wikimedia.org/wikipedia/commons/e/e4/Okra_%28Abelmoschus_esculentus%29_Feb_2019._DSC_0060_01.jpg",
    "sili": "https://upload.wikimedia.org/wikipedia/commons/5/5c/Baby_Bell_pepper_%27%27Capsicum_annuum%27%27_.jpg",
    "pipino": "https://upload.wikimedia.org/wikipedia/commons/9/96/Cucumis_sativus_002.jpg",
    "kangkong": "https://upload.wikimedia.org/wikipedia/commons/d/d7/Ipomoea_aquatica_water_spinach.jpg",
    "lettuce": "https://upload.wikimedia.org/wikipedia/commons/d/da/Lactuca_sativa_001.jpg",
    # Pests
    "fruit_borer": "https://upload.wikimedia.org/wikipedia/commons/d/d4/Helicoverpa_armigera_01.jpg",
    "tylcv": "https://upload.wikimedia.org/wikipedia/commons/9/91/Tomato_yellow_leaf_curl_virus_on_tomato.jpg",
    "diamondback": "https://upload.wikimedia.org/wikipedia/commons/6/6f/Plutella_xylostella_moth.jpg",
    "thrips": "https://upload.wikimedia.org/wikipedia/commons/e/e5/Thrips_tabaci.jpg",
    "armyworm": "https://upload.wikimedia.org/wikipedia/commons/2/23/Spodoptera_frugiperda_larva.jpg",
    "powdery_mildew": "https://upload.wikimedia.org/wikipedia/commons/e/e0/Powdery_mildew_on_squash_leaf.jpg",
    "aphids": "https://upload.wikimedia.org/wikipedia/commons/b/b3/Aphis_gossypii_winged_and_wingless.jpg",
    "leafminer": "https://upload.wikimedia.org/wikipedia/commons/8/87/Serpentine_leafminer_damage.jpg",
    "flea_beetle": "https://upload.wikimedia.org/wikipedia/commons/a/a2/Flea_beetle_shot_holes.jpg",
    "bacterial_wilt": "https://upload.wikimedia.org/wikipedia/commons/c/c5/Bacterial_wilt_of_solanaceous_crops.jpg",
    "downy_mildew": "https://upload.wikimedia.org/wikipedia/commons/7/7b/Downy_mildew_cucumber.jpg",
    "anthracnose": "https://upload.wikimedia.org/wikipedia/commons/3/36/Anthracnose_on_chili_pepper.jpg",
    # Soils
    "loam": "https://upload.wikimedia.org/wikipedia/commons/6/6a/Rich_loam_topsoil.jpg",
    "clay": "https://upload.wikimedia.org/wikipedia/commons/2/23/Clay_soil_texture.jpg",
    "sandy": "https://upload.wikimedia.org/wikipedia/commons/5/52/Sandy_agricultural_soil.jpg",
    "silty": "https://upload.wikimedia.org/wikipedia/commons/8/83/Silt_loam_soil_profile.jpg",
    "peaty": "https://upload.wikimedia.org/wikipedia/commons/1/1a/Peat_soil_texture.jpg",
    "chalky": "https://upload.wikimedia.org/wikipedia/commons/d/d1/Chalky_calcareous_soil.jpg"
}

headers = {'User-Agent': 'Mozilla/5.0 (Android Mobile App; MapTanim/1.0)'}

print("Testing direct URLs...")
all_ok = True
for key, url in urls.items():
    try:
        req = urllib.request.Request(url, headers=headers)
        res = urllib.request.urlopen(req)
        print(f"  [OK {res.status}] {key}")
    except Exception as e:
        print(f"  [FAILED] {key}: {e} ({url})")
        all_ok = False

if all_ok:
    print("\nALL URLs ARE WORKING 200 OK!")
