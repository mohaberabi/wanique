import com.android.build.api.dsl.LibraryExtension
import com.mohaberabi.convention.addUiLayerDependency
import com.mohaberabi.convention.configureAndroidCompose
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class AndroidFeatureUiConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {

        target.run {
            pluginManager.run {
                apply("runique.android.library.compose")
            }


            dependencies {

                addUiLayerDependency(target)

            }

        }
    }
}