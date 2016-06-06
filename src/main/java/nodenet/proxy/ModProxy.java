package nodenet.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;

public abstract class ModProxy {

	public abstract EntityPlayer getPlayerLooking(Vec3d start, Vec3d end);

	public abstract void reloadConfigs();

	public abstract void preInit();
	
}
