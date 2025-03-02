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

package wtf.moonlight.gui.click.astolfo.component.impl;

import org.lwjglx.input.Mouse;
import wtf.moonlight.features.values.impl.BoolValue;
import wtf.moonlight.gui.click.Component;
import wtf.moonlight.gui.font.Fonts;
import wtf.moonlight.utils.render.MouseUtils;
import wtf.moonlight.utils.render.RenderUtils;

import java.awt.*;

public class BooleanComponent extends Component {

    private final BoolValue value;

    public BooleanComponent(BoolValue value) {
        this.value = value;
        setHeight(11);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {

        if (value.get()) {
            RenderUtils.drawRect(getX() + 3F, getY(), (getWidth() - 5), getHeight(), new Color(164, 53, 144));
        }
        Fonts.interSemiBold.get(13).drawString(value.getName(), getX() + 5F, getY() + 4F, -1);

        super.drawScreen(mouseX, mouseY);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (MouseUtils.isHovered2(getX(), getY(), getWidth(), getHeight(), mouseX, mouseY)) {
            if (mouseButton == 0) {
                value.set(!value.get());
            }
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean isVisible() {
        return this.value.canDisplay();
    }
}
