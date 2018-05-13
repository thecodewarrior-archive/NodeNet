package com.thecodewarrior.nodenet

import net.minecraft.client.renderer.GlStateManager

inline fun glMatrix(block: () -> Unit) {
    GlStateManager.pushMatrix()
    block()
    GlStateManager.popMatrix()
}