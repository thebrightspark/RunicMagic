package brightspark.runicmagic.capability

import brightspark.runicmagic.RunicMagic
import brightspark.runicmagic.init.RMBlocks
import brightspark.runicmagic.init.RMCapabilities
import brightspark.runicmagic.init.RMSpells
import brightspark.runicmagic.item.StaffItem
import brightspark.runicmagic.message.SyncSpellsCapMessage
import brightspark.runicmagic.model.CanCastResult
import brightspark.runicmagic.model.Location
import brightspark.runicmagic.model.RuneType
import brightspark.runicmagic.model.SpellCastData
import brightspark.runicmagic.spell.Spell
import brightspark.runicmagic.spell.SpellHandler
import brightspark.runicmagic.util.RMUtils
import brightspark.runicmagic.util.sendToPlayer
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundNBT
import net.minecraft.nbt.ListNBT
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World
import net.minecraftforge.common.util.Constants
import net.minecraftforge.fml.server.ServerLifecycleHooks

interface SpellCap : RMCap {
	companion object {
		val RL = ResourceLocation(RunicMagic.MOD_ID, "spell")
	}

	/**
	 * Gets the current selected spell
	 */
	fun getSelectedSpell(): Spell?

	/**
	 * Sets the current selected spell to the given spell
	 * Returns false if it's the current spell or the spell is not selectable
	 */
	fun setSelectedSpell(player: PlayerEntity, spell: Spell): Boolean

	/**
	 * Gets the current selected spell cooldown
	 */
	fun getSpellCooldown(): Int

	/**
	 * Gets a copy of all spell cooldowns
	 */
	fun getCooldowns(world: World): Map<Spell, Long>

	/**
	 * Used by messages to sync all spell cooldowns from the server
	 */
	fun setCooldowns(cooldowns: Map<Spell, Long>)

	/**
	 * Starts a cooldown for the given spell
	 */
	fun addCooldown(player: PlayerEntity, spell: Spell)

	/**
	 * Used by messages to sync a specific spell cooldown from the server
	 */
	fun updateCooldown(spell: Spell, cooldown: Long?)

	/**
	 * Gets the location of the player's gatestone if they have one placed
	 */
	fun getGatestone(): Location?

	/**
	 * Sets the location of the player's gatestone
	 */
	fun setGatestone(location: Location?)

	/**
	 * Checks if the player can execute the given spell
	 * If explicitSpell is null, then checks the current selected spell
	 */
	fun canExecuteSpell(player: PlayerEntity, stack: ItemStack, explicitSpell: Spell?): CanCastResult

	/**
	 * Executes the currently selected explicitSpell if there is one
	 * It's expected that canExecuteSpell has already been called before this!
	 */
	fun executeSpell(player: ServerPlayerEntity, stack: ItemStack, explicitSpell: Spell?): CanCastResult

	/**
	 * Called when a spell is about to be executed to do any final checks and processing
	 * Spell should be cancelled if this returns false
	 */
	fun onSpellExecuted(player: ServerPlayerEntity, spell: Spell, data: SpellCastData): Boolean

	class Impl : SpellCap {
		private var selectedSpell: Spell? = null
		private val cooldowns: MutableMap<Spell, Long> = mutableMapOf()
		private var gatestoneLocation: Location? = null

		override fun getSelectedSpell(): Spell? = selectedSpell

		override fun setSelectedSpell(player: PlayerEntity, spell: Spell): Boolean {
			RunicMagic.LOG.info("Setting spell: $selectedSpell -> $spell")
			if (selectedSpell == spell || !spell.selectable)
				return false
			selectedSpell = spell
			if (player is ServerPlayerEntity)
				RunicMagic.NETWORK.sendToPlayer(SyncSpellsCapMessage(spell), player)
			return true
		}

		override fun getSpellCooldown(): Int = selectedSpell?.cooldown ?: -1

		override fun getCooldowns(world: World): Map<Spell, Long> {
			val worldTime = world.gameTime
			// TODO: Do changes to the entry set affect the map?
			cooldowns.entries.removeIf { it.value <= worldTime }
			return cooldowns.toMap()
		}

		override fun setCooldowns(cooldowns: Map<Spell, Long>) = this.cooldowns.putAll(cooldowns)

		override fun addCooldown(player: PlayerEntity, spell: Spell) {
			if (player.isCreative)
				return
			val cooldown = spell.cooldown
			if (cooldown > 0) {
				val cooldownTimestamp = player.world.gameTime + cooldown
				cooldowns[spell] = cooldownTimestamp
				if (player is ServerPlayerEntity)
					RunicMagic.NETWORK.sendToPlayer(SyncSpellsCapMessage(spell, cooldownTimestamp), player)
			}
		}

		override fun updateCooldown(spell: Spell, cooldown: Long?) {
			cooldown?.let { cooldowns[spell] = it } ?: cooldowns.remove(spell)
		}

		override fun getGatestone(): Location? {
			gatestoneLocation?.let { loc ->
				ServerLifecycleHooks.getCurrentServer()?.let {
					val world = it.getWorld(loc.dimensionKey)
					if (world == null || world.getBlockState(loc.position).block != RMBlocks.GATESTONE)
						gatestoneLocation = null
				}
			}
			return gatestoneLocation
		}

		override fun setGatestone(location: Location?) {
			gatestoneLocation = location
		}

		override fun canExecuteSpell(player: PlayerEntity, stack: ItemStack, explicitSpell: Spell?): CanCastResult {
			val spell = explicitSpell ?: selectedSpell ?: return CanCastResult.NO_SPELL

			// Check if player is in creative
			if (player.isCreative) return CanCastResult.SUCCESS

			// Check player level
			val level = player.getCapability(RMCapabilities.LEVEL).resolve()
			if (!level.isPresent || level.get().getLevel() < spell.level) return CanCastResult.LEVEL

			// Check cooldown
			cooldowns[spell]?.let {
				if (it <= player.world.gameTime)
					cooldowns.remove(spell)
				else
					return CanCastResult.COOLDOWN
			}

			// Check spell requirements
			if (!spell.canCast(player)) return CanCastResult.SPELL_REQ

			// Check runes
			if (!RMUtils.hasRunes(player.inventory.mainInventory, StaffItem.calculateRuneCost(stack, spell)))
				return CanCastResult.RUNES

			return CanCastResult.SUCCESS
		}

		override fun executeSpell(player: ServerPlayerEntity, stack: ItemStack, explicitSpell: Spell?): CanCastResult {
			val spell = explicitSpell ?: selectedSpell ?: return CanCastResult.NO_SPELL
			RunicMagic.LOG.info("Executing $spell")

			val level = player.getCapability(RMCapabilities.LEVEL).resolve()
			val attackBonus = StaffItem.getAttackBonus(stack)
			val runeType = StaffItem.getRuneType(stack)
			val data = SpellCastData(level.get().getLevel(), attackBonus, runeType)

			// If spell has a cast time, add to handler (runes are removed when cast is finished)
			if (spell.castTime > 0) {
				SpellHandler.addSpellCast(player, spell, data)
				return CanCastResult.SUCCESS
			}

			// Remove runes and add cooldown
			// FIXME: If spell execution fails, then runes are still removed...
			if (!onSpellExecuted(player, spell, data))
				return CanCastResult.RUNES

			// Execute spell
			if (!spell.execute(player, data))
				return CanCastResult.SPELL_REQ

			RunicMagic.LOG.info("Successfully instantly executed spell $spell")
			return CanCastResult.SUCCESS
		}

		override fun onSpellExecuted(player: ServerPlayerEntity, spell: Spell, data: SpellCastData): Boolean {
			if (player.isCreative) return true

			// Remove runes for cost
			val cost = spell.runeCost.toMutableMap()
			data.runeCostReduction.takeIf { it != RuneType.NONE }?.let { cost.remove(it) }
			val inv = player.inventory.mainInventory
			if (!RMUtils.hasRunes(inv, cost)) return false
			RMUtils.removeRunes(inv, cost)

			// Add cooldown
			addCooldown(player, spell)
			return true
		}

		override fun dataChanged(player: ServerPlayerEntity) =
			RunicMagic.NETWORK.sendToPlayer(SyncSpellsCapMessage(selectedSpell, cooldowns, gatestoneLocation), player)

		override fun serializeNBT(): CompoundNBT = CompoundNBT().also { nbt ->
			selectedSpell?.let { nbt.putString("spell", it.registryName.toString()) }

			nbt.put("cooldowns", ListNBT().apply {
				cooldowns.forEach { (spell, cooldown) ->
					add(CompoundNBT().apply {
						putString("spell", spell.registryName.toString())
						putLong("cooldown", cooldown)
					})
				}
			})

			gatestoneLocation?.let { nbt.put("gatestone", it.serializeNBT()) }
		}

		override fun deserializeNBT(nbt: CompoundNBT) {
			selectedSpell = if (nbt.contains("spell")) RMSpells.get(nbt.getString("spell")) else null

			cooldowns.clear()
			nbt.getList("cooldowns", Constants.NBT.TAG_COMPOUND).forEach {
				val compound = it as CompoundNBT
				val spell = RMSpells.get(compound.getString("spell")) ?: return@forEach
				cooldowns[spell] = compound.getLong("cooldown")
			}

			gatestoneLocation = if (nbt.contains("gatestone")) Location(nbt.getCompound("gatestone")) else null
		}
	}
}
