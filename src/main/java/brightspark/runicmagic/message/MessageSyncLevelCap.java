package brightspark.runicmagic.message;

import brightspark.runicmagic.capability.CapLevel;
import brightspark.runicmagic.init.RMCapabilities;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageSyncLevelCap implements IMessage
{
	private int experience;

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
			EntityPlayer player = Minecraft.getMinecraft().player;
			CapLevel level = RMCapabilities.getLevel(player);
			if(level != null)
				level.setExperienceInternal(message.experience);
			return null;
		}
	}
}
