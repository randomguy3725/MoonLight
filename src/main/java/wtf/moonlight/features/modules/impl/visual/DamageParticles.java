package wtf.moonlight.features.modules.impl.visual;

import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.opengl.GL11;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.misc.EntityUpdateEvent;
import wtf.moonlight.events.impl.player.UpdateEvent;
import wtf.moonlight.events.impl.render.Render3DEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@ModuleInfo(name = "DamageParticles",category = ModuleCategory.Visual)
public class DamageParticles extends Module {

    private final Object2FloatOpenHashMap<EntityLivingBase> healthMap = new Object2FloatOpenHashMap<>();
    private final ArrayDeque<Particles> particles = new ArrayDeque<>();

    @EventTarget
    public void onUpdate(EntityUpdateEvent e) {
        EntityLivingBase entity = e.getEntity();
        if (entity == mc.thePlayer) return;
        if (!this.healthMap.containsKey(entity)) this.healthMap.put(entity, entity.getHealth());
        float floatValue = this.healthMap.getFloat(entity);
        float health = entity.getHealth();
        if (floatValue != health) {
            boolean heal = health > floatValue;
            boolean crit = entity.hurtResistantTime < 18 || mc.thePlayer.motionY < 0 && !mc.thePlayer.onGround;
            String color = heal ? "\247a" : crit ? "\247c" : "\247e";
            String text = floatValue - health < 0.0f ? color + roundToPlace((floatValue - health) * -1.0f, 1) : color + roundToPlace(floatValue - health, 1);
            Location location = new Location(entity);
            location.setY(entity.getEntityBoundingBox().minY + (entity.getEntityBoundingBox().maxY - entity.getEntityBoundingBox().minY) / 2.0);
            location.setX(location.getX() - 0.5 + new Random(System.currentTimeMillis()).nextInt(5) * 0.1);
            location.setZ(location.getZ() - 0.5 + new Random(System.currentTimeMillis() + 1).nextInt(5) * 0.1);
            this.particles.add(new Particles(location, text));
            this.healthMap.remove(entity);
            this.healthMap.put(entity, entity.getHealth());
        }
    }

    @EventTarget
    public void onRender(Render3DEvent e) {
        for (Particles p : this.particles) {
            double x = p.location.getX();
            double n = x - mc.getRenderManager().renderPosX;
            double y = p.location.getY();
            double n2 = y - mc.getRenderManager().renderPosY;
            double z = p.location.getZ();
            double n3 = z - mc.getRenderManager().renderPosZ;
            GlStateManager.pushMatrix();
            GlStateManager.enablePolygonOffset();
            GlStateManager.doPolygonOffset(1.0f, -1500000.0f);
            GlStateManager.translate((float) n, (float) n2, (float) n3);
            GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
            float textY = mc.gameSettings.thirdPersonView == 2 ? -1.0F : 1.0F;
            GlStateManager.rotate(mc.getRenderManager().playerViewX, textY, 0.0f, 0.0f);
            final double size = 0.03;
            GlStateManager.scale(-size, -size, size);
            GL11.glDepthMask(false);
            mc.fontRendererObj.drawStringWithShadow(p.text, (float) -(mc.fontRendererObj.getStringWidth(p.text) / 2), (float) -(mc.fontRendererObj.FONT_HEIGHT - 1), 0);
            mc.fontRendererObj.drawStringWithShadow(p.text, (float) -(mc.fontRendererObj.getStringWidth(p.text) / 2), (float) -(mc.fontRendererObj.FONT_HEIGHT - 1), 0);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            GL11.glDepthMask(true);
            GlStateManager.doPolygonOffset(1.0f, 1500000.0f);
            GlStateManager.disablePolygonOffset();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }

    public static double roundToPlace(double p_roundToPlace_0_, int p_roundToPlace_2_) {
        if (p_roundToPlace_2_ < 0) throw new IllegalArgumentException();
        return new BigDecimal(p_roundToPlace_0_).setScale(p_roundToPlace_2_, RoundingMode.HALF_UP).doubleValue();
    }

    @EventTarget
    public void onUpdate(UpdateEvent eventUpdate) {
        for (var iterator = this.particles.iterator(); iterator.hasNext(); ) {
            Particles update = iterator.next();
            ++update.ticks;
            if (update.ticks <= 10)
                update.location.setY(update.location.getY() + update.ticks * 0.005);
            if (update.ticks > 20)
                iterator.remove();
        }
    }

    public static class Particles {
        public int ticks;
        public Location location;
        public String text;

        public Particles(final Location location, final String text) {
            this.location = location;
            this.text = text;
            this.ticks = 0;
        }
    }

    @Getter
    @Setter
    public static class Location {
        private double x;
        private double y;
        private double z;
        private float yaw;
        private float pitch;

        public Location(double x, double y, double z, float yaw, float pitch) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.yaw = yaw;
            this.pitch = pitch;
        }


        public Location(EntityLivingBase entity) {
            this.x = entity.posX;
            this.y = entity.posY;
            this.z = entity.posZ;
            this.yaw = 0.0f;
            this.pitch = 0.0f;
        }
    }
}
