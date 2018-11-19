package brightspark.runicmagic.entity.render;

import brightspark.runicmagic.entity.EntityHelixProjectile;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;

import javax.annotation.Nullable;

public class RenderSpellProjectile extends Render
{
	public static final Factory FACTORY = new Factory();

	protected RenderSpellProjectile(RenderManager renderManager)
	{
		super(renderManager);
	}

	@Override
	public void doRender(Entity entity, double x, double y, double z, float entityYaw, float partialTicks)
	{
		//Render nothing
	}

	@Nullable
	@Override
	protected ResourceLocation getEntityTexture(Entity entity)
	{
		return null;
	}

	private static class Factory implements IRenderFactory<EntityHelixProjectile>
	{
		@Override
		public Render createRenderFor(RenderManager manager)
		{
			return new RenderSpellProjectile(manager);
		}
	}
}
