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

import java.io.ByteArrayOutputStream
import java.io.OutputStream
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
val energyVersion          = project.property("energy.version").toString()
val galacticraftApiVersion = project.property("galacticraft.api.version").toString()
val machineLibVersion      = project.property("machinelib.version").toString()
val architecturyVersion    = project.property("architectury.version").toString()
val reiVersion             = project.property("rei.version").toString()
val jeiVersion             = project.property("jei.version").toString()
val myronVersion           = project.property("myron.version").toString()
val badpacketsVersion      = project.property("badpackets.version").toString()
val wthitVersion           = project.property("wthit.version").toString()
val portingLibVersion      = project.property("porting.lib.version").toString()
val runtimeOptional        = project.property("optional_dependencies.enabled").toString().toBoolean()

plugins {
    java
    `maven-publish`
    id("fabric-loom") version("1.0-SNAPSHOT")
    id("org.cadixdev.licenser") version("0.6.1")
    id("io.github.juuxel.loom-quiltflower") version("1.7.3")
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
        register("datagen") {
            server()
            name("Data Generation")
            runDir("build/datagen")
            vmArgs("-Dfabric-api.datagen", "-Dfabric-api.datagen.output-dir=${file("src/main/generated")}", "-Dfabric-api.datagen.strict-validation=false")
        }
        register("datagenClient") {
            client()
            name("Data Generation Client")
            runDir("build/datagen")
            vmArgs("-Dfabric-api.datagen", "-Dfabric-api.datagen.output-dir=${file("src/main/generated")}", "-Dfabric-api.datagen.strict-validation")
        }
        register("gametest") {
            server()
            name("Game Test")
            source(sourceSets.test.get())
            vmArg("-Dfabric-api.gametest")
        }
        register("gametestClient") {
            client()
            name("Game Test Client")
            source(sourceSets.test.get())
            vmArg("-Dfabric-api.gametest")
        }
    }
}

repositories {
    mavenLocal()
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
    maven("https://mvn.devos.one/snapshots/")
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
    maven("https://dvs1.progwml6.com/files/maven/") {
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
//    includedDependency("dev.monarkhes:myron:$myronVersion") {
//        exclude(group = "net.fabricmc")
//        exclude(group = "net.fabricmc.fabric-api")
//    }
    listOf(
        "model_loader",
        "extensions",
        "obj_loader",
        "accessors",
        "constants",
        "common"
    ).forEach {
        includedDependency("io.github.fabricators_of_create.Porting-Lib:$it:${portingLibVersion}+${minecraftVersion}") { isTransitive = false }
    }
    includedDependency("me.shedaniel.cloth:cloth-config-fabric:$clothConfigVersion") {
        exclude(group = "net.fabricmc")
        exclude(group = "net.fabricmc.fabric-api")
    }
    includedDependency("teamreborn:energy:$energyVersion") {
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
    // Optional Dependencies
    optionalDependency("com.terraformersmc:modmenu:$modMenuVersion") { isTransitive = false }
    optionalDependency("lol.bai:badpackets:fabric-$badpacketsVersion") { isTransitive = false }
    optionalDependency("mcp.mobius.waila:wthit:fabric-$wthitVersion") { isTransitive = false }
    optionalDependency("dev.architectury:architectury-fabric:${architecturyVersion}") { isTransitive = false }
    optionalDependency("me.shedaniel:RoughlyEnoughItems-fabric:$reiVersion") {
        exclude(group = "me.shedaniel.cloth")
        exclude(group = "net.fabricmc")
        exclude(group = "net.fabricmc.fabric-api")
    }
    modCompileOnlyApi("mezz.jei:jei-${minecraftVersion}-common-api:${jeiVersion}")
    modCompileOnlyApi("mezz.jei:jei-${minecraftVersion}-fabric-api:${jeiVersion}")
    // at runtime, use the full JEI jar for Fabric
    modRuntimeOnly("mezz.jei:jei-${minecraftVersion}-fabric:${jeiVersion}")

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
        val mavenBase = "https://maven.galacticraft.dev/"
        maven(if(isSnapshot) "$mavenBase/snapshots" else mavenBase) {
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
    ext {
        set("year", "2023")
        set("company", "Team Galacticraft")
    }
}

quiltflower {
    addToRuntimeClasspath.set(true)
}

tasks.withType(JavaCompile::class) {
    dependsOn(tasks.checkLicenses)
    options.encoding = "UTF-8"
    options.release.set(17)
}

/**
 * From:
 * @see net.fabricmc.loom.configuration.FabricApiExtension.getDependencyNotation
 */
fun getFabricApiModule(moduleName: String): String {
    return "net.fabricmc.fabric-api:${moduleName}:${fabricApi.moduleVersion(moduleName, fabricVersion)}"
}

fun DependencyHandler.optionalDependency(dependencyNotation: String, dependencyConfiguration: Action<ExternalModuleDependency>) {
    modCompileOnly(dependencyNotation, dependencyConfiguration)
    if (!net.fabricmc.loom.util.OperatingSystem.isCIBuild() && runtimeOptional) {
        modRuntimeOnly(dependencyNotation, dependencyConfiguration)
    }
}

fun DependencyHandler.includedDependency(dependencyNotation: String, dependencyConfiguration: Action<ExternalModuleDependency>) {
    include(modApi(dependencyNotation, dependencyConfiguration), dependencyConfiguration)
}

// inspired by https://github.com/TerraformersMC/GradleScripts/blob/2.0/ferry.gradle
fun getVersionDecoration(): String {
    if ((System.getenv("DISABLE_VERSION_DECORATION") ?: "false") == "true") return ""
    if (project.hasProperty("release")) return ""

    var version = "+build"
    if (!"git".testForProcess()) {
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

fun String.testForProcess(): Boolean {
    return try {
        rootProject.exec {
            commandLine(split("\\s".toRegex()))
            workingDir = rootProject.projectDir
            isIgnoreExitValue = true
            standardOutput = OutputStream.nullOutputStream()
            errorOutput = OutputStream.nullOutputStream()
        }
        true;
    } catch (ex: Exception) {
        false;
    }
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
