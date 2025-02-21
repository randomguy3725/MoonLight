/*
 * MoonLight Hacked Client
 *
 * A free and open-source hacked client for Minecraft.
 * Developed using Minecraft's resources.
 *
 * Repository: https://github.com/randomguy3725/MoonLight
 *
 * Author(s): [Randumbguy & wxdbie & opZywl & MukjepScarlet & lucas & eonian]
 */
package wtf.moonlight.events.impl.render;

import net.minecraft.client.gui.ScaledResolution;
import wtf.moonlight.events.impl.Event;

public record Render2DEvent(float partialTicks, ScaledResolution scaledResolution) implements Event {
}

