package cj.gradle.deploy.target.location

import groovy.transform.CompileStatic
import cj.gradle.deploy.target.RemoteTarget
import cj.gradle.deploy.target.discovery.action.DiscoveryAction

@CompileStatic
interface DeployLocation {
    DiscoveryAction createAction()

    RemoteTarget getTarget()

    String friendlyString()
}