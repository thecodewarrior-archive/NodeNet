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

class MissingTypeRenderer(node: Node): NodeRenderer(node) {
    override val color: Color
        get() = Color.MAGENTA

    override fun render() {
    }
}