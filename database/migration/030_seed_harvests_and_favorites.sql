-- Migration 030: Seed Harvest Records and Favorites for Verification
INSERT INTO public.harvest_records (
    id, farm_id, plot_id, farm_name, plot_label, crop_name, crop_variety,
    planted_date, harvested_at, harvested_date, growing_duration_days,
    yield_kg, yield_units, quality_grade, quality_rating, seasonal_insight, notes
) VALUES (
    '00000000-0000-0000-0000-000000000031', '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000011',
    'Murcia Demonstration Backyard Bed', 'Bed 1 - Tomato', 'Tomato', 'Diamante Max F1',
    '2026-08-01', NOW(), '2026-10-15', 75, 14.5, 32, 'Grade A', 5,
    'Peak dry-season yield with high marketable quality.', 'Harvested firm red fruits.'
), (
    '00000000-0000-0000-0000-000000000032', '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000012',
    'Murcia Demonstration Backyard Bed', 'Bed 2 - Eggplant', 'Eggplant', 'Dumaguete Long Purple',
    '2026-07-25', NOW(), '2026-10-15', 82, 18.0, 45, 'Grade A', 5,
    'Vigorous vegetative growth and continuous fruiting.', 'Glossy purple, no borer damage.'
) ON CONFLICT (id) DO NOTHING;

INSERT INTO public.user_favorite_crops (user_id, crop_name) VALUES
('887ad02c-520f-4bb0-8bff-a700d4526871', 'Tomato'),
('887ad02c-520f-4bb0-8bff-a700d4526871', 'Eggplant'),
('887ad02c-520f-4bb0-8bff-a700d4526871', 'Pechay'),
('887ad02c-520f-4bb0-8bff-a700d4526871', 'Ampalaya'),
('887ad02c-520f-4bb0-8bff-a700d4526871', 'Sitaw')
ON CONFLICT (user_id, crop_name) DO NOTHING;
