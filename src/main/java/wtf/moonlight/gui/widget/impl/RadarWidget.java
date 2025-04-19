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
        switch (setting.radarMode.get()) {
            case "Default": {
                GL11.glPushMatrix();

                float x = renderX;
                float y = renderY;
                width = setting.radarSize.get();
                height = setting.radarSize.get();
                float cx = x + (width / 2f);
                float cy = y + (height / 2f);

                RenderUtils.drawBorderedRect(x, y, width, height, 1, 0xFF444444, 0xFF222222);
                RenderUtils.drawRect(x + (width / 2f) - 0.5f, y, 1, height, 0xFF444444);
                RenderUtils.drawRect(x, y + (height / 2f) - 0.5f, width, 1, 0xFF444444);
                RenderUtils.drawRect(cx - 1, cy - 1, 2, 2, 0xFFFFFF00);

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
            break;
            case "Exhi": {
                GL11.glPushMatrix();

                float x = renderX;
                float y = renderY;
                width = setting.radarSize.get();
                height = setting.radarSize.get();
                float cx = x + (width / 2f);
                float cy = y + (height / 2f);

                RenderUtils.drawExhiRect(x, y - 1, width, height, 1);
                RenderUtils.drawRect(x + (width / 2f) - 0.5f, y, 1, height, 0xFF444444);
                RenderUtils.drawRect(x, y + (height / 2f) - 0.5f, width, 1, 0xFF444444);
                RenderUtils.drawRect(cx - 1, cy - 1, 2, 2, 0xFFFFFF00);

                float h = (System.currentTimeMillis() % 30000L) / 30000.0f * 255;
                float h2 = (h + 85f) % 255f;
                float h3 = (h + 170f) % 255f;

                RenderUtils.drawGradientRect(x, y - 1, width / 2, 0.5f, true, Color.getHSBColor(h / 255f, 0.8f, 1.0f).getRGB(), Color.getHSBColor(h2 / 255f, 0.8f, 1.0f).getRGB());
                RenderUtils.drawGradientRect(x + width / 2, y - 1, width / 2, 0.5f, true, Color.getHSBColor(h2 / 255f, 0.8f, 1.0f).getRGB(), Color.getHSBColor(h3 / 255f, 0.8f, 1.0f).getRGB());

                int maxDist = (int) (setting.radarSize.get() / 2);
                for (Entity entity : mc.theWorld.loadedEntityList) {

                    if (entity == mc.thePlayer) continue;

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
                                    new Color(255, 95, 34).getRGB());
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
            break;
            case "Astolfo": {
                GL11.glPushMatrix();

                float x = renderX;
                float y = renderY;
                width = setting.radarSize.get();
                height = setting.radarSize.get();
                float cx = x + (width / 2f);
                float cy = y + (height / 2f);

                RenderUtils.drawRect(x, y, width, height, new Color(0, 0, 0, 140).getRGB());
                RenderUtils.drawRect(x + (width / 2f) - 0.5f, y, 1f, height, new Color(255,255,255).getRGB());
                RenderUtils.drawRect(x, y + (height / 2f) - 0.5f, width, 1f, new Color(255,255,255).getRGB());
                RenderUtils.drawRect(x, y - 1, width, 1, setting.color());

                int maxDist = (int) (setting.radarSize.get() / 2);
                for (Entity entity : mc.theWorld.loadedEntityList) {
                    if (entity == mc.thePlayer) continue;

                    double dx = MathUtils.interpolate(entity.prevPosX, entity.posX, mc.timer.renderPartialTicks)
                            - MathUtils.interpolate(mc.thePlayer.prevPosX, mc.thePlayer.posX,
                            mc.timer.renderPartialTicks);

                    double dz = MathUtils.interpolate(entity.prevPosZ, entity.posZ, mc.timer.renderPartialTicks)
                            - MathUtils.interpolate(mc.thePlayer.prevPosZ, mc.thePlayer.posZ,
                            mc.timer.renderPartialTicks);

                    if ((dx * dx + dz * dz) <= (maxDist * maxDist)) {
                        float dist = MathHelper.sqrt_double(dx * dx + dz * dz);
                        double[] vector = getLookVector(RotationUtils.getRotations(entity.getPositionEyes(1.0f))[0] - MathUtils.interpolate(mc.thePlayer.prevRotationYaw, mc.thePlayer.rotationYaw, mc.timer.renderPartialTicks));
                        if (entity instanceof EntityPlayer) {
                            RenderUtils.drawRect(cx - 1 - ((float) vector[0] * dist), cy - 1 - ((float) vector[1] * dist), 2, 2,
                                    new Color(255, 255, 255).getRGB());
                        }
                    }
                }
                GL11.glPopMatrix();
            }
            break;
        }
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
