package nodenet.network.messages;

import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;

import io.netty.buffer.ByteBuf;
import nodenet.network.NetworkHandler;
import nodenet.node.EntityNodeBase;

public class PacketNodeSettingsUpdate implements IMessage {
	
	protected int id;
	protected NBTTagCompound tag;
	
    public PacketNodeSettingsUpdate() { }

    public PacketNodeSettingsUpdate(int id, NBTTagCompound tag) {
    	this.id = id;
    	this.tag = tag;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
    	id = buf.readInt();
    	tag = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
    	buf.writeInt(id);
    	ByteBufUtils.writeTag(buf, tag);
    }

    public static class Handler implements IMessageHandler<PacketNodeSettingsUpdate, IMessage> {
    	
        @Override
        public IMessage onMessage(PacketNodeSettingsUpdate message, MessageContext ctx) {
            IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj;
            mainThread.addScheduledTask(() -> {
            	NetworkHandler.notifyPacketHandling("NodeSettingsUpdate");
            	Entity plainentity = ctx.getServerHandler().playerEntity.worldObj.getEntityByID(message.id);
            	if(!( plainentity instanceof EntityNodeBase ))
            		return;
            	EntityNodeBase entity = (EntityNodeBase) plainentity;
            	entity.getNode().updateSettings(message.tag);
            });
            return null;
        }
    }
}
