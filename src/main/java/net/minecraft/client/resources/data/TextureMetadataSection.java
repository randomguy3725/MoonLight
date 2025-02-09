package net.minecraft.client.resources.data;

import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntLists;

public class TextureMetadataSection implements IMetadataSection
{
    private final boolean textureBlur;
    private final boolean textureClamp;
    private final IntList listMipmaps;

    public TextureMetadataSection(boolean p_i45102_1_, boolean p_i45102_2_, IntList p_i45102_3_)
    {
        this.textureBlur = p_i45102_1_;
        this.textureClamp = p_i45102_2_;
        this.listMipmaps = p_i45102_3_;
    }

    public boolean getTextureBlur()
    {
        return this.textureBlur;
    }

    public boolean getTextureClamp()
    {
        return this.textureClamp;
    }

    public IntList getListMipmaps()
    {
        return IntLists.unmodifiable(this.listMipmaps);
    }
}
