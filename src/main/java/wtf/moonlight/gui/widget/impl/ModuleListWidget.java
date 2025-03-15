package wtf.moonlight.gui.widget.impl;

import wtf.moonlight.events.impl.render.Shader2DEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.gui.widget.Widget;
import wtf.moonlight.utils.animations.Animation;
import wtf.moonlight.utils.animations.Direction;
import wtf.moonlight.utils.animations.Translate;
import wtf.moonlight.utils.render.ColorUtils;
import wtf.moonlight.utils.render.RenderUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ModuleListWidget extends Widget {

    private final int PADDING = 2;

    public ModuleListWidget() {
        super("ModuleList");
        this.x = 10;
        this.y = 10;
    }

    @Override
    public void onShader(Shader2DEvent event) {

        if (!shouldRender()) return;

        int middle = sr.getScaledWidth() / 2;
        List<Module> enabledModules = getEnabledModules();

        float offset = 0;
        float lastWidth = 0;

        for (int i = 0; i < enabledModules.size(); i++) {
            Module module = enabledModules.get(i);
            module.getAnimation().setDuration((int) setting.animSpeed.get());
            int width = getModuleWidth(module);
            int height = getModuleHeight() - 2;

            RenderPosition position = calculateRenderPosition(module, width, middle);

            if (setting.animation.is("ScaleIn")) {
                if (renderX < middle) {
                    RenderUtils.scaleStart(position.x + (width / 2.0f),
                            position.y + offset + mc.fontRendererObj.FONT_HEIGHT,
                            (float) module.getAnimation().getOutput());
                } else {
                    RenderUtils.scaleStart(position.x - (width / 2.0f) + this.width,
                            position.y + offset + mc.fontRendererObj.FONT_HEIGHT,
                            (float) module.getAnimation().getOutput());
                }
            }

            renderShaderModule(module, position.x, position.y, offset, width, height,
                    position.alphaAnimation, middle, lastWidth, i, enabledModules.size());

            if (setting.animation.is("ScaleIn")) {
                RenderUtils.scaleEnd();
            }

            if (!module.isHidden()) {
                offset = calculateNextOffset(module, height, offset);
            }

            lastWidth = width;
        }
    }

    @Override
    public void render() {
        if (!shouldRender()) return;

        int middle = sr.getScaledWidth() / 2;
        List<Module> enabledModules = getEnabledModules();

        float offset = 0;
        float lastWidth = 0;

        for (int i = 0; i < enabledModules.size(); i++) {
            Module module = enabledModules.get(i);
            module.getAnimation().setDuration((int) setting.animSpeed.get());
            int width = getModuleWidth(module);
            int height = getModuleHeight() - 2;

            RenderPosition position = calculateRenderPosition(module, width, middle);

            if (setting.animation.is("ScaleIn")) {
                if (renderX < middle) {
                    RenderUtils.scaleStart(position.x + (width / 2.0f),
                            position.y + offset + mc.fontRendererObj.FONT_HEIGHT,
                            (float) module.getAnimation().getOutput());
                } else {
                    RenderUtils.scaleStart(position.x - (width / 2.0f) + this.width,
                            position.y + offset + mc.fontRendererObj.FONT_HEIGHT,
                            (float) module.getAnimation().getOutput());
                }
            }

            renderModule(module, position.x, position.y, offset, width, height,
                    position.alphaAnimation, middle, lastWidth, i, enabledModules.size());

            if (setting.animation.is("ScaleIn")) {
                RenderUtils.scaleEnd();
            }

            if (!module.isHidden()) {
                offset = calculateNextOffset(module, height, offset);
            }
            lastWidth = width;
        }
        setting.scoreBoardHeight = (int) offset;
    }

    private List<Module> getEnabledModules() {
        List<Module> enabledModules = new ArrayList<>();
        for (Module module : INSTANCE.getModuleManager().getModules()) {
            if (module.isHidden()) {
                continue;
            }
            Animation moduleAnimation = module.getAnimation();
            moduleAnimation.setDirection(module.isEnabled() ? Direction.FORWARDS : Direction.BACKWARDS);
            if (!module.isEnabled() && moduleAnimation.finished(Direction.BACKWARDS)) continue;
            enabledModules.add(module);
        }
        enabledModules.sort(Comparator.comparing(this::getModuleWidth).reversed());
        return enabledModules;
    }

    private int getModuleWidth(Module module) {
        return !setting.cFont.get() ?
                mc.fontRendererObj.getStringWidth(module.getName() + module.getTag()) :
                setting.getFr().getStringWidth(module.getName() + module.getTag());
    }

    private int getModuleHeight() {
        return !setting.cFont.get() ?
                mc.fontRendererObj.FONT_HEIGHT :
                setting.getFr().getHeight();
    }

    private void renderShaderModule(Module module, float localX, float localY, float offset, int width, int height,
                                    float alphaAnimation, int middle, float lastWidth, int index, int totalModules) {
        if (setting.background.get()) {
            renderShaderBackground(localX, localY, offset, width, height, middle,index);
        }

        if (setting.line.get()) {
            renderLines(localX, localY, offset, width, height, middle, lastWidth, index, totalModules);
        }

        renderText(module, localX, localY, offset, width, alphaAnimation, middle,index);
    }

    private void renderModule(Module module, float localX, float localY, float offset, int width, int height,
                              float alphaAnimation, int middle, float lastWidth, int index, int totalModules) {
        if (setting.background.get()) {
            renderBackground(localX, localY, offset, width, height, middle,index);
        }

        if (setting.line.get()) {
            renderLines(localX, localY, offset, width, height, middle, lastWidth, index, totalModules);
        }

        renderText(module, localX, localY, offset, width, alphaAnimation, middle,index);
    }

    private void renderBackground(float localX, float localY, float offset, int width, int height, int middle, int index) {
        if (localX < middle) {
            RenderUtils.drawRect(localX - PADDING, localY + offset, width + 3,
                    height + PADDING + setting.textHeight.get(), setting.bgColor(index));
        } else {
            RenderUtils.drawRect(localX + this.width - 4 - width, localY + offset + 1,
                    width + 3, height + PADDING + setting.textHeight.get(), setting.bgColor(index));
        }
    }
    private void renderShaderBackground(float localX, float localY, float offset, int width, int height, int middle, int index) {
        if (localX < middle) {
            RenderUtils.drawRect(localX - PADDING, localY + offset, width + 3,
                    height + PADDING + setting.textHeight.get(), setting.color(index));
        } else {
            RenderUtils.drawRect(localX + this.width - 4 - width, localY + offset + 1,
                    width + 3, height + PADDING + setting.textHeight.get(), setting.color(index));
        }
    }


    private void renderLines(float localX, float localY, float offset, int width, int height,
                             int middle, float lastWidth, int index, int totalModules) {
        // Main line
        if (localX < middle) {
            RenderUtils.drawRect(localX - PADDING, localY + offset, 1,
                    height + 3 + setting.textHeight.get(), setting.color(index));
        } else {
            RenderUtils.drawRect(localX + this.width + PADDING - 3, localY + offset + 1, 1,
                    height + PADDING + setting.textHeight.get(), setting.color(index));
        }

        if (setting.outLine.get()) {
            renderOutlines(localX, localY, offset, width, height, middle, lastWidth, index, totalModules);
        }
    }

    private void renderOutlines(float localX, float localY, float offset, int width, int height,
                                int middle, float lastWidth, int index, int totalModules) {
        // Side lines
        if (localX < middle) {
            RenderUtils.drawRect(localX + width + 1, localY + offset + 1, 1,
                    height + PADDING + setting.textHeight.get(), setting.color(index));
        } else {
            RenderUtils.drawRect(localX + this.width - width - PADDING - 3, localY + offset, 1,
                    height + 3 + setting.textHeight.get(), setting.color(index));
        }

        // Middle lines
        if (index > 0) {
            renderMiddleLines(localX, localY, offset, width, middle, lastWidth, index);
        }

        // Top lines
        if (index == 0) {
            renderTopLines(localX, localY, width, middle);
        }

        // Bottom lines
        if (index == totalModules - 1) {
            renderBottomLines(localX, localY, offset, width, height, middle,index);
        }
    }

    private void renderMiddleLines(float localX, float localY, float offset, int width, int middle, float lastWidth, int index) {
        if (localX < middle) {
            RenderUtils.drawRect(localX + width + 1, localY + offset, lastWidth - width, 1, setting.color(index));
        } else {
            RenderUtils.drawRect(localX + this.width - width - 4, localY + offset, -(lastWidth - width), 1, setting.color(index));
        }
    }

    private void renderTopLines(float localX, float localY, int width, int middle) {
        if (localX < middle) {
            RenderUtils.drawRect(localX - PADDING, localY, width + 4, 1, setting.color(0));
        } else {
            RenderUtils.drawRect(localX - width - PADDING + this.width - 3, localY, width + 5, 1, setting.color(0));
        }
    }

    private void renderBottomLines(float localX, float localY, float offset, int width, int height, int middle, int index) {
        if (localX < middle) {
            RenderUtils.drawRect(localX - 1, localY + offset + height + PADDING + setting.textHeight.get(),
                    width + PADDING, 1, setting.color(index));
        } else {
            RenderUtils.drawRect(localX - 4 - width + this.width,
                    localY + offset + height + PADDING + setting.textHeight.get(),
                    width + 3, 1, setting.color(index));
        }
    }

    private void renderText(Module module, float localX, float localY, float offset,
                            int width, float alphaAnimation, int middle, int index) {
        String text = module.getName() + module.getTag();
        int color = ColorUtils.swapAlpha(setting.color(index), (int) alphaAnimation * setting.getMainColor().getAlpha());
        float textY = localY + offset + (setting.cFont.get() ? 6 : 2);

        if (localX < middle) {
            if (!setting.cFont.get()) {
                mc.fontRendererObj.drawStringWithShadow(text, localX, textY, color);
            } else {
                setting.getFr().drawStringWithShadow(text, localX, textY, color);
            }
        } else {
            float textX = localX - width + this.width - 2;
            if (!setting.cFont.get()) {
                mc.fontRendererObj.drawStringWithShadow(text, textX, textY, color);
            } else {
                setting.getFr().drawStringWithShadow(text, textX, textY, color);
            }
        }
    }

    private static class RenderPosition {
        float x, y, alphaAnimation;

        RenderPosition(float x, float y, float alphaAnimation) {
            this.x = x;
            this.y = y;
            this.alphaAnimation = alphaAnimation;
        }
    }

    private RenderPosition calculateRenderPosition(Module module, int width, int middle) {
        float localX = renderX;
        float localY = renderY;
        float alphaAnimation = 1.0f;

        float MOVE_IN_SCALE = 2.0f;
        switch (setting.animation.get()) {
            case "MoveIn":
                if (localX > middle) {
                    localX += (float) Math.abs(module.getAnimation().getOutput() - 1.0) *
                            (MOVE_IN_SCALE + width);
                } else {
                    localX -= (float) Math.abs((module.getAnimation().getOutput() - 1.0) *
                            (MOVE_IN_SCALE + width));
                }
                break;
            case "ScaleIn":
                alphaAnimation = (float) module.getAnimation().getOutput();
                break;
            case "Slide In":
                Translate translate = module.getTranslate();
                if (localX < middle) {
                    if (module.isEnabled()) {
                        translate.translate(MOVE_IN_SCALE + localX, localY);
                    } else {
                        translate.animate((-width) + localX, -25.0);
                    }
                } else {
                    if (module.isEnabled()) {
                        translate.translate(localX, localY);
                    } else {
                        translate.animate(localX - width + this.width, -25.0);
                    }
                }

                localX = (float) translate.getX();
                localY = (float) translate.getY();
                break;
        }

        return new RenderPosition(localX, localY, alphaAnimation);
    }

    private float calculateNextOffset(Module module, int height, float offset) {
        return (float) (offset + ((module.getAnimation().getOutput()) * (height + setting.textHeight.get())) + PADDING);
    }

    @Override
    public boolean shouldRender() {
        return setting.isEnabled() && setting.elements.isEnabled("Module List");
    }
}