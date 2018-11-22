package brightspark.runicmagic.spell.teleport;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

public class SpellTeleportHome extends SpellTeleportBase
{
	public SpellTeleportHome()
	{
		super("home");
	}

	@Override
	protected BlockPos getDestinationPos(EntityPlayer player, int dimensionId)
	{
		return player.getBedLocation(dimensionId);
	}
}
