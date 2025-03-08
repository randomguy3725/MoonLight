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

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemFireball;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import org.apache.commons.lang3.Range;
import org.lwjglx.input.Keyboard;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.misc.WorldEvent;
import wtf.moonlight.events.impl.packet.PacketEvent;
import wtf.moonlight.events.impl.player.*;
import wtf.moonlight.events.impl.render.Render2DEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.values.impl.BoolValue;
import wtf.moonlight.features.values.impl.ModeValue;
import wtf.moonlight.features.values.impl.SliderValue;
import wtf.moonlight.gui.font.Fonts;
import wtf.moonlight.utils.player.MovementUtils;
import wtf.moonlight.utils.player.PlayerUtils;

import java.util.Objects;

@ModuleInfo(name = "LongJump", category = ModuleCategory.Movement, key = Keyboard.KEY_F)
public class LongJump extends Module {
    public final ModeValue mode = new ModeValue("Mode", new String[]{"Watchdog Fireball", "Old Matrix", "Miniblox","Watchdog Damage"}, "Watchdog Fireball", this);
    public final ModeValue wdFBMode = new ModeValue("Fireball Mode", new String[]{"High"}, "High", this, () -> mode.is("Watchdog Fireball"));
    private final SliderValue oMatrixTimer = new SliderValue("Matrix Timer", 0.3f, 0.1f, 1, 0.01f, this, () -> mode.is("Old Matrix"));
    private final BoolValue boost = new BoolValue("Boost", true, this, () -> mode.is("Watchdog Fireball"));
    public final BoolValue noBob = new BoolValue("No Bob", true, this);
    private int lastSlot = -1;
    //fb
    private int ticks = -1;
    private boolean setSpeed;
    public static boolean stopModules;
    private boolean sentPlace;
    private int initTicks;
    private boolean thrown;

    //matrix
    private boolean mPacket;
    private int matrixTimer = 0;

    //miniblox
    private boolean jumped = false;
    private int currentTimer = 0;
    private int pauseTimes = 0;
    private int activeTicks = 0;

    //watchdog damage
    public int dmgTicks;

    //others
    private boolean velo;
    private double distance;
    private int ticksSinceVelocity;

    @Override
    public void onEnable() {
        lastSlot = mc.thePlayer.inventory.currentItem;
        ticks = 0;
        distance = 0;

        if (mode.is("Watchdog Fireball")) {
            int fbSlot = getFBSlot();
            if (fbSlot == -1) {
                toggle();
            }

            stopModules = true;
            initTicks = 0;
        }
        if (Objects.equals(mode.get(), "Watchdog Damage")) {
            dmgTicks = 0;
        }
    }

