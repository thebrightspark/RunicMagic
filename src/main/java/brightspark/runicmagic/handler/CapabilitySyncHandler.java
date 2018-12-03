package brightspark.runicmagic.handler;

import brightspark.runicmagic.RunicMagic;
import brightspark.runicmagic.capability.CapSpell;
import brightspark.runicmagic.gui.GuiSpellSelect;
import brightspark.runicmagic.init.RMCapabilities;
import brightspark.runicmagic.message.MessageSyncLevelCap;
import brightspark.runicmagic.message.MessageSyncSpellsCap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * This class is used by the sync messages to handle messages.
 * If the message arrives too quickly and the player is still null then the update is cached and applied once the
 * player entity joins the world.
 */
@Mod.EventBusSubscriber(modid = RunicMagic.MOD_ID, value = Side.CLIENT)
public class CapabilitySyncHandler
{
    private static MessageSyncSpellsCap pendingSpellsCap;
    private static MessageSyncLevelCap pendingLevelCap;

    @SubscribeEvent
    public static void onPlayerJoinWorld(EntityJoinWorldEvent event)
    {
        if(event.getEntity() instanceof EntityPlayerSP)
        {
            if(pendingSpellsCap != null)
                handleUpdate((EntityPlayerSP) event.getEntity(), pendingSpellsCap);
            if(pendingLevelCap != null)
                handleUpdate((EntityPlayerSP) event.getEntity(), pendingLevelCap);
        }
    }

    public static void handleUpdate(EntityPlayerSP player, MessageSyncSpellsCap message)
    {
        if(player == null)
            pendingSpellsCap = message;
        else
        {
            CapSpell spells = RMCapabilities.getSpells(player);
            if(message.changed)
                message.cooldowns.forEach(spells::updateCooldown);
            else
                spells.setCooldowns(message.cooldowns);
            spells.setSpell(player, message.selectedSpell);
            //Update open GUI
            GuiScreen gui = Minecraft.getMinecraft().currentScreen;
            if(gui instanceof GuiSpellSelect)
                ((GuiSpellSelect) gui).onSpellCapChange();
        }
    }

    public static void handleUpdate(EntityPlayerSP player, MessageSyncLevelCap message)
    {
        if(player == null)
            pendingLevelCap = message;
        else
            RMCapabilities.getLevel(player).setExperienceInternal(message.experience);
    }
}
