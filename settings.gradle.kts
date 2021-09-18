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
        maven("https://server.bbkr.space/artifactory/libs-release/") {
            name = "Cotton"
            content {
                includeGroup("io.github.juuxel.loom-quiltflower")
                includeGroup("io.github.juuxel")
            }
        }
        gradlePluginPortal()
    }
}

rootProject.name = "Galacticraft"