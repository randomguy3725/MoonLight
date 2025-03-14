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
package wtf.moonlight.gui.widget.impl;

import lombok.val;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import wtf.moonlight.Moonlight;
import wtf.moonlight.events.impl.render.Shader2DEvent;
import wtf.moonlight.features.modules.impl.visual.Interface;
import wtf.moonlight.gui.font.Fonts;
import wtf.moonlight.gui.widget.Widget;
import wtf.moonlight.utils.render.ColorUtils;
import wtf.moonlight.utils.render.RenderUtils;
import wtf.moonlight.utils.render.RoundedUtils;

import java.awt.*;

public class SessionInfoWidget extends Widget {
    public SessionInfoWidget() {
        super("Session Info");
        this.x = 0.1f;
        this.y = 0.2f;
    }

    @Override
    public void onShader(Shader2DEvent event) {
        switch (setting.sessionInfoMode.get()) {
            case "Rise":
                if (event.getShaderType() == Shader2DEvent.ShaderType.GLOW) {
                    RoundedUtils.drawRound(renderX, renderY, this.width, this.height, 11, new Color(setting.color(0)));
                }
                if (event.getShaderType() == Shader2DEvent.ShaderType.BLUR) {
                    RoundedUtils.drawRound(renderX, renderY, this.width, this.height, 11, new Color(setting.color(0)));
                }
                break;
            case "Moon":
                if (event.getShaderType() == Shader2DEvent.ShaderType.GLOW) {
                    RoundedUtils.drawRound(renderX + 9, renderY, width, height, 8f, new Color(setting.color(0)));
                } else RoundedUtils.drawRound(renderX + 9, renderY, width, height, 8f, new Color(0, 0, 0,255));

                break;
            case "Opai":
                if (event.getShaderType() == Shader2DEvent.ShaderType.GLOW) {
                    RoundedUtils.drawRound(renderX + 9, renderY,  width, height, 8f, new Color(setting.color(0)));
                } else RoundedUtils.drawRound(renderX + 9, renderY, width, height, 8f, new Color(0, 0, 0,255));

            case "Novo":
                RoundedUtils.drawRound(renderX, renderY, width, height, 8f, new Color(setting.color(),true));
                break;

            case "Novo 2":
                RenderUtils.drawRect(renderX, renderY, width, height, setting.color());
                break;
        }
    }

