/*
 * Copyright (c) 2019-2021 HRZN LTD
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import java.time.format.DateTimeFormatter
import java.time.Year

// Minecraft, Mappings, Loader Versions
val minecraftVersion       = project.property("minecraft.version").toString()
val yarnBuild              = project.property("yarn.build").toString()
val loaderVersion          = project.property("loader.version").toString()

// Mod Info
val modVersion             = project.property("mod.version").toString()
val modName                = project.property("mod.name").toString()
val modGroup               = project.property("mod.group").toString()

// Dependency Version
val fabricVersion          = project.property("fabric.version").toString()
val clothConfigVersion     = project.property("cloth.config.version").toString()
val modMenuVersion         = project.property("modmenu.version").toString()
val ucVersion              = project.property("uc.version").toString()
val cardinalVersion        = project.property("cardinal.version").toString()
val galacticraftApiVersion = project.property("galacticraft.api.version").toString()
val reiVersion             = project.property("rei.version").toString()
val cottonResourcesVersion = project.property("cotton.resources.version").toString()
val fomlVersion            = project.property("foml.version").toString()
val bannerppVersion        = project.property("bannerpp.version").toString()

plugins {
    id("fabric-loom") version("0.5-SNAPSHOT")
    id("maven-publish")
    id("org.cadixdev.licenser") version("0.5.0")
    id("java")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

base {
    archivesBaseName = modName
    group = modGroup
    version = modVersion
}

loom {
    refmapName = "galacticraft-rewoven.refmap.json"
    accessWidener = File("src/main/resources/galacticraft_rewoven.accesswidener")
}

repositories {
    mavenLocal()
    maven {
        setUrl("https://maven.shedaniel.me/")
        content {
            includeGroup("me.shedaniel.cloth")
            includeGroup("me.shedaniel")
        }
    }
    maven {
        setUrl("https://server.bbkr.space/artifactory/libs-release/")
        content {
            includeGroup("io.github.cottonmc")
            includeGroup("io.github.fablabsmc")
        }
    }
    maven {
        setUrl("https://maven.terraformersmc.com/")
        content {
            includeGroup("com.terraformersmc")
        }
    }
    maven {
        setUrl("https://dl.bintray.com/ladysnake/libs/")
        content {
            includeGroup("io.github.onyxstudios.Cardinal-Components-API")
        }
    }
    maven {
        setUrl("https://maven.abusedmaster.xyz/")
        content {
            includeGroup("dev.onyxstudios")
        }
    }
    maven {
        setUrl("https://cdn.hrzn.studio/maven/")
        content {
            includeGroup("com.hrznstudio")
        }
    }
}

/**
 * From:
 * @see net.fabricmc.loom.util.FabricApiExtension.getDependencyNotation
 */
fun getFabricApiModule(moduleName: String, fabricApiVersion: String): String {
    return String.format("net.fabricmc.fabric-api:%s:%s", moduleName,
        fabricApi.moduleVersion(moduleName, fabricApiVersion))
}