    @Override
    public void onDisable() {
        if (Objects.equals(mode.get(), "Watchdog Fireball")) {
            if (lastSlot != -1) {
                mc.thePlayer.inventory.currentItem = lastSlot;
            }


            ticks = lastSlot = -1;
            setSpeed = stopModules = sentPlace = false;
            initTicks = 0;
        }

        if (Objects.equals(mode.get(), "Old Matrix")) {
            mPacket = false;
            matrixTimer = 0;
            mc.timer.timerSpeed = 1f;
        }

        if (Objects.equals(mode.get(), "Miniblox")) {
            jumped = false;
            currentTimer = 0;
            pauseTimes = 0;
            activeTicks = 0;
            MovementUtils.stop();
        }
        velo = false;
        ticksSinceVelocity = 0;
    }

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        setTag(mode.get());
        switch (mode.get()) {
            case "Old Matrix":
                if (!mPacket) {
                    if (mc.thePlayer.onGround)
                        mc.thePlayer.jump();
                    sendPacketNoEvent(new C03PacketPlayer(false));
                    mPacket = true;
                }
                if (mPacket) {
                    mc.timer.timerSpeed = oMatrixTimer.get();
                    mc.thePlayer.motionX = 1.97 * -Math.sin(MovementUtils.getDirection());
                    mc.thePlayer.motionZ = 1.97 * Math.cos(MovementUtils.getDirection());
                    mc.thePlayer.motionY = 0.42;
                    matrixTimer++;

                    if (matrixTimer >= 3) {
                        toggle();
                    }
                }
                break;

            case "Miniblox":
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), false);
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), false);
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.getKeyCode(), false);
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.getKeyCode(), false);
                activeTicks++;

                if (activeTicks <= 10) {
                    MovementUtils.stop();
                } else {
                    if (!jumped) {
                        if (mc.thePlayer.onGround) {
                            MovementUtils.stop();
                            mc.thePlayer.jump();
                        }

                        jumped = true;
                    } else {
                        int maxTimer = 0;

                        switch (pauseTimes) {
                            case 0:
                                mc.thePlayer.motionX = 1.9 * -Math.sin(MovementUtils.getDirection());
                                mc.thePlayer.motionZ = 1.9 * Math.cos(MovementUtils.getDirection());
                                maxTimer = 10;
                                break;
                            case 1:
                                mc.thePlayer.motionX = 1.285 * -Math.sin(MovementUtils.getDirection());
                                mc.thePlayer.motionZ = 1.285 * Math.cos(MovementUtils.getDirection());
                                maxTimer = 15;
                                break;
                            case 2:
                                mc.thePlayer.motionX = 1.1625 * -Math.sin(MovementUtils.getDirection());
                                mc.thePlayer.motionZ = 1.1625 * Math.cos(MovementUtils.getDirection());
                                maxTimer = 5;
                                break;
                        }

                        mc.thePlayer.motionY = 0.29;
                        currentTimer++;

                        if (Range.between(4, maxTimer).contains(currentTimer)) {
                            MovementUtils.stop();
                        } else if (currentTimer > maxTimer) {
                            pauseTimes++;
                            currentTimer = 0;
                            jumped = false;
                        }
                    }

                    if (pauseTimes >= 3) {
                        MovementUtils.stop();
                        toggle();
                    }
                    break;
                }

            case "Watchdog Fireball":
                if (velo)
                    ticksSinceVelocity++;
                break;

            case "Watchdog Damage":
                if (velo)
                    ticksSinceVelocity++;
                break;
        }
    }

    @EventTarget
    public void onMotion(MotionEvent event) {

        if (event.isPost()) {
            distance += Math.hypot(mc.thePlayer.posX - mc.thePlayer.lastTickPosX, mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ);
        }
        switch (mode.get()) {
            case "Watchdog Fireball":
                if (event.isPre()) {

                    if (velo && mc.thePlayer.onGround) {
                        toggle();
                    }

                    switch (wdFBMode.get()) {
                        case "High":
                            if (mc.thePlayer.hurtTime == 10) {
                                mc.thePlayer.motionY = 1.1f;
                            }

                            if (ticksSinceVelocity <= 80 && ticksSinceVelocity >= 1) {
                                mc.thePlayer.motionY += 0.028f;
                            }

                            if (ticksSinceVelocity == 28) {
                                if (boost.get()) {
                                    MovementUtils.strafe(0.42);
                                }
                                mc.thePlayer.motionY = 0.16f;
                            }
                            if (ticksSinceVelocity >= 35 && ticksSinceVelocity <= 50) {
                                MovementUtils.strafe();
                                mc.thePlayer.posY = mc.thePlayer.posY + .029f;

                            }

                            if (ticksSinceVelocity >= 3 && ticksSinceVelocity <= 50) {
                                MovementUtils.strafe();
                            }
                            break;
                    }

                    if (initTicks == 0) {

                        event.setYaw(mc.thePlayer.rotationYaw - 180);
                        event.setPitch(89);
                        int fireballSlot = getFBSlot();
                        if (fireballSlot != -1 && fireballSlot != mc.thePlayer.inventory.currentItem) {
                            mc.thePlayer.inventory.currentItem = fireballSlot;
                        }
                    }
                    if (initTicks == 1) {

                        if (!sentPlace) {
                            sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                            sentPlace = true;
                        }
                    } else if (initTicks == 2) {

                        if (lastSlot != -1) {
                            mc.thePlayer.inventory.currentItem = lastSlot;
                            lastSlot = -1;
                        }
                    }
                    if (setSpeed) {

                        stopModules = true;
                        MovementUtils.strafe(1.768f);
                        ticks++;
                    }
                    if (initTicks < 3) {
                        initTicks++;
                    }

                    if (setSpeed) {
                        if (ticks > 1) {
                            stopModules = setSpeed = false;
                            ticks = 0;
                            return;
                        }
                        stopModules = true;
                        ticks++;
                        MovementUtils.strafe(1.768f);
                    }
                }

                break;
            case "Watchdog Damage":
                if (event.isPre()) {

                    ++dmgTicks;

                    if (dmgTicks < 44 && dmgTicks % 2 == 0) {
                        event.setX(event.getX() - (double) handleX() * 0.09D);
                        event.setZ(event.getZ() - (double) handleZ() * 0.09D);
                    }

                    if (dmgTicks == 1) {
                        event.setY(event.getY() + 0.034939999302625656D);
                        event.setOnGround(false);
                    } else if (dmgTicks < 45) {
                        event.setY(event.getY() + (dmgTicks % 2 != 0 ? 0.1449999064207077D : Math.random() / 5000.0D));
                        event.setOnGround(false);
                        if (dmgTicks == 44) {
                            MovementUtils.strafe((0.2830 + MovementUtils.getSpeedEffect() * 0.2) - 0.005);
                            mc.thePlayer.jump();
                        }
                    } else if (dmgTicks == 45) {
                        mc.timer.timerSpeed = 0.6f;
                        event.setY(event.getY() + 1.0E-5D);
                        sendPacket(new C03PacketPlayer(true));

                    } else if(dmgTicks == 47){
                        mc.timer.timerSpeed = 1;
                    }

                    if (ticksSinceVelocity > 3 && ticksSinceVelocity < 38 || ticksSinceVelocity > 37 && mc.thePlayer.motionY <= 0.0D) {
                        mc.thePlayer.motionY += 0.02830D;
                    }

                    switch (ticksSinceVelocity) {
                        case 1:
                            mc.thePlayer.motionX *= 2.1;
                            mc.thePlayer.motionZ *= 2.1;
                        case 2:
                        case 3:
                        case 4:
                        case 5:
                        case 8:
                        case 9:
                        case 10:
                        case 11:
                        default:
                            break;
                        case 6:
                            mc.thePlayer.motionY = 0.02999D;
                            break;
                        case 7:
                            mc.thePlayer.motionY = 0.02995D;
                            break;
                        case 12:
                            mc.thePlayer.motionY = 0.0D;
                            break;
                        case 13:
                            mc.thePlayer.motionY += 0.00999D;
                    }

                    if(velo && mc.thePlayer.onGround)
                        toggle();
                }
                break;
        }
    }

    @EventTarget
    public void onStrafe(StrafeEvent event) {
        switch (mode.get()) {

            case "Watchdog Fireball":
                if (ticksSinceVelocity <= 70 && ticksSinceVelocity >= 1) {
                    mc.thePlayer.motionX *= 1.0003;
                    mc.thePlayer.motionZ *= 1.0003;
                }

                if (ticksSinceVelocity == 1) {
                    mc.thePlayer.motionX *= 1.15;
                    mc.thePlayer.motionZ *= 1.15;
                }


                if (mc.thePlayer.hurtTime == 8) {
                    mc.thePlayer.motionX *= 1.02;
                    mc.thePlayer.motionZ *= 1.02;
                }

                if (mc.thePlayer.hurtTime == 7) {
                    mc.thePlayer.motionX *= 1.0004;
                    mc.thePlayer.motionZ *= 1.0004;
                }

                if (mc.thePlayer.hurtTime == 6) {
                    mc.thePlayer.motionX *= 1.0004;
                    mc.thePlayer.motionZ *= 1.0004;
                }

                if (mc.thePlayer.hurtTime == 5) {
                    mc.thePlayer.motionX *= 1.0004;
                    mc.thePlayer.motionZ *= 1.0004;
                }

                if (mc.thePlayer.hurtTime <= 4 && !(mc.thePlayer.hurtTime == 0)) {
                    mc.thePlayer.motionX *= 1.0004;
                    mc.thePlayer.motionZ *= 1.0004;
                }
                break;
        }
    }

    @EventTarget
    public void onPacket(PacketEvent event) {
        Packet<?> packet = event.getPacket();

        if (mode.is("Watchdog Fireball")) {
            if (packet instanceof C08PacketPlayerBlockPlacement c08PacketPlayerBlockPlacement && c08PacketPlayerBlockPlacement.getStack() != null && c08PacketPlayerBlockPlacement.getStack().getItem() instanceof ItemFireball) {
                thrown = true;
                if (mc.thePlayer.onGround) {
                    mc.thePlayer.jump();
                }
            }

            if (packet instanceof S12PacketEntityVelocity s12PacketEntityVelocity) {
                if (s12PacketEntityVelocity.getEntityID() == mc.thePlayer.getEntityId()) {
                    if (thrown) {
                        ticksSinceVelocity = 0;
                        ticks = 0;
                        setSpeed = true;
                        thrown = false;
                        stopModules = true;
                        velo = true;
                    }
                }
            }
        }

        if (mode.is("Watchdog Damage")) {
            if (packet instanceof S12PacketEntityVelocity s12PacketEntityVelocity
                    && s12PacketEntityVelocity.getEntityID() == mc.thePlayer.getEntityId()
                    && !event.isCancelled()
            ) {
                double x = (double) s12PacketEntityVelocity.getMotionX() / 8000.0D;
                double z = (double) s12PacketEntityVelocity.getMotionZ() / 8000.0D;
                double speed = Math.hypot(x, z);
                MovementUtils.strafe(MovementUtils.clamp(speed, 0.44D, 0.48D));
                mc.thePlayer.motionY = (double) s12PacketEntityVelocity.getMotionY() / 8000.0D;
                ticksSinceVelocity = 0;
                event.setCancelled(true);
                velo = true;
            }
        }
    }

    @EventTarget
    public void onRender2D(Render2DEvent event) {
        Fonts.interSemiBold.get(15).drawCenteredString((Math.round(distance * 100.0) / 100.0) + "blocks", (float) event.scaledResolution().getScaledWidth() / 2, (float) event.scaledResolution().getScaledHeight() / 2 - 30, -1);
    }

    @EventTarget
    public void onMove(MoveEvent event) {
        if (mode.is("Watchdog Damage")) {
            if (dmgTicks < 44) {
                event.setZ(0.0D);
                event.setX(0.0D);
            }
        }
    }

    @EventTarget
    public void onJump(JumpEvent event){
        if (mode.is("Watchdog Damage")) {
            if (dmgTicks < 44) {
                event.setCancelled(true);
            }
        }
    }

    @EventTarget
    public void onWorld(WorldEvent event) {
        setEnabled(false);
    }

    private int getFBSlot() {
        for (int i = 36; i <= 44; i++) {
            ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (stack != null && stack.getItem() instanceof ItemFireball) {
                return i - 36;
            }
        }
        return -1;
    }

    public float handleX() {
        return MovementUtils.handleX((float) mc.thePlayer.motionX);
    }

    public float handleZ() {
        return MovementUtils.handleZ((float) mc.thePlayer.motionZ);
    }
}
