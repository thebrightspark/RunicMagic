package brightspark.runemagic.init;

import brightspark.runemagic.RuneMagic;
import brightspark.runemagic.enums.StaffType;
import brightspark.runemagic.item.ItemRune;
import brightspark.runemagic.item.ItemStaff;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.LinkedList;
import java.util.List;

@GameRegistry.ObjectHolder(RuneMagic.MOD_ID)
public class RMItems
{
	private static List<Item> ITEMS = new LinkedList<>();

	public static final Item rune = null;
	public static final Item staff_basic = null;
	public static final Item staff_battle = null;
	public static final Item staff_mystic = null;
	public static final Item staff_limitless = null;

	private static void add(Item item)
	{
		ITEMS.add(item);
	}

	public static void init()
	{
		add(new ItemRune());

		for(StaffType type : StaffType.values())
			add(new ItemStaff(type));
	}

	public static Item[] getItems()
	{
		if(ITEMS.isEmpty())
			init();
		return ITEMS.toArray(new Item[0]);
	}
}
