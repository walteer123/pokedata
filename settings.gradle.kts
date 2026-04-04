pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "pokedata"

includeBuild("build-logic")

include(":app")
include(":feature:pokemon-list")
include(":feature:pokemon-detail")
include(":feature:search")
include(":feature:favorites")
include(":core:data")
include(":core:designsystem")
include(":core:navigation")
include(":core:ui")
include(":core:testing")
