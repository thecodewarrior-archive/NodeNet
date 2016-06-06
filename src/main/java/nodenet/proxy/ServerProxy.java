package nodenet.proxy;

import java.util.List;

import net.minecraftforge.fml.server.FMLServerHandler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.Vec3d;

import nodenet.raytrace.RayTraceUtil;

public class ServerProxy extends ModProxy {

	@Override
	public EntityPlayer getPlayerLooking(Vec3d start, Vec3d end) {
		EntityPlayer player = null;
		List<EntityPlayerMP> players = FMLServerHandler.instance().getServer().getPlayerList().getPlayerList();
		
		for (final EntityPlayerMP p : players) { // for each player
			Vec3d lookStart = RayTraceUtil.getStartVec(p);
			Vec3d lookEnd   = RayTraceUtil.getEndVec(p);
			double lookDistance = RayTraceUtil.getBlockReachDistance(p);
			
			double dStart  = lookStart.distanceTo(start);
			double dEnd    = lookEnd  .distanceTo(start);
			
			double dStart_ = lookStart.distanceTo(end);
			double dEnd_   = lookEnd  .distanceTo(end);
			
			
			if(dStart + dEnd == lookDistance && dStart_ + dEnd_ == lookDistance) {
				player = p; break;
			}
		}
		return player;
	}

	@Override
	public void reloadConfigs() {}

	@Override
	public void preInit() {}

}
