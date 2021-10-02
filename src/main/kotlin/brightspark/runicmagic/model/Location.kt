package brightspark.runicmagic.model

import net.minecraft.nbt.CompoundNBT
import net.minecraft.util.RegistryKey
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.Registry
import net.minecraft.world.World
import net.minecraftforge.common.util.INBTSerializable

class Location : INBTSerializable<CompoundNBT> {
	lateinit var dimension: ResourceLocation
		private set
	lateinit var position: BlockPos
		private set
	val dimensionKey: RegistryKey<World> by lazy { RegistryKey.getOrCreateKey(Registry.WORLD_KEY, dimension) }

	constructor(dimension: ResourceLocation, position: BlockPos) {
		this.dimension = dimension
		this.position = position
	}

	constructor(nbt: CompoundNBT) {
		deserializeNBT(nbt)
	}

	override fun serializeNBT(): CompoundNBT = CompoundNBT().apply {
		putString("dim", dimension.toString())
		putLong("pos", position.toLong())
	}

	override fun deserializeNBT(nbt: CompoundNBT) {
		dimension = ResourceLocation(nbt.getString("dim"))
		position = BlockPos.fromLong(nbt.getLong("pos"))
	}
}