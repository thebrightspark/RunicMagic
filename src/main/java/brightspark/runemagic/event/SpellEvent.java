package brightspark.runemagic.event;

import brightspark.runemagic.spell.Spell;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class SpellEvent extends PlayerEvent
{
	private final Spell spell;

	public SpellEvent(EntityPlayer player, Spell spell)
	{
		super(player);
		this.spell = spell;
	}

	public Spell getSpell()
	{
		return spell;
	}
}
