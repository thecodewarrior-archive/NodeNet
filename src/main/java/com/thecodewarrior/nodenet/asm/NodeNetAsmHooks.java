package com.thecodewarrior.nodenet.asm;

import com.thecodewarrior.nodenet.client.NodeInteractionClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SuppressWarnings("unused")
public class NodeNetAsmHooks {
    public static final NodeNetAsmHooks INSTANCE = new NodeNetAsmHooks();
    private static float x, y;

    @SideOnly(Side.CLIENT)
    public static boolean processKeyBinds() {
        return NodeInteractionClient.processKeyBinds();
    }

    @SideOnly(Side.CLIENT)
    public static void getMouseOver(float partialTicks) {
        NodeInteractionClient.getMouseOver(partialTicks);
    }

    @SideOnly(Side.CLIENT)
    public static boolean shouldSkipSettingObjectMouseOver(float partialTicks) {
        return NodeInteractionClient.shouldSkipSettingObjectMouseOver(partialTicks);
    }
}
