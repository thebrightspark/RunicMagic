package brightspark.runicmagic.init

import brightspark.runicmagic.particle.ColouredParticleData
import brightspark.runicmagic.particle.MovingParticle
import brightspark.runicmagic.particle.RainParticle
import brightspark.runicmagic.particle.StaticParticle
import brightspark.runicmagic.util.setRegName
import com.mojang.serialization.Codec
import net.minecraft.client.Minecraft
import net.minecraft.client.particle.IAnimatedSprite
import net.minecraft.client.particle.IParticleFactory
import net.minecraft.client.particle.Particle
import net.minecraft.client.particle.ParticleManager
import net.minecraft.client.world.ClientWorld
import net.minecraft.particles.BasicParticleType
import net.minecraft.particles.IParticleData
import net.minecraft.particles.IParticleData.IDeserializer
import net.minecraft.particles.ParticleType
import net.minecraftforge.event.RegistryEvent
import thedarkcolour.kotlinforforge.forge.objectHolder
import java.awt.Color

typealias ParticleBase = ParticleType<out IParticleData>

object RMParticles {
	private val mcParticles: ParticleManager by lazy { Minecraft.getInstance().particles }

	val SINGLE_MOVING: ParticleBase by objectHolder("single_moving")
	val CLOUD: ParticleBase by objectHolder("cloud")
	val RAIN: ParticleBase by objectHolder("rain")

	fun registerTypes(event: RegistryEvent.Register<ParticleType<*>>) = event.registry.registerAll(
		colouredParticle("single_moving", ColouredParticleData.DESERIALISER, ColouredParticleData::createCodec),
		particle("cloud"),
		particle("rain")
	)

	fun registerFactories() {
		factory(SINGLE_MOVING) { sprite, data, world, x, y, z, _, _, _ ->
			MovingParticle(world, x, y, z, (data as ColouredParticleData).colour, sprite)
		}
		factory(CLOUD) { sprite, _, world, x, y, z, _, _, _ ->
			StaticParticle(world, x, y, z, Color.WHITE, sprite).apply { maxAge *= 3 }
		}
		factory(RAIN) { sprite, _, world, x, y, z, _, _, _ ->
			RainParticle(world, x, y, z).apply { selectSpriteRandomly(sprite) }
		}
	}

	private fun particle(name: String): ParticleType<*> =
		BasicParticleType(false).setRegName(name)

	private fun <T : IParticleData> colouredParticle(
		name: String,
		deserialiser: IDeserializer<T>,
		codecFunc: (ParticleType<T>) -> Codec<T>
	): ParticleType<*> = object : ParticleType<T>(false, deserialiser) {
		override fun func_230522_e_(): Codec<T> = codecFunc(this)
	}.setRegName(name)

//	private fun <T : IParticleData> factory(
//		particleType: ParticleType<T>,
//		factory: ParticleManager.IParticleMetaFactory<T>
//	) = mcParticles.registerFactory(particleType, factory)
//
//	private fun <T : IParticleData> factory(
//		particleType: ParticleType<T>,
//		factory: IParticleFactory<T>
//	) = mcParticles.registerFactory(particleType, factory)

	private fun <T : IParticleData> factory(
		particleType: ParticleType<T>,
		factory: (IAnimatedSprite, T, ClientWorld, Double, Double, Double, Double, Double, Double) -> Particle
	) = mcParticles.registerFactory(particleType, ParticleManager.IParticleMetaFactory {
		IParticleFactory { type, world, x, y, z, xSpeed, ySpeed, zSpeed ->
			factory(it, type, world, x, y, z, xSpeed, ySpeed, zSpeed)
		}
	})
}
