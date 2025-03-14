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
package wtf.moonlight.utils.player;

import com.google.common.base.Predicates;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.*;
import net.optifine.reflect.Reflector;
import org.jetbrains.annotations.NotNull;
import wtf.moonlight.Moonlight;
import wtf.moonlight.events.annotations.EventPriority;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.misc.MouseOverEvent;
import wtf.moonlight.events.impl.misc.TickEvent;
import wtf.moonlight.events.impl.misc.WorldEvent;
import wtf.moonlight.events.impl.packet.PacketEvent;
import wtf.moonlight.events.impl.player.*;
import wtf.moonlight.features.modules.impl.visual.Rotation;
import wtf.moonlight.utils.InstanceAccess;
import wtf.moonlight.utils.math.MathUtils;

import java.util.List;
import java.util.Objects;

import static java.lang.Math.hypot;

public class RotationUtils implements InstanceAccess {
    public static float[] currentRotation = null, serverRotation = new float[]{}, previousRotation = null;
    public static MovementCorrection currentCorrection = MovementCorrection.OFF;
    private static boolean enabled;
    private static int keepLength;
    private static boolean smoothlyReset;
    public static String cachedMode;
    public static float cachedMinYawRotSpeed;
    public static float cachedMaxYawRotSpeed;
    public static float cachedMinPitchRotSpeed;
    public static float cachedMaxPitchRotSpeed;
    public static float elasticity;
    public static float dampingFactor;
    public static float bezierP0;
    public static float bezierP1;
    public static float bezierP2;
    public static float bezierP3;
    public static float bezierP4;
    public static float bezierP5;
    public static float bezierP6;
    public static float bezierP7;

    public static String[] smoothModes = new String[]{"Slerp", "Adaptive Bezier", "Adaptive Slerp", "Sinusoidal", "Spring", "Cosine Interpolation", "Logarithmic Interpolation", "Elastic Spring", "Bezier"};

    public static boolean shouldRotate() {
        return currentRotation != null;
    }

    public static void setRotation(float[] rotation) {
        setRotation(rotation, MovementCorrection.OFF);
    }

    public static void setRotation(float[] rotation, final MovementCorrection correction) {
        RotationUtils.currentRotation = applyGCDFix(serverRotation, rotation);
        currentCorrection = correction;
        smoothlyReset = false;
        enabled = true;
    }

    public static void setRotation(float[] rotation, final MovementCorrection correction,int keepLength) {
        RotationUtils.currentRotation = applyGCDFix(serverRotation, rotation);
        currentCorrection = correction;
        smoothlyReset = false;

        RotationUtils.keepLength = keepLength;

        enabled = true;
    }

    public static void setRotation(float[] rotation, String mode, final MovementCorrection correction, float minYawRotSpeed, float maxYawRotSpeed, float minPitchRotSpeed, float maxPitchRotSpeed,float bezierP0,float bezierP1,float bezierP2,float bezierP3,float bezierP4,float bezierP5,float bezierP6,float bezierP7,float elasticity,float dampingFactor, boolean smoothlyReset) {
        RotationUtils.currentRotation = smooth(serverRotation, rotation, mode, MathUtils.randomizeInt(minYawRotSpeed,maxYawRotSpeed), MathUtils.randomizeInt(minPitchRotSpeed,maxPitchRotSpeed), bezierP0, bezierP1, bezierP2, bezierP3, bezierP4, bezierP5, bezierP6, bezierP7,elasticity,dampingFactor);
        currentCorrection = correction;
        RotationUtils.smoothlyReset = smoothlyReset;
        cachedMinYawRotSpeed = minYawRotSpeed;
        cachedMaxYawRotSpeed = maxYawRotSpeed;
        cachedMinPitchRotSpeed = minPitchRotSpeed;
        cachedMaxPitchRotSpeed = maxPitchRotSpeed;
        cachedMode = mode;
        RotationUtils.bezierP0 = bezierP0;
        RotationUtils.bezierP1 = bezierP1;
        RotationUtils.bezierP2 = bezierP2;
        RotationUtils.bezierP3 = bezierP3;
        RotationUtils.bezierP4 = bezierP4;
        RotationUtils.bezierP5 = bezierP5;
        RotationUtils.bezierP6 = bezierP6;
        RotationUtils.bezierP7 = bezierP7;
        RotationUtils.elasticity = elasticity;
        RotationUtils.dampingFactor = dampingFactor;

        enabled = true;
    }

