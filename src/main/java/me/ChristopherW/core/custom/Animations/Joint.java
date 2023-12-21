package me.ChristopherW.core.custom.Animations;

import org.joml.Matrix4f;
import java.util.ArrayList;

public class Joint {
    public final int index;// ID
	public final String name;
	public final ArrayList<Joint> children = new ArrayList<Joint>();

	private Matrix4f animatedTransform = new Matrix4f();
	
	private final Matrix4f localBindTransform;
	private Matrix4f inverseBindTransform = new Matrix4f();
    
    public Joint(int index, String name, Matrix4f bindLocalTransform) {
		this.index = index;
		this.name = name;
		this.localBindTransform = bindLocalTransform;
	}

    public void addChild(Joint child) {
		this.children.add(child);
	}

    public Matrix4f getAnimatedTransform() {
		return animatedTransform;
	}

    public void setAnimationTransform(Matrix4f animationTransform) {
		this.animatedTransform = animationTransform;
	}

    public Matrix4f getInverseBindTransform() {
		return inverseBindTransform;
	}

    protected void calcInverseBindTransform(Matrix4f parentBindTransform) {
		Matrix4f bindTransform = parentBindTransform.mul(localBindTransform, null);
		bindTransform.invert(inverseBindTransform);
		for (Joint child : children) {
			child.calcInverseBindTransform(bindTransform);
		}
	}
}
