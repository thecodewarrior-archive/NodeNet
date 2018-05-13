package com.thecodewarrior.nodenet.client

import com.thecodewarrior.nodenet.common.CommonProxy
import com.thecodewarrior.nodenet.common.entity.EntityNodeBase
import net.minecraftforge.fml.client.registry.RenderingRegistry
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent

class ClientProxy: CommonProxy() {
    override fun pre(event: FMLPreInitializationEvent) {
        super.pre(event)
        RenderingRegistry.registerEntityRenderingHandler(EntityNodeBase::class.java, { RenderNode(it) })
    }
}