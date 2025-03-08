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

import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections.BlockData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.potion.Potion;
import net.minecraft.util.*;
import org.lwjglx.input.Keyboard;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.misc.WorldEvent;
import wtf.moonlight.events.impl.packet.PacketEvent;
import wtf.moonlight.events.impl.player.*;
import wtf.moonlight.events.impl.render.Render3DEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.modules.impl.combat.KillAura;
import wtf.moonlight.features.modules.impl.visual.Interface;
import wtf.moonlight.features.values.impl.BoolValue;
import wtf.moonlight.features.values.impl.ModeValue;
import wtf.moonlight.features.values.impl.MultiBoolValue;
import wtf.moonlight.features.values.impl.SliderValue;
import wtf.moonlight.utils.math.MathUtils;
import wtf.moonlight.utils.misc.SpoofSlotUtils;
import wtf.moonlight.utils.player.*;
import wtf.moonlight.utils.render.RenderUtils;

import java.util.*;

@ModuleInfo(name = "Scaffold", category = ModuleCategory.Movement)
public class Scaffold extends Module {
    private final ModeValue switchBlock = new ModeValue("Switch Block", new String[]{"Silent", "Switch", "Spoof"}, "Spoof", this);
    private final BoolValue biggestStack = new BoolValue("Biggest Stack", false, this);
    private final ModeValue mode = new ModeValue("Mode", new String[]{"Normal", "Telly", "Snap", "Watchdog"}, "Normal", this);
    private final SliderValue minTellyTicks = new SliderValue("Min Telly Ticks", 2, 1, 5, this, () -> mode.is("Telly"));
    private final SliderValue maxTellyTicks = new SliderValue("Max Telly Ticks", 4, 1, 5, this, () -> mode.is("Telly"));
    private final ModeValue rotations = new ModeValue("Rotations", new String[]{"Normal", "Normal 2","Normal 3", "Strict", "God Bridge", "Reverse", "Custom", "Unfair Pitch", "Hypixel", "Derp"}, "Normal", this);
    private final SliderValue customYaw = new SliderValue("Custom Yaw", 180, 0, 180, 1, this, () -> rotations.is("Custom"));
    private final SliderValue minPitch = new SliderValue("Min Pitch Range", 55, 50, 90, .1f, this, () -> rotations.is("Custom") || rotations.is("God Bridge"));
    public final SliderValue maxPitch = new SliderValue("Max Pitch Range", 75, 50, 90, .1f, this, () -> rotations.is("Custom") || rotations.is("God Bridge"));
    private final BoolValue customRotationSetting = new BoolValue("Custom Rotation Setting", false, this);
    private final ModeValue smoothMode = new ModeValue("Rotations Smooth", RotationUtils.smoothModes, RotationUtils.smoothModes[0], this, customRotationSetting::get);
    private final SliderValue minYawRotSpeed = new SliderValue("Min Yaw Rotation Speed", 45, 1, 180, 1, this, customRotationSetting::get);
    private final SliderValue minPitchRotSpeed = new SliderValue("Min Pitch Rotation Speed", 45, 1, 180, 1, this, customRotationSetting::get);
    private final SliderValue maxYawRotSpeed = new SliderValue("Max Yaw Rotation Speed", 90, 1, 180, 1, this, customRotationSetting::get);
    private final SliderValue maxPitchRotSpeed = new SliderValue("Max Pitch Rotation Speed", 90, 1, 180, 1, this, customRotationSetting::get);
    private final SliderValue bezierP0 = new SliderValue("Bezier P0", 0f, 0f, 1f, 1, this, () -> customRotationSetting.canDisplay() && customRotationSetting.get() && (smoothMode.is(RotationUtils.smoothModes[1]) || smoothMode.is(RotationUtils.smoothModes[8])));
    private final SliderValue bezierP1 = new SliderValue("Bezier P1", 0.05f, 0f, 1f, 1, this, () -> customRotationSetting.canDisplay() && customRotationSetting.get() && (smoothMode.is(RotationUtils.smoothModes[1]) || smoothMode.is(RotationUtils.smoothModes[8])));
    private final SliderValue bezierP2 = new SliderValue("Bezier P2", 0.2f, 0f, 1f, 1, this, () -> customRotationSetting.canDisplay() && customRotationSetting.get() && (smoothMode.is(RotationUtils.smoothModes[1]) || smoothMode.is(RotationUtils.smoothModes[8])));
    private final SliderValue bezierP3 = new SliderValue("Bezier P3", 0.4f, 0f, 1f, 1, this, () -> customRotationSetting.canDisplay() && customRotationSetting.get() && (smoothMode.is(RotationUtils.smoothModes[1]) || smoothMode.is(RotationUtils.smoothModes[8])));
    private final SliderValue bezierP4 = new SliderValue("Bezier P4", 0.6f, 0f, 1f, 1, this, () -> customRotationSetting.canDisplay() && customRotationSetting.get() && (smoothMode.is(RotationUtils.smoothModes[1]) || smoothMode.is(RotationUtils.smoothModes[8])));
    private final SliderValue bezierP5 = new SliderValue("Bezier P5", 0.8f, 0f, 1f, 1, this, () -> customRotationSetting.canDisplay() && customRotationSetting.get() && (smoothMode.is(RotationUtils.smoothModes[1]) || smoothMode.is(RotationUtils.smoothModes[8])));
    private final SliderValue bezierP6 = new SliderValue("Bezier P6", 0.95f, 0f, 1f, 1, this, () -> customRotationSetting.canDisplay() && customRotationSetting.get() && (smoothMode.is(RotationUtils.smoothModes[1]) || smoothMode.is(RotationUtils.smoothModes[8])));
    private final SliderValue bezierP7 = new SliderValue("Bezier P7", 0.1f, 0f, 1f, 1, this, () -> customRotationSetting.canDisplay() && customRotationSetting.get() && (smoothMode.is(RotationUtils.smoothModes[1]) || smoothMode.is(RotationUtils.smoothModes[8])));
    private final SliderValue elasticity = new SliderValue("Elasticity", 0.3f, 0.1f, 1f, 0.01f, this, () -> customRotationSetting.canDisplay() && customRotationSetting.get() && smoothMode.is(RotationUtils.smoothModes[7]));
    private final SliderValue dampingFactor = new SliderValue("Damping Factor", 0.5f, 0.1f, 1f, 0.01f, this, () -> customRotationSetting.canDisplay() && customRotationSetting.get() && smoothMode.is(RotationUtils.smoothModes[7]));
    public final BoolValue smoothlyResetRotation = new BoolValue("Smoothly Reset Rotation", true, this, customRotationSetting::get);
    private final MultiBoolValue addons = new MultiBoolValue("Addons", Arrays.asList(
            new BoolValue("Sprint", true),
            new BoolValue("Swing", true),
            new BoolValue("Movement Fix", true),
            new BoolValue("Ray Trace", true),
            new BoolValue("Keep Y", false),
            new BoolValue("Speed Keep Y", false),
            new BoolValue("Safe Walk", false),
            new BoolValue("Safe Walk When No Data", false),
            new BoolValue("AD Strafe", false),
            new BoolValue("Hover", false),
            new BoolValue("Sneak", false),
            new BoolValue("Jump", false),
            new BoolValue("Target Block ESP", false)
    ), this);
    private final SliderValue blocksToJump = new SliderValue("Blocks To Jump", 7, 1, 8, this, () -> addons.isEnabled("Jump"));
    private final SliderValue blocksToSneak = new SliderValue("Blocks To Sneak", 7, 1, 8, this, () -> addons.isEnabled("Sneak"));
    private final SliderValue sneakDistance = new SliderValue("Sneak Distance", 0, 0, 0.5f, 0.01f, this, () -> addons.isEnabled("Sneak"));
    private final ModeValue tower = new ModeValue("Tower", new String[]{"Jump", "Vanilla", "Watchdog"}, "Jump", this, () -> !mode.is("Telly"));
    private final ModeValue towerMove = new ModeValue("Tower Move", new String[]{"Jump", "Vanilla", "Watchdog", "Low"}, "Jump", this, () -> !mode.is("Telly"));
    private final BoolValue stop = new BoolValue("Stop", true, this, () -> towerMove.is("Watchdog"));
    private final ModeValue wdSprint = new ModeValue("WD Sprint Mode", new String[]{"Offset"}, "Bottom", this, () -> mode.is("Watchdog") && addons.isEnabled("Sprint") && !addons.isEnabled("Keep Y"));
    private final BoolValue sprintBoost = new BoolValue("Sprint Boost Test", true, this, () -> mode.is("Watchdog") && addons.isEnabled("Sprint") && !addons.isEnabled("Keep Y"));
    private final ModeValue wdKeepY = new ModeValue("WD Keep Y Mode", new String[]{"Extra", "Vanilla"}, "Extra", this, () -> mode.is("Watchdog") && addons.isEnabled("Sprint") && (addons.isEnabled("Keep Y") || addons.isEnabled("Speed Keep Y")));
    private final ModeValue wdLowhop = new ModeValue("WD Fast Fall Mode", new String[]{"8 Tick", "7 Tick", "Disabled"}, "Disabled", this, () -> mode.is("Watchdog") && addons.isEnabled("Sprint") && addons.isEnabled("Keep Y"));
    private final BoolValue unFlagged = new BoolValue("Un Flagged Test", true, this, () -> mode.is("Watchdog") && addons.isEnabled("Sprint"));
    private final BoolValue unPatch = new BoolValue("Un Patch Test", true, this, () -> mode.is("Watchdog") && addons.isEnabled("Sprint") && (addons.isEnabled("Keep Y") || addons.isEnabled("Speed Keep Y")));
    private final SliderValue straightSpeed = new SliderValue("Keep Y Straight Speed", 1, 0.5f, 1f, 0.01f, this, () -> mode.is("Watchdog") && addons.isEnabled("Sprint") && (addons.isEnabled("Keep Y") || addons.isEnabled("Speed Keep Y")));
    private final SliderValue diagonalSpeed = new SliderValue("Keep Y Diagonal Speed", 0.95f, 0.5f, 1f, 0.01f, this, () -> mode.is("Watchdog") && addons.isEnabled("Sprint") && (addons.isEnabled("Keep Y") || addons.isEnabled("Speed Keep Y")));
    public final ModeValue counter = new ModeValue("Counter", new String[]{"None", "Simple", "Normal", "Exhibition", "Adjust", "Novo"}, "Normal", this);
    public PlaceData data;
    private int oloSlot = -1;
    private double onGroundY;
    private BlockPos targetBlock;
    private float[] rotation;
    private float[] previousRotation;
    private boolean canPlace = true;
    private int blocksPlaced;
    private boolean placing;
    private int tellyTicks;
    private boolean placed;
    private boolean isOnRightSide;
    private boolean flagged;
    private float derpYaw;

