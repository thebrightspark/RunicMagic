package brightspark.runicmagic.block;

import brightspark.runicmagic.RunicMagic;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class RMBlockBase extends Block
{
	public RMBlockBase(String name, Material material)
	{
		super(material);
		setRegistryName(name);
		setTranslationKey(name);
		setCreativeTab(RunicMagic.TAB);
	}
}
