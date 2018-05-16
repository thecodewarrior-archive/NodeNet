package com.thecodewarrior.nodenet.common.item

import com.teamwizardry.librarianlib.features.base.item.ItemMod
import com.teamwizardry.librarianlib.features.kotlin.minus
import com.teamwizardry.librarianlib.features.kotlin.plus
import com.teamwizardry.librarianlib.features.kotlin.times
import com.teamwizardry.librarianlib.features.kotlin.unaryMinus
import com.teamwizardry.librarianlib.features.network.PacketHandler
import com.thecodewarrior.nodenet.client.NodeManipulatorHandle
import com.thecodewarrior.nodenet.client.NodeTraceResult
import com.thecodewarrior.nodenet.client.Ray
import com.thecodewarrior.nodenet.common.entity.EntityNode
import com.thecodewarrior.nodenet.common.network.PacketDeleteNode
import com.thecodewarrior.nodenet.rotationPitch
import com.thecodewarrior.nodenet.rotationYaw
import net.minecraft.client.Minecraft
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import net.minecraftforge.client.model.animation.Animation

class ItemNodeManipulator: ItemMod("manipulator"), INodeInteractingItem {
    var manipulatingNode: EntityNode? = null
    var manipulatingHandle: NodeManipulatorHandle? = null

    override fun leftClickBegan(node: NodeTraceResult?) {
        if(node != null) {
            manipulatingNode = node.entity
        }
    }

    override fun rightClickBegan(node: NodeTraceResult?) {
        if(node?.entity == manipulatingNode && node?.handle != null) {
            manipulatingHandle = node.handle
            manipulatingHandle?.storeDragRelativeState(1f)
        }
    }

    override fun rightClickEnded(node: NodeTraceResult?) {
        manipulatingHandle = null
    }
}