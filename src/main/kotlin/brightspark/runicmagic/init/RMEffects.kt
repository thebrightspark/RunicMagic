package brightspark.runicmagic.init

import brightspark.runicmagic.effect.ConfusionEffect
import brightspark.runicmagic.effect.StaggerEffect
import brightspark.runicmagic.util.setRegName
import net.minecraft.potion.Effect
import net.minecraftforge.event.RegistryEvent
import thedarkcolour.kotlinforforge.forge.objectHolder

object RMEffects {
	val CONFUSION: Effect by objectHolder("confusion")
	val STAGGER: Effect by objectHolder("stagger")

	fun register(event: RegistryEvent.Register<Effect>) = event.registry.registerAll(
		effect("confusion", ConfusionEffect()),
		effect("stagger", StaggerEffect())
	)

	private fun effect(name: String, effect: Effect): Effect = effect.setRegName(name)
}
