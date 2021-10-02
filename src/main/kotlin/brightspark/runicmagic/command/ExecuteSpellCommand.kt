package brightspark.runicmagic.command

import brightspark.runicmagic.command.ExecuteSpellCommand.doSpell
import brightspark.runicmagic.item.StaffItem
import brightspark.runicmagic.model.RuneType
import brightspark.runicmagic.model.SpellCastData
import brightspark.runicmagic.spell.SpellHandler
import brightspark.runicmagic.util.RMUtils
import brightspark.runicmagic.util.thenArgument
import brightspark.runicmagic.util.thenLiteral
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource
import net.minecraft.util.text.StringTextComponent

object ExecuteSpellCommand : AbstractCommand(
	"spell",
	{
		thenArgument("spell", SpellArgType.spell()) {
			executes { doSpell(it, false) }
		}
		thenLiteral("cast") {
			thenArgument("spell", SpellArgType.spell()) {
				executes { doSpell(it, true) }
			}
		}
	}
) {
	fun doSpell(ctx: CommandContext<CommandSource>, cast: Boolean): Int {
		val source = ctx.source
		val player = source.asPlayer()
		val spell = SpellArgType.getSpell(ctx, "spell")
		val staff = RMUtils.findHeldItem(player) { it.item is StaffItem }
		val runeType = staff?.let { (it.second.item as StaffItem).runeType } ?: RuneType.NONE
		val castData = SpellCastData(99, 0F, runeType)

		val spellId = spell.registryName
		if (cast) {
			SpellHandler.addSpellCast(player, spell, castData)
			source.sendFeedback(StringTextComponent("Started casting spell $spellId"), false)
		} else {
			if (spell.execute(player, castData))
				source.sendFeedback(StringTextComponent("Executed spell $spellId"), false)
			else {
				source.sendFeedback(StringTextComponent("Failed to execute spell $spell"), false)
				return 0
			}
		}
		return 1
	}
}
