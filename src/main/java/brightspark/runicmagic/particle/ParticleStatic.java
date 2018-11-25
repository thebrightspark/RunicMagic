package brightspark.runicmagic.particle;

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

public class ParticleStatic extends Particle
{
	private boolean useCustomTexture;
	private ResourceLocation textureRL;

	public ParticleStatic(World worldIn, Vec3d position, Color colour, int particleIconIndex)
	{
		this(worldIn, position, colour);
		setParticleTextureIndex(particleIconIndex);
		useCustomTexture = false;
	}

	public ParticleStatic(World worldIn, Vec3d position, Color colour, TextureAtlasSprite texture)
	{
		this(worldIn, position, colour);
		setParticleTexture(texture);
		textureRL = new ResourceLocation(texture.getIconName());
		useCustomTexture = true;
	}

	private ParticleStatic(World worldIn, Vec3d position, Color colour)
	{
		super(worldIn, position.x, position.y, position.z);
		//Set colour with slight variance
		float[] colourParts;
		switch(getColourVariance())
		{
			case BRIGHTNESS:
				colourParts = randColourVary1(colour);
				break;
			case COLOUR:
				colourParts = randColourVary2(colour);
				break;
			case NONE:
			default:
				colourParts = colour.getRGBColorComponents(null);
				break;
		}
		setRBGColorF(colourParts[0], colourParts[1], colourParts[2]);
	}

	private float colourIntToFloat(int colour)
	{
		return MathHelper.clamp((float) colour / 255F, 0F, 1F);
	}

	private float[] randColourVary1(Color colour)
	{
		int randChange = rand.nextInt(20) - 10;
		return new float[] {
			colourIntToFloat(colour.getRed() + randChange),
			colourIntToFloat(colour.getGreen() + randChange),
			colourIntToFloat(colour.getBlue() + randChange)};
	}

	private float[] randColourVary2(Color colour)
	{
		return new float[] {
			randColourPartVary(colour.getRed()),
			randColourPartVary(colour.getGreen()),
			randColourPartVary(colour.getBlue())};
	}

	private float randColourPartVary(int colourComponent)
	{
		int maxChange = Math.min(colourComponent / 8, 20);
		int randChange = rand.nextInt(maxChange) - (maxChange / 2);
		return colourIntToFloat(colourComponent + randChange);
	}

	protected ColourVariance getColourVariance()
	{
		return ColourVariance.BRIGHTNESS;
	}

	@Override
	public int getFXLayer()
	{
		return useCustomTexture ? 1 : 0;
	}

	@Override
	public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ)
	{
		if(useCustomTexture)
			Minecraft.getMinecraft().getTextureManager().bindTexture(textureRL);
		super.renderParticle(buffer, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
		if(useCustomTexture)
			Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
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

	protected enum ColourVariance
	{
		NONE,
		BRIGHTNESS,
		COLOUR
	}
}
