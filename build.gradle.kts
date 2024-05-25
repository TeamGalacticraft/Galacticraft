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

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// Build Info
val isCi = (System.getenv("CI") ?: "false") == "true"

// Minecraft, Mappings, Loader Versions
val minecraftVersion         = project.property("minecraft.version").toString()
val loaderVersion            = project.property("loader.version").toString()
val yarnBuild                = project.property("yarn.build").toString()

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
val architecturyVersion      = project.property("architectury.version").toString()
val objVersion               = project.property("obj.version").toString()

plugins {
    java
    `maven-publish`
    id("fabric-loom") version("1.6-SNAPSHOT")
    id("org.cadixdev.licenser") version("0.6.1")
    id("org.ajoberstar.grgit") version("5.2.2")
    id("dev.galacticraft.mojarn") version("0.1.2")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17

    withSourcesJar()
    withJavadocJar()
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
    val env = System.getenv()
    if (env.containsKey("PRE_RELEASE") && env["PRE_RELEASE"] == "true") {
        append("-pre")
    }
    append('+')
    if (env.containsKey("GITHUB_RUN_NUMBER")) {
        append(env["GITHUB_RUN_NUMBER"])
    } else {
        val grgit = extensions.findByType<org.ajoberstar.grgit.Grgit>()
        if (grgit?.head() != null) {
            append(grgit.head().id.substring(0, 8))
            if (!grgit.status().isClean) {
                append("-dirty")
            }
        } else {
            append("unknown")
        }
    }
}
println("Galacticraft: $version")
base.archivesName.set(modName)

