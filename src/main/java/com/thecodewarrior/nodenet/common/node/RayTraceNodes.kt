package com.thecodewarrior.nodenet.common.node

import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.minus
import com.teamwizardry.librarianlib.features.kotlin.plus
import com.teamwizardry.librarianlib.features.kotlin.times
import com.teamwizardry.librarianlib.features.utilities.client.ClientRunnable
import com.thecodewarrior.nodenet.client.NodeInteractionClient
import com.thecodewarrior.nodenet.common.entity.EntityNode
import com.thecodewarrior.nodenet.common.item.ModItems
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.RayTraceResult

fun EntityPlayer.rayTraceNodes(partialTicks: Float, ignoring: List<EntityNode> = listOf()): NodeTraceResult? {

    val eyePos = this.getPositionEyes(partialTicks)
    val endPos = eyePos + this.getLook(partialTicks) * 512

    val entities = this.world.getEntities(EntityNode::class.java, { true })

    val tracedEntities = entities.mapNotNull { entity ->
        var cancel = false
        ClientRunnable.run {
            cancel = ignoring.any { entity === it }
        }
        if(cancel) return@mapNotNull null

        val radius = entity.visualRadius(this)
        val hit = AxisAlignedBB(vec(-radius, -radius, -radius), vec(radius, radius, radius))
            .offset(entity.positionVector).calculateIntercept(eyePos, endPos)

        if(hit != null) {
            return@mapNotNull NodeTraceResult(entity, hit)
        } else {
            return@mapNotNull null
        }
    }
    return tracedEntities.minBy { eyePos.distanceTo(it.hit.hitVec) }
}


fun EntityNode.visualRadius(player: EntityPlayer): Double {
    val distance = player.getPositionEyes(0f).distanceTo(this.positionVector)
    var radius = 2/16.0
    ClientRunnable.run {
        if(this == NodeInteractionClient.nodeMouseOver?.entity)
            radius = 3/16.0
    }
    return Math.max(radius, radius * distance/18)
}

data class NodeTraceResult(val entity: EntityNode, val hit: RayTraceResult)
