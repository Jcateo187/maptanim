package com.maptanim.app.data.local

object LegalContent {

    val TERMS_AND_CONDITIONS = """
        MAPTANIM SMART FARMING — TERMS & CONDITIONS
        Effective Date: August 2026 | Version 1.2
        STI West Negros University Capstone Project

        1. ACCEPTANCE OF TERMS
        By creating an account, accessing, or using the MapTanim Mobile Application ("MapTanim", "App", or "Service"), you agree to be bound by these Terms & Conditions. If you do not agree to these terms, you may not use the Service.

        2. MAPTANIM AGROECOLOGICAL ENGINE
        MapTanim provides interactive 2D farm mapping, Philippine vegetable crop schedules, Companion Planting algorithms, and micro-climate monitoring. The decision support outputs are recommendations based on agricultural references and STI WNU research. Final farming decisions remain the responsibility of the farmer.

        3. USER ACCOUNT & SECURITY
        - You are responsible for maintaining the confidentiality of your account credentials.
        - You agree to provide accurate information (e.g. valid email or nickname) when creating an account.
        - Guest users may operate the App locally, but cloud synchronization requires an active account.

        4. INTELLECTUAL PROPERTY & DATA
        All trademarks, software architecture, isometric canvas rendering engines, and Philippines-tailored crop database structures belong to the MapTanim Capstone Development Team and STI West Negros University.

        5. LIMITATION OF LIABILITY
        MapTanim and its development team shall not be liable for crop loss, adverse weather impact, or farming yields resulting from natural disasters or improper agricultural practices.

        6. MODIFICATIONS
        We reserve the right to update these terms at any time. Continued use of MapTanim constitutes acceptance of updated terms.
    """.trimIndent()

    val PRIVACY_POLICY = """
        MAPTANIM SMART FARMING — PRIVACY POLICY
        Effective Date: August 2026 | Version 1.2
        Compliance: Philippine Data Privacy Act of 2012 (Republic Act No. 10173)

        1. INFORMATION WE COLLECT
        MapTanim collects minimal necessary data to deliver smart agricultural planning:
        - Account Data: Email address and selected Farmer Nickname.
        - Farm Data: Plot boundaries, crop types, planting dates, and task logs.
        - Device & Local Storage: Local SQLite cache (Room Database) for offline farm editing.

        2. PHILIPPINE DATA PRIVACY ACT COMPLIANCE
        We strictly uphold your rights under Republic Act No. 10173 (Data Privacy Act of 2012). Your data is processed lawfully, transparently, and only for specified agricultural support purposes.

        3. OFFLINE CACHING & CLOUD SYNC
        - All farm layouts and crop data are stored locally on your device for immediate offline access.
        - Cloud sync occurs via secure Supabase PostgreSQL database with Row-Level Security (RLS), ensuring only you can read or update your personal farm data.

        4. DATA SHARING & SECURITY
        We do NOT sell, rent, or trade your personal or farm data to third parties. Anonymized data may be used solely for academic thesis evaluation at STI West Negros University.

        5. USER RIGHTS & CONTACT
        You have the right to request access to, correction of, or deletion of your stored account data at any time through the Profile Settings menu.
    """.trimIndent()
}
