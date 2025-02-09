package net.minecraftforge.client.model;

import net.minecraft.util.EnumFacing;

import javax.vecmath.Matrix4f;

public interface ITransformation
{
    Matrix4f getMatrix();

    EnumFacing rotate(EnumFacing var1);

    int rotate(EnumFacing var1, int var2);
}
