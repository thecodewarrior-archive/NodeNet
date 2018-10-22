package com.thecodewarrior.nodenet.client.render.node

import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.pos
import com.thecodewarrior.nodenet.common.node.visualRadius
import com.thecodewarrior.nodenet.common.node.Node
import com.thecodewarrior.nodenet.drawing
import com.thecodewarrior.nodenet.edges
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.math.AxisAlignedBB
import org.lwjgl.opengl.GL11
import java.awt.Color

abstract class NodeClient(val node: Node<*>) {
    abstract val color: Color
    abstract fun render()
    abstract fun createConfigurationGui(): GuiScreen?

    fun coreVisualRadius(): Double {
        return node.entity.visualRadius(Minecraft.getMinecraft().player)
    }

    fun renderCore() {
        val radius = coreVisualRadius()

        drawing { tessellator, vb ->
            GlStateManager.glLineWidth(2f)
            GlStateManager.color(color.red/255f, color.green/255f, color.blue/255f, color.alpha/255f)
            vb.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION)
            AxisAlignedBB(vec(-radius, -radius, -radius), vec(radius, radius, radius)).edges.forEach {
                vb.pos(it.first).endVertex()
                vb.pos(it.second).endVertex()
            }
            tessellator.draw()
        }
    }
}

class NodeClientDefault(node: Node<*>): NodeClient(node) {
    override val color: Color
        get() = Color.DARK_GRAY

    override fun render() {
    }

    override fun createConfigurationGui(): GuiScreen? {
        return null
    }
}
