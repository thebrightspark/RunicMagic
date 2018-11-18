package brightspark.runemagic.block;

import brightspark.runemagic.RuneMagic;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class RMBlockBase extends Block
{
	public RMBlockBase(String name, Material material)
	{
		super(material);
		setRegistryName(name);
		setTranslationKey(name);
		setCreativeTab(RuneMagic.TAB);
	}
}
