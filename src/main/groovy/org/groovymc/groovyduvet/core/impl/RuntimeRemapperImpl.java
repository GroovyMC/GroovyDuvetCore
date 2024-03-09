/*
 * Copyright (C) 2023 Luke Bemish, GroovyMC, and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package org.groovymc.groovyduvet.core.impl;

import com.google.auto.service.AutoService;
import dev.lukebemish.opensesame.runtime.RuntimeRemapper;
import org.groovymc.groovyduvet.core.impl.compile.ClassMappings;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@AutoService(RuntimeRemapper.class)
public class RuntimeRemapperImpl implements RuntimeRemapper {
    @Override
    public @Nullable String remapMethodName(String className, String name, String methodDesc) {
        var methods = ClassMappings.getMethods().get(className);
        if (methods != null) {
            Map<String, String> methodNames = methods.get(name);
            if (methodNames != null) {
                return methodNames.get(methodDesc);
            }
        }
        return null;
    }

    @Override
    public @Nullable String remapFieldName(String className, String name, String type) {
        var fields = ClassMappings.getFields().get(className);
        if (fields != null) {
            return fields.get(name);
        }
        return null;
    }

    @Override
    public @Nullable String remapClassName(String className) {
        return ClassMappings.getMojToRuntime().get(className);
    }
}
