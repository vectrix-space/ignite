package space.vectrix.ignite.api.service;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * A singleton instance of this, loaded by the bootstrapper to manipulate
 * launch properties before they occur.
 */
public interface IBootstrapService {
  /**
   * The bootstrap service name.
   *
   * @return The name
   */
  @NonNull String name();

  /**
   * Returns {@code true} if the service is in the right environment
   * to be used, otherwise returns {@code false}.
   *
   * @return Whether the service is in a valid environment
   */
  boolean validate();

  /**
   * Executes the underlying functions to manipulate the launch properties.
   *
   * @throws Throwable Any errors from the functions
   */
  void execute() throws Throwable;
}
