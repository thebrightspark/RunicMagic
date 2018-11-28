package brightspark.runicmagic.capability;

import brightspark.runicmagic.LevelManager;
import brightspark.runicmagic.RunicMagic;
import brightspark.runicmagic.handler.NetworkHandler;
import brightspark.runicmagic.init.RMCapabilities;
import brightspark.runicmagic.message.MessageSyncLevelCap;
import brightspark.runicmagic.util.RunicMagicException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

// https://runescape.fandom.com/wiki/Experience
public interface CapLevel extends RMCapability
{
	ResourceLocation RL = new ResourceLocation(RunicMagic.MOD_ID, "level");

	static RMCapabilityProvider<CapLevel> getProvider()
	{
		return new RMCapabilityProvider<>(RMCapabilities.LEVEL);
	}

	int getLevel();

	void setLevel(EntityPlayerMP player, int level);

	int getExperience();

	void setExperience(EntityPlayerMP player, int experience);

	boolean setExperienceInternal(int experience);

	int addExperience(EntityPlayerMP player, int amount);

	class Impl implements CapLevel
	{
		private int level, experience, levelMaxExp;

		@Override
		public int getLevel()
		{
			return level;
		}

		@Override
		public void setLevel(EntityPlayerMP player, int level)
		{
			boolean changed = this.level != level;
			if(changed)
			{
				this.level = level;
				calcLevel(true);
				dataChanged(player);
			}
		}

		@Override
		public int getExperience()
		{
			return experience;
		}

		@Override
		public void setExperience(EntityPlayerMP player, int experience)
		{
			if(setExperienceInternal(experience))
				dataChanged(player);
		}

		@Override
		public boolean setExperienceInternal(int experience)
		{
			boolean changed = this.experience != experience;
			if(changed)
			{
				this.experience = experience;
				calcLevel(true);
			}
			return changed;
		}

		@Override
		public int addExperience(EntityPlayerMP player, int amount)
		{
			experience += amount;
			calcLevel(false);
			dataChanged(player);
			return experience;
		}

		private void calcLevel(boolean force)
		{
			if(level > 0 && levelMaxExp > 0 && experience < levelMaxExp)
				return;
			if(force)
			{
				level = LevelManager.getLevelForXp(experience);
				levelMaxExp = LevelManager.get(level);
			}
			else
			{
				if(level <= 0)
				{
					level = LevelManager.getLevelForXp(experience);
					if(level <= 0)
						throw new RunicMagicException("Can't determine level for xp: " + experience);
				}
				if(levelMaxExp <= 0)
					levelMaxExp = LevelManager.get(level);
				while(experience >= levelMaxExp)
				{
					level++;
					levelMaxExp = LevelManager.get(level);
				}
			}
		}

		@Override
		public void dataChanged(EntityPlayerMP player)
		{
			NetworkHandler.network.sendTo(new MessageSyncLevelCap(experience), player);
		}

		@Override
		public NBTTagCompound serializeNBT()
		{
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setInteger("xp", experience);
			return nbt;
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt)
		{
			experience = nbt.getInteger("xp");
			//Calculate new level as necessary
			if(level <= 0 || levelMaxExp <= 0)
				calcLevel(true);
			else if(experience >= levelMaxExp)
				calcLevel(false);
		}
	}
}
