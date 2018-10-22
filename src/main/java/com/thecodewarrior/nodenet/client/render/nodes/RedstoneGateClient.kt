package com.thecodewarrior.nodenet.client.render.nodes

import com.thecodewarrior.nodenet.client.gui.GuiNodeRedstoneGate
import com.thecodewarrior.nodenet.client.render.node.NodeClient
import com.thecodewarrior.nodenet.common.node.Node
import com.thecodewarrior.nodenet.common.nodes.RedstoneGateNode
import com.thecodewarrior.nodenet.common.nodes.RedstoneSignal
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.GuiScreen
import java.awt.Color

class RedstoneGateClient(node: Node<*>): NodeClient(node) {
    override val color: Color
        get() = if(node.output == RedstoneSignal.ON) Color.RED else Color.RED.darker()

    override fun render() {
    }

    override fun createConfigurationGui(): GuiScreen? {
        return GuiNodeRedstoneGate(node as RedstoneGateNode)
    }
}