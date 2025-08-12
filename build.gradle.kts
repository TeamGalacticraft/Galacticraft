/*
 * Copyright (c) 2019-2025 Team Galacticraft
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
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import java.net.HttpURLConnection
import java.net.URL

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
val appleskinVersion         = project.property("appleskin.version").toString()
val objVersion               = project.property("obj.version").toString()

plugins {
    java
    `maven-publish`
    id("fabric-loom") version("1.10-SNAPSHOT")
    id("com.diffplug.spotless") version("7.0.4")
    id("org.ajoberstar.grgit") version("5.3.2")
    id("dev.galacticraft.mojarn") version("0.6.1+19")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21

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
    mixin.add(sourceSets.test.get(), "galacticraft-test.refmap.json")

    runs {
        getByName("client") {
            name("Minecraft Client")
            source(sourceSets.test.get())
            property("fabric-tag-conventions-v2.missingTagTranslationWarning", "VERBOSE")
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
            property("fabric-api.gametest.report-file", "${project.layout.buildDirectory.get().file("junit.xml")}")
        }

        afterEvaluate {
            val mixinJarFile = configurations.runtimeClasspath.get().incoming.artifactView {
                componentFilter {
                    it is ModuleComponentIdentifier
                            && it.group == "net.fabricmc"
                            && it.module == "sponge-mixin"
                }
            }.files.first()

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
    maven("https://repo.terradevelopment.net/repository/maven-releases/") {
        // https://maven.galacticraft.net/repository/maven-releases
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
    maven("https://maven.ryanliptak.com/") {
        content {
            includeGroup("squeek.appleskin")
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

// --- GitHub contributors -> fabric.mod.json updater (Kotlin DSL) ---

// ---- Config
val ownerRepo = "TeamGalacticraft/Galacticraft"
val modJsonPath = "src/main/resources/fabric.mod.json"
val etagFile = file("$buildDir/contributors.etag")

val gsonPretty: Gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
val gson: Gson = GsonBuilder().disableHtmlEscaping().create()

data class ContributorApi(
    val login: String?,
    val html_url: String?,
    val type: String?,
    val contributions: Int?
)

fun httpGetContributors(ownerRepo: String, useETag: Boolean): Pair<List<ContributorApi>, String?> {
    val results = mutableListOf<ContributorApi>()
    val token = System.getenv("GITHUB_TOKEN")?.takeIf { it.isNotBlank() }
    var url: String? = "https://api.github.com/repos/$ownerRepo/contributors?per_page=100"
    var latestEtag: String? = null

    while (url != null) {
        val conn = (URL(url).openConnection() as HttpURLConnection).apply {
            setRequestProperty("Accept", "application/vnd.github+json")
            if (token != null) setRequestProperty("Authorization", "Bearer $token")
            if (useETag && etagFile.exists()) setRequestProperty("If-None-Match", etagFile.readText().trim())
            connectTimeout = 15_000
            readTimeout = 15_000
        }

        val code = conn.responseCode

        // If conditional and first page says not modified, short-circuit
        if (results.isEmpty() && useETag && code == HttpURLConnection.HTTP_NOT_MODIFIED) {
            conn.disconnect()
            return emptyList<ContributorApi>() to null
        }

        if (code >= 400) {
            val err = conn.errorStream?.readBytes()?.toString(Charsets.UTF_8)
            conn.disconnect()
            throw GradleException("GitHub API error $code while fetching $url\n$err")
        }

        conn.inputStream.use { input ->
            val listType = object : TypeToken<List<ContributorApi>>() {}.type
            val page: List<ContributorApi> = gson.fromJson(input.reader(), listType)
            results.addAll(page)
        }

        if (latestEtag == null) latestEtag = conn.getHeaderField("ETag")

        val linkHeader = conn.getHeaderField("Link")
        conn.disconnect()

        url = linkHeader
            ?.split(",")
            ?.mapNotNull { part ->
                val m = Regex("""<([^>]+)>;\s*rel="([^"]+)"""").find(part.trim())
                if (m != null && m.groupValues[2] == "next") m.groupValues[1] else null
            }
            ?.firstOrNull()
    }

    return results to latestEtag
}

fun buildContributorsPayload(raw: List<ContributorApi>): List<Map<String, Any?>> {
    val filtered = raw.filter { c ->
        val type = c.type?.lowercase() ?: ""
        val login = c.login?.lowercase() ?: ""
        !type.contains("bot") && !login.contains("bot")
    }.sortedByDescending { it.contributions ?: 0 }

    // Only name + contact per FMJ
    return filtered.map { c ->
        mapOf(
            "name" to (c.login ?: ""),
            "contact" to mapOf("homepage" to (c.html_url ?: ""))
        )
    }
}

fun readExistingContributors(root: JsonElement): List<Map<String, Any?>> {
    if (!root.isJsonObject) return emptyList()
    val obj = root.asJsonObject
    if (!obj.has("contributors") || !obj["contributors"].isJsonArray) return emptyList()
    return obj["contributors"].asJsonArray.mapNotNull { el ->
        if (!el.isJsonObject) return@mapNotNull null
        val o = el.asJsonObject
        mapOf(
            "name" to (o.get("name")?.asString ?: ""),
            "contact" to (o.getAsJsonObject("contact")?.let { cobj ->
                mapOf("homepage" to (cobj.get("homepage")?.asString ?: ""))
            } ?: emptyMap<String, Any?>())
        )
    }
}

// --- Conditional task: only updates if changed (uses ETag)
tasks.register("updateContributorsConditional") {
    group = "verification"
    description = "Update contributors only if API changed (ETag)."

    doLast {
        val modJsonFile = file(modJsonPath)
        if (!modJsonFile.exists()) throw GradleException("fabric.mod.json not found at $modJsonPath")

        val rootEl: JsonElement = modJsonFile.reader(Charsets.UTF_8).use { JsonParser.parseReader(it) }
        val existing = readExistingContributors(rootEl)

        val (raw, newEtag) = try {
            httpGetContributors(ownerRepo, useETag = true)
        } catch (e: Exception) {
            logger.warn("Failed to reach GitHub: ${e.message}. Keeping existing contributors.")
            return@doLast
        }

        if (raw.isEmpty() && newEtag == null) {
            println("Contributors unchanged (ETag 304). Skipping update.")
            return@doLast
        }

        val newContrib = buildContributorsPayload(raw)

        if (existing == newContrib) {
            println("Contributors already up-to-date. No changes written.")
            if (newEtag != null) etagFile.writeText(newEtag)
            return@doLast
        }

        val rootObj = rootEl.asJsonObject
        rootObj.add("contributors", gson.toJsonTree(newContrib))
        modJsonFile.writeText(gsonPretty.toJson(rootObj), Charsets.UTF_8)
        if (newEtag != null) etagFile.writeText(newEtag)

        println("Updated contributors (conditional) in fabric.mod.json (${newContrib.size})")
    }
}

// --- Force task: always fetch and overwrite (ignores ETag for request)
tasks.register("updateContributorsForce") {
    group = "verification"
    description = "Always fetch contributors and overwrite fabric.mod.json."

    doLast {
        val modJsonFile = file(modJsonPath)
        if (!modJsonFile.exists()) throw GradleException("fabric.mod.json not found at $modJsonPath")

        val rootEl: JsonElement = modJsonFile.reader(Charsets.UTF_8).use { JsonParser.parseReader(it) }

        val (raw, newEtag) = try {
            httpGetContributors(ownerRepo, useETag = false) // force fresh fetch
        } catch (e: Exception) {
            throw GradleException("Failed to fetch GitHub contributors: ${e.message}", e)
        }

        val newContrib = buildContributorsPayload(raw)

        val rootObj = rootEl.asJsonObject
        rootObj.add("contributors", gson.toJsonTree(newContrib))
        modJsonFile.writeText(gsonPretty.toJson(rootObj), Charsets.UTF_8)
        if (newEtag != null) etagFile.writeText(newEtag)

        println("Updated contributors (force) in fabric.mod.json (${newContrib.size})")
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
    "compat"("squeek.appleskin:appleskin-fabric:$appleskinVersion")

    modCompileOnly("me.shedaniel:RoughlyEnoughItems-api-fabric:$reiVersion")
    modCompileOnly("me.shedaniel:RoughlyEnoughItems-default-plugin-fabric:$reiVersion")
    modRuntimeOnly("me.shedaniel:RoughlyEnoughItems-fabric:$reiVersion")

    modCompileOnly("mezz.jei:jei-$minecraftVersion-fabric-api:$jeiVersion")

    testImplementation("net.fabricmc:fabric-loader-junit:$loaderVersion")
}

tasks.processResources {
    val properties = mapOf(
        "version" to project.version
    )
    inputs.properties(properties)

    filesMatching("fabric.mod.json") {
        expand(properties)
    }

    // Minify json resources (https://stackoverflow.com/a/41029113)
    doLast {
        val jsonSlurper = groovy.json.JsonSlurper().setType(groovy.json.JsonParserType.LAX)
        fileTree(mapOf("dir" to outputs.files.asPath, "includes" to listOf("**/*.json", "**/*.mcmeta"))).forEach {
            groovy.json.JsonOutput.toJson(jsonSlurper.parse(it))
        }
    }
}

tasks.javadoc {
    options.encoding = "UTF-8"
}

tasks.withType(JavaCompile::class) {
    options.encoding = "UTF-8"
    options.release.set(21)
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
    useJUnitPlatform()
    workingDir = project.file("run")
    dependsOn(tasks.getByName("runGametest"))
}

spotless {
    lineEndings = com.diffplug.spotless.LineEnding.UNIX
    ratchetFrom("origin/main")            // only check diffs in main

    java {
        target("src/**/*.java")
        targetExclude("src/main/generated/**", "**/build/**")
        licenseHeader(processLicenseHeader(rootProject.file("LICENSE")))
        leadingTabsToSpaces()
        removeUnusedImports()
        trimTrailingWhitespace()
    }
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

fun processLicenseHeader(license: File): String {
    val text = license.readText()
    return "/*\n * " + text.substring(text.indexOf("Copyright"))
        .replace("\n", "\n * ")
        .replace("* \n", "*\n")
        .trim() + "/\n\n"
}
