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
package space.vectrix.ignite.bootstrap.applaunch.handler;

import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.IModuleLayerManager;
import cpw.mods.modlauncher.api.ITransformationService;
import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.IncompatibleEnvironmentException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import space.vectrix.ignite.bootstrap.applaunch.util.Constants;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public final class PlatformTransformationService implements ITransformationService {
  private static final Logger LOGGER = LogManager.getLogger("IgniteBootstrap | TransformationService");

  public PlatformTransformationService() {}

  @Override
  public @NotNull String name() {
    return Constants.IGNITE_TRANSFORMATION_SERVICE;
  }

  @Override
  public void initialize(final @NotNull IEnvironment environment) {

  }

  @Override
  public List<Resource> beginScanning(final @NotNull IEnvironment environment) {
    return ITransformationService.super.beginScanning(environment);
  }

  @Override
  public List<Resource> completeScan(final @NotNull IModuleLayerManager layerManager) {
    return ITransformationService.super.completeScan(layerManager);
  }

  @Override
  public void onLoad(final @NotNull IEnvironment environment, final @NotNull Set<String> otherServices) throws IncompatibleEnvironmentException {

  }

  @Override
  @SuppressWarnings("rawtypes")
  public @NotNull List<ITransformer> transformers() {
    return Collections.emptyList();
  }
}
