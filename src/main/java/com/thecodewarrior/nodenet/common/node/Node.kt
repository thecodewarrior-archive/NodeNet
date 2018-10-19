package com.thecodewarrior.nodenet.common.node

import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.plus
import com.teamwizardry.librarianlib.features.saving.Savable
import com.teamwizardry.librarianlib.features.saving.SaveInPlace
import com.thecodewarrior.nodenet.client.NodeRenderer
import com.thecodewarrior.nodenet.client.Ray
import com.thecodewarrior.nodenet.client.RedstoneReaderNodeRenderer
import com.thecodewarrior.nodenet.common.entity.EntityNode
import com.thecodewarrior.nodenet.setPosition
import net.minecraft.util.EnumParticleTypes
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.awt.Color
import java.lang.Math.floor

@SaveInPlace
open class Node(val entity: EntityNode) {

    @delegate:SideOnly(Side.CLIENT)
    @get:SideOnly(Side.CLIENT)
    val renderer: NodeRenderer? by lazy {
        null
    }

    var powered = false

    fun clientTick() {
        if(powered) {
            entity.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, entity.posX, entity.posY, entity.posZ, 0.0, 0.1, 0.0)
        }
    }

    fun serverTick() {
        val pos = BlockPos(floor(entity.posX), floor(entity.posY), floor(entity.posZ))
        if(entity.world.isBlockPowered(pos)) {
            powered = true
            entity.connectedEntities().forEach {
                it.node.powered = true
            }
        }
    }
}