/**
 * MapTanim Admin - Supabase Live Connection Tester
 * Validates REST API connectivity and tests core tables across the backend.
 * 
 * Usage:
 *   npm run supabase:test
 *   or: node scripts/test-connection.js
 */

import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

// Read .env if available
const envPath = path.resolve(__dirname, '../.env');
let envSupabaseUrl = '';
let envSupabaseKey = '';

if (fs.existsSync(envPath)) {
  const content = fs.readFileSync(envPath, 'utf8');
  for (const line of content.split('\n')) {
    const trimmed = line.trim();
    if (trimmed.startsWith('VITE_SUPABASE_URL=')) {
      envSupabaseUrl = trimmed.split('=')[1].trim();
    } else if (trimmed.startsWith('VITE_SUPABASE_ANON_KEY=')) {
      envSupabaseKey = trimmed.split('=')[1].trim();
    }
  }
}

const SUPABASE_URL = envSupabaseUrl || 'https://ojilvcglpzbtpjxguhzj.supabase.co';
const SUPABASE_ANON_KEY = envSupabaseKey || 'sb_publishable_fH5qY2HaAg-coV89IxOl2Q_Xf9ySGMU';

console.log('='.repeat(65));
console.log('🌱 MapTanim Admin <-> Supabase Backend Connection Test');
console.log('='.repeat(65));
console.log(`Endpoint:    ${SUPABASE_URL}`);
console.log(`Project Ref: ojilvcglpzbtpjxguhzj`);
console.log(`Auth Mode:   Anon API Key`);
console.log('-'.repeat(65));

const tablesToTest = [
  { name: 'crops', desc: 'Canonical 15 Philippine Crops' },
  { name: 'dss_rules', desc: 'Companion Planting Rule Matrix' },
  { name: 'feedback', desc: 'Farmer Support Tickets Queue' },
  { name: 'profiles', desc: 'Farmer Accounts & Profiles' },
  { name: 'farms', desc: 'Registered Smallholder Farms' },
  { name: 'crop_plots', desc: 'Active Isometric Plots' },
  { name: 'notifications', desc: 'System & Advisory Notifications' },
  { name: 'harvest_records', desc: 'Harvest & Post-Harvest Logs' }
];

async function testTable(table) {
  const startTime = Date.now();
  const url = `${SUPABASE_URL}/rest/v1/${table.name}?select=*&limit=1`;
  try {
    const res = await fetch(url, {
      method: 'GET',
      headers: {
        'apikey': SUPABASE_ANON_KEY,
        'Authorization': `Bearer ${SUPABASE_ANON_KEY}`,
        'Prefer': 'count=exact'
      }
    });

    const elapsed = Date.now() - startTime;
    if (res.ok) {
      const contentRange = res.headers.get('content-range');
      const totalCount = contentRange ? contentRange.split('/')[1] : 'OK';
      console.log(`  ✔ [${res.status}] ${table.name.padEnd(16)} -> Total: ${String(totalCount).padStart(4)} rows (${elapsed}ms) - ${table.desc}`);
      return true;
    } else {
      const errText = await res.text();
      console.log(`  ✘ [${res.status}] ${table.name.padEnd(16)} -> Failed: ${errText.slice(0, 80)} (${elapsed}ms)`);
      return false;
    }
  } catch (err) {
    console.log(`  ✘ [ERR] ${table.name.padEnd(16)} -> ${err.message}`);
    return false;
  }
}

async function run() {
  let passed = 0;
  for (const table of tablesToTest) {
    const ok = await testTable(table);
    if (ok) passed++;
  }

  console.log('-'.repeat(65));
  if (passed === tablesToTest.length) {
    console.log(`🎉 Supabase Backend Status: 100% OPERATIONAL (${passed}/${tablesToTest.length} tables verified)`);
  } else {
    console.log(`⚠️ Supabase Backend Status: Partial Connection (${passed}/${tablesToTest.length} tables verified)`);
  }
  console.log('='.repeat(65));
}

run();
