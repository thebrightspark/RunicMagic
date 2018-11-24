package brightspark.runicmagic.command;

import brightspark.runicmagic.init.RMSpells;
import brightspark.runicmagic.spell.Spell;
import brightspark.runicmagic.spell.SpellHandler;
import brightspark.runicmagic.util.SpellCastData;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nullable;
import java.util.List;

public class CommandExecuteSpell extends CommandBase
{
	@Override
	public String getName()
	{
		return "spell";
	}

	@Override
	public String getUsage(ICommandSender sender)
	{
		return "spell ['cast'] <spellRegistryName>";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if(!(sender instanceof EntityPlayerMP))
			throw new CommandException("Only players can use this command");
		if(args.length <= 0 || args.length > 2)
			throw new WrongUsageException(getUsage(sender));

		boolean cast = args.length == 2 && args[0].equalsIgnoreCase("cast");
		Spell spell = RMSpells.getSpell(args[cast ? 1 : 0]);
		if(spell == null)
			throw new CommandException("Spell %s does not exist!", args[0]);

		if(cast)
		{
			SpellHandler.addSpellCast((EntityPlayerMP) sender, spell, new SpellCastData(99, 0));
			sender.sendMessage(new TextComponentString("Started casting spell " + args[0]));
		}
		else
		{
			if(spell.execute((EntityPlayerMP) sender, new SpellCastData(99, 0)))
				sender.sendMessage(new TextComponentString("Executed spell " + args[0]));
			else
				sender.sendMessage(new TextComponentString("Failed to execute spell " + args[0]));
		}
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
	{
		return args.length == 1 || args.length == 2 ? RMSpells.getRegNames() : null;
	}
}
