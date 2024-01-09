package me.ChristopherW.core.custom.Animations;

import org.joml.Matrix4f;

public class Bone
{
    private int id;
    private String name;
    private final Matrix4f offsetMatrix;
    private Matrix4f transformation;

    public Bone(int id, String name, Matrix4f offsetMatrix)
    {
        this.id = id;
        this.name = name;
        this.offsetMatrix = offsetMatrix;
    }

    public int boneId() {
        return id;
    }
    public String getName()
    {
        return name;
    }

    public Matrix4f getOffsetMatrix()
    {
        return offsetMatrix;
    }

    public Matrix4f getTransformation()
    {
        return transformation;
    }

    void setTransformation(Matrix4f transformation)
    {
        this.transformation = transformation;
    }
}