package brightspark.runicmagic.spell.projectile;

import brightspark.runicmagic.enums.RuneType;
import brightspark.runicmagic.enums.SpellType;

public class SpellBolt extends SpellProjectileBase
{
	public SpellBolt(RuneType runeType, int level)
	{
		super(runeType + "_bolt", runeType, SpellType.ELEMENTAl, level);
		addRuneCost(RuneType.AIR, 2);
		addRuneCost(runeType, 2);
	}
}
