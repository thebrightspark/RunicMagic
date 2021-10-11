package brightspark.runicmagic.spell

import brightspark.runicmagic.model.RuneType
import brightspark.runicmagic.model.SpellCastData
import brightspark.runicmagic.model.SpellType
import brightspark.runicmagic.util.RunicMagicException
import brightspark.runicmagic.util.appendTranslation
import brightspark.runicmagic.util.sendMessage
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.vector.Vector3d
import net.minecraft.util.text.StringTextComponent
import net.minecraft.world.World
import net.minecraftforge.registries.ForgeRegistryEntry
import java.util.*
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

abstract class Spell(props: Properties) : ForgeRegistryEntry<Spell>() {
	val iconRL: ResourceLocation by lazy {
		ResourceLocation(registryName!!.namespace, "textures/spells/${registryName!!.path}.png")
	}
	val unlocName: String by lazy { "${registryName!!.namespace}.spell.${registryName!!.path}.name" }

	val spellType: SpellType = props.spellType
	val level: Int = props.levelReq
	//If true, then is a selectable spell for use with a staff
	//If false, then is an instant click spell (e.g. teleport spell)
	val selectable: Boolean = props.selectable
	val cooldown: Int = props.cooldown
	val castTime: Int = props.castTime
	val runeCost: Map<RuneType, Int> = props.runeCost.toMap()

	/**
	 * Checks if the player meets the spell's requirements to start casting
	 * @param player The player
	 * @return Whether the spell can be cast
	 */
	abstract fun canCast(player: PlayerEntity): Boolean

	/**
	 * Process updating the casting of this spell if it's not instant
	 * @param world The world
	 * @param progress The current progress in ticks since the spell was executed
	 * @return true if the spell should be cancelled
	 */
	open fun updateCasting(world: World, player: PlayerEntity, progress: Int): Boolean {
		if (castTime > 0)
			throw RunicMagicException("Non-instant casting spell $this does not override Spell#updateCasting!")
		return false
	}

	/**
	 * Executes this spell and returns true if successful
	 * Will be called instantly for instant spells, or at the end of casting
	 * @return true if spell cast successfully
	 */
	abstract fun execute(player: ServerPlayerEntity, data: SpellCastData): Boolean

	/**
	 * Called when a spell cast is cancelled
	 */
	open fun onCastCancel(player: PlayerEntity) {
		// TODO: Localise
		player.sendMessage(StringTextComponent("Spell ").appendTranslation(unlocName).appendString(" cancelled!"))
	}

	override fun toString(): String = registryName.toString()

	//
	// Util methods
	//

	protected fun randOffset(world: World, variance: Double): Double =
		if (variance == 0.0) 0.0 else (world.rand.nextDouble() * variance) - (variance / 2)

	protected fun posOffset(world: World, posComponent: Double, variance: Double): Double =
		posComponent + randOffset(world, variance)

	protected fun posOffset(world: World, pos: Vector3d, xVary: Double, yVary: Double, zVary: Double): Vector3d =
		pos.add(randOffset(world, xVary), randOffset(world, yVary), randOffset(world, zVary))

	protected fun randVector(random: Random): Vector3d {
		val phi = random.nextDouble() * 2 * Math.PI
		val theta = acos((random.nextDouble() * 2.0) - 1.0)
		return Vector3d(sin(theta) * cos(phi), sin(theta) * sin(phi), cos(theta))
	}

	// TODO: Change this so only explicit player movement counts? e.g. being pushed by water or another entity doesn't count
	protected fun hasPlayerMoved(player: PlayerEntity): Boolean =
		player.prevPosX != player.posX || player.prevPosY != player.posY || player.prevPosZ != player.posZ

	protected fun countItemsInPlayerInv(player: PlayerEntity, stack: ItemStack): Int =
		(0 until player.inventory.sizeInventory).sumBy {
			val invStack = player.inventory.getStackInSlot(it)
			return@sumBy if (ItemStack.areItemsEqualIgnoreDurability(stack, invStack)) invStack.count else 0
		}

	protected fun playerHasStack(player: PlayerEntity, predicate: (ItemStack) -> Boolean): Boolean =
		(0 until player.inventory.sizeInventory).any {
			player.inventory.getStackInSlot(it).run { !isEmpty && predicate(this) }
		}

	protected fun playerHasItem(player: PlayerEntity, item: Item): Boolean = playerHasStack(player) { it.item == item }

	protected fun playerHasSpace(player: PlayerEntity): Boolean =
		(0 until player.inventory.sizeInventory).any { player.inventory.getStackInSlot(it).isEmpty }

	protected fun givePlayerStack(player: PlayerEntity, stack: ItemStack) {
		if (!player.addItemStackToInventory(stack))
			player.entityDropItem(stack)
	}

	class Properties(
		var spellType: SpellType,
		var levelReq: Int,
		var selectable: Boolean = true,
		var cooldown: Int = 0,
		var castTime: Int = 0,
		var runeCost: MutableMap<RuneType, Int> = mutableMapOf()
	) {
		fun setSpellType(spellType: SpellType): Properties {
			this.spellType = spellType
			return this
		}

		fun setLevelReq(levelReq: Int): Properties {
			this.levelReq = levelReq
			return this
		}

		fun setSelectable(selectable: Boolean): Properties {
			this.selectable = selectable
			return this
		}

		fun setCooldown(cooldown: Int): Properties {
			this.cooldown = cooldown
			return this
		}

		fun setCastTime(castTime: Int): Properties {
			this.castTime = castTime
			return this
		}

		fun addRuneCost(runeType: RuneType, amount: Int): Properties {
			runeCost[runeType] = amount
			return this
		}

		fun addRuneCost(vararg cost: Pair<RuneType, Int>): Properties {
			cost.forEach { runeCost[it.first] = it.second }
			return this
		}
	}
}
