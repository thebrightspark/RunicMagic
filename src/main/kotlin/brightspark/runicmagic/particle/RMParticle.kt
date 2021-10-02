package brightspark.runicmagic.particle

import net.minecraft.client.particle.SpriteTexturedParticle
import net.minecraft.client.world.ClientWorld

abstract class RMParticle(world: ClientWorld, x: Double, y: Double, z: Double) :
	SpriteTexturedParticle(world, x, y, z) {
	override fun tick() {
		prevPosX = posX
		prevPosY = posY
		prevPosZ = posZ
		if (age++ >= maxAge)
			setExpired()
		else
			update()
	}

	open fun update(): Unit = Unit
}
