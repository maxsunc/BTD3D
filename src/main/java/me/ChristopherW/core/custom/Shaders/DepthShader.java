package me.ChristopherW.core.custom.Shaders;

import org.joml.Matrix4f;

import me.ChristopherW.core.Camera;
import me.ChristopherW.core.IShader;
import me.ChristopherW.core.ShaderManager;
import me.ChristopherW.core.custom.Animations.RiggedModel;
import me.ChristopherW.core.entity.Entity;
import me.ChristopherW.core.utils.Transformation;

public class DepthShader extends ShaderManager implements IShader{

    public DepthShader(String vertexPath, String fragmentPath) throws Exception {
        super(vertexPath, fragmentPath);
    }

    @Override
    public void start() {
        try {
            this.init();
            this.link();
            // create default shader uniforms
            this.createUniform("transformationMatrix");
            this.createUniform("lightSpaceMatrix");
            this.createUniform("bones");
            this.createUniform("animated");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void prepare(Entity entity, Camera camera) {
        Matrix4f modelMatrix = Transformation.createTransformationMatrix(entity);
        this.setUniform("transformationMatrix", modelMatrix);
        this.setUniform("lightSpaceMatrix", camera.getLightSpaceMatrix());
        boolean animated = entity.getModel() instanceof RiggedModel;
        Matrix4f[] b = new Matrix4f[50];
        if(animated) {  
            RiggedModel rm = ((RiggedModel)entity.getModel());
            for(int i = 0; i < rm.getBones().length; i++) {
                b[i] = rm.getBones()[i].getTransformation();
            }
        }
        this.setUniform("bones", b);
        this.setUniform("animated", animated ? 1 : 0);
    }
    
}
