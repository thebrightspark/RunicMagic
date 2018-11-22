package brightspark.runicmagic.spell.projectile;

import brightspark.runicmagic.enums.RuneType;

public class SpellBolt extends SpellProjectileBase
{
	public SpellBolt(RuneType runeType)
	{
		super(runeType + "_bolt", runeType);
		addRuneCost(RuneType.AIR, 2);
		addRuneCost(runeType, 2);
	}
}
