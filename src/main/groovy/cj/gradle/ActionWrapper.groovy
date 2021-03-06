package cj.gradle

import groovy.transform.CompileStatic
import org.gradle.api.Action
import cj.gradle.deploy.context.DeployContext
import cj.gradle.ClosureUtils

/**
 * A simple class to wrap a Closure in an Action
 * Groovy's cast will never delegate the closure
 */
@CompileStatic
class ActionWrapper<T> implements Action {
  private Closure closure

  ActionWrapper(Closure closure) {
    this.closure = closure
  }

  void execute(T t) {
    ClosureUtils.delegateCall(t, closure)
  }
}
