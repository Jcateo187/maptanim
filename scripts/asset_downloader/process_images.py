"""
Image Processing, Compression & Resizing Module
"""

import hashlib
from pathlib import Path
from typing import Optional, Tuple, Dict, Any
from PIL import Image, ImageOps
try:
    from .config import MAX_PHOTO_SIZE, THUMBNAIL_SIZE, WEBP_QUALITY
except ImportError:
    from config import MAX_PHOTO_SIZE, THUMBNAIL_SIZE, WEBP_QUALITY


def compute_sha256(file_path: Path) -> str:
    """Computes SHA256 hash of a file."""
    hasher = hashlib.sha256()
    with open(file_path, "rb") as f:
        for chunk in iter(lambda: f.read(65536), b""):
            hasher.update(chunk)
    return hasher.hexdigest()


def process_image(
    input_path: Path,
    output_path: Path,
    thumbnail_path: Optional[Path] = None,
    max_size: Tuple[int, int] = MAX_PHOTO_SIZE,
    thumb_size: Tuple[int, int] = THUMBNAIL_SIZE,
    quality: int = WEBP_QUALITY,
) -> Dict[str, Any]:
    """
    Processes an input image:
    1. Fixes EXIF orientation.
    2. Resizes while maintaining aspect ratio within max_size bounds.
    3. Saves optimized WebP.
    4. Generates square thumbnail if requested.
    5. Computes SHA256 hash.
    """
    output_path.parent.mkdir(parents=True, exist_ok=True)
    with Image.open(input_path) as img:
        # Correct orientation
        img = ImageOps.exif_transpose(img)
        
        # Convert RGB/RGBA
        if img.mode not in ("RGB", "RGBA"):
            img = img.convert("RGBA" if "transparency" in img.info else "RGB")

        # Copy for thumbnail
        img_copy = img.copy()

        # Resize main image keeping aspect ratio
        img.thumbnail(max_size, Image.Resampling.LANCZOS)
        
        # Determine format based on output extension
        fmt = "WEBP" if output_path.suffix.lower() == ".webp" else "PNG"
        img.save(output_path, format=fmt, quality=quality, optimize=True)

        sha256_hash = compute_sha256(output_path)
        thumb_hash = None

        if thumbnail_path:
            thumbnail_path.parent.mkdir(parents=True, exist_ok=True)
            # Create square thumbnail
            thumb_img = ImageOps.fit(img_copy, thumb_size, Image.Resampling.LANCZOS)
            thumb_fmt = "WEBP" if thumbnail_path.suffix.lower() == ".webp" else "PNG"
            thumb_img.save(thumbnail_path, format=thumb_fmt, quality=quality, optimize=True)
            thumb_hash = compute_sha256(thumbnail_path)

        return {
            "processed_path": str(output_path),
            "thumbnail_path": str(thumbnail_path) if thumbnail_path else None,
            "width": img.width,
            "height": img.height,
            "sha256": sha256_hash,
            "thumb_sha256": thumb_hash,
        }
