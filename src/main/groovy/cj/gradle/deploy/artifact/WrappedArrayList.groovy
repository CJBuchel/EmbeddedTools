package cj.gradle.deploy.artifact

import org.gradle.api.Action
import cj.gradle.ActionWrapper
import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors
import cj.gradle.deploy.context.DeployContext

// An ArrayList of actions, with overridding the leftShift operator
// to take a closure. Enables closures from the DSL
@CompileStatic
@InheritConstructors
class WrappedArrayList extends ArrayList<Action<DeployContext>> {
  WrappedArrayList leftShift(Closure closure) {
    Action<DeployContext> wrapper = new ActionWrapper<DeployContext>(closure)
    this.add(wrapper)
    return this
  }
}
