package brightspark.runicmagic.spell.teleport;

import brightspark.runicmagic.enums.SpellType;
import brightspark.runicmagic.particle.ParticleRising;
import brightspark.runicmagic.spell.Spell;
import brightspark.runicmagic.util.ClientUtils;
import brightspark.runicmagic.util.SpellCastData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;

import java.awt.*;

public class SpellTeleportBase extends Spell
{
	public SpellTeleportBase(String name, SpellType spellType, int level)
	{
		super("teleport_" + name, spellType, level);
		selectable = false;
		cooldown = 600; //30s
		castTime = 200;
	}

	@Override
	public boolean canCast(EntityPlayer player)
	{
		return hasPlayerMoved(player);
	}

	@Override
	public boolean updateCasting(World world, EntityPlayer player, int progress)
	{
		if(world.isRemote)
		{
			Vec3d playerPos = player.getPositionVector();
			int particles = progress / 10;
			for(int i = 0; i < particles; i++)
				ClientUtils.spawnParticle(new ParticleRising(world, posOffset(world, playerPos, 1D, 0D, 1D), new Color(0x2c1863)));
		}
		return hasPlayerMoved(player);
	}

	@Override
	public boolean execute(EntityPlayer player, SpellCastData data)
	{
		int dim = getDestinationDimId(player);
		if(player.dimension != dim)
			player.changeDimension(dim);
		BlockPos p = getDestinationPos(player, dim);
		if(p == null)
			return false;
		Vec3d destPos = new Vec3d(p).add(0.5D, 0, 0.5D);

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

	protected BlockPos getDestinationPos(EntityPlayer player, int dimensionId)
	{
		return player.getPosition();
	}
}
