package com.example.examplemod

import net.minecraft.init.Blocks
import cpw.mods.fml.common.Mod
import cpw.mods.fml.common.Mod.EventHandler
import cpw.mods.fml.common.event.FMLInitializationEvent

@Mod(modid = ExampleMod.MODID, version = ExampleMod.VERSION)
class ExampleMod {

    companion object {
        const val MODID = "examplemod"
        const val VERSION = "@version@"
    }

    @EventHandler
    fun init(event: FMLInitializationEvent) {
        // some example code
        println("DIRT BLOCK >> " + Blocks.dirt.unlocalizedName)
    }

}
