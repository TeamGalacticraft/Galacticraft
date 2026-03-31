pluginManagement {
    repositories {
        mavenLocal {
            content {
                includeGroup("dev.galacticraft")
                includeGroup("dev.galacticraft.mojarn")
            }
        }
        maven("https://maven.fabricmc.net/") {
            name = "Fabric"
            content {
                includeGroup("net.fabricmc")
                includeGroup("net.fabricmc.fabric-api")
                includeGroup("fabric-loom")
            }
        }
        maven("https://repo.terradevelopment.net/repository/maven-releases/") {
            content {
                includeGroup("dev.galacticraft")
                includeGroup("dev.galacticraft.mojarn")
            }
        }
        maven("https://maven.neoforged.net/releases") {
            name = "NeoForge"
            content {
                includeGroup("net.neoforged")
            }
        }
        gradlePluginPortal()
    }
}

rootProject.name = "Galacticraft"

include(":common")
include(":fabric")
include(":neoforge")