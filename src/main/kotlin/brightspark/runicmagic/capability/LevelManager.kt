package brightspark.runicmagic.capability

import brightspark.runicmagic.RunicMagic
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.round

object LevelManager {
	private const val MAX_LEVEL = 99

	private val experience = Int2IntOpenHashMap(MAX_LEVEL).apply {
		put(1, 0)
		(2..MAX_LEVEL).forEach { put(it, experienceForLevel(it)) }
		RunicMagic.LOG.info("Generated XP per level:\n${(1..MAX_LEVEL).joinToString("\n") { "$it\t=${get(it)}" }}")
	}

	private fun clampLevel(level: Int): Int = min(max(level, 1), MAX_LEVEL)

	private fun experienceForLevel(level: Int): Int {
		if (level < 1)
			return 0
		val levelD = min(level, MAX_LEVEL).toDouble()
		val levelEighth = levelD / 8.0
		val d1 = 2.0.pow((levelD - 1.0) / 7.0) - 1.0
		val d2 = 1.0 - 2.0.pow(-(1.0 / 7.0))
		val xp = ((levelD * levelEighth) - levelEighth) + (75.0 * (d1 / d2))
		val truncation = 0.109 * levelD
		return round(xp - truncation).toInt()
	}

	fun getXpForLevel(level: Int): Int = clampLevel(level).let { clampedLevel ->
		experience.getOrPut(clampedLevel) {
			experienceForLevel(clampedLevel).also {
				RunicMagic.LOG.info("Lazily generated experience for level $clampedLevel: $it")
			}
		}
	}

	fun getXpToNextLevel(level: Int): Int =
		if (level < MAX_LEVEL) { clampLevel(level).let { getXpForLevel(it + 1) - getXpForLevel(it) } } else 0

	fun getLevelForXp(xp: Int): Int {
		if (xp <= 0) return 1
		(1..99).forEach {
			if (xp >= experience[it] && xp < experience[it + 1])
				return it
		}
		return experience[MAX_LEVEL]
	}
}
