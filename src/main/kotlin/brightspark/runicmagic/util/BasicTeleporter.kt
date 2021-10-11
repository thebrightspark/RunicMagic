package brightspark.runicmagic.util

import net.minecraft.entity.Entity
import net.minecraft.world.server.ServerWorld
import net.minecraftforge.common.util.ITeleporter
import java.util.function.Function

object BasicTeleporter : ITeleporter {
	override fun placeEntity(
		entity: Entity?,
		currentWorld: ServerWorld?,
		destWorld: ServerWorld?,
		yaw: Float,
		repositionEntity: Function<Boolean, Entity>
	): Entity = repositionEntity.apply(false)
}
