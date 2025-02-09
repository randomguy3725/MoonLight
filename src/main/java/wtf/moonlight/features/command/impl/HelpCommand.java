/*
 * MoonLight Hacked Client
 *
 * A free and open-source hacked client for Minecraft.
 * Developed using Minecraft's resources.
 *
 * Repository: https://github.com/randomguy3725/MoonLight
 *
 * Author(s): [Randumbguy & opZywl & lucas]
 */
package wtf.moonlight.features.command.impl;

import wtf.moonlight.Moonlight;
import wtf.moonlight.features.command.Command;
import wtf.moonlight.utils.misc.DebugUtils;

import java.util.Arrays;

public final class HelpCommand extends Command {
    @Override
    public String[] getAliases() {
        return new String[]{"help", "h"};
    }

    @Override
    public void execute(final String[] arguments) {
        for (final Command command : Moonlight.INSTANCE.getCommandManager().cmd) {
            if(!(command instanceof ModuleCommand))
                DebugUtils.sendMessage(Arrays.toString(command.getAliases()) + ": " + command.getUsage());
        }
    }

    @Override
    public String getUsage() {
        return "help/h";
    }
}
