package brightspark.runicmagic.spell.teleport

import brightspark.runicmagic.init.RMCapabilities
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.util.RegistryKey
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class GatestoneTeleportSpell(props: Properties) : TeleportBaseSpell(props) {
	override fun canCast(player: PlayerEntity): Boolean =
		player.getCapability(RMCapabilities.SPELLS).map { it.getGatestone() != null }.orElse(false)
			&& super.canCast(player)

	override fun getDestinationDimension(player: ServerPlayerEntity): RegistryKey<World>? =
		player.getCapability(RMCapabilities.SPELLS).resolve().map { it.getGatestone()?.dimensionKey }.orElse(null)

	override fun getDestinationPosition(player: ServerPlayerEntity): BlockPos? =
		player.getCapability(RMCapabilities.SPELLS).resolve().map { it.getGatestone()?.position }.orElse(null)
}
