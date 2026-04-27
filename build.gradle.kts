plugins {
    id("com.android.application") version "8.12.3" apply false
    id("com.android.library") version "8.12.3" apply false
    id("org.jetbrains.kotlin.android") version "2.2.21" apply false
    id("androidx.navigation.safeargs.kotlin") version "2.9.5" apply false

    // ✅ Firebase & Google Services
    id("com.google.gms.google-services") version "4.4.4" apply false
    id("com.google.firebase.crashlytics") version "3.0.6" apply false

    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin") version "2.0.1" apply false
}

// ✅ تنظيف المشروع
tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}
