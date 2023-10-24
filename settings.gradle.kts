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

rootProject.name = "Starling Bank App"
include(":app")
include(":core")
include(":core:domain")
include(":core:data")
include(":core:designsystem")
include("feature")
include(":feature:home")
include(":core:common")
include(":feature:savingsgoals")
