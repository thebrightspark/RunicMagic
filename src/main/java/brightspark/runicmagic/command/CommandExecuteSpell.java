package brightspark.runicmagic.command;

import brightspark.runicmagic.init.RMSpells;
import brightspark.runicmagic.spell.Spell;
import brightspark.runicmagic.util.SpellCastData;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
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
		return null;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if(!(sender instanceof EntityPlayerMP))
			throw new CommandException("Only players can use this command");

		Spell spell = RMSpells.getSpell(args[0]);
		if(spell == null)
			throw new CommandException("Spell %s does not exist!", args[0]);

		if(spell.execute((EntityPlayerMP) sender, new SpellCastData(99, 99)))
			sender.sendMessage(new TextComponentString("Executed spell " + args[0]));
		else
			sender.sendMessage(new TextComponentString("Failed to execute spell " + args[0]));
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
	{
		return args.length == 1 ? RMSpells.getRegNames() : null;
	}
}
