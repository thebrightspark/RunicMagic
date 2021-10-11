package brightspark.runicmagic.command

import brightspark.runicmagic.capability.LevelManager
import brightspark.runicmagic.command.SetLevelCommand.setLevel
import brightspark.runicmagic.init.RMCapabilities
import brightspark.runicmagic.util.thenArgument
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource
import net.minecraft.util.text.StringTextComponent

object SetLevelCommand : AbstractCommand(
	"level",
	{
		thenArgument("level", IntegerArgumentType.integer(0, LevelManager.MAX_LEVEL)) {
			executes { setLevel(it, IntegerArgumentType.getInteger(it, "level")) }
		}
	}
) {
	fun setLevel(ctx: CommandContext<CommandSource>, level: Int): Int {
		val source = ctx.source
		val player = source.asPlayer()
		player.getCapability(RMCapabilities.LEVEL).ifPresent {
			it.setLevel(player, level)
		}
		source.sendFeedback(StringTextComponent("Set level to $level"), false)
		return 1
	}
}
