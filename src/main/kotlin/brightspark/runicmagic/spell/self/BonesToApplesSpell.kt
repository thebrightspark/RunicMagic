package brightspark.runicmagic.spell.self

import brightspark.runicmagic.init.RMParticles
import brightspark.runicmagic.model.SpellCastData
import brightspark.runicmagic.particle.ColouredParticleData
import brightspark.runicmagic.util.addParticle
import brightspark.runicmagic.util.onClient
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.world.World
import java.awt.Color

// https://runescape.wiki/w/Bones_to_Bananas
class BonesToApplesSpell(props: Properties) : SelfBaseSpell(props) {
	override fun canCast(player: PlayerEntity): Boolean = !hasPlayerMoved(player) && playerHasItem(player, Items.BONE)

	override fun updateCasting(world: World, player: PlayerEntity, progress: Int): Boolean {
		world.onClient {
			val look = player.lookVec
			val pos = player.positionVec.add(look.x, player.eyeHeight * 0.85, look.z)
			val progressFloat = progress.toFloat() / castTime.toFloat()
			val invProgressFloat = 1F - progressFloat
			val colour = Color(1F, invProgressFloat, invProgressFloat)

			repeat(10) {
				val vec = randVector(rand).scale(rand.nextFloat() * 0.5)
				addParticle(ColouredParticleData(RMParticles.SINGLE_MOVING, colour), pos.add(vec))
			}
		}
		return hasPlayerMoved(player)
	}

	override fun execute(player: PlayerEntity, data: SpellCastData): Boolean {
		var success = false
		player.inventory.mainInventory.let {
			it.forEachIndexed { i, stack ->
				if (!stack.isEmpty && stack.item == Items.BONE) {
					it[i] = ItemStack(Items.APPLE, stack.count)
					success = true
				}
			}
		}
		return success
	}
}
