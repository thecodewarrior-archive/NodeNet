package nodenet.item;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import nodenet.Const;
import nodenet.NodeClient;
import nodenet.NodeNet;
import nodenet.gui.inventory.IInventoryContainerItem;
import nodenet.gui.inventory.ItemInventoryWrapper;
import nodenet.network.NetworkHandler;
import nodenet.network.messages.PacketNodeSettingsQuery;
import nodenet.node.EntityNodeBase;
import nodenet.node.NodeUtil.EnumNodes;
import nodenet.raytrace.RayTraceUtil.ITraceResult;
import nodenet.raytrace.node.NodeHit;
import nodenet.util.Logs;

public class ItemNodeConfigurator extends ItemNodeBase implements IInventoryContainerItem {

	public ItemNodeConfigurator() {
		super("nodeConfigurator");
		setMaxStackSize(1);
	}
	
	@Override
	public Object[] getInformationArguments(ItemStack stack, EntityPlayer player) {
		
		String nodeText = "NONE";
		
		int index = 0;
		if(stack.hasTagCompound()) {
			index = stack.getTagCompound().getInteger("selectedSlot");
		}
		
		IInventory inventory = new ItemInventoryWrapper(stack);
		ItemStack nodeStack = inventory.getStackInSlot(index);
		
		if(nodeStack != null) {
			if(nodeStack.getItemDamage() < EnumNodes.values().length) {
				EnumNodes type = EnumNodes.values()[nodeStack.getItemDamage()];
				nodeText = I18n.format("node." + type.toString() + ".name");
			}
		}
		
		int amountLeft = 0;
		
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack amountStack = inventory.getStackInSlot(i);
			if(amountStack != null && nodeStack != null && amountStack.getItemDamage() == nodeStack.getItemDamage()) {
				amountLeft += amountStack.stackSize;
			}
		}
		
		return new Object[] {
			nodeText, amountLeft
		};
	}
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		
		super.addInformation(stack, playerIn, tooltip, advanced);
	}

	@Override
	public boolean leftClickNodeClient(ITraceResult<NodeHit> hit, ItemStack stack, EntityPlayer player) {
		return false;
	}

	@Override
	public EnumActionResult rightClickNodeClient(ITraceResult<NodeHit> hit, ItemStack stack, EntityPlayer player) {
		if(NodeClient.getSel() == hit.data().node) {
			NetworkHandler.network.sendToServer(new PacketNodeSettingsQuery(hit.data().node.getEntityId()));
			return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.PASS;
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn,
			EnumHand hand) {
		ActionResult<ItemStack> res = super.onItemRightClick(itemStackIn, worldIn, playerIn, hand);
		if(playerIn.isSneaking() && res.getType() == EnumActionResult.PASS) {
			if (!worldIn.isRemote) {
	            playerIn.openGui(NodeNet.INSTANCE, Const.GUI.NODE_MANIPULATOR, playerIn.worldObj, (int) playerIn.posX, (int) playerIn.posY, (int) playerIn.posZ);
	        }
			res = new ActionResult<ItemStack>(EnumActionResult.SUCCESS, res.getResult());
		}
		return res;
	}
	
	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if(!playerIn.isSneaking() && !worldIn.isRemote) {
			int index = 0;
			if(stack.hasTagCompound()) {
				index = stack.getTagCompound().getInteger("selectedSlot");
			}
			
			IInventory inventory = new ItemInventoryWrapper(stack);
			ItemStack nodeStack = inventory.getStackInSlot(index);
			
			if(nodeStack != null) {
				if(nodeStack.getItemDamage() < EnumNodes.values().length) {
				
					EntityNodeBase entity = new EntityNodeBase(worldIn, pos.getX()+hitX, pos.getY()+hitY, pos.getZ()+hitZ, EnumNodes.values()[nodeStack.getItemDamage()]);
					switch (facing) {
					case NORTH:
						entity.rotationYaw = 180;
						break;
					case SOUTH:
						break;
					case EAST:
						entity.rotationYaw = -90;
						break;
					case WEST:
						entity.rotationYaw = 90;
						break;
					case UP:
						entity.rotationPitch = -90;
						break;
					case DOWN:
						entity.rotationPitch = 90;
						break;
		
					default:
						break;
					}
					
					worldIn.spawnEntityInWorld(entity);
					
					if(!playerIn.capabilities.isCreativeMode) {
						int targetDamage = nodeStack.getItemDamage();
						
						inventory.decrStackSize(index, 1);
						inventory.markDirty();
						
						if(nodeStack.stackSize == 0) {
							for (int i = 0; i < inventory.getSizeInventory(); i++) {
								if(i == index)
									continue;
								ItemStack checkStack = inventory.getStackInSlot(i);
								if(checkStack != null && checkStack.getItemDamage() == targetDamage) {
									stack.getTagCompound().setInteger("selectedSlot", i);
									break;
								}
							}
						}
					}
					
					return EnumActionResult.SUCCESS;
				} else {
					Logs.error("ERROR! Incorrect node item metadata! (%d, max is %d)", nodeStack.getItemDamage(), EnumNodes.values().length-1);
				} // damage < max
			} // stack != null
		}
		return super.onItemUse(stack, playerIn, worldIn, pos, hand, facing, hitX, hitY, hitZ);
	}

	
	@Override
	public int getInventorySize() {
		return 5;
	}

	@Override
	public String getGuiUnlocalizedName() {
		return "gui.title." + this.getUnlocalizedName().substring(5);
	}

}
