package brightspark.runicmagic.util;

import brightspark.runicmagic.RMConfig;
import brightspark.runicmagic.RunicMagic;
import brightspark.runicmagic.enums.RuneType;
import brightspark.runicmagic.init.RMItems;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import java.util.Map;

public class CommonUtils
{
	public static boolean hasRunes(NonNullList<ItemStack> inventory, Map<RuneType, Short> runeCost)
	{
		for(ItemStack stack : inventory)
		{
			if(stack.getItem() == RMItems.rune)
			{
				RuneType runeType = RuneType.getFromMeta(stack.getMetadata());
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
				RuneType runeType = RuneType.getFromMeta(stack.getMetadata());
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

	/**
	 * Calculates the accuracy as a percentage to hit the target with a spell
	 * Mostly based on the hit chance logic in RuneScape
	 * @param magicLevel The caster's magic level
	 * @param weaponLevel The caster's weapon level
	 * @param targetEntityArmour The target's total armour value
	 * @return A number between 0 and 100 of the chance to hit
	 */
	// https://runescape.fandom.com/wiki/Hit_chance
	public static int calculateAccuracy(int magicLevel, int weaponLevel, int targetEntityArmour)
	{
		if(!RMConfig.useAccuracyCalculation)
			return Math.round((float) RMConfig.hitChanceMultiplier);

		int levelAccuracy = (int) Math.round(Math.pow(0.0008D * (double) magicLevel, 3D) + (4 * magicLevel) + 40);
		int baseAccuracy = (int) (levelAccuracy + (2.5F * weaponLevel));
		double accuracy = 55D * ((double) baseAccuracy / ((double) targetEntityArmour * RMConfig.accuracyArmourMultiplier));
		int result = Math.max((int) Math.round(accuracy * RMConfig.hitChanceMultiplier), 100);
		RunicMagic.LOG.info("Calculated accuracy: {} from magicLevel: {}, weaponLevel: {}, targetArmour: {}", result, magicLevel, weaponLevel, targetEntityArmour);
		return result;
	}
}
