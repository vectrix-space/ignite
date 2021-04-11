Ignite
======
![GitHub Workflow Status (branch)](https://img.shields.io/github/workflow/status/vectrix-space/ignite/build/master)
[![MIT License](https://img.shields.io/badge/license-MIT-blue)](license.txt)
[![Maven Central](https://img.shields.io/maven-central/v/space.vectrix.ignite/ignite-api?label=stable)](https://search.maven.org/search?q=g:space.vectrix.ignite%20AND%20a:ignite*)
![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/space.vectrix.ignite/ignite-api?label=dev&server=https%3A%2F%2Fs01.oss.sonatype.org)

Bootstraps the Minecraft Server with [ModLauncher] to apply [Mixins] and [Access Transformers] from Ignite mods.

## Building
__Note:__ If you do not have [Gradle] installed then use `./gradlew` for Unix systems or Git Bash and gradlew.bat for Windows systems in place of any 'gradle' command.

In order to build Ignite you simply need to run the `gradle` command. You can find the compiled JAR file in `./target` labeled 'ignite-launcher.jar'.

## Launcher Usage

The Ignite launcher must be executed instead of the Minecraft Server. Ignite will launch the Minecraft Server itself, additionally passing in any extra arguments you provide it.

`java -Dignite.launch.jar=./paper.jar -Dignite.launch.target=org.bukkit.craftbukkit.Main -Dignite.mod.directory=./plugins -Dignite.config.directory=./plugins -jar ignite-launcher.jar`

**Note:** You must add the flag `-javaagent:./ignite-launcher.jar` if you're running Java 8 or below.

## Mod Usage

To depend on the Ignite API in order to create your mod, you will need to add the following to your buildscript:

* Maven
```xml
<dependency>
  <groupId>space.vectrix.ignite</groupId>
  <artifactId>ignite-api</artifactId>
  <version>0.3.0</version>
</dependency>
```

* Gradle
```groovy
repositories {
  mavenCentral()
}

dependencies {
  compile "space.vectrix.ignite:ignite-api:0.3.0"
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
  "requiredMixins": [
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
