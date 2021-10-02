package brightspark.runicmagic.spell.self

import brightspark.runicmagic.model.SpellCastData
import net.minecraft.advancements.CriteriaTriggers
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.item.EnchantedBookItem
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.stats.Stats
import net.minecraft.util.Hand
import net.minecraft.util.SoundCategory
import net.minecraft.util.SoundEvents

class EnchantSpell(private val enchantLevel: Int, props: Properties) : SelfBaseSpell(props) {
	override fun canCast(player: PlayerEntity): Boolean =
		player.heldItemMainhand.let { !it.isEmpty && it.isEnchantable }

	override fun execute(player: PlayerEntity, data: SpellCastData): Boolean {
		val stack = player.heldItemMainhand
		if (stack.isEmpty || !stack.isEnchantable) return false

		// Enchant logic taken from EnchantmentContainer#enchantItem
		val rand = player.world.rand
		val enchantments = EnchantmentHelper.buildEnchantmentList(rand, stack, enchantLevel, false)
		val isBook = stack.item === Items.BOOK
		if (isBook && enchantments.size > 1)
			enchantments.removeAt(rand.nextInt(enchantments.size))
		else if (enchantments.isEmpty())
			return false

		val resultStack = if (isBook)
			ItemStack(Items.ENCHANTED_BOOK).apply { tag = stack.orCreateTag.copy() }
		else
			stack

		enchantments.forEach {
			if (isBook)
				EnchantedBookItem.addEnchantment(resultStack, it)
			else
				resultStack.addEnchantment(it.enchantment, it.enchantmentLevel)
		}

		player.run {
			setHeldItem(Hand.MAIN_HAND, resultStack)
			addStat(Stats.ENCHANT_ITEM)
			world.playSound(
				null,
				position,
				SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE,
				SoundCategory.PLAYERS,
				1F,
				rand.nextFloat() * 0.1F + 0.9F
			)
			if (this is ServerPlayerEntity)
				CriteriaTriggers.ENCHANTED_ITEM.trigger(this, resultStack, enchantLevel)
		}
		return true
	}
}
