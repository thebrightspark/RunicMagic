package brightspark.runemagic.spell.projectile;

import brightspark.runemagic.RuneMagic;
import net.minecraft.entity.Entity;
import net.minecraft.util.EntityDamageSourceIndirect;

import javax.annotation.Nullable;

public class DamageSourceSpellProjectile extends EntityDamageSourceIndirect
{
	public DamageSourceSpellProjectile(Entity source, @Nullable Entity indirectEntityIn)
	{
		super(RuneMagic.MOD_ID + ".spell.projectile", source, indirectEntityIn);
		setProjectile();
	}
}
