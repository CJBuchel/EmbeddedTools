package cj.gradle.deploy

import groovy.transform.CompileStatic
import cj.gradle.deploy.artifact.AbstractArtifact
import cj.gradle.deploy.artifact.Artifact
import cj.gradle.deploy.artifact.ArtifactDeployTask
import cj.gradle.deploy.artifact.ArtifactsExtension
import cj.gradle.deploy.artifact.CacheableArtifact
import cj.gradle.deploy.cache.CacheExtension
import cj.gradle.deploy.cache.CacheMethod
import cj.gradle.deploy.target.RemoteTarget
import cj.gradle.deploy.target.TargetsExtension
import cj.gradle.deploy.target.discovery.TargetDiscoveryTask
import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectCollection
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.tasks.TaskCollection

import javax.inject.Inject
import java.util.concurrent.Callable

@CompileStatic
class DeployExtension {
    TargetsExtension targets
    ArtifactsExtension artifacts
    CacheExtension cache

    Project project

    @Inject
    DeployExtension(Project project) {
        this.project = project
        targets = ((ExtensionAware)this).extensions.create('targets', TargetsExtension, project)
        artifacts = ((ExtensionAware)this).extensions.create('artifacts', ArtifactsExtension, project)
        cache = ((ExtensionAware)this).extensions.create('cache', CacheExtension, project)

        this.targets.all { RemoteTarget target ->
            project.tasks.register("discover${target.name.capitalize()}".toString(), TargetDiscoveryTask) { TargetDiscoveryTask task ->
                task.group = "EmbeddedTools"
                task.description = "Determine the address(es) of target ${target.name.capitalize()}"
                task.target = target
            }
        }

        this.artifacts.all { AbstractArtifact artifact ->
            if (artifact instanceof CacheableArtifact)
                ((CacheableArtifact)artifact).setCacheResolver(this.cache)

            artifact.targets.all { Object tObj ->
                RemoteTarget target = this.targets.resolve(tObj)
                project.tasks.register("deploy${artifact.name.capitalize()}${target.name.capitalize()}".toString(), ArtifactDeployTask) { ArtifactDeployTask task ->
                    task.artifact = artifact
                    task.target = target

                    task.dependsOn({ project.tasks.withType(TargetDiscoveryTask).matching { TargetDiscoveryTask t -> t.target == target }} as Callable<TaskCollection> )
                    task.dependsOn(artifact.dependencies)
                }
            }
        }

        def deployTask = project.tasks.register("deploy") { Task task ->
            task.group = "EmbeddedTools"
            task.description = "Deploy all artifacts on all targets"
            task.dependsOn({ project.tasks.withType(ArtifactDeployTask).matching { ArtifactDeployTask t -> !t.artifact.explicit }} as Callable<TaskCollection> )
//            project.tasks.withType(ArtifactDeployTask).all { ArtifactDeployTask task2 ->
//                task.dependsOn(task2)
//            }
        }
    }

    def targets(final Action<NamedDomainObjectCollection<? extends RemoteTarget>> action) {
        action.execute(targets)
    }

    def artifacts(final Action<NamedDomainObjectCollection<? extends Artifact>> action) {
        action.execute(artifacts)
    }

    def cache(final Action<NamedDomainObjectCollection<? extends CacheMethod>> action) {
        action.execute(cache)
    }
}
