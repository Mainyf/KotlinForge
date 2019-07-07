package io.github.mainyf.bundle

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import java.nio.file.Files

class CreateATFileTask extends DefaultTask {

    @TaskAction
    def taskAction() {
        def bundleInfo = project.extensions.getByName("bundle") as BundlePluginExtension
        def modName = bundleInfo.modName
        if (isBlank(modName)) {
            throw new IllegalArgumentException("modName cannot empty")
        }
        def atFileName = bundleInfo.getAtFileName()
        def metaInfoDir = project.file("src/main/resources/META-INF").toPath()
        if (Files.notExists(metaInfoDir)) {
            Files.createDirectories(metaInfoDir)
        }
        def atFile = metaInfoDir.resolve(atFileName)
        if (Files.notExists(atFile)) {
            Files.createFile(atFile)
            atFile.write("# public net.minecraft.client.Minecraft *")
            println("${atFileName} create successful.")
        } else {
            println("${atFileName} already exists.")
        }
    }

    private static boolean isBlank(String str) {
        int strLen
        if (str == null || (strLen = str.length()) == 0) {
            return true
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false
            }
        }
        return true
    }

}
