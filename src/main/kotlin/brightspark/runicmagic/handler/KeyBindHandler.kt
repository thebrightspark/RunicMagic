package brightspark.runicmagic.handler

import brightspark.runicmagic.gui.SpellSelectScreen
import net.minecraft.client.Minecraft
import net.minecraft.client.settings.KeyBinding
import net.minecraft.client.util.InputMappings
import net.minecraftforge.client.event.InputEvent
import net.minecraftforge.client.settings.KeyConflictContext
import net.minecraftforge.fml.client.registry.ClientRegistry
import org.lwjgl.glfw.GLFW

object KeyBindHandler {
	private val KEY_OPEN_SPELL_MENU = KeyBinding(
		"key.runicmagic.spellselect",
		KeyConflictContext.IN_GAME,
		InputMappings.Type.KEYSYM,
		GLFW.GLFW_KEY_M,
		"key.runicmagic.category"
	)

	private fun regKeybinds(vararg keybindings: KeyBinding) =
		keybindings.forEach { ClientRegistry.registerKeyBinding(it) }

	fun register() = regKeybinds(KEY_OPEN_SPELL_MENU)

	fun onKey() {
		if (KEY_OPEN_SPELL_MENU.isPressed) {
			Minecraft.getInstance().run { displayGuiScreen(SpellSelectScreen(player!!)) }
		}
	}
}
