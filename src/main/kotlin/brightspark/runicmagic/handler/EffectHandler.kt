package brightspark.runicmagic.handler

import brightspark.runicmagic.init.RMEffects
import net.minecraft.entity.LivingEntity
import net.minecraftforge.event.entity.living.LivingAttackEvent

object EffectHandler {
	fun onEntityAttacked(event: LivingAttackEvent) {
		event.source.trueSource?.let {
			if (it is LivingEntity) {
				if (it.isPotionActive(RMEffects.CONFUSION))
					handleConfusionEffect(event, it)
				if (it.isPotionActive(RMEffects.STAGGER))
					handleStaggerEffect(event, it)
			}
		}
	}

	// TODO: Spawn particles and play a sound when misses
	// 5% chance for attack to miss target
	private fun handleConfusionEffect(event: LivingAttackEvent, source: LivingEntity) {
		if (source.rng.nextDouble() < 0.05) event.isCanceled = true
	}

	// TODO: Spawn particles and play a sound when misses
	// 10% chance for attack to miss target
	private fun handleStaggerEffect(event: LivingAttackEvent, source: LivingEntity) {
		if (source.rng.nextDouble() < 0.1) event.isCanceled = true
	}
}
