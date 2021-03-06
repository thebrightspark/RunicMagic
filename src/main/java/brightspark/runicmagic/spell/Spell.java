package brightspark.runicmagic.spell;

import brightspark.runicmagic.RunicMagic;
import brightspark.runicmagic.enums.RuneType;
import brightspark.runicmagic.enums.SpellType;
import brightspark.runicmagic.util.RunicMagicException;
import brightspark.runicmagic.util.SpellCastData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;

public abstract class Spell extends IForgeRegistryEntry.Impl<Spell>
{
	private final String unlocName;
	private final ResourceLocation iconRL;
	private final SpellType spellType;
	private final int level;
	//If true, then is a selectable spell for use with a staff
	//If false, then is an instant click spell (e.g. teleport spell)
	protected boolean selectable = true;
	protected int cooldown = 0;
	protected int castTime = 0;

	private Map<RuneType, Short> cost = new HashMap<>();

	public Spell(String name, SpellType spellType, int level)
	{
		setRegistryName(name);
		iconRL = new ResourceLocation(RunicMagic.MOD_ID, "textures/spells/" + name + ".png");
		this.spellType = spellType;
		this.level = level;
		unlocName = RunicMagic.MOD_ID + ".spell." + name + ".name";
	}

	/**
	 * Gets the ResourceLocation of this spell's icon
	 * @return Icon ResourceLocation
	 */
	public ResourceLocation getIconRL()
	{
		return iconRL;
	}

	/**
	 * Gets the spell type of this spell
	 * @return The spell type
	 */
	public SpellType getSpellType()
	{
		return spellType;
	}

	/**
	 * The magic level required to use this spell
	 * @return Magic level requirement
	 */
	public int getLevel()
	{
		return level;
	}

	/**
	 * Checks if the player meets the spell's requirements to start casting
	 * @param player The player
	 * @return Whether the spell can be cast
	 */
	public abstract boolean canCast(EntityPlayer player);

	/**
	 * Process updating the casting of this spell if it's not instant
	 * @param world The world
	 * @param progress The current progress in ticks since the spell was executed
	 * @return true if the spell should be cancelled
	 */
	public boolean updateCasting(World world, EntityPlayer player, int progress)
	{
		if(castTime > 0)
			throw new RunicMagicException("Non-instant casting spell %s does not override Spell#updateCasting!", this);
		return false;
	}

	/**
	 * Executes this spell and returns true if successful
	 * Will be called instantly for instant spells, or at the end of casting
	 */
	public abstract boolean execute(EntityPlayer player, SpellCastData data);

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

	@Override
	public String toString()
	{
		return getRegistryName().toString();
	}

	// <<<<<<<< Util methods >>>>>>>>

	protected static double getRandOffset(World world, double variance)
	{
		return variance == 0D ? 0D : (world.rand.nextDouble() * variance) - (variance / 2);
	}

	protected static Vec3d posOffset(World world, Vec3d pos, double xVary, double yVary, double zVary)
	{
		return pos.add(getRandOffset(world, xVary), getRandOffset(world, yVary), getRandOffset(world, zVary));
	}

	protected boolean hasPlayerMoved(EntityPlayer player)
	{
		return player.prevPosX != player.posX || player.prevPosY != player.posY || player.prevPosZ != player.posZ;
	}

	protected int countItems(EntityPlayer player, ItemStack stack)
	{
		int count = 0;
		for(int i = 0; i < player.inventory.getSizeInventory(); i++)
		{
			ItemStack invStack = player.inventory.getStackInSlot(i);
			if(OreDictionary.itemMatches(stack, invStack, false))
				count += invStack.getCount();
		}
		return count;
	}

	protected static Vec3d createRandVector(Random random)
	{
		double phi = random.nextDouble() * 2 * Math.PI;
		double theta = Math.acos((random.nextDouble() * 2D) - 1D);
		double x = Math.sin(theta) * Math.cos(phi);
		double y = Math.sin(theta) * Math.sin(phi);
		double z = Math.cos(theta);
		return new Vec3d(x, y, z);
	}

	protected boolean playerHasStack(EntityPlayer player, ItemStack stack, boolean strict)
	{
		return playerHasStack(player, invStack -> OreDictionary.itemMatches(stack, invStack, strict));
	}

	protected boolean playerHasStack(EntityPlayer player, Predicate<ItemStack> predicate)
	{
		for(int i = 0; i < player.inventory.getSizeInventory(); i++)
			if(predicate.test(player.inventory.getStackInSlot(i)))
				return true;
		return false;
	}

	protected boolean playerHasSpace(EntityPlayer player)
	{
		return playerHasStack(player, ItemStack::isEmpty);
	}

	protected void givePlayerStack(EntityPlayer player, ItemStack stack)
	{
		if(!player.addItemStackToInventory(stack))
			player.entityDropItem(stack, 0F);
	}
}
