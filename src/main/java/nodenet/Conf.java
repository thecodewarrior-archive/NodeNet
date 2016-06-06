package nodenet;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Conf {
	
	public static boolean logPackets = false;
	
	public static File file;
	public static Configuration config;
	
	public static final String CATEGORY_GENERAL = "general";
	public static final String CATEGORY_DEV = "developer options";
	
	@SubscribeEvent
    public void onConfigChangedEvent(OnConfigChangedEvent event)
    {
        if (Const.MODID.equals(event.getModID()))
        {
            loadConfigs(config);
        }
    }

    public static void loadConfigsFromFile(File configFile)
    {
        file = configFile;
        config = new Configuration(configFile);
        config.load();

        loadConfigs(config);
    }

    public static void loadConfigs(Configuration conf)
    {
    	Property prop;
    	
    	prop = conf.get(CATEGORY_DEV, "Log packet handling", false).setRequiresMcRestart(false);
    	prop.setComment("Useful to see if more packets are being sent than needed");
    	logPackets = (boolean)prop.getBoolean();
    	
        if (conf.hasChanged() == true)
        {
            conf.save();
            NodeNet.proxy.reloadConfigs();
        }
    }

	
}
