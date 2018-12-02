package brightspark.runicmagic.init;

import brightspark.runicmagic.RunicMagic;
import brightspark.runicmagic.block.BlockGatestone;
import brightspark.runicmagic.block.BlockObelisk;
import brightspark.runicmagic.block.BlockRuneOre;
import brightspark.runicmagic.block.tileentity.TileGatestone;
import brightspark.runicmagic.enums.RuneType;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.LinkedList;
import java.util.List;

@GameRegistry.ObjectHolder(RunicMagic.MOD_ID)
public class RMBlocks
{
	private static List<Item> ITEMS = new LinkedList<>();
	private static List<Block> BLOCKS = new LinkedList<>();

	public static final Block rune_ore = null;

	public static final Block obelisk_water = null;
	public static final Block obelisk_earth = null;
	public static final Block obelisk_fire = null;
	public static final Block obelisk_air = null;

	public static final Block gatestone = null;

	private static void add(Block block, Item itemBlock)
	{
		BLOCKS.add(block);
		ITEMS.add(itemBlock.setRegistryName(block.getRegistryName()));
	}

	private static void add(Block block)
	{
		add(block, new ItemBlock(block));
	}

	private static void regTE(Class<? extends TileEntity> tileEntityClass, Block block)
	{
		GameRegistry.registerTileEntity(tileEntityClass, block.getRegistryName());
	}

	private static void init()
	{
		add(new BlockRuneOre());

		add(new BlockObelisk(RuneType.WATER));
		add(new BlockObelisk(RuneType.EARTH));
		add(new BlockObelisk(RuneType.FIRE));
		add(new BlockObelisk(RuneType.AIR));

		Block block;
		add(block = new BlockGatestone(), new ItemBlock(block).setMaxStackSize(1));
		regTE(TileGatestone.class, block);
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
