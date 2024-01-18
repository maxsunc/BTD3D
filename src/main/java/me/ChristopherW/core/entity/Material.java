package me.ChristopherW.core.entity;

import org.joml.Vector4f;

public class Material {
    private float specular;
    private float reflectability;
    private Vector4f colorFilter;
    private float colorBlending;

    private Texture texture;

    public Material() {
        this.specular = 0.0f;
        this.reflectability = 0;
        this.colorFilter = new Vector4f(1);
        this.colorBlending = 0;
        this.texture = null;
    }

    public Material(Material in) {
        this.specular = in.specular;
        this.reflectability = in.reflectability;
        this.texture = in.texture;
        this.colorFilter = new Vector4f(in.colorFilter);
        this.colorBlending = 0;
    }
    public Material(float specular, float reflectability) {
        this(specular, reflectability, null, new Vector4f(1));
    }
    public Material(Texture texture) {
        this(0.0f, 0, texture, new Vector4f(1));
    }

    public Material(float specular, float reflectability, Texture texture, Vector4f colorFilter) {
        this.specular = specular;
        this.reflectability = reflectability;
        this.texture = texture;
        this.colorFilter = new Vector4f(colorFilter);
        this.colorBlending = 0;
    }

    public Material(float specular, float reflectability, Texture texture) {
        this(specular, reflectability, texture, new Vector4f(1));
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
    public Vector4f getColorFilter() {
        return colorFilter;
    }

    public void setColorFilter(Vector4f colorFilter) {
        this.colorFilter = colorFilter;
    }

    public float getColorBlending() {
        return colorBlending;
    }

    public void setColorBlending(float colorBlending) {
        this.colorBlending = colorBlending;
    }

    
}
