import com.google.firebase.appdistribution.gradle.firebaseAppDistribution

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.appdistribution)
}

android {
    namespace = "com.maptanim.app"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.maptanim.app"
        minSdk = 29
        targetSdk = 34
        versionCode = 4
        versionName = "1.2.7"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
            firebaseAppDistribution {
                appId = "1:605883983200:android:1e5411b580262a2b422a05"
                groups = "testers"
                releaseNotes = System.getenv("FIREBASE_RELEASE_NOTES") ?: "MapTanim v1.2.7 Release Test Build - DSS matrix, beginner backyard mode, and SVG rendering improvements"
                System.getenv("FIREBASE_SERVICE_ACCOUNT_JSON_PATH")?.takeIf { it.isNotBlank() }?.let {
                    serviceCredentialsFile = it
                }
            }
        }
        debug {
            firebaseAppDistribution {
                appId = "1:605883983200:android:1e5411b580262a2b422a05"
                groups = "testers"
                releaseNotes = System.getenv("FIREBASE_RELEASE_NOTES") ?: "MapTanim v1.2.7 Debug Test Build - DSS matrix, beginner backyard mode, and SVG rendering improvements"
                System.getenv("FIREBASE_SERVICE_ACCOUNT_JSON_PATH")?.takeIf { it.isNotBlank() }?.let {
                    serviceCredentialsFile = it
                }
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
    lint {
        abortOnError = false
        checkReleaseBuilds = false
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation("androidx.compose.material:material-icons-core:1.7.8")
    implementation("androidx.compose.material:material-icons-extended:1.7.8")
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.coil.compose)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // ViewModel + Coroutines
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.kotlinx.coroutines.android)

    // Room Database
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // WorkManager
    implementation("androidx.work:work-runtime-ktx:2.10.0")

    testImplementation(libs.junit)

    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // Supabase
    implementation(platform("io.github.jan-tennert.supabase:bom:3.6.0"))
    implementation("io.github.jan-tennert.supabase:postgrest-kt")
    implementation("io.github.jan-tennert.supabase:auth-kt")
    implementation("io.github.jan-tennert.supabase:storage-kt")

    // Ktor
    implementation("io.ktor:ktor-client-okhttp:3.5.1")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.11.0")
    implementation(project(":backend"))

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
}
