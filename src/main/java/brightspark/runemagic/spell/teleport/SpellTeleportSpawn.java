package brightspark.runemagic.spell.teleport;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

public class SpellTeleportSpawn extends SpellTeleportBase
{
	public SpellTeleportSpawn()
	{
		super("teleport_spawn");
	}

	@Override
	protected BlockPos getDestinationPos(EntityPlayer player)
	{
		return player.world.getSpawnPoint();
	}
}
