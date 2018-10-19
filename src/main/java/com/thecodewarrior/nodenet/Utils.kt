package com.thecodewarrior.nodenet

import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.plus
import com.teamwizardry.librarianlib.features.kotlin.times
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.entity.Entity
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import java.util.*

inline fun glMatrix(block: () -> Unit) {
    GlStateManager.pushMatrix()
    block()
    GlStateManager.popMatrix()
}

fun Entity.renderPosition(partialTicks: Float): Vec3d {
    return this.positionVector + (vec(
            this.prevPosX - this.posX,
            this.prevPosY - this.posY,
            this.prevPosZ - this.posZ
    ) * (1-partialTicks))
}

fun Entity.setPosition(v: Vec3d) {
    this.setPosition(v.x, v.y, v.z)
}

fun World.getEntityByUUID(uuid: UUID, cachedId: Int?): Pair<Entity, Int>? {
    var entity: Entity?
    if(cachedId != null) {
        entity = this.getEntityByID(cachedId)
        if (entity?.persistentID == uuid) return entity to cachedId
    }
    entity = this.loadedEntityList.find { it.persistentID == uuid }
    if(entity != null) return entity to entity.entityId
    return null
}

fun Vec3d.rotationYaw(): Float {
    return -Math.toDegrees(Math.atan2(this.x, this.z)).toFloat()
}

fun Vec3d.rotationPitch(): Float {
    return -Math.toDegrees(Math.asin(this.y)).toFloat()
}

inline fun drawing(callback: (tessellator: Tessellator, vb: BufferBuilder) -> Unit) {
    callback(Tessellator.getInstance(), Tessellator.getInstance().buffer)
}

val AxisAlignedBB.edges: List<Pair<Vec3d, Vec3d>>
    get() {
        return listOf(
            vec(0, 0, 0), vec(1, 0, 0),
            vec(0, 1, 0), vec(1, 1, 0),
            vec(0, 0, 1), vec(1, 0, 1),
            vec(0, 1, 1), vec(1, 1, 1),

            vec(0, 0, 0), vec(0, 1, 0),
            vec(1, 0, 0), vec(1, 1, 0),
            vec(0, 0, 1), vec(0, 1, 1),
            vec(1, 0, 1), vec(1, 1, 1),

            vec(0, 0, 0), vec(0, 0, 1),
            vec(0, 1, 0), vec(0, 1, 1),
            vec(1, 0, 0), vec(1, 0, 1),
            vec(1, 1, 0), vec(1, 1, 1)
        ).map {
            vec(
                minX + (maxX-minX) * it.x,
                minY + (maxY-minY) * it.y,
                minZ + (maxZ-minZ) * it.z
            )
        }.chunked(2).map { (from, to) -> from to to }
    }

val EnumFacing.frontOffset: Vec3d
    get() {
        return vec(frontOffsetX, frontOffsetY, frontOffsetZ)
    }
