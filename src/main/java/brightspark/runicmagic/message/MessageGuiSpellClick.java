package brightspark.runicmagic.message;

import brightspark.runicmagic.capability.CapSpell;
import brightspark.runicmagic.init.RMCapabilities;
import brightspark.runicmagic.init.RMSpells;
import brightspark.runicmagic.item.ItemStaff;
import brightspark.runicmagic.spell.Spell;
import brightspark.runicmagic.util.CommonUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.apache.commons.lang3.tuple.Pair;

public class MessageGuiSpellClick implements IMessage
{
    private Spell spell;

    public MessageGuiSpellClick() {}

    public MessageGuiSpellClick(Spell spell)
    {
        this.spell = spell;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        spell = RMSpells.getSpell(ByteBufUtils.readUTF8String(buf));
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, spell.getRegistryName().toString());
    }

    public static class Handler implements IMessageHandler<MessageGuiSpellClick, IMessage>
    {
        @Override
        public IMessage onMessage(MessageGuiSpellClick message, MessageContext ctx)
        {
            EntityPlayerMP player = ctx.getServerHandler().player;
            CapSpell capSpell = RMCapabilities.getSpells(player);
            if(capSpell == null)
                return null;
            if(message.spell.isSelectable())
                capSpell.setSpell(player, message.spell); //This will trigger a sync back to the client
            else
            {
                Pair<ItemStack, EnumHand> held = CommonUtils.findHeldItem(player, heldStack -> heldStack.getItem() instanceof ItemStaff);
                capSpell.executeSpell(player, held == null ? null : held.getKey(), message.spell);
            }
            return null;
        }
    }
}
