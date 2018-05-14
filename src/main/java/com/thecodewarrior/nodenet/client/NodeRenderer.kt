package com.thecodewarrior.nodenet.client

import com.teamwizardry.librarianlib.features.forgeevents.CustomWorldRenderEvent
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.*
import com.thecodewarrior.nodenet.common.entity.EntityNode
import com.thecodewarrior.nodenet.common.item.INodeInteractingItem
import com.thecodewarrior.nodenet.common.item.INodeVisibleItem
import com.thecodewarrior.nodenet.common.item.ModItems
import com.thecodewarrior.nodenet.renderPosition
import com.thecodewarrior.nodenet.rotationPitch
import com.thecodewarrior.nodenet.rotationYaw
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.math.Vec3d
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.lwjgl.opengl.GL11
import java.awt.Color

object NodeRenderer {
    init { MinecraftForge.EVENT_BUS.register(this) }

    @SubscribeEvent
    fun renderWorld(e: CustomWorldRenderEvent) {
        val player = Minecraft.getMinecraft().player
        if(player.heldItemMainhand.item !is INodeVisibleItem) return
        val entities = e.world.getEntities(EntityNode::class.java, { true })

        GlStateManager.glLineWidth(4f)
        val tessellator = Tessellator.getInstance()
        val vb = tessellator.buffer

        GlStateManager.depthFunc(GL11.GL_GREATER)
        drawForEach(entities, e.partialTicks) { entity ->
            renderNodeAlways(entity, e.partialTicks)
        }
        GlStateManager.depthFunc(GL11.GL_ALWAYS)
        vb.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR)
        entities.forEach { entity ->
            val connected = entity.connectedEntities()
            if(connected.isNotEmpty()) {
                connected.forEach {
                    vb.pos(ModItems.manipulator.nodePosition(entity, e.partialTicks)).color(Color.cyan).endVertex()
                    vb.pos(ModItems.manipulator.nodePosition(it, e.partialTicks)).color(Color.cyan).endVertex()
                }
            }
        }
        tessellator.draw()
        drawForEach(entities, e.partialTicks) { entity ->
            GlStateManager.depthFunc(GL11.GL_LEQUAL)
            renderNodeAlways(entity, e.partialTicks)
            renderNode(entity, e.partialTicks)
        }
        drawForEach(entities, e.partialTicks, true) { entity ->
            GlStateManager.depthFunc(GL11.GL_LEQUAL)
            renderNodeAxisAligned(entity, e.partialTicks)
        }

