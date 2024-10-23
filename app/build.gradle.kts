plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.0"
}

android {
    namespace = "com.ai.aishotclientkotlin"
    compileSdk = 34


    defaultConfig {
        applicationId = "com.ai.aishotclientkotlin"
        minSdk = 33  //for the pixel6a 13 versions
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }


        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf(
                    "room.schemaLocation" to "$projectDir/schemas",
                    "dagger.hilt.disableModulesHaveInstallInCheck" to "true"
                )
            }
        }

        ndk {
            // Use libc++_shared as the STL (Standard Template Library)
           // abiFilters.add("armeabi-v7a") // or whichever ABIs you support
            abiFilters.add("arm64-v8a")
            // abiFilters.add("x86")
            abiFilters.add("x86_64")
        }
        externalNativeBuild {
            cmake {
                cppFlags.add("-std=c++11")
            }
        }

//        buildConfigField ("String", "TYPEONE", toJavaCodeString("My Awesome Url"))
//        buildConfigField ("String", "TYPETWO", toJavaCodeString("Secret Key"))
//        buildConfigField ("String", "LOGIN", toJavaCodeString("Page Url"))
//        buildConfigField ("String", "REGISTER", toJavaCodeString("Page Url"))
//        buildConfigField ("String", "LANGUAGES", toJavaCodeString("Page Url"))
//        buildConfigField ("String", "WORDS", toJavaCodeString("Page Url"))
//
//        buildConfigField ("String", "SplashImage", toJavaCodeString("Image Url"))
//        buildConfigField ("String", "LoginImage", toJavaCodeString("Image Url"))
//        buildConfigField ("String", "LanguageImage", toJavaCodeString("Image Url"))
    }
//    externalNativeBuild {
//        cmake {
//            path = file("src/main/cpp/CMakeLists.txt")  // Specify where your CMakeLists.txt is located
//        }
//    }
//    sourceSets {
//        getByName("main") {
//            jniLibs.srcDirs("src/main/jniLibs")
//        }
//    }
    buildTypes {
        debug {
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


    kotlinOptions {
        jvmTarget = "11"
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
    //    pickFirst()
//        jniLibs {
//            pickFirsts.add("lib/**/libc++_shared.so")
//        }
    }

}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
//    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.lottie.compose)
    implementation(libs.landscapist.glide)
    implementation(libs.landscapist.animation)
    implementation(libs.landscapist.coil3)
 //   implementation("io.coil-kt:coil-compose:2.7.0")
    implementation("io.coil-kt.coil3:coil-compose:3.0.0-alpha04")
    implementation(libs.landscapist.palette)
    implementation(libs.landscapist.placeholder)
    implementation(libs.androidx.compose.animation)
    // https://mvnrepository.com/artifact/com.google.dagger/dagger
    implementation("com.google.dagger:dagger:2.52")
    // https://mvnrepository.com/artifact/com.google.dagger/hilt-android-gradle-plugin
    implementation("com.google.dagger:hilt-android-gradle-plugin:2.52")
    implementation(libs.androidx.palette.ktx)
    implementation(libs.play.services.measurement.api)
    implementation(libs.pose.detection)
   // implementation(libs.core)
    // https://mvnrepository.com/artifact/com.google.dagger/hilt-android-compiler
    kapt("com.google.dagger:hilt-android-compiler:2.52")
 //   implementation("androidx.hilt:hilt-lifecycle-viewmodel:1.0.0-alpha03")
    // https://mvnrepository.com/artifact/androidx.hilt/hilt-navigation-compose
   // runtimeOnly("androidx.hilt:hilt-navigation-compose:1.0.0")

    implementation("androidx.room:room-ktx:2.6.1")
    kapt ("androidx.room:room-compiler:2.6.1")

    implementation("com.google.dagger:hilt-android:2.52")
    kapt("com.google.dagger:hilt-compiler:2.52")
    //!!TODO ,check these versions;
//    implementation("androidx.compose.runtime:runtime-livedata:1.4.3")
//    implementation("androidx.activity:activity-compose:1.7.2")
//    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.1")
//    implementation ("androidx.lifecycle:lifecycle-extensions:2.2.0")
//    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.1")
//    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.4.1")
//    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:2.4.1")
//
//
//    // https://mvnrepository.com/artifact/androidx.hilt/hilt-navigation-fragment
  // implementation("androidx.hilt:hilt-navigation-fragment:1.2.0")
    kapt("com.github.bumptech.glide:compiler:4.16.0")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.1")

    implementation("com.google.accompanist:accompanist-insets:0.30.0")
    implementation("com.google.accompanist:accompanist-flowlayout:0.30.0")



//https://mvnrepository.com/artifact/com.github.skydoves/sandwich
//runtimeOnly 'com.github.skydoves:sandwich:2.0.8'
    implementation("com.github.skydoves:sandwich:2.0.8")
    implementation("com.github.skydoves:sandwich-retrofit:2.0.8")

    implementation("com.github.skydoves:sandwich-ktor:2.0.8")
    implementation("com.github.skydoves:sandwich-ktorfit:2.0.8")


//https://mvnrepository.com/artifact/com.github.skydoves/whatif
    implementation ("com.github.skydoves:whatif:1.1.4")
// https://mvnrepository.com/artifact/com.jakewharton.timber/timber
    implementation ("com.jakewharton.timber:timber:5.0.0")

// https://mvnrepository.com/artifact/androidx.compose.material/material-icons-extended
    implementation("androidx.compose.material:material-icons-extended:1.6.8")

//https://mvnrepository.com/artifact/com.airbnb.android/lottie-compose

    //opencv
    //TODO delete the opencv android .because I use the openc project
   // implementation ("org.opencv:opencv-android:1.0.1")

    implementation ("com.google.mlkit:object-detection:17.0.0")
    implementation ("com.google.mlkit:object-detection-custom:17.0.0")
    implementation ("com.google.mlkit:face-detection:16.1.6")
    implementation ("com.google.mlkit:pose-detection-accurate:17.0.0")
    implementation ("com.google.mlkit:pose-detection:17.0.0")
    implementation ("androidx.camera:camera-camera2:1.3.4")
    implementation("com.google.mlkit:image-labeling:17.0.7")
    implementation ("androidx.camera:camera-camera2:1.3.4")
    implementation ("androidx.camera:camera-view:1.3.4")


    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))



    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")

