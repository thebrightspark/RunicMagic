package brightspark.runemagic.item;

import brightspark.runemagic.enums.RuneType;

// https://runescape.fandom.com/wiki/Runes
public class ItemRune extends RMItemSubBase
{
	public ItemRune()
	{
		super("rune", RuneType.allNames());
	}
}
