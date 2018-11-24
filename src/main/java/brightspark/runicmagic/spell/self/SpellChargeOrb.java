package brightspark.runicmagic.spell.self;

import brightspark.runicmagic.block.BlockObelisk;
import brightspark.runicmagic.enums.RuneType;
import brightspark.runicmagic.init.RMItems;
import brightspark.runicmagic.util.SpellCastData;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

public class SpellChargeOrb extends SpellSelfBase
{
	private static final ItemStack UNPOWERED_ORB = new ItemStack(RMItems.orb, 1, 0);
	private final RuneType runeType;

	public SpellChargeOrb(RuneType runeType)
	{
		super("charge_orb_" + runeType);
		this.runeType = runeType;
		addRuneCost(RuneType.COSMIC, 3);
		addRuneCost(runeType, 30);
	}

	private boolean isBlockLookingAtValid(EntityPlayer player)
	{
		IBlockState state = getBlockLookingAt(player);
		return state != null && state.getBlock() instanceof BlockObelisk && ((BlockObelisk) state.getBlock()).getRuneType() == runeType;
	}

	@Override
	public boolean canCast(EntityPlayer player)
	{
		return findHeldItem(player, UNPOWERED_ORB) == null || !isBlockLookingAtValid(player);
	}

	@Override
	public boolean updateCasting(World world, EntityPlayer player, int progress)
	{
		return canCast(player);
	}

	@Override
	public boolean execute(EntityPlayer player, SpellCastData data)
	{
		Pair<ItemStack, EnumHand> heldOrb = findHeldItem(player, UNPOWERED_ORB);
		if(heldOrb == null)
			return false;
		if(!isBlockLookingAtValid(player))
			return false;
		//TODO: Should probably make the orb it's own item class to convert meta reliably...
		player.setHeldItem(heldOrb.getValue(), new ItemStack(RMItems.orb, runeType.ordinal()));
		return true;
	}
}
