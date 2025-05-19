plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.project.makeagain"
    compileSdk = 35

    buildFeatures {
        buildConfig = true
    }

    val apiKey: String = project.rootProject.file("local.properties")
        .takeIf { it.exists() }
        ?.readLines()
        ?.find { it.startsWith("API_KEY=") }
        ?.substringAfter("API_KEY=")
        ?.trim() ?: ""

    defaultConfig {
        applicationId = "com.project.makeagain"
        minSdk = 27
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "API_KEY", "\"$apiKey\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.books)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation (libs.circleimageview)
    implementation (libs.mpandroidchart)
    implementation (libs.navigation.fragment.ktx)
    implementation (libs.navigation.ui.ktx)
    implementation (libs.retrofit)
    implementation (libs.converter.gson)
    implementation (libs.shimmer)
    implementation (libs.glide)
    annotationProcessor (libs.compiler)
}