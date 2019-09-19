package cj.gradle.deploy.context

import groovy.transform.CompileStatic
import cj.gradle.deploy.CommandDeployResult
import cj.gradle.deploy.cache.CacheMethod
import cj.gradle.deploy.sessions.SessionController
import cj.gradle.deploy.target.location.DeployLocation
import cj.gradle.log.ETLogger

@CompileStatic
interface DeployContext {
    SessionController getController()

    // Get the deploy logger
    ETLogger getLogger()

    // Get the working directory
    String getWorkingDir()

    DeployLocation getDeployLocation()

    // Run a command (execute)
    CommandDeployResult execute(String command)

    // Send a batch of files
    void put(Map<String, File> files, CacheMethod cache)

    // Send a single file
    void put(File source, String dest, CacheMethod cache)

    // Send multiple files, and trigger cache checking only once
    void put(Set<File> files, CacheMethod cache)

    String friendlyString()

    DeployContext subContext(String workingDir)
}
