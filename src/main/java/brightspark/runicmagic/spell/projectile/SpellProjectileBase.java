package brightspark.runicmagic.spell.projectile;

import brightspark.runicmagic.entity.EntityHelixProjectile;
import brightspark.runicmagic.entity.EntitySpellProjectile;
import brightspark.runicmagic.enums.RuneType;
import brightspark.runicmagic.spell.Spell;
import brightspark.runicmagic.util.SpellCastData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import java.util.function.Function;

public class SpellProjectileBase extends Spell
{
	private final Function<EntityPlayer, EntitySpellProjectile> entityFactory;

	public SpellProjectileBase(String name, RuneType runeType)
	{
		super(name);
		entityFactory = player -> new EntityHelixProjectile(player, runeType);
		cooldown = 10;
	}

	public SpellProjectileBase(String name, Function<EntityPlayer, EntitySpellProjectile> entityFactory)
	{
		super(name);
		this.entityFactory = entityFactory;
		cooldown = 10; //0.5s
	}

	protected float getAttackDamage()
	{
		return 2f;
	}

	@Override
	public boolean execute(EntityPlayer player, SpellCastData data)
	{
		if(player.world.isRemote)
			return false;
		World world = player.world;
		//TODO: Set projectile attack damage
		float attackDamage = getAttackDamage() + data.getAttackBonus();
		EntitySpellProjectile entity = entityFactory.apply(player);
		return world.spawnEntity(entity);
	}
}
