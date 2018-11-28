package brightspark.runicmagic.item;

import brightspark.runicmagic.capability.CapSpell;
import brightspark.runicmagic.enums.RuneType;
import brightspark.runicmagic.enums.StaffType;
import brightspark.runicmagic.init.RMCapabilities;
import brightspark.runicmagic.spell.Spell;
import brightspark.runicmagic.util.CommonUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
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
		if(!(stack.getItem() instanceof ItemStaff))
			return null;
		Map<RuneType, Short> cost = spell.getCost();
		RuneType runeType = ((ItemStaff) stack.getItem()).getRuneType(stack.getMetadata());
		if(runeType != null)
			cost.remove(runeType);
		return cost;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
	{
		ItemStack stack = playerIn.getHeldItem(handIn);
		CapSpell capSpell = playerIn.getCapability(RMCapabilities.SPELL, null);
		if(capSpell != null && capSpell.canExecuteSpell(playerIn, stack, null))
		{
			if(playerIn instanceof EntityPlayerMP)
			{
				//Only execute on server side
				if(capSpell.executeSpell((EntityPlayerMP) playerIn, type.getAttackBonus(), null))
				{
					//Remove runes from inventory
					CommonUtils.removeRunes(playerIn.inventory.mainInventory, calculateRuneCost(stack, capSpell.getSelectedSpell()));
				}
			}

			if(!playerIn.isCreative())
			{
				//Set cooldown if not in creative
				Integer cooldown = capSpell.getSpellCooldown();
				if(cooldown != null)
					playerIn.getCooldownTracker().setCooldown(this, cooldown);
			}
			return new ActionResult<>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
		}
		return new ActionResult<>(EnumActionResult.FAIL, playerIn.getHeldItem(handIn));
	}
}
