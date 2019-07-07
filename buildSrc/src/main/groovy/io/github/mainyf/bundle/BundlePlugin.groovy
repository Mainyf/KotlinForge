package io.github.mainyf.bundle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.compile.JavaCompile
import org.apache.tools.ant.filters.ReplaceTokens

class BundlePlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.extensions.add("bundle", BundlePluginExtension)

        project.task("createATFile", type: CreateATFileTask) {
            group = "bundle"
            description = "create access transform file"
        }

//        task sourcesForRelease(type: Copy) {
//            from 'src/main/kotlin'
//            into 'build/adjustedSrc'
//            filter(ReplaceTokens, tokens: [VERSION: '2.3.1'])
//        }
//
//        task compileForRelease(type: JavaCompile, dependsOn: sourcesForRelease) {
//            source = sourcesForRelease.destinationDir
//            classpath = sourceSets.main.compileClasspath
//            destinationDir = file('build/adjustedClasses')
//        }

//        project.afterEvaluate {

//            def bundleInfo = project.extensions.getByName("bundle") as BundlePluginExtension
//
//            def sourcesForRelease = project.task('sourcesForRelease', Copy) {
//                from bundleInfo.javaSrcDirs, bundleInfo.kotlinSrcDirs
//                into 'build/sourceTmp'
//                filter(ReplaceTokens, tokens: [VERSION: '2.3.1'])
//            }
//
//            project.task('compileForRelease', JavaCompile, dependsOn: sourcesForRelease) {
//                source = sourcesForRelease.destinationDir
//                classpath = sourceSets.main.compileClasspath
//                destinationDir = file('build/adjustedClasses')
//            }
//        }

//        project.afterEvaluate {
//
//            def bundleInfo = project.extensions.getByName("bundle") as BundlePluginExtension
//
//            def replaceInfo = project.task('replaceInfo', type: Copy) {
//                filter { line ->
//                    return line.replaceAll('@VERSION@', bundleInfo.version)
//                }
//            }
//
//            project.tasks.getByName("compileJava").doFirst {
//
//            }
//
//            project.tasks.getByName("compileJava").dependsOn.add(replaceInfo)
//
//        }
    }

}
