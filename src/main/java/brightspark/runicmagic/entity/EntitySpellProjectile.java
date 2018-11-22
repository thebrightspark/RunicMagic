package brightspark.runicmagic.entity;

import brightspark.runicmagic.spell.projectile.DamageSourceSpellProjectile;
import brightspark.runicmagic.spell.projectile.SpellProjectileBase;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

public abstract class EntitySpellProjectile extends Entity implements IProjectile
{
	private static final Predicate<Entity> TARGETS = ((Predicate<Entity>) EntitySelectors.NOT_SPECTATING::apply).and(EntitySelectors.IS_ALIVE::apply).and(Entity::canBeCollidedWith);

	private EntityLivingBase shooter;
	private SpellProjectileBase spell;
	private float damage = 4F;
	private int knockback = 1;

	public EntitySpellProjectile(World worldIn)
	{
		super(worldIn);
		setSize(0.5F, 0.5F);
	}

	public EntitySpellProjectile(EntityLivingBase shooter, SpellProjectileBase spell)
	{
		this(shooter.world);
		setPosition(shooter.posX, shooter.posY + shooter.getEyeHeight() - 0.1D, shooter.posZ);
		setHeadingFromShooter(shooter, 0F, 0.25F); //TODO: Speed this up later after
		this.shooter = shooter;
		this.spell = spell;
	}

	public void setAttackDamage(float damage)
	{
		this.damage = damage;
	}

