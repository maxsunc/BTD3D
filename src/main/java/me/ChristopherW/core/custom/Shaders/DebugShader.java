package me.ChristopherW.core.custom.Shaders;

import me.ChristopherW.core.Camera;
import me.ChristopherW.core.IShader;
import me.ChristopherW.core.ShaderManager;
import me.ChristopherW.core.entity.Entity;

public class DebugShader extends ShaderManager implements IShader {

    public DebugShader(String vertexPath, String fragmentPath) throws Exception {
        super(vertexPath, fragmentPath);
    }

    @Override
    public void start() {
        try {
            this.init();
            this.link();
            // create default shader uniforms
            this.createUniform("depthMap");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void prepare(Entity entity, Camera camera) {
        this.setUniform("depthMap", 1);
    }
    
}
