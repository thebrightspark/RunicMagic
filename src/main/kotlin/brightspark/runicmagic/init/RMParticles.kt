package brightspark.runicmagic.init

import brightspark.runicmagic.particle.RainParticle
import brightspark.runicmagic.particle.StaticParticle
import brightspark.runicmagic.util.setRegName
import net.minecraft.client.Minecraft
import net.minecraft.client.particle.IAnimatedSprite
import net.minecraft.client.particle.IParticleFactory
import net.minecraft.client.particle.Particle
import net.minecraft.client.particle.ParticleManager
import net.minecraft.client.world.ClientWorld
import net.minecraft.particles.BasicParticleType
import net.minecraft.particles.IParticleData
import net.minecraft.particles.ParticleType
import net.minecraftforge.event.RegistryEvent
import thedarkcolour.kotlinforforge.forge.objectHolder
import java.awt.Color

object RMParticles {
	private val mcParticles: ParticleManager by lazy { Minecraft.getInstance().particles }

	val CLOUD: ParticleType<out IParticleData> by objectHolder("cloud")
	val RAIN: ParticleType<out IParticleData> by objectHolder("rain")

	fun registerTypes(event: RegistryEvent.Register<ParticleType<*>>) = event.registry.registerAll(
		particle("cloud"),
		particle("rain")
	)

	fun registerFactories() {
		factory(CLOUD) { sprite, _, world, x, y, z, _, _, _ ->
			StaticParticle(world, x, y, z, Color.WHITE, sprite).apply { maxAge *= 3 }
		}
		factory(RAIN) { sprite, _, world, x, y, z, _, _, _ ->
			RainParticle(world, x, y, z).apply { selectSpriteRandomly(sprite) }
		}
	}

	private fun particle(name: String, alwaysShow: Boolean = false): ParticleType<*> =
		BasicParticleType(alwaysShow).setRegName(name)

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
