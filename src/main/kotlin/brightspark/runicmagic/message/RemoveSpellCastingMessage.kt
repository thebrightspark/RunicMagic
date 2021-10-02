package brightspark.runicmagic.message

import brightspark.runicmagic.spell.SpellHandler
import brightspark.runicmagic.util.Message
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.PacketBuffer
import net.minecraftforge.fml.network.NetworkEvent
import java.util.*
import java.util.function.Supplier

class RemoveSpellCastingMessage : Message {
	lateinit var uuid: UUID

	@Suppress("unused")
	constructor()

	constructor(player: PlayerEntity) {
		uuid = player.uniqueID
	}

	override fun encode(buffer: PacketBuffer) {
		buffer.writeUniqueId(uuid)
	}

	override fun decode(buffer: PacketBuffer) {
		uuid = buffer.readUniqueId()
	}

	override fun consume(context: Supplier<NetworkEvent.Context>) = context.get().run {
		enqueueWork {
			SpellHandler.removeSpellCast(uuid)
		}
		packetHandled = true
	}
}
