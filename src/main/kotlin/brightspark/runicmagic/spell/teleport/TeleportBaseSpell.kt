package brightspark.runicmagic.spell.teleport

import brightspark.runicmagic.init.RMParticles
import brightspark.runicmagic.model.SpellCastData
import brightspark.runicmagic.particle.ColouredParticleData
import brightspark.runicmagic.spell.Spell
import brightspark.runicmagic.util.BasicTeleporter
import brightspark.runicmagic.util.addParticle
import brightspark.runicmagic.util.onClient
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.util.RegistryKey
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.vector.Vector3d
import net.minecraft.world.World
import java.awt.Color

abstract class TeleportBaseSpell(props: Properties) : Spell(props) {
	companion object {
		private val COLOR = Color(0x2c1863)
	}

	override fun canCast(player: PlayerEntity): Boolean = !hasPlayerMoved(player)

	override fun updateCasting(world: World, player: PlayerEntity, progress: Int): Boolean {
		world.onClient {
			val pos = player.positionVec
			val particleData = ColouredParticleData(RMParticles.SINGLE_MOVING, COLOR)
			val numParticles = ((progress.toFloat() / castTime.toFloat()) * 20F).toInt()
			repeat(numParticles) {
				addParticle(
					particleData,
					posOffset(world, pos, 1.0, 0.0, 1.0),
					Vector3d(0.0, (rand.nextDouble() * 0.02) + 0.04, 0.0)
				)
			}
		}
		return hasPlayerMoved(player)
	}

	override fun execute(player: ServerPlayerEntity, data: SpellCastData): Boolean {
		val world = player.serverWorld
		val server = world.server

		val dim = getDestinationDimension(player)?.let { server.getWorld(it) } ?: return false
		if (!player.world.dimensionKey.location.equals(dim)) {
			player.changeDimension(dim, BasicTeleporter)
			return true
		}

		var pos = getDestinationPosition(player)?.let { Vector3d(it.x + 0.5, it.y.toDouble(), it.z + 0.5) }
			?: return false
		var box = player.boundingBox.offset(pos.subtract(player.positionVec))

		val yChange = if (world.hasNoCollisions(box)) -1.0 else 1.0
		while (!world.hasNoCollisions(box) && pos.y > 0 && pos.y <= dim.height) {
			pos = pos.add(0.0, yChange, 0.0)
			box = box.offset(0.0, yChange, 0.0)
		}

		// FIXME: Why isn't this actually teleporting??
		player.setPositionAndUpdate(pos.x, pos.y, pos.z)
		return true
	}

	open fun getDestinationDimension(player: ServerPlayerEntity): RegistryKey<World>? = World.OVERWORLD

	open fun getDestinationPosition(player: ServerPlayerEntity): BlockPos? = player.position
}
