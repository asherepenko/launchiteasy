buildscript {
    repositories {
        jcenter()
        google()
    }

    dependencies {
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.3.0-alpha04")
        classpath("com.android.tools.build:gradle:3.6.2")
        classpath ("com.google.gms:google-services:4.3.3")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.0.0-beta03")
        classpath("com.google.firebase:perf-plugin:1.3.1")
        classpath(kotlin("gradle-plugin", version = "1.3.71"))
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
