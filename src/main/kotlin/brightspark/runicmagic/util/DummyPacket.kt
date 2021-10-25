package brightspark.runicmagic.util

import net.minecraft.client.network.play.IClientPlayNetHandler
import net.minecraft.network.IPacket
import net.minecraft.network.PacketBuffer

object DummyPacket : IPacket<IClientPlayNetHandler> {
	override fun readPacketData(buf: PacketBuffer) = Unit

	override fun writePacketData(buf: PacketBuffer) = Unit

	override fun processPacket(handler: IClientPlayNetHandler) = Unit
}
