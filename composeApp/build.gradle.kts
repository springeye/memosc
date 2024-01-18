import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    kotlin("plugin.serialization") version "1.9.21"
    id("com.google.devtools.ksp") version "1.9.21-1.0.16"
    id("de.jensklingenberg.ktorfit") version "1.11.0"
    id("app.cash.sqldelight") version "2.0.1"

}
sqldelight {
    databases {
        create("AppDatabase") {
            packageName.set("com.github.springeye.memosc.db.model")
        }
    }
}
val ktorfitVersion = "1.11.0"
val sqllinVersion = "1.2.3"
kotlin {

    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

    jvm("desktop")
//
//    listOf(
//        iosX64(),
//        iosArm64(),
//        iosSimulatorArm64()
//    ).forEach { iosTarget ->
//        iosTarget.binaries.framework {
//            baseName = "ComposeApp"
//            isStatic = true
//        }
//    }

    sourceSets {
        val desktopMain by getting
//        iosMain.dependencies {
////            implementation(libs.ktor.client.darwin)
//            implementation("app.cash.paging:paging-runtime-uikit:3.3.0-alpha02-0.4.0")
//        }
        androidMain.dependencies {
            implementation(libs.compose.ui.tooling.preview.android)
            implementation(libs.androidx.activity.compose)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.kotlinx.coroutines.android)
            api(libs.androidx.startup)
            implementation(libs.android.driver)
            implementation(libs.koin.android)
            // Jetpack WorkManager
            implementation(libs.koin.androidx.workmanager)
            // Navigation Graph
            implementation(libs.koin.androidx.navigation)
            implementation(libs.koin.androidx.compose)
//            implementation(libs.voyager.bottomSheetNavigator)
//            implementation(libs.voyager.tabNavigator)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
            implementation(libs.ktor.client.cio)
            implementation(libs.jetbrains.ui.tooling.preview.desktop)
            implementation(libs.androidx.ui.graphics.desktop)
            implementation(libs.sqlite.driver)

        }

        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
//            implementation(compose.material)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)
            @OptIn(ExperimentalComposeLibrary::class)
            implementation(compose.components.resources)
            implementation(libs.ktor.client.core)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.okio)
            implementation(libs.stately.common)

            // Multiplatform

            // Navigator
            implementation(libs.voyager.navigator)

            // Screen Model
            implementation(libs.voyager.screenModel)

            // BottomSheetNavigator
            implementation(libs.voyager.bottomSheetNavigator)

            // TabNavigator
            implementation(libs.voyager.tabNavigator)

            // Transitions
            implementation(libs.voyager.transitions)

            implementation(libs.voyager.koin)
            implementation(libs.koin.core)
            implementation(libs.koin.compose)


            implementation(libs.kotlin.stdlib)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.datetime)


            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.logging)
            implementation(libs.napier)


            implementation(libs.napier)

            implementation(libs.androidx.datastore.preferences.core)
//            api(libs.retrofit)
//            api(libs.retrofit.converter.gson)
//            implementation(libs.okhttp)
//            api(libs.okhttp.interceptor.logging)

            implementation(libs.ktorfit.lib)

            implementation(libs.multiplatform.markdown.renderer)

            implementation(libs.kamel.image)
            implementation(libs.mpfilepicker)
            implementation(libs.highlights)
            implementation(libs.napier.v271)
            implementation(libs.paging.compose.common)
            implementation(libs.androidx.paging3.extensions)
            implementation("app.cash.sqldelight:coroutines-extensions:2.0.1")
        }

    }
}

android {
    namespace = "com.github.springeye.memosc"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "com.github.springeye.memosc"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    dependencies {
        debugImplementation(libs.compose.ui.tooling)

    }
}

compose.desktop {
    application {
        mainClass = "com.github.springeye.memosc.MainKt"
        buildTypes.release {
            proguard {
                isEnabled=false
                configurationFiles.from("rules.pro")
            }
        }
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi,TargetFormat.Exe, TargetFormat.Deb)
            packageName = "memosc"
            packageVersion = libs.versions.packageVersion.get()

            linux{
                iconFile=rootProject.file("launcher/logo.png")
            }
            macOS{
                iconFile=rootProject.file("launcher/logo.png")
            }
            windows{
                shortcut=true
                menuGroup=packageName
                iconFile=rootProject.file("launcher/logo.png")
            }
        }
    }
}
ksp{
}
dependencies {

    add("kspCommonMainMetadata", "de.jensklingenberg.ktorfit:ktorfit-ksp:$ktorfitVersion")
//    add("kspIosX64","de.jensklingenberg.ktorfit:ktorfit-ksp:$ktorfitVersion")
//    add("kspIosArm64","de.jensklingenberg.ktorfit:ktorfit-ksp:$ktorfitVersion")
//    add("kspIosSimulatorArm64","de.jensklingenberg.ktorfit:ktorfit-ksp:$ktorfitVersion")
    add("kspDesktop","de.jensklingenberg.ktorfit:ktorfit-ksp:$ktorfitVersion")
    add("kspAndroid","de.jensklingenberg.ktorfit:ktorfit-ksp:$ktorfitVersion")
//    add("kspLinuxArm64","de.jensklingenberg.ktorfit:ktorfit-ksp:$ktorfitVersion")
//    add("kspLinuxX64","de.jensklingenberg.ktorfit:ktorfit-ksp:$ktorfitVersion")

}