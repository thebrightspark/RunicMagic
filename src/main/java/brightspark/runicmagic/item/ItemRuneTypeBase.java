package brightspark.runicmagic.item;

import brightspark.runicmagic.enums.RuneType;

import static brightspark.runicmagic.enums.RuneType.*;

public class ItemRuneTypeBase extends RMItemSubBase
{
	public static final RuneType[] MAIN_TYPES = new RuneType[] {AIR, WATER, EARTH, FIRE, MIND, BODY, COSMIC, CHAOS, NATURE, LAW, DEATH, BLOOD, SOUL};
	private final RuneType[] runeTypes;

	public ItemRuneTypeBase(String name)
	{
		this(name, MAIN_TYPES);
	}

	public ItemRuneTypeBase(String name, RuneType... runeTypes)
	{
		super(name, RuneType.getNames(runeTypes));
		this.runeTypes = runeTypes;
	}

	public RuneType getRuneType(int meta)
	{
		return meta >= 0 && meta < runeTypes.length ? runeTypes[meta] : null;
	}
}