    @Override
    public void render() {

        String srv;
        if (mc.isSingleplayer()) {
            srv = "Singleplayer";
        } else
            srv = mc.getCurrentServerData().serverIP.toLowerCase().contains("ccc.blocksmc.com") ? "blocksmc.com" : mc.getCurrentServerData().serverIP;

        switch (setting.sessionInfoMode.get()) {
            case "Default":
                this.width = 150;
                this.height = 100;
                RoundedUtils.drawRound(renderX, renderY, width, height, 10, new Color(setting.bgColor(), true));


                Fonts.interRegular.get(14).drawStringWithShadow("Session Time: " + RenderUtils.sessionTime(), renderX + 7.5f, renderY + 10, -1);
                Fonts.interRegular.get(14).drawStringWithShadow("User: " + mc.thePlayer.getName(), renderX + 7.5f, renderY + 10 + Fonts.interRegular.get(13).getHeight(), -1);

                float xOffset = 10;

                for (int i = 0; i < setting.match; i++) {
                    if (setting.matchKilled > 0 || setting.prevMatchKilled > 0) {
                        int prevKilled = setting.prevMatchKilled;
                        int barHeight = i == 5 ? setting.matchKilled : prevKilled;

                        GL11.glPushMatrix();
                        RenderUtils.scissor(renderX + xOffset, (renderY + height) - (2 * 10), 5, (2 * 10));
                        GL11.glEnable(GL11.GL_SCISSOR_TEST);
                        Color color = i == 5 ? new Color(setting.color()) : new Color(setting.color()).darker().darker();
                        RoundedUtils.drawRound(renderX + xOffset, (renderY + height) - (barHeight * 10) + 2, 5, (barHeight * 10) + 2, 4, color);
                        GL11.glDisable(GL11.GL_SCISSOR_TEST);
                        GL11.glPopMatrix();
                    } else {
                        Fonts.nursultan.get(15).drawString("U", renderX + xOffset, renderY + height - Fonts.nursultan.get(15).getHeight(), -1);
                    }
                    xOffset += 10;
                }

                Fonts.interRegular.get(15).drawStringWithShadow("Current game: ", renderX + xOffset, (renderY + height) - (setting.matchKilled * 10) - Fonts.interRegular.get(15).getHeight() * 2 + (float) Fonts.interRegular.get(13).getHeight() / 2, -1);
                Fonts.interRegular.get(13).drawStringWithShadow(setting.matchKilled + " kills", renderX + xOffset, (renderY + height) - (setting.matchKilled * 10) - Fonts.interRegular.get(15).getHeight() + (float) Fonts.interRegular.get(13).getHeight() / 2, -1);
                break;

            case "Rise":
                this.width = 140;
                this.height = 55;
                double padding = 8;
                RoundedUtils.drawRoundOutline(renderX, renderY, this.width, this.height, 11, 0.1f, new Color(0, 0, 0, 100), new Color(setting.color(0)));

                Fonts.interRegular.get(24).drawCenteredString("Session Stats", renderX + this.width / 2f, renderY + padding, setting.color(0));
                Fonts.interRegular.get(18).drawCenteredString(RenderUtils.sessionTime(), renderX + this.width / 2f, renderY + padding + 19, new Color(255, 255, 255, 200).getRGB());
                Fonts.interRegular.get(18).drawCenteredString("kills: " + setting.killed, renderX + 35, renderY + padding + 32, new Color(255, 255, 255, 200).getRGB());
                Fonts.interRegular.get(18).drawCenteredString("wins: " + setting.won, renderX + 105, renderY + padding + 32, new Color(255, 255, 255, 200).getRGB());
                break;

            case "Moon":

                this.width = 150;
                this.height = 128;

                RoundedUtils.drawRound(renderX + 9, renderY, width, height, 8f, new Color(setting.bgColor(), true));
                Fonts.interSemiBold.get(26).drawCenteredStringWithShadow("Session Information", renderX + 83, renderY + 8, -1);
                Fonts.interSemiBold.get(16).drawStringWithShadow(RenderUtils.sessionTime(), renderX + 42, renderY + 107, -1);
                Fonts.interSemiBold.get(16).drawStringWithShadow(setting.won + " wins", renderX + 42, renderY + 82, -1);
                Fonts.interSemiBold.get(16).drawStringWithShadow(setting.killed + " kills", renderX + 42, renderY + 57, -1);
                Fonts.interSemiBold.get(16).drawStringWithShadow(srv, renderX + 42, renderY + 33, -1);
                RoundedUtils.drawRound(renderX + 18, renderY + 50, 20, 20, 5f, new Color(setting.color()));
                RoundedUtils.drawRound(renderX + 18, renderY + 75, 20, 20, 5f, new Color(setting.color()));
                RoundedUtils.drawRound(renderX + 18, renderY + 100, 20, 20, 5f, new Color(setting.color()));
                Fonts.session.get(24).drawStringWithShadow("A", renderX + 24.5f, renderY + 107f, new Color(setting.color()).darker().darker().darker().getRGB());
                Fonts.session.get(24).drawStringWithShadow("D", renderX + 23f, renderY + 81, new Color(setting.color()).darker().darker().darker().getRGB());
                Fonts.session2.get(24).drawStringWithShadow("1", renderX + 22f, renderY + 57f, new Color(setting.color()).darker().darker().darker().getRGB());

                if (!srv.equals("Singleplayer")) {
                    mc.getTextureManager().bindTexture(new ResourceLocation(("servers/" + srv + "/icon")));
                    RoundedUtils.drawRoundTextured(renderX + 18, renderY + 25, 20, 20, 5f, 1f);
                }

                break;

            case "Opai":

                this.width = Fonts.interSemiBold.get(17).getStringWidth(RenderUtils.sessionTime2()) + 110;
                this.height = 65;

                RoundedUtils.drawRound(renderX + 9, renderY, width, height, 8f, new Color(setting.bgColor(), true));
                RoundedUtils.drawRound(renderX + 9, renderY, width, 14, 7.5f, new Color(ColorUtils.darker(setting.color(), 0.75f)));
                RoundedUtils.drawRound(renderX + 9, renderY + 7.5F, width, 8.5F, 0f, new Color(ColorUtils.darker(setting.color(), 0.75f)));
                RenderUtils.renderPlayer2D(mc.thePlayer, renderX + 13f, renderY + 24f, 37.5F, 10, -1);
                Fonts.interBold.get(16).drawCenteredString("Session", renderX + 30, renderY + 6, -1);
                Fonts.interSemiBold.get(17).drawString("§7Played for " + RenderUtils.sessionTime2(), renderX + 58, renderY + 31, -1);
                Fonts.interSemiBold.get(17).drawString(setting.won + " wins", renderX + 58, renderY + 55, -1);
                Fonts.interSemiBold.get(17).drawString(setting.killed + " kills", renderX + 58, renderY + 43, -1);
                break;

            case "Novo": {

                this.height = Fonts.sfui.get(24).getHeight() + Fonts.sfui.get(18).getHeight() * 3 + 3;
                this.width = 140;
                RoundedUtils.drawRound(renderX, renderY, width, height, 8f, new Color(setting.bgColor(), true));

                float left = renderX + 5;
                float right = renderX + width - 5;

                float textY = renderY + 2 + 1;

                Fonts.sfui.get(24).drawCenteredStringWithShadow("Session Information", renderX + width / 2, textY, -1);

                RenderUtils.drawGradientRect(left, textY + Fonts.sfui.get(24).getHeight() - 2, width - 5 * 2, 1, true, setting.color(0), setting.color(90));

                textY += 2;

                Fonts.sfui.get(18).drawStringWithShadow("Play Time:", left, textY + Fonts.sfui.get(24).getHeight(), -1);
                Fonts.sfui.get(18).drawStringWithShadow(RenderUtils.sessionTime(), right - Fonts.sfui.get(18).getStringWidth(RenderUtils.sessionTime()), textY + Fonts.sfui.get(24).getHeight(), -1);

                Fonts.sfui.get(18).drawStringWithShadow("Games Won:", left, textY + Fonts.sfui.get(24).getHeight() + Fonts.sfui.get(18).getHeight(), -1);
                Fonts.sfui.get(18).drawStringWithShadow(setting.won + "", right - Fonts.sfui.get(18).getStringWidth(setting.won + ""), textY + Fonts.sfui.get(24).getHeight() + Fonts.sfui.get(18).getHeight(), -1);

                Fonts.sfui.get(18).drawStringWithShadow("Players Killed:", left, textY + Fonts.sfui.get(24).getHeight() + Fonts.sfui.get(18).getHeight() * 2, -1);
                Fonts.sfui.get(18).drawStringWithShadow(setting.killed + "", right - Fonts.sfui.get(18).getStringWidth(setting.killed + ""), textY + Fonts.sfui.get(24).getHeight() + Fonts.sfui.get(18).getHeight() * 2, -1);
            }
            break;

            case "Novo 2": {
                this.height = Fonts.interSemiBold.get(20).getHeight() + Fonts.sfui.get(18).getHeight() * 3 + 3;
                this.width = 140;

                RenderUtils.drawRect(renderX, renderY, width, height, setting.bgColor());
                if (Moonlight.INSTANCE.getModuleManager().getModule(Interface.class).color.is("Fade")) {
                    RenderUtils.drawHorizontalGradientSideways(renderX, renderY, width, 1, Moonlight.INSTANCE.getModuleManager().getModule(Interface.class).getMainColor().getRGB(), Moonlight.INSTANCE.getModuleManager().getModule(Interface.class).getSecondColor().getRGB());
                } else if (Moonlight.INSTANCE.getModuleManager().getModule(Interface.class).color.is("Dynamic")){
                    RenderUtils.drawHorizontalGradientSideways(renderX, renderY, width, 1, Moonlight.INSTANCE.getModuleManager().getModule(Interface.class).getMainColor().getRGB(), ColorUtils.darker(Moonlight.INSTANCE.getModuleManager().getModule(Interface.class).getMainColor().getRGB(), 0.25F));
                } else {
                RenderUtils.drawRect(renderX, renderY, width, 1, setting.color(0));
                }

                float left = renderX + 2;
                float right = renderX + width - 2;

                float textY = renderY + 4;

                Fonts.interSemiBold.get(20).drawStringWithShadow("Current Session", left, textY, -1);

                Fonts.sfui.get(18).drawStringWithShadow("Play Time:", left, textY + Fonts.sfui.get(20).getHeight(), -1);
                Fonts.sfui.get(18).drawStringWithShadow(RenderUtils.sessionTime(), right - Fonts.sfui.get(18).getStringWidth(RenderUtils.sessionTime()), textY + Fonts.sfui.get(20).getHeight(), -1);

                Fonts.sfui.get(18).drawStringWithShadow("Games Won:", left, textY + Fonts.sfui.get(20).getHeight() + Fonts.sfui.get(18).getHeight(), -1);
                Fonts.sfui.get(18).drawStringWithShadow(setting.won + "", right - Fonts.sfui.get(18).getStringWidth(setting.won + ""), textY + Fonts.sfui.get(20).getHeight() + Fonts.sfui.get(18).getHeight(), -1);

                Fonts.sfui.get(18).drawStringWithShadow("Players Killed:", left, textY + Fonts.sfui.get(20).getHeight() + Fonts.sfui.get(18).getHeight() * 2, -1);
                Fonts.sfui.get(18).drawStringWithShadow(setting.killed + "", right - Fonts.sfui.get(18).getStringWidth(setting.killed + ""), textY + Fonts.sfui.get(20).getHeight() + Fonts.sfui.get(18).getHeight() * 2, -1);
            }
            break;

        }
    }

    @Override
    public boolean shouldRender() {
        return setting.isEnabled() && setting.elements.isEnabled("Session Info") && !setting.sessionInfoMode.is("Exhi");
    }
}
