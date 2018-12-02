package brightspark.runicmagic.spell.self;

import brightspark.runicmagic.enums.SpellType;
import brightspark.runicmagic.init.RMBlocks;
import brightspark.runicmagic.init.RMCapabilities;
import brightspark.runicmagic.util.SpellCastData;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
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
        return player.world.setBlockState(player.getPosition(), RMBlocks.gatestone.getDefaultState());
    }
}
