package brightspark.runicmagic.capability;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

public interface RMCapability extends INBTSerializable<NBTTagCompound>
{
	/**
	 * Will send updates for all data in this capability
	 */
	void dataChanged(EntityPlayerMP player);

	RMCapability get(EntityPlayerMP player);

	default void copyDataToPlayerCap(EntityPlayerMP playerNew)
	{
		RMCapability capNew = get(playerNew);
		if(capNew != null)
		{
			capNew.deserializeNBT(serializeNBT());
			capNew.dataChanged(playerNew);
		}
	}
}
