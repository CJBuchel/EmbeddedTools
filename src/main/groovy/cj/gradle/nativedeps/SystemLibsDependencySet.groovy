package cj.gradle.nativedeps

import groovy.transform.CompileStatic

@CompileStatic
interface SystemLibsDependencySet {
    List<String> getSystemLibs()
}