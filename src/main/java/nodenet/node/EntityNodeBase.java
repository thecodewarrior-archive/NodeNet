package nodenet.node;

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

import javax.annotation.Nullable;

import io.netty.buffer.ByteBuf;
import nodenet.Const;
import nodenet.Matrix4;
import nodenet.NodeClient;
import nodenet.NodeNet;
import nodenet.item.ItemNodeBase;
import nodenet.network.NetworkHandler;
import nodenet.network.messages.PacketClientPortConnection;
import nodenet.network.messages.PacketUpdateNode;
import nodenet.network.messages.PacketUpdatePort;
import nodenet.node.NodeUtil.EnumNodes;
import nodenet.node.net.InputPort;
import nodenet.node.net.OutputPort;
import nodenet.raytrace.CustomAABBCollide;
import nodenet.raytrace.RayTraceUtil;
import nodenet.raytrace.RayTraceUtil.ITraceResult;
import nodenet.raytrace.RayTraceUtil.ITraceable;
import nodenet.raytrace.RayTraceUtil.SimpleTraceResult;
import nodenet.raytrace.node.NodeHit;
import nodenet.raytrace.node.NodeTraceable;
import nodenet.raytrace.primitives.Box;
import nodenet.raytrace.primitives.TexCoords;
import nodenet.register.ItemRegister;
import nodenet.util.GeneralUtil;

public class EntityNodeBase extends Entity implements IEntityAdditionalSpawnData {

	public static double SIZE = 0.25;
	public int destroyTimer;
	
	protected boolean isPosDirty = false;
	protected NodeBase node;
	protected EnumNodes nodeType;
	
	public EntityNodeBase(World worldIn) {
		super(worldIn);
	}
	
	public EntityNodeBase(World worldIn, double x, double y, double z, EnumNodes node) {
		this(worldIn);
		setPosition(x, y, z);
		nodeType = node;
		this.node = nodeType.create(this);
	}

	@Override
	public void onUpdate() {
		if(destroyTimer > 0)
			destroyTimer--;
		if(this.worldObj.isRemote) {
			node.clientTick();
		} else {
			node.serverTick();
		}
		
		if(!worldObj.isRemote) {
			int i = 0;
			for(OutputPort<?> port : node.outputs()) {
				if(port.isModified()) {
					PacketBuffer buf = NetworkHandler.createBuffer();
					port.writeValueToBuf(buf);
					firePacket(new PacketUpdatePort(this, true, i, buf));
					if( port.updateConnected(this.worldObj) )
						firePacket(new PacketClientPortConnection(this.getEntityId(), i, port.connectedPoints()));
					port.resetModified();
				}
				i++;
			}
			for(InputPort<?> port : node.inputs()) {
				if(port.isModified() && port.needsClientUpdate()) {
					PacketBuffer buf = NetworkHandler.createBuffer();
					port.writeToBuf(buf);
					firePacket(new PacketUpdatePort(this, false, i, buf));
					port.resetModified();
				}
				i++;
			}
		}
	}
	
	@Override
	public void onEntityUpdate() {
		// NOOP
	}
	
	@Override
	public void onChunkLoad() {
		node.onLoad();
	}
	
	public NodeBase getNode() {
		return node;
	}

	public boolean clientRightClick(EntityPlayer player, int hit) {
		return false;
	}
	
	public boolean clientLeftClick(EntityPlayer player, int hit) {
		return false;
	}
	
	public void onLeftClick(EntityPlayer player, int hit) {
		if(destroyTimer > 0 && destroyTimer < 9) {
			this.kill();
		} else {
			destroyTimer = 10;
			sendNodeUpdate();
		}
	}
	
