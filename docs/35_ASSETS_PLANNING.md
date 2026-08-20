# 35. Asset Pipeline & Production Specifications

> 📌 **Navigation**: [◀ 34. Direct Soil Crop Planting & Resize System](file:///d:/Development/MapTanim/docs/34_CROP_PLANTING_AND_RESIZE_SYSTEM.md) | [🏠 Master Index](file:///d:/Development/MapTanim/docs/README.md) | [36. Crop Variety Timeline & Seasonality ▶](file:///d:/Development/MapTanim/docs/36_CROP_VARIETY_TIMELINE_AND_SEASONALITY.md)

---
# 🌾 MapTanim Asset Pipeline Specification

> **Version**: 1.0  
> **Purpose**: Define the complete asset pipeline for MapTanim, including self-created isometric game sprites (specifying exact canvas sizes, pivot positions, grid anchors, and types) aligned strictly with **[34_CROP_PLANTING_AND_RESIZE_SYSTEM.md](file:///d:/Development/MapTanim/docs/34_CROP_PLANTING_AND_RESIZE_SYSTEM.md)**, alongside automated educational asset sourcing, processing, licensing, caching, and serving.

---

# 1. Goals

The asset system shall:
- support thousands of crops
- support crop varieties
- support pests
- support weeds
- support diseases
- support fertilizers
- support farm objects (trellises, fences, soil beds)
- support isometric game sprites (aligned with Doc 34 engine)
- support educational photos
- support offline caching
- support free cloud hosting
- avoid copyright infringement

---

# 2. Asset Categories

There are TWO completely different asset libraries.

---

## Library A: Isometric Game Assets
These are rendered inside the farm canvas strictly following the Doc 34 rendering engine specifications.  
**Never download these from the internet.**  
These must be original artwork created specifically for MapTanim.

```text
sprites/
    -- IN-GRID PLANTING ASSETS (Vegetable Crops Focus Only) --
    crops/               <-- Vegetable crop growth stages & sprites
    trellises/           <-- Support structures for vining vegetables
    soil/                <-- Mounded soil bed bases
    
    -- OUTER PERIMETER & BACKGROUND SCENERY (Static Framing Area) --
    background_scenery/
        irrigation/         <-- Outer water channels, pumps, drainage ditches
        fences/             <-- Outer perimeter fence tiles
        trees_and_rocks/    <-- Background trees, rocks, bushes
        water_lily/         <-- Static water pond details
    
    -- UI & OVERLAY BADGES --
    ui/
        badges/
        monitoring/         <-- Floating DSS badges (Water, Pest, Weed Alert, Fertilizer)
        tasks/
        tray/
```

> [!IMPORTANT]
> **Philippine Vegetable Crops Scope**:  
> MapTanim focuses **EXCLUSIVELY ON PHILIPPINE VEGETABLE CROPS** (DA-PH Priority High-Value Crops & *Bahay Kubo* staples). Non-vegetable crops, fruit orchards (e.g. apple, orange trees), and commercial flower gardens are excluded from interactive planting grid beds.

### 2.1 Philippine Vegetable Crop Matrix & Production Status

| Local Name (Tagalog/PH) | Crop Species | Category / Part | Stage 1 | Stage 2 | Stage 3 | Stage 4 | Stage 5 | Production Status |
|---|---|---|---|---|---|---|---|---|
| **Karots** 🥕 | Carrot | Root | `crop_carrot_1` | `crop_carrot_2` | `crop_carrot_3` | `crop_carrot_4` | `crop_carrot_5` | ✅ Complete (100%) |
| **Sitaw** 🫘 | String Beans | Podded / Legume | `crop_stringbeans_1` | `crop_stringbeans_2` | `crop_stringbeans_3` | `crop_stringbeans_4` | `crop_stringbeans_5` | ✅ Complete (100%) |
| **Talong** 🍆 | Eggplant | Solanaceous | `crop_eggplant_1` | `crop_eggplant_2` | `crop_eggplant_3` | `crop_eggplant_4` | `crop_eggplant_5` | ⏳ Planned |
| **Kamatis** 🍅 | Tomato | Solanaceous | `crop_tomato_1` | `crop_tomato_2` | `crop_tomato_3` | `crop_tomato_4` | `crop_tomato_5` | ⏳ Planned |
| **Sibuyas** 🧅 | Onion | Bulb | `crop_onion_1` | `crop_onion_2` | `crop_onion_3` | `crop_onion_4` | `crop_onion_5` | ⏳ Planned |
| **Kalabasa** 🎃 | Pumpkin / Squash | Cucurbit / Vine | `crop_pumpkin_1` | `crop_pumpkin_2` | `crop_pumpkin_3` | `crop_pumpkin_4` | `crop_pumpkin_5` | ⏳ Planned |
| **Mais** 🌽 | Corn | Grain / Field | `crop_corn_1` | `crop_corn_2` | `crop_corn_3` | `crop_corn_4` | `crop_corn_5` | ⏳ Planned |
| **Repolyo** 🥬 | Cabbage | Leafy / Brassica | `crop_cabbage_1` | `crop_cabbage_2` | `crop_cabbage_3` | `crop_cabbage_4` | `crop_cabbage_5` | ⏳ Planned |
| **Pechay** 🥬 | Pechay / Bok Choy | Leafy Green | `crop_pechay_1` | `crop_pechay_2` | `crop_pechay_3` | `crop_pechay_4` | `crop_pechay_5` | ⏳ Planned |
| **Ampalaya** 🥒 | Bitter Gourd | Cucurbit / Vine | `crop_ampalaya_1` | `crop_ampalaya_2` | `crop_ampalaya_3` | `crop_ampalaya_4` | `crop_ampalaya_5` | ⏳ Planned |
| **Okra** 🌿 | Okra | Fruit Vegetable | `crop_okra_1` | `crop_okra_2` | `crop_okra_3` | `crop_okra_4` | `crop_okra_5` | ⏳ Planned |
| **Sili** 🌶️ | Chili / Pepper | Solanaceous | `crop_sili_1` | `crop_sili_2` | `crop_sili_3` | `crop_sili_4` | `crop_sili_5` | ⏳ Planned |
| **Pipino** 🥒 | Cucumber | Cucurbit / Vine | `crop_pipino_1` | `crop_pipino_2` | `crop_pipino_3` | `crop_pipino_4` | `crop_pipino_5` | ⏳ Planned |
| **Kangkong** 🥬 | Water Spinach | Leafy Green | `crop_kangkong_1` | `crop_kangkong_2` | `crop_kangkong_3` | `crop_kangkong_4` | `crop_kangkong_5` | ⏳ Planned |
| **Litsugas** 🥗 | Lettuce | Leafy Green | `crop_lettuce_1` | `crop_lettuce_2` | `crop_lettuce_3` | `crop_lettuce_4` | `crop_lettuce_5` | ⏳ Planned |

---

## Library B: Educational Assets
Used only inside UI reference screens:
- Crop Library (Philippine Vegetable Species & Varieties)
- Disease Library (Philippine Crop Diseases & Symptoms)
- Pest Library (Philippine Pests & Predators)
- Variety Library (DA-PH Registered Cultivars)
- Help & Advisory Pages

```text
photos/
    crops/
    varieties/
    diseases/
    pests/
    weeds/
    fertilizer/
```
These are downloaded automatically via Python processing scripts matching Philippine agricultural databases.

---

# 3. Directory Structure

```text
assets/
    sprites/
        crops/            <-- Philippine vegetable crops (sitaw, talong, kamatis, etc.)
            carrot/
            stringbeans/
            eggplant/
            tomato/
            onion/
            pumpkin/
            corn/
            cabbage/
            pechay/
            ampalaya/
            okra/
            sili/
        trellises/
        soil/
        background_scenery/
            irrigation/       <-- Outer farm perimeter water source, pump & channels
            fences/           <-- Outer boundary wooden fence
            trees_and_rocks/  <-- Outer mango, coconut, banana trees & rocks
            water_lily/       <-- Outer water pond detailing
        ui/
            badges/
            monitoring/
            tasks/
            tray/
    photos/
        crops/
        diseases/
        pests/
        weeds/
        varieties/
    metadata/
        crops/
        diseases/
        pests/
    cache/
    temp/
```

---

# 4. Self-Created Asset Specifications (Library A - Game Sprites)

All original artwork created for MapTanim must follow strict **Size**, **Position Anchor (Pivot)**, and **Type** rules to ensure proper alignment inside Compose `DrawScope` without visual float or rendering distortion.

### 4.1 Technical Artwork Standards
- **Isometric Ratio**: Standard 2:1 dimetric projection angle ($\arctan(0.5) \approx 26.565^\circ$).
- **Baseline Pivot Anchor**: The anchor point $(0.5, 1.0)$ is located at the **bottom-center** of the base stem/root. When placed at tile coordinate `(worldX + 0.5, worldY + 0.5)`, the baseline pivot aligns exactly with the center of the 1m soil diamond.
- **Scaling Rule**: Always rendered at native 1:1 scale (`scaleFactor = 1.0f`).

### 4.2 Crop Sprite Package & Stage Specifications

Every crop folder under `sprites/crops/<crop_name>/` contains the following self-created assets:

| File Name | Description | Size (Px) | Anchor Point (X, Y) | Format / Type | Purpose |
|---|---|---|---|---|---|
| `stage1.png` | Stage 1 (Seed/Sprout) | $128 \times 128$ | Bottom-Center $(0.5, 0.90)$ | WebP / PNG8 | Germination shoot |
| `stage2.png` | Stage 2 (Seedling) | $128 \times 128$ | Bottom-Center $(0.5, 0.90)$ | WebP / PNG8 | Early vegetative foliage |
| `stage3.png` | Stage 3 (Vegetative) | $256 \times 256$ | Bottom-Center $(0.5, 0.90)$ | WebP / PNG8 | Mid-growth established plant |
| `stage4.png` | Stage 4 (Flowering) | $256 \times 256$ | Bottom-Center $(0.5, 0.90)$ | WebP / PNG8 | Mature foliage with flower buds |
| `stage5.png` | Stage 5 (Harvest Ready) | $256 \times 256$ | Bottom-Center $(0.5, 0.90)$ | WebP / PNG8 | Peak yield with ripe produce |
| `soil.png` | Mounded Soil Base | $256 \times 128$ | Center $(0.5, 0.5)$ | WebP / PNG8 | 1m × 1m isometric dirt bed overlay |
| `flower.png` | Individual Floral Bloom | $64 \times 64$ | Center $(0.5, 0.5)$ | WebP / PNG8 | Detached floral icon / DSS detail |
| `fruit_green.png` | Unripe Produce Sprite | $128 \times 128$ | Center $(0.5, 0.5)$ | WebP / PNG8 | Mid-growth yield overlay |
| `fruit_ripe.png` | Ripe Produce Sprite | $128 \times 128$ | Center $(0.5, 0.5)$ | WebP / PNG8 | Harvest yield overlay |
| `icon.png` | Crop Tray Card Graphic | $128 \times 128$ | Center $(0.5, 0.5)$ | WebP / PNG8 | UI Crop Tray drawer card |
| `thumbnail.png` | Small Square Thumbnail | $64 \times 64$ | Center $(0.5, 0.5)$ | WebP / PNG8 | List & compact table thumbnail |
| `shadow.png` | Soft Ground Drop Shadow | $128 \times 64$ | Center $(0.5, 0.5)$ | WebP / PNG8 | Translucent dark ground shadow |
### 4.3 Spatial Boundary Rules: In-Grid Planting vs. Outer Scenery

> [!IMPORTANT]
> **Core Spatial Rule (Doc 34 Engine)**:  
> - **Inside the $45\text{m} \times 45\text{m}$ Farm Grid**: The user focuses **ONLY on planting crops** (crop zones, foliage). These are interactive, moveable objects managed via the Crop Tray drawer.
> - **Outside the $45\text{m} \times 45\text{m}$ Grid (Perimeter Scenery)**: **Irrigation** (outer water channels, pump stations, drainage ditches, water lily ponds), perimeter wooden fences, background trees (mango, coconut, banana), and decorative rocks belong to **fixed background scenery** (`background_scenery/backgound_1.png`). They frame the farm canvas and are not placed inside planting plots.

```text
┌─────────────────────────────────────────────────────────────┐
│  BACKGROUND SCENERY & PERIMETER INFRASTRUCTURE               │
│  (Mango Trees, Coconut Trees, Water Lily Pond)               │
│                                                             │
│    ================ PERIMETER FENCE ================        │
│   │                                                 │       │
│   │   ┌─────────────────────────────────────────┐   │       │
│   │   │  45m × 45m INTERACTIVE PLANTING GRID    │   │       │  🌊 IRRIGATION
│   │   │                                         │   │       │  CANAL / PUMP
│   │   │  • Crop Zones (Carrots, Beans, etc.)    │   │       │  (Outside
│   │   │  • Soil Beds & Trellises                │   │       │   Grid)
│   │   │                                         │   │       │
│   │   └─────────────────────────────────────────┘   │       │
│   │                                                 │       │
│    ================ PERIMETER FENCE ================        │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

---

# 5. Standard Growth Stages

Every crop follows the same 5-stage lifecycle:

| Stage | Description | System Behavior (Doc 34 Engine) |
|---|---|---|
| **Stage 1** | Seed | Germination phase; minimal water requirement |
| **Stage 2** | Seedling | Early shoot growth; vulnerable to weeds |
| **Stage 3** | Vegetative | Primary foliage expansion; high water demand |
| **Stage 4** | Flowering | Floral bloom phase; fertilizer application window |
| **Stage 5** | Harvest Ready | Ripe fruit/root; triggers single-tap harvest action |

---

# 6. Support Structures & Trellises

Some crops (e.g. String Beans, Tomatoes, Cucumbers) require support structures.  
**Rule**: Never duplicate supports inside plant sprites. Reuse decoupled support assets.

### 6.1 Support Asset Specifications (`sprites/trellises/`)

| Asset Name | Structure Type | Grid Coverage | Canvas Size (Px) | Anchor Point | Compatible Crops |
|---|---|---|---|---|---|
| `aframe.png` | Wooden A-Frame Trellis | $1\text{m} \times 1\text{m}$ | $256 \times 384$ | Bottom-Center $(0.5, 0.95)$ | String Beans, Pole Beans |
| `bamboo_single.png` | Vertical Bamboo Stake | $1\text{m} \times 1\text{m}$ | $128 \times 384$ | Bottom-Center $(0.5, 0.95)$ | Tomato, Eggplant |
| `bamboo_double.png` | Crossed Bamboo Poles | $1\text{m} \times 2\text{m}$ | $256 \times 384$ | Bottom-Center $(0.5, 0.95)$ | Cucumber, Bitter Gourd |
| `tomato_cage.png` | Wire Tomato Cage | $1\text{m} \times 1\text{m}$ | $256 \times 256$ | Bottom-Center $(0.5, 0.95)$ | Bush Tomato, Pepper |
| `vertical.png` | Overhead Rope Net | $1\text{m} \times 1\text{m}$ | $256 \times 384$ | Bottom-Center $(0.5, 0.95)$ | Vining Cucurbits |
| `wire.png` | T-Post Support Wire | $1\text{m} \times 2\text{m}$ | $256 \times 256$ | Bottom-Center $(0.5, 0.95)$ | Commercial Tomato Beds |

### 6.2 Rendering Layer Order (Doc 34 Assembly)
1. **Layer 0**: Base Soil Tile (`soil.png`)
2. **Layer 1**: Support Structure (`aframe.png` / `bamboo_single.png`)
3. **Layer 2**: Vine / Foliage Sprite (`stage1.png`–`stage5.png`)
4. **Layer 3**: Flowers (`flower.png`)
5. **Layer 4**: Fruit / Pods (`fruit_ripe.png`)

---

# 7. DSS, Monitoring, Badges, Tasks & UI Specifications

The Decision Support System (DSS) and monitoring tools render floating overlays atop crop zones and farm tiles.

### 7.1 Monitoring & DSS Badges (`sprites/ui/monitoring/`)

| Badge Graphic | Name / State | Canvas Size | Anchor Point | Visual Style | Function |
|---|---|---|---|---|---|
| `badge_water.png` | Water Needed | $64 \times 64$ | Center | Blue Drop Pill (`#1E88E5`) | Triggers irrigation action |
| `badge_pest.png` | Pest Alert | $64 \times 64$ | Center | Red Warning Pill (`#E53935`) | Displays pest advisory |
| `badge_fertilizer.png` | Fertilizer Needed | $64 \times 64$ | Center | Orange Sprout Pill (`#FB8C00`) | Indicates nutrient deficit |
| `badge_harvest.png` | Harvest Ready | $64 \times 64$ | Center | Glowing Gold Star (`#FFD54F`) | Prompts harvesting |
| `badge_health_good.png`| Optimal Health | $48 \times 48$ | Center | Green Check Badge (`#4CAF50`) | Confirms healthy zone |
| `badge_disease.png` | Disease Alert | $64 \times 64$ | Center | Purple Shield (`#8E24AA`) | Opens pathology guide |

### 7.2 Task Pin & Calendar Indicators (`sprites/ui/tasks/`)

| Graphic | Name / Function | Canvas Size | Anchor Point | Positioning Rule |
|---|---|---|---|---|
| `pin_task_active.png` | Active Field Task Pin | $64 \times 80$ | Bottom-Center $(0.5, 1.0)$ | Rendered at `topEdgeCenter` of Crop Zone |
| `pin_task_overdue.png`| Overdue Task Alert Pin | $64 \times 80$ | Bottom-Center $(0.5, 1.0)$ | Blinking Red Pin at zone center |
| `badge_calendar.png` | Scheduled Planting Date | $48 \times 48$ | Center | Attached to floating zone pill header |

---

# 8. Download Sources & Authoritative Real Data APIs (Library B - Educational Assets)

> [!IMPORTANT]
> **Strict Zero-Mock Data Policy**:  
> All crop information, disease symptoms, pest profiles, weed characteristics, and fertilizer guides **MUST BE 100% REAL, CURRENT, AND SCIENTIFICALLY ACCURATE DATA**.  
> Mock data, dummy text (`Lorem Ipsum`), or placeholder entries are **STRICTLY FORBIDDEN**. When users search via in-app search or external search engines, the data retrieved must match verified scientific and agricultural records.

### Approved Real-Data & Media Sources
1. **PlantVillage API & Open Dataset**
   - **License**: CC0 / Open Access
   - **Real Data**: Verified crop disease diagnostic symptoms, pathogen classifications, and organic/chemical treatment protocols.
2. **GBIF (Global Biodiversity Information Facility API)**
   - **License**: CC0 / CC-BY
   - **Real Data**: Exact scientific taxonomy (Kingdom, Family, Genus, Species), biological distribution, and botanical classification.
3. **Wikidata & DBpedia SPARQL APIs**
   - **License**: CC0 / Public Domain
   - **Real Data**: Cultivar profiles, growth duration (days to harvest), optimal soil pH ranges, temperature tolerances, and regional origin.
4. **Wikimedia Commons API**
   - **License**: Public Domain / CC-BY / CC-BY-SA
   - **Real Data**: High-resolution educational species photos, verified leaf/fruit symptoms, and historical crop variety photos.
5. **Department of Agriculture (DA-PH) & PhilRice Agricultural Guidelines**
   - **License**: Public Government Data
   - **Real Data**: Local Philippine crop variety names (e.g., *Sitao*, *Talong*, *Kamatis*), regional planting calendars, and NPK fertilizer guidelines.
6. **iNaturalist API**
   - **License**: CC0 / CC-BY / CC-BY-SA
   - **Real Data**: Field observation photos, pest infestation geographical points, and natural enemy predator data.

---

# 9. Forbidden Sources & Prohibited Practices

### Prohibited Data Practices
- ✘ **Dummy / Mock Text**: Never use placeholder descriptions (`test_crop_123`, `Lorem ipsum dolor`).
- ✘ **Unverified Web Scraping**: Never scrape unverified blogs or user-generated forums with unconfirmed claims.

### Forbidden Image Sources
- Google Images
- Facebook / Instagram / Pinterest
- Commercial Stock Libraries (Shutterstock, Getty Images, Adobe Stock)
- DeviantArt (unless explicitly released under CC0/CC-BY)

---

# 10. License & Verification Rules

### Allowed Licenses
- ✔ **CC0** (Creative Commons Zero / Public Domain)
- ✔ **Public Domain**
- ✔ **CC-BY 4.0 / 3.0** (Attribution required)
- ✔ **CC-BY-SA 4.0** (Attribution-ShareAlike)

### Verification Requirements
Every entry fetched by the Python downloader pipeline (`asset_downloader/`) MUST undergo automated license validation (`verify_license.py`) and SHA256 checksum verification before insertion into the database or search index.

---

# 11. Real Metadata JSON Schema Specification

Every downloaded asset and catalog entry MUST generate an exact, real-world JSON metadata file:

```json
{
  "id": "tomato_disease_early_blight",
  "category": "disease",
  "common_name": "Early Blight",
  "local_name_ph": "Blight sa Kamatis",
  "scientific_name": "Alternaria solani",
  "taxonomic_family": "Solanaceae",
  "affected_crops": ["Tomato", "Eggplant", "Potato"],
  "symptoms": [
    "Concentric dark brown rings ('target board' pattern) on mature leaves",
    "Yellow halo surrounding brown necrotic spots",
    "Stem lesions near soil line leading to collar rot"
  ],
  "treatment_protocol": {
    "organic": "Apply copper-based fungicide or neem oil solution every 7-10 days",
    "chemical": "Chlorothalonil or Mancozeb application upon first symptom appearance",
    "cultural_prevention": "Crop rotation with non-solanaceous crops, drip irrigation to avoid wet foliage, staking plants"
  },
  "optimal_conditions": {
    "temperature_range_c": "24-29",
    "humidity_threshold_pct": 80
  },
  "media": {
    "primary_photo_url": "photos/diseases/early_blight_001.webp",
    "thumbnail_url": "photos/diseases/thumb_early_blight_001.webp",
    "source_url": "https://commons.wikimedia.org/wiki/File:Tomato_early_blight.jpg",
    "author": "PlantVillage / Penn State University",
    "license": "CC-BY 4.0",
    "download_date": "2026-07-30",
    "hash_sha256": "ab239ab87c12f45de190cdeef341"
  }
}
```

---

# 12. Image Processing Pipeline

An automated Python processing pipeline will handle downloaded photos:
- ✔ Resize to standard bounds
- ✔ Compress to optimized WebP
- ✔ Auto-rotate based on EXIF orientation
- ✔ Crop to square aspect ratios where required
- ✔ Standardize naming
- ✔ Generate low-res preview thumbnails
- ✔ Remove identical/duplicate images

**Output Specification**:
- Photos: Max $512 \times 512$ px (WebP format)
- Thumbnails: $128 \times 128$ px (WebP format)

---

# 13. Naming Conventions

### Library A (Self-Created Sprites)
- Crop Stage: `crop_tomato_stage1.png`
- Flower: `flower_tomato.png`
- Fruit: `fruit_tomato_ripe.png`
- Thumbnail: `thumb_tomato.webp`

### Library B (Educational Photos)
- Photo: `photo_tomato_001.webp`
- Disease: `disease_early_blight_001.webp`
- Variety: `variety_roma_tomato_001.webp`

---

# 14. Cloud Storage Architecture

Hosted via **Supabase Storage**:

```text
supabase-storage/
├── sprites/
│   ├── crops/
│   ├── trellises/
│   ├── soil/
│   └── ui/
├── photos/
│   ├── crops/
│   ├── diseases/
│   ├── pests/
│   └── weeds/
├── metadata/
│   ├── crops/
│   └── diseases/
└── guides/
```

---

# 15. Database Architecture

The local SQLite (Room) and remote Supabase PostgreSQL databases store relative paths / public URLs only.

```json
{
  "crop_id": "crop_tomato",
  "sprite_url": "sprites/crops/tomato/",
  "photo_url": "photos/crops/tomato/",
  "metadata_url": "metadata/crops/tomato.json"
}
```

---

# 16. Offline Caching Rules

- **Library A (Sprites)**: Cache permanently on device storage (bundled in APK or synced once).
- **Library B (Photos)**: Cache for 30 days in device storage.
- **Metadata**: Cache for 7 days.
- **Educational Guides**: Cache for 30 days.

---

# 17. Python Downloader Responsibilities

The automated downloader script shall:
- Search approved APIs (Wikimedia, PlantVillage, iNaturalist)
- Verify license compatibility before downloading
- Save exact attribution metadata
- Generate square thumbnails
- Compress images to WebP
- Execute pHash duplicate detection
- Upload processed assets to Supabase Storage
- Generate `manifest.json`
- Skip non-compliant licenses automatically
- Retry network failures automatically

---

# 18. Duplicate Detection

Uses a two-tier duplicate elimination strategy:
1. **SHA256 Hash**: Identifies identical binary files.
2. **Perceptual Hash (pHash)**: Identifies visually identical/cropped images.  
*Automatically deletes duplicate images prior to upload.*

---

# 19. Asset Manifest & Target Scale

### Asset Production Scale Targets

| Category | Estimated Count | Storage Type |
|---|---|---|
| **Crop Photos** | 1,500+ | Supabase / Local Cache |
| **Crop Varieties** | 2,000+ | Supabase / Local Cache |
| **Disease Photos** | 2,000+ | Supabase / Local Cache |
| **Pest Photos** | 1,000+ | Supabase / Local Cache |
| **Weed Photos** | 500+ | Supabase / Local Cache |
| **Sprite Assets (Library A)** | 250+ | Bundled APK / Perm Cache |
| **UI & DSS Badges (Library A)** | 200+ | Bundled APK / Perm Cache |
| **Soil & Fence Assets (Library A)**| 30+ | Bundled APK / Perm Cache |
| **Trellis Structures (Library A)** | 20+ | Bundled APK / Perm Cache |
| **Total Asset Volume** | **7,000–10,000 Assets** | |

---

# 20. Future Python Modules Architecture

The automated downloader shall be structured as decoupled, single-responsibility Python modules:

```text
asset_downloader/
├── download_wikimedia.py
├── download_plantvillage.py
├── download_inaturalist.py
├── download_pexels.py
├── process_images.py
├── remove_duplicates.py
├── generate_metadata.py
├── generate_manifest.py
├── upload_supabase.py
├── cache_manager.py
├── verify_license.py
├── config.py
└── main.py
```

---

# 21. Isometric Asset Dimensions & Tile Alignment Specifications

> **Purpose**: Exact pixel specifications, alignment rules, and full asset checklist for `FarmCanvasRenderer`.

## 📐 Isometric Tile System — Core Constants

The renderer uses these constants in `IsometricProjection`:
- `TILE_W = 128 pixels` (1 world meter = 128px wide)
- `TILE_H = 64 pixels` (1 world meter = 64px tall — strict 2:1 ratio)

### Recommended Asset Resolution: **2× (256 × 128 base)**
- **Base tile**: 256px wide × 128px tall
- The renderer scales them down to `TILE_W × TILE_H` at zoom=1.0

## 💎 The Isometric Diamond Anatomy

Every flat ground tile must be a **diamond (rhombus)** that fills its rectangular bounding box:
- Top vertex: `(128, 0)` — center of top edge
- Right vertex: `(256, 64)` — center of right edge
- Bottom vertex: `(128, 128)` — center of bottom edge
- Left vertex: `(0, 64)` — center of left edge

> [!CAUTION]
> The diamond MUST touch all 4 edge midpoints exactly with ZERO transparent padding inside diamond edges.

## 🌿 Ground Terrain & Soil Assets (`256 × 128 px`)
- **Grass Tiles**: `grass_01.png` through `grass_05.png` (256×128, flat diamond, no 3D sidewalls).
- **Soil Tiles**: `soil_01.png` through `soil_08.png` (256×128, flat diamond mapped to soil types).
- **Crop Sprites**: `crop_[name]_[1-4].png` (64×64 px per plant stage, anchor at bottom-center `(32, 64)`).
- **Trellis Sprites**: `trellis_aframe.png` (128×192 px, anchor at `(64, 192)`).

---

## 📚 Related Documentation & Cross References
- 📄 [Master Documentation Hub](file:///d:/Development/MapTanim/docs/README.md)
- 📄 [00. Getting Started Guide](file:///d:/Development/MapTanim/docs/00_GETTING_STARTED.md)
- 📄 [03. System Architecture](file:///d:/Development/MapTanim/docs/03_SYSTEM_ARCHITECTURE.md)
- 📄 [11. App Navigation](file:///d:/Development/MapTanim/docs/11_NAVIGATION.md)
- 📄 [12. UI/UX Guidelines](file:///d:/Development/MapTanim/docs/12_UI_UX_GUIDELINES.md)
- 📄 [13. Design System](file:///d:/Development/MapTanim/docs/13_DESIGN_SYSTEM.md)
- 📄 [14. Component Library](file:///d:/Development/MapTanim/docs/14_COMPONENT_LIBRARY.md)
- 📄 [15. Render Engine](file:///d:/Development/MapTanim/docs/15_RENDER_ENGINE.md)
- 📄 [16. Interactive Plot Mapping](file:///d:/Development/MapTanim/docs/16_INTERACTIVE_PLOT_MAPPING.md)
- 📄 [18. View Mode](file:///d:/Development/MapTanim/docs/18_VIEW_MODE.md)
- 📄 [19. Edit Mode](file:///d:/Development/MapTanim/docs/19_EDIT_MODE.md)
- 📄 [34. Direct Soil Crop Planting & Resize System](file:///d:/Development/MapTanim/docs/34_CROP_PLANTING_AND_RESIZE_SYSTEM.md)
- 📄 [38. Audio & Sound Assets Planning](file:///d:/Development/MapTanim/docs/38_AUDIO_AND_SOUND_ASSETS_PLANNING.md)
- 📄 [39. Crop View Interaction & Variety Simulation](file:///d:/Development/MapTanim/docs/39_CROP_VIEW_INTERACTION_AND_VARIETY_SIMULATION.md)
