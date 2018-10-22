package com.thecodewarrior.nodenet.common.item

import com.teamwizardry.librarianlib.features.base.item.ItemMod
import com.teamwizardry.librarianlib.features.network.PacketHandler
import com.thecodewarrior.nodenet.common.network.PacketConfigConnectNodes
import com.thecodewarrior.nodenet.common.node.NodeTraceResult
import com.thecodewarrior.nodenet.common.network.PacketConnectNodes
import net.minecraft.client.Minecraft

class ItemNodeConfigurator: ItemMod("configurator"), INodeInteractingItem {
    var connectingFromNode: Int? = null

    override fun rightClickBegan(node: NodeTraceResult?) {
        connectingFromNode = node?.entity?.entityId
    }

    override fun rightClickEnded(node: NodeTraceResult?) {
        val from = connectingFromNode
        connectingFromNode = null

        if(from != null && node != null && from != node.entity.entityId) {
            val packet = PacketConfigConnectNodes(node.entity.entityId, from)
            packet.handle(Minecraft.getMinecraft().player)
            PacketHandler.NETWORK.sendToServer(packet)
        }
    }

    override fun leftClickBegan(node: NodeTraceResult?) {
        if(node != null) {
            val gui = node.entity.node.client.createConfigurationGui()
            if(gui != null) {
                Minecraft.getMinecraft().displayGuiScreen(gui)
            }
//            val packet = PacketConnectNodes(node.entity.entityId, from)
//            packet.handle(Minecraft.getMinecraft().player)
//            PacketHandler.NETWORK.sendToServer(packet)
        }
    }
}