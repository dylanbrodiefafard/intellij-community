package com.jetbrains.packagesearch.intellij.plugin.extensibility

import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.jetbrains.packagesearch.intellij.plugin.extensions.gradle.GradleModuleTransformer
import com.jetbrains.packagesearch.intellij.plugin.extensions.maven.MavenModuleTransformer
import kotlin.streams.toList

/**
 * Extension point used to register [Module]s transformations to [ProjectModule]s.
 * See [GradleModuleTransformer] or [MavenModuleTransformer] for examples.
 */
interface ModuleTransformer {

    companion object {

        private val extensionPointName: ExtensionPointName<ModuleTransformer> =
            ExtensionPointName.create("com.intellij.packagesearch.moduleTransformer")

        fun getAllModuleTransformersFor(project: Project): List<ModuleTransformer> =
            extensionPointName.extensions(project).toList()
    }

    /**
     * Transforms [nativeModules] in a [ProjectModule] module if possible, else returns an empty list.
     * It's implementation should use the IntelliJ platform APIs for a given build system (eg.
     * Gradle or Maven), detect if and which [nativeModules] are controlled by said build system
     * and transform them accordingly.
     *
     * NOTE: some [Module]s in [nativeModules] may be already disposed or about to be. Be sure to
     * handle any exception and filter out the ones not working.
     *
     * @param nativeModules The native [Module]s that will be transformed.
     * @return [ProjectModule]s wrapping [nativeModules] or an empty list.
     */
    fun transformModules(nativeModules: List<Module>): List<ProjectModule>

    /**
     * Transforms [nativeModules] in a [ProjectModule] module if possible, else returns an empty list.
     * It's implementation should use the IntelliJ platform APIs for a given build system (eg.
     * Gradle or Maven), detect if and which [nativeModules] are controlled by said build system
     * and transform them accordingly.
     *
     * NOTE: some [Module]s in [nativeModules] may be already disposed or about to be. Be sure to
     * handle any exception and filter out the ones not working.
     *
     * @param nativeModules The native [Module]s that will be transformed.
     * @return [ProjectModule]s wrapping [nativeModules] or an empty list.
     */
    fun transformModules(nativeModules: Array<Module>): List<ProjectModule> =
        transformModules(nativeModules.toList())

}
