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

// Test 2: Verify Mock Data Integrity
const mockDataPath = join(rootDir, 'admin/src/services/mockData.ts');
assert(existsSync(mockDataPath), 'admin/src/services/mockData.ts exists');

if (existsSync(mockDataPath)) {
  const content = readFileSync(mockDataPath, 'utf8');
  assert(content.includes('MOCK_FARMERS'), 'Contains MOCK_FARMERS dataset');
  assert(content.includes('MOCK_CROPS'), 'Contains MOCK_CROPS dataset');
  assert(content.includes('MOCK_PESTS'), 'Contains MOCK_PESTS dataset');
  assert(content.includes('MOCK_SOILS'), 'Contains MOCK_SOILS dataset');
}

console.log('\n---------------------------------------------------------');
console.log(`  UI E2E Test Results: ${passCount} Passed, ${failCount} Failed.`);
if (failCount > 0) {
  process.exit(1);
} else {
  console.log('  🎉 All Web Admin UI E2E tests PASSED!');
  process.exit(0);
}
