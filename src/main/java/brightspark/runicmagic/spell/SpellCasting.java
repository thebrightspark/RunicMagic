package brightspark.runicmagic.spell;

import brightspark.runicmagic.RunicMagic;
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

	public boolean update(World world, EntityPlayer player, boolean isClientSideSinglePlayer)
	{
		boolean shouldCancel = spell.updateCasting(world, player, progress);
        //If on a single player world, the handler class is shared between client and server,
        // so we only want to update progress once per tick
		if(!isClientSideSinglePlayer)
			progress++;
		if(shouldCancel)
		{
			//Spell decided it should be cancelled
			spell.onCastCancel(player);
			RunicMagic.LOG.info("Cast cancelled by spell {} at progress {} / {}", spell, progress, spell.getCastTime());
		}
		else if(shouldCancel = progress >= spell.getCastTime())
		{
			//Finished casting - execute spell
			RunicMagic.LOG.info("Cast finished for spell {}", spell);
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
