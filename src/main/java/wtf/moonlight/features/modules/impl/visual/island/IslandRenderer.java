package wtf.moonlight.features.modules.impl.visual.island;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.opengl.GL11;
import wtf.moonlight.Moonlight;
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

@Getter
@Setter
public class IslandRenderer implements InstanceAccess {

    public static IslandRenderer INSTANCE = new IslandRenderer();

    public ContinualAnimation animatedX = new ContinualAnimation();
    public ContinualAnimation animatedY = new ContinualAnimation();
    public float x,y,width, height;
    private ScaledResolution sr;

    public FontRenderer largest = Fonts.interBold.get(20);
    public FontRenderer titleFont = Fonts.interMedium.get(18);
    public FontRenderer medium = Fonts.interMedium.get(15);
    public FontRenderer small = Fonts.interRegular.get(10);

    public String title, description;

    public IslandRenderer() {
        this.sr = new ScaledResolution(mc);
        if (mc.theWorld == null) {
            x = sr.getScaledWidth() / 2f;
            y = 40;
            width = 0;
            height = 0;
            this.title = "";
        }
    }

    public void render(ScaledResolution sr, boolean shader) {
        this.sr = sr;

        if (mc.theWorld == null) {
            x = sr.getScaledWidth() / 2f;
            y = 40;
            width = 0;
            height = 0;
            this.title = "";
        }

        if (Moonlight.INSTANCE.getModuleManager().getModule(Scaffold.class).isEnabled() && Moonlight.INSTANCE.getModuleManager().getModule(Scaffold.class).getBlockCount() > 0) {

            title = "Block Counter";
            int size = Moonlight.INSTANCE.getModuleManager().getModule(Scaffold.class).getBlockCount();
            description = "Stack Size: " + (size > 64 ? EnumChatFormatting.GREEN : size > 32 ? EnumChatFormatting.YELLOW : EnumChatFormatting.RED) + size;

            width = Math.max(medium.getStringWidth(description), largest.getStringWidth(title) + 10) + 10;
            height = 30;
            x = sr.getScaledWidth() / 2f;
            y = 40;

            runToXy(x, y);

            GL11.glEnable(GL11.GL_SCISSOR_TEST);

            drawBackgroundAuto(1);

            RoundedUtils.drawRound(animatedX.getOutput() + 6, animatedY.getOutput() + ((y - animatedY.getOutput()) * 2), width - 12, 5f, 2.5f, new Color(255, 255, 255, 80));
            RoundedUtils.drawRound(animatedX.getOutput() + 6, animatedY.getOutput() + ((y - animatedY.getOutput()) * 2), (width - 12) * Math.min(64,size) / 64, 5f, 2.5f, new Color(255, 255, 255, 255));
            
            if(!shader) {
                largest.drawString(title, animatedX.getOutput() + 5, animatedY.getOutput() + 6, -1);
                medium.drawString(description, animatedX.getOutput() + 5, animatedY.getOutput() + 18, -1);
            }

            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        } else if (!Moonlight.INSTANCE.getNotificationManager().getNotifications().isEmpty()) {

            for (Notification notification : Moonlight.INSTANCE.getNotificationManager().getNotifications()) {
                Animation animation = notification.getAnimation();
                animation.setDirection(notification.getTimerUtils().hasTimeElapsed((long) notification.getTime()) ? Direction.BACKWARDS : Direction.FORWARDS);

                if (!Moonlight.INSTANCE.getModuleManager().getModule(Interface.class).notificationMode.is("Exhi") && notification.getAnimation().finished(Direction.BACKWARDS)) {
                    Moonlight.INSTANCE.getNotificationManager().getNotifications().remove(notification);
                }
            }

            Notification notification = Moonlight.INSTANCE.getNotificationManager().getNotifications().get(Moonlight.INSTANCE.getNotificationManager().getNotifications().size() - 1);

            Animation animation = notification.getAnimation();
            animation.setDirection(notification.getTimerUtils().hasTimeElapsed((long) notification.getTime()) ? Direction.BACKWARDS : Direction.FORWARDS);

            if (!animation.finished(Direction.BACKWARDS)) {
                title = notification.getTitle();
                description = notification.getDescription();

                width = Math.max(medium.getStringWidth(description), largest.getStringWidth(title) + 10) + 10;
                height = 30;
                x = sr.getScaledWidth() / 2f;
                y = 40;

                runToXy(x, y);

                GL11.glEnable(GL11.GL_SCISSOR_TEST);

                drawBackgroundAuto(1);

                RoundedUtils.drawRound(animatedX.getOutput() + 6, animatedY.getOutput() + ((y - animatedY.getOutput()) * 2), (width - 12), 5f, 2.5f, new Color(255, 255, 255, 80));
                RoundedUtils.drawRound(animatedX.getOutput() + 6, animatedY.getOutput() + ((y - animatedY.getOutput()) * 2), (width - 12) * Math.min((notification.getTimerUtils().getTime() / notification.getTime()), 1), 5f, 2.5f, new Color(255, 255, 255, 255));

                if (!shader) {
                    largest.drawString(title, animatedX.getOutput() + 5, animatedY.getOutput() + 6, -1);
                    medium.drawString(description, animatedX.getOutput() + 5, animatedY.getOutput() + 18, -1);
                }

                GL11.glDisable(GL11.GL_SCISSOR_TEST);
            }
        } else {
            title = "MoonLight" + EnumChatFormatting.WHITE + " | " + mc.thePlayer.getName() + " | " + Minecraft.getDebugFPS() + " FPS";
            width = titleFont.getStringWidth(title) + 10;
            height = 15;
            x = sr.getScaledWidth() / 2f;
            y = 40;

            runToXy(x, y);

            GL11.glPushMatrix();
            GL11.glEnable(GL11.GL_SCISSOR_TEST);

            drawBackgroundAuto(0);

            if(!shader) {
                titleFont.drawString(title, animatedX.getOutput() + 5, animatedY.getOutput() + 5, Moonlight.INSTANCE.getModuleManager().getModule(Interface.class).color());
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

    public void drawBackgroundAuto(int identifier) {
        float renderHeight = ((y - animatedY.getOutput()) * 2) + (identifier == 1 ? 10 : 0);

        RenderUtils.scissor(animatedX.getOutput() - 1, animatedY.getOutput() - 1, ((x - animatedX.getOutput()) * 2) + 2, renderHeight + 2);
        RoundedUtils.drawRound(animatedX.getOutput(), animatedY.getOutput(), (x - animatedX.getOutput()) * 2, renderHeight, 7, new Color(Moonlight.INSTANCE.getModuleManager().getModule(Interface.class).bgColor()));
    }
}
