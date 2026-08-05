import com.android.build.gradle.internal.dsl.SigningConfig
import java.io.FileInputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
    alias(libs.plugins.firebase.crashlytics)
}

fun versionCodeDate(): Int {
    return SimpleDateFormat("yyMMdd").format(Date()).toInt()
}

android {
    namespace = "com.walhalla.mtprotoloader"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.walhalla.mtprotoloader"
        minSdkVersion(libs.versions.android.minSdk.get().toInt())
        targetSdkVersion(libs.versions.android.targetSdk.get().toInt())
        
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
        create("x") {
            keyAlias = "release"
            keyPassword = "release"
            storeFile = file("keystore/keystore.jks")
            storePassword = "release"
        }
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("x")
            versionNameSuffix = "-DEMO"
        }
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("x")
            versionNameSuffix = ".release"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(project(":webview"))

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test:runner:1.6.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation(libs.gson)

    implementation(project(":ui"))

    implementation(libs.picasso) {
        exclude(group = "com.android.support")
    }

    implementation(libs.qrgen)
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.androidx.multidex)
    implementation(libs.firebase.core)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)
    implementation(libs.play.services.ads)
    implementation(libs.firebase.database)

    implementation(libs.androidx.lifecycle.process)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.lifecycle.common.java8)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    
    implementation(libs.localechanger)
    implementation(libs.androidx.preference.ktx)
    implementation(libs.pulsator4droid)
    implementation(libs.kotlin.stdlib.jdk8)
    implementation(libs.androidbrowserhelper)
    implementation(libs.kotlin.stdlib)
}

apply(plugin = "com.google.gms.google-services")
