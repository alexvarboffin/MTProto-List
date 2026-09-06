pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        maven {
            url = uri("https://jitpack.io")
        }
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://jitpack.io")
        }
    }
}

rootProject.name = "mtproto"

include(":mtproto")
include(":loader")

include(":ui")
project(":ui").projectDir = File("C:\\src\\Synced\\WalhallaUI\\features\\ui")

include(":wads")
project(":wads").projectDir = File("C:\\src\\Synced\\WalhallaUI\\features\\wads")

include(":shared")
project(":shared").projectDir = File("C:\\src\\Synced\\WalhallaUI\\shared")

include(":threader")
project(":threader").projectDir = File("D:\\dev\\android\\Compatibility\\threader")

include(":nativetemplates")

include(":webview")
project(":webview").projectDir = File("C:\\src\\Synced\\WalhallaUI\\features\\webview")

include(":LoaderNew")
include(":mtprotocompose")
include(":kmp:mtprotoshared")
