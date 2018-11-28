package brightspark.runicmagic.init;

import brightspark.runicmagic.RunicMagic;
import brightspark.runicmagic.enums.RuneType;
import brightspark.runicmagic.enums.StaffType;
import brightspark.runicmagic.item.ItemRuneTypeBase;
import brightspark.runicmagic.item.ItemStaff;
import brightspark.runicmagic.item.ItemTalisman;
import brightspark.runicmagic.item.RMItemBase;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.LinkedList;
import java.util.List;

@GameRegistry.ObjectHolder(RunicMagic.MOD_ID)
public class RMItems
{
	private static List<Item> ITEMS = new LinkedList<>();

	//Crafting Ingredients
	public static final Item rune_essence = null;
	public static final Item pure_essence = null;
	public static final Item rune = null;
	public static final Item talisman = null;
	public static final Item orb = null;

	//Staffs
	public static final Item staff_basic = null;
	public static final Item staff_battle = null;
	public static final Item staff_mystic = null;
	//public static final Item staff_limitless = null;

	private static void add(Item item)
	{
		ITEMS.add(item);
	}

	public static void init()
	{
		add(new RMItemBase("rune_essence"));
		add(new RMItemBase("pure_essence"));
		add(new ItemRuneTypeBase("rune"));
        add(new ItemTalisman());
		add(new ItemRuneTypeBase("orb", RuneType.NONE, RuneType.AIR, RuneType.WATER, RuneType.EARTH, RuneType.FIRE).setMaxStackSize(1));

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
