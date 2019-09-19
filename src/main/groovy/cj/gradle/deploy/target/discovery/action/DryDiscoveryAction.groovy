package cj.gradle.deploy.target.discovery.action

import groovy.transform.CompileStatic
import cj.gradle.deploy.context.DefaultDeployContext
import cj.gradle.deploy.context.DeployContext
import cj.gradle.deploy.sessions.DrySessionController
import cj.gradle.deploy.target.discovery.DiscoveryState
import cj.gradle.deploy.target.location.DeployLocation
import cj.gradle.log.ETLogger
import cj.gradle.log.ETLoggerFactory

@CompileStatic
class DryDiscoveryAction extends AbstractDiscoveryAction {

    private ETLogger log

    DryDiscoveryAction(DeployLocation loc) {
        super(loc)
        this.log = ETLoggerFactory.INSTANCE.create(toString())
    }

    @Override
    DeployContext discover() {
        DrySessionController controller = new DrySessionController()
        return new DefaultDeployContext(controller, log, deployLocation, deployLocation.target.directory)
    }

    @Override
    DiscoveryState getState() {
        return DiscoveryState.CONNECTED
    }
}