    public static void setRotation(float[] rotation, String mode, final MovementCorrection correction, float minYawRotSpeed, float maxYawRotSpeed, float minPitchRotSpeed, float maxPitchRotSpeed,float bezierP0,float bezierP1,float bezierP2,float bezierP3,float bezierP4,float bezierP5,float bezierP6,float bezierP7,float elasticity,float dampingFactor,int keepLength, boolean smoothlyReset) {
        RotationUtils.currentRotation = smooth(serverRotation, rotation, mode, MathUtils.randomizeInt(minYawRotSpeed,maxYawRotSpeed), MathUtils.randomizeInt(minPitchRotSpeed,maxPitchRotSpeed), bezierP0, bezierP1, bezierP2, bezierP3, bezierP4, bezierP5, bezierP6, bezierP7,elasticity,dampingFactor);
        currentCorrection = correction;
        RotationUtils.smoothlyReset = smoothlyReset;
        cachedMinYawRotSpeed = minYawRotSpeed;
        cachedMaxYawRotSpeed = maxYawRotSpeed;
        cachedMinPitchRotSpeed = minPitchRotSpeed;
        cachedMaxPitchRotSpeed = maxPitchRotSpeed;
        cachedMode = mode;
        RotationUtils.bezierP0 = bezierP0;
        RotationUtils.bezierP1 = bezierP1;
        RotationUtils.bezierP2 = bezierP2;
        RotationUtils.bezierP3 = bezierP3;
        RotationUtils.bezierP4 = bezierP4;
        RotationUtils.bezierP5 = bezierP5;
        RotationUtils.bezierP6 = bezierP6;
        RotationUtils.bezierP7 = bezierP7;
        RotationUtils.elasticity = elasticity;
        RotationUtils.dampingFactor = dampingFactor;

        RotationUtils.keepLength = keepLength;

        enabled = true;
    }

    @EventTarget
    @EventPriority(-100)
    public void onTick(TickEvent event){
        if(shouldRotate()){
            keepLength--;

            if (keepLength <= 0) {
                enabled = false;
            }
        }
    }

    @EventTarget
    @EventPriority(-100)
    public void onRotationUpdate(UpdateEvent event) {

        if (!enabled && currentRotation != null) {

            double distanceToPlayerRotation = getRotationDifference(currentRotation, new float[]{mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch});

            if (!smoothlyReset || distanceToPlayerRotation < 1) {
                resetRotation();
                return;
            }

            if (distanceToPlayerRotation > 0) {
                RotationUtils.currentRotation = smooth(Objects.requireNonNullElse(currentRotation, serverRotation), new float[]{mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch}, cachedMode, MathUtils.randomizeInt(cachedMinYawRotSpeed,cachedMaxYawRotSpeed), MathUtils.randomizeInt(cachedMinPitchRotSpeed,cachedMaxPitchRotSpeed), bezierP0, bezierP1, bezierP2, bezierP3, bezierP4, bezierP5, bezierP6, bezierP7,elasticity,dampingFactor);
            }
        }
    }

    @EventTarget
    private void onMove(MoveInputEvent event) {
        if (currentCorrection == MovementCorrection.SILENT) {
            final float yaw = currentRotation[0];
            MovementUtils.fixMovement(event, yaw);
        }
    }

