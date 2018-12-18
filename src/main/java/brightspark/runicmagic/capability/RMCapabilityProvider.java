package brightspark.runicmagic.capability;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RMCapabilityProvider<C extends RMCapability> implements ICapabilitySerializable<NBTTagCompound>
{
	private Capability<C> capability;
	private C instance;

	public RMCapabilityProvider(Capability<C> capability)
	{
		this.capability = capability;
		instance = capability.getDefaultInstance();
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
	{
		return this.capability == capability;
	}

	@SuppressWarnings("unchecked")
	@Nullable
	@Override
	public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
	{
		return hasCapability(capability, facing) ? (T) instance : null;
	}

	@Override
	public NBTTagCompound serializeNBT()
	{
		return instance.serializeNBT();
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt)
	{
		instance.deserializeNBT(nbt);
	}
}
