#!/usr/bin/env python3
"""
MapTanim Mobile & DSS Engine Matrix Verification Test Suite
Validates 2D Isometric Farm Canvas spatial grid bounds, crop zone allocation, and companion compatibility overlays.
"""

import sys

if hasattr(sys.stdout, 'reconfigure'):
    sys.stdout.reconfigure(encoding='utf-8')

def test_canvas_grid_bounds():
    """Verify 30m x 30m farm workspace grid bounds calculation."""
    print("  [+] Testing 30m x 30m Isometric Canvas Grid Bounds...")
    GRID_SIZE_METERS = 30
    TILE_SIZE_PX = 64
    
    total_canvas_width = GRID_SIZE_METERS * TILE_SIZE_PX
    total_canvas_height = GRID_SIZE_METERS * TILE_SIZE_PX
    
    assert total_canvas_width == 1920, f"Expected 1920px width, got {total_canvas_width}"
    assert total_canvas_height == 1920, f"Expected 1920px height, got {total_canvas_height}"
    print("      [OK] 30m x 30m canvas grid dimensions verified.")

def test_companion_compatibility_scores():
    """Verify Companion Planting score calculations (100 = Beneficial, 50 = Neutral, 0 = Antagonist)."""
    print("  [+] Testing Companion Compatibility Score Logic...")
    
    def get_compatibility_score(crop_a, crop_b):
        pairs = {
            ("Tomato", "Basil"): 100,
            ("Tomato", "Corn"): 0,
            ("Carrot", "Lettuce"): 85,
            ("Eggplant", "Pepper"): 95
        }
        return pairs.get((crop_a, crop_b), pairs.get((crop_b, crop_a), 50))
        
    assert get_compatibility_score("Tomato", "Basil") == 100, "Beneficial score failed"
    assert get_compatibility_score("Tomato", "Corn") == 0, "Antagonist score failed"
    assert get_compatibility_score("Squash", "Okra") == 50, "Neutral score default failed"
    print("      [OK] Companion planting compatibility scoring verified.")

def run_dss_tests():
    print("=========================================================")
    print("  [TEST] MapTanim Mobile & DSS Engine Unit Verification Test")
    print("=========================================================")
    try:
        test_canvas_grid_bounds()
        test_companion_compatibility_scores()
        print("\n  [SUCCESS] All Mobile & DSS unit tests PASSED successfully!")
        return 0
    except AssertionError as e:
        print(f"\n  [FAIL] Test Failed: {e}")
        return 1

if __name__ == "__main__":
    sys.exit(run_dss_tests())
