package com.thecodewarrior.nodenet.client.gui

import com.teamwizardry.librarianlib.features.gui.EnumMouseButton
import com.teamwizardry.librarianlib.features.gui.GuiBase
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.gui.components.ComponentSprite
import com.teamwizardry.librarianlib.features.gui.components.ComponentText
import com.teamwizardry.librarianlib.features.gui.components.ComponentVoid
import com.teamwizardry.librarianlib.features.kotlin.enumMapOf
import com.teamwizardry.librarianlib.features.sprite.Sprite
import com.teamwizardry.librarianlib.features.sprite.Texture
import com.thecodewarrior.nodenet.common.node.Node
import com.thecodewarrior.nodenet.common.nodes.RedstoneGateNode
import com.thecodewarrior.nodenet.common.nodes.RedstoneGateNode.Gate
import net.minecraft.client.resources.I18n
import net.minecraft.util.ResourceLocation
import java.util.Locale

class GuiNodeRedstoneGate(val node: RedstoneGateNode): GuiBase(122, 80) {

    val gate = ComponentSprite(null, 15, 12, 32, 16)
    val name = ComponentText(31, 3, ComponentText.TextAlignH.CENTER)

    init {
        val wrap = ComponentVoid(0, 0)
        val bg = ComponentSprite(backgroundSprite, 0, 0)
        wrap.add(bg, gate, name)
        mainComponents.add(wrap)
        wrap.transform.scale = 2.0
        updateGate()
        gate.BUS.hook(GuiComponentEvents.MouseClickEvent::class.java) { e ->
            val offset =
                when(e.button) {
                    EnumMouseButton.RIGHT -> -1
                    EnumMouseButton.LEFT -> 1
                    EnumMouseButton.MIDDLE -> -node.config.gate.ordinal
                    else -> 0
                }
            node.config.gate = Gate.values()[(node.config.gate.ordinal+offset) % Gate.values().size]
            node.configChanged()
            updateGate()
        }
    }

    fun updateGate() {
        name.text.setValue(I18n.format("nodenet:gui.redstone_gate.gate." + node.config.gate.name.toLowerCase(Locale.ROOT)))
    }

    override fun doesGuiPauseGame(): Boolean {
        return false
    }

    companion object {
        val texture = Texture(ResourceLocation("nodenet", "textures/gui/node/redstone_gate.png"))
        val backgroundSprite = texture.getSprite("background", 61, 40)
        val gateSprites = mapOf(
            Gate.AND to texture.getSprite("and", 32, 16),
            Gate.OR to texture.getSprite("or", 32, 16),
            Gate.XOR to texture.getSprite("xor", 32, 16),
            Gate.NAND to texture.getSprite("nand", 32, 16),
            Gate.NOR to texture.getSprite("nor", 32, 16),
            Gate.XNOR to texture.getSprite("xnor", 32, 16)
        )
    }
}