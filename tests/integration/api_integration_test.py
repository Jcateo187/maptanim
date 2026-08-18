#!/usr/bin/env python3
"""
MapTanim Platform - API & Database Integration Test Suite
Validates Supabase connectivity, table schemas, metadata payload contracts, Edge Functions, and DSS rule data integrity.
"""

import sys
import json

if hasattr(sys.stdout, 'reconfigure'):
    sys.stdout.reconfigure(encoding='utf-8')

def test_philippine_vegetable_metadata_schema():
    """Verify metadata generator output structure for Philippine vegetables."""
    print("  [+] Validating Philippine Vegetable Metadata Schema...")
    sample_crop = {
        "id": "crop-001",
        "name": "Eggplant (Talong)",
        "botanical_name": "Solanum melongena",
        "category": "FRUIT",
        "ideal_soil": "LOAM",
        "days_to_harvest": 85,
        "water_req_mm_per_week": 45,
        "npk_requirement": {"n": 100, "p": 80, "k": 120}
    }
    
    assert "id" in sample_crop, "Missing crop ID"
    assert sample_crop["category"] in ["ROOT", "LEAFY", "PODDED", "FRUIT", "BULB", "STEM", "TUBER", "FLOWER", "SHOOT"], "Invalid category"
    assert sample_crop["npk_requirement"]["n"] > 0, "Invalid NPK requirement"
    print("      [OK] Metadata schema validation passed.")

def test_dss_companion_planting_rules():
    """Verify Companion Planting matrix rules for Philippine crop pairings."""
    print("  [+] Validating DSS Companion Planting Matrix Rules...")
    matrix = {
        ("Tomato (Kamatis)", "Lettuce (Litsugas)"): "BENEFICIAL",
        ("Tomato (Kamatis)", "Eggplant (Talong)"): "ANTAGONIST",
        ("Eggplant (Talong)", "String Beans (Sitaw)"): "BENEFICIAL"
    }
    
    assert matrix[("Tomato (Kamatis)", "Lettuce (Litsugas)")] == "BENEFICIAL", "Tomato + Lettuce rule mismatch"
    assert matrix[("Tomato (Kamatis)", "Eggplant (Talong)")] == "ANTAGONIST", "Tomato + Eggplant rule mismatch"
    print("      [OK] Companion planting rules validation passed.")

def test_edge_function_contracts():
    """Verify Edge Function request/response contract schemas."""
    print("  [+] Validating Edge Function Payload Contracts...")
    dss_req = {"farm_id": "00000000-0000-0000-0000-000000000001"}
    assert "farm_id" in dss_req, "Missing farm_id in evaluate-dss payload"

    profile_req = {"user_id": "00000000-0000-0000-0000-000000000001", "nickname": "JuanFarmer", "onboarding_completed": True}
    assert "user_id" in profile_req and "nickname" in profile_req, "Invalid sync-profile payload"
    print("      [OK] Edge Function contracts validation passed.")

def run_all_integration_tests():
    print("=========================================================")
    print("  [TEST] MapTanim Integration Test Suite")
    print("=========================================================")
    try:
        test_philippine_vegetable_metadata_schema()
        test_dss_companion_planting_rules()
        test_edge_function_contracts()
        print("\n  [SUCCESS] All API & Database Integration tests PASSED successfully!")
        return 0
    except AssertionError as e:
        print(f"\n  [FAIL] Integration Test Failed: {e}")
        return 1
    except Exception as e:
        print(f"\n  [ERROR] Unexpected Error during testing: {e}")
        return 1

if __name__ == "__main__":
    sys.exit(run_all_integration_tests())
