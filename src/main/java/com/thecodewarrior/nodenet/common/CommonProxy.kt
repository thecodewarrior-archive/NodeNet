package com.thecodewarrior.nodenet.common

import com.teamwizardry.librarianlib.features.kotlin.toRl
import com.thecodewarrior.nodenet.NodeNet
import com.thecodewarrior.nodenet.common.entity.EntityNodeBase
import com.thecodewarrior.nodenet.common.item.ModItems
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.registry.EntityRegistry

open class CommonProxy {
    open fun pre(event: FMLPreInitializationEvent) {
        ModItems
        EntityRegistry.registerModEntity("nodenet:node".toRl(), EntityNodeBase::class.java, "node", 0,
                NodeNet, 32, Integer.MAX_VALUE, false)
    }

    open fun init(event: FMLInitializationEvent) {
    }

    open fun post(event: FMLPostInitializationEvent) {
    }
}