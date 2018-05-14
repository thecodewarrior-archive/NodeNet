package com.thecodewarrior.nodenet

import com.teamwizardry.librarianlib.features.base.ModCreativeTab
import com.teamwizardry.librarianlib.features.utilities.LoggerBase
import com.thecodewarrior.nodenet.common.CommonProxy
import com.thecodewarrior.nodenet.common.item.ModItems
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent

@Mod(modid = NodeNet.MODID, version = NodeNet.VERSION, name = NodeNet.MODNAME, dependencies = NodeNet.DEPENDENCIES,
        modLanguageAdapter = NodeNet.ADAPTER, acceptedMinecraftVersions = NodeNet.ALLOWED)
object NodeNet {

    @Mod.EventHandler
    fun preInit(e: FMLPreInitializationEvent) {
        PROXY.pre(e)
    }

    @Mod.EventHandler
    fun init(e: FMLInitializationEvent) {
        PROXY.init(e)
    }

    @Mod.EventHandler
    fun postInit(e: FMLPostInitializationEvent) {
        PROXY.post(e)
    }

    const val MODID = "nodenet"
    const val MODNAME = "NodeNet"
    const val MAJOR = "GRADLE:VERSION"
    const val MINOR = "GRADLE:BUILD"
    const val VERSION = "$MAJOR.$MINOR"
    const val ALLOWED = "[1.12,)"
    const val CLIENT = "com.thecodewarrior.nodenet.client.ClientProxy"
    const val SERVER = "com.thecodewarrior.nodenet.common.CommonProxy"
    const val DEPENDENCIES = "required-after:librarianlib;required-after:forge@[13.19.1.2195,)"
    const val ADAPTER = "net.shadowfacts.forgelin.KotlinAdapter"

    @SidedProxy(clientSide = CLIENT, serverSide = SERVER)
    lateinit var PROXY: CommonProxy

    val creativeTab = object : ModCreativeTab() {
        override val iconStack: ItemStack
            get() = ItemStack(ModItems.manipulator)

        init {
            this.registerDefaultTab()
        }
    }
}

object NodeLog : LoggerBase("NodeNet")
