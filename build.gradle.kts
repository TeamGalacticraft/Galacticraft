/*
 * Copyright (c) 2019-2023 Team Galacticraft
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

// Minecraft, Mappings, Loader Versions
val minecraftVersion       = project.property("minecraft.version").toString()
val loaderVersion          = project.property("loader.version").toString()

// Mod Info
val modVersion             = project.property("mod.version").toString()
val modName                = project.property("mod.name").toString()
val modGroup               = project.property("mod.group").toString()

// Dependency Versions
val fabricVersion          = project.property("fabric.version").toString()
val clothConfigVersion     = project.property("cloth.config.version").toString()
val modMenuVersion         = project.property("modmenu.version").toString()
val galacticraftApiVersion = project.property("galacticraft.api.version").toString()
val machineLibVersion      = project.property("machinelib.version").toString()
//val architecturyVersion    = project.property("architectury.version").toString()
val reiVersion             = project.property("rei.version").toString()
val jeiVersion             = project.property("jei.version").toString()
val badpacketsVersion      = project.property("badpackets.version").toString()
val wthitVersion           = project.property("wthit.version").toString()
val portingLibVersion      = project.property("porting.lib.version").toString()
val runtimeOptional        = project.property("optional_dependencies.enabled").toString().toBoolean() && !net.fabricmc.loom.util.OperatingSystem.isCIBuild()

plugins {
    java
    `maven-publish`
    id("fabric-loom") version("1.1-SNAPSHOT")
    id("io.github.juuxel.loom-quiltflower") version("1.8.0")
    id("org.cadixdev.licenser") version("0.6.1")
    id("org.ajoberstar.grgit") version("5.0.0")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    withSourcesJar()
}

sourceSets {
    main {
        resources {
            srcDir("src/main/generated")
            exclude(".cache/**")
        }
    }
}

group = modGroup
version = modVersion + getVersionDecoration()
println("Galacticraft: $version")
base.archivesName.set(modName)

loom {
    accessWidenerPath.set(project.file("src/main/resources/galacticraft.accesswidener"))
    mixin.add(sourceSets.main.get(), "galacticraft.refmap.json")

    runs {
        runConfigs.forEach {
            it.property("mixin.hotSwap", "true")
        }
        register("datagen") {
            server()
            name("Data Generation")
            runDir("build/datagen")
            vmArgs("-Dfabric-api.datagen", "-Dfabric-api.datagen.modid=galacticraft"/*, "-Dgalacticraft.mixin.compress_datagen"*/, "-Dfabric-api.datagen.output-dir=${file("src/main/generated")}", "-Dfabric-api.datagen.strict-validation=false")
        }
        register("datagenClient") {
            client()
            name("Data Generation Client")
            runDir("build/datagen")
            vmArgs("-Dfabric-api.datagen", "-Dfabric-api.datagen.modid=galacticraft"/*, "-Dgalacticraft.mixin.compress_datagen"*/, "-Dfabric-api.datagen.output-dir=${file("src/main/generated")}", "-Dfabric-api.datagen.strict-validation")
        }
        register("gametest") {
            server()
            name("Game Test")
            source(sourceSets.test.get())
            property("mixin.hotSwap", "true")
            vmArg("-Dfabric-api.gametest")
        }
        register("gametestClient") {
            client()
            name("Game Test Client")
            source(sourceSets.test.get())
            property("mixin.hotSwap", "true")
            vmArg("-Dfabric-api.gametest")
        }
    }
}

