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

    var signalBacking: Signal? = null
    val signal: Signal
        get() {
            if(signalBacking == null) {
                signalBacking = computeSignal()
            }
            return signalBacking!!
        }

    fun computeSignal(): Signal {
        val pos = BlockPos(floor(entity.posX), floor(entity.posY), floor(entity.posZ))
        return RedstoneSignal(entity.world.isBlockPowered(pos))
//        return NoSignal
    }
    fun gatherInputs(): List<Signal> {
        return entity.connectedEntities().map { it.node.signal }
    }

    fun clientTick() {
        if(gatherInputs().any { it == RedstoneSignal.ON }) {
            entity.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, entity.posX, entity.posY, entity.posZ, 0.0, 0.1, 0.0)
        }
    }

    fun serverTick() {
    }

}

interface Signal
object NoSignal: Signal
data class RedstoneSignal(val powered: Boolean): Signal {
    companion object {
        val ON = RedstoneSignal(true)
        val OFF = RedstoneSignal(false)
    }
}
