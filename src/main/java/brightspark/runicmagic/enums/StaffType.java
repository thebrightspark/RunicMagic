package brightspark.runicmagic.enums;

import java.util.Locale;

import static brightspark.runicmagic.enums.RuneType.*;

public enum StaffType
{
	BASIC(0, NONE, AIR, WATER, EARTH, FIRE),
	BATTLE(2, NONE, AIR, WATER, EARTH, FIRE, LAVA, MUD, STEAM),
	MYSTIC(3, AIR, WATER, EARTH, FIRE, LAVA, MUD, STEAM);
	//LIMITLESS(5);

	private final float attackBonus;
	private final RuneType[] runeTypes;
	private final String[] runeTypeNames;

	StaffType(float attackBonus, RuneType... runeTypes)
	{
		this.attackBonus = attackBonus;
		this.runeTypes = runeTypes;
		runeTypeNames = new String[runeTypes.length];
		for(int i = 0; i < runeTypes.length; i++)
			runeTypeNames[i] = runeTypes[i].toString();
	}

	public RuneType[] getRuneTypes()
	{
		return runeTypes;
	}

	public String[] getTypeNames()
	{
		return runeTypeNames;
	}

	public float getAttackBonus()
	{
		return attackBonus;
	}

	@Override
	public String toString()
	{
		return super.toString().toLowerCase(Locale.ROOT);
	}
}
