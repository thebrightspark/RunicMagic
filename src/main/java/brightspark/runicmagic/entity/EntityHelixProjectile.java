package brightspark.runicmagic.entity;

import brightspark.runicmagic.particle.ParticleAir;
import brightspark.runicmagic.util.ClientUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityHelixProjectile extends EntitySpellProjectile
{
	private int particleBearing;

	public EntityHelixProjectile(World worldIn)
	{
		super(worldIn);
	}

	public EntityHelixProjectile(EntityLivingBase shooter)
	{
		super(shooter);
	}

	@Override
	protected void spawnParticles(Vec3d centerPos)
	{
		ClientUtils.spawnParticle(new ParticleAir(world, centerPos, new Vec3d(motionX, motionY, motionZ), particleBearing));
		int bearing180 = particleBearing + 180;
		if(bearing180 >= 360)
			bearing180 -= 360;
		ClientUtils.spawnParticle(new ParticleAir(world, centerPos, new Vec3d(motionX, motionY, motionZ), bearing180));

		particleBearing += 2;
		if(particleBearing >= 360)
			particleBearing -= 360;

		//world.spawnParticle(EnumParticleTypes.FLAME, centerPos.x, centerPos.y, centerPos.z, 0D, 0D, 0D);
	}
}
