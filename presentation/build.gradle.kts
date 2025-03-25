import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.dagger.hilt)
    alias(libs.plugins.serialization)
    alias(libs.plugins.ksp)
}
fun getProperties(propertyKey: String): String {
    return gradleLocalProperties(rootDir,providers).getProperty(propertyKey)
}
android {
    namespace = "com.androiddev.snsappwithcompose"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.androiddev.snsappwithcompose"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        buildConfigField("String", "KAKAO_API_KEY", getProperties("KAKAO_API_KEY"))
        resValue("string", "KAKAO_REDIRECT_URI", "kakao${getProperties("KAKAO_API_KEY")}")
        buildConfigField("String", "NAVER_CLIENT_ID", getProperties("NAVER_CLIENT_ID"))
        buildConfigField("String", "NAVER_CLIENT_SECRET", getProperties("NAVER_CLIENT_SECRET"))
        buildConfigField("String", "BASE_URL", getProperties("BASE_URL"))
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {


    implementation(project(":domain"))
    implementation(project(":data"))

    implementation(libs.coil3.compose)
    implementation(libs.coil3.okhttp)


    //tabrow
    implementation(libs.accompanist.pager)
    implementation(libs.accompanist.pager.indicators)
    //location service
    implementation(libs.service.location)
    // Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // Dagger - Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    ksp(libs.androidx.hilt.compiler)

    implementation(libs.material.icons)
    implementation(libs.kotlinx.serialization.json)


    implementation(libs.navigation.compose)
    implementation(libs.hilt.navigation.compose)

    //네이버로그인
    implementation(libs.navercorp.nid.oauth)
    implementation(libs.androidx.security.crypto)
    implementation(libs.support.core.utils)
    implementation(libs.navercorp.nid.oauth)
    implementation (libs.androidx.security.crypto)
    implementation (libs.support.core.utils)
    implementation (libs.androidx.browser)

    //카톡로그인
    implementation(libs.kakao.v2.all) // 전체 모듈 설치, 2.11.0 버전부터 지원
    implementation(libs.kakao.v2.user)
    //snapper fling behavior
    implementation(libs.dev.chrisbanes.snapper)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material3.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}