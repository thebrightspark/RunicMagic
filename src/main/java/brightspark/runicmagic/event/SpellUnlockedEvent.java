package brightspark.runicmagic.event;

import brightspark.runicmagic.spell.Spell;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Event;

//TODO: Call this when selection GUI is opened/updated

/**
 * This event is called by the spell selection GUI to check if a spell is "unlocked"
 * Locked spells will not be clickable
 *
 * Set the result to Result.DENY to lock the spell
 */
@Event.HasResult
public class SpellUnlockedEvent extends SpellEvent
{
	public SpellUnlockedEvent(EntityPlayer player, Spell spell)
	{
		super(player, spell);
	}
}
