package brightspark.runicmagic.capability;

import brightspark.runicmagic.util.NetworkHandler;
import brightspark.runicmagic.init.RMCapabilities;
import brightspark.runicmagic.message.MessageSyncSpellsCap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

@Mod.EventBusSubscriber
public class CapabilityHandler
{
	@SubscribeEvent
	public static void attachCapability(AttachCapabilitiesEvent<Entity> event)
	{
		//Attach a new capability to players
		if(event.getObject() instanceof EntityPlayer && !RMCapabilities.hasSpells(event.getObject()))
			event.addCapability(CapSpell.RL, CapSpell.getProvider());
	}

	@SubscribeEvent
	public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event)
	{
		//Updates the client capability when they log into the server
		if(event.player instanceof EntityPlayerMP)
		{
			CapSpell cap = RMCapabilities.getSpells(event.player);
			if(cap != null)
				NetworkHandler.network.sendTo(new MessageSyncSpellsCap(cap.getCooldowns(), false), (EntityPlayerMP) event.player);
		}
	}

	@SubscribeEvent
	public static void onClonePlayer(net.minecraftforge.event.entity.player.PlayerEvent.Clone event)
	{
		//Copy capability data over to the new player entity on death
		if(event.isWasDeath() && event.getEntityPlayer() instanceof EntityPlayerMP)
		{
			EntityPlayerMP player = (EntityPlayerMP) event.getEntityPlayer();
			CapSpell oldCap = RMCapabilities.getSpells(event.getOriginal());
			CapSpell newCap = RMCapabilities.getSpells(player);
			if(oldCap == null || newCap == null)
				return;
			newCap.deserializeNBT(oldCap.serializeNBT());
			newCap.dataChanged(player);
		}
	}
}
