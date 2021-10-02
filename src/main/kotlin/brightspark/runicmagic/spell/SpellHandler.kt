package brightspark.runicmagic.spell

import brightspark.runicmagic.RunicMagic
import brightspark.runicmagic.message.AddSpellCastingMessage
import brightspark.runicmagic.model.SpellCastData
import brightspark.runicmagic.util.RMUtils
import brightspark.runicmagic.util.sendToAll
import net.minecraft.client.Minecraft
import net.minecraft.entity.player.PlayerEntity
import net.minecraftforge.event.TickEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.server.ServerLifecycleHooks
import java.util.*

@Mod.EventBusSubscriber(modid = RunicMagic.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
object SpellHandler {
	private val CASTS = mutableMapOf<UUID, SpellCasting>()

	@SubscribeEvent
	fun onServerTick(event: TickEvent.ServerTickEvent) {
		if (event.phase != TickEvent.Phase.END) return
		val server = ServerLifecycleHooks.getCurrentServer()
		CASTS.entries.removeIf {
			val player = server.playerList.getPlayerByUUID(it.key)
			return@removeIf player == null || it.value.update(player.world, player, false)
		}
	}

	@SubscribeEvent
	fun onClientTick(event: TickEvent.ClientTickEvent) {
		if (event.phase != TickEvent.Phase.END) return
		val mc = Minecraft.getInstance()
		val world = mc.world ?: return
		val singlePlayer = mc.isSingleplayer
		CASTS.entries.removeIf {
			val player = world.getPlayerByUuid(it.key)
			return@removeIf player == null || it.value.update(world, player, singlePlayer)
		}
	}

	fun addSpellCast(player: PlayerEntity, spell: Spell, data: SpellCastData) {
		RunicMagic.LOG.info("Adding spell cast for $spell")
		val casting = CASTS.compute(player.uniqueID) { uuid, existing ->
			// Cancel existing spell casting
			existing?.spell?.onCastCancel(player)
			// Add new spell casting
			return@compute SpellCasting(spell, data)
		}
		// Notify all players if on a dedicated server
		if (RMUtils.isDedicatedServer)
			RunicMagic.NETWORK.sendToAll(AddSpellCastingMessage(player, casting!!))
	}

	fun removeSpellCast(playerUuid: UUID) {
		CASTS.remove(playerUuid)
	}
}
