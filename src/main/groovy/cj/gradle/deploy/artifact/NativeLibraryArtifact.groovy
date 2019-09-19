package cj.gradle.deploy.artifact

import groovy.transform.CompileStatic
import cj.gradle.deploy.context.DeployContext
import cj.gradle.nativedeps.DependencySpecExtension
import cj.gradle.nativedeps.ETNativeDepSet
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.file.FileCollection

import javax.inject.Inject

@CompileStatic
class NativeLibraryArtifact extends FileCollectionArtifact {

    @Inject
    NativeLibraryArtifact(String name, Project project) {
        super(name, project)
        library = name
    }

    String library = null
    String targetPlatform = null
    String flavor = null
    String buildType = null

    @Override
    void deploy(DeployContext ctx) {
        def sets = project.getExtensions().getByType(DependencySpecExtension).sets

        def candidates = sets.findAll { ETNativeDepSet set ->
            set.name.equals(library) && set.appliesTo(getFlavor(), getBuildType(), getTargetPlatform())
        } as List<ETNativeDepSet>

        if (candidates.empty)
            throw new GradleException("${toString()} cannot find suitable dependency for library ${library}, " +
                    "platform ${getTargetPlatform()}, flavor ${getFlavor()}, buildType ${getBuildType()}")

        files.set(candidates.collect { it.getRuntimeFiles() }.inject { a,b -> a + b } as FileCollection)

        super.deploy(ctx)
    }
}
