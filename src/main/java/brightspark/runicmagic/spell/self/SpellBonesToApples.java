package brightspark.runicmagic.spell.self;

import brightspark.runicmagic.enums.RuneType;
import brightspark.runicmagic.util.SpellCastData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

// Turns all bones in player inventory into apples
public class SpellBonesToApples extends SpellSelfBase
{
	public SpellBonesToApples()
	{
		super("bones_to_apples");
		cooldown = 1200; //60 secs
		addRuneCost(RuneType.NATURE, 1);
		addRuneCost(RuneType.EARTH, 2);
		addRuneCost(RuneType.WATER, 2);
	}

	@Override
	public boolean execute(EntityPlayer player, SpellCastData data)
	{
		boolean success = false;

		NonNullList<ItemStack> playerInv = player.inventory.mainInventory;
		for(int i = 0; i < playerInv.size(); i++)
		{
			ItemStack stack = playerInv.get(i);
			if(stack.getItem() == Items.BONE)
			{
				playerInv.set(i, new ItemStack(Items.APPLE, stack.getCount()));
				success = true;
			}
		}

		return success;
	}
}
