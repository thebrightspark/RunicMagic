package brightspark.runicmagic.util;

import brightspark.runicmagic.RunicMagic;
import brightspark.runicmagic.message.*;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class NetworkHandler
{
	public static SimpleNetworkWrapper network;
	private static int discriminator = 0;

	private static <REQ extends IMessage, REPLY extends IMessage> void regMessage(Class<? extends IMessageHandler<REQ, REPLY>> messageHandler, Class<REQ> requestMessageType, Side side)
	{
		network.registerMessage(messageHandler, requestMessageType, discriminator++, side);
	}

	public static void init()
	{
		network = NetworkRegistry.INSTANCE.newSimpleChannel(RunicMagic.MOD_ID);
		regMessage(MessageSyncSpellsCap.Handler.class, MessageSyncSpellsCap.class, Side.CLIENT);
		regMessage(MessageSyncLevelCap.Handler.class, MessageSyncLevelCap.class, Side.CLIENT);
		regMessage(MessageAddSpellCasting.Handler.class, MessageAddSpellCasting.class, Side.CLIENT);
		regMessage(MessageRemoveSpellCasting.Handler.class, MessageRemoveSpellCasting.class, Side.CLIENT);
		regMessage(MessageOpenSpellGui.Handler.class, MessageOpenSpellGui.class, Side.CLIENT);
		regMessage(MessageSetSelectedSpell.Handler.class, MessageSetSelectedSpell.class, Side.SERVER);
	}
}
