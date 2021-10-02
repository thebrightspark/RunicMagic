package brightspark.runicmagic.capability

import brightspark.runicmagic.RunicMagic
import brightspark.runicmagic.message.SyncLevelCapMessage
import brightspark.runicmagic.util.sendToPlayer
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.nbt.CompoundNBT
import net.minecraft.util.ResourceLocation

interface LevelCap : RMCap {
	companion object {
		val RL = ResourceLocation(RunicMagic.MOD_ID, "level")
	}

	fun getLevel(): Int

	fun setLevel(player: ServerPlayerEntity, level: Int)

	fun getExperience(): Int

	fun setExperience(player: ServerPlayerEntity, experience: Int)

	fun setExperienceInternal(experience: Int): Boolean

	fun addExperience(player: ServerPlayerEntity, experience: Int): Int

	class Impl : LevelCap {
		private var level: Int = 0
		private var experience: Int = 0
		private var levelMaxExp: Int = 0

		override fun getLevel(): Int = level

		override fun setLevel(player: ServerPlayerEntity, level: Int) {
			if (this.level != level) {
				this.level = level
				calcLevel(true)
				dataChanged(player)
			}
		}

		override fun getExperience(): Int = experience

		override fun setExperience(player: ServerPlayerEntity, experience: Int) {
			if (setExperienceInternal(experience))
				dataChanged(player)
		}

		override fun setExperienceInternal(experience: Int): Boolean {
			val changed = this.experience != experience
			if (changed) {
				this.experience = experience
				calcLevel(true)
			}
			return changed
		}

		override fun addExperience(player: ServerPlayerEntity, experience: Int): Int {
			this.experience += experience
			calcLevel(false)
			dataChanged(player)
			return this.experience
		}

		override fun dataChanged(player: ServerPlayerEntity) =
			RunicMagic.NETWORK.sendToPlayer(SyncLevelCapMessage(experience), player)

		private fun calcLevel(force: Boolean) {
			if (level > 0 && levelMaxExp > 0 && experience < levelMaxExp)
				return
			if (force) {
				level = LevelManager.getLevelForXp(experience)
				levelMaxExp = LevelManager.getXpForLevel(level + 1)
			} else {
				if (level <= 0)
					level = LevelManager.getLevelForXp(experience)
				if (levelMaxExp <= 0)
					levelMaxExp = LevelManager.getXpForLevel(level + 1)
				while (experience >= levelMaxExp) {
					level++
					levelMaxExp = LevelManager.getXpForLevel(level + 1)
				}
			}
		}

		override fun serializeNBT(): CompoundNBT = CompoundNBT().apply { putInt("xp", experience) }

		override fun deserializeNBT(nbt: CompoundNBT) {
			experience = nbt.getInt("xp")
			// Calc new level as necessary
			if (level <= 0 || levelMaxExp <= 0)
				calcLevel(true)
			else if (experience >= levelMaxExp)
				calcLevel(false)
		}
	}
}
