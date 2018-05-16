package com.thecodewarrior.nodenet.common.entity

import com.sun.deploy.util.GeneralUtil
import com.teamwizardry.librarianlib.features.base.entity.EntityMod
import com.teamwizardry.librarianlib.features.kotlin.readTag
import com.teamwizardry.librarianlib.features.kotlin.writeTag
import com.teamwizardry.librarianlib.features.math.Matrix4
import com.teamwizardry.librarianlib.features.network.PacketHandler
import com.teamwizardry.librarianlib.features.saving.AbstractSaveHandler
import com.teamwizardry.librarianlib.features.saving.Save
import com.teamwizardry.librarianlib.features.saving.SaveInPlace
import com.thecodewarrior.nodenet.common.item.ItemNodeManipulator
import com.thecodewarrior.nodenet.common.item.ModItems
import com.thecodewarrior.nodenet.common.network.PacketMoveNode
import com.thecodewarrior.nodenet.common.network.PacketRotateNode
import com.thecodewarrior.nodenet.common.node.Node
import com.thecodewarrior.nodenet.getEntityByUUID
import io.netty.buffer.ByteBuf
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.Vec3d
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.TextComponentString
import net.minecraft.world.World
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
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
                if(it !in value) cached.remove(it)
            }
        }
    var cached = mutableMapOf<UUID, Int?>()

    init {
        println("Yay my dudes!")
    }

    var node = Node(this)

    override fun getEntityBoundingBox(): AxisAlignedBB {
        return AxisAlignedBB(Vec3d.ZERO, Vec3d.ZERO)
    }

    constructor(worldIn: World, x: Double, y: Double, z: Double): this(worldIn) {
        setPosition(x, y, z)
    }

    override fun onUpdate() {
        if(world.isRemote && ModItems.manipulator.manipulatingNode == this) {
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
        this.ignoreFrustumCheck = true
    }

    @SideOnly(Side.CLIENT)
    override fun isInRangeToRenderDist(distance: Double): Boolean {
        val dist = 256.0
        return distance < dist * dist
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

}

