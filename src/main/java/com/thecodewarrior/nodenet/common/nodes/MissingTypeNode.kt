package com.thecodewarrior.nodenet.common.nodes

import com.thecodewarrior.nodenet.common.entity.EntityNode
import com.thecodewarrior.nodenet.common.node.NoConfig
import com.thecodewarrior.nodenet.common.node.Node
import net.minecraft.util.EnumParticleTypes

class MissingTypeNode(entity: EntityNode): Node<NoConfig>(entity, NoConfig()) {

}