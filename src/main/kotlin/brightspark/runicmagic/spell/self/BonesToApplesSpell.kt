package brightspark.runicmagic.spell.self

import brightspark.runicmagic.model.SpellCastData
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items

// TODO: Bones to apples animation?
// https://runescape.wiki/w/Bones_to_Bananas
class BonesToApplesSpell(props: Properties) : SelfBaseSpell(props) {
	override fun canCast(player: PlayerEntity): Boolean = playerHasItem(player, Items.BONE)

	override fun execute(player: PlayerEntity, data: SpellCastData): Boolean {
		var success = false
		player.inventory.mainInventory.let {
			it.forEachIndexed { i, stack ->
				if (!stack.isEmpty && stack.item == Items.BONE) {
					it[i] = ItemStack(Items.BONE, stack.count)
					success = true
				}
			}
		}
		return success
	}
}
