package brightspark.runemagic.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.awt.*;
import java.util.List;

public class ParticleProjectileBase extends Particle
{
	protected final Vec3d entityMotion;

	protected ParticleProjectileBase(World worldIn, Vec3d position, Vec3d entityMotion, Color colour)
	{
		super(worldIn, position.x, position.y, position.z);
		this.entityMotion = entityMotion;
		//Set colour with slight variance
		setRBGColorF(
			randColourVary(colour.getRed()),
			randColourVary(colour.getGreen()),
			randColourVary(colour.getBlue())
		);
	}

	private float randColourVary(int colourComponent)
	{
		int maxChange = Math.min(colourComponent / 20, 20);
		int randChange = rand.nextInt(maxChange) - (maxChange / 2);
		return (float) MathHelper.clamp(colourComponent + randChange, 0, 255) / 255F;
	}

	@Override
	public void onUpdate()
	{
		//Just base update logic
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;

		if(particleAge++ >= particleMaxAge)
			setExpired();
	}

	@Override
	public void move(double x, double y, double z)
	{
		double origX = x;
		double origY = y;
		double origZ = z;
		List<AxisAlignedBB> list = world.getCollisionBoxes(null, getBoundingBox().offset(x, y, z));

		for(AxisAlignedBB axisalignedbb : list)
			y = axisalignedbb.calculateYOffset(getBoundingBox(), y);

		setBoundingBox(getBoundingBox().offset(0.0D, y, 0.0D));

		for(AxisAlignedBB axisalignedbb1 : list)
			x = axisalignedbb1.calculateXOffset(getBoundingBox(), x);

		setBoundingBox(getBoundingBox().offset(x, 0.0D, 0.0D));

		for(AxisAlignedBB axisalignedbb2 : list)
			z = axisalignedbb2.calculateZOffset(getBoundingBox(), z);

		setBoundingBox(getBoundingBox().offset(0.0D, 0.0D, z));

		resetPositionToBB();

		//If collided with a block, then die
		if(origY != y || origX != x || origZ != z)
			setExpired();
	}
}
