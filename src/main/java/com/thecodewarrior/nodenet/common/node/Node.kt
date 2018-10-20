package com.thecodewarrior.nodenet.common.node

import com.teamwizardry.librarianlib.features.saving.SaveInPlace
import com.teamwizardry.librarianlib.features.utilities.client.ClientRunnable
import com.thecodewarrior.nodenet.client.render.node.NodeRenderer
import com.thecodewarrior.nodenet.client.render.node.NodeRendererDefault
import com.thecodewarrior.nodenet.common.entity.EntityNode
import net.minecraft.client.Minecraft
import net.minecraft.util.EnumParticleTypes
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.awt.Color
import java.lang.Math.floor
import java.lang.ref.WeakReference
import java.util.WeakHashMap

@SaveInPlace
open class Node(val entity: EntityNode) {

    @delegate:SideOnly(Side.CLIENT)
    @get:SideOnly(Side.CLIENT)
    val renderer: NodeRenderer by lazy {
        NodeType.REGISTRY.getValue(entity.type)?.createRenderer(this) ?: NodeRendererDefault(this)
    }

    private var lastCompute: Long = -1
    private var lastGather: Long = -1
    private val tickNumber: Long
        get() = tickNumbers[entity.world] ?: 0
    var output: Signal? = null
        private set
        get() {
            if(tickNumber != lastCompute) {
                lastCompute = tickNumber
                field = computeSignal()
            }
            return field
        }
    var inputs: List<Signal> = emptyList()
        private set
        get() {
            if(tickNumber != lastGather) {
                lastGather = tickNumber
                field = entity.connectedEntities().mapNotNull { it.node.output }
            }
            return field
        }

    protected open fun computeSignal(): Signal? {
        return null
    }

    @SideOnly(Side.CLIENT)
    open fun clientTick() {
    }

    open fun serverTick() {
    }

    companion object {
        init { MinecraftForge.EVENT_BUS.register(this) }
        private var tickNumbers = WeakHashMap<World, Long>()

        @SubscribeEvent
        fun preTick(e: TickEvent.WorldTickEvent) {
            if(e.phase == TickEvent.Phase.START) {
                tickNumbers[e.world] = (tickNumbers[e.world] ?: 0) + 1
            }
        }

        @SubscribeEvent
        fun clientTick(e: TickEvent.ClientTickEvent) {
            if(e.phase == TickEvent.Phase.START) {
                ClientRunnable.run {
                    val clientWorld = Minecraft.getMinecraft().world
                    tickNumbers[clientWorld] = (tickNumbers[clientWorld] ?: 0) + 1
                }
            }
        }
    }
}

interface Signal {
    val color: Color
}
