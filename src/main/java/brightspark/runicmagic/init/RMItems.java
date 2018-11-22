package brightspark.runicmagic.init;

import brightspark.runicmagic.RunicMagic;
import brightspark.runicmagic.enums.StaffType;
import brightspark.runicmagic.item.ItemRune;
import brightspark.runicmagic.item.ItemStaff;
import brightspark.runicmagic.item.RMItemSubBase;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.LinkedList;
import java.util.List;

@GameRegistry.ObjectHolder(RunicMagic.MOD_ID)
public class RMItems
{
	private static List<Item> ITEMS = new LinkedList<>();

	//Crafting Ingredients
	public static final Item rune = null;
	public static final Item orb = null;

	//Staffs
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
		add(new RMItemSubBase("orb", "unpowered", "air", "water", "earth", "fire").setMaxStackSize(1));

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
