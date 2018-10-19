package com.thecodewarrior.nodenet.common.item

import com.teamwizardry.librarianlib.core.client.ClientTickHandler.partialTicks
import com.teamwizardry.librarianlib.features.base.item.ItemMod
import com.teamwizardry.librarianlib.features.kotlin.plus
import com.teamwizardry.librarianlib.features.kotlin.times
import com.thecodewarrior.nodenet.client.NodeTraceResult
import com.thecodewarrior.nodenet.common.entity.EntityNode
import com.thecodewarrior.nodenet.frontOffset
import net.minecraft.client.Minecraft

class ItemNodeManipulator: ItemMod("manipulator"), INodeInteractingItem {
    var draggingNode: EntityNode? = null

    override fun leftClickBegan(node: NodeTraceResult?) {
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
            val targetPos = rtr?.let { it.hitVec + it.sideHit.frontOffset * -0.0625 } ?: tip
            draggingNode.setPosition(targetPos.x, targetPos.y, targetPos.z)
        }
    }
}