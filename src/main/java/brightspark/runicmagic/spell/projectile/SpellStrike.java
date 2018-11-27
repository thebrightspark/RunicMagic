package brightspark.runicmagic.spell.projectile;

import brightspark.runicmagic.enums.RuneType;
import brightspark.runicmagic.enums.SpellType;

public class SpellStrike extends SpellProjectileBase
{
	public SpellStrike(RuneType runeType, int level)
	{
		super("strike_" + runeType, runeType, SpellType.ELEMENTAl, level);
		addRuneCost(RuneType.AIR, 1);
		addRuneCost(runeType, 1);
	}
}
