package brightspark.runicmagic.spell.teleport;

import brightspark.runicmagic.enums.SpellType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class SpellTeleportSpawn extends SpellTeleportBase
{
	public SpellTeleportSpawn()
	{
		super("spawn", SpellType.TELESELF, 0);
	}

	@Override
	protected BlockPos getDestinationPos(EntityPlayer player, int dimensionId)
	{
		return !player.world.isRemote ? FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(dimensionId).getSpawnPoint() : null;
	}
}
