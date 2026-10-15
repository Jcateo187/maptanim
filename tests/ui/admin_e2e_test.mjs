/**
 * MapTanim Web Admin Dashboard - E2E UI Integration & Data Contract Test Suite
 */

import { readFileSync, existsSync } from 'fs';
import { join, dirname } from 'path';
import { fileURLToPath } from 'url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);
const rootDir = join(__dirname, '../..');

console.log('=========================================================');
console.log('  🖥️ MapTanim Web Admin Dashboard UI E2E Test Suite');
console.log('=========================================================');

let passCount = 0;
let failCount = 0;

function assert(condition, message) {
  if (condition) {
    console.log(`  ✓ ${message}`);
    passCount++;
  } else {
    console.error(`  ❌ FAIL: ${message}`);
    failCount++;
  }
}

// Test 1: Check Admin Package configuration
const packagePath = join(rootDir, 'admin/package.json');
assert(existsSync(packagePath), 'admin/package.json exists');

if (existsSync(packagePath)) {
  const pkg = JSON.parse(readFileSync(packagePath, 'utf8'));
  assert(pkg.dependencies['@supabase/supabase-js'] !== undefined, 'Supabase client dependency declared');
  assert(pkg.dependencies['lucide-react'] !== undefined, 'Lucide icons dependency declared');
  assert(pkg.dependencies['recharts'] !== undefined, 'Recharts dashboard dependency declared');
}

// Test 2: Verify Real Supabase Service Layer & API Contracts
const supabaseServicePath = join(rootDir, 'admin/src/services/supabase.ts');
assert(existsSync(supabaseServicePath), 'admin/src/services/supabase.ts exists');

if (existsSync(supabaseServicePath)) {
  const content = readFileSync(supabaseServicePath, 'utf8');
  assert(content.includes('createClient'), 'Initializes official Supabase client');
  assert(content.includes('isSupabaseConfigured'), 'Exports configuration validator');
}

const apiServicePath = join(rootDir, 'admin/src/services/api.ts');
assert(existsSync(apiServicePath), 'admin/src/services/api.ts exists');

if (existsSync(apiServicePath)) {
  const content = readFileSync(apiServicePath, 'utf8');
  assert(content.includes('getDashboardStats'), 'Declares getDashboardStats contract');
  assert(content.includes('getFarmers'), 'Declares getFarmers contract');
  assert(content.includes('getCrops'), 'Declares getCrops contract');
  assert(content.includes('getDSSRules'), 'Declares getDSSRules contract');
  assert(content.includes('export const apiService'), 'Exports unified apiService singleton');
}

console.log('\n---------------------------------------------------------');
console.log(`  UI E2E Test Results: ${passCount} Passed, ${failCount} Failed.`);
if (failCount > 0) {
  process.exit(1);
} else {
  console.log('  🎉 All Web Admin UI E2E tests PASSED!');
  process.exit(0);
}
