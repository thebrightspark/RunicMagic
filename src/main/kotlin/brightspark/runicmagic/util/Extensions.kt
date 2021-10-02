package brightspark.runicmagic.util

import brightspark.runicmagic.RunicMagic
import brightspark.runicmagic.command.AbstractCommand
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.entity.Entity
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.particles.BasicParticleType
import net.minecraft.particles.IParticleData
import net.minecraft.particles.ParticleType
import net.minecraft.util.Util
import net.minecraft.util.math.vector.Vector3d
import net.minecraft.util.text.*
import net.minecraft.world.IWorldReader
import net.minecraft.world.World
import net.minecraftforge.fml.network.PacketDistributor
import net.minecraftforge.fml.network.simple.SimpleChannel
import net.minecraftforge.registries.ForgeRegistryEntry
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

/*
 * -------
 *  WORLD
 * -------
 */

/**
 * Runs the [op] if this [World] is on the client side
 */
inline infix fun <W : IWorldReader, R> W.onClient(op: W.() -> R): R? = if (this.isRemote) op(this) else null

/**
 * Runs the [op] if this [World] is on the server side
 */
inline infix fun <W : IWorldReader, R> W.onServer(op: W.() -> R): R? = if (!this.isRemote) op(this) else null

fun World.addParticle(particleType: ParticleType<out IParticleData>, pos: Vector3d, vel: Vector3d = Vector3d.ZERO) =
	this.addParticle(particleType as BasicParticleType, pos.x, pos.y, pos.z, vel.x, vel.y, vel.z)

/*
 * ----------------
 *  SIMPLE CHANNEL
 * ----------------
 */

/**
 * Registers a [Message] with the given [index]
 */
@Suppress("INACCESSIBLE_TYPE")
fun <T : Message> SimpleChannel.registerMessage(messageClass: KClass<T>, index: Int) {
	this.registerMessage(
		index,
		messageClass.java,
		{ message, buffer -> message.encode(buffer) },
		{ message -> messageClass.createInstance().apply { decode(message) } },
		{ message, context -> message.consume(context) }
	)
}

/**
 * Sends the [message] to the [player] client
 */
fun SimpleChannel.sendToPlayer(message: Message, player: ServerPlayerEntity): Unit =
	this.send(PacketDistributor.PLAYER.with { player }, message)

/**
 * Sends the [message] to all clients
 */
fun SimpleChannel.sendToAll(message: Message): Unit = this.send(PacketDistributor.ALL.noArg(), message)

/*
 * -----------------
 *  TEXT COMPONENTS
 * -----------------
 */

/**
 * Adds a new [StringTextComponent] to the end of the sibling list, with the specified [obj]. Same as calling
 * [IFormattableTextComponent.appendString] and giving it the result of calling [Any.toString] on [obj].
 */
fun IFormattableTextComponent.appendString(obj: Any): IFormattableTextComponent = this.appendString(obj.toString())

/**
 * Adds a new [TranslationTextComponent] to the end of the sibling list, with the specified translation key and
 * arguments. Same as calling [IFormattableTextComponent.appendSibling] with a new [TranslationTextComponent].
 */
fun IFormattableTextComponent.appendTranslation(translationKey: String, vararg args: Any): IFormattableTextComponent =
	this.appendSibling(TranslationTextComponent(translationKey, args))

/**
 * Adds a new [StringTextComponent] to the end of the sibling list, with the specified [text] and [style].
 * Same as calling [IFormattableTextComponent.appendSibling] with a new [StringTextComponent] and calling
 * [IFormattableTextComponent.setStyle] on that.
 */
fun IFormattableTextComponent.appendStyledString(text: String, style: Style): IFormattableTextComponent =
	this.appendSibling(StringTextComponent(text).setStyle(style))

/**
 * Adds a new [StringTextComponent] to the end of the sibling list, with the specified [text] and [styles].
 * Same as calling [IFormattableTextComponent.appendSibling] with a new [StringTextComponent] and calling
 * [IFormattableTextComponent.mergeStyle] on that.
 */
fun IFormattableTextComponent.appendStyledString(
	text: String,
	vararg styles: TextFormatting
): IFormattableTextComponent =
	this.appendSibling(StringTextComponent(text).mergeStyle(*styles))

/*
 * ----------
 *  COMMANDS
 * ----------
 */

/**
 * Registers all [commands] to this [CommandDispatcher]
 */
fun CommandDispatcher<CommandSource>.register(vararg commands: AbstractCommand): Unit =
	commands.forEach { this.register(it.builder) }

fun <T : ArgumentBuilder<CommandSource, T>> T.thenLiteral(
	name: String,
	block: LiteralArgumentBuilder<CommandSource>.() -> Unit
): T = this.then(Commands.literal(name).apply(block))

fun <T : ArgumentBuilder<CommandSource, T>, ARG> T.thenArgument(
	argumentName: String,
	argument: ArgumentType<ARG>,
	block: RequiredArgumentBuilder<CommandSource, ARG>.() -> Unit
): T = this.then(Commands.argument(argumentName, argument).apply(block))

fun <T : ArgumentBuilder<CommandSource, T>> T.thenCommand(command: AbstractCommand, block: T.() -> Unit = {}): T =
	this.then(command.builder).apply(block)

/*
 * ------
 *  MISC
 * ------
 */

fun <T : ForgeRegistryEntry<T>> T.setRegName(name: String): T =
	this.setRegistryName(RunicMagic.MOD_ID, name)

/**
 * Overload for [Entity.sendMessage] which uses [Util.DUMMY_UUID] instead of an explicit UUID
 */
fun Entity.sendMessage(textComponent: ITextComponent): Unit = this.sendMessage(textComponent, Util.DUMMY_UUID)
