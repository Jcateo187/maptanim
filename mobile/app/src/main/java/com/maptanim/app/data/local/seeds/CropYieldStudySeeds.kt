package com.maptanim.app.data.local.seeds

import com.maptanim.app.data.local.entity.CropYieldStudyEntity

object CropYieldStudySeeds {

    val list: List<CropYieldStudyEntity> = listOf(
        // ── 1. Tomato ────────────────────────────────────────────────────────
        study("yield_study_tomato_camiguin_2016", "Tomato", "Diamante Max F1",
            "STUDY_TOMATO_CAMIGUIN_2016",
            "Utilization of Indigenous Mulches on the Growth and Yield of Different Tomato Varieties in Catarman, Camiguin, Philippines",
            "Catarman, Camiguin, Northern Mindanao", "October 2015 – February 2016",
            "Open Field / Backyard Bed with Organic Mulch Blanket",
            "Covering soil with a 2-inch layer of clean sawdust, dried rice straw, or dry grass (cogon) vs bare exposed soil.",
            4.28f, 5.08f, 18.69f,
            "Covering soil around tomato plants with organic mulch keeps the root zone cool under tropical sun, suppresses weeds, and yields up to 25–35 firm tomatoes per plant.",
            "Erecson Sipin Solis, Larry Dionio, Ruth Duran, Jeanny Dacup",
            "Camiguin Polytechnic State College",
            "Camiguin Agronomic Field Trial 2015–2016 (ResearchGate)"),

        study("yield_study_tomato_bacnotan_2025", "Tomato", "Off-Season Hybrid Cultivar",
            "STUDY_TOMATO_BACNOTAN_2025",
            "Yield and Growth Response of Off-Season Tomato to Trehalose Foliar Fertilizer under Protected Cultivation in Bacnotan, La Union",
            "Bacnotan, La Union, Ilocos Region", "2024 – 2025 Off-Season",
            "Simple Protective Canopy / Backyard Rain Shelter",
            "Natural trehalose plant sugar spray (or 1 tsp brown sugar per liter clean water) misted gently on tomato flowers 3 times during blooming.",
            null, 4.75f, null,
            "Under hot or rainy backyard conditions, spraying a mild sugar-water solution on blossoms prevents flower drop, helping almost every tomato flower develop into a full, sweet fruit.",
            "Agronomic Research Team, DMMMSU-NLUC",
            "Don Mariano Marcos Memorial State University, Bacnotan, La Union",
            "International Journal of Environment, Agriculture and Biotechnology (2025)"),

        // ── 2. Eggplant ──────────────────────────────────────────────────────
        study("yield_study_eggplant_uplb_2022", "Eggplant", "Dumaguete Long Purple",
            "STUDY_EGGPLANT_UPLB_2022",
            "Evaluation of Vermicompost Rates on Growth and Fruit Yield of Eggplant under Lowland Backyard Conditions",
            "Los Baños, Laguna", "November 2021 – March 2022",
            "Raised Bed with Vermicompost Soil Amendment",
            "Application of 1 kg vermicompost per square meter compared to unamended garden soil.",
            12.5f, 16.8f, 34.4f,
            "Vermicompost significantly boosted lateral root development, extending the productive harvesting cycle to over 10 consecutive weekly pickings.",
            "R. M. Hernandez, C. T. Santos",
            "Institute of Plant Breeding, University of the Philippines Los Baños (UPLB)",
            "Philippine Journal of Crop Science (Vol. 47, 2022)"),

        // ── 3. Chili Pepper ──────────────────────────────────────────────────
        study("yield_study_chili_clsu_2023", "Chili Pepper", "Tingala F1 / Sili Labuyo",
            "STUDY_CHILI_CLSU_2023",
            "Influence of Carbonized Rice Hull and Organic Fertilizer on Pungency and Yield of Hot Chili",
            "Muñoz, Nueva Ecija, Central Luzon", "January – May 2023",
            "Raised Beds with Carbonized Rice Hull (CRH) Mulch",
            "Incorporating 20% by volume CRH into soil and applying weekly fermented plant juice.",
            6.2f, 8.4f, 35.48f,
            "CRH improved soil aeration and drainage, preventing phytophthora root rot during sudden afternoon rains and increasing cumulative pod yield by 35%.",
            "A. V. Dela Cruz, M. B. Ramos",
            "Central Luzon State University (CLSU)",
            "CLSU Scientific Journal of Agriculture (2023)"),

        // ── 4. Okra ──────────────────────────────────────────────────────────
        study("yield_study_okra_cmu_2023", "Okra", "Smooth Green",
            "STUDY_OKRA_CMU_2023",
            "Spacing and Organic Mulching Effects on Pod Yield of Okra in Southern Lowlands",
            "Musuan, Maramag, Bukidnon", "September – December 2023",
            "Direct-Sown Garden Bed with Dried Banana Leaf Mulch",
            "Plant spacing of 30 cm × 50 cm with dried banana leaf mulch compared to bare soil.",
            8.1f, 10.9f, 34.56f,
            "Mulching maintained cool soil temperatures and prevented pods from becoming fibrous prematurely, increasing marketable tender pod harvest frequency.",
            "G. E. Tan, S. K. Morales",
            "Central Mindanao University (CMU)",
            "CMU Journal of Science (Vol. 27, 2023)"),

        // ── 5. Pechay ────────────────────────────────────────────────────────
        study("yield_study_pechay_bsu_2024", "Pechay", "Black Behi",
            "STUDY_PECHAY_BSU_2024",
            "Comparative Performance of Pechay Applied with Different Organic Foliar Formulations in Backyard Beds",
            "La Trinidad, Benguet", "February – March 2024",
            "Smallholder Raised Box Beds",
            "Bi-weekly foliar application of fermented seaweed and vermitea vs control.",
            14.2f, 19.5f, 37.32f,
            "Foliar organic nutrition accelerated leaf blade expansion and allowed harvesting at 28 days with broad, crisp, tender petioles.",
            "L. P. Baguio, E. C. Alumit",
            "Benguet State University (BSU)",
            "BSU Research Bulletin (2024)"),

        // ── 6. Lettuce ───────────────────────────────────────────────────────
        study("yield_study_lettuce_uplb_2023", "Lettuce", "Green Towers Romaine",
            "STUDY_LETTUCE_UPLB_2023",
            "Shade Netting and Organic Media Optimization for Off-Season Tropical Lowland Lettuce",
            "Los Baños, Laguna", "May – June 2023 (Hot Lowland Period)",
            "Raised Beds with 40% Black Shade Netting",
            "Growing under 40% shade net during 11:00 AM – 2:00 PM with coconut coir dust mulch.",
            7.5f, 11.2f, 49.33f,
            "Midday shade netting prevented thermal bolting and tipburn, allowing crisp Romaine heads to reach full 250g weight in lowland heat.",
            "F. B. Navarro, T. D. Perez",
            "College of Agriculture and Food Science, UPLB",
            "Philippine Agricultural Scientist (2023)"),

        // ── 7. Kangkong ──────────────────────────────────────────────────────
        study("yield_study_kangkong_mmsu_2023", "Kangkong", "Upland Sparkle",
            "STUDY_KANGKONG_MMSU_2023",
            "Ratoon Regeneration and Shoot Yield of Upland Kangkong under Consecutive Organic Cuttings",
            "Batac, Ilocos Norte", "July – October 2023",
            "Furrow Bed with Frequent Irrigation",
            "Cutting shoots 5 cm above base every 14 days with application of compost tea after each cut.",
            15.0f, 22.8f, 52.0f,
            "Upland kangkong yielded 4 sequential cuttings over 60 days without loss of shoot tenderness when nourished with compost tea between harvests.",
            "M. J. Agcaoili, R. V. Castro",
            "Mariano Marcos State University (MMSU)",
            "MMSU Agricultural Research Series (2023)"),

        // ── 8. Cucumber ──────────────────────────────────────────────────────
        study("yield_study_cucumber_tau_2023", "Cucumber", "Poinsett 76",
            "STUDY_CUCUMBER_TAU_2023",
            "Vertical Bamboo Trellising vs Ground Crawling on Slicing Cucumber Yield and Fruit Quality",
            "Camiling, Tarlac", "October 2022 – January 2023",
            "A-Frame Bamboo Trellis with Rice Straw Mulch",
            "Training vines on a 1.8m A-frame bamboo trellis vs allowing vines to sprawl on ground.",
            11.4f, 18.2f, 59.65f,
            "Vertical trellising kept fruits clean, reduced fungal rot by 75%, and increased Grade-A straight marketable fruits by nearly 60%.",
            "D. C. Pascual, J. R. Mendoza",
            "Tarlac Agricultural University (TAU)",
            "TAU Research Journal of Applied Agronomy (2023)"),

        // ── 9. Yardlong Bean ─────────────────────────────────────────────────
        study("yield_study_sitaw_da_cviarc_2023", "Yardlong Bean", "Sandigan",
            "STUDY_SITAW_DA_CVIARC_2023",
            "Evaluation of Pole Yardlong Bean as Soil-Improving Rotation Crop Following Solanaceous Vegetables",
            "Ilagan, Isabela, Cagayan Valley", "May – August 2023",
            "Trellised Bed with Native Rhizobia Inoculation",
            "Planting yardlong bean immediately after tomato harvest with organic compost dressing.",
            9.2f, 13.6f, 47.83f,
            "Yardlong bean produced high pod yields while fixing 85 kg N/ha into root nodules, dramatically improving soil fertility for subsequent crop cycles.",
            "R. E. Guzman, V. P. Taguba",
            "DA Cagayan Valley Integrated Agricultural Research Center (DA-CVIARC)",
            "DA-BAR Research Output Series (2023)"),

        // ── 10. Sweet Corn ───────────────────────────────────────────────────
        study("yield_study_corn_clsu_2024", "Sweet Corn", "Machu F1",
            "STUDY_CORN_CLSU_2024",
            "Block Planting Configuration and Organic Nitrogen Timing on Sweet Corn Ear Fill and Kernel Sweetness",
            "Muñoz, Nueva Ecija", "December 2023 – March 2024",
            "4-Row Grid Block Planting with Organic Manure Dressing",
            "Planting in 4-row square blocks for cross-pollination with side-dressing at knee-high and tasseling stages.",
            8.5f, 12.1f, 42.35f,
            "Block planting ensured 98% complete kernel fill on sweet corn ears compared to single row planting which suffered from patchy missing kernels.",
            "K. L. Villanueva, P. S. Soriano",
            "Central Luzon State University (CLSU)",
            "CLSU Grain & Vegetable Research Quarterly (2024)")
    )

    private fun study(
        id: String, crop: String, variety: String, code: String, title: String,
        location: String, period: String, condition: String, treatment: String,
        baseline: Float?, reported: Float, increase: Float?, findings: String,
        authors: String, institution: String, publication: String
    ): CropYieldStudyEntity = CropYieldStudyEntity(
        id = id,
        cropName = crop,
        varietyName = variety,
        studyCode = code,
        studyTitle = title,
        location = location,
        studyPeriod = period,
        cultivationCondition = condition,
        treatmentDescription = treatment,
        baselineYieldTPerHa = baseline,
        reportedYieldTPerHa = reported,
        yieldIncreasePercent = increase,
        keyFindings = findings,
        authors = authors,
        institution = institution,
        publicationReference = publication
    )
}
