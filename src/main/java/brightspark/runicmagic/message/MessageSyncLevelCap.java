package brightspark.runicmagic.message;

import brightspark.runicmagic.handler.CapabilitySyncHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageSyncLevelCap implements IMessage
{
	public int experience;

	public MessageSyncLevelCap() {}

	public MessageSyncLevelCap(int experience)
	{
		this.experience = experience;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		experience = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(experience);
	}

	public static class Handler implements IMessageHandler<MessageSyncLevelCap, IMessage>
	{
		@Override
		public IMessage onMessage(MessageSyncLevelCap message, MessageContext ctx)
		{
			CapabilitySyncHandler.handleUpdate(Minecraft.getMinecraft().player, message);
			return null;
		}
	}
}
