package brightspark.runicmagic.particle;

import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.awt.*;

public class ParticleAir extends ParticleProjectileBase
{
	public ParticleAir(World worldIn, Vec3d position, Vec3d entityMotion, int bearing)
	{
		super(worldIn, position, entityMotion, new Color(250, 250, 240));
		setParticleTextureIndex(128); //TODO: TEMP
		setMaxAge(20);
		float velocity = 0.2F; //rand.nextFloat() * 0.2F;
		//Some calculation I found online that will spawn the particles in a cone, but I don't know how to make it
		// rotate towards the direction the entity is moving
		//double bearing = Math.toRadians(rand.nextInt(360));
		double angle = Math.toRadians(20D);
		double vecX = Math.cos(bearing) * Math.cos(angle);
		double vecY = Math.sin(bearing) * Math.cos(angle);
		double vecZ = Math.sin(angle);
		motionX = vecX * velocity;
		motionY = vecY * velocity;
		motionZ = vecZ * velocity;

		//My old Flare logic
		/*
		motionX = changeMotionComponentFromSpeed(entityMotion.x);
		motionY = changeMotionComponentFromSpeed(entityMotion.y);
		motionZ = changeMotionComponentFromSpeed(entityMotion.z);
		*/
	}

	//From old flare particle code
	private double changeMotionComponentFromSpeed(double speed)
	{
		double motion = speed * (rand.nextDouble() / 4d + 0.75d);
		if(Math.abs(motion) < 0.125d)
			motion = rand.nextDouble() / 4d - 0.125d;
		else if(Math.abs(motion) < 0.25d)
			motion = rand.nextDouble() / 4d * (speed < 0 ? -1 : 1);
		return motion;
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();

		move(motionX, motionY, motionZ);
		motionX *= 0.9;
		motionY *= 0.9;
		motionZ *= 0.9;
	}
}
