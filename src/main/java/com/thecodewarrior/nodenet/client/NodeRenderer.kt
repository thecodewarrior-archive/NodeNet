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

        val tessellator = Tessellator.getInstance()
        val vb = tessellator.buffer

        ModItems.manipulator.manipulatingHandle?.update(e.partialTicks)

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
                    vb.pos(entity.positionVector).color(Color.cyan).endVertex()
                    vb.pos(it.positionVector).color(Color.cyan).endVertex()
                }
            }
        }
        tessellator.draw()
        drawForEach(entities, e.partialTicks) { entity ->
            GlStateManager.depthFunc(GL11.GL_LEQUAL)
            renderNodeAlways(entity, e.partialTicks)
            renderNode(entity, e.partialTicks)
        }

        if(player.heldItemMainhand.item == ModItems.connector) {
            ModItems.connector.connectingFromNode?.let { e.world.getEntityByID(it) }?.let { source ->
                val startPos = source.positionVector

                val mouseOver = NodeInteractionClient.nodeMouseOver
                val endPos = if(mouseOver != null) {
                    mouseOver.entity.positionVector
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

    fun drawForEach(entities: List<EntityNode>, partialTicks: Float, draw: (entity: EntityNode) -> Unit) {
        val tinyOffset = vec(1e-3, 1e-3, 1e-3)
        entities.forEach { entity ->
            GlStateManager.pushMatrix()

            val renderPos = entity.positionVector + tinyOffset
            GlStateManager.translate(renderPos.x, renderPos.y, renderPos.z)

            draw(entity)

            GlStateManager.popMatrix()
        }
    }

    fun renderNodeAlways(node: EntityNode, partialTicks: Float) {
        val tessellator = Tessellator.getInstance()
        val vb = tessellator.buffer

        val relativePosition = node.positionVector - Minecraft.getMinecraft().player.renderPosition(partialTicks)
        val radius = node.visualRadius(relativePosition.lengthVector())

        val steps = 12
        GlStateManager.glLineWidth(4f)

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

        GlStateManager.glLineWidth(4f)
        vb.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR)
        vb.pos(vec(0, 0, 0)).color(Color.blue).endVertex()
        vb.pos(vec(0, 0, 1)).color(Color.blue).endVertex()
        tessellator.draw()

        node.node.handles.forEach {
            if(ModItems.manipulator.manipulatingNode == node) {
                it.draw()
                if(ModItems.manipulator.manipulatingHandle == it) {
                    it.drawInUse()
                }
            }
        }
    }
}