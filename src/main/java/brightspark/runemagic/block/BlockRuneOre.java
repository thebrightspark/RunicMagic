package brightspark.runemagic.block;

import brightspark.runemagic.init.RMItems;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;

import java.util.Random;

public class BlockRuneOre extends RMBlockBase
{
	public BlockRuneOre()
	{
		super("rune_ore", Material.ROCK);
	}

	@Override
	public int quantityDropped(Random random)
	{
		return random.nextInt(5) + 5; //5 - 10 items dropped
	}

	@Override
	public int quantityDroppedWithBonus(int fortune, Random random)
	{
		return quantityDropped(random) + random.nextInt(fortune * 2);
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune)
	{
		return RMItems.rune;
	}
}
