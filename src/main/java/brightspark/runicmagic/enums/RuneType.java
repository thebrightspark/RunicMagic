package brightspark.runicmagic.enums;

import java.util.Locale;

public enum RuneType
{
	NONE,

	//Elemental
	AIR,
	WATER,
	EARTH,
	FIRE,

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
	ASTRAL,
	NATURE,
	LAW,
	DEATH,
	ARMADYL,
	BLOOD,
	SOUL;

	private static String[] all;
	private static RuneType[] staffTypes;
	public static String[] staffNames;

	private RuneType[] subTypes = null;

	RuneType() {}

	RuneType(RuneType... subTypes)
	{
		this.subTypes = subTypes;
	}

	static
	{
		RuneType[] values = values();
		all = new String[values.length];
		for(int i = 0; i < values.length; i++)
			all[i] = values[i].name().toLowerCase(Locale.ROOT);

		staffTypes = new RuneType[] {NONE, AIR, WATER, EARTH, FIRE};

		staffNames = new String[staffTypes.length];
		for(int i = 0; i < staffTypes.length; i++)
			staffNames[i] = staffTypes[i].toString();
	}

	public static String[] allNames()
	{
		return all;
	}

	public static String[] staffNames()
	{
		return staffNames;
	}

	public static RuneType getFromMeta(int meta)
	{
		if(meta < 0 || meta >= values().length)
			return null;
		return values()[meta];
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
