package brightspark.runemagic.item;

import brightspark.runemagic.capability.spell.CapSpell;
import brightspark.runemagic.enums.RuneType;
import brightspark.runemagic.enums.StaffType;
import brightspark.runemagic.init.RMCapabilities;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

// https://runescape.fandom.com/wiki/Staff_(weapon_type)
public class ItemStaff extends RMItemSubBase
{
	private final StaffType type;

	public ItemStaff(StaffType type)
	{
		super("staff_" + type, RuneType.staffNames());
		this.type = type;
	}

	public static RuneType getRuneType(ItemStack stack)
	{
		return stack.getItem() instanceof ItemStaff && stack.getMetadata() >= 0 && stack.getMetadata() < RuneType.staffNames().length ?
			RuneType.getFromMeta(stack.getMetadata()) : null;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
	{
		CapSpell capSpell = playerIn.getCapability(RMCapabilities.SPELL, null);
		if(capSpell != null && capSpell.canExecuteSpell(playerIn, null))
		{
			//TODO: Use runes from player inventory
			if(playerIn instanceof EntityPlayerMP)
				//Only execute on server side
				capSpell.executeSpell((EntityPlayerMP) playerIn);

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
