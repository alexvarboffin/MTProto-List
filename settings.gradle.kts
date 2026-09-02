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
<<<<<<< HEAD
project(":wads").projectDir = File("C:\\src\\Synced\\WalhallaUI\\features\\wads\\")
include(":shared")
project(":shared").projectDir = File("C:\\src\\Synced\\WalhallaUI\\shared")

include(":threader")
project(":threader").projectDir = File("G:\\source\\walhalla\\sdk\\multithreader\\threader\\")
//project(":threader").projectDir = File("G:\\source\\walhalla\\sdk\\android\\multithreader\\threader\\")


include(":nativetemplates")
=======
project(":wads").projectDir = File("C:\\Synced\\WalhallaUI\\features\\wads\\")

include(":shared")
project(":shared").projectDir = File("C:\\Synced\\WalhallaUI\\shared\\")

include(":threader")
project(":threader").projectDir = File("C:\\SYNCED\\multithreader\\threader\\")

//include(":nativetemplates")
//project(":nativetemplates").projectDir = File("G:\\source\\walhalla\\sdk\\Skazkinanoch\\nativetemplates\\")

>>>>>>> 2650c16f923a34a04151cfebe9d29e2875f91a04
// include(":data0")

// include(":feature:webview-main")
include(":webview")
project(":webview").projectDir = File("C:\\src\\Synced\\WalhallaUI\\features\\webview\\")

include(":LoaderNew")
//project(":webview").projectDir = File("C:\\Synced\\WalhallaUI\\features\\webview\\")

include(":mtprotocompose")
include(":kmp:mtprotoshared")
