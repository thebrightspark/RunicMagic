package brightspark.runicmagic.model

import java.awt.Color
import java.util.*

enum class RuneType {
	NONE(Color(0, 0, 0), false, false),

	//Elemental
	AIR(Color(0xFAFAF0), true, false),
	WATER(Color(0x6666FF), true, false),
	EARTH(Color(0x964F0C), true, false),
	FIRE(Color(0xDF2628), true, false),

	//Combination
	MIST(Color(0xE6E6FF), AIR, WATER),
	DUST(Color(0x805020), AIR, EARTH),
	MUD(Color(0x804000), WATER, EARTH),
	SMOKE(Color(0x404040), AIR, FIRE),
	STEAM(Color(0xE6E6FF), WATER, FIRE),
	LAVA(Color(0xFF6400), EARTH, FIRE),

	//Catalytic
	MIND(Color(0xFF7E00), true, false),
	BODY(Color(0x0000FF), true, false),
	COSMIC(Color(0xFFFF00), true, true),
	CHAOS(Color(0xFF7E00), true, true),
	NATURE(Color(0x00FF00), true, true),
	LAW(Color(0x0000FF), true, true),
	DEATH(Color(0xFFFFFF), true, true),

	ASTRAL(Color(0xFFE6FF), false, true),
	BLOOD(Color(0xFF0000), true, true),
	SOUL(Color(0x8080FF), true, true);

	companion object {
		val ELEMENTAL_TYPES = listOf(AIR, WATER, EARTH, FIRE)
		val MAIN_TYPES = values().filter { it.subTypes.isEmpty() }
	}

	val colour: Color
	val hasTalisman: Boolean
	val needsPureEssence: Boolean
	val subTypes: Array<out RuneType>
	val unlocName: String = "item.runicmagic.rune_${name.toLowerCase(Locale.ROOT)}"

	constructor(colour: Color, hasTalisman: Boolean, needsPureEssence: Boolean) {
		this.colour = colour
		this.hasTalisman = hasTalisman
		this.needsPureEssence = needsPureEssence
		this.subTypes = emptyArray()
	}

	// Used for combination type runes
	constructor(colour: Color, vararg subTypes: RuneType) {
		this.colour = colour
		hasTalisman = false
		needsPureEssence = true
		this.subTypes = subTypes
	}

	override fun toString(): String = super.toString().toLowerCase(Locale.ROOT)
}
