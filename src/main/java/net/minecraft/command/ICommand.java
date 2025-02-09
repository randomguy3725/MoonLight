package net.minecraft.command;

import net.minecraft.util.BlockPos;

import java.util.List;

public interface ICommand extends Comparable<ICommand>
{
    String getCommandName();

    String getCommandUsage(ICommandSender sender);

    List<String> getCommandAliases();

    void processCommand(ICommandSender sender, String[] args) throws CommandException;

    boolean canCommandSenderUseCommand(ICommandSender sender);

    List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos);

    boolean isUsernameIndex(String[] args, int index);
}
