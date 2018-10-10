package jaci.gradle.toolchains

import groovy.transform.CompileStatic
import jaci.gradle.log.ETLogger
import jaci.gradle.log.ETLoggerFactory
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.internal.text.TreeFormatter
import org.gradle.language.base.internal.registry.LanguageTransformContainer
import org.gradle.model.Mutate
import org.gradle.model.RuleSource
import org.gradle.nativeplatform.NativeBinarySpec
import org.gradle.nativeplatform.platform.internal.NativePlatformInternal
import org.gradle.nativeplatform.plugins.NativeComponentPlugin
import org.gradle.nativeplatform.toolchain.GccPlatformToolChain
import org.gradle.nativeplatform.toolchain.internal.NativeToolChainInternal
import org.gradle.nativeplatform.toolchain.internal.ToolType
import org.gradle.platform.base.BinaryContainer
import org.gradle.platform.base.internal.BinarySpecInternal

@CompileStatic
class ToolchainsPlugin implements Plugin<Project> {
    void apply(Project project) {
        project.getPluginManager().apply(NativeComponentPlugin.class)
        project.extensions.create("toolchainUtil", ToolchainUtilExtension)
    }

    @CompileStatic
    static class ToolchainUtilExtension {
        void defineGccTools(GccPlatformToolChain platformToolchain, String prefix, String suffix) {
            platformToolchain.cppCompiler.executable       = prefix + platformToolchain.cppCompiler.executable + suffix
            platformToolchain.cCompiler.executable         = prefix + platformToolchain.cCompiler.executable + suffix
            platformToolchain.linker.executable            = prefix + platformToolchain.linker.executable + suffix
            platformToolchain.assembler.executable         = prefix + platformToolchain.assembler.executable + suffix
            platformToolchain.staticLibArchiver.executable = prefix + platformToolchain.staticLibArchiver.executable + suffix
        }
    }

    @CompileStatic
    static class ToolchainRules extends RuleSource {
        static final Map<String, ToolType> LANG_TOOLS_MAP = [
                "cpp" : ToolType.CPP_COMPILER,
                "c" : ToolType.C_COMPILER,
                "objcpp" : ToolType.OBJECTIVECPP_COMPILER,
                "objc" : ToolType.OBJECTIVEC_COMPILER,
                "rc" : ToolType.WINDOW_RESOURCES_COMPILER,
                "asm" : ToolType.ASSEMBLER
        ]

        static void markUnavailable(ETLogger log, NativeBinarySpec bin, String reason, boolean disable) {
            log.logErrorHead("Skipping build: $bin: $reason")
            if (disable)
                ((BinarySpecInternal)bin).setBuildable(false)
        }

        // TODO: drive this logic based on the platform (i.e. OptionalNativePlatform)
        static void configureOptional(NativeBinarySpec bin, LanguageTransformContainer langTransforms) {
            def log = ETLoggerFactory.INSTANCE.create("ToolchainRules")

            log.debug("Configuring optionals for binary: $bin")
            def tc = (NativeToolChainInternal)bin.toolChain
            def toolProvider = tc.select((NativePlatformInternal)bin.targetPlatform)
            log.debug("Tool Provider: ${toolProvider}")

            if (toolProvider.isAvailable()) {
                for (ss in bin.inputs) {
                    log.debug("Querying transforms for input: ${ss}")

                    boolean hasTransform = false
                    for (transform in langTransforms) {
                        log.debug("Querying transform ${transform}")
                        if (transform.getSourceSetType().isInstance(ss)) {
                            hasTransform = true
                            def requiresTool = LANG_TOOLS_MAP[transform.languageName]
                            log.debug("Found transform: ${transform.languageName}")
                            log.debug("Requires tool: ${requiresTool}")

                            def searchResult = toolProvider.locateTool(requiresTool)
                            if (!searchResult.isAvailable()) {
                                markUnavailable(log, bin, "Toolchain ${tc.name} cannot build ${bin.targetPlatform.name} (tool ${requiresTool} not found)", true)
                                log.info("Could not find tool: ${requiresTool}")
                                def fmt = new TreeFormatter()
                                searchResult.explain(fmt)
                                log.info(fmt.toString())
                            }
                        }
                    }
                    if (!hasTransform)
                        markUnavailable(log, bin, "Binary does not have a language transform for input ${ss}.", true)
                }
                if (bin.inputs.empty) {
                    markUnavailable(log, bin, "Binary has no inputs", true)
                }
            } else {
                // Gradle automatically disables cases where a toolchain can't be found for this platform.
                markUnavailable(log, bin, "Could not find valid toolchain for platform ${bin.targetPlatform.name}", false)
            }
        }

        @Mutate
        void configureOptionalBuildables(BinaryContainer binaries, LanguageTransformContainer languageTransforms) {
            binaries.withType(NativeBinarySpec) { NativeBinarySpec bin ->
                configureOptional(bin, languageTransforms)
            }
        }
    }
}
