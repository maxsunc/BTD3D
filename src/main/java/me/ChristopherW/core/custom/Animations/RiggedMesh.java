package me.ChristopherW.core.custom.Animations;

import me.ChristopherW.core.IShader;
import me.ChristopherW.core.custom.Shaders.AnimatedShader;
import me.ChristopherW.core.entity.Material;
import me.ChristopherW.core.entity.Mesh;
import me.ChristopherW.core.entity.Model;
import me.ChristopherW.core.entity.Texture;
import me.ChristopherW.core.utils.Utils;

import java.util.HashMap;

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
    private AINode root;
    private AIAnimation[] animations;

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
        this.root = model.root;
        this.animations = model.animations;
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
    public AIAnimation getAnimation(int index) {
        return animations[index];
    }
    public AIAnimation[] getAnimations() {
        return animations;
    }
    public void setAnimations(AIAnimation[] animations) {
        this.animations = animations;
    }
    public AINode getRoot() {
        return root;
    }
    public void setRoot(AINode root) {
        this.root = root;
    }

    public void updateAnimation(float time, int animationIndex)
    {
        assert animationIndex >= 0 && animationIndex < animations.length;
        updateBoneTransformation(time, animationIndex);
    }

    private void updateBoneTransformation(float timeInSeconds, int animationIndex)
    {
        Matrix4f identity = new Matrix4f();

        AIAnimation target = animations[animationIndex];

        float ticksPerSecond = target.mTicksPerSecond() != 0 ? (float) target.mTicksPerSecond() : 60.0f;
        float ticks = timeInSeconds * ticksPerSecond;
        float animationTime = (ticks % (float) target.mDuration());

        processNode(target, animationTime, root, identity);
    }

    private void processNode(AIAnimation target, float animationTime, AINode node, Matrix4f parentTransform)
    {
        String nodeName = node.mName().dataString();

        Matrix4f nodeTransform = Utils.convertMatrix(node.mTransformation());

        AINodeAnim boneAnimation = findBoneAnimation(target, nodeName);

        // If this node refers bone (contains animation), Do interpolate transforms.
        if (boneAnimation != null)
        {
            Vector3f interpolatedScale = calcInterpolatedScale(animationTime, boneAnimation);
            Matrix4f scaleMatrix = new Matrix4f().scale(interpolatedScale);

            Quaternionf interpolatedRotation = calcInterpolatedRotation(animationTime, boneAnimation);
            Matrix4f rotationMatrix = new Matrix4f().rotate(interpolatedRotation);

            Vector3f interpolatedPosition = calcInterpolatedPosition(animationTime, boneAnimation);
            Matrix4f translationMatrix = new Matrix4f().translate(interpolatedPosition);

            nodeTransform = Utils.multiplyMatrices(translationMatrix, rotationMatrix, scaleMatrix);
        }

        Matrix4f toGlobalSpace = Utils.multiplyMatrices(parentTransform, nodeTransform);

        Bone bone = findBone(nodeName);
        if (bone != null)
            bone.setTransformation(Utils.multiplyMatrices(toGlobalSpace, bone.getOffsetMatrix()));

        // Recursively process the child nodes
        for (int i = 0; i < node.mNumChildren(); i++)
        {
            AINode childNode = AINode.create(node.mChildren().get(i));
            processNode(target, animationTime, childNode, toGlobalSpace);
        }
    }

    // Each node has a name. If that node is bone, the node name equals to bone name.
    private AINodeAnim findBoneAnimation(AIAnimation target, String nodeName)
    {
        for (int i = 0; i < target.mNumChannels(); i++)
        {
            AINodeAnim nodeAnim = AINodeAnim.create(target.mChannels().get(i));

            if (nodeAnim.mNodeName().dataString().equals(nodeName))
                return nodeAnim;
        }

        return null;
    }

    private Vector3f calcInterpolatedScale(float timeAt, AINodeAnim boneAnimation)
    {
        if (boneAnimation.mNumScalingKeys() == 1) {
            AIVector3D vec = boneAnimation.mScalingKeys().get(0).mValue();
            return new Vector3f(vec.x(), vec.y(), vec.z());
        }

        int index0 = findScaleIndex(timeAt, boneAnimation);
        int index1 = index0 + 1;
        float time0 = (float) boneAnimation.mScalingKeys().get(index0).mTime();
        float time1 = (float) boneAnimation.mScalingKeys().get(index1).mTime();
        float deltaTime = time1 - time0;
        float percentage = (timeAt - time0) / deltaTime;

        AIVector3D vec1 = boneAnimation.mScalingKeys().get(index0).mValue();
        AIVector3D vec2 = boneAnimation.mScalingKeys().get(index1).mValue();
        Vector3f start = new Vector3f(vec1.x(), vec1.y(), vec1.z());
        Vector3f end = new Vector3f(vec2.x(), vec2.y(), vec2.z());
        Vector3f delta = end.sub(start);

        return start.add(delta.mul(percentage));
    }

    private int findScaleIndex(float timeAt, AINodeAnim boneAnimation)
    {
        assert boneAnimation.mNumScalingKeys() > 0;

        for (int i = 0; i < boneAnimation.mNumScalingKeys() - 1; i++)
        {
            if (timeAt < boneAnimation.mScalingKeys().get(i + 1).mTime())
                return i;
        }

        return 0;
    }

    private Quaternionf calcInterpolatedRotation(float timeAt, AINodeAnim boneAnimation)
    {
        if (boneAnimation.mNumRotationKeys() == 1) {
            AIQuaternion q = boneAnimation.mRotationKeys().get(0).mValue();
            return new Quaternionf(q.x(), q.y(), q.z(), q.w());
        }

        int index0 = findRotationIndex(timeAt, boneAnimation);
        int index1 = index0 + 1;
        float time0 = (float) boneAnimation.mRotationKeys().get(index0).mTime();
        float time1 = (float) boneAnimation.mRotationKeys().get(index1).mTime();
        float deltaTime = time1 - time0;
        float percentage = (timeAt - time0) / deltaTime;

        
        AIQuaternion q1 = boneAnimation.mRotationKeys().get(index0).mValue();
        AIQuaternion q2 = boneAnimation.mRotationKeys().get(index1).mValue();
        Quaternionf start = new Quaternionf(q1.x(), q1.y(), q1.z(), q1.w());
        Quaternionf end = new Quaternionf(q2.x(), q2.y(), q2.z(), q2.w());

        return Utils.slerp(start, end, percentage);
    }

    private int findRotationIndex(float timeAt, AINodeAnim boneAnimation)
    {
        assert boneAnimation.mNumRotationKeys() > 0;

        for (int i = 0; i < boneAnimation.mNumRotationKeys() - 1; i++)
        {
            if (timeAt < boneAnimation.mRotationKeys().get(i + 1).mTime())
                return i;
        }

        return 0;
    }

    private Vector3f calcInterpolatedPosition(float timeAt, AINodeAnim boneAnimation)
    {
        if (boneAnimation.mNumPositionKeys() == 1) {
            AIVector3D vec = boneAnimation.mPositionKeys().get(0).mValue();
            return new Vector3f(vec.x(), vec.y(), vec.z());
        }

        int index0 = findPositionIndex(timeAt, boneAnimation);
        int index1 = index0 + 1;
        float time0 = (float) boneAnimation.mPositionKeys().get(index0).mTime();
        float time1 = (float) boneAnimation.mPositionKeys().get(index1).mTime();
        float deltaTime = time1 - time0;
        float percentage = (timeAt - time0) / deltaTime;

        AIVector3D vec1 = boneAnimation.mPositionKeys().get(index0).mValue();
        AIVector3D vec2 = boneAnimation.mPositionKeys().get(index1).mValue();
        Vector3f start = new Vector3f(vec1.x(), vec1.y(), vec1.z());
        Vector3f end = new Vector3f(vec2.x(), vec2.y(), vec2.z());
        Vector3f delta = end.sub(start);

        return start.add(delta.mul(percentage));
    }

    private int findPositionIndex(float timeAt, AINodeAnim boneAnimation)
    {
        assert boneAnimation.mNumPositionKeys() > 0;

        for (int i = 0; i < boneAnimation.mNumPositionKeys() - 1; i++)
        {
            if (timeAt < boneAnimation.mPositionKeys().get(i + 1).mTime())
                return i;
        }

        return 0;
    }

    private Bone findBone(String nodeName)
    {
        for (Bone b : bones)
            if (b.getName().equals(nodeName))
                return b;

        return null;
    }
}
