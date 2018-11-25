package brightspark.runicmagic.spell.projectile;

import brightspark.runicmagic.enums.RuneType;
import brightspark.runicmagic.enums.SpellType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;

public class SpellWeaken extends SpellProjectileBase
{
	public SpellWeaken()
	{
		super("weaken", SpellType.CURSES, 11);
		addRuneCost(RuneType.BODY, 1);
	}

	@Override
	public void applyEffects(EntityLivingBase entityHit)
	{
		//Weakness 1 for 1 minute
		entityHit.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 1200, 0));
	}
}
