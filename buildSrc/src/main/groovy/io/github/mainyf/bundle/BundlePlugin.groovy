package io.github.mainyf.bundle

import net.minecraftforge.gradle.delayed.DelayedFile
import net.minecraftforge.gradle.tasks.user.SourceCopyTask
import net.minecraftforge.gradle.user.UserConstants
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.internal.plugins.DslObject
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.SourceSet
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

class BundlePlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.extensions.add("bundle", BundlePluginExtension)

        project.task("createATFile", type: CreateATFileTask) {
            group = "bundle"
            description = "create access transform file"
        }
        project.afterEvaluate { afterEvaluate(project) }

    }

    private static void afterEvaluate(Project project) {
        makeKotlinSourceTask(project)
    }

    private static void makeKotlinSourceTask(Project project) {
        JavaPluginConvention javaConv = (JavaPluginConvention) project.getConvention().getPlugins().get("java")
        SourceSet main = javaConv.getSourceSets().getByName(SourceSet.MAIN_SOURCE_SET_NAME)
        if (project.getPlugins().hasPlugin("kotlin")) {
            KotlinSourceSet set = (KotlinSourceSet) new DslObject(main).getConvention().getPlugins().get("kotlin")
            DelayedFile dir = new DelayedFile(project, UserConstants.SOURCES_DIR + "/kotlin")

            SourceCopyTask task = project.getTasks().create("sourceMainKotlin", SourceCopyTask.class)
            task.setSource(set.getKotlin())
            task.setOutput(dir)
            task.replace(getSourceCodeVariable(project))

            KotlinCompile compile = (KotlinCompile) project.getTasks().getByName(main.getCompileTaskName("kotlin"))
            compile.dependsOn("sourceMainKotlin")

            compile.setSource(new File(dir.toString()))

        }
    }

    static Map<String, Object> getSourceCodeVariable(Project project) {
        def variable = getVariable(project)
        def result = new HashMap<String, Object>()
        for (Map.Entry<String, Object> entry : variable) {
            result.put('@' + entry.key.toUpperCase() + '@', entry.value)
        }
        return result
    }

    static Map<String, Object> getVariable(Project project) {
        def bundleInfo = project.extensions.getByName("bundle") as BundlePluginExtension
        return [
                'version'    : bundleInfo.version,
                'mcversion'  : project.minecraft.version,
                'author'     : bundleInfo.getAuthor(),
                'description': bundleInfo.description,
                'modid'      : bundleInfo.getModId(),
                'name'       : bundleInfo.modName
        ]
    }


}
