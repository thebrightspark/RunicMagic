package brightspark.runemagic.spell.projectile.elemental;

import brightspark.runemagic.entity.EntityHelixProjectile;
import brightspark.runemagic.enums.RuneType;
import brightspark.runemagic.spell.projectile.SpellProjectileBase;

public class SpellEarthStrike extends SpellProjectileBase
{
	public SpellEarthStrike()
	{
		super("earth_strike", EntityHelixProjectile::new); //TEMP ENTITY
		addRuneCost(RuneType.AIR, 1);
		addRuneCost(RuneType.EARTH, 1);
	}
}
