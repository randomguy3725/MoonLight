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

package wtf.moonlight.gui.click.astolfo.component;

import lombok.Getter;
import lombok.Setter;
import org.lwjglx.input.Mouse;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.impl.visual.ClickGUI;
import wtf.moonlight.features.values.Value;
import wtf.moonlight.features.values.impl.*;
import wtf.moonlight.gui.click.Component;
import wtf.moonlight.gui.click.IComponent;
import wtf.moonlight.gui.click.astolfo.component.impl.*;
import wtf.moonlight.gui.font.Fonts;
import wtf.moonlight.utils.render.ColorUtils;
import wtf.moonlight.utils.render.MouseUtils;
import wtf.moonlight.utils.render.RenderUtils;

import java.awt.*;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
public class ModuleComponent implements IComponent {
    @Setter
    private float x, y, width, height;
    private final Module module;
    private final CopyOnWriteArrayList<Component> values = new CopyOnWriteArrayList<>();

    public ModuleComponent(Module module) {
        this.module = module;
        for (Value value : module.getValues()) {
            if (value instanceof BoolValue boolValue) {
                values.add(new BooleanComponent(boolValue));
            }
            if (value instanceof ColorValue colorValue) {
                values.add(new ColorComponent(colorValue));
            }
            if (value instanceof SliderValue sliderValue) {
                values.add(new SliderComponent(sliderValue));
            }
            if (value instanceof ModeValue modeValue) {
                values.add(new ModeComponent(modeValue));
            }
            if (value instanceof MultiBoolValue multiBoolValue) {
                values.add(new MultiBooleanComponent(multiBoolValue));
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {

        float yOffset = 11;
        if (!module.isExpanded()) {
            RenderUtils.drawRect(getX() + 3F, y, width - 5, 11F, new Color(36, 36, 36));
            if (module.isEnabled()) {
                RenderUtils.drawRect(getX() + 3F, y, width - 5, 11F, new Color(164, 53, 144));
            }
        }

        if (MouseUtils.isHovered2(x, y, width, 11F, mouseX, mouseY)) {
            if (!module.isExpanded() && !module.isEnabled()) {
                RenderUtils.drawRect(getX() + 3F, y, width - 5, 11F, new Color(255, 255, 255, 50));
            }
        }

        Fonts.interSemiBold.get(15).drawString(module.getName().toLowerCase(),
                getX() + width - Fonts.interSemiBold.get(15).getStringWidth(module.getName().toLowerCase()) - 3F,
                y + 4F,
                module.isEnabled() && module.isExpanded() ? new Color(164, 53, 144).getRGB()
                        : new Color(160, 160, 160).getRGB());

        if (module.isExpanded()) {
            for (Component component : values) {
                if (!component.isVisible()) continue;
                component.setX(x);
                component.setY(y + yOffset);
                component.setWidth(width);
                component.drawScreen(mouseX, mouseY);

                yOffset += component.getHeight();
            }
        }

        this.height = yOffset;

        IComponent.super.drawScreen(mouseX, mouseY);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {

        if (MouseUtils.isHovered2(x, y, width, 11F, mouseX, mouseY)) {
            if (mouseButton == 1) {
                if (!module.getValues().isEmpty())
                    module.setExpanded(!module.isExpanded());
            }

            if (mouseButton == 0) {
                module.toggle();
            }
        }

        for (Component value : values){
            value.mouseClicked(mouseX, mouseY, mouseButton);
        }

        IComponent.super.mouseClicked(mouseX, mouseY, mouseButton);
    }
}
