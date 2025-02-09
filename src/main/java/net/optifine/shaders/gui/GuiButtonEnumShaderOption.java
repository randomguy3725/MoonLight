package net.optifine.shaders.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.optifine.shaders.Shaders;
import net.optifine.shaders.config.EnumShaderOption;

public class GuiButtonEnumShaderOption extends GuiButton
{
    private EnumShaderOption enumShaderOption = null;

    public GuiButtonEnumShaderOption(EnumShaderOption enumShaderOption, int x, int y, int widthIn, int heightIn)
    {
        super(enumShaderOption.ordinal(), x, y, widthIn, heightIn, getButtonText(enumShaderOption));
        this.enumShaderOption = enumShaderOption;
    }

    public EnumShaderOption getEnumShaderOption()
    {
        return this.enumShaderOption;
    }

    private static String getButtonText(EnumShaderOption eso)
    {
        String s = I18n.format(eso.getResourceKey()) + ": ";

        return switch (eso) {
            case ANTIALIASING -> s + GuiShaders.toStringAa(Shaders.configAntialiasingLevel);
            case NORMAL_MAP -> s + GuiShaders.toStringOnOff(Shaders.configNormalMap);
            case SPECULAR_MAP -> s + GuiShaders.toStringOnOff(Shaders.configSpecularMap);
            case RENDER_RES_MUL -> s + GuiShaders.toStringQuality(Shaders.configRenderResMul);
            case SHADOW_RES_MUL -> s + GuiShaders.toStringQuality(Shaders.configShadowResMul);
            case HAND_DEPTH_MUL -> s + GuiShaders.toStringHandDepth(Shaders.configHandDepthMul);
            case CLOUD_SHADOW -> s + GuiShaders.toStringOnOff(Shaders.configCloudShadow);
            case OLD_HAND_LIGHT -> s + Shaders.configOldHandLight.getUserValue();
            case OLD_LIGHTING -> s + Shaders.configOldLighting.getUserValue();
            case SHADOW_CLIP_FRUSTRUM -> s + GuiShaders.toStringOnOff(Shaders.configShadowClipFrustrum);
            case TWEAK_BLOCK_DAMAGE -> s + GuiShaders.toStringOnOff(Shaders.configTweakBlockDamage);
            default -> s + Shaders.getEnumShaderOption(eso);
        };
    }

    public void updateButtonText()
    {
        this.displayString = getButtonText(this.enumShaderOption);
    }
}
