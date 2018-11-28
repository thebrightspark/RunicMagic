package brightspark.runicmagic.util;

import brightspark.runicmagic.enums.RuneType;
import brightspark.runicmagic.init.RMItems;
import brightspark.runicmagic.item.ItemRune;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.Map;

public class CommonUtils
{
	public static boolean hasRunes(NonNullList<ItemStack> inventory, Map<RuneType, Short> runeCost)
	{
		for(ItemStack stack : inventory)
		{
			if(stack.getItem() == RMItems.rune)
			{
				RuneType runeType = ItemRune.getRuneType(stack.getMetadata());
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
				RuneType runeType = ItemRune.getRuneType(stack.getMetadata());
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
}
