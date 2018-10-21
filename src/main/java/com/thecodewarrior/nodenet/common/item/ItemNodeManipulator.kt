package com.thecodewarrior.nodenet.common.item

import com.teamwizardry.librarianlib.core.client.ClientTickHandler
import com.teamwizardry.librarianlib.core.client.ClientTickHandler.partialTicks
import com.teamwizardry.librarianlib.features.base.item.ItemMod
import com.teamwizardry.librarianlib.features.kotlin.plus
import com.teamwizardry.librarianlib.features.kotlin.times
import com.teamwizardry.librarianlib.features.network.PacketHandler
import com.thecodewarrior.nodenet.common.node.NodeTraceResult
import com.thecodewarrior.nodenet.common.entity.EntityNode
import com.thecodewarrior.nodenet.common.network.PacketDeleteNode
import com.thecodewarrior.nodenet.common.node.NodeType
import com.thecodewarrior.nodenet.common.node.rayTraceNodes
import com.thecodewarrior.nodenet.frontOffset
import com.thecodewarrior.nodenet.snapToGrid
import net.minecraft.client.Minecraft
import net.minecraft.util.math.RayTraceResult

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
            val player = Minecraft.getMinecraft().player
            val eyes = player.getPositionEyes(partialTicks)
            val range = eyes.distanceTo(node.hit.hitVec)
            val tip = eyes + player.getLook(partialTicks) * range
            val rtr = player.world.rayTraceBlocks(eyes, tip)
            if(rtr?.typeOfHit != RayTraceResult.Type.BLOCK) {
                draggingNode = node.entity
            }
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
            val nodeTrace = player.rayTraceNodes(partialTicks, listOf(draggingNode))
            val rtr = nodeTrace?.hit ?: player.world.rayTraceBlocks(eyes, tip)
            var targetPos = tip.snapToGrid(1/16.0)
            if(rtr != null) {
                val offset = NodeType.REGISTRY.getValue(draggingNode.type)?.positioningInset ?: 0.0
                targetPos = rtr.hitVec.snapToGrid(1/16.0)
                targetPos += rtr.sideHit.frontOffset * offset
            }
            draggingNode.setPosition(targetPos.x, targetPos.y, targetPos.z)
        }
    }
}