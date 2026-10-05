package com.maptanim.app.data.local.seeds

import com.maptanim.app.data.local.entity.CropVarietyEntity

object CropVarietySeeds {

    val list: List<CropVarietyEntity> = listOf(
        // ── 1. Tomato (Kamatis) ──────────────────────────────────────────────
        CropVarietyEntity(
            id = "var_tomato_diamante_max_f1",
            cropName = "Tomato",
            varietyName = "Diamante Max F1",
            localNamePh = "Kamatis Diamante Max F1",
            breederOrganization = "East-West Seed Philippines / NSIC Registered",
            growthDurationDays = 60,
            stage1SproutDays = 5,
            stage2SeedlingDays = 13,
            stage3VegetativeDays = 20,
            stage4FloweringDays = 16,
            stage5HarvestDays = 6,
            wateringIntervalDays = 2,
            fertilizeIntervalDays = 10,
            optimalSeasonsCsv = "YEAR_ROUND,WET,DRY",
            diseaseResistance = "Resistant to tomato yellow leaf curl virus (TYLCV) and bacterial wilt.",
            description = "High-yielding determinate hybrid producing firm, thick-walled oval fruits ideal for wet and dry seasons.",
            sourceCitation = "DA-BPI National Seed Industry Council (NSIC) & East-West Seed 2024"
        ),
        CropVarietyEntity(
            id = "var_tomato_apollo",
            cropName = "Tomato",
            varietyName = "Apollo",
            localNamePh = "Kamatis Apollo",
            breederOrganization = "UPLB-IPB / DA-BPI Lowland Release",
            growthDurationDays = 72,
            stage1SproutDays = 6,
            stage2SeedlingDays = 15,
            stage3VegetativeDays = 24,
            stage4FloweringDays = 20,
            stage5HarvestDays = 7,
            wateringIntervalDays = 2,
            fertilizeIntervalDays = 12,
            optimalSeasonsCsv = "DRY",
            diseaseResistance = "Moderate tolerance to heat and bacterial wilt.",
            description = "Traditional lowland release bred for tropical heat tolerance and open-pollinated backyard beds.",
            sourceCitation = "Institute of Plant Breeding (IPB) UPLB & DA-BPI Solanaceous Bulletin"
        ),

        // ── 2. Eggplant (Talong) ─────────────────────────────────────────────
        CropVarietyEntity(
            id = "var_eggplant_dumaguete_long_purple",
            cropName = "Eggplant",
            varietyName = "Dumaguete Long Purple",
            localNamePh = "Talong Dumaguete Long Purple",
            breederOrganization = "Bureau of Plant Industry (BPI) / Open Pollinated",
            growthDurationDays = 80,
            stage1SproutDays = 7,
            stage2SeedlingDays = 18,
            stage3VegetativeDays = 25,
            stage4FloweringDays = 20,
            stage5HarvestDays = 10,
            wateringIntervalDays = 2,
            fertilizeIntervalDays = 14,
            optimalSeasonsCsv = "YEAR_ROUND,DRY",
            diseaseResistance = "Tolerant to bacterial wilt and phomopsis blight.",
            description = "Prolific open-pollinated variety producing slender, deep-purple cylindrical fruits 25–30 cm long.",
            sourceCitation = "DA-BPI Philippine Vegetable Production Guide: Eggplant (2023)"
        ),
        CropVarietyEntity(
            id = "var_eggplant_casino_f1",
            cropName = "Eggplant",
            varietyName = "Casino F1",
            localNamePh = "Talong Casino F1",
            breederOrganization = "East-West Seed Philippines",
            growthDurationDays = 75,
            stage1SproutDays = 6,
            stage2SeedlingDays = 16,
            stage3VegetativeDays = 23,
            stage4FloweringDays = 20,
            stage5HarvestDays = 10,
            wateringIntervalDays = 2,
            fertilizeIntervalDays = 10,
            optimalSeasonsCsv = "YEAR_ROUND,WET,DRY",
            diseaseResistance = "High resistance to bacterial wilt and leaf curl.",
            description = "Top commercial hybrid with glossy dark purple fruits and vigorous extended fruiting flush.",
            sourceCitation = "East-West Seed Technical Field Bulletin 2024"
        ),

        // ── 3. Chili Pepper (Sili) ───────────────────────────────────────────
        CropVarietyEntity(
            id = "var_chili_tingala",
            cropName = "Chili Pepper",
            varietyName = "Tingala F1",
            localNamePh = "Sili Tingala / Labuyo",
            breederOrganization = "East-West Seed / NSIC",
            growthDurationDays = 70,
            stage1SproutDays = 8,
            stage2SeedlingDays = 17,
            stage3VegetativeDays = 22,
            stage4FloweringDays = 15,
            stage5HarvestDays = 8,
            wateringIntervalDays = 2,
            fertilizeIntervalDays = 12,
            optimalSeasonsCsv = "YEAR_ROUND,DRY",
            diseaseResistance = "High tolerance to anthracnose and phytophthora root rot.",
            description = "Upright-fruiting hot chili hybrid producing intense pungency and continuous harvesting flushes.",
            sourceCitation = "DA-BPI Capsicum Production Standards (2023)"
        ),
        CropVarietyEntity(
            id = "var_chili_espada",
            cropName = "Chili Pepper",
            varietyName = "Espada Panigang",
            localNamePh = "Sili Panigang / Haba",
            breederOrganization = "UPLB-IPB / DA-BPI",
            growthDurationDays = 65,
            stage1SproutDays = 7,
            stage2SeedlingDays = 15,
            stage3VegetativeDays = 21,
            stage4FloweringDays = 14,
            stage5HarvestDays = 8,
            wateringIntervalDays = 2,
            fertilizeIntervalDays = 10,
            optimalSeasonsCsv = "YEAR_ROUND,WET,DRY",
            diseaseResistance = "Moderate resistance to pepper mild mottle virus.",
            description = "Long light-green mild chili essential for Sinigang with crunchy texture and glossy skin.",
            sourceCitation = "DA-BPI National Chili Catalog 2024"
        ),

        // ── 4. Okra ──────────────────────────────────────────────────────────
        CropVarietyEntity(
            id = "var_okra_smooth_green",
            cropName = "Okra",
            varietyName = "Smooth Green",
            localNamePh = "Okra Makinis",
            breederOrganization = "Bureau of Plant Industry (BPI)",
            growthDurationDays = 55,
            stage1SproutDays = 4,
            stage2SeedlingDays = 10,
            stage3VegetativeDays = 18,
            stage4FloweringDays = 13,
            stage5HarvestDays = 10,
            wateringIntervalDays = 2,
            fertilizeIntervalDays = 10,
            optimalSeasonsCsv = "YEAR_ROUND,WET,DRY",
            diseaseResistance = "Resistant to yellow vein mosaic virus (YVMV).",
            description = "Spineless, dark-green 5-ridged pods that stay tender up to 10–12 cm length.",
            sourceCitation = "DA-BPI Okra Technical Bulletin 2023"
        ),

        // ── 5. Pechay ────────────────────────────────────────────────────────
        CropVarietyEntity(
            id = "var_pechay_black_behi",
            cropName = "Pechay",
            varietyName = "Black Behi",
            localNamePh = "Pechay Tagalog / Black Behi",
            breederOrganization = "DA-BPI / Lowland Open Pollinated",
            growthDurationDays = 30,
            stage1SproutDays = 3,
            stage2SeedlingDays = 7,
            stage3VegetativeDays = 12,
            stage4FloweringDays = 4,
            stage5HarvestDays = 4,
            wateringIntervalDays = 1,
            fertilizeIntervalDays = 7,
            optimalSeasonsCsv = "YEAR_ROUND,WET,DRY",
            diseaseResistance = "Moderate resistance to soft rot and black rot under raised beds.",
            description = "Fast-maturing dark-green leafy vegetable with tender white petioles; ideal for quick backyard cycles.",
            sourceCitation = "DA-BPI Lowland Leafy Vegetable Guide (2023)"
        ),

        // ── 6. Lettuce (Letsugas) ────────────────────────────────────────────
        CropVarietyEntity(
            id = "var_lettuce_green_towers",
            cropName = "Lettuce",
            varietyName = "Green Towers",
            localNamePh = "Letsugas Romaine",
            breederOrganization = "East-West Seed / Benguet Trials",
            growthDurationDays = 40,
            stage1SproutDays = 3,
            stage2SeedlingDays = 9,
            stage3VegetativeDays = 18,
            stage4FloweringDays = 5,
            stage5HarvestDays = 5,
            wateringIntervalDays = 1,
            fertilizeIntervalDays = 7,
            optimalSeasonsCsv = "DRY,COOL",
            diseaseResistance = "High tolerance to tipburn and bolting in subtropical heat.",
            description = "Crisp, upright Romaine heads with sweet crunchy rib texture; excellent for backyard salad beds.",
            sourceCitation = "Benguet State University Horticultural Leaflet (2024)"
        ),

        // ── 7. Kangkong ──────────────────────────────────────────────────────
        CropVarietyEntity(
            id = "var_kangkong_upland",
            cropName = "Kangkong",
            varietyName = "Upland Sparkle",
            localNamePh = "Kangkong Katihan",
            breederOrganization = "DA-BPI / East-West Seed",
            growthDurationDays = 28,
            stage1SproutDays = 3,
            stage2SeedlingDays = 6,
            stage3VegetativeDays = 11,
            stage4FloweringDays = 4,
            stage5HarvestDays = 4,
            wateringIntervalDays = 1,
            fertilizeIntervalDays = 7,
            optimalSeasonsCsv = "YEAR_ROUND,WET,DRY",
            diseaseResistance = "Resistant to white rust (Albugo ipomoeae-aquaticae).",
            description = "Broad pointed leaves on succulent hollow stems bred specifically for raised garden soil beds.",
            sourceCitation = "DA-BPI Leafy Greens Production Reference 2023"
        ),

        // ── 8. Cucumber (Pipino) ─────────────────────────────────────────────
        CropVarietyEntity(
            id = "var_cucumber_poinsett_76",
            cropName = "Cucumber",
            varietyName = "Poinsett 76",
            localNamePh = "Pipino Poinsett",
            breederOrganization = "UPLB-IPB / Open Pollinated",
            growthDurationDays = 55,
            stage1SproutDays = 4,
            stage2SeedlingDays = 10,
            stage3VegetativeDays = 19,
            stage4FloweringDays = 14,
            stage5HarvestDays = 8,
            wateringIntervalDays = 2,
            fertilizeIntervalDays = 10,
            optimalSeasonsCsv = "DRY,YEAR_ROUND",
            diseaseResistance = "Multiple resistance to powdery mildew, downy mildew, and anthracnose.",
            description = "Dark-green cylindrical slicing cucumber with crisp flesh and small seed cavity.",
            sourceCitation = "Institute of Plant Breeding (IPB) UPLB Bulletin"
        ),

        // ── 9. Yardlong Bean (Sitaw) ─────────────────────────────────────────
        CropVarietyEntity(
            id = "var_sitaw_sandigan",
            cropName = "Yardlong Bean",
            varietyName = "Sandigan Light Green",
            localNamePh = "Sitaw Sandigan",
            breederOrganization = "UPLB-IPB / NSIC Released",
            growthDurationDays = 60,
            stage1SproutDays = 4,
            stage2SeedlingDays = 10,
            stage3VegetativeDays = 20,
            stage4FloweringDays = 16,
            stage5HarvestDays = 10,
            wateringIntervalDays = 2,
            fertilizeIntervalDays = 12,
            optimalSeasonsCsv = "YEAR_ROUND,DRY,WET",
            diseaseResistance = "Resistant to bean rust and mosaic virus; naturally fixes atmospheric nitrogen.",
            description = "Heavy-yielding pole legume with tender 55–65 cm long light-green pods. Key crop rotation anchor.",
            sourceCitation = "DA-BPI National Legume Production Standards 2024"
        ),

        // ── 10. Sweet Corn (Mais) ────────────────────────────────────────────
        CropVarietyEntity(
            id = "var_corn_machu",
            cropName = "Sweet Corn",
            varietyName = "Machu F1",
            localNamePh = "Mais Tamis Machu",
            breederOrganization = "East-West Seed / NSIC",
            growthDurationDays = 72,
            stage1SproutDays = 4,
            stage2SeedlingDays = 11,
            stage3VegetativeDays = 25,
            stage4FloweringDays = 20,
            stage5HarvestDays = 12,
            wateringIntervalDays = 3,
            fertilizeIntervalDays = 14,
            optimalSeasonsCsv = "DRY,WET",
            diseaseResistance = "Tolerant to downy mildew and Southern corn leaf blight.",
            description = "Super-sweet yellow kernel hybrid with excellent ear filling and tip cover; sturdy stalks.",
            sourceCitation = "DA-BPI Corn Program & East-West Seed 2024"
        )
    )
}
