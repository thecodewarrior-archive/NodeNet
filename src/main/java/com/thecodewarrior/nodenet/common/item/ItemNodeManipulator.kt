package com.thecodewarrior.nodenet.common.item

import com.teamwizardry.librarianlib.features.base.item.ItemMod
import com.teamwizardry.librarianlib.features.kotlin.minus
import com.teamwizardry.librarianlib.features.kotlin.plus
import com.teamwizardry.librarianlib.features.kotlin.times
import com.teamwizardry.librarianlib.features.kotlin.unaryMinus
import com.teamwizardry.librarianlib.features.network.PacketHandler
import com.thecodewarrior.nodenet.client.Ray
import com.thecodewarrior.nodenet.common.entity.EntityNode
import com.thecodewarrior.nodenet.common.network.PacketDeleteNode
import com.thecodewarrior.nodenet.rotationPitch
import com.thecodewarrior.nodenet.rotationYaw
import net.minecraft.client.Minecraft
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import net.minecraftforge.client.model.animation.Animation

class ItemNodeManipulator: ItemMod("manipulator"), INodeInteractingItem {
    var draggingNode: EntityNode? = null
    var draggingDistance: Double? = null
    var rotationNormal: Vec3d? = null

    override fun leftClickBegan(node: EntityNode?) {
        if(node != null) {
            val packet = PacketDeleteNode(node.entityId)
            packet.handle(Minecraft.getMinecraft().player)
            PacketHandler.NETWORK.sendToServer(packet)
        }
    }

    override fun rightClickBegan(node: EntityNode?) {
        if(node != null && rotationNormal == null) {
            draggingNode = node
            draggingDistance = (Minecraft.getMinecraft().player.getPositionEyes(Animation.getPartialTickTime())
                    - node.positionVector).lengthVector()
            updatePosition()
        }
    }

    override fun rightClickEnded(node: EntityNode?) {
        updatePosition()
        if(draggingDistance != null) {
            draggingNode = null
            draggingDistance = null
        }
    }

    override fun rightClickTick(node: EntityNode?) {
        if(draggingDistance != null) {
            updatePosition()
        }
    }

    override fun middleClickBegan(node: EntityNode?) {
        if(node != null && draggingDistance == null) {
            draggingNode = node
            rotationNormal = Minecraft.getMinecraft().player.getPositionEyes(Animation.getPartialTickTime()) -
                    node.positionVector
            updateNormal()
        }
    }

    override fun middleClickEnded(node: EntityNode?) {
        if(rotationNormal != null) {
            updateNormal()
            draggingNode = null
            rotationNormal = null
        }
    }

    override fun middleClickTick(node: EntityNode?) {
        if(rotationNormal != null) {
            updateNormal()
        }
    }

    fun updatePosition() {
        val node = draggingNode ?: return
        val nodePos = nodePosition(node, 0f)
        node.setPosition(nodePos.x, nodePos.y, nodePos.z)
    }

    fun updateNormal() {
        val node = draggingNode ?: return
        val normal = nodeLookNonNormalized(node, 0f).normalize()
        node.rotationPitch = normal.rotationPitch()
        node.rotationYaw = normal.rotationYaw()
    }

    fun nodePosition(node: EntityNode, partialTicks: Float): Vec3d {
        if(node != draggingNode) return node.positionVector
        val draggingDistance = draggingDistance ?: return node.positionVector

        val player = Minecraft.getMinecraft().player
        if (!player.isSneaking) {
            val reachDistance = Minecraft.getMinecraft().playerController.blockReachDistance.toDouble()
            val trace = player.rayTrace(reachDistance, partialTicks)
            if (trace != null && trace.typeOfHit != RayTraceResult.Type.MISS) {
                node.setPosition(trace.hitVec.x, trace.hitVec.y, trace.hitVec.z)
            }
        }

        val eyePos = player.getPositionEyes(partialTicks)
        val lookVec = player.getLook(partialTicks) * draggingDistance
        return eyePos + lookVec
    }

    fun nodeLookNonNormalized(node: EntityNode, partialTicks: Float): Vec3d {
        if(node != draggingNode) return node.getLook(partialTicks)
        val rotationNormal = rotationNormal ?: return node.getLook(partialTicks)

        val player = Minecraft.getMinecraft().player

        val eyePos = player.getPositionEyes(partialTicks)
        val lookVec = player.getLook(partialTicks)

        val lookDistance = Ray(eyePos, lookVec)
                .intersectWithPlane(node.positionVector, -rotationNormal)
                ?: return node.getLook(partialTicks)
        return eyePos + lookVec * lookDistance - node.positionVector
    }
}