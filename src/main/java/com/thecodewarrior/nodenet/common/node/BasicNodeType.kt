package com.thecodewarrior.nodenet.common.node

import com.teamwizardry.librarianlib.features.utilities.client.ClientRunnable
import com.thecodewarrior.nodenet.client.render.node.NodeClient
import com.thecodewarrior.nodenet.common.entity.EntityNode
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.function.Supplier
import java.util.function.Function

class BasicNodeType(
    name: ResourceLocation,
    override val positioningInset: Double,
    private val nodeConstructor: (EntityNode) -> Node<*>,
    clientConstructor: () -> (Node<*>) -> NodeClient
): NodeType() {
    constructor(name: ResourceLocation,
        positioningInset: Double,
        nodeConstructor: Function<EntityNode, Node<*>>,
        clientConstructor: Supplier<Function<Node<*>, NodeClient>>):
        this(name,
            positioningInset,
            { nodeConstructor.apply(it) },
            { clientConstructor.get().let { con -> { con.apply(it) } } }
        )
    init {
        this.registryName = name
        ClientRunnable.run {
            this.clientConstructor = clientConstructor()
        }
    }

    @SideOnly(Side.CLIENT)
    private lateinit var clientConstructor: (Node<*>) -> NodeClient

    override fun createClient(node: Node<*>): NodeClient {
        return clientConstructor(node)
    }

    override fun createNode(entity: EntityNode): Node<*> {
        return nodeConstructor(entity)
    }
}