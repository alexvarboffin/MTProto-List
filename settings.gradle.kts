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
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "mtproto"
include(":mtproto")
include(":loader")

include(":ui")
project(":ui").projectDir = File("C:\\Synced\\WalhallaUI\\features\\ui")

include(":wads")
project(":wads").projectDir = File("C:\\Synced\\WalhallaUI\\features\\wads\\")
include(":shared")
project(":shared").projectDir = File("C:\\Synced\\WalhallaUI\\features\\wads\\")

include(":threader")
project(":threader").projectDir = File("G:\\source\\walhalla\\sdk\\multithreader\\threader\\")

include(":nativetemplates")
project(":nativetemplates").projectDir = File("G:\\source\\walhalla\\sdk\\Skazkinanoch\\nativetemplates\\")

// include(":data0")

// include(":feature:webview-main")
include(":webview")
project(":webview").projectDir = File("C:\\Synced\\WalhallaUI\\features\\webview\\")

