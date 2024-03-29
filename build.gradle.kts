// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application").version("8.0.0").apply(false)
    id("com.android.library").version("8.0.0").apply(false)
    id("androidx.navigation.safeargs.kotlin").version("2.5.3").apply(false)
    id("org.jetbrains.kotlin.kapt").version("1.8.20").apply(false)
    id("org.jetbrains.kotlin.android") version "1.8.20" apply false
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}