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

import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.internal.Strings;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import space.vectrix.ignite.installer.InstallerTerminal;
import space.vectrix.ignite.installer.transformer.TransformingClassLoader;
import space.vectrix.ignite.installer.util.ScopedClassWriter;

import java.io.IOException;
import java.util.jar.JarFile;

public final class PaperclipProcessorService implements InstallProcessorService {
  private static final OptionParser PARSER = new OptionParser();

  private static final ArgumentAcceptingOptionSpec<String> PAPERCLIP_CLASSPATH_ARGUMENT = PaperclipProcessorService.PARSER
    .accepts("paperclipClasspath", "Paperclip Classpath")
    .withRequiredArg()
    .defaultsTo("io.papermc.paperclip.Paperclip");

  static {
    PaperclipProcessorService.PARSER.allowsUnrecognizedOptions();
  }

  public static String PAPERCLIP_CLASSPATH;
  public static String PAPERCLIP_ENTRY;

  @Override
  public void initialize() {
    final OptionSet options = PaperclipProcessorService.PARSER.parse(Strings.join(InstallerTerminal.REMAINING_ARGS, " "));

    PaperclipProcessorService.PAPERCLIP_CLASSPATH = options.valueOf(PaperclipProcessorService.PAPERCLIP_CLASSPATH_ARGUMENT);

    PaperclipProcessorService.PAPERCLIP_ENTRY = PaperclipProcessorService.PAPERCLIP_CLASSPATH.replace('.', '/') + ".class";
  }

  @Override
  public @NotNull String name() {
    return "paperclip";
  }

  @Override
  public boolean scan(final @NotNull JarFile file) {
    return file.getEntry(PaperclipProcessorService.PAPERCLIP_ENTRY) != null;
  }

  @Override
  public void execute(final @NotNull JarFile file) throws Exception {
    // Set paperclip to patch only, we launch the server ourselves.
    System.setProperty("paperclip.patchonly", "true");

    // Create the transforming class loader.
    try(final TransformingClassLoader classLoader = new TransformingClassLoader(this.name(), ClassLoader.getSystemClassLoader())) {
      // Add the paperclip transformer.
      classLoader.transformer(new PaperclipTransformer(classLoader));

      // Add the paperclip jar.
      classLoader.addURL(InstallerTerminal.INSTALL_JAR.toRealPath().toUri().toURL());

      // Launch paperclip.
      final Class<?> mainClass = Class.forName(PaperclipProcessorService.PAPERCLIP_CLASSPATH, true, classLoader);
      mainClass
        .getMethod("main", String[].class)
        .invoke(null, (Object) new String[0]);

      // Remove the patchonly flag.
      System.getProperties().remove("paperclip.patchonly");
    }
  }

  public static final class PaperclipTransformer implements TransformingClassLoader.Transformer {
    private final ClassLoader loader;

    /* package */ PaperclipTransformer(final ClassLoader loader) {
      this.loader = loader;
    }

    @Override
    public byte[] transform(final @NotNull String className, final byte[] classBytes) throws IOException {
      final ClassReader reader = new ClassReader(classBytes);
      final ClassWriter writer = new ScopedClassWriter(reader, this.loader, ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
      reader.accept(new PaperclipClassVisitor(writer), ClassReader.EXPAND_FRAMES);
      return writer.toByteArray();
    }

    public static final class PaperclipClassVisitor extends ClassVisitor {
      public PaperclipClassVisitor(final ClassVisitor visitor) {
        super(Opcodes.ASM9, visitor);
      }

      @Override
      public MethodVisitor visitMethod(final int access, final String name, final String descriptor, final String signature, final String[] exceptions) {
        final MethodVisitor mv = this.cv.visitMethod(access, name, descriptor, signature, exceptions);
        return new PaperclipMethodVisitor(descriptor, mv);
      }
    }

    public static final class PaperclipMethodVisitor extends MethodVisitor {
      private final String descriptor;

      public PaperclipMethodVisitor(final String descriptor, final MethodVisitor visitor) {
        super(Opcodes.ASM9, visitor);

        this.descriptor = descriptor;
      }

      @Override
      public void visitMethodInsn(final int opcode, final String owner, final String name, final String descriptor, final boolean isInterface) {
        if(name.equals("setupClasspath")) {
          super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
          // After the method is written return.
          this.visitInsn(Opcodes.RETURN);
          return;
        }

        // Return before system exit calls.
        if (owner.equals("java/lang/System") && name.equals("exit")) {
          if (this.descriptor.endsWith("V")) {
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
}
