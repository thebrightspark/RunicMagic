package brightspark.runicmagic.util;

import brightspark.runicmagic.enums.RuneType;
import brightspark.runicmagic.init.RMItems;
import brightspark.runicmagic.item.ItemRuneTypeBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

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
}
