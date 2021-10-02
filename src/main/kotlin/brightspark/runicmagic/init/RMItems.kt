package brightspark.runicmagic.init

import brightspark.runicmagic.RunicMagic
import brightspark.runicmagic.item.RuneItem
import brightspark.runicmagic.item.StaffItem
import brightspark.runicmagic.item.TalismanItem
import brightspark.runicmagic.model.RuneType
import brightspark.runicmagic.model.StaffType
import brightspark.runicmagic.util.setRegName
import net.minecraft.block.Block
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraftforge.event.RegistryEvent
import thedarkcolour.kotlinforforge.forge.objectHolder

object RMItems {
	val RUNE_ESSENCE: Item by objectHolder("rune_essence")
	val PURE_ESSENCE: Item by objectHolder("pure_essence")

	val ORB_NONE: Item by objectHolder("orb_none")
	val ORB_AIR: Item by objectHolder("orb_air")
	val ORB_WATER: Item by objectHolder("orb_water")
	val ORB_EARTH: Item by objectHolder("orb_earth")
	val ORB_FIRE: Item by objectHolder("orb_fire")

	fun register(event: RegistryEvent.Register<Item>) = event.registry.registerAll(
		// Essences
		item("rune_essence"),
		item("pure_essence"),
		// Runes
		*RuneType.values().filter { it != RuneType.NONE }.map { runeItem("rune_$it", it) }.toTypedArray(),
		// Talismans
		*RuneType.values().filter { it.hasTalisman }.map { talismanItem(it) }.toTypedArray(),
		// Staves
		*StaffType.values().flatMap { staffType -> staffType.runeTypes.map { staffItem(staffType, it) } }.toTypedArray(),
		// Orbs
		runeItem("orb_none", RuneType.NONE, props().maxStackSize(1)),
		*RuneType.ELEMENTAL_TYPES.map { runeItem("orb_$it", it, props().maxStackSize(1)) }.toTypedArray(),

		// Item blocks
		*RMBlocks.BLOCKS.map { blockItem(it) }.toTypedArray()
	)

	private fun props(): Item.Properties = Item.Properties().apply { group(RunicMagic.GROUP) }

	private fun item(name: String, props: Item.Properties = props()): Item = Item(props).setRegName(name)

	private fun runeItem(name: String, runeType: RuneType, props: Item.Properties = props()): Item =
		RuneItem(runeType, props).setRegName(name)

	private fun talismanItem(runeType: RuneType): Item =
		TalismanItem(runeType, props().maxStackSize(1)).setRegName("talisman_$runeType")

	private fun staffItem(staffType: StaffType, runeType: RuneType): Item =
		StaffItem(staffType, runeType, props().maxStackSize(1)).setRegName("staff_${staffType}_$runeType")

	private fun blockItem(block: Block): Item =
		BlockItem(block, props()).setRegName(block.registryName!!.path)
}
