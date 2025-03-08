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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.player.UpdateEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.values.impl.BoolValue;
import wtf.moonlight.features.values.impl.ModeValue;
import wtf.moonlight.features.values.impl.SliderValue;
import wtf.moonlight.utils.math.MathUtils;
import wtf.moonlight.utils.math.TimerUtils;
import wtf.moonlight.utils.player.MovementCorrection;
import wtf.moonlight.utils.player.PlayerUtils;
import wtf.moonlight.utils.player.RotationUtils;

@ModuleInfo(name = "Annoy", category = ModuleCategory.Combat)
public class Annoy extends Module {

    private final SliderValue fov = new SliderValue("FOV",180,1,180,this);
    public SliderValue delay = new SliderValue("Delay", 1000, 500, 5000, 250,this);
    private final BoolValue customRotationSetting = new BoolValue("Custom Rotation Setting", false, this);
    private final ModeValue smoothMode = new ModeValue("Rotations Smooth", RotationUtils.smoothModes, RotationUtils.smoothModes[0], this, customRotationSetting::get);
    private final SliderValue minYawRotSpeed = new SliderValue("Min Yaw Rotation Speed", 45, 1,180,1, this, customRotationSetting::get);
    private final SliderValue minPitchRotSpeed = new SliderValue("Min Pitch Rotation Speed", 45, 1,180,1, this, customRotationSetting::get);
    private final SliderValue maxYawRotSpeed = new SliderValue("Max Yaw Rotation Speed", 90, 1,180,1, this, customRotationSetting::get);
    private final SliderValue maxPitchRotSpeed = new SliderValue("Max Pitch Rotation Speed", 90, 1,180,1, this, customRotationSetting::get);
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
    public final BoolValue smoothlyResetRotation = new BoolValue("Smoothly Reset Rotation", true, this, customRotationSetting::get);
    public final BoolValue moveFix = new BoolValue("Move Fix",true,this);
    public final ModeValue moveFixMode = new ModeValue("Movement", new String[]{"Silent", "Strict"}, "Silent", this, moveFix::get);

    public final TimerUtils timer = new TimerUtils();
    public boolean cancel;

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        KillAura killAura = getModule(KillAura.class);

        if (!(killAura.target instanceof EntityPlayer target))
            return;

        int newSlot = getBlockSlot();
        if (newSlot == -1) {
            cancel = false;
            return;
        }

        if (PlayerUtils.getDistanceToEntityBox(target) <= 3) {
            cancel = false;
            return;
        }

        if (!killAura.isEnabled()) {
            cancel = false;
            return;
        }

        if (!timer.hasTimeElapsed((long) delay.get(), false)) {
            if (cancel) {
                cancel = false;
            }
            return;
        }


        int prevSlot = mc.thePlayer.inventory.currentItem;
        float[] rots = RotationUtils.getRotations(target.posX, target.posY - 1.6, target.posZ);

        if (customRotationSetting.get()) {
            RotationUtils.setRotation(rots,smoothMode.get(),moveFix.get() ? moveFixMode.is("Strict") ? MovementCorrection.STRICT : MovementCorrection.SILENT : MovementCorrection.OFF, minYawRotSpeed.get(), maxYawRotSpeed.get(), minPitchRotSpeed.get(), maxPitchRotSpeed.get(),
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
            RotationUtils.setRotation(rots,moveFix.get() ? moveFixMode.is("Strict") ? MovementCorrection.STRICT : MovementCorrection.SILENT : MovementCorrection.OFF);
        }

        MovingObjectPosition rayTrace = RotationUtils.rayTrace(mc.playerController.getBlockReachDistance(), 1);
        mc.thePlayer.inventory.currentItem = newSlot;
        if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem(), rayTrace.getBlockPos(), rayTrace.sideHit, rayTrace.hitVec)) {
            mc.thePlayer.swingItem();
            mc.getItemRenderer().resetEquippedProgress();
        }
        mc.thePlayer.inventory.currentItem = prevSlot;

        cancel = true;
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

                if (item instanceof ItemBlock) {
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

            if (!(is.getItem() instanceof ItemBlock)) {
                continue;
            }

            blockCount += is.stackSize;
        }

        return blockCount;
    }
}
