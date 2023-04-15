plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    compileSdk = 33
    defaultConfig {
        applicationId = "ru.zrd.vcblr"
        minSdk = 32
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
        dataBinding = true
    }
    namespace = "ru.zrd.vcblr"
}

kotlin {
    jvmToolchain(11)
}

dependencies {
    implementation("androidx.core:core-ktx:1.10.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    //implementation("androidx.legacy:legacy-support-v4:1.0.0")

    // csv parser
    implementation("net.sf.supercsv:super-csv:2.4.0")

    // preferences
    implementation("androidx.preference:preference:1.2.0")

    // navigation + androidx.fragment.app.viewModels function
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.3")
    // navigation
    implementation("androidx.navigation:navigation-ui-ktx:2.5.3")

    // app database api
    implementation("androidx.room:room-runtime:2.5.1")
    implementation("androidx.room:room-ktx:2.5.1") // Kotlin Extensions and Coroutines support for Room
    kapt("androidx.room:room-compiler:2.5.1") // use Kotlin annotation processing tool (kapt)

    testImplementation ("junit:junit:4.13.2")

    androidTestImplementation ("androidx.test.ext:junit:1.1.5")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.5.1")
}