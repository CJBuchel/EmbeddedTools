package cj.gradle.deploy.artifact

import groovy.transform.CompileStatic
import cj.gradle.Resolver
import cj.gradle.deploy.cache.CacheMethod
import cj.gradle.deploy.context.DeployContext
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.Property

import javax.inject.Inject

@CompileStatic
class FileCollectionArtifact extends AbstractArtifact implements CacheableArtifact {

    @Inject
    FileCollectionArtifact(String name, Project project) {
        super(name, project)
        files = project.objects.property(FileCollection.class)
    }

    final Property<FileCollection> files

    void setFiles(FileCollection collection) {
        this.files.set(collection)
    }

    Object cache = "md5sum"
    Resolver<CacheMethod> cacheResolver

    @Override
    void deploy(DeployContext context) {
        if (files.isPresent())
            context.put(files.get().files, cacheResolver?.resolve(cache))
        else
            context.logger?.log("No file(s) provided for ${toString()}")
    }
}
