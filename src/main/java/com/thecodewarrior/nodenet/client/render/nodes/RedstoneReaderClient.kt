package com.thecodewarrior.nodenet.client.render.nodes

import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.color
import com.teamwizardry.librarianlib.features.kotlin.plus
import com.teamwizardry.librarianlib.features.kotlin.pos
import com.teamwizardry.librarianlib.features.kotlin.unaryMinus
import com.thecodewarrior.nodenet.client.render.node.NodeClient
import com.thecodewarrior.nodenet.common.node.Node
import com.thecodewarrior.nodenet.common.nodes.RedstoneSignal
import com.thecodewarrior.nodenet.drawing
import com.thecodewarrior.nodenet.edges
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.math.floor

class RedstoneReaderClient(node: Node<*>): NodeClient(node) {
    override val color: Color
        get() = if(node.output == RedstoneSignal.ON) Color.RED else Color.RED.darker()

    override fun render() {
        val posInBlock = vec(
            node.entity.posX - floor(node.entity.posX),
            node.entity.posY - floor(node.entity.posY),
            node.entity.posZ - floor(node.entity.posZ)
        )
        GlStateManager.glLineWidth(4f)

        drawing { tessellator, vb ->
            GlStateManager.glLineWidth(2f)
            GlStateManager.color(color.red/255f, color.green/255f, color.blue/255f, color.alpha/255f)
            vb.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION)
            AxisAlignedBB(BlockPos.ORIGIN).offset(-posInBlock).edges.forEach {
                vb.pos(it.first).endVertex()
                vb.pos(it.second).endVertex()
            }
            tessellator.draw()
        }

        drawing { tessellator, vb ->
            vb.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR)

            vb.pos(vec(0, 0, 0)).color(color).endVertex()
            vb.pos(-posInBlock + vec(0, 0, 0)).color(color).endVertex()
            vb.pos(vec(0, 0, 0)).color(color).endVertex()
            vb.pos(-posInBlock + vec(0, 0, 1)).color(color).endVertex()
            vb.pos(vec(0, 0, 0)).color(color).endVertex()
            vb.pos(-posInBlock + vec(0, 1, 0)).color(color).endVertex()
            vb.pos(vec(0, 0, 0)).color(color).endVertex()
            vb.pos(-posInBlock + vec(0, 1, 1)).color(color).endVertex()
            vb.pos(vec(0, 0, 0)).color(color).endVertex()
            vb.pos(-posInBlock + vec(1, 0, 0)).color(color).endVertex()
            vb.pos(vec(0, 0, 0)).color(color).endVertex()
            vb.pos(-posInBlock + vec(1, 0, 1)).color(color).endVertex()
            vb.pos(vec(0, 0, 0)).color(color).endVertex()
            vb.pos(-posInBlock + vec(1, 1, 0)).color(color).endVertex()
            vb.pos(vec(0, 0, 0)).color(color).endVertex()
            vb.pos(-posInBlock + vec(1, 1, 1)).color(color).endVertex()

            tessellator.draw()
        }
    }

    override fun createConfigurationGui(): GuiScreen? {
        return null
    }
}