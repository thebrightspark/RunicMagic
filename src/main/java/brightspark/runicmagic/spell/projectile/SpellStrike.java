package brightspark.runicmagic.spell.projectile;

import brightspark.runicmagic.enums.RuneType;

public class SpellStrike extends SpellProjectileBase
{
	public SpellStrike(RuneType runeType)
	{
		super(runeType + "_strike", runeType);
		addRuneCost(RuneType.AIR, 1);
		addRuneCost(runeType, 1);
	}
}
