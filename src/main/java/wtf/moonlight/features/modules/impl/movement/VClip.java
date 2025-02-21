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
import wtf.moonlight.features.values.impl.ModeValue;
import wtf.moonlight.features.values.impl.SliderValue;

@ModuleInfo(name = "VClip", category = ModuleCategory.Movement)
public class VClip extends Module {
    ModeValue mode = new ModeValue("Mode", new String[]{"Up","Down","Smart"}, "Down", this);
    SliderValue upAmount = new SliderValue("Up Blocks", 1, 0.1f, 10.0f, 0.05f, this, () -> mode.is("Up") || mode.is("Smart"));
    SliderValue downAmount = new SliderValue("Down Blocks", 1, 0.1f, 10.0f, 0.05f, this, () -> mode.is("Down") || mode.is("Smart"));

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        setTag(mode.get());
        if (mode.is("Up"))  {
            mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + upAmount.get(), mc.thePlayer.posZ);
            setEnabled(false);
        }
        if (mode.is("Down"))  {
            mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - downAmount.get(), mc.thePlayer.posZ);
            setEnabled(false);
        }
        if (mode.is("Smart")) {
            if (mc.thePlayer.rotationPitch <= 0)
                mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + upAmount.get(), mc.thePlayer.posZ);
            setEnabled(false);
            if (mc.thePlayer.rotationPitch >= 0)
                mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - downAmount.get(), mc.thePlayer.posZ);
            setEnabled(false);
        }
    }
}
