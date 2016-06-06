package nodenet.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import nodenet.NodeClient;
import nodenet.network.NetworkHandler;
import nodenet.network.messages.PacketNodeClick;
import nodenet.network.messages.PacketNodeInteract;
import nodenet.node.NodeUtil;
import nodenet.raytrace.RayTraceUtil.ITraceResult;
import nodenet.raytrace.node.NodeHit;

public abstract class ItemNodeBase extends ItemBase {

	public ItemNodeBase(String name) {
		super(name);
	}

	@Override
	public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack) {
		if(entityLiving instanceof EntityPlayer && entityLiving.worldObj.isRemote) {			
			ITraceResult<NodeHit> result = NodeUtil.nodeHit;
			if(result == null || result.data() == null)
				return super.onEntitySwing(entityLiving, stack);
			
			boolean ret = leftClickNodeClient(result, stack, (EntityPlayer)entityLiving);
			if(!ret) {
				NetworkHandler.network.sendToServer(new PacketNodeClick(result.data().node.getEntityId(), result.data().hit));
				if(NodeClient.getSel() != result.data().node) {
					NodeClient.setSel(result.data().node);
					NodeClient.outputIndex = -1;
					NodeClient.isRelocating = false;
				}
			}
			return ret;
		}
		return super.onEntitySwing(entityLiving, stack);
	}
	
	public abstract boolean leftClickNodeClient(ITraceResult<NodeHit> hit, ItemStack stack, EntityPlayer player);
	public abstract EnumActionResult rightClickNodeClient(ITraceResult<NodeHit> hit, ItemStack stack, EntityPlayer player);
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn,
			EnumHand hand) {
		if(worldIn.isRemote) {
			ITraceResult<NodeHit> result = NodeUtil.nodeHit;
			if(result == null || result.data() == null)
				return super.onItemRightClick(itemStackIn, worldIn, playerIn, hand);
			
			NetworkHandler.network.sendToServer(new PacketNodeInteract(result.data().node.getEntityId(), result.data().hit, result.data().data));
			
			ItemStack passStack = itemStackIn.copy();
			EnumActionResult res = rightClickNodeClient(result, passStack, playerIn);
			return new ActionResult<ItemStack>(res, passStack);
		}
		
		return new ActionResult<ItemStack>(EnumActionResult.PASS, itemStackIn);
	}
	
}