loom {
    accessWidenerPath.set(project.file("src/main/resources/galacticraft.accesswidener"))
    mixin.add(sourceSets.main.get(), "galacticraft.refmap.json")

    runs {
        getByName("client") {
            name("Minecraft Client")
            source(sourceSets.test.get())
        }

        getByName("server") {
            name("Minecraft Server")
            source(sourceSets.test.get())
        }

        register("datagen") {
            client()
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
            property("fabric-api.gametest.report-file", "${project.layout.buildDirectory.get()}/junit.xml")
        }

        afterEvaluate {
            val mixinJarFile = configurations.compileClasspath.get().files { it.group == "net.fabricmc" && it.name == "sponge-mixin" }.first()
            configureEach {
                vmArg("-javaagent:$mixinJarFile")

                property("mixin.hotSwap", "true")
                property("mixin.debug.export", "true")
            }
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
}

configurations {
    val core = create("core")
    val compat = create("compat")

    modImplementation {
        extendsFrom(core)
        extendsFrom(compat)
    }

    include {
        extendsFrom(core)
    }
}

dependencies {
    // Minecraft, Mappings, Loader
    minecraft("com.mojang:minecraft:$minecraftVersion")
    mappings(if (!isCi && yarnBuild.isNotEmpty()) {
        mojarn.mappings("net.fabricmc:yarn:$minecraftVersion+build.$yarnBuild:v2")
    } else {
        loom.officialMojangMappings()
    })
    modImplementation("net.fabricmc:fabric-loader:$loaderVersion")
    modImplementation("net.fabricmc.fabric-api:fabric-api:$fabricVersion")

    // Mandatory Dependencies (Included with Jar-In-Jar)
    include(implementation("de.javagl:obj:$objVersion")) {}

    "core"("dev.galacticraft:dynamicdimensions-fabric:$dynamicdimensionsVersion")
    "core"("dev.galacticraft:MachineLib:$machineLibVersion")
    "core"("lol.bai:badpackets:fabric-$badpacketsVersion")

    // Optional Dependencies
    "compat"("com.terraformersmc:modmenu:$modMenuVersion")
    "compat"("me.shedaniel.cloth:cloth-config-fabric:$clothConfigVersion")
    "compat"("mcp.mobius.waila:wthit:fabric-$wthitVersion")
    "compat"("dev.architectury:architectury-fabric:$architecturyVersion") // required for REI fluid support

    multicompat(group = "me.shedaniel",
            api = "RoughlyEnoughItems-api-fabric",
            extra = "RoughlyEnoughItems-default-plugin-fabric",
            runtime = "RoughlyEnoughItems-fabric",
            version = reiVersion) {
        exclude(group = "net.fabricmc.fabric-api")
    }

    multicompat(group = "mezz.jei",
            api = "jei-$minecraftVersion-common-api",
            extra = "jei-$minecraftVersion-fabric-api",
//            runtime = "jei-$minecraftVersion-fabric", // we already have REI at runtime
            version = jeiVersion) {
        exclude(group = "net.fabricmc.fabric-api")
    }

    testImplementation("net.fabricmc:fabric-loader-junit:$loaderVersion")
}

tasks.processResources {
    inputs.property("version", project.version)

    filesMatching("fabric.mod.json") {
        expand("version" to project.version)
    }

    // Minify json resources (https://stackoverflow.com/a/41029113)
    doLast {
        val jsonSlurper = groovy.json.JsonSlurper().setType(groovy.json.JsonParserType.LAX)
        fileTree(mapOf("dir" to outputs.files.asPath, "includes" to listOf("**/*.json", "**/*.mcmeta"))).forEach {
            groovy.json.JsonOutput.toJson(jsonSlurper.parse(it))
        }
    }
}

tasks.processTestResources {
    inputs.property("version", project.version)

    filesMatching("fabric.mod.json") {
        expand("version" to project.version)
    }
}

tasks.javadoc {
    options.encoding = "UTF-8"
}

tasks.withType(JavaCompile::class) {
    options.encoding = "UTF-8"
    options.release.set(17)
}

tasks.jar {
    from("LICENSE") {
        rename { "${it}_${modName}"}
    }

    manifest {
        attributes(
            "Specification-Title" to modName,
            "Specification-Vendor" to "Team Galacticraft",
            "Specification-Version" to modVersion,
            "Implementation-Title" to project.name,
            "Implementation-Version" to "${project.version}",
            "Implementation-Vendor" to "Team Galacticraft",
            "Implementation-Timestamp" to LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
            "Maven-Artifact" to "${project.group}:${modName}:${project.version}",
        )
    }
}

tasks.test {
    workingDir = project.file("run")
    dependsOn(tasks.getByName("runGametest"))
}

license {
    setHeader(project.file("LICENSE_HEADER.txt"))
    include("**/dev/galacticraft/**/*.java")
    include("build.gradle.kts")
}

publishing {
    publications {
        register("mavenJava", MavenPublication::class) {
            groupId = modGroup
            artifactId = modName
            version = project.version.toString()

            from(components["java"])

            pom {
                organization {
                    name.set("Team Galacticraft")
                    url.set("https://github.com/TeamGalacticraft")
                }

                scm {
                    url.set("https://github.com/TeamGalacticraft/Galacticraft")
                    connection.set("scm:git:git://github.com/TeamGalacticraft/Galacticraft.git")
                    developerConnection.set("scm:git:git@github.com:TeamGalacticraft/Galacticraft.git")
                }

                issueManagement {
                    system.set("github")
                    url.set("https://github.com/TeamGalacticraft/Galacticraft/issues")
                }

                licenses {
                    license {
                        name.set("MIT")
                        url.set("https://github.com/TeamGalacticraft/Galacticraft/blob/main/LICENSE")
                    }
                }
            }
        }
    }

    repositories {
        if (System.getenv().containsKey("NEXUS_REPOSITORY_URL")) {
            maven(System.getenv("NEXUS_REPOSITORY_URL")!!) {
                credentials {
                    username = System.getenv("NEXUS_USER")
                    password = System.getenv("NEXUS_PASSWORD")
                }
            }
        }
    }
}

fun DependencyHandler.multicompat(group: String, api: String, extra: String, runtime: String = "", version: String, action: Action<ExternalModuleDependency> = Action {}) {
    modCompileOnly(group = group, name = api, version = version, dependencyConfiguration = action)
    modCompileOnly(group = group, name = extra, version = version, dependencyConfiguration = action)
    if (runtime.isNotBlank()) modRuntimeOnly(group = group, name = runtime, version = version, dependencyConfiguration = action)
}
