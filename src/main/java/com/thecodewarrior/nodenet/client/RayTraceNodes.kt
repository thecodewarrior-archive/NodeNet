package com.thecodewarrior.nodenet.client

import com.teamwizardry.librarianlib.features.kotlin.dot
import com.teamwizardry.librarianlib.features.kotlin.minus
import com.teamwizardry.librarianlib.features.kotlin.times
import com.thecodewarrior.nodenet.common.entity.EntityNode
import net.minecraft.entity.player.EntityPlayer
import kotlin.math.max

fun EntityPlayer.rayTraceNodes(partialTicks: Float): NodeTraceResult? {

    val origin = this.getPositionEyes(partialTicks)
    val lookNormal = this.getLook(partialTicks)

    val entities = this.world.getEntities(EntityNode::class.java, { true })

    val tracedEntities = entities.mapNotNull { entity ->
        val relativeToEyes = entity.positionVector - origin
        val length = relativeToEyes dot lookNormal
        val endPos = lookNormal * length

        val relative = endPos - relativeToEyes
        // length isn't technically the distance from the node to us, but it's close for every entity that matters
        val radius = entity.visualRadius(length)
        return@mapNotNull if(relative.lengthSquared() < radius * radius)
            NodeTraceResult(entity, length)
        else
            null
    }
    return tracedEntities.minBy { it.distance }
}

fun EntityNode.visualRadius(distance: Double): Double {
    // 30 is a magic number figured out by fiddling with it.
    return max(1/8.0, distance/30) * if(this == NodeInteractionClient.nodeMouseOver?.entity) 1.5 else 1.0
}

data class NodeTraceResult(val entity: EntityNode, val distance: Double)