	@Override
	protected void entityInit() {}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt)
	{
		damage = nbt.getFloat("damage");
		knockback = nbt.getInteger("knockback");
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt)
	{
		nbt.setFloat("damage", damage);
		nbt.setInteger("knockback", knockback);
	}

	public void setHeadingFromShooter(Entity shooter, float velocity, float inaccuracy)
	{
		float rotYaw = shooter.rotationYaw;
		float rotPitch = shooter.rotationPitch;
		float x = -MathHelper.sin(rotYaw * 0.017453292f) * MathHelper.cos(rotPitch * 0.017453292f);
		float y = -MathHelper.sin(rotPitch * 0.017453292f);
		float z = MathHelper.cos(rotYaw * 0.017453292f) * MathHelper.cos(rotPitch * 0.017453292f);
		shoot(x, y, z, velocity, inaccuracy);
		motionX += shooter.motionX;
		motionZ += shooter.motionZ;
		if(!shooter.onGround)
			motionY += shooter.motionY;
	}

	private double genRandInaccuracy(float inaccuracy)
	{
		return rand.nextGaussian() * 0.007499999832361937D * (double) inaccuracy;
	}

	@Override
	public void shoot(double x, double y, double z, float velocity, float inaccuracy)
	{
		double magnitude = MathHelper.sqrt(x * x + y * y + z * z);
		x = (x / magnitude + genRandInaccuracy(inaccuracy)) * velocity;
		y = (y / magnitude + genRandInaccuracy(inaccuracy)) * velocity;
		z = (z / magnitude + genRandInaccuracy(inaccuracy)) * velocity;
		motionX = x;
		motionY = y;
		motionZ = z;
		double hMagnitude = MathHelper.sqrt(x * x + z * z);
		rotationYaw = (float) (MathHelper.atan2(x, z) * (180d / Math.PI));
		rotationPitch = (float) (MathHelper.atan2(y, hMagnitude) * (180D / Math.PI));
		prevRotationYaw = rotationYaw;
		prevRotationPitch = rotationPitch;
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();

		//Ray trace in front of the projectile between its current and next position
		Vec3d posNow = new Vec3d(posX, posY, posZ);
		Vec3d posNext = posNow.add(motionX, motionY, motionZ);
		RayTraceResult ray = world.rayTraceBlocks(posNow, posNext, true, true, false);
		posNow = new Vec3d(posX, posY, posZ);
		posNext = posNow.add(motionX, motionY, motionZ);
		if(ray != null)
			posNext = ray.hitVec;

		//Get entity hit if one is on the projectile's path
		Entity entityHit = findEntityOnPath(posNow, posNext);
		if(entityHit != null)
		{
			if(entityHit instanceof EntityPlayer && shooter instanceof EntityPlayer && !((EntityPlayer) shooter).canAttackPlayer((EntityPlayer) entityHit))
				ray = null;
			else
				ray = new RayTraceResult(entityHit);
		}

		//If has hit something, call onHit
		if(ray != null)
			onHit(ray);

		if(world.isRemote)
			spawnParticles(getEntityBoundingBox().getCenter());

		//Move
		posX += motionX;
		posY += motionY;
		posZ += motionZ;

		setPosition(posX, posY, posZ);
		doBlockCollisions();
	}

	/**
	 * Called every tick on the client side just before moving to spawn particles
	 */
	protected abstract void spawnParticles(Vec3d centerPos);

	private void onHit(RayTraceResult ray)
	{
		Entity entityHit = ray.entityHit;
		if(entityHit != null)
		{
			DamageSource damageSource = new DamageSourceSpellProjectile(this, shooter == null ? this : shooter);

			//Hit entity
			if(entityHit.attackEntityFrom(damageSource, damage))
			{
				if(entityHit instanceof EntityLivingBase)
				{
					EntityLivingBase entityLiving = (EntityLivingBase) entityHit;

					//Knockback
					entityLiving.addVelocity(knockback * 0.6D / motionX, knockback * 0.6D / motionY, knockback * 0.6D / motionZ);

					//Enchantments
					if(shooter instanceof EntityLivingBase)
					{
						EnchantmentHelper.applyThornEnchantments(entityLiving, shooter);
						EnchantmentHelper.applyArthropodEnchantments(shooter, entityLiving);
					}

					onEntityHit(entityLiving);

					//Play hit sound for shooter (arrow ding)
					if(entityLiving != shooter && entityLiving instanceof EntityPlayer && shooter instanceof EntityPlayerMP)
						((EntityPlayerMP) shooter).connection.sendPacket(new SPacketChangeGameState(6, 0F));
				}

				//Play hit sound
				//TODO: Custom spell projectile sound effects?
				playSound(SoundEvents.ENTITY_ARROW_HIT, 1F, 1.2F / (rand.nextFloat() * 0.2F + 0.9F));

				if(!(entityHit instanceof EntityEnderman))
					setDead();
			}
			else
				//Couldn't hit entity
				setDead();
		}
		else
		{
			//Hit a block
			playSound(SoundEvents.ENTITY_ARROW_HIT, 1.0F, 1.2F / (rand.nextFloat() * 0.2F + 0.9F));
			BlockPos pos = ray.getBlockPos();
			IBlockState state = world.getBlockState(pos);
			if(state.getMaterial() != Material.AIR)
				state.getBlock().onEntityCollision(world, pos, state, this);
			setDead();
		}
	}

	/**
	 * Do something when this projectile hits an entity
	 */
	protected void onEntityHit(EntityLivingBase entity)
	{
		entity.attackEntityFrom(new DamageSourceSpellProjectile(this, shooter), damage);
		spell.applyEffects(entity);
	}

	public Vec3d getCenter(AxisAlignedBB box)
	{
		return new Vec3d(box.minX + (box.maxX - box.minX) * 0.5D, box.minY + (box.maxY - box.minY) * 0.5D, box.minZ + (box.maxZ - box.minZ) * 0.5D);
	}

	private List<Entity> getEntitiesInArea(AxisAlignedBB area, List<Entity> entities)
	{
		List<Entity> entitiesInArea = new LinkedList<>();
		entities.forEach((entity) ->
		{
			if(entity.getEntityBoundingBox().intersects(area) && TARGETS.test(entity))
				entitiesInArea.add(entity);
		});
		return entitiesInArea;
	}

	//Copied from EntityArrow
	@Nullable
	protected Entity findEntityOnPath(Vec3d start, Vec3d end)
	{
		Entity closestEntity = null;
		double closestDistance = 0.0D;

		AxisAlignedBB startAABB = getEntityBoundingBox();
		AxisAlignedBB endAABB = startAABB.offset(motionX, motionY, motionZ);
		Vec3d startCenter = getCenter(getEntityBoundingBox());
		Vec3d endCenter = getCenter(endAABB);

		AxisAlignedBB area = new AxisAlignedBB(startCenter, endCenter).grow(startAABB.getAverageEdgeLength());
		List<Entity> possibleEntities = world.getEntitiesInAABBexcluding(this, area, TARGETS::test);
		if(possibleEntities.isEmpty())
			return null;

		//This will scan down the path of the projectile to its next position for any entities

		//We'll use this AABB to scan down the path
		AxisAlignedBB movingAABB = new AxisAlignedBB(startAABB.minX, startAABB.minY, startAABB.minZ, startAABB.maxX, startAABB.maxY, startAABB.maxZ);
		//The direct distance between the centers of the start and end AABBs
		double distanceToEnd = startCenter.distanceTo(endCenter);
		//The direct distance we want to move the scanning AABB each iteration
		double stepMove = startAABB.getAverageEdgeLength() * 1.5;
		//How many iterations (minus the end position) it will take to scan the path
		int steps = (int) Math.floor(distanceToEnd / stepMove);
		//The vector we'll add to the scanning AABB every iteration
		Vec3d stepVec = steps == 0 ?
			new Vec3d(motionX, motionY, motionZ) :
			endCenter.subtract(startCenter).scale(1D / (double) steps);

		for(int i = 0; i < steps; i++)
		{
			if(i > 0) movingAABB = movingAABB.offset(stepVec);
			List<Entity> list = getEntitiesInArea(movingAABB, possibleEntities);
			for(Entity entity : list)
			{
				if(entity != shooter)
				{
					AxisAlignedBB aabb = entity.getEntityBoundingBox();
					RayTraceResult raytraceresult = aabb.calculateIntercept(start, end);
					if(raytraceresult != null)
					{
						double distance = start.squareDistanceTo(raytraceresult.hitVec);
						if(distance < closestDistance || closestDistance == 0.0D)
						{
							closestEntity = entity;
							closestDistance = distance;
						}
					}
				}
			}
			if(closestEntity != null)
				break;
		}

		return closestEntity;
	}
}
