package brightspark.runicmagic.enums;

import net.minecraft.client.resources.I18n;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public enum RuneType
{
	NONE,

	//Elemental
	AIR(0, new Color(0xFAFAF0)),
	WATER(1, new Color(0x6666FF)),
	EARTH(2, new Color(0x964F0C)),
	FIRE(3, new Color(0xDF2628)),

	//Combination
	MIST(4, AIR, WATER),
	DUST(5, AIR, EARTH),
	MUD(6, WATER, EARTH),
	SMOKE(7, AIR, FIRE),
	STEAM(8, WATER, FIRE),
	LAVA(9, EARTH, FIRE),

	//Catalytic
	MIND(10),
	BODY(11),
	COSMIC(12),
	CHAOS(13),
	NATURE(14),
	LAW(15),
	DEATH(16),

	ASTRAL(17),
	BLOOD(18),
	SOUL(19);

	private static final List<RuneType> typeByMeta;
	private static final String[] runeNames, talismanNames, staffNames, staffNames2;

	private int meta;
	private Color colour = null;
	private RuneType[] subTypes = null;
	private final String unlocName;

	RuneType()
	{
		unlocName = "runetype." + name().toLowerCase(Locale.ROOT) + ".name";
	}

	RuneType(int meta)
	{
		this();
		this.meta = meta;
	}

	RuneType(int meta, Color colour)
	{
		this(meta);
		this.colour = colour;
	}

	RuneType(int meta, RuneType... subTypes)
	{
		this(meta);
		this.subTypes = subTypes;
	}

	static
	{
		typeByMeta = new LinkedList<>();
		for(RuneType type : values())
			if(type.meta >= 0)
				typeByMeta.set(type.meta, type);
		runeNames = getRuneNames();
		talismanNames = getNames(AIR, WATER, EARTH, FIRE, MIND, BODY, COSMIC, CHAOS, NATURE, LAW, DEATH, BLOOD, SOUL);
		staffNames = getNames(NONE, AIR, WATER, EARTH, FIRE);
		staffNames2 = getNames(NONE, AIR, WATER, EARTH, FIRE, LAVA, MUD, STEAM);
	}

	private static String[] getRuneNames()
	{
		List<String> names = new LinkedList<>();
		for(RuneType runeType : typeByMeta)
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
		return typeByMeta.get(meta);
	}

	public int getMeta()
	{
		return meta;
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

	public String getTranslation()
	{
		return I18n.format(unlocName);
	}

	@Override
	public String toString()
	{
		return super.toString().toLowerCase(Locale.ROOT);
	}
}
