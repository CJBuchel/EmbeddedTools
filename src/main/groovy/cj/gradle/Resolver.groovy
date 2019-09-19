package cj.gradle

import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors

@CompileStatic
interface Resolver<T> {
    T resolve(Object o)

    @CompileStatic
    @InheritConstructors(constructorAnnotations = true)
    static class ResolveFailedException extends RuntimeException {}
}
