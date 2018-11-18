package brightspark.runemagic.item;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class RMItemSubBase extends RMItemBase
{
	protected String[] subNames;

	public RMItemSubBase(String name, String... subNames)
	{
		super(name);
		setHasSubtypes(subNames != null && subNames.length > 0);
		this.subNames = hasSubtypes ? subNames : null;
	}

	@Override
	public void getSubItems(NonNullList<ItemStack> items)
	{
		if(hasSubtypes)
			for(int i = 0; i < subNames.length; i++)
				items.add(new ItemStack(this, 1, i));
		else
			items.add(new ItemStack(this));
	}

	@Override
	public String getTranslationKey(ItemStack stack)
	{
		if(hasSubtypes)
		{
			int meta = stack.getMetadata();
			if(meta >= 0 && meta < subNames.length)
				return super.getTranslationKey(stack) + "." + subNames[meta];
		}
		return super.getTranslationKey(stack);
	}

	public String[] getSubNames()
	{
		return subNames;
	}
}
