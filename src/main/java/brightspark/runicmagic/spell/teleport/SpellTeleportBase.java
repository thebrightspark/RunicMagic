package brightspark.runicmagic.spell.teleport;

import brightspark.runicmagic.spell.Spell;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;

public class SpellTeleportBase extends Spell
{
	public SpellTeleportBase(String name)
	{
		super(name);
		selectable = false;
		cooldown = 600; //30s
	}

	@Override
	public boolean execute(EntityPlayer player)
	{
		if(player.world.isRemote)
			return false;
		int dim = getDestinationDimId(player);
		if(player.dimension != dim)
			player.changeDimension(dim);
		Vec3d destPos = new Vec3d(getDestinationPos(player)).add(0.5D, 0, 0.5D);

		//Go up until position is clear to teleport player there
		World world = player.world;
		AxisAlignedBB destBox = player.getEntityBoundingBox().offset(destPos.x - player.posX, destPos.y - player.posY, destPos.z - player.posZ);
		while(!world.getCollisionBoxes(player, destBox).isEmpty() && destPos.y < world.provider.getActualHeight())
		{
			destPos = destPos.add(0, 1, 0);
			destBox = destBox.offset(0, 1, 0);
		}

		return player.attemptTeleport(destPos.x, destPos.y, destPos.z);
	}

	protected int getDestinationDimId(EntityPlayer player)
	{
		return DimensionType.OVERWORLD.getId();
	}

	protected BlockPos getDestinationPos(EntityPlayer player)
	{
		return player.getPosition();
	}
}