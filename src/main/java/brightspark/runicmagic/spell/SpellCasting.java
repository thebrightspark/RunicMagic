package brightspark.runicmagic.spell;

import brightspark.runicmagic.handler.NetworkHandler;
import brightspark.runicmagic.message.MessageRemoveSpellCasting;
import brightspark.runicmagic.util.SpellCastData;
import net.minecraft.entity.player.EntityPlayer;
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
		boolean result = shouldCancel;
		if(shouldCancel)
			spell.onCastCancel(player);
		else if(result = progress >= spell.getCastTime())
			spell.execute(player, data);
		if(result && !world.isRemote)
			NetworkHandler.network.sendToAll(new MessageRemoveSpellCasting(player));
		return result;
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
