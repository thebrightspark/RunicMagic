package brightspark.runicmagic.init;

import brightspark.runicmagic.capability.RMCapabilityStorage;
import brightspark.runicmagic.capability.spell.CapSpell;
import net.minecraft.entity.Entity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class RMCapabilities
{
	@CapabilityInject(CapSpell.class)
	public static final Capability<CapSpell> SPELL = null;

	public static void init()
	{
		CapabilityManager.INSTANCE.register(CapSpell.class, new RMCapabilityStorage<>(), CapSpell.Impl::new);
	}

	public static boolean hasSpells(Entity player)
	{
		return player.hasCapability(SPELL, null);
	}

	public static CapSpell getSpells(Entity player)
	{
		return player.getCapability(SPELL, null);
	}
}
