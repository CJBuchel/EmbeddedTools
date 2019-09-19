package cj.gradle.deploy.target.discovery

import groovy.transform.CompileStatic
import cj.gradle.deploy.target.discovery.action.DiscoveryAction

@CompileStatic
class DiscoveryFailedException extends Exception {
    DiscoveryAction action

    DiscoveryFailedException(DiscoveryAction action, Throwable cause) {
        super(cause)
        this.action = action
    }
}
