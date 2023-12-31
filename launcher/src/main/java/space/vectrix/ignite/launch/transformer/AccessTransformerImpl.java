/*
 * This file is part of Ignite, licensed under the MIT License (MIT).
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
package space.vectrix.ignite.launch.transformer;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.accesswidener.AccessWidener;
import net.fabricmc.accesswidener.AccessWidenerClassVisitor;
import net.fabricmc.accesswidener.AccessWidenerReader;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import space.vectrix.ignite.launch.ember.TransformPhase;
import space.vectrix.ignite.launch.ember.TransformerService;
import space.vectrix.ignite.util.IgniteConstants;

/**
 * Provides the access transformer for Ignite.
 *
 * @author vectrix
 * @since 1.0.0
 */
public final class AccessTransformerImpl implements TransformerService {
  private final AccessWidener widener = new AccessWidener();
  private final AccessWidenerReader widenerReader = new AccessWidenerReader(this.widener);

  /**
   * Adds a widener to this transformer.
   *
   * @param path the configuration path
   * @throws IOException if an error occurs while reading the widener
   * @since 1.0.0
   */
  public void addWidener(final @NotNull Path path) throws IOException {
    try(final BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
      this.widenerReader.read(reader);
    }
  }

  @Override
  public void prepare() {
  }

  @Override
  public int priority(final @NotNull TransformPhase phase) {
    // Only transform targets on the initialize phase.
    if(phase != TransformPhase.INITIALIZE) return -1;
    // This prioritizes access widener near the beginning of the transformation
    // pipeline.
    return 25;
  }

  @Override
  public boolean shouldTransform(final @NotNull Type type, final @NotNull ClassNode node) {
    // Only transform targets that need to be widened.
    return this.widener.getTargets().contains(node.name.replace('/', '.'));
  }

  @Override
  public boolean transform(final @NotNull Type type, final @NotNull ClassNode node, final @NotNull TransformPhase phase) throws Throwable {
    final ClassNode widened = new ClassNode(IgniteConstants.ASM_VERSION);
    widened.accept(node);

    final ClassVisitor visitor = AccessWidenerClassVisitor.createClassVisitor(IgniteConstants.ASM_VERSION, node, this.widener);

    node.visibleAnnotations = null;
    node.invisibleAnnotations = null;
    node.visibleTypeAnnotations = null;
    node.invisibleTypeAnnotations = null;
    node.attrs = null;
    node.nestMembers = null;
    node.permittedSubclasses = null;
    node.recordComponents = null;
    node.innerClasses.clear();
    node.fields.clear();
    node.methods.clear();
    node.interfaces.clear();

    widened.accept(visitor);
    return true;
  }
}
