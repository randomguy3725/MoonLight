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
package wtf.moonlight.features.modules.impl.combat;

import com.google.common.collect.Lists;
import com.viaversion.viarewind.protocol.protocol1_8to1_9.Protocol1_8To1_9;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import de.florianmichael.viamcp.fixes.AttackOrder;
import net.minecraft.block.BlockAir;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.*;
import net.minecraft.util.*;
import org.apache.commons.lang3.RandomUtils;
import org.lwjglx.input.Keyboard;
import org.lwjglx.input.Mouse;
import wtf.moonlight.Moonlight;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.player.StrafeEvent;
import wtf.moonlight.events.impl.player.UpdateEvent;
import wtf.moonlight.events.impl.render.Render2DEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.modules.impl.visual.Interface;
import wtf.moonlight.features.modules.impl.player.BedNuker;
import wtf.moonlight.features.modules.impl.movement.Scaffold;
import wtf.moonlight.features.values.impl.BoolValue;
import wtf.moonlight.features.values.impl.ModeValue;
import wtf.moonlight.features.values.impl.MultiBoolValue;
import wtf.moonlight.features.values.impl.SliderValue;
import wtf.moonlight.utils.animations.Direction;
import wtf.moonlight.utils.animations.impl.DecelerateAnimation;
import wtf.moonlight.utils.math.MathUtils;
import wtf.moonlight.utils.math.TimerUtils;
import wtf.moonlight.utils.packet.BlinkComponent;
import wtf.moonlight.utils.player.*;

import java.security.SecureRandom;
import java.util.*;

