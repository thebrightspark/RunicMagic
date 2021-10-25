package brightspark.runicmagic.spell.projectile

import brightspark.runicmagic.entity.SpellEntity
import brightspark.runicmagic.model.SpellCastData
import brightspark.runicmagic.spell.Spell
import brightspark.runicmagic.util.onServer
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.util.DamageSource
import net.minecraft.util.IndirectEntityDamageSource

open class ProjectileBaseSpell(
	props: Properties,
	private val entityType: EntityType<SpellEntity>,
	val baseDamage: Float
) : Spell(props) {
	override fun canCast(player: PlayerEntity): Boolean = true

	override fun execute(player: ServerPlayerEntity, data: SpellCastData): Boolean {
		player.world.onServer {
			// FIXME: This entity isn't spawning?
			entityType.create(this)?.let {
				it.spell = this@ProjectileBaseSpell
				it.shooter = player
				this.addEntity(it)
			}
		}
		return true
	}

	fun createDamageSource(spellEntity: SpellEntity, entitySource: Entity?): DamageSource =
		IndirectEntityDamageSource(registryName.toString(), spellEntity, entitySource).setProjectile()

	open fun affectEntityHit(projectile: Entity, caster: Entity?, entityHit: Entity) = Unit
}
