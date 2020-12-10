buildscript {
    repositories {
        jcenter()
        google()
    }

    dependencies {
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.3.2")
        classpath("com.android.tools.build:gradle:4.1.1")
        classpath ("com.google.gms:google-services:4.3.4")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.4.1")
        classpath("com.google.firebase:perf-plugin:1.3.4")
        classpath("org.koin:koin-gradle-plugin:2.2.1")
        classpath(kotlin("gradle-plugin", version = "1.4.21"))
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
