package com.thecodewarrior.nodenet.common.item

import com.thecodewarrior.nodenet.client.NodeTraceResult
import com.thecodewarrior.nodenet.common.entity.EntityNode
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World

/**
 * These methods are only called on the client
 */
interface INodeInteractingItem: INodeVisibleItem {
    fun rightClickBegan(node: NodeTraceResult?) {}
    fun rightClickTick(node: NodeTraceResult?) {}
    fun rightClickEnded(node: NodeTraceResult?) {}

    fun leftClickBegan(node: NodeTraceResult?) {}
    fun leftClickTick(node: NodeTraceResult?) {}
    fun leftClickEnded(node: NodeTraceResult?) {}

    fun middleClickBegan(node: NodeTraceResult?) {}
    fun middleClickTick(node: NodeTraceResult?) {}
    fun middleClickEnded(node: NodeTraceResult?) {}
}