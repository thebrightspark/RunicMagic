package brightspark.runicmagic.item;

import brightspark.runicmagic.capability.CapSpell;
import brightspark.runicmagic.enums.CanCastResult;
import brightspark.runicmagic.enums.RuneType;
import brightspark.runicmagic.enums.StaffType;
import brightspark.runicmagic.init.RMCapabilities;
import brightspark.runicmagic.spell.Spell;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import java.util.Map;

// https://runescape.fandom.com/wiki/Staff_(weapon_type)
public class ItemStaff extends ItemRuneTypeBase
{
	private final StaffType type;

	public ItemStaff(StaffType type)
	{
		super("staff_" + type, type.getRuneTypes());
		this.type = type;
		setFull3D();
	}

	public static Map<RuneType, Short> calculateRuneCost(ItemStack stack, Spell spell)
	{
		Map<RuneType, Short> cost = spell.getCost();
		if(stack == null || !(stack.getItem() instanceof ItemStaff))
			return cost;
		RuneType runeType = ((ItemStaff) stack.getItem()).getRuneType(stack.getMetadata());
		if(runeType != null)
			cost.remove(runeType);
		return cost;
	}

	public static float getAttackBonus(ItemStack stack)
	{
		return stack != null && stack.getItem() instanceof ItemStaff ? ((ItemStaff) stack.getItem()).type.getAttackBonus() : 0F;
	}

	public static RuneType getRuneType(ItemStack stack)
	{
		return stack != null && stack.getItem() instanceof ItemStaff ? ((ItemStaff) stack.getItem()).getRuneType(stack.getMetadata()) : null;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
	{
		ItemStack stack = playerIn.getHeldItem(handIn);
		CapSpell capSpell = playerIn.getCapability(RMCapabilities.SPELL, null);
		if(playerIn instanceof EntityPlayerMP)
		{
			CanCastResult result = capSpell.canExecuteSpell(playerIn, stack, null);

			if(result == CanCastResult.SUCCESS)
				result = capSpell.executeSpell((EntityPlayerMP) playerIn, stack, null);

			if(result != CanCastResult.SUCCESS)
			{
				playerIn.sendMessage(new TextComponentTranslation(result.getFailLang()));
				return new ActionResult<>(EnumActionResult.FAIL, playerIn.getHeldItem(handIn));
			}
		}
		return new ActionResult<>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
	}
}
