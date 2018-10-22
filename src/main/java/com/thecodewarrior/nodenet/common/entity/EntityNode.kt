package com.thecodewarrior.nodenet.common.entity

import com.teamwizardry.librarianlib.features.base.entity.EntityMod
import com.teamwizardry.librarianlib.features.kotlin.readTag
import com.teamwizardry.librarianlib.features.kotlin.toRl
import com.teamwizardry.librarianlib.features.kotlin.writeTag
import com.teamwizardry.librarianlib.features.network.PacketHandler
import com.teamwizardry.librarianlib.features.saving.Save
import com.teamwizardry.librarianlib.features.utilities.client.ClientRunnable
import com.thecodewarrior.nodenet.common.item.ModItems
import com.thecodewarrior.nodenet.common.network.PacketMoveNode
import com.thecodewarrior.nodenet.common.network.PacketRotateNode
import com.thecodewarrior.nodenet.common.node.NoConfig
import com.thecodewarrior.nodenet.common.node.Node
import com.thecodewarrior.nodenet.common.node.NodeType
import com.thecodewarrior.nodenet.getEntityByUUID
import io.netty.buffer.ByteBuf
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.*

class EntityNode(worldIn: World): EntityMod(worldIn), IEntityAdditionalSpawnData {
    @Save
    var connections = mutableSetOf<UUID>()
        set(value) {
            field = value
            cached.keys.toList().forEach {
                if(it !in value && it !in configConnections) cached.remove(it)
            }
        }
    @Save
    var configConnections = mutableSetOf<UUID>()
        set(value) {
            field = value
            cached.keys.toList().forEach {
                if(it !in value && it !in connections) cached.remove(it)
            }
        }
    var cached = mutableMapOf<UUID, Int?>()
    @Save
    var type: ResourceLocation = "missingno".toRl()
    var node: Node<*> = Node(this, NoConfig())

    override fun getEntityBoundingBox(): AxisAlignedBB {
        return AxisAlignedBB(Vec3d.ZERO, Vec3d.ZERO)
    }

    constructor(worldIn: World, x: Double, y: Double, z: Double, type: ResourceLocation): this(worldIn) {
        setPosition(x, y, z)
        this.type = type

        node = NodeType.REGISTRY.getValue(type)!!.createNode(this)
    }

    override fun onUpdate() {
        node.serverTick()
        ClientRunnable.run {
            node.clientTick()
        }
        if(world.isRemote && ModItems.manipulator.draggingNode == this) {
            PacketHandler.NETWORK.sendToServer(PacketMoveNode(this.entityId, this.positionVector))
            PacketHandler.NETWORK.sendToServer(PacketRotateNode(this.entityId, this.rotationPitch, this.rotationYaw))
        }
    }

    override fun canBeCollidedWith(): Boolean {
        return false
    }

    override fun entityInit() {
        setEntityInvulnerable(true)
        setSize(0f, 0f)
    }

    override fun readCustomBytes(buf: ByteBuf) {
        this.node.readFromNBT(buf.readTag())
    }

    override fun readCustomNBT(compound: NBTTagCompound) {
        this.node.readFromNBT(compound)
    }

    override fun writeCustomBytes(buf: ByteBuf) {
        buf.writeTag(this.node.writeToNBT(NBTTagCompound()))
    }

    override fun writeCustomNBT(compound: NBTTagCompound) {
        this.node.writeToNBT(compound)
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        val oldType = type
        super.readFromNBT(compound)

        if(type != oldType) {
            node = NodeType.REGISTRY.getValue(type)!!.createNode(this)
        }
    }

    @SideOnly(Side.CLIENT)
    override fun isInRangeToRenderDist(distance: Double): Boolean {
        return false
    }

    override fun readSpawnData(additionalData: ByteBuf) {
        readFromNBT(additionalData.readTag())
    }

    override fun writeSpawnData(buffer: ByteBuf) {
        buffer.writeTag(writeToNBT(NBTTagCompound()))
    }

    fun connectedTo(other: EntityNode): Boolean {
        return connections.contains(other.persistentID)
    }

    fun disconnect(other: EntityNode) {
        connections.remove(other.persistentID)
        cached.remove(other.persistentID)
        this.dispatchEntityToNearbyPlayers()
    }

    fun canConnectTo(other: EntityNode): Boolean {
        return true
    }

    fun connectTo(other: EntityNode) {
        connections.add(other.persistentID)
        cached[other.persistentID] = other.entityId
        this.dispatchEntityToNearbyPlayers()
    }

    fun connectedEntities(): List<EntityNode> {
        return connections.mapNotNull {
            val result = world.getEntityByUUID(it, cached[it])
            cached[it] = result?.second
            if(result == null) {
                return@mapNotNull null
            }
            return@mapNotNull result.first as? EntityNode
        }
    }

    fun configConnectedTo(other: EntityNode): Boolean {
        return configConnections.contains(other.persistentID)
    }

    fun disconnectConfig(other: EntityNode) {
        this.configConnections.remove(other.persistentID)
        this.cached.remove(other.persistentID)
        this.dispatchEntityToNearbyPlayers()

        other.configConnections.remove(this.persistentID)
        other.cached.remove(this.persistentID)
        other.dispatchEntityToNearbyPlayers()
    }

    fun canConnectConfigTo(other: EntityNode): Boolean {
        return other.node.config.javaClass == node.config.javaClass
    }

    fun connectConfigTo(other: EntityNode) {
        this.configConnections.add(other.persistentID)
        this.cached[other.persistentID] = other.entityId
        this.dispatchEntityToNearbyPlayers()

        other.configConnections.add(this.persistentID)
        other.cached[this.persistentID] = this.entityId
        other.dispatchEntityToNearbyPlayers()
    }

    fun connectedConfigEntities(): List<EntityNode> {
        return configConnections.mapNotNull {
            val result = world.getEntityByUUID(it, cached[it])
            cached[it] = result?.second
            if(result == null) {
                return@mapNotNull null
            }
            return@mapNotNull result.first as? EntityNode
        }
    }
}

