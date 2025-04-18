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
package wtf.moonlight.gui.mainmenu;

import lombok.Getter;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.util.EnumChatFormatting;
import wtf.moonlight.Moonlight;
import wtf.moonlight.gui.button.MenuButton;
import wtf.moonlight.gui.font.Fonts;
import wtf.moonlight.utils.render.RoundedUtils;
import wtf.moonlight.utils.render.shader.impl.Blur;
import wtf.moonlight.utils.render.shader.impl.MainMenu;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GuiMainMenu extends GuiScreen {

    private final List<MenuButton> buttons = List.of(
            new MenuButton("Singleplayer"),
            new MenuButton("Multiplayer"),
            new MenuButton("Alt Manager"),
            new MenuButton("Settings"),
            new MenuButton("Exit"));

    private final List<ChangeLog> logs = new ArrayList<>();

    public GuiMainMenu() {
        logs.add(new ChangeLog("New TargetESP modes", ChangeLogType.ADDITION));
        logs.add(new ChangeLog("NoWeb and NoFluid", ChangeLogType.ADDITION));
        logs.add(new ChangeLog("InvManager Using Item Check", ChangeLogType.ADDITION));
        logs.add(new ChangeLog("Watchdog NoSlowdown Speed Up", ChangeLogType.ADDITION));
        logs.add(new ChangeLog("Opai Session Info", ChangeLogType.ADDITION));
        logs.add(new ChangeLog("New Nursultan Watermark", ChangeLogType.ADDITION));
        logs.add(new ChangeLog("Atmosphere Fog Distance setting", ChangeLogType.ADDITION));
        logs.add(new ChangeLog("Optimization and code cleanups", ChangeLogType.IMPROVEMENT));
        logs.add(new ChangeLog("Fixed Sprint activation after item use", ChangeLogType.FIX));
        logs.add(new ChangeLog("Fixed BedNuker sometimes failing to break", ChangeLogType.FIX));
        logs.add(new ChangeLog("Fixed Alt Manager adding multiple accounts", ChangeLogType.FIX));
    }

    @Override
    public void initGui() {
        buttons.forEach(MenuButton::initGui);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        MainMenu.draw(Moonlight.INSTANCE.getStartTimeLong());

        float buttonWidth = 140;
        float buttonHeight = 25;

        int count = 20;

        RoundedUtils.drawRound(width / 2f - buttonWidth / 2f - 20,(height / 2f) - 60,buttonWidth + 20 * 2,200,10,new Color(0,0,0,64));

        Blur.startBlur();
        RoundedUtils.drawRound(width / 2f - buttonWidth / 2f - 20,(height / 2f) - 60,buttonWidth + 20 * 2,200,10,new Color(0,0,0,64));
        Blur.endBlur(10,3);

        Fonts.interBold.get(35).drawCenteredString(Moonlight.INSTANCE.getClientName(), (width / 2f - buttonWidth / 2f) + buttonWidth / 2, (height / 2f) + count - (buttons.size() * buttonHeight) / 2f, -1);
        Fonts.interMedium.get(14).drawStringWithShadow("Welcome back," + EnumChatFormatting.AQUA + Moonlight.INSTANCE.getDiscordRP().getName(), width - (2 + Fonts.interMedium.get(14).getStringWidth("Welcome back," + Moonlight.INSTANCE.getDiscordRP().getName())), height - (2 + Fonts.interMedium.get(14).getHeight()), -1);

        for (MenuButton button : buttons) {
            button.x = width / 2f - buttonWidth / 2f;
            button.y = ((height / 2f) + count - (buttons.size() * buttonHeight) / 2f) + Fonts.interBold.get(35).getHeight() + 2;
            button.width = buttonWidth;
            button.height = buttonHeight;
            button.clickAction = () -> {
                switch (button.text) {
                    case "Singleplayer" -> mc.displayGuiScreen(new GuiSelectWorld(this));
                    case "Multiplayer" -> mc.displayGuiScreen(new GuiMultiplayer(this));
                    case "Alt Manager" -> mc.displayGuiScreen(Moonlight.INSTANCE.getAltRepositoryGUI());
                    case "Settings" -> mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings));
                    case "Exit" -> mc.shutdown();
                }
            };
            button.drawScreen(mouseX, mouseY);
            count += (int) (buttonHeight + 3);
        }

        int i = 0;

        for (ChangeLog changeLog : logs) {
            if (changeLog != null) {
                if (changeLog.getLog() != null) {
                    Fonts.interBold.get(20).drawStringWithShadow("Changelogs", 5, 3, -1);
                    Fonts.interBold.get(15).drawStringWithShadow(changeLog.type.character + changeLog.getLog(), 5, i * (Fonts.interBold.get(20).getHeight()) + Fonts.interBold.get(20).getHeight() + 2, changeLog.type.stringColor);
                }
                i++;
            }
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        buttons.forEach(button -> button.mouseClicked(mouseX, mouseY, mouseButton));
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    public boolean doesGuiPauseGame() {
        return false;
    }

    public static class ChangeLog {

        @Getter
        private final String log;
        private final ChangeLogType type;

        public ChangeLog(String log, ChangeLogType type) {
            this.log = log;
            this.type = type;
        }
    }

    public enum ChangeLogType {
        ADDITION("[+]", new Color(54, 239, 61).getRGB()),
        FIX("[~]", new Color(255, 225, 99).getRGB()),
        IMPROVEMENT("[*]", new Color(103, 241, 114).getRGB()),
        REMOVAL("[-]", new Color(255, 64, 64).getRGB()),
        OTHER("[?]", new Color(180, 72, 180).getRGB());

        @Getter
        private final String character;
        private final int stringColor;

        ChangeLogType(String character, int stringColor) {
            this.character = character;
            this.stringColor = stringColor;
        }
    }
}