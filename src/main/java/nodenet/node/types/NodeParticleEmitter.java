package nodenet.node.types;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.Vec3d;

import nodenet.Const;
import nodenet.gui.client.GuiNodeParticleEmitter;
import nodenet.node.EntityNodeBase;
import nodenet.node.NodeBase;
import nodenet.node.net.InputPort;
import nodenet.node.port.IntInput;
import scala.actors.threadpool.Arrays;

public class NodeParticleEmitter extends NodeBase {

	IntInput input = new IntInput(15);
	boolean isFire = false;
	
	public NodeParticleEmitter(EntityNodeBase entity) {
		super(entity);
		input.needsClientUpdate(true);
	}
	
	@Override
	public GuiScreen createGui(NBTTagCompound tag) {
		return new GuiNodeParticleEmitter(entity.getEntityId(), tag);
	}
	
	@Override
	public NBTTagCompound getSettings() {
		NBTTagCompound tag = super.getSettings();
		tag.setBoolean("FIRE", isFire);
		return tag;
	}
	
	@Override
	public void updateSettings(NBTTagCompound tag) {
		super.updateSettings(tag);
		isFire = tag.getBoolean("FIRE");
	}
	
	@Override
	public List<InputPort> inputs() {
		return Arrays.asList(new InputPort[] { input });
	}
	
	@Override
	public void clientTick() {
		if(input.getValue() <= 0)
			return;
		Vec3d look = entity.getLook(1).scale(0.25);
		for (int i = 0; i < Const.RAND().nextInt(5); i++) {
			entity.worldObj.spawnParticle(isFire ? EnumParticleTypes.FLAME : EnumParticleTypes.SMOKE_NORMAL,
					entity.posX+rand(-0.0625, 0.0625), entity.posY+rand(-0.0625,  0.0625 ), entity.posZ+rand(-0.0625,  0.0625 ),
					look.xCoord+rand(-0.03125, 0.03125), look.yCoord+rand(-0.03125, 0.03125), look.zCoord+rand(-0.03125, 0.03125), new int[0]);
		}
	}
	
	public double rand(double min, double max) {
		return ( Const.RAND().nextDouble()*(max-min) )+min;
	}

}
