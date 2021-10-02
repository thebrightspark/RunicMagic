package brightspark.runicmagic.tile

import brightspark.runicmagic.init.RMCapabilities
import brightspark.runicmagic.init.RMTileEntities
import net.minecraft.block.BlockState
import net.minecraft.nbt.CompoundNBT
import net.minecraft.util.Util
import java.util.*

class GatestoneTile : RMTile(RMTileEntities.TILE_GATESTONE) {
	var ownerUuid: UUID = Util.DUMMY_UUID
		private set

	fun setOwner(playerUUID: UUID, state: BlockState = world!!.getBlockState(pos)) {
		ownerUuid = playerUUID
		markDirty()
		notifyUpdate(state)
	}

	/**
	 * Makes sure that this Gatestone is valid by checking the player data exists and the position is valid
	 */
	fun validateGatestone(): Boolean {
		if (ownerUuid == Util.DUMMY_UUID) return false
		val server = world!!.server!!
		if (server.playerProfileCache.getProfileByUUID(ownerUuid) == null) return false
		// If player is offline, then we can't validate the location since we can't access the player's capability data
		// while they're offline! So we'll just presume it's valid for now
		val owner = server.playerList.getPlayerByUUID(ownerUuid) ?: return true
		val spells = owner.getCapability(RMCapabilities.SPELLS).resolve()
		if (!spells.isPresent || spells.get().getGatestone() == null)
			return false
		return spells.get().getGatestone()!!.let { it.dimensionKey == world!!.dimensionKey && it.position == pos }
	}

	override fun serializeNBT(): CompoundNBT = super.serializeNBT().apply { putUniqueId("owner", ownerUuid) }

	override fun deserializeNBT(nbt: CompoundNBT) {
		super.deserializeNBT(nbt)
		ownerUuid = nbt.getUniqueId("owner")
	}
}
