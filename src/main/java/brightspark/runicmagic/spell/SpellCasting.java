package brightspark.runicmagic.spell;

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

	public UpdateResult update(World world, EntityPlayer player)
	{
		boolean shouldCancel = spell.updateCasting(world, player, progress++);
		if(!shouldCancel && progress >= spell.getCastTime())
		{
			spell.execute(player, data);
			return UpdateResult.COMPLETE;
		}
		return shouldCancel ? UpdateResult.CANCEL : UpdateResult.PASS;
	}

	public Spell getSpell()
	{
		return spell;
	}

	public enum UpdateResult
	{
		PASS,
		CANCEL,
		COMPLETE
	}
}
