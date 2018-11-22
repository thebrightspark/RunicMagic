package brightspark.runicmagic.block;

import brightspark.runicmagic.enums.RuneType;
import net.minecraft.block.material.Material;

public class BlockAltar extends RMBlockBase
{
	//TODO: Make this with blockstates
	public BlockAltar(RuneType runeType)
	{
		super("altar_" + runeType, Material.ROCK);
	}
}
