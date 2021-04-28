package space.vectrix.ignite.applaunch.service;

import org.checkerframework.checker.nullness.qual.NonNull;
import space.vectrix.ignite.api.service.IBootstrapService;

public final class DummyBootstrapService implements IBootstrapService {
  @Override
  public @NonNull String name() {
    return "dummy";
  }

  @Override
  public boolean validate() {
    return true;
  }

  @Override
  public void execute() throws Throwable {
    // no-op
  }
}
