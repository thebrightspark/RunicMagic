package brightspark.runemagic.message;

import brightspark.runemagic.capability.spell.CapSpell;
import brightspark.runemagic.init.RMCapabilities;
import brightspark.runemagic.init.RMSpells;
import brightspark.runemagic.spell.Spell;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
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
	private boolean changed;
	private Map<Spell, Long> cooldowns;

	public MessageSyncSpellsCap() {}

	public MessageSyncSpellsCap(Map<Spell, Long> cooldowns, boolean changed)
	{
		this.changed = changed;
		this.cooldowns = cooldowns;
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
	}

	public static class Handler implements IMessageHandler<MessageSyncSpellsCap, IMessage>
	{
		@Override
		public IMessage onMessage(MessageSyncSpellsCap message, MessageContext ctx)
		{
			EntityPlayer player = Minecraft.getMinecraft().player;
			CapSpell spells = RMCapabilities.getSpells(player);
			if(spells == null)
				return null;
			if(message.changed)
				message.cooldowns.forEach(spells::updateCooldown);
			else
				spells.setCooldowns(message.cooldowns);
			return null;
		}
	}
}