    private HoverState hoverState = HoverState.DONE;
    private static final Set<Block> blacklistedBlocks = Set.of(Blocks.air, Blocks.water, Blocks.flowing_water, Blocks.lava, Blocks.wooden_slab, Blocks.chest, Blocks.flowing_lava,
            Blocks.enchanting_table, Blocks.carpet, Blocks.glass_pane, Blocks.skull, Blocks.stained_glass_pane, Blocks.iron_bars, Blocks.snow_layer, Blocks.ice, Blocks.packed_ice,
            Blocks.coal_ore, Blocks.diamond_ore, Blocks.emerald_ore, Blocks.trapped_chest, Blocks.torch, Blocks.anvil,
            Blocks.noteblock, Blocks.jukebox, Blocks.tnt, Blocks.gold_ore, Blocks.iron_ore, Blocks.lapis_ore, Blocks.lit_redstone_ore, Blocks.quartz_ore, Blocks.redstone_ore,
            Blocks.wooden_pressure_plate, Blocks.stone_pressure_plate, Blocks.light_weighted_pressure_plate, Blocks.heavy_weighted_pressure_plate,
            Blocks.stone_button, Blocks.wooden_button, Blocks.lever, Blocks.tallgrass, Blocks.tripwire, Blocks.tripwire_hook, Blocks.rail, Blocks.waterlily, Blocks.red_flower,
            Blocks.red_mushroom, Blocks.brown_mushroom, Blocks.vine, Blocks.trapdoor, Blocks.yellow_flower, Blocks.ladder, Blocks.furnace, Blocks.sand, Blocks.cactus,
            Blocks.dispenser, Blocks.dropper, Blocks.crafting_table, Blocks.pumpkin, Blocks.sapling, Blocks.cobblestone_wall,
            Blocks.oak_fence, Blocks.activator_rail, Blocks.detector_rail, Blocks.golden_rail, Blocks.redstone_torch, Blocks.acacia_stairs,
            Blocks.birch_stairs, Blocks.brick_stairs, Blocks.dark_oak_stairs, Blocks.jungle_stairs, Blocks.nether_brick_stairs, Blocks.oak_stairs,
            Blocks.quartz_stairs, Blocks.red_sandstone_stairs, Blocks.sandstone_stairs, Blocks.spruce_stairs, Blocks.stone_brick_stairs, Blocks.stone_stairs, Blocks.double_wooden_slab, Blocks.stone_slab, Blocks.double_stone_slab, Blocks.stone_slab2, Blocks.double_stone_slab2,
            Blocks.web, Blocks.gravel, Blocks.daylight_detector_inverted, Blocks.daylight_detector, Blocks.soul_sand, Blocks.piston, Blocks.piston_extension,
            Blocks.piston_head, Blocks.sticky_piston, Blocks.iron_trapdoor, Blocks.ender_chest, Blocks.end_portal, Blocks.end_portal_frame, Blocks.standing_banner,
            Blocks.wall_banner, Blocks.deadbush, Blocks.slime_block, Blocks.acacia_fence_gate, Blocks.birch_fence_gate, Blocks.dark_oak_fence_gate,
            Blocks.jungle_fence_gate, Blocks.spruce_fence_gate, Blocks.oak_fence_gate);

