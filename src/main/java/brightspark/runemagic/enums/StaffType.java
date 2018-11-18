package brightspark.runemagic.enums;

import java.util.Locale;

public enum StaffType
{
	BASIC,
	BATTLE,
	MYSTIC,
	LIMITLESS;

	@Override
	public String toString()
	{
		return super.toString().toLowerCase(Locale.ROOT);
	}
}
