package com.thecodewarrior.nodenet.common.node

import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.plus
import com.teamwizardry.librarianlib.features.saving.Savable
import com.teamwizardry.librarianlib.features.saving.SaveInPlace
import com.thecodewarrior.nodenet.client.DraggingAxisNodeManipulatorHandle
import com.thecodewarrior.nodenet.client.NodeManipulatorHandle
import com.thecodewarrior.nodenet.client.NodeRenderer
import com.thecodewarrior.nodenet.client.Ray
import com.thecodewarrior.nodenet.client.RedstoneReaderNodeRenderer
import com.thecodewarrior.nodenet.common.entity.EntityNode
import com.thecodewarrior.nodenet.setPosition
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.awt.Color

@SaveInPlace
open class Node(val entity: EntityNode) {

    val handles = mutableListOf<NodeManipulatorHandle>(
            DraggingAxisNodeManipulatorHandle(this, Ray(vec(0, 0, 0), vec(1, 0, 0)),
                    Color.red, 0.5, 0.25) {
                val pos = entity.positionVector + it
                entity.setPosition(pos)
            },
            DraggingAxisNodeManipulatorHandle(this, Ray(vec(0, 0, 0), vec(0, 1, 0)),
                    Color.green, 0.5, 0.25) {
                val pos = entity.positionVector + it
                entity.setPosition(pos)
            },
            DraggingAxisNodeManipulatorHandle(this, Ray(vec(0, 0, 0), vec(0, 0, 1)),
                    Color.blue, 0.5, 0.25) {
                val pos = entity.positionVector + it
                entity.setPosition(pos)
            }
    )

    @SideOnly(Side.CLIENT)
    @JvmField
    var renderer: NodeRenderer? = RedstoneReaderNodeRenderer(entity)

    fun clientTick() {
    }

    fun serverTick() {
    }
}