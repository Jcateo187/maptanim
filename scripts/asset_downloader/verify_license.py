"""
License Verification & Compliance Module
"""

from typing import Dict, Tuple
try:
    from .config import ALLOWED_LICENSES
except ImportError:
    from config import ALLOWED_LICENSES

def verify_license_compliance(license_str: str) -> Tuple[bool, str]:
    """
    Validates if an image/asset license is compliant with MapTanim legal requirements (CC0, CC-BY, CC-BY-SA, Public Domain).
    Returns (is_valid, normalized_license_name).
    """
    if not license_str:
        return False, "Unknown / Missing License"

    norm = license_str.strip().lower()

    # Exact or substring matches for allowed licenses
    if "cc0" in norm or "public domain" in norm or "zero" in norm:
        return True, "CC0 1.0 Universal"
    elif "by-sa 4.0" in norm or "by-sa/4.0" in norm:
        return True, "CC-BY-SA 4.0"
    elif "by-sa 3.0" in norm or "by-sa/3.0" in norm:
        return True, "CC-BY-SA 3.0"
    elif "by 4.0" in norm or "by/4.0" in norm:
        return True, "CC-BY 4.0"
    elif "by 3.0" in norm or "by/3.0" in norm:
        return True, "CC-BY 3.0"
    elif "cc by" in norm:
        return True, "CC-BY 4.0"
    
    # Explicit rejection for forbidden sources
    for forbidden in ["shutterstock", "getty", "adobe", "all rights reserved", "copyright"]:
        if forbidden in norm:
            return False, f"Prohibited License: {license_str}"

    return False, f"Unverified License: {license_str}"
