package brightspark.runicmagic.message

import brightspark.runicmagic.init.RMCapabilities
import brightspark.runicmagic.util.Message
import net.minecraft.client.Minecraft
import net.minecraft.network.PacketBuffer
import net.minecraftforge.fml.network.NetworkEvent
import java.util.function.Supplier

class SyncLevelCapMessage : Message {
	private var experience: Int = 0

	@Suppress("unused")
	constructor()

	constructor(experience: Int) {
		this.experience = experience
	}

	override fun encode(buffer: PacketBuffer) {
		buffer.writeInt(experience)
	}

	override fun decode(buffer: PacketBuffer) {
		experience = buffer.readInt()
	}

	override fun consume(context: Supplier<NetworkEvent.Context>) = context.get().run {
		enqueueWork {
			Minecraft.getInstance().player!!.getCapability(RMCapabilities.LEVEL).ifPresent {
				it.setExperienceInternal(experience)
			}
		}
		packetHandled = true
	}
}
