package brightspark.runicmagic.capability;

import brightspark.runicmagic.RunicMagic;
import brightspark.runicmagic.enums.CanCastResult;
import brightspark.runicmagic.enums.RuneType;
import brightspark.runicmagic.handler.NetworkHandler;
import brightspark.runicmagic.init.RMBlocks;
import brightspark.runicmagic.init.RMCapabilities;
import brightspark.runicmagic.init.RMSpells;
import brightspark.runicmagic.item.ItemStaff;
import brightspark.runicmagic.message.MessageSyncSpellsCap;
import brightspark.runicmagic.spell.Spell;
import brightspark.runicmagic.spell.SpellHandler;
import brightspark.runicmagic.util.CommonUtils;
import brightspark.runicmagic.util.Location;
import brightspark.runicmagic.util.SpellCastData;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public interface CapSpell extends RMCapability
{
	ResourceLocation RL = new ResourceLocation(RunicMagic.MOD_ID, "spell");

	@SuppressWarnings("ConstantConditions")
	static RMCapabilityProvider<CapSpell> getProvider()
	{
		return new RMCapabilityProvider<>(RMCapabilities.SPELL);
	}

	/**
	 * Gets the current selected spell
	 */
	Spell getSelectedSpell();

	/**
	 * Sets the current selected spell to the given spell
	 * Returns false if it's the current spell or the spell is not selectable
	 */
	boolean setSpell(EntityPlayer player, Spell spell);

	/**
	 * Gets the current selected spell cooldown
	 */
	Integer getSpellCooldown();

	/**
	 * Gets a copy of all spell cooldowns
	 */
	Map<Spell, Long> getCooldowns(World world);

	/**
	 * Checks if the player can execute the given spell
	 * If spell is null, then checks the current selected spell
	 */
	CanCastResult canExecuteSpell(EntityPlayer player, @Nullable ItemStack stack, @Nullable Spell spell);

	/**
	 * Executes the currently selected spell if there is one
	 * It's expected that canExecuteSpell has already been called before this!
	 */
	CanCastResult executeSpell(EntityPlayerMP player, ItemStack heldStack, @Nullable Spell spell);

	/**
	 * Called when a spell is about to be executed to do any final checks and processing
	 * Spell should be cancelled if this returns false
	 */
	boolean onSpellExecuted(EntityPlayerMP player, Spell spell, SpellCastData data);

	/**
	 * Starts a cooldown for the given spell
	 */
	void addCooldown(EntityPlayer player, Spell spell);

	/**
	 * Used by messages to sync a specific spell cooldown from the server
	 */
	void updateCooldown(Spell spell, Long cooldown);

	/**
	 * Used by messages to sync all spell cooldowns from the server
	 */
	void setCooldowns(Map<Spell, Long> cooldowns);

	/**
	 * Gets the location of the player's gatestone if they have one placed
	 */
	Location getGatestone();

	/**
	 * Sets the location of the player's gatestone
	 */
	void setGatestone(@Nullable Location location);

	class Impl implements CapSpell
	{
		private Spell selectedSpell = null;
		private Map<Spell, Long> cooldowns = new HashMap<>();
		private Location gatestoneLocation = null;

		@Override
		public boolean setSpell(EntityPlayer player, Spell spell)
		{
			RunicMagic.LOG.info("Setting spell: {} -> {}", selectedSpell, spell);
			if(selectedSpell == spell || !spell.isSelectable())
				return false;
			selectedSpell = spell;
			if(player instanceof EntityPlayerMP)
				dataChanged((EntityPlayerMP) player);
			return true;
		}

		@Override
		public Spell getSelectedSpell()
		{
			return selectedSpell;
		}

		@Override
		public Integer getSpellCooldown()
		{
			return selectedSpell != null ? selectedSpell.getCooldown() : null;
		}

		@Override
		public Map<Spell, Long> getCooldowns(World world)
		{
			long worldTime = world.getTotalWorldTime();
			cooldowns.entrySet().removeIf(entry -> entry.getValue() <= worldTime);
			return new HashMap<>(cooldowns);
		}

		// Spell parameter is used to specify a non-selectable spell (like a teleport)
		@Override
		public CanCastResult canExecuteSpell(EntityPlayer player, @Nullable ItemStack stack, @Nullable Spell nonSelectableSpell)
		{
			Spell spell = nonSelectableSpell == null ? selectedSpell : nonSelectableSpell;
			if(spell == null)
				return CanCastResult.NO_SPELL;
			if(player.isCreative())
				return CanCastResult.SUCCESS;
			//Check player level
			int level = RMCapabilities.getLevel(player).getLevel();
			if(level < spell.getLevel())
				return CanCastResult.LEVEL;
			//Check cooldowns
			Long cooldown = cooldowns.get(spell);
			if(cooldown != null)
			{
				if(cooldown <= player.world.getTotalWorldTime())
					cooldowns.remove(spell);
				else
					return CanCastResult.COOLDOWN;
			}
			//Check spell requirements
			if(!spell.canCast(player))
				return CanCastResult.SPELL_REQ;
			//Check the player has enough runes to cast the spell
			if(!CommonUtils.hasRunes(player.inventory.mainInventory, ItemStaff.calculateRuneCost(stack, spell)))
				return CanCastResult.RUNES;
			return CanCastResult.SUCCESS;
		}

		// Spell parameter is used to specify a non-selectable spell (like a teleport)
		@Override
		public CanCastResult executeSpell(EntityPlayerMP player, ItemStack heldStack, @Nullable Spell spell)
		{
			if(spell == null && selectedSpell == null)
				return CanCastResult.NO_SPELL;
			Spell spellToExecute = spell == null ? selectedSpell : spell;
			RunicMagic.LOG.info("Executing {}", spellToExecute);

			int level = RMCapabilities.getLevel(player).getLevel();
			float attackBonus = ItemStaff.getAttackBonus(heldStack);
			RuneType runeType = ItemStaff.getRuneType(heldStack);
			SpellCastData data = new SpellCastData(level, attackBonus, runeType);
			if(spellToExecute.getCastTime() > 0)
			{
				SpellHandler.addSpellCast(player, spellToExecute, data);
				return CanCastResult.SUCCESS;
			}
			if(!onSpellExecuted(player, spell, data))
				return CanCastResult.RUNES;
			if(!spellToExecute.execute(player, data))
				return CanCastResult.SPELL_REQ;
			RunicMagic.LOG.info("Successfully instantly executed spell {}", spellToExecute);
			return CanCastResult.SUCCESS;
		}

		@Override
		public boolean onSpellExecuted(EntityPlayerMP player, Spell spell, SpellCastData data)
		{
			if(player.isCreative())
				return true;
			//Remove runes for cost
			Map<RuneType, Short> cost = spell.getCost();
			RuneType typeToRemove = data.getRuneCostReduction();
			if(typeToRemove != null)
				cost.remove(typeToRemove);
			if(!CommonUtils.hasRunes(player.inventory.mainInventory, cost))
				return false;
			CommonUtils.removeRunes(player.inventory.mainInventory, cost);
			//Add cooldown
			addCooldown(player, spell);
			return true;
		}

		@Override
		public void addCooldown(EntityPlayer player, Spell spell)
		{
			if(player.isCreative())
				return;
			long cooldown = selectedSpell.getCooldown();
			if(cooldown > 0)
				cooldowns.put(selectedSpell, player.world.getTotalWorldTime() + cooldown);
		}

		@Override
		public void updateCooldown(Spell spell, @Nullable Long cooldown)
		{
			if(cooldown == null)
				cooldowns.remove(spell);
			else
				cooldowns.put(spell, cooldown);
		}

		@Override
		public void setCooldowns(Map<Spell, Long> cooldowns)
		{
			this.cooldowns = cooldowns;
		}

		@Override
		public Location getGatestone()
		{
			if(gatestoneLocation != null)
			{
				//Make sure the location still has a gatestone there
				MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
				if(server != null)
				{
					World world = server.getWorld(gatestoneLocation.getDimension());
					IBlockState state = world.getBlockState(gatestoneLocation.getPosition());
					if(state.getBlock() != RMBlocks.gatestone)
						gatestoneLocation = null;
				}
			}
			return gatestoneLocation;
		}

		@Override
		public void setGatestone(@Nullable Location location)
		{
			gatestoneLocation = location;
		}

		@Override
		public void dataChanged(EntityPlayerMP player)
		{
			//TODO: Try make this more efficient?
			NetworkHandler.network.sendTo(new MessageSyncSpellsCap(cooldowns, false, selectedSpell), player);
		}

		@SuppressWarnings("ConstantConditions")
		@Override
		public NBTTagCompound serializeNBT()
		{
			NBTTagCompound nbt = new NBTTagCompound();
			if(selectedSpell != null)
				nbt.setString("spell", selectedSpell.getRegistryName().toString());

			NBTTagList list = new NBTTagList();
			cooldowns.forEach((spell, cooldown) -> {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setString("spell", spell.getRegistryName().toString());
				tag.setLong("cooldown", cooldown);
				list.appendTag(tag);
			});
			nbt.setTag("cooldowns", list);

			return nbt;
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt)
		{
			String spellName = nbt.getString("spell");
			if(!spellName.isEmpty())
				selectedSpell = RMSpells.getSpell(spellName);

			cooldowns.clear();
			NBTTagList list = nbt.getTagList("cooldowns", Constants.NBT.TAG_COMPOUND);
			for(NBTBase tag : list)
			{
				NBTTagCompound compound = (NBTTagCompound) tag;
				Spell spell = RMSpells.getSpell(compound.getString("spell"));
				if(spell == null)
					continue;
				cooldowns.put(spell, compound.getLong("cooldown"));
			}
		}
	}
}
