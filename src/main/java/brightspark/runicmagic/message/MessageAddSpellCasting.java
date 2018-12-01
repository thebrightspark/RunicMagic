package brightspark.runicmagic.message;

import brightspark.runicmagic.enums.RuneType;
import brightspark.runicmagic.init.RMSpells;
import brightspark.runicmagic.spell.Spell;
import brightspark.runicmagic.spell.SpellCasting;
import brightspark.runicmagic.spell.SpellHandler;
import brightspark.runicmagic.util.SpellCastData;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.UUID;

public class MessageAddSpellCasting implements IMessage
{
    private UUID playerUuid;
    private SpellCasting spellCasting;

    public MessageAddSpellCasting() {}

    public MessageAddSpellCasting(EntityPlayer player, SpellCasting spellCasting)
    {
        playerUuid = player.getUniqueID();
        this.spellCasting = spellCasting;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        long most = buf.readLong();
        long least = buf.readLong();
        playerUuid = new UUID(most, least);
        Spell spell = RMSpells.getSpell(ByteBufUtils.readUTF8String(buf));
        int magicLevel = buf.readInt();
        float attackBonus = buf.readFloat();
        RuneType runeType = RuneType.getById(buf.readShort());
        SpellCastData data = new SpellCastData(magicLevel, attackBonus, runeType);
        spellCasting = new SpellCasting(spell, data);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeLong(playerUuid.getMostSignificantBits());
        buf.writeLong(playerUuid.getLeastSignificantBits());
        ByteBufUtils.writeUTF8String(buf, spellCasting.getSpell().getRegistryName().toString());
        buf.writeInt(spellCasting.getData().getMagicLevel());
        buf.writeFloat(spellCasting.getData().getAttackBonus());
        buf.writeShort(spellCasting.getData().getRuneCostReduction().ordinal());
    }

    public static class Handler implements IMessageHandler<MessageAddSpellCasting, IMessage>
    {
        @Override
        public IMessage onMessage(MessageAddSpellCasting message, MessageContext ctx)
        {
            Minecraft.getMinecraft().addScheduledTask(() ->
                SpellHandler.addSpellCast(Minecraft.getMinecraft().player, message.spellCasting.getSpell(), message.spellCasting.getData()));
            return null;
        }
    }
}
