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

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.packet.PacketEvent;
import wtf.moonlight.events.impl.player.UpdateEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.modules.impl.exploit.Blink;
import wtf.moonlight.features.values.impl.BoolValue;
import wtf.moonlight.features.values.impl.ModeValue;
import wtf.moonlight.features.values.impl.SliderValue;
import wtf.moonlight.utils.packet.BlinkComponent;
import wtf.moonlight.utils.packet.PacketUtils;
import wtf.moonlight.utils.packet.PingSpoofComponent;
import wtf.moonlight.utils.player.MovementUtils;

@ModuleInfo(name = "Fly", category = ModuleCategory.Movement)
public class Fly extends Module {

    public final ModeValue mode = new ModeValue("Mode", new String[]{"Vanilla", "Miniblox"}, "Vanilla", this);
    private final SliderValue moveSpeed = new SliderValue("Speed", 2f, 1f, 10f, 0.1f, this, () -> mode.is("Vanilla") || mode.is("Miniblox"));
    private final SliderValue upSpeed = new SliderValue("Up Speed", 2f, 0.1f, 5f, 0.1f, this, () -> mode.is("Vanilla") || mode.is("Miniblox"));
    private final SliderValue downSpeed = new SliderValue("Down Speed", 2f, 0.1f, 5f, 0.1f, this, () -> mode.is("Vanilla") || mode.is("Miniblox"));
    private final BoolValue cancelSetback = new BoolValue("Cancel Setback", false, this, () -> mode.is("Miniblox"));
    private final BoolValue autoResync = new BoolValue("Auto Resync", false, this, () -> mode.is("Miniblox"));
    private final BoolValue stop = new BoolValue("Stop", false, this, () -> mode.is("Miniblox"));
    private final SliderValue stopTicks = new SliderValue("Stop Ticks", 25f, 5f, 100f, 1f, this, () -> mode.is("Miniblox") && stop.get());
    private final BoolValue lockY = new BoolValue("Lock Y", true, this, () -> mode.is("Miniblox"));
    private final BoolValue offsetY = new BoolValue("Offset Y", true, this, () -> mode.is("Miniblox"));
    private final SliderValue stopY = new SliderValue("Y Offset", 0.4f, 0, 1.0f, 0.05f, this, () -> mode.is("Miniblox") && offsetY.get());
    private final BoolValue slow = new BoolValue("Slow", false, this, () -> mode.is("Miniblox"));
    private final SliderValue slowWaitTicks = new SliderValue("Slow Wait Ticks", 25f, 5f, 100f, 1f, this, () -> mode.is("Miniblox") && slow.get());
    private final SliderValue slowAmount = new SliderValue("Slow Amount", 2f, 1f, 3f, 0.1f, this, () -> mode.is("Miniblox") && slow.get());
    private int ticksUntilStart;

    @Override
    public void onEnable() {
        ticksUntilStart = 0;
        if (mode.is("Miniblox") && autoResync.get()) {
            mc.thePlayer.sendChatMessage("/resync");
        }
    }

    @Override
    public void onDisable() {
        ticksUntilStart = 0;
    }


    @EventTarget
    public void onUpdate(UpdateEvent event) {
        ticksUntilStart++;
        setTag(mode.get());
        switch (mode.get()) {
            case "Vanilla":
                if (!mc.thePlayer.isJumping) {
                    mc.thePlayer.motionY = 0.0D;
                }
                if (mc.thePlayer.isJumping) {
                    mc.thePlayer.motionY = upSpeed.get();
                }
                if (mc.thePlayer.isSneaking()) {
                    mc.thePlayer.motionY = -downSpeed.get();
                }

                MovementUtils.strafe(moveSpeed.get());

                break;
            case "Miniblox":
                if (offsetY.get())
                    if (mc.thePlayer.onGround) {
                        mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + stopY.get(), mc.thePlayer.posZ);
                    }

                if (ticksUntilStart <= stopTicks.get() && stop.get()) {
                    mc.thePlayer.motionY = 0.0D;
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), false);
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), false);
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.getKeyCode(), false);
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.getKeyCode(), false);
                }

                if (stop.get())
                    if (ticksUntilStart >= stopTicks.get()) {
                        if (!mc.thePlayer.isJumping) {
                            mc.thePlayer.motionY = 0.0D;
                        }
                        if (!lockY.get()) {
                            if (mc.thePlayer.isJumping)
                                mc.thePlayer.motionY = upSpeed.get();
                        }
                        if (!lockY.get()) {
                            if (mc.thePlayer.isSneaking())
                                mc.thePlayer.motionY = -downSpeed.get();
                        }

                        if (lockY.get()) {
                            mc.thePlayer.motionY = 0.0D;
                        }

                        MovementUtils.setSpeed(moveSpeed.get());
                    }

                if (!stop.get()) {
                    if (!mc.thePlayer.isJumping) {
                        mc.thePlayer.motionY = 0.0D;
                    }
                    if (!lockY.get()) {
                        if (mc.thePlayer.isJumping)
                            mc.thePlayer.motionY = upSpeed.get();
                    }
                    if (!lockY.get()) {
                        if (mc.thePlayer.isSneaking())
                            mc.thePlayer.motionY = -downSpeed.get();
                    }

                    if (lockY.get()) {
                        mc.thePlayer.motionY = 0.0D;
                    }

                    MovementUtils.setSpeed(moveSpeed.get());
                }

                if (slow.get())
                    if (ticksUntilStart >= slowWaitTicks.get()) {
                        if (!mc.thePlayer.isJumping) {
                            mc.thePlayer.motionY = 0.0D;
                        }
                        if (!lockY.get()) {
                            if (mc.thePlayer.isJumping)
                                mc.thePlayer.motionY = upSpeed.get();
                        }
                        if (!lockY.get()) {
                            if (mc.thePlayer.isSneaking())
                                mc.thePlayer.motionY = -downSpeed.get();
                        }

                        if (lockY.get()) {
                            mc.thePlayer.motionY = 0.0D;
                        }

                        MovementUtils.setSpeed(moveSpeed.get() / slowAmount.get());
                    }
                break;
        }
    }

    @EventTarget
    public void onPacket(PacketEvent event) {
        if (mc.thePlayer == null) return;
        Packet packet = event.getPacket();
        switch (mode.get()) {
            case "Miniblox":
                if (cancelSetback.get()) {
                    if (event.getPacket() instanceof S08PacketPlayerPosLook s08 && mc.thePlayer.ticksExisted >= 100) {
                        event.setCancelled(true);

                        PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(
                                        s08.getX(),
                                        s08.getY(),
                                        s08.getZ(),
                                        s08.getYaw(),
                                        s08.getPitch(),
                                        mc.thePlayer.onGround
                                )
                        );

                        PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(
                                        mc.thePlayer.posX,
                                        mc.thePlayer.posY,
                                        mc.thePlayer.posZ,
                                        mc.thePlayer.rotationYaw,
                                        mc.thePlayer.rotationPitch,
                                        mc.thePlayer.onGround
                                )
                        );
                    }
                }
        }
    }
}
