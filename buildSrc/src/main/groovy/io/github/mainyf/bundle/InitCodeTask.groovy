package io.github.mainyf.bundle


import org.gradle.api.DefaultTask
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.TaskAction

import java.nio.file.Files
import java.nio.file.Path


class InitCodeTask extends DefaultTask {

    @TaskAction
    def taskAction() {
        def bundleInfo = project.extensions.getByName("bundle") as BundlePluginExtension
        def javaConv = (JavaPluginConvention) project.getConvention().getPlugins().get("java")
        def folder = project.file("${bundleInfo.kotlinSrcDirs}/${bundleInfo.getFullFilePath()}").toPath()
        if (Files.notExists(folder)) {
            Files.createDirectories(folder)
        }
        makeResource(javaConv)
        makeMainClassFile(bundleInfo, folder)
        makeReferencesClassFile(bundleInfo, folder)
        makeCommonProxyClassFile(bundleInfo, folder)
        makeClientProxyClassFile(bundleInfo, folder)
    }

    private void makeMainClassFile(BundlePluginExtension bundleInfo, Path folder) {
        def mainClassName = bundleInfo.getMainClass()
        def mainClass = folder.resolve("${mainClassName}.kt")
        if(Files.notExists(mainClass)) {
            Files.createFile(mainClass)
            mainClass.write("package ${bundleInfo.fullPackage}\n" +
                    "\n" +
                    "import ${bundleInfo.fullPackage}.common.CommonProxy\n" +
                    "import cpw.mods.fml.common.Mod\n" +
                    "import cpw.mods.fml.common.Mod.EventHandler\n" +
                    "import cpw.mods.fml.common.SidedProxy\n" +
                    "import cpw.mods.fml.common.event.FMLInitializationEvent\n" +
                    "import cpw.mods.fml.common.event.FMLPostInitializationEvent\n" +
                    "import cpw.mods.fml.common.event.FMLPreInitializationEvent\n" +
                    "import cpw.mods.fml.common.event.FMLServerStartingEvent\n" +
                    "\n" +
                    "@Mod(modid = References.MODID, version = References.VERSION)\n" +
                    "class ${mainClassName} {\n" +
                    "\n" +
                    "    companion object {\n" +
                    "\n" +
                    "        @Mod.Instance\n" +
                    "        var INSTANCE: ${mainClassName}? = null\n" +
                    "\n" +
                    "        @SidedProxy(clientSide = \"${bundleInfo.fullPackage}.client.ClientProxy\", serverSide = \"${bundleInfo.fullPackage}.common.CommonProxy\")\n" +
                    "        var proxy: CommonProxy? = null\n" +
                    "    }\n" +
                    "\n" +
                    "    @EventHandler\n" +
                    "    fun preInit(event: FMLPreInitializationEvent) {\n" +
                    "        proxy?.preInit(event)\n" +
                    "    }\n" +
                    "\n" +
                    "    @EventHandler\n" +
                    "    fun init(event: FMLInitializationEvent) {\n" +
                    "        proxy?.init(event)\n" +
                    "    }\n" +
                    "\n" +
                    "    @EventHandler\n" +
                    "    fun init(event: FMLPostInitializationEvent) {\n" +
                    "        proxy?.init(event)\n" +
                    "    }\n" +
                    "\n" +
                    "    @EventHandler\n" +
                    "    fun onServerStart(event: FMLServerStartingEvent) {\n" +
                    "        proxy?.onServerStart(event)\n" +
                    "    }\n" +
                    "\n" +
                    "}")
        }
    }

    private void makeReferencesClassFile(BundlePluginExtension bundleInfo, Path folder) {
        def referencesClass = folder.resolve("References.kt")
        if(Files.notExists(referencesClass)) {
            Files.createFile(referencesClass)
            referencesClass.write("package ${bundleInfo.fullPackage}\n" +
                    "\n" +
                    "object References {\n" +
                    "\n" +
                    "    const val MOD_NAME = \"@NAME@\"\n" +
                    "    const val MODID = \"@MODID@\"\n" +
                    "    const val VERSION = \"@VERSION@\"\n" +
                    "    const val MC_VERSION = \"@MCVERSION@\"\n" +
                    "    const val author = \"@AUTHOR@\"\n" +
                    "\n" +
                    "}")
        }
    }

    private void makeCommonProxyClassFile(BundlePluginExtension bundleInfo, Path folder) {
        def commonClassName = "CommonProxy"
        def commonClass = folder.resolve("common${File.separator}${commonClassName}.kt")
        if(Files.notExists(commonClass.parent)) {
            Files.createDirectories(commonClass.parent)
        }
        if(Files.notExists(commonClass)) {
            Files.createFile(commonClass)
            commonClass.write("package ${bundleInfo.fullPackage}.common\n" +
                    "\n" +
                    "import cpw.mods.fml.common.event.*\n" +
                    "\n" +
                    "open class CommonProxy {\n" +
                    "\n" +
                    "    fun preInit(event: FMLPreInitializationEvent) {\n" +
                    "\n" +
                    "    }\n" +
                    "\n" +
                    "    fun init(event: FMLInitializationEvent) {\n" +
                    "\n" +
                    "    }\n" +
                    "\n" +
                    "    fun init(event: FMLPostInitializationEvent) {\n" +
                    "\n" +
                    "    }\n" +
                    "\n" +
                    "    fun onServerStart(event: FMLServerStartingEvent) {\n" +
                    "\n" +
                    "    }\n" +
                    "\n" +
                    "}")
        }
    }

    private void makeClientProxyClassFile(BundlePluginExtension bundleInfo, Path folder) {
        def clientClassName = "ClientProxy"
        def clientClass = folder.resolve("client${File.separator}${clientClassName}.kt")
        if(Files.notExists(clientClass.parent)) {
            Files.createDirectories(clientClass.parent)
        }
        if(Files.notExists(clientClass)) {
            Files.createFile(clientClass)
            clientClass.write("package ${bundleInfo.fullPackage}.client\n" +
                    "\n" +
                    "import ${bundleInfo.fullPackage}.common.CommonProxy\n" +
                    "\n" +
                    "class ClientProxy : CommonProxy() {\n" +
                    "\n" +
                    "}")
        }

    }

    private void makeResource(JavaPluginConvention javaConv) {
        def resource = project.file((javaConv.sourceSets.main.resources.source as String[]).first()).toPath()
        if(Files.notExists(resource)) {
            Files.createDirectories(resource)
        }
        def mcMetadata = resource.resolve("mcmod.info")
        if(Files.notExists(mcMetadata)) {
            Files.createFile(mcMetadata)
            mcMetadata.write("[\n" +
                    "    {\n" +
                    "      \"modid\": \"\${modid}\",\n" +
                    "      \"name\": \"\${name}\",\n" +
                    "      \"description\": \"\${description}\",\n" +
                    "      \"version\": \"\${version}\",\n" +
                    "      \"mcversion\": \"\${mcversion}\",\n" +
                    "      \"url\": \"\",\n" +
                    "      \"updateUrl\": \"\",\n" +
                    "      \"authorList\": [\${author}],\n" +
                    "      \"credits\": \"\",\n" +
                    "      \"logoFile\": \"\",\n" +
                    "      \"screenshots\": [],\n" +
                    "      \"dependencies\": []\n" +
                    "    }\n" +
                    "]")
        }
    }

}
