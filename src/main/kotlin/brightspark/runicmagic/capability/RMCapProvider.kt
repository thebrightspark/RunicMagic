package brightspark.runicmagic.capability

import net.minecraft.nbt.CompoundNBT
import net.minecraft.util.Direction
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilitySerializable
import net.minecraftforge.common.util.INBTSerializable
import net.minecraftforge.common.util.LazyOptional

class RMCapProvider<C : INBTSerializable<CompoundNBT>>(
	capSupplier: () -> Capability<C>,
	capInstanceSupplier: () -> C
) : ICapabilitySerializable<CompoundNBT> {
	private val capability: Capability<C> by lazy(capSupplier)
	private val instance = LazyOptional.of(capInstanceSupplier)

	override fun <T> getCapability(cap: Capability<T>, side: Direction?): LazyOptional<T> =
		capability.orEmpty(cap, instance)

	override fun serializeNBT(): CompoundNBT = instance.map { it.serializeNBT() }.orElse(CompoundNBT())

	override fun deserializeNBT(nbt: CompoundNBT?) = instance.ifPresent { it.deserializeNBT(nbt) }
}
