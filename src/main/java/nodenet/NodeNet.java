package nodenet;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import net.minecraft.entity.player.EntityPlayer;

import mcjty.lib.McJtyLib;
import mcjty.lib.base.ModBase;
import mcjty.lib.varia.WrenchChecker;
import nodenet.gui.GuiHandler;
import nodenet.network.NetworkHandler;
import nodenet.node.NodeUtil;
import nodenet.proxy.ModProxy;
import nodenet.register.BlockRegister;
import nodenet.register.ItemRegister;
import nodenet.register.NodeRegister;

@Mod(modid = Const.MODID, version = Const.VERSION)
public class NodeNet implements ModBase{

	@SidedProxy(clientSide="nodenet.proxy.ClientProxy", serverSide="nodenet.proxy.ServerProxy")
	public static ModProxy proxy;
	
	@Instance
	public static NodeNet INSTANCE;
	
	@EventHandler
    public void preInit(FMLPreInitializationEvent event) {
    	BlockRegister.register();
    	ItemRegister.register();
    	NodeRegister.register();
    	
    	NetworkHandler.init();
    	McJtyLib.preInit(event);
    	
    	Conf.loadConfigsFromFile(event.getSuggestedConfigurationFile());
    	MinecraftForge.EVENT_BUS.register(proxy);
    	proxy.preInit();
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event) {
    	WrenchChecker.init();
    	// just to load the classes and their instances
		NodeUtil.INSTANCE.getClass();
		GuiHandler.INSTANCE.getClass();
    }
    

	@Override
	public String getModId() {
		return Const.MODID;
	}

	@Override
	public void openManual(EntityPlayer player, int bookindex, String page) {
		
	}
}
