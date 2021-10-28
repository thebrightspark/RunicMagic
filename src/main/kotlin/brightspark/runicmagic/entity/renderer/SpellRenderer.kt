package brightspark.runicmagic.entity.renderer

import brightspark.runicmagic.entity.SpellEntity
import com.mojang.blaze3d.matrix.MatrixStack
import net.minecraft.client.renderer.IRenderTypeBuffer
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererManager
import net.minecraft.util.ResourceLocation

class SpellRenderer(renderManager: EntityRendererManager) : EntityRenderer<SpellEntity>(renderManager) {
	override fun getEntityTexture(entity: SpellEntity): ResourceLocation? = null

	override fun render(
		entityIn: SpellEntity,
		entityYaw: Float,
		partialTicks: Float,
		matrixStackIn: MatrixStack,
		bufferIn: IRenderTypeBuffer,
		packedLightIn: Int
	) = Unit
}
