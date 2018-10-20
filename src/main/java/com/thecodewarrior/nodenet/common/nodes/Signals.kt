package com.thecodewarrior.nodenet.common.nodes

import com.thecodewarrior.nodenet.common.node.Signal
import java.awt.Color

data class RedstoneSignal(val powered: Boolean): Signal {
    override val color: Color
        get() = if(powered) Color.RED else Color.RED.darker()

    companion object {
        val ON = RedstoneSignal(true)
        val OFF = RedstoneSignal(false)
    }
}
