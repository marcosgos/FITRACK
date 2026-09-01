import java.util.Properties
//Archivo en el que se detallan todas las dependecia, configuraciones y librerias
//que la aplicacion va a necesitar para los diferentes archivos al ejecutarse.

plugins {
    alias(libs.plugins.android.application)
}

// Carga las propiedades definidas en el archivo local.properties.
// Este archivo se utiliza para almacenar información que no debe incluirse
// directamente en el código fuente, como el Client ID de Google.
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")

if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

android {

    // Identificador del paquete de la aplicación.
    namespace = "com.marcos.fittrack"

    // Versión del SDK de Android utilizada para compilar el proyecto.
    compileSdk {
        version = release(37)
    }

    defaultConfig {

        // Identificador único de la aplicación.
        applicationId = "com.marcos.fittrack"

        // Versión mínima de Android compatible con la aplicación.
        minSdk = 24

        // Versión de Android utilizada como objetivo de la aplicación.
        targetSdk = 36

        // Código y nombre de versión de la aplicación.
        versionCode = 1
        versionName = "1.0"

        // Clase utilizada para realizar las pruebas instrumentadas.
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        /*
         * Obtiene el Client ID de Google desde local.properties y lo añade
         * a BuildConfig para poder utilizarlo desde el código Kotlin.
         */
        buildConfigField(
            "String",
            "GOOGLE_WEB_CLIENT_ID",
            "\"${localProperties.getProperty("GOOGLE_WEB_CLIENT_ID", "")}\""
        )
    }

    buildTypes {
        release {
            // Desactiva las optimizaciones para la versión release.
            optimization {
                enable = false
            }
        }
    }

    // Configura la compatibilidad del proyecto con Java 11.
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    /*
     * Activa la generación de la clase BuildConfig.
     * Es necesario activarlo explícitamente en versiones recientes
     * de Android Gradle Plugin.
     */
    buildFeatures {
        buildConfig = true
    }
}

dependencies {

    // Dependencias principales de AndroidX.
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)

    // Dependencias para realizar pruebas.
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)

    // Retrofit: comunicación con APIs REST.
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")

    // OkHttp: registro de las peticiones HTTP.
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Componentes Lifecycle para ViewModel y LiveData.
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.7")

    // Permite utilizar lifecycleScope para ejecutar corrutinas desde las Activities.
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")

    /*
     * Credential Manager permite implementar el inicio de sesión moderno
     * con Google y sustituye al antiguo GoogleSignInClient.
     *
     * googleid proporciona las herramientas necesarias para trabajar
     * con el ID Token de Google.
     */
    implementation("androidx.credentials:credentials:1.5.0")
    implementation("androidx.credentials:credentials-play-services-auth:1.5.0")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")

    // RecyclerView: Usado para la listar el repo de ejercicios.
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // Glide: Usado para cargar y mostrar las imagenes del repo de ejercicios.
    implementation("com.github.bumptech.glide:glide:4.16.0")
}