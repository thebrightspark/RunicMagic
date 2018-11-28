package brightspark.runicmagic.item;

import brightspark.runicmagic.enums.RuneType;

import static brightspark.runicmagic.enums.RuneType.*;

// https://runescape.fandom.com/wiki/Runes
public class ItemRune extends RMItemSubBase
{
	public static final RuneType[] runeTypes = new RuneType[] {AIR, WATER, EARTH, FIRE, MIND, BODY, COSMIC, CHAOS, NATURE, LAW, DEATH, BLOOD, SOUL};

	public ItemRune()
	{
		super("rune", RuneType.getNames(runeTypes));
	}

	public static RuneType getRuneType(int meta)
	{
		return meta >= 0 && meta < runeTypes.length ? runeTypes[meta] : null;
	}
}
