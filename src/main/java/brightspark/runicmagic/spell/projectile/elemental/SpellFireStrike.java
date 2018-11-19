package brightspark.runicmagic.spell.projectile.elemental;

import brightspark.runicmagic.entity.EntityHelixProjectile;
import brightspark.runicmagic.enums.RuneType;
import brightspark.runicmagic.spell.projectile.SpellProjectileBase;

public class SpellFireStrike extends SpellProjectileBase
{
	public SpellFireStrike()
	{
		super("fire_strike", EntityHelixProjectile::new); //TEMP ENTITY
		addRuneCost(RuneType.AIR, 1);
		addRuneCost(RuneType.FIRE, 1);
	}
}
