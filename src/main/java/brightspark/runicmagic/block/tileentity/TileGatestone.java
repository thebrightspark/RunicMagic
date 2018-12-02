package brightspark.runicmagic.block.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;

import java.util.UUID;

public class TileGatestone extends RMTileEntity
{
    private UUID playerUuid = null;

    public UUID getPlayerUuid()
    {
        return playerUuid;
    }

    public void setPlayerUuid(IBlockState state, UUID playerUuid)
    {
        this.playerUuid = playerUuid;
        markDirty();
        notifyUpdate(pos, state);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        if(playerUuid != null)
            nbt.setUniqueId("player", playerUuid);
        return super.writeToNBT(nbt);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        if(nbt.hasUniqueId("player"))
            playerUuid = nbt.getUniqueId("player");
    }
}
