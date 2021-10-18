package brightspark.runicmagic

import brightspark.runicmagic.command.ExecuteSpellCommand
import brightspark.runicmagic.command.SetLevelCommand
import brightspark.runicmagic.handler.KeyBindHandler
import brightspark.runicmagic.init.*
import brightspark.runicmagic.message.*
import brightspark.runicmagic.util.register
import brightspark.runicmagic.util.registerMessage
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.event.InputEvent
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent
import net.minecraftforge.event.RegisterCommandsEvent
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.fml.network.NetworkRegistry
import net.minecraftforge.fml.network.simple.SimpleChannel
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import thedarkcolour.kotlinforforge.forge.FORGE_BUS
import thedarkcolour.kotlinforforge.forge.MOD_BUS

/*
Old 1.12.2 code:
https://github.com/thebrightspark/RunicMagic/tree/442ba0d5c0a7e98b611ed6c01a113704e6be4730
 */
@Mod(RunicMagic.MOD_ID)
object RunicMagic {
	internal const val MOD_ID = "runicmagic"

	val LOG: Logger = LogManager.getLogger(RunicMagic::class)

	val GROUP = object : ItemGroup(MOD_ID) {
		override fun createIcon(): ItemStack = ItemStack(Items.REDSTONE_TORCH)
	}

	private const val NETWORK_PROTOCOL = "1";
	val NETWORK: SimpleChannel = NetworkRegistry.newSimpleChannel(
		ResourceLocation(MOD_ID, "main"),
		{ "1" },
		NETWORK_PROTOCOL::equals,
		NETWORK_PROTOCOL::equals
	).apply {
		listOf(
			AddSpellCastingMessage::class,
			RemoveSpellCastingMessage::class,
			SpellSelectMessage::class,
			SyncLevelCapMessage::class,
			SyncSpellsCapMessage::class
		).forEachIndexed { index, kClass -> registerMessage(kClass, index) }
	}

	init {
		MOD_BUS.apply {
			addListener<FMLClientSetupEvent> {
				RMBlocks.clientInit(it)
				KeyBindHandler.register()
			}
			addListener<FMLCommonSetupEvent> { RMCapabilities.register() }
			addListener<ParticleFactoryRegisterEvent> { RMParticles.registerFactories() }
			addListener<RegistryEvent.NewRegistry> { RMSpells.createRegistry() }
			addGenericListener(RMItems::register)
			addGenericListener(RMBlocks::register)
			addGenericListener(RMTileEntities::register)
			addGenericListener(RMParticles::registerTypes)
			addGenericListener(RMSpells::register)
		}
		FORGE_BUS.apply {
			addListener<InputEvent.KeyInputEvent> { KeyBindHandler.onKey() }
			addListener(RMCapabilities::attach)
			addListener(RMCapabilities::playerClone)
			addListener(RMCapabilities::playerLoggedIn)
			addListener(RMCapabilities::playerRespawn)
			addListener(RMCapabilities::playerDimChanged)
			addListener<RegisterCommandsEvent> { it.dispatcher.register(ExecuteSpellCommand, SetLevelCommand) }
		}
	}
}
