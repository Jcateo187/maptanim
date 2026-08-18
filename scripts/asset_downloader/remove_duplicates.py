"""
Two-Tier Deduplication Engine (SHA256 Checksum + Perceptual Hash)
"""

import hashlib
from pathlib import Path
from typing import List, Set, Tuple
from PIL import Image

def compute_phash(image_path: Path, hash_size: int = 8) -> str:
    """
    Computes average/perceptual hash of an image for visual similarity detection.
    """
    try:
        with Image.open(image_path) as img:
            img = img.convert("L").resize((hash_size, hash_size), Image.Resampling.LANCZOS)
            pixels = list(img.getdata())
            avg = sum(pixels) / len(pixels)
            bits = "".join("1" if p > avg else "0" for p in pixels)
            return f"{int(bits, 2):016x}"
    except Exception:
        return ""

def hamming_distance(hash1: str, hash2: str) -> int:
    """Calculates Hamming distance between two hex hashes."""
    if not hash1 or not hash2 or len(hash1) != len(hash2):
        return 999
    val1 = int(hash1, 16)
    val2 = int(hash2, 16)
    return bin(val1 ^ val2).count("1")

def deduplicate_directory(target_dir: Path, threshold: int = 4) -> Tuple[List[Path], List[Path]]:
    """
    Scans a directory for duplicates using SHA256 and perceptual hash (pHash).
    Returns (unique_files, deleted_duplicates).
    """
    seen_sha256: Set[str] = set()
    phash_list: List[Tuple[Path, str]] = []
    unique_files: List[Path] = []
    deleted_files: List[Path] = []

    for file_path in target_dir.rglob("*.*"):
        if file_path.suffix.lower() not in (".png", ".webp", ".jpg", ".jpeg"):
            continue

        # 1. Check Exact SHA256 Match
        hasher = hashlib.sha256()
        with open(file_path, "rb") as f:
            for chunk in iter(lambda: f.read(65536), b""):
                hasher.update(chunk)
        file_sha256 = hasher.hexdigest()

        if file_sha256 in seen_sha256:
            deleted_files.append(file_path)
            file_path.unlink(missing_ok=True)
            continue

        # 2. Check Visual Similarity pHash
        phash = compute_phash(file_path)
        is_visual_duplicate = False
        for _, existing_phash in phash_list:
            if hamming_distance(phash, existing_phash) <= threshold:
                is_visual_duplicate = True
                break

        if is_visual_duplicate:
            deleted_files.append(file_path)
            file_path.unlink(missing_ok=True)
        else:
            seen_sha256.add(file_sha256)
            phash_list.append((file_path, phash))
            unique_files.append(file_path)

    return unique_files, deleted_files
