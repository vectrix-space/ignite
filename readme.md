Implant
=======

**Under development and not stable!**

Allows the application of mixins provided by mods on Mineteria.

## Building
__Note:__ If you do not have [Gradle] installed then use ./gradlew for Unix systems or Git Bash and gradlew.bat for Windows systems in place of any 'gradle' command.

In order to build Implant you simply need to run the `gradle` command. You can find the compiled JAR file in `./build/libs` labeled similarly to 'Implant-x.x.x-SNAPSHOT-shaded.jar'.

## Usage

Implant must be executed instead of the Minecraft Server Jar. Implant will bootstrap mixins scan the mod directory to apply any mixins, then proceed with launching the Minecraft Server Jar itself.

`java -Dimplant.launch.jar=./spigot.jar -Dimplant.launch.target=org.bukkit.craftbukkit.Main -Dimplant.mod.directory=./plugins -jar Implant-0.1.0-SNAPSHOT-shaded.jar`

## Inspiration

This project has many parts inspired by the following projects:

- [Orion]
- [Sponge]
- [plugin-spi]

[Gradle]: https://www.gradle.org/
[Orion]: https://github.com/OrionMinecraft/Orion
[Sponge]: https://github.com/SpongePowered/Sponge
[plugin-spi]: https://github.com/SpongePowered/plugin-spi
