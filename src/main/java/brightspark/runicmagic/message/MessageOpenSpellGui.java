package brightspark.runicmagic.message;

import brightspark.runicmagic.RunicMagic;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageOpenSpellGui implements IMessage
{
    @Override
    public void fromBytes(ByteBuf buf) {}

    @Override
    public void toBytes(ByteBuf buf) {}

    public static class Handler implements IMessageHandler<MessageOpenSpellGui, IMessage>
    {
        @Override
        public IMessage onMessage(MessageOpenSpellGui message, MessageContext ctx)
        {
            EntityPlayer player = Minecraft.getMinecraft().player;
            BlockPos pos = player.getPosition();
            player.openGui(RunicMagic.instance, 0, player.world, pos.getX(), pos.getY(), pos.getZ());
            return null;
        }
    }
}
