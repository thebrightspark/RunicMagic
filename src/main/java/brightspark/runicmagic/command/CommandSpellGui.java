package brightspark.runicmagic.command;

import brightspark.runicmagic.message.MessageOpenSpellGui;
import brightspark.runicmagic.util.NetworkHandler;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class CommandSpellGui extends CommandBase
{
    @Override
    public String getName()
    {
        return "spellgui";
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
        NetworkHandler.network.sendTo(new MessageOpenSpellGui(), (EntityPlayerMP) sender);
    }
}
