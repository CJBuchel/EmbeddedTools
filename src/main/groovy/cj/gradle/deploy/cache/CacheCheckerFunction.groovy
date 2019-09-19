package cj.gradle.deploy.cache

import groovy.transform.CompileStatic
import cj.gradle.deploy.context.DeployContext

@CompileStatic
@FunctionalInterface
interface CacheCheckerFunction {
  boolean check(DeployContext ctx, String filename, File localFile)
}
