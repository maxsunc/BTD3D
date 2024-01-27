package me.ChristopherW.core.custom.Animations;

import java.util.Arrays;

import org.joml.Matrix4f;

import me.ChristopherW.core.utils.Config;

public class AnimationData {

    public static final Matrix4f[] DEFAULT_BONES_MATRICES = new Matrix4f[Config.MAX_BONES];

    static {
        Matrix4f zeroMatrix = new Matrix4f().zero();
        Arrays.fill(DEFAULT_BONES_MATRICES, zeroMatrix);
    }

    private RiggedMesh.Animation currentAnimation;
    private int currentFrameIdx;

    public AnimationData(RiggedMesh.Animation currentAnimation) {
        currentFrameIdx = 0;
        this.currentAnimation = currentAnimation;
    }
    public AnimationData(AnimationData in) {
        this.currentFrameIdx = in.currentFrameIdx;
        this.currentAnimation = in.currentAnimation;
    }

    public RiggedMesh.Animation getCurrentAnimation() {
        return currentAnimation;
    }

    public RiggedMesh.AnimatedFrame getCurrentFrame() {
        return currentAnimation.frames().get(currentFrameIdx);
    }

    public int getCurrentFrameIdx() {
        return currentFrameIdx;
    }

    public boolean nextFrame() {
        int nextFrame = currentFrameIdx + 1;
        if (nextFrame > currentAnimation.frames().size() - 1) {
            currentFrameIdx = 0;
            return true;
        } else {
            currentFrameIdx = nextFrame;
            return false;
        }
    }

    public void setCurrentAnimation(RiggedMesh.Animation currentAnimation) {
        currentFrameIdx = 0;
        this.currentAnimation = currentAnimation;
    }

    public void setFrame(int id) {
        this.currentFrameIdx = id;
    }
}