package com.thecodewarrior.nodenet.common.item

import com.thecodewarrior.nodenet.common.node.NodeTraceResult
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

/**
 * These methods are only called on the client
 */
interface INodeInteractingItem: INodeVisibleItem {
    @SideOnly(Side.CLIENT)
    fun rightClickBegan(node: NodeTraceResult?) {}
    @SideOnly(Side.CLIENT)
    fun rightClickTick(node: NodeTraceResult?) {}
    @SideOnly(Side.CLIENT)
    fun rightClickEnded(node: NodeTraceResult?) {}

    @SideOnly(Side.CLIENT)
    fun leftClickBegan(node: NodeTraceResult?) {}
    @SideOnly(Side.CLIENT)
    fun leftClickTick(node: NodeTraceResult?) {}
    @SideOnly(Side.CLIENT)
    fun leftClickEnded(node: NodeTraceResult?) {}

    @SideOnly(Side.CLIENT)
    fun middleClickBegan(node: NodeTraceResult?) {}
    @SideOnly(Side.CLIENT)
    fun middleClickTick(node: NodeTraceResult?) {}
    @SideOnly(Side.CLIENT)
    fun middleClickEnded(node: NodeTraceResult?) {}
}