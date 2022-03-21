Ignite [![Discord](https://img.shields.io/discord/819522977586348052?style=for-the-badge)](https://discord.gg/rYpaxPFQrj)
======
![GitHub Workflow Status (branch)](https://img.shields.io/github/workflow/status/vectrix-space/ignite/build/main)
[![MIT License](https://img.shields.io/badge/license-MIT-blue)](license.txt)
[![Maven Central](https://img.shields.io/maven-central/v/space.vectrix.ignite/ignite-api?label=stable)](https://search.maven.org/search?q=g:space.vectrix.ignite%20AND%20a:ignite*)
![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/space.vectrix.ignite/ignite-api?label=dev&server=https%3A%2F%2Fs01.oss.sonatype.org)

A [Mixin] and [Access Widener] mod loader for Spigot/Paper.

## Running a Server

Download the `ignite-launcher.jar` from [here](https://github.com/vectrix-space/ignite/releases/latest).

Place the `ignite-launcher.jar` into the same directory with your Minecraft Server jar (i.e `paper.jar`, `spigot.jar` or `server.jar`).

Start your server with the following command depending on the server software you are running:
- Paper (1.18+): `java -Dignite.service=paper -jar ignite-launcher.jar` (with Minecraft Server jar named `paper.jar`)
- Spigot (1.18+): `java -Dignite.service=spigot -jar ignite-launcher.jar` (with Minecraft Server jar named `spigot.jar`)
- Legacy Paper: `java -javaagent:./ignite-launcher.jar -Dignite.service=legacy_paper -jar ignite-launcher.jar` (with Minecraft Server jar named `paper.jar`)
- Other: `java -javaagent:./ignite-launcher.jar -jar ignite-launcher.jar` (with Minecraft Server jar named `server.jar`)

The mods can then be placed into the `mods` directory that will be created, along with any mod configuration in `configs`.

**Note:** Various properties can be applied to the launch command to support a custom server fork, or environment. Check the advanced usage below.

## Creating a Mod

To depend on the Ignite API in order to create your mod, you will need to add the following to your buildscript:

* Maven
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
    <version>0.7.4</version>
  </dependency>
  <dependency>
    <groupId>org.spongepowered</groupId>
    <artifactId>mixin</artifactId>
    <version>0.8.5</version>
  </dependency>
</dependencies>
```

* Gradle
```groovy
repositories {
  mavenCentral()
  maven {
    url = "https://repo.spongepowered.org/maven/"
  }
}

dependencies {
  compile "space.vectrix.ignite:ignite-api:0.7.4"
  compile "org.spongepowered:mixin:0.8.5"
}
```

You will also need to depend on the server binary in order to compile your mod for your specified target(s).

**Note:** To support custom mappings you should check out the [Pacifist Remapper](https://github.com/PacifistMC/pacifist-remapper) gradle plugin.

### Configuring your Mod

Your mod will require a `ignite.mod.json` in order to be located as a mod. The `ignite.mod.json` provides the metadata needed to load your mixins and access wideners.

Example `ignite.mod.json`:
```json
{
  "id": "example",
  "version": "1.0.0",
  "entry": "space.vectrix.example.ExampleMod",
  "dependencies": [
    "ignite"
  ],
  "mixins": [
    "mixins.example.core.json"
  ]
}
```

The mods will need to be placed in the directory the launcher will be targeting to load.

#### Using Mixins

The Mixin configuration files will need to be available in your mods binary in order to be loaded. The name of each configuration file should be added to the `mixins` section in
your `ignite.mod.json`, or alternatively could be added to your jar manifest.

[Mixin Specification]

#### Using Access Wideners

The Access Wideners configuration files will need to be available in your mods binary in order to be loaded. The name of each configuration file should be added to the `access_wideners`
section in your `ignite.mod.json`, or alternatively could be added to your jar manifest with the `AccessWidener` key.

**Warning:** Access wideners should only be used in situations where Mixin will not work!

[Access Widener Specification]

## Advanced Usage

Ignite has some properties that can be set on startup to change the launch target, mod directory and more. The following could be added to your startup script:

- The bootstrap service to use. (e.g `-Dignite.service=dummy`)
- The path to the server jar. (e.g `-Dignite.jar=./server.jar`)
- The classpath to the server entry point. (e.g `-Dignite.target=org.bukkit.craftbukkit.Main`)
- The directory ignite libraries will be located. (e.g `-Dignite.libraries=./libraries`)
- The directory ignite mods will be located. (e.g `-Dignite.mods=./mods`)
- The directory ignite mod configs will be located. (e.g `-Dignite.configs=./configs`)

### Bootstrap Services

Bootstrap services provide platform specific modifications to the launch process. In most cases these platforms may not work without using their specified service.
The following target jars will require you to use one:

- Paper (1.18+):
  - Service name: `paper` (e.g `-Dignite.service=paper`)
  - The `ignite.jar` property will be overridden by this service, unless you set `ignite.paper.override=false`.
  - Extra properties:
    - The minecraft server version paperclip will be patching. (e.g `-Dignite.paper.minecraft=1.18.2`)
    - The path to the paperclip jar. (e.g `-Dignite.paper.jar=./paper.jar`)
    - The classpath to the paperclip entry point. (e.g `-Dignite.paper.target=io.papermc.paperclip.Paperclip`)

- Spigot (1.18+):
  - Service name: `spigot` (e.g `-Dignite.service=spigot`)
  - The `ignite.jar` and `ignite.libraries` will be overridden by this service, so you should not set them.
  - Extra properties:
    - The spigot version will be using. (e.g `-Dignite.spigot.version=1.18-R0.1-SNAPSHOT`)
    - The path to the spigot bootstrap jar. (e.g `-Dignite.spigot.jar=./spigot.jar`)
    - The classpath to the spigot bootstrap entry point. (e.g `-Dignite.spigot.target=org.bukkit.craftbukkit.bootstrap.Main`)

- Paper Legacy:
  - Service name: `legacy_paper` (e.g `-Dignite.service=legacy_paper`)
  - The `ignite.jar` property will be overridden by this service, unless you set `ignite.paper.override=false`.
  - Extra properties:
    - The minecraft server version paperclip will be patching. (e.g `-Dignite.paper.minecraft=1.18`)
    - The path to the paperclip jar. (e.g `-Dignite.paper.jar=./paper.jar`)
    - The classpath to the paperclip entry point. (e.g `-Dignite.paper.target=io.papermc.paperclip.Paperclip`)

## Building
__Note:__ If you do not have [Gradle] installed then use `./gradlew` for Unix systems or Git Bash and gradlew.bat for Windows systems in place of any 'gradle' command.

In order to build Ignite you simply need to run the `gradle` command. You can find the compiled JAR file in `./target` labeled 'ignite-launcher.jar'.

## Inspiration

This project has many parts inspired by the following projects:

- [Orion]
- [Sponge]
- [Velocity]
- [plugin-spi]

Initially designed for [Mineteria](https://mineteria.com/).

[Mixin]: https://github.com/SpongePowered/Mixin
[Access Widener]: https://github.com/FabricMC/access-widener
[Mixin Specification]: https://github.com/SpongePowered/Mixin/wiki/Introduction-to-Mixins---The-Mixin-Environment#mixin-configuration-files
[Access Widener Specification]: https://fabricmc.net/wiki/tutorial:accesswideners

[Gradle]: https://www.gradle.org/
[Orion]: https://github.com/OrionMinecraft/Orion
[Sponge]: https://github.com/SpongePowered/Sponge
[Velocity]: https://github.com/VelocityPowered/Velocity
[plugin-spi]: https://github.com/SpongePowered/plugin-spi
