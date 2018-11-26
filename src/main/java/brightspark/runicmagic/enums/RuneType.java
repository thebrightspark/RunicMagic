package brightspark.runicmagic.enums;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;
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

	private static final String[] runeNames, talismanNames, staffNames, staffNames2;

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
		runeNames = getRuneNames();
		talismanNames = getNames(AIR, WATER, EARTH, FIRE, MIND, BODY, COSMIC, CHAOS, NATURE, LAW, DEATH, BLOOD, SOUL);
		staffNames = getNames(NONE, AIR, WATER, EARTH, FIRE);
		staffNames2 = getNames(NONE, AIR, WATER, EARTH, FIRE, LAVA, MUD, STEAM);
	}

	private static String[] getRuneNames()
	{
		List<String> names = new LinkedList<>();
		for(RuneType runeType : values())
			if(runeType != NONE)
				names.add(runeType.toString());
		return names.toArray(new String[0]);
	}

	private static String[] getNames(RuneType... runeTypes)
	{
		String[] names = new String[runeTypes.length];
		for(int i = 0; i < runeTypes.length; i++)
			names[i] = runeTypes[i].toString();
		return names;
	}

	public static String[] runeNames()
	{
		return runeNames;
	}

	public static String[] talismanNames()
	{
		return talismanNames;
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

	public boolean hasSubTypes()
	{
		return subTypes != null;
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
