pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven(url = "https://jitpack.io")
    }
}


dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()
        // ✅ المستودعات الإضافية تُضاف هنا فقط
        maven(url = "https://jitpack.io")
        maven(url = "https://nexusmobile.fawrystaging.com:2597/repository/maven-public/")
    }
}

rootProject.name = "Salamtech"

// ✅ تضمين الموديولات بشكل منظم
include(":app")
include(":core")
include(":domain")
include(":commonlib")
