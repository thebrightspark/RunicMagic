package brightspark.runicmagic.enums;

import java.awt.*;
import java.util.Locale;

public enum RuneType
{
	NONE,

	//Elemental
	AIR(new Color(0xFAFAF0)),
	WATER(new Color(0x6666FF)),
	EARTH(new Color(0x964F0C)),
	FIRE(new Color(0xDF2628)),

	//Combination
	MIST(AIR, WATER),
	DUST(AIR, EARTH),
	MUD(WATER, EARTH),
	SMOKE(AIR, FIRE),
	STEAM(WATER, FIRE),
	LAVA(EARTH, FIRE),

	//Catalytic
	MIND,
	BODY,
	COSMIC,
	CHAOS,
	NATURE,
	LAW,
	DEATH,

	ASTRAL,
	BLOOD,
	SOUL;

	private static final String[] all, staffNames, staffNames2;

	private Color colour = null;
	private RuneType[] subTypes = null;

	RuneType() {}

	RuneType(Color colour)
	{
		this.colour = colour;
	}

	RuneType(RuneType... subTypes)
	{
		this.subTypes = subTypes;
	}

	static
	{
		all = getNames(values());
		staffNames = getNames(NONE, AIR, WATER, EARTH, FIRE);
		staffNames2 = getNames(NONE, AIR, WATER, EARTH, FIRE, LAVA, MUD, STEAM);
	}

	private static String[] getNames(RuneType... runeTypes)
	{
		String[] names = new String[runeTypes.length];
		for(int i = 0; i < runeTypes.length; i++)
			names[i] = runeTypes[i].toString();
		return names;
	}

	public static String[] allNames()
	{
		return all;
	}

	public static String[] staffNames()
	{
		return staffNames;
	}

	public static String[] staffNames2()
	{
		return staffNames2;
	}

	public static RuneType getFromMeta(int meta)
	{
		if(meta < 0 || meta >= values().length)
			return null;
		return values()[meta];
	}

	public Color getColour()
	{
		return colour;
	}

	public RuneType[] getSubTypes()
	{
		return subTypes;
	}

	@Override
	public String toString()
	{
		return super.toString().toLowerCase(Locale.ROOT);
	}
}
