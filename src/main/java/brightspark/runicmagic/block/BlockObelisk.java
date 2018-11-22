package brightspark.runicmagic.block;

import brightspark.runicmagic.enums.RuneType;
import net.minecraft.block.material.Material;

public class BlockObelisk extends RMBlockBase
{
	private RuneType runeType;

	//TODO: Make this with blockstates
	public BlockObelisk(RuneType runeType)
	{
		super("obelisk_" + runeType, Material.ROCK);
		this.runeType = runeType;
	}

	public RuneType getRuneType()
	{
		return runeType;
	}
}
