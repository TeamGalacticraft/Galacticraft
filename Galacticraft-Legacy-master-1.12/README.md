<p align="center"><img src="https://cloud.githubusercontent.com/assets/6842258/25335525/4f21552e-28eb-11e7-91bb-de5e1ef602da.jpg"></p>

<p align="center">
  <a href="https://github.com/TeamGalacticraft/Galacticraft-Legacy/graphs/contributors/"><img src="https://img.shields.io/github/contributors/TeamGalacticraft/Galacticraft-Legacy.svg" alt="GitHub contributors"/></a>
  <a href="https://github.com/TeamGalacticraft/Galacticraft-Legacy/commit/"><img src="https://img.shields.io/github/commits-since/TeamGalacticraft/Galacticraft-Legacy/first-commit" alt="GitHub commits"/></a>
  <img src="https://img.shields.io/github/languages/code-size/TeamGalacticraft/Galacticraft-Legacy.svg" alt="Code size"/>
  <img src="https://img.shields.io/github/repo-size/TeamGalacticraft/Galacticraft-Legacy.svg" alt="GitHub repo size"/>
  <img src="https://tokei.rs/b1/github/cabaletta/baritone?category=code" alt="Lines of Code"/>
</p>
<p align="center">
  <a href="https://www.curseforge.com/minecraft/mc-mods/galacticraft-legacy"><img alt="curseforge" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/available/curseforge_vector.svg">
  <a href="https://modrinth.com/mod/galacticraft-legacy"><img alt="modrinth" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/available/modrinth_vector.svg">
</p>
<p align="center"> 
  <a href="https://discord.galacticraft.net/"><img alt="discord-plural" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/social/discord-plural_vector.svg"></a>
  <a href="https://patreon.com/teamgalacticraft"><img alt="patreon-plural" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/donate/patreon-plural_vector.svg"></a>
</p>

Galacticraft-Legacy
============

The 1.12.2 version of Galacticraft that receives updates, forked from the original [Repository](https://github.com/micdoodle8/Galacticraft). If you're wondering why this repository shows to be behind in commits. The original only receives updates to its Capes list, this repository does not contain a capes list.

------

## Mod Developers

To import the mod into your IDE environment, It's best to use artifacts published to our [Maven Repository](https://maven.galacticraft.net/).
```gradle
repositories {
    maven {
        url "https://maven.galacticraft.net/repository/legacy/"
    }
}

dependencies {
    compileOnly fg.deobf("dev.galacticraft:galacticraft-legacy:${version}")
}
```
> *Note: a 'deobf' version is still distributed, But it is only advisable to use this classifier is your already using mappings: `stable_39-1.12`. To avoid any issues it is best to use the main artifact*

## Addon Developers

While you can still import Galacticraft into your IDE using the above method. A special-use Gradle Plugin has been developed that handles all the setup for you. 

Get the plugin on [Gradle Plugin Repository](https://plugins.gradle.org/plugin/net.galacticraft.addon) or check out the [galacticraft-addon-template](https://github.com/TeamGalacticraft/galacticraft-addon-template) repository

## Developing Galacticraft-Legacy

### Prerequisites
* Java 8 JDK
* An IDE with a compatible [Project Lombok plugin](https://projectlombok.org/download)

>Should you encounter any weird errors concerning missing getter or setter methods, it's probably because your code has not been processed by Project Lombok's processor.

### Clone

`git clone https://github.com/TeamGalacticraft/Galacticraft-Legacy.git`

### Import

After all Prerequisites and cloning is finished, simply import the project into your IDE.
