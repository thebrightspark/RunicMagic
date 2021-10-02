package brightspark.runicmagic.model

import brightspark.runicmagic.model.RuneType.*
import java.util.*

enum class StaffType(val attackBonus: Float, vararg val runeTypes: RuneType) {
	BASIC(0F, NONE, AIR, WATER, EARTH, FIRE),
	BATTLE(2F, NONE, AIR, WATER, EARTH, FIRE, LAVA, MUD, STEAM),
	MYSTIC(3F, AIR, WATER, EARTH, FIRE, LAVA, MUD, STEAM);
	//LIMITLESS(5);

	override fun toString(): String = super.toString().toLowerCase(Locale.ROOT)
}
