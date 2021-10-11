package brightspark.runicmagic.spell.self

import brightspark.runicmagic.block.ObeliskBlock
import brightspark.runicmagic.init.RMItems
import brightspark.runicmagic.item.RuneItem
import brightspark.runicmagic.model.SpellCastData
import brightspark.runicmagic.util.RMUtils
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.world.World

/*
https://runescape.wiki/w/Charge_Air_Orb
https://runescape.wiki/w/Charge_Earth_Orb
https://runescape.wiki/w/Charge_Fire_Orb
https://runescape.wiki/w/Charge_Water_Orb
 */
class ChargeOrbSpell(private val orbItem: RuneItem, props: Properties) : SelfBaseSpell(props) {
	override fun canCast(player: PlayerEntity): Boolean =
		RMUtils.isHoldingItem(player, RMItems.ORB_NONE) && isLookingAtValidBlock(player)

	override fun updateCasting(world: World, player: PlayerEntity, progress: Int): Boolean = canCast(player)

	override fun execute(player: ServerPlayerEntity, data: SpellCastData): Boolean {
		val held = RMUtils.findHeldItem(player, RMItems.ORB_NONE) ?: return false
		if (!isLookingAtValidBlock(player)) return false
		held.second.shrink(1)
		val orbStack = ItemStack(orbItem)
		if (!player.addItemStackToInventory(orbStack))
			player.entityDropItem(orbStack)
		return true
	}

	private fun isLookingAtValidBlock(player: PlayerEntity): Boolean = getBlockLookingAt(player)
		?.let { it.block is ObeliskBlock && (it.block as ObeliskBlock).runeType == orbItem.runeType }
		?: false
}
