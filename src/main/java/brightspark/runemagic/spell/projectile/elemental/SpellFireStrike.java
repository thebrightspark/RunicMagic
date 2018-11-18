package brightspark.runemagic.spell.projectile.elemental;

import brightspark.runemagic.entity.EntityHelixProjectile;
import brightspark.runemagic.enums.RuneType;
import brightspark.runemagic.spell.projectile.SpellProjectileBase;

public class SpellFireStrike extends SpellProjectileBase
{
	public SpellFireStrike()
	{
		super("fire_strike", EntityHelixProjectile::new); //TEMP ENTITY
		addRuneCost(RuneType.AIR, 1);
		addRuneCost(RuneType.FIRE, 1);
	}
}
