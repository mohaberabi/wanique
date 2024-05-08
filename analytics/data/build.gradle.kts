@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.runique.android.library)

}

android {
    namespace = "com.mohaberabi.anayltics.data"

}

dependencies {

    implementation(libs.kotlinx.coroutines.core)
    implementation(projects.core.database)
    implementation(projects.core.domain)
    implementation(projects.analytics.domain)
    implementation(libs.bundles.koin)


}