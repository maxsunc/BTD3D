package me.ChristopherW.core.custom.Shaders;

import org.joml.Matrix4f;

import me.ChristopherW.core.Camera;
import me.ChristopherW.core.IShader;
import me.ChristopherW.core.ShaderManager;
import me.ChristopherW.core.entity.Entity;
import me.ChristopherW.core.entity.Material;
import me.ChristopherW.core.utils.GlobalVariables;
import me.ChristopherW.core.utils.Transformation;

public class DefaultShader extends ShaderManager implements IShader {

    public DefaultShader(String vertexPath, String fragmentPath) throws Exception {
        super(vertexPath, fragmentPath);
    }

    public void createMaterialUniform(String uniformName) throws Exception {
        createUniform(uniformName + ".specular");
        createUniform(uniformName + ".hasTexture");
        createUniform(uniformName + ".reflectability");
    }

    public void setUniform(String uniformName, Material material) {
        setUniform(uniformName + ".specular", material.getSpecular());
        setUniform(uniformName + ".hasTexture", material.hasTexture() ? 1 : 0);
        setUniform(uniformName + ".reflectability", material.getReflectability());
    }

    @Override
    public void start() {
        try {
            this.init();
            this.link();
            // create default shader uniforms
            this.createUniform("textureSampler");
            this.createUniform("shadowMap");
            this.createUniform("transformationMatrix");
            this.createUniform("projectionMatrix");
            this.createUniform("viewMatrix");
            this.createUniform("m3x3InvTrans");
            this.createUniform("lightSpaceMatrix");
            this.createUniform("shadowFiltering");
            this.createMaterialUniform("material");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void prepare(Entity entity, Camera camera) {
        this.setUniform("textureSampler", 0);
        this.setUniform("shadowMap", 1);
        Matrix4f modelMatrix = Transformation.createTransformationMatrix(entity);
        this.setUniform("transformationMatrix", modelMatrix);
        this.setUniform("viewMatrix", Transformation.createViewMatrix(camera));
        this.setUniform("m3x3InvTrans", Transformation.createInvTransMatrix(modelMatrix));
        this.setUniform("lightSpaceMatrix", camera.getLightSpaceMatrix());
        this.setUniform("shadowFiltering", GlobalVariables.SHADOW_FILTERING);
        this.setUniform("material", entity.getModel().getMaterial());
    }
    
}