repositories {
    maven("https://maven.galacticraft.net/repository/maven-releases/") {
        content {
            includeGroup("dev.galacticraft")
        }
    }
    maven("https://maven.galacticraft.net/repository/maven-snapshots/") {
        name = "Galacticraft Repository"
        content {
            includeVersionByRegex("dev.galacticraft", ".*", ".*-SNAPSHOT")
        }
    }
    maven("https://mvn.devos.one/snapshots/") {
        content {
            includeGroup("io.github.fabricators_of_create.Porting-Lib")
        }
    }
    maven("https://maven.shedaniel.me/") {
        content {
            includeGroup("me.shedaniel.cloth.api")
            includeGroup("me.shedaniel.cloth")
            includeGroup("me.shedaniel")
            includeGroup("dev.architectury")
        }
    }
    maven("https://maven.modmuss50.me/") {
        content {
            includeGroup("teamreborn")
        }
    }
    maven("https://hephaestus.dev/release/") {
        content {
            includeGroup("dev.monarkhes")
        }
    }
    maven("https://maven.terraformersmc.com/releases/") {
        content {
            includeGroup("com.terraformersmc")
        }
    }
    maven("https://server.bbkr.space/artifactory/libs-release/") {
        content {
            includeGroup("io.github.fablabsmc")
        }
    }
    maven("https://maven.bai.lol") {
        content {
            includeGroup("mcp.mobius.waila")
            includeGroup("lol.bai")
        }
    }
    maven("https://maven.blamejared.com/") {
        content {
            includeGroup("mezz.jei")
        }
    }
}

dependencies {
    // Minecraft, Mappings, Loader
    minecraft("com.mojang:minecraft:$minecraftVersion")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:$loaderVersion")

    modImplementation("net.fabricmc.fabric-api:fabric-api:$fabricVersion")

    // Mandatory Dependencies (Included with Jar-In-Jar)
    listOf(
        "obj_loader",
        "model_loader",
        "extensions",
        "accessors",
        "constants"
    ).forEach {
        includedRuntimeDependency("io.github.fabricators_of_create.Porting-Lib:$it:${portingLibVersion}") { isTransitive = false }
    }
    includedDependency("me.shedaniel.cloth:cloth-config-fabric:$clothConfigVersion") {
        exclude(group = "net.fabricmc")
        exclude(group = "net.fabricmc.fabric-api")
    }
    includedDependency("dev.galacticraft:GalacticraftAPI:$galacticraftApiVersion") {
        exclude(group = "net.fabricmc")
        exclude(group = "net.fabricmc.fabric-api")
        exclude(group = "dev.galacticraft", module = "MachineLib")
    }
    includedDependency("dev.galacticraft:MachineLib:$machineLibVersion") {
        exclude(group = "net.fabricmc")
        exclude(group = "net.fabricmc.fabric-api")
    }
    includedDependency("lol.bai:badpackets:fabric-$badpacketsVersion") { isTransitive = false }

    // Optional Dependencies
    optionalRuntime("com.terraformersmc:modmenu:$modMenuVersion") { isTransitive = false }
    optionalRuntime("mcp.mobius.waila:wthit:fabric-$wthitVersion") { isTransitive = false }
//    optionalDependency("dev.architectury:architectury-fabric:${architecturyVersion}") { isTransitive = false }
    optionalRuntime("me.shedaniel:RoughlyEnoughItems-fabric:$reiVersion") {
        exclude(group = "me.shedaniel.cloth")
        exclude(group = "net.fabricmc")
        exclude(group = "net.fabricmc.fabric-api")
    }
    modCompileOnly("mezz.jei:jei-1.19.4-common-api:${jeiVersion}")
    modCompileOnly("mezz.jei:jei-1.19.4-fabric-api:${jeiVersion}")
    // at runtime, use the full JEI jar for Fabric
    optionalRuntimeOnly("mezz.jei:jei-1.19.4-fabric:${jeiVersion}")

    // Runtime Dependencies
    modRuntimeOnly("net.fabricmc.fabric-api:fabric-api:$fabricVersion")
}

tasks.processResources {
    inputs.property("version", project.version)

    filesMatching("fabric.mod.json") {
        expand("version" to project.version)
    }

    // Minify json resources
    // https://stackoverflow.com/questions/41028030/gradle-minimize-json-resources-in-processresources#41029113
    doLast {
        fileTree(mapOf("dir" to outputs.files.asPath, "includes" to listOf("**/*.json", "**/*.mcmeta"))).forEach {
                file: File -> file.writeText(groovy.json.JsonOutput.toJson(groovy.json.JsonSlurper().parse(file)))
        }
    }
}

