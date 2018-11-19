package brightspark.runicmagic.spell.projectile;

import brightspark.runicmagic.RunicMagic;
import net.minecraft.entity.Entity;
import net.minecraft.util.EntityDamageSourceIndirect;

import javax.annotation.Nullable;

public class DamageSourceSpellProjectile extends EntityDamageSourceIndirect
{
	public DamageSourceSpellProjectile(Entity source, @Nullable Entity indirectEntityIn)
	{
		super(RunicMagic.MOD_ID + ".spell.projectile", source, indirectEntityIn);
		setProjectile();
	}
}
