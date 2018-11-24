package brightspark.runicmagic.spell.self;

import brightspark.runicmagic.spell.Spell;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.tuple.Pair;

public abstract class SpellSelfBase extends Spell
{
	public SpellSelfBase(String name)
	{
		super(name);
		selectable = false;
	}

	protected Pair<ItemStack, EnumHand> findHeldItem(EntityPlayer player, ItemStack stack)
	{
		for(EnumHand hand : EnumHand.values())
		{
			ItemStack heldStack = player.getHeldItem(hand);
			if(heldStack.isItemEqual(stack))
				return Pair.of(heldStack, hand);
		}
		return null;
	}

	protected IBlockState getBlockLookingAt(EntityPlayer player)
	{
		Vec3d eyePos = player.getPositionEyes(1F);
		Vec3d look = player.getLookVec();
		Vec3d end = eyePos.add(look.scale(player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue()));
		RayTraceResult result = player.world.rayTraceBlocks(eyePos, end, false, false, true);
		return result.typeOfHit == RayTraceResult.Type.BLOCK ? player.world.getBlockState(result.getBlockPos()) : null;
	}

	protected boolean playerHasStack(EntityPlayer player, ItemStack stack, boolean strict)
	{
		for(int i = 0; i < player.inventory.getSizeInventory(); i++)
			if(OreDictionary.itemMatches(stack, player.inventory.getStackInSlot(i), strict))
				return true;
		return false;
	}
}
