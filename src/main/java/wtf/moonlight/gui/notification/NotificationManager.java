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
package wtf.moonlight.gui.notification;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import wtf.moonlight.features.modules.impl.visual.Interface;
import wtf.moonlight.gui.font.Fonts;
import wtf.moonlight.utils.InstanceAccess;
import wtf.moonlight.utils.animations.Animation;
import wtf.moonlight.utils.animations.Direction;
import wtf.moonlight.utils.animations.Translate;
import wtf.moonlight.utils.render.ColorUtils;
import wtf.moonlight.utils.render.RenderUtils;
import wtf.moonlight.utils.render.RoundedUtils;

import java.awt.*;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

@Getter
public class NotificationManager implements InstanceAccess {
    private final Deque<Notification> notifications = new ConcurrentLinkedDeque<>();

    @Setter
    private float toggleTime = 2;

    public void post(NotificationType type, String title, String description) {
        post(new Notification(type, title, description));
    }

    public void post(NotificationType type, String title, String description, float time) {
        post(new Notification(type, title, description, time));
    }
    public void post(NotificationType type, String title) {
        post(new Notification(type, title, title));
    }

    private void post(Notification notification) {
        if (INSTANCE.getModuleManager().getModule(Interface.class).elements.isEnabled("Notification") || INSTANCE.getModuleManager().getModule(Interface.class).elements.isEnabled("Island")) {
            notifications.add(notification);
        }
    }

