package brightspark.runicmagic.enums;

import java.util.Locale;

public enum StaffType
{
	BASIC(0),
	BATTLE(2),
	MYSTIC(3),
	LIMITLESS(5);

	private float attackBonus;

	StaffType(float attackBonus)
	{
		this.attackBonus = attackBonus;
	}

	public String[] getTypeNames()
	{
		switch(this)
		{
			case BASIC:		return RuneType.staffNames();
			case BATTLE:
			case MYSTIC:
			case LIMITLESS:	return RuneType.staffNames2();
			default:		return null;
		}
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
