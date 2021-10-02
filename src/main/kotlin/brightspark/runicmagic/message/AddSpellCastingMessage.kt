package brightspark.runicmagic.message

import brightspark.runicmagic.spell.SpellCasting
import brightspark.runicmagic.spell.SpellHandler
import brightspark.runicmagic.util.Message
import net.minecraft.client.Minecraft
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.PacketBuffer
import net.minecraftforge.fml.network.NetworkEvent
import java.util.*
import java.util.function.Supplier

class AddSpellCastingMessage : Message {
	private lateinit var uuid: UUID
	private lateinit var casting: SpellCasting

	@Suppress("unused")
	constructor()

	constructor(player: PlayerEntity, casting: SpellCasting) {
		this.uuid = player.uniqueID
		this.casting = casting
	}

	override fun encode(buffer: PacketBuffer) {
		buffer.run {
			writeUniqueId(uuid)
			writeCompoundTag(casting.serializeNBT())
		}
	}

	override fun decode(buffer: PacketBuffer) {
		buffer.run {
			uuid = readUniqueId()
			casting = SpellCasting(readCompoundTag()!!)
		}
	}

	override fun consume(context: Supplier<NetworkEvent.Context>) = context.get().run {
		enqueueWork {
			SpellHandler.addSpellCast(Minecraft.getInstance().player!!, casting.spell, casting.data)
		}
		packetHandled = true
	}
}
