/*
 * Copyright (C) 2022-2023 Luke Bemish, GroovyMC, and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

ModsDotGroovy.make {
    issueTrackerUrl = 'https://github.com/GroovyMC/groovyduvet/issues'
    license = 'LGPL-3.0-or-later'
    languageAdapters = [
        'groovyduvet': 'org.groovymc.groovyduvet.core.impl.GroovyAdapter'
    ]
    mod {
        modId = 'groovyduvet_core'
        version = this.version
        displayName = 'GroovyDuvet: Core'
        description = 'Core module for GroovyDuvet'
        author 'Luke Bemish'
        dependencies {
            fabricLoader = ">=${this.fabricLoaderVersion}"
        }
        displayUrl = 'https://github.com/GroovyMC/groovyduvet'
        entrypoints {
            preLaunch = [
                    adapted {
                        adapter = 'groovyduvet'
                        value = 'org.groovymc.groovyduvet.core.impl.DevExtensionLoader'
                    },
                    adapted {
                        adapter = 'groovyduvet'
                        value = 'org.groovymc.groovyduvet.core.impl.mappings.MetaclassMappingsProvider'
                    }
            ]
        }
    }
    custom = [
        'modmenu' : [
                'badges':['library'],
                'parent':[
                        'id':'groovyduvet',
                        'name':'GroovyDuvet',
                        'description':'Language adapter and wrapper libraries for Groovy mods on Quilt/Fabric',
                        'badges':['library']
                ]
        ]
    ]
    mixin = "groovyduvet.mixin.json"
}
