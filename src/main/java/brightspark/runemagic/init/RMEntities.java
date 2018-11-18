package brightspark.runemagic.init;

import brightspark.runemagic.RuneMagic;
import brightspark.runemagic.entity.EntityHelixProjectile;
import brightspark.runemagic.entity.render.RenderSpellProjectile;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.registries.IForgeRegistry;

public class RMEntities
{
	private static int entityId = 0;

	private static <E extends Entity> EntityEntry createProjectile(Class<E> entityClass, String name)
	{
		return createEntity(entityClass, name, 5, true);
	}

	private static <E extends Entity> EntityEntry createEntity(Class<E> entityClass, String name, int updateFrequency, boolean sendVelocityUpdates)
	{
		return createBuilder(entityClass, name, updateFrequency, sendVelocityUpdates).build();
	}

	private static EntityEntryBuilder<Entity> createBuilder(Class<? extends Entity> entityClass, String name, int updateFrequency, boolean sendVelocityUpdates)
	{
		return EntityEntryBuilder.create()
			.entity(entityClass)
			.name(String.format("%s.%s", RuneMagic.MOD_ID, name))
			.id(new ResourceLocation(RuneMagic.MOD_ID, name), entityId++)
			.tracker(64, updateFrequency, sendVelocityUpdates);
	}

	private static <E extends Entity> void regRender(Class<E> entityClass, IRenderFactory<? super E> renderFactory)
	{
		RenderingRegistry.registerEntityRenderingHandler(entityClass, renderFactory);
	}

	public static void register(IForgeRegistry<EntityEntry> registry)
	{
		registry.registerAll(
			createProjectile(EntityHelixProjectile.class, "air_projectile")
		);
	}

	public static void registerRenders()
	{
		regRender(EntityHelixProjectile.class, RenderSpellProjectile.FACTORY);
	}
}
