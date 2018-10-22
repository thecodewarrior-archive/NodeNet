package com.thecodewarrior.nodenet.client

import com.thecodewarrior.nodenet.client.render.NodeWorldRenderer
import com.thecodewarrior.nodenet.common.CommonProxy
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent

class ClientProxy: CommonProxy() {
    override fun pre(event: FMLPreInitializationEvent) {
        super.pre(event)
        NodeWorldRenderer
    }
}