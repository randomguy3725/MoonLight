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

package wtf.moonlight.gui.click.astolfo;

import net.minecraft.client.gui.GuiScreen;
import org.lwjglx.opengl.Display;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.gui.click.astolfo.window.Window;
import wtf.moonlight.utils.render.RenderUtils;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AstolfoGui extends GuiScreen {
    private final List<Window> windows = new ArrayList<>();

    public AstolfoGui() {
        float x = 50, y = 50;
        for (final ModuleCategory category : ModuleCategory.values()) {
            windows.add(new Window(category, x, y));
            x += 100 + 10;
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        this.windows.forEach(window -> {
            window.mouseClicked(mouseX, mouseY, mouseButton);
        });
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        this.windows.forEach(window -> {
            window.mouseReleased(mouseX, mouseY, state);
        });
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public void initGui() {
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderUtils.drawRect(0, 0, Display.getWidth(), Display.getHeight(), new Color(0, 0, 0, 150));
        this.windows.forEach(window -> {
            window.drawScreen(mouseX, mouseY);
        });
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

}
