package io.github.mainyf.bundle

import net.minecraftforge.gradle.common.BaseExtension
import net.minecraftforge.gradle.delayed.DelayedFile
import net.minecraftforge.gradle.tasks.user.SourceCopyTask
import net.minecraftforge.gradle.user.UserConstants
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.internal.plugins.DslObject
import org.gradle.api.plugins.BasePluginConvention
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.language.jvm.tasks.ProcessResources
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

        project.task("initCode", type: InitCodeTask) {
            group = "bundle"
            description = "init code"
        }

        project.afterEvaluate { afterEvaluate(project) }

    }

    private static void afterEvaluate(Project project) {
        def javaConv = (JavaPluginConvention) project.getConvention().getPlugins().get("java")
        def sourceSets = javaConv.getSourceSets()
        def bundleInfo = project.extensions.getByName("bundle") as BundlePluginExtension

        sourceSets.main.kotlin.srcDirs = [bundleInfo.kotlinSrcDirs]
        sourceSets.main.java.srcDirs = [bundleInfo.javaSrcDirs]

        project.configurations.create("embed")
        project.dependencies.add(bundleInfo.hasIncludeKtRuntime ? "embed" : "compile", "org.jetbrains.kotlin:kotlin-stdlib-jdk8:+")
        def embed = project.configurations.getByName("embed")
        project.configurations.getByName("compile").extendsFrom(embed)

        project.tasks.withType(Jar) {
            if (project.file(bundleInfo.getAtFileName()).exists()) {
                println("access transform exists, include to manifest file.")
                manifest {
                    attributes 'FMLAT': bundleInfo.getAtFileName()
                }
            }
            from project.configurations.getByName("embed").collect { it.isDirectory() ? it : project.zipTree(it) }
        }

        makeJavaCompile(project, javaConv, bundleInfo)
        makeProcessResources(project, javaConv)
        makeKotlinSourceTask(project, javaConv)
    }

    private static void makeJavaCompile(Project project, JavaPluginConvention javaConv, BundlePluginExtension bundleInfo) {
        javaConv.sourceCompatibility = javaConv.targetCompatibility = bundleInfo.javaVersuon
        project.tasks.withType(JavaCompile) {
            sourceCompatibility = targetCompatibility = bundleInfo.javaVersuon
            options.encoding = bundleInfo.encoding
            options.compilerArgs << "-Xlint:unchecked" << "-Xlint:deprecation"
        }
    }

    private static void makeProcessResources(Project project, JavaPluginConvention javaConv) {
        project.tasks.withType(ProcessResources) {
            def sourceSets = javaConv.getSourceSets()
//            inputs.property "version", bundleInfo.version
//            inputs.property "mcversion", project.minecraft.version

            from(sourceSets.main.resources.srcDirs) {
                include 'mcmodTmp.info'
                expand getVariable(project)
            }

            from(sourceSets.main.resources.srcDirs) {
                exclude 'mcmodTmp.info'
            }
        }
    }

    private static void makeKotlinSourceTask(Project project, JavaPluginConvention javaConv) {
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
        def minecraftInfo = project.extensions.getByName("minecraft") as BaseExtension
        return [
                'version'    : bundleInfo.version,
                'mcversion'  : minecraftInfo.version,
                'author'     : bundleInfo.getAuthor(),
                'authors'     : bundleInfo.getAuthors(),
                'description': bundleInfo.description,
                'modid'      : bundleInfo.getModId(),
                'name'       : bundleInfo.modName
        ]
    }


}
