package brightspark.runicmagic.spell;

import brightspark.runicmagic.util.SpellCastData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.*;

@Mod.EventBusSubscriber(Side.SERVER)
public class SpellHandler
{
	private static final Map<UUID, SpellCasting> CASTS = new HashMap<>();

	@SubscribeEvent
	public static void onWorldTick(TickEvent.WorldTickEvent event)
	{
		if(event.phase != TickEvent.Phase.END)
			return;

		Set<UUID> toRemove = new HashSet<>();
		CASTS.forEach((uuid, spellCasting) -> {
			EntityPlayer player = event.world.getPlayerEntityByUUID(uuid);
			SpellCasting.UpdateResult result = spellCasting.update(event.world, player);
			switch(result)
			{
				case CANCEL:
					if(player != null)
						spellCasting.getSpell().onCastCancel(player);
				case COMPLETE:
					toRemove.add(uuid);
					break;
				case PASS:
				default:
					//Do nothing
			}
		});
		toRemove.forEach(CASTS::remove);
	}

	public static void addSpellCast(EntityPlayer player, Spell spell, SpellCastData data)
	{
		UUID uuid = player.getUniqueID();
		SpellCasting spellCasting = CASTS.get(uuid);
		//Cancel current spell
		if(spellCasting != null)
			spellCasting.getSpell().onCastCancel(player);
		//Start new spell cast
		CASTS.put(uuid, new SpellCasting(spell, data));
	}
}
