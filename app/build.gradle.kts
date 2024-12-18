import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import org.gradle.kotlin.dsl.android


plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.kotlin.plugin.compose")
}
fun getApiKey(propertyKey: String): String {
    return gradleLocalProperties(rootDir,providers).getProperty(propertyKey)
}
android {
    namespace = "com.androiddev.snsappwithcompose"
    compileSdk = 34

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
        buildConfigField("String", "KAKAO_API_KEY", getApiKey("KAKAO_API_KEY"))
        resValue("string", "KAKAO_REDIRECT_URI", "kakao${getApiKey("KAKAO_API_KEY")}")
        buildConfigField("String", "GOOGLE_OAUTH_CLIENT_ID", getApiKey("GOOGLE_OAUTH_CLIENT_ID"))
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
    //composeOptions {
    //    kotlinCompilerExtensionVersion = "1.5.1"
   // }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okhttp3:okhttp:5.0.0-alpha.2")
    implementation ("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.2")
    // Dagger - Hilt
    implementation("com.google.dagger:hilt-android:2.50")
    kapt("com.google.dagger:hilt-android-compiler:2.50")
    kapt ("androidx.hilt:hilt-compiler:1.2.0")

    implementation("androidx.navigation:navigation-compose:2.8.5")
    // implementation("androidx.compose.material:material-icons-extended:$compose_ui_version")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")


    //카톡로그인
    implementation("com.kakao.sdk:v2-all:2.12.1") // 전체 모듈 설치, 2.11.0 버전부터 지원
    implementation("com.kakao.sdk:v2-user:2.12.1")

    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation(platform("androidx.compose:compose-bom:2024.12.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    //implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material3:material3-android:1.3.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.12.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}