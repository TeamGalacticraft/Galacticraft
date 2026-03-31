import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.javadoc.Javadoc

plugins {
    base
    `maven-publish`
    id("com.diffplug.spotless") version("7.0.4") apply false
    id("org.ajoberstar.grgit") version("5.3.2")
    id("fabric-loom") version("1.10-SNAPSHOT") apply false
    id("dev.galacticraft.mojarn") version("0.6.1+19") apply false
}

val isCi = (System.getenv("CI") ?: "false") == "true"

val minecraftVersion = property("minecraft.version").toString()
val modVersion = property("mod.version").toString()
val modName = property("mod.name").toString()
val modGroup = property("mod.group").toString()

group = modGroup
version = buildString {
    append(modVersion)
    val env = System.getenv()
    if (env["PRE_RELEASE"] == "true") {
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

allprojects {
    group = rootProject.group
    version = rootProject.version

    repositories {
        mavenLocal()

        maven("https://repo.terradevelopment.net/repository/maven-releases/") {
            content {
                includeGroup("dev.galacticraft")
                includeGroup("dev.galacticraft.mojarn")
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
                includeGroup("dev.emi")
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

        maven("https://maven.fabricmc.net/") {
            name = "Fabric"
        }

        maven("https://maven.neoforged.net/releases") {
            name = "NeoForge"
        }

        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "maven-publish")
    apply(plugin = "com.diffplug.spotless")

    extensions.configure<JavaPluginExtension> {
        toolchain.languageVersion.set(JavaLanguageVersion.of(rootProject.property("java.version").toString().toInt()))
        withSourcesJar()
        withJavadocJar()
    }

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        options.release.set(rootProject.property("java.version").toString().toInt())
    }

    tasks.withType<Javadoc>().configureEach {
        options.encoding = "UTF-8"
    }

    tasks.withType<Jar>().configureEach {
        from(rootProject.file("LICENSE")) {
            rename { "${it}_${modName}" }
        }

        manifest {
            attributes(
                "Specification-Title" to modName,
                "Specification-Vendor" to "Team Galacticraft",
                "Specification-Version" to modVersion,
                "Implementation-Title" to project.name,
                "Implementation-Version" to project.version.toString(),
                "Implementation-Vendor" to "Team Galacticraft",
                "Implementation-Timestamp" to LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
                "Maven-Artifact" to "${project.group}:${project.name}:${project.version}"
            )
        }
    }

    extensions.configure<com.diffplug.gradle.spotless.SpotlessExtension>("spotless") {
        lineEndings = com.diffplug.spotless.LineEnding.UNIX
        java {
            licenseHeader(processLicenseHeader(rootProject.file("LICENSE")))
            leadingTabsToSpaces()
            removeUnusedImports()
            trimTrailingWhitespace()
        }
    }

    publishing {
        publications {
            register<MavenPublication>("mavenJava") {
                groupId = project.group.toString()
                artifactId = project.name
                version = project.version.toString()

                afterEvaluate {
                    from(components["java"])
                }

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
}

fun processLicenseHeader(license: File): String {
    val text = license.readText()
    return "/*\n * " + text.substring(text.indexOf("Copyright"))
        .replace("\n", "\n * ")
        .replace("* \n", "*\n")
        .trim() + "/\n\n"
}