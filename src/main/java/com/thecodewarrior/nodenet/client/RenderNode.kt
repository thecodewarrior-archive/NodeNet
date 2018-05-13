package com.thecodewarrior.nodenet.client

import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import org.lwjgl.opengl.GL11
import com.sun.tools.internal.xjc.reader.Ring.begin
import com.thecodewarrior.nodenet.common.entity.EntityNodeBase
import net.minecraft.client.renderer.Tessellator
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.client.renderer.GlStateManager
import com.teamwizardry.librarianlib.features.shader.ShaderHelper
import org.lwjgl.opengl.ARBShaderObjects
import net.minecraft.client.Minecraft
import com.sun.deploy.util.GeneralUtil
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.color
import com.teamwizardry.librarianlib.features.kotlin.pos
import com.thecodewarrior.nodenet.glMatrix
import scala.tools.nsc.typechecker.DestructureTypes.`DestructureType$class`.node
import net.minecraft.util.ResourceLocation
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.client.renderer.entity.Render
import net.minecraft.entity.Entity
import org.fusesource.jansi.Ansi
import java.awt.Color

class RenderNode(renderManager: RenderManager): Render<EntityNodeBase>(renderManager) {

    override fun getEntityTexture(entity: EntityNodeBase): ResourceLocation? {
        // TODO Auto-generated method stub
        return null
    }

    override fun doRender(entity: EntityNodeBase, x: Double, y: Double, z: Double, entityYaw: Float, partialTicks: Float) {
        val tessellator = Tessellator.getInstance()
        val vb = tessellator.buffer

        GlStateManager.disableTexture2D()
        GlStateManager.disableLighting()
        GlStateManager.disableCull()
        GlStateManager.disableBlend()

        glMatrix {
            GlStateManager.translate(x, y, z)
            GlStateManager.rotate(entity.rotationYaw, 0f, -1f, 0f)
            GlStateManager.rotate(entity.rotationPitch, 1f, 0f, 0f)

            var red = Color.red.darker()
            var green = Color.green.darker()
            var blue = Color.blue.darker()
            var white = Color.white.darker()
            val r = 1/8f

            GlStateManager.depthMask(false)
            GlStateManager.depthFunc(GL11.GL_GREATER)

            vb.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR)
            vb.pos(vec(r, 0, 0)).color(red).endVertex()
            vb.pos(vec(0, 0, 0)).color(red).endVertex()
            vb.pos(vec(0, 0, 0)).color(white).endVertex()
            vb.pos(vec(-r, 0, 0)).color(white).endVertex()

            vb.pos(vec(0, r, 0)).color(green).endVertex()
            vb.pos(vec(0, 0, 0)).color(green).endVertex()
            vb.pos(vec(0, 0, 0)).color(white).endVertex()
            vb.pos(vec(0, -r, 0)).color(white).endVertex()

            vb.pos(vec(0, 0, 0)).color(blue).endVertex()
            vb.pos(vec(0, 0, 3*r)).color(blue).endVertex()
            tessellator.draw()

            red = red.brighter()
            green = green.brighter()
            blue = blue.brighter()
            white = white.brighter()

            GlStateManager.depthMask(true)
            GlStateManager.depthFunc(GL11.GL_LEQUAL)

            vb.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR)
            vb.pos(vec(r, 0, 0)).color(red).endVertex()
            vb.pos(vec(0, 0, 0)).color(red).endVertex()
            vb.pos(vec(0, 0, 0)).color(white).endVertex()
            vb.pos(vec(-r, 0, 0)).color(white).endVertex()

            vb.pos(vec(0, r, 0)).color(green).endVertex()
            vb.pos(vec(0, 0, 0)).color(green).endVertex()
            vb.pos(vec(0, 0, 0)).color(white).endVertex()
            vb.pos(vec(0, -r, 0)).color(white).endVertex()

            vb.pos(vec(0, 0, 0)).color(blue).endVertex()
            vb.pos(vec(0, 0, 3*r)).color(blue).endVertex()
            tessellator.draw()
        }

        GlStateManager.enableTexture2D()
        GlStateManager.enableLighting()
        GlStateManager.enableCull()
        GlStateManager.disableBlend()

    }

    override fun doRenderShadowAndFire(entityIn: Entity, x: Double, y: Double, z: Double, yaw: Float, partialTicks: Float) {
        // NOOP
    }

}
