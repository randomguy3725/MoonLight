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
package wtf.moonlight.features.modules.impl.movement;

import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.player.UpdateEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.values.impl.BoolValue;
import wtf.moonlight.utils.player.MovementUtils;

@ModuleInfo(name = "Strafe", category = ModuleCategory.Movement)
public class Strafe extends Module {

    public final BoolValue ground = new BoolValue("Ground", true, this);
    public final BoolValue air = new BoolValue("Air", true, this);

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        if (mc.thePlayer.onGround && ground.get()) MovementUtils.strafe();
        if (!mc.thePlayer.onGround && air.get()) MovementUtils.strafe();
    }
}