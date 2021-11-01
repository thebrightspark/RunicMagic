package brightspark.runicmagic.init

import brightspark.runicmagic.RunicMagic
import brightspark.runicmagic.item.RuneItem
import brightspark.runicmagic.model.RuneType.*
import brightspark.runicmagic.model.SpellType
import brightspark.runicmagic.spell.Spell
import brightspark.runicmagic.spell.projectile.ProjectileBaseSpell
import brightspark.runicmagic.spell.self.*
import brightspark.runicmagic.spell.teleport.GatestoneTeleportSpell
import brightspark.runicmagic.spell.teleport.HomeTeleportSpell
import brightspark.runicmagic.util.setRegName
import net.minecraft.item.Item
import net.minecraft.util.ResourceLocation
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.registries.IForgeRegistry
import net.minecraftforge.registries.RegistryBuilder
import java.awt.Color

// https://runescape.fandom.com/wiki/List_of_spells
object RMSpells {
	// TODO: Move this somewhere better?
	private val EARTH_COLOUR: Color = Color(0x00BB00)

	lateinit var REGISTRY: IForgeRegistry<Spell>

	val REGISTRY_KEYS: Iterable<ResourceLocation> by lazy { REGISTRY.keys.sorted() }

	fun createRegistry() {
		REGISTRY = RegistryBuilder<Spell>()
			.setName(ResourceLocation(RunicMagic.MOD_ID, "spells"))
			.setType(Spell::class.java)
			.disableSaving()
			.allowModification()
			.create()
	}

	fun register(event: RegistryEvent.Register<Spell>) = event.registry.registerAll(
		// Teleport
		spell("teleport_home", HomeTeleportSpell(teleportProps(SpellType.TELESELF, 40))),
		spell("teleport_gatestone", GatestoneTeleportSpell(teleportProps(SpellType.TELESELF, 32))),

		// Projectile Attack
		// FYI: Damage values are from Runescape - we scale it down in #elementalSpell
		elementalSpell("strike_air", 1, AIR.colour, 48),
		elementalSpell("strike_water", 5, WATER.colour, 48),
		elementalSpell("strike_earth", 9, EARTH_COLOUR, 86),
		elementalSpell("strike_fire", 13, FIRE.colour, 124),
		elementalSpell("bolt_air", 17, AIR.colour, 163),
		elementalSpell("bolt_water", 23, WATER.colour, 220),
		elementalSpell("bolt_earth", 29, EARTH_COLOUR, 278),
		elementalSpell("bolt_fire", 35, FIRE.colour, 336),
		elementalSpell("blast_air", 41, AIR.colour, 393),
		elementalSpell("blast_water", 47, WATER.colour, 451),
		elementalSpell("blast_earth", 53, EARTH_COLOUR, 508),
		elementalSpell("blast_fire", 59, FIRE.colour, 566),
		elementalSpell("wave_air", 62, AIR.colour, 595),
		elementalSpell("wave_water", 65, WATER.colour, 624),
		elementalSpell("wave_earth", 70, EARTH_COLOUR, 672),
		elementalSpell("wave_fire", 75, FIRE.colour, 720),
		elementalSpell("surge_air", 81, AIR.colour, 777),
		elementalSpell("surge_water", 85, WATER.colour, 816),
		elementalSpell("surge_earth", 90, EARTH_COLOUR, 864),
		elementalSpell("surge_fire", 95, FIRE.colour, 883),

		// Projectile Effect

		// Self
		enchantSpell("enchant_1", 5, enchantProps(7).addRuneCost(COSMIC to 1, WATER to 1)),
		enchantSpell("enchant_2", 10, enchantProps(27).addRuneCost(COSMIC to 1, AIR to 3)),
		enchantSpell("enchant_3", 15, enchantProps(49).addRuneCost(COSMIC to 1, FIRE to 5)),
		enchantSpell("enchant_4", 20, enchantProps(57).addRuneCost(COSMIC to 1, EARTH to 10)),
		enchantSpell("enchant_5", 25, enchantProps(68).addRuneCost(COSMIC to 1, EARTH to 15, WATER to 15)),
		enchantSpell("enchant_6", 30, enchantProps(87).addRuneCost(COSMIC to 1, EARTH to 20, FIRE to 20)),
		spell(
			"bones_to_apples",
			BonesToApplesSpell(
				alchemyProps(15).setCastTime(40).setCooldown(1200).addRuneCost(NATURE to 1, EARTH to 2, WATER to 2)
			)
		),
		spell(
			"humidify",
			HumidifySpell(alchemyProps(68).setCastTime(80).addRuneCost(ASTRAL to 1, FIRE to 1, WATER to 3))
		),
		spell(
			"gatestone_create",
			GatestoneCreateSpell(
				selfProps(SpellType.OTHER, 32).setCastTime(60).setCooldown(600).addRuneCost(COSMIC to 3)
			)
		),

		// Charge Orb
		chargeOrbSpell("charge_orb_water", RMItems.ORB_WATER, enchantProps(56).addRuneCost(COSMIC to 3, WATER to 30)),
		chargeOrbSpell("charge_orb_earth", RMItems.ORB_EARTH, enchantProps(60).addRuneCost(COSMIC to 3, EARTH to 30)),
		chargeOrbSpell("charge_orb_fire", RMItems.ORB_FIRE, enchantProps(63).addRuneCost(COSMIC to 3, FIRE to 30)),
		chargeOrbSpell("charge_orb_air", RMItems.ORB_AIR, enchantProps(66).addRuneCost(COSMIC to 3, AIR to 30))
	)

	private fun props(spellType: SpellType, level: Int): Spell.Properties = Spell.Properties(spellType, level)

	private fun teleportProps(spellType: SpellType, level: Int): Spell.Properties =
		props(spellType, level).setSelectable(false).setCooldown(600).setCastTime(200)

	// TODO: Add cast time to projectile spells for an animation
	private fun projectileProps(spellType: SpellType, level: Int): Spell.Properties =
		props(spellType, level).setSelectable(true).setCooldown(10)

	private fun elementalProps(level: Int): Spell.Properties = projectileProps(SpellType.ELEMENTAL, level)

	private fun selfProps(spellType: SpellType, level: Int): Spell.Properties =
		props(spellType, level).setSelectable(false)

	private fun alchemyProps(level: Int): Spell.Properties = selfProps(SpellType.ALCHEMY, level)

	private fun enchantProps(level: Int): Spell.Properties = selfProps(SpellType.ENCHANTMENT, level)

	private fun spell(name: String, spell: Spell): Spell = spell.setRegName(name)

	// FYI: Damage atm is calculated as Runescape spell damage / 25
	private fun elementalSpell(name: String, level: Int, colour: Color, damage: Int): Spell =
		spell(name, ProjectileBaseSpell(elementalProps(level), RMEntities.SPELL, colour, damage.toFloat() / 25F))

	private fun enchantSpell(name: String, enchantLevel: Int, props: Spell.Properties): Spell =
		spell(name, EnchantSpell(enchantLevel, props))

	private fun chargeOrbSpell(name: String, runeItem: Item, props: Spell.Properties): Spell =
		spell(name, ChargeOrbSpell(runeItem as RuneItem, props))

	fun get(regName: String): Spell? = REGISTRY.getValue(ResourceLocation(regName))

	fun get(regName: ResourceLocation): Spell? = REGISTRY.getValue(regName)

	fun getAllSorted(): List<Spell> = REGISTRY.values.sortedBy { it.level }
}
