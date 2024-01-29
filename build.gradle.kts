/*
 * Copyright (c) 2019-2024 Team Galacticraft
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

// Build Info
val buildNumber = System.getenv("BUILD_NUMBER") ?: ""
val commitHash = System.getenv("GITHUB_SHA") ?: project.getCommitHash()
val prerelease = (System.getenv("PRE_RELEASE") ?: "false") == "true"

// Minecraft, Mappings, Loader Versions
val minecraftVersion         = project.property("minecraft.version").toString()
val loaderVersion            = project.property("loader.version").toString()
val parchmentVersion         = project.property("parchment.version").toString()

// Mod Info
val modVersion               = project.property("mod.version").toString()
val modName                  = project.property("mod.name").toString()
val modGroup                 = project.property("mod.group").toString()

// Dependency Versions
val fabricVersion            = project.property("fabric.version").toString()
val clothConfigVersion       = project.property("cloth.config.version").toString()
val modMenuVersion           = project.property("modmenu.version").toString()
val dynamicdimensionsVersion = project.property("dynamicdimensions.version").toString()
val machineLibVersion        = project.property("machinelib.version").toString()
val reiVersion               = project.property("rei.version").toString()
val jeiVersion               = project.property("jei.version").toString()
val badpacketsVersion        = project.property("badpackets.version").toString()
val wthitVersion             = project.property("wthit.version").toString()
val portingLibVersion        = project.property("porting.lib.version").toString()
val runtimeOptional          = project.property("optional_dependencies.enabled").toString().toBoolean() && System.getenv("CI") == null

plugins {
    java
    `maven-publish`
    id("fabric-loom") version("1.5-SNAPSHOT")
    id("org.cadixdev.licenser") version("0.6.1")
    id("org.ajoberstar.grgit") version("5.2.1")
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
            exclude(".cache/")
        }
    }
}

group = modGroup
version = buildString {
    append(modVersion)
    if (prerelease) {
        append("-pre")
    }
    append('+')
    if (buildNumber.isNotBlank()) {
        append(buildNumber)
    } else if (commitHash.isNotEmpty()) {
        append(commitHash.substring(0, 8))
        if (project.hasProperty("grgit")) {
            if ((project.property("grgit") as org.ajoberstar.grgit.Grgit?)?.status()?.isClean != true) {
                append("-dirty")
            }
        }
    } else {
        append("unknown")
    }
}
println("Galacticraft: $version")
base.archivesName.set(modName)

loom {
    accessWidenerPath.set(project.file("src/main/resources/galacticraft.accesswidener"))
    mixin.add(sourceSets.main.get(), "galacticraft.refmap.json")

    runs {
        runConfigs.forEach {
            it.property("mixin.hotSwap", "true")
            it.property("mixin.debug.export", "true")
        }
        register("datagen") {
            server()
            name("Data Generation")
            runDir("build/datagen")
            property("fabric-api.datagen")
            property("fabric-api.datagen.modid", "galacticraft")
            property("fabric-api.datagen.output-dir", project.file("src/main/generated").toString())
            property("fabric-api.datagen.strict-validation", "false")
        }
        register("gametest") {
            server()
            name("Game Test")
            source(sourceSets.test.get())
            property("fabric-api.gametest")
        }
        register("gametestClient") {
            client()
            name("Game Test Client")
            source(sourceSets.test.get())
            property("fabric-api.gametest")
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
            includeGroup("dev.galacticraft")
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
    maven("https://maven.terraformersmc.com/releases/") {
        content {
            includeGroup("com.terraformersmc")
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
    maven("https://maven.parchmentmc.org") {
        content {
            includeGroup("org.parchmentmc.data")
        }
    }
}

dependencies {
    // Minecraft, Mappings, Loader
    minecraft("com.mojang:minecraft:$minecraftVersion")
    mappings(if (parchmentVersion.isNotEmpty()) {
        loom.layered {
            officialMojangMappings()
            parchment("org.parchmentmc.data:parchment-$minecraftVersion:$parchmentVersion@zip")
        }
    } else {
        loom.officialMojangMappings()
    })

    modImplementation("net.fabricmc:fabric-loader:$loaderVersion")

    modImplementation("net.fabricmc.fabric-api:fabric-api:$fabricVersion")

    // Mandatory Dependencies (Included with Jar-In-Jar)
    listOf(
        "obj_loader",
        "model_loader",
        "core"
    ).forEach {
        includedRuntimeDependency("io.github.fabricators_of_create.Porting-Lib:$it:${portingLibVersion}") { isTransitive = false }
    }
    includedDependency("me.shedaniel.cloth:cloth-config-fabric:$clothConfigVersion") {
        exclude(group = "net.fabricmc")
        exclude(group = "net.fabricmc.fabric-api")
    }
    includedDependency("dev.galacticraft:dynamicdimensions-fabric:$dynamicdimensionsVersion") {
        exclude(group = "net.fabricmc")
        exclude(group = "net.fabricmc.fabric-api")
    }
    includedDependency("dev.galacticraft:MachineLib:$machineLibVersion") {
        exclude(group = "net.fabricmc")
        exclude(group = "net.fabricmc.fabric-api")
    }
    includedDependency("lol.bai:badpackets:fabric-$badpacketsVersion") { isTransitive = false }

    // Optional Dependencies
    optionalRuntime("com.terraformersmc:modmenu:$modMenuVersion") { isTransitive = false }
    optionalRuntime("mcp.mobius.waila:wthit:fabric-$wthitVersion") { isTransitive = false }
    optionalRuntime("me.shedaniel:RoughlyEnoughItems-fabric:$reiVersion") {
        exclude(group = "me.shedaniel.cloth")
        exclude(group = "net.fabricmc")
        exclude(group = "net.fabricmc.fabric-api")
    }
    modCompileOnly("mezz.jei:jei-$minecraftVersion-common-api:${jeiVersion}")
    modCompileOnly("mezz.jei:jei-$minecraftVersion-fabric-api:${jeiVersion}")
    // at runtime, use the full JEI jar for Fabric
    optionalRuntimeOnly("mezz.jei:jei-$minecraftVersion-fabric:${jeiVersion}")

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
        // get rid of comments used by density function editor
        fileTree(mapOf("dir" to outputs.files.asPath, "includes" to listOf("**/*.json", "**/*.mcmeta"))).forEach { file ->
            var content = file.readText().replace("\\/\\/\\[df-editor\\].*\$".toRegex(), "")

            try {
                content = groovy.json.JsonOutput.toJson(groovy.json.JsonSlurper().parseText(content))
                file.writeText(content)
            } catch (e: Exception) {
                // Handle the case where the content is not valid JSON after removal
                logger.warn("Error processing file ${file}: ${e.message}")
            }
        }
    }

    // overwrite generated file
    duplicatesStrategy = DuplicatesStrategy.WARN
    doLast {
        copy {
            from("src/main/resources/data")
            into("src/main/generated/data")
        }
    }
    }

tasks.javadoc {
    options.encoding = "UTF-8"
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
        val mavenRelease = "https://maven.galacticraft.dev/repository/maven-releases/"
        val mavenSnapshot = "https://maven.galacticraft.dev/repository/maven-snapshots/"
        maven(if(prerelease) mavenSnapshot else mavenRelease) {
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
    if (runtimeOptional) {
        modRuntimeOnly(dependencyNotation)
    }
}

fun DependencyHandler.includedDependency(dependencyNotation: String, dependencyConfiguration: Action<ExternalModuleDependency>) {
    include(modApi(dependencyNotation, dependencyConfiguration), dependencyConfiguration)
}

fun DependencyHandler.includedRuntimeDependency(dependencyNotation: String, dependencyConfiguration: Action<ExternalModuleDependency>) {
    include(modRuntimeOnly(dependencyNotation, dependencyConfiguration), dependencyConfiguration)
}

fun Project.getCommitHash(): String {
    if (hasProperty("grgit")) {
        return (property("grgit") as org.ajoberstar.grgit.Grgit?)?.head()?.id ?: ""
    }
    return ""
}
