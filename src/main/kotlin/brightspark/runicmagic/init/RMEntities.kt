package brightspark.runicmagic.init

import brightspark.runicmagic.RunicMagic
import brightspark.runicmagic.entity.SpellEntity
import brightspark.runicmagic.entity.renderer.SpellRenderer
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererManager
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityClassification
import net.minecraft.entity.EntityType
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.client.registry.RenderingRegistry
import thedarkcolour.kotlinforforge.forge.objectHolder

object RMEntities {
	val SPELL: EntityType<SpellEntity> by objectHolder("spell")

	fun register(event: RegistryEvent.Register<EntityType<*>>) = event.registry.registerAll(
		entity("spell", ::SpellEntity) {
			size(0.5F, 0.5F).trackingRange(64).updateInterval(5).setShouldReceiveVelocityUpdates(true)
		}
	)

	fun registerRenderers() {
		renderer(SPELL, ::SpellRenderer)
	}

	private fun <T : Entity> entity(
		name: String,
		factory: (EntityType<*>, World) -> T,
		builderFunc: EntityType.Builder<T>.() -> Unit
	): EntityType<T> {
		val regName = ResourceLocation(RunicMagic.MOD_ID, name)
		return EntityType.Builder.create(factory, EntityClassification.MISC)
			.apply(builderFunc)
			.build(regName.toString())
			.apply { this.setRegistryName(regName) }
	}

	private fun <T : Entity> renderer(
		entityType: EntityType<T>,
		factory: (EntityRendererManager) -> EntityRenderer<T>
	) = RenderingRegistry.registerEntityRenderingHandler(entityType, factory)
}
