plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.example.paycurrencyexchange"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.paycurrencyexchange"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val apiKey = project.properties["API_KEY"]?.toString() ?: "\"\""
        val baseUrl = project.properties["BASE_URL"]?.toString() ?: "\"\""
        buildConfigField("String", "API_KEY", apiKey)
        buildConfigField("String", "BASE_URL", baseUrl)
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
    kotlinOptions {
        jvmTarget = "1.8"
    }

    viewBinding {
        enable = true
    }

    buildFeatures {
        buildConfig = true
    }

    packagingOptions {
        exclude("META-INF/LICENSE.md")
    }

}

dependencies {

    val room_version = "2.6.1"

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.arch.core:core-testing:2.2.0")
    implementation("com.squareup.okhttp3:mockwebserver:4.12.0")
    testImplementation("io.mockk:mockk:1.12.4")
    testImplementation("io.mockk:mockk-android:1.12.3")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.1")


    //retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    //room
    implementation("androidx.room:room-ktx:$room_version")
    kapt("androidx.room:room-compiler:$room_version")

    //lifecycle
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.fragment:fragment-ktx:1.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")

    //hilt
    implementation("com.google.dagger:hilt-android:2.50")
    kapt("com.google.dagger:hilt-android-compiler:2.50")

}

kapt {
    correctErrorTypes = true
}