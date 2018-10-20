package com.thecodewarrior.nodenet.common.node

import com.teamwizardry.librarianlib.features.utilities.client.ClientRunnable
import com.thecodewarrior.nodenet.client.render.node.NodeRenderer
import com.thecodewarrior.nodenet.common.entity.EntityNode
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.function.Supplier
import java.util.function.Function

class BasicNodeType(
    name: ResourceLocation,
    private val nodeConstructor: (EntityNode) -> Node,
    rendererConstructor: () -> (Node) -> NodeRenderer
): NodeType() {
    constructor(name: ResourceLocation,
        nodeConstructor: Function<EntityNode, Node>,
        rendererConstructor: Supplier<Function<Node, NodeRenderer>>):
        this(name,
            { nodeConstructor.apply(it) },
            { rendererConstructor.get().let { con -> { con.apply(it) } } }
        )
    init {
        this.registryName = name
        ClientRunnable.run {
            this.rendererConstructor = rendererConstructor()
        }
    }

    @SideOnly(Side.CLIENT)
    private lateinit var rendererConstructor: (Node) -> NodeRenderer

    override fun createRenderer(node: Node): NodeRenderer {
        return rendererConstructor(node)
    }

    override fun createNode(entity: EntityNode): Node {
        return nodeConstructor(entity)
    }
}