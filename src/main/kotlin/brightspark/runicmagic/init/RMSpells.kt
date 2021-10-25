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

// https://runescape.fandom.com/wiki/List_of_spells
object RMSpells {
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
		// FYI: Damage atm is calculated as Runescape spell damage / 20
		elementalSpell("air_strike", 1, 2.4F),

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

	private fun elementalSpell(name: String, level: Int, damage: Float = 0F): Spell =
		spell(name, ProjectileBaseSpell(elementalProps(level), RMEntities.SPELL, damage))

	private fun enchantSpell(name: String, enchantLevel: Int, props: Spell.Properties): Spell =
		spell(name, EnchantSpell(enchantLevel, props))

	private fun chargeOrbSpell(name: String, runeItem: Item, props: Spell.Properties): Spell =
		spell(name, ChargeOrbSpell(runeItem as RuneItem, props))

	fun get(regName: String): Spell? = REGISTRY.getValue(ResourceLocation(regName))

	fun get(regName: ResourceLocation): Spell? = REGISTRY.getValue(regName)

	fun getAllSorted(): List<Spell> = REGISTRY.values.sortedBy { it.level }
}
