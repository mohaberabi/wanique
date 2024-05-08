@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.runique.android.library)

}

android {
    namespace = "com.mohaberabi.core.notification"

}

dependencies {

    implementation(libs.bundles.koin)
    implementation(libs.androidx.core.ktx)
    implementation(projects.core.domain)
    implementation(projects.core.presentation.ui)

    implementation(projects.core.presentation.designsystem)

}