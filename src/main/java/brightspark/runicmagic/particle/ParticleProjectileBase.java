package brightspark.runicmagic.particle;

import brightspark.runicmagic.util.ClientUtils;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.awt.*;

public class ParticleProjectileBase extends ParticleStatic
{
	protected final Vec3d entityMotion;

	public ParticleProjectileBase(World worldIn, Vec3d position, Vec3d entityMotion, Color colour)
	{
		super(worldIn, position, colour, ClientUtils.textureCloudy);
		this.entityMotion = entityMotion;
	}

	@Override
	public void onUpdate()
	{
		//TODO: Make projectiles move
		super.onUpdate();
	}
}
