package wtf.moonlight.features.modules.impl.visual.island;

import net.minecraft.item.ItemStack;
import wtf.moonlight.Moonlight;

import wtf.moonlight.gui.notification.NotificationType;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraft.util.EnumChatFormatting;
import net.optifine.util.FontUtils;
import org.lwjgl.opengl.GL11;

import wtf.moonlight.features.modules.impl.movement.Scaffold;
import wtf.moonlight.features.modules.impl.visual.Interface;
import wtf.moonlight.gui.font.FontRenderer;
import wtf.moonlight.gui.font.Fonts;
import wtf.moonlight.gui.notification.Notification;
import wtf.moonlight.utils.InstanceAccess;
import wtf.moonlight.utils.animations.Animation;
import wtf.moonlight.utils.animations.ContinualAnimation;
import wtf.moonlight.utils.animations.Direction;
import wtf.moonlight.utils.render.RenderUtils;
import wtf.moonlight.utils.render.RoundedUtils;

import java.awt.*;
import java.util.Collection;

@Getter
@Setter
public class IslandRenderer implements InstanceAccess {

    public static IslandRenderer INSTANCE = new IslandRenderer();
    public static S38PacketPlayerListItem.AddPlayerData playerData;
    public ContinualAnimation animatedX = new ContinualAnimation();
    public ContinualAnimation animatedY = new ContinualAnimation();
    public float x, y,y1, width, height;
    private ScaledResolution sr;

    public FontRenderer largest = Fonts.interBold.get(20);
    public FontRenderer logo = Fonts.logo.get(50);
    public FontRenderer titleFont = Fonts.interMedium.get(16);
    public FontRenderer medium = Fonts.interMedium.get(15);
    public FontRenderer small = Fonts.interRegular.get(10);

    public String title, description, logotitile;

    public IslandRenderer() {
        this.sr = new ScaledResolution(mc);
        if (mc.theWorld == null) {
            x = sr.getScaledWidth() / 2f;
            y = 25;
            width = 0;
            height = 0;
            this.title = "";
        }
    }

    public void shader(ScaledResolution sr, boolean shader) {
        renderBase(sr, shader, true);
    }

    public void render(ScaledResolution sr, boolean shader) {
        renderBase(sr, shader, false);
    }

    private void renderBase(ScaledResolution sr, boolean shader, boolean isShader) {
        this.sr = sr;

        if (mc.theWorld == null) {
            x = sr.getScaledWidth() / 2f;
            y = 25;
            width = 0;
            height = 0;
            this.title = "";
            return;
        }

        if (Moonlight.INSTANCE.getModuleManager().getModule(Scaffold.class).isEnabled() && Moonlight.INSTANCE.getModuleManager().getModule(Scaffold.class).getBlockCount() > 0) {
            handleScaffoldRender(shader, isShader);
        } else {
            handleNotificationRender(sr, shader, isShader);
        }
    }

