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

import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.time.Year
import java.time.format.DateTimeFormatter

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
val lbaVersion             = project.property("lba.version").toString()
val energyVersion          = project.property("energy.version").toString()
val galacticraftApiVersion = project.property("galacticraft.api.version").toString()
val reiVersion             = project.property("rei.version").toString()
val myronVersion           = project.property("myron.version").toString()
val bannerppVersion        = project.property("bannerpp.version").toString()
val wthitVersion           = project.property("wthit.version").toString()
val runtimeOptional        = project.property("optional_dependencies.enabled") == "true"

plugins {
    java
    `maven-publish`
    id("fabric-loom") version("0.8-SNAPSHOT")
    id("org.cadixdev.licenser") version("0.6.1")
}

configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_16
    targetCompatibility = JavaVersion.VERSION_16
}

group = modGroup
version = modVersion + getVersionDecoration()

base {
    archivesName.set(modName)
}

loom {
    refmapName = "galacticraft.refmap.json"
    accessWidener = rootProject.file("src/main/resources/galacticraft.accesswidener")
}

repositories {
    mavenLocal()
    maven("https://maven.shedaniel.me/") {
        content {
            includeGroup("me.shedaniel.cloth.api")
            includeGroup("me.shedaniel.cloth")
            includeGroup("me.shedaniel")
            includeGroup("dev.architectury")
        }
    }
    maven("https://server.bbkr.space/artifactory/libs-release/") {
        content {
            includeGroup("io.github.fablabsmc")
        }
    }
    maven("https://alexiil.uk/maven/") {
        content {
            includeGroup("alexiil.mc.lib")
        }
    }
    maven(/*"https://maven.terraformersmc.com/"*/"https://raw.githubusercontent.com/TerraformersMC/Archive/main/releases/") { //fixme: wait for TerraformersMC maven to stabilize
        content {
            includeGroup("com.terraformersmc")
        }
    }
    maven("https://hephaestus.dev/release/") {
        content {
            includeGroup("dev.monarkhes")
        }
    }
    maven("https://bai.jfrog.io/artifactory/maven/") {
        content {
            includeGroup("mcp.mobius.waila")
        }
    }
    maven("https://maven.galacticraft.dev") {
        content {
            includeGroup("dev.galacticraft")
        }
    }
}

/**
 * From:
 * @see net.fabricmc.loom.configuration.FabricApiExtension.getDependencyNotation
 */
fun getFabricApiModule(moduleName: String, fabricApiVersion: String): String {
    return String.format("net.fabricmc.fabric-api:%s:%s", moduleName,
        fabricApi.moduleVersion(moduleName, fabricApiVersion))
}

fun optionalImplementation(dependencyNotation: String, dependencyConfiguration: Action<ExternalModuleDependency>) {
    project.dependencies.modCompileOnly(dependencyNotation, dependencyConfiguration)
    if (!net.fabricmc.loom.util.OperatingSystem.isCIBuild() && runtimeOptional) project.dependencies.modRuntime(dependencyNotation, dependencyConfiguration)
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
        modImplementation(getFabricApiModule(it, fabricVersion)) { isTransitive = false }
    }

    // Mandatory Dependencies (Included with Jar-In-Jar)
    include(modImplementation("dev.monarkhes:myron:$myronVersion") {
        exclude(group = "net.fabricmc")
        exclude(group = "net.fabricmc.fabric-api")
    })
    include(modImplementation("me.shedaniel.cloth:cloth-config-fabric:$clothConfigVersion") {
        exclude(group = "net.fabricmc")
        exclude(group = "net.fabricmc.fabric-api")
    })
    include(modApi("dev.galacticraft:GalacticraftEnergy:$energyVersion") { isTransitive = false })
    include(modApi("dev.galacticraft:GalacticraftAPI:$galacticraftApiVersion") { isTransitive = false })
    include(modApi("alexiil.mc.lib:libblockattributes-core:$lbaVersion") { isTransitive = false })
    include(modApi("alexiil.mc.lib:libblockattributes-items:$lbaVersion") { isTransitive = false })
    include(modApi("alexiil.mc.lib:libblockattributes-fluids:$lbaVersion") { isTransitive = false })

    // Optional Dependencies
    optionalImplementation("com.terraformersmc:modmenu:$modMenuVersion") { isTransitive = false }
    optionalImplementation("mcp.mobius.waila:wthit:fabric-$wthitVersion") { isTransitive = false }
    optionalImplementation("io.github.fablabsmc:bannerpp:$bannerppVersion") { isTransitive = false }
    optionalImplementation("me.shedaniel:RoughlyEnoughItems-fabric:$reiVersion") {
        exclude(group = "me.shedaniel.cloth")
        exclude(group = "net.fabricmc")
        exclude(group = "net.fabricmc.fabric-api")
        exclude(group = "org.jetbrains")
    }

    // Other Dependencies
    modRuntime("net.fabricmc.fabric-api:fabric-api:$fabricVersion")
}

