"""
MapTanim Asset Downloader & Processing Pipeline Configuration
Includes full Philippine Priority Vegetable Crop varieties, 5-stage timeline durations,
expected harvest date calculators, and path definitions.
"""

import os
from pathlib import Path

# Base Paths
BASE_DIR = Path(__file__).resolve().parent.parent.parent
ASSETS_DIR = BASE_DIR / "assets"
SPRITES_DIR = ASSETS_DIR / "sprites"
PHOTOS_DIR = ASSETS_DIR / "photos"
METADATA_DIR = ASSETS_DIR / "metadata"
CACHE_DIR = ASSETS_DIR / "cache"
TEMP_DIR = ASSETS_DIR / "temp"

# Mobile Assets Path
MOBILE_ASSETS_DIR = BASE_DIR / "mobile" / "app" / "src" / "main" / "assets"
MOBILE_METADATA_DIR = MOBILE_ASSETS_DIR / "metadata"

# Allowed Creative Commons & Public Domain Licenses
ALLOWED_LICENSES = [
    "CC0",
    "CC0 1.0",
    "CC0 1.0 Universal",
    "Public Domain",
    "CC-BY 4.0",
    "CC-BY 3.0",
    "CC-BY-SA 4.0",
    "CC-BY-SA 3.0",
]

# Image Dimensions & Quality Specifications
MAX_PHOTO_SIZE = (512, 512)
THUMBNAIL_SIZE = (128, 128)
WEBP_QUALITY = 85

