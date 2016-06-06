package nodenet.network.messages;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import net.minecraft.entity.Entity;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;

import io.netty.buffer.ByteBuf;
import nodenet.network.NetworkHandler;
import nodenet.node.EntityNodeBase;

public class PacketNodeInteract implements IMessage {
    
    protected int id, hit, data;
    
    public PacketNodeInteract() { }

    public PacketNodeInteract(int id, int hit, int data) {
    	this.id = id;
    	this.hit = hit;
    	this.data = data;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        id = buf.readInt();
        hit = buf.readInt();
        data = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
    	buf.writeInt(id);
    	buf.writeInt(hit);
    	buf.writeInt(data);
    }

    public static class Handler implements IMessageHandler<PacketNodeInteract, IMessage> {
    	
        @Override
        public IMessage onMessage(PacketNodeInteract message, MessageContext ctx) {
            IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj;
            mainThread.addScheduledTask(() -> {
            	NetworkHandler.notifyPacketHandling("NodeInteract");
            	Entity plainentity = ctx.getServerHandler().playerEntity.worldObj.getEntityByID(message.id);
            	if(!( plainentity instanceof EntityNodeBase ))
            		return;
            	EntityNodeBase entity = (EntityNodeBase) plainentity;
            	entity.onRightClick(ctx.getServerHandler().playerEntity, message.hit, message.data);
            });
            return null;
        }
    }
}
