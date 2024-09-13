plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("kotlin-kapt")
}

android {
    namespace = "com.example.sacrenachat"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.sacrenachat"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
    buildFeatures {
        buildConfig = true
        viewBinding = true
    }
    signingConfigs {
        // Saves the time at which the build was made.
        buildTypes {
            debug {
                buildConfigField(
                    "String",
                    "BUILD_TIME",
                    "\"" + System.currentTimeMillis().toString() + "\""
                )
            }
            release {
                buildConfigField(
                    "String",
                    "BUILD_TIME",
                    "\"" + System.currentTimeMillis().toString() + "\""
                )
            }
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")

    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:2.6.1")
    implementation("androidx.lifecycle:lifecycle-common-java8:2.6.1")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.cardview:cardview:1.0.0") {
        exclude(module = "support-v4")
    }
    implementation("androidx.recyclerview:recyclerview:1.2.1") {
        exclude(module = "support-v4")
    }
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.0")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.lifecycle:lifecycle-common-java8:2.3.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.0")
    implementation("com.squareup.moshi:moshi:1.12.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.12.0")
    implementation("com.github.bumptech.glide:glide:4.12.0")
    kapt("com.github.bumptech.glide:compiler:4.12.0")
//    implementation("com.github.CanHub:Android-Image-Cropper:3.3.5")
    implementation("com.vanniktech:android-image-cropper:4.6.0")

    implementation("com.afollestad.material-dialogs:core:3.3.0")
    implementation("com.afollestad.material-dialogs:datetime:3.3.0")
    implementation("com.afollestad.material-dialogs:bottomsheets:3.3.0")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("com.afollestad.assent:core:3.0.0-RC4")

    implementation("io.getstream:stream-chat-android-ui-components:6.4.3")
    implementation("io.getstream:stream-chat-android-offline:6.4.3")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-ktx:1.7.2")
    implementation("androidx.fragment:fragment-ktx:1.3.6")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}