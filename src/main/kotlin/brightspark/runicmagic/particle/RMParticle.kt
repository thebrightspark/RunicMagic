package brightspark.runicmagic.particle

import com.mojang.blaze3d.vertex.IVertexBuilder
import net.minecraft.client.particle.IAnimatedSprite
import net.minecraft.client.particle.IParticleRenderType
import net.minecraft.client.particle.SpriteTexturedParticle
import net.minecraft.client.renderer.ActiveRenderInfo
import net.minecraft.client.world.ClientWorld
import net.minecraft.util.math.MathHelper
import java.awt.Color
import kotlin.math.min

open class RMParticle(world: ClientWorld, x: Double, y: Double, z: Double, colour: Color) :
	SpriteTexturedParticle(world, x, y, z) {
	var animatedSprite: IAnimatedSprite? = null
		set(value) {
			field = value
			value?.let { selectSpriteWithAge(it) }
		}
	var colour2: FloatArray? = null
	var fadeOut: Boolean = false

	init {
		// Set colour with slight variance
		@Suppress("LeakingThis")
		val colourParts = when (getColourVariance()) {
			ColourVariance.BRIGHTNESS -> randColourVary1(colour)
			ColourVariance.COLOUR -> randColourVary2(colour)
			ColourVariance.NONE -> colour.getRGBComponents(null)
		}
		particleRed = colourParts[0]
		particleGreen = colourParts[1]
		particleBlue = colourParts[2]
		particleAlpha = colourParts[3]
	}

	override fun getRenderType(): IParticleRenderType = IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT

	override fun tick() {
		prevPosX = posX
		prevPosY = posY
		prevPosZ = posZ
		if (age++ >= maxAge)
			setExpired()
		else
			update()
	}

	open fun update() {
		animatedSprite?.let {
			selectSpriteWithAge(it)
		}

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

		if (fadeOut) {
			val halfMaxAge = maxAge / 2
			if (age > halfMaxAge)
				setAlphaF(1F - ((age - halfMaxAge).toFloat() / halfMaxAge.toFloat()))
		}
	}

	override fun renderParticle(buffer: IVertexBuilder, renderInfo: ActiveRenderInfo, partialTicks: Float) {
		super.renderParticle(buffer, renderInfo, partialTicks)
	}

	override fun getBrightnessForRender(partialTick: Float): Int = 15728880

	protected open fun getColourVariance(): ColourVariance = ColourVariance.BRIGHTNESS

	private fun colourIntToFloat(colour: Int): Float = MathHelper.clamp(colour.toFloat() / 255f, 0f, 1f)

	private fun randColourVary1(colour: Color): FloatArray {
		val randChange = rand.nextInt(20) - 10
		return floatArrayOf(
			colourIntToFloat(colour.red + randChange),
			colourIntToFloat(colour.green + randChange),
			colourIntToFloat(colour.blue + randChange),
			colourIntToFloat(colour.alpha)
		)
	}

	private fun randColourVary2(colour: Color): FloatArray = floatArrayOf(
		randColourPartVary(colour.red),
		randColourPartVary(colour.green),
		randColourPartVary(colour.blue),
		colourIntToFloat(colour.alpha)
	)

	private fun randColourPartVary(colourComponent: Int): Float {
		val maxChange = min(colourComponent / 8, 20)
		val randChange: Int = rand.nextInt(maxChange) - maxChange / 2
		return colourIntToFloat(colourComponent + randChange)
	}

	protected enum class ColourVariance {
		NONE, BRIGHTNESS, COLOUR
	}
}
