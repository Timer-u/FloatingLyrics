import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
    alias(libs.plugins.compose.compiler)
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
        versionCode = 20241208
        versionName = "20241208"

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

    // 视图绑定工具
    implementation(libs.androidx.ui.viewbinding)

    // Junit5
    testImplementation(libs.junit.jupiter)

    // Mockito
    testImplementation(libs.mockito.core)

    // Apache Commons Text
    implementation(libs.commons.text)

    // LeakCanary
    debugImplementation(libs.leakcanary.android)

    // DocumentFile
    implementation(libs.androidx.documentfile)

    // Room
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
    implementation(libs.com.google.devtools.ksp.gradle.plugin)
    testImplementation(libs.androidx.room.testing)

    // NetWork
    implementation(libs.coil3.coil.compose)
    implementation(libs.coil.network.okhttp)
    implementation(libs.gson)
    // okhttp3
    implementation(platform(libs.okhttp.bom))
    implementation(libs.squareup.okhttp)
    implementation(libs.logging.interceptor)
    testImplementation(libs.mockwebserver)

    // Icons
    implementation(libs.material.icons.extended)
    implementation(libs.material.icons.core)

    //Navigation
    implementation(libs.navigation.compose)
    androidTestImplementation(libs.navigation.testing)

    // ConstraintLayout
    implementation(libs.constraintlayout)
    implementation(libs.constraintlayout.compose)

    // LifeCycle
    implementation(libs.androidx.lifecycle.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.lifecycle.livedata.core.ktx)
    implementation(libs.androidx.lifecycle.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.viewmodel.savedstate)
    implementation(libs.androidx.lifecycle.service)
    implementation(libs.androidx.lifecycle.lifecycle.runtime.compose)
    testImplementation(libs.androidx.lifecycle.lifecycle.runtime.testing)

    // Accompanist
    implementation(libs.google.accompanist.swiperefresh)

    implementation(libs.runtime.livedata)
    implementation(libs.androidx.material)

    // 初始依赖
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}