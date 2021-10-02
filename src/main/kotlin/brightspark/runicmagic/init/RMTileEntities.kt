package brightspark.runicmagic.init

import brightspark.runicmagic.tile.GatestoneTile
import brightspark.runicmagic.util.setRegName
import net.minecraft.block.Block
import net.minecraft.tileentity.TileEntity
import net.minecraft.tileentity.TileEntityType
import net.minecraftforge.event.RegistryEvent
import thedarkcolour.kotlinforforge.forge.objectHolder

object RMTileEntities {
	val TILE_GATESTONE: TileEntityType<GatestoneTile> by objectHolder("gatestone")

	fun register(event: RegistryEvent.Register<TileEntityType<*>>) = event.registry.registerAll(
		tileEntityType(::GatestoneTile, RMBlocks.GATESTONE)
	)

	@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
	private fun tileEntityType(factory: () -> TileEntity, block: Block): TileEntityType<*> =
		TileEntityType.Builder.create(factory, block).build(null).setRegName(block.registryName!!.path)
}