    private void handleScaffoldRender(boolean shader, boolean isShader) {
        title = "Scaffold Toggled";
        int size = Moonlight.INSTANCE.getModuleManager().getModule(Scaffold.class).getBlockCount();
        description = "" + (size > 64 ? EnumChatFormatting.WHITE : size > 10 ? EnumChatFormatting.WHITE : EnumChatFormatting.RED) + size + " blocks left";

        width = Math.max(medium.getStringWidth(description), largest.getStringWidth(title) + 10) + 10 + 10;
        height = 30;
        x = sr.getScaledWidth() / 2f;
        y = 25;

        runToXy(x, y);

        GL11.glEnable(GL11.GL_SCISSOR_TEST);

        if (isShader) {
            drawShaderBackgroundAuto(1);
        } else {
            drawBackgroundAuto(1);
        }

        RoundedUtils.drawRound(animatedX.getOutput() + 6, animatedY.getOutput() + ((y - animatedY.getOutput()) * 2) + 5, width - 12 + 14, 5f, 2.5f, new Color(255, 255, 255, 80));
        RoundedUtils.drawRound(animatedX.getOutput() + 6, animatedY.getOutput() + ((y - animatedY.getOutput()) * 2) + 5, (width - 12) * Math.min(64, size) / 64 + 14, 5f, 2.5f, new Color(255, 255, 255, 255));

        if (!shader) {
            largest.drawString(title, animatedX.getOutput() + 5, animatedY.getOutput() + 6, new Color(219, 255, 249, 255).getRGB());
            medium.drawString(description, animatedX.getOutput() + 5, animatedY.getOutput() + 18, -1);
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    private void handleNotificationRender(ScaledResolution sr, boolean shader, boolean isShader) {
        var notifications = Moonlight.INSTANCE.getNotificationManager().getNotifications();
        if (!notifications.isEmpty()) {
            boolean isExhi = Moonlight.INSTANCE.getModuleManager().getModule(Interface.class).notificationMode.is("Exhi");
            notifications.removeIf(it -> {
                var animation = it.getAnimation();
                animation.setDirection(it.getTimerUtils().hasTimeElapsed((long) it.getTime()) ? Direction.BACKWARDS : Direction.FORWARDS);
                return !isExhi && it.getAnimation().finished(Direction.BACKWARDS);
            });

            int count = 0;


            Notification notification = notifications.getLast();
            Animation animation = notification.getAnimation();

            if (!animation.finished(Direction.BACKWARDS)) {
                title = notification.getTitle();
                description = notification.getDescription();

                width = Math.max(medium.getStringWidth(description), largest.getStringWidth(title) + 10) + 10 + 10 + 50;
                height = 30;
                x = sr.getScaledWidth() / 2f;

                y = 25;
                float renderHeight = (y - animatedY.getOutput()) * 2 + 10;

                runToXy(x, y);

                GL11.glEnable(GL11.GL_SCISSOR_TEST);

                if (isShader) {
                    drawShaderBackgroundAuto(1);
                } else {
                    drawBackgroundAuto(1);
                }

                if (NotificationType.WARNING.equals(notification.getNotificationType())) {
                    RoundedUtils.drawRound(animatedX.getOutput() + 6 + 7 - 1, animatedY.getOutput() + 18 - 5 + 2, 25, 16, 7f, new Color(224, 255, 251, 255));
                    RoundedUtils.drawRound(animatedX.getOutput() + 6 + 9 + 1 - 1, animatedY.getOutput() + 18 - 5 + 4 + 1, 10, 10f, 5f, new Color(117, 117, 117, 255));
                } else if (NotificationType.OKAY.equals(notification.getNotificationType())) {
                    RoundedUtils.drawRound(animatedX.getOutput() + 6 + 7 - 1, animatedY.getOutput() + 18 - 5 + 2, 25, 16, 7f, new Color(224, 255, 251, 255));
                    RoundedUtils.drawRound(animatedX.getOutput() + 6 + 18, animatedY.getOutput() + 18 - 5 + 4 + 1, 10, 10f, 5f, new Color(117, 117, 117, 255));
                }

                if (!shader) {
                    largest.drawString(title, animatedX.getOutput() + 5 + 45, animatedY.getOutput() + 6 + 8, -1);
                    medium.drawString(description, animatedX.getOutput() + 5 + 45, animatedY.getOutput() + 18 + 8, -1);
                }

                GL11.glDisable(GL11.GL_SCISSOR_TEST);
            }
        } else {
            title = "Moonlight" + EnumChatFormatting.WHITE + " | " + mc.thePlayer.getName() + " | " + Minecraft.getDebugFPS() + " FPS" + " | " + getServerPing() + " ms";
            logotitile = "a";
            width = titleFont.getStringWidth(title) + 10;
            height = 15;
            x = sr.getScaledWidth() / 2f;
            y = 25;

            runToXy(x, y);

            GL11.glPushMatrix();
            GL11.glEnable(GL11.GL_SCISSOR_TEST);

            if (isShader) {
                drawShaderBackgroundAuto(0);
            } else {
                drawBackgroundAuto(0);
            }

            if (!shader) {
                titleFont.drawString(title, animatedX.getOutput() + 3 + logo.getStringWidth(logotitile) + 5, animatedY.getOutput() + 5 + 2, -1);
                logo.drawString(logotitile, animatedX.getOutput() + 5, animatedY.getOutput() - 5, new Color(224, 255, 251, 255).getRGB());
            }

            GL11.glDisable(GL11.GL_SCISSOR_TEST);
            GL11.glPopMatrix();
        }
    }

    public float getRenderX(float x) {
        return x - width / 2;
    }

    public float getRenderY(float y) {
        return y - height / 2;
    }

    public void runToXy(float realX, float realY) {
        animatedX.animate(getRenderX(realX), 40);
        animatedY.animate(getRenderY(realY), 40);
    }

    public static int getServerPing() {
        // 获取 Minecraft 实例
        Minecraft minecraft = Minecraft.getMinecraft();
        // 获取网络处理客户端实例
        NetHandlerPlayClient netHandler = minecraft.getNetHandler();

        if (netHandler != null) {
            // 获取所有玩家的网络信息
            Collection<NetworkPlayerInfo> playerInfoCollection = netHandler.getPlayerInfoMap();
            // 通常第一个玩家信息就是本地玩家的信息
            if (!playerInfoCollection.isEmpty()) {
                NetworkPlayerInfo playerInfo = playerInfoCollection.iterator().next();
                // 获取 ping 值
                return playerInfo.getResponseTime();
            }
        }
        return 0; // 若无法获取 ping 值，返回 0
    }

    public void drawBackgroundAuto(int identifier) {
        float renderHeight = ((y - animatedY.getOutput()) * 2) + (identifier == 1 ? 10 : 0);

        RenderUtils.scissor(animatedX.getOutput() - 1, animatedY.getOutput() - 1, ((x - animatedX.getOutput()) * 2) + 2 + 14, renderHeight + 2 + 4);
        RoundedUtils.drawRound(animatedX.getOutput(), (float) animatedY.getOutput(), (x - animatedX.getOutput()) * 2 + 14, renderHeight + 4, 9, new Color(10, 10, 10, 180));
    }

    public void drawBackgroundAuto2(int identifier) {
        float renderHeight = ((y1 - animatedY.getOutput()) * 2) + (identifier == 1 ? 10 : 0);

        RenderUtils.scissor(animatedX.getOutput() - 1, animatedY.getOutput() - 1, ((x - animatedX.getOutput()) * 2) + 2 + 14, renderHeight + 2 + 4);
        RoundedUtils.drawRound(animatedX.getOutput(), (float) animatedY.getOutput(), (x - animatedX.getOutput()) * 2 + 14, renderHeight + 4, 9, new Color(10, 10, 10, 180));
    }

    public void drawShaderBackgroundAuto2(int identifier) {
        float renderHeight = ((y1 - animatedY.getOutput()) * 2) + (identifier == 1 ? 10 : 0);

        RenderUtils.scissor(animatedX.getOutput() - 1, animatedY.getOutput() - 1, ((x - animatedX.getOutput()) * 2) + 2 + 14, renderHeight + 2 + 4);
        RoundedUtils.drawRound(animatedX.getOutput(), animatedY.getOutput(), (x - animatedX.getOutput()) * 2 + 14, renderHeight + 4, 9, new Color(10 10, 10, 255));
    }


    public void drawShaderBackgroundAuto(int identifier) {
        float renderHeight = ((y - animatedY.getOutput()) * 2) + (identifier == 1 ? 10 : 0);

        RenderUtils.scissor(animatedX.getOutput() - 1, animatedY.getOutput() - 1, ((x - animatedX.getOutput()) * 2) + 2 + 14, renderHeight + 2 + 4);
        RoundedUtils.drawRound(animatedX.getOutput(), animatedY.getOutput(), (x - animatedX.getOutput()) * 2 + 14, renderHeight + 4, 9, new Color(65, 65, 65, 255));
    }
}
