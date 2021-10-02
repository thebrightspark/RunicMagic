package brightspark.runicmagic.util

import brightspark.runicmagic.item.RuneItem
import brightspark.runicmagic.model.RuneType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.Hand
import net.minecraft.util.NonNullList
import net.minecraftforge.fml.server.ServerLifecycleHooks
import java.util.concurrent.TimeUnit
import kotlin.math.min

object RMUtils {
	val isDedicatedServer: Boolean by lazy { ServerLifecycleHooks.getCurrentServer()?.isDedicatedServer ?: false }

	fun hasRunes(inventory: NonNullList<ItemStack>, runeCost: Map<RuneType, Int>): Boolean {
		val remainingCost = runeCost.toMutableMap()
		inventory.filter { !it.isEmpty && it.item is RuneItem }.forEach {
			val runeType = (it.item as RuneItem).runeType
			remainingCost.computeIfPresent(runeType) { _, cost ->
				val newCost = cost - it.count
				return@computeIfPresent if (newCost <= 0) null else newCost
			}
			if (remainingCost.isEmpty())
				return true
		}
		return false
	}

	fun removeRunes(inventory: NonNullList<ItemStack>, runeCost: Map<RuneType, Int>) {
		val remainingCost = runeCost.toMutableMap()
		inventory.indices.forEach {
			val stack = inventory[it]
			if (stack.isEmpty || stack.item !is RuneItem) return@forEach
			val runeType = (stack.item as RuneItem).runeType
			remainingCost.computeIfPresent(runeType) { _, cost ->
				val actualCost = min(cost, stack.count)
				val newCost = cost - actualCost
				stack.shrink(actualCost)
				return@computeIfPresent if (newCost <= 0) null else newCost
			}
			if (remainingCost.isEmpty())
				return
		}
	}

	fun isHoldingItem(player: PlayerEntity, item: Item): Boolean = isHoldingItem(player) { it.item == item }

	fun isHoldingItem(player: PlayerEntity, predicate: (Item) -> Boolean): Boolean = Hand.values().any {
		player.getHeldItem(it).run { !isEmpty && predicate(item) }
	}

	fun findHeldItem(player: PlayerEntity, item: Item): Pair<Hand, ItemStack>? = findHeldItem(player) { it == item }

	fun findHeldItem(player: PlayerEntity, predicate: (Item) -> Boolean): Pair<Hand, ItemStack>? {
		Hand.values().forEach {
			val heldStack = player.getHeldItem(it)
			if (!heldStack.isEmpty && predicate(heldStack.item))
				return it to heldStack
		}
		return null
	}

	fun ticksToSecondsString(ticks: Long): String {
		var millis = ticks * 50L
		val seconds = TimeUnit.MILLISECONDS.toSeconds(millis)
		millis -= TimeUnit.SECONDS.toMillis(seconds)
		return if (millis == 0L) "${seconds}s" else "${seconds}.${millis}s"
	}
}
