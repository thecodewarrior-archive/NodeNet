package com.thecodewarrior.nodenet.common.node

import com.thecodewarrior.nodenet.client.render.node.NodeClient
import com.thecodewarrior.nodenet.common.entity.EntityNode
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.registries.IForgeRegistry
import net.minecraftforge.registries.IForgeRegistryEntry

abstract class NodeType: IForgeRegistryEntry.Impl<NodeType>() {
    abstract val positioningInset: Double
    @SideOnly(Side.CLIENT)
    abstract fun createClient(node: Node<*>): NodeClient
    abstract fun createNode(entity: EntityNode): Node<*>

    companion object {
        @JvmStatic
        lateinit var REGISTRY: IForgeRegistry<NodeType>
    }
}

