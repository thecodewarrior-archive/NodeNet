package com.thecodewarrior.nodenet.common.nodes

import com.teamwizardry.librarianlib.features.saving.Savable
import com.thecodewarrior.nodenet.common.entity.EntityNode
import com.thecodewarrior.nodenet.common.node.Node
import com.thecodewarrior.nodenet.common.node.NodeConfig
import net.minecraft.util.EnumParticleTypes

class ParticleGeneratorNode(entity: EntityNode): Node<ParticleGeneratorNode.Config>(entity, Config()) {
    override fun clientTick() {
        if(inputs.any { it == RedstoneSignal.ON }) {
            entity.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, entity.posX, entity.posY, entity.posZ, 0.0, 0.1, 0.0)
        }
    }

    @Savable
    class Config(): NodeConfig<Config>() {
        override fun clone(): Config {
            return Config()
        }
    }
}