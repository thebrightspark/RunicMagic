package brightspark.runicmagic.particle

import net.minecraft.client.particle.IAnimatedSprite
import net.minecraft.client.world.ClientWorld

abstract class AnimatedSpriteTexturedParticle(
	world: ClientWorld,
	x: Double,
	y: Double,
	z: Double,
	private val animatedSprite: IAnimatedSprite
) : RMParticle(world, x, y, z) {
	init {
		@Suppress("LeakingThis")
		selectSpriteWithAge(animatedSprite)
	}

	override fun update() {
		super.update()
		selectSpriteWithAge(animatedSprite)
	}
}
