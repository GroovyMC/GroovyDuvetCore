/*
 * Copyright (C) 2023 Luke Bemish, GroovyMC, and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package org.groovymc.groovyduvet.core.impl

import groovy.transform.CompileStatic
import groovy.transform.stc.POJO
import net.fabricmc.loader.api.FabricLoader

import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType

@CompileStatic
@POJO
class PlatformSpecificGetter {
    private PlatformSpecificGetter() {}

    private static final String QUILT_CLASS = 'org.quiltmc.loader.api.QuiltLoader'
    private static final String FABRIC_CLASS = 'net.fabricmc.loader.impl.FabricLoaderImpl'
    private static final String GAME_PROVIDER_CLASS = 'net.fabricmc.loader.impl.game.GameProvider'

    public static final String RAW_GAME_VERSION = calculateRawGameVersion()

    private static String calculateRawGameVersion() {
        var lookup = MethodHandles.lookup()
        try {
            Class<?> clazz = PlatformSpecificGetter.classLoader.loadClass(QUILT_CLASS)
            return (String) lookup.findStatic(clazz, 'getRawGameVersion', MethodType.methodType(String.class)).invokeWithArguments()
        } catch (ClassNotFoundException ignored) {
            Class<?> clazz = PlatformSpecificGetter.classLoader.loadClass(FABRIC_CLASS)
            Class<?> gameProviderClazz = PlatformSpecificGetter.classLoader.loadClass(GAME_PROVIDER_CLASS)
            Object gameProvider = lookup.findVirtual(clazz, 'getGameProvider', MethodType.methodType(gameProviderClazz)).invokeWithArguments(FabricLoader.instance)
            return (String) lookup.findVirtual(gameProviderClazz, 'getRawGameVersion', MethodType.methodType(String.class)).invokeWithArguments(gameProvider)
        }
    }
}
