package brightspark.runicmagic.particle;

import brightspark.runicmagic.RunicMagic;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.awt.*;
import java.util.List;

public class ParticleProjectileBase extends Particle
{
	private static final ResourceLocation TEXTURE_LOC = new ResourceLocation(RunicMagic.MOD_ID, "textures/particle/cloudy.png");
	private static TextureAtlasSprite TEXTURE;

	protected final Vec3d entityMotion;

	protected ParticleProjectileBase(World worldIn, Vec3d position, Vec3d entityMotion, Color colour)
	{
		super(worldIn, position.x, position.y, position.z);
		this.entityMotion = entityMotion;
		setParticleTexture(TEXTURE);
		//Set colour with slight variance
		setRBGColorF(
			randColourVary(colour.getRed()),
			randColourVary(colour.getGreen()),
			randColourVary(colour.getBlue())
		);
	}

	public static void registerTexture()
	{
		TEXTURE = Minecraft.getMinecraft().getTextureMapBlocks().registerSprite(TEXTURE_LOC);
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

	@Override
	public int getFXLayer()
	{
		return 1;
	}

	@Override
	public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ)
	{
		Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE_LOC);
		super.renderParticle(buffer, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
		Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
	}
}