	public void onRightClick(EntityPlayer player, int hit, int data) {
		if(worldObj.isRemote) {
			return;
		}
		
		if(hit == 0)
			return;
		
		if(hit == Const.NODE.PITCH) {
			this.rotationPitch = data;
		}
		
		if(hit == Const.NODE.YAW) {
			this.rotationYaw = data;
		}
		
		if(this.rotationPitch < -90) {
			float diff = this.rotationPitch+90;
			this.rotationPitch -= 2* diff;
			this.rotationYaw += 180;
		}
		if(this.rotationPitch > 90) {
			float diff = this.rotationPitch-90;
			this.rotationPitch -= 2* diff;
			this.rotationYaw += 180;
		}
		
		this.rotationYaw = this.rotationYaw % 360;
		
		sendNodeUpdate();
	}
	
	public ITraceResult<NodeHit> rayTraceNode(@Nullable EntityPlayer player, Vec3d start, Vec3d end) {
		if(!player.worldObj.isRemote)
			return RayTraceUtil.<ITraceResult<NodeHit>>miss(); // we only want to raytrace on the client, as only they have all the information
		
		if(player != null) {
			if(!GeneralUtil.isHolding(player, (stack) -> stack.getItem() instanceof ItemNodeBase)) {
				return RayTraceUtil.<ITraceResult<NodeHit>>miss();
			}
		}
		
		ITraceResult<NodeHit> hit = RayTraceUtil.<ITraceResult<NodeHit>>miss();
		
		if(GeneralUtil.isHolding(player, (stack) -> stack.getItem() == ItemRegister.nodeManipulator) && NodeClient.getSel() == this)
			hit = RayTraceUtil.min(hit, rayTraceRotRings(player, start, end));
		
		hit = RayTraceUtil.min(hit, rayTraceBox(player, start, end));
		
		return hit;
	}
	
	public ITraceResult<NodeHit> rayTraceBox(EntityPlayer player, Vec3d start, Vec3d end) {
		Vec3d relStart = start.subtract(posX, posY, posZ);
		Vec3d relEnd   =   end.subtract(posX, posY, posZ);
		
		Matrix4 matrix = new Matrix4();
		Matrix4 invmatrix = new Matrix4();
		
		   matrix.rotate(Math.toRadians( this.rotationPitch ), new Vec3d( 1, 0, 0));
		invmatrix.rotate(Math.toRadians( this.rotationPitch ), new Vec3d(-1, 0, 0));
		   matrix.rotate(Math.toRadians( this.rotationYaw ),   new Vec3d(0,  1, 0));
		invmatrix.rotate(Math.toRadians( this.rotationYaw ),   new Vec3d(0, -1, 0));
			
		relStart = matrix.apply(relStart);
		relEnd = matrix.apply(relEnd);
		
		double s = SIZE/2;
		AxisAlignedBB aabb = new AxisAlignedBB(-s, -s, -s, s, s, s);
		RayTraceResult boxHit = aabb.calculateIntercept(relStart, relEnd);
		
		if(boxHit != null) {
			return new SimpleTraceResult<NodeHit>(start, invmatrix.apply(boxHit.hitVec).addVector(posX, posY, posZ), new NodeHit(this, 0, boxHit.sideHit.ordinal()));
		}
		
		return null;
	}
	
	public ITraceResult<NodeHit> rayTraceRotRings(EntityPlayer player, Vec3d start, Vec3d end) {
		@SuppressWarnings("unchecked")
		ITraceResult<NodeHit> hit = (ITraceResult<NodeHit>) RayTraceUtil.MISS_RESULT;
		
		Vec3d relStart = start.subtract(posX, posY, posZ);
		Vec3d relEnd   =   end.subtract(posX, posY, posZ);
		
		Vec3d yawHit = relEnd.getIntermediateWithYValue(relStart, 0);
		
		if(yawHit != null) {
			double len = yawHit.lengthVector();
			if(len > 0.375 && len < 0.5) {
				double angle = Math.toDegrees(Math.atan2(yawHit.xCoord, yawHit.zCoord));
				hit = RayTraceUtil.min(hit, new SimpleTraceResult<NodeHit>(start, yawHit.addVector(posX, posY, posZ), new NodeHit(this, Const.NODE.YAW, -(int)angle)));
			}
		}
		
		Matrix4 matrix = new Matrix4();
		matrix.rotate(Math.toRadians( this.rotationYaw ), new Vec3d(0, 1, 0));
		Matrix4 invmatrix = new Matrix4();
		invmatrix.rotate(Math.toRadians( this.rotationYaw ), new Vec3d(0, -1, 0));
		
		relStart = matrix.apply(relStart);
		relEnd = matrix.apply(relEnd);
		
		Vec3d pitchHit = relEnd.getIntermediateWithXValue(relStart, 0);
		
		if(pitchHit != null) {
			double len = pitchHit.lengthVector();
			if(len > 0.375 && len < 0.5) {
				double angle = Math.toDegrees(Math.atan2(pitchHit.yCoord, pitchHit.zCoord));
				ITraceResult<NodeHit> h = new SimpleTraceResult<NodeHit>(start, invmatrix.apply(pitchHit).addVector(posX, posY, posZ), new NodeHit(this, Const.NODE.PITCH, -(int)angle));
				hit = RayTraceUtil.min(hit, h);
			}
		}
		
		return hit;
	}
	