    @EventTarget
    private void onStrafe(StrafeEvent event) {
        if (shouldRotate()) {
            if (currentCorrection == MovementCorrection.STRICT || currentCorrection == MovementCorrection.SILENT) {
                event.setYaw(currentRotation[0]);
            }
        }
    }

    @EventTarget
    private void onJump(JumpEvent event) {
        if (shouldRotate()) {
            if (currentCorrection != MovementCorrection.OFF) {
                event.setYaw(currentRotation[0]);
            }
        }
    }

    @EventTarget
    public void onPacket(final PacketEvent event) {
        final Packet<?> packet = event.getPacket();

        if(packet instanceof C03PacketPlayer packetPlayer) {

            if(currentRotation != null && (currentRotation[0] != serverRotation[0] || currentRotation[1] != serverRotation[1])) {
                packetPlayer.yaw = currentRotation[0];
                packetPlayer.pitch = currentRotation[1];
                packetPlayer.rotating = true;
            }

            if(packetPlayer.rotating) serverRotation = new float[]{packetPlayer.yaw, packetPlayer.pitch};
        }
    }

    @EventTarget
    public void onWorld(WorldEvent event) {
        resetRotation();
    }

    @EventTarget
    @EventPriority(-100)
    public void onMotion(MotionEvent event) {
        if ((event.isPost() || !smoothlyReset) && currentRotation != null) {
            double distanceToPlayerRotation = getRotationDifference(currentRotation, new float[]{mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch});

            if (!enabled) {

                if (!smoothlyReset || distanceToPlayerRotation < 1) {
                    resetRotation();
                    return;
                }

                if (distanceToPlayerRotation > 0) {
                    RotationUtils.currentRotation = smooth(Objects.requireNonNullElse(currentRotation, serverRotation), new float[]{mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch}, cachedMode, MathUtils.randomizeInt(cachedMinYawRotSpeed,cachedMaxYawRotSpeed), MathUtils.randomizeInt(cachedMinPitchRotSpeed,cachedMaxPitchRotSpeed), bezierP0, bezierP1, bezierP2, bezierP3, bezierP4, bezierP5, bezierP6, bezierP7,elasticity,dampingFactor);
                }
            }
        }
    }

    private static void resetRotation() {
        keepLength = 0;
        enabled = false;
        RotationUtils.currentRotation = null;
        currentCorrection = MovementCorrection.OFF;
    }

    public static float[] smooth(final float[] currentRotation, final float[] targetRotation, String mode, float hSpeed, float vSpeed,float bezierP0,float bezierP1,float bezierP2,float bezierP3,float bezierP4,float bezierP5,float bezierP6,float bezierP7,float elasticity,float dampingFactor) {

        float[] result = new float[]{smooth(currentRotation[0], targetRotation[0], mode, hSpeed, bezierP0, bezierP1, bezierP2, bezierP3, bezierP4, bezierP5, bezierP6, bezierP7,elasticity,dampingFactor), smooth(currentRotation[1], targetRotation[1], mode, vSpeed, bezierP0, bezierP1, bezierP2, bezierP3, bezierP4, bezierP5, bezierP6, bezierP7,elasticity,dampingFactor)};

        return applyGCDFix(currentRotation, result);
    }

