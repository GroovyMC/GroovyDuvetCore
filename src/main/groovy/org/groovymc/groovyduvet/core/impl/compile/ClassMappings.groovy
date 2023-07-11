/*
 * Copyright (C) 2022-2023 Luke Bemish, GroovyMC, and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package org.groovymc.groovyduvet.core.impl.compile

import groovy.transform.CompileStatic

@CompileStatic
final class ClassMappings {
    private ClassMappings() {}

    static final Map<String, String> mojToRuntime = [:]
    static final Map<String, Map<String, String>> mojToRuntimePackages = [:]
    static final Map<String, Map<String, List<String>>> methods = [:]
    static final Map<String, Map<String, String>> fields = [:]

    static addMappings(Map<String, String> mappings, Map<String, Map<String, List<String>>> methods, Map<String, Map<String, String>> fields) {
        mojToRuntime.clear()
        mojToRuntimePackages.clear()
        ClassMappings.methods.clear()
        ClassMappings.fields.clear()

        ClassMappings.methods.putAll(methods)
        ClassMappings.fields.putAll(fields)
        ClassMappings.mojToRuntime.putAll(mappings)
        mappings.each { moj, runtime ->
            def mojPackage = moj.substring(0, moj.lastIndexOf('.'))
            def mojName = moj.substring(moj.lastIndexOf('.') + 1)
            if (!mojToRuntimePackages.containsKey(mojPackage)) {
                mojToRuntimePackages.put(mojPackage, new HashMap<>())
            }
            mojToRuntimePackages[mojPackage].put(mojName, runtime)
        }
    }
}
