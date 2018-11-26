package brightspark.runicmagic.item;

import brightspark.runicmagic.enums.RuneType;

// https://runescape.fandom.com/wiki/Runes
public class ItemRune extends RMItemSubBase
{
	public ItemRune()
	{
		super("rune", RuneType.runeNames());
	}
}
