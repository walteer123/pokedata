plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.android.gradle.plugin)
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.kotlin.compose.gradle.plugin)
    implementation(libs.ktlint.gradle)
}

gradlePlugin {
    plugins {
        register("featureModule") {
            id = "pokedata.feature"
            implementationClass = "FeatureModulePlugin"
        }
        register("coreModule") {
            id = "pokedata.core"
            implementationClass = "CoreModulePlugin"
        }
    }
}
