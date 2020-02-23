buildscript {
    repositories {
        jcenter()
        google()
    }

    dependencies {
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.3.0-alpha02")
        classpath("com.android.tools.build:gradle:3.5.3")
        classpath ("com.google.gms:google-services:4.3.3")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.0.0-beta02")
        classpath("com.google.firebase:perf-plugin:1.3.1")
        classpath(kotlin("gradle-plugin", version = "1.3.61"))
    }
}

allprojects {
    repositories {
        google()
        jcenter()
    }
}

val clean by tasks.registering(Delete::class) {
    delete(rootProject.buildDir)
}
