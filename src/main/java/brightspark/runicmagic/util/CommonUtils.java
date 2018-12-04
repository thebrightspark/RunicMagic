package brightspark.runicmagic.util;

import brightspark.runicmagic.enums.RuneType;
import brightspark.runicmagic.init.RMItems;
import brightspark.runicmagic.item.ItemRuneTypeBase;
import brightspark.runicmagic.item.ItemStaff;
import com.google.common.base.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class CommonUtils
{
	public static boolean hasRunes(NonNullList<ItemStack> inventory, Map<RuneType, Short> runeCost)
	{
		for(ItemStack stack : inventory)
		{
			if(stack.getItem() == RMItems.rune)
			{
				RuneType runeType = ((ItemRuneTypeBase) stack.getItem()).getRuneType(stack.getMetadata());
				if(runeType != null)
				{
					runeCost.computeIfPresent(runeType, (type, cost) -> {
						short newCost = (short) (cost - stack.getCount());
						return newCost <= 0 ? null : newCost;
					});
					if(runeCost.isEmpty())
						return true;
				}
			}
		}
		return false;
	}

	public static void removeRunes(NonNullList<ItemStack> inventory, Map<RuneType, Short> runeCost)
	{
		for(int i = 0; i < inventory.size(); i++)
		{
			ItemStack stack = inventory.get(i);
			if(stack.getItem() == RMItems.rune)
			{
				RuneType runeType = ((ItemRuneTypeBase) stack.getItem()).getRuneType(stack.getMetadata());
				if(runeType != null)
				{
					runeCost.computeIfPresent(runeType, (type, cost) -> {
						int actualCost = Math.min(cost, stack.getCount());
						int newCost = cost - actualCost;
						stack.shrink(actualCost);
						return newCost <= 0 ? null : (short) newCost;
					});
					if(stack.getCount() <= 0)
						inventory.set(i, ItemStack.EMPTY);
					if(runeCost.isEmpty())
						return;
				}
			}
		}
	}

	public static boolean isIntegratedServer()
	{
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		return server != null && !server.isDedicatedServer();
	}

	public static Pair<ItemStack, EnumHand> findHeldItem(EntityPlayer player, ItemStack stack)
	{
		return findHeldItem(player, heldStack -> OreDictionary.itemMatches(stack, heldStack, false));
	}

	public static Pair<ItemStack, EnumHand> findHeldStaff(EntityPlayer player)
	{
		return findHeldItem(player, heldStack -> heldStack.getItem() instanceof ItemStaff);
	}

	public static Pair<ItemStack, EnumHand> findHeldItem(EntityPlayer player, Predicate<ItemStack> predicate)
	{
		for(EnumHand hand : EnumHand.values())
		{
			ItemStack heldStack = player.getHeldItem(hand);
			if(predicate.test(heldStack))
				return Pair.of(heldStack, hand);
		}
		return null;
	}

	public static String ticksToSecsString(long ticks)
	{
		long millis = ticks * 50;
		long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
		millis -= TimeUnit.SECONDS.toMillis(seconds);
		if(millis == 0)
			return seconds + "s";
		else
		    //TODO: Improve this...
			return seconds + "." + millis + "s";
	}

	public static Set<Entity> getEntitiesInArea(AxisAlignedBB area, List<Entity> entities)
	{
		return entities.stream().filter(entity -> entity.getEntityBoundingBox().intersects(area)).collect(Collectors.toSet());
	}

	public static Entity rayTraceEntities(World world, Vec3d start, Vec3d end, @Nullable Predicate<? super Entity> predicate)
	{
		//Get all entities within start and end pos to make later entity collision detection quicker
		List<Entity> allEntities = world.getEntitiesInAABBexcluding(null, new AxisAlignedBB(start, end), predicate);
		if(allEntities.isEmpty())
			return null;
		//Create a 0.1 big AABB for the ray tracing
		double movingBoxRadius = 0.05D;
		AxisAlignedBB movingBox = new AxisAlignedBB(start.subtract(movingBoxRadius, movingBoxRadius, movingBoxRadius), start.add(movingBoxRadius, movingBoxRadius, movingBoxRadius));
		Entity closestEntity = null;
		double closestDistance = 0D;

		double distanceToTrace = start.distanceTo(end);
		double stepMove = movingBoxRadius * 1.9D;
		int steps = (int) Math.floor(distanceToTrace / stepMove);
		Vec3d stepMoveVec = end.subtract(start);
		if(steps > 0)
			stepMoveVec.scale(1D / (double) steps);

		for(int i = 0; i < steps; i++)
		{
			if(i > 0)
				movingBox.offset(stepMoveVec);
			Set<Entity> entities = getEntitiesInArea(movingBox, allEntities);
			for(Entity entity : entities)
			{
				RayTraceResult ray = entity.getEntityBoundingBox().calculateIntercept(start, end);
				if(ray != null)
				{
					double distance = start.squareDistanceTo(ray.hitVec);
					if(distance < closestDistance || closestDistance == 0D)
					{
						closestEntity = entity;
						closestDistance = distance;
					}
				}
			}
			//No point ray tracing further, as any more entities won't be as close as this one
			if(closestEntity != null)
				break;
		}
		return closestEntity;
	}
}
