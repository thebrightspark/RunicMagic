package brightspark.runicmagic.capability

import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.nbt.CompoundNBT
import net.minecraftforge.common.util.INBTSerializable

interface RMCap : INBTSerializable<CompoundNBT> {
	fun dataChanged(player: ServerPlayerEntity)
}
