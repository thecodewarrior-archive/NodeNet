package com.thecodewarrior.nodenet.client.render.nodes

import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.color
import com.teamwizardry.librarianlib.features.kotlin.plus
import com.teamwizardry.librarianlib.features.kotlin.pos
import com.teamwizardry.librarianlib.features.kotlin.unaryMinus
import com.thecodewarrior.nodenet.client.render.node.NodeRenderer
import com.thecodewarrior.nodenet.common.entity.EntityNode
import com.thecodewarrior.nodenet.common.node.Node
import com.thecodewarrior.nodenet.common.nodes.RedstoneSignal
import com.thecodewarrior.nodenet.drawing
import com.thecodewarrior.nodenet.edges
import com.thecodewarrior.nodenet.glMatrix
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.math.floor

class RedstoneReaderRenderer(node: Node): NodeRenderer(node) {
    override fun render() {
        val c = if(node.output == RedstoneSignal.ON) Color.RED else Color.RED.darker()
        renderDefault(c)

        val posInBlock = vec(
            node.entity.posX - floor(node.entity.posX),
            node.entity.posY - floor(node.entity.posY),
            node.entity.posZ - floor(node.entity.posZ)
        )
        GlStateManager.glLineWidth(4f)

        drawing { tessellator, vb ->
            GlStateManager.glLineWidth(2f)
            GlStateManager.color(c.red/255f, c.green/255f, c.blue/255f, c.alpha/255f)
            vb.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION)
            AxisAlignedBB(BlockPos.ORIGIN).offset(-posInBlock).edges.forEach {
                vb.pos(it.first).endVertex()
                vb.pos(it.second).endVertex()
            }
            tessellator.draw()
        }

        drawing { tessellator, vb ->
            vb.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR)

            vb.pos(vec(0, 0, 0)).color(c).endVertex()
            vb.pos(-posInBlock + vec(0, 0, 0)).color(c).endVertex()
            vb.pos(vec(0, 0, 0)).color(c).endVertex()
            vb.pos(-posInBlock + vec(0, 0, 1)).color(c).endVertex()
            vb.pos(vec(0, 0, 0)).color(c).endVertex()
            vb.pos(-posInBlock + vec(0, 1, 0)).color(c).endVertex()
            vb.pos(vec(0, 0, 0)).color(c).endVertex()
            vb.pos(-posInBlock + vec(0, 1, 1)).color(c).endVertex()
            vb.pos(vec(0, 0, 0)).color(c).endVertex()
            vb.pos(-posInBlock + vec(1, 0, 0)).color(c).endVertex()
            vb.pos(vec(0, 0, 0)).color(c).endVertex()
            vb.pos(-posInBlock + vec(1, 0, 1)).color(c).endVertex()
            vb.pos(vec(0, 0, 0)).color(c).endVertex()
            vb.pos(-posInBlock + vec(1, 1, 0)).color(c).endVertex()
            vb.pos(vec(0, 0, 0)).color(c).endVertex()
            vb.pos(-posInBlock + vec(1, 1, 1)).color(c).endVertex()

            tessellator.draw()
        }
    }
}