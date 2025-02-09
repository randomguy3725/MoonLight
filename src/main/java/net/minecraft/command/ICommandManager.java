package net.minecraft.command;

import net.minecraft.util.BlockPos;

import java.util.List;
import java.util.Map;

public interface ICommandManager
{
    int executeCommand(ICommandSender sender, String rawCommand);

    List<String> getTabCompletionOptions(ICommandSender sender, String input, BlockPos pos);

    List<ICommand> getPossibleCommands(ICommandSender sender);

    Map<String, ICommand> getCommands();
}