@ModuleInfo(name = "KillAura", category = ModuleCategory.Combat, key = Keyboard.KEY_R)
public class KillAura extends Module {
    private final SliderValue fov = new SliderValue("FOV", 180, 1, 180, this);
    private final ModeValue mode = new ModeValue("Mode", new String[]{"Switch", "Single"}, "Switch", this);
    public final SliderValue switchDelayValue = new SliderValue("SwitchDelay", 15, 0, 20, this, () -> mode.is("Switch"));
    private final ModeValue priority = new ModeValue("Priority", new String[]{"Range", "Armor", "Health", "HurtTime", "FOV"}, "Health", this);
    private final ModeValue aimMode = new ModeValue("Aim Position", new String[]{"Head", "Torso", "Legs", "Nearest", "Test"}, "Nearest", this);
    private final BoolValue inRange = new BoolValue("Rotation In Range", false, this);
    private final SliderValue minAimRange = new SliderValue("Lowest Aim Range", 1, 0, 1, 0.05f, this, inRange::get);
    private final SliderValue maxAimRange = new SliderValue("Highest Aim Range", 1, 0, 1, 0.05f, this, inRange::get);
    private final BoolValue heuristics = new BoolValue("Heuristics", false, this);
    private final BoolValue bruteforce = new BoolValue("Bruteforce", true, this);
    private final BoolValue smartVec = new BoolValue("Smart Vec", true, this);
    private final BoolValue smartRotation = new BoolValue("Smart Rotation", true, this);
    private final BoolValue customRotationSetting = new BoolValue("Custom Rotation Setting", false, this);
    private final ModeValue smoothMode = new ModeValue("Rotations Smooth", RotationUtils.smoothModes, RotationUtils.smoothModes[0], this,() -> customRotationSetting.canDisplay() && customRotationSetting.get());
    private final SliderValue minYawRotSpeed = new SliderValue("Min Yaw Rotation Speed", 45, 1,180,1, this,() -> customRotationSetting.canDisplay() && customRotationSetting.get());
    private final SliderValue minPitchRotSpeed = new SliderValue("Min Pitch Rotation Speed", 45, 1,180,1, this,() -> customRotationSetting.canDisplay() && customRotationSetting.get());
    private final SliderValue maxYawRotSpeed = new SliderValue("Max Yaw Rotation Speed", 90, 1,180,1, this,() -> customRotationSetting.canDisplay() && customRotationSetting.get());
    private final SliderValue maxPitchRotSpeed = new SliderValue("Max Pitch Rotation Speed", 90, 1,180,1, this,() -> customRotationSetting.canDisplay() && customRotationSetting.get());
    private final SliderValue bezierP0 = new SliderValue("Bezier P0", 0f, 0f, 1f,1, this,() -> customRotationSetting.canDisplay() && customRotationSetting.get() && (smoothMode.is(RotationUtils.smoothModes[1]) || smoothMode.is(RotationUtils.smoothModes[8])));
    private final SliderValue bezierP1 = new SliderValue("Bezier P1", 0.05f, 0f, 1f,1, this,() -> customRotationSetting.canDisplay() && customRotationSetting.get() && (smoothMode.is(RotationUtils.smoothModes[1]) || smoothMode.is(RotationUtils.smoothModes[8])));
    private final SliderValue bezierP2 = new SliderValue("Bezier P2", 0.2f, 0f, 1f,1, this,() -> customRotationSetting.canDisplay() && customRotationSetting.get() && (smoothMode.is(RotationUtils.smoothModes[1]) || smoothMode.is(RotationUtils.smoothModes[8])));
    private final SliderValue bezierP3 = new SliderValue("Bezier P3", 0.4f, 0f, 1f,1, this,() -> customRotationSetting.canDisplay() && customRotationSetting.get() && (smoothMode.is(RotationUtils.smoothModes[1]) || smoothMode.is(RotationUtils.smoothModes[8])));
    private final SliderValue bezierP4 = new SliderValue("Bezier P4", 0.6f, 0f, 1f,1, this,() -> customRotationSetting.canDisplay() && customRotationSetting.get() && (smoothMode.is(RotationUtils.smoothModes[1]) || smoothMode.is(RotationUtils.smoothModes[8])));
    private final SliderValue bezierP5 = new SliderValue("Bezier P5", 0.8f, 0f, 1f,1, this,() -> customRotationSetting.canDisplay() && customRotationSetting.get() && (smoothMode.is(RotationUtils.smoothModes[1]) || smoothMode.is(RotationUtils.smoothModes[8])));
    private final SliderValue bezierP6 = new SliderValue("Bezier P6", 0.95f, 0f, 1f,1, this,() -> customRotationSetting.canDisplay() && customRotationSetting.get() && (smoothMode.is(RotationUtils.smoothModes[1]) || smoothMode.is(RotationUtils.smoothModes[8])));
    private final SliderValue bezierP7 = new SliderValue("Bezier P7", 0.1f, 0f, 1f,1, this,() -> customRotationSetting.canDisplay() && customRotationSetting.get() && (smoothMode.is(RotationUtils.smoothModes[1]) || smoothMode.is(RotationUtils.smoothModes[8])));
    private final SliderValue elasticity = new SliderValue("Elasticity", 0.3f, 0.1f, 1f,0.01f, this,() -> customRotationSetting.canDisplay() && customRotationSetting.get() && smoothMode.is(RotationUtils.smoothModes[7]));
    private final SliderValue dampingFactor = new SliderValue("Damping Factor", 0.5f, 0.1f, 1f,0.01f, this,() -> customRotationSetting.canDisplay() && customRotationSetting.get() && smoothMode.is(RotationUtils.smoothModes[7]));
    private final SliderValue keepLength = new SliderValue("Keep Length", 1, 0, 20,1, this);
    public final BoolValue smoothlyResetRotation = new BoolValue("Smoothly Reset Rotation", true, this,() -> customRotationSetting.canDisplay() && customRotationSetting.get());
    private final BoolValue randomize = new BoolValue("Randomize", false, this);
    public final ModeValue randomizerot = new ModeValue("RandomizeRotation", new String[]{"Random","Advanced"}, "Noise", this, randomize::get);
    public final SliderValue yawStrength = new SliderValue("YawStrength",5f,1,35f,this, () -> this.randomize.get() && this.randomizerot.is("Random") || this.randomizerot.is("RandomSecure"));
    public final SliderValue pitchStrength = new SliderValue("PitchStrength",5f,1,35f,this, () -> this.randomize.get() && this.randomizerot.is("Random") || this.randomizerot.is("RandomSecure"));

    public final MultiBoolValue rdadvanceaddons = new MultiBoolValue("Random Addons", Arrays.asList(new BoolValue("Sin Cos Random", true),
            new BoolValue("Randomize", false)),this,randomize::get);

    public final SliderValue frequency = new SliderValue("SpeedSinCos", 1.5f, 0f, 5.0f, 0.01f, this,() -> this.randomizerot.is("Advanced") && this.rdadvanceaddons.isEnabled("Sin Cos Random"));
    public final SliderValue yStrengthAimPattern = new SliderValue("YStrengthAmplitudeSinCos", 3.5f, 0f, 15.0f, 0.01f, this, () -> this.randomizerot.is("Advanced") && this.rdadvanceaddons.isEnabled("Sin Cos Random"));
    public final SliderValue xStrengthAimPattern = new SliderValue("XStrengthAmplitudeSinCos", 3.5f, 0f, 15.0f, 0.01f, this, () -> this.randomizerot.is("Advanced") && this.rdadvanceaddons.isEnabled("Sin Cos Random"));

