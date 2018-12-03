package brightspark.runicmagic.spell.teleport;

import brightspark.runicmagic.enums.SpellType;
import brightspark.runicmagic.init.RMCapabilities;
import brightspark.runicmagic.util.Location;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

public class SpellTeleportGatestone extends SpellTeleportBase
{
    public SpellTeleportGatestone()
    {
        super("gatestone_teleport", SpellType.TELESELF, 32);
    }

    @Override
    public boolean canCast(EntityPlayer player)
    {
        return RMCapabilities.getSpells(player).getGatestone() != null && super.canCast(player);
    }

    @Override
    protected Integer getDestinationDimId(EntityPlayer player)
    {
        Location location = RMCapabilities.getSpells(player).getGatestone();
        return location != null ? location.getDimension() : null;
    }

    @Override
    protected BlockPos getDestinationPos(EntityPlayer player, int dimensionId)
    {
        Location location = RMCapabilities.getSpells(player).getGatestone();
        return location != null ? location.getPosition() : null;
    }
}
