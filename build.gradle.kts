// Root build.gradle.kts
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
}

allprojects {
    repositories {
        google()
        mavenLocal()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

tasks.register<Delete>("clean") {
    delete(layout.buildDirectory)
}
