package brightspark.runicmagic;

import net.minecraftforge.common.config.Config;

@Config(modid = RunicMagic.MOD_ID)
public class RMConfig
{
	@Config.Comment({"Whether to use the accuracy calculation based on RuneScape's hit chance logic",
		"If false, then the hitChanceMultiplier will be used directly"})
	public static boolean useAccuracyCalculation = true;

	@Config.Comment({"This is the armour multiplier used in accuracy calculations",
		"The default value of 14 is based on vanilla Iron Armour compared to the Iron Armour from RuneScape"})
	@Config.RangeDouble(min = 0, max = 100)
	//https://runescape.fandom.com/wiki/Armour/Melee_armour#Melee_armour_sets
	public static double accuracyArmourMultiplier = 14F;

	@Config.Comment("This is applied to the hit chance once calculated, unless useAccuracyCalculation is false, then it's used directly")
	@Config.RangeDouble(min = 0, max = 100)
	public static double hitChanceMultiplier = 1F;
}
