package io.github.mainyf.bundle

import groovy.json.internal.Charsets
import org.gradle.api.JavaVersion

class BundlePluginExtension {

    String group = "io.github.mainyf"
    String modName = "Mod Name"
    String version = "1.0"
    List<String> author = Arrays.asList("Mainyf")
    String description = ""
    String javaVersuon = JavaVersion.VERSION_1_8
    String encoding = Charsets.UTF_8.toString()
    String kotlinSrcDirs = "src/main/kotlin"
    String javaSrcDirs = "src/main/java"

    String getAuthor() {
        return "\"${author.join("\",\"")}\""
    }

    String getModId() {
        return modName.replace(" ", "").toLowerCase()
    }

    String getAtFileName() {
        return "${getModId()}_at.cfg"
    }

}
