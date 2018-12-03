package brightspark.runicmagic.block.tileentity;

import brightspark.runicmagic.init.RMCapabilities;
import brightspark.runicmagic.util.Location;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
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

    /**
     * Makes sure that this Gatestone is valid by checking the player data exists and the position is valid
     */
    public boolean validateGatestone()
    {
        if(playerUuid == null)
            return false;
        if(world.getMinecraftServer().getPlayerProfileCache().getProfileByUUID(playerUuid) == null)
            return false;
        EntityPlayer owner = world.getMinecraftServer().getPlayerList().getPlayerByUUID(playerUuid);
        if(owner == null)
            //We can't validate the location since we can't access the player's capability data while they're offline!
            //So we'll just presume it's valid for now
            return true;
        Location location = RMCapabilities.getSpells(owner).getGatestone();
        return location.getDimension() == world.provider.getDimension() && location.getPosition().equals(pos);
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
