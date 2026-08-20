"""
Metadata Generator adhering strictly to Doc 35 Section 11, Doc 36 & Doc 39 Schemas
"""

import json
import hashlib
from datetime import datetime, timedelta
from pathlib import Path
from typing import Dict, Any, List, Optional
try:
    from .config import METADATA_DIR, MOBILE_METADATA_DIR
except ImportError:
    from config import METADATA_DIR, MOBILE_METADATA_DIR

def compute_hash(val: str) -> str:
    return hashlib.sha256(val.encode('utf-8')).hexdigest()

def create_crop_metadata(
    crop_id: str,
    common_name: str,
    local_name_ph: str,
    scientific_name: str,
    taxonomic_family: str,
    category: str,
    days_to_harvest: str,
    optimal_ph: str,
    temp_range: str,
    description: str,
    primary_photo: str,
    thumbnail: str,
    author: str,
    license_name: str,
    source_url: str,
    sha256: str,
    varieties: Optional[List[Dict[str, Any]]] = None
) -> Path:
    """Generates standardized crop metadata JSON file with variety and timeline specifications."""
    if sha256 == "pending_processing" or not sha256:
        sha256 = compute_hash(source_url)

    today_str = datetime.now().strftime("%Y-%m-%d")

    metadata = {
        "id": f"crop_{crop_id}",
        "category": "crop",
        "common_name": common_name,
        "local_name_ph": local_name_ph,
        "scientific_name": scientific_name,
        "taxonomic_family": taxonomic_family,
        "crop_type": category,
        "growing_specifications": {
            "days_to_harvest": days_to_harvest,
            "optimal_soil_ph": optimal_ph,
            "optimal_temperature_c": temp_range,
            "description": description
        },
        "varieties": varieties or [
            {
                "variety_id": f"var_{crop_id}_standard",
                "variety_name": "Standard Philippine Hybrid",
                "local_name_ph": f"{local_name_ph} Hybrid",
                "growth_duration_days": int(days_to_harvest.split('-')[0]) if '-' in days_to_harvest else 60,
                "stage_days": {
                    "stage1_sprout": 5,
                    "stage2_seedling": 12,
                    "stage3_vegetative": 20,
                    "stage4_flowering": 16,
                    "stage5_harvest": 7
                },
                "optimal_seasons": ["YEAR_ROUND"],
                "watering_interval_days": 2,
                "fertilize_interval_days": 14,
                "sample_planted_date": today_str,
                "sample_expected_harvest_date": (datetime.now() + timedelta(days=60)).strftime("%Y-%m-%d"),
                "description": f"Standard commercial cultivar for Philippine agricultural regions."
            }
        ],
        "media": {
            "primary_photo_url": primary_photo,
            "thumbnail_url": thumbnail,
            "source_url": source_url,
            "author": author,
            "license": license_name,
            "hash_sha256": sha256
        }
    }
    
    output_path = METADATA_DIR / "crops" / f"{crop_id}.json"
    output_path.parent.mkdir(parents=True, exist_ok=True)
    with open(output_path, "w", encoding="utf-8") as f:
        json.dump(metadata, f, indent=2, ensure_ascii=False)

    mobile_output_path = MOBILE_METADATA_DIR / "crops" / f"{crop_id}.json"
    mobile_output_path.parent.mkdir(parents=True, exist_ok=True)
    with open(mobile_output_path, "w", encoding="utf-8") as f:
        json.dump(metadata, f, indent=2, ensure_ascii=False)
    
    return output_path


def create_disease_metadata(
    disease_id: str,
    common_name: str,
    local_name_ph: str,
    scientific_name: str,
    taxonomic_family: str,
    affected_crops: List[str],
    symptoms: List[str],
    organic_treatment: str,
    chemical_treatment: str,
    cultural_prevention: str,
    temp_range: str,
    humidity_threshold: int,
    primary_photo: str,
    thumbnail: str,
    author: str,
    license_name: str,
    source_url: str,
    sha256: str
) -> Path:
    """Generates standardized disease metadata JSON file matching Doc 35 Section 11."""
    if sha256 == "pending_processing" or not sha256:
        sha256 = compute_hash(source_url)

    metadata = {
        "id": disease_id,
        "category": "disease",
        "common_name": common_name,
        "local_name_ph": local_name_ph,
        "scientific_name": scientific_name,
        "taxonomic_family": taxonomic_family,
        "affected_crops": affected_crops,
        "symptoms": symptoms,
        "treatment_protocol": {
            "organic": organic_treatment,
            "chemical": chemical_treatment,
            "cultural_prevention": cultural_prevention
        },
        "optimal_conditions": {
            "temperature_range_c": temp_range,
            "humidity_threshold_pct": humidity_threshold
        },
        "media": {
            "primary_photo_url": primary_photo,
            "thumbnail_url": thumbnail,
            "source_url": source_url,
            "author": author,
            "license": license_name,
            "hash_sha256": sha256
        }
    }

    output_path = METADATA_DIR / "diseases" / f"{disease_id}.json"
    output_path.parent.mkdir(parents=True, exist_ok=True)
    with open(output_path, "w", encoding="utf-8") as f:
        json.dump(metadata, f, indent=2, ensure_ascii=False)

    return output_path
