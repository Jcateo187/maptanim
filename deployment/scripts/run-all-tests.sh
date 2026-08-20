#!/usr/bin/env bash
# MapTanim - Unified Multi-Platform Test Runner (Bash)

set -e

echo "========================================================="
echo "  🧪 MapTanim Unified Test Suite Execution"
echo "========================================================="
echo ""

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
ROOT_DIR="$( cd "$SCRIPT_DIR/../.." && pwd )"
cd "$ROOT_DIR"

# Step 1: Admin Dashboard TypeScript & E2E Tests
echo "[1/5] Testing Admin Web Dashboard..."
cd "$ROOT_DIR/admin"
npx tsc --noEmit
cd "$ROOT_DIR"
node "$ROOT_DIR/tests/ui/admin_e2e_test.mjs"

# Step 2: Integration & Database Tests
echo ""
echo "[2/5] Running API & Database Integration Tests..."
python3 "$ROOT_DIR/tests/integration/api_integration_test.py"

# Step 3: Mobile & DSS Engine Unit Tests
echo ""
echo "[3/5] Running Mobile & DSS Engine Unit Tests..."
python3 "$ROOT_DIR/tests/unit/dss_matrix_test.py"

# Step 4: Performance & Load Benchmark Tests
echo ""
echo "[4/5] Running Performance Load Simulation Tests..."
python3 "$ROOT_DIR/tests/performance/load_simulation_test.py"

# Step 5: Mobile & Backend Gradle Unit Tests
echo ""
echo "[5/5] Running Mobile & Backend Gradle Unit Tests..."
cd "$ROOT_DIR"
chmod +x gradlew
./gradlew test

echo ""
echo "========================================================="
echo "  🎉 All test suites completed successfully!"
echo "========================================================="
