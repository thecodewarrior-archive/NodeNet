package com.thecodewarrior.nodenet.common.item

import com.teamwizardry.librarianlib.features.base.item.ItemMod
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

abstract class ItemNodeInteractor(name: String, vararg variants: String): ItemMod(name, *variants) {
    override fun onItemRightClick(worldIn: World?, playerIn: EntityPlayer?, handIn: EnumHand?): ActionResult<ItemStack> {
        return super.onItemRightClick(worldIn, playerIn, handIn)
    }

    override fun onItemUse(player: EntityPlayer?, worldIn: World?, pos: BlockPos?, hand: EnumHand?, facing: EnumFacing?, hitX: Float, hitY: Float, hitZ: Float): EnumActionResult {
        return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ)
    }
}