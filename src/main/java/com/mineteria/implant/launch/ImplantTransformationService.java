package com.mineteria.implant.launch;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mineteria.implant.ImplantCore;
import com.mineteria.implant.mod.ModResource;
import com.mineteria.implant.util.ImplantConstants;
import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.ITransformationService;
import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.IncompatibleEnvironmentException;
import net.minecraftforge.accesstransformer.AccessTransformerEngine;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ImplantTransformationService implements ITransformationService {
  private final Logger logger = LogManager.getLogger("ImplantTransformation");

  @Override
  public @NonNull String name() {
    return "implant_transformation";
  }

  @Override
  public void initialize(final @NonNull IEnvironment environment) {
    // No-op
  }

  @Override
  public void beginScanning(final @NonNull IEnvironment environment) {
    // No-op
  }

  @Override
  public @NonNull List<Map.Entry<String, Path>> runScan(final @NonNull IEnvironment environment) {
    ImplantCore.INSTANCE.getEngine().locateResources();
    ImplantCore.INSTANCE.getEngine().loadCandidates();

    final List<Map.Entry<String, Path>> launchResources = new ArrayList<>();
    for (final ModResource resource : ImplantCore.INSTANCE.getEngine().getCandidates()) {
      final String atFiles = resource.getManifest().getMainAttributes().getValue(ImplantConstants.AT);
      if (atFiles != null) {
        for (final String atFile : atFiles.split(",")) {
          if (!atFile.endsWith(".cfg")) continue;

          AccessTransformerEngine.INSTANCE.addResource(resource.getFileSystem().getPath(ImplantConstants.META_INF).resolve(atFile), atFile);
        }
      }

      final Map.Entry<String, Path> entry = Maps.immutableEntry(resource.getPath().getFileName().toString(), resource.getPath());
      launchResources.add(entry);
    }

    return launchResources;
  }

  @Override
  public void onLoad(final @NonNull IEnvironment env, final @NonNull Set<String> otherServices) throws IncompatibleEnvironmentException {
    // No-op
  }

  @Override
  @SuppressWarnings("rawtypes")
  public @NonNull List<ITransformer> transformers() {
    return ImmutableList.of();
  }
}
