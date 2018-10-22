package com.thecodewarrior.nodenet.common.node

import com.teamwizardry.librarianlib.features.saving.AbstractSaveHandler
import com.teamwizardry.librarianlib.features.saving.Savable
import com.teamwizardry.librarianlib.features.saving.Save
import com.teamwizardry.librarianlib.features.saving.SaveInPlace
import com.teamwizardry.librarianlib.features.utilities.client.ClientRunnable
import com.thecodewarrior.nodenet.client.render.node.NodeClient
import com.thecodewarrior.nodenet.client.render.node.NodeClientDefault
import com.thecodewarrior.nodenet.common.entity.EntityNode
import net.minecraft.client.Minecraft
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.awt.Color
import java.util.WeakHashMap

open class Node<T: NodeConfig<T>>(val entity: EntityNode, var config: T) {

    protected open fun computeSignal(): Signal? {
        return null
    }

    @SideOnly(Side.CLIENT)
    open fun clientTick() {
    }

    open fun serverTick() {
    }

    val type: NodeType = NodeType.REGISTRY.getValue(entity.type)!!

    @field:SideOnly(Side.CLIENT)
    @get:SideOnly(Side.CLIENT)
    val client: NodeClient = type.createClient(this)

    fun configChanged() {
        propagateConfig(this.config, mutableSetOf())
    }

    private fun propagateConfig(config: T, visitedSet: MutableSet<Node<T>>) {
        if(visitedSet.add(this)) {
            this.config = config.clone()
            this.entity.connectedConfigEntities().forEach {
                @Suppress("UNCHECKED_CAST")
                (it.node as Node<T>).propagateConfig(config, visitedSet)
            }
        }
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

    fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        var comp = NBTTagCompound().apply { AbstractSaveHandler.writeAutoNBT(config, false) }
        compound.setTag("auto", comp)
        comp = NBTTagCompound().apply { config.writeCustomNBT(this) }
        compound.setTag("custom", comp)
        return compound
    }

    fun readFromNBT(compound: NBTTagCompound) {
        AbstractSaveHandler.readAutoNBT(config, compound.getCompoundTag("auto"), false)
        config.readCustomNBT(compound.getCompoundTag("custom"))
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

@SaveInPlace
abstract class NodeConfig<T> {
    abstract fun clone(): T

    open fun writeCustomNBT(compound: NBTTagCompound) {
        // NO-OP
    }

    open fun readCustomNBT(compound: NBTTagCompound) {
        // NO-OP
    }

}

class NoConfig: NodeConfig<NoConfig>() {
    override fun clone(): NoConfig {
        return NoConfig()
    }
}
