package nodenet.register;

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;

import nodenet.item.ItemBase;
import nodenet.item.ItemNode;
import nodenet.item.ItemNodeConfigurator;
import nodenet.item.ItemNodeConnector;
import nodenet.item.ItemNodeManipulator;

public class ItemRegister {
	
	public static ItemNodeManipulator nodeManipulator;
	public static ItemNodeConnector nodeConnector;
	public static ItemNodeConfigurator nodeConf;
	public static ItemNode nodes;
	
	public static void register() {
		nodeManipulator = new ItemNodeManipulator();
		nodeConnector = new ItemNodeConnector();
		nodeConf = new ItemNodeConfigurator();
		nodes = new ItemNode();
	}
	
	public static List<ItemBase> renderRegsiterItems = new ArrayList<>();
	
	@SideOnly(Side.CLIENT)
	public static void initRender() {
		registerRender();
	}
	
	@SideOnly(Side.CLIENT)
	private static void registerRender() {		
		for (ItemBase item : renderRegsiterItems) {
			String[] customVariants = item.getCustomRenderVariants();
			if(customVariants == null) { 
				ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), ""));
			} else {
				for (int i = 0; i < customVariants.length; i++) {
					if( "".equals(customVariants[i]) ) {
						ModelLoader.setCustomModelResourceLocation(item, i, new ModelResourceLocation(item.getRegistryName(), ""));
					} else {
						ModelLoader.setCustomModelResourceLocation(item, i, new ModelResourceLocation(item.getRegistryName() + "." + customVariants[i], ""));
					}
				}
			}
//			item.initModel();
		}
	}
}
