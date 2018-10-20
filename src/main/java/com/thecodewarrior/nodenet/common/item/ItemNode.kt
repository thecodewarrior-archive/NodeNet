package com.thecodewarrior.nodenet.common.item

import com.teamwizardry.librarianlib.features.base.item.ItemMod
import com.teamwizardry.librarianlib.features.kotlin.NBT
import com.teamwizardry.librarianlib.features.kotlin.nbt
import com.teamwizardry.librarianlib.features.kotlin.toRl
import com.thecodewarrior.nodenet.common.entity.EntityNode
import com.thecodewarrior.nodenet.common.node.Node
import com.thecodewarrior.nodenet.common.node.NodeType
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagString
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.NonNullList
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class ItemNode: ItemMod("node"), INodeVisibleItem {

    override fun getSubItems(tab: CreativeTabs, subItems: NonNullList<ItemStack>) {
        if (!isInCreativeTab(tab)) return
        subItems.addAll(NodeType.REGISTRY.mapNotNull {
            if(it.registryName == "missingno".toRl()) return@mapNotNull null
            val stack = ItemStack(this, 1)
            stack.nbt["type"] = NBTTagString(it.registryName.toString())
            stack
        })
    }

    override fun getUnlocalizedName(stack: ItemStack): String {
        return "item.nodenet:node.${(stack.nbt["type"] as? NBTTagString)?.string}"
    }

    override fun onItemUse(player: EntityPlayer, worldIn: World, pos: BlockPos, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): EnumActionResult {
        if(!worldIn.isRemote) {
            val entity = EntityNode(worldIn, pos.x + hitX.toDouble(), pos.y + hitY.toDouble(), pos.z + hitZ.toDouble(),
                (player.getHeldItem(hand).nbt["type"] as NBTTagString).string.toRl()
            )
            worldIn.spawnEntity(entity)
        }
        return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ)
    }
}