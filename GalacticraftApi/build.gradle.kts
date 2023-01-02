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

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

plugins {
    java
    `maven-publish`
}

group = "dev.galacticraft"
version = "0.4.0-prealpha.27"

val modId           = project.property("mod.id").toString()
val modVersion      = project.property("mod.version").toString()
val modName         = project.property("mod.name").toString()
val dyndims         = project.property("dyndims.version").toString()

java {
    withJavadocJar()
}

loom {
    accessWidenerPath.set(project.file("src/main/resources/${modId}.accesswidener"))

    mixin {
        add(sourceSets.main.get(), "${modId}.refmap.json")
    }

    mods {
        create("galacticraft-api") {
            sourceSet(sourceSets.main.get())
        }
        create("gc-api-test") {
            sourceSet(sourceSets.test.get())
        }
    }

    runs {
        register("datagen") {
            server()
            name("Data Generation: API")
            runDir("build/datagen")
            vmArgs("-Dfabric-api.datagen", "-Dfabric-api.datagen.output-dir=${file("src/main/generated")}", "-Dfabric-api.datagen.strict-validation", "-Dmixin.debug.export=true")
            ideConfigGenerated(true)
        }
        register("gametest") {
            server()
            name("Game Test: API")
            source(sourceSets.test.get())
            vmArgs("-Dfabric-api.gametest", "-Dfabric-api.gametest.report-file=${project.buildDir}/junit.xml", "-ea", "-Dmixin.debug.export=true")
            ideConfigGenerated(true)
        }
        register("gametestClient") {
            server()
            name("Game Test Client: API")
            source(sourceSets.test.get())
            vmArgs("-Dfabric-api.gametest", "-Dfabric-api.gametest.report-file=${project.buildDir}/junit.xml", "-ea", "-Dmixin.debug.export=true")
            ideConfigGenerated(true)
        }
    }
}

dependencies {
    modImplementation("dev.galacticraft:dyndims-fabric:$dyndims")
}

tasks.jar {
    from("LICENSE")
    manifest {
        attributes(
            "Specification-Title" to modName,
            "Specification-Vendor" to "Team Galacticraft",
            "Specification-Version" to modVersion,
            "Implementation-Title" to project.name,
            "Implementation-Version" to "${project.version}",
            "Implementation-Vendor" to "Team Galacticraft",
            "Implementation-Timestamp" to LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
            "Built-On-Java" to "${System.getProperty("java.vm.version")} (${System.getProperty("java.vm.vendor")})"
        )
    }
}

publishing {
    publications {
        register("mavenJava", MavenPublication::class) {
            groupId = "dev.galacticraft"
            artifactId = modName

            from(components["java"])
        }
    }
    repositories {
        if (System.getenv().containsKey("NEXUS_REPOSITORY_URL")) {
            maven(System.getenv("NEXUS_REPOSITORY_URL")) {
                credentials {
                    username = System.getenv("NEXUS_USER")
                    password = System.getenv("NEXUS_PASSWORD")
                }
            }
        } else {
            println("No nexus repository url found, publishing to local maven repo")
            mavenLocal()
        }
    }
}