package cj.gradle.deploy.cache

import groovy.transform.CompileStatic
import cj.gradle.deploy.context.DeployContext

@CompileStatic
@FunctionalInterface
interface CompatibleFunction {
  boolean check(DeployContext ctx)
}
