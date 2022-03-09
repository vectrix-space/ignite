package space.vectrix.ignite.applaunch;

import org.spongepowered.configurate.gson.GsonConfigurationLoader;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;
import space.vectrix.ignite.api.Blackboard;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;

public final class IgniteConfigurations {
  private static final OpenOption[] SINK_OPTIONS = new OpenOption[] {
    StandardOpenOption.CREATE,
    StandardOpenOption.TRUNCATE_EXISTING,
    StandardOpenOption.WRITE,
    StandardOpenOption.DSYNC
  };

  public static void configure() {
    Blackboard.computeProperty(Blackboard.GSON_LOADER, path -> GsonConfigurationLoader.builder()
      .source(() -> Files.newBufferedReader(path, StandardCharsets.UTF_8))
      .sink(() -> Files.newBufferedWriter(path, StandardCharsets.UTF_8, IgniteConfigurations.SINK_OPTIONS))
      .defaultOptions(options -> options.shouldCopyDefaults(true))
      .build()
    );

    Blackboard.computeProperty(Blackboard.HOCON_LOADER, path -> HoconConfigurationLoader.builder()
      .source(() -> Files.newBufferedReader(path, StandardCharsets.UTF_8))
      .sink(() -> Files.newBufferedWriter(path, StandardCharsets.UTF_8, IgniteConfigurations.SINK_OPTIONS))
      .defaultOptions(options -> options.shouldCopyDefaults(true))
      .build()
    );

    Blackboard.computeProperty(Blackboard.YAML_LOADER, path -> YamlConfigurationLoader.builder()
      .source(() -> Files.newBufferedReader(path, StandardCharsets.UTF_8))
      .sink(() -> Files.newBufferedWriter(path, StandardCharsets.UTF_8, IgniteConfigurations.SINK_OPTIONS))
      .defaultOptions(options -> options.shouldCopyDefaults(true))
      .build()
    );
  }

  private IgniteConfigurations() {}
}
