package brightspark.runicmagic.spell.self

import brightspark.runicmagic.init.RMParticles
import brightspark.runicmagic.model.SpellCastData
import brightspark.runicmagic.util.addParticle
import brightspark.runicmagic.util.onClient
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.fluid.Fluids
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.world.World
import net.minecraftforge.fluids.FluidAttributes
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.FluidUtil
import kotlin.math.min

class HumidifySpell(props: Properties) : SelfBaseSpell(props) {
	companion object {
		private val BUCKET = ItemStack(Items.BUCKET)
	}

	override fun canCast(player: PlayerEntity): Boolean = !hasPlayerMoved(player) && playerHasItem(player, Items.BUCKET)

	override fun updateCasting(world: World, player: PlayerEntity, progress: Int): Boolean {
		world.onClient {
			val pos = player.positionVec.add(0.0, 3.0, 0.0)

			// Clouds
			repeat(15) {
				addParticle(RMParticles.CLOUD, posOffset(world, pos, 1.5, 0.5, 1.5))
			}

			if (progress >= 20) {
				// Rain
				repeat(min(10 + (countItemsInPlayerInv(player, BUCKET) * 2), 200)) {
					addParticle(RMParticles.RAIN, posOffset(world, pos, 1.2, 0.3, 1.2))
				}
			}
		}
		return hasPlayerMoved(player)
	}

	override fun execute(player: ServerPlayerEntity, data: SpellCastData): Boolean {
		var bucketsFound = false
		val inv = player.inventory
		repeat(inv.sizeInventory) {
			val stack = inv.getStackInSlot(it)
			if (stack.item == Items.BUCKET) {
				val count = stack.count
				inv.setInventorySlotContents(it, createWaterBucket())
				if (count > 1)
					repeat(count - 1) { givePlayerStack(player, createWaterBucket()) }
				bucketsFound = true
			}
		}
		return bucketsFound
	}

	private fun createWaterBucket(): ItemStack =
		FluidUtil.getFilledBucket(FluidStack(Fluids.WATER, FluidAttributes.BUCKET_VOLUME))
}
