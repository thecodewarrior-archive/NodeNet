package com.thecodewarrior.nodenet.common.network

import com.teamwizardry.librarianlib.features.network.PacketBase
import com.teamwizardry.librarianlib.features.saving.Save
import com.thecodewarrior.nodenet.common.entity.EntityNode
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.Vec3d
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext

class PacketRotateNode(
        @Save var node: Int = 0,
        @Save var pitch: Float = 0f,
        @Save var yaw: Float = 0f
): PacketBase() {

    override fun handle(ctx: MessageContext) {
        handle(ctx.serverHandler.player)
    }

    fun handle(player: EntityPlayer) {
        val node = player.world.getEntityByID(node) as? EntityNode ?: return
        node.rotationPitch = pitch
        node.rotationYaw = yaw
    }
}