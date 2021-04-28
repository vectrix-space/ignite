package space.vectrix.ignite.applaunch.service;

import cpw.mods.modlauncher.ServiceLoaderStreamUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import space.vectrix.ignite.api.service.IBootstrapService;

import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;

public final class BootstrapServiceHandler {
  private final ServiceLoader<IBootstrapService> bootstrapHandlerServices;
  private final Map<String, IBootstrapService> bootstrapServiceLookup;

  public BootstrapServiceHandler() {
    this.bootstrapHandlerServices = ServiceLoaderStreamUtils.errorHandlingServiceLoader(IBootstrapService.class, error -> {
      final RuntimeException problem = new RuntimeException("Encountered an exception attempting to load a bootstrap service!", error);
      problem.printStackTrace();
    });
    this.bootstrapServiceLookup = ServiceLoaderStreamUtils.toMap(this.bootstrapHandlerServices, IBootstrapService::name);
  }

  public @NonNull Optional<IBootstrapService> findService(final String name) {
    return Optional.ofNullable(this.bootstrapServiceLookup.get(name));
  }
}