        if(player.heldItemMainhand.item == ModItems.connector) {
            ModItems.connector.connectingFromNode?.let { e.world.getEntityByID(it) }?.let { source ->
                val startPos = source.positionVector

                val mouseOver = NodeInteractionClient.nodeMouseOver
                val endPos = if(mouseOver != null) {
                    ModItems.manipulator.nodePosition(mouseOver.entity, e.partialTicks)
                } else {
                    player.getPositionEyes(e.partialTicks) + (player.getLook(e.partialTicks) * 2)
                }

                GlStateManager.depthFunc(GL11.GL_ALWAYS)
                vb.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR)
                vb.pos(startPos).color(Color.green).endVertex()
                vb.pos(endPos).color(Color.green).endVertex()
                tessellator.draw()
                GlStateManager.depthFunc(GL11.GL_LEQUAL)
            }
        }

        GlStateManager.depthFunc(GL11.GL_LEQUAL)
    }

    fun drawForEach(entities: List<EntityNode>, partialTicks: Float, axisAligned: Boolean = false, draw: (entity: EntityNode) -> Unit) {
        val tinyOffset = vec(1e-3, 1e-3, 1e-3)
        entities.forEach { entity ->
            GlStateManager.pushMatrix()

            val renderPos = ModItems.manipulator.nodePosition(entity, partialTicks) + tinyOffset
            GlStateManager.translate(renderPos.x, renderPos.y, renderPos.z)

            if(!axisAligned) {
                val facing = ModItems.manipulator.nodeLookNonNormalized(entity, partialTicks).normalize()
                val renderRotationPitch = facing.rotationPitch()
                val renderRotationYaw = facing.rotationYaw()
                GlStateManager.rotate(renderRotationYaw, 0f, -1f, 0f)
                GlStateManager.rotate(renderRotationPitch, 1f, 0f, 0f)
            }

            draw(entity)

            GlStateManager.popMatrix()
        }
    }

    fun renderNodeAlways(node: EntityNode, partialTicks: Float) {
        val tessellator = Tessellator.getInstance()
        val vb = tessellator.buffer

        val relativePosition = ModItems.manipulator.nodePosition(node, partialTicks) -
                Minecraft.getMinecraft().player.renderPosition(partialTicks)
        val radius = node.visualRadius(relativePosition.lengthVector())

        val steps = 12

        vb.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR)
        for (step in 0..steps) {
            val angle = (step / steps.toDouble()) * (Math.PI * 2)
            val x = Math.cos(angle) * radius
            val y = Math.sin(angle) * radius
            val z = 0
            vb.pos(vec(x, y, z)).color(Color.blue).endVertex()
        }
        tessellator.draw()

        vb.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR)
        for (step in 0..steps) {
            val angle = (step / steps.toDouble()) * (Math.PI * 2)
            val x = Math.cos(angle) * radius
            val y = 0
            val z = Math.sin(angle) * radius
            vb.pos(vec(x, y, z)).color(Color.green).endVertex()
        }
        tessellator.draw()

        vb.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR)
        for (step in 0..steps) {
            val angle = (step / steps.toDouble()) * (Math.PI * 2)
            val x = 0
            val y = Math.cos(angle) * radius
            val z = Math.sin(angle) * radius
            vb.pos(vec(x, y, z)).color(Color.red).endVertex()
        }
        tessellator.draw()
    }

    fun renderNode(node: EntityNode, partialTicks: Float) {
        val tessellator = Tessellator.getInstance()
        val vb = tessellator.buffer

        vb.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR)
        vb.pos(vec(0, 0, 0)).color(Color.blue).endVertex()
        vb.pos(vec(0, 0, 1)).color(Color.blue).endVertex()
        tessellator.draw()
    }

    fun renderNodeAxisAligned(node: EntityNode, partialTicks: Float) {
        val tessellator = Tessellator.getInstance()
        val vb = tessellator.buffer

        if(ModItems.manipulator.draggingNode == node) {
            if(ModItems.manipulator.draggingDistance != null) {
                GL11.glEnable(GL11.GL_LINE_STIPPLE)
                GL11.glLineStipple(1, 0b1111000011110000.toShort())

                vb.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR)
                vb.pos(vec(0, 0, 0)).color(Color.gray).endVertex()
                vb.pos(vec(0, 0, -50)).color(Color.gray).endVertex()
                vb.pos(vec(0, 0, 0)).color(Color.gray).endVertex()
                vb.pos(vec(0, 0, 50)).color(Color.gray).endVertex()
                vb.pos(vec(0, 0, 0)).color(Color.gray).endVertex()
                vb.pos(vec(0, -50, 0)).color(Color.gray).endVertex()
                vb.pos(vec(0, 0, 0)).color(Color.gray).endVertex()
                vb.pos(vec(0, 50, 0)).color(Color.gray).endVertex()
                vb.pos(vec(0, 0, 0)).color(Color.gray).endVertex()
                vb.pos(vec(-50, 0, 0)).color(Color.gray).endVertex()
                vb.pos(vec(0, 0, 0)).color(Color.gray).endVertex()
                vb.pos(vec(50, 0, 0)).color(Color.gray).endVertex()
                tessellator.draw()
                GL11.glDisable(GL11.GL_LINE_STIPPLE)
            }
            val rotationNormal = ModItems.manipulator.rotationNormal
            if(rotationNormal != null) {
                GlStateManager.enableBlend()
                val planeColor = Color(64, 64, 64, 128)
                val unitX = (rotationNormal cross vec(0, 1, 0))
                        .let {if(it.lengthVector() == 0.0) vec(1, 0, 0) else it}
                        .normalize()
                val unitZ = (rotationNormal cross unitX).normalize()

                vb.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR)
                vb.pos(vec(0, 0, 0)).color(planeColor).endVertex()
                val steps = 12
                for (step in 0..steps) {
                    val angle = (step / steps.toDouble()) * (Math.PI * 2)
                    vb.pos(unitX * (Math.cos(angle)) +
                            unitZ * (Math.sin(angle))
                    ).color(planeColor).endVertex()
                }
                tessellator.draw()
                GlStateManager.disableBlend()

                val intersection = ModItems.manipulator.nodeLookNonNormalized(node, partialTicks)
                GL11.glEnable(GL11.GL_LINE_STIPPLE)
                GL11.glLineStipple(1, 0b1111000011110000.toShort())
                vb.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR)
                vb.pos(vec(0, 0, 0)).color(Color.gray).endVertex()
                vb.pos(intersection).color(Color.gray).endVertex()
                tessellator.draw()
                GL11.glDisable(GL11.GL_LINE_STIPPLE)
            }
        }
    }
}