    @Override
    public void onEnable() {

        if (addons.isEnabled("Hover") && mc.thePlayer.onGround && !isEnabled(Speed.class)) {
            hoverState = HoverState.JUMP;
        } else {
            hoverState = HoverState.DONE;
        }

        oloSlot = mc.thePlayer.inventory.currentItem;
        onGroundY = mc.thePlayer.getEntityBoundingBox().minY;
        previousRotation = new float[]{mc.thePlayer.rotationYaw + 180, 82};

        if (wdSprint.canDisplay() && wdSprint.is("Offset") && !(PlayerUtils.getBlock(mc.thePlayer.getPosition()) instanceof BlockLiquid) && !addons.isEnabled("Hover")) {
            if (mc.thePlayer.onGround) {
                hoverState = HoverState.JUMP;
            } else {
                hoverState = HoverState.DONE;
            }
        }

        flagged = false;

        canPlace = true;
    }

    @Override
    public void onDisable() {
        switch (switchBlock.get()) {
            case "Silent":
                sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                break;
            case "Switch":
                mc.thePlayer.inventory.currentItem = oloSlot;
                break;
            case "Spoof":
                mc.thePlayer.inventory.currentItem = oloSlot;
                SpoofSlotUtils.stopSpoofing();
                break;
        }
        previousRotation = rotation = null;
        blocksPlaced = 0;
        placed = placing = false;
        tellyTicks = 0;
        data = null;
        targetBlock = null;

        if (wdSprint.canDisplay() && sprintBoost.get()) {
            mc.thePlayer.motionX *= .8;
            mc.thePlayer.motionZ *= .8;
        }
    }

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        setTag(mode.get());

        data = null;

        if (getBlockSlot() == -1)
            return;

        // must be on here so the yaw is updated properly
        if (rotations.is("Derp")) {
            derpYaw += 30;
        }

        switch (switchBlock.get()) {
            case "Silent":
                sendPacketNoEvent(new C09PacketHeldItemChange(getBlockSlot()));
                break;
            case "Switch":
                mc.thePlayer.inventory.currentItem = getBlockSlot();
                break;
            case "Spoof":
                mc.thePlayer.inventory.currentItem = getBlockSlot();
                SpoofSlotUtils.startSpoofing(oloSlot);
                break;
        }

        if (mc.thePlayer.onGround) {
            onGroundY = mc.thePlayer.getEntityBoundingBox().minY;
        }

        double posY = mc.thePlayer.getEntityBoundingBox().minY;

