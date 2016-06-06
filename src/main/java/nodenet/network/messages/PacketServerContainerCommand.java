package nodenet.network.messages;

import java.util.HashMap;
import java.util.Map;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;

import io.netty.buffer.ByteBuf;
import mcjty.lib.network.AbstractServerCommand;
import mcjty.lib.network.Argument;
import mcjty.lib.network.NetworkTools;
import nodenet.gui.CommandContainer;
import nodenet.network.NetworkHandler;

public class PacketServerContainerCommand implements IMessage {
	
    protected String command;
    protected Map<String,Argument> args;
	
    public PacketServerContainerCommand() { }

    public PacketServerContainerCommand(String command, Argument... arguments) {
    	this.command = command;
        if (arguments == null) {
            this.args = null;
        } else {
            this.args = new HashMap<String, Argument>(arguments.length);
            for (Argument arg : arguments) {
                args.put(arg.getName(), arg);
            }
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        command = NetworkTools.readString(buf);
        args = AbstractServerCommand.readArguments(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
    	NetworkTools.writeString(buf, command);
    	AbstractServerCommand.writeArguments(buf, args);
    }

    public static class Handler implements IMessageHandler<PacketServerContainerCommand, IMessage> {
    	
        @Override
        public IMessage onMessage(PacketServerContainerCommand message, MessageContext ctx) {
            IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj;
            mainThread.addScheduledTask(() -> {
            	NetworkHandler.notifyPacketHandling("ServerContainerCommand");
            	if(ctx.getServerHandler().playerEntity.openContainer instanceof CommandContainer) {
            		((CommandContainer)ctx.getServerHandler().playerEntity.openContainer).execute(message.command, message.args);
            	}
            });
            return null;
        }
    }
}
