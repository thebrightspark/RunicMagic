package brightspark.runicmagic.item

import brightspark.runicmagic.init.RMCapabilities
import brightspark.runicmagic.model.RuneType
import brightspark.runicmagic.model.StaffType
import brightspark.runicmagic.spell.Spell
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.world.World

// https://runescape.fandom.com/wiki/Staff_(weapon_type)
class StaffItem(val staffType: StaffType, runeType: RuneType, props: Properties) : RuneItem(runeType, props) {
	companion object {
		private fun getStaffItem(stack: ItemStack): StaffItem? =
			if (stack.isEmpty || stack.item !is StaffItem) null else stack.item as StaffItem

		fun calculateRuneCost(stack: ItemStack, spell: Spell): Map<RuneType, Int> =
			spell.runeCost.toMutableMap().apply { getStaffItem(stack)?.let { remove(it.runeType) } }

		fun getAttackBonus(stack: ItemStack): Float =
			getStaffItem(stack)?.staffType?.attackBonus ?: 0F

		fun getRuneType(stack: ItemStack): RuneType =
			getStaffItem(stack)?.runeType ?: RuneType.NONE
	}

	override fun onItemRightClick(world: World, player: PlayerEntity, hand: Hand): ActionResult<ItemStack> {
		val stack = player.getHeldItem(hand)
		if (player is ServerPlayerEntity) {
			// TODO: Fire CanCastResult event
			player.getCapability(RMCapabilities.SPELLS).ifPresent {
				it.executeSpell(player, stack, null)
			}
		}
		return ActionResult.resultSuccess(stack)
	}
}
