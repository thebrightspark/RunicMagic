package brightspark.runicmagic.spell.projectile.curse

import net.minecraft.potion.Effect
import net.minecraft.potion.EffectInstance
import java.awt.Color

class SimpleCurseSpell : CurseBaseSpell {
	private val effectSupplier: () -> EffectInstance

	constructor(
		props: Properties,
		projectileColour: Color,
		effectSupplier: () -> EffectInstance
	) : super(props, projectileColour) {
		this.effectSupplier = effectSupplier
	}

	constructor(
		props: Properties,
		projectileColour: Color,
		effect: Effect,
		duration: Int = 60,
		amplifier: Int = 0
	) : this(props, projectileColour, { EffectInstance(effect, duration * 20, amplifier) })

	override fun createEffectInstance(): EffectInstance = effectSupplier()
}
