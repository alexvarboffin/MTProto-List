// Root build.gradle.kts
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
<<<<<<< HEAD
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false
=======
    alias(libs.plugins.kotlin.compose) apply false
>>>>>>> 2650c16f923a34a04151cfebe9d29e2875f91a04
}

tasks.register<Delete>("clean") {
    delete(layout.buildDirectory)
}