    public void publish(ScaledResolution sr,boolean shader) {
        float yOffset = 0;
        for (Notification notification : getNotifications()) {
            float width = (float) notification.getWidth();
            float height = (float) notification.getHeight();

            Animation animation = notification.getAnimation();
            animation.setDirection(notification.getTimerUtils().hasTimeElapsed((long) notification.getTime()) ? Direction.BACKWARDS : Direction.FORWARDS);

            if (!INSTANCE.getModuleManager().getModule(Interface.class).notificationMode.is("Exhi") && notification.getAnimation().finished(Direction.BACKWARDS)) {
                getNotifications().remove(notification);
            }

            if (!animation.finished(Direction.BACKWARDS)) {
                float x = 0;
                float y = 0;
                float yVal;
                float actualOffset = 0;
                switch (INSTANCE.getModuleManager().getModule(Interface.class).notificationMode.get()) {
                    case "Default":
                        actualOffset = 3;

                        x = (sr.getScaledWidth() - ((sr.getScaledWidth() / 2f + width / 2f)));
                        y = sr.getScaledHeight() / 2f - height / 2f + 55 + yOffset;

                        yVal = (y + height) - height;
                        if (!shader) {
                            RoundedUtils.drawRound(x, yVal, width + 2, height, 4, ColorUtils.applyOpacity(new Color(INSTANCE.getModuleManager().getModule(Interface.class).bgColor(), true), (float) notification.getAnimation().getOutput()));

                            Fonts.interMedium.get(15).drawCenteredStringNoFormat(notification.getDescription(), x + width / 2f,
                                    yVal + Fonts.interMedium.get(15).getMiddleOfBox(height) + 2, ColorUtils.applyOpacity(-1, (float) notification.getAnimation().getOutput()));
                        } else {
                            RoundedUtils.drawRound(x, yVal, width + 2, height, 4, ColorUtils.applyOpacity(new Color(INSTANCE.getModuleManager().getModule(Interface.class).bgColor(), true), (float) notification.getAnimation().getOutput()));
                        }
                        yOffset += (height + actualOffset) * (float) notification.getAnimation().getOutput();
                        break;
                    case "Test":

                        notification.getAnimation().setDuration(200);
                        actualOffset = 3;

                        x = sr.getScaledWidth() - (width + 5);

                        float heightVal = (float) (height * notification.getAnimation().getOutput());
                        yVal = (y + height) - heightVal;
                        if (!shader) {
                            RoundedUtils.drawRound(x, yVal, width, heightVal, 4, ColorUtils.applyOpacity(new Color(INSTANCE.getModuleManager().getModule(Interface.class).bgColor(), true), (float) notification.getAnimation().getOutput()));
                            Fonts.interSemiBold.get(15).drawCenteredStringNoFormat(notification.getTitle(), x + width / 2f, yVal + 2, ColorUtils.applyOpacity(Color.WHITE.getRGB(), (float) notification.getAnimation().getOutput()));
                            Fonts.interSemiBold.get(15).drawCenteredStringNoFormat(notification.getDescription(), x + width / 2f, yVal + 2 + Fonts.interSemiBold.get(15).getHeight(), ColorUtils.applyOpacity(Color.WHITE.getRGB(), (float) notification.getAnimation().getOutput()));
                        } else {
                            RoundedUtils.drawRound(x, yVal, width, heightVal, 4, ColorUtils.applyOpacity(new Color(INSTANCE.getModuleManager().getModule(Interface.class).bgColor(), true), (float) notification.getAnimation().getOutput()));
                        }
                        yOffset += (height + actualOffset) * (float) notification.getAnimation().getOutput();
                        break;
                    case "Test2":
                        if (!shader) {
                            notification.getAnimation().setDuration(350);
                            actualOffset = 10;

                            x = (float) (sr.getScaledWidth() - (width + 5) * animation.getOutput());
                            y = sr.getScaledHeight() - 43 - height - yOffset;

                            RenderUtils.drawRect(x, y, width, height, INSTANCE.getModuleManager().getModule(Interface.class).bgColor());
                            Fonts.interSemiBold.get(17).drawStringNoFormat(notification.getTitle(), x + 7, y + 7, new Color(255, 255, 255, 255).getRGB());
                            Fonts.interRegular.get(17).drawStringNoFormat(notification.getDescription(), x + 7, y + 18f, new Color(255, 255, 255, 120).getRGB());
                            RenderUtils.drawRect(x, y + height - 1, width * Math.min((notification.getTimerUtils().getTime() / notification.getTime()), 1), 1, INSTANCE.getModuleManager().getModule(Interface.class).color());
                        } else {
                            RenderUtils.drawRect(x, y, width, height, INSTANCE.getModuleManager().getModule(Interface.class).bgColor());
                        }
                        yOffset += (height + actualOffset) * (float) notification.getAnimation().getOutput();
                        break;
                    case "Exhi": {
                        Translate translate = notification.getTranslate();
                        boolean middlePos = INSTANCE.getModuleManager().getModule(Interface.class).centerNotif.get() && mc.thePlayer != null && (mc.currentScreen instanceof GuiChat || mc.currentScreen == null);
                        int scaledHeight = sr.getScaledHeight();
                        int scaledWidth = sr.getScaledWidth();
                        y = middlePos ? (int) (scaledHeight / 2.0f + 43.0f) : scaledHeight - (mc.currentScreen instanceof GuiChat ? 45 : 31);
                        if (!notification.getTimerUtils().hasTimeElapsed(notification.getTime())) {
                            translate.translate(middlePos ? scaledWidth / 2.0f - (width / 2.0f) : (scaledWidth - width), y + yOffset);
                            if (middlePos) {
                                yOffset += height;
                            }
                        } else {
                            translate.translate(scaledWidth, y + yOffset);
                            if (!middlePos) {
                                yOffset += height;
                            }
                        }
                        if (!shader) {
                            RenderUtils.drawRect((float) translate.getX(), (float) translate.getY(), width, height, new Color(0, 0, 0, 185).getRGB());
                            float percentage = Math.min((notification.getTimerUtils().getTime() / notification.getTime()), 1);
                            RenderUtils.drawRect((float) (translate.getX() + (width * percentage)), (float) (translate.getY() + height - 1), width - (width * percentage), 1, notification.getNotificationType().getColor().getRGB());
                            RenderUtils.drawImage(new ResourceLocation("moonlight/texture/noti/" + notification.getNotificationType().getName() + ".png"), (float) translate.getX() + 2f, (float) translate.getY() + 4.5f, 18, 18);

                            Fonts.interRegular.get(18).drawStringNoFormat(notification.getTitle(), translate.getX() + 21.5f, translate.getY() + 4.5, -1);
                            Fonts.interRegular.get(14).drawStringNoFormat(notification.getDescription(), translate.getX() + 21.5f, translate.getY() + 15.5, -1);
                        }
                        if (!middlePos) {
                            yOffset -= height;
                        }
                    }
                    break;

                    case "Type 2":

                        notification.getAnimation().setDuration(150);
                        actualOffset = 3;

                        x = sr.getScaledWidth() - (width + 5);
                        y = sr.getScaledHeight() - 43 - height - yOffset;

                        GlStateManager.pushMatrix();
                        GlStateManager.translate(x + width / 2F, y + height / 2F, 0);
                        GlStateManager.scale(animation.getOutput(), animation.getOutput(), animation.getOutput());
                        GlStateManager.translate(-(x + width / 2F), -(y + height / 2F), 0);

                        if (!shader) {
                            RoundedUtils.drawRound(x, y, width, height, 4, new Color(INSTANCE.getModuleManager().getModule(Interface.class).bgColor(), true));
                            RenderUtils.drawCircle(x + 16f, y + 15f, 0, 360, 13f, .1f, true, -1);
                            RenderUtils.drawGradientCircle(x + 16f, y + 15f, 0, 360, 13f, INSTANCE.getModuleManager().getModule(Interface.class).color(0), INSTANCE.getModuleManager().getModule(Interface.class).color(90));
                            if (notification.getNotificationType() == NotificationType.INFO) {
                                Fonts.noti.get(42).drawStringNoFormat("B", x + 11F, y + 8F, 0);
                            } else if (notification.getNotificationType() == NotificationType.NOTIFY) {
                                Fonts.noti.get(42).drawStringNoFormat("A", x + 14F, y + 8F, 0);
                            } else if (notification.getNotificationType() == NotificationType.WARNING) {
                                Fonts.noti2.get(42).drawStringNoFormat("L", x + 9F, y + 10F, 0);
                            } else {
                                Fonts.noti2.get(42).drawStringNoFormat("M", x + 8F, y + 10F, 0);
                            }
                            Fonts.interSemiBold.get(20).drawStringNoFormat(notification.getTitle(), x + 34F, y + 4F, -1);
                            Fonts.interMedium.get(17).drawStringNoFormat(notification.getDescription(), x + 34F, y + 17F, -1);
                        } else {
                            RoundedUtils.drawRound(x, y, width, height, 4, new Color(INSTANCE.getModuleManager().getModule(Interface.class).color(), true));
                        }

                        yOffset += (float) ((height + actualOffset) * animation.getOutput());

                        GlStateManager.popMatrix();
                        break;
                    case "Type 3":
                        animation.setDuration(250);
                        actualOffset = 8;

                        x = sr.getScaledWidth() - (width + 5) * (float) animation.getOutput();
                        y = sr.getScaledHeight() - (yOffset + 18 + height);

                        RoundedUtils.drawRound(x, y, width, height, 4f, new Color(INSTANCE.getModuleManager().getModule(Interface.class).bgColor(0), true));
                        RoundedUtils.drawRound(x + 3, y + 5, 25, 25, 4f, new Color(INSTANCE.getModuleManager().getModule(Interface.class).color(0), true));
                        if (!shader) {
                            Fonts.interRegular.get(22).drawStringWithShadow(notification.getTitle(), x + 32, y + 6, -1);
                            Fonts.interRegular.get(20).drawStringWithShadow(notification.getDescription(), x + 32, y + 20, -1);
                            Fonts.noti2.get(50).drawStringNoFormat("k", x + 9.5f, y + 10, new Color(INSTANCE.getModuleManager().getModule(Interface.class).color(0), true).darker().darker().darker().getRGB());
                        }

                        yOffset += (float) ((height + actualOffset) * animation.getOutput());
                        break;

                    case "Type 4": {
                        animation.setDuration(250);

                        actualOffset = 5;

                        x = sr.getScaledWidth() - (width + 5) * (float) animation.getOutput();
                        y = sr.getScaledHeight() - (yOffset + 18 + height) * (float) animation.getOutput();

                        RenderUtils.drawRoundedRect(x, y, width, height, 12, INSTANCE.getModuleManager().getModule(Interface.class).bgColor(0));
                        float percentage = Math.min((notification.getTimerUtils().getTime() / notification.getTime()), 1);
                        RenderUtils.drawRect(x + 3, y + height - 3, width * percentage - 5, 1, INSTANCE.getModuleManager().getModule(Interface.class).color(0));

                        Fonts.interSemiBold.get(20).drawString(notification.getTitle(), x + 6, y + 4, -1);
                        Fonts.interSemiBold.get(16).drawString(notification.getDescription(), x + 6, y + height - Fonts.interSemiBold.get(20).getHeight(), -1);

                        yOffset += (float) ((height + actualOffset) * animation.getOutput());
                    }
                    break;

                    case "Type 5": {
                        if (!shader) {
                            animation.setDuration(250);

                            actualOffset = 6;

                            x = sr.getScaledWidth() - (width) * (float) animation.getOutput();
                            y = sr.getScaledHeight() - (yOffset + 18 + height);

                            RenderUtils.drawRect(x, y, width, height, new Color(0, 0, 0, 100).getRGB());

                            mc.fontRendererObj.drawString(notification.getTitle(), x + 25, y + 4F, Color.WHITE.getRGB(), false);
                            mc.fontRendererObj.drawString(notification.getDescription(), x + 25, y + 12.5F, Color.GRAY.getRGB(), false);

                            RenderUtils.drawImage(new ResourceLocation("moonlight/texture/noti/" + notification.getNotificationType().getName() + ".png"), x + 3.5f, y + 3, 16, 16);

                            float percentage = Math.min((notification.getTimerUtils().getTime() / notification.getTime()), 1);
                            RenderUtils.drawRect(x + (width * percentage), y + height - 1, width - (width * percentage), 1, notification.getNotificationType().getColor().getRGB());

                            yOffset += (float) ((height + actualOffset) * animation.getOutput());
                        }
                    }
                    break;
                }
            }
        }
    }
}