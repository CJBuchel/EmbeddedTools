package cj.gradle.deploy.artifact

import groovy.transform.CompileStatic
import cj.gradle.Resolver
import cj.gradle.deploy.cache.CacheMethod
import cj.gradle.deploy.context.DeployContext
import org.gradle.api.Project
import org.gradle.api.provider.Property

import javax.inject.Inject

@CompileStatic
class FileArtifact extends AbstractArtifact implements CacheableArtifact {

    @Inject
    FileArtifact(String name, Project project) {
        super(name, project)
        file = project.objects.property(File.class)
    }

    final Property<File> file

    void setFile(File file) {
        this.file.set(file)
    }

    String filename = null

    Object cache = "md5sum"

    Resolver<CacheMethod> cacheResolver

    @Override
    void deploy(DeployContext context) {
        if (file.isPresent()) {
            File f = file.get()
            context.put(f, (filename == null ? f.name : filename), cacheResolver?.resolve(cache))
        } else
            context.logger?.log("No file provided for ${toString()}")
    }

}
