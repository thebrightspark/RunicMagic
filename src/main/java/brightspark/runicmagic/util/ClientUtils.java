package brightspark.runicmagic.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;

public class ClientUtils
{
	private static Minecraft mc = Minecraft.getMinecraft();

	public static void spawnParticle(Particle particle)
	{
		mc.effectRenderer.addEffect(particle);
	}
}
