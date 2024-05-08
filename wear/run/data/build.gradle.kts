@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.runique.android.library)
}

android {
    namespace = "com.mohaberabi.wear.run.data"

    defaultConfig {
        minSdk = 24

    }

}

dependencies {
    implementation(libs.bundles.koin)

    implementation(libs.androidx.health.services.client)

    implementation(projects.wear.run.domain)

    implementation(projects.core.domain)
    implementation(projects.core.connectivity.domain)


}