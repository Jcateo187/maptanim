/**
 * MapTanim - Upload Official Metadata Images to Supabase Storage (100% Free • No Credit Card)
 * 
 * Usage:
 *   node scripts/upload_metadata_to_supabase.js
 */

import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const SUPABASE_URL = process.env.VITE_SUPABASE_URL || 'https://ojilvcglpzbtpjxguhzj.supabase.co';
const SUPABASE_KEY = process.env.VITE_SUPABASE_ANON_KEY || 'sb_publishable_fH5qY2HaAg-coV89IxOl2Q_Xf9ySGMU';

const projectRoot = path.resolve(__dirname, '..');
const metadataDir = path.join(projectRoot, 'mobile/app/src/main/assets/metadata');

const CROP_IMAGES_DIR = path.join(metadataDir, 'crops_images');
const PEST_IMAGES_DIR = path.join(metadataDir, 'pest');
const SOIL_IMAGES_DIR = path.join(metadataDir, 'soil_images');

const BUCKET_NAME = 'crop-images';

async function uploadToSupabase(filePath, fileName) {
  const fileBuffer = fs.readFileSync(filePath);
  const uploadUrl = `${SUPABASE_URL}/storage/v1/object/${BUCKET_NAME}/${fileName}`;

  const res = await fetch(uploadUrl, {
    method: 'POST',
    headers: {
      'apikey': SUPABASE_KEY,
      'Authorization': `Bearer ${SUPABASE_KEY}`,
      'Content-Type': 'image/png',
      'x-upsert': 'true',
    },
    body: fileBuffer,
  });

  if (!res.ok) {
    const errorText = await res.text();
    throw new Error(`Upload failed (${res.status}): ${errorText}`);
  }

  const publicUrl = `${SUPABASE_URL}/storage/v1/object/public/${BUCKET_NAME}/${fileName}`;
  return publicUrl;
}

async function updateCropDatabase(cropName, publicUrl) {
  const queryUrl = `${SUPABASE_URL}/rest/v1/crops?name=ilike.*${encodeURIComponent(cropName)}*`;
  const res = await fetch(queryUrl, {
    method: 'PATCH',
    headers: {
      'apikey': SUPABASE_KEY,
      'Authorization': `Bearer ${SUPABASE_KEY}`,
      'Content-Type': 'application/json',
      'Prefer': 'return=minimal',
    },
    body: JSON.stringify({
      image_url: publicUrl,
      updated_at: new Date().toISOString(),
    }),
  });
  return res.ok;
}

async function main() {
  console.log(`🌾 MapTanim Supabase Storage Uploader (100% Free • No Card Needed)`);
  console.log(`🎯 Supabase Endpoint: ${SUPABASE_URL}\n`);

  const cropFiles = [
    { name: 'Tomato', file: 'tomato.png' },
    { name: 'Eggplant', file: 'eggplant.png' },
    { name: 'Chili', file: 'sili.png' },
    { name: 'Cabbage', file: 'cabbage.png' },
    { name: 'Pechay', file: 'pechay.png' },
    { name: 'Onion', file: 'onion.png' },
    { name: 'Carrot', file: 'carrot.png' },
    { name: 'Yardlong String Bean', file: 'sitaw.png' },
    { name: 'Lettuce', file: 'lettuce.png' },
    { name: 'Cucumber', file: 'pipino.png' },
    { name: 'Bitter Gourd', file: 'ampalaya.png' },
    { name: 'Okra', file: 'okra.png' },
    { name: 'Corn', file: 'corn.png' },
    { name: 'Squash', file: 'pumpkin.png' },
    { name: 'Water Spinach', file: 'kangkong.png' },
  ];

  console.log(`📦 Uploading 15 Official Crop Images to '${BUCKET_NAME}' bucket...`);
  for (const crop of cropFiles) {
    const fullPath = path.join(CROP_IMAGES_DIR, crop.file);
    if (!fs.existsSync(fullPath)) continue;

    process.stdout.write(`  Uploading ${crop.file}... `);
    try {
      const publicUrl = await uploadToSupabase(fullPath, crop.file);
      await updateCropDatabase(crop.name, publicUrl);
      console.log(`✔`);
      console.log(`    ↳ Public URL: ${publicUrl}`);
    } catch (err) {
      console.log(`✘ ${err.message}`);
    }
  }

  console.log(`\n🎉 Supabase Storage sync completed!`);
}

main().catch(err => {
  console.error('Fatal error:', err);
  process.exit(1);
});