    public final SliderValue yawStrengthAddon = new SliderValue("Yaw Strength Randomize",5f,1,35f,this, () -> this.randomizerot.is("Advanced") && this.rdadvanceaddons.isEnabled("Randomize"));
    public final SliderValue pitchStrengthAddon = new SliderValue("Pitch Strength Randomize",5f,1,35f,this, () -> this.randomizerot.is("Advanced") &&  this.rdadvanceaddons.isEnabled("Randomize"));

    private final SliderValue minAps = new SliderValue("Min Aps", 9, 1, 20, this);
    private final SliderValue maxAps = new SliderValue("Max Aps", 11, 1, 20, this);
    private final ModeValue apsMode = new ModeValue("Aps Mode", new String[]{"Random", "Secure Random", "Full Random"}, "Random", this);
    public final ModeValue rangeMode = new ModeValue("Range Mode",new String[]{"Client","Client 2","Client 3","Server Test"},"Client",this);
    public final SliderValue searchRange = new SliderValue("Search Range", 6.0F, 2.0F, 16F, .1f, this);
    private final BoolValue pauseRotations = new BoolValue("Pause Rotations", false, this);
    private final SliderValue pauseRange = new SliderValue("Pause Range", 0.5f, 0.1f, 6, 0.1f, this, pauseRotations::get);
    public final SliderValue rotationRange = new SliderValue("Rotation Range", 3.0F, 2.0F, 16F, .1f, this);
    public final BoolValue preSwingWithRotationRange = new BoolValue("Pre Swing With Rotation Range", true, this);
    public final MultiBoolValue addons = new MultiBoolValue("Addons", Arrays.asList(new BoolValue("Movement Fix", false), new BoolValue("Perfect Hit", false), new BoolValue("Ray Cast", true), new BoolValue("Hit Select", false)), this);
    public final SliderValue attackRange = new SliderValue("Attack Range", 3.0F, 2.0F, 6F, .1f, this);
    public final SliderValue hitSelectRange = new SliderValue("Hit Select Range", 3.0F, 2.0F, 6F, .1f, this,() -> addons.isEnabled("Hit Select"));
    public final BoolValue auto = new BoolValue("Auto", false, this,() -> addons.isEnabled("Hit Select"));
    public final BoolValue sprintCheck = new BoolValue("Sprint Check", false, this,() -> addons.isEnabled("Hit Select") && auto.get());
    public final SliderValue wallAttackRange = new SliderValue("Wall Attack Range", 0.0F, 0.0F, 6F, .1f, this);
    public final SliderValue blockRange = new SliderValue("Block Range", 5.0F, 2.0F, 16F, .1f, this);
    public final ModeValue autoBlock = new ModeValue("AutoBlock", new String[]{"None", "Vanilla", "HYT", "Watchdog", "Release", "Interact"}, "Fake", this);
    public final BoolValue interact = new BoolValue("Interact", false, this, () -> !autoBlock.is("None"));
    public final BoolValue via = new BoolValue("Via", false, this, () -> !autoBlock.is("None"));
    public final BoolValue slow = new BoolValue("Slowdown", false, this, () -> !autoBlock.is("None"));
    public final SliderValue releaseBlockRate = new SliderValue("Block Rate", 100, 1, 100, 1, this, () -> autoBlock.is("Release"));
    public final BoolValue forceDisplayBlocking = new BoolValue("Force Display Blocking", false, this);
    private final MultiBoolValue targetOption = new MultiBoolValue("Targets", Arrays.asList(new BoolValue("Players", true), new BoolValue("Mobs", false),
            new BoolValue("Animals", false), new BoolValue("Invisible", true), new BoolValue("Dead", false)), this);
    public final MultiBoolValue filter = new MultiBoolValue("Filter", Arrays.asList(new BoolValue("Teams", true), new BoolValue("Friends", true)), this);
    public final ModeValue movementFix = new ModeValue("Movement", new String[]{"Silent", "Strict"}, "Silent", this, () -> addons.isEnabled("Movement Fix"));
    public final BoolValue noScaffold = new BoolValue("No Scaffold", false, this);
    public final BoolValue noInventory = new BoolValue("No Inventory", false, this);
    public final BoolValue noBedNuker = new BoolValue("No Bed Nuker", false, this);
    public final List<EntityLivingBase> targets = new ArrayList<>();
    public EntityLivingBase target;
    private final TimerUtils attackTimer = new TimerUtils();
    private final TimerUtils switchTimer = new TimerUtils();
    private final TimerUtils perfectHitTimer = new TimerUtils();
    private final TimerUtils afterHitSelectTimer = new TimerUtils();
    private int index;
    private int clicks;
    private int maxClicks;
    public boolean isBlocking;
    public boolean renderBlocking;
    public boolean blinked;
    public int blinkTicks;
    public float[] prevRotation;
    public Vec3 prevVec;
    public Vec3 currentVec;
    public Vec3 targetVec;
    public boolean doHitSelect = false;
    public boolean autoHitSelect;

