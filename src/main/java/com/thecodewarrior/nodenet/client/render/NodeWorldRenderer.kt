package com.thecodewarrior.nodenet.client.render

import com.teamwizardry.librarianlib.core.client.ClientTickHandler
import com.teamwizardry.librarianlib.core.client.ClientTickHandler.partialTicks
import com.teamwizardry.librarianlib.features.forgeevents.CustomWorldRenderEvent
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.*
import com.thecodewarrior.nodenet.client.NodeInteractionClient
import com.thecodewarrior.nodenet.client.visualRadius
import com.thecodewarrior.nodenet.common.entity.EntityNode
import com.thecodewarrior.nodenet.common.item.INodeVisibleItem
import com.thecodewarrior.nodenet.common.item.ModItems
import com.thecodewarrior.nodenet.common.item.ModItems.node
import com.thecodewarrior.nodenet.drawing
import com.thecodewarrior.nodenet.edges
import com.thecodewarrior.nodenet.glMatrix
import com.thecodewarrior.nodenet.renderPosition
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.RayTraceResult
import net.minecraftforge.client.ForgeHooksClient
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.lwjgl.opengl.GL11
import java.awt.Color

object NodeWorldRenderer {
    init { MinecraftForge.EVENT_BUS.register(this) }

    @SubscribeEvent
    fun renderWorld(e: CustomWorldRenderEvent) {
        val player = Minecraft.getMinecraft().player
        val playerLook = player.lookVec

        if(player.heldEquipment.all { it.item !is INodeVisibleItem }) return
        val entities = e.world.getEntities(EntityNode::class.java, { true })

        if(player.heldEquipment.any { it.item == ModItems.manipulator } &&
            Minecraft.getMinecraft().objectMouseOver?.typeOfHit != RayTraceResult.Type.BLOCK) {
            renderBlockMouseOver()
        }

        val tessellator = Tessellator.getInstance()
        val vb = tessellator.buffer

        val showAngle =
            if(player.heldEquipment.any { it.item == ModItems.connector })
                50.0
            else
                30.0
        val showThreshold = MathHelper.cos(Math.toRadians(showAngle).toFloat())
        fun shouldShow(entity: EntityNode, threshold: Float): Boolean {
            return (entity.positionVector - player.getPositionEyes(e.partialTicks)).normalize() dot playerLook > threshold
        }
        drawForEach(entities, e.partialTicks) { entity ->
            if (NodeInteractionClient.nodeMouseOver?.entity == entity || shouldShow(entity, showThreshold)) {
                GlStateManager.depthFunc(GL11.GL_ALWAYS)
            } else {
                GlStateManager.depthFunc(GL11.GL_LEQUAL)
            }
            renderNode(entity, e.partialTicks)
        }
        GlStateManager.depthFunc(GL11.GL_ALWAYS)
        vb.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR)
        entities.forEach { entity ->
            val connected = entity.connectedEntities()
            if(connected.isNotEmpty()) {
                connected.forEach {
                    if(shouldShow(entity, showThreshold) || shouldShow(it, showThreshold)) {
                        renderLine(entity, it)
                    }
                }
            }
        }
        tessellator.draw()

        if(player.heldEquipment.any { it.item == ModItems.connector }) {
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

    fun renderBlockMouseOver() {
        val mc = Minecraft.getMinecraft()
        val player = mc.player
        val eyes = player.getPositionEyes(partialTicks)
        val tip = eyes + player.getLook(partialTicks) * 5
        val rtr = player.world.rayTraceBlocks(eyes, tip)
        if(rtr?.typeOfHit == RayTraceResult.Type.BLOCK) {
            glMatrix {
                val playerPos = player.renderPosition(partialTicks)
                GlStateManager.translate(playerPos.x, playerPos.y, playerPos.z)
                GlStateManager.disableAlpha()
                if (!ForgeHooksClient.onDrawBlockHighlight(mc.renderGlobal, player, rtr, 0, partialTicks))
                    mc.renderGlobal.drawSelectionBox(player, rtr, 0, partialTicks)
                GlStateManager.enableAlpha()
            }
        }

    }

    fun renderLine(from: EntityNode, to: EntityNode) {
        val tessellator = Tessellator.getInstance()
        val vb = tessellator.buffer

        val color = to.node.output?.color ?: Color.DARK_GRAY
        val diff = to.positionVector - from.positionVector
        val gapSize = 1/4.0
        val lineLength = diff.lengthVector()
        val normal = diff / lineLength

        val time = (ClientTickHandler.ticks + ClientTickHandler.partialTicks) / 20.0
        val gapPos = (time * 2) % lineLength

        val farLength = gapPos - gapSize/2
        val closeLength = lineLength - (gapPos + gapSize/2)

        if(closeLength > 0) {
            vb.pos(from.positionVector).color(color).endVertex()
            vb.pos(from.positionVector + normal * closeLength).color(color).endVertex()
        }

        if(farLength > 0) {
            vb.pos(to.positionVector).color(color).endVertex()
            vb.pos(to.positionVector - normal * farLength).color(color).endVertex()
        }
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

    fun renderNode(entity: EntityNode, partialTicks: Float) {
        entity.node.renderer.renderCore()
        if(NodeInteractionClient.nodeMouseOver?.entity == entity) {
            entity.node.renderer.render()
        }
    }
}