package me.ChristopherW.core.custom.Shaders;

import org.joml.Matrix4f;

import me.ChristopherW.core.Camera;
import me.ChristopherW.core.IShader;
import me.ChristopherW.core.ShaderManager;
import me.ChristopherW.core.entity.Entity;
import me.ChristopherW.core.entity.Material;
import me.ChristopherW.core.entity.Mesh;
import me.ChristopherW.core.utils.Config;
import me.ChristopherW.core.utils.Transformation;

public class DefaultShader extends ShaderManager implements IShader {

    public DefaultShader(String vertexPath, String fragmentPath) throws Exception {
        super(vertexPath, fragmentPath);
    }

    public void createMaterialUniform(String uniformName) throws Exception {
        createUniform(uniformName + ".specular");
        createUniform(uniformName + ".color");
        createUniform(uniformName + ".colorBlending");
        createUniform(uniformName + ".hasTexture");
        createUniform(uniformName + ".reflectability");
    }

    public void setUniform(String uniformName, Material material) {
        setUniform(uniformName + ".specular", material.getSpecular());
        setUniform(uniformName + ".color", material.getColorFilter());
        setUniform(uniformName + ".colorBlending", material.getColorBlending());
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
            this.createUniform("skyColor");
            this.createUniform("sunPos");
            this.createUniform("showFog");
            this.createMaterialUniform("material");
            this.createUniform("shadowsEnabled");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void prepare(Entity entity, Mesh mesh, Camera camera) {
        this.setUniform("textureSampler", 0);
        this.setUniform("shadowMap", 1);
        Matrix4f modelMatrix = Transformation.createTransformationMatrix(entity);
        this.setUniform("transformationMatrix", modelMatrix);
        this.setUniform("viewMatrix", Transformation.createViewMatrix(camera));
        this.setUniform("m3x3InvTrans", Transformation.createInvTransMatrix(modelMatrix));
        this.setUniform("lightSpaceMatrix", camera.getLightSpaceMatrix());
        this.setUniform("shadowFiltering", Config.SHADOW_FILTERING ? 1 : 0);
        this.setUniform("material", mesh.getMaterial());
        this.setUniform("skyColor", Config.BG_COLOR);
        this.setUniform("showFog", Config.FOG ? 1 : 0);
        this.setUniform("sunPos", Config.SUN_POS);
        this.setUniform("shadowsEnabled", Config.SHADOW_RES > 0 ? 1 : 0);
    }
}
