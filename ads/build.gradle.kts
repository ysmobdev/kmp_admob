plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinCocoapods)
    alias(libs.plugins.androidLibrary)
}

kotlin {
    androidTarget()
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    cocoapods {
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        version = "1.0"
        ios.deploymentTarget = "16.0"
        framework {
            baseName = "ads"
            isStatic = true
        }
        pod("Google-Mobile-Ads-SDK") {
            moduleName = "GoogleMobileAds"
            extraOpts += listOf("-compiler-option", "-fmodules")
        }
    }
    
    sourceSets {
        androidMain.dependencies {
            api(libs.playServices.ads)
        }
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
            implementation(projects.utils)
        }
        commonTest.dependencies {

        }
    }
}

android {
    namespace = "ua.wc.ads"
    compileSdk = 34
    defaultConfig {
        minSdk = 23
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}