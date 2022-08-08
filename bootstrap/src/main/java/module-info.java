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
module space.vectrix.ignite.bootstrap {
  requires java.base;
  requires jopt.simple;
  requires org.apache.logging.log4j;
  requires org.apache.logging.log4j.core;
  requires org.objectweb.asm.tree;
  requires cpw.mods.securejarhandler;
  requires transitive cpw.mods.modlauncher;
  requires transitive space.vectrix.ignite.api;
  requires static transitive org.jetbrains.annotations;
  exports space.vectrix.ignite.bootstrap.applaunch.blackboard;
  exports space.vectrix.ignite.bootstrap.applaunch.handler;
  exports space.vectrix.ignite.bootstrap.applaunch.service;
  exports space.vectrix.ignite.bootstrap.applaunch.util;
  exports space.vectrix.ignite.bootstrap.applaunch;
  provides cpw.mods.modlauncher.api.ILaunchHandlerService with space.vectrix.ignite.bootstrap.applaunch.handler.PlatformLaunchService;
  provides cpw.mods.modlauncher.api.ITransformationService with space.vectrix.ignite.bootstrap.applaunch.handler.PlatformTransformationService;
  // For the installer to launch into, as we cannot directly invoke the main class.
  provides java.util.function.BiConsumer with space.vectrix.ignite.bootstrap.applaunch.service.InstallLaunchConsumer;
}
