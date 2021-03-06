package brightspark.runicmagic.handler;

import brightspark.runicmagic.RunicMagic;
import brightspark.runicmagic.capability.CapLevel;
import brightspark.runicmagic.capability.CapSpell;
import brightspark.runicmagic.init.RMCapabilities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

@Mod.EventBusSubscriber(modid = RunicMagic.MOD_ID)
public class CapabilityHandler
{
	@SubscribeEvent
	public static void attachCapability(AttachCapabilitiesEvent<Entity> event)
	{
		//Attach a new capability to players
		if(event.getObject() instanceof EntityPlayer && !RMCapabilities.hasSpells(event.getObject()))
		{
			event.addCapability(CapSpell.RL, CapSpell.getProvider());
			event.addCapability(CapLevel.RL, CapLevel.getProvider());
		}
	}

	@SubscribeEvent
	public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event)
	{
		//Updates the client capability when they log into the server
		if(event.player instanceof EntityPlayerMP)
		{
			EntityPlayerMP player = (EntityPlayerMP) event.player;
			CapSpell capSpell = RMCapabilities.getSpells(event.player);
			if(capSpell != null)
				capSpell.dataChanged(player);
			CapLevel capLevel = RMCapabilities.getLevel(event.player);
			if(capLevel != null)
				capLevel.dataChanged(player);
		}
	}

	@SubscribeEvent
	public static void onClonePlayer(net.minecraftforge.event.entity.player.PlayerEvent.Clone event)
	{
		//Copy capability data over to the new player entity on death
		if(event.isWasDeath() && event.getEntityPlayer() instanceof EntityPlayerMP)
		{
			EntityPlayer playerOld = event.getOriginal();
			EntityPlayerMP playerNew = (EntityPlayerMP) event.getEntityPlayer();

			CapSpell capSpells = RMCapabilities.getSpells(playerOld);
			if(capSpells != null)
				capSpells.copyDataToPlayerCap(playerNew);

			CapLevel capLevel = RMCapabilities.getLevel(playerOld);
			if(capLevel != null)
				capLevel.copyDataToPlayerCap(playerNew);
		}
	}
}