    private static float smooth(float current, float target, String mode, float speed, float bezierP0, float bezierP1, float bezierP2, float bezierP3, float bezierP4, float bezierP5, float bezierP6, float bezierP7,float elasticity,float dampingFactor) {

        speed /= 180;
        switch (mode) {
            case "Slerp": {
                float delta = MathHelper.wrapAngleTo180_float(target - current);
                return current + delta * speed;
            }
            case "Adaptive Bezier": {
                float p0 = bezierP0;
                float p1 = bezierP1;
                float p2 = bezierP2;
                float p3 = bezierP3;
                float p4 = bezierP4;
                float p5 = bezierP5;
                float p6 = bezierP6;
                float p7 = bezierP7;

                float factor = (float) ((Math.pow(1 - speed, 7) * p0) +
                        7 * Math.pow(1 - speed, 6) * speed * p1 +
                        21 * Math.pow(1 - speed, 5) * Math.pow(speed, 2) * p2 +
                        35 * Math.pow(1 - speed, 4) * Math.pow(speed, 3) * p3 +
                        35 * Math.pow(1 - speed, 3) * Math.pow(speed, 4) * p4 +
                        21 * Math.pow(1 - speed, 2) * Math.pow(speed, 5) * p5 +
                        7 * (1 - speed) * Math.pow(speed, 6) * p6 +
                        Math.pow(speed, 7) * p7);
                float delta = MathHelper.wrapAngleTo180_float(target - current);
                float distance = Math.abs(delta);
                float deltaTime = Math.min(distance, factor);
                return current + deltaTime * Math.signum(delta);
            }
            case "Adaptive Slerp": {
                float delta = MathHelper.wrapAngleTo180_float(target - current);
                float distance = Math.abs(delta);
                double smoothFactor = Math.pow(distance, 2.0);
                float deltaTime = Math.min((float) smoothFactor * speed, distance);
                return current + deltaTime * Math.signum(delta);
            }
            case "Sinusoidal": {
                float delta = MathHelper.wrapAngleTo180_float(target - current);
                float factor = (float) Math.sin((speed * Math.PI) / 2);
                return current + delta * factor ;
            }
            case "Cosine Interpolation": {
                float delta = MathHelper.wrapAngleTo180_float(target - current);
                float factor = (float) ((1 - Math.cos(Math.PI * speed)) * 0.5f);
                return current + delta * factor;
            }
            case "Logarithmic Interpolation": {
                float delta = MathHelper.wrapAngleTo180_float(target - current);
                float factor = (float) Math.log(1 + speed);
                return current + delta * factor;
            }
            case "Elastic Spring": {
                float delta = MathHelper.wrapAngleTo180_float(target - current);
                float elastic = elasticity;
                float damping = dampingFactor;
                float factor = (float) (Math.exp((-elastic * speed)) * Math.cos(damping * speed * Math.PI));
                return current + delta * factor;
            }
            case "Bezier": {

                float p0 = bezierP0;
                float p1 = bezierP1;
                float p2 = bezierP2;
                float p3 = bezierP3;
                float p4 = bezierP4;
                float p5 = bezierP5;
                float p6 = bezierP6;
                float p7 = bezierP7;
                float factor = (float) ((Math.pow(1 - speed, 7) * p0) +
                        7 * Math.pow(1 - speed, 6) * speed * p1 +
                        21 * Math.pow(1 - speed, 5) * Math.pow(speed, 2) * p2 +
                        35 * Math.pow(1 - speed, 4) * Math.pow(speed, 3) * p3 +
                        35 * Math.pow(1 - speed, 3) * Math.pow(speed, 4) * p4 +
                        21 * Math.pow(1 - speed, 2) * Math.pow(speed, 5) * p5 +
                        7 * (1 - speed) * Math.pow(speed, 6) * p6 +
                        Math.pow(speed, 7) * p7);
                return current + MathHelper.wrapAngleTo180_float(target - current) * factor;
            }
            default: {
                return target;
            }
        }
    }

    public static float[] applyGCDFix(float[] prevRotation, float[] currentRotation) {
        final float f = (float) (mc.gameSettings.mouseSensitivity * (1 + Math.random() / 100000) * 0.6F + 0.2F);
        final double gcd = f * f * f * 8.0F * 0.15D;
        final float yaw = prevRotation[0] + (float) (Math.round((currentRotation[0] - prevRotation[0]) / gcd) * gcd);
        final float pitch = prevRotation[1] + (float) (Math.round((currentRotation[1] - prevRotation[1]) / gcd) * gcd);

        return new float[]{yaw, pitch};
    }

