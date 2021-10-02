package brightspark.runicmagic.util

import net.minecraft.util.IReorderingProcessor
import net.minecraft.util.text.IFormattableTextComponent
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.Style

object EmptyTextComponent : IFormattableTextComponent {
	private const val EMPTY_STRING = ""

	override fun getStyle(): Style = Style.EMPTY

	override fun getUnformattedComponentText(): String = EMPTY_STRING

	override fun getSiblings(): MutableList<ITextComponent> = mutableListOf()

	override fun copyRaw(): IFormattableTextComponent = this

	override fun deepCopy(): IFormattableTextComponent = this

	override fun func_241878_f(): IReorderingProcessor = IReorderingProcessor.field_242232_a

	override fun setStyle(style: Style): IFormattableTextComponent = this

	override fun appendSibling(sibling: ITextComponent): IFormattableTextComponent = this
}
