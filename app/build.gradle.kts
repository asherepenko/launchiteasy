import com.github.triplet.gradle.androidpublisher.ReleaseStatus
import java.io.FileInputStream
import java.util.Properties
import org.jetbrains.kotlin.config.KotlinCompilerVersion
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.google.services)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.androidx.navigation.safeargs)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.firebase.perf)
    alias(libs.plugins.build.version)
    alias(libs.plugins.play.publisher)
}

val archivesBaseName = "launchiteasy"

val localPropertiesFile = rootProject.file("local.properties")
val keystorePropertiesFile = rootProject.file("keystore.properties")
val playstorePropertiesFile = rootProject.file("playstore.properties")

android {
    compileSdk = 33

    namespace = "com.sherepenko.android.launchiteasy"

    defaultConfig {
        minSdk = 23
        targetSdk = 33
        applicationId = "com.sherepenko.android.launchiteasy"
        versionCode = buildVersion.versionCode
        versionName = buildVersion.versionName
        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        setProperty("archivesBaseName", "$archivesBaseName-$versionName")

        javaCompileOptions {
            annotationProcessorOptions {
                argument("room.schemaLocation", "$projectDir/schemas")
            }
        }

        if (localPropertiesFile.exists()) {
            val localProperties = Properties().apply {
                load(FileInputStream(localPropertiesFile))
            }

            buildConfigField(
                "String",
                "OPEN_WEATHER_API_KEY",
                "\"${localProperties.getProperty("openweather.apiKey", "")}\""
            )
        } else {
            buildConfigField(
                "String",
                "OPEN_WEATHER_API_KEY",
                "\"${System.getenv("OPEN_WEATHER_API_KEY")}\""
            )
        }

        buildConfigField("int", "WEATHER_FORECASTS_LIMIT", "12")
    }

    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }

    packagingOptions {
        exclude("META-INF/LICENSE")
        exclude("META-INF/NOTICE")
    }

    testOptions {
        unitTests.apply {
            isIncludeAndroidResources = true
        }
    }

    signingConfigs {
        create("release") {
            if (keystorePropertiesFile.exists()) {
                val keystoreProperties = Properties().apply {
                    load(FileInputStream(keystorePropertiesFile))
                }

                storeFile = rootProject.file(keystoreProperties.getProperty("keystore.sign.file"))
                storePassword = keystoreProperties.getProperty("keystore.sign.password")
                keyAlias = keystoreProperties.getProperty("keystore.sign.key.alias")
                keyPassword = keystoreProperties.getProperty("keystore.sign.key.password")
            } else if (!System.getenv("KEYSTORE_FILE").isNullOrEmpty()) {
                storeFile = rootProject.file(System.getenv("KEYSTORE_FILE"))
                storePassword = System.getenv("KEYSTORE_PASSWORD")
                keyAlias = System.getenv("KEYSTORE_KEY_ALIAS")
                keyPassword = System.getenv("KEYSTORE_KEY_PASSWORD")
            } else {
                val debugSigningConfig = getByName("debug")

                storeFile = debugSigningConfig.storeFile
                storePassword = debugSigningConfig.storePassword
                keyAlias = debugSigningConfig.keyAlias
                keyPassword = debugSigningConfig.keyPassword
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

ktlint {
    verbose.set(true)
    android.set(true)

    reporters {
        reporter(ReporterType.PLAIN)
        reporter(ReporterType.CHECKSTYLE)
    }
}

play {
    if (playstorePropertiesFile.exists()) {
        val playstoreProperties = Properties().apply {
            load(FileInputStream(playstorePropertiesFile))
        }

        serviceAccountCredentials.set(
            rootProject.file(playstoreProperties.getProperty("playstore.credentials"))
        )
        defaultToAppBundles.set(true)
        track.set("alpha")
        releaseStatus.set(ReleaseStatus.IN_PROGRESS)
    } else if (!System.getenv("PLAYSTORE_CREDENTIALS").isNullOrEmpty()) {
        serviceAccountCredentials.set(
            rootProject.file(System.getenv("PLAYSTORE_CREDENTIALS"))
        )
        defaultToAppBundles.set(true)
        track.set("alpha")
        releaseStatus.set(ReleaseStatus.IN_PROGRESS)
    } else {
        enabled.set(false)
    }
}

dependencies {
    coreLibraryDesugaring(libs.android.desugar.jdk)
    ksp(libs.androidx.room.compiler)
    implementation(kotlin("stdlib-jdk8", KotlinCompilerVersion.VERSION))
    implementation(platform(libs.firebase.bom))
    implementation(platform(libs.okhttp.bom))
    implementation(libs.android.location)
    implementation(libs.android.material)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.collection.ktx)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.preference.ktx)
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.coil)
    implementation(libs.koin)
    implementation(libs.timber)
    implementation(libs.bundles.androidx.lifecycle)
    implementation(libs.bundles.androidx.navigation)
    implementation(libs.bundles.androidx.room)
    implementation(libs.bundles.firebase)
    implementation(libs.bundles.inflationx.calligraphy)
    implementation(libs.bundles.jackson)
    implementation(libs.bundles.okhttp)
    implementation(libs.bundles.retrofit)
    implementation(libs.bundles.stetho)
}
