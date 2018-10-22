package com.thecodewarrior.nodenet.client.render.nodes

import com.thecodewarrior.nodenet.client.render.node.NodeClient
import com.thecodewarrior.nodenet.common.node.Node
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.GuiScreen
import java.awt.Color

class ParticleGeneratorClient(node: Node<*>): NodeClient(node) {
    override val color: Color
        get() = Color.GREEN.darker()

    override fun render() {
    }

    override fun createConfigurationGui(): GuiScreen? {
        return null
    }
}