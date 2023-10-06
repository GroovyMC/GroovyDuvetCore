/*
 * Copyright (C) 2022-2023 Luke Bemish, GroovyMC, and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package org.groovymc.groovyduvet.core.impl;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.slf4j.Logger;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

public class ExtensionSideChecker {
    private ExtensionSideChecker() {}
    public static final String INSTANCE_CLASSES = "extensionClasses";
    public static final String STATIC_CLASSES = "staticExtensionClasses";
    private static final String ONLYIN = "Lnet/minecraftforge/api/distmarker/OnlyIn;";
    private static final String ENVIRONMENT = "Lnet/fabricmc/api/Environment;";
    private static final String LEGACY_EXTENSION = "Lio/github/groovymc/cgl/api/extension/EnvironmentExtension;";
    private static final String EXTENSION = "Lorg/groovymc/cgl/api/extension/EnvironmentExtension;";
    private static final String SERVER_ONLY = "Lorg/quiltmc/loader/api/minecraft/DedicatedServerOnly;";
    private static final String CLIENT_ONLY = "Lorg/quiltmc/loader/api/minecraft/ClientOnly;";

    private static final String skipOnClient = "Skipping extension class {} as we are on the client";
    private static final String skipOnServer = "Skipping extension class {} as we are on the server";

    private static final Logger LOGGER = LogUtils.getLogger();

    public static boolean checkSidedness(String className, ClassLoader classLoader) {
        AtomicBoolean isOnDist = new AtomicBoolean(true);
        try (var stream = classLoader.getResourceAsStream(className.replace('.', '/') + ".class")) {
            if (stream == null) {
                return false;
            }
            new ClassReader(stream).accept(new ClassVisitor(Opcodes.ASM9) {
                @Override
                public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
                    switch (desc) {
                        case ONLYIN, ENVIRONMENT, EXTENSION, LEGACY_EXTENSION -> {
                            return new AnnotationVisitor(Opcodes.ASM9) {
                                @Override
                                public void visitEnum(String name, String descriptor, String value) {
                                    if (name.equals("value")) {
                                        String s = value.toUpperCase(Locale.ROOT);
                                        if (s.equals("SERVER") || s.equals("DEDICATED_SERVER")) {
                                            isOnDist.set(FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER);
                                            if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
                                                LOGGER.info(skipOnClient, className);
                                        } else if (s.equals("CLIENT")) {
                                            isOnDist.set(FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT);
                                            if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER)
                                                LOGGER.info(skipOnServer, className);
                                        }
                                    }
                                }
                            };
                        }
                        case SERVER_ONLY -> {
                            isOnDist.set(FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER);
                            if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
                                LOGGER.info(skipOnClient, className);
                        }
                        case CLIENT_ONLY -> {
                            isOnDist.set(FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT);
                            if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER)
                                LOGGER.info(skipOnServer, className);
                        }
                    }
                    return super.visitAnnotation(desc, visible);
                }
            }, ClassReader.SKIP_CODE);
        } catch (Exception e) {
            LOGGER.error("Failed to check side  dness of class {}", className, e);
            isOnDist.set(false);
        }

        return isOnDist.get();
    }
}
