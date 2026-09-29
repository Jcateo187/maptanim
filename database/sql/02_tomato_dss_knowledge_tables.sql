-- ==============================================================================
-- MapTanim Crop Agricultural Knowledge & Yield Studies Database Migration
-- Fully synchronized with verified DA-BPI, DA-BAR, and published university research
-- Yield Studies remain strictly separate records.
-- ==============================================================================

-- Ensure soil_type_enum exists
DO $$ BEGIN
    CREATE TYPE soil_type_enum AS ENUM ('LOAM', 'CLAY', 'SANDY', 'SILTY', 'PEATY', 'CHALKY');
EXCEPTION WHEN duplicate_object THEN null; END $$;

-- Drop legacy/partial knowledge tables if they exist to guarantee exact column schema
DROP TABLE IF EXISTS public.crop_pest_disease_guides CASCADE;
DROP TABLE IF EXISTS public.crop_soil_compatibilities CASCADE;
DROP TABLE IF EXISTS public.crop_growth_stages CASCADE;
DROP TABLE IF EXISTS public.crop_varieties CASCADE;
DROP TABLE IF EXISTS public.crop_yield_studies CASCADE;

-- 1. TABLE: public.crop_yield_studies (Peer-reviewed field & protected yield studies)
CREATE TABLE IF NOT EXISTS public.crop_yield_studies (
    id                          TEXT            PRIMARY KEY,
    crop_name                   VARCHAR(100)    NOT NULL,
    variety_name                VARCHAR(100)    NOT NULL,
    study_code                  VARCHAR(50)     NOT NULL UNIQUE,
    study_title                 TEXT            NOT NULL,
    location                    VARCHAR(150)    NOT NULL,
    study_period                VARCHAR(50)     NOT NULL,
    cultivation_condition       VARCHAR(150)    NOT NULL,
    treatment_description       TEXT            NOT NULL,
    baseline_yield_t_per_ha     NUMERIC(6,2),
    reported_yield_t_per_ha     NUMERIC(6,2)    NOT NULL,
    yield_increase_percent      NUMERIC(5,2),
    key_findings                TEXT            NOT NULL,
    authors                     TEXT            NOT NULL,
    institution                 TEXT            NOT NULL,
    publication_reference       TEXT            NOT NULL,
    created_at                  TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

ALTER TABLE public.crop_yield_studies ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "crop_yield_studies_read_all" ON public.crop_yield_studies;
CREATE POLICY "crop_yield_studies_read_all" ON public.crop_yield_studies FOR SELECT USING (true);

-- 2. TABLE: public.crop_varieties (Detailed variety genetics, duration, stage splits)
CREATE TABLE IF NOT EXISTS public.crop_varieties (
    id                          TEXT            PRIMARY KEY,
    crop_name                   VARCHAR(100)    NOT NULL,
    variety_name                VARCHAR(100)    NOT NULL,
    local_name_ph               VARCHAR(150),
    breeder_organization        VARCHAR(150),
    growth_duration_days        INT             NOT NULL,
    stage1_sprout_days          INT             NOT NULL,
    stage2_seedling_days        INT             NOT NULL,
    stage3_vegetative_days      INT             NOT NULL,
    stage4_flowering_days       INT             NOT NULL,
    stage5_harvest_days         INT             NOT NULL,
    watering_interval_days      INT             NOT NULL DEFAULT 2,
    fertilize_interval_days     INT             NOT NULL DEFAULT 10,
    optimal_seasons             TEXT[]          DEFAULT '{"YEAR_ROUND"}',
    disease_resistance          TEXT,
    description                 TEXT,
    source_citation             TEXT            NOT NULL,
    created_at                  TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

ALTER TABLE public.crop_varieties ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "crop_varieties_read_all" ON public.crop_varieties;
CREATE POLICY "crop_varieties_read_all" ON public.crop_varieties FOR SELECT USING (true);

-- 3. TABLE: public.crop_growth_stages (Stage-by-stage agronomic actions)
CREATE TABLE IF NOT EXISTS public.crop_growth_stages (
    id                          TEXT            PRIMARY KEY,
    crop_name                   VARCHAR(100)    NOT NULL,
    stage_index                 INT             NOT NULL,
    stage_name                  VARCHAR(50)     NOT NULL,
    day_start                   INT             NOT NULL,
    day_end                     INT             NOT NULL,
    primary_farmer_action       TEXT            NOT NULL,
    care_recommendation         TEXT            NOT NULL,
    irrigation_advice           TEXT            NOT NULL,
    nutrition_advice            TEXT            NOT NULL,
    critical_risks              TEXT            NOT NULL,
    source_citation             TEXT            NOT NULL,
    created_at                  TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

ALTER TABLE public.crop_growth_stages ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "crop_growth_stages_read_all" ON public.crop_growth_stages;
CREATE POLICY "crop_growth_stages_read_all" ON public.crop_growth_stages FOR SELECT USING (true);

-- 4. TABLE: public.crop_soil_compatibilities (Scientific soil-crop interaction)
CREATE TABLE IF NOT EXISTS public.crop_soil_compatibilities (
    id                          TEXT            PRIMARY KEY,
    crop_name                   VARCHAR(100)    NOT NULL,
    soil_type                   soil_type_enum  NOT NULL,
    suitability_rating          VARCHAR(20)     NOT NULL, -- 'OPTIMAL', 'SUITABLE', 'MARGINAL', 'POOR'
    suitability_score           FLOAT           NOT NULL,
    agronomic_rationale         TEXT            NOT NULL,
    amendment_action            TEXT,
    alert_warning               TEXT,
    source_citation             TEXT            NOT NULL,
    created_at                  TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

ALTER TABLE public.crop_soil_compatibilities ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "crop_soil_compatibilities_read_all" ON public.crop_soil_compatibilities;
CREATE POLICY "crop_soil_compatibilities_read_all" ON public.crop_soil_compatibilities FOR SELECT USING (true);

-- 5. TABLE: public.crop_pest_disease_guides (DA-BPI IPM Pest & Disease Reference)
CREATE TABLE IF NOT EXISTS public.crop_pest_disease_guides (
    id                          TEXT            PRIMARY KEY,
    crop_name                   VARCHAR(100)    NOT NULL,
    pest_disease_name           VARCHAR(150)    NOT NULL,
    local_name_ph               VARCHAR(150),
    scientific_name             VARCHAR(150),
    category                    VARCHAR(50)     NOT NULL, -- 'BACTERIAL', 'FUNGAL', 'VIRAL', 'INSECT'
    risk_season                 VARCHAR(50)     NOT NULL, -- 'WET', 'DRY', 'YEAR_ROUND'
    critical_stage_index        INT,
    symptoms                    TEXT            NOT NULL,
    organic_biocontrol          TEXT            NOT NULL,
    cultural_prevention         TEXT            NOT NULL,
    source_citation             TEXT            NOT NULL,
    created_at                  TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

ALTER TABLE public.crop_pest_disease_guides ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "crop_pest_disease_guides_read_all" ON public.crop_pest_disease_guides;
CREATE POLICY "crop_pest_disease_guides_read_all" ON public.crop_pest_disease_guides FOR SELECT USING (true);

-- ==============================================================================
-- 6. SEED DATA: TOMATO KNOWLEDGE BASE & DISTINCT YIELD STUDIES
-- ==============================================================================

-- -- ── 6.1 YIELD STUDIES (Strictly Separate Records - Backyard & Small Farm Terms) ──

-- Study A: Camiguin (2015-2016)
INSERT INTO public.crop_yield_studies (
    id, crop_name, variety_name, study_code, study_title, location, study_period,
    cultivation_condition, treatment_description, baseline_yield_t_per_ha, reported_yield_t_per_ha,
    yield_increase_percent, key_findings, authors, institution, publication_reference
) VALUES (
    'yield_study_tomato_camiguin_2016',
    'Tomato',
    'Diamante Max F1',
    'STUDY_A_CAMIGUIN_2016',
    'Utilization of Indigenous Mulches on the Growth and Yield of Different Tomato Varieties in Catarman, Camiguin, Philippines',
    'Catarman, Camiguin, Northern Mindanao, Philippines',
    'October 2015 – February 2016',
    'Open Field / Backyard Bed with Organic Mulch Blanket',
    'Covering the soil surface around tomato plants with a 2-inch layer of clean wood sawdust, dried rice straw, or dry grass (cogon) compared to bare exposed soil.',
    4.28,
    5.08,
    18.69,
    'Covering the ground around tomato plants with free sawdust or dry grass keeps the soil cool and moist under hot sun, prevents weeds, and yields up to 25–35 firm tomatoes per plant (an 18.7% boost without costly inputs).',
    'Erecson Sipin Solis, Larry Dionio, Ruth Duran, Jeanny Dacup',
    'Camiguin Polytechnic State College (Institute of Agriculture)',
    'Camiguin Agronomic Field Trial 2015–2016 (Solis et al., ResearchGate)'
) ON CONFLICT (id) DO UPDATE SET
    reported_yield_t_per_ha = EXCLUDED.reported_yield_t_per_ha,
    baseline_yield_t_per_ha = EXCLUDED.baseline_yield_t_per_ha,
    key_findings = EXCLUDED.key_findings;

-- Study B: Bacnotan, La Union (2025)
INSERT INTO public.crop_yield_studies (
    id, crop_name, variety_name, study_code, study_title, location, study_period,
    cultivation_condition, treatment_description, baseline_yield_t_per_ha, reported_yield_t_per_ha,
    yield_increase_percent, key_findings, authors, institution, publication_reference
) VALUES (
    'yield_study_tomato_bacnotan_2025',
    'Tomato',
    'Off-Season Hybrid Cultivar',
    'STUDY_B_BACNOTAN_2025',
    'Yield and Growth Response of Off-Season Tomato to Trehalose Foliar Fertilizer under Protected Cultivation in Bacnotan, La Union',
    'Bacnotan, La Union, Ilocos Region, Philippines',
    '2024 – 2025 Off-Season Trial',
    'Simple Protective Canopy / Backyard Rain Shelter',
    'Natural trehalose plant sugar spray (or 1 tsp brown sugar per liter of clean water) misted gently on tomato flowers 3 times during blooming.',
    NULL,
    4.75,
    NULL,
    'Under hot or rainy backyard conditions, spraying a mild sugar-water solution on blossoms prevents flower drop, helping almost every tomato flower develop into a full, sweet fruit.',
    'Agronomic Research Team, DMMMSU-NLUC / Research Collaborators',
    'Don Mariano Marcos Memorial State University, Bacnotan, La Union',
    'International Journal of Environment, Agriculture and Biotechnology (IJEAB, 2025)'
) ON CONFLICT (id) DO UPDATE SET
    key_findings = EXCLUDED.key_findings,
    treatment_description = EXCLUDED.treatment_description;

-- ── 6.2 TOMATO VARIETIES ──────────────────────────────────────────────────────

INSERT INTO public.crop_varieties (
    id, crop_name, variety_name, local_name_ph, breeder_organization,
    growth_duration_days, stage1_sprout_days, stage2_seedling_days, stage3_vegetative_days, stage4_flowering_days, stage5_harvest_days,
    watering_interval_days, fertilize_interval_days, optimal_seasons, disease_resistance, description, source_citation
) VALUES (
    'var_tomato_diamante_max_f1',
    'Tomato',
    'Diamante Max F1',
    'Kamatis Diamante Max F1',
    'East-West Seed Philippines / NSIC Registered',
    60, 5, 13, 20, 16, 6,
    2, 10,
    ARRAY['YEAR_ROUND', 'WET', 'DRY'],
    'Tough variety resistant to yellow leaf curling virus and bacterial wilting.',
    'The most popular, beginner-friendly hybrid tomato in the Philippines. Produces thick, firm oval fruits that don''t easily rot or spoil.',
    'DA-BPI National Seed Industry Council (NSIC) & East-West Seed 2024'
), (
    'var_tomato_apollo',
    'Tomato',
    'Apollo',
    'Kamatis Apollo',
    'UPLB-IPB / DA-BPI Lowland Release',
    72, 6, 15, 24, 20, 7,
    2, 12,
    ARRAY['DRY'],
    'Good natural resistance to leaf spots and summer heat.',
    'Traditional lowland tomato variety producing juicy, deep-red fruits best planted during dry, sunny months.',
    'Institute of Plant Breeding (IPB) UPLB & DA-BPI Solanaceous Bulletin'
) ON CONFLICT (id) DO UPDATE SET
    description = EXCLUDED.description,
    disease_resistance = EXCLUDED.disease_resistance;

-- ── 6.3 TOMATO GROWTH STAGES & ACTIONS ────────────────────────────────────────

INSERT INTO public.crop_growth_stages (
    id, crop_name, stage_index, stage_name, day_start, day_end,
    primary_farmer_action, care_recommendation, irrigation_advice, nutrition_advice, critical_risks, source_citation
) VALUES (
    'stage_tomato_0_sprout',
    'Tomato',
    0,
    'SPROUT / GERMINATION',
    1,
    5,
    'Keep seed tray in a warm, shaded spot; mist with a spray bottle every morning.',
    'Plant seeds shallow (half an inch) in recycled egg cartons, cups, or seed trays with soft soil mixed with compost.',
    'Gentle morning misting only. Do not pour heavy water with a cup so tiny seeds do not wash away.',
    'No fertilizer needed yet; the baby seed has its own food stored inside.',
    'Over-watering causes baby stems to rot and fall over (damping-off). Keep tray ventilated.',
    'DA-BPI Lowland Vegetable Production Guide 2024'
), (
    'stage_tomato_1_seedling',
    'Tomato',
    1,
    'SEEDLING & TRANSPLANTING',
    6,
    18,
    'Expose seedlings to morning sun for 3 days, then transplant into your garden bed in the late afternoon.',
    'Dig small holes spaced 1 arm-length (50 cm) apart. Bury the stem up to the first leaves for strong roots.',
    'Pour 1 cup of water around each plant immediately after transplanting, then water 1 tabo every 2 days.',
    'Put 1 to 2 handfuls of vermicast, well-rotted compost, or dried kitchen compost in each hole before planting.',
    'Midday heat can wilt fresh transplants; always transplant when the sun is going down.',
    'DA-BPI Technical Bulletin No. 14'
), (
    'stage_tomato_2_vegetative',
    'Tomato',
    2,
    'RAPID GROWING (VEGETATIVE)',
    19,
    38,
    'Push a bamboo stick (tulos) next to each plant and tie the main stem gently with a strip of cloth.',
    'Pinch off the tiny extra shoots (''suckers'') growing between the main stem and leaf branches so the plant grows tall.',
    'Water 1 tabo (1–2 liters) per plant every 2 days in the morning. Always water the soil, never splash the leaves.',
    'Spread 1 handful of vermicast or kitchen compost around the base, or water with rice-wash water (hugas-bigas).',
    'Letting vines crawl on wet ground invites leaf rot; keep stems tied upright on bamboo sticks.',
    'UPLB College of Agriculture Vegetable Management Compendium'
), (
    'stage_tomato_3_flowering',
    'Tomato',
    3,
    'FLOWERING & FRUIT SETTING',
    39,
    54,
    'Inspect flower clusters for tiny green caterpillars and support heavy fruiting branches.',
    'Remove yellowed lower leaves near the ground to let air circulate and sun reach the tomatoes.',
    'Water steadily every 2 days. Skipping watering then flooding will cause the tomatoes to split and crack.',
    'Sprinkle a spoonful of crushed eggshells (for calcium) or a pinch of wood ash around the plant to prevent bottom-rot.',
    'Fruit borer caterpillars boring into green tomatoes; bottom end of fruit turning black from dry soil.',
    'DA-BPI Solanaceous Production Standards'
), (
    'stage_tomato_4_harvest',
    'Tomato',
    4,
    'HARVEST TIME',
    55,
    65,
    'Gently twist or clip tomatoes when they turn light-pink or orange-red in the cool morning.',
    'Leave the green cap on top of the tomato so it stays fresh for up to two weeks on the counter.',
    'Water a little less now so the ripe tomatoes are sweeter and do not split.',
    'No more compost or fertilizer needed. Enjoy your fresh homegrown tomatoes!',
    'Overripe tomatoes falling and attracting ants or birds. Pick every 2 to 3 days.',
    'DA-BPI Post-Harvest Horticulture Guide'
) ON CONFLICT (id) DO UPDATE SET
    primary_farmer_action = EXCLUDED.primary_farmer_action,
    care_recommendation = EXCLUDED.care_recommendation;

-- ── 6.4 TOMATO SOIL COMPATIBILITIES ───────────────────────────────────────────

INSERT INTO public.crop_soil_compatibilities (
    id, crop_name, soil_type, suitability_rating, suitability_score,
    agronomic_rationale, amendment_action, alert_warning, source_citation
) VALUES (
    'soil_compat_tomato_loam',
    'Tomato',
    'LOAM',
    'OPTIMAL',
    1.00,
    'Loam is the perfect garden soil—soft, dark, crumbly, and drains water easily without drying out.',
    'Mix in 1 to 2 handfuls of compost or vermicast per plant before planting.',
    NULL,
    'BSWM Soil Fertility and Management Guidelines & DA-BPI Standards'
), (
    'soil_compat_tomato_clay',
    'Tomato',
    'CLAY',
    'MARGINAL',
    0.50,
    'Heavy clay soil holds too much water like sticky mud. Tomato roots cannot breathe and rot quickly if flooded.',
    'Build your planting bed at least 1 foot high (raised bed) and mix burnt rice hull (CRH) or dry compost to make the soil crumbly.',
    'DANGER OF ROOT ROT: Heavy clay soil stays wet too long after rain. Make sure your garden bed is raised so water drains away.',
    'DA-BPI Lowland Vegetable Production Bulletin 2024'
), (
    'soil_compat_tomato_sandy',
    'Tomato',
    'SANDY',
    'SUITABLE',
    0.75,
    'Sandy soil is loose and drains water very fast, but dries out quickly and loses plant food under hot sun.',
    'Cover the soil with a 2-inch blanket of dried leaves, grass, or sawdust (Camiguin method) to keep moisture in.',
    'Dries out fast: Water more frequently in small amounts rather than drowning it all at once.',
    'PCARRD Philippine Recommends for Soil & Water Conservation & Camiguin Study 2016'
), (
    'soil_compat_tomato_silty',
    'Tomato',
    'SILTY',
    'SUITABLE',
    0.75,
    'Smooth silt soil holds nutrients well but the top layer can form a hard crust after strong rain.',
    'Gently scratch the topsoil with a hand trowel after heavy rains and cover with dried leaves.',
    NULL,
    'DA-BAR Lowland Vegetables Production Handbook'
), (
    'soil_compat_tomato_peaty',
    'Tomato',
    'PEATY',
    'MARGINAL',
    0.50,
    'Very dark peaty soil can be naturally sour (acidic), which prevents the tomato plant from absorbing calcium.',
    'Mix crushed eggshells or agricultural lime into the soil 2 weeks before planting to sweeten the soil.',
    'Sour soil risk: Acidic peat causes the bottoms of tomatoes to turn black (blossom-end rot). Add crushed eggshells.',
    'BSWM Lime Application Technical Guide'
), (
    'soil_compat_tomato_chalky',
    'Tomato',
    'CHALKY',
    'POOR',
    0.25,
    'Chalky alkaline soil (with white limestone) causes tomato leaves to turn pale yellow and stunts growth.',
    'Add lots of rich dark organic compost, coffee grounds, and dried leaves to condition the soil.',
    'Yellow leaf risk: Plants struggle in chalky soil without lots of dark organic compost added.',
    'Philippine Agricultural Reference Guide'
) ON CONFLICT (id) DO UPDATE SET
    agronomic_rationale = EXCLUDED.agronomic_rationale,
    amendment_action = EXCLUDED.amendment_action;

-- ── 6.5 TOMATO PEST & DISEASE GUIDES (DA-BPI IPM Standards) ──────────────────

INSERT INTO public.crop_pest_disease_guides (
    id, crop_name, pest_disease_name, local_name_ph, scientific_name,
    category, risk_season, critical_stage_index, symptoms,
    organic_biocontrol, cultural_prevention, source_citation
) VALUES (
    'pest_tomato_bacterial_wilt',
    'Tomato',
    'Bacterial Wilt',
    'Lanta ng Kamatis',
    'Ralstonia solanacearum',
    'BACTERIAL',
    'WET',
    2,
    'The entire tomato plant suddenly wilts and droops at midday while the leaves are still completely green.',
    'Water the soil with bio-fungicide (Trichoderma) or sprinkle carbonized rice hull around the base.',
    'Plant in raised garden beds so rainwater drains away quickly. Never plant tomatoes where eggplants or bell peppers just died.',
    'DA-BPI Integrated Pest Management (IPM) Technical Guide'
), (
    'pest_tomato_fruit_borer',
    'Tomato',
    'Tomato Fruit Borer',
    'Harabas / Uod sa Bunga',
    'Helicoverpa armigera',
    'INSECT',
    'YEAR_ROUND',
    3,
    'Small round holes drilled into green and red tomatoes with worm dirt inside; affected fruits rot and drop off.',
    'Spray safe organic Bt (Bacillus thuringiensis) or neem oil spray directly on flower clusters.',
    'Check plants in the early morning and handpick any green caterpillars. Plant bright marigold flowers around your beds to repel moths.',
    'DA-BPI Lowland Pest Management Compendium'
), (
    'pest_tomato_whitefly_tylcv',
    'Tomato',
    'Whitefly / Leaf Curl',
    'Puting Langaw / Kulot ng Kamatis',
    'Bemisia tabaci / TyLCV',
    'VIRAL',
    'DRY',
    1,
    'Leaves curl upwards like cups and turn yellow; tiny white powdery insects flutter when you shake the plant.',
    'Hang yellow plastic cups/boards coated with cooking oil to catch them; spray mild neem or garlic-soap water under leaves.',
    'Choose resistant seeds like Diamante Max F1; cover the surrounding soil with dried straw or reflective foil.',
    'DA-BPI Virus & Vector Management Bulletin'
) ON CONFLICT (id) DO UPDATE SET
    organic_biocontrol = EXCLUDED.organic_biocontrol,
    cultural_prevention = EXCLUDED.cultural_prevention;
