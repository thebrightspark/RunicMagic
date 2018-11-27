package brightspark.runicmagic.message;

import brightspark.runicmagic.capability.CapSpell;
import brightspark.runicmagic.init.RMCapabilities;
import brightspark.runicmagic.init.RMSpells;
import brightspark.runicmagic.spell.Spell;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageSetSelectedSpell implements IMessage
{
    private Spell spell;

    public MessageSetSelectedSpell() {}

    public MessageSetSelectedSpell(Spell spell)
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

    public static class Handler implements IMessageHandler<MessageSetSelectedSpell, IMessage>
    {
        @Override
        public IMessage onMessage(MessageSetSelectedSpell message, MessageContext ctx)
        {
            EntityPlayerMP player = ctx.getServerHandler().player;
            CapSpell capSpell = RMCapabilities.getSpells(player);
            if(capSpell == null)
                return null;
            capSpell.setSpell(player, message.spell); //This will trigger a sync back to the client
            return null;
        }
    }
}
