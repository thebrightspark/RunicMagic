package brightspark.runicmagic.spell;

import brightspark.runicmagic.RunicMagic;
import brightspark.runicmagic.message.MessageAddSpellCasting;
import brightspark.runicmagic.util.NetworkHandler;
import brightspark.runicmagic.util.SpellCastData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
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
	public static void onWorldTick(TickEvent.WorldTickEvent event)
	{
		if(event.phase != TickEvent.Phase.END)
			return;

		World world = event.world;
		CASTS.entrySet().removeIf(entry -> entry.getValue().update(world, world.getPlayerEntityByUUID(entry.getKey())));
	}

	public static void addSpellCast(EntityPlayer player, Spell spell, SpellCastData data)
	{
		SpellCasting casting = CASTS.compute(player.getUniqueID(), (uuid, spellCasting) -> {
			if(spellCasting != null)
				//Cancel existing spell casting
				spellCasting.getSpell().onCastCancel(player);
			//Add new spell casting
			return new SpellCasting(spell, data);
		});
		if(!player.world.isRemote)
			NetworkHandler.network.sendToAll(new MessageAddSpellCasting(player, casting));
	}

	public static void removeSpellCast(UUID playerUuid)
	{
		CASTS.remove(playerUuid);
	}
}
