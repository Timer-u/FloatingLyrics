import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.plugin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
    alias(libs.plugins.kotlin.android)
}

room {
    schemaDirectory("$projectDir/schemas")
}


android {
    namespace = "com.wzvideni.floatinglyrics"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.wzvideni.floatinglyrics"
        minSdk = 23
        targetSdk = 35
        versionCode = 20250510
        versionName = "20250510"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    // Room测试
    sourceSets {
        // Adds exported schema location as test app assets.
        getByName("androidTest").assets.srcDir("$projectDir/schemas")
    }

    signingConfigs {
        create("wzvideni") {
            val keystorePropertiesFile: File = rootProject.file("keystore.properties")
            val keystoreProperties = Properties()
            keystoreProperties.load(FileInputStream(keystorePropertiesFile))
            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
            storeFile = file(keystoreProperties["storeFile"] as String)
            storePassword = keystoreProperties["storePassword"] as String
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("wzvideni")
            isDebuggable = false
            // Enables code shrinking, obfuscation, and optimization for only
            // your project's release build type. Make sure to use a build
            // variant with `isDebuggable=false`.
            isMinifyEnabled = true

            // Enables resource shrinking, which is performed by the
            // Android Gradle plugin.
            isShrinkResources = true

            proguardFiles(
                // Includes the default ProGuard rules files that are packaged with
                // the Android Gradle plugin. To learn more, go to the section about
                // R8 configuration files.
                getDefaultProguardFile("proguard-android-optimize.txt"),

                // Includes a local, custom Proguard rules file
                "proguard-rules.pro"
            )
        }
    }
    // 启用数据绑定
    dataBinding {
        enable = true
    }

    // 启用视图绑定
    viewBinding {
        enable = true
    }

    applicationVariants.all {
        outputs.map { it as com.android.build.gradle.internal.api.BaseVariantOutputImpl }
            .forEach { output ->
                val outputFileName = "FloatingLyrics_c${versionCode}_${buildType.name}.apk"
                output.outputFileName = outputFileName
            }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlinOptions {
        jvmTarget = "21"
    }
    buildFeatures {
        compose = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            // 在正式版APK中排除DebugProbesKt.bin
            excludes += "DebugProbesKt.bin"
        }
    }
}

dependencies {

    // Apache Commons Text
    implementation(libs.commons.text)

    // LeakCanary
    debugImplementation(libs.leakcanary.android)


    // KSP
    implementation(libs.google.devtools.ksp.gradle.plugin)

    // Gson
    implementation(libs.google.code.gson)

    // LiveData
    implementation(libs.androidx.compose.runtime.livedata)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    testImplementation(libs.androidx.room.testing)

    // Coil3
    implementation(libs.coil3.kt.compose)
    implementation(libs.coil3.kt.okhttp)

    //Navigation
    implementation(libs.navigation.compose)
    androidTestImplementation(libs.navigation.testing)

    // ConstraintLayout
    implementation(libs.constraintlayout)
    implementation(libs.constraintlayout.compose)

    // Accompanist
    implementation(libs.google.accompanist.swiperefresh)

    // okhttp3
    implementation(platform(libs.squareup.okhttp3.bom))
    implementation(libs.squareup.okhttp3)
    implementation(libs.squareup.logging.interceptor)
    testImplementation(libs.squareup.mockwebserver)

    // ComposeMaterial
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.material.icons.extended)

    // LifeCycle
    implementation(libs.androidx.lifecycle.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.lifecycle.livedata.core.ktx)
    implementation(libs.androidx.lifecycle.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.viewmodel.savedstate)
    implementation(libs.androidx.lifecycle.service)
    implementation(libs.androidx.lifecycle.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.lifecycle.runtime.compose)
    testImplementation(libs.androidx.lifecycle.lifecycle.runtime.testing)

    // ComposeUi
    implementation(libs.androidx.compose.ui.viewbinding)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    // ComposeBom
    implementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(platform(libs.androidx.compose.bom))

    // Basic
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.material3)

    // Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}