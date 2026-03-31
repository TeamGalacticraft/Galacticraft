val isCi = (System.getenv("CI") ?: "false") == "true"

val minecraftVersion = rootProject.property("minecraft.version").toString()
val loaderVersion = rootProject.property("loader.version").toString()
val yarnBuild = rootProject.property("yarn.build").toString()
val modName = rootProject.property("mod.name").toString()

plugins {
    id("fabric-loom")
    id("dev.galacticraft.mojarn")
    java
}

base {
    archivesName.set("${modName}-common")
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")
    mappings(
        if (!isCi && yarnBuild.isNotEmpty()) {
            mojarn.mappings("net.fabricmc:yarn:$minecraftVersion+build.$yarnBuild:v2")
        } else {
            loom.officialMojangMappings()
        }
    )

    modImplementation("net.fabricmc:fabric-loader:$loaderVersion")
}