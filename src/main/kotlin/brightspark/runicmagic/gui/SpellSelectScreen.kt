package brightspark.runicmagic.gui

import brightspark.runicmagic.RunicMagic
import brightspark.runicmagic.init.RMCapabilities
import brightspark.runicmagic.init.RMSpells
import brightspark.runicmagic.item.RuneItem
import brightspark.runicmagic.item.StaffItem
import brightspark.runicmagic.message.SpellSelectMessage
import brightspark.runicmagic.model.RuneType
import brightspark.runicmagic.spell.Spell
import brightspark.runicmagic.util.RMUtils
import brightspark.runicmagic.util.appendTranslation
import com.mojang.blaze3d.matrix.MatrixStack
import net.minecraft.client.Minecraft
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.StringTextComponent
import net.minecraft.util.text.TextFormatting
import net.minecraft.util.text.TranslationTextComponent

class SpellSelectScreen(private val player: PlayerEntity) : RMScreen("Spell Select", "spell_select", 196, 196) {
	companion object {
		private const val BUTTON_SIZE = 18
		private const val BUTTON_SPACING = BUTTON_SIZE + 4
		private const val BUTTON_START_POS = 12
		private const val BUTTON_GRID_SIZE = 8
		private const val TAB = "  "
	}

	private val spellCap = player.getCapability(RMCapabilities.SPELLS).resolve().get()
	private val levelCap = player.getCapability(RMCapabilities.LEVEL).resolve().get()
	private val runesInInv = mutableMapOf<RuneType, Int>()

	override fun init() {
		super.init()
		updateCachedRunes()

		// Add buttons
		val selectedSpell = spellCap.getSelectedSpell()
		RMSpells.getAllSorted().forEachIndexed { i, spell ->
			addButton(
				SpellButton(
					BUTTON_START_POS + (i * BUTTON_SPACING - (i / BUTTON_GRID_SIZE * (BUTTON_GRID_SIZE * BUTTON_SPACING))),
					BUTTON_START_POS + (i / BUTTON_GRID_SIZE * BUTTON_SPACING),
					spell,
					spell == selectedSpell
				).apply { active = levelCap.getLevel() >= spell.level && hasRunes(spell) }
			)
		}
	}

	private fun updateButtons() {
		val selectedSpell = spellCap.getSelectedSpell()
		buttons.forEach {
			if (it !is SpellButton) return@forEach
			it.run {
				active = levelCap.getLevel() >= spell.level && hasRunes(spell)
				selected = spell == selectedSpell
			}
		}
	}

	private fun updateCachedRunes() {
		runesInInv.clear()
		player.inventory.mainInventory.forEach { stack ->
			if (stack.isEmpty || stack.item !is RuneItem) return@forEach
			runesInInv.compute((stack.item as RuneItem).runeType) { _, count ->
				count?.let { it + stack.count } ?: stack.count
			}
		}
		RMUtils.findHeldItem(player) { it is StaffItem }?.let {
			runesInInv.put(StaffItem.getRuneType(it.second), -1)
		}
	}

	private fun hasRunes(spell: Spell): Boolean =
		spell.runeCost.all { (type, amount) -> runesInInv[type]?.let { it < 0 || it >= amount } ?: false }

	private fun selectSpell(button: SpellButton) = RunicMagic.NETWORK.sendToServer(SpellSelectMessage(button.spell))

	private fun spellTooltip(button: SpellButton): List<ITextComponent> = mutableListOf<ITextComponent>().apply {
		val spell = button.spell

		val level = spell.level
		val levelColour = if (levelCap.getLevel() >= level) TextFormatting.LIGHT_PURPLE else TextFormatting.DARK_PURPLE
		add(
			StringTextComponent("[$level] ").mergeStyle(levelColour)
				.appendSibling(TranslationTextComponent(spell.unlocName).mergeStyle(spell.spellType.getMagicTypeColour()))
		)
		add(TranslationTextComponent(spell.spellType.getTranslation()).mergeStyle(TextFormatting.GRAY))

		if (!spell.selectable)
			add(StringTextComponent("Will cast on click"))

		add(StringTextComponent(""))

		if (spell.cooldown > 0)
			add(StringTextComponent("Cooldown: ${RMUtils.ticksToSecondsString(spell.cooldown.toLong())}"))
		spellCap.getCooldowns(minecraft!!.world!!)[spell]?.let {
			add(StringTextComponent("Currently on cooldown for ${RMUtils.ticksToSecondsString(it)}"))
		}

		add(StringTextComponent("Cost:"))
		val cost = spell.runeCost
		if (cost.isEmpty())
			add(StringTextComponent("${TAB}None"))
		else
			cost.entries.sortedBy { it.key }.forEach { (runeType, amount) ->
				val inInv = runesInInv.getOrDefault(runeType, 0)
				add(
					StringTextComponent("${TAB}${if (inInv < 0) "∞" else inInv}/${amount} ")
						.mergeStyle(if (inInv < 0 || inInv >= amount) TextFormatting.GREEN else TextFormatting.RED)
						.appendTranslation(runeType.unlocName)
				)
			}
	}

	fun onSpellCapChanged() {
		updateCachedRunes()
		updateButtons()
	}

	private inner class SpellButton(x: Int, y: Int, val spell: Spell, var selected: Boolean) : RMButton(
		x,
		y,
		BUTTON_SIZE,
		BUTTON_SIZE,
		238,
		0,
		{ selectSpell(it as SpellButton) },
		{ spellTooltip(it as SpellButton) }
	) {
		override fun getYImage(isHovered: Boolean): Int = when {
			selected -> 3
			!active -> 0
			isHovered -> 2
			else -> 1
		}

		override fun renderBg(matrixStack: MatrixStack, minecraft: Minecraft, mouseX: Int, mouseY: Int) {
			minecraft.textureManager.bindTexture(spell.iconRL)
			blit(matrixStack, x + 1, y + 1, 0, 0, 16, 16)
		}
	}
}
