package com.thecodewarrior.nodenet.common.nodes

import com.thecodewarrior.nodenet.common.entity.EntityNode
import com.thecodewarrior.nodenet.common.node.Node
import com.thecodewarrior.nodenet.common.node.Signal
import net.minecraft.util.math.BlockPos
import kotlin.math.floor

class RedstoneReaderNode(entity: EntityNode): Node(entity) {
    override fun computeSignal(): Signal {
        val pos = BlockPos(floor(entity.posX), floor(entity.posY), floor(entity.posZ))
        return RedstoneSignal(entity.world.isBlockPowered(pos))
    }
}

