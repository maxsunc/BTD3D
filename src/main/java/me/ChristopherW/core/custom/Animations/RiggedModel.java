package me.ChristopherW.core.custom.Animations;

import me.ChristopherW.core.entity.Material;
import me.ChristopherW.core.entity.Model;
import java.util.Map;
import java.util.Arrays;

public class RiggedModel extends Model {
    private int[] jointIds;
    private float[] weights;
    private Map<String, Animation> animations;
    private Animation currentAnimation;

    public RiggedModel(int id, int vertexCount) {
        super(id, vertexCount);
    }
    public RiggedModel(int id, int vertexCount, String path) {
        super(id, vertexCount, path);
    }

    public int[] getJointIds() {
        return jointIds;
    }

    public void setJointIds(int[] jointIds) {
        this.jointIds = jointIds;
    }
    
    public float[] getWeights() {
        return weights;
    }

    public void setWeights(float[] weights) {
        this.weights = weights;
    }

    public void play(String key) {
        this.currentAnimation = animations.get(key);
        for(AnimatedFrame f : currentAnimation.getFrames()) {
            System.out.println(Arrays.toString(f.getJointMatrices()));
        }
    }
    public void play(Animation anim) {
        this.currentAnimation = anim;
    }
    public Animation getAnimation(String key) {
        return animations.get(key);
    }
    public Map<String, Animation> getAnimations() {
        return animations;
    }
    public void setAnimations(Map<String, Animation> animations) {
        this.animations = animations;
    }
    public Animation getCurrentAnimation() {
        return this.currentAnimation;
    }
}
