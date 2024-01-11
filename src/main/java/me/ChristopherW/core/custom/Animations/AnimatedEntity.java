package me.ChristopherW.core.custom.Animations;

import java.util.Arrays;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.linux.Flock;

import me.ChristopherW.core.custom.Animations.RiggedMesh.AnimatedFrame;
import me.ChristopherW.core.custom.Animations.RiggedMesh.Animation;
import me.ChristopherW.core.entity.Entity;
import me.ChristopherW.core.entity.Mesh;

import java.util.HashMap;

public class AnimatedEntity extends Entity {

    private int animationId;

    public AnimatedEntity(RiggedModel model, Vector3f position, Vector3f rotation, Vector3f scale) {
        super(model, position, rotation, scale);
    }
    

    public AnimatedEntity(String name, RiggedModel model, Vector3f position, Vector3f rotation, Vector3f scale) {
        super(name, model, position, rotation, scale);
    }

    public void setAnimationId(int id) {
        for(Mesh mesh : this.getModel().getMeshes().values()) {
            RiggedMesh rm = (RiggedMesh)mesh;
            rm.setAnimationData(new AnimationData(rm.getAnimationList().get(id)));
        }
        this.animationId = id;
    }

    private double animationTick;

    
    public double getAnimationTick() {
        return animationTick;
    }


    public void setAnimationTick(double animationTick) {
        this.animationTick = animationTick;
    }

    public void tick(double amount) {
        this.animationTick += amount;
    }

    AnimatedFrame currentAnimatedFrame;
    public RiggedMesh.AnimatedFrame getCurrentFrame(RiggedMesh mesh) {
        
        AnimatedFrame currentAnimatedFrame = mesh.getAnimationData().getCurrentAnimation().frames().get(mesh.getAnimationData().getCurrentFrameIdx());
        if(currentAnimatedFrame != null) {
            this.currentAnimatedFrame = currentAnimatedFrame;
            return currentAnimatedFrame;
        }

        return null;
    }

    public int getAnimationCount() {
        for(Mesh mesh : this.getModel().getMeshes().values()) {
            RiggedMesh rm = (RiggedMesh)mesh;
            int animationCount = rm.getAnimationList().size();
            return animationCount;
        }
        return -1;
    }

    public int getCurrentFrameIdx() {
        for(Mesh mesh : this.getModel().getMeshes().values()) {
            RiggedMesh rm = (RiggedMesh)mesh;
            
            int currentFrameId = rm.getAnimationData().getCurrentFrameIdx();
            if(rm.getAnimationData() != null) {
                return currentFrameId;
            }
        }
        return -1;
    }

    public boolean nextFrame() {
        boolean loopComplete = false;
        for(Mesh mesh : this.getModel().getMeshes().values()) {
            RiggedMesh rm = (RiggedMesh)mesh;
            
            if(rm.getAnimationData() != null) {
                loopComplete = rm.getAnimationData().nextFrame();
            }
        }
        return loopComplete;
    }


    public int getAnimationId() {
        return animationId;
    }
}