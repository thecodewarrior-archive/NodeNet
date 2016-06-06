package nodenet.raytrace.node;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;

import nodenet.raytrace.RayTraceUtil.ITraceResult;
import nodenet.raytrace.RayTraceUtil.ITraceable;
import nodenet.raytrace.RayTraceUtil.SimpleTraceResult;
import nodenet.raytrace.RayTraceUtil.TraceablePrimitive;
import nodenet.raytrace.primitives.TexCoords;

public class NodeTraceable implements ITraceable<EntityPlayer, Integer> {

	int id;
	TraceablePrimitive<?> traceable;
	TexCoords uv;
	
	public NodeTraceable(int id, TraceablePrimitive<?> traceable, TexCoords uv) {
		this.id = id;
		this.traceable = traceable;
		this.uv = uv;
	}
	
	public int getId() {
		return id;
	}

	public TraceablePrimitive<?> getTraceable() {
		return traceable;
	}

	public TexCoords getUv() {
		return uv;
	}

	@Override
	public ITraceResult<Integer> trace(Vec3d start, Vec3d end, EntityPlayer param) {
		return new SimpleTraceResult<Integer>(traceable.trace(start, end), id);
	}

}
