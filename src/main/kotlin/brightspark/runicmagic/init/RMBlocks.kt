package brightspark.runicmagic.init

import brightspark.runicmagic.block.GatestoneBlock
import brightspark.runicmagic.block.ObeliskBlock
import brightspark.runicmagic.block.RuneOreBlock
import brightspark.runicmagic.model.RuneType
import brightspark.runicmagic.util.setRegName
import net.minecraft.block.AbstractBlock.Properties
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.RenderTypeLookup
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.common.ToolType
import net.minecraftforge.event.RegistryEvent
import thedarkcolour.kotlinforforge.forge.objectHolder

object RMBlocks {
	val BLOCKS: MutableList<Block> = mutableListOf()

	val GATESTONE: Block by objectHolder("gatestone")

	fun register(event: RegistryEvent.Register<Block>) = event.registry.registerAll(
		runeOreBlock("rune_ore"),
		*RuneType.ELEMENTAL_TYPES.map { obeliskBlock("obelisk_$it", it) }.toTypedArray(),
		gatestoneBlock("gatestone")
	)

	@OnlyIn(Dist.CLIENT)
	fun clientInit() {
		RenderTypeLookup.setRenderLayer(GATESTONE, RenderType.getCutout())
	}

	private fun rockProps(
		material: Material = Material.ROCK,
		harvestLevel: Int = 0,
		hardness: Float = 3F,
		resistance: Float = 3F
	): Properties = Properties.create(material)
		.setRequiresTool()
		.harvestTool(ToolType.PICKAXE)
		.harvestLevel(harvestLevel)
		.hardnessAndResistance(hardness, resistance)

	private fun unbreakableProps(): Properties = Properties.create(Material.ROCK)
		.hardnessAndResistance(-1F, 3600000F)
		.noDrops()

	private fun Block.setup(name: String): Block = this.setRegName(name).apply { BLOCKS += this }

	private fun runeOreBlock(name: String): Block = RuneOreBlock(rockProps()).setup(name)

	private fun obeliskBlock(name: String, runeType: RuneType): Block =
		ObeliskBlock(runeType, unbreakableProps()).setup(name)

	private fun gatestoneBlock(name: String): Block =
		GatestoneBlock(
			Properties.create(Material.GLASS)
				.doesNotBlockMovement()
				.noDrops()
				.zeroHardnessAndResistance()
		).setup(name)
}
