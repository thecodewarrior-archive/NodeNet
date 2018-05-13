package com.thecodewarrior.nodenet.client

import com.teamwizardry.librarianlib.features.kotlin.dot
import com.teamwizardry.librarianlib.features.kotlin.minus
import com.teamwizardry.librarianlib.features.kotlin.plus
import com.teamwizardry.librarianlib.features.kotlin.times
import com.thecodewarrior.nodenet.common.entity.EntityNodeBase
import net.minecraft.client.Minecraft
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.util.math.Vec3d

object NodeInteractionClient {
    var nodeMouseOver: EntityNodeBase? = null

    /**
     * Return true to skip normal handling
     */
    @JvmStatic
    fun processKeyBinds(): Boolean {
        return false
    }

    @JvmStatic
    fun getMouseOver(partialTicks: Float) {
        val player = Minecraft.getMinecraft().player ?: return
        val origin = player.getPositionEyes(partialTicks)
        val normal = player.getLook(partialTicks)

        val entities = player.world.getEntities(EntityNodeBase::class.java, { true })

        val tracedEntities = entities.map { entity ->
            val relativeToEyes = entity.positionVector - origin
            val length = relativeToEyes dot normal
            val endPos = normal * length

            val relative = endPos - relativeToEyes
            return@map if(relative.lengthSquared() < entity.radius * entity.radius)
                entity to length
            else
                entity to -1.0
        }.filter { it.second >= 0 }

        nodeMouseOver = tracedEntities.minBy { it.second }?.first
        if(nodeMouseOver != null)
            Minecraft.getMinecraft().objectMouseOver = RayTraceResult(RayTraceResult.Type.MISS, Vec3d.ZERO,
                    EnumFacing.UP, BlockPos.ORIGIN)
    }

    /**
     * return true to skip setting `Minecraft.objectMouseOver` in `EntityRenderer.getMouseOver`
     */
    @JvmStatic
    fun shouldSkipSettingObjectMouseOver(partialTicks: Float): Boolean {
        return nodeMouseOver != null
    }
}
