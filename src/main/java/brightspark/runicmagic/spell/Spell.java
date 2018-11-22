package brightspark.runicmagic.spell;

import brightspark.runicmagic.RunicMagic;
import brightspark.runicmagic.enums.RuneType;
import brightspark.runicmagic.util.RunicMagicException;
import brightspark.runicmagic.util.SpellCastData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.HashMap;
import java.util.Map;

public abstract class Spell extends IForgeRegistryEntry.Impl<Spell>
{
	private final String unlocName;
	//If true, then is a selectable spell for use with a staff
	//If false, then is an instant click spell (e.g. teleport spell)
	protected boolean selectable = true;
	protected boolean instantCast = true;
	protected int cooldown = 0;
	protected int castTime = 0;

	private Map<RuneType, Short> cost = new HashMap<>();

	public Spell(String name)
	{
		setRegistryName(name);
		unlocName = RunicMagic.MOD_ID + ".spell." + name + ".name";
	}

	/**
	 * Executes this spell and returns true if successful
	 * Will be called instantly for instant spells, or at the end of casting
	 */
	public abstract boolean execute(EntityPlayer player, SpellCastData data);

	/**
	 * Process updating the casting of this spell if it's not instant
	 * @param world The world
	 * @param progress The current progress in ticks since the spell was executed
	 * @return true if the spell should be cancelled
	 */
	public boolean updateCasting(World world, EntityPlayer player, int progress)
	{
		if(!instantCast)
			throw new RunicMagicException("Non-instant casting spell %s does not override Spell#updateCasting!");
		return false;
	}

	/**
	 * Called when a spell cast is cancelled
	 */
	public void onCastCancel(EntityPlayer player)
	{
		player.sendMessage(new TextComponentString("Spell ").appendSibling(getUnlocNameTextComponent()).appendText(" cancelled!"));
	}

	/**
	 * Add a rune cost to the total cost to cast this spell
	 */
	public Spell addRuneCost(RuneType type, int amount)
	{
		cost.put(type, (short) amount);
		return this;
	}

	/**
	 * Returns a copy of the costs to cast this spell
	 */
	public Map<RuneType, Short> getCost()
	{
		return new HashMap<>(cost);
	}

	/**
	 * If this spell is selectable for use with a staff
	 */
	public boolean isSelectable()
	{
		return selectable;
	}

	/**
	 * Cooldown for this spell in ticks
	 */
	public int getCooldown()
	{
		return cooldown;
	}

	/**
	 * Time for this spell to cast in ticks
	 */
	public int getCastTime()
	{
		return castTime;
	}

	public String getUnlocName()
	{
		return unlocName;
	}

	public TextComponentTranslation getUnlocNameTextComponent()
	{
		return new TextComponentTranslation(getUnlocName());
	}
}
