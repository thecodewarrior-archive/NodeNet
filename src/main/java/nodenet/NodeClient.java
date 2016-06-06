package nodenet;

import java.lang.ref.WeakReference;

import nodenet.node.EntityNodeBase;

public class NodeClient {
	
	private static WeakReference<EntityNodeBase> sel = new WeakReference<>(null);
	
	public static EntityNodeBase getSel() {
		return sel.get();
	}
	
	public static void setSel(EntityNodeBase node) {
		sel = new WeakReference<EntityNodeBase>(node);
	}
	
	public static boolean isRelocating = false;
	
	public static int outputIndex = -1;
	
}
