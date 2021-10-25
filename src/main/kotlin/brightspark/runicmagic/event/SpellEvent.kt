package brightspark.runicmagic.event

import brightspark.runicmagic.entity.SpellEntity
import brightspark.runicmagic.spell.Spell
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.RayTraceResult
import net.minecraftforge.event.entity.ProjectileImpactEvent
import net.minecraftforge.event.entity.player.PlayerEvent
import net.minecraftforge.eventbus.api.Cancelable
import net.minecraftforge.eventbus.api.Event

/**
 * Base event for [Spell] related events
 */
abstract class SpellEvent(val spell: Spell, player: PlayerEntity) : PlayerEvent(player)

/**
 * Base event for when a [Spell] is cast by a player
 */
abstract class SpellCastEvent(spell: Spell, player: PlayerEntity) : SpellEvent(spell, player) {
	/**
	 * Fired just before the spell is about to be cast
	 * Cancel to prevent the spell from being cast
	 */
	@Cancelable
	class Pre(spell: Spell, player: PlayerEntity) : SpellCastEvent(spell, player)

	/**
	 * Fired just after the spell has been cast and provides the spell entity
	 */
	class Post(spell: Spell, player: PlayerEntity, val spellEntity: SpellEntity) : SpellCastEvent(spell, player)
}

/**
 * This event is called by the spell selection GUI to check if a spell is "unlocked"
 * Locked spells will not be clickable
 *
 * Result.ALLOW -> Unlocks the spell
 * Result.DEFAULT -> Default logic - spell is unlocked if player level is equal or above spell level requirement
 * Result.DENY -> Locks the spell
 */
@Event.HasResult
open class SpellUnlockedEvent(spell: Spell, player: PlayerEntity) : SpellEvent(spell, player)

/**
 * Fired when a spell entity hits something
 * Cancel to stop anything from happening on impact
 */
@Cancelable
class SpellImpactEvent(entity: SpellEntity, ray: RayTraceResult) : ProjectileImpactEvent(entity, ray)
