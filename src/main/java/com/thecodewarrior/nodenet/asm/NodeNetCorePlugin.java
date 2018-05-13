package com.thecodewarrior.nodenet.asm;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import java.util.Map;

//-Dfml.coreMods.load=com.thecodewarrior.nodenet.asm.NodeNetCorePlugin

@IFMLLoadingPlugin.Name("NodeNet Plugin")
@IFMLLoadingPlugin.TransformerExclusions("com.thecodewarrior.nodenet.asm")
@IFMLLoadingPlugin.SortingIndex(1001) // After runtime deobfuscation
public class NodeNetCorePlugin implements IFMLLoadingPlugin {

    public static boolean runtimeDeobf = false;

    @Override
    public String[] getASMTransformerClass() {
        return new String[]{
                "com.thecodewarrior.nodenet.asm.NodeNetTransformer"
        };
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
        runtimeDeobf = (Boolean) data.get("runtimeDeobfuscationEnabled");
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
