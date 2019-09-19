package cj.gradle.deploy.artifact

import groovy.transform.CompileStatic
import cj.gradle.Resolver
import cj.gradle.deploy.cache.CacheMethod

@CompileStatic
interface CacheableArtifact extends Artifact {
    Object getCache()
    void setCache(Object cacheMethod)

    void setCacheResolver(Resolver<CacheMethod> resolver)
}