tasks.processResources {
    inputs.property("version", project.version)

    filesMatching("fabric.mod.json") {
        expand(mutableMapOf("version" to project.version))
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
            "Implementation-Version"   to project.version,
            "Implementation-Vendor"    to "Team Galacticraft",
            "Implementation-Timestamp" to DateTimeFormatter.ISO_DATE_TIME,
            "Maven-Artifact"           to "$modGroup:$modName:$project.version"
        ))
    }
}

publishing {
    publications {
        register("mavenJava", MavenPublication::class) {
            groupId = "dev.galacticraft"
            artifactId = "Galacticraft"

            artifact(tasks.remapJar) { builtBy(tasks.remapJar) }
            artifact(tasks.getByName("sourcesJar", Jar::class)) { builtBy(tasks.remapSourcesJar) }
            artifact(tasks.getByName("javadocJar", Jar::class))
        }
    }
    repositories {
        maven {
            setUrl("s3://maven.galacticraft.dev")
            authentication {
                register("awsIm", AwsImAuthentication::class)
            }
        }
    }
}

license {
    setHeader(project.file("LICENSE_HEADER.txt"))
    include("**/dev/galacticraft/**/*.java")
    include("build.gradle.kts")
    ext {
        set("year", Year.now().value)
        set("company", "Team Galacticraft")
    }
}

tasks.withType(JavaCompile::class) {
    dependsOn(tasks.checkLicenses)
    options.encoding = "UTF-8"
    options.release.set(16)
}

// inspired by https://github.com/TerraformersMC/GradleScripts/blob/2.0/ferry.gradle
fun getVersionDecoration(): String {
    if ((System.getenv("DISABLE_VERSION_DECORATION") ?: "false") == "true") return ""
    if (project.hasProperty("release")) return ""

    var version = "+build"
    if ("git".exitValue() != 1) {
        version += ".unknown"
    } else {
        val branch = "git branch --show-current".execute()
        if (branch.isNotEmpty() && branch != "main") {
            version += ".${branch}"
        }
        val commitHashLines = "git rev-parse --short HEAD".execute()
        if (commitHashLines.isNotEmpty()) {
            version += ".${commitHashLines}"
        }
        val dirty = "git diff-index --quiet HEAD".exitValue()
        if (dirty != 0) {
            version += "-modified"
        }
    }
    return version
}

// from https://discuss.gradle.org/t/how-to-run-execute-string-as-a-shell-command-in-kotlin-dsl/32235/9
fun String.execute(): String {
    val output = ByteArrayOutputStream()
    rootProject.exec {
        commandLine(split("\\s".toRegex()))
        workingDir = rootProject.projectDir
        isIgnoreExitValue = true
        standardOutput = output
        errorOutput = OutputStream.nullOutputStream()
    }

    return String(output.toByteArray()).trim()
}

fun String.exitValue(): Int {
    return rootProject.exec {
        commandLine(split("\\s".toRegex()))
        workingDir = rootProject.projectDir
        isIgnoreExitValue = true
        standardOutput = OutputStream.nullOutputStream()
        errorOutput = OutputStream.nullOutputStream()
    }.exitValue
}