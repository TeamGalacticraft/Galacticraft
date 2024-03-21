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
        maven("https://maven.galacticraft.net/repository/maven-releases/") {
            content {
                includeGroup("dev.galacticraft")
                includeGroup("dev.galacticraft.mojarn")
            }
        }
        gradlePluginPortal()
    }
}

rootProject.name = "Galacticraft"