plugins {
    alias(libs.plugins.runique.android.library.compose)
}

android {
    namespace = "com.mohaberabi.core.presentation.designsystem"

}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)
    // to use this lib in children of this module
    api(libs.androidx.compose.material3)
}