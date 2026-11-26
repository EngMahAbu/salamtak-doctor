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
        maven {
            url = uri("https://nexus-mobile.fawrystaging.com:8047/repository/maven-public/")
            credentials {
                username = "integration"
                password = "Mobile#F@wry#2022"
            }
            metadataSources {
                mavenPom()
                artifact()
            }
            content {
                includeGroup("com.fawry.fawrypay")
            }
        }
    }
}

rootProject.name = "Salamtech"

// ✅ تضمين الموديولات بشكل منظم
include(":app")
include(":core")
include(":domain")
include(":commonlib")
