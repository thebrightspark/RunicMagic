package brightspark.runicmagic.effect

import brightspark.runicmagic.init.RMEffects
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.ai.attributes.AttributeModifierManager
import net.minecraft.potion.Effect
import net.minecraft.potion.EffectType

class StaggerEffect : Effect(EffectType.HARMFUL, 0xFF00FF) {
	override fun applyAttributesModifiersToEntity(
		entity: LivingEntity,
		attributeMap: AttributeModifierManager,
		amplifier: Int
	) {
		entity.removePotionEffect(RMEffects.CONFUSION)
		super.applyAttributesModifiersToEntity(entity, attributeMap, amplifier)
	}
}
