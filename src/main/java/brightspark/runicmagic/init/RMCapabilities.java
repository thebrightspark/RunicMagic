package brightspark.runicmagic.init;

import brightspark.runicmagic.capability.CapLevel;
import brightspark.runicmagic.capability.RMCapabilityStorage;
import brightspark.runicmagic.capability.CapSpell;
import net.minecraft.entity.Entity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class RMCapabilities
{
	@CapabilityInject(CapSpell.class)
	public static final Capability<CapSpell> SPELL = null;
	@CapabilityInject(CapLevel.class)
	public static final Capability<CapLevel> LEVEL = null;

	public static void init()
	{
		CapabilityManager.INSTANCE.register(CapSpell.class, new RMCapabilityStorage<>(), CapSpell.Impl::new);
		CapabilityManager.INSTANCE.register(CapLevel.class, new RMCapabilityStorage<>(), CapLevel.Impl::new);
	}

	public static boolean hasSpells(Entity player)
	{
		return player.hasCapability(SPELL, null);
	}

	public static CapSpell getSpells(Entity player)
	{
		return player.getCapability(SPELL, null);
	}

	public static boolean hasLevel(Entity player)
	{
		return player.hasCapability(LEVEL, null);
	}

	public static CapLevel getLevel(Entity player)
	{
		return player.getCapability(LEVEL, null);
	}
}
