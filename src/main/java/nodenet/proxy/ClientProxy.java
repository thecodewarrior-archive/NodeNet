package nodenet.proxy;

import net.minecraftforge.common.MinecraftForge;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;

import nodenet.Conf;
import nodenet.register.ItemRegister;
import nodenet.register.NodeRegister;
import nodenet.render.ShaderHelper;

public class ClientProxy extends ModProxy {

	@Override
	public EntityPlayer getPlayerLooking(Vec3d start, Vec3d end) {
		return Minecraft.getMinecraft().thePlayer;
	}

	@Override
	public void reloadConfigs() {}

	@Override
	public void preInit() {
		ItemRegister.initRender();
		NodeRegister.initRender();
		
		MinecraftForge.EVENT_BUS.register(new Conf());
		
		ShaderHelper.initShaders();
	}

}
