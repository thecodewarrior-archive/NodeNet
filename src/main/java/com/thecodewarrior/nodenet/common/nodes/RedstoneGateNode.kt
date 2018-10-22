package com.thecodewarrior.nodenet.common.nodes

import com.teamwizardry.librarianlib.features.saving.Savable
import com.thecodewarrior.nodenet.common.entity.EntityNode
import com.thecodewarrior.nodenet.common.node.Node
import com.thecodewarrior.nodenet.common.node.NodeConfig
import com.thecodewarrior.nodenet.common.node.Signal
import net.minecraft.init.Blocks
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import kotlin.math.floor

class RedstoneGateNode(entity: EntityNode): Node<RedstoneGateNode.Config>(entity, Config()) {

    override fun computeSignal(): Signal {
        return config.gate.compute(inputs)
    }

    @Savable
    data class Config(
        var gate: Gate = Gate.OR
    ): NodeConfig<Config>() {
        override fun clone(): Config {
            return this.copy()
        }
    }

    enum class Gate(val computer: (List<Signal>) -> Signal) {
        OR({ inputs ->
            RedstoneSignal(inputs.any { it == RedstoneSignal.ON })
        }),
        NOR({ inputs ->
            RedstoneSignal(!inputs.any { it == RedstoneSignal.ON })
        }),
        AND({ inputs ->
            RedstoneSignal(inputs.all { it == RedstoneSignal.ON })
        }),
        NAND({ inputs ->
            RedstoneSignal(!inputs.all { it == RedstoneSignal.ON })
        }),
        XOR({ inputs ->
            RedstoneSignal(inputs.count { it == RedstoneSignal.ON } % 2 == 1)
        }),
        XNOR({ inputs ->
            RedstoneSignal(inputs.count { it == RedstoneSignal.ON } % 2 != 1)
        }),
        ;

        fun compute(inputs: List<Signal>): Signal {
            return computer(inputs)
        }
    }
}


