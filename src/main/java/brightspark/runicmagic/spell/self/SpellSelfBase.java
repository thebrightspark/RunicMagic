package brightspark.runicmagic.spell.self;

import brightspark.runicmagic.enums.SpellType;
import brightspark.runicmagic.spell.Spell;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.oredict.OreDictionary;

import java.util.function.Predicate;

public abstract class SpellSelfBase extends Spell
{
	public SpellSelfBase(String name, SpellType spellType, int level)
	{
		super(name, spellType, level);
		selectable = false;
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
		return playerHasStack(player, invStack -> OreDictionary.itemMatches(stack, invStack, strict));
	}

	protected boolean playerHasStack(EntityPlayer player, Predicate<ItemStack> predicate)
	{
		for(int i = 0; i < player.inventory.getSizeInventory(); i++)
			if(predicate.test(player.inventory.getStackInSlot(i)))
				return true;
		return false;
	}

	protected boolean playerHasSpace(EntityPlayer player)
	{
		return playerHasStack(player, ItemStack::isEmpty);
	}
}
