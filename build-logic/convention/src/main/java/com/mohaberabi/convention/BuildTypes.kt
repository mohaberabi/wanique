package com.mohaberabi.convention

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.BuildType
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.DynamicFeatureExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure


internal fun Project.configureBuildTypes(
    commonExtension: CommonExtension<*, *, *, *, *>,
    extensionType: ExtensionType,
) {


    commonExtension.run {
        val apiKey = gradleLocalProperties(rootDir).getProperty("API_KEY")

        buildFeatures {
            buildConfig = true
        }
        when (extensionType) {
            ExtensionType.APPLICATION -> {
                extensions.configure<ApplicationExtension> {

                    buildTypes {
                        debug {
                            configDebugBuildType(apiKey)

                        }
                        release {
                            configReleaseBuildType(apiKey, commonExtension)

                        }
                    }
                }
            }

            ExtensionType.LIBRARY -> {
                extensions.configure<LibraryExtension> {

                    buildTypes {
                        debug {
                            configDebugBuildType(apiKey)
                        }
                        release {

                            configReleaseBuildType(apiKey, commonExtension)
                            isMinifyEnabled = false
                            proguardFiles(
                                getDefaultProguardFile("proguard-android-optimize.txt"),
                                "proguard-rules.pro"
                            )
                        }
                    }
                }
            }

            ExtensionType.DYNAMIC_FEATURE -> {
                extensions.configure<DynamicFeatureExtension> {

                    buildTypes {
                        debug {
                            configDebugBuildType(apiKey)
                        }
                        release {

                            configReleaseBuildType(apiKey, commonExtension)
                            isMinifyEnabled = false
                            proguardFiles(
                                getDefaultProguardFile("proguard-android-optimize.txt"),
                                "proguard-rules.pro"
                            )
                        }
                    }
                }
            }
        }


    }
}

private fun BuildType.configDebugBuildType(api: String) {

    buildConfigField("String", "API_KEY", "\"$api\"")
//    buildConfigField(
//        "String", "BASE_URL", "\"https://runique.pl-coding.com\""
//    )
    buildConfigField("String", "BASE_URL", "\"https://runique.pl-coding.com:8080\"")

}

private fun BuildType.configReleaseBuildType(
    api: String,
    commonExtension: CommonExtension<*, *, *, *, *>,
) {

    buildConfigField("String", "API_KEY", "\"$api\"")
    buildConfigField("String", "BASE_URL", "\"https://runique.pl-coding.com:8080\"")

//    buildConfigField(
//        "String", "BASE_URL", "\"https://runique.pl-coding.com\""
//    )
    isMinifyEnabled = false
    proguardFiles(
        commonExtension.getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro"
    )
}