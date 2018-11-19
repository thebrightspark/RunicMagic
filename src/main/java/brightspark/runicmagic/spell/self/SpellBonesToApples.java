package brightspark.runicmagic.spell.self;

import brightspark.runicmagic.enums.RuneType;
import brightspark.runicmagic.spell.Spell;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class SpellBonesToApples extends Spell
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
	public boolean execute(EntityPlayer player)
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
