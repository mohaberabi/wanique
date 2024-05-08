@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.runique.android.feature.ui)
}

android {
    namespace = "com.mohaberabi.anaylitcs.presentation"

}

dependencies {

    implementation(projects.analytics.domain)


}