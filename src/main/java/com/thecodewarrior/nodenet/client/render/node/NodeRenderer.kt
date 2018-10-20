package com.thecodewarrior.nodenet.client.render.node

import com.teamwizardry.librarianlib.core.client.ClientTickHandler.partialTicks
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.minus
import com.teamwizardry.librarianlib.features.kotlin.pos
import com.thecodewarrior.nodenet.client.visualRadius
import com.thecodewarrior.nodenet.common.node.Node
import com.thecodewarrior.nodenet.drawing
import com.thecodewarrior.nodenet.edges
import com.thecodewarrior.nodenet.renderPosition
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.math.AxisAlignedBB
import net.minecraftforge.registries.IForgeRegistry
import net.minecraftforge.registries.IForgeRegistryEntry
import org.lwjgl.opengl.GL11
import java.awt.Color

abstract class NodeRenderer(val node: Node) {
    abstract val color: Color
    abstract fun render()

    fun coreVisualRadius(): Double {
        val relativePosition = node.entity.positionVector - Minecraft.getMinecraft().player.renderPosition(partialTicks)
        return node.entity.visualRadius(relativePosition.lengthVector())
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

class NodeRendererDefault(node: Node): NodeRenderer(node) {
    override val color: Color
        get() = Color.DARK_GRAY

    override fun render() {
    }
}
