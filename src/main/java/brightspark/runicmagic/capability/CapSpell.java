package brightspark.runicmagic.capability;

import brightspark.runicmagic.RunicMagic;
import brightspark.runicmagic.enums.RuneType;
import brightspark.runicmagic.init.RMCapabilities;
import brightspark.runicmagic.init.RMSpells;
import brightspark.runicmagic.item.ItemStaff;
import brightspark.runicmagic.message.MessageSyncSpellsCap;
import brightspark.runicmagic.spell.Spell;
import brightspark.runicmagic.util.CommonUtils;
import brightspark.runicmagic.util.NetworkHandler;
import brightspark.runicmagic.util.SpellCastData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;

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
	boolean setSpell(EntityPlayerMP player, Spell spell);

	/**
	 * Gets the current selected spell cooldown
	 */
	Integer getSpellCooldown();

	/**
	 * Gets a copy of all spell cooldowns
	 */
	Map<Spell, Long> getCooldowns();

	/**
	 * Checks if a spell is currently selected
	 */
	boolean hasSpellSelected();

	/**
	 * Checks if the player can execute the given spell
	 * If spell is null, then checks the current selected spell
	 */
	boolean canExecuteSpell(EntityPlayer player, @Nullable ItemStack stack, @Nullable Spell spell);

	/**
	 * Executes the currently selected spell if there is one
	 * Things that call this should handle the removal of runes from the player's inventory
	 */
	boolean executeSpell(EntityPlayerMP player, float attackBonus, @Nullable Spell spell);

	/**
	 * Used by messages to sync a specific spell cooldown from the server
	 */
	void updateCooldown(Spell spell, Long cooldown);

	/**
	 * Used by messages to sync all spell cooldowns from the server
	 */
	void setCooldowns(Map<Spell, Long> cooldowns);

	class Impl implements CapSpell
	{
		private Spell selectedSpell = null;
		private Map<Spell, Long> cooldowns = new HashMap<>();

		@Override
		public boolean setSpell(EntityPlayerMP player, Spell spell)
		{
			if(selectedSpell == spell || !spell.isSelectable())
				return false;
			selectedSpell = spell;
			dataChanged(player);
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
		public Map<Spell, Long> getCooldowns()
		{
			return new HashMap<>(cooldowns);
		}

		@Override
		public boolean hasSpellSelected()
		{
			return selectedSpell != null;
		}

		// Spell parameter is used to specify a non-selectable spell (like a teleport)
		@Override
		public boolean canExecuteSpell(EntityPlayer player, @Nullable ItemStack stack, @Nullable Spell nonSelectableSpell)
		{
			Spell spell = nonSelectableSpell == null ? selectedSpell : nonSelectableSpell;
			if(spell == null)
				return false;
			//TODO: Check player's magic level against level required for the spell
			//Check cooldowns
			Long cooldown = cooldowns.get(spell);
			if(cooldown != null)
			{
				if(player.world.getTotalWorldTime() < cooldown)
					cooldowns.remove(spell);
				else
					return false;
			}

			//Check the player has enough runes to cast the spell
			Map<RuneType, Short> spellCost = ItemStaff.calculateRuneCost(stack, spell);
			boolean hasRunes = spellCost.isEmpty() || CommonUtils.hasRunes(player.inventory.mainInventory, spell.getCost());
			//Check spell requirements
			return hasRunes && spell.canCast(player);
		}

		// Spell parameter is used to specify a non-selectable spell (like a teleport)
		@Override
		public boolean executeSpell(EntityPlayerMP player, float attackBonus, @Nullable Spell spell)
		{
			if(!hasSpellSelected())
				return false;
			Spell spellToExecute = spell == null ? selectedSpell : spell;
			if(spellToExecute == null)
				return false;
			boolean success = spellToExecute.execute(player, new SpellCastData(50, attackBonus)); //TODO: Implement player magic level
			if(success)
			{
				long cooldown = selectedSpell.getCooldown();
				if(cooldown > 0)
					cooldowns.put(selectedSpell, player.world.getTotalWorldTime() + cooldown);
			}
			return success;
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
		public void dataChanged(EntityPlayerMP player)
		{
			NetworkHandler.network.sendTo(new MessageSyncSpellsCap(cooldowns, false), player);
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
