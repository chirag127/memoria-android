plugins {
    alias(libs.plugins.memoria.android.application)
    alias(libs.plugins.memoria.android.compose)
    alias(libs.plugins.memoria.android.hilt)
}

android {
    namespace = "chirag127.memoria"
    defaultConfig {
        applicationId = "chirag127.memoria"
        versionCode = 1
        versionName = "0.1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // Two flavors: `play` ships only green/yellow captures (clean Play submission);
    // `full` adds restricted services (NotificationListener/Accessibility) for
    // F-Droid / sideload only. Restricted manifest entries live in src/full/.
    flavorDimensions += "distribution"
    productFlavors {
        create("play") { dimension = "distribution" }
        create("full") { dimension = "distribution" }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":core:ui"))
    implementation(project(":core:common"))
    implementation(project(":data:repository"))
    implementation(project(":feature:capture"))
    implementation(project(":feature:timeline"))
    implementation(project(":feature:search"))
    implementation(project(":feature:settings"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.work.runtime.ktx)
    implementation(libs.timber)
}
