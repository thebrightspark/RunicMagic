package brightspark.runicmagic.message

import brightspark.runicmagic.init.RMCapabilities
import brightspark.runicmagic.init.RMSpells
import brightspark.runicmagic.item.StaffItem
import brightspark.runicmagic.spell.Spell
import brightspark.runicmagic.util.Message
import brightspark.runicmagic.util.RMUtils
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketBuffer
import net.minecraftforge.fml.network.NetworkEvent
import java.util.function.Supplier

class SpellSelectMessage : Message {
	private lateinit var spell: Spell

	@Suppress("unused")
	constructor()

	constructor(spell: Spell) {
		this.spell = spell
	}

	override fun encode(buffer: PacketBuffer) {
		buffer.writeResourceLocation(spell.registryName!!)
	}

	override fun decode(buffer: PacketBuffer) {
		spell = RMSpells.get(buffer.readResourceLocation())!!
	}

	override fun consume(context: Supplier<NetworkEvent.Context>) = context.get().run {
		enqueueWork {
			val player = sender ?: return@enqueueWork
			player.getCapability(RMCapabilities.SPELLS).ifPresent { spells ->
				if (spell.selectable)
					spells.setSelectedSpell(player, spell)
				else
					spells.executeSpell(
						player,
						RMUtils.findHeldItem(player) { it is StaffItem }?.second ?: ItemStack.EMPTY,
						spell
					)
			}
		}
		packetHandled = true
	}
}
