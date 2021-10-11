package brightspark.runicmagic.spell.teleport

import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.util.math.BlockPos

class HomeTeleportSpell(props: Properties) : TeleportBaseSpell(props) {
	override fun getDestinationPosition(player: ServerPlayerEntity): BlockPos? = player.bedPosition.orElse(null)
}
