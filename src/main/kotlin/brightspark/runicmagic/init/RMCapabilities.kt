package brightspark.runicmagic.init

import brightspark.runicmagic.capability.*
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityInject
import net.minecraftforge.common.capabilities.CapabilityManager
import net.minecraftforge.event.AttachCapabilitiesEvent
import net.minecraftforge.event.entity.player.PlayerEvent

object RMCapabilities {
	@set:CapabilityInject(LevelCap::class)
	@JvmStatic
	lateinit var LEVEL: Capability<LevelCap>

	@set:CapabilityInject(SpellCap::class)
	@JvmStatic
	lateinit var SPELLS: Capability<SpellCap>

	private val capabilities: MutableSet<CapData> = mutableSetOf()

	fun register() {
		regCap(LevelCap.RL, ::LEVEL, LevelCap::Impl)
		regCap(SpellCap.RL, ::SPELLS, SpellCap::Impl)
	}

	private fun copyCapData(oldPlayer: PlayerEntity, newPlayer: PlayerEntity) = capabilities.forEach { cap ->
		oldPlayer.getCapability(cap.capability).ifPresent { oldCap ->
			newPlayer.getCapability(cap.capability).ifPresent { newCap ->
				newCap.deserializeNBT(oldCap.serializeNBT())
			}
		}
	}

	private fun sendCapData(player: PlayerEntity) {
		if (player is ServerPlayerEntity)
			capabilities.forEach { cap -> player.getCapability(cap.capability).ifPresent { it.dataChanged(player) } }
	}

	/*
	 * ----------------
	 *  EVENT HANDLERS
	 * ----------------
	 */

	fun attach(event: AttachCapabilitiesEvent<Entity>) {
		if (event.`object` !is PlayerEntity)
			return

		capabilities.forEach {
			if (!event.capabilities.containsKey(it.key))
				event.addCapability(it.key, it.providerSupplier())
		}
	}

	fun playerClone(event: PlayerEvent.Clone) {
		if (event.isWasDeath && event.player is ServerPlayerEntity)
			copyCapData(event.original, event.player)
	}

	fun playerLoggedIn(event: PlayerEvent.PlayerLoggedInEvent) = sendCapData(event.player)

	fun playerRespawn(event: PlayerEvent.PlayerRespawnEvent) = sendCapData(event.player)

	fun playerDimChanged(event: PlayerEvent.PlayerChangedDimensionEvent) = sendCapData(event.player)

	private inline fun <reified C : RMCap> regCap(
		key: ResourceLocation,
		noinline capSupplier: () -> Capability<C>,
		noinline capInstanceSupplier: () -> C
	) {
		CapabilityManager.INSTANCE.register(C::class.java, DelegatingCapabilityStorage<C>(), capInstanceSupplier)
		capabilities += CapData(key, capSupplier) { RMCapProvider(capSupplier, capInstanceSupplier) }
	}

	private data class CapData(
		val key: ResourceLocation,
		private val capSupplier: () -> Capability<out RMCap>,
		val providerSupplier: () -> RMCapProvider<out RMCap>
	) {
		val capability: Capability<out RMCap> by lazy(capSupplier)
	}
}
