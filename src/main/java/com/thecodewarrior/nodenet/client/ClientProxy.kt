package com.thecodewarrior.nodenet.client

import com.teamwizardry.librarianlib.features.kotlin.toRl
import com.thecodewarrior.nodenet.client.render.NodeWorldRenderer
import com.thecodewarrior.nodenet.client.render.node.NodeRenderer
import com.thecodewarrior.nodenet.common.CommonProxy
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.registries.RegistryBuilder

class ClientProxy: CommonProxy() {
    override fun pre(event: FMLPreInitializationEvent) {
        super.pre(event)
        NodeWorldRenderer
    }
}