        if ((hoverState != HoverState.DONE || addons.isEnabled("Keep Y") && !isEnabled(Speed.class) || addons.isEnabled("Speed Keep Y") && isEnabled(Speed.class)) && !mc.gameSettings.keyBindJump.isKeyDown()) {
            posY = onGroundY;
        }

        if (wdKeepY.canDisplay() && wdKeepY.is("Extra") && !towering() && (addons.isEnabled("Keep Y") && !isEnabled(Speed.class) || addons.isEnabled("Speed Keep Y") && isEnabled(Speed.class)) && !mc.gameSettings.keyBindJump.isKeyDown()) {
            posY = mc.thePlayer.ticksExisted % 6 != 0 ? onGroundY : mc.thePlayer.getEntityBoundingBox().minY;
        }

        if (towerMoving() || towering()) {
            onGroundY = posY = mc.thePlayer.getEntityBoundingBox().minY;
        }

        if (mode.is("Telly") && mc.thePlayer.onGround) {
            tellyTicks = MathUtils.randomizeInt((int) minTellyTicks.get(), (int) maxTellyTicks.get());
        }

        double posX = mc.thePlayer.posX;
        double posZ = mc.thePlayer.posZ;

        targetBlock = new BlockPos(posX, posY - 1, posZ);

        data = grab(targetBlock);

        if (tower.canDisplay() && towering() && !isEnabled(Speed.class) && tower.is("Watchdog") && !placing) {
            BlockPos xPos = data.blockPos.add(-1, 0, 0);
            if (canBePlacedOn(xPos)) {
                data.blockPos = xPos;
            }
        }

        placing = false;

        if (isEnabled(KillAura.class) && !getModule(KillAura.class).noScaffold.get() && getModule(KillAura.class).target != null && getModule(KillAura.class).shouldAttack() && data == null) {
            return;
        }

        if (mode.is("Telly") && mc.thePlayer.onGround) {
            tellyTicks = MathUtils.randomizeInt((int) minTellyTicks.get(), (int) maxTellyTicks.get());
        }

