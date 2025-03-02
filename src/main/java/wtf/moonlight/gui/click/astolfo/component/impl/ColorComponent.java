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
import wtf.moonlight.features.values.impl.ColorValue;
import wtf.moonlight.gui.click.Component;
import wtf.moonlight.gui.click.astolfo.HSBData;
import wtf.moonlight.gui.font.Fonts;
import wtf.moonlight.utils.render.MouseUtils;
import wtf.moonlight.utils.render.RenderUtils;

import java.awt.*;

public class ColorComponent extends Component {

    private final ColorValue value;
    private boolean expanded;

    public ColorComponent(ColorValue value) {
        this.value = value;
        setHeight(11);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        HSBData data = new HSBData(value.get());
        final float[] hsba = {
                data.getHue(),
                data.getSaturation(),
                data.getBrightness(),
                value.get().getAlpha(),
        };
        RenderUtils.drawRoundedRect(getX() + 88, getY() + 1F, 9F, 9F, 3f,
                value.get().getRGB());
        Fonts.interSemiBold.get(13).drawString(value.getName(),getX() +  5f,
                5.5f + getY(), 0xffffffff, false);

        if (expanded) {

            RenderUtils.drawRect(getX() + 98 + 3, getY(), 61, 61,
                    new Color(0, 0, 0));
            RenderUtils.drawRect(getX() + 98 + 3.5F, 0.5F + getY(), 60, 60,
                    Color.getHSBColor(hsba[0], 1, 1));
            RenderUtils.drawHorizontalGradientSideways(getX() + 98F + 3.5F,
                    0.5F + getY(), 60, 60, Color.getHSBColor(hsba[0], 0, 1).getRGB(),
                    0x00F);
            RenderUtils.drawVerticalGradientSideways(getX() + 98 + 3.5f,
                    0.5F + getY(), 60, 60, 0x00F,
                    Color.getHSBColor(hsba[0], 1, 0).getRGB());

            RenderUtils.drawRect(getX() + 98 + 3.5f + hsba[1] * 60 - .5f,
                    0.5F + ((1 - hsba[2]) * 60) - .5f + getY(), 1.5f, 1.5f,
                    new Color(0, 0, 0));
            RenderUtils.drawRect(getX() + 98 + 3.5F + hsba[1] * 60,
                    0.5F + ((1 - hsba[2]) * 60) + getY(), .5f, .5f, value.get());

            final boolean onSB = MouseUtils.isHovered2(getX() + 98 + 3, getY() + 0.5F, 61, 61,
                    mouseX, mouseY);

            if (onSB && Mouse.isButtonDown(0)) {
                data.setSaturation(Math.min(Math.max((mouseX - (getX() + 98) - 3) / 60F, 0), 1));
                data.setBrightness(
                        1 - Math.min(Math.max((mouseY - getY() - getHeight()) / 60F, 0), 1));
                value.set(data.getAsColor());

            }

            RenderUtils.drawRect(getX() + 98 + 67, getY(), 10, 61,
                    new Color(0, 0, 0));

            for (float f = 0F; f < 5F; f += 1F) {
                final Color lasCol = Color.getHSBColor(f / 5F, 1F, 1F);
                final Color tarCol = Color.getHSBColor(Math.min(f + 1F, 5F) / 5F, 1F, 1F);
                RenderUtils.drawVerticalGradientSideways(getX() + 98 + 67.5F,
                        0.5F + f * 12 + getY(), 9, 12, lasCol.getRGB(),
                        tarCol.getRGB());
            }

            RenderUtils.drawRect(getX() + 98 + 67.5F, -1 + hsba[0] * 60 + getY(), 9,
                    2, new Color(0, 0, 0));
            RenderUtils.drawRect(getX() + 98 + 67.5F, -0.5f + hsba[0] * 60 + getY(),
                    9, 1, new Color(204, 198, 255));

            final boolean onHue = MouseUtils.isHovered2(getX() + 98 + 67,
                    getY(), 10, 61, mouseX, mouseY);

            if (onHue && Mouse.isButtonDown(0)) {
                data.setHue(Math.min(Math.max((mouseY - getY() - getHeight()) / 60F, 0), 1));
                value.set(data.getAsColor());
            }
        }
        super.drawScreen(mouseX, mouseY);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (MouseUtils.isHovered2(getX() ,getY(), 100F, getHeight(), mouseX, mouseY)) {
            if (mouseButton == 0) {
                expanded = !expanded;
            }
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean isVisible() {
        return this.value.canDisplay();
    }
}
