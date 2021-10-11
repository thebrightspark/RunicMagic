package brightspark.runicmagic.particle

import net.minecraft.client.world.ClientWorld
import java.awt.Color

class MovingParticle(
	world: ClientWorld,
	x: Double,
	y: Double,
	z: Double,
	vx: Double,
	vy: Double,
	vz: Double,
	colour: Color
) : RMParticle(world, x, y, z, colour) {
	init {
		motionX = vx
		motionY = vy
		motionZ = vz
	}

	override fun update() {
		super.update()
		move(motionX, motionY, motionZ)
	}
}
