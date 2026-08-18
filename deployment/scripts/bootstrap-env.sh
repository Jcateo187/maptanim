#!/usr/bin/env bash
# MapTanim - Local Developer Environment Setup Script (Bash)

set -e

echo "========================================================="
echo "  🌱 MapTanim Developer Environment Setup & Verification"
echo "========================================================="
echo ""

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
ROOT_DIR="$( cd "$SCRIPT_DIR/../.." && pwd )"
cd "$ROOT_DIR"

# 1. Verify Prerequisites
echo "[1/4] Checking Prerequisites..."

if command -v java >/dev/null 2>&1; then
    echo "  ✅ Java detected."
else
    echo "  ❌ Java (JDK 17) is missing! Please install JDK 17."
fi

if command -v node >/dev/null 2>&1; then
    echo "  ✅ Node.js detected: $(node --version)"
else
    echo "  ❌ Node.js is missing! Please install Node 20+."
fi

if command -v docker >/dev/null 2>&1; then
    echo "  ✅ Docker detected: $(docker --version)"
else
    echo "  ⚠️ Docker not detected."
fi

# 2. Setup Environment Variables File
echo ""
echo "[2/4] Setting up Environment Variables..."
if [ ! -f "$ROOT_DIR/deployment/.env" ]; then
    cp "$ROOT_DIR/deployment/.env.example" "$ROOT_DIR/deployment/.env"
    echo "  ✅ Created deployment/.env from .env.example"
else
    echo "  ℹ️ deployment/.env already exists."
fi

# 3. Install Admin Dependencies
echo ""
echo "[3/4] Installing Admin Web Dashboard Dependencies..."
if [ -d "$ROOT_DIR/admin" ]; then
    cd "$ROOT_DIR/admin"
    npm install
    echo "  ✅ Admin dependencies installed."
    cd "$ROOT_DIR"
fi

# 4. Verify Gradle Wrapper
echo ""
echo "[4/4] Verifying Gradle Setup..."
if [ -f "$ROOT_DIR/gradlew" ]; then
    chmod +x "$ROOT_DIR/gradlew"
    echo "  ✅ Gradle wrapper present and executable."
else
    echo "  ❌ gradlew missing!"
fi

echo ""
echo "========================================================="
echo "  🎉 Environment setup check completed successfully!"
echo "========================================================="
