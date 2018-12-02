package brightspark.runicmagic.util;

import net.minecraft.util.math.BlockPos;

public class Location
{
    private final int dimension;
    private final BlockPos position;

    public Location(int dimension, BlockPos position)
    {
        this.dimension = dimension;
        this.position = position;
    }

    public int getDimension()
    {
        return dimension;
    }

    public BlockPos getPosition()
    {
        return position;
    }
}
