package brightspark.runicmagic.tile

import net.minecraft.block.BlockState
import net.minecraft.nbt.CompoundNBT
import net.minecraft.network.NetworkManager
import net.minecraft.network.play.server.SUpdateTileEntityPacket
import net.minecraft.tileentity.TileEntity
import net.minecraft.tileentity.TileEntityType

abstract class RMTile(type: TileEntityType<*>) : TileEntity(type) {
	protected fun notifyUpdate(state: BlockState) = world!!.notifyBlockUpdate(pos, state, state, 3)

	override fun getUpdatePacket(): SUpdateTileEntityPacket = SUpdateTileEntityPacket(pos, 0, updateTag)

	override fun getUpdateTag(): CompoundNBT = serializeNBT()

	override fun onDataPacket(net: NetworkManager, pkt: SUpdateTileEntityPacket) = deserializeNBT(pkt.nbtCompound)
}
