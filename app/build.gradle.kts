import java.io.FileInputStream
import java.util.Properties
import org.jetbrains.kotlin.config.KotlinCompilerVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

plugins {
    id("com.android.application")
    id("com.github.triplet.play") version "2.7.2"
    id("org.jlleitschuh.gradle.ktlint") version "9.2.1"
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
}

val archivesBaseName = "launchiteasy"
val buildVersion = BuildVersion(rootProject.file("version"))

val localPropertiesFile = rootProject.file("local.properties")
val keystorePropertiesFile = rootProject.file("keystore.properties")
val playstorePropertiesFile = rootProject.file("playstore.properties")

android {
    compileSdkVersion(29)

    defaultConfig {
        minSdkVersion(23)
        targetSdkVersion(29)
        applicationId = "com.sherepenko.android.launchiteasy"
        versionCode = buildVersion.versionCode
        versionName = buildVersion.versionName
        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        setProperty("archivesBaseName", "$archivesBaseName-$versionName")

        javaCompileOptions {
            annotationProcessorOptions {
                arguments = mapOf("room.schemaLocation" to "$projectDir/schemas")
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

    compileOptions {
        coreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    lintOptions {
        ignore("InvalidPackage")
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
        getByName("release") {
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

        serviceAccountCredentials = rootProject.file(
            playstoreProperties.getProperty("playstore.credentials")
        )
        defaultToAppBundles = true
        track = "alpha"
        releaseStatus = "inProgress"
    } else if (!System.getenv("PLAYSTORE_CREDENTIALS").isNullOrEmpty()) {
        serviceAccountCredentials = rootProject.file(
            System.getenv("PLAYSTORE_CREDENTIALS")
        )
        defaultToAppBundles = true
        track = "alpha"
        releaseStatus = "inProgress"
    } else {
        isEnabled = false
    }
}

val glideVersion = "4.11.0"
val jacksonVersion = "2.11.0"
val koinVersion = "2.1.5"
val lifecycleVersion = "2.2.0"
val navigationVersion = "2.3.0-beta01"
val okHttpVersion = "4.6.0"
val retrofitVersion = "2.8.1"
val roomVersion = "2.2.5"
val stethoVersion = "1.5.1"
val workVersion = "2.3.4"

dependencies {
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.0.5")
    kapt("androidx.lifecycle:lifecycle-compiler:$lifecycleVersion")
    kapt("androidx.room:room-compiler:$roomVersion")
    kapt("com.github.bumptech.glide:compiler:$glideVersion")
    implementation(kotlin("stdlib-jdk8", KotlinCompilerVersion.VERSION))
    implementation("androidx.appcompat:appcompat:1.1.0")
    implementation("androidx.collection:collection-ktx:1.1.0")
    implementation("androidx.constraintlayout:constraintlayout:2.0.0-beta6")
    implementation("androidx.core:core-ktx:1.3.0")
    implementation("androidx.fragment:fragment-ktx:1.2.4")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:$lifecycleVersion")
    implementation("androidx.navigation:navigation-fragment-ktx:$navigationVersion")
    implementation("androidx.navigation:navigation-runtime-ktx:$navigationVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navigationVersion")
    implementation("androidx.preference:preference-ktx:1.1.1")
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0-rc01")
    implementation("androidx.work:work-runtime-ktx:$workVersion")
    implementation("com.facebook.stetho:stetho:$stethoVersion")
    implementation("com.facebook.stetho:stetho-okhttp3:$stethoVersion")
    implementation("com.fasterxml.jackson.core:jackson-annotations:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    implementation("com.github.bumptech.glide:glide:$glideVersion")
    implementation("io.github.inflationx:calligraphy3:3.1.1")
    implementation("io.github.inflationx:viewpump:2.0.3")
    implementation("com.google.android.gms:play-services-location:17.0.0")
    implementation("com.google.android.material:material:1.2.0-beta01")
    implementation("com.google.firebase:firebase-analytics:17.4.2")
    implementation("com.google.firebase:firebase-config-ktx:19.1.4")
    implementation("com.google.firebase:firebase-crashlytics:17.0.0")
    implementation("com.google.firebase:firebase-messaging:20.2.0")
    implementation("com.google.firebase:firebase-perf:19.0.7")
    implementation("com.jakewharton.timber:timber:4.7.1")
    implementation("com.squareup.okhttp3:okhttp:$okHttpVersion")
    implementation("com.squareup.okhttp3:logging-interceptor:$okHttpVersion")
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-jackson:$retrofitVersion")
    implementation("org.koin:koin-androidx-ext:$koinVersion")
    implementation("org.koin:koin-androidx-scope:$koinVersion")
    implementation("org.koin:koin-androidx-viewmodel:$koinVersion")
    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.3")
    testImplementation("junit:junit:4.13")
    testImplementation("androidx.test:core:1.2.0")
    testImplementation("androidx.test:runner:1.2.0")
    testImplementation("androidx.test.ext:junit:1.1.1")
    testImplementation("com.google.truth:truth:0.44")
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")
    testImplementation("org.koin:koin-test:$koinVersion")
    testImplementation("org.robolectric:robolectric:4.3.1")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }

    val incrementMajor by registering(IncrementVersion::class) {
        increment = Increment.MAJOR
        version = buildVersion
    }

    val incrementMinor by registering(IncrementVersion::class) {
        increment = Increment.MINOR
        version = buildVersion
    }

    val incrementPatch by registering(IncrementVersion::class) {
        increment = Increment.PATCH
        version = buildVersion
    }
}

apply(plugin = "androidx.navigation.safeargs.kotlin")
apply(plugin = "com.google.gms.google-services")
apply(plugin = "com.google.firebase.crashlytics")
apply(plugin = "com.google.firebase.firebase-perf")