	List<NodeTraceable> traces;
	NodeTraceable box;
	
	public List<NodeTraceable> baseHits() {
		initTraces();
		return traces;
	}
	
	public void initTraces() {

		traces = new ArrayList<>();
		
		double s = SIZE/2;
		box = new NodeTraceable(0,
			new Box(
				new Vec3d(-s, -s, -s),
				new Vec3d( s,  s,  s)
			), TexCoords.NULL
		);
			
	}
	
	@Override
	public AxisAlignedBB getEntityBoundingBox() {
		return super.getEntityBoundingBox();
	}
	
	@Override
	public boolean canBeCollidedWith() {
		return true;
	}
	
	@Override
	public void setEntityBoundingBox(AxisAlignedBB bb) {
		final EntityNodeBase self = this;
		super.setEntityBoundingBox(new CustomAABBCollide(bb,
			new ITraceable<Object, NodeHit>() {

				@Override
				public ITraceResult<NodeHit> trace(Vec3d start, Vec3d end, Object param) {
					return self.rayTraceNode(NodeNet.proxy.getPlayerLooking(start, end), start, end);
				}
				
			}
		));
	}
	
	@Override
	protected void entityInit() {
		setEntityInvulnerable(true);
		setSize(0, 0);
		initTraces();
		this.ignoreFrustumCheck = true;
//		this.node = new NodeParticleEmitter(this);
	}
	
	@SideOnly(Side.CLIENT)
    public int getBrightnessForRender(float partialTicks) {
        return 15 << 20 | 15 << 4;
	}
    public float getBrightness(float partialTicks) { return 15; }
    @SideOnly(Side.CLIENT)
    public boolean isInRangeToRenderDist(double distance)
    {
        double dist = 32;
        return distance < dist*dist;
    }

	public ITextComponent getNodeName() {
		return new TextComponentString("ASDF");
	}
	
	public void sendNodeUpdate() {
		if(!worldObj.isRemote) {
			firePacket(new PacketUpdateNode(this));
		}
	}
	
	public void firePacket(IMessage message) {
		NetworkHandler.network.sendToAllAround(message, new TargetPoint(worldObj.provider.getDimension(), posX, posY, posZ, 32));
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		nodeType = EnumNodes.values()[ compound.getInteger("nodeID") ];
		if(node == null)
			node = nodeType.create(this);
		node.readFromNBT(compound.getCompoundTag("node"));
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		NBTTagCompound nodeTag = new NBTTagCompound();
		node.saveToNBT(nodeTag);
		
		compound.setInteger("nodeID", nodeType.ordinal());
		compound.setTag("node", nodeTag);
	}

	@Override
	public void writeSpawnData(ByteBuf buffer) {
		buffer.writeInt(nodeType.ordinal());
		node.writeSpawnData(buffer);
	}

	@Override
	public void readSpawnData(ByteBuf additionalData) {
		nodeType = EnumNodes.values()[ additionalData.readInt() ];
		node = nodeType.create(this);
		node.readSpawnData(additionalData);
	}

}
