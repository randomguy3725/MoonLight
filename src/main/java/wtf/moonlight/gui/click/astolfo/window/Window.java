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

package wtf.moonlight.gui.click.astolfo.window;

import kotlin.collections.CollectionsKt;
import lombok.Getter;
import wtf.moonlight.Moonlight;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.gui.click.IComponent;
import wtf.moonlight.gui.click.astolfo.component.ModuleComponent;
import wtf.moonlight.gui.font.Fonts;
import wtf.moonlight.utils.render.MouseUtils;
import wtf.moonlight.utils.render.RenderUtils;

import java.awt.*;
import java.util.List;

public class Window implements IComponent {
    private final List<ModuleComponent> moduleComponents;
    @Getter
    private final ModuleCategory category;
    private float x, y, dragX, dragY;
    private float width = 100, height;
    private boolean expand = false;
    private boolean dragging = false;


    public Window(ModuleCategory category, float x, float y) {
        this.category = category;
        this.x = x;
        this.y = y;

        this.moduleComponents = CollectionsKt.map(
                Moonlight.INSTANCE.getModuleManager().getModulesByCategory(category),
                ModuleComponent::new
        );
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {

        if (dragging) {
            x = (mouseX + dragX);
            y = (mouseY + dragY);
        }

        RenderUtils.drawBorderedRect(x, y, width, height, 1F, new Color(25, 25, 25).getRGB(), new Color(160, 50, 145).getRGB());
        /*if (!expand) {
            RenderUtils.drawImage(new ResourceLocation("client/icon/eye_close.png"), x + 80F, y + 3F, 8F, 8F,
                    new Color(60, 60, 60).getRGB());
        } else {
            RenderUtils.drawImage(new ResourceLocation("client/icon/eye_open.png"), x + 80F, y + 3F, 8F, 8F,
                    new Color(164, 53, 144).getRGB());
        }
        // if (expand) height += 3F;
        final ResourceLocation categoryResourceLocation;
        switch (category) {
            case COMBAT:
                categoryResourceLocation = new ResourceLocation("client/icon/combat_good.png");
                break;
            case MOVEMENT:
                categoryResourceLocation = new ResourceLocation("client/icon/movement_good.png");

                break;
            case RENDER:
                categoryResourceLocation = new ResourceLocation("client/icon/visual_good.png");
                break;

            case PLAYER:
                categoryResourceLocation = new ResourceLocation("client/icon/player_good.png");
                break;
            default:
                categoryResourceLocation = new ResourceLocation("client/icon/world_good.png");
                break;

        }

        RenderUtils.drawImage(categoryResourceLocation, 90F, 3F, 8F, 8F, new Color(164, 53, 144).getRGB());*/

        float componentOffsetY = 15;
        if (expand) {
            for (ModuleComponent moduleComponent : moduleComponents) {
                moduleComponent.setX(x);
                moduleComponent.setY(y + componentOffsetY);
                moduleComponent.setWidth(width);
                moduleComponent.drawScreen(mouseX, mouseY);
                componentOffsetY += moduleComponent.getHeight();
            }
        }

        height = componentOffsetY;

        Fonts.interSemiBold.get(15).drawString(category.getName().toLowerCase(), x + 5F, y + 4F, -1);

        IComponent.super.drawScreen(mouseX, mouseY);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        dragging = false;
        for (ModuleComponent moduleComponent : moduleComponents) {
            moduleComponent.mouseReleased(mouseX,mouseY,state);
        }
        IComponent.super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (MouseUtils.isHovered2(x, y, width, 13F, mouseX, mouseY) && mouseButton == 0) {
            dragging = true;
            dragX = x - mouseX;
            dragY = y - mouseY;
        }

        if (MouseUtils.isHovered2(x, y, width, 20F, mouseX, mouseY) && mouseButton == 1) {
            expand = !expand;
        }

        if (expand && !MouseUtils.isHovered2(x, y, width, 20F, mouseX, mouseY)) {
            for (ModuleComponent moduleComponent : moduleComponents) {
                moduleComponent.mouseClicked(mouseX,mouseY,mouseButton);
            }
        }

        IComponent.super.mouseClicked(mouseX, mouseY, mouseButton);
    }
}
