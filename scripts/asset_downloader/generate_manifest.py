"""
Asset Catalog Manifest Generator
"""

import json
from datetime import datetime, timezone
from pathlib import Path
from typing import Dict, Any, List
try:
    from .config import ASSETS_DIR, METADATA_DIR, PHOTOS_DIR, SPRITES_DIR
    from .process_images import compute_sha256
except ImportError:
    from config import ASSETS_DIR, METADATA_DIR, PHOTOS_DIR, SPRITES_DIR
    from process_images import compute_sha256


def generate_asset_manifest() -> Path:
    """
    Scans assets directory and compiles a unified manifest.json listing relative paths,
    categories, SHA256 checksums, and metadata linkage.
    """
    manifest: Dict[str, Any] = {
        "generator": "MapTanim Asset Pipeline v1.0",
        "generated_at": datetime.now(timezone.utc).isoformat(),
        "total_assets": 0,
        "categories": {
            "sprites": [],
            "photos": [],
            "metadata": []
        }
    }

    total_count = 0

    # 1. Index Sprites
    if SPRITES_DIR.exists():
        for p in SPRITES_DIR.rglob("*.*"):
            if p.suffix.lower() in (".png", ".webp", ".jpg"):
                rel = str(p.relative_to(ASSETS_DIR)).replace("\\", "/")
                manifest["categories"]["sprites"].append({
                    "path": rel,
                    "filename": p.name,
                    "sha256": compute_sha256(p)
                })
                total_count += 1

    # 2. Index Photos
    if PHOTOS_DIR.exists():
        for p in PHOTOS_DIR.rglob("*.*"):
            if p.suffix.lower() in (".png", ".webp", ".jpg"):
                rel = str(p.relative_to(ASSETS_DIR)).replace("\\", "/")
                manifest["categories"]["photos"].append({
                    "path": rel,
                    "filename": p.name,
                    "sha256": compute_sha256(p)
                })
                total_count += 1

    # 3. Index Metadata
    if METADATA_DIR.exists():
        for p in METADATA_DIR.rglob("*.json"):
            rel = str(p.relative_to(ASSETS_DIR)).replace("\\", "/")
            manifest["categories"]["metadata"].append({
                "path": rel,
                "filename": p.name,
                "sha256": compute_sha256(p)
            })
            total_count += 1

    manifest["total_assets"] = total_count

    output_path = ASSETS_DIR / "manifest.json"
    with open(output_path, "w", encoding="utf-8") as f:
        json.dump(manifest, f, indent=2, ensure_ascii=False)

    return output_path
