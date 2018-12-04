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

    @Override
    public boolean updateCasting(World world, EntityPlayer player, int progress)
    {
        if(world.isRemote)
        {
            //Taken calculations from EntityPlayer#getLook
            float lookX = MathHelper.cos((player.rotationYawHead - 90) * 0.017453292F - (float) Math.PI);
            float lookZ = MathHelper.sin((player.rotationYawHead - 90) * 0.017453292F - (float) Math.PI);
            Vec3d pos = player.getPositionVector().add(lookX, player.getEyeHeight() * 0.85F, lookZ);
            for(int i = 0; i < 5; i++)
            {
                Vec3d offset = createRandVector(world.rand).scale(0.3D);
                ParticleMoving particle = new ParticleMoving(world, pos.add(offset), Color.ORANGE, 0)
                    .setMotion(offset.scale(-0.05D))
                    .setSecondColour(Color.WHITE);
                particle.setMaxAge(20);
                ClientUtils.spawnParticle(particle);
            }
            if(progress == castTime)
            {
                for(int i = 0; i < 100; i++)
                {
                    Vec3d dir = createRandVector(world.rand).scale(world.rand.nextDouble() * 0.05D);
                    ParticleMoving particle = new ParticleMoving(world, pos, Color.ORANGE, 0)
                            .setMotion(dir)
                            .setFadeOut()
                            .setSecondColour(Color.RED);
                    particle.setMaxAge(20);
                    ClientUtils.spawnParticle(particle);
                }
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
