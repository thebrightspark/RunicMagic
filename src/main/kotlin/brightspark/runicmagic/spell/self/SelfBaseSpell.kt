package brightspark.runicmagic.spell.self

import brightspark.runicmagic.spell.Spell
import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.RayTraceContext
import net.minecraft.util.math.RayTraceResult
import net.minecraftforge.common.ForgeMod

abstract class SelfBaseSpell(props: Properties) : Spell(props) {
	protected fun getBlockLookingAt(player: PlayerEntity): BlockState? {
		val world = player.world
		val eyePos = player.getEyePosition(1F)
		val endPos = eyePos.add(player.lookVec.scale(player.getAttributeValue(ForgeMod.REACH_DISTANCE.get())))
		val raytrace = world.rayTraceBlocks(
			RayTraceContext(
				eyePos,
				endPos,
				RayTraceContext.BlockMode.VISUAL,
				RayTraceContext.FluidMode.NONE,
				player
			)
		)
		return if (raytrace.type == RayTraceResult.Type.BLOCK) world.getBlockState(raytrace.pos) else null
	}
}
