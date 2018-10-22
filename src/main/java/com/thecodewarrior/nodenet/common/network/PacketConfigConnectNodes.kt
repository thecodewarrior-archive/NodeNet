package com.thecodewarrior.nodenet.common.network

import com.teamwizardry.librarianlib.features.network.PacketBase
import com.teamwizardry.librarianlib.features.saving.Save
import com.thecodewarrior.nodenet.common.entity.EntityNode
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext

class PacketConfigConnectNodes(
        @Save var from: Int = 0,
        @Save var to: Int = 0
): PacketBase() {

    override fun handle(ctx: MessageContext) {
        handle(ctx.serverHandler.player)
    }

    fun handle(player: EntityPlayer) {
        val from = player.world.getEntityByID(from) as? EntityNode ?: return
        val to = player.world.getEntityByID(to) as? EntityNode ?: return

        if (from.configConnectedTo(to)) {
            from.disconnectConfig(to)
        } else {
            if(from.canConnectConfigTo(to) && to.canConnectConfigTo(from)) {
                from.connectConfigTo(to)
            }
        }
    }
}