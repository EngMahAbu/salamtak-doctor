plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("androidx.navigation.safeargs")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "com.salamtak"
    compileSdk = 35
    defaultConfig {
        applicationId = "com.salamtak.salamtakdoctor"
        minSdk = 24
        targetSdk = 35
        versionCode = 38
        versionName = "2.2.9"
        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
    }

    splits {
        abi {
            isEnable = true
            isUniversalApk = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    packagingOptions {
        resources {
            excludes += "org/joda/time/format/*.properties"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17 // يفضل 17 للمشاريع الحديثة
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
        buildConfig = true
    }

    lint {
        abortOnError = false
        checkReleaseBuilds = false
    }

    bundle {
        language {
            enableSplit = false
        }
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.2.21")

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.13.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("androidx.coordinatorlayout:coordinatorlayout:1.3.0")
    implementation("androidx.recyclerview:recyclerview:1.4.0")
    implementation(project(":domain"))
    //implementation(project(":commonlib"))
    implementation(project(":core"))


    implementation("androidx.compose.ui:ui:1.9.4")
    implementation("androidx.media3:media3-session:1.8.0")
    implementation("org.chromium.net:cronet-embedded:141.7340.3")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")

    implementation("androidx.multidex:multidex:2.0.1")
    implementation("id.zelory:compressor:3.0.1")

    // Retrofit + Moshi
    val version_retrofit = "2.8.2"
    val version_moshi = "1.11.0"
    implementation("com.squareup.retrofit2:retrofit:$version_retrofit")
    implementation("com.squareup.retrofit2:converter-moshi:$version_retrofit")
    implementation("com.squareup.moshi:moshi:$version_moshi")
    implementation("com.squareup.moshi:moshi-kotlin:$version_moshi")
    implementation("com.squareup.retrofit2:converter-gson:3.0.0")

    // UI libs
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.9.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.9.5")

    // Coroutines
    val version_kotlin_coroutines = "1.5.0"
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$version_kotlin_coroutines")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$version_kotlin_coroutines")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.10.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.4")

    // Retrofit Coroutines Adapter
    val version_retrofit_coroutines_adapter = "0.9.2"
    implementation("com.jakewharton.retrofit:retrofit2-kotlin-coroutines-adapter:$version_retrofit_coroutines_adapter")

    // Koin
    val koin_version = "3.0.1"
    implementation("io.insert-koin:koin-android:3.5.6")
    implementation("io.insert-koin:koin-core:3.5.6")


    implementation("com.google.android.gms:play-services-auth:21.4.0")
    implementation("com.google.android.gms:play-services-auth-api-phone:18.3.0")
    implementation("androidx.fragment:fragment:1.8.9")
    implementation("androidx.fragment:fragment-ktx:1.8.9")

    // UI Helpers

    implementation("com.squareup.okhttp3:logging-interceptor:5.2.1")
    implementation("com.squareup.okhttp3:okhttp:5.3.0")

    // Images
    val glideVersion = "4.13.0"
    implementation("com.github.bumptech.glide:glide:$glideVersion")
    kapt("com.github.bumptech.glide:compiler:$glideVersion")
    implementation("com.squareup.picasso:picasso:2.71828")

    // OTP Views
    implementation("com.github.mukeshsolanki:android-otpview-pinview:2.1.2")
    implementation("com.github.aabhasr1:OtpView:v1.1.2")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.6.0"))

    // Firebase services
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-messaging-ktx")
    implementation("com.google.firebase:firebase-config-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")

    // Google Sign-In
    implementation("com.google.android.gms:play-services-auth:21.4.0")


    // Maps
    implementation("com.google.android.gms:play-services-maps:19.2.0")
    implementation("com.google.code.gson:gson:2.13.2")

    // Networking
    implementation("com.github.amitshekhariitbhu.Fast-Android-Networking:android-networking:1.0.4")

    // UI extra
    implementation("com.github.jakob-grabner:Circle-Progress-View:1.4")
    implementation("com.akexorcist:snap-time-picker:1.0.3")
    implementation("com.facebook.android:facebook-android-sdk:18.1.3")
    implementation("com.pierfrancescosoffritti.androidyoutubeplayer:core:11.0.1")
    implementation("com.pierfrancescosoffritti.androidyoutubeplayer:chromecast-sender:0.32")
    implementation("androidx.work:work-runtime-ktx:2.11.0")
    implementation("com.airbnb.android:lottie:6.7.0")
    implementation("com.archit.calendar:awesome-calendar:2.0.0")
    implementation("com.hbb20:ccp:2.7.3")
    implementation("com.github.jonasmferreira:StateProgressBar:1.0")
    implementation("io.agora.rtc:full-sdk:4.6.0")
    implementation("commons-codec:commons-codec:1.20.0")
    implementation("io.coil-kt:coil:2.7.0")
    implementation("com.fawry.fawrypay:sdk:0.1.84")
    implementation("com.karumi:dexter:6.2.3")
    implementation("com.makeramen:roundedimageview:2.3.0")
    implementation("com.intuit.sdp:sdp-android:1.1.1")
    implementation("com.intuit.ssp:ssp-android:1.1.1")
}


