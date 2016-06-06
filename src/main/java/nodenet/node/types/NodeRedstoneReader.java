package nodenet.node.types;

import java.util.List;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import nodenet.node.EntityNodeBase;
import nodenet.node.NodeBase;
import nodenet.node.net.OutputPort;
import nodenet.node.port.IntOutput;
import scala.actors.threadpool.Arrays;

public class NodeRedstoneReader extends NodeBase {
	
	public IntOutput value;
	
	public NodeRedstoneReader(EntityNodeBase entity) {
		super(entity);
		value = new IntOutput(0, this);
	}
	
	@Override
	public List<OutputPort> outputs() {
		return Arrays.asList(new OutputPort[] { value });
	}
	
	@Override
	public int getColor() {
		if(value.getValue() > 0) {
			return 0xFF0000;
		} else {
			return 0x7F0000;
		}
	}
	
	@Override
	public void serverTick() {
		Vec3d pos = entity.getPositionVector().subtract( entity.getLook(1).scale(0.125) );
		BlockPos bpos = new BlockPos(pos);
		value.setValue(entity.worldObj.getRedstonePower(bpos, EnumFacing.UP));
	}
	
}
