package brightspark.runicmagic.entity;

import brightspark.runicmagic.particle.ParticleStatic;
import brightspark.runicmagic.spell.projectile.SpellProjectileBase;
import brightspark.runicmagic.util.ClientUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.awt.*;

public class EntityHelixProjectile extends EntitySpellProjectile
{
	private int particleBearing;
	private Color colour;

	public EntityHelixProjectile(World worldIn)
	{
		super(worldIn);
	}

	public EntityHelixProjectile(EntityLivingBase shooter, SpellProjectileBase spell, Color colour)
	{
		super(shooter, spell);
		this.colour = colour == null ? new Color(1F, 1F, 1F) : colour;
	}

	@Override
	protected void spawnParticles(Vec3d centerPos)
	{
		ClientUtils.spawnParticle(new ParticleStatic(world, centerPos, colour, ClientUtils.textureCloudy));
		/**
		ClientUtils.spawnParticle(new ParticleAir(world, centerPos, new Vec3d(motionX, motionY, motionZ), particleBearing));
		int bearing180 = particleBearing + 180;
		if(bearing180 >= 360)
			bearing180 -= 360;
		ClientUtils.spawnParticle(new ParticleAir(world, centerPos, new Vec3d(motionX, motionY, motionZ), bearing180));

		particleBearing += 2;
		if(particleBearing >= 360)
			particleBearing -= 360;
		*/
		//world.spawnParticle(EnumParticleTypes.FLAME, centerPos.x, centerPos.y, centerPos.z, 0D, 0D, 0D);
	}
}
