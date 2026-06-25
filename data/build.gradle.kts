import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import org.gradle.kotlin.dsl.android
import org.gradle.kotlin.dsl.hilt

plugins {
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.android.library)
    alias(libs.plugins.dagger.hilt)
    alias(libs.plugins.ksp)
}
fun getProperties(propertyKey: String): String {
    return gradleLocalProperties(rootDir,providers).getProperty(propertyKey)
}
android {
    namespace = "com.androiddev.data"
    compileSdk = 36

    defaultConfig {
        minSdk = 23

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        buildConfig = true
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(project(":domain"))
    implementation(libs.paging.runtime)
    //DataStore
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.datastore.core)
    // Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // Retrofit
    implementation (libs.retrofit)
    implementation (libs.converter.gson)
    implementation (libs.okhttp)
    implementation (libs.logging.interceptor)

// Dagger - Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    ksp (libs.androidx.hilt.compiler)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}