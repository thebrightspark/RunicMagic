package brightspark.runicmagic.spell.projectile.curse

import brightspark.runicmagic.init.RMEntities
import brightspark.runicmagic.spell.projectile.ProjectileBaseSpell
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.potion.EffectInstance
import java.awt.Color

abstract class CurseBaseSpell(props: Properties, projectileColour: Color) :
	ProjectileBaseSpell(props, RMEntities.SPELL, projectileColour, 0F) {
	override fun affectEntityHit(projectile: Entity, caster: Entity?, entityHit: Entity) {
		if (entityHit is LivingEntity)
			entityHit.addPotionEffect(createEffectInstance())
	}

	abstract fun createEffectInstance(): EffectInstance
}
