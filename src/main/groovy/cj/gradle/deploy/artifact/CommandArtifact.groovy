package cj.gradle.deploy.artifact

import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors
import cj.gradle.deploy.CommandDeployResult
import cj.gradle.deploy.context.DeployContext

@CompileStatic
@InheritConstructors(constructorAnnotations = true)
class CommandArtifact extends AbstractArtifact {

    String command = null
    CommandDeployResult result = null

    @Override
    void deploy(DeployContext context) {
        this.result = context.execute(command)
    }

}