dependencies {
    // Minecraft, Mappings, Loader
    minecraft("com.mojang:minecraft:$minecraftVersion")
    mappings("net.fabricmc:yarn:$minecraftVersion+build.$yarnBuild:v2")
    modImplementation("net.fabricmc:fabric-loader:$loaderVersion")

    // Fabric Api Modules
    listOf(
        "fabric-api-base",
        "fabric-blockrenderlayer-v1",
        "fabric-command-api-v1",
        "fabric-content-registries-v0",
        "fabric-item-groups-v0",
        "fabric-models-v0",
        "fabric-networking-blockentity-v0",
        "fabric-networking-api-v1",
        "fabric-object-builder-api-v1",
        "fabric-particles-v1",
        "fabric-registry-sync-v0",
        "fabric-renderer-api-v1",
        "fabric-renderer-indigo",
        "fabric-renderer-registries-v1",
        "fabric-rendering-fluids-v1",
        "fabric-resource-loader-v0",
        "fabric-screen-handler-api-v1",
        "fabric-structure-api-v1",
        "fabric-tag-extensions-v0",
        "fabric-textures-v0",
        "fabric-tool-attribute-api-v1"
    ).forEach {
        modImplementation(getFabricApiModule(it, fabricVersion)) { isTransitive = false}
    }

    // Mandatory Dependencies (Included with Jar-In-Jar)
    include(modImplementation("dev.onyxstudios:FOML:$fomlVersion") {
        exclude(group = "net.fabricmc")
        exclude(group = "net.fabricmc.fabric-api")
    })
    include(modImplementation("me.shedaniel.cloth:config-2:$clothConfigVersion") {
        exclude(group = "net.fabricmc")
        exclude(group = "net.fabricmc.fabric-api")
    })
    include(modApi("com.hrznstudio:GalacticraftAPI:$galacticraftApiVersion") { isTransitive = false })
    include(modImplementation("io.github.fablabsmc:bannerpp:$bannerppVersion") { isTransitive = false })
    include(modApi("io.github.cottonmc:cotton-resources:$cottonResourcesVersion") { isTransitive = false })
    include(modApi("io.github.cottonmc:UniversalComponents:$ucVersion") { isTransitive = false })
    include(modApi("io.github.onyxstudios.Cardinal-Components-API:cardinal-components-base:$cardinalVersion") { isTransitive = false })
    include(modApi("io.github.onyxstudios.Cardinal-Components-API:cardinal-components-block:$cardinalVersion") { isTransitive = false })
    include(modApi("io.github.onyxstudios.Cardinal-Components-API:cardinal-components-item:$cardinalVersion") { isTransitive = false })
    include(modApi("io.github.onyxstudios.Cardinal-Components-API:cardinal-components-entity:$cardinalVersion") { isTransitive = false })

    // Optional Dependencies
    modImplementation("com.terraformersmc:modmenu:$modMenuVersion") { isTransitive = false }
    modImplementation("me.shedaniel:RoughlyEnoughItems:$reiVersion") {
        exclude(group = "me.shedaniel.cloth")
        exclude(group = "net.fabricmc")
        exclude(group = "net.fabricmc.fabric-api")
        exclude(group = "org.jetbrains")
    }

    // Other Dependencies
    modRuntime("net.fabricmc.fabric-api:fabric-api:$fabricVersion")
    modImplementationMapped("com.google.code.findbugs:jsr305:3.0.1") { isTransitive = false }
}

tasks.processResources {
    inputs.property("version", project.version)

    filesMatching("fabric.mod.json") {
        expand(mapOf("version" to project.version))
    }

    // Minify json resources
    // https://stackoverflow.com/questions/41028030/gradle-minimize-json-resources-in-processresources#41029113
    doLast {
        fileTree(mapOf("dir" to outputs.files.asPath, "includes" to listOf("**/*.json", "**/*.mcmeta"))).forEach {
                file: File -> file.writeText(groovy.json.JsonOutput.toJson(groovy.json.JsonSlurper().parse(file)))
        }
    }
}

java {
    withSourcesJar()
}

tasks.create<Jar>("javadocJar") {
    from(tasks.javadoc)
    archiveClassifier.set("javadoc")
}

tasks.jar {
    from("LICENSE")
    manifest {
        attributes(mapOf(
            "Implementation-Title"     to modName,
            "Implementation-Version"   to modVersion,
            "Implementation-Vendor"    to "HRZN LTD",
            "Implementation-Timestamp" to DateTimeFormatter.ISO_DATE_TIME,
            "Maven-Artifact"           to "$modGroup:$modName:$modVersion"
        ))
    }
}

publishing {
    publications {
        register("mavenJava", MavenPublication::class) {
            groupId = "com.hrznstudio"
            artifactId = "galacticraft"

            artifact(tasks.jar) { builtBy(tasks.remapJar) }
            artifact(tasks.getByName("sourcesJar", Jar::class)) { builtBy(tasks.remapSourcesJar) }
            artifact(tasks.getByName("javadocJar", Jar::class))
        }
    }
    repositories {
        maven {
            setUrl("s3://cdn.hrzn.studio/maven")
            authentication {
                register("awsIm", AwsImAuthentication::class)
            }
        }
    }
}

license {
    header = project.file("LICENSE_HEADER.txt")
    include("**/com/hrznstudio/**/*.java")
    include("build.gradle.kts")
    ext(mapOf(
        "year" to Year.now().value,
        "company" to "HRZN LTD"
    ))
}