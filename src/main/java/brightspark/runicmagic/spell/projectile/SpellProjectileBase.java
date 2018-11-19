package brightspark.runicmagic.spell.projectile;

import brightspark.runicmagic.entity.EntitySpellProjectile;
import brightspark.runicmagic.spell.Spell;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import java.util.function.Function;

public class SpellProjectileBase extends Spell
{
	private final Function<EntityPlayer, EntitySpellProjectile> entityFactory;

	public SpellProjectileBase(String name, Function<EntityPlayer, EntitySpellProjectile> entityFactory)
	{
		super(name);
		this.entityFactory = entityFactory;
		cooldown = 10; //0.5s
	}

	@Override
	public boolean execute(EntityPlayer player)
	{
		if(player.world.isRemote)
			return false;
		World world = player.world;
		EntitySpellProjectile entity = entityFactory.apply(player);
		return world.spawnEntity(entity);
	}
}