    public static float getAngleDifference(float a, float b) {
        return MathHelper.wrapAngleTo180_float(a - b);
    }

    public static float[] getAngles(Entity entity) {
        if (entity == null) return null;
        final EntityPlayerSP player = mc.thePlayer;

        final double diffX = entity.posX - player.posX,
                diffY = entity.posY + (entity.getEyeHeight() / 5 * 3) - (player.posY + player.getEyeHeight()),
                diffZ = entity.posZ - player.posZ, dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);

        final float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F,
                pitch = (float) -(Math.atan2(diffY, dist) * 180.0D / Math.PI);

        return new float[]{player.rotationYaw + MathHelper.wrapAngleTo180_float(
                yaw - player.rotationYaw), player.rotationPitch + MathHelper.wrapAngleTo180_float(pitch - player.rotationPitch)};
    }

    public static float i(final double n, final double n2) {
        return (float) (Math.atan2(n - mc.thePlayer.posX, n2 - mc.thePlayer.posZ) * 57.295780181884766 * -1.0);
    }

    public static double distanceFromYaw(final Entity entity) {
        return Math.abs(MathHelper.wrapAngleTo180_double(i(entity.posX, entity.posZ) - mc.thePlayer.rotationYaw));
    }

    public static double getRotationDifference(float[] e) {
        return getRotationDifference(serverRotation, e);
    }

    public static double getRotationDifference(Vec3 e) {
        float[] entityRotation = getRotations(e.xCoord, e.yCoord, e.zCoord);
        return getRotationDifference(entityRotation);
    }

    public static float getRotationDifference(final Entity entity) {
        float[] target = RotationUtils.getRotations(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ);
        return (float) hypot(Math.abs(getAngleDifference(target[0], mc.thePlayer.rotationYaw)), Math.abs(target[1] - mc.thePlayer.rotationPitch));
    }

    public static float getRotationDifference(final Entity entity, final Entity entity2) {
        float[] target = RotationUtils.getRotations(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ);
        float[] target2 = RotationUtils.getRotations(entity2.posX, entity2.posY + entity2.getEyeHeight(), entity2.posZ);
        return (float) hypot(Math.abs(getAngleDifference(target[0], target2[0])), Math.abs(target[1] - target2[1]));
    }

    public static float getRotationDifference(final float[] a, final float[] b) {
        return (float) hypot(Math.abs(getAngleDifference(a[0], b[0])), Math.abs(a[1] - b[1]));
    }

    public static MovingObjectPosition rayTrace(float[] rot, double blockReachDistance, float partialTicks) {
        Vec3 vec3 = mc.thePlayer.getPositionEyes(partialTicks);
        Vec3 vec31 = mc.thePlayer.getLookCustom(rot[0], rot[1]);
        Vec3 vec32 = vec3.addVector(vec31.xCoord * blockReachDistance, vec31.yCoord * blockReachDistance, vec31.zCoord * blockReachDistance);
        return mc.theWorld.rayTraceBlocks(vec3, vec32, false, false, false);
    }

    public static MovingObjectPosition rayTrace(double blockReachDistance, float partialTicks) {
        Vec3 vec3 = mc.thePlayer.getPositionEyes(partialTicks);
        Vec3 vec31 = mc.thePlayer.getLookCustom(serverRotation[0], serverRotation[1]);
        Vec3 vec32 = vec3.addVector(vec31.xCoord * blockReachDistance, vec31.yCoord * blockReachDistance, vec31.zCoord * blockReachDistance);
        return mc.theWorld.rayTraceBlocks(vec3, vec32, false, false, false);
    }

    public static float[] getRotations(double rotX, double rotY, double rotZ, double startX, double startY, double startZ) {
        double x = rotX - startX;
        double y = rotY - startY;
        double z = rotZ - startZ;
        double dist = MathHelper.sqrt_double(x * x + z * z);
        float yaw = (float) (Math.atan2(z, x) * 180.0 / Math.PI) - 90.0F;
        float pitch = (float) (-(Math.atan2(y, dist) * 180.0 / Math.PI));
        return new float[]{yaw, pitch};
    }

    public static float[] getRotations(double posX, double posY, double posZ) {
        return getRotations(posX, posY, posZ, mc.thePlayer.posX, mc.thePlayer.posY + (double) mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
    }

    public static float[] getRotations(Vec3 vec) {
        return getRotations(vec.xCoord, vec.yCoord, vec.zCoord);
    }

    public static float[] getRotations(BlockPos blockPos) {
        return getRotations(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, mc.thePlayer.posX, mc.thePlayer.posY + (double)mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
    }


    public static float calculateYawFromSrcToDst(final float yaw,
                                                 final double srcX,
                                                 final double srcZ,
                                                 final double dstX,
                                                 final double dstZ) {
        final double xDist = dstX - srcX;
        final double zDist = dstZ - srcZ;
        final float var1 = (float) (StrictMath.atan2(zDist, xDist) * 180.0 / Math.PI) - 90.0F;
        return yaw + MathHelper.wrapAngleTo180_float(var1 - yaw);
    }

    public static Vec3 getBestHitVec(final Entity entity) {
        final Vec3 positionEyes = mc.thePlayer.getPositionEyes(1);
        final AxisAlignedBB entityBoundingBox = entity.getEntityBoundingBox();
        final double ex = MathHelper.clamp_double(positionEyes.xCoord, entityBoundingBox.minX, entityBoundingBox.maxX);
        final double ey = MathHelper.clamp_double(positionEyes.yCoord, entityBoundingBox.minY, entityBoundingBox.maxY);
        final double ez = MathHelper.clamp_double(positionEyes.zCoord, entityBoundingBox.minZ, entityBoundingBox.maxZ);
        return new Vec3(ex, ey, ez);
    }

    public static float getYaw(@NotNull BlockPos pos) {
        return getYaw(new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5));
    }

    public static float getYaw(@NotNull AbstractClientPlayer from, @NotNull Vec3 pos) {
        return from.rotationYaw +
                MathHelper.wrapAngleTo180_float(
                        (float) Math.toDegrees(Math.atan2(pos.zCoord - from.posZ, pos.xCoord - from.posX)) - 90f - from.rotationYaw
                );
    }

    public static float getYaw(@NotNull Vec3 pos) {
        return getYaw(mc.thePlayer, pos);
    }

    public static float getPitch(@NotNull BlockPos pos) {
        return getPitch(new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5));
    }

    public static float getPitch(@NotNull AbstractClientPlayer from, @NotNull Vec3 pos) {
        double diffX = pos.xCoord - from.posX;
        double diffY = pos.yCoord - (from.posY + from.getEyeHeight());
        double diffZ = pos.zCoord - from.posZ;

        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        return from.rotationPitch + MathHelper.wrapAngleTo180_float((float) -Math.toDegrees(Math.atan2(diffY, diffXZ)) - from.rotationPitch);
    }

    public static float getPitch(@NotNull Vec3 pos) {
        return getPitch(mc.thePlayer, pos);
    }

    public static MovingObjectPosition rayCast(final float[] rotation, final double range) {
        return rayCast(rotation, range, mc.timer.renderPartialTicks);
    }

    public static boolean isLookingAtEntity(final float[] rotation,final double range) {
        return isLookingAtEntity(RotationUtils.rayCast(rotation,range).entityHit,rotation,range);
    }

    public static boolean isLookingAtEntity(Entity target, final double range) {
        return isLookingAtEntity(target,RotationUtils.serverRotation,range);
    }

    public static boolean isLookingAtEntity(Entity target, float[] rotations, final double range) {
        Vec3 src = mc.thePlayer.getPositionEyes(1.0f);
        Vec3 rotationVec = mc.thePlayer.getLookCustom(rotations[0], rotations[1]);
        Vec3 dest = src.addVector(rotationVec.xCoord * range, rotationVec.yCoord * range, rotationVec.zCoord * range);
        MovingObjectPosition obj = mc.theWorld.rayTraceBlocks(src, dest, false, false, true);
        if (obj == null) {
            return false;
        }
        return target.getEntityBoundingBox().expand(target.getCollisionBorderSize(), target.getCollisionBorderSize(), target.getCollisionBorderSize()).calculateIntercept(src, dest) != null;
    }

    public static MovingObjectPosition rayCast(final float[] rots, final double range, final float partialTicks) {
        MovingObjectPosition objectMouseOver = null;
        Entity entity = mc.getRenderViewEntity();

        if (entity != null && mc.theWorld != null)
        {
            Entity pointedEntity = null;
            double d0 = mc.playerController.getBlockReachDistance();
            objectMouseOver = entity.rayTraceCustom(d0, partialTicks,rots[0],rots[1]);
            double d1 = d0;
            Vec3 vec3 = entity.getPositionEyes(partialTicks);
            boolean flag = false;
            double i = range;

            MouseOverEvent mouseOverEvent = new MouseOverEvent(i);
            Moonlight.INSTANCE.getEventManager().call(mouseOverEvent);

            i = mouseOverEvent.getRange();

            if (mc.playerController.extendedReach())
            {
                d0 = 6.0D;
                d1 = 6.0D;
            }
            else if (d0 > (ViaLoadingBase.getInstance().getTargetVersion().getVersion() <= 47 ? 3.0D : 2.9D))
            {
                flag = true;
            }

            if (objectMouseOver != null)
            {
                d1 = objectMouseOver.hitVec.distanceTo(vec3);
            }

            Vec3 vec31 = entity.getLookCustom(RotationUtils.serverRotation[0],RotationUtils.serverRotation[1]);
            Vec3 vec32 = vec3.addVector(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0);
            Vec3 vec33 = null;
            float f = 1.0F;
            List<Entity> list = mc.theWorld.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().addCoord(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0).expand(f, f, f), Predicates.and(EntitySelectors.NOT_SPECTATING, Entity::canBeCollidedWith));
            double d2 = d1;

            for (Entity entity1 : list) {
                float f1 = entity1.getCollisionBorderSize();
                AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().expand(f1, f1, f1);
                MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);

                if (axisalignedbb.isVecInside(vec3)) {
                    if (d2 >= 0.0D) {
                        pointedEntity = entity1;
                        vec33 = movingobjectposition == null ? vec3 : movingobjectposition.hitVec;
                        d2 = 0.0D;
                    }
                } else if (movingobjectposition != null) {
                    double d3 = vec3.distanceTo(movingobjectposition.hitVec);

                    if (d3 < d2 || d2 == 0.0D) {
                        boolean flag1 = false;

                        if (Reflector.ForgeEntity_canRiderInteract.exists()) {
                            flag1 = Reflector.callBoolean(entity1, Reflector.ForgeEntity_canRiderInteract);
                        }

                        if (!flag1 && entity1 == entity.ridingEntity) {
                            if (d2 == 0.0D) {
                                pointedEntity = entity1;
                                vec33 = movingobjectposition.hitVec;
                            }
                        } else {
                            pointedEntity = entity1;
                            vec33 = movingobjectposition.hitVec;
                            d2 = d3;
                        }
                    }
                }
            }

            if (pointedEntity != null && flag && vec3.distanceTo(vec33) > (ViaLoadingBase.getInstance().getTargetVersion().getVersion() <= 47 ? i : i - 0.1f))
            {
                pointedEntity = null;
                objectMouseOver = new MovingObjectPosition(MovingObjectPosition.MovingObjectType.MISS, vec33, null, new BlockPos(vec33));
            }

            if (pointedEntity != null && (d2 < d1 || objectMouseOver == null))
            {
                objectMouseOver = new MovingObjectPosition(pointedEntity, vec33);
            }
        }
        return objectMouseOver;
    }

    public static float[] faceTrajectory(Entity target, boolean predict, float predictSize,float gravity,float velocity) {
        EntityPlayerSP player = mc.thePlayer;

        double posX = target.posX + (predict ? (target.posX - target.prevPosX) * predictSize : 0.0) - (player.posX + (predict ? player.posX - player.prevPosX : 0.0));
        double posY = target.getEntityBoundingBox().minY + (predict ? (target.getEntityBoundingBox().minY - target.prevPosY) * predictSize : 0.0) + target.getEyeHeight() - 0.15 - (player.getEntityBoundingBox().minY + (predict ? player.posY - player.prevPosY : 0.0)) - player.getEyeHeight();
        double posZ = target.posZ + (predict ? (target.posZ - target.prevPosZ) * predictSize : 0.0) - (player.posZ + (predict ? player.posZ - player.prevPosZ : 0.0));
        double posSqrt = Math.sqrt(posX * posX + posZ * posZ);

        velocity = Math.min((velocity * velocity + velocity * 2) / 3, 1f);

        float gravityModifier = 0.12f * gravity;

        return new float[]{
                (float) Math.toDegrees(Math.atan2(posZ, posX)) - 90f,
                (float) -Math.toDegrees(Math.atan((velocity * velocity - Math.sqrt(
                        velocity * velocity * velocity * velocity - gravityModifier * (gravityModifier * posSqrt * posSqrt + 2 * posY * velocity * velocity)
                )) / (gravityModifier * posSqrt)))
        };
    }

    public static float[] faceTrajectory(Entity target, boolean predict, float predictSize) {

        float gravity = 0.03f;
        float velocity = 0;

        return faceTrajectory(target,predict,predictSize,gravity,velocity);
    }
    public static Vec3 heuristics(Entity entity, Vec3 xyz) {
        double boxSize = 0.2;
        float f11 = entity.getCollisionBorderSize();
        double minX = MathHelper.clamp_double(
                xyz.xCoord - boxSize, entity.getEntityBoundingBox().minX - (double)f11, entity.getEntityBoundingBox().maxX + (double)f11
        );
        double minY = MathHelper.clamp_double(
                xyz.yCoord - boxSize, entity.getEntityBoundingBox().minY - (double)f11, entity.getEntityBoundingBox().maxY + (double)f11
        );
        double minZ = MathHelper.clamp_double(
                xyz.zCoord - boxSize, entity.getEntityBoundingBox().minZ - (double)f11, entity.getEntityBoundingBox().maxZ + (double)f11
        );
        double maxX = MathHelper.clamp_double(
                xyz.xCoord + boxSize, entity.getEntityBoundingBox().minX - (double)f11, entity.getEntityBoundingBox().maxX + (double)f11
        );
        double maxY = MathHelper.clamp_double(
                xyz.yCoord + boxSize, entity.getEntityBoundingBox().minY - (double)f11, entity.getEntityBoundingBox().maxY + (double)f11
        );
        double maxZ = MathHelper.clamp_double(
                xyz.zCoord + boxSize, entity.getEntityBoundingBox().minZ - (double)f11, entity.getEntityBoundingBox().maxZ + (double)f11
        );
        xyz.xCoord = MathHelper.clamp_double(xyz.xCoord + MathUtils.randomSin(), minX, maxX);
        xyz.yCoord = MathHelper.clamp_double(xyz.yCoord + MathUtils.randomSin(), minY, maxY);
        xyz.zCoord = MathHelper.clamp_double(xyz.zCoord + MathUtils.randomSin(), minZ, maxZ);
        return xyz;
    }
}
