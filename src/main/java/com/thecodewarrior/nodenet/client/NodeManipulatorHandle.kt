package com.thecodewarrior.nodenet.client

import com.teamwizardry.librarianlib.features.kotlin.clamp
import com.teamwizardry.librarianlib.features.kotlin.color
import com.teamwizardry.librarianlib.features.kotlin.minus
import com.teamwizardry.librarianlib.features.kotlin.pos
import com.thecodewarrior.nodenet.common.node.Node
import com.thecodewarrior.nodenet.drawing
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.math.Vec3d
import org.lwjgl.opengl.GL11
import java.awt.Color

interface NodeManipulatorHandle {
    fun update(partialTicks: Float)
    fun storeDragRelativeState(partialTicks: Float)
    fun traceHandle(lookRay: Ray): Double
    fun draw()
    fun drawInUse()
}

class DraggingAxisNodeManipulatorHandle(
        val node: Node, val ray: Ray,
        val color: Color, val handleDistance: Double, val handleLength: Double,
        val updater: (Vec3d) -> Unit
): NodeManipulatorHandle {
    var point = ray.origin
    var otherPoint: Vec3d = ray.origin
    var dragMultiplier = 0.0

    override fun update(partialTicks: Float) {
        val lookRay = lookRay(partialTicks)
        val multipliers = ray.closestPointTo(lookRay)

        if(multipliers.second < 0) // the closest point is behind the player
            updater(ray.origin)
        val adjustedMultiplier = multipliers.first - dragMultiplier
        val adjustedMultiplierPlayer = multipliers.second
        point = ray.pointWithMultiplier(adjustedMultiplier)
        otherPoint = lookRay.pointWithMultiplier(adjustedMultiplierPlayer)

        updater(point)
    }

    override fun storeDragRelativeState(partialTicks: Float) {
        dragMultiplier = ray.closestPointTo(lookRay(partialTicks)).first
    }

    private fun lookRay(partialTicks: Float): Ray {
        val player = Minecraft.getMinecraft().player
        val eyePos = player.getPositionEyes(partialTicks)
        val lookVec = player.getLook(partialTicks)
        return Ray(eyePos-node.entity.positionVector, lookVec)
    }

    override fun traceHandle(lookRay: Ray): Double {
        val multipliers = ray.closestPointTo(lookRay)
        val ourPoint = ray.pointWithMultiplier(multipliers.first)
        val theirPoint = lookRay.pointWithMultiplier(multipliers.second)
        val separation = (ourPoint - theirPoint)
                        .lengthSquared()
        if(
                multipliers.first > handleDistance &&
                multipliers.first < handleDistance + handleLength &&
                separation < 1/64.0
        ) {
            return multipliers.second
        }
        return -1.0
    }

    override fun draw() {
        drawing { tessellator, vb ->
            GlStateManager.glLineWidth(6f)
            vb.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR)
            vb.pos(ray.pointWithMultiplier(handleDistance)).color(color).endVertex()
            vb.pos(ray.pointWithMultiplier(handleDistance + handleLength)).color(color).endVertex()
            tessellator.draw()
        }
    }

    override fun drawInUse() {
//        GL11.glEnable(GL11.GL_LINE_STIPPLE)
//        GL11.glLineStipple(1, 0b1111000011110000.toShort())
//
//        drawing { tessellator, vb ->
//            vb.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR)
//            vb.pos(ray.pointWithMultiplier(dragMultiplier)).color(Color.gray).endVertex()
//            vb.pos(otherPoint).color(Color.gray).endVertex()
//            tessellator.draw()
//        }
//
//        GL11.glDisable(GL11.GL_LINE_STIPPLE)
    }

}