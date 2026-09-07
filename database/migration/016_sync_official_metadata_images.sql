-- ==============================================================================
-- Migration 016: Sync Official Metadata Images for Philippine Crops
-- Target: Supabase PostgreSQL (public schema)
-- Replaces all generic/external placeholder URLs with authentic MapTanim metadata assets
-- ==============================================================================

UPDATE public.crops SET image_url = '/metadata/crops_images/tomato.png' WHERE name ILIKE '%Tomato%' OR local_name ILIKE '%Kamatis%';
UPDATE public.crops SET image_url = '/metadata/crops_images/eggplant.png' WHERE name ILIKE '%Eggplant%' OR local_name ILIKE '%Talong%';
UPDATE public.crops SET image_url = '/metadata/crops_images/sili.png' WHERE name ILIKE '%Chili%' OR name ILIKE '%Pepper%' OR local_name ILIKE '%Sili%';
UPDATE public.crops SET image_url = '/metadata/crops_images/cabbage.png' WHERE name ILIKE '%Cabbage%' OR local_name ILIKE '%Repolyo%';
UPDATE public.crops SET image_url = '/metadata/crops_images/pechay.png' WHERE name ILIKE '%Pechay%' OR name ILIKE '%Bok Choy%';
UPDATE public.crops SET image_url = '/metadata/crops_images/onion.png' WHERE name ILIKE '%Onion%' OR local_name ILIKE '%Sibuyas%';
UPDATE public.crops SET image_url = '/metadata/crops_images/carrot.png' WHERE name ILIKE '%Carrot%' OR local_name ILIKE '%Karot%';
UPDATE public.crops SET image_url = '/metadata/crops_images/sitaw.png' WHERE name ILIKE '%Bean%' OR local_name ILIKE '%Sitaw%';
UPDATE public.crops SET image_url = '/metadata/crops_images/lettuce.png' WHERE name ILIKE '%Lettuce%' OR local_name ILIKE '%Litsugas%';
UPDATE public.crops SET image_url = '/metadata/crops_images/pipino.png' WHERE name ILIKE '%Cucumber%' OR local_name ILIKE '%Pipino%';
UPDATE public.crops SET image_url = '/metadata/crops_images/ampalaya.png' WHERE name ILIKE '%Bitter Gourd%' OR local_name ILIKE '%Ampalaya%';
UPDATE public.crops SET image_url = '/metadata/crops_images/okra.png' WHERE name ILIKE '%Okra%';
UPDATE public.crops SET image_url = '/metadata/crops_images/corn.png' WHERE name ILIKE '%Corn%' OR local_name ILIKE '%Mais%';
UPDATE public.crops SET image_url = '/metadata/crops_images/pumpkin.png' WHERE name ILIKE '%Squash%' OR name ILIKE '%Pumpkin%' OR local_name ILIKE '%Kalabasa%';
UPDATE public.crops SET image_url = '/metadata/crops_images/kangkong.png' WHERE name ILIKE '%Spinach%' OR name ILIKE '%Kangkong%';
