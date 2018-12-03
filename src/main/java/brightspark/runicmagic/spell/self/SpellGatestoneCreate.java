package brightspark.runicmagic.spell.self;

import brightspark.runicmagic.block.tileentity.TileGatestone;
import brightspark.runicmagic.enums.SpellType;
import brightspark.runicmagic.init.RMBlocks;
import brightspark.runicmagic.init.RMCapabilities;
import brightspark.runicmagic.util.Location;
import brightspark.runicmagic.util.SpellCastData;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SpellGatestoneCreate extends SpellSelfBase
{
    public SpellGatestoneCreate()
    {
        super("gatestone_create", SpellType.OTHER, 32);
        cooldown = 600; //30s
        //TODO: Uncomment once particles done
        //castTime = 60; //3s
    }

    @Override
    public boolean canCast(EntityPlayer player)
    {
        IBlockState state = player.world.getBlockState(player.getPosition());
        return RMCapabilities.getSpells(player).getGatestone() == null &&
                state.getBlock().isReplaceable(player.world, player.getPosition()) &&
                player.world.getBlockState(player.getPosition().down()).isSideSolid(player.world, player.getPosition().down(), EnumFacing.UP);
    }

    @Override
    public boolean updateCasting(World world, EntityPlayer player, int progress)
    {
        //TODO: Particles
        return super.updateCasting(world, player, progress);
    }

    @Override
    public boolean execute(EntityPlayer player, SpellCastData data)
    {
        if(!canCast(player))
            return false;

        BlockPos pos = player.getPosition();
        if(player.world.setBlockState(pos, RMBlocks.gatestone.getDefaultState()))
        {
            TileEntity te = player.world.getTileEntity(pos);
            if(te instanceof TileGatestone)
            {
                ((TileGatestone) te).setPlayerUuid(player.world.getBlockState(pos), player.getUniqueID());
                RMCapabilities.getSpells(player).setGatestone(new Location(player.dimension, pos));
                return true;
            }
            else
                player.world.destroyBlock(pos, false);
        }
        return false;
    }
}
