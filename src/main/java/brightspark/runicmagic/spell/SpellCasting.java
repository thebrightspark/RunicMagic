package brightspark.runicmagic.spell;

import brightspark.runicmagic.handler.NetworkHandler;
import brightspark.runicmagic.init.RMCapabilities;
import brightspark.runicmagic.message.MessageRemoveSpellCasting;
import brightspark.runicmagic.util.SpellCastData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

public class SpellCasting
{
	private final Spell spell;
	private final SpellCastData data;
	private int progress;

	public SpellCasting(Spell spell, SpellCastData data)
	{
		this.spell = spell;
		this.data = data;
		progress = 0;
	}

	public boolean update(World world, EntityPlayer player)
	{
		boolean shouldCancel = spell.updateCasting(world, player, progress++);
		if(shouldCancel)
			//Spell decided it should be cancelled
			spell.onCastCancel(player);
		else if(shouldCancel = progress >= spell.getCastTime())
		{
			//Finished casting - execute spell
			if(!world.isRemote && RMCapabilities.getSpells(player).onSpellExecuted((EntityPlayerMP) player, spell, data))
				return spell.execute(player, data);
		}
		if(shouldCancel && !world.isRemote)
			//If cancelled, then update clients
			NetworkHandler.network.sendToAll(new MessageRemoveSpellCasting(player));
		return shouldCancel;
	}

	public Spell getSpell()
	{
		return spell;
	}

	public SpellCastData getData()
	{
		return data;
	}
}
