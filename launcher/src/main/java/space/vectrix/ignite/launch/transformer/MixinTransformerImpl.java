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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.transformer.IMixinTransformer;
import org.spongepowered.asm.mixin.transformer.IMixinTransformerFactory;
import org.spongepowered.asm.service.ISyntheticClassRegistry;
import org.spongepowered.asm.transformers.MixinClassReader;
import space.vectrix.ignite.launch.ember.TransformPhase;
import space.vectrix.ignite.launch.ember.TransformerService;
import space.vectrix.ignite.util.IgniteConstants;

/**
 * Provides the mixin transformer for Ignite.
 *
 * @author vectrix
 * @since 1.0.0
 */
public final class MixinTransformerImpl implements TransformerService {
  private IMixinTransformerFactory transformerFactory;
  private IMixinTransformer transformer;
  private ISyntheticClassRegistry registry;

  /**
   * Offers the transformer factory to this transformer.
   *
   * @param factory the transformer factory
   * @since 1.0.0
   */
  public void offer(final @NotNull IMixinTransformerFactory factory) {
    this.transformerFactory = factory;
  }

  @Override
  public void prepare() {
    if(this.transformerFactory == null) throw new IllegalStateException("Transformer factory is not available!");
    this.transformer = this.transformerFactory.createTransformer();
    this.registry = this.transformer.getExtensions().getSyntheticClassRegistry();
  }

  @Override
  public int priority(final @NotNull TransformPhase phase) {
    // We don't want mixin trying to transform targets it's already
    // transforming.
    if(phase == TransformPhase.MIXIN) return -1;
    // This prioritizes mixin in the middle of the transformation
    // pipeline.
    return 50;
  }

  @Override
  public boolean shouldTransform(final @NotNull Type type, final @NotNull ClassNode node) {
    // We want to send everything for mixin to decide.
    return true;
  }

  @Override
  public @Nullable ClassNode transform(final @NotNull Type type, final @NotNull ClassNode node, final @NotNull TransformPhase phase) throws Throwable {
    // Generate the class if it is synthetic through mixin.
    if(this.shouldGenerateClass(type)) {
      return this.generateClass(type, node) ? node : null;
    }

    // Transform the class through mixin.
    return this.transformer.transformClass(MixinEnvironment.getCurrentEnvironment(), type.getClassName(), node) ? node : null;
  }

  /**
   * Returns the class node for the given canonical name, internal name and
   * input class bytes.
   *
   * @param canonicalName the canonical name
   * @param internalName the internal name
   * @param input the input class bytes
   * @param readerFlags the reader flags
   * @return the class node
   * @throws ClassNotFoundException if the class could not be found
   * @since 1.0.0
   */
  public @NotNull ClassNode classNode(final @NotNull String canonicalName, final @NotNull String internalName, final byte@NotNull [] input, final int readerFlags) throws ClassNotFoundException {
    if(input.length != 0) {
      final ClassNode node = new ClassNode(IgniteConstants.ASM_VERSION);
      final ClassReader reader = new MixinClassReader(input, canonicalName);
      reader.accept(node, readerFlags);
      return node;
    }

    final Type type = Type.getObjectType(internalName);
    if(this.shouldGenerateClass(type)) {
      final ClassNode node = new ClassNode(IgniteConstants.ASM_VERSION);
      if(this.generateClass(type, node)) return node;
    }

    throw new ClassNotFoundException(canonicalName);
  }

  /* package */ boolean shouldGenerateClass(final @NotNull Type type) {
    return this.registry.findSyntheticClass(type.getClassName()) != null;
  }

  /* package */ boolean generateClass(final @NotNull Type type, final @NotNull ClassNode node) {
    return this.transformer.generateClass(MixinEnvironment.getCurrentEnvironment(), type.getClassName(), node);
  }
}
