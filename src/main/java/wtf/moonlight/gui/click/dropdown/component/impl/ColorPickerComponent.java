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
package wtf.moonlight.gui.click.dropdown.component.impl;

import wtf.moonlight.features.values.impl.ColorValue;
import wtf.moonlight.gui.click.Component;
import wtf.moonlight.gui.font.Fonts;
import wtf.moonlight.utils.animations.Animation;
import wtf.moonlight.utils.animations.Direction;
import wtf.moonlight.utils.animations.impl.EaseOutSine;
import wtf.moonlight.utils.render.MouseUtils;
import wtf.moonlight.utils.render.RenderUtils;
import wtf.moonlight.utils.render.RoundedUtils;

import java.awt.*;

public class ColorPickerComponent extends Component {

    private final ColorValue setting;
    private final Animation open = new EaseOutSine(250, 1);
    private boolean opened, pickingHue, pickingOthers, pickingAlpha;

    public ColorPickerComponent(ColorValue setting) {
        this.setting = setting;
        open.setDirection(Direction.BACKWARDS);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        open.setDirection(opened ? Direction.FORWARDS : Direction.BACKWARDS);
        this.setHeight((float) (Fonts.interRegular.get(15).getHeight() + ((Fonts.interRegular.get(15).getHeight() + 2 + 45 + 2 + 12) * open.getOutput())));

        final float[] hsb = new float[]{setting.getHue(), setting.getSaturation(), setting.getBrightness()};
        final float alpha = setting.getAlpha();

        Fonts.interRegular.get(15).drawString(setting.getName(), getX() + 4, getY(), -1);
        RoundedUtils.drawRound(getX() + getWidth() - 18, getY(), 15, Fonts.interRegular.get(15).getHeight() - 3, 2, setting.get());

        if (opened) {

            RoundedUtils.drawGradientRound(getX() + 2, getY() + Fonts.interRegular.get(15).getHeight() + 2, getWidth() - 4, (float) (45 * open.getOutput()), 4, Color.BLACK, Color.WHITE, Color.BLACK, Color.getHSBColor(setting.getHue(), 1, 1));

            for (int max = (int) (getWidth() - 8), i = 0; i < max; i++) {
                RoundedUtils.drawRound(getX() + i + 4, (float) (getY() + Fonts.interRegular.get(15).getHeight() + 2 + (45 * open.getOutput()) + 4), 2, 4, 2, Color.getHSBColor(i / (float) max, 1, 1));
            }

            float alphaSliderY = (float) (getY() + Fonts.interRegular.get(15).getHeight() + 2 + (45 * open.getOutput()) + 12);
            drawCheckerboard(getX() + 4, alphaSliderY, getWidth() - 8, 4);

            for (int max = (int) (getWidth() - 8), i = 0; i < max; i++) {
                float alphaValue = i / (float) max;
                Color alphaColor = new Color(
                        setting.get().getRed(),
                        setting.get().getGreen(),
                        setting.get().getBlue(),
                        (int)(alphaValue * 255)
                );
                RoundedUtils.drawRound(getX() + i + 4, alphaSliderY, 2, 4, 1, alphaColor);
            }

            float sliderX = getX() + 4;
            float sliderWidth = getWidth() - 8;
            float alphaHandleX = sliderX + (sliderWidth * alpha);
            alphaHandleX = Math.max(sliderX + 2, Math.min(sliderX + sliderWidth - 2, alphaHandleX));
            RenderUtils.drawCircle((int) alphaHandleX, (int) alphaSliderY + 2, 0, 360, 2, .1f, false, -1);

            float gradientX = getX() + 4;
            float gradientY = getY() + Fonts.interRegular.get(15).getHeight() + 2;
            float gradientWidth = getWidth() - 8;
            float gradientHeight = (float) (45 * open.getOutput());

            float pickerY = (gradientY) + (gradientHeight * (1 - hsb[2]));
            float pickerX = (gradientX) + (gradientWidth * hsb[1] - 1);
            pickerY = Math.max(Math.min(gradientY + gradientHeight - 2, pickerY), gradientY - 2);
            pickerX = Math.max(Math.min(gradientX + gradientWidth - 2, pickerX), gradientX - 2);

            if (pickingHue) {
                setting.setHue(Math.min(1, Math.max(0, (mouseX - gradientX) / gradientWidth)));
            }

            if (pickingOthers) {
                setting.setBrightness(Math.min(1, Math.max(0, 1 - ((mouseY - gradientY) / gradientHeight))));
                setting.setSaturation(Math.min(1, Math.max(0, (mouseX - gradientX) / gradientWidth)));
            }

            if (pickingAlpha) {
                float newAlpha = (mouseX - sliderX) / sliderWidth;

                newAlpha = Math.max(0.0f, Math.min(1.0f, newAlpha));

                setting.setAlpha(newAlpha);
            }

            RenderUtils.drawCircle((int) pickerX, (int) pickerY, 0, 360, 2, .1f, false, -1);
        }
        super.drawScreen(mouseX, mouseY);
    }

    private void drawCheckerboard(float x, float y, float width, float height) {
        RoundedUtils.drawRound(x, y, width, height, 2, new Color(200, 200, 200));
        int squareSize = 4;
        boolean white = true;
        for (int i = 0; i < width; i += squareSize) {
            for (int j = 0; j < height; j += squareSize) {
                if (!white) {
                    Color color = new Color(150, 150, 150);
                    float drawWidth = Math.min(squareSize, width - i);
                    float drawHeight = Math.min(squareSize, height - j);

                    if (i > 2 && i < width - 2 || j > 0 && j < height - 0) {
                        RoundedUtils.drawRound(x + i, y + j, drawWidth, drawHeight, 0, color);
                    }
                }
                white = !white;
            }
            if (height / squareSize % 2 == 0) {
                white = !white;
            }
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {

        if (MouseUtils.isHovered2(getX() + getWidth() - 18, getY(), 15, Fonts.interRegular.get(15).getHeight(), mouseX, mouseY)) {
            opened = !opened;
        }

        if (opened) {
            if (MouseUtils.isHovered2(getX() + 4, getY() + Fonts.interRegular.get(15).getHeight() + 2, getWidth() - 8, (float) (45 * open.getOutput()), mouseX, mouseY)) {
                pickingOthers = true;
            }
            if (MouseUtils.isHovered2(getX() + 4, (float) (getY() + Fonts.interRegular.get(15).getHeight() + 2 + (45 * open.getOutput()) + 4), getWidth() - 8, 6, mouseX, mouseY)) {
                pickingHue = true;
            }

            float alphaSliderY = (float) (getY() + Fonts.interRegular.get(15).getHeight() + 2 + (45 * open.getOutput()) + 12);
            if (MouseUtils.isHovered2(getX() + 4, alphaSliderY, getWidth() - 8, 6, mouseX, mouseY)) {
                pickingAlpha = true;
            }
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        if (state == 0) {
            pickingHue = false;
            pickingOthers = false;
            pickingAlpha = false;
        }
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public boolean isVisible() {
        return this.setting.canDisplay();
    }
}