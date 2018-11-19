package brightspark.runicmagic.spell.projectile.elemental;

import brightspark.runicmagic.entity.EntityHelixProjectile;
import brightspark.runicmagic.enums.RuneType;
import brightspark.runicmagic.spell.projectile.SpellProjectileBase;

public class SpellAirStrike extends SpellProjectileBase
{
	public SpellAirStrike()
	{
		super("air_strike", EntityHelixProjectile::new);
		addRuneCost(RuneType.AIR, 1);
	}
}
