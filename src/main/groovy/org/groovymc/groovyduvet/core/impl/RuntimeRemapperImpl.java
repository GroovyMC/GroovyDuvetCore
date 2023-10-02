/*
 * Copyright (C) 2023 Luke Bemish, GroovyMC, and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package org.groovymc.groovyduvet.core.impl;

import com.google.auto.service.AutoService;
import dev.lukebemish.opensesame.runtime.RuntimeRemapper;
import org.groovymc.groovyduvet.core.impl.compile.ClassMappings;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@AutoService(RuntimeRemapper.class)
public class RuntimeRemapperImpl implements RuntimeRemapper {
    @Override
    public @Nullable String remapMethodName(Class<?> parent, String name, Class<?>[] args) {
        String className = parent.getName();
        var methods = ClassMappings.getMethods().get(className);
        if (methods != null) {
            List<String> methodNames = methods.get(name);
            if (methodNames != null) {
                for (String methodName : methodNames) {
                    try {
                        parent.getDeclaredMethod(methodName, args);
                        return methodName;
                    } catch (NoSuchMethodException ignored) {}
                }
            }
        }
        return null;
    }

    @Override
    public @Nullable String remapFieldName(Class<?> parent, String name) {
        String className = parent.getName();
        var fields = ClassMappings.getFields().get(className);
        if (fields != null) {
            return fields.get(name);
        }
        return null;
    }
}
