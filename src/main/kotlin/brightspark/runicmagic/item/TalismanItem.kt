package brightspark.runicmagic.item

import brightspark.runicmagic.model.RuneType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.world.World

class TalismanItem(runeType: RuneType, props: Properties) : RuneItem(runeType, props) {
	override fun onItemRightClick(world: World, player: PlayerEntity, hand: Hand): ActionResult<ItemStack> {
		// TODO: Tell player direction to closest rune alter for this talisman type
		return super.onItemRightClick(world, player, hand)
	}
}
