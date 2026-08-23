import com.android.build.api.dsl.AndroidResources

plugins {
    alias(libs.plugins.android.application)
    id("org.jetbrains.kotlin.plugin.compose")
}


/*buildscript {
    dependencies {
        // For KGP
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:KGP_VERSION")

        // For KSP
        classpath("com.google.devtools.ksp:symbol-processing-gradle-plugin:KSP_VERSION")
    }
}*/

android {
    namespace = "com.trbear9.ui"
    compileSdk = 37

    lint {
        baseline = file("lint-baseline.xml")
    }

    defaultConfig {
//        ndk{
//            abiFilters += listOf("arm64-v8a","armeabi-v7a", "x86", "x86_64")
//        }
        applicationId = "com.trbear9.ui"
        minSdk = 25
        targetSdk = 37
        versionCode = 7
        versionName = "1.0.6"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            storeFile = file("/home/kujatic/Desktop/openfarm.jks")
            storePassword = "12x;13y?23~z!"
            keyAlias = "Jasper"
            keyPassword = "12x;13y?23~z!"
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isShrinkResources = true
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            ndk {
                debugSymbolLevel = "FULL"
            }
        }
    }


    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        mlModelBinding = true
        viewBinding = true
        compose = true
    }
//    buildToolsVersion = "35.0.0"
    dependenciesInfo {
        includeInApk = true
        includeInBundle = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }

    packaging {
        resources {
            pickFirsts += "messages/JavaOptionBundle.properties"
            pickFirsts += "kotlin/reflect/reflect.kotlin_builtins"
            pickFirsts += "DebugProbesKt.bin"
            pickFirsts += "META-INF/**"
            pickFirsts += "mozilla/public-suffix-list.txt"
        }
    }
}


dependencies {
    implementation("io.github.pseudoankit:coachmark:3.0.1")
    implementation(libs.androidx.compiler)
    implementation(libs.androidx.foundation.layout)
    implementation("com.github.jeziellago:compose-markdown:0.4.1")
//    implementation("com.google.ai.edge.litert:litert-support-api:2.2.0")
//    implementation("com.google.ai.edge.litert:litert-support:1.4.0")
    implementation("com.google.ai.edge.litert:litert:2.2.0")
    implementation("com.open-meteo:open-meteo-api-kotlin:0.7.1-beta.1")
    implementation(libs.androidx.ui.test.junit4)
    implementation("org.apache.commons:commons-csv:1.10.0")
//    implementation("org.slf4j:slf4j-api:2.0.17")
    implementation ("androidx.paging:paging-runtime:3.3.6")
    implementation ("androidx.paging:paging-compose:3.3.6")
    implementation(libs.vico.compose)
//    implementation(libs.vico.compose.m2)
//    implementation(libs.vico.compose.m3)
//    implementation(libs.vico.multiplatform)
    implementation(libs.vico.views)
//    implementation(libs.vico.multiplatform.m2)
//    implementation(libs.vico.multiplatform.m3)
    implementation("io.coil-kt:coil-compose:2.6.0")
    implementation("com.google.accompanist:accompanist-flowlayout:0.30.1")
    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation("androidx.compose.material:material-icons-extended:1.5.0")
    implementation("androidx.navigation:navigation-compose:2.9.4")
//    implementation("com.github.TrainingBear.OPSI-PlantAPI:api:3.1.3"){
//        exclude(group = "org.jetbrains.kotlin")
//        exclude(group = "org.jetbrains")
//        exclude("messages/JavaOptionBundle.properties")
//    }
    implementation("com.fasterxml.jackson.core:jackson-databind:2.20.0")
    implementation("androidx.viewpager2:viewpager2:1.1.0")
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.leanback)
    implementation(libs.glide)
    implementation(libs.activity)
    implementation(libs.mediarouter)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.room.compiler.processing.testing){
        exclude(group = "org.checkerframework", module = "checker-qual")
        exclude(group = "com.google.auto.value", module = "auto-value")
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-compiler-embeddable")
    }

    implementation(libs.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.video)

    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.extensions)
    implementation(libs.core.ktx)
    implementation(libs.androidx.foundation.android)
    implementation(libs.androidx.material3.android)
    implementation(libs.firebase.crashlytics.buildtools)

    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    testCompileOnly(libs.lombok)
    testAnnotationProcessor(libs.lombok)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.tooling.preview)
    debugImplementation(libs.androidx.ui.tooling)
    testImplementation(kotlin("test"))

}

tasks.register("printDirectDependencies") {
    doLast {
        configurations["implementation"].allDependencies.forEach {
            println("- ${it.group}:${it.name}:${it.version}")
        }
    }
}