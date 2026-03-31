import org.gradle.api.artifacts.component.ModuleComponentIdentifier

val isCi = (System.getenv("CI") ?: "false") == "true"
val runJei = project.getProperties().getOrDefault("jei", "false").toString().toBoolean()
val runEmi = project.getProperties().getOrDefault("emi", "false").toString().toBoolean()
val runRei = project.getProperties().getOrDefault("rei", (!runJei && !runEmi).toString()).toString().toBoolean()

val minecraftVersion = rootProject.property("minecraft.version").toString()
val loaderVersion = rootProject.property("loader.version").toString()
val yarnBuild = rootProject.property("yarn.build").toString()

val modName = rootProject.property("mod.name").toString()
val fabricVersion = rootProject.property("fabric.version").toString()
val clothConfigVersion = rootProject.property("cloth.config.version").toString()
val modMenuVersion = rootProject.property("modmenu.version").toString()
val dynamicdimensionsVersion = rootProject.property("dynamicdimensions.version").toString()
val machineLibVersion = rootProject.property("machinelib.version").toString()
val reiVersion = rootProject.property("rei.version").toString()
val jeiVersion = rootProject.property("jei.version").toString()
val emiVersion = rootProject.property("emi.version").toString()
val badpacketsVersion = rootProject.property("badpackets.version").toString()
val wthitVersion = rootProject.property("wthit.version").toString()
val architecturyVersion = rootProject.property("architectury.version").toString()
val appleskinVersion = rootProject.property("appleskin.version").toString()
val objVersion = rootProject.property("obj.version").toString()

plugins {
    id("fabric-loom")
    id("dev.galacticraft.mojarn")
}

base {
    archivesName.set("${modName}-fabric")
}

loom {
    accessWidenerPath.set(file("src/main/resources/galacticraft.accesswidener"))
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
            property("fabric-api.datagen.output-dir", file("src/main/generated").toString())
            property("fabric-api.datagen.strict-validation", "false")
        }

        register("gametest") {
            server()
            name("Game Test")
            source(sourceSets.test.get())
            property("fabric-api.gametest")
            property("fabric-api.gametest.report-file", "${layout.buildDirectory.get().file("junit.xml")}")
        }

        afterEvaluate {
            val mixinJarFile = configurations.runtimeClasspath.get().incoming.artifactView {
                componentFilter {
                    it is ModuleComponentIdentifier &&
                            it.group == "net.fabricmc" &&
                            it.module == "sponge-mixin"
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

sourceSets {
    main {
        resources {
            srcDir("src/main/generated")
            exclude(".cache/")
        }
    }
}

configurations {
    val core = create("core")
    val compat = create("compat")

    named("modImplementation") {
        extendsFrom(core)
        extendsFrom(compat)
    }

    named("include") {
        extendsFrom(core)
    }
}

dependencies {
    implementation(project(path = ":common", configuration = "namedElements"))

    minecraft("com.mojang:minecraft:$minecraftVersion")
    mappings(
        if (!isCi && yarnBuild.isNotEmpty()) {
            mojarn.mappings("net.fabricmc:yarn:$minecraftVersion+build.$yarnBuild:v2")
        } else {
            loom.officialMojangMappings()
        }
    )

    modImplementation("net.fabricmc:fabric-loader:$loaderVersion")
    modImplementation("net.fabricmc.fabric-api:fabric-api:$fabricVersion")

    include(implementation("de.javagl:obj:$objVersion")) {}

    "core"("dev.galacticraft:dynamicdimensions-fabric:$dynamicdimensionsVersion")
    "core"("dev.galacticraft:MachineLib:$machineLibVersion")
    "core"("lol.bai:badpackets:fabric-$badpacketsVersion")

    "compat"("com.terraformersmc:modmenu:$modMenuVersion")
    "compat"("me.shedaniel.cloth:cloth-config-fabric:$clothConfigVersion")
    "compat"("mcp.mobius.waila:wthit:fabric-$wthitVersion")
    "compat"("dev.architectury:architectury-fabric:$architecturyVersion")
    "compat"("squeek.appleskin:appleskin-fabric:$appleskinVersion")

    modCompileOnly("me.shedaniel:RoughlyEnoughItems-api-fabric:$reiVersion")
    modCompileOnly("me.shedaniel:RoughlyEnoughItems-default-plugin-fabric:$reiVersion")
    if (runRei) {
        modLocalRuntime("me.shedaniel:RoughlyEnoughItems-fabric:$reiVersion")
    }

    modCompileOnly("mezz.jei:jei-$minecraftVersion-fabric-api:$jeiVersion")
    if (runJei) {
        modLocalRuntime("mezz.jei:jei-$minecraftVersion-fabric:$jeiVersion")
    }

    modCompileOnly("dev.emi:emi-fabric:$emiVersion:api")
    if (runEmi) {
        modLocalRuntime("dev.emi:emi-fabric:$emiVersion")
    }

    testImplementation("net.fabricmc:fabric-loader-junit:$loaderVersion")
}

tasks.processResources {
    val properties = mapOf(
        "version" to project.version,
        "fabricVersion" to rootProject.property("fabric.version"),
        "minecraftVersion" to rootProject.property("minecraft.version")
    )

    inputs.properties(properties)

    filesMatching("fabric.mod.json") {
        expand(properties)
    }

    doLast {
        val jsonSlurper = groovy.json.JsonSlurper().setType(groovy.json.JsonParserType.LAX)
        fileTree(mapOf("dir" to outputs.files.asPath, "includes" to listOf("**/*.json", "**/*.mcmeta"))).forEach {
            groovy.json.JsonOutput.toJson(jsonSlurper.parse(it))
        }
    }
}

tasks.test {
    useJUnitPlatform()
    workingDir = file("run")
    dependsOn(tasks.getByName("runGametest"))
}