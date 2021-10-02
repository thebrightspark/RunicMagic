package brightspark.runicmagic.block

import brightspark.runicmagic.init.RMCapabilities
import brightspark.runicmagic.model.Location
import brightspark.runicmagic.tile.GatestoneTile
import brightspark.runicmagic.util.onServer
import brightspark.runicmagic.util.sendMessage
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ActionResultType
import net.minecraft.util.Direction
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.BlockRayTraceResult
import net.minecraft.util.math.shapes.ISelectionContext
import net.minecraft.util.math.shapes.VoxelShape
import net.minecraft.util.text.StringTextComponent
import net.minecraft.world.*

class GatestoneBlock(props: Properties) : Block(props) {
	companion object {
		private val SHAPE = makeCuboidShape(6.0, 0.0, 6.0, 10.0, 10.0, 10.0)
	}

	override fun hasTileEntity(state: BlockState?): Boolean = true

	override fun createTileEntity(state: BlockState, world: IBlockReader): TileEntity = GatestoneTile()

	override fun onBlockActivated(
		state: BlockState,
		world: World,
		pos: BlockPos,
		player: PlayerEntity,
		hand: Hand,
		ray: BlockRayTraceResult
	): ActionResultType {
		world.onServer {
			val te = world.getTileEntity(pos)
			if (te is GatestoneTile && te.validateGatestone()) {
				val owner = world.server!!.playerList.getPlayerByUUID(te.ownerUuid)
				val name = if (player == owner) "you" else owner!!.displayName.string
				player.sendMessage(StringTextComponent("This Gatestone belongs to $name")) // TODO: Localise
			} else {
				world.destroyBlock(pos, false)
			}
		}
		return ActionResultType.SUCCESS
	}

	override fun onBlockPlacedBy(
		world: World,
		pos: BlockPos,
		state: BlockState,
		placer: LivingEntity?,
		stack: ItemStack
	) {
		if (placer is PlayerEntity) {
			val spells = placer.getCapability(RMCapabilities.SPELLS).resolve()
			if (spells.isPresent) {
				val te = world.getTileEntity(pos)
				if (te is GatestoneTile) {
					te.setOwner(placer.uniqueID, state)
					spells.get().setGatestone(Location(world.dimensionKey.location, pos))
					return
				}
			}
		}
		world.destroyBlock(pos, false)
	}

	override fun neighborChanged(
		state: BlockState,
		world: World,
		pos: BlockPos,
		blockIn: Block,
		fromPos: BlockPos,
		isMoving: Boolean
	) {
		if (!isValidPosition(state, world, pos) ||
			!world.getTileEntity(pos).let { it is GatestoneTile && it.validateGatestone() }) {
			onDestroyed(world, pos)
			world.destroyBlock(pos, false)
		}
	}

	override fun onPlayerDestroy(world: IWorld, pos: BlockPos, state: BlockState) = onDestroyed(world, pos)

	override fun onExplosionDestroy(world: World, pos: BlockPos, explosion: Explosion) = onDestroyed(world, pos)

	override fun isValidPosition(state: BlockState, world: IWorldReader, pos: BlockPos): Boolean =
		pos.down().let { world.getBlockState(it).isSolidSide(world, it, Direction.UP) }

	private fun onDestroyed(world: IWorldReader, pos: BlockPos) {
		if (world !is IEntityReader) return
		world.onServer {
			val te = getTileEntity(pos)
			if (te !is GatestoneTile) return
			getPlayerByUuid(te.ownerUuid)?.let { owner ->
				owner.getCapability(RMCapabilities.SPELLS).ifPresent {
					it.setGatestone(null)
					owner.sendMessage(StringTextComponent("Gatestone has been destroyed!")) // TODO: Localise
				}
			}
		}
	}

	override fun getShape(
		state: BlockState,
		worldIn: IBlockReader,
		pos: BlockPos,
		context: ISelectionContext
	): VoxelShape = SHAPE
}
