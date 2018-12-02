package brightspark.runicmagic.block;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public abstract class RMBlockContainerBase<T extends TileEntity> extends RMBlockBase implements ITileEntityProvider
{
    public RMBlockContainerBase(String name, Material material)
    {
        super(name, material);
    }

    @SuppressWarnings("unchecked")
    public T getTileEntity(IBlockAccess world, BlockPos pos)
    {
        return (T) world.getTileEntity(pos);
    }
}