tasks.create<Jar>("javadocJar") {
    from(tasks.javadoc)
    archiveClassifier.set("javadoc")
    tasks.build.get().dependsOn(this)
}

tasks.jar {
    from("LICENSE")
    manifest {
        attributes(
            "Implementation-Title"     to modName,
            "Implementation-Version"   to project.version,
            "Implementation-Vendor"    to "Team Galacticraft",
            "Implementation-Timestamp" to DateTimeFormatter.ISO_DATE_TIME,
            "Maven-Artifact"           to "$modGroup:$modName:${project.version}"
        )
    }
}

publishing {
    publications {
        register("mavenJava", MavenPublication::class) {
            groupId = modGroup
            artifactId = modName
            version = project.version.toString()

            from(components["java"])
        }
    }
    repositories {
        val isSnapshot: Boolean = System.getenv("SNAPSHOT")?.equals("true") ?: true
        val mavenRelease = "https://maven.galacticraft.dev/repository/maven-releases/"
        val mavenSnapshot = "https://maven.galacticraft.dev/repository/maven-snapshots/"
        maven(if(isSnapshot) mavenSnapshot else mavenRelease) {
            name = "maven"
            credentials(PasswordCredentials::class)
            authentication {
                register("basic", BasicAuthentication::class)
            }
        }
    }
}

license {
    setHeader(project.file("LICENSE_HEADER.txt"))
    include("**/dev/galacticraft/**/*.java")
    include("build.gradle.kts")
}

quiltflower {
    addToRuntimeClasspath.set(true)
}

tasks.withType(JavaCompile::class) {
    options.encoding = "UTF-8"
    options.release.set(17)
}

afterEvaluate {
    loom {
        runs {
            configureEach {
                val mixinJarFile = configurations.compileClasspath.get().files { it.group == "net.fabricmc" && it.name == "sponge-mixin" }.first()
                vmArg("-javaagent:$mixinJarFile")
            }
        }
    }
}

fun DependencyHandler.optionalRuntime(dependencyNotation: String, dependencyConfiguration: Action<ExternalModuleDependency>) {
    if (runtimeOptional) {
        modImplementation(dependencyNotation, dependencyConfiguration)
    } else {
        modCompileOnly(dependencyNotation, dependencyConfiguration)
        modRuntimeOnly(dependencyNotation, dependencyConfiguration)
    }
}

fun DependencyHandler.optionalRuntimeOnly(dependencyNotation: String) {
    if (!net.fabricmc.loom.util.OperatingSystem.isCIBuild() && runtimeOptional) {
        modRuntimeOnly(dependencyNotation)
    }
}

fun DependencyHandler.includedDependency(dependencyNotation: String, dependencyConfiguration: Action<ExternalModuleDependency>) {
    include(modApi(dependencyNotation, dependencyConfiguration), dependencyConfiguration)
}

fun DependencyHandler.includedRuntimeDependency(dependencyNotation: String, dependencyConfiguration: Action<ExternalModuleDependency>) {
    include(modRuntimeOnly(dependencyNotation, dependencyConfiguration), dependencyConfiguration)
}

// inspired by https://github.com/TerraformersMC/GradleScripts/blob/2.0/ferry.gradle
fun getVersionDecoration(): String {
    if ((System.getenv("DISABLE_VERSION_DECORATION") ?: "false") == "true") return ""
    if (project.hasProperty("release")) return ""

    var decoration = "+build"
    if (grgit.head() == null) {
        decoration += ".unknown"
    } else {
        val branch = grgit.branch.current()
        if (branch != null && branch.name != "main") {
            decoration += ".${branch.name}"
        }
        val commitHashLines = grgit.head().abbreviatedId
        if (commitHashLines != null && commitHashLines.isNotEmpty()) {
            decoration += "-${commitHashLines}"
        }

        if (!grgit.status().isClean) {
            decoration += "-modified"
        }
    }
    return decoration
}

