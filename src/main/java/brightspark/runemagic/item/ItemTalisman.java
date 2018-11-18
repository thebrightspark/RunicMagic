package brightspark.runemagic.item;

import brightspark.runemagic.enums.RuneType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemTalisman extends RMItemSubBase
{
	public ItemTalisman()
	{
		super("talisman", RuneType.allNames());
		setMaxStackSize(1);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
	{
		//TODO: Tells the player the direction to the closest rune altar for this talisman type
		return super.onItemRightClick(worldIn, playerIn, handIn);
	}
}