    public float[] rotation;

    @Override
    public void onEnable() {
        clicks = 0;
        attackTimer.reset();
    }

    @Override
    public void onDisable() {
        unblock();
        if (renderBlocking) {
            renderBlocking = false;
        }
        if (blinked) {
            BlinkComponent.dispatch();
        }
        target = null;
        targets.clear();
        index = 0;
        switchTimer.reset();
        prevRotation = rotation = null;
        prevVec = currentVec = targetVec = null;
        blinkTicks = 0;
        Iterator<Map.Entry<EntityPlayer, DecelerateAnimation>> iterator = getModule(Interface.class).animationEntityPlayerMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<EntityPlayer, DecelerateAnimation> entry = iterator.next();
            DecelerateAnimation animation = entry.getValue();

            animation.setDirection(Direction.BACKWARDS);
            if (animation.finished(Direction.BACKWARDS)) {
                iterator.remove();
            }
        }
    }

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        targets.clear();

        if ((target == null || !shouldBlock()) && renderBlocking) {
            renderBlocking = false;
        }

        setTag(mode.get());

        if (((isEnabled(Scaffold.class) && noScaffold.get() ||
                !noScaffold.get() && isEnabled(Scaffold.class) && mc.theWorld.getBlockState(getModule(Scaffold.class).data.blockPos).getBlock() instanceof BlockAir) ||
                noInventory.get() && mc.currentScreen instanceof GuiContainer ||
                (noBedNuker.get() && isEnabled(BedNuker.class) && getModule(BedNuker.class).bedPos != null || !noBedNuker.get() && isEnabled(BedNuker.class) && getModule(BedNuker.class).rotate)
        ) && target != null) {
            if (blinked) {
                BlinkComponent.dispatch();
                blinked = false;
            }
        }

        getTargets();
        if (!targets.isEmpty()) {
            if (targets.size() > 1) {
                switch (priority.get()) {
                    case "Armor":
                        targets.sort(Comparator.comparingInt(EntityLivingBase::getTotalArmorValue));
                        break;
                    case "Range":
                        targets.sort(Comparator.comparingDouble(mc.thePlayer::getDistanceToEntity));
                        break;
                    case "Health":
                        targets.sort(Comparator.comparingDouble(EntityLivingBase::getHealth));
                        break;
                    case "HurtTime":
                        targets.sort(Comparator.comparingInt(entity -> entity.hurtTime));
                        break;
                    case "FOV":
                        targets.sort(Comparator.comparingDouble(RotationUtils::distanceFromYaw));
                        break;
                }
            }

            if (switchTimer.hasTimeElapsed((long) (switchDelayValue.get() * 100L)) && targets.size() > 1) {
                ++index;
                switchTimer.reset();
            }

            if (index >= targets.size()) {
                index = 0;
            }
            target = targets.get(Objects.equals(mode.get(), "Switch") ? index : 0);

        } else {
            target = null;
            prevRotation = rotation = null;
            prevVec = currentVec = targetVec = null;
            unblock();
            if (blinked) {
                BlinkComponent.dispatch();
                blinked = false;
            }
            clicks = 0;
            blinkTicks = 0;
            return;
        }

        if (mc.thePlayer.isSpectator() || mc.thePlayer.isDead || (isEnabled(Scaffold.class) && noScaffold.get() ||
                !noScaffold.get() && isEnabled(Scaffold.class) && mc.theWorld.getBlockState(getModule(Scaffold.class).data.blockPos).getBlock() instanceof BlockAir) ||
                noInventory.get() && mc.currentScreen instanceof GuiContainer ||
                (noBedNuker.get() && isEnabled(BedNuker.class) && getModule(BedNuker.class).bedPos != null || !noBedNuker.get() && isEnabled(BedNuker.class) && getModule(BedNuker.class).rotate)
        ) return;

        if (target != null) {

            if (PlayerUtils.getDistanceToEntityBox(target) < rotationRange.get()) {
                if (PlayerUtils.getDistanceToEntityBox(target) < pauseRange.get() && pauseRotations.get()) {
                    return;
                }

                rotation = calcToEntity(target);
                if (customRotationSetting.canDisplay() && customRotationSetting.get()) {
                    RotationUtils.setRotation(rotation,
                            smoothMode.get(),
                            addons.isEnabled("Movement Fix") ? movementFix.is("Strict") ? MovementCorrection.STRICT : MovementCorrection.SILENT : MovementCorrection.OFF,
                            minYawRotSpeed.get(), maxYawRotSpeed.get(), minPitchRotSpeed.get(), maxPitchRotSpeed.get(),
                            bezierP0.get(),
                            bezierP1.get(),
                            bezierP2.get(),
                            bezierP3.get(),
                            bezierP4.get(),
                            bezierP5.get(),
                            bezierP6.get(),
                            bezierP7.get(),
                            elasticity.get(),
                            dampingFactor.get(),
                            (int) keepLength.get(), smoothlyResetRotation.get());
                } else {
                    RotationUtils.setRotation(rotation, addons.isEnabled("Movement Fix") ? movementFix.is("Strict") ? MovementCorrection.STRICT : MovementCorrection.SILENT : MovementCorrection.OFF, (int) keepLength.get());
                }


                prevRotation = rotation;

                if (preSwingWithRotationRange.get()) {
                    if (PlayerUtils.getDistanceToEntityBox(target) <= (mc.thePlayer.canEntityBeSeen(target) ? rotationRange.get() : 0) &&
                            PlayerUtils.getDistanceToEntityBox(target) > (!mc.thePlayer.canEntityBeSeen(target) ? wallAttackRange.get() : attackRange.get())
                    ) {
                        maxClicks = clicks;

                        for (int i = 0; i < maxClicks; i++) {
                            mc.thePlayer.swingItem();
                            clicks--;
                        }
                    }
                }
            }

            if (shouldBlock()) {
                renderBlocking = true;
            }

            if (preTickBlock()) return;

            if (clicks == 0) return;

            if (isBlocking || autoBlock.is("HYT"))
                if (preAttack()) return;

            if (shouldAttack()) {
                maxClicks = clicks;
                for (int i = 0; i < maxClicks; i++) {
                    attack();
                    clicks--;
                }
            }

            if (!autoBlock.is("None") && (shouldBlock() || autoBlock.is("HYT"))) {
                if (Mouse.isButtonDown(2))
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
                postAttack();
            }
        }
    }

    @EventTarget
    public void onStrafe(StrafeEvent event) {
        if (auto.canDisplay() && auto.get() && target != null && shouldAttack() && target.hurtTime < 6 && !mc.gameSettings.keyBindJump.isKeyDown() && !checks() && mc.thePlayer.onGround && (sprintCheck.get() && MovementUtils.canSprint(true) || !sprintCheck.get())) {
            mc.thePlayer.jump();
            if (mc.thePlayer.offGroundTicks >= 4)
                autoHitSelect = true;
        } else {
            autoHitSelect = false;
        }
    }

    @EventTarget
    public void onRender2D(Render2DEvent event) {
        double min = minAps.get();
        double max = maxAps.get();
        switch (apsMode.get()) {
            case "Random":
                if (attackTimer.hasTimeElapsed(1000L / (MathUtils.nextInt((int) min, (int) max))) && target != null) {
                    clicks++;
                    attackTimer.reset();
                }
                break;

            case "Secure Random": {
                double time = MathHelper.clamp_double(
                        min + ((max - min) * new SecureRandom().nextDouble()), min, max);

                if (attackTimer.hasTimeElapsed((float) (1000L / time))) {
                    clicks++;
                    attackTimer.reset();
                }
                break;
            }
            case "Full Random": {
                min *= MathUtils.nextDouble(0, 1);
                max *= MathUtils.nextDouble(0, 1);

                double time = (max / min) * (MathUtils.nextDouble(min, max));

                if (attackTimer.hasTimeElapsed((float) (1000L / time))) {
                    clicks++;
                    attackTimer.reset();
                }

                break;
            }
        }
    }

    private boolean preTickBlock() {
        switch (autoBlock.get()) {
            case "Watchdog":
                if (blinkTicks >= 3) {
                    blinkTicks = 0;
                }
                blinkTicks++;
                switch (blinkTicks) {
                    case 0:
                        return true;
                    case 1:
                        if(isBlocking) {
                            BlinkComponent.blinking = true;
                            unblock();
                            blinked = true;
                            return true;
                        }
                    case 2:
                        return false;
                }
                break;
        }
        return false;
    }

    private boolean preAttack() {
        switch (autoBlock.get()) {
            case "Release":
                if (clicks + 1 == maxClicks) {
                    if (!(releaseBlockRate.get() > 0 && RandomUtils.nextInt(0, 100) <= releaseBlockRate.get()))
                        break;
                    block();
                    isBlocking = true;
                }
                break;

            case "Interact":
                if (isBlocking) {
                    unblock();
                    return true;
                }
                break;
            case "HYT":
                if (this.isBlocking && !getModule(AutoGap.class).eating) {
                    unblock();
                }

                if (isBlocking) {
                    unblock();
                }
                break;
        }
        return false;
    }

    private void postAttack() {
        switch (autoBlock.get()) {
            case "Vanilla":
                block();
                break;
            case "Interact":
                block(true);
                break;
            case "HYT":
                if (!shouldBlock() && isBlocking)
                    unblock();
                break;
            case "Watchdog":
                block(true);
                BlinkComponent.dispatch();
                break;
        }
    }

    private void block() {
        block(interact.get());
    }

    public void block(boolean interact) {
        if (!isBlocking) {

            if (interact) {
                sendPacket(new C02PacketUseEntity(target, C02PacketUseEntity.Action.INTERACT));
            }

            if (via.get()) {
                if (!getModule(AutoGap.class).eating && ViaLoadingBase.getInstance().getTargetVersion().getVersion() > 47) {
                    sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                    PacketWrapper useItem = PacketWrapper.create(29, null, Via.getManager().getConnectionManager().getConnections().iterator().next());
                    useItem.write(Type.VAR_INT, 1);
                    com.viaversion.viarewind.utils.PacketUtil.sendToServer(useItem, Protocol1_8To1_9.class, true, true);
                }
            } else {
                sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
            }
            isBlocking = true;
        }
    }

    public void unblock() {
        if (isBlocking) {
            if (mode.is("HYT")// || mode.is("Watchdog")
            ) {
                sendPacket(new C09PacketHeldItemChange((mc.thePlayer.inventory.currentItem + 1) % 8));
                sendPacket(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
            } else {
                sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
            }
            isBlocking = false;
        }
    }

    public void attack() {
        if (autoBlock.is("Release"))
            unblock();
        MovingObjectPosition rayCast = RotationUtils.rayCast(RotationUtils.currentRotation, attackRange.get());
        if (addons.isEnabled("Ray Cast") && rayCast.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY && rayCast.entityHit instanceof EntityLivingBase) {
            if (canAttack((EntityLivingBase) rayCast.entityHit)) {
                if (getModule(AutoGap.class).isEnabled() && getModule(AutoGap.class).alwaysAttack.get() && getModule(AutoGap.class).eating) {
                    AttackOrder.sendFixedAttackNoPacketEvent(mc.thePlayer, rayCast.entityHit);
                } else {
                    AttackOrder.sendFixedAttack(mc.thePlayer, rayCast.entityHit);
                }
            }
        } else {
            if (canAttack(target)) {
                if (getModule(AutoGap.class).isEnabled() && getModule(AutoGap.class).alwaysAttack.get() && getModule(AutoGap.class).eating) {
                    AttackOrder.sendFixedAttackNoPacketEvent(mc.thePlayer, target);
                } else {
                    AttackOrder.sendFixedAttack(mc.thePlayer, target);
                }
            }
        }
        perfectHitTimer.reset();
    }

    public boolean canAttack(EntityLivingBase entity) {
        if(addons.isEnabled("Hit Select")) {
            if (mc.thePlayer.hurtTime < 5 || autoHitSelect) {
                if (this.getDistanceToEntity(this.target) < this.hitSelectRange.get()) {
                    this.doHitSelect = true;
                }
            } else {
                this.doHitSelect = false;
            }
            if (this.getDistanceToEntity(this.target) > this.hitSelectRange.get()) {
                this.doHitSelect = false;
            } else if (this.afterHitSelectTimer.hasTimeElapsed(900L)) {
                this.doHitSelect = false;
                this.afterHitSelectTimer.reset();
            }

            if(!doHitSelect)
                return false;
        }

        if(addons.isEnabled("Perfect Hit"))
            return (entity.hurtTime <= 2 || perfectHitTimer.hasTimeElapsed(900));

        return true;
    }

    public boolean isHoldingSword() {
        return mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword;
    }

    private boolean checks() {
        return mc.thePlayer.isInLava() || mc.thePlayer.isBurning() || mc.thePlayer.isInWater() || mc.thePlayer.isInWeb;
    }

    private void getTargets() {
        for (final Entity entity : mc.theWorld.loadedEntityList) {
            if (entity instanceof EntityLivingBase e) {
                if (isValid(e) && PlayerUtils.getDistanceToEntityBox(e) <= searchRange.get() && (RotationUtils.getRotationDifference(e) <= fov.get() || fov.get() == 180))
                    targets.add(e);
                else
                    targets.remove(e);
            }
        }
    }

    public double getDistanceToEntity(Entity entity) {
        switch (rangeMode.get()) {
            case "Client":
                return PlayerUtils.getDistanceToEntityBox(entity);
            case "Server":
                double x = (double) entity.serverPosX / 32.0D;
                double y = (double) entity.serverPosY / 32.0D;
                double z = (double) entity.serverPosZ / 32.0D;
                return new Vec3(x, y, z).getDistanceAtEyeByVec(mc.thePlayer, mc.thePlayer.posX + mc.thePlayer.getCollisionBorderSize(), mc.thePlayer.posY, mc.thePlayer.posZ + mc.thePlayer.getCollisionBorderSize());
            case "Client 2":
                return entity.getDistance(mc.thePlayer.getPositionEyes(1));
            case "Client 3":
                return PlayerUtils.calculatePerfectRangeToEntity(entity);
        }
        return 0;
    }

    public boolean isValid(Entity entity) {
        if ((filter.isEnabled("Teams") && PlayerUtils.isInTeam(entity))) {
            return false;
        }
        if (entity instanceof EntityLivingBase && (targetOption.isEnabled("Dead") || entity.isEntityAlive()) && entity != mc.thePlayer) {
            if (targetOption.isEnabled("Invisible") || !entity.isInvisible()) {
                if (targetOption.isEnabled("Players") && entity instanceof EntityPlayer) {
                    if (filter.isEnabled("Friends") && Moonlight.INSTANCE.getFriendManager().isFriend((EntityPlayer) entity))
                        return false;
                    return !isEnabled(AntiBot.class) || !getModule(AntiBot.class).isBot((EntityPlayer) entity);
                }
            }
            return (targetOption.isEnabled("Mobs") && PlayerUtils.isMob(entity)) || (targetOption.isEnabled("Animals") && PlayerUtils.isAnimal(entity));
        }
        return false;
    }

    public boolean shouldAttack() {
        return PlayerUtils.getDistanceToEntityBox(target) <= (!mc.thePlayer.canEntityBeSeen(target) ? wallAttackRange.get() : attackRange.get());
    }

    public boolean shouldBlock() {
        return PlayerUtils.getDistanceToEntityBox(target) <= blockRange.get() && isHoldingSword();
    }

    public float[] calcToEntity(EntityLivingBase entity) {

        prevVec = currentVec;

        Vec3 playerPos = mc.thePlayer.getPositionEyes(1);
        Vec3 entityPos = entity.getPositionVector();
        AxisAlignedBB boundingBox = entity.getEntityBoundingBox();

        switch (aimMode.get()) {
            case "Head":
                targetVec = entityPos.add(0.0, entity.getEyeHeight(), 0.0);
                break;
            case "Torso":
                targetVec = entityPos.add(0.0, entity.height * 0.75, 0.0);
                break;
            case "Legs":
                targetVec = entityPos.add(0.0, entity.height * 0.45, 0.0);
                break;
            case "Nearest":
                targetVec = RotationUtils.getBestHitVec(entity);
                break;
            case "Test":

                Vec3 test = new Vec3(entity.posX, entity.posY, entity.posZ);

                double diffY;
                for (diffY = boundingBox.minY + 0.7D; diffY < boundingBox.maxY - 0.1D; diffY += 0.1D) {
                    if (mc.thePlayer.getPositionEyes(1).distanceTo(new Vec3(entity.posX, diffY, entity.posZ)) < mc.thePlayer.getPositionEyes(1).distanceTo(test)) {
                        test = new Vec3(entity.posX, diffY, entity.posZ);
                    }
                }
                targetVec = test;
                break;
            default:
                targetVec = entityPos;
        }

        if (heuristics.get()) {
            targetVec = RotationUtils.heuristics(entity, targetVec);
        }

        if (bruteforce.get()) {
            if (RotationUtils.rayCast(RotationUtils.getRotations(targetVec), rotationRange.get()).typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY) {
                final double xWidth = boundingBox.maxX - boundingBox.minX;
                final double zWidth = boundingBox.maxZ - boundingBox.minZ;
                final double height = boundingBox.maxY - boundingBox.minY;
                for (double x = 0.0; x < 1.0; x += 0.2) {
                    for (double y = 0.0; y < 1.0; y += 0.2) {
                        for (double z = 0.0; z < 1.0; z += 0.2) {
                            final Vec3 hitVec = new Vec3(boundingBox.minX + xWidth * x, boundingBox.minY + height * y, boundingBox.minZ + zWidth * z);
                            if (RotationUtils.rayCast(RotationUtils.getRotations(hitVec), rotationRange.get()).typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
                                targetVec = hitVec;
                            }
                        }
                    }
                }
            }
        }

        if (inRange.get()) {
            double minAimY = entity.posY + entity.getEyeHeight() * minAimRange.get();
            double maxAimY = entity.posY + entity.getEyeHeight() * maxAimRange.get();

            if (RotationUtils.getBestHitVec(entity).yCoord < minAimY) {
                targetVec.yCoord = minAimY;
            }

            if (RotationUtils.getBestHitVec(entity).yCoord > maxAimY) {
                targetVec.yCoord = maxAimY;
            }
        }

        currentVec = targetVec;

        if (smartVec.get()) {
            MovingObjectPosition test = RotationUtils.rayCast(RotationUtils.getRotations(prevVec), rotationRange.get());
            if (test.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY && (addons.isEnabled("Ray Cast") || !addons.isEnabled("Ray Cast") && test.entityHit == target)) {
                currentVec = prevVec;
            }
        }

        double deltaX = currentVec.xCoord - playerPos.xCoord;
        double deltaY = currentVec.yCoord - playerPos.yCoord;
        double deltaZ = currentVec.zCoord - playerPos.zCoord;

        float yaw = (float) -(Math.atan2(deltaX, deltaZ) * (180.0 / Math.PI));
        float pitch = (float) (-Math.toDegrees(Math.atan2(deltaY, Math.hypot(deltaX, deltaZ))));

        if(this.randomize.get()) {
            switch (this.randomizerot.get()) {
                case "Random" -> {
                    yaw += MathUtils.randomizeDouble(-this.yawStrength.get(), this.yawStrength.get());
                    pitch += MathUtils.randomizeDouble(-this.pitchStrength.get(), this.pitchStrength.get());
                }
                case "Advanced" -> {
                    if(rdadvanceaddons.isEnabled("Sin Cos Random")) {
                        double time = System.currentTimeMillis() / 1000.0D;
                        double frequency = this.frequency.get();
                        double yawAmplitude = this.xStrengthAimPattern.get();
                        double pitchAmplitude = this.yStrengthAimPattern.get();

                        yaw += (Math.sin(time * frequency) * yawAmplitude);
                        pitch += (float) (Math.cos(time * frequency) * pitchAmplitude);
                    }

                    if(rdadvanceaddons.isEnabled("Randomize")) {
                        yaw += MathUtils.randomizeDouble(-this.yawStrengthAddon.get(), this.yawStrengthAddon.get());
                        pitch += MathUtils.randomizeDouble(-this.pitchStrengthAddon.get(), this.pitchStrengthAddon.get());
                    }
                }
            }
        }

        if (smartRotation.get() && prevRotation != null) {
            MovingObjectPosition test = RotationUtils.rayCast(prevRotation, rotationRange.get());
            MovingObjectPosition test2 = RotationUtils.rayCast(new float[]{yaw, prevRotation[1]}, rotationRange.get());
            MovingObjectPosition test3 = RotationUtils.rayCast(new float[]{prevRotation[0], pitch}, rotationRange.get());

            if (test.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY && (addons.isEnabled("Ray Cast") || !addons.isEnabled("Ray Cast") && test.entityHit == target)) {
                return prevRotation;
            }

            if (test2.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY && (addons.isEnabled("Ray Cast") || !addons.isEnabled("Ray Cast") && test.entityHit == target)) {
                return new float[]{yaw, prevRotation[1]};
            }
            if (test3.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY && (addons.isEnabled("Ray Cast") || !addons.isEnabled("Ray Cast") && test.entityHit == target)) {
                return new float[]{prevRotation[0], pitch};
            }
        }

        pitch = MathHelper.clamp_float(pitch,-90,90);

        return new float[]{yaw, pitch};
    }
}