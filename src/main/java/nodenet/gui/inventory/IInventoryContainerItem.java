package nodenet.gui.inventory;

public interface IInventoryContainerItem {

	public String getGuiUnlocalizedName();
	public int getInventorySize();
	public default int getStackLimit() { return 64; }
	
}
