package com.thecodewarrior.nodenet.common.item

import com.teamwizardry.librarianlib.features.base.item.ItemMod
import com.thecodewarrior.nodenet.common.entity.EntityNodeBase
import com.thecodewarrior.nodenet.common.node.Node
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class ItemNode: ItemMod("node") {
    override fun onItemUse(player: EntityPlayer, worldIn: World, pos: BlockPos, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): EnumActionResult {
        if(!worldIn.isRemote) {
            val entity = EntityNodeBase(worldIn, pos.x + hitX.toDouble(), pos.y + hitY.toDouble(), pos.z + hitZ.toDouble(), Node())
            worldIn.spawnEntity(entity)
        }
        return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ)
    }
}