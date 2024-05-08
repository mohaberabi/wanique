@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.runique.android.library.compose)

}

android {
    namespace = "com.mohaberabi.core.presentation.designsystem_wear"

    defaultConfig {
        minSdk = 30


    }

}

dependencies {

    api(projects.core.presentation.designsystem)

    implementation(libs.androidx.wear.compose.material)
}