package brightspark.runicmagic.entity

import brightspark.runicmagic.event.SpellImpactEvent
import brightspark.runicmagic.init.RMParticles
import brightspark.runicmagic.init.RMSpells
import brightspark.runicmagic.particle.ColouredParticleData
import brightspark.runicmagic.spell.projectile.ProjectileBaseSpell
import brightspark.runicmagic.util.addParticle
import brightspark.runicmagic.util.onClient
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.projectile.ProjectileHelper
import net.minecraft.nbt.CompoundNBT
import net.minecraft.network.IPacket
import net.minecraft.util.EntityPredicates
import net.minecraft.util.Util
import net.minecraft.util.math.EntityRayTraceResult
import net.minecraft.util.math.RayTraceContext
import net.minecraft.util.math.RayTraceResult
import net.minecraft.util.math.vector.Vector3d
import net.minecraft.world.World
import net.minecraft.world.server.ServerWorld
import net.minecraftforge.fml.network.NetworkHooks
import thedarkcolour.kotlinforforge.forge.FORGE_BUS
import java.awt.Color
import java.util.*

class SpellEntity(entityType: EntityType<*>, world: World) : Entity(entityType, world) {
	var spell: ProjectileBaseSpell? = null
	private var shooterUuid: UUID? = shooter?.uniqueID
	private var shooterId: Int? = shooter?.entityId
	var shooter: Entity?
		get() = if (world is ServerWorld)
			shooterUuid?.let { (world as ServerWorld).getEntityByUuid(it) }
		else if (shooterId != 0)
			shooterId?.let { world.getEntityByID(it) }
		else
			null
		set(value) {
			value?.let {
				shooterUuid = it.uniqueID
				shooterId = it.entityId
				setPosition(it.posX, it.posYEye - 0.1, it.posZ)
				setDirectionAndMotion(it)
			} ?: run {
				shooterUuid = null
				shooterId = null
			}
		}

	private val entityPredicate =
		EntityPredicates.NOT_SPECTATING.and(EntityPredicates.IS_ALIVE).and(EntityPredicates.pushableBy(this))

	private fun setDirectionAndMotion(entity: Entity) {
		val yaw = entity.rotationYaw
		val pitch = entity.rotationPitch
		setRotation(yaw, pitch)
		motion = Vector3d.fromPitchYaw(pitch, yaw)
	}

	override fun registerData() = Unit

	override fun tick() {
		super.tick()

		var posNext = positionVec.add(motion)
		var raytrace: RayTraceResult? = world.rayTraceBlocks(
			RayTraceContext(
				positionVec,
				posNext,
				RayTraceContext.BlockMode.COLLIDER,
				RayTraceContext.FluidMode.NONE,
				this
			)
		)
		raytrace?.let {
			if (it.type != RayTraceResult.Type.MISS)
				posNext = it.hitVec
		}

		ProjectileHelper.rayTraceEntities(
			world,
			this,
			positionVec,
			posNext,
			boundingBox.expand(motion).grow(1.0),
			entityPredicate
		)?.let { raytrace = it }

		raytrace?.let {
			if (it.type == RayTraceResult.Type.ENTITY && it is EntityRayTraceResult) {
				val entityHit = it.entity
				val entityShooter = shooter
				if (entityHit is PlayerEntity && entityShooter is PlayerEntity
					&& !entityShooter.canAttackPlayer(entityHit)
				) {
					return@let
				}
			}
			if (it.type != RayTraceResult.Type.MISS && !FORGE_BUS.post(SpellImpactEvent(this, it))) {
				onImpact(it)
			}
		}

		// Move
		setPosition(posNext.x, posNext.y, posNext.z)
		doBlockCollisions()

		// Particles
		world.onClient {
			addParticle(ColouredParticleData(RMParticles.SINGLE_MOVING, Color.WHITE), positionVec)
		}
	}

	private fun onImpact(ray: RayTraceResult) {
		when (ray.type) {
			RayTraceResult.Type.BLOCK -> setDead()
			RayTraceResult.Type.ENTITY -> {
				val entityHit = (ray as EntityRayTraceResult).entity
				val shooterEntity = shooter
				// TODO: Scale damage depending on player magic level
				//  https://runescape.fandom.com/wiki/Ability_damage#Magic
				spell?.let {
					val damage = it.baseDamage
					if (damage > 0F)
						entityHit.attackEntityFrom(it.createDamageSource(this, shooterEntity), damage)
					it.affectEntityHit(this, shooterEntity, entityHit)
				}
				setDead()
			}
			else -> Unit
		}
	}

	override fun readAdditional(nbt: CompoundNBT) {
		spell = RMSpells.get(nbt.getString("spell")) as ProjectileBaseSpell
		shooterUuid = nbt.getUniqueId("shooter")
	}

	override fun writeAdditional(nbt: CompoundNBT) {
		nbt.putString("spell", spell?.let { it.registryName.toString() } ?: "")
		nbt.putUniqueId("shooter", shooterUuid ?: Util.DUMMY_UUID)
	}

	override fun createSpawnPacket(): IPacket<*> = NetworkHooks.getEntitySpawningPacket(this)
}
