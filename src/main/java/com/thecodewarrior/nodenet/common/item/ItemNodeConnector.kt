package com.thecodewarrior.nodenet.common.item

import com.teamwizardry.librarianlib.features.base.item.ItemMod
import com.teamwizardry.librarianlib.features.network.PacketHandler
import com.thecodewarrior.nodenet.common.entity.EntityNode
import com.thecodewarrior.nodenet.common.network.PacketConnectNodes
import net.minecraft.client.Minecraft
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumHand
import net.minecraft.world.World
import java.util.*

class ItemNodeConnector: ItemMod("connector"), INodeInteractingItem {
    var connectingFromNode: Int? = null

    override fun rightClickBegan(node: EntityNode?) {
        connectingFromNode = node?.entityId
    }

    override fun rightClickEnded(node: EntityNode?) {
        val from = connectingFromNode
        connectingFromNode = null

        if(from != null && node != null && from != node.entityId) {
            val packet = PacketConnectNodes(from, node.entityId)
            packet.handle(Minecraft.getMinecraft().player)
            PacketHandler.NETWORK.sendToServer(packet)
        }
    }
}