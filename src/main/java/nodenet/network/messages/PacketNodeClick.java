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

public class PacketNodeClick implements IMessage {
    
    protected int id, hit;
    
    public PacketNodeClick() { }

    public PacketNodeClick(int id, int hit) {
    	this.id = id;
    	this.hit = hit;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        id = buf.readInt();
        hit = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
    	buf.writeInt(id);
    	buf.writeInt(hit);
    }

    public static class Handler implements IMessageHandler<PacketNodeClick, IMessage> {
    	
        @Override
        public IMessage onMessage(PacketNodeClick message, MessageContext ctx) {
            IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj;
            mainThread.addScheduledTask(() -> {
            	NetworkHandler.notifyPacketHandling("NodeClick");
            	Entity plainentity = ctx.getServerHandler().playerEntity.worldObj.getEntityByID(message.id);
            	if(!( plainentity instanceof EntityNodeBase ))
            		return;
            	EntityNodeBase entity = (EntityNodeBase) plainentity;
            	entity.onLeftClick(ctx.getServerHandler().playerEntity, message.hit);
            });
            return null;
        }
    }
}
