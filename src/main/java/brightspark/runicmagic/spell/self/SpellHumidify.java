package brightspark.runicmagic.spell.self;

import brightspark.runicmagic.enums.RuneType;
import brightspark.runicmagic.particle.ParticleCloud;
import brightspark.runicmagic.particle.ParticleRain;
import brightspark.runicmagic.util.ClientUtils;
import brightspark.runicmagic.util.SpellCastData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

public class SpellHumidify extends SpellSelfBase
{
	private static final ItemStack BUCKET = new ItemStack(Items.BUCKET);

	public SpellHumidify()
	{
		super("humidify");
		addRuneCost(RuneType.ASTRAL, 1);
		addRuneCost(RuneType.FIRE, 1);
		addRuneCost(RuneType.WATER, 3);
		castTime = 80;
	}

	private static ItemStack createWaterBucket()
	{
		return FluidUtil.getFilledBucket(new FluidStack(FluidRegistry.WATER, Fluid.BUCKET_VOLUME));
	}

	@Override
	public boolean canCast(EntityPlayer player)
	{
		if(hasPlayerMoved(player))
			return false;
		for(int i = 0; i < player.inventory.getSizeInventory(); i++)
			if(player.inventory.getStackInSlot(i).getItem() == Items.BUCKET)
				return true;
		return false;
	}

	@Override
	public boolean updateCasting(World world, EntityPlayer player, int progress)
	{
		if(world.isRemote)
		{
			Vec3d playerPos = player.getPositionVector().add(0, 3D, 0);
			//Clouds
			for(int i = 0; i < 5; i++)
				ClientUtils.spawnParticle(new ParticleCloud(world, posOffset(world, playerPos, 1.5D, 0.5D, 1.5D)));

			if(progress >= 20)
			{
				//Rain
				int particles = Math.min(10 + (countItems(player, BUCKET) * 2), 200);
				for(int i = 0; i < particles; i++)
					ClientUtils.spawnParticle(new ParticleRain(world, posOffset(world, playerPos, 1.2D, 0.3D, 1.2D)));
			}
		}
		return hasPlayerMoved(player);
	}

	@Override
	public boolean execute(EntityPlayer player, SpellCastData data)
	{
		boolean bucketsFound = false;
		InventoryPlayer inv = player.inventory;
		for(int i = 0; i < inv.getSizeInventory(); i++)
		{
			ItemStack stack = inv.getStackInSlot(i);
			if(stack.getItem() == Items.BUCKET)
			{
				int count = stack.getCount();
				inv.setInventorySlotContents(i, createWaterBucket());
				if(count > 1)
				{
					for(int num = 1; num < count; num++)
					{
						ItemStack water = createWaterBucket();
						if(!player.addItemStackToInventory(water))
							player.entityDropItem(water, 0F);
					}
				}
				bucketsFound = true;
			}
		}
		return bucketsFound;
	}
}