//
//    implementation ("org.apache.poi:poi:5.2.3")
//    implementation ("org.apache.poi:poi-ooxml:5.2.3")

    implementation(project(":opencv"))

    //TODO: 以下是另外的opengl方法；
  /*  implementation("org.rajawali3d:rajawali:1.2.1970")
    // https://mvnrepository.com/artifact/com.google.android.filament/filament-android
    implementation("com.google.android.filament:filament-android:1.53.4")
// https://mvnrepository.com/artifact/com.google.android.filament/gltfio-android
    implementation("com.google.android.filament:gltfio-android:1.53.4")
// https://mvnrepository.com/artifact/com.google.android.filament/filament-utils-android
    implementation("com.google.android.filament:filament-utils-android:1.53.4")*/



    //https://github.com/SceneView/sceneview-android
    implementation("io.github.sceneview:sceneview:2.2.1")
    implementation("io.github.sceneview:arsceneview:2.2.1")

    //手势识别：
    // https://mvnrepository.com/artifact/com.google.mediapipe/hands
    implementation("com.google.mediapipe:hands:0.10.15")
    // https://mvnrepository.com/artifact/com.google.mediapipe/facemesh
    implementation("com.google.mediapipe:facemesh:0.10.15")

// https://mvnrepository.com/artifact/com.google.accompanist/accompanist-permissions
    implementation("com.google.accompanist:accompanist-permissions:0.34.0")


    //# required, enough for most devices. 用androiview之外的另外一种选择；
    implementation("tv.danmaku.ijk.media:ijkplayer-java:0.8.8")
    implementation("tv.danmaku.ijk.media:ijkplayer-armv7a:0.8.8")
    implementation("tv.danmaku.ijk.media:ijkplayer-x86:0.8.8")


    implementation("com.google.android.exoplayer:exoplayer:2.18.5")
   // implementation(" tv.danmaku.ijk.media:ijkplayer-x86_64:0.8.8")
    //#check the android explayer can be run bilibili? https://support.huaweicloud.com/vod/index.html
    //图片轮播；和全屏
    // https://mvnrepository.com/artifact/com.google.accompanist/accompanist-pager
    implementation("com.google.accompanist:accompanist-pager:0.34.0")

    implementation("com.github.Philjay:mpandroidchart:3.1.0")
    // https://mvnrepository.com/artifact/com.tencent.mm.opensdk/wechat-sdk-android-without-mta
    implementation("com.tencent.mm.opensdk:wechat-sdk-android-without-mta:6.8.0")

    implementation("com.tencent.mm.opensdk:wechat-sdk-android:+")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
kapt {
    correctErrorTypes = true
}

//// Create a Random instance for generating values
//object Utils {
//            val r = Random(System.currentTimeMillis())
//}
//
//// Function to convert a string to a Java code string
//fun toJavaCodeString(string: String): String {
//    val b = string.toByteArray()
//    val c = b.size
//    val sb = StringBuilder()
//
//    sb.append("(object {")
//    sb.append("var t: Int = 0")
//    sb.append("override fun toString(): String {")
//    sb.append("val buf = ByteArray($c)")
//
//    for (i in b.indices) {
//        val t = Utils.r.nextInt()
//        val f = Utils.r.nextInt(24) + 1
//
//        val encodedValue = (t and (0xff shl f).inv()) or (b[i].toInt() shl f)
//        sb.append("t = $encodedValue")
//        sb.append("buf[$i] = (t shr $f).toByte()")
//    }
//
//    sb.append("return buf.toString(Charsets.UTF_8)")
//    sb.append("}}.toString())")
//
//    return sb.toString()
//}