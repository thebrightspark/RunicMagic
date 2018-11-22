package brightspark.runicmagic.message;

import brightspark.runicmagic.spell.SpellHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.UUID;

public class MessageRemoveSpellCasting implements IMessage
{
    private UUID playerUuid;

    public MessageRemoveSpellCasting() {}

    public MessageRemoveSpellCasting(EntityPlayer player)
    {
        playerUuid = player.getUniqueID();
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        long most = buf.readLong();
        long least = buf.readLong();
        playerUuid = new UUID(most, least);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeLong(playerUuid.getMostSignificantBits());
        buf.writeLong(playerUuid.getLeastSignificantBits());
    }

    public static class Handler implements IMessageHandler<MessageRemoveSpellCasting, IMessage>
    {
        @Override
        public IMessage onMessage(MessageRemoveSpellCasting message, MessageContext ctx)
        {
            SpellHandler.removeSpellCast(message.playerUuid);
            return null;
        }
    }
}
