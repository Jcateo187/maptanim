"""
Wikimedia Commons Open Access Downloader Module
"""

import json
import urllib.request
import urllib.parse
from pathlib import Path
from typing import Dict, Any, List, Optional
try:
    from .config import TEMP_DIR
    from .verify_license import verify_license_compliance
except ImportError:
    from config import TEMP_DIR
    from verify_license import verify_license_compliance

WIKIMEDIA_API_URL = "https://commons.wikimedia.org/w/api.php"

def search_wikimedia_commons(query: str, limit: int = 5) -> List[Dict[str, Any]]:
    """
    Queries Wikimedia Commons API for images matching the scientific or common crop name.
    Validates Creative Commons / Public Domain license.
    """
    params = {
        "action": "query",
        "format": "json",
        "generator": "search",
        "gsrsearch": f"file:{query}",
        "gsrnamespace": "6",
        "gsrlimit": str(limit),
        "prop": "imageinfo",
        "iiprop": "url|user|extmetadata"
    }

    url = f"{WIKIMEDIA_API_URL}?{urllib.parse.urlencode(params)}"
    headers = {"User-Agent": "MapTanimAssetPipeline/1.0 (philippine.agriculture@maptanim.app)"}

    results = []
    try:
        req = urllib.request.Request(url, headers=headers)
        with urllib.request.urlopen(req, timeout=10) as resp:
            data = json.loads(resp.read().decode())
            pages = data.get("query", {}).get("pages", {})
            for _, page_info in pages.items():
                image_info_list = page_info.get("imageinfo", [])
                if not image_info_list:
                    continue
                info = image_info_list[0]
                meta = info.get("extmetadata", {})
                
                license_name = meta.get("LicenseShortName", {}).get("value", "")
                is_valid, norm_license = verify_license_compliance(license_name)
                
                if not is_valid:
                    continue

                artist = meta.get("Artist", {}).get("value", "Wikimedia Contributor")
                # Clean HTML tags in artist string if present
                if "<" in artist and ">" in artist:
                    import re
                    artist = re.sub(r"<[^>]+>", "", artist)

                results.append({
                    "title": page_info.get("title", ""),
                    "image_url": info.get("url", ""),
                    "description_url": info.get("descriptionurl", ""),
                    "author": artist.strip() or "Wikimedia Commons",
                    "license": norm_license,
                    "original_license": license_name,
                })
    except Exception as e:
        print(f"[Wikimedia API Warning] Search for '{query}' failed: {e}")

    return results


def download_file(url: str, output_path: Path) -> bool:
    """Downloads a remote file with User-Agent header."""
    headers = {"User-Agent": "MapTanimAssetPipeline/1.0 (philippine.agriculture@maptanim.app)"}
    try:
        output_path.parent.mkdir(parents=True, exist_ok=True)
        req = urllib.request.Request(url, headers=headers)
        with urllib.request.urlopen(req, timeout=15) as resp, open(output_path, "wb") as out:
            out.write(resp.read())
        return True
    except Exception as e:
        print(f"[Download Failure] Could not fetch {url}: {e}")
        return False
