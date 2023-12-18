package me.ChristopherW.core.entity;

public class Material {
    private float specular;
    private float reflectability;
    private Texture texture;

    public Material() {
        this.specular = 0.0f;
        this.reflectability = 0;
        this.texture = null;
    }

    public Material(Material in) {
        this.specular = in.specular;
        this.reflectability = in.reflectability;
        this.texture = in.texture;
    }
    public Material(float specular, float reflectability) {
        this(specular, reflectability, null);
    }

    public Material(Texture texture) {
        this(0.0f, 0, texture);
    }

    public Material(float specular, float reflectability, Texture texture) {
        this.specular = specular;
        this.reflectability = reflectability;
        this.texture = texture;
    }

    public void setSpecular(float specular) {
        this.specular = specular;
    }

    public float getSpecular() {
        return specular;
    }

    public float getReflectability() {
        return reflectability;
    }

    public void setReflectability(float reflectability) {
        this.reflectability = reflectability;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public boolean hasTexture() {
        return texture != null;
    }
}
