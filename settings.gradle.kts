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
        maven("http://5.161.199.182/repository/maven-releases/") {
            isAllowInsecureProtocol = true
            // https://maven.galacticraft.net/repository/maven-releases
            content {
                includeGroup("dev.galacticraft")
                includeGroup("dev.galacticraft.mojarn")
            }
        }
        gradlePluginPortal()
    }
}

rootProject.name = "Galacticraft"