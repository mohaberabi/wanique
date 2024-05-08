import com.android.build.api.dsl.LibraryExtension
import com.mohaberabi.convention.ExtensionType
import com.mohaberabi.convention.configureBuildTypes
import com.mohaberabi.convention.configureKotlinAndroid
import com.mohaberabi.convention.configureKotlinJvm
import com.mohaberabi.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin

class JvmKtorConventionLibrary : Plugin<Project> {

    override fun apply(target: Project) {


        target.run {
            pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")


            dependencies {

                "implementation"(libs.findBundle("ktor").get())
            }
        }
    }
}