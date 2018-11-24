package brightspark.runicmagic.particle;

import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ParticleRain extends net.minecraft.client.particle.ParticleRain
{
	public ParticleRain(World worldIn, Vec3d pos)
	{
		super(worldIn, pos.x, pos.y, pos.z);
		motionY = 0;
	}
}
