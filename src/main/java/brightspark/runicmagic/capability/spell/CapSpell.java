package brightspark.runicmagic.capability.spell;

import brightspark.runicmagic.RunicMagic;
import brightspark.runicmagic.capability.RMCapability;
import brightspark.runicmagic.capability.RMCapabilityProvider;
import brightspark.runicmagic.init.RMCapabilities;
import brightspark.runicmagic.init.RMSpells;
import brightspark.runicmagic.spell.Spell;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
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
	boolean canExecuteSpell(EntityPlayer player, Spell spell);

	/**
	 * Executes the currently selected spell if there is one
	 */
	boolean executeSpell(EntityPlayerMP player);

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

		@Override
		public boolean canExecuteSpell(EntityPlayer player, @Nullable Spell spell)
		{
			Spell toCheck = spell == null ? selectedSpell : spell;
			if(toCheck == null || toCheck.isSelectable()) //Ignore selectables, as that's handled by the staff
				return false;
			//Check cooldowns
			Long cooldown = cooldowns.get(toCheck);
			if(cooldown == null || player.world.getTotalWorldTime() < cooldown)
			{
				if(cooldown != null)
					cooldowns.remove(toCheck);
				return true;
			}
			return false;
		}

		@Override
		public boolean executeSpell(EntityPlayerMP player)
		{
			if(!hasSpellSelected() || !canExecuteSpell(player, null))
				return false;
			selectedSpell.execute(player);
			long cooldown = selectedSpell.getCooldown();
			if(cooldown > 0)
				cooldowns.put(selectedSpell, player.world.getTotalWorldTime() + cooldown);
			return true;
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
			//TODO: Send message to client to sync data
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
