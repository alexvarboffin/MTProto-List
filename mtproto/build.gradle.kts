import java.util.*

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
    alias(libs.plugins.firebase.crashlytics)
}

fun versionCodeDate(): Int {
    return java.text.SimpleDateFormat("yyMMdd").format(Date()).toInt()
}

android {
    namespace = "com.walhalla.mtprotolist"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.walhalla.mtprotolist"
        minSdkVersion(libs.versions.minSdk.get().toInt())
        targetSdkVersion(libs.versions.targetSdk.get().toInt())
        
        val code = versionCodeDate()
        versionCode = code
        versionName = "1.1.$code"

        resConfigs("en", "es", "fr", "de", "it", "pt", "el", "ru", "ja", "zh-rCN", "zh-rTW", "ko", "ar", "uk", "vi", "uz", "az")
        multiDexEnabled = true
        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }

    bundle {
        storeArchive {
            enable = true
        }
    }

    signingConfigs {
        create("debug") {
            keyAlias = "release"
            keyPassword = "release"
            storeFile = file("keystore/keystore.jks")
            storePassword = "release"
        }

        getByName("release") {
            keyAlias = "release"
            keyPassword = "release"
            storeFile = file("keystore/keystore.jks")
            storePassword = "release"
        }
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("debug")
            versionNameSuffix = "-DEMO"
        }
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
            versionNameSuffix = ".release"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))
    
    implementation(libs.androidx.appcompat)
    implementation(libs.google.material)
    implementation(libs.androidx.constraintlayout)
    implementation(project(":features:webview"))

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test:runner:1.6.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    implementation(libs.gson)

    implementation(project(":features:ui"))
    implementation(project(":features:wads"))

    implementation(libs.picasso) {
        exclude(group = "com.android.support")
    }

    implementation(libs.qrgen)
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.androidx.multidex)
    implementation(libs.firebase.core)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)
    implementation(libs.play.services-ads)
    implementation(libs.firebase.database)

    implementation(libs.androidx.lifecycle.process)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.lifecycle.common.java8)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.viewmodel-ktx)
    
    implementation(libs.localechanger)
    implementation(libs.androidx.preference)
    implementation(libs.pulsator4droid)
    implementation(libs.kotlin.stdlib-jdk8)
    implementation(libs.androidbrowserhelper)
    implementation(libs.kotlin.stdlib)
    implementation(libs.play.app-update)
    implementation(libs.play.app-update-ktx)
    implementation(libs.sdp.android)
    implementation(libs.konfetti.xml)
    implementation(libs.onesignal)
}

tasks.register<Copy>("copyAabToBuildFolder") {
    val outputDirectory = file("C:/build")
    if (!outputDirectory.exists()) {
        outputDirectory.mkdirs()
    }
    from("${layout.buildDirectory.get().asFile}/outputs/bundle/release") {
        include("*.aab")
    }
    into(outputDirectory)
}

apply(plugin = "com.google.gms.google-services")
