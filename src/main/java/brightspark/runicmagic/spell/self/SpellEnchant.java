package brightspark.runicmagic.spell.self;

import brightspark.runicmagic.enums.RuneType;
import brightspark.runicmagic.util.SpellCastData;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

import java.util.List;

// Randomly enchants the held item like an enchantment table
public class SpellEnchant extends SpellSelfBase
{
	private byte level;

	public SpellEnchant(int level)
	{
		super("enchant_" + level);
		this.level = (byte) (level * 5);
		addRuneCost(RuneType.COSMIC, 1);
	}

	@Override
	public boolean execute(EntityPlayer player, SpellCastData data)
	{
		ItemStack stack = player.getHeldItemMainhand();
		if(stack.isItemEnchanted() || !stack.isItemEnchantable())
			return false;
		//Enchant (logic taken from enchantment table)
		List<EnchantmentData> enchantments = EnchantmentHelper.buildEnchantmentList(player.world.rand, stack, level, false);
		if(stack.getItem() == Items.BOOK && enchantments.size() > 1)
			enchantments.remove(player.world.rand.nextInt(enchantments.size()));
		if(enchantments.isEmpty())
			return false;
		for(EnchantmentData enchantment : enchantments)
		{
			if(stack.getItem() == Items.BOOK)
			{
				stack = new ItemStack(Items.ENCHANTED_BOOK);
				ItemEnchantedBook.addEnchantment(stack, enchantment);
			}
			else
				stack.addEnchantment(enchantment.enchantment, enchantment.enchantmentLevel);
		}
		player.setHeldItem(EnumHand.MAIN_HAND, stack);
		return true;
	}
}
