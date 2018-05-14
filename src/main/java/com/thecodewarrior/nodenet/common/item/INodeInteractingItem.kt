package com.thecodewarrior.nodenet.common.item

import com.thecodewarrior.nodenet.common.entity.EntityNode
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World

/**
 * These methods are only called on the client
 */
interface INodeInteractingItem: INodeVisibleItem {
    fun rightClickBegan(node: EntityNode?) {}
    fun rightClickTick(node: EntityNode?) {}
    fun rightClickEnded(node: EntityNode?) {}

    fun leftClickBegan(node: EntityNode?) {}
    fun leftClickTick(node: EntityNode?) {}
    fun leftClickEnded(node: EntityNode?) {}

    fun middleClickBegan(node: EntityNode?) {}
    fun middleClickTick(node: EntityNode?) {}
    fun middleClickEnded(node: EntityNode?) {}
}