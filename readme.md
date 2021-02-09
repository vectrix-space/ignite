Ignite
======

Bootstraps the Minecraft Server with [ModLauncher] to apply [Mixins] and [Access Transformers] from mods.

## Building
__Note:__ If you do not have [Gradle] installed then use ./gradlew for Unix systems or Git Bash and gradlew.bat for Windows systems in place of any 'gradle' command.

In order to build Ignite you simply need to run the `gradle` command. You can find the compiled JAR file in `./build/libs` labeled similarly to 'Ignite-x.x.x-SNAPSHOT-shaded.jar'.

## Launcher Usage

The Ignite launcher must be executed instead of the Minecraft Server. Ignite will launch the Minecraft Server itself, additionally passing in any extra arguments you provide it.

`java -javaagent:./Ignite-0.1.1-shaded.jar -Dignite.launch.jar=./paper.jar -Dignite.launch.target=org.bukkit.craftbukkit.Main -Dignite.mod.directory=./plugins -jar Ignite-0.1.1-shaded.jar`

**Note:** You must use the `-javaagent` flag pointing to the launcher in order for it to start.

## Mod Usage

Your mod will require a `META-INF/mod.json` in order to be located as a mod. The `META-INF/mod.json` provides the ID of the mod, and a list of Mixin configuration file names.

Example `META-INF/mod.json`:
```json
{
  "id": "example-mixins",
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
- [plugin-spi]

[ModLauncher]: https://github.com/cpw/modlauncher
[Mixins]: https://github.com/SpongePowered/Mixin
[Access Transformers]: https://github.com/MinecraftForge/AccessTransformers
[Mixin Specification]: https://github.com/SpongePowered/Mixin/wiki/Introduction-to-Mixins---The-Mixin-Environment#mixin-configuration-files
[AT Specification]: https://github.com/MinecraftForge/AccessTransformers/blob/master/FMLAT.md

[Gradle]: https://www.gradle.org/
[Orion]: https://github.com/OrionMinecraft/Orion
[Sponge]: https://github.com/SpongePowered/Sponge
[plugin-spi]: https://github.com/SpongePowered/plugin-spi
