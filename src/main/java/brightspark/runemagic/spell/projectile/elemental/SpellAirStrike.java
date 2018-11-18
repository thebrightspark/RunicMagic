package brightspark.runemagic.spell.projectile.elemental;

import brightspark.runemagic.entity.EntityHelixProjectile;
import brightspark.runemagic.enums.RuneType;
import brightspark.runemagic.spell.projectile.SpellProjectileBase;

public class SpellAirStrike extends SpellProjectileBase
{
	public SpellAirStrike()
	{
		super("air_strike", EntityHelixProjectile::new);
		addRuneCost(RuneType.AIR, 1);
	}
}
