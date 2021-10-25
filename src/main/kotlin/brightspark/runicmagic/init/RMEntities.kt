package brightspark.runicmagic.init

import brightspark.runicmagic.RunicMagic
import brightspark.runicmagic.entity.SpellEntity
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityClassification
import net.minecraft.entity.EntityType
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World
import net.minecraftforge.event.RegistryEvent
import thedarkcolour.kotlinforforge.forge.objectHolder

object RMEntities {
	// TODO: Create renderer!
	val SPELL: EntityType<SpellEntity> by objectHolder("spell")

	fun register(event: RegistryEvent.Register<EntityType<*>>) = event.registry.registerAll(
		entity("spell", ::SpellEntity)
	)

	private fun <T : Entity> entity(name: String, factory: (EntityType<*>, World) -> T): EntityType<T> {
		val regName = ResourceLocation(RunicMagic.MOD_ID, name)
		return EntityType.Builder.create(factory, EntityClassification.MISC)
			.build(regName.toString())
			.apply { this.setRegistryName(regName) }
	}
}
