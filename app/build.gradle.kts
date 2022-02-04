plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    compileSdk = 32
    defaultConfig {
        applicationId = "ru.zrd.vcblr"
        minSdk = 31
        targetSdk = 32
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        dataBinding = true
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.4.1")
    implementation("com.google.android.material:material:1.5.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.3")
    //implementation("androidx.legacy:legacy-support-v4:1.0.0")

    // csv parser
    implementation("net.sf.supercsv:super-csv:2.4.0")

    // preferences
    implementation("androidx.preference:preference:1.2.0")

    // navigation + androidx.fragment.app.viewModels function
    implementation("androidx.navigation:navigation-fragment-ktx:2.4.0")
    // navigation
    implementation("androidx.navigation:navigation-ui-ktx:2.4.0")

    // app database api
    implementation("androidx.room:room-runtime:2.4.1")
    implementation("androidx.room:room-ktx:2.4.1") // Kotlin Extensions and Coroutines support for Room
    kapt("androidx.room:room-compiler:2.4.1") // use Kotlin annotation processing tool (kapt)

    testImplementation ("junit:junit:4.13.2")

    androidTestImplementation ("androidx.test.ext:junit:1.1.3")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.4.0")
}