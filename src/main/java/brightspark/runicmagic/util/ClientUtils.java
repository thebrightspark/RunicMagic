package brightspark.runicmagic.util;

import brightspark.runicmagic.RunicMagic;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ClientUtils
{
	private static Minecraft mc = Minecraft.getMinecraft();

	public static TextureAtlasSprite textureCloudy;

	private static ResourceLocation createParticleTextureRL(String name)
	{
		return new ResourceLocation(RunicMagic.MOD_ID, "textures/particle/" + name + ".png");
	}

	public static void initTextures()
	{
		textureCloudy = ClientUtils.registerTexture(createParticleTextureRL("cloudy"));
	}

	public static TextureAtlasSprite registerTexture(ResourceLocation resourceLocation)
	{
		return mc.getTextureMapBlocks().registerSprite(resourceLocation);
	}

	public static void spawnParticle(Particle particle)
	{
		mc.effectRenderer.addEffect(particle);
	}

	public static void spawnParticle(World world, EnumParticleTypes particleType, Vec3d pos)
	{
		world.spawnParticle(particleType, pos.x, pos.y, pos.z, 0, 0, 0);
	}
}
