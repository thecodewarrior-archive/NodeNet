package com.thecodewarrior.nodenet.client

import com.thecodewarrior.nodenet.common.CommonProxy
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent

class ClientProxy: CommonProxy() {
    override fun pre(event: FMLPreInitializationEvent) {
        super.pre(event)
        NodeWorldRenderer
//        RenderingRegistry.registerEntityRenderingHandler(EntityNode::class.java, { RenderNode(it) })
    }
}