package brightspark.runicmagic.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.INBTSerializable;

public class Location implements INBTSerializable<NBTTagCompound>
{
    private int dimension;
    private BlockPos position;

    public Location(int dimension, BlockPos position)
    {
        this.dimension = dimension;
        this.position = position;
    }

    public Location(NBTTagCompound nbt)
    {
        deserializeNBT(nbt);
    }

    public int getDimension()
    {
        return dimension;
    }

    public BlockPos getPosition()
    {
        return position;
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("dim", dimension);
        nbt.setLong("pos", position.toLong());
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt)
    {
        dimension = nbt.getInteger("dim");
        position = BlockPos.fromLong(nbt.getLong("pos"));
    }
}
