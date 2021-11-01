package brightspark.runicmagic.spell.projectile.curse

import net.minecraft.potion.EffectInstance
import net.minecraft.potion.Effects
import java.awt.Color

class WeakenSpell(props: Properties) : CurseBaseSpell(props, Color.MAGENTA) {
	override fun createEffectInstance(): EffectInstance = EffectInstance(Effects.WEAKNESS, 200)
}
