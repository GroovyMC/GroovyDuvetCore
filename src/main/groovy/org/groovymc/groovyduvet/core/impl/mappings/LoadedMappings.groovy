/*
 * Copyright (C) 2022-2023 Luke Bemish, GroovyMC, and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package org.groovymc.groovyduvet.core.impl.mappings

import groovy.transform.CompileStatic
import groovy.transform.stc.POJO

@CompileStatic
@POJO
class LoadedMappings {
    // runtime class name (with dots) to map of moj -> runtime names
    final Map<String, Map<String, List<String>>> methods
    final Map<String, Map<String, String>> fields
    final Set<String> mappable

    LoadedMappings(Map<String, Map<String, Map<String, String>>> methods, Map<String, Map<String, String>> fields) {
        this.methods = methods.collectEntries {k, v ->
            [(k): v.collectEntries {k2, v2 -> [(k2): v2.values().collect()]}]
        }
        this.fields = fields

        List<String> emptyRemovalQueue = []
        this.methods.each (className,methodMap) -> {
            List<String> noKnownMappingsRemovalQueue = []
            methodMap.forEach(official,srg) -> {
                List<String> unnecessaryRemovalQueue = []
                srg.forEach (a)->{
                    if (official==a) unnecessaryRemovalQueue.add(a)
                }
                unnecessaryRemovalQueue.each {srg.remove it}
                if (srg.isEmpty()) noKnownMappingsRemovalQueue.add(official)
            }
            noKnownMappingsRemovalQueue.each {methodMap.remove it}

            if (methodMap.isEmpty()) {
                emptyRemovalQueue.add(className)
            }
        }
        emptyRemovalQueue.each {this.methods.remove it}

        emptyRemovalQueue.clear()
        this.fields.forEach (className,fieldMap) -> {
            List<String> unnecessaryRemovalQueue = []
            fieldMap.forEach (official,srg) -> {
                if (official==srg) unnecessaryRemovalQueue.add(official)
                return
            }
            unnecessaryRemovalQueue.each {fieldMap.remove it}

            if (fieldMap.isEmpty()) {
                emptyRemovalQueue.add(className)
            }
        }
        emptyRemovalQueue.each {this.fields.remove it}

        this.mappable = methods.keySet() + fields.keySet()
    }
}
