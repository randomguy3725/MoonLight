package wtf.moonlight.gui.widget.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;
import wtf.moonlight.events.impl.render.Shader2DEvent;
import wtf.moonlight.gui.widget.Widget;
import wtf.moonlight.utils.math.MathUtils;
import wtf.moonlight.utils.player.RotationUtils;
import wtf.moonlight.utils.render.RenderUtils;

import java.awt.*;

public class RadarWidget extends Widget {
    public RadarWidget() {
        super("Radar");
    }

    @Override
    public void onShader(Shader2DEvent event) {

    }

    @Override
    public void render() {
        GL11.glPushMatrix();

        float x = renderX;
        float y = renderY;
        width = setting.radarSize.get();
        height = setting.radarSize.get();
        float cx = x + (width / 2f);
        float cy = y + (height / 2f);

        RenderUtils.drawBorderedRect(x, y, width,  height, 1, new Color(40, 40, 40, 255).getRGB(), new Color(29, 29, 29, 255).getRGB());
        RenderUtils.drawRect(x + (width / 2f) - 0.5f, y, 1, height, new Color(255, 255, 255, 50).getRGB());
        RenderUtils.drawRect(x, y + (height / 2f) - 0.5f, width, 1, new Color(255, 255, 255, 50).getRGB());
        RenderUtils.drawRect(cx - 1, cy - 1, 2, 2, new Color(255, 255, 255, 50).getRGB());

        int maxDist = (int) (setting.radarSize.get() / 2);
        for (Entity entity : mc.theWorld.loadedEntityList) {

            double dx = MathUtils.interpolate(entity.prevPosX, entity.posX, mc.timer.renderPartialTicks)
                    - MathUtils.interpolate(mc.thePlayer.prevPosX, mc.thePlayer.posX,
                    mc.timer.renderPartialTicks);

            double dz = MathUtils.interpolate(entity.prevPosZ, entity.posZ, mc.timer.renderPartialTicks)
                    - MathUtils.interpolate(mc.thePlayer.prevPosZ, mc.thePlayer.posZ,
                    mc.timer.renderPartialTicks);

            if ((dx * dx + dz * dz) <= (maxDist * maxDist)) {
                float dist = MathHelper.sqrt_double(dx * dx + dz * dz);
                double[] vector = getLookVector(RotationUtils.getRotations(entity.getPositionEyes(1.0f))[0] - MathUtils.interpolate(mc.thePlayer.prevRotationYaw, mc.thePlayer.rotationYaw, mc.timer.renderPartialTicks));
                if (entity instanceof EntityMob) {
                    RenderUtils.drawRect(cx - 1 - ((float) vector[0] * dist), cy - 1 - ((float) vector[1] * dist), 2, 2,
                            new Color(248, 178, 0).getRGB());
                } else if (entity instanceof EntityPlayer) {
                    RenderUtils.drawRect(cx - 1 - ((float) vector[0] * dist), cy - 1 - ((float) vector[1] * dist), 2, 2,
                            new Color(248, 0, 0).getRGB());
                } else if (entity instanceof EntityAnimal || entity instanceof EntitySquid || entity instanceof EntityVillager || entity instanceof EntityGolem) {
                    RenderUtils.drawRect(cx - 1 - ((float) vector[0] * dist), cy - 1 - ((float) vector[1] * dist), 2, 2,
                            new Color(0, 252, 103).getRGB());
                }

                if (entity instanceof EntityPlayer) {
                    RenderUtils.drawRect(cx - 1 - ((float) vector[0] * dist), cy - 1 - ((float) vector[1] * dist), 2, 2,
                            new Color(248, 0, 0).getRGB());
                }
            }
        }
        GL11.glPopMatrix();
    }

    @Override
    public boolean shouldRender() {
        return setting.isEnabled() && setting.elements.isEnabled("Radar");
    }
    public double[] getLookVector(float yaw) {
        yaw *= MathHelper.deg2Rad;
        return new double[]{
                -MathHelper.sin(yaw),
                MathHelper.cos(yaw)
        };
    }
}
