package me.ChristopherW.core.custom.Animations;

import me.ChristopherW.core.IShader;
import me.ChristopherW.core.custom.Shaders.AnimatedShader;
import me.ChristopherW.core.entity.Material;
import me.ChristopherW.core.entity.Mesh;
import me.ChristopherW.core.entity.Texture;
import me.ChristopherW.core.utils.Utils;

import java.util.HashMap;
import java.util.List;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.assimp.AIAnimation;
import org.lwjgl.assimp.AINode;
import org.lwjgl.assimp.AINodeAnim;
import org.lwjgl.assimp.AIQuaternion;
import org.lwjgl.assimp.AIVector3D;

public class RiggedMesh extends Mesh {
    private Bone[] bones;
    private float[] weights;
    private List<Animation> animationList;
    private AnimationData animationData;

    public AnimationData getAnimationData() {
        return animationData;
    }
    public void setAnimationData(AnimationData animationData) {
        this.animationData = animationData;
    }

    public RiggedMesh(int id, int vertexCount) {
        super(id, vertexCount);
        try {      
            AnimatedShader shader = new AnimatedShader("/shaders/animation.vs", "/shaders/animation.fs");
            this.setShader(shader);
            shader.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public RiggedMesh(int id, int vertexCount, Texture texture) {
        super(id, vertexCount, texture);
        try {      
            AnimatedShader shader = new AnimatedShader("/shaders/animation.vs", "/shaders/animation.fs");
            this.setShader(shader);
            shader.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public RiggedMesh(int id, int vertexCount, String path) {
        super(id, vertexCount, path);
        try {      
            AnimatedShader shader = new AnimatedShader("/shaders/animation.vs", "/shaders/animation.fs");
            this.setShader(shader);
            shader.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public RiggedMesh(RiggedMesh model) {
        super(model);
        try {      
            AnimatedShader shader = new AnimatedShader("/shaders/animation.vs", "/shaders/animation.fs");
            this.setShader(shader);
            shader.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.bones = model.bones;
        this.weights = model.weights;
        this.animationList = model.animationList;
    }
    
    public Bone[] getBones() {
        return bones;
    }

    public void setBones(Bone[] bones) {
        this.bones = bones;
    }
    
    public float[] getWeights() {
        return weights;
    }

    public void setWeights(float[] weights) {
        this.weights = weights;
    }
    
    public void setAnimations(List<Animation> list) {
        this.animationList = list;
    }

    public List<Animation> getAnimationList() {
        return animationList;
    }

    public record AnimatedFrame(Matrix4f[] boneMatrices) {
    }

    public record Animation(String name, double duration, List<AnimatedFrame> frames) {
    }

    private Bone findBone(String nodeName)
    {
        for (Bone b : bones)
            if (b.getName().equals(nodeName))
                return b;

        return null;
    }
}
