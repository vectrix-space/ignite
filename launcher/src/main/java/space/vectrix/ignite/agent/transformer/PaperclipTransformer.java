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
package space.vectrix.ignite.agent.transformer;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import space.vectrix.ignite.util.IgniteConstants;

/**
 * Provides a transformer for replacing Paperclips {@link System#exit(int)}s
 * with returns.
 *
 * @author vectrix
 * @since 1.0.0
 */
public final class PaperclipTransformer implements ClassFileTransformer {
  private final String target;

  /**
   * Creates a new paperclip transformer.
   *
   * @param target the target class
   * @since 1.0.0
   */
  public PaperclipTransformer(final @NotNull String target) {
    this.target = target;
  }

  @Override
  public byte[] transform(final ClassLoader loader, final String className, final Class<?> classBeingRedefined,
                          final ProtectionDomain protectionDomain, final byte[] classFileBuffer) throws IllegalClassFormatException {
    if(!className.equals(this.target)) return null;
    final ClassReader reader = new ClassReader(classFileBuffer);
    final ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
    reader.accept(new PaperclipClassVisitor(writer), ClassReader.EXPAND_FRAMES);
    return writer.toByteArray();
  }

  private static final class PaperclipClassVisitor extends ClassVisitor {
    private PaperclipClassVisitor(final @NotNull ClassVisitor visitor) {
      super(IgniteConstants.ASM_VERSION, visitor);
    }

    @Override
    public MethodVisitor visitMethod(final int access, final @NotNull String name, final @NotNull String descriptor, final @NotNull String signature, final @NotNull String[] exceptions) {
      final MethodVisitor mv = this.cv.visitMethod(access, name, descriptor, signature, exceptions);
      return new PaperclipMethodVisitor(descriptor, mv);
    }
  }

  private static final class PaperclipMethodVisitor extends MethodVisitor {
    private final String descriptor;

    private PaperclipMethodVisitor(final @NotNull String descriptor, final @NotNull MethodVisitor visitor) {
      super(IgniteConstants.ASM_VERSION, visitor);

      this.descriptor = descriptor;
    }

    @Override
    public void visitMethodInsn(final int opcode, final @NotNull String owner, final @NotNull String name,
                                final @NotNull String descriptor, final boolean isInterface) {
      if(name.equals("setupClasspath")) {
        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
        // After the method is written return.
        this.visitInsn(Opcodes.RETURN);
        return;
      }

      // Return before system exit calls.
      if(owner.equals("java/lang/System") && name.equals("exit")) {
        if(this.descriptor.endsWith("V")) {
          // Void descriptor return type, will return normally...
          this.visitInsn(Opcodes.RETURN);
        } else {
          // Otherwise, return null.
          this.visitInsn(Opcodes.ACONST_NULL);
          this.visitInsn(Opcodes.ARETURN);
        }
      }

      super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
    }
  }
}
