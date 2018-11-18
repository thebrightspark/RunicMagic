package brightspark.runemagic.event;

import brightspark.runemagic.spell.Spell;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

//TODO: Call spell casting events

/**
 * Called when a spell is cast by a player
 */
public class SpellCastEvent extends SpellEvent
{
	public SpellCastEvent(EntityPlayer player, Spell spell)
	{
		super(player, spell);
	}

	/**
	 * Called just before the spell is about to be cast
	 * Cancel to prevent the spell from being cast
	 */
	@Cancelable
	public class Pre extends SpellCastEvent
	{
		public Pre(EntityPlayer player, Spell spell)
		{
			super(player, spell);
		}
	}

	/**
	 * Called just after the spell has been cast and provides the spell entity
	 */
	public class Post extends SpellCastEvent
	{
		//TODO: Change this to the actual spell entity type when created!
		private final Entity spellEntity;

		public Post(EntityPlayer player, Spell spell, Entity spellEntity)
		{
			super(player, spell);
			this.spellEntity = spellEntity;
		}

		public Entity getSpellEntity()
		{
			return spellEntity;
		}
	}
}
