"""
iNaturalist Agricultural Observation Downloader Module
"""

import json
import urllib.request
import urllib.parse
from typing import Dict, Any, List
try:
    from .verify_license import verify_license_compliance
except ImportError:
    from verify_license import verify_license_compliance

INATURALIST_API_URL = "https://api.inaturalist.org/v1/observations"

def fetch_inaturalist_observations(taxon_name: str, limit: int = 3) -> List[Dict[str, Any]]:
    """
    Fetches CC-licensed observation photos for agricultural species and pests from iNaturalist API.
    """
    params = {
        "q": taxon_name,
        "licensed": "true",
        "photo_licensed": "true",
        "per_page": str(limit),
        "quality_grade": "research"
    }

    url = f"{INATURALIST_API_URL}?{urllib.parse.urlencode(params)}"
    headers = {"User-Agent": "MapTanimAssetPipeline/1.0 (philippine.agriculture@maptanim.app)"}

    results = []
    try:
        req = urllib.request.Request(url, headers=headers)
        with urllib.request.urlopen(req, timeout=10) as resp:
            data = json.loads(resp.read().decode())
            for obs in data.get("results", []):
                photos = obs.get("photos", [])
                if not photos:
                    continue
                photo = photos[0]
                license_code = photo.get("license_code", "")
                is_valid, norm_license = verify_license_compliance(license_code)
                if not is_valid:
                    continue

                url_large = photo.get("url", "").replace("square", "large")
                user = obs.get("user", {}).get("login", "iNaturalist Observer")

                results.append({
                    "id": f"inat_{obs.get('id')}",
                    "species_guess": obs.get("species_guess", taxon_name),
                    "image_url": url_large,
                    "author": f"{user} (via iNaturalist)",
                    "license": norm_license,
                    "source_url": obs.get("uri", "")
                })
    except Exception as e:
        print(f"[iNaturalist API Warning] Search for '{taxon_name}' failed: {e}")

    return results
