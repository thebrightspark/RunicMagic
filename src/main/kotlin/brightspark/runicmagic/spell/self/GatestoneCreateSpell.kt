package brightspark.runicmagic.spell.self

import brightspark.runicmagic.init.RMBlocks
import brightspark.runicmagic.init.RMCapabilities
import brightspark.runicmagic.init.RMParticles
import brightspark.runicmagic.model.Location
import brightspark.runicmagic.model.SpellCastData
import brightspark.runicmagic.particle.ColouredParticleData
import brightspark.runicmagic.tile.GatestoneTile
import brightspark.runicmagic.util.addParticle
import brightspark.runicmagic.util.onClient
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.fluid.Fluids
import net.minecraft.util.Direction
import net.minecraft.world.World
import net.minecraft.world.server.ServerWorld
import java.awt.Color

class GatestoneCreateSpell(props: Properties) : SelfBaseSpell(props) {
	override fun canCast(player: PlayerEntity): Boolean = player.getCapability(RMCapabilities.SPELLS).map {
		val world = player.world
		val pos = player.position
		val posDown = pos.down()
		val state = world.getBlockState(pos)
		return@map state.isReplaceable(Fluids.EMPTY)
			&& world.getBlockState(posDown).isSolidSide(world, posDown, Direction.UP)
			&& !hasPlayerMoved(player)
	}.orElse(false)

	override fun updateCasting(world: World, player: PlayerEntity, progress: Int): Boolean {
		world.onClient {
			val look = player.lookVec
			val pos = player.positionVec.add(look.x, player.eyeHeight * 0.85, look.z)
			repeat(10) {
				val offset = randVector(rand).scale(0.3)
				addParticle(
					ColouredParticleData(RMParticles.SINGLE_MOVING, Color.ORANGE, Color.WHITE, 20),
					pos.add(offset),
					offset.scale(-0.05)
				)
			}
			if (progress == castTime) {
				repeat(150) {
					addParticle(
						ColouredParticleData(RMParticles.SINGLE_MOVING, Color.ORANGE, Color.RED, 20, true),
						pos,
						randVector(rand).scale(rand.nextDouble() * 0.05)
					)
				}
			}
		}
		return hasPlayerMoved(player)
	}

	override fun execute(player: ServerPlayerEntity, data: SpellCastData): Boolean {
		val world = player.world
		val pos = player.position
		if (world.setBlockState(pos, RMBlocks.GATESTONE.defaultState)) {
			val te = world.getTileEntity(pos)
			if (te is GatestoneTile) {
				te.setOwner(player.uniqueID)
				return player.getCapability(RMCapabilities.SPELLS)
					.map { cap ->
						cap.getGatestone()?.let {
							(world as ServerWorld).server.getWorld(it.dimensionKey)?.removeBlock(it.position, false)
						}
						cap.setGatestone(Location(world.dimensionKey.location, pos))
						return@map true
					}
					.orElse(false)
			}
		}
		return false
	}
}
