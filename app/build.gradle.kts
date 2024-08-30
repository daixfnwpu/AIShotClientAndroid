plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.ai.aishotclientkotlin"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.ai.aishotclientkotlin"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
//    implementation(libs.androidx.material.icons.core)
//    implementation("com.google.android.material:material:1.12.0")
//  // Compose dependencies
//    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
//
//    implementation("com.google.accompanist:accompanist-flowlayout:0.32.0")
//
//        implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
//        implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
//
//    implementation("androidx.lifecycle:lifecycle-viewmodel:2.8.0")
//
//
////    //Dagger - Hilt
//    implementation("com.google.dagger:hilt-android:2.50")
//    implementation("com.google.dagger:hilt-android-compiler:2.50")
//    implementation("androidx.hilt:hilt-lifecycle-viewmodel:1.0.0-alpha03")
//    implementation("androidx.hilt:hilt-compiler:1.2.0")
//    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
//    implementation("com.google.dagger:hilt-android-compiler:2.50")
//
////
////    //Compose-Lottie
//    implementation("com.airbnb.android:lottie-compose:6.4.0")
////
////    // Glide
//    implementation("com.github.skydoves:landscapist-glide:2.3.2")
////
////    //System UI Controller
//    implementation("com.google.accompanist:accompanist-systemuicontroller:0.34.0")

    // Test rules and transitive dependencies:
//    androidTestImplementation("libs.androidx.compose.ui.ui-test-junit4")
 //   androidTestImplementation("libs.com.google.dagger.hilt-android-testing")




    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}