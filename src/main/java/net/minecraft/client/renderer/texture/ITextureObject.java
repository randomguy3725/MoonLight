package net.minecraft.client.renderer.texture;

import net.minecraft.client.resources.IResourceManager;
import net.optifine.shaders.MultiTexID;

import java.io.IOException;

public interface ITextureObject
{
    void setBlurMipmap(boolean p_174936_1_, boolean p_174936_2_);

    void restoreLastBlurMipmap();

    void loadTexture(IResourceManager resourceManager) throws IOException;

    int getGlTextureId();

    MultiTexID getMultiTexID();
}
