package nodenet.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;

import nodenet.NodeClient;
import nodenet.network.NetworkHandler;
import nodenet.network.messages.PacketNodeConnect;
import nodenet.raytrace.RayTraceUtil.ITraceResult;
import nodenet.raytrace.node.NodeHit;

public class ItemNodeConnector extends ItemNodeBase {

	public ItemNodeConnector() {
		super("nodeConnector");
		setMaxStackSize(1);
	}

	@Override
	public boolean leftClickNodeClient(ITraceResult<NodeHit> hit, ItemStack stack, EntityPlayer player) {
		NodeClient.setSel(hit.data().node);
		if(hit.data().node.getNode().outputs().size() != 0)
			NodeClient.outputIndex = 0;
		return false;
	}

	@Override
	public EnumActionResult rightClickNodeClient(ITraceResult<NodeHit> hit, ItemStack stack, EntityPlayer player) {
		if( NodeClient.getSel() != null &&
			NodeClient.getSel() != hit.data().node &&
			NodeClient.outputIndex >= 0) {
			
			NetworkHandler.network.sendToServer(new PacketNodeConnect(
					NodeClient.getSel().getEntityId(), NodeClient.outputIndex,
					hit.data().node.getEntityId(), 0));
			return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.FAIL;
	}
	

}
