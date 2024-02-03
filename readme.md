<div align="center">
  <br/>
  <img src="./.github/ignite.png" width="250" height="250" alt="Ignite Logo">
  <br/><br/>
  <p><strong><a href="https://github.com/vectrix-space/ignite">Ignite</a></strong> is a <a href="https://github.com/SpongePowered/Mixin">Mixin</a> loader for Spigot/Paper.</p>
  <br/>
</div>

<div align="center">

![Build Status](https://github.com/vectrix-space/ignite/actions/workflows/build.yml/badge.svg)
[![MIT License](https://img.shields.io/badge/license-MIT-blue)](license.txt)
[![Discord](https://img.shields.io/discord/819522977586348052)](https://discord.gg/rYpaxPFQrj)
[![Maven Central](https://img.shields.io/maven-central/v/space.vectrix.ignite/ignite-api?label=stable)](https://search.maven.org/search?q=g:space.vectrix.ignite%20AND%20a:ignite*)
![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/space.vectrix.ignite/ignite-api?label=dev&server=https%3A%2F%2Fs01.oss.sonatype.org)

</div>

## Install

Download the `ignite.jar` from the [releases page](https://github.com/vectrix-space/ignite/releases/latest).

Place the `ignite.jar` into the same directory with your Minecraft Server jar (i.e `paper.jar`, `spigot.jar` or `server.jar`).

Run your original start command, but replace the normal server jar with `ignite.jar`. If you are using _Java 8_ you will need to 
add `-javaagent:./ignite.jar` to your start command.

If Ignite cannot start your server, you may need to add additional startup flags. See the Advanced Section below for more information.
If you're still confused, be sure to [ask for help](https://discord.gg/rYpaxPFQrj).

The mods can then be placed into the mods directory that will be created.

## Making a Mod

The [ignite-mod-template](https://github.com/vectrix-space/ignite-mod-template) is a template you can use to start a project for Paper without needing to do all the setup yourself.

To depend on the Ignite API in order to create your mod, you will need to add the following to your buildscript:

<br/>

#### Maven
```xml
<repositories>
  <repository>
    <url>https://repo.spongepowered.org/maven/</url>
  </repository>
</repositories>

<dependencies>
  <dependency>
    <groupId>space.vectrix.ignite</groupId>
    <artifactId>ignite-api</artifactId>
    <version>1.0.0-SNAPSHOT</version>
  </dependency>
  <dependency>
    <groupId>org.spongepowered</groupId>
    <artifactId>mixin</artifactId>
    <version>0.8.5</version>
  </dependency>
  <dependency>
    <groupId>io.github.llamalad7</groupId>
    <artifactId>mixinextras-common</artifactId>
    <version>0.3.5</version>
  </dependency>
</dependencies>
```

<br/>

#### Gradle
```groovy
repositories {
  mavenCentral()
  maven {
    url = "https://repo.spongepowered.org/maven/"
  }
}

dependencies {
  compileOnly "space.vectrix.ignite:ignite-api:1.0.0-SNAPSHOT"
  compileOnly "org.spongepowered:mixin:0.8.5"
  compileOnly "io.github.llamalad7:mixinextras-common:0.3.5"
}
```

You will also need to depend on the server binary in order to compile your mod for your specified target(s).

**Note:** To support custom mappings you should check out [ignite-mod-template](https://github.com/vectrix-space/ignite-mod-template) 
if you're running Paper. For Spigot check out [Pacifist Remapper](https://github.com/PacifistMC/pacifist-remapper).

### Configuring your Mod

Your mod will require a `ignite.mod.json` in order to be located as a mod. The `ignite.mod.json` provides the metadata needed to load 
your mixins and access wideners.

Example `ignite.mod.json`:
```json
{
  "id": "example",
  "version": "1.0.0",
  "mixins": [
    "mixins.example.core.json"
  ],
  "wideners": [
    "example.accesswidener"
  ]
}
```

The mods will need to be placed in the directory the launcher will be targeting to load.

#### Using Mixins

The Mixin configuration files will need to be available in your mods binary in order to be loaded. The name of each configuration file 
should be added to the `mixins` section in your `ignite.mod.json`, or alternatively could be added to your jar manifest.

[Mixin Specification]

#### Using Access Wideners

The Access Wideners configuration files will need to be available in your mods binary in order to be loaded. The name of each 
configuration file should be added to the `wideners` section in your `ignite.mod.json`, or alternatively could be added to your 
jar manifest with the `AccessWidener` key.

**Warning:** Access wideners should only be used in situations where Mixin will not work!

[Access Widener Specification]

## Advanced Usage

Ignite has some properties that can be set on startup to change the launch target, mod directory and more. The following could be added 
to your startup script:

- The game locator service to use. (e.g `-Dignite.locator=dummy`)
- The path to the server jar. (e.g `-Dignite.jar=./server.jar`)
- The classpath to the server entry point. (e.g `-Dignite.target=org.bukkit.craftbukkit.Main`)
- The directory ignite libraries will be located. (e.g `-Dignite.libraries=./libraries`)
- The directory ignite mods will be located. (e.g `-Dignite.mods=./mods`)

### Game Locators

Game locators provide platform specific modifications to the launch process. In some cases where the platform cannot be automatically 
detected, these flags can set the information it needs to launch.

The following targets could use the following flags:

- Paper (1.18+):
  - Service name: `paper` (e.g `-Dignite.locator=paper`)
  - Extra properties:
    - The path to the paperclip jar. (e.g `-Dignite.paper.jar=./paper.jar`)
    - The classpath to the paperclip entry point. (e.g `-Dignite.paper.target=io.papermc.paperclip.Paperclip`)
    - The minecraft server version paperclip will be patching. (e.g `-Dignite.paper.version=1.20.4`)

- Spigot (1.18+):
  - Service name: `spigot` (e.g `-Dignite.locator=spigot`)
  - Extra properties:
    - Tge path to the spigot bundler directory. (e.g `-Dignite.spigot.bundler=./bundler`)
    - The path to the spigot bootstrap jar. (e.g `-Dignite.spigot.jar=./spigot.jar`)
    - The classpath to the spigot bootstrap entry point. (e.g `-Dignite.spigot.target=org.bukkit.craftbukkit.bootstrap.Main`)
    - The spigot version it will be using. (e.g `-Dignite.spigot.version=1.20.4-R0.1-SNAPSHOT`)

- Paper Legacy:
  - Service name: `legacy_paper` (e.g `-Dignite.locator=legacy_paper`)
  - Extra properties:
    - The path to the paperclip jar. (e.g `-Dignite.paper.jar=./paper.jar`)
    - The classpath to the paperclip entry point. (e.g `-Dignite.paper.target=io.papermc.paperclip.Paperclip`)
    - The minecraft server version paperclip will be patching. (e.g `-Dignite.paper.version=1.12.2`)

## Building
__Note:__ If you do not have [Gradle] installed then use `./gradlew` for Unix systems or Git Bash and gradlew.bat for Windows systems in 
place of any 'gradle' command.

In order to build Ignite you simply need to run the `gradle build` command. You can find the compiled JAR file in `./build/libs/` named 
'ignite.jar'.

## Inspiration

This project has many parts inspired by the following projects:

- [Orion]
- [Sponge]
- [Velocity]
- [plugin-spi]

[Mixin]: https://github.com/SpongePowered/Mixin
[Access Widener]: https://github.com/FabricMC/access-widener
[Mixin Specification]: https://github.com/SpongePowered/Mixin/wiki/Introduction-to-Mixins---The-Mixin-Environment#mixin-configuration-files
[Access Widener Specification]: https://fabricmc.net/wiki/tutorial:accesswideners

[Gradle]: https://www.gradle.org/
[Orion]: https://github.com/OrionMinecraft/Orion
[Sponge]: https://github.com/SpongePowered/Sponge
[Velocity]: https://github.com/VelocityPowered/Velocity
[plugin-spi]: https://github.com/SpongePowered/plugin-spi
