package com.mineteria.ignite.mod;

import com.google.common.collect.Maps;
import com.google.common.graph.Graph;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import com.mineteria.ignite.api.mod.ModContainer;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("UnstableApiUsage")
public final class ModDependencyResolver {
  public static List<ModContainer> resolveDependencies(final @NonNull ModEngine engine, final @NonNull Collection<ModContainer> containers) throws IllegalStateException {
    final List<ModContainer> sortedContainers = new ArrayList<>(containers);
    sortedContainers.sort(Comparator.comparing(ModContainer::getId));

    final MutableGraph<ModContainer> graph = GraphBuilder.directed()
      .allowsSelfLoops(false)
      .expectedNodeCount(sortedContainers.size())
      .build();

    final Map<String, ModContainer> candidateMap = Maps.uniqueIndex(sortedContainers, ModContainer::getId);

    for (final ModContainer container : sortedContainers) {
      graph.addNode(container);

      // Required Dependencies
      final List<String> requiredDependencies = container.getConfig().getRequiredDependencies();
      if (requiredDependencies != null) {
        for (final String requiredDependency : requiredDependencies) {
          final ModContainer dependency = candidateMap.get(requiredDependency);
          if (dependency != null) {
            graph.putEdge(container, dependency);
          } else {
            engine.getLogger().error("Unable to resolve required dependency '" + requiredDependency + "' for '" + container.getId() + "!");
            graph.removeNode(container);
          }
        }
      }

      // Optional Dependencies
      final List<String> optionalDependencies = container.getConfig().getOptionalDependencies();
      if (optionalDependencies != null) {
        for (final String optionalDependency : optionalDependencies) {
          final ModContainer dependency = candidateMap.get(optionalDependency);
          if (dependency != null) {
            graph.putEdge(container, dependency);
          } else {
            engine.getLogger().error("Unable to resolve optional dependency '" + optionalDependency + "' for '" + container.getId() + "!");
            graph.removeNode(container);
          }
        }
      }
    }

    final List<ModContainer> sorted = new ArrayList<>();
    final Map<ModContainer, Mark> marks = new HashMap<>();

    for (final ModContainer node : graph.nodes()) {
      ModDependencyResolver.visitNode(graph, node, marks, sorted, new ArrayDeque<>());
    }

    return sorted;
  }

  private static void visitNode(final @NonNull Graph<ModContainer> dependencyGraph,
                                final @NonNull ModContainer node,
                                final @NonNull Map<ModContainer, Mark> marks,
                                final @NonNull List<ModContainer> sorted,
                                final @NonNull Deque<ModContainer> currentIteration) throws IllegalStateException {
    final Mark mark = marks.getOrDefault(node, Mark.NOT_VISITED);
    if (mark == Mark.PERMANENT) {
      return;
    } else if (mark == Mark.TEMPORARY) {
      // Circular dependency.
      currentIteration.addLast(node);

      final StringBuilder loopGraph = new StringBuilder();
      for (final ModContainer container : currentIteration) {
        loopGraph.append(container.getId());
        loopGraph.append(" -> ");
      }

      loopGraph.setLength(loopGraph.length() - 4);
      throw new IllegalStateException("Circular dependency detected: " + loopGraph.toString());
    }

    currentIteration.addLast(node);
    marks.put(node, Mark.TEMPORARY);
    for (final ModContainer edge : dependencyGraph.successors(node)) {
      ModDependencyResolver.visitNode(dependencyGraph, edge, marks, sorted, currentIteration);
    }

    marks.put(node, Mark.PERMANENT);
    currentIteration.removeLast();
    sorted.add(node);
  }

  private ModDependencyResolver() {}

  private enum Mark {
    NOT_VISITED,
    TEMPORARY,
    PERMANENT
  }
}
