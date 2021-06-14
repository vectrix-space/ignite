Ignite [![Discord](https://img.shields.io/discord/819522977586348052?style=for-the-badge)](https://discord.gg/rYpaxPFQrj)
======
![GitHub Workflow Status (branch)](https://img.shields.io/github/workflow/status/vectrix-space/ignite/build/main)
[![MIT License](https://img.shields.io/badge/license-MIT-blue)](license.txt)
[![Maven Central](https://img.shields.io/maven-central/v/space.vectrix.ignite/ignite-api?label=stable)](https://search.maven.org/search?q=g:space.vectrix.ignite%20AND%20a:ignite*)
![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/space.vectrix.ignite/ignite-api?label=dev&server=https%3A%2F%2Fs01.oss.sonatype.org)

Bootstraps the Minecraft Server with [ModLauncher] to apply [Mixins] and [Access Transformers] from Ignite mods.

## Building
__Note:__ If you do not have [Gradle] installed then use `./gradlew` for Unix systems or Git Bash and gradlew.bat for Windows systems in place of any 'gradle' command.

In order to build Ignite you simply need to run the `gradle` command. You can find the compiled JAR file in `./target` labeled 'ignite-launcher.jar'.

## Launcher Usage

The Ignite launcher must be executed instead of the Minecraft Server. Ignite will launch the Minecraft Server itself, additionally passing in any extra arguments you provide it.
Usually you would want to put the launcher jar, and the server jar in the same directory. 

This would then be run like `java -jar ignite-launcher.jar`. Any other parameters will be passed onto and effect the target server.

**Note:** You must add the flag `-javaagent:./ignite-launcher.jar` if you're running Java 8 or below.

**Note:** If the target jar is a Paper jar, then you may want to read about the bootstrap services below to make launching easier.

### Properties

Ignite has some properties that can be set on startup to change the launch target, mod directory and more. The following could be added to your startup script:

- The bootstrap service to use. (e.g `-Dignite.launch.service=dummy`)
- The path to the server jar. (e.g `-Dignite.launch.jar=./server.jar`)
- The classpath to the server entry point. (e.g `-Dignite.launch.target=org.bukkit.craftbukkit.Main`)
- The directory ignite mods will be located. (e.g `-Dignite.mod.directory=./mods`)
- The directory ignite mod configs will be located. (e.g `-Dignite.config.directory=./configs`)

### Bootstrap Services

Bootstrap services provide platform specific modifications to the launch process. In most cases these platforms may not work without using their specified service.
The following target jars will require you to use one:

- Paperclip:
  - Service name: `paperclip` (e.g `-Dignite.launch.service=paperclip`)
  - The `ignite.launch.jar` property will be overridden by this service, so you do not need to set it manually.
  - Extra properties:
    - The minecraft server version paperclip will be patching. (e.g `-Dignite.paperclip.minecraft=1.16.5`)
    - The path to the paperclip jar. (e.g `-Dignite.paperclip.jar=./paper.jar`)
    - The classpath to the paperclip entry point. (e.g `-Dignite.paperclip.target=io.papermc.paperclip.Paperclip`)

## Mod Usage

To depend on the Ignite API in order to create your mod, you will need to add the following to your buildscript:

* Maven
```xml
<dependency>
  <groupId>space.vectrix.ignite</groupId>
  <artifactId>ignite-api</artifactId>
  <version>0.4.0</version>
</dependency>
```

* Gradle
```groovy
repositories {
  mavenCentral()
}

dependencies {
  compile "space.vectrix.ignite:ignite-api:0.4.0"
}
```

### Setup

Your mod will require a `META-INF/ignite-mod.json` in order to be located as a mod. The `META-INF/ignite-mod.json` provides the ID of the mod, and a list of Mixin configuration file names.

Example `META-INF/ignite-mod.json`:
```json
{
  "id": "example",
  "version": "1.0.0",
  "target": "space.vectrix.example.ExampleMod",
  "mixins": [
    "mixins.example.core.json"
  ]
}
```

The mods will need to be placed in the directory the launcher will be targeting to load.

#### Mixins

The Mixin configuration files will need to be a resource inside the mod jar, which will be used to apply the configured mixins. [Mixin Specification]

#### Access Transformers

The Access Transformers configuration file path should be provided in the manifest with the key `AT`. [AT Specification]

## Inspiration

This project has many parts inspired by the following projects:

- [Orion]
- [Sponge]
- [Velocity]
- [plugin-spi]

Initially designed for [Mineteria](https://mineteria.com/).

[ModLauncher]: https://github.com/cpw/modlauncher
[Mixins]: https://github.com/SpongePowered/Mixin
[Access Transformers]: https://github.com/MinecraftForge/AccessTransformers
[Mixin Specification]: https://github.com/SpongePowered/Mixin/wiki/Introduction-to-Mixins---The-Mixin-Environment#mixin-configuration-files
[AT Specification]: https://github.com/MinecraftForge/AccessTransformers/blob/master/FMLAT.md

[Gradle]: https://www.gradle.org/
[Orion]: https://github.com/OrionMinecraft/Orion
[Sponge]: https://github.com/SpongePowered/Sponge
[Velocity]: https://github.com/VelocityPowered/Velocity
[plugin-spi]: https://github.com/SpongePowered/plugin-spi
