package cj.gradle.deploy.target.location

import groovy.transform.CompileStatic
import cj.gradle.deploy.target.discovery.action.DiscoveryAction
import cj.gradle.deploy.target.discovery.action.DryDiscoveryAction

@CompileStatic
class DryDeployLocation extends AbstractDeployLocation {

    private DeployLocation inner

    DryDeployLocation(DeployLocation inner) {
        super(inner.target)
        this.inner = inner
    }

    @Override
    DiscoveryAction createAction() {
        return new DryDiscoveryAction(inner)
    }

    @Override
    String friendlyString() {
        return "DryRun DeployLocation (wrapping ${inner.toString()})"
    }
}
