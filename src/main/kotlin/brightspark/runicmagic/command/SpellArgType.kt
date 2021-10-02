package brightspark.runicmagic.command

import brightspark.runicmagic.init.RMSpells
import brightspark.runicmagic.spell.Spell
import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.command.ISuggestionProvider
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.StringTextComponent
import java.util.concurrent.CompletableFuture

class SpellArgType private constructor() : ArgumentType<Spell> {
	companion object {
		private val SPELL_INVALID: DynamicCommandExceptionType =
			DynamicCommandExceptionType { StringTextComponent("Unknown spell $it") }

		fun spell(): SpellArgType = SpellArgType()

		fun <S> getSpell(context: CommandContext<S>, name: String): Spell = context.getArgument(name, Spell::class.java)
	}

	override fun parse(reader: StringReader): Spell {
		val start = reader.cursor
		val spellId = ResourceLocation.read(reader)
		return RMSpells.REGISTRY.getValue(spellId) ?: run {
			reader.cursor = start
			throw SPELL_INVALID.create(spellId)
		}
	}

	override fun <S : Any?> listSuggestions(
		context: CommandContext<S>,
		builder: SuggestionsBuilder
	): CompletableFuture<Suggestions> = ISuggestionProvider.suggestIterable(RMSpells.REGISTRY_KEYS, builder)
}
