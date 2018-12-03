package brightspark.runicmagic.message;

import brightspark.runicmagic.handler.CapabilitySyncHandler;
import brightspark.runicmagic.init.RMSpells;
import brightspark.runicmagic.spell.Spell;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.HashMap;
import java.util.Map;

public class MessageSyncSpellsCap implements IMessage
{
	//If true then each cooldown in the map will be updated in the client
	//If false then the whole map will be replaced in the client
	public boolean changed;
	public Map<Spell, Long> cooldowns;
	public Spell selectedSpell;

	public MessageSyncSpellsCap() {}

	public MessageSyncSpellsCap(Map<Spell, Long> cooldowns, boolean changed, Spell selectedSpell)
	{
		this.changed = changed;
		this.cooldowns = cooldowns;
		this.selectedSpell = selectedSpell;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		changed = buf.readBoolean();
		short num = buf.readShort();
		cooldowns = new HashMap<>(num);
		for(short i = 0; i < num; i++)
		{
			Spell spell = RMSpells.getSpell(ByteBufUtils.readUTF8String(buf));
			long cooldown = buf.readLong();
			cooldowns.put(spell, cooldown);
		}
		String selected = ByteBufUtils.readUTF8String(buf);
		selectedSpell = selected.isEmpty() ? null : RMSpells.getSpell(selected);
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeBoolean(changed);
		buf.writeShort(cooldowns.size());
		for(Map.Entry<Spell, Long> cooldown : cooldowns.entrySet())
		{
			ByteBufUtils.writeUTF8String(buf, cooldown.getKey().getRegistryName().toString());
			buf.writeLong(cooldown.getValue());
		}
		ByteBufUtils.writeUTF8String(buf, selectedSpell == null ? "" : selectedSpell.getRegistryName().toString());
	}

	public static class Handler implements IMessageHandler<MessageSyncSpellsCap, IMessage>
	{
		@Override
		public IMessage onMessage(MessageSyncSpellsCap message, MessageContext ctx)
		{
			CapabilitySyncHandler.handleUpdate(Minecraft.getMinecraft().player, message);
			return null;
		}
	}
}
