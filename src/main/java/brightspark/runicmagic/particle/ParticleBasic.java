package brightspark.runicmagic.particle;

import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.awt.*;

public class ParticleBasic extends ParticleProjectileBase
{
	public ParticleBasic(World worldIn, Vec3d position, Color colour)
	{
		super(worldIn, position, new Vec3d(0, 0, 0), colour);
	}
}
