/*
 * This file is part of ignite, licensed under the MIT License (MIT).
 *
 * Copyright (c) vectrix.space <https://vectrix.space/>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package space.vectrix.ignite.installer.service;

import org.jetbrains.annotations.NotNull;
import space.vectrix.ignite.service.InstallProcessorService;

import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public final class ProcessorServiceHandler {
  private final ServiceLoader<InstallProcessorService> serviceLoader;
  private final Map<String, InstallProcessorService> serviceMap;

  public ProcessorServiceHandler() {
    this.serviceLoader = ServiceLoader.load(InstallProcessorService.class);
    this.serviceMap = this.serviceLoader.stream()
      .map(ServiceLoader.Provider::get)
      .collect(Collectors.<InstallProcessorService, String, InstallProcessorService>toUnmodifiableMap(InstallProcessorService::name, x -> x));
  }

  public @NotNull Optional<InstallProcessorService> findService(final @NotNull String name) {
    return Optional.ofNullable(this.serviceMap.get(name));
  }

  public @NotNull Optional<InstallProcessorService> findService(final @NotNull JarFile file) {
    return this.serviceLoader.stream()
      .map(ServiceLoader.Provider::get)
      .filter(installProcessorService -> installProcessorService.scan(file))
      .findFirst();
  }
}
