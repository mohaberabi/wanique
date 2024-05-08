import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.DynamicFeatureExtension
import com.mohaberabi.convention.addUiLayerDependency
import com.mohaberabi.convention.configureAndroidCompose
import com.mohaberabi.convention.configureBuildTypes
import com.mohaberabi.convention.configureKotlinAndroid
import com.mohaberabi.convention.libs

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.internal.impldep.org.glassfish.jaxb.runtime.v2.schemagen.xmlschema.ExtensionType
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin

class AndroidDynamicFeatureConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.run {
            pluginManager.run {
                apply("com.android.dynamic-feature")
                apply("org.jetbrains.kotlin.android")
            }
            extensions.configure<DynamicFeatureExtension> {
                configureKotlinAndroid(this)
                configureAndroidCompose(this)

                configureBuildTypes(
                    commonExtension = this,
                    extensionType = com.mohaberabi.convention.ExtensionType.DYNAMIC_FEATURE
                )
            }

            dependencies {
                addUiLayerDependency(target)
                "testImplementation"(kotlin("test"))
            }
        }
    }
}