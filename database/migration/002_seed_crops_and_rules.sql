-- MapTanim Versioned Migration 002: Seed Data for Crops, Companion Matrix & Notifications

INSERT INTO public.crops (name, local_name, botanical_name, category, days_to_harvest, optimal_ph_min, optimal_ph_max, season, suitable_soils, description)
VALUES
    ('Tomato', 'Kamatis', 'Solanum lycopersicum', 'FRUIT', 60, 6.0, 6.8, 'DRY', ARRAY['LOAM', 'SANDY', 'SILTY']::soil_type_enum[], 'High-value fruit vegetable sensitive to moisture.'),
    ('Eggplant', 'Talong', 'Solanum melongena', 'FRUIT', 75, 5.5, 6.8, 'YEAR_ROUND', ARRAY['LOAM', 'CLAY', 'SILTY']::soil_type_enum[], 'Popular lowland vegetable, warm season crop.'),
    ('Chili Pepper', 'Siling Haba / Labuyo', 'Capsicum frutescens', 'FRUIT', 65, 6.0, 7.0, 'YEAR_ROUND', ARRAY['LOAM', 'SANDY', 'SILTY']::soil_type_enum[], 'Hot spice crop resilient to warm weather.'),
    ('Cabbage', 'Repolyo', 'Brassica oleracea var. capitata', 'LEAFY', 60, 6.0, 6.5, 'DRY', ARRAY['LOAM', 'SILTY', 'PEATY']::soil_type_enum[], 'Cool-season leafy brassica crop.'),
    ('Pechay', 'Pechay', 'Brassica rapa subsp. chinensis', 'LEAFY', 28, 6.0, 7.0, 'YEAR_ROUND', ARRAY['LOAM', 'SILTY', 'PEATY']::soil_type_enum[], 'Fast turnaround leafy brassica grown in lowland beds.'),
    ('Onion', 'Sibuyas', 'Allium cepa', 'BULB', 110, 6.0, 7.0, 'DRY', ARRAY['LOAM', 'SANDY', 'SILTY']::soil_type_enum[], 'Bulb crop sensitive to weed competition.'),
    ('Carrot', 'Karot', 'Daucus carota', 'ROOT', 85, 5.8, 6.8, 'DRY', ARRAY['LOAM', 'SANDY']::soil_type_enum[], 'Deep loose soil preferred for smooth root growth.'),
    ('Yardlong String Bean', 'Sitaw', 'Vigna unguiculata subsp. sesquipedalis', 'FRUIT', 48, 5.5, 6.5, 'YEAR_ROUND', ARRAY['LOAM', 'SANDY', 'SILTY']::soil_type_enum[], 'Nitrogen-fixing legume vegetable.'),
    ('Lettuce', 'Litsugas', 'Lactuca sativa', 'LEAFY', 45, 6.0, 7.0, 'WET', ARRAY['LOAM', 'PEATY', 'SILTY']::soil_type_enum[], 'Fast-growing tender leafy vegetable.'),
    ('Cucumber', 'Pipino', 'Cucumis sativus', 'FRUIT', 50, 6.0, 6.8, 'YEAR_ROUND', ARRAY['LOAM', 'SANDY', 'SILTY']::soil_type_enum[], 'Vining fruit crop requiring support or space.'),
    ('Okra', 'Okra', 'Abelmoschus esculentus', 'FRUIT', 45, 6.0, 7.5, 'WET', ARRAY['LOAM', 'CLAY', 'SANDY']::soil_type_enum[], 'Drought-tolerant tropical vegetable.'),
    ('Corn', 'Mais', 'Zea mays', 'FRUIT', 65, 5.8, 7.0, 'YEAR_ROUND', ARRAY['LOAM', 'CLAY', 'SILTY']::soil_type_enum[], 'Heavy feeder crop, good support structure.'),
    ('Squash', 'Kalabasa', 'Cucurbita moschata', 'FRUIT', 80, 5.6, 6.8, 'WET', ARRAY['LOAM', 'CLAY', 'SILTY']::soil_type_enum[], 'Sprawling vine crop high in Vitamin A.'),
    ('Water Spinach', 'Kangkong', 'Ipomoea aquatica', 'LEAFY', 30, 5.5, 7.0, 'YEAR_ROUND', ARRAY['LOAM', 'SILTY', 'PEATY']::soil_type_enum[], 'Water spinach, fast growing leafy green.'),
    ('Bitter Gourd', 'Ampalaya', 'Momordica charantia', 'FRUIT', 55, 6.0, 6.7, 'YEAR_ROUND', ARRAY['LOAM', 'SANDY', 'SILTY']::soil_type_enum[], 'High-value medicinal vining crop cultivated with bamboo trellises.')
ON CONFLICT (name) DO NOTHING;

INSERT INTO public.dss_rules (crop_a, crop_b, relationship, reason, source)
VALUES
    ('Tomato', 'Lettuce', 'BENEFICIAL', 'Lettuce shades roots, reduces moisture loss', 'BPI Guidelines'),
    ('Tomato', 'Eggplant', 'ANTAGONIST', 'Same family, shared pests (fruit borer)', 'DA-BAR Companion Guide'),
    ('Tomato', 'Carrot', 'BENEFICIAL', 'Carrot aerates soil around tomato roots', 'BPI Guidelines'),
    ('Cucumber', 'Corn', 'BENEFICIAL', 'Corn provides climbing support', 'DA-BAR Companion Guide'),
    ('Eggplant', 'Yardlong String Bean', 'BENEFICIAL', 'Beans fix nitrogen for eggplant', 'DA-BAR Companion Guide'),
    ('Cabbage', 'Onion', 'BENEFICIAL', 'Onion repels cabbage loopers', 'BPI Guidelines'),
    ('Lettuce', 'Carrot', 'BENEFICIAL', 'Companion harvest timing aligned', 'BPI Guidelines'),
    ('Onion', 'Yardlong String Bean', 'ANTAGONIST', 'Onion inhibits bean growth', 'DA-BAR Companion Guide'),
    ('Bitter Gourd', 'Squash', 'ANTAGONIST', 'Both are cucurbits sharing identical pests and competing for vine space', 'BPI Guidelines'),
    ('Corn', 'Squash', 'BENEFICIAL', 'Squash ground cover suppresses weeds around corn stalks', 'DA-BAR Companion Guide')
ON CONFLICT DO NOTHING;

INSERT INTO public.notifications (title, body, notification_type, is_read)
VALUES
    ('📢 System Update v1.2.0', 'MapTanim Admin deployed direct-to-soil grid performance optimizations and sync upgrades.', 'SYSTEM_UPDATE', FALSE),
    ('🌾 New Crop Added: Sweet Corn', 'Admin added Sweet Corn (Zea mays) to the crop planting library. Tap to view growth stages.', 'CROP_ADDITION', FALSE),
    ('🛠 Bug Fix & Security Patch', 'Resolved offline database synchronization and plot status updating issues.', 'BUG_FIX', TRUE)
ON CONFLICT DO NOTHING;
