import java.text.SimpleDateFormat
import java.util.Date

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.services)
}

fun versionCodeDate(): Int {
    return SimpleDateFormat("yyMMdd").format(Date()).toInt()
}

android {
    namespace = "com.walhalla.mtprotocompose"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.walhalla.mtprotolist"
        minSdk = 24
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        val code = versionCodeDate()
        versionCode = code
        versionName = "1.1.$code"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        multiDexEnabled = true
    }

    signingConfigs {
        create("x0") {
            keyAlias = "release"
            keyPassword = "release"
            storeFile = file("../mtproto/keystore/keystore.jks")
            storePassword = "release"
        }
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("x0")
            versionNameSuffix = "-DEMO"
        }
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("x0")
            versionNameSuffix = ".release"
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
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
        buildConfig = true
    }
}

dependencies {
    implementation(project(":kmp:mtprotoshared"))
    implementation(project(":ui"))
    implementation(project(":wads"))
    implementation(project(":webview"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.multidex)
    implementation(libs.gson)
    implementation(libs.qrgen)
    implementation(libs.picasso) {
        exclude(group = "com.android.support")
    }
    implementation(libs.play.services.ads)
    implementation(libs.firebase.database)
    implementation(libs.localechanger)
    implementation(libs.androidx.preference.ktx)

    implementation(libs.androidx.lifecycle.process)
    implementation(libs.konfetti.xml)
    implementation(libs.onesignal)
    implementation(libs.pulsator4droid)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation("androidx.compose.foundation:foundation")
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

apply(plugin = "com.google.gms.google-services")
