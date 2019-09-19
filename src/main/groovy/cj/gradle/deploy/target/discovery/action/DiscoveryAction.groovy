package cj.gradle.deploy.target.discovery.action

import groovy.transform.CompileStatic
import cj.gradle.deploy.context.DeployContext
import cj.gradle.deploy.target.discovery.DiscoveryFailedException
import cj.gradle.deploy.target.discovery.DiscoveryState
import cj.gradle.deploy.target.location.DeployLocation

import java.util.concurrent.Callable

@CompileStatic
interface DiscoveryAction extends Callable<DeployContext> {

    DeployContext discover()

    DiscoveryFailedException getException()

    DiscoveryState getState()

    DeployLocation getDeployLocation()

}