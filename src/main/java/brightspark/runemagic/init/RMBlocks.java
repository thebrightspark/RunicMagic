package brightspark.runemagic.init;

import brightspark.runemagic.RuneMagic;
import brightspark.runemagic.block.BlockRuneOre;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.LinkedList;
import java.util.List;

@GameRegistry.ObjectHolder(RuneMagic.MOD_ID)
public class RMBlocks
{
	private static List<Item> ITEMS = new LinkedList<>();
	private static List<Block> BLOCKS = new LinkedList<>();

	public static final Block rune_ore = null;

	private static void add(Block block)
	{
		BLOCKS.add(block);
		ITEMS.add(new ItemBlock(block).setRegistryName(block.getRegistryName()));
	}

	private static void init()
	{
		add(new BlockRuneOre());
	}

	public static Block[] getBlocks()
	{
		if(BLOCKS.isEmpty())
			init();
		return BLOCKS.toArray(new Block[0]);
	}

	public static Item[] getItemBlocks()
	{
		if(ITEMS.isEmpty())
			init();
		return ITEMS.toArray(new Item[0]);
	}
}
