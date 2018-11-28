package brightspark.runicmagic.enums;

import net.minecraft.client.resources.I18n;

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

	private Color colour = null;
	private RuneType[] subTypes = null;
	private final String unlocName;

	RuneType()
	{
		unlocName = "runetype." + name().toLowerCase(Locale.ROOT) + ".name";
	}

	RuneType(Color colour)
	{
		this();
		this.colour = colour;
	}

	RuneType(RuneType... subTypes)
	{
		this();
		this.subTypes = subTypes;
	}

	public static String[] getNames(RuneType... runeTypes)
	{
		String[] names = new String[runeTypes.length];
		for(int i = 0; i < runeTypes.length; i++)
			names[i] = runeTypes[i].toString();
		return names;
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
