package brightspark.runicmagic.message

import brightspark.runicmagic.gui.SpellSelectScreen
import brightspark.runicmagic.init.RMCapabilities
import brightspark.runicmagic.init.RMSpells
import brightspark.runicmagic.model.Location
import brightspark.runicmagic.spell.Spell
import brightspark.runicmagic.util.Message
import net.minecraft.client.Minecraft
import net.minecraft.network.PacketBuffer
import net.minecraftforge.fml.network.NetworkEvent
import java.util.function.Supplier

class SyncSpellsCapMessage : Message {
	private var selectedSpell: Spell? = null
	private var cooldownsChanged: Boolean = false
	private var cooldowns: Map<Spell, Long>? = null
	private var gatestoneLocation: Location? = null

	@Suppress("unused")
	constructor()

	constructor(selectedSpell: Spell) {
		this.selectedSpell = selectedSpell
	}

	constructor(spellOnCooldown: Spell, cooldown: Long) : this(mapOf(spellOnCooldown to cooldown), true)

	constructor(cooldowns: Map<Spell, Long>, changed: Boolean) {
		cooldownsChanged = changed
		this.cooldowns = cooldowns
	}

	constructor(selectedSpell: Spell?, cooldowns: Map<Spell, Long>, gatestoneLocation: Location?) {
		this.selectedSpell = selectedSpell
		this.cooldowns = cooldowns
		this.gatestoneLocation = gatestoneLocation
	}

	override fun encode(buffer: PacketBuffer) {
		buffer.run {
			writeBoolean(selectedSpell != null)
			selectedSpell?.let {
				writeResourceLocation(it.registryName!!)
			}
			writeBoolean(cooldowns != null)
			cooldowns?.let {
				writeBoolean(cooldownsChanged)
				writeInt(it.size)
				it.forEach { (spell, cooldown) ->
					writeResourceLocation(spell.registryName!!)
					writeLong(cooldown)
				}
			}
		}
	}

	override fun decode(buffer: PacketBuffer) = buffer.run {
		selectedSpell = if (readBoolean()) RMSpells.get(readResourceLocation()) else null
		val hasCooldowns = readBoolean()
		cooldownsChanged = if (hasCooldowns) readBoolean() else false
		cooldowns = if (hasCooldowns) {
			(0 until readInt()).map { RMSpells.get(readResourceLocation())!! to readLong() }.toMap()
		} else null
	}

	override fun consume(context: Supplier<NetworkEvent.Context>) = context.get().run {
		enqueueWork {
			val mc = Minecraft.getInstance()
			val player = mc.player!!
			player.getCapability(RMCapabilities.SPELLS).ifPresent { spells ->
				selectedSpell?.let { spells.setSelectedSpell(player, it) }
				cooldowns?.let { if (cooldownsChanged) it.forEach(spells::updateCooldown) else spells.setCooldowns(it) }

				mc.currentScreen?.takeIf { it is SpellSelectScreen }?.let {
					(it as SpellSelectScreen).onSpellCapChanged()
				}
			}
		}
		packetHandled = true
	}
}
