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
package wtf.moonlight.events.impl.player;

import lombok.AllArgsConstructor;
import wtf.moonlight.events.impl.Event;

@AllArgsConstructor
public class LookEvent implements Event {

    public float[] rotation;
}
