package brightspark.runemagic.spell;

import net.minecraft.world.World;

public class SpellCasting
{
	private final Spell spell;
	private int progress;

	public SpellCasting(Spell spell)
	{
		this.spell = spell;
		progress = 0;
	}

	public boolean update(World world)
	{
		return spell.updateCasting(world, progress++);
	}

	public Spell getSpell()
	{
		return spell;
	}
}
