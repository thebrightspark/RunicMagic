package brightspark.runicmagic.model

import brightspark.runicmagic.RunicMagic

enum class CanCastResult(failLang: String) {
	SUCCESS("success"),
	NO_SPELL("nospell"),
	COOLDOWN("cooldown"),
	LEVEL("level"),
	RUNES("runes"),
	SPELL_REQ("spellreq");

	val failLang: String = "${RunicMagic.MOD_ID}.message.castfail.$failLang"
}
