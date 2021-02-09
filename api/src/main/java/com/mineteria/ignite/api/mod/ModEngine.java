package com.mineteria.ignite.api.mod;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

public interface ModEngine {
  void locateResources();
  void loadCandidates();
  void loadContainers();
  @NonNull List<ModResource> getCandidates();
  @NonNull List<ModContainer> getContainers();
}
