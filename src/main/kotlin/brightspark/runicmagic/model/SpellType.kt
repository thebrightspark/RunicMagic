package brightspark.runicmagic.model

import brightspark.runicmagic.model.SpellType.MagicType.*
import net.minecraft.client.resources.I18n
import net.minecraft.util.text.TextFormatting
import java.util.*

enum class SpellType(val magicType: MagicType) {
	ELEMENTAL(COMBAT),
	CURSES(COMBAT),
	SUPPORT(COMBAT),

	TELESELF(TELEPORT),
	TELEOTHER(TELEPORT),
	TELEGROUP(TELEPORT),

	ENCHANTMENT(SKILLING),
	ALCHEMY(SKILLING),
	OTHER(SKILLING);

	val unlocName: String = "spelltype.${name.toLowerCase(Locale.ROOT)}.name"

	fun getTranslation(): String = I18n.format(unlocName)

	fun getMagicTypeColour(): TextFormatting = magicType.textColour

	enum class MagicType(val textColour: TextFormatting) {
		COMBAT(TextFormatting.RED),
		TELEPORT(TextFormatting.BLUE),
		SKILLING(TextFormatting.GOLD)
	}
}