        if (addons.isEnabled("Sprint")) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), true);
        } else {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), false);
            mc.thePlayer.setSprinting(false);
        }

        if (tower.canDisplay() && (!tower.is("Jump") && towering() && !isEnabled(Speed.class) || !towerMove.is("Jump") && towerMoving())) {
            hoverState = HoverState.JUMP;
            blocksPlaced = 0;
        }

        switch (hoverState) {
            case JUMP:
                if (mc.thePlayer.onGround && !isEnabled(Speed.class) && !mc.gameSettings.keyBindJump.isKeyDown()) {
                    mc.thePlayer.jump();
                }
                hoverState = HoverState.FALL;
                break;
            case FALL:
                if (mc.thePlayer.onGround)
                    hoverState = HoverState.DONE;
                break;
        }

        if (!placed) {
            rotation = new float[]{mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch};
        }

        canPlace = (mode.is("Telly") && mc.thePlayer.offGroundTicks >= tellyTicks || mode.is("Snap") && data != null || !mode.is("Telly") && !mode.is("Snap"));

        getRotations();

        place(data.blockPos, data.facing, getVec3(data));
    }

    @EventTarget
    public void onRotationUpdate(UpdateEvent event) {
        if (canPlace) {
            if (customRotationSetting.get()) {
                RotationUtils.setRotation(rotation, smoothMode.get(), addons.isEnabled("Movement Fix") ? MovementCorrection.SILENT : MovementCorrection.OFF, minYawRotSpeed.get(), maxYawRotSpeed.get(), minPitchRotSpeed.get(), maxPitchRotSpeed.get(),
                        bezierP0.get(),
                        bezierP1.get(),
                        bezierP2.get(),
                        bezierP3.get(),
                        bezierP4.get(),
                        bezierP5.get(),
                        bezierP6.get(),
                        bezierP7.get(),
                        elasticity.get(),
                        dampingFactor.get(), smoothlyResetRotation.get());
            } else {
                RotationUtils.setRotation(rotation, addons.isEnabled("Movement Fix") ? MovementCorrection.SILENT : MovementCorrection.OFF);
            }
        }
    }

    @EventTarget
    public void onSafeWalk(SafeWalkEvent event) {
        if (addons.isEnabled("Safe Walk") && mc.thePlayer.onGround || addons.isEnabled("Safe Walk When No Data") && data == null) {
            event.setCancelled(true);
        }
    }

    @EventTarget
    public void onMovementInput(MoveInputEvent event) {

        if (isEnabled(KillAura.class) && !getModule(KillAura.class).noScaffold.get() && getModule(KillAura.class).target != null && getModule(KillAura.class).shouldAttack() && data == null) {
            return;
        }

        if (mode.is("Watchdog") && towerMoving() && !isEnabled(Speed.class) && towerMove.is("Jump")) {
            event.setJumping(placing);
        }

        if (addons.isEnabled("AD Strafe") && MovementUtils.isMoving() && MovementUtils.isMovingStraight() && mc.currentScreen == null && !Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCodeDefault()) && mc.thePlayer.onGround) {
            final BlockPos b = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 0.5, mc.thePlayer.posZ);
            if (mc.thePlayer.getHorizontalFacing(mc.thePlayer.rotationYaw + 180) == EnumFacing.EAST) {
                if (b.getZ() + 0.5 > mc.thePlayer.posZ) {
                    event.setStrafe(1.0f);
                } else {
                    event.setStrafe(-1.0f);
                }
            } else if (mc.thePlayer.getHorizontalFacing(mc.thePlayer.rotationYaw + 180) == EnumFacing.WEST) {
                if (b.getZ() + 0.5 < mc.thePlayer.posZ) {
                    event.setStrafe(1.0f);
                } else {
                    event.setStrafe(-1.0f);
                }
            } else if (mc.thePlayer.getHorizontalFacing(mc.thePlayer.rotationYaw + 180) == EnumFacing.SOUTH) {
                if (b.getX() + 0.5 < mc.thePlayer.posX) {
                    event.setStrafe(1.0f);
                } else {
                    event.setStrafe(-1.0f);
                }
            } else if (b.getX() + 0.5 > mc.thePlayer.posX) {
                event.setStrafe(1.0f);
            } else {
                event.setStrafe(-1.0f);
            }
        }

        if (addons.isEnabled("Sneak")) {

            double dif = 0.5;
            BlockPos blockPos = new BlockPos(mc.thePlayer).down();

            for (EnumFacing side : EnumFacing.values()) {
                if (side.getAxis() == EnumFacing.Axis.Y) {
                    continue;
                }

                BlockPos neighbor = blockPos.offset(side);

                if (PlayerUtils.isReplaceable(neighbor)) {
                    double calcDif = (side.getAxis() == EnumFacing.Axis.Z) ?
                            Math.abs(neighbor.getZ() + 0.5 - mc.thePlayer.posZ) :
                            Math.abs(neighbor.getX() + 0.5 - mc.thePlayer.posX) - 0.5;

                    if (calcDif < dif) {
                        dif = calcDif;
                    }
                }
            }

            if (mc.thePlayer.onGround && (PlayerUtils.isReplaceable(blockPos) || dif < sneakDistance.get()) && blocksPlaced == blocksToSneak.get()) {
                event.setSneaking(true);
            }
            if (blocksPlaced > blocksToSneak.get())
                blocksPlaced = 0;
        }
    }

    @EventTarget
    public void onStrafe(StrafeEvent event) {

        if (isEnabled(KillAura.class) && !getModule(KillAura.class).noScaffold.get() && getModule(KillAura.class).target != null && getModule(KillAura.class).shouldAttack() && data == null) {
            return;
        }

        if (wdLowhop.canDisplay() && wdLowhop.is("7 Tick") && placed && !towering() && !isEnabled(Speed.class) && !towerMoving()) {
            switch (mc.thePlayer.offGroundTicks) {
                case 1:
                    mc.thePlayer.motionY += 0.057f;
                    break;
                case 3:
                    mc.thePlayer.motionY -= 0.1309f;
                    break;
                case 4:
                    mc.thePlayer.motionY -= 0.2;
                    break;
            }
        }

        if (towerMove.is("Low")) {
            if (towerMoving()) {
                if (mc.thePlayer.offGroundTicks == 1) {
                    mc.thePlayer.motionY += 0.057f;
                    MovementUtils.strafe(Math.max(MovementUtils.getSpeed(), 0.33f + MovementUtils.getSpeedEffect() * 0.075));
                }

                if (mc.thePlayer.offGroundTicks == 3) {
                    mc.thePlayer.motionY -= 0.1309f;
                }

                if (mc.thePlayer.offGroundTicks == 4) {
                    mc.thePlayer.motionY -= 0.2;
                }
            }
        }

        if (mc.thePlayer.onGround) {
            if (((addons.isEnabled("Keep Y") || mode.is("Telly") || wdKeepY.canDisplay())) && MovementUtils.isMoving() && !towering() && !towerMoving() && (addons.isEnabled("Keep Y") && !isEnabled(Speed.class))) {
                mc.thePlayer.jump();
            }
        }

        if (addons.isEnabled("Jump")) {
            if (mc.thePlayer.onGround && !mc.gameSettings.keyBindJump.isKeyDown() && MovementUtils.isMoving() && MovementUtils.isMovingStraight()
                    && !mc.thePlayer.isSneaking()) {
                if (blocksPlaced >= blocksToJump.get()) {
                    mc.thePlayer.jump();
                    blocksPlaced = 0;
                }
            } else {
                blocksPlaced = 0;
            }
        }
    }

    @EventTarget
    public void onMove(MoveEvent event) {

        if (isEnabled(KillAura.class) && !getModule(KillAura.class).noScaffold.get() && getModule(KillAura.class).target != null && getModule(KillAura.class).shouldAttack() && data == null) {
            return;
        }

        if (tower.canDisplay()) {
            if (tower.get().equals("Vanilla")) {
                if (!mc.thePlayer.isPotionActive(Potion.jump)) {
                    if (towering()) {
                        event.setY(mc.thePlayer.motionY = 0.42);
                    }
                }
            }
        }

        if (towerMove.canDisplay()) {
            if (towerMove.get().equals("Vanilla")) {
                if (MovementUtils.isMoving() && MovementUtils.getSpeed() > 0.1 && !mc.thePlayer.isPotionActive(Potion.jump)) {
                    if (towerMoving()) {
                        mc.thePlayer.motionY = 0.42f;
                    }
                }
            }
        }
    }

    @EventTarget
    public void onAfterJump(AfterJumpEvent event) {

        if (isEnabled(KillAura.class) && !getModule(KillAura.class).noScaffold.get() && getModule(KillAura.class).target != null && getModule(KillAura.class).shouldAttack() && data == null) {
            return;
        }

        if (mode.is("Watchdog") && mc.gameSettings.keyBindJump.isKeyDown() && towerMove.is("Jump") && placing) {
            MovementUtils.strafe(0.4);
        }
    }

    @EventTarget
    public void onMotion(MotionEvent event) {

        if (event.isPost())
            return;

        if (isEnabled(KillAura.class) && !getModule(KillAura.class).noScaffold.get() && getModule(KillAura.class).target != null && getModule(KillAura.class).shouldAttack() && data == null) {
            return;
        }

        if (!(PlayerUtils.getBlock(mc.thePlayer.getPosition()) instanceof BlockLiquid) && !towering() && !isEnabled(Speed.class) && !towerMoving()) {

            if (unFlagged.get()) {
                if (flagged) {
                    mc.thePlayer.motionX *= 0.2;
                    mc.thePlayer.motionZ *= 0.2;
                    if (blocksPlaced > 2)
                        flagged = false;
                }
            }

            if (wdSprint.canDisplay() && !(addons.isEnabled("Speed Keep Y") & isEnabled(Speed.class) || addons.isEnabled("Keep Y"))) {
                if (wdSprint.is("Offset")) {
                    if (mc.thePlayer.onGround) {
                        event.setY(event.getY() + 1E-13F);
                    }
                }

                if (!isEnabled(Speed.class) && mc.thePlayer.onGround && sprintBoost.get() && (unFlagged.get() && !flagged || !unFlagged.get())) {
                    mc.thePlayer.motionX *= 1.14 - MovementUtils.getSpeedEffect() * .01 - Math.random() * 1E-4;
                    mc.thePlayer.motionZ *= 1.14 - MovementUtils.getSpeedEffect() * .01 - Math.random() * 1E-4;
                }
            }
        }

        if (wdKeepY.canDisplay() && (addons.isEnabled("Speed Keep Y") && isEnabled(Speed.class) || !addons.isEnabled("Speed Keep Y")) && !towering() && !isEnabled(Speed.class) && !towerMoving() && !isEnabled(Speed.class) && addons.isEnabled("Sprint") && mc.thePlayer.onGround && MovementUtils.isMoving()) {
            MovementUtils.strafe(MovementUtils.getSpeed() * (MovementUtils.isMovingStraight() ? straightSpeed.get() : diagonalSpeed.get()));
        }

        if (wdLowhop.canDisplay() && wdLowhop.is("8 Tick") && placed && !towering() && !isEnabled(Speed.class) && !towerMoving()) {
            boolean down = false;
            int simpleY = (int) Math.round((event.y % 1) * 10000);

            if (simpleY == 13) {
                mc.thePlayer.motionY = mc.thePlayer.motionY - 0.02483;
            }

            if (simpleY == 2000) {
                mc.thePlayer.motionY = mc.thePlayer.motionY - 0.1913;
            }

            if (simpleY == 13) down = true;
            if (down) event.y -= 1E-5;
        }

        if (tower.canDisplay()) {
            if (tower.get().equals("Watchdog")) {
                if (!mc.thePlayer.isPotionActive(Potion.jump) && placed) {
                    if (towering()) {
                        MovementUtils.stopXZ();

                        if (mc.thePlayer.posY % 1 == 0.5) {
                            mc.thePlayer.motionY = 0.42F;
                        }

                        if (mc.thePlayer.posX % 1.0 != 0.0 && !MovementUtils.isMoving()) {
                            double velocityXAdjustment = (Math.round(mc.thePlayer.posX) - mc.thePlayer.posX);
                            mc.thePlayer.motionX = Math.min(velocityXAdjustment, 0.281);
                        }

                        int valY = (int) Math.round((event.y % 1) * 10000);

                        if (valY == 0) {
                            mc.thePlayer.motionY = 0.42F;
                        } else if (valY > 4000 && valY < 4300) {
                            mc.thePlayer.motionY = 0.33;
                        } else if (valY > 7000) {
                            mc.thePlayer.motionY = 1 - mc.thePlayer.posY % 1;
                        }

                    }
                }
            }
        }

        if (towerMove.canDisplay()) {
            if (towerMove.get().equals("Watchdog")) {
                if (MovementUtils.isMoving() && MovementUtils.getSpeed() > 0.1 && !mc.thePlayer.isPotionActive(Potion.jump)) {
                    if (towerMoving()) {
                        int valY = (int) Math.round((event.y % 1) * 10000);
                        if (valY == 0) {
                            mc.thePlayer.motionY = 0.42F;
                            MovementUtils.strafe((float) 0.26 + MovementUtils.getSpeedEffect() * 0.03);
                        } else if (valY > 4000 && valY < 4300) {
                            mc.thePlayer.motionY = 0.33;
                            MovementUtils.strafe((float) 0.26 + MovementUtils.getSpeedEffect() * 0.03);
                        } else if (valY > 7000) {
                            mc.thePlayer.motionY = 1 - mc.thePlayer.posY % 1;
                        }
                    }
                }
            }
        }
    }

    @EventTarget
    public void onRender3D(Render3DEvent event) {
        if (isEnabled(KillAura.class) && !getModule(KillAura.class).noScaffold.get() && getModule(KillAura.class).target != null && getModule(KillAura.class).shouldAttack() && data == null) {
            return;
        }

        if (addons.isEnabled("Target Block ESP")) {
            RenderUtils.renderBlock(data.blockPos, getModule(Interface.class).color(0, 100), false, true);
        }
    }

    @EventTarget
    public void onPacket(PacketEvent event) {
        if (event.getPacket() instanceof S08PacketPlayerPosLook) {
            if (wdSprint.canDisplay() && !(PlayerUtils.getBlock(mc.thePlayer.getPosition()) instanceof BlockLiquid) && wdSprint.is("Offset")) {
                blocksPlaced = 0;
                flagged = true;
            }
        }
    }

    @EventTarget
    public void onWorld(WorldEvent event) {
        setEnabled(false);
    }

    public void getRotations() {

        switch (rotations.get()) {
            case "Normal": {
                if (data != null)
                    rotation = RotationUtils.getRotations(getVec3(data));
            }
            break;
            case "Normal 2": {
                if (data != null)
                    rotation = RotationUtils.getRotations(data.blockPos);
            }
            break;
            case "Normal 3": {
                if (data != null)
                    rotation = getBestRotation(data.blockPos,data.facing);
            }
            break;
            case "Strict": {
                if (data != null)
                    rotation = RotationUtils.getRotations(getHitVecOptimized(data.blockPos, data.facing));
            }
            break;

            case "God Bridge": {
                float movingYaw = MovementUtils.getRawDirection() + 180;

                if (mc.thePlayer.onGround) {
                    isOnRightSide = Math.floor(mc.thePlayer.posX + Math.cos(Math.toRadians(movingYaw)) * 0.5) != Math.floor(mc.thePlayer.posX) ||
                            Math.floor(mc.thePlayer.posZ + Math.sin(Math.toRadians(movingYaw)) * 0.5) != Math.floor(mc.thePlayer.posZ);

                    BlockPos posInDirection = mc.thePlayer.getPosition().offset(EnumFacing.fromAngle(movingYaw), 1);

                    boolean isLeaningOffBlock = mc.theWorld.getBlockState(mc.thePlayer.getPosition().down()) instanceof BlockAir;
                    boolean nextBlockIsAir = mc.theWorld.getBlockState(posInDirection.down()).getBlock() instanceof BlockAir;

                    if (isLeaningOffBlock && nextBlockIsAir) {
                        isOnRightSide = !isOnRightSide;
                    }
                }

                float yaw = MovementUtils.isMovingStraight() ? (movingYaw + (isOnRightSide ? 45 : -45)) : movingYaw;

                float finalYaw = Math.round(yaw / 45f) * 45f;

                for (float i = minPitch.get(); i < maxPitch.get(); i += 0.01f) {
                    float[] rot = new float[]{finalYaw, i};
                    MovingObjectPosition ray = RotationUtils.rayTrace(rot, mc.playerController.getBlockReachDistance(), 1);

                    if (ray.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && ray.getBlockPos().equalsBlockPos(data.blockPos)) {
                        rotation = rot;
                    }
                }
            }
            break;
            case "Custom": {
                float yaw = MovementUtils.getRawDirection() + customYaw.get();

                for (float i = minPitch.get(); i < maxPitch.get(); i += 0.01f) {
                    float[] rot = new float[]{yaw, i};
                    MovingObjectPosition ray = RotationUtils.rayTrace(rot, mc.playerController.getBlockReachDistance(), 1);

                    if (ray.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && ray.getBlockPos().equalsBlockPos(data.blockPos)) {
                        rotation = rot;
                    }
                }
            }
            break;
            case "Hypixel": {
                float yaw = MovementUtils.getRawDirection();
                    if (MovementUtils.isMovingStraight()) {
                        if (Math.abs(MathHelper.wrapAngleTo180_double(RotationUtils.getRotations(targetBlock)[0] - MovementUtils.getRawDirection() - 125)) < Math.abs(MathHelper.wrapAngleTo180_double(RotationUtils.getRotations(targetBlock)[0] - MovementUtils.getRawDirection() + 125))) {
                            yaw += 125;
                        } else {
                            yaw -= 125;
                        }
                    } else {
                        yaw += 132;
                    }

                    rotation[0] = yaw;

                    if (data != null) {
                        rotation[1] = getBestRotation(data.blockPos, data.facing)[1];
                    } else {
                        rotation = new float[]{yaw, previousRotation[1]};
                    }

            }
            break;
            case "Unfair Pitch": {
                rotation = new float[]{mc.thePlayer.rotationYaw, 93};
            }
            break;
            case "Derp":
                rotation = new float[]{derpYaw, 85};
                break;
        }


        if (tower.canDisplay() && tower.is("Watchdog") && towering()) {
            rotation = getBestRotation(data.blockPos, data.facing);
        }

        if (unPatch.canDisplay() && (addons.isEnabled("Speed Keep Y") && isEnabled(Speed.class) || !addons.isEnabled("Speed Keep Y")) && unPatch.get() && (mc.thePlayer.offGroundTicks == 1 || mc.thePlayer.onGround) && !towering() && !towerMoving()) {
            rotation = new float[]{mc.thePlayer.rotationYaw, previousRotation[1]};
            canPlace = false;
        }

        previousRotation = rotation;
    }

    public boolean towering() {
        return Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode()) && !MovementUtils.isMoving();
    }

    public boolean towerMoving() {
        return Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode()) && MovementUtils.isMoving();
    }

    public int getBlockSlot() {
        int slot = -1, size = 0;

        if (getBlockCount() == 0) {
            return -1;
        }

        for (int i = 36; i < 45; i++) {
            final Slot s = mc.thePlayer.inventoryContainer.getSlot(i);

            if (s.getHasStack()) {
                final Item item = s.getStack().getItem();
                final ItemStack is = s.getStack();

                if (item instanceof ItemBlock && !blacklistedBlocks.contains(((ItemBlock) item).getBlock()) && (biggestStack.get() && is.stackSize > size || !biggestStack.get())) {
                    size = is.stackSize;
                    slot = i;
                }
            }
        }

        return slot - 36;
    }

    public int getBlockCount() {
        int blockCount = 0;

        for (int i = 36; i < 45; ++i) {
            if (!mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) continue;

            final ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();

            if (!(is.getItem() instanceof ItemBlock && !blacklistedBlocks.contains(((ItemBlock) is.getItem()).getBlock()))) {
                continue;
            }

            blockCount += is.stackSize;
        }

        return blockCount;
    }

    private void place(BlockPos pos, EnumFacing facing, Vec3 hitVec) {

        if (canPlace && data != null) {

            if (!addons.isEnabled("Ray Trace")) {
                if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem(), pos, facing, hitVec)) {
                    if (addons.isEnabled("Swing")) {
                        mc.thePlayer.swingItem();
                        mc.getItemRenderer().resetEquippedProgress();
                    } else
                        mc.getNetHandler().addToSendQueue(new C0APacketAnimation());
                    placing = true;
                    blocksPlaced += 1;
                    placed = true;
                }
            } else {
                MovingObjectPosition ray = RotationUtils.rayTrace(mc.playerController.getBlockReachDistance(), 1);
                if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem(), ray.getBlockPos(), ray.sideHit, ray.hitVec)) {
                    if (addons.isEnabled("Swing")) {
                        mc.thePlayer.swingItem();
                        mc.getItemRenderer().resetEquippedProgress();
                    } else
                        mc.getNetHandler().addToSendQueue(new C0APacketAnimation());
                    placing = true;
                    blocksPlaced += 1;
                    placed = true;
                }
            }
        }
    }

    public Vec3 getHitVecOptimized(BlockPos blockPos, EnumFacing facing) {
        Vec3 eyes = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);

        return MathUtils.closestPointOnFace(new AxisAlignedBB(blockPos, blockPos.add(1, 1, 1)), facing, eyes);
    }

    public float[] getBestRotation(BlockPos blockPos, EnumFacing face) {
        Vec3i faceVec = face.getDirectionVec();

        float minX, maxX, minY, maxY, minZ, maxZ;

        if (faceVec.getX() == 0) {
            minX = 0.1f;
            maxX = 0.9f;
        } else if (faceVec.getX() == 1) {
            minX = maxX = 1.0f;
        } else if (faceVec.getX() == -1) {
            minX = maxX = 0.0f;
        } else {
            minX = 0.1f;
            maxX = 0.9f;
        }

        if (faceVec.getY() == 0) {
            minY = 0.1f;
            maxY = 0.9f;
        } else if (faceVec.getY() == 1) {
            minY = maxY = 1.0f;
        } else if (faceVec.getY() == -1) {
            minY = maxY = 0.0f;
        } else {
            minY = 0.1f;
            maxY = 0.9f;
        }

        if (faceVec.getZ() == 0) {
            minZ = 0.1f;
            maxZ = 0.9f;
        } else if (faceVec.getZ() == 1) {
            minZ = maxZ = 1.0f;
        } else if (faceVec.getZ() == -1) {
            minZ = maxZ = 0.0f;
        } else {
            minZ = 0.1f;
            maxZ = 0.9f;
        }

        float[] bestRot = RotationUtils.getRotations(blockPos);
        double bestDist = RotationUtils.getRotationDifference(bestRot);

        for (float x = minX; x <= maxX; x += 0.1f) {
            for (float y = minY; y <= maxY; y += 0.1f) {
                for (float z = minZ; z <= maxZ; z += 0.1f) {
                    Vec3 candidateLocal = new Vec3(x, y, z);
                    Vec3 candidateWorld = candidateLocal.add(new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ()));

                    double diff = RotationUtils.getRotationDifference(candidateWorld);
                    if (diff < bestDist) {
                        bestDist = diff;
                        bestRot = RotationUtils.getRotations(candidateWorld);
                    }
                }
            }
        }

        return bestRot;
    }


    private PlaceData grab(BlockPos pos) {
        if (!(PlayerUtils.getBlock(pos) instanceof BlockAir)) {
            return null;
        }

        for (final EnumFacing facing : EnumFacing.VALUES) {
            if (canBePlacedOn(pos.add(facing.getOpposite().getDirectionVec())))
                return new PlaceData(pos.add(facing.getOpposite().getDirectionVec()), facing);
        }

        for (final EnumFacing enumFacing : EnumFacing.VALUES) {
            final BlockPos currentPos = pos.add(enumFacing.getDirectionVec());

            for (final EnumFacing facing : EnumFacing.VALUES) {
                if (canBePlacedOn(currentPos.add(facing.getOpposite().getDirectionVec())))
                    return new PlaceData(currentPos.add(facing.getOpposite().getDirectionVec()), facing);
            }
        }

        return null;
    }

    public static boolean canBePlacedOn(final BlockPos blockPos) {
        final Material material = mc.theWorld.getBlockState(blockPos).getBlock().getMaterial();

        return (material.blocksMovement() && material.isSolid() && !(PlayerUtils.getBlock(blockPos) instanceof BlockAir));
    }

    private Vec3 getVec3(PlaceData data) {
        BlockPos pos = data.blockPos;
        EnumFacing face = data.facing;
        double x = pos.getX() + 0.5D;
        double y = pos.getY() + 0.5D;
        double z = pos.getZ() + 0.5D;
        x += face.getFrontOffsetX() / 2.0D;
        z += face.getFrontOffsetZ() / 2.0D;
        y += face.getFrontOffsetY() / 2.0D;

        return new Vec3(x, y, z);
    }

    @AllArgsConstructor
    public static class PlaceData {
        public BlockPos blockPos;
        public EnumFacing facing;
    }


    enum HoverState {
        JUMP,
        FALL,
        DONE
    }
}
