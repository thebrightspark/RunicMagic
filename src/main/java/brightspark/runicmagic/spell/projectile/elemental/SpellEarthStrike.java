package brightspark.runicmagic.spell.projectile.elemental;

import brightspark.runicmagic.entity.EntityHelixProjectile;
import brightspark.runicmagic.enums.RuneType;
import brightspark.runicmagic.spell.projectile.SpellProjectileBase;

public class SpellEarthStrike extends SpellProjectileBase
{
	public SpellEarthStrike()
	{
		super("earth_strike", EntityHelixProjectile::new); //TEMP ENTITY
		addRuneCost(RuneType.AIR, 1);
		addRuneCost(RuneType.EARTH, 1);
	}
}
