package brightspark.runicmagic.gui

import brightspark.runicmagic.RunicMagic
import brightspark.runicmagic.util.EmptyTextComponent
import com.mojang.blaze3d.matrix.MatrixStack
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.button.Button
import net.minecraft.client.gui.widget.button.Button.ITooltip
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.MathHelper
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.StringTextComponent

open class RMScreen(
	title: String,
	imageName: String,
	protected val guiWidth: Int = 256,
	protected val guiHeight: Int = 256,
	protected val imageWidth: Int = 256,
	protected val imageHeight: Int = 256
) : Screen(StringTextComponent(title)) {
	protected val imageResLoc = ResourceLocation(RunicMagic.MOD_ID, "textures/gui/$imageName.png")
	protected var guiLeft = 0
	protected var guiTop = 0

	override fun init() {
		super.init()
		guiLeft = (width - guiWidth) / 2
		guiTop = (height - guiHeight) / 2
	}

	override fun render(matrixStack: MatrixStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
		RenderSystem.color3f(1F, 1F, 1F)
		renderBackground(matrixStack)
		minecraft!!.textureManager.bindTexture(imageResLoc)
		blit(matrixStack, guiLeft, guiTop, 0F, 0F, guiWidth, guiHeight, imageWidth, imageHeight)
		renderAfterBackground(matrixStack, mouseX, mouseY, partialTicks)
		super.render(matrixStack, mouseX, mouseY, partialTicks)
	}

	protected open fun renderAfterBackground(matrixStack: MatrixStack, mouseX: Int, mouseY: Int, partialTicks: Float) {}

	private fun renderOnTooltip(matrixStack: MatrixStack, mouseX: Int, mouseY: Int, tooltip: List<ITextComponent>) {
		if (tooltip.isNotEmpty())
			renderWrappedToolTip(matrixStack, tooltip, mouseX, mouseY, font)
	}

	protected open inner class RMButton(
		x: Int,
		y: Int,
		width: Int,
		height: Int,
		protected val iconX: Int,
		protected val iconY: Int,
		onPress: (Button) -> Unit,
		private val onTooltip: (Button) -> List<ITextComponent> = { emptyList() }
	) : Button(
		guiLeft + x,
		guiTop + y,
		width,
		height,
		EmptyTextComponent,
		onPress,
		ITooltip { button, matrixStack, mouseX, mouseY ->
			renderOnTooltip(matrixStack, mouseX, mouseY, onTooltip(button))
		}
	) {
		override fun render(matrixStack: MatrixStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
			isHovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height

			val mc = Minecraft.getInstance()
			mc.textureManager.bindTexture(imageResLoc)
			RenderSystem.color4f(1F, 1F, 1F, alpha)
			RenderSystem.enableBlend()
			RenderSystem.defaultBlendFunc()
			RenderSystem.enableDepthTest()

			// Render button image
			blit(
				matrixStack,
				x,
				y,
				iconX.toFloat(),
				iconY.toFloat() + (getYImage(isHovered()) * height),
				width,
				height,
				imageWidth,
				imageHeight
			)
			renderBg(matrixStack, mc, mouseX, mouseY)

			// Render text
			if (message != EmptyTextComponent) {
				drawCenteredString(
					matrixStack,
					mc.fontRenderer,
					message,
					x + width / 2,
					y + (height - 8) / 2,
					fgColor or MathHelper.ceil(alpha * 255.0f) shl 24
				)
			}

			// Render tooltip
			if (isHovered()) renderToolTip(matrixStack, mouseX, mouseY)
		}
	}
}
