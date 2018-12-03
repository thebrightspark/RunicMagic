package brightspark.runicmagic.block;

import brightspark.runicmagic.block.tileentity.TileGatestone;
import brightspark.runicmagic.init.RMCapabilities;
import brightspark.runicmagic.util.Location;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.UUID;

public class BlockGatestone extends RMBlockContainerBase<TileGatestone>
{
    //TEMP
    private static final AxisAlignedBB TORCH_AABB = new AxisAlignedBB(0.4000000059604645D, 0.0D, 0.4000000059604645D, 0.6000000238418579D, 0.6000000238418579D, 0.6000000238418579D);

    public BlockGatestone()
    {
        super("gatestone", Material.GLASS);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileGatestone();
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if(worldIn.isRemote)
            return true;
        TileGatestone te = getTileEntity(worldIn, pos);
        if(te.validateGatestone())
        {
            EntityPlayer owner = worldIn.getMinecraftServer().getPlayerList().getPlayerByUUID(te.getPlayerUuid());
            String name = playerIn.equals(owner) ? "you" : owner.getDisplayNameString();
            playerIn.sendMessage(new TextComponentString("This Gatestone belongs to " + name));
        }
        else
            worldIn.destroyBlock(pos, false);
        return true;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        if(placer instanceof EntityPlayer)
        {
            TileGatestone te = getTileEntity(worldIn, pos);
            te.setPlayerUuid(state, placer.getUniqueID());
            RMCapabilities.getSpells(placer).setGatestone(new Location(placer.dimension, pos));
        }
        else
            worldIn.destroyBlock(pos, false);
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        onGatestoneBroken(worldIn, pos);
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        if(!canPlaceBlockAt(worldIn, pos) || !getTileEntity(worldIn, pos).validateGatestone())
        {
            onGatestoneBroken(worldIn, pos);
            worldIn.destroyBlock(pos, false);
        }
    }

    /**
     * Notifies the player with a message that their gatestone has been destroyed
     */
    private void onGatestoneBroken(World world, BlockPos pos)
    {
        if(world.isRemote)
            return;
        TileGatestone te = getTileEntity(world, pos);
        UUID playerUuid = te.getPlayerUuid();
        if(playerUuid == null)
            return;
        EntityPlayer player = world.getPlayerEntityByUUID(playerUuid);
        if(player == null)
            return;
        RMCapabilities.getSpells(player).setGatestone(null);
        player.sendMessage(new TextComponentString("Gatestone has been destroyed!"));
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        BlockPos downPos = pos.down();
        return worldIn.getBlockState(downPos).isSideSolid(worldIn, downPos, EnumFacing.UP);
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
    {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return Items.AIR;
    }

    @Override
    public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player)
    {
        return false;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return TORCH_AABB;
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
    {
        return NULL_AABB;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Override
    public BlockRenderLayer getRenderLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }
}
