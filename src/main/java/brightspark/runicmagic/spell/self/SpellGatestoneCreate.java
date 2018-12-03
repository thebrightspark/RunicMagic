package brightspark.runicmagic.spell.self;

import brightspark.runicmagic.block.tileentity.TileGatestone;
import brightspark.runicmagic.enums.SpellType;
import brightspark.runicmagic.init.RMBlocks;
import brightspark.runicmagic.init.RMCapabilities;
import brightspark.runicmagic.particle.ParticleMoving;
import brightspark.runicmagic.util.ClientUtils;
import brightspark.runicmagic.util.Location;
import brightspark.runicmagic.util.SpellCastData;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.awt.*;
import java.util.Random;

public class SpellGatestoneCreate extends SpellSelfBase
{
    public SpellGatestoneCreate()
    {
        super("gatestone_create", SpellType.OTHER, 32);
        cooldown = 600; //30s
        castTime = 60; //3s
    }

    @Override
    public boolean canCast(EntityPlayer player)
    {
        IBlockState state = player.world.getBlockState(player.getPosition());
        return RMCapabilities.getSpells(player).getGatestone() == null &&
                state.getBlock().isReplaceable(player.world, player.getPosition()) &&
                player.world.getBlockState(player.getPosition().down()).isSideSolid(player.world, player.getPosition().down(), EnumFacing.UP) &&
                !hasPlayerMoved(player);
    }

    // https://gist.github.com/andrewbolster/10274979
    private static Vec3d createRandVector(Random random)
    {
        //TODO: This only works for a semi-circle!?
        double phi = random.nextDouble() * Math.PI;
        double theta = Math.acos((random.nextDouble() * 2D) - 1D);
        double x = Math.sin(theta) * Math.cos(phi);
        double y = Math.sin(theta) * Math.sin(phi);
        double z = Math.cos(theta);
        return new Vec3d(x, y, z);
    }

    @Override
    public boolean updateCasting(World world, EntityPlayer player, int progress)
    {
        //TODO: Particles
        if(world.isRemote)
        {
            //Taken calculations from EntityPlayer#getLook
            float lookX = MathHelper.cos((player.rotationYawHead - 90) * 0.017453292F - (float) Math.PI);
            float lookZ = MathHelper.sin((player.rotationYawHead - 90) * 0.017453292F - (float) Math.PI);
            Vec3d pos = player.getPositionVector().add(lookX, player.getEyeHeight() - 0.25F, lookZ);
            for(int i = 0; i < 5; i++)
            {
                Vec3d offset = createRandVector(world.rand).scale(0.2D);
                ParticleMoving particle = new ParticleMoving(world, pos.add(offset), new Color(0xFFA500), 0);
                particle.setMotion(offset.scale(-0.1D));
                particle.setMaxAge(10);
                ClientUtils.spawnParticle(particle);
            }
        }
        return hasPlayerMoved(player);
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
