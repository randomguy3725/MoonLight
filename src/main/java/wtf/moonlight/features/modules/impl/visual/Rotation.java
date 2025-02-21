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
package wtf.moonlight.features.modules.impl.visual;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import org.jetbrains.annotations.NotNull;
import wtf.moonlight.events.annotations.EventPriority;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.player.LookEvent;
import wtf.moonlight.events.impl.render.Render3DEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.values.impl.BoolValue;
import wtf.moonlight.features.values.impl.SliderValue;
import wtf.moonlight.utils.math.MathUtils;
import wtf.moonlight.utils.player.RotationUtils;
import wtf.moonlight.utils.render.RenderUtils;

@ModuleInfo(name = "Rotation", category = ModuleCategory.Visual)
public class Rotation extends Module {

    public final BoolValue body = new BoolValue("Render Body", true, this);
    public final BoolValue realistic = new BoolValue("Realistic", true, this, body::get);
    public final BoolValue fixAim = new BoolValue("Fix Aim", true, this);
    private final BoolValue aimPoint = new BoolValue("Aim Point", false, this, fixAim::get);
    private final SliderValue dotSize = new SliderValue("Size", 0.1f, 0.05f, 0.2f, 0.05f, this, () -> aimPoint.canDisplay() && aimPoint.get());

    public double x,y,z = 0;
    public double prevX, prevY, prevZ = 0;

    @EventTarget
    @EventPriority(-1)
    public void onLook(LookEvent event) {
        if (fixAim.get()) {
            event.prevPitch = mc.thePlayer.prevRotationPitchHead;
            event.prevYaw = mc.thePlayer.prevRotationYawHead;
            event.yaw = mc.thePlayer.rotationYawHead;
            event.pitch = mc.thePlayer.rotationPitchHead;
        }
    }

    @EventTarget
    @EventPriority(0)
    public void onRender3D(Render3DEvent event) {
        if (aimPoint.canDisplay() && aimPoint.get()) {
            double distance = mc.objectMouseOver.hitVec.getDistanceAtEyeByVec(mc.thePlayer);
            final Vec3 vec31 = mc.thePlayer.getLook(event.partialTicks());
            final Vec3 vec32 = mc.thePlayer.getPositionEyes(event.partialTicks()).addVector(vec31.xCoord * distance, vec31.yCoord * distance, vec31.zCoord * distance);

            prevX = x;
            prevY = y;
            prevZ = z;

            x = vec32.xCoord;
            y = vec32.yCoord;
            z = vec32.zCoord;

            Vec3 vec = new Vec3(MathUtils.interpolate(prevX, x, event.partialTicks()), MathUtils.interpolate(prevY, y, event.partialTicks()), MathUtils.interpolate(prevZ, z, event.partialTicks()));

            final double x = mc.thePlayer.prevPosX + (mc.thePlayer.posX - mc.thePlayer.prevPosX) * event.partialTicks();
            final double y = mc.thePlayer.prevPosY + (mc.thePlayer.posY - mc.thePlayer.prevPosY) * event.partialTicks();
            final double z = mc.thePlayer.prevPosZ + (mc.thePlayer.posZ - mc.thePlayer.prevPosZ) * event.partialTicks();

            double d = dotSize.get() / 2;
            AxisAlignedBB target = new AxisAlignedBB(vec.xCoord - d, vec.yCoord - d, vec.zCoord - d, vec.xCoord + d, vec.yCoord + d, vec.zCoord + d);

            AxisAlignedBB axis = new AxisAlignedBB(target.minX - x, target.minY - y, target.minZ - z, target.maxX - x, target.maxY - y, target.maxZ - z);
            RenderUtils.drawAxisAlignedBB(axis, true, getModule(Interface.class).color());
        }
    }
}
