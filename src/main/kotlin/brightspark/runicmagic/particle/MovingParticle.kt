package brightspark.runicmagic.particle

import net.minecraft.client.particle.IAnimatedSprite
import net.minecraft.client.world.ClientWorld
import net.minecraft.util.math.vector.Vector3d
import java.awt.Color

class MovingParticle : StaticParticle {
	private var colour2: FloatArray? = null
	private var fadeOut: Boolean = false

	constructor(world: ClientWorld, position: Vector3d, colour: Color, sprite: IAnimatedSprite)
		: this(world, position.x, position.y, position.z, colour, sprite)

	constructor(world: ClientWorld, x: Double, y: Double, z: Double, colour: Color, sprite: IAnimatedSprite)
		: super(world, x, y, z, colour, sprite)

	fun setMotion(motion: Vector3d): MovingParticle {
		motionX = motion.x
		motionY = motion.y
		motionZ = motion.z
		return this
	}

	fun setSecondColour(colour2: Color): MovingParticle {
		this.colour2 = colour2.getRGBColorComponents(null)
		return this
	}

	fun setFadeOut(): MovingParticle {
		fadeOut = true
		return this
	}

	override fun update() {
		super.update()
		move(motionX, motionY, motionZ)

		colour2?.let {
			if (age < maxAge) {
				// Change the colour towards colour2
				val ageLeft = maxAge - age
				val r = (it[0] - particleRed) / ageLeft
				val g = (it[1] - particleGreen) / ageLeft
				val b = (it[2] - particleBlue) / ageLeft
				setColor(particleRed + r, particleGreen + g, particleBlue + b)
			}
		}
		if (fadeOut)
			setAlphaF(1F - (age.toFloat() / maxAge.toFloat()))
	}
}
