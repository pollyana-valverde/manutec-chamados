plugins {
    alias(libs.plugins.android.application)
    // Lê o app/google-services.json (que NÃO vai para o Git). Veja o README.
    alias(libs.plugins.google.services)
}

android {
    namespace = "br.com.techinova.manutec"
    compileSdk = 36

    defaultConfig {
        applicationId = "br.com.techinova.manutec"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.recyclerview)
    implementation(libs.activity)

    // Firebase: o BoM define as versões de Auth e Firestore
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)

    // Leitura e geração de QR Code (ZXing)
    implementation(libs.zxing.embedded)

    testImplementation(libs.junit)
}
