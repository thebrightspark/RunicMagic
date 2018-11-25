package brightspark.runicmagic.spell.projectile;

import brightspark.runicmagic.entity.EntityHelixProjectile;
import brightspark.runicmagic.entity.EntitySpellProjectile;
import brightspark.runicmagic.enums.RuneType;
import brightspark.runicmagic.enums.SpellType;
import brightspark.runicmagic.spell.Spell;
import brightspark.runicmagic.util.SpellCastData;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import java.awt.*;
import java.util.function.Function;

public class SpellProjectileBase extends Spell
{
	private final Function<EntityPlayer, EntitySpellProjectile> entityFactory;
	protected float attackDamage = 2F;

	public SpellProjectileBase(String name, SpellType spellType, int level)
	{
		this(name, new Color(1F, 1F, 1F), spellType, level);
	}

	public SpellProjectileBase(String name, RuneType runeType, SpellType spellType, int level)
	{
		this(name, runeType.getColour(), spellType, level);
	}

	public SpellProjectileBase(String name, Color colour, SpellType spellType, int level)
	{
		super(name, spellType, level);
		entityFactory = player -> new EntityHelixProjectile(player, this, colour);
		cooldown = 10;
	}

	public SpellProjectileBase(String name, Function<EntityPlayer, EntitySpellProjectile> entityFactory, SpellType spellType, int level)
	{
		super(name, spellType, level);
		this.entityFactory = entityFactory;
		cooldown = 10; //0.5s
	}

	@Override
	public boolean canCast(EntityPlayer player)
	{
		return true;
	}

	public void applyEffects(EntityLivingBase entityHit) {}

	@Override
	public boolean execute(EntityPlayer player, SpellCastData data)
	{
		if(player.world.isRemote)
			return false;
		World world = player.world;
		EntitySpellProjectile entity = entityFactory.apply(player);
		entity.setAttackDamage(attackDamage + data.getAttackBonus());
		return world.spawnEntity(entity);
	}
}
