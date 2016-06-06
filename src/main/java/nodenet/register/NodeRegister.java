package nodenet.register;

import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

import nodenet.NodeNet;
import nodenet.node.EntityNodeBase;
import nodenet.node.render.RenderNode;

public class NodeRegister {

	public static void register() {
		int id = 0;
		EntityRegistry.registerModEntity(EntityNodeBase.class, "node", id++, NodeNet.INSTANCE, 32, Integer.MAX_VALUE, false);
	}
	
	public static void initRender() {
		RenderingRegistry.registerEntityRenderingHandler(EntityNodeBase.class, (manager) -> new RenderNode(manager));
	}
	
}
