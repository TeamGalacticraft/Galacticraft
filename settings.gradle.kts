pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/") {
            name = "Fabric"
            content {
                includeGroup("net.fabricmc")
                includeGroup("net.fabricmc.fabric-api")
                includeGroup("fabric-loom")
            }
        }
        gradlePluginPortal()
    }
}

rootProject.name = "Galacticraft"