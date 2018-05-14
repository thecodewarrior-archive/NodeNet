package com.thecodewarrior.nodenet.common

import com.teamwizardry.librarianlib.features.kotlin.toRl
import com.teamwizardry.librarianlib.features.network.PacketHandler
import com.thecodewarrior.nodenet.NodeNet
import com.thecodewarrior.nodenet.common.entity.EntityNode
import com.thecodewarrior.nodenet.common.item.ModItems
import com.thecodewarrior.nodenet.common.network.PacketConnectNodes
import com.thecodewarrior.nodenet.common.network.PacketDeleteNode
import com.thecodewarrior.nodenet.common.network.PacketMoveNode
import com.thecodewarrior.nodenet.common.network.PacketRotateNode
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.registry.EntityRegistry
import net.minecraftforge.fml.relauncher.Side

open class CommonProxy {
    open fun pre(event: FMLPreInitializationEvent) {
        ModItems
        EntityRegistry.registerModEntity("nodenet:node".toRl(), EntityNode::class.java, "node", 0,
                NodeNet, 256, Integer.MAX_VALUE, false)
        network()
    }

    open fun init(event: FMLInitializationEvent) {
    }

    open fun post(event: FMLPostInitializationEvent) {
    }

    private fun network() {
        PacketHandler.register(PacketConnectNodes::class.java, Side.SERVER)
        PacketHandler.register(PacketDeleteNode::class.java, Side.SERVER)
        PacketHandler.register(PacketMoveNode::class.java, Side.SERVER)
        PacketHandler.register(PacketRotateNode::class.java, Side.SERVER)
    }
}