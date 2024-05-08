import com.android.build.api.dsl.ApplicationExtension
import com.mohaberabi.convention.configureBuildTypes
import com.mohaberabi.convention.configureKotlinAndroid
import com.mohaberabi.convention.libs

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.internal.impldep.org.glassfish.jaxb.runtime.v2.schemagen.xmlschema.ExtensionType
import org.gradle.kotlin.dsl.configure

class AndroidApplicationConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.run {
            pluginManager.run {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")
            }
            extensions.configure<ApplicationExtension> {
                defaultConfig {
                    applicationId = libs.findVersion("projectApplicationId").get().toString()
                    targetSdk = libs.findVersion("projectTargetSdkVersion").get().toString().toInt()

                    versionCode = libs.findVersion("projectVersionCode").get().toString().toInt()
                    versionName = libs.findVersion("projectVersionName").get().toString()
                }

                configureKotlinAndroid(this)

                configureBuildTypes(this, com.mohaberabi.convention.ExtensionType.APPLICATION)
            }

        }
    }
}