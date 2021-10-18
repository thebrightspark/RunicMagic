package brightspark.runicmagic.spell

import brightspark.runicmagic.RunicMagic
import brightspark.runicmagic.init.RMCapabilities
import brightspark.runicmagic.init.RMSpells
import brightspark.runicmagic.message.RemoveSpellCastingMessage
import brightspark.runicmagic.model.RuneType
import brightspark.runicmagic.model.SpellCastData
import brightspark.runicmagic.util.onClient
import brightspark.runicmagic.util.onServer
import brightspark.runicmagic.util.sendToAll
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.nbt.CompoundNBT
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World
import net.minecraftforge.common.util.INBTSerializable

class SpellCasting : INBTSerializable<CompoundNBT> {
	lateinit var spell: Spell
		private set
	lateinit var data: SpellCastData
		private set
	var progress: Int = 0
		private set

	constructor(spell: Spell, data: SpellCastData) {
		this.spell = spell
		this.data = data
	}

	constructor(nbt: CompoundNBT) {
		deserializeNBT(nbt)
	}

	fun update(world: World, player: PlayerEntity, isClientSideSinglePlayer: Boolean): Boolean {
		var shouldCancel = spell.updateCasting(world, player, progress)

		// Client side might update the spell casting longer than the cast time while we wait for the server to execute
		// the spell. So we make sure to finish it before the progress runs past the cast time.
		world.onClient {
			if (progress >= spell.castTime)
				return true
		}

		if (shouldCancel) {
			// Spell stopped casting
			spell.onCastCancel(player)
			RunicMagic.LOG.info("Cast cancelled by spell $spell at progress $progress / ${spell.castTime}")
		} else {
			shouldCancel = progress >= spell.castTime
			if (shouldCancel) {
				// Finished casting
				RunicMagic.LOG.info("Cast finished for spell $spell")
				world.onServer {
					val spells = player.getCapability(RMCapabilities.SPELLS).resolve().get()
					if (spells.onSpellExecuted(player as ServerPlayerEntity, spell, data)) {
						return spell.execute(player, data)
					}
				}
			}
		}

		// If on a single player world, the handler class is shared between client and server,
		// so we only want to update progress once per tick
		if (!isClientSideSinglePlayer) {
			progress++

			// If cancelled, then update clients
			if (shouldCancel) {
				world.onServer {
					RunicMagic.NETWORK.sendToAll(RemoveSpellCastingMessage(player))
				}
			}
		}

		return shouldCancel
	}

	override fun serializeNBT(): CompoundNBT = CompoundNBT().apply {
		putString("spell", spell.registryName.toString())
		putInt("level", data.magicLevel)
		putFloat("attack", data.attackBonus)
		putInt("rune", data.runeCostReduction.ordinal)
	}

	override fun deserializeNBT(nbt: CompoundNBT) = nbt.run {
		spell = RMSpells.get(ResourceLocation(getString("spell")))!!
		data = SpellCastData(getInt("level"), getFloat("attack"), RuneType.values()[getInt("rune")])
	}
}
