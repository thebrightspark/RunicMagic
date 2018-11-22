package brightspark.runicmagic.spell.projectile;

import brightspark.runicmagic.enums.RuneType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;

public class SpellConfuse extends SpellProjectileBase
{
	public SpellConfuse()
	{
		super("confuse");
		addRuneCost(RuneType.MIND, 1);
	}

	@Override
	public void applyEffects(EntityLivingBase entityHit)
	{
		//Nausea 1 for 10 seconds
		entityHit.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 200, 0));
	}
}
