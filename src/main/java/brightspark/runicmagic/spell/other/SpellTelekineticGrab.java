package brightspark.runicmagic.spell.other;

import brightspark.runicmagic.enums.RuneType;
import brightspark.runicmagic.enums.SpellType;
import brightspark.runicmagic.particle.ParticleMoving;
import brightspark.runicmagic.spell.Spell;
import brightspark.runicmagic.util.ClientUtils;
import brightspark.runicmagic.util.CommonUtils;
import brightspark.runicmagic.util.SpellCastData;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.awt.*;

public class SpellTelekineticGrab extends Spell
{
	private static final double particleSeparation = 0.2D;
	private static final int particleStepSeparation = 4;

	public SpellTelekineticGrab()
	{
		super("telekinetic_grab", SpellType.OTHER, 33);
		addRuneCost(RuneType.LAW, 1);
		addRuneCost(RuneType.AIR, 1);
		castTime = 40;
		cooldown = 40;
	}

	private EntityItem getEntityItemLookingAt(EntityPlayer player)
	{
		Vec3d start = player.getPositionEyes(1F);
		Vec3d end = start.add(player.getLookVec().scale(10D));
		return (EntityItem) CommonUtils.rayTraceEntities(player.world, start, end, entity -> entity instanceof EntityItem);
	}

	@Override
	public boolean canCast(EntityPlayer player)
	{
		return getEntityItemLookingAt(player) != null && playerHasSpace(player);
	}

	@Override
	public boolean updateCasting(World world, EntityPlayer player, int progress)
	{
		EntityItem entityItem = getEntityItemLookingAt(player);
		if(world.isRemote && entityItem != null)
		{
			if(progress >= 0)
			{
				//Particles along line from player to entity
				Vec3d start = player.getPositionVector().add(0, player.getEyeHeight() * 0.85F, 0);
				Vec3d end = CommonUtils.getBox(entityItem).getCenter();
				double distance = start.distanceTo(end);
				int steps = (int) Math.floor(distance / particleSeparation);
				Vec3d stepMoveVec = end.subtract(start).scale(1D / (double) steps);

				//int i = steps - (progress % steps);
				//TODO: Trying to get the particles "moving" towards the player
				int stepOffset = progress % particleStepSeparation;
				int numParticles = steps / particleStepSeparation;

				//for(int i = 1; i < steps; i++)
				for(int i = 1; i < numParticles; i++)
				{
					int stepI = (i + stepOffset) * particleStepSeparation;
					if(stepI > steps)
						break;
					Vec3d pos = start.add(stepMoveVec.scale(i * particleStepSeparation));
					ParticleMoving particle = new ParticleMoving(world, pos, Color.CYAN, 0)
						.setMotion(createRandVector(world.rand).scale(0.02D))
						.setFadeOut();
					particle.setMaxAge(10);
					ClientUtils.spawnParticle(particle);
				}
			}

			if(progress == castTime)
			{
				Vec3d end = CommonUtils.getBox(entityItem).getCenter();
				for(int i = 0; i < 200; i++)
				{
					Vec3d dir = createRandVector(world.rand).scale(0.05D);
					ParticleMoving particle = new ParticleMoving(world, end, Color.CYAN, 0)
						.setMotion(dir)
						.setFadeOut();
					particle.setMaxAge(20);
					ClientUtils.spawnParticle(particle);
				}
			}
		}
		return entityItem == null;
	}

	@Override
	public boolean execute(EntityPlayer player, SpellCastData data)
	{
		EntityItem entityItem = getEntityItemLookingAt(player);
		if(entityItem == null)
			return false;
		entityItem.onCollideWithPlayer(player);
		return true;
	}
}
