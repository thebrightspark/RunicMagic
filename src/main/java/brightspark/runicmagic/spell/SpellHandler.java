package brightspark.runicmagic.spell;

import brightspark.runicmagic.RunicMagic;
import brightspark.runicmagic.handler.NetworkHandler;
import brightspark.runicmagic.message.MessageAddSpellCasting;
import brightspark.runicmagic.util.CommonUtils;
import brightspark.runicmagic.util.SpellCastData;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = RunicMagic.MOD_ID)
public class SpellHandler
{
	private static final Map<UUID, SpellCasting> CASTS = new HashMap<>();

	@SubscribeEvent
	public static void onServerTick(TickEvent.ServerTickEvent event)
	{
		if(event.phase != TickEvent.Phase.END)
			return;

		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		if(server == null)
			return;
		CASTS.entrySet().removeIf(entry -> {
			EntityPlayer player = server.getPlayerList().getPlayerByUUID(entry.getKey());
			return player == null || entry.getValue().update(player.world, player, false);
		});
	}

	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event)
	{
		if(event.phase != TickEvent.Phase.END)
			return;

		Minecraft mc = Minecraft.getMinecraft();
		boolean isSinglePlayer = mc.isSingleplayer();
		CASTS.entrySet().removeIf(entry -> {
			EntityPlayer player = mc.world.getPlayerEntityByUUID(entry.getKey());
			return player == null || entry.getValue().update(mc.world, player, isSinglePlayer);
		});
	}

	public static void addSpellCast(EntityPlayer player, Spell spell, SpellCastData data)
	{
		RunicMagic.LOG.info("Adding spell cast for spell {}", spell);
		SpellCasting casting = CASTS.compute(player.getUniqueID(), (uuid, spellCasting) -> {
			if(spellCasting != null)
				//Cancel existing spell casting
				spellCasting.getSpell().onCastCancel(player);
			//Add new spell casting
			return new SpellCasting(spell, data);
		});
		if(!player.world.isRemote && !CommonUtils.isIntegratedServer())
			NetworkHandler.network.sendToAll(new MessageAddSpellCasting(player, casting));
	}

	public static void removeSpellCast(UUID playerUuid)
	{
		CASTS.remove(playerUuid);
	}
}
