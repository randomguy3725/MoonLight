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

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.network.play.client.C03PacketPlayer;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.misc.TickEvent;
import wtf.moonlight.events.impl.player.MotionEvent;
import wtf.moonlight.events.impl.player.PostStepEvent;
import wtf.moonlight.events.impl.player.StrafeEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.values.impl.ModeValue;
import wtf.moonlight.features.values.impl.SliderValue;
import wtf.moonlight.utils.player.MovementUtils;
import wtf.moonlight.utils.player.PlayerUtils;

@ModuleInfo(name = "Step", category = ModuleCategory.Movement)
public class Step extends Module {

    public final ModeValue mode = new ModeValue("Mode", new String[]{"NCP"}, "NCP", this);
    public final SliderValue timer = new SliderValue("Timer", 1, 0.05f, 1, 0.05f, this);
    public final SliderValue delay = new SliderValue("Delay", 1000, 0, 2500, 100, this);
    private final double[] MOTION = new double[] {.42, .75, 1.};
    private long lastStep = 0;
    private boolean stepped = false;

    @Override
    public void onDisable() {
        mc.thePlayer.stepHeight = 0.6f;
        mc.timer.timerSpeed = 1;
        lastStep = 0L;
    }

    @EventTarget
    public void onPostStep(PostStepEvent event) {
        if (mode.get().equals("NCP")) {
            if (event.getHeight() == 1 && mc.thePlayer.onGround && !PlayerUtils.inLiquid() && !isEnabled(Speed.class) && !isEnabled(Scaffold.class) && !mc.gameSettings.keyBindJump.isKeyDown()) {
                    Block block = PlayerUtils.getBlock(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
                    if (block instanceof BlockStairs || block instanceof BlockSlab) return;

                    lastStep = System.currentTimeMillis() + 300L;
                    mc.timer.timerSpeed = timer.get();
                    for (double motion : MOTION) {
                        mc.timer.timerSpeed = timer.get();
                        sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + motion, mc.thePlayer.posZ, false));
                    }
            }
        }
    }

    @EventTarget
    public void onStrafe(StrafeEvent event) {
        if (mode.get().equals("NCP")) {
            if (mc.thePlayer.onGround && !PlayerUtils.inLiquid() && isEnabled(Speed.class) && mc.thePlayer.isCollidedHorizontally) {
                mc.thePlayer.jump();
            }
        }
    }

    @EventTarget
    public void onTick(TickEvent event) {
        if (stepped) {
            mc.timer.timerSpeed = 1;
            mc.thePlayer.stepHeight = 0.6f;
            stepped = false;
        }
        if (System.currentTimeMillis() > lastStep && !isEnabled(Speed.class) && !isEnabled(Scaffold.class)) {
            mc.thePlayer.stepHeight = 1;
            lastStep = 0L;
            stepped = true;
        }
    }

}
