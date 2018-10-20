package com.thecodewarrior.nodenet.common

import com.teamwizardry.librarianlib.features.kotlin.toRl
import com.teamwizardry.librarianlib.features.network.PacketHandler
import com.thecodewarrior.nodenet.NodeNet
import com.thecodewarrior.nodenet.client.render.nodes.MissingTypeRenderer
import com.thecodewarrior.nodenet.client.render.nodes.ParticleGeneratorRenderer
import com.thecodewarrior.nodenet.client.render.nodes.RedstoneReaderRenderer
import com.thecodewarrior.nodenet.common.entity.EntityNode
import com.thecodewarrior.nodenet.common.item.ModItems
import com.thecodewarrior.nodenet.common.network.PacketConnectNodes
import com.thecodewarrior.nodenet.common.network.PacketDeleteNode
import com.thecodewarrior.nodenet.common.network.PacketMoveNode
import com.thecodewarrior.nodenet.common.network.PacketRotateNode
import com.thecodewarrior.nodenet.common.node.BasicNodeType
import com.thecodewarrior.nodenet.common.node.NodeType
import com.thecodewarrior.nodenet.common.nodes.MissingTypeNode
import com.thecodewarrior.nodenet.common.nodes.ParticleGeneratorNode
import com.thecodewarrior.nodenet.common.nodes.RedstoneReaderNode
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.registry.EntityRegistry
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.registries.RegistryBuilder

open class CommonProxy {
    init { MinecraftForge.EVENT_BUS.register(this) }
    open fun pre(event: FMLPreInitializationEvent) {
        ModItems
        EntityRegistry.registerModEntity("nodenet:node".toRl(), EntityNode::class.java, "node", 0,
                NodeNet, 256, Integer.MAX_VALUE, false)
        network()
        NodeType.REGISTRY = RegistryBuilder<NodeType>()
            .setType(NodeType::class.java)
            .setMaxID(256)
            .setName("nodenet:node_type".toRl())
            .setDefaultKey("missingno".toRl())
            .create()
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

    @Suppress("MoveLambdaOutsideParentheses")
    @SubscribeEvent
    fun registerRenderers(e: RegistryEvent.Register<NodeType>) {
        NodeType.REGISTRY.registerAll(
            BasicNodeType("missingno".toRl(), ::MissingTypeNode, { ::MissingTypeRenderer }),
            BasicNodeType("nodenet:redstone_reader".toRl(), ::RedstoneReaderNode, { ::RedstoneReaderRenderer }),
            BasicNodeType("nodenet:particle_generator".toRl(), ::ParticleGeneratorNode, { ::ParticleGeneratorRenderer })
        )
    }
}