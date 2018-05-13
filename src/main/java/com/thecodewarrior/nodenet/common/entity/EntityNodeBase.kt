package com.thecodewarrior.nodenet.common.entity

import com.sun.deploy.util.GeneralUtil
import com.teamwizardry.librarianlib.features.math.Matrix4
import com.teamwizardry.librarianlib.features.saving.SaveInPlace
import com.thecodewarrior.nodenet.common.node.Node
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

@SaveInPlace
class EntityNodeBase(worldIn: World): Entity(worldIn), IEntityAdditionalSpawnData {
    var destroyTimer: Int = 0
    var radius: Double = 1/8.0

//    lateinit var node: Node

    override fun getEntityBoundingBox(): AxisAlignedBB {
        return AxisAlignedBB(Vec3d.ZERO, Vec3d.ZERO)
    }

    val nodeName: ITextComponent
        get() = TextComponentString("ASDF")

    constructor(worldIn: World, x: Double, y: Double, z: Double, node: Node): this(worldIn) {
        setPosition(x, y, z)
//        this.node = node
    }

    override fun onUpdate() {
        if (destroyTimer > 0)
            destroyTimer--
//        if (this.world.isRemote) {
//            node.clientTick()
//        } else {
//            node.serverTick()
//        }
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
        val dist = 32.0
        return distance < dist * dist
    }

    override fun readEntityFromNBT(compound: NBTTagCompound) {

    }

    override fun writeEntityToNBT(compound: NBTTagCompound) {
    }

    override fun writeSpawnData(buffer: ByteBuf) {
    }

    override fun readSpawnData(additionalData: ByteBuf) {
    }

}

