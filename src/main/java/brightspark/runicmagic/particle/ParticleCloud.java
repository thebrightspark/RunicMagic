package brightspark.runicmagic.particle;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.awt.*;

public class ParticleCloud extends ParticleStatic
{
	private float oSize;

	public ParticleCloud(World worldIn, Vec3d position)
	{
		super(worldIn, position, new Color(0xA1A1A1), 7);
		setMaxAge(40);
		oSize = particleScale * 0.75F * 2.5F;
	}

	@Override
	public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ)
	{
		float f = ((float) particleAge + partialTicks) / (float) particleMaxAge * 32.0F;
		f = MathHelper.clamp(f, 0.0F, 1.0F);
		particleScale = oSize * f;
		super.renderParticle(buffer, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();
		setParticleTextureIndex(7 - particleAge * 8 / particleMaxAge);
	}
}
