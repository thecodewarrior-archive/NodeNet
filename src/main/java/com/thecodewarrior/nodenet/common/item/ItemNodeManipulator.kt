package com.thecodewarrior.nodenet.common.item

import com.teamwizardry.librarianlib.core.client.ClientTickHandler
import com.teamwizardry.librarianlib.core.client.ClientTickHandler.partialTicks
import com.teamwizardry.librarianlib.features.base.item.ItemMod
import com.teamwizardry.librarianlib.features.kotlin.plus
import com.teamwizardry.librarianlib.features.kotlin.times
import com.teamwizardry.librarianlib.features.network.PacketHandler
import com.thecodewarrior.nodenet.client.NodeTraceResult
import com.thecodewarrior.nodenet.common.entity.EntityNode
import com.thecodewarrior.nodenet.common.network.PacketDeleteNode
import com.thecodewarrior.nodenet.common.node.NodeType
import com.thecodewarrior.nodenet.frontOffset
import com.thecodewarrior.nodenet.snapToGrid
import net.minecraft.client.Minecraft

class ItemNodeManipulator: ItemMod("manipulator"), INodeInteractingItem {
    var draggingNode: EntityNode? = null
    var lastLeftClick = 0

    override fun leftClickBegan(node: NodeTraceResult?) {
        if(node != null) {
            if (ClientTickHandler.ticks <= lastLeftClick + 5) {
                PacketHandler.NETWORK.sendToServer(PacketDeleteNode(node.entity.entityId))
            }
            lastLeftClick = ClientTickHandler.ticks
        }
    }

    override fun rightClickBegan(node: NodeTraceResult?) {
        if(node != null) {
            draggingNode = node.entity
        }
    }

    override fun rightClickEnded(node: NodeTraceResult?) {
        draggingNode = null
    }

    override fun rightClickTick(node: NodeTraceResult?) {
        val draggingNode = draggingNode
        val player = Minecraft.getMinecraft().player
        if(draggingNode != null) {
            val eyes = player.getPositionEyes(partialTicks)
            val tip = eyes + player.getLook(partialTicks) * 5
            val rtr = player.world.rayTraceBlocks(eyes, tip)
            val offset = NodeType.REGISTRY.getValue(draggingNode.type)?.positioningInset ?: 0.0
            val targetPos = rtr?.let { it.hitVec.snapToGrid(1/16.0) + it.sideHit.frontOffset * offset } ?: tip
            draggingNode.setPosition(targetPos.x, targetPos.y, targetPos.z)
        }
    }
}