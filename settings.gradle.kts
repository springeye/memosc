rootProject.name = "memosc"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
//        maven("https://maven.aliyun.com/repository/public/")
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {

        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")


    }
}

include(":composeApp")