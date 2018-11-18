package brightspark.runemagic.item;

import brightspark.runemagic.RuneMagic;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class RMItemBase extends Item
{
	public RMItemBase(String name)
	{
		setRegistryName(name);
		setTranslationKey(name);
		setCreativeTab(RuneMagic.TAB);
	}

	@Override
	public final void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
	{
		if(isInCreativeTab(tab))
			getSubItems(items);
	}

	protected void getSubItems(NonNullList<ItemStack> items) {}
}
