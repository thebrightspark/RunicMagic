package brightspark.runicmagic.particle

import net.minecraft.client.particle.RainParticle
import net.minecraft.client.world.ClientWorld

class RainParticle(world: ClientWorld, x: Double, y: Double, z: Double) : RainParticle(world, x, y, z) {
	init {
		motionY = 0.0
	}

	override fun getBrightnessForRender(partialTick: Float): Int = 15728880
}
