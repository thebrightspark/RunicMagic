package brightspark.runicmagic.particle

import net.minecraft.client.particle.RainParticle
import net.minecraft.client.world.ClientWorld
import net.minecraft.util.math.vector.Vector3d

class RainParticle(world: ClientWorld, x: Double, y: Double, z: Double) : RainParticle(world, x, y, z) {
	init {
		motionY = 0.0
	}
}
