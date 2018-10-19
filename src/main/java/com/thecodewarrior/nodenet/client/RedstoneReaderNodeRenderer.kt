package com.thecodewarrior.nodenet.client

import com.teamwizardry.librarianlib.core.client.ClientTickHandler.partialTicks
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.color
import com.teamwizardry.librarianlib.features.kotlin.plus
import com.teamwizardry.librarianlib.features.kotlin.pos
import com.teamwizardry.librarianlib.features.kotlin.unaryMinus
import com.thecodewarrior.nodenet.common.entity.EntityNode
import com.thecodewarrior.nodenet.glMatrix
import com.thecodewarrior.nodenet.renderPosition
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import org.lwjgl.opengl.GL11
import java.awt.Color

class RedstoneReaderNodeRenderer(val entity: EntityNode): NodeRenderer {
    override fun render() {
        val tessellator = Tessellator.getInstance()
        val vb = tessellator.buffer

        val absolutePos = BlockPos(entity.positionVector)
        val blockPos = -vec(entity.posX - absolutePos.x, entity.posY - absolutePos.y, entity.posZ - absolutePos.z)
        GlStateManager.glLineWidth(4f)
        glMatrix {
            GlStateManager.translate(blockPos.x, blockPos.y, blockPos.z)
            vb.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR)

            vb.pos(vec(0, 0, 0)).color(Color.RED.darker()).endVertex()
            vb.pos(vec(1, 0, 0)).color(Color.RED.darker()).endVertex()
            vb.pos(vec(0, 1, 0)).color(Color.RED.darker()).endVertex()
            vb.pos(vec(1, 1, 0)).color(Color.RED.darker()).endVertex()
            vb.pos(vec(0, 0, 1)).color(Color.RED.darker()).endVertex()
            vb.pos(vec(1, 0, 1)).color(Color.RED.darker()).endVertex()
            vb.pos(vec(0, 1, 1)).color(Color.RED.darker()).endVertex()
            vb.pos(vec(1, 1, 1)).color(Color.RED.darker()).endVertex()

            vb.pos(vec(0, 0, 0)).color(Color.RED.darker()).endVertex()
            vb.pos(vec(0, 1, 0)).color(Color.RED.darker()).endVertex()
            vb.pos(vec(1, 0, 0)).color(Color.RED.darker()).endVertex()
            vb.pos(vec(1, 1, 0)).color(Color.RED.darker()).endVertex()
            vb.pos(vec(0, 0, 1)).color(Color.RED.darker()).endVertex()
            vb.pos(vec(0, 1, 1)).color(Color.RED.darker()).endVertex()
            vb.pos(vec(1, 0, 1)).color(Color.RED.darker()).endVertex()
            vb.pos(vec(1, 1, 1)).color(Color.RED.darker()).endVertex()

            vb.pos(vec(0, 0, 0)).color(Color.RED.darker()).endVertex()
            vb.pos(vec(0, 0, 1)).color(Color.RED.darker()).endVertex()
            vb.pos(vec(0, 1, 0)).color(Color.RED.darker()).endVertex()
            vb.pos(vec(0, 1, 1)).color(Color.RED.darker()).endVertex()
            vb.pos(vec(1, 0, 0)).color(Color.RED.darker()).endVertex()
            vb.pos(vec(1, 0, 1)).color(Color.RED.darker()).endVertex()
            vb.pos(vec(1, 1, 0)).color(Color.RED.darker()).endVertex()
            vb.pos(vec(1, 1, 1)).color(Color.RED.darker()).endVertex()

            tessellator.draw()
        }

        glMatrix {
            vb.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR)

            vb.pos(vec(0, 0, 0)).color(Color.RED.darker()).endVertex()
            vb.pos(blockPos + vec(0, 0, 0)).color(Color.RED.darker()).endVertex()
            vb.pos(vec(0, 0, 0)).color(Color.RED.darker()).endVertex()
            vb.pos(blockPos + vec(0, 0, 1)).color(Color.RED.darker()).endVertex()
            vb.pos(vec(0, 0, 0)).color(Color.RED.darker()).endVertex()
            vb.pos(blockPos + vec(0, 1, 0)).color(Color.RED.darker()).endVertex()
            vb.pos(vec(0, 0, 0)).color(Color.RED.darker()).endVertex()
            vb.pos(blockPos + vec(0, 1, 1)).color(Color.RED.darker()).endVertex()
            vb.pos(vec(0, 0, 0)).color(Color.RED.darker()).endVertex()
            vb.pos(blockPos + vec(1, 0, 0)).color(Color.RED.darker()).endVertex()
            vb.pos(vec(0, 0, 0)).color(Color.RED.darker()).endVertex()
            vb.pos(blockPos + vec(1, 0, 1)).color(Color.RED.darker()).endVertex()
            vb.pos(vec(0, 0, 0)).color(Color.RED.darker()).endVertex()
            vb.pos(blockPos + vec(1, 1, 0)).color(Color.RED.darker()).endVertex()
            vb.pos(vec(0, 0, 0)).color(Color.RED.darker()).endVertex()
            vb.pos(blockPos + vec(1, 1, 1)).color(Color.RED.darker()).endVertex()

            tessellator.draw()
        }
    }
}