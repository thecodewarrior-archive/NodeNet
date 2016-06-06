package nodenet;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.math.Vec3d;

import nodenet.register.ItemRegister;

public class Const {
	private Const() {}
	
	public static final String MODID = "nodenet";
    public static final String VERSION = "0.0.1";
    
    public static CreativeTabs TAB = new CreativeTabs("nodenet") {
		
		@Override
		public Item getTabIconItem() {
			return ItemRegister.nodeManipulator;
		}
	};
    
    public static boolean developmentEnvironment = (Boolean)Launch.blackboard.get("fml.deobfuscatedEnvironment");
    
    public static final Vec3d VEC_CENTER = new Vec3d(0.5, 0.5, 0.5);
    public static final Vec3d VEC_ANTICENTER = VEC_CENTER.scale(-1);
    public static Random RAND() { return ThreadLocalRandom.current(); }
    
    public static class GUI {
    	public static int NODE_MANIPULATOR;
    }
    
    public static class COMMAND {
    	private COMMAND() {}
    	public static final String OPTIONS = "options";
    }
    
    public static class NODE {
    	private NODE() {}
    	public static final int PITCH = 100;
    	public static final int YAW = 101;
    }
}
