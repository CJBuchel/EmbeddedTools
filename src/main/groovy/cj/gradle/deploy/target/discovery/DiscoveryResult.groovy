package cj.gradle.deploy.target.discovery

import groovy.transform.Canonical
import groovy.transform.CompileStatic
import cj.gradle.deploy.context.DeployContext
import cj.gradle.deploy.target.location.DeployLocation

@Canonical
@CompileStatic
class DiscoveryResult {
    DeployLocation location
    DiscoveryState state

    // On fail
    DiscoveryFailedException failure

    // On succeed
    DeployContext context
}