# Philippine Priority Vegetable Crops & Variety Matrix
PHILIPPINE_VEGETABLE_CROPS = [
    {
        "id": "carrot",
        "tagalog_name": "Karots",
        "english_name": "Carrot",
        "scientific_name": "Daucus carota",
        "family": "Apiaceae",
        "category": "Root",
        "default_variety": "Terracotta F1",
        "growth_duration_days": 85,
        "ph_priority": True,
    },
    {
        "id": "stringbeans",
        "tagalog_name": "Sitaw",
        "english_name": "String Beans",
        "scientific_name": "Vigna unguiculata subsp. sesquipedalis",
        "family": "Fabaceae",
        "category": "Podded / Legume",
        "default_variety": "Sandigan F1",
        "growth_duration_days": 48,
        "ph_priority": True,
    },
    {
        "id": "eggplant",
        "tagalog_name": "Talong",
        "english_name": "Eggplant",
        "scientific_name": "Solanum melongena",
        "family": "Solanaceae",
        "category": "Solanaceous",
        "default_variety": "Morena F1",
        "growth_duration_days": 75,
        "ph_priority": True,
    },
    {
        "id": "tomato",
        "tagalog_name": "Kamatis",
        "english_name": "Tomato",
        "scientific_name": "Solanum lycopersicum",
        "family": "Solanaceae",
        "category": "Solanaceous",
        "default_variety": "Diamante Max F1",
        "growth_duration_days": 60,
        "ph_priority": True,
    },
    {
        "id": "onion",
        "tagalog_name": "Sibuyas",
        "english_name": "Onion",
        "scientific_name": "Allium cepa",
        "family": "Amaryllidaceae",
        "category": "Bulb",
        "default_variety": "Red Pinoy F1",
        "growth_duration_days": 110,
        "ph_priority": True,
    },
    {
        "id": "pumpkin",
        "tagalog_name": "Kalabasa",
        "english_name": "Pumpkin / Squash",
        "scientific_name": "Cucurbita moschata",
        "family": "Cucurbitaceae",
        "category": "Cucurbit / Vine",
        "default_variety": "Suprema F1",
        "growth_duration_days": 80,
        "ph_priority": True,
    },
    {
        "id": "corn",
        "tagalog_name": "Mais",
        "english_name": "Corn",
        "scientific_name": "Zea mays",
        "family": "Poaceae",
        "category": "Grain / Field",
        "default_variety": "Machismo F1",
        "growth_duration_days": 65,
        "ph_priority": True,
    },
    {
        "id": "cabbage",
        "tagalog_name": "Repolyo",
        "english_name": "Cabbage",
        "scientific_name": "Brassica oleracea var. capitata",
        "family": "Brassicaceae",
        "category": "Leafy / Brassica",
        "default_variety": "K-S Cross F1",
        "growth_duration_days": 60,
        "ph_priority": True,
    },
    {
        "id": "pechay",
        "tagalog_name": "Pechay",
        "english_name": "Pechay / Bok Choy",
        "scientific_name": "Brassica rapa subsp. chinensis",
        "family": "Brassicaceae",
        "category": "Leafy Green",
        "default_variety": "Pavon",
        "growth_duration_days": 28,
        "ph_priority": True,
    },
    {
        "id": "ampalaya",
        "tagalog_name": "Ampalaya",
        "english_name": "Bitter Gourd",
        "scientific_name": "Momordica charantia",
        "family": "Cucurbitaceae",
        "category": "Cucurbit / Vine",
        "default_variety": "Jade Star XL F1",
        "growth_duration_days": 55,
        "ph_priority": True,
    },
    {
        "id": "okra",
        "tagalog_name": "Okra",
        "english_name": "Okra",
        "scientific_name": "Abelmoschus esculentus",
        "family": "Malvaceae",
        "category": "Fruit Vegetable",
        "default_variety": "Smooth Green",
        "growth_duration_days": 45,
        "ph_priority": True,
    },
    {
        "id": "sili",
        "tagalog_name": "Sili",
        "english_name": "Chili / Pepper",
        "scientific_name": "Capsicum annuum",
        "family": "Solanaceae",
        "category": "Solanaceous",
        "default_variety": "Django F1",
        "growth_duration_days": 65,
        "ph_priority": True,
    },
    {
        "id": "pipino",
        "tagalog_name": "Pipino",
        "english_name": "Cucumber",
        "scientific_name": "Cucumis sativus",
        "family": "Cucurbitaceae",
        "category": "Cucurbit / Vine",
        "default_variety": "Emerald F1",
        "growth_duration_days": 50,
        "ph_priority": True,
    },
    {
        "id": "kangkong",
        "tagalog_name": "Kangkong",
        "english_name": "Water Spinach",
        "scientific_name": "Ipomoea aquatica",
        "family": "Convolvulaceae",
        "category": "Leafy Green",
        "default_variety": "Tsing Chiang",
        "growth_duration_days": 30,
        "ph_priority": True,
    },
    {
        "id": "lettuce",
        "tagalog_name": "Litsugas",
        "english_name": "Lettuce",
        "scientific_name": "Lactuca sativa",
        "family": "Asteraceae",
        "category": "Leafy Green",
        "default_variety": "Grand Rapids",
        "growth_duration_days": 45,
        "ph_priority": True,
    },
]

# Create directories if missing
def ensure_directories():
    for path in [
        ASSETS_DIR,
        SPRITES_DIR,
        PHOTOS_DIR,
        METADATA_DIR,
        CACHE_DIR,
        TEMP_DIR,
        PHOTOS_DIR / "crops",
        PHOTOS_DIR / "diseases",
        PHOTOS_DIR / "pests",
        PHOTOS_DIR / "weeds",
        PHOTOS_DIR / "varieties",
        METADATA_DIR / "crops",
        METADATA_DIR / "diseases",
        METADATA_DIR / "pests",
        MOBILE_ASSETS_DIR,
        MOBILE_METADATA_DIR / "crops",
        SPRITES_DIR / "crops",
        SPRITES_DIR / "trellises",
        SPRITES_DIR / "soil",
        SPRITES_DIR / "background_scenery",
        SPRITES_DIR / "ui",
    ]:
        path.mkdir(parents=True, exist_ok=